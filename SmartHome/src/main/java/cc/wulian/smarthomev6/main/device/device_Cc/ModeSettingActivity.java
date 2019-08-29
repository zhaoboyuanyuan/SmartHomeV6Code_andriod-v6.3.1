package cc.wulian.smarthomev6.main.device.device_Cc;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

public class ModeSettingActivity extends BaseTitleActivity {
    private RelativeLayout itemFrontEdge;
    private RelativeLayout itemAfterEdge;
    private ImageView ivFrontSelected;
    private ImageView ivAfterSelected;
    private TextView tvTip1;
    private TextView tvTip2;
    private Device mDevice;
    private String deviceID;
    private String modeValue;
    private static final String LOAD_SET_VALUE = "load_set_value";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cc_mode_setting, true);
        EventBus.getDefault().register(this);
    }


    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(R.string.device_Cc_mode_set);
    }

    @Override
    protected void initView() {
        super.initView();
        itemAfterEdge = (RelativeLayout) findViewById(R.id.item_after_edge);
        itemFrontEdge = (RelativeLayout) findViewById(R.id.item_front_edge);
        ivFrontSelected = (ImageView) findViewById(R.id.iv_front_selected);
        ivAfterSelected = (ImageView) findViewById(R.id.iv_after_selected);
        tvTip1 = (TextView) findViewById(R.id.tv_tip1);
        tvTip2 = (TextView) findViewById(R.id.tv_tip2);
        tvTip1.setText("\t\t" + getString(R.string.device_Cc_mode_set_1));
        tvTip2.setText("\t\t" + getString(R.string.device_Cc_mode_set_2));
    }

    @Override
    protected void initData() {
        super.initData();
        deviceID = getIntent().getStringExtra("deviceID");
        modeValue = getIntent().getStringExtra("modeValue");
        mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
        if (TextUtils.isEmpty(modeValue)) {
            return;
        }
        updateView(modeValue);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        itemFrontEdge.setOnClickListener(this);
        itemAfterEdge.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.item_after_edge:
                sendCmd(0x8004, new JSONArray().put("0"));
                break;
            case R.id.item_front_edge:
                sendCmd(0x8004, new JSONArray().put("1"));
                break;
            default:
                break;
        }
    }

    private void updateView(String value) {
        switch (value) {
            case "0":
                ivAfterSelected.setVisibility(View.VISIBLE);
                ivFrontSelected.setVisibility(View.INVISIBLE);
                break;
            case "1":
                ivFrontSelected.setVisibility(View.VISIBLE);
                ivAfterSelected.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }

    private void sendCmd(int commandId, JSONArray parameter) {
        if (!mDevice.isOnLine()) {
            return;
        }
        ProgressDialogManager.getDialogManager().showDialog(LOAD_SET_VALUE, ModeSettingActivity.this, null, null, 5000);
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
                                if (attribute.attributeId == 0x8001) {
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
