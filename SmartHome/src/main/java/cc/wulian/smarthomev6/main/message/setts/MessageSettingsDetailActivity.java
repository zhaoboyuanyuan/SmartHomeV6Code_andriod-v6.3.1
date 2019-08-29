package cc.wulian.smarthomev6.main.message.setts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.UserPushInfo;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

public class MessageSettingsDetailActivity extends BaseTitleActivity {

    public static final String FILTER = "deviceID";

    private String mDeviceID;
    private Device mDevice;
    private int log, alarm;
    private ToggleButton mToggleLog, mToggleAlarm;
    private DeviceApiUnit mDeviceApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_settings_detail, true);

        mDeviceID = getIntent().getStringExtra(FILTER);
        mDevice = MainApplication.getApplication().getDeviceCache().get(mDeviceID);
        alarm = getIntent().getIntExtra("alarm", 1);
        log = getIntent().getIntExtra("log", 1);

        mDeviceApi = new DeviceApiUnit(this);
        mToggleLog = (ToggleButton) findViewById(R.id.message_settings_toggle_log);
        mToggleAlarm = (ToggleButton) findViewById(R.id.message_settings_toggle_alarm);

        mToggleAlarm.setChecked(alarm == 1);
        mToggleLog.setChecked(log == 1);

        mToggleAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setAlarm(isChecked);
            }
        });

        mToggleLog.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setLog(isChecked);
            }
        });
    }

    /**
     * 显示报警设置
     */
    public static void start(Context context, String deviceID, int alarm, int log, String type) {
        Intent intent = new Intent(context, MessageSettingsDetailActivity.class);
        intent.putExtra(FILTER, deviceID);
        intent.putExtra("alarm", alarm);
        intent.putExtra("log", log);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected void initTitle() {

        if (mDevice == null) {
            setToolBarTitle(R.string.Device_More_AlarmSetting_PushSetting);
        } else {
            setToolBarTitle(DeviceInfoDictionary.getNameByDevice(mDevice));
        }
    }

    private void setAlarm(final boolean b) {
        mDeviceApi.doSaveUserPushSetts(mDeviceID, null, b ? "1" : "0", "1", "1", null, new DeviceApiUnit.DeviceApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                mToggleAlarm.setChecked(b);
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.single(msg);
            }
        });
    }

    private void setLog(final boolean b) {
        mDeviceApi.doSaveUserPushSetts(mDeviceID, null, b ? "1" : "0", "1", "2", null, new DeviceApiUnit.DeviceApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                mToggleLog.setChecked(b);
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.single(msg);
            }
        });
    }
}
