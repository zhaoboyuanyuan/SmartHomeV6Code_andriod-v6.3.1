package cc.wulian.smarthomev6.main.device.device_91;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.DeviceMoreActivity;
import cc.wulian.smarthomev6.support.core.device.Attribute;
import cc.wulian.smarthomev6.support.core.device.Cluster;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.device.Endpoint;
import cc.wulian.smarthomev6.support.core.device.EndpointParser;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.customview.ColorBrightSwitch;
import cc.wulian.smarthomev6.support.customview.ColorTempSwitch;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;

public class Device91Activity extends BaseTitleActivity {

    private String deviceID;
    private Device mDevice;
    private ColorBrightSwitch colorBrightSwitch;
    private ColorTempSwitch colorTempSwitch;
    private View relative_offline;
    private String colorTemp;//色温
    private String colorBright;//亮度

    public static void start(Context context, String deviceID) {
        Intent intent = new Intent(context, Device91Activity.class);
        intent.putExtra("deviceID", deviceID);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_91, true);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initView() {
        colorBrightSwitch = (ColorBrightSwitch) findViewById(R.id.color_bright);
        colorTempSwitch = findView(R.id.color_temp);
        relative_offline = findViewById(R.id.relative_offline);
        colorBrightSwitch.setTouchMove(true);
        colorTempSwitch.setTouchMove(true);

        colorTempSwitch.setOnSwitchListener(new ColorTempSwitch.OnSwitchListener() {
            @Override
            public void onSwitch() {
                Log.i(TAG, "onSwitch: " + colorBrightSwitch.getPercent());
                if (colorTempSwitch.getPercent() > 0) {
                } else {
//                    colorTempSwitch.waiting(true);
                }
            }

            @Override
            public void onRegulate(float percent) {
                Log.i(TAG, "onRegulate: " + String.format("%.0f", percent * 510));
                if (colorTempSwitch.getPercent() > 0) {
                    sendCmd(0x0001, String.format("%.0f", percent * 510));
                }
            }
        });
        colorBrightSwitch.setOnSwitchListener(new ColorBrightSwitch
                .OnSwitchListener() {
            @Override
            public void onSwitch() {
                Log.i(TAG, "onSwitch: " + colorBrightSwitch.getPercent());
                if (colorBrightSwitch.getPercent() > 0) {
                    sendCmd(0x0002, "0");
                } else {
//                    colorBrightSwitch.waiting(true);
                    sendCmd(0x0002, "255");
                }
            }

            @Override
            public void onRegulate(float percent) {
                Log.i(TAG, "onRegulate: " + String.format("%.0f", percent * 255));
                if (colorBrightSwitch.getPercent() > 0) {
                    sendCmd(0x0002, String.format("%.0f", percent * 255));
                }
            }
        });
        updateMode();
    }

    @Override
    protected void initData() {
        super.initData();
        sendCmd(0x0003, null);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        deviceID = getIntent().getStringExtra("deviceID");
        mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
        setToolBarTitleAndRightImg(DeviceInfoDictionary.getNameByDevice(mDevice), R.drawable.icon_more);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && mDevice != null) {
            if (TextUtils.equals(event.device.devID, mDevice.devID)) {
                if (event.device.mode == 0) {
                    dealDevice(event.device);
                } else if (event.device.mode == 1) {
                    updateMode();
                    sendCmd(0x0003, null);
                } else if (event.device.mode == 2) {
                    updateMode();
                }
            }
        }
    }

    private void dealDevice(Device device) {
        EndpointParser.parse(device, new EndpointParser.ParserCallback() {
            @Override
            public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                String attributeValue = attribute.attributeValue;
                if (attribute.attributeId == 0x0001) {
                    colorTemp = attributeValue;
                    Log.i(TAG, "色温: " + colorTemp);
                    updateColorTempSwitch();
                } else if (attribute.attributeId == 0x0002) {
                    colorBright = attributeValue;
                    Log.i(TAG, "亮度：" + colorBright);
                    updateColorBrightSwitch();
                }
            }
        });
    }

    private void updateColorTempSwitch() {
        colorTempSwitch.waiting(false);
        if (!TextUtils.isEmpty(colorTemp)) {
            try {
                int result = Integer.valueOf(colorTemp);
                colorTempSwitch.setPercent(result / 510f);
            } catch (Exception e) {
            }
        }
    }

    private void updateColorBrightSwitch() {
        colorBrightSwitch.waiting(false);
        if (!TextUtils.isEmpty(colorBright)) {
            try {
                int result = Integer.valueOf(colorBright);
                colorBrightSwitch.setPercent(result / 255f);
            } catch (Exception e) {
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceInfoChanged(DeviceInfoChangedEvent event) {
        if (event.deviceInfoBean != null && mDevice != null) {
            if (TextUtils.equals(event.deviceInfoBean.devID, mDevice.devID)) {
                mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                if (event.deviceInfoBean.mode == 2) {
                    setToolBarTitleAndRightImg(DeviceInfoDictionary.getNameByDevice(mDevice), R.drawable.icon_more);
                }
            }
        }
    }

    /**
     * 更新上下线
     */
    private void updateMode() {
        if (mDevice != null && mDevice.isOnLine()) {
            // 上线
            colorBrightSwitch.setEnabled(true);
            relative_offline.setVisibility(View.INVISIBLE);
        } else {
            // 离线
            colorBrightSwitch.setPercent(0);
            colorBrightSwitch.setEnabled(false);
            relative_offline.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.img_right:
                startMore();
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
            object.put("commandType", 1);
            object.put("commandId", commandId);
            object.put("clusterId", 0x0008);
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

    /**
     * 设置更多信息
     */
    private void startMore() {
        Intent intent = new Intent(Device91Activity.this, DeviceMoreActivity.class);
        intent.putExtra(DeviceMoreActivity.KEY_DEVICE_ID, deviceID);
        startActivity(intent);
    }
}
