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
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GetRoomListEvent;
import cc.wulian.smarthomev6.support.event.RoomInfoEvent;

/**
 * Created by luzx on 2017/8/24
 * Function: LED炫彩灯 首页widget
 */

public class HomeWidget_90_LED extends RelativeLayout implements IWidgetLifeCycle, View.OnClickListener {

    private static final String TAG = HomeWidget_90_LED.class.getSimpleName();
    private Device mDevice;
    private HomeItemBean mHomeItemBean;
    private ImageView iv_off_btn, iv_colorful_btn, iv_sunlight_btn, iv_blue_btn, iv_green_btn, iv_red_btn;
    private TextView textName, textState, textArea;
    private String bule = "1397EF";
    private String green = "8DD652";
    private String red = "F35927";
    private int mode;//1：关闭2：炫彩3：暖白4：蓝色5：绿色6：红色

    public HomeWidget_90_LED(Context context) {
        super(context);
        initView(context);
    }

    public HomeWidget_90_LED(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        final View rootView = LayoutInflater.from(context).inflate(R.layout.view_home_90_led, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        textName = (TextView) rootView.findViewById(R.id.widget_title_name);
        textArea = (TextView) rootView.findViewById(R.id.widget_title_room);
        textState = (TextView) rootView.findViewById(R.id.widget_title_state);

        iv_off_btn = (ImageView) rootView.findViewById(R.id.off_btn);
        iv_colorful_btn = (ImageView) rootView.findViewById(R.id.colorful_btn);
        iv_sunlight_btn = (ImageView) rootView.findViewById(R.id.sunlight_btn);
        iv_blue_btn = (ImageView) rootView.findViewById(R.id.blue_btn);
        iv_green_btn = (ImageView) rootView.findViewById(R.id.green_btn);
        iv_red_btn = (ImageView) rootView.findViewById(R.id.red_btn);

        DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();
        int titleH = mDisplayMetrics.widthPixels - TypedValue.complexToDimensionPixelSize(30, mDisplayMetrics);
        textName.setMaxWidth(titleH / 2);
        textArea.setMaxWidth(titleH / 4);

        iv_off_btn.setOnClickListener(this);
        iv_colorful_btn.setOnClickListener(this);
        iv_sunlight_btn.setOnClickListener(this);
        iv_blue_btn.setOnClickListener(this);
        iv_green_btn.setOnClickListener(this);
        iv_red_btn.setOnClickListener(this);
    }

    @Override
    public void onBindViewHolder(HomeItemBean bean) {
        EventBus.getDefault().register(this);
        if (bean == null) {
            return;
        }
        mHomeItemBean = HomeWidgetManager.get(bean.getWidgetID());
        mDevice = MainApplication.getApplication().getDeviceCache().get(bean.getWidgetID());
        // 设置在线离线的状态
        updateMode();

        setName();
        setRoomName();
        initShowData();
        sendCmd(4, null);//获取所有状态信息
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    private void initShowData() {
        if(mDevice.mode == 2){
            // 设备离线
            updateMode();
        }else{
            EndpointParser.parse(mDevice, new EndpointParser.ParserCallback() {
                @Override
                public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                    if (cluster.clusterId == 8) {
                        for(Attribute attr : cluster.attributes){
                            switch (attr.attributeId){
                                case 1:
                                    if(bule.equals(attr.attributeValue.substring(0, 6))){
                                        mode = 4;
                                    }else if(green.equals(attr.attributeValue.substring(0, 6))){
                                        mode = 5;
                                    }else if(red.equals(attr.attributeValue.substring(0, 6))){
                                        mode = 6;
                                    }
                                    break;
                                case 4:
                                    if("02".equals(attr.attributeValue.substring(8, 10))){
                                        mode = 2;
                                    }else{
                                        if(bule.equals(attr.attributeValue.substring(0, 6))){
                                            mode = 4;
                                        }else if(green.equals(attr.attributeValue.substring(0, 6))){
                                            mode = 5;
                                        }else if(red.equals(attr.attributeValue.substring(0, 6))){
                                            mode = 6;
                                        }
                                        if("00".equals(attr.attributeValue.substring(6, 8))){
                                            mode = 1;
                                        }
                                    }
                                    break;
                                case 5:
                                    mode = 2;
                                    break;
                                case 6:
                                    if("00".equals(attr.attributeValue)){
                                        mode = 1;
                                    }else{
                                        mode = 3;
                                    }
                                    break;
                            }
                        }
                    }
                }
            });
            // 更新上线
            updateMode();
        }
        updateState(mode);
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
     * 更新开关状态
     */
    private void updateState(int mode) {
        switch (mode) {
            case 1:
                iv_off_btn.setImageResource(R.drawable.led_90_off_btn_selected);
                iv_colorful_btn.setImageResource(R.drawable.led_90_mode_btn);
                iv_sunlight_btn.setImageResource(R.drawable.led_90_mode_btn);
                iv_blue_btn.setImageResource(R.drawable.led_90_blue_btn);
                iv_green_btn.setImageResource(R.drawable.led_90_green_btn);
                iv_red_btn.setImageResource(R.drawable.led_90_red_btn);
                break;
            case 2:
                iv_colorful_btn.setImageResource(R.drawable.led_90_mode_btn_selected);
                iv_off_btn.setImageResource(R.drawable.led_90_off_btn);
                iv_sunlight_btn.setImageResource(R.drawable.led_90_mode_btn);
                iv_blue_btn.setImageResource(R.drawable.led_90_blue_btn);
                iv_green_btn.setImageResource(R.drawable.led_90_green_btn);
                iv_red_btn.setImageResource(R.drawable.led_90_red_btn);
                break;
            case 3:
                iv_sunlight_btn.setImageResource(R.drawable.led_90_mode_btn_selected);
                iv_colorful_btn.setImageResource(R.drawable.led_90_mode_btn);
                iv_off_btn.setImageResource(R.drawable.led_90_off_btn);
                iv_blue_btn.setImageResource(R.drawable.led_90_blue_btn);
                iv_green_btn.setImageResource(R.drawable.led_90_green_btn);
                iv_red_btn.setImageResource(R.drawable.led_90_red_btn);
                break;
            case 4:
                iv_blue_btn.setImageResource(R.drawable.led_90_blue_btn_selected);
                iv_sunlight_btn.setImageResource(R.drawable.led_90_mode_btn);
                iv_colorful_btn.setImageResource(R.drawable.led_90_mode_btn);
                iv_off_btn.setImageResource(R.drawable.led_90_off_btn);
                iv_green_btn.setImageResource(R.drawable.led_90_green_btn);
                iv_red_btn.setImageResource(R.drawable.led_90_red_btn);
                break;
            case 5:
                iv_green_btn.setImageResource(R.drawable.led_90_green_btn_selected);
                iv_blue_btn.setImageResource(R.drawable.led_90_blue_btn);
                iv_sunlight_btn.setImageResource(R.drawable.led_90_mode_btn);
                iv_colorful_btn.setImageResource(R.drawable.led_90_mode_btn);
                iv_off_btn.setImageResource(R.drawable.led_90_off_btn);
                iv_red_btn.setImageResource(R.drawable.led_90_red_btn);
                break;
            case 6:
                iv_red_btn.setImageResource(R.drawable.led_90_red_btn_selected);
                iv_green_btn.setImageResource(R.drawable.led_90_green_btn);
                iv_blue_btn.setImageResource(R.drawable.led_90_blue_btn);
                iv_sunlight_btn.setImageResource(R.drawable.led_90_mode_btn);
                iv_colorful_btn.setImageResource(R.drawable.led_90_mode_btn);
                iv_off_btn.setImageResource(R.drawable.led_90_off_btn);
                break;
        }
    }

    /**
     * 更新上下线
     */
    private void updateMode() {
        if (mDevice != null && mDevice.isOnLine()) {
            // 上线
            textState.setText(R.string.Device_Online);
            textState.setTextColor(getResources().getColor(R.color.colorPrimary));
            iv_off_btn.setEnabled(true);
            iv_colorful_btn.setEnabled(true);
            iv_sunlight_btn.setEnabled(true);
            iv_blue_btn.setEnabled(true);
            iv_green_btn.setEnabled(true);
            iv_red_btn.setEnabled(true);
        } else {
            // 离线
            textState.setText(R.string.Device_Offline);
            textState.setTextColor(getResources().getColor(R.color.newStateText));
            iv_off_btn.setEnabled(false);
            iv_colorful_btn.setEnabled(false);
            iv_sunlight_btn.setEnabled(false);
            iv_blue_btn.setEnabled(false);
            iv_green_btn.setEnabled(false);
            iv_red_btn.setEnabled(false);
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
                    sendCmd(4, null);//获取所有状态信息
                    // 更新上线
                } else if (event.device.mode == 0) {
                    EndpointParser.parse(event.device, new EndpointParser.ParserCallback() {
                        @Override
                        public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                            if (cluster.clusterId == 8) {
                                for(Attribute attr : cluster.attributes){
                                    switch (attr.attributeId){
                                        case 1:
                                            if(bule.equals(attr.attributeValue.substring(0, 6))){
                                                mode = 4;
                                            }else if(green.equals(attr.attributeValue.substring(0, 6))){
                                                mode = 5;
                                            }else if(red.equals(attr.attributeValue.substring(0, 6))){
                                                mode = 6;
                                            }
                                            break;
                                        case 4:
                                            if(attr.attributeValue.length() == 10){
                                                if("02".equals(attr.attributeValue.substring(8, 10))){
                                                    mode = 2;
                                                }else{
                                                    if(bule.equals(attr.attributeValue.substring(0, 6))){
                                                        mode = 4;
                                                    }else if(green.equals(attr.attributeValue.substring(0, 6))){
                                                        mode = 5;
                                                    }else if(red.equals(attr.attributeValue.substring(0, 6))){
                                                        mode = 6;
                                                    }
                                                    if("00".equals(attr.attributeValue.substring(6, 8))){
                                                        mode = 1;
                                                    }
                                                }
                                            }
                                            break;
                                        case 5:
                                            mode = 2;
                                            break;
                                        case 6:
                                            if("00".equals(attr.attributeValue)){
                                                mode = 1;
                                            }else{
                                                mode = 3;
                                            }
                                            break;
                                    }
                                }
                            }
                        }
                    });
                    //必须先更新开关状态，再更新数值，因为实时功率的显示依赖开关状态
                    updateState(mode);
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
        JSONArray ja = new JSONArray();
        switch (v.getId()) {
            case R.id.off_btn:
//                iv_off_btn.setEnabled(false);
                ja.put("000");
                sendCmd(2, ja);
                sendCmd(6, ja);
                break;
            case R.id.colorful_btn:
//                iv_colorful_btn.setEnabled(false);
                ja.put("1");
                sendCmd(5, ja);
                break;
            case R.id.sunlight_btn:
//                iv_sunlight_btn.setEnabled(false);
                ja.put("255");
                sendCmd(6, ja);
                break;
            case R.id.blue_btn:
//                iv_blue_btn.setEnabled(false);
                ja.put("019151239255");
                sendCmd(1, ja);
                break;
            case R.id.green_btn:
//                iv_green_btn.setEnabled(false);
                ja.put("141214082255");
                sendCmd(1, ja);
                break;
            case R.id.red_btn:
//                iv_red_btn.setEnabled(false);
                ja.put("243089039255");
                sendCmd(1, ja);
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
                object.put("endpointNumber", 1);
                object.put("commandType", 1);
                object.put("clusterId", 8);
                object.put("commandId", commandId);
                if(parameter != null){
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
