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
 * Created by luzx on 2017/7/18
 * Function: 美标空调温控器 首页widget
 */

public class HomeWidget_82_Controller extends RelativeLayout implements IWidgetLifeCycle, View.OnClickListener {

    private static final String TAG = HomeWidget_82_Controller.class.getSimpleName();
    private Device mDevice;
    private HomeItemBean mHomeItemBean;
    private View refrigerationHeatModeView;
    private View autoModeView;
    private TextView textName, textState, textArea;
    private TextView tv_refrigeration_heat_mode, tv_refrigeration_heat_speed, tv_refrigeration_heat_temperature,
            tv_auto_mode, tv_auto_speed, tv_auto_min_temperature, tv_auto_max_temperature, tv_auto_max_temperature_format,
            tv_auto_min_temperature_format, tv_refrigeration_heat_temperature_format;
    private ImageView iv_switch_btn, auto_min_temperature_add_btn, auto_min_temperature_sub_btn, auto_max_temperature_add_btn,
            auto_max_temperature_sub_btn, refrigeration_heat_temperature_add_btn, refrigeration_heat_temperature_sub_btn;
    private ProgressRing pr_one_switch;
    private RelativeLayout title;
    private Context mContext;
    private int temperatureFormat;
    private int modeFlag;
    private float[] temperatureC = new float[]{10,10.5f,11,11.5f,12,12.5f,13,13.5f,14,14.5f,15,15.5f,16,16.5f,17,17.5f,18,18.5f,
            19,19.5f,20,20.5f,21,21.5f,22,22.5f,23,23.5f,24,24.5f,25,25.5f,26,26.5f,27,27.5f,28,28.5f,29,29.5f,30,30.5f,31,31.5f,32};
    private int[] temperatureF = new int[] {50,51,52,53,54,55,55,56,57,58,59,60,61,62,63,64,64,65,66,67,68,69,70,71,72,
            73,73,74,75,76,77,78,79,80,81,82,82,83,84,85,86,87,88,89,90};

    /**
     * 开关状态：-1 不可用，0 关，1 开
     */
    private int openState = -1;

    public HomeWidget_82_Controller(Context context) {
        super(context);
        mContext = context;
        initView(context);
    }

    public HomeWidget_82_Controller(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context);
    }

    private void initView(Context context) {
        final View rootView = LayoutInflater.from(context).inflate(R.layout.view_home_82_controller, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        textName = (TextView) rootView.findViewById(R.id.widget_title_name);
        textArea = (TextView) rootView.findViewById(R.id.widget_title_room);
        textState = (TextView) rootView.findViewById(R.id.widget_title_state);

        iv_switch_btn = (ImageView) rootView.findViewById(R.id.switch_btn);

        refrigerationHeatModeView = rootView.findViewById(R.id.refrigeration_heat_mode_layout);
        tv_refrigeration_heat_mode = (TextView) rootView.findViewById(R.id.tv_refrigeration_heat_mode);
        tv_refrigeration_heat_speed = (TextView) rootView.findViewById(R.id.tv_refrigeration_heat_speed);
        tv_refrigeration_heat_temperature = (TextView) rootView.findViewById(R.id.tv_refrigeration_heat_temperature);
        tv_refrigeration_heat_temperature_format = (TextView) rootView.findViewById(R.id.tv_refrigeration_heat_temperature_format);

        autoModeView = rootView.findViewById(R.id.auto_mode_layout);
        tv_auto_mode = (TextView) rootView.findViewById(R.id.tv_auto_mode);
        tv_auto_speed = (TextView) rootView.findViewById(R.id.tv_auto_speed);
        tv_auto_min_temperature = (TextView) rootView.findViewById(R.id.tv_auto_min_temperature);
        tv_auto_max_temperature = (TextView) rootView.findViewById(R.id.tv_auto_max_temperature);
        tv_auto_max_temperature_format = (TextView) rootView.findViewById(R.id.tv_auto_max_temperature_format);
        tv_auto_min_temperature_format = (TextView) rootView.findViewById(R.id.tv_auto_min_temperature_format);

        auto_min_temperature_add_btn = (ImageView) rootView.findViewById(R.id.auto_min_temperature_add_btn);
        auto_min_temperature_sub_btn = (ImageView) rootView.findViewById(R.id.auto_min_temperature_sub_btn);
        auto_max_temperature_add_btn = (ImageView) rootView.findViewById(R.id.auto_max_temperature_add_btn);
        auto_max_temperature_sub_btn = (ImageView) rootView.findViewById(R.id.auto_max_temperature_sub_btn);
        refrigeration_heat_temperature_add_btn = (ImageView) rootView.findViewById(R.id.refrigeration_heat_temperature_add_btn);
        refrigeration_heat_temperature_sub_btn = (ImageView) rootView.findViewById(R.id.refrigeration_heat_temperature_sub_btn);
        pr_one_switch = (ProgressRing) rootView.findViewById(R.id.one_switch_progress);

        DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();
        int titleH = mDisplayMetrics.widthPixels - TypedValue.complexToDimensionPixelSize(30, mDisplayMetrics);
        textName.setMaxWidth(titleH / 2);
        textArea.setMaxWidth(titleH / 4);

        iv_switch_btn.setOnClickListener(this);
        auto_min_temperature_add_btn.setOnClickListener(this);
        auto_min_temperature_sub_btn.setOnClickListener(this);
        auto_max_temperature_add_btn.setOnClickListener(this);
        auto_max_temperature_sub_btn.setOnClickListener(this);
        refrigeration_heat_temperature_add_btn.setOnClickListener(this);
        refrigeration_heat_temperature_sub_btn.setOnClickListener(this);

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
        sendCmd(0x8010, null);
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
                        if (attribute.attributeId == 0x8001) {
                            updateBaseInfo(attribute.attributeValue);
                        }
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
     * 更新上下线
     */
    private void updateMode() {
        if (mDevice != null && mDevice.isOnLine()) {
            // 上线
            textState.setText(R.string.Device_Online);
            textState.setTextColor(getResources().getColor(R.color.colorPrimary));
            iv_switch_btn.setEnabled(true);
            auto_min_temperature_add_btn.setImageResource(R.drawable.home_weight_temperature_add);
            auto_min_temperature_sub_btn.setImageResource(R.drawable.home_weight_temperature_sub);
            auto_max_temperature_add_btn.setImageResource(R.drawable.home_weight_temperature_add);
            auto_max_temperature_sub_btn.setImageResource(R.drawable.home_weight_temperature_sub);
            refrigeration_heat_temperature_add_btn.setImageResource(R.drawable.home_weight_temperature_add);
            refrigeration_heat_temperature_sub_btn.setImageResource(R.drawable.home_weight_temperature_sub);

            tv_refrigeration_heat_mode.setTextColor(0xff000000);
            tv_refrigeration_heat_speed.setTextColor(0xff000000);
            tv_refrigeration_heat_temperature.setTextColor(0xff000000);
            tv_auto_mode.setTextColor(0xff000000);
            tv_auto_speed.setTextColor(0xff000000);
            tv_auto_min_temperature.setTextColor(0xff000000);
            tv_auto_max_temperature.setTextColor(0xff000000);
            tv_auto_max_temperature_format.setTextColor(0xff000000);
            tv_auto_min_temperature_format.setTextColor(0xff000000);
            tv_refrigeration_heat_temperature_format.setTextColor(0xff000000);
            auto_min_temperature_add_btn.setEnabled(true);
            auto_min_temperature_sub_btn.setEnabled(true);
            auto_max_temperature_add_btn.setEnabled(true);
            auto_max_temperature_sub_btn.setEnabled(true);
            refrigeration_heat_temperature_add_btn.setEnabled(true);
            refrigeration_heat_temperature_sub_btn.setEnabled(true);
        } else {
            // 离线
            openState = -1;
            textState.setText(R.string.Device_Offline);
            textState.setTextColor(getResources().getColor(R.color.newStateText));
            iv_switch_btn.setEnabled(false);

            auto_min_temperature_add_btn.setImageResource(R.drawable.home_weight_temperature_add);
            auto_min_temperature_sub_btn.setImageResource(R.drawable.home_weight_temperature_sub);
            auto_max_temperature_add_btn.setImageResource(R.drawable.home_weight_temperature_add);
            auto_max_temperature_sub_btn.setImageResource(R.drawable.home_weight_temperature_sub);
            refrigeration_heat_temperature_add_btn.setImageResource(R.drawable.home_weight_temperature_add);
            refrigeration_heat_temperature_sub_btn.setImageResource(R.drawable.home_weight_temperature_sub);

            tv_refrigeration_heat_mode.setTextColor(0xff9C9D9D);
            tv_refrigeration_heat_speed.setTextColor(0xff9C9D9D);
            tv_refrigeration_heat_temperature.setTextColor(0xff9C9D9D);
            tv_auto_mode.setTextColor(0xff9C9D9D);
            tv_auto_speed.setTextColor(0xff9C9D9D);
            tv_auto_min_temperature.setTextColor(0xff9C9D9D);
            tv_auto_max_temperature.setTextColor(0xff9C9D9D);
            tv_auto_max_temperature_format.setTextColor(0xff9C9D9D);
            tv_auto_min_temperature_format.setTextColor(0xff9C9D9D);
            tv_refrigeration_heat_temperature_format.setTextColor(0xff9C9D9D);

            auto_min_temperature_add_btn.setEnabled(false);
            auto_min_temperature_sub_btn.setEnabled(false);
            auto_max_temperature_add_btn.setEnabled(false);
            auto_max_temperature_sub_btn.setEnabled(false);
            refrigeration_heat_temperature_add_btn.setEnabled(false);
            refrigeration_heat_temperature_sub_btn.setEnabled(false);
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
                                if (attribute.attributeId == 0x8001) {
                                    updateBaseInfo(attribute.attributeValue);
                                }
                            }
                        }
                    });
                }
            }
        } else if (event.device == null) {
            updateMode();
        }
    }

    private void updateBaseInfo(String value){
        if (value != null && value.length() >= 36) {
            try {
                String switchValue = value.substring(0, 2);
                int switchFlag = Integer.valueOf(switchValue);
                switch (switchFlag) {
                    case 0:
                        openState = 0;
                        iv_switch_btn.setImageResource(R.drawable.switch_off);
                        iv_switch_btn.setBackgroundResource(R.drawable.home_widget_circle_gray);
                        break;
                    case 1:
                        openState = 1;
                        iv_switch_btn.setImageResource(R.drawable.switch_on);
                        iv_switch_btn.setBackgroundResource(R.drawable.home_widget_circle_green);
                        break;
                }
                pr_one_switch.setState(ProgressRing.FINISHED);
                String speed = value.substring(2, 4);
                int speedFlag = Integer.valueOf(speed);
                temperatureFormat = Integer.valueOf(value.substring(4, 6));
                String mode = value.substring(10, 12);
                modeFlag = Integer.valueOf(mode);
                float temperature = 0;
                switch (modeFlag){
                    case 1:
                        refrigerationHeatModeView.setVisibility(View.VISIBLE);
                        autoModeView.setVisibility(View.GONE);
                        tv_refrigeration_heat_mode.setText(R.string.Device_Widget_82_Type_Heat);
                        if(speedFlag == 1){
                            tv_refrigeration_heat_speed.setText(R.string.Device_Widget_82_Speed4);
                        }else if(speedFlag == 2){
                            tv_refrigeration_heat_speed.setText("打开");
                        }
                        String heat_temperature = value.substring(12, 16);
                        temperature = Integer.valueOf(heat_temperature, 16) / 100f;
                        if(temperatureFormat == 0){
                            tv_refrigeration_heat_temperature.setText(temperature + "");
                            tv_refrigeration_heat_temperature_format.setText("℃");
                        }else{

                            tv_refrigeration_heat_temperature.setText(toF(temperature) + "");
                            tv_refrigeration_heat_temperature_format.setText("℉");
                        }
                        break;
                    case 2:
                        refrigerationHeatModeView.setVisibility(View.VISIBLE);
                        autoModeView.setVisibility(View.GONE);
                        tv_refrigeration_heat_mode.setText(R.string.Device_Widget_82_Type_Cold);
                        if(speedFlag == 1){
                            tv_refrigeration_heat_speed.setText(R.string.Device_Widget_82_Speed4);
                        }else if(speedFlag == 2){
                            tv_refrigeration_heat_speed.setText("打开");
                        }
                        String refrigeration_temperature = value.substring(16, 20);
                        temperature = Integer.valueOf(refrigeration_temperature, 16) / 100f;
                        if(temperatureFormat == 0){
                            tv_refrigeration_heat_temperature.setText(temperature + "");
                            tv_refrigeration_heat_temperature_format.setText("℃");
                        }else{
                            tv_refrigeration_heat_temperature.setText(toF(temperature) + "");
                            tv_refrigeration_heat_temperature_format.setText("℉");
                        }
                        break;
                    case 3:
                        refrigerationHeatModeView.setVisibility(View.GONE);
                        autoModeView.setVisibility(View.VISIBLE);
                        tv_auto_mode.setText(R.string.Device_Widget_82_Type_Auto);
                        if(speedFlag == 1){
                            tv_auto_speed.setText(R.string.Device_Widget_82_Speed4);
                        }else if(speedFlag == 2){
                            tv_auto_speed.setText("打开");
                        }
                        String min_temperature = value.substring(26, 30);
                        String max_temperature = value.substring(30, 34);
                        float min = Integer.valueOf(min_temperature, 16) / 100f;
                        float max = Integer.valueOf(max_temperature, 16) / 100f;
                        if(temperatureFormat == 0){
                            tv_auto_min_temperature.setText(min + "");
                            tv_auto_max_temperature.setText(max + "");
                            tv_auto_max_temperature_format.setText("℃");
                            tv_auto_min_temperature_format.setText("℃");
                        }else{
                            tv_auto_min_temperature.setText(toF(min) + "");
                            tv_auto_max_temperature.setText(toF(max) + "");
                            tv_auto_max_temperature_format.setText("℉");
                            tv_auto_min_temperature_format.setText("℉");
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private int toF(float temperature){
        for(int i=0; i<temperatureC.length; i++){
            if(temperature == temperatureC[i]){
                return temperatureF[i];
            }
        }
        for(int i=0; i<temperatureC.length; i++){
            if(Math.round(temperature) == temperatureC[i]){
                return temperatureF[i];
            }
        }
        return -1;
    }

    private float toC(float temperature){
        for(int i=0; i<temperatureF.length; i++){
            if(temperature == temperatureF[i]){
                return temperatureC[i];
            }
        }
        return -1;
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
        String temperatureParams = null;
        float minTemperature = 0;
        float maxTemperature = 0;
        float temperature = 0;
        switch (v.getId()) {
            case R.id.switch_btn:
                iv_switch_btn.setEnabled(false);
                switch (openState) {
                    case 0:
                        sendCmd(1, null);
                        break;
                    case 1:
                        sendCmd(0, null);
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
            case R.id.auto_min_temperature_add_btn:
                try {
                    minTemperature = Float.valueOf(tv_auto_min_temperature.getText().toString());
                    maxTemperature = Float.valueOf(tv_auto_max_temperature.getText().toString());
                    if(temperatureFormat == 0){
                        minTemperature += 0.5;
                        if(maxTemperature - minTemperature < 3){
                            return;
                        }
                    }else{
                        minTemperature += 1;
                        if(maxTemperature - minTemperature < 6){
                            return;
                        }
                        minTemperature = toC(minTemperature);
                        maxTemperature = toC(maxTemperature);
                    }
                    if(minTemperature == -1 || minTemperature > 32){
                        return;
                    }
//                    tv_auto_min_temperature.setText(minTemperature + "");
                    ja = new JSONArray();
                    temperatureParams = "31"+ transformTemperature(minTemperature) + transformTemperature(maxTemperature);
                    ja.put(temperatureParams);
                    sendCmd(0x8012, ja);
                } catch (Exception e) {
                    WLog.e("luzx", e.getMessage());
                }
                break;
            case R.id.auto_min_temperature_sub_btn:
                try {
                    minTemperature = Float.valueOf(tv_auto_min_temperature.getText().toString());
                    maxTemperature = Float.valueOf(tv_auto_max_temperature.getText().toString());
                    if(temperatureFormat == 0){
                        minTemperature -= 0.5;
                    }else{
                        minTemperature -= 1;
                        minTemperature = toC(minTemperature);
                        maxTemperature = toC(maxTemperature);
                    }
                    if(minTemperature == -1 || minTemperature < 10){
                        return;
                    }
//                    tv_auto_min_temperature.setText(minTemperature + "");
                    ja = new JSONArray();
                    temperatureParams = "31"+ transformTemperature(minTemperature) + transformTemperature(maxTemperature);
                    ja.put(temperatureParams);
                    sendCmd(0x8012, ja);
                } catch (Exception e) {
                    WLog.e("luzx", e.getMessage());
                }
                break;
            case R.id.auto_max_temperature_add_btn:
                try {
                    minTemperature = Float.valueOf(tv_auto_min_temperature.getText().toString());
                    maxTemperature = Float.valueOf(tv_auto_max_temperature.getText().toString());
                    if(temperatureFormat == 0){
                        maxTemperature += 0.5;
                    }else{
                        maxTemperature += 1;
                        maxTemperature = toC(maxTemperature);
                        minTemperature = toC(minTemperature);
                    }
                    if(maxTemperature == -1 || maxTemperature > 32){
                        return;
                    }
//                    tv_auto_max_temperature.setText(maxTemperature + "");
                    ja = new JSONArray();
                    temperatureParams = "31"+ transformTemperature(minTemperature) + transformTemperature(maxTemperature);;
                    ja.put(temperatureParams);
                    sendCmd(0x8012, ja);
                } catch (Exception e) {
                    WLog.e("luzx", e.getMessage());
                }
                break;
            case R.id.auto_max_temperature_sub_btn:
                try {
                    minTemperature = Float.valueOf(tv_auto_min_temperature.getText().toString());
                    maxTemperature = Float.valueOf(tv_auto_max_temperature.getText().toString());
                    if(temperatureFormat == 0){
                        maxTemperature -= 0.5;
                        if(maxTemperature - minTemperature < 3){
                            return;
                        }
                    }else{
                        maxTemperature -= 1;
                        if(maxTemperature - minTemperature < 6){
                            return;
                        }
                        maxTemperature = toC(maxTemperature);
                        minTemperature = toC(minTemperature);
                    }
                    if(maxTemperature == -1 || maxTemperature < 10){
                        return;
                    }
//                    tv_auto_max_temperature.setText(maxTemperature + "");
                    ja = new JSONArray();
                    temperatureParams = "31"+ transformTemperature(minTemperature) + transformTemperature(maxTemperature);
                    ja.put(temperatureParams);
                    sendCmd(0x8012, ja);
                } catch (Exception e) {
                    WLog.e("luzx", e.getMessage());
                }
                break;
            case R.id.refrigeration_heat_temperature_add_btn:
                try {
                    temperature = Float.valueOf(tv_refrigeration_heat_temperature.getText().toString());
                    if(temperatureFormat == 0){
                        temperature += 0.5;
                    }else{
                        temperature += 1;
                        temperature = toC(temperature);
                    }
                    if(temperature == -1 || temperature > 32){
                        return;
                    }
//                    tv_refrigeration_heat_temperature.setText(temperature + "");
                    ja = new JSONArray();
                    if(modeFlag == 1){
                        temperatureParams = "11" + transformTemperature(temperature) + "0000";
                    }else if(modeFlag == 2){
                        temperatureParams = "210000" + transformTemperature(temperature);
                    }
                    ja.put(temperatureParams);
                    sendCmd(0x8012, ja);
                } catch (Exception e) {
                    WLog.e("luzx", e.getMessage());
                }
                break;
            case R.id.refrigeration_heat_temperature_sub_btn:
                try {
                    temperature = Float.valueOf(tv_refrigeration_heat_temperature.getText().toString());
                    if(temperatureFormat == 0){
                        temperature -= 0.5;
                    }else{
                        temperature -= 1;
                        temperature = toC(temperature);
                    }
                    if(temperature == -1 || temperature < 10){
                        return;
                    }
//                    tv_refrigeration_heat_temperature.setText(temperature + "");
                    ja = new JSONArray();
                    if(modeFlag == 1){
                        temperatureParams = "11" + transformTemperature(temperature) + "0000";
                    }else if(modeFlag == 2){
                        temperatureParams = "210000" + transformTemperature(temperature);
                    }
                    ja.put(temperatureParams);
                    sendCmd(0x8012, ja);
                } catch (Exception e) {
                    WLog.e("luzx", e.getMessage());
                }
                break;
        }
    }

    private int transformTemperature(float temperature){
        return (int) (temperature * 100);
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
                if (parameter != null) {
                    object.put("parameter", parameter);
                }
                object.put("clusterId", 0x0201);
                MainApplication.getApplication()
                        .getMqttManager()
                        .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
