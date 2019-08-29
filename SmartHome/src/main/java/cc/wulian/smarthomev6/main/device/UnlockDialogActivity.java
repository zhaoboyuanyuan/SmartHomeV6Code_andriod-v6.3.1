package cc.wulian.smarthomev6.main.device;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceRelationBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceRelationListBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.VibratorUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by zbl on 2017/6/15.
 * 摄像机开锁界面
 * 支持所有门锁
 */

public class UnlockDialogActivity extends BaseActivity implements View.OnClickListener {

    private static final String KEY_DEVICE_ID = "deviceId";

    private View[] btn_number = new View[10];
    private View btn_exit;
    private ImageView btn_unlock;
    private TextView btn_clear;
    private TextView tv_content;

    private String deviceId;
    private Device device;

    public static void startByCondition(Context context, DeviceRelationListBean bean, boolean isShared) {
        if (bean != null && bean.deviceRelation != null && bean.deviceRelation.size() > 0) {
            DeviceRelationBean relationBean = bean.deviceRelation.get(0);

            if (isShared) {
                List<String> gatewayIdList = Preference.getPreferences().getCurrentGatewayList();
                boolean hasGateway = false;
                for (String gatewayId : gatewayIdList) {
                    if (TextUtils.equals(gatewayId, relationBean.gatewayId)) {
                        hasGateway = true;
                        break;
                    }
                }
                if (!hasGateway) {
                    ToastUtil.show(R.string.Lookever_Not_Bind_Share);
                    return;
                }
            }

            if (TextUtils.equals(Preference.getPreferences().getCurrentGatewayID(), relationBean.gatewayId)) {
                Device device = MainApplication.getApplication().getDeviceCache().get(relationBean.targetDeviceid);
                if (device != null) {
                    if (device.isOnLine()) {
                        UnlockDialogActivity.start(context, device.devID);
                    } else {
                        ToastUtil.show(R.string.Lookever_Lock_Offline);
                    }
                } else {
                    if (isShared) {
                        ToastUtil.show(R.string.Lookever_Not_Bind_Share);
                    } else {
                        ToastUtil.show(R.string.Lookever_Lock_Deleted);
                    }
                }
            } else {
                ToastUtil.show(R.string.Lookever_Lock_Cannot_Open);
            }
        } else {
            ToastUtil.show(R.string.Lookever_Not_Bind_lock);
        }
    }

    public static void start(Context context, String deviceId) {
        Intent intent = new Intent(context, UnlockDialogActivity.class);
        intent.putExtra(KEY_DEVICE_ID, deviceId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock);
        EventBus.getDefault().register(this);
        initView();
        initData();
        initListeners();
    }

    @Override
    public boolean enableSwipeBack() {
        return false;
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    protected void initView() {
        tv_content = (TextView) findViewById(R.id.tv_content);

        btn_number[0] = findViewById(R.id.btn_number0);
        btn_number[1] = findViewById(R.id.btn_number1);
        btn_number[2] = findViewById(R.id.btn_number2);
        btn_number[3] = findViewById(R.id.btn_number3);
        btn_number[4] = findViewById(R.id.btn_number4);
        btn_number[5] = findViewById(R.id.btn_number5);
        btn_number[6] = findViewById(R.id.btn_number6);
        btn_number[7] = findViewById(R.id.btn_number7);
        btn_number[8] = findViewById(R.id.btn_number8);
        btn_number[9] = findViewById(R.id.btn_number9);

        btn_clear = (TextView) findViewById(R.id.btn_clear);
        btn_unlock = (ImageView) findViewById(R.id.btn_unlock);
        btn_exit = findViewById(R.id.btn_exit);
        btn_unlock.setEnabled(false);
        btn_clear.setEnabled(false);
    }

    protected void initData() {
        deviceId = getIntent().getStringExtra(KEY_DEVICE_ID);
        device = MainApplication.getApplication().getDeviceCache().get(deviceId);
        if (device == null) {
            ToastUtil.show(R.string.http_error_20110);
            finish();
        }
    }

    protected void initListeners() {
        int tag = 0;
        for (View view : btn_number) {
            VibratorUtil.holdSpeakVibration();
            view.setOnClickListener(numberClickListener);
            view.setTag(tag);
            tag += 1;
        }
        btn_clear.setOnClickListener(this);
        btn_unlock.setOnClickListener(this);
        btn_exit.setOnClickListener(this);
        tv_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    btn_unlock.setImageResource(R.drawable.ic_lock_open_nor);
                    btn_unlock.setEnabled(false);
                    btn_clear.setTextColor(getResources().getColor(R.color.device_search_text_color));
                    btn_clear.setEnabled(false);

                } else {
                    btn_unlock.setImageResource(R.drawable.ic_lock_open);
                    btn_unlock.setEnabled(true);
                    btn_clear.setTextColor(getResources().getColor(R.color.colorPrimary));
                    btn_clear.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    View.OnClickListener numberClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int number = (int) v.getTag();
            tv_content.append(Integer.toString(number));
        }
    };

    @Override
    public void onClick(View v) {
        if (v == btn_clear) {
            tv_content.setText("");
        } else if (v == btn_unlock) {
            sendUnlockData();
        } else if (v == btn_exit) {
            finish();
        }
    }

    private void sendUnlockData() {
        if (TextUtils.equals("70", device.type)) {
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(
                            MQTTCmdHelper.createUnlock(
                                    device.gwID,
                                    device.devID,
                                    tv_content.getText().toString().trim()),
                            MQTTManager.MODE_GATEWAY_FIRST);

            MainApplication.getApplication().getMqttManager().publishEncryptedMessage(
                    MQTTCmdHelper.createGatewayConfig(
                            device.gwID,
                            3,
                            MainApplication.getApplication().getLocalInfo().appID,
                            device.devID,
                            null,
                            null,
                            null
                    ), MQTTManager.MODE_GATEWAY_FIRST
            );
        } else if (TextUtils.equals("OP", device.type) || TextUtils.equals("Bc", device.type)
                || device.type.equals("Bg") || device.type.equals("Bd")
                || device.type.equals("OW") || device.type.equals("Bf")
                || device.type.equals("Bq")) {
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(
                            MQTTCmdHelper.createUnlock(
                                    device.gwID,
                                    device.devID,
                                    tv_content.getText().toString().trim()),
                            MQTTManager.MODE_GATEWAY_FIRST);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && device != null) {
            if (TextUtils.equals(event.device.devID, device.devID)) {
                WLog.i(TAG, "开锁有回复了: " + event.device.data);
                dealDevice(event.device);
            }
        }
    }

    /**
     * 解析设备数据
     */
    private void dealDevice(Device device) {
        updateMode(device.mode);
        if (device.mode == 3) {
            // 设备删除
        } else if (device.mode == 2) {
            // 设备离线
        } else if (device.mode == 1) {
            // 更新上线
            dealData(device.data);
        } else if (device.mode == 0) {
            dealData(device.data);
        }
    }


    private void dealData(String data) {
        try {
            JSONObject object = new JSONObject(data);
            String type = object.getString("type");
            JSONArray endpoints = object.getJSONArray("endpoints");
            JSONArray clusters = ((JSONObject) endpoints.get(0)).getJSONArray("clusters");
            JSONArray attributes = ((JSONObject) clusters.get(0)).getJSONArray("attributes");
            String attributeValue = ((JSONObject) attributes.get(0)).getString("attributeValue");
            int attributeId = ((JSONObject) attributes.get(0)).getInt("attributeId");
            // 更新状态
            updateState(type, attributeId, attributeValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 更新上下线
     */
    private void updateMode(int mode) {
        switch (mode) {
            case 0:
            case 4:
            case 1:
                // 上线 view更新
                break;
            case 2:
                // 离线 view更新
                finish();
                break;
            case 3:
                finish();
                break;
        }
    }

    private void updateState(String type, int attributeId, String attributeValue) {
        WLog.i(TAG, "updateState: attributeId-" + attributeId + ", attributeValue-" + attributeValue);
        if (attributeValue.isEmpty()) {
            return;
        }
        int value = 0;
        try {
            value = Integer.parseInt(attributeValue);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        WLog.i(TAG, attributeId + " " + attributeValue);
        switch (type) {
            case "70":
                switch (attributeId) {
                    case 0x0000:
                        handleAttribute_0(value);
                        break;
                    case 0x8001:
                        handleAttribute_8001(value);
                        break;
                    case 0x8002:
                        handleAttribute_8002(value);
                        break;
                    case 0x8003:
                        handleAttribute_8003(value);
                        break;
                    case 0x8004:
                        handleAttribute_8004(value);
                        break;
                }
                break;
            case "Bg":
            case "Bd":
            case "OP":
            case "Bq":
            case "Bf":
                switch (attributeId) {
                    case 0x8002:
                        handleAttribute_8002(value);
                        break;
                    case 0x8006:
                        if (value == 2) {
                            ToastUtil.show(R.string.Home_Widget_Password_Error);
                            tv_content.setText("");
                        }
                        break;
                }
                break;
            case "OW":
                switch (attributeId) {
                    case 0x8002:
                        handleAttribute_8002(value);
                        break;
                    case 0x8006:
                        if (value == 10) {
                            ToastUtil.show(R.string.Home_Widget_Password_Error);
                            tv_content.setText("");
                        }
                        break;
                }
                break;

        }

    }

    private void handleAttribute_0(int value) {
        switch (value) {
            case 1:
                // 上锁
                break;
            case 2:
                // 解锁
                break;
            case 3:
                // 上保险
                break;
            case 4:
                // 接触保险
                break;
            case 5:
                // 接触反锁
                break;
            case 6:
                // 反锁
                break;
        }
    }

    private void handleAttribute_8001(int value) {
        switch (value) {
            case 1:
                // 入侵报警
                break;
            case 2:
                // 报警解除
                break;
            case 3:
                // 破坏报警
                break;
            case 4:
                // 密码连续出错
                break;
            case 5:
                // 欠压报警
                break;
        }
    }

    private void handleAttribute_8002(int value) {
        ToastUtil.show(R.string.Home_Widget_Lock_Opened);
        // 清空输入框
        tv_content.setText("");
        finish();
    }

    private void handleAttribute_8003(int value) {
        switch (value) {
            case 1:
                // 密码校验成功
                break;
            case 2:
                // 密码校验失败
                ToastUtil.show(R.string.Home_Widget_Password_Error);
                // 清空输入框
                tv_content.setText("");
                break;
            case 3:
                // 强制上锁
                break;
            case 4:
                // 自动上锁
                break;
            case 5:
                // 登记密码
                break;
        }
    }

    private void handleAttribute_8004(int value) {
        switch (value) {
            case 1:
                // 开锁
                break;
        }
    }
}
