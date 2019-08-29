package cc.wulian.smarthomev6.main.device.device_13;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.customview.RegulateSwitch;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

public class RegulateSwitch13Activity extends BaseTitleActivity {

    private String deviceID;
    private Device mDevice;
    private RegulateSwitch regulate_switch_1;
    private RegulateSwitch regulate_switch_2;
    private View relative_offline;
    private TextView tvSwitch1;
    private TextView tvSwitch2;
    private ImageView ivEdit1;
    private ImageView ivEdit2;
    private WLDialog dialog;
    private WLDialog.Builder builder;
    private String switch1Name;
    private String switch2Name;


    public static void start(Context context, String deviceID) {
        Intent intent = new Intent(context, RegulateSwitch13Activity.class);
        intent.putExtra("deviceID", deviceID);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_13, true);
        EventBus.getDefault().register(this);
    }


    @Override
    protected void initListeners() {
        super.initListeners();
        ivEdit1.setOnClickListener(this);
        ivEdit2.setOnClickListener(this);
    }

    @Override
    protected void initView() {
        regulate_switch_1 = (RegulateSwitch) findViewById(R.id.regulate_switch_1);
        regulate_switch_2 = (RegulateSwitch) findViewById(R.id.regulate_switch_2);
        relative_offline = findViewById(R.id.relative_offline);
        tvSwitch1 = (TextView) findViewById(R.id.tv_switch_1);
        tvSwitch2 = (TextView) findViewById(R.id.tv_switch_2);
        ivEdit1 = (ImageView) findViewById(R.id.iv_edit_1);
        ivEdit2 = (ImageView) findViewById(R.id.iv_edit_2);
        regulate_switch_1.setTouchMove(true);
        regulate_switch_2.setTouchMove(true);
        regulate_switch_1.setOnSwitchListener(new RegulateSwitch.OnSwitchListener() {
            @Override
            public void onSwitch() {
                JSONArray ja = new JSONArray();
                if (regulate_switch_1.getPercent() > 0) {
                    ja.put("0");
                } else {
                    ja.put("100");
                }
                sendCmd(1, ja);
            }

            @Override
            public void onRegulate(float percent) {
                JSONArray ja = new JSONArray();
                if (regulate_switch_1.getPercent() > 0) {
                    ja.put(String.format("%.0f", percent * 100));
                }
                sendCmd(1, ja);
            }
        });
        regulate_switch_2.setOnSwitchListener(new RegulateSwitch.OnSwitchListener() {
            @Override
            public void onSwitch() {
                JSONArray ja = new JSONArray();
                if (regulate_switch_2.getPercent() > 0) {
                    ja.put("0");
                } else {
                    ja.put("100");
                }
                sendCmd(2, ja);
            }

            @Override
            public void onRegulate(float percent) {
                JSONArray ja = new JSONArray();
                if (regulate_switch_2.getPercent() > 0) {
                    ja.put(String.format("%.0f", percent * 100));
                }
                sendCmd(2, ja);
            }
        });
        initShowData();

    }

    @Override
    protected void initTitle() {
        super.initTitle();
        deviceID = getIntent().getStringExtra("deviceID");
        mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
        setToolBarTitleAndRightImg(DeviceInfoDictionary.getNameByDevice(mDevice), R.drawable.icon_more);
    }

    private void initShowData() {
        if (mDevice.mode == 0) {
            EndpointParser.parse(mDevice, new EndpointParser.ParserCallback() {
                @Override
                public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                    if (cluster.clusterId == 0x0008) {
                        if (attribute.attributeId == 0x0011) {
                            if (!TextUtils.isEmpty(attribute.attributeValue)) {
                                dealData(endpoint.endpointNumber, endpoint.endpointName, attribute.attributeValue);
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
                                dealData(endpoint.endpointNumber, endpoint.endpointName, attribute.attributeValue);
                            }
                        }
                    }
                }
            });
        }
    }

    private void dealData(int endpointNumber, String endpointName, String attributeValue) {
        int value = Integer.valueOf(attributeValue);
        switch (endpointNumber) {
            case 1:
                if (TextUtils.isEmpty(endpointName)) {
                    switch1Name = getString(R.string.Home_Widget_Switch) + endpointNumber;
                } else {
                    switch1Name = endpointName;
                }
                regulate_switch_1.waiting(false);
                regulate_switch_1.setPercent(value / 100f);
                tvSwitch1.setText(switch1Name);
                break;
            case 2:
                if (TextUtils.isEmpty(endpointName)) {
                    switch2Name = getString(R.string.Home_Widget_Switch) + endpointNumber;
                } else {
                    switch2Name = endpointName;
                }
                regulate_switch_2.waiting(false);
                regulate_switch_2.setPercent(value / 100f);
                tvSwitch2.setText(switch2Name);
                break;
        }

    }

    private void showChangeNameDialog(final int endpointNumber, final String endpointName) {
        builder = new WLDialog.Builder(this);
        builder.setTitle(getString(R.string.Device_Rename))
                .setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setEditTextHint(R.string.EditText_Device_Nick)
                .setEditTextText(endpointName)
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .hasSocketView(false)
                .hasCurtainView(false)
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        if (TextUtils.isEmpty(msg.trim())) {
                            ToastUtil.show(R.string.Mine_Rename_Empty);
                            return;
                        }
                        changeName(endpointNumber, msg);
                        dialog.dismiss();
                    }

                    @Override
                    public void onClickNegative(View view) {
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    private void changeName(int endpointNumber, String endpointName) {
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "502");
            object.put("gwID", mDevice.gwID);
            object.put("devID", mDevice.devID);
            object.put("mode", 2);
            object.put("endpointNumber", endpointNumber);
            object.put("endpointName", endpointName);
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
        if (event.device != null && mDevice != null) {
            if (TextUtils.equals(event.device.devID, mDevice.devID)) {
                mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                if (event.device.mode == 0) {
                    EndpointParser.parse(event.device, new EndpointParser.ParserCallback() {
                        @Override
                        public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                            if (cluster.clusterId == 0x0008) {
                                if (attribute.attributeId == 0x0011) {
                                    if (!TextUtils.isEmpty(attribute.attributeValue)) {
                                        dealData(endpoint.endpointNumber, endpoint.endpointName, attribute.attributeValue);
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
                    EndpointParser.parse(MainApplication.getApplication().getDeviceCache().get(mDevice.devID), new EndpointParser.ParserCallback() {
                        @Override
                        public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                            if (cluster.clusterId == 0x0008) {
                                if (attribute.attributeId == 0x0011) {
                                    if (!TextUtils.isEmpty(attribute.attributeValue)) {
                                        dealData(endpoint.endpointNumber, endpoint.endpointName, attribute.attributeValue);
                                    }
                                }
                            }
                        }
                    });
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
            regulate_switch_1.setEnabled(true);
            regulate_switch_2.setEnabled(true);
            relative_offline.setVisibility(View.INVISIBLE);
        } else {
            // 离线
            regulate_switch_1.setPercent(0);
            regulate_switch_1.setEnabled(false);
            regulate_switch_2.setPercent(0);
            regulate_switch_2.setEnabled(false);
            relative_offline.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.img_right:
                startMore();
                break;
            case R.id.iv_edit_1:
                showChangeNameDialog(1, switch1Name);
                break;
            case R.id.iv_edit_2:
                showChangeNameDialog(2, switch2Name);
                break;
        }
    }

    private void sendCmd(int endpointNumber, JSONArray parameter) {
        if (!mDevice.isOnLine()) {
            return;
        }
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", mDevice.gwID);
            object.put("devID", mDevice.devID);
            object.put("commandType", 1);
            object.put("endpointNumber", endpointNumber);
            object.put("endpointNumber", endpointNumber);
            object.put("commandId", 0x08);
            if (parameter != null) {
                object.put("parameter", parameter);
            }
            object.put("clusterId", 0x08);
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
        Intent intent = new Intent(RegulateSwitch13Activity.this, DeviceMoreActivity.class);
        intent.putExtra(DeviceMoreActivity.KEY_DEVICE_ID, deviceID);
        startActivity(intent);
    }
}
