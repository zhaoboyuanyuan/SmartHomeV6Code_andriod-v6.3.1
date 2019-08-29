package cc.wulian.smarthomev6.main.device.gateway_mini.device_d8;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceCache;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.WLog;
import cc.wulian.wrecord.utils.Preferences;

/**
 * 报时设置
 */
public class DeviceD8CountFragment extends Fragment implements ToggleButton.OnCheckedChangeListener, View.OnClickListener {

    private static final String TAG = "DeviceD8CountFragment";
    private LinearLayout llWholeAlarm;
    private LinearLayout llAlarmTime;
    private LinearLayout llAlarmVoice;
    private ToggleButton tbAlarm;

    private String deviceID;
    private Device mDevice;
    private String attributeValue;
    private String param;

    public static DeviceD8CountFragment start(String did) {
        DeviceD8CountFragment f = new DeviceD8CountFragment();
        Bundle b = new Bundle();
        b.putString("deviceID", did);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceID = getArguments().getString("deviceID");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_device_d8_count, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initListener();
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView(View view) {
        llAlarmTime = (LinearLayout) view.findViewById(R.id.ll_alarm_time);
        llAlarmVoice = (LinearLayout) view.findViewById(R.id.ll_alarm_voice);
        llWholeAlarm = (LinearLayout) view.findViewById(R.id.ll_integral_alarm);
        tbAlarm = (ToggleButton) view.findViewById(R.id.tb_alarm);
    }

    private void initListener() {
        llAlarmTime.setOnClickListener(this);
        llWholeAlarm.setOnClickListener(this);
        llAlarmVoice.setOnClickListener(this);
        tbAlarm.setOnCheckedChangeListener(this);
    }

    private void initData() {
        mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
    }

    private void dealDevice(String json) {
        try {
            JSONObject object = new JSONObject(json);
            JSONArray endpoints = object.getJSONArray("endpoints");
            JSONArray clusters = ((JSONObject) endpoints.get(0)).optJSONArray("clusters");
            JSONArray attributes = ((JSONObject) clusters.get(0)).optJSONArray("attributes");
            attributeValue = ((JSONObject) attributes.get(0)).optString("attributeValue");
            int attributeId = ((JSONObject) attributes.get(0)).optInt("attributeId");
            updateView(attributeValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * abxxxxxx
     * a表示开关：0 关 1开
     * b表示声音模式：1：语音 2：滴滴 3：波动 4：布谷
     * xxxxxx表示播报时间
     *
     * @param attributeValue
     */
    private void updateView(String attributeValue) {
        if (!TextUtils.isEmpty(attributeValue)) {
            if (attributeValue.substring(0, 1).equals("0")) {
                tbAlarm.setChecked(false);
            } else if (attributeValue.substring(0, 1).equals("1")) {
                tbAlarm.setChecked(true);
            }
        }
    }


    private void sendCmd(String param) {
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", mDevice.gwID);
            object.put("devID", deviceID);
            object.put("cluster", 0x0500);
            object.put("endpointNumber", 1);
            object.put("endpointType", 0x0402);
            object.put("commandType", 1);
            object.put("commandId", 0x8003);
            JSONArray array = new JSONArray();
            array.put(param);
            object.put("parameter", array);

            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.tb_alarm:
                if (isChecked) {
                    llAlarmVoice.setVisibility(View.VISIBLE);
                    llAlarmTime.setVisibility(View.VISIBLE);
                    param = 1 + attributeValue.substring(1, attributeValue.length());
                } else {
                    llAlarmVoice.setVisibility(View.GONE);
                    llAlarmTime.setVisibility(View.GONE);
                    param = 0 + attributeValue.substring(1, attributeValue.length());
                }
                sendCmd(param);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_alarm_time:
                startActivity(new Intent(getActivity(), DeviceD8AlarmTimeActivity.class)
                        .putExtra("deviceID", deviceID)
                        .putExtra("attributeValue", attributeValue));
                break;
            case R.id.ll_alarm_voice:
                startActivity(new Intent(getActivity(), DeviceD8AlarmVoiceActivity.class)
                        .putExtra("deviceID", deviceID)
                        .putExtra("attributeValue", attributeValue));
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        WLog.i(TAG, "onDeviceReport: deviceID = " + event.device.devID);
        if (event.device != null && mDevice != null) {
            if (TextUtils.equals(event.device.devID, mDevice.devID)) {
                mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                dealDevice(event.device.data);
            }
        }
    }


}
