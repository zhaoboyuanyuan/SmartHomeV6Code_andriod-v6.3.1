package cc.wulian.smarthomev6.main.home.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.device_Cc.RegulateSwitchCcActivity;
import cc.wulian.smarthomev6.support.core.device.Attribute;
import cc.wulian.smarthomev6.support.core.device.Cluster;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.device.Endpoint;
import cc.wulian.smarthomev6.support.core.device.EndpointParser;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.customview.RegulateSwitch;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GetRoomListEvent;
import cc.wulian.smarthomev6.support.event.RoomInfoEvent;

/**
 * Created by huxc on 2019/02/15
 * Function: 致敬系列调光开关 首页widget
 */

public class HomeWidget_Cc_RegulateSwitch extends RelativeLayout implements IWidgetLifeCycle, View.OnClickListener {

    private static final String TAG = HomeWidget_Cc_RegulateSwitch.class.getSimpleName();
    private Context mContext;
    private TextView tv_name, tv_sate, tv_room;
    private RegulateSwitch regulate_switch;
    private Device mDevice;

    public HomeWidget_Cc_RegulateSwitch(Context context) {
        super(context);
        mContext = context;
        initView(context);
    }

    public HomeWidget_Cc_RegulateSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context);
    }

    private void initView(Context context) {
        final View rootView = LayoutInflater.from(context).inflate(R.layout.view_home_cc_regulate_switch, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tv_name = (TextView) rootView.findViewById(R.id.widget_title_name);
        tv_room = (TextView) rootView.findViewById(R.id.widget_title_room);
        tv_sate = (TextView) rootView.findViewById(R.id.widget_title_state);
        regulate_switch = (RegulateSwitch) rootView.findViewById(R.id.regulate_switch);
        regulate_switch.setShowBrightness(false);
        DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();
        int titleH = mDisplayMetrics.widthPixels - TypedValue.complexToDimensionPixelSize(30, mDisplayMetrics);
        tv_name.setMaxWidth(titleH / 2);
        tv_room.setMaxWidth(titleH / 4);
        findViewById(R.id.root_view).setOnClickListener(this);
        regulate_switch.setOnSwitchListener(new RegulateSwitch.OnSwitchListener() {
            @Override
            public void onSwitch() {
                if (regulate_switch.getPercent() > 0) {
                    sendCmd(0x00, null);
                } else {
//                    regulate_switch.waiting(true);
                    sendCmd(0x01, null);
                }
            }

            @Override
            public void onRegulate(float percent) {
                JSONArray ja = new JSONArray();
                if (regulate_switch.getPercent() > 0) {
                    ja.put("" + (percent * 100));
                }
                sendCmd(0x08, ja);
            }
        });
    }

    @Override
    public void onBindViewHolder(HomeItemBean bean) {
        EventBus.getDefault().register(this);
        if (bean == null) {
            return;
        }
        mDevice = MainApplication.getApplication().getDeviceCache().get(bean.getWidgetID());
        // 设置在线离线的状态
        updateMode();
        setName();
        setRoomName();
        initShowData();
        sendCmd(0x8009, null);
    }

    private void initShowData() {
        if (mDevice.mode == 0) {
            EndpointParser.parse(mDevice, new EndpointParser.ParserCallback() {
                @Override
                public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                    if (cluster.clusterId == 0x0008) {
                        if (attribute.attributeId == 0x0011) {
                            if (!TextUtils.isEmpty(attribute.attributeValue)) {
                                int value = Integer.valueOf(attribute.attributeValue);
                                regulate_switch.setPercent(value / 100f);
                            }
                        }
                    }
                }
            });
        } else if (mDevice.mode == 2) {
            // 设备离线
            updateMode();
        } else if (mDevice.mode == 1) {
            // 更新上线
            updateMode();
            EndpointParser.parse(mDevice, new EndpointParser.ParserCallback() {
                @Override
                public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                    if (cluster.clusterId == 0x0008) {
                        if (attribute.attributeId == 0x0011) {
                            if (!TextUtils.isEmpty(attribute.attributeValue)) {
                                int value = Integer.valueOf(attribute.attributeValue);
                                regulate_switch.setPercent(value / 100f);
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    private void setName() {
        String name = DeviceInfoDictionary.getNameByDevice(mDevice);
        tv_name.setText(name);
    }

    /**
     * 设置分区名
     */
    private void setRoomName() {
        String areaName = ((MainApplication) getContext().getApplicationContext()).getRoomCache().getRoomName(mDevice.roomID);
        tv_room.setText(areaName);
    }

    /**
     * 更新上下线
     */
    private void updateMode() {
        if (mDevice != null && mDevice.isOnLine()) {
            // 上线
            tv_sate.setText(R.string.Device_Online);
            regulate_switch.setEnabled(true);
            tv_sate.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else {
            // 离线
            tv_sate.setText(R.string.Device_Offline);
            tv_sate.setTextColor(getResources().getColor(R.color.newStateText));
            regulate_switch.setPercent(0);
            regulate_switch.setEnabled(false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && mDevice != null) {
            if (TextUtils.equals(event.device.devID, mDevice.devID)) {
                mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                if (event.device.mode == 0) {
                    EndpointParser.parse(event.device, new EndpointParser.ParserCallback() {
                        @Override
                        public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                            if (cluster.clusterId == 0x0008) {
                                if (attribute.attributeId == 0x0011) {
                                    regulate_switch.waiting(false);
                                    if (!TextUtils.isEmpty(attribute.attributeValue)) {
                                        float percent = Integer.valueOf(attribute.attributeValue) / 100f;
                                        if (percent != regulate_switch.getPercent()) {
                                            regulate_switch.setPercent(percent);
                                        }
                                    }
                                }
                            }
                        }
                    });
                } else if (event.device.mode == 2) {
                    // 设备离线
                    updateMode();
                } else if (event.device.mode == 1) {
                    // 更新上线
                    updateMode();
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceInfoChanged(DeviceInfoChangedEvent event) {
        if (event.deviceInfoBean != null && mDevice != null) {
            if (TextUtils.equals(event.deviceInfoBean.devID, mDevice.devID)) {
                mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                if (event.deviceInfoBean.mode == 2) {
                    setRoomName();
                    setName();
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
        switch (v.getId()) {
            case R.id.root_view:
                RegulateSwitchCcActivity.start(mContext, mDevice.devID);
                break;
        }
    }

    private void sendCmd(int commandId, JSONArray parameter) {
        if (!mDevice.isOnLine()) {
            return;
        }
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
            object.put("clusterId", 0x0008);
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
