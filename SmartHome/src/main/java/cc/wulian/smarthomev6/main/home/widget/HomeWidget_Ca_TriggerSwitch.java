package cc.wulian.smarthomev6.main.home.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
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
 * Created by hxc on 2017/7/13.
 * Func:    金属窗帘首页widget
 * Email:   hxc242313@qq.com
 */

public class HomeWidget_Ca_TriggerSwitch extends RelativeLayout implements IWidgetLifeCycle, View.OnClickListener {
    private ImageView ivTurnOn;
    private ImageView ivTurnOff;
    private ImageView ivStop;
    private TextView textName, textArea, textState;
    private RelativeLayout title;
    private ImageView ivBindMode;
    private Device mDevice;
    private int mode;
    private Context mContext;
    private LinearLayout layoutCurtainMode;

    public HomeWidget_Ca_TriggerSwitch(Context context) {
        super(context);
        this.mContext = context;
        initView(context);
    }

    @Override
    @Subscribe
    public void onBindViewHolder(HomeItemBean bean) {
        EventBus.getDefault().register(this);

        mDevice = MainApplication.getApplication().getDeviceCache().get(bean.getWidgetID());
        mode = mDevice.mode;
        updateMode();
        setName();
        setRoomName();
        sendCmd(0x02, null);
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);

    }

    private void initView(Context context) {
        final View rootView = LayoutInflater.from(context).inflate(R.layout.view_home_ca_trigger_switch, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ivTurnOn = (ImageView) rootView.findViewById(R.id.iv_turn_on);
        ivTurnOff = (ImageView) rootView.findViewById(R.id.iv_turn_off);
        ivStop = (ImageView) rootView.findViewById(R.id.iv_stop);
        ivBindMode = (ImageView) rootView.findViewById(R.id.iv_bind_mode);
        textName = (TextView) rootView.findViewById(R.id.widget_title_name);
        textState = (TextView) rootView.findViewById(R.id.widget_title_state);
        textArea = (TextView) rootView.findViewById(R.id.widget_title_room);
        title = (RelativeLayout) rootView.findViewById(R.id.widget_relative_title);
        layoutCurtainMode = (LinearLayout) rootView.findViewById(R.id.layout_curtain_mode);
        DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();
        int titleH = mDisplayMetrics.widthPixels - TypedValue.complexToDimensionPixelSize(30, mDisplayMetrics);
        textName.setMaxWidth(titleH / 2);
        textArea.setMaxWidth(titleH / 4);

        ivTurnOn.setOnClickListener(this);
        ivTurnOff.setOnClickListener(this);
        ivStop.setOnClickListener(this);

    }

    private void setName() {
        textName.setText(DeviceInfoDictionary.getNameByDevice(mDevice));
    }

    /**
     * 设置分区名
     */
    private void setRoomName() {
        String areaName = ((MainApplication) getContext().getApplicationContext()).getRoomCache().getRoomName(mDevice.roomID);
        textArea.setText(areaName);
    }

    private void updateView(String attributeValue) {
        switch (attributeValue) {
            case "0":
                layoutCurtainMode.setVisibility(GONE);
                ivBindMode.setVisibility(VISIBLE);
                break;
            case "1":
                layoutCurtainMode.setVisibility(VISIBLE);
                ivBindMode.setVisibility(GONE);
                break;
        }
    }


    /**
     * 更新上下线
     */
    private void updateMode() {
        switch (mode) {
            case 0:
            case 4:
            case 1:
                // 上线
                ivTurnOn.setClickable(true);
                ivTurnOff.setClickable(true);
                ivStop.setClickable(true);
                textState.setText(R.string.Device_Online);
                textState.setTextColor(getResources().getColor(R.color.colorPrimary));
                ivTurnOn.setImageResource(R.drawable.home_widget_open);
                ivTurnOff.setImageResource(R.drawable.home_widget_close);
                ivStop.setImageResource(R.drawable.home_widget_stop);
                break;
            case 2:
                // 离线
                ivTurnOn.setClickable(false);
                ivTurnOff.setClickable(false);
                ivStop.setClickable(false);
                textState.setText(R.string.Device_Offline);
                textState.setTextColor(getResources().getColor(R.color.newStateText));
                ivTurnOn.setImageResource(R.drawable.icon_open_offline);
                ivTurnOff.setImageResource(R.drawable.icon_colse_offline);
                ivStop.setImageResource(R.drawable.icon_stop_offline);
                break;
            case 3:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceInfoChanged(DeviceInfoChangedEvent event) {
        if (event.deviceInfoBean != null && mDevice != null) {
            if (TextUtils.equals(event.deviceInfoBean.devID, mDevice.devID)) {
                if (event.deviceInfoBean.mode == 2) {
                    mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                    setRoomName();
                    setName();
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && mDevice != null) {
            if (TextUtils.equals(event.device.devID, mDevice.devID)) {
                setName();
                updateMode();
                EndpointParser.parse(mDevice, new EndpointParser.ParserCallback() {
                    @Override
                    public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                        if (cluster.clusterId == 0x0102) {
                            if (attribute.attributeId == 0x8002) {
                                if (!TextUtils.isEmpty(attribute.attributeValue)) {
                                    updateView(attribute.attributeValue);
                                }
                            }
                        }
                    }
                });
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
            case R.id.iv_turn_on:
                sendCmd(0x01, "2");
                break;
            case R.id.iv_turn_off:
                sendCmd(0x01, "3");
                break;
            case R.id.iv_stop:
                sendCmd(0x01, "1");
                break;
            default:
                break;
        }
    }

    private void sendCmd(int commandId, String parameter) {
        if (!mDevice.isOnLine()) {
            return;
        }
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", mDevice.gwID);
            object.put("devID", mDevice.devID);
            object.put("clusterId", 0x0102);
            object.put("commandType", 1);
            object.put("commandId", commandId);
            object.put("endpointNumber", 1);
            if (parameter != null) {
                JSONArray array = new JSONArray();
                array.put(parameter);
                object.put("parameter", array);
            }
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
