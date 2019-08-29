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


import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

/**
 * created by huxc  on 2018/4/8.
 * func：智能水阀
 * email: hxc242313@qq.com
 */

public class HomeWidget_28_WaterValve extends RelativeLayout implements IWidgetLifeCycle, View.OnClickListener {
    private static final String SWITCH_STATE_ON = "1";
    private static final String SWITCH_STATE_OFF = "0";
    private Context mContext;
    private Device mDevice;
    private HomeItemBean mHomeItemBean;
    private ImageView ivWaterValveState;
    private ImageView ivSwitchState;
    private ProgressRing prSwitch;

    private TextView textName, textState, textArea;
    private String switchState;

    public HomeWidget_28_WaterValve(Context context) {
        super(context);
        mContext = context;
        initView(context);
    }

    public HomeWidget_28_WaterValve(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context);
    }

    private void initView(Context context) {
        final View rootView = LayoutInflater.from(context).inflate(R.layout.view_home_28_water_valve, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        textName = (TextView) rootView.findViewById(R.id.widget_title_name);
        textArea = (TextView) rootView.findViewById(R.id.widget_title_room);
        textState = (TextView) rootView.findViewById(R.id.widget_title_state);
        ivWaterValveState = (ImageView) findViewById(R.id.img_left);
        ivSwitchState = (ImageView) findViewById(R.id.img_right);
        prSwitch = (ProgressRing) rootView.findViewById(R.id.one_switch_progress);

        rootView.findViewById(R.id.root_view).setOnClickListener(this);

        DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();
        int titleH = mDisplayMetrics.widthPixels - TypedValue.complexToDimensionPixelSize(30, mDisplayMetrics);
        textName.setMaxWidth(titleH / 2);
        textArea.setMaxWidth(titleH / 4);

        ivSwitchState.setOnClickListener(this);
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
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    private void setName() {
        if (mDevice == null) {
            textName.setText(DeviceInfoDictionary.getNameByTypeAndName(mHomeItemBean.getName(), mHomeItemBean.getType()));
        } else {
            textName.setText(DeviceInfoDictionary.getNameByTypeAndName(mDevice.type, mDevice.name));
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
        } else {
            // 离线
            textState.setText(R.string.Device_Offline);
            textState.setTextColor(getResources().getColor(R.color.newStateText));
        }
    }

    /**
     * 设置分区名
     */
    private void setRoomName() {
        String areaName = ((MainApplication) getContext().getApplicationContext()).getRoomCache().getRoomName(mDevice);
        textArea.setText(areaName);
    }
    private void initShowData() {
        switchState = null;
        if(mDevice.mode == 2){
            // 设备离线
            updateMode();
        }else{
            EndpointParser.parse(mDevice, new EndpointParser.ParserCallback() {
                @Override
                public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                    if (cluster.clusterId == 6) {
                        if (attribute.attributeId == 0) {
                            switchState = attribute.attributeValue;
                        }
                    }
                }
            });
            // 更新上线
            updateMode();
        }
        if(!TextUtils.isEmpty(switchState)){
            updateState(switchState);
        }
    }



    /**
     * 更新状态
     *
     * @param attributeValue
     */
    private void updateState(String attributeValue) {
        switch (attributeValue) {
            case SWITCH_STATE_OFF:
                ivSwitchState.setImageResource(R.drawable.device_water_valve_switch_off);
                ivWaterValveState.setImageResource(R.drawable.icon_water_valve_off);
                break;
            case SWITCH_STATE_ON:
                ivSwitchState.setImageResource(R.drawable.device_water_valve_switch_on);
                ivWaterValveState.setImageResource(R.drawable.icon_water_valve_on);
                break;
        }

    }


    private void sendCmd(int commandId) {
        if (mDevice.isOnLine()) {
            JSONObject object = new JSONObject();
            object.put("cmd", "501");
            object.put("gwID", mDevice.gwID);
            object.put("devID", mDevice.devID);
            object.put("commandType", 1);
            object.put("clusterId", 6);
            object.put("commandId", commandId);
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
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
                    EndpointParser.parse(event.device, new EndpointParser.ParserCallback() {
                        @Override
                        public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                            if (cluster.clusterId == 6) {
                                if (attribute.attributeId == 0) {
                                    switchState = attribute.attributeValue;
                                    prSwitch.setState(ProgressRing.FINISHED);
                                }
                            }
                        }
                    });
                    updateMode();
                    if(!TextUtils.isEmpty(switchState)){
                        updateState(switchState);
                    }
                    // 更新上线
                } else if (event.device.mode == 0) {
                    EndpointParser.parse(event.device, new EndpointParser.ParserCallback() {
                        @Override
                        public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                            if (cluster.clusterId == 6) {
                                if (attribute.attributeId == 0) {
                                    switchState = attribute.attributeValue;
                                    prSwitch.setState(ProgressRing.FINISHED);
                                }
                            }
                        }
                    });
                    if(!TextUtils.isEmpty(switchState)){
                        updateState(switchState);
                    }
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_right:
                if (!TextUtils.isEmpty(switchState)) {
                    if (switchState.equals("1")) {
                        sendCmd(0);
                    } else {
                        sendCmd(1);
                    }
                }
                ivSwitchState.setEnabled(false);
                prSwitch.setTimeout(8000);
                prSwitch.setState(ProgressRing.WAITING);
                prSwitch.setAnimatorListener(new ProgressRing.AnimatorListener() {
                    @Override
                    public void onTimeout() {
                        ivSwitchState.setEnabled(true);
                    }

                    @Override
                    public void onEnd() {
                        ivSwitchState.setEnabled(true);
                    }
                });
                break;
        }

    }
}
