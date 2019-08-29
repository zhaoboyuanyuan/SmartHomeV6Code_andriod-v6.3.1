package cc.wulian.smarthomev6.main.device.device_Cc;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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

public class LightSourceSettingActivity extends BaseTitleActivity {
    private RelativeLayout itemCanAdjust;
    private RelativeLayout itemNotAdjust;
    private ImageView ivCanAdjust;
    private ImageView ivNotAdjust;
    private Device mDevice;
    private String deviceID;
    private String lightSourceValue;
    private static final String LOAD_SET_VALUE = "load_set_value";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cc_light_source_setting, true);
        EventBus.getDefault().register(this);
    }


    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(R.string.device_Cc_light_source_set);
    }

    @Override
    protected void initView() {
        super.initView();
        itemNotAdjust = (RelativeLayout) findViewById(R.id.item_not_adjust);
        itemCanAdjust = (RelativeLayout) findViewById(R.id.item_can_adjust);
        ivCanAdjust = (ImageView) findViewById(R.id.iv_can_adjust);
        ivNotAdjust = (ImageView) findViewById(R.id.iv_not_adjust);
    }

    @Override
    protected void initData() {
        super.initData();
        deviceID = getIntent().getStringExtra("deviceID");
        lightSourceValue = getIntent().getStringExtra("lightSourceValue");
        mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
        if(TextUtils.isEmpty(lightSourceValue)){
            return;
        }
        updateView(lightSourceValue);
    }


    @Override
    protected void initListeners() {
        super.initListeners();
        itemCanAdjust.setOnClickListener(this);
        itemNotAdjust.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.item_can_adjust:
                sendCmd(0x8007, new JSONArray().put("1"));
                break;
            case R.id.item_not_adjust:
                sendCmd(0x8007, new JSONArray().put("0"));
                break;
            default:
                break;
        }
    }

    private void sendCmd(int commandId, JSONArray parameter) {
        if (!mDevice.isOnLine()) {
            return;
        }
        ProgressDialogManager.getDialogManager().showDialog(LOAD_SET_VALUE, LightSourceSettingActivity.this, null, null, 5000);
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

    private void updateView(String value) {
        switch (value) {
            case "0":
                ivNotAdjust.setVisibility(View.VISIBLE);
                ivCanAdjust.setVisibility(View.INVISIBLE);
                break;
            case "1":
                ivCanAdjust.setVisibility(View.VISIBLE);
                ivNotAdjust.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
                                if (attribute.attributeId == 0x8004) {
                                    updateView(attribute.attributeValue);
                                }
                            }
                        }
                    });
                }
            }
        }
    }
}
