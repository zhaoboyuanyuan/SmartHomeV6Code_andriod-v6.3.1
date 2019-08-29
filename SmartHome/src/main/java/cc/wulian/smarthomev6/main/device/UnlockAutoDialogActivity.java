package cc.wulian.smarthomev6.main.device;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.VibratorUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by zbl on 2017/6/15.
 * 可视门锁开锁界面,自动开锁
 */

public class UnlockAutoDialogActivity extends BaseActivity implements View.OnClickListener {

    private static final String KEY_DEVICE_ID = "deviceId";

    private View[] btn_number = new View[10];
    private View btn_clear, btn_delpw, btn_exit;
    private TextView tv_notice;
    private View tv_content;

    private String deviceId;
    private Device device;

    public static void start(Context context, String deviceId) {
        Intent intent = new Intent(context, UnlockAutoDialogActivity.class);
        intent.putExtra(KEY_DEVICE_ID, deviceId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlockauto);
        EventBus.getDefault().register(this);
        initView();
        initPwViews();
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
//        tv_content = findViewById(R.id.tv_content);

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

        btn_clear = findViewById(R.id.btn_clear);
        btn_delpw = findViewById(R.id.btn_delpw);
        btn_exit = findViewById(R.id.btn_exit);
        tv_notice = (TextView) findViewById(R.id.tv_notice);
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
            view.setOnClickListener(numberClickListener);
            view.setTag(tag);
            tag += 1;
        }
        btn_clear.setOnClickListener(this);
        btn_delpw.setOnClickListener(this);
        btn_exit.setOnClickListener(this);
    }

    View.OnClickListener numberClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            VibratorUtil.holdSpeakVibration();
            int number = (int) v.getTag();
            addValuesToList(Integer.toString(number));
        }
    };

    @Override
    public void onClick(View v) {
        if (v == btn_clear) {
            clearPw();
        } else if (v == btn_delpw) {
            deletePwValue();
        } else if (v == btn_exit) {
            finish();
        }
    }

    private void sendUnlockData() {
        String fullpwValue = getFullPwValue();
        if (device != null && !StringUtil.isNullOrEmpty(fullpwValue) && fullpwValue.length() == 6) {
            if (TextUtils.equals("70", device.type)) {
                MainApplication.getApplication()
                        .getMqttManager()
                        .publishEncryptedMessage(
                                MQTTCmdHelper.createUnlock(
                                        device.gwID,
                                        device.devID,
                                        fullpwValue),
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
            } else if (TextUtils.equals("OP", device.type) || TextUtils.equals("Bc", device.type) || TextUtils.equals("Bn", device.type)) {
                MainApplication.getApplication()
                        .getMqttManager()
                        .publishEncryptedMessage(
                                MQTTCmdHelper.createUnlock(
                                        device.gwID,
                                        device.devID,
                                        fullpwValue),
                                MQTTManager.MODE_GATEWAY_FIRST);
            }
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
            JSONArray endpoints = object.getJSONArray("endpoints");
            JSONArray clusters = ((JSONObject) endpoints.get(0)).getJSONArray("clusters");
            JSONArray attributes = ((JSONObject) clusters.get(0)).getJSONArray("attributes");
            String attributeValue = ((JSONObject) attributes.get(0)).getString("attributeValue");
            int attributeId = ((JSONObject) attributes.get(0)).getInt("attributeId");
            // 更新状态
            updateState(attributeId, attributeValue);
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

    private void updateState(int attributeId, String attributeValue) {
        WLog.i(TAG, "updateState: attributeId-" + attributeId + ", attributeValue-" + attributeValue);
        if (attributeValue.isEmpty()) {
            return;
        }
        int value = Integer.parseInt(attributeValue);
        WLog.i(TAG, attributeId + " " + attributeValue);

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
            case 0x8006:
                handleAttribute_8006(value);
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
//        tv_content.setText("");
        clearPw();
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
//                tv_content.setText("");
                clearPw();
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


    private void handleAttribute_8006(int value) {
        String errormsg = "";
        switch (value) {
            case 1: {
                errormsg = getString(R.string.Device_BcRemind_01);//"无线端密码开锁失败!";
            }
            case 2: {
                errormsg = getString(R.string.Device_BcRemind_02);//"开锁密码验证错误";
            }
            case 5: {
                errormsg = getString(R.string.Device_Vidicon_AdministratorPassworderror);
            }
            case 6: {
                errormsg = getString(R.string.Device_BcRemind_03);//"动态密码失败";
            }
            default: {
                errormsg = getString(R.string.Home_Widget_Password_Error);//"操作失败";
            }
        }
//        ToastUtil.single(errormsg);
        tv_notice.setText(errormsg);
        clearPw();
//        tv_notice.setTextColor(getResources().getColor(R.color.red));
//        delayClearNotice();
    }

    private void delayClearNotice() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                tv_notice.setText(R.string.GatewayChangePwd_NewPwd_Hint);
                tv_notice.setTextColor(getResources().getColor(R.color.red));
            }
        }, 3000);
    }

    List<String> pwValues = null;
    List<View> pwViews = null;

    private void initPwViews() {
        if (pwValues == null) {
            pwValues = new ArrayList<>();
        }
        pwValues.clear();
        if (pwViews == null) {
            pwViews = new ArrayList<>();
        }
        pwViews.clear();
        pwViews.add(findViewById(R.id.pwView1));
        pwViews.add(findViewById(R.id.pwView2));
        pwViews.add(findViewById(R.id.pwView3));
        pwViews.add(findViewById(R.id.pwView4));
        pwViews.add(findViewById(R.id.pwView5));
        pwViews.add(findViewById(R.id.pwView6));
    }

    private void clearPw() {
        pwValues.clear();
        for (View view : pwViews) {
            view.setSelected(false);
        }
    }

    private void addValuesToList(String pwValue) {
        if (!StringUtil.isNullOrEmpty(pwValue) && pwValues.size() < 6) {
            pwValues.add(pwValue);
            int len = pwValues.size();
            pwViews.get(len - 1).setSelected(true);
            if (len == 6) {
                sendUnlockData();
            }
        }
    }

    private String getFullPwValue() {
        String fullPwValue = "";
        if (pwValues.size() == 6) {
            for (String pwValue : pwValues) {
                fullPwValue += pwValue;
            }
        }
        return fullPwValue;
    }

    private void deletePwValue() {
        if (pwValues != null && pwValues.size() > 0) {
            pwValues.remove(pwValues.size() - 1);
            int len = pwValues.size();
            pwViews.get(len).setSelected(false);
        }
    }

}
