package cc.wulian.smarthomev6.main.device.device_Cc;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.SeekBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.device.Attribute;
import cc.wulian.smarthomev6.support.core.device.Cluster;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.Endpoint;
import cc.wulian.smarthomev6.support.core.device.EndpointParser;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;

public class RangeSettingActivity extends BaseTitleActivity implements SeekBar.OnSeekBarChangeListener {
    private static final String LOAD_SET_VALUE = "load_set_value";
    private SeekBar sbLowBright;
    private SeekBar sbHighBright;
    private TextView tvLowBright;
    private TextView tvHighBright;
    private String minValue;
    private String maxValue;
    private String deviceID;
    private Device mDevice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cc_range_setting, true);
        EventBus.getDefault().register(this);
    }


    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(R.string.device_Cc_range_set);
    }

    @Override
    protected void initView() {
        super.initView();
        sbLowBright = (SeekBar) findViewById(R.id.sb_low_bright);
        sbHighBright = (SeekBar) findViewById(R.id.sb_high_bright);
        tvHighBright = (TextView) findViewById(R.id.tv_high_progress);
        tvLowBright = (TextView) findViewById(R.id.tv_low_progress);
    }

    @Override
    protected void initData() {
        super.initData();
        deviceID = getIntent().getStringExtra("deviceID");
        minValue = getIntent().getStringExtra("minValue");
        maxValue = getIntent().getStringExtra("maxValue");
        mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
        if (TextUtils.isEmpty(maxValue) || TextUtils.isEmpty(minValue)) {
            return;
        }
        updateView(minValue, maxValue);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        sbLowBright.setOnSeekBarChangeListener(this);
        sbHighBright.setOnSeekBarChangeListener(this);
    }

    private void updateView(String minValue, String maxValue) {
        if (!TextUtils.isEmpty(minValue)) {
            sbLowBright.setProgress(Math.round(Float.valueOf(String.valueOf(Integer.parseInt(minValue) - 12)) / 23 * 100));
            tvLowBright.setText(minValue);
        }
        if ((!TextUtils.isEmpty(maxValue))) {
            sbHighBright.setProgress(Math.round(Float.valueOf(String.valueOf(Integer.parseInt(maxValue) - 40)) / 33 * 100));
            tvHighBright.setText(maxValue);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.sb_low_bright:
                tvLowBright.setText(String.valueOf(12 + Math.round(Float.parseFloat(String.valueOf(progress)) / 100 * 23)));
                break;
            case R.id.sb_high_bright:
                tvHighBright.setText(String.valueOf(40 + Math.round(Float.parseFloat(String.valueOf(progress)) / 100 * 33)));
                break;
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getId()) {
            case R.id.sb_low_bright:
                sendCmd(0x8005, new JSONArray().put(String.valueOf(12 + Math.round(Float.parseFloat(String.valueOf(seekBar.getProgress())) / 100 * 23))));
                break;
            case R.id.sb_high_bright:
                sendCmd(0x8006, new JSONArray().put(String.valueOf(40 + Math.round(Float.parseFloat(String.valueOf(seekBar.getProgress())) / 100 * 33))));
                break;
        }
    }

    private void sendCmd(int commandId, JSONArray parameter) {
        if (!mDevice.isOnLine()) {
            return;
        }
        ProgressDialogManager.getDialogManager().showDialog(LOAD_SET_VALUE, RangeSettingActivity.this, null, null, 5000);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null) {
            if (TextUtils.equals(event.device.devID, deviceID)) {
                progressDialogManager.dimissDialog(LOAD_SET_VALUE, 0);
                if (event.device.mode == 3) {
                    // 设备删除
                } else if (event.device.mode == 2) {
                    // 设备离线
                } else if (event.device.mode == 1) {
                    // 更新上线
                } else if (event.device.mode == 0) {
                    EndpointParser.parse(event.device, new EndpointParser.ParserCallback() {
                        @Override
                        public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                            if (cluster.clusterId == 0x0008) {
                                if (attribute.attributeId == 0x8005) {
                                    updateView(attribute.attributeValue, null);
                                } else if (attribute.attributeId == 0x8006) {
                                    updateView(null, attribute.attributeValue);
                                }
                            }
                        }
                    });
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
