package cc.wulian.smarthomev6.main.home.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.device.Attribute;
import cc.wulian.smarthomev6.support.core.device.Cluster;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.device.Endpoint;
import cc.wulian.smarthomev6.support.core.device.EndpointParser;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.customview.ProgressRing;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GetRoomListEvent;
import cc.wulian.smarthomev6.support.event.RoomInfoEvent;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by Wulian on 2018/9/5.
 */

public class HomeWidget_Bs_Controller extends RelativeLayout implements IWidgetLifeCycle, View.OnClickListener {

    private static final String TAG = HomeWidget_Bs_Controller.class.getSimpleName();
    private Device mDevice;
    private HomeItemBean mHomeItemBean;

    private TextView textName, textState, textArea;
    private TextView tv_mode, tv_temp, tv_temperature, tv_temperature_format;
    private ImageView iv_switch_btn, temperature_add_btn, temperature_sub_btn;
    private ProgressRing pr_one_switch;
    private RelativeLayout title;
    private Context mContext;
    private int mode, leave;
    private String leaveTemp = "0";
    private String indoorTemp = "0";
    private String groundTemp = "0";

    /**
     * 开关状态：-1 不可用，0 关，1 开
     */
    private int openState = -1;

    public HomeWidget_Bs_Controller(Context context) {
        super(context);
        mContext = context;
        initView(context);
    }

    public HomeWidget_Bs_Controller(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context);
    }

    private void initView(Context context) {
        final View rootView = LayoutInflater.from(context).inflate(R.layout.view_home_bs_controller, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        textName = (TextView) rootView.findViewById(R.id.widget_title_name);
        textArea = (TextView) rootView.findViewById(R.id.widget_title_room);
        textState = (TextView) rootView.findViewById(R.id.widget_title_state);

        tv_mode = (TextView) rootView.findViewById(R.id.tv_mode);
        tv_temp = (TextView) rootView.findViewById(R.id.tv_set_temp);
        tv_temperature = (TextView) rootView.findViewById(R.id.tv_temperature);
        tv_temperature_format = (TextView) rootView.findViewById(R.id.tv_temperature_format);

        temperature_sub_btn = (ImageView) rootView.findViewById(R.id.temperature_sub_btn);
        temperature_add_btn = (ImageView) rootView.findViewById(R.id.temperature_add_btn);
        iv_switch_btn = (ImageView) rootView.findViewById(R.id.switch_btn);
        pr_one_switch = (ProgressRing) rootView.findViewById(R.id.one_switch_progress);

        DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();
        int titleH = mDisplayMetrics.widthPixels - TypedValue.complexToDimensionPixelSize(30, mDisplayMetrics);
        textName.setMaxWidth(titleH / 2);
        textArea.setMaxWidth(titleH / 4);

        iv_switch_btn.setOnClickListener(this);
        temperature_sub_btn.setOnClickListener(this);
        temperature_add_btn.setOnClickListener(this);
    }

    @Override
    public void onBindViewHolder(HomeItemBean bean) {
        EventBus.getDefault().register(this);
        if (bean == null) {
            return;
        }
        mHomeItemBean = HomeWidgetManager.get(bean.getWidgetID());
        mDevice = MainApplication.getApplication().getDeviceCache().get(bean.getWidgetID());

        setName();
        setRoomName();
        initShowData();
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    private void initShowData() {
        if (mDevice.mode == 2) {
            // 设备离线
            updateMode();
        } else {
            EndpointParser.parse(mDevice, new EndpointParser.ParserCallback() {
                @Override
                public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                    if (cluster.clusterId == 0x0201) {
                        updateInfo(attribute);
                    }
                }
            });
            updateMode();
        }
        sendCmd(0x0101, null);
    }

    private void setName() {
        if (mDevice == null) {
            textName.setText(DeviceInfoDictionary.getNameByTypeAndName(mHomeItemBean.getName(), mHomeItemBean.getType()));
        } else {
            textName.setText(DeviceInfoDictionary.getNameByTypeAndName(mDevice.type, mDevice.name));
        }
    }

    /**
     * 设置分区名
     */
    private void setRoomName() {
        String areaName = ((MainApplication) getContext().getApplicationContext()).getRoomCache().getRoomName(mDevice);
        textArea.setText(areaName);
    }

    /**
     * 获取资源
     */
    private int getResByType(int mode) {
        /**
         * 01：单室内
         * 02：双温双控
         * 03：单地板
         */
        int modeRes = 0;
        switch (mode) {
            case 1:
                modeRes = R.string.Widget_Bs_01;
                break;
            case 2:
                modeRes = R.string.Widget_Bs_02;
                break;
            case 3:
                modeRes = R.string.Widget_Bs_03;
                break;
        }
        return modeRes;
    }

    /**
     * 更新上下线
     */
    private void updateMode() {
        if (mDevice != null && mDevice.isOnLine()) {
            // 上线
            textState.setText(R.string.Device_Online);
            textState.setTextColor(getResources().getColor(R.color.colorPrimary));

            tv_mode.setTextColor(0xff000000);
            tv_temperature.setTextColor(0xff000000);
            tv_temperature_format.setTextColor(0xff000000);
            iv_switch_btn.setEnabled(true);
            if(openState == 1){
                if (leave != 1){
                    temperature_add_btn.setEnabled(true);
                    temperature_sub_btn.setEnabled(true);
                    temperature_sub_btn.setImageResource(R.drawable.icon_arrows_left_primary);
                    temperature_add_btn.setImageResource(R.drawable.icon_arrows_right_primary);
                }else{
                    temperature_add_btn.setEnabled(false);
                    temperature_sub_btn.setEnabled(false);
                    temperature_sub_btn.setImageResource(R.drawable.icon_arrows_left_nor);
                    temperature_add_btn.setImageResource(R.drawable.icon_arrows_right_nor);
                }
            }else{
                temperature_add_btn.setEnabled(false);
                temperature_sub_btn.setEnabled(false);
                temperature_sub_btn.setImageResource(R.drawable.icon_arrows_left_nor);
                temperature_add_btn.setImageResource(R.drawable.icon_arrows_right_nor);
            }
        } else {
            // 离线
            openState = -1;
            textState.setText(R.string.Device_Offline);
            textState.setTextColor(getResources().getColor(R.color.newStateText));
            iv_switch_btn.setImageResource(R.drawable.switch_off);
            iv_switch_btn.setBackgroundResource(R.drawable.home_widget_circle_gray);
            temperature_sub_btn.setImageResource(R.drawable.icon_arrows_left_nor);
            temperature_add_btn.setImageResource(R.drawable.icon_arrows_right_nor);
            tv_mode.setTextColor(0xff9C9D9D);
            tv_temperature.setTextColor(0xff9C9D9D);
            tv_temperature_format.setTextColor(0xff9C9D9D);
            iv_switch_btn.setEnabled(false);
            temperature_add_btn.setEnabled(false);
            temperature_sub_btn.setEnabled(false);
        }
    }

    private void updateInfo(Attribute attribute){
        try {
            switch (attribute.attributeId){
                case 0x8101: {
                    int switchState = Integer.valueOf(attribute.attributeValue);
                    switch (switchState) {
                        case 0:
                            openState = 0;
                            iv_switch_btn.setImageResource(R.drawable.switch_off);
                            iv_switch_btn.setBackgroundResource(R.drawable.home_widget_circle_gray);
                            temperature_sub_btn.setImageResource(R.drawable.icon_arrows_left_nor);
                            temperature_add_btn.setImageResource(R.drawable.icon_arrows_right_nor);
                            temperature_add_btn.setEnabled(false);
                            temperature_sub_btn.setEnabled(false);
                            break;
                        case 1:
                            openState = 1;
                            iv_switch_btn.setImageResource(R.drawable.switch_on);
                            iv_switch_btn.setBackgroundResource(R.drawable.home_widget_circle_green);
                            break;
                        case 2:
                            openState = 1;
                            iv_switch_btn.setImageResource(R.drawable.switch_on);
                            iv_switch_btn.setBackgroundResource(R.drawable.home_widget_circle_green);
                            break;
                    }
                    pr_one_switch.setState(ProgressRing.FINISHED);
                    break;
                }
                case 0x8102:
                    indoorTemp = attribute.attributeValue;
                    break;
                case 0x8103:
                    groundTemp = attribute.attributeValue;
                    break;
                case 0x8104:
                    leaveTemp = attribute.attributeValue;
                    break;
                case 0x8105:
                    leave = Integer.valueOf(attribute.attributeValue);
                    break;
                case 0x8106: {
                    mode = Integer.valueOf(attribute.attributeValue);
                    if (leave == 0){
                        tv_mode.setText(getResByType(mode));
                        if (mode == 1 || mode == 2){
                            tv_temp.setText(R.string.Widget_Bs_05);
                            tv_temperature.setText(indoorTemp);
                        }else if (mode == 3){
                            tv_temp.setText(R.string.Widget_Bs_06);
                            tv_temperature.setText(groundTemp);
                        }
                    }else if (leave == 1){
                        tv_mode.setText(R.string.Widget_Bs_04);

                        tv_temp.setText(R.string.Device_Widget_Oi_Set);
                        tv_temperature.setText(leaveTemp);
                    }
                    break;
                }
            }
        }catch (Exception e){
            WLog.e("fzm", e.getMessage());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && mDevice != null) {
            if (TextUtils.equals(event.device.devID, mDevice.devID)) {
                mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                if (event.device.mode == 3) {
                    // 设备删除
                } else if (event.device.mode == 2) {
                    // 设备离线
                    updateMode();
                } else if (event.device.mode == 1) {
                    updateMode();
                    // 更新上线
                } else if (event.device.mode == 0) {
                    EndpointParser.parse(event.device, new EndpointParser.ParserCallback() {
                        @Override
                        public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                            if (cluster.clusterId == 0x0201) {
                                updateInfo(attribute);
                            }
                        }
                    });
                    updateMode();
                }
            }
        } else if (event.device == null) {
            updateMode();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceInfoChanged(DeviceInfoChangedEvent event) {
        if (event.deviceInfoBean != null && mDevice != null) {
            if (TextUtils.equals(event.deviceInfoBean.devID, mDevice.devID)) {
                if (event.deviceInfoBean.mode == 2) {
                    mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                    textName.setText(DeviceInfoDictionary.getNameByTypeAndName(mDevice.type, event.deviceInfoBean.name));
                    setRoomName();
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRoomInfoEvent(RoomInfoEvent event) {
        mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
        setRoomName();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetRoomListEvent(GetRoomListEvent event) {
        mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
        setRoomName();
    }

    @Override
    public void onClick(View v) {
        JSONArray ja = null;
        switch (v.getId()) {
            case R.id.switch_btn:
                iv_switch_btn.setEnabled(false);
                ja = new JSONArray();
                switch (openState) {
                    case 0:
                        ja.put("1");
                        sendCmd(0x0102, ja);
                        break;
                    case 1:
                        ja.put("0");
                        sendCmd(0x0102, ja);
                        break;
                }
                pr_one_switch.setTimeout(5000);
                pr_one_switch.setState(ProgressRing.WAITING);
                pr_one_switch.setAnimatorListener(new ProgressRing.AnimatorListener() {
                    @Override
                    public void onTimeout() {
                        iv_switch_btn.setEnabled(true);
                    }

                    @Override
                    public void onEnd() {
                        iv_switch_btn.setEnabled(true);
                    }
                });
                break;
            case R.id.temperature_add_btn:
                try {
                    float temperature = Float.valueOf(tv_temperature.getText().toString());
                    //离开模式温度上限30
//                    if (leave == 1 && temperature >= 30.0){
//                        return;
//                    }
                    //单地板模式温度上限45
                    if ((leave == 0  && mode == 3 )&& temperature >= 45.0){
                        return;
                    }
                    //室内、双温双控模式温度上限35
                    if ((leave == 0  && (mode == 1 || mode == 2) )&& temperature >= 35.0){
                        return;
                    }
                    temperature += 0.5;
                    tv_temperature.setText(temperature + "");
                    String tempFormat = "" + temperature;
                    if (temperature<10){
                        tempFormat = "00"+(int)(temperature*10);
                    }else{
                        tempFormat = "0"+(int)(temperature*10);
                    }
                    ja = new JSONArray();
                    ja.put("" + tempFormat);

//                    //离开模式温度设置
//                    if (leave == 1){
//                        sendCmd(0x0105, ja);
//                    }
                    //单地板模式温度
                    if (leave == 0  && mode == 3){
                        sendCmd(0x0104, ja);
                    }
                    //室内、双温双控模式温度
                    if (leave == 0  && (mode == 1 || mode == 2)){
                        sendCmd(0x0103, ja);
                    }
                } catch (Exception e) {
                    WLog.e("fzm", e.getMessage());
                }
                break;
            case R.id.temperature_sub_btn:
                try {
                    float temperature = Float.valueOf(tv_temperature.getText().toString());
//                    //离开模式温度下限5
//                    if (leave == 1 && temperature <= 5.0){
//                        return;
//                    }
                    //单地板模式温度下限20
                    if ((leave == 0  && mode == 3 )&& temperature <= 20.0){
                        return;
                    }
                    //室内、双温双控模式温度下限5
                    if ((leave == 0  && (mode == 1 || mode == 2) )&& temperature <= 5.0){
                        return;
                    }
                    temperature -= 0.5;
                    tv_temperature.setText(temperature + "");
                    String tempFormat = "" + temperature;
                    if (temperature<10){
                        tempFormat = "00"+(int)(temperature*10);
                    }else{
                        tempFormat = "0"+(int)(temperature*10);
                    }
                    ja = new JSONArray();
                    ja.put("" + tempFormat);
//                    //离开模式温度设置
//                    if (leave == 1){
//                        sendCmd(0x0105, ja);
//                    }
                    //单地板模式温度
                    if (leave == 0  && mode == 3){
                        sendCmd(0x0104, ja);
                    }
                    //室内、双温双控模式温度
                    if (leave == 0  && (mode == 1 || mode == 2)){
                        sendCmd(0x0103, ja);
                    }
                } catch (Exception e) {
                    WLog.e("fzm", e.getMessage());
                }
                break;
        }
    }

    private void sendCmd(int commandId, JSONArray parameter) {
        if (mDevice.isOnLine()) {
            JSONObject object = new JSONObject();
            try {
                object.put("cmd", "501");
                object.put("gwID", mDevice.gwID);
                object.put("devID", mDevice.devID);
                object.put("commandType", 1);
                object.put("commandId", commandId);
                object.put("clusterId", 0x0201);
                if (parameter != null) {
                    object.put("parameter", parameter);
                }
                MainApplication.getApplication()
                        .getMqttManager()
                        .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}