package cc.wulian.smarthomev6.main.home.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
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
 * Created by huxc on 2019/03/01.
 * Func:致敬系列2路窗帘首页widget
 */

public class HomeWidget_Cb_TriggerSwitch extends RelativeLayout implements IWidgetLifeCycle, View.OnClickListener {
    private ImageView ivTurnOn;
    private ImageView ivTurnOff;
    private ImageView ivStop;
    private TextView textName, textArea, textState;
    private RelativeLayout title;
    private Device mDevice;
    private int mode;
    private Context mContext;
    private ViewGroup switch_layout;
    private int checkPosition = 0;
    private String oneSwitchMode;
    private String twoSwitchMode;

    public HomeWidget_Cb_TriggerSwitch(Context context) {
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
        final View rootView = LayoutInflater.from(context).inflate(R.layout.view_home_cb_trigger_switch, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ivTurnOn = (ImageView) rootView.findViewById(R.id.iv_turn_on);
        ivTurnOff = (ImageView) rootView.findViewById(R.id.iv_turn_off);
        ivStop = (ImageView) rootView.findViewById(R.id.iv_stop);
        textName = (TextView) rootView.findViewById(R.id.widget_title_name);
        textState = (TextView) rootView.findViewById(R.id.widget_title_state);
        textArea = (TextView) rootView.findViewById(R.id.widget_title_room);
        title = (RelativeLayout) rootView.findViewById(R.id.widget_relative_title);
        switch_layout = (ViewGroup) rootView.findViewById(R.id.switch_layout);
        DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();
        int titleH = mDisplayMetrics.widthPixels - TypedValue.complexToDimensionPixelSize(30, mDisplayMetrics);
        textName.setMaxWidth(titleH / 2);
        textArea.setMaxWidth(titleH / 4);

        ivTurnOn.setOnClickListener(this);
        ivTurnOff.setOnClickListener(this);
        ivStop.setOnClickListener(this);
        initSwitchLayout(switch_layout, 0);
//        rootView.findViewById(R.id.root_view).setOnClickListener(this);

    }

    private void initSwitchLayout(final ViewGroup parent, final int checkPosition) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            CheckedTextView text = (CheckedTextView) parent.getChildAt(i);
            text.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("homeWidgetCb", "onClick: " + v.getTag());
                    if (TextUtils.equals("one", v.getTag().toString())) {
                        updateData(0, 1, oneSwitchMode);
                    } else if (TextUtils.equals("two", v.getTag().toString())) {
                        updateData(1, 2, twoSwitchMode);
                    }
                    for (int j = 0; j < parent.getChildCount(); j++) {
                        CheckedTextView checkedTextView = (CheckedTextView) parent.getChildAt(j);
                        if (checkedTextView.isChecked()) {
                            checkedTextView.setChecked(false);
                            checkedTextView.setTextColor(getResources().getColor(R.color.home_widget_curtain));
                        } else {
                            HomeWidget_Cb_TriggerSwitch.this.checkPosition = j;
                            checkedTextView.setChecked(true);
                            checkedTextView.setTextColor(getResources().getColor(R.color.white));
                        }
                    }
                    Log.i("homeWidgetCb", "onClick: " + checkPosition);
                }
            });
            if (i == checkPosition) {
                this.checkPosition = checkPosition;
                text.setChecked(true);
                text.setTextColor(getResources().getColor(R.color.white));
            } else {
                text.setChecked(false);
                text.setTextColor(getResources().getColor(R.color.home_widget_curtain));
            }
        }
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
                for (int i = 0; i < switch_layout.getChildCount(); i++) {
                    CheckedTextView text = (CheckedTextView) switch_layout.getChildAt(i);
                    text.setEnabled(true);
                    if (i == checkPosition) {
                        text.setChecked(true);
                        text.setTextColor(getResources().getColor(R.color.white));
                    } else {
                        text.setChecked(false);
                        text.setTextColor(getResources().getColor(R.color.home_widget_curtain));
                    }
                }
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
                for (int i = 0; i < switch_layout.getChildCount(); i++) {
                    CheckedTextView text = (CheckedTextView) switch_layout.getChildAt(i);
                    text.setEnabled(false);
                    text.setTextColor(getResources().getColor(R.color.home_widget_curtain_offline));
                }
                break;
            case 3:
                break;
        }
    }

    private void updateData(int checkPosition, int endpointNumber, String attributeValue) {
        if (endpointNumber == 1) {
            oneSwitchMode = attributeValue;
        } else if (endpointNumber == 2) {
            twoSwitchMode = attributeValue;
        }
        switch (checkPosition) {
            case 0:
                if (endpointNumber == 1) {
                    boolean isCurtainMode = TextUtils.equals(oneSwitchMode, "1");
                    ivTurnOn.setVisibility(isCurtainMode ? VISIBLE : INVISIBLE);
                    ivTurnOff.setVisibility(isCurtainMode ? VISIBLE : INVISIBLE);
                    if (isCurtainMode) {
                        ivStop.setClickable(true);
                        if (mDevice.isOnLine()) {
                            ivStop.setImageResource(R.drawable.home_widget_stop);
                        } else {
                            ivStop.setImageResource(R.drawable.icon_stop_offline);
                        }
                    } else {
                        ivStop.setClickable(false);
                        ivStop.setImageResource(R.drawable.icon_mode_not_curtain);
                    }

                }
                break;
            case 1:
                if (endpointNumber == 2) {
                    boolean isCurtainMode = TextUtils.equals(twoSwitchMode, "1");
                    ivTurnOn.setVisibility(isCurtainMode ? VISIBLE : INVISIBLE);
                    ivTurnOff.setVisibility(isCurtainMode ? VISIBLE : INVISIBLE);
                    if (isCurtainMode) {
                        ivStop.setClickable(true);
                        ivStop.setImageResource(R.drawable.home_widget_stop);
                    } else {
                        ivStop.setClickable(false);
                        ivStop.setImageResource(R.drawable.icon_mode_not_curtain);
                    }
                }
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
                mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                setName();
                updateMode();
                EndpointParser.parse(mDevice, new EndpointParser.ParserCallback() {
                    @Override
                    public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                        if (cluster.clusterId == 0x0102) {
                            if (attribute.attributeId == 0x8002) {
                                if (!TextUtils.isEmpty(attribute.attributeValue)) {
                                    updateData(checkPosition, endpoint.endpointNumber, attribute.attributeValue);
                                    Log.i("homeWidgetCb", "checkPosition = " + checkPosition + ",endpointNumber = " + endpoint.endpointNumber + ",attribute = " + attribute);
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
            if (checkPosition == 0) {
                object.put("endpointNumber", 1);
            } else if (checkPosition == 1) {
                object.put("endpointNumber", 2);
            }
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
