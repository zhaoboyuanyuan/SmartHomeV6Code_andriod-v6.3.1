package cc.wulian.smarthomev6.main.device.camera_lc.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.UserPushInfo;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.LcDetectionSwitchBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.LcDeviceInfoBean;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

public class LcSafeProtectActivity extends BaseTitleActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private RelativeLayout rlProtectTime;
    private RelativeLayout rlAlarmPush;
    private TextView tvAlarmPush;
    private ToggleButton tbMoveDetection;
    private ToggleButton tbAlarmPush;
    private DeviceApiUnit deviceApiUnit;

    private String deviceId;
    private String channelId;

    public static void start(Context context, String deviceId, String channelId) {
        Intent intent = new Intent(context, LcSafeProtectActivity.class);
        intent.putExtra("deviceId", deviceId);
        intent.putExtra("channelId", channelId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lc_safe_protect, true);
    }

    @Override
    protected void initView() {
        super.initView();
        rlProtectTime = (RelativeLayout) findViewById(R.id.rl_protect_time);
        rlAlarmPush = (RelativeLayout) findViewById(R.id.rl_alarm_push);
        tvAlarmPush = (TextView) findViewById(R.id.tv_alarm_push);
        tbAlarmPush = (ToggleButton) findViewById(R.id.tb_alarm_push);
        tbMoveDetection = (ToggleButton) findViewById(R.id.tb_move_detection);

    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(R.string.Safety_Protection);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        tbAlarmPush.setOnCheckedChangeListener(this);
        tbMoveDetection.setOnCheckedChangeListener(this);
        rlProtectTime.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        deviceId = getIntent().getExtras().getString("deviceId");
        channelId = getIntent().getExtras().getString("channelId");
        deviceApiUnit = new DeviceApiUnit(this);
        getSwitchStatus();
        getAlarmPushStatus();
    }

    private void getSwitchStatus() {
        deviceApiUnit.getDeviceDetectionSwitch(deviceId, channelId, new DeviceApiUnit.DeviceApiCommonListener<LcDetectionSwitchBean>() {
            @Override
            public void onSuccess(LcDetectionSwitchBean bean) {
                if (bean != null) {
                    int status = bean.getAlarmStatus();
                    tbMoveDetection.setChecked(status == 1);
                    showAlarmPush();
                }
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(msg);
            }
        });
    }

    private void setMoveDetectionSwitch(int status) {
        deviceApiUnit.setDeviceDetectionSwitch(deviceId, channelId, status, new DeviceApiUnit.DeviceApiCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {

            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(msg);
            }
        });
    }

    private void showAlarmPush() {
        rlAlarmPush.setVisibility(tbMoveDetection.isChecked() ? View.VISIBLE : View.GONE);
        tvAlarmPush.setVisibility(tbMoveDetection.isChecked() ? View.VISIBLE : View.GONE);
    }

    private void getAlarmPushStatus() {
        deviceApiUnit.doQueryDevicePushSetts(deviceId, "2", "1", new DeviceApiUnit.DeviceApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                UserPushInfo userPushInfo = (UserPushInfo) bean;
                if (!((UserPushInfo) bean).userPushInfo.isEmpty()) {
                    for (UserPushInfo.UserPushInfoBean bean2 :
                            userPushInfo.userPushInfo) {
                        if (TextUtils.equals(bean2.deviceId, deviceId)) {
                            tbAlarmPush.setChecked(bean2.pushFlag == 1);
                        }
                    }
                }

            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.single(msg);
            }
        });
    }

    private void setAlarmStatus(final boolean b) {
        deviceApiUnit.doSaveUserPushSetts(deviceId, null, b ? "1" : "0", "1", "1", null, new DeviceApiUnit.DeviceApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                tbAlarmPush.setChecked(b);
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.single(msg);
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_protect_time:
                LcProtectTimeActivity.start(LcSafeProtectActivity.this, deviceId, channelId);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.tb_move_detection:
                if (isChecked) {
                    setMoveDetectionSwitch(1);
                } else {
                    setMoveDetectionSwitch(2);
                }
                showAlarmPush();
                break;
            case R.id.tb_alarm_push:
                if (isChecked) {
                    setAlarmStatus(true);
                } else {
                    setAlarmStatus(false);
                }
                break;
        }
    }
}
