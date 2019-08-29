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
 * Created by Wulian on 2018/8/14.
 */

public class HomeWidget_Br_Controller extends RelativeLayout implements IWidgetLifeCycle, View.OnClickListener {

    private static final String TAG = HomeWidget_Br_Controller.class.getSimpleName();
    private Device mDevice;
    private HomeItemBean mHomeItemBean;

    private TextView textName, textState, textArea;
    private TextView tv_mode, tv_speed, tv_temperature, tv_temperature_format;
    private ImageView iv_switch_btn, temperature_add_btn, temperature_sub_btn;
    private ProgressRing pr_one_switch;
    private RelativeLayout title;
    private Context mContext;
    private int mode;

    /**
     * 开关状态：0 关，1 开
     */
    private int openState = 0;
    /**
     * 模式：1 制热，2 制冷，3通风
     */
    private int mModeState = 0;

    public HomeWidget_Br_Controller(Context context) {
        super(context);
        mContext = context;
        initView(context);
    }

    public HomeWidget_Br_Controller(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context);
    }

    private void initView(Context context) {
        final View rootView = LayoutInflater.from(context).inflate(R.layout.view_home_br_controller, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        textName = (TextView) rootView.findViewById(R.id.widget_title_name);
        textArea = (TextView) rootView.findViewById(R.id.widget_title_room);
        textState = (TextView) rootView.findViewById(R.id.widget_title_state);

        tv_mode = (TextView) rootView.findViewById(R.id.tv_mode);
        tv_speed = (TextView) rootView.findViewById(R.id.tv_speed);
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
        if (!mDevice.isOnLine()) {
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
    private int getModeResByType(int mode) {
        /**
         0：制热
         1：制冷
         2：通风
         */
        int modeRes = -1;
        switch (mode) {
            case 0:
                modeRes = R.string.Device_Widget_Hot_Ok;
                break;
            case 1:
                modeRes = R.string.Device_Widget_Cold_Ok;
                break;
            case 2:
                modeRes = R.string.Device_Widget_Wind_Ok;
                break;
            default:
                modeRes = R.string.Home_Widget_Unknownstate;
                break;
        }
        return modeRes;
    }

    /**
     * 获取资源
     */
    private int getSpeedResByType(int speed) {
        /**
         0：关闭
         1：低
         2：中
         3：高
         4：自动
         */
        int speedRes = 0;
        switch (speed) {
            case 0:
                speedRes = R.string.Device_Widget_82_Speed5;
                break;
            case 1:
                speedRes = R.string.Device_Widget_Oi_Speed3;
                break;
            case 2:
                speedRes = R.string.Device_Widget_Oi_Speed2;
                break;
            case 3:
                speedRes = R.string.Device_Widget_Oi_Speed1;
                break;
            case 4:
                speedRes = R.string.Device_Widget_Oi_Speed4;
                break;
            default:
                speedRes = R.string.Home_Widget_Unknownstate;
                break;
        }
        return speedRes;
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
            tv_speed.setTextColor(0xff000000);
            tv_temperature.setTextColor(0xff000000);
            tv_temperature_format.setTextColor(0xff000000);
            iv_switch_btn.setEnabled(true);
            if(openState == 1 && (mode == 1 || mode == 0)){
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
            if (openState == 1 && mModeState == 2){
                tv_temperature.setText("--");
                tv_temperature_format.setText("");
            }else if (openState == 0 && (TextUtils.equals(tv_temperature.getText(),"--"))){
                tv_temperature.setText("--");
                tv_temperature_format.setText("");
            }else {
                tv_temperature_format.setText("℃");
            }
        } else {
            // 离线
            textState.setText(R.string.Device_Offline);
            textState.setTextColor(getResources().getColor(R.color.newStateText));
            iv_switch_btn.setImageResource(R.drawable.switch_off);
            iv_switch_btn.setBackgroundResource(R.drawable.home_widget_circle_gray);
            temperature_sub_btn.setImageResource(R.drawable.icon_arrows_left_nor);
            temperature_add_btn.setImageResource(R.drawable.icon_arrows_right_nor);
            tv_mode.setTextColor(0xff9C9D9D);
            tv_speed.setTextColor(0xff9C9D9D);
            tv_temperature.setTextColor(0xff9C9D9D);
            tv_temperature_format.setTextColor(0xff9C9D9D);
            iv_switch_btn.setEnabled(false);
            temperature_add_btn.setEnabled(false);
            temperature_sub_btn.setEnabled(false);
            tv_temperature.setText("--");
            tv_temperature_format.setText("");
        }
    }

    private void updateInfo(Attribute attribute){
        try {
            if (attribute.attributeId == 0x8101) {
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
                        iv_switch_btn.setBackgroundResource(R.drawable.home_widget_circle_green);
                        break;
                }
                pr_one_switch.setState(ProgressRing.FINISHED);
            } else if (attribute.attributeId == 0x8104) {
                mode = Integer.valueOf(attribute.attributeValue);
                mModeState = mode;
                tv_mode.setText(getModeResByType(mode));
            } else if (attribute.attributeId == 0x8102) {
                int speed = Integer.valueOf(attribute.attributeValue);
                tv_speed.setText(getSpeedResByType(speed));
            } else if (attribute.attributeId == 0x8105){
                if (mModeState == 0){
                    tv_temperature.setText(attribute.attributeValue);
                }
            } else if (attribute.attributeId == 0x8106){
                if (mModeState == 1){
                    tv_temperature.setText(attribute.attributeValue);
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
                        sendCmd(0x0101, ja);
                        break;
                    case 1:
                        ja.put("0");
                        sendCmd(0x0101, ja);
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
                    if(temperature >= 32.0){
                        return;
                    }
                    temperature += 0.5;
                    tv_temperature.setText(temperature + "");
                    ja = new JSONArray();
                    ja.put(temperature+"");
                    if (mModeState == 0){
                        sendCmd(0x0104, ja);
                    }else if (mModeState == 1){
                        sendCmd(0x0105, ja);
                    }
                } catch (Exception e) {
                    WLog.e("fzm", e.getMessage());
                }
                break;
            case R.id.temperature_sub_btn:
                try {
                    float temperature = Float.valueOf(tv_temperature.getText().toString());
                    if(temperature <= 10.0){
                        return;
                    }
                    temperature -= 0.5;
                    tv_temperature.setText(temperature + "");
                    ja = new JSONArray();
                    ja.put(temperature+"");
                    if (mModeState == 0){
                        sendCmd(0x0104, ja);
                    }else if (mModeState == 1){
                        sendCmd(0x0105, ja);
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