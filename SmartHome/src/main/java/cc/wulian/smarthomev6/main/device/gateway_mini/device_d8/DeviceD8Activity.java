package cc.wulian.smarthomev6.main.device.gateway_mini.device_d8;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.DeviceMoreActivity;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;

public class DeviceD8Activity extends BaseTitleActivity {

    private static final String KEY_DEVICE_ID = "deviceID";
    private RadioGroup mRadioGroupControl;
    private RadioButton mButtonLight, mButtonVoice, mButtonCount;

    private Device mDevice;
    private String deviceID;
    private String gatewayId;
    private FragmentTransaction ft;
    private Fragment mFragmentLight, mFragmentVocie, mFragmentCount;

    public static void start(Context activity, String deviceID) {
        Intent intent = new Intent(activity, DeviceD8Activity.class);
        intent.putExtra(KEY_DEVICE_ID, deviceID);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_d8, true);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initData() {
        deviceID = getIntent().getStringExtra(KEY_DEVICE_ID);
        mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
        setTitleText(DeviceInfoDictionary.getNameByTypeAndName(mDevice.type, mDevice.name));
        gatewayId = mDevice.gwID;
        ft = getSupportFragmentManager().beginTransaction();
        mFragmentCount = DeviceD8CountFragment.start(deviceID);
        mFragmentLight = DeviceD8LightFragment.start(deviceID);
        mFragmentVocie = DeviceD8VoiceFragment.start(deviceID);
        ft.add(R.id.device_d8_frame, mFragmentLight, mFragmentLight.getClass().getSimpleName()).commit();
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitleAndRightImg(R.string.Minigateway_Devicelist_Name, R.drawable.icon_more);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.img_right:
                Intent intent = new Intent(this, DeviceD8MoreActivity.class);
                intent.putExtra(DeviceMoreActivity.KEY_DEVICE_ID, deviceID);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void initView() {
        mRadioGroupControl = (RadioGroup) findViewById(R.id.rg_switch_d8_control);
        mButtonCount = (RadioButton) findViewById(R.id.rb_tab_count);
        mButtonLight = (RadioButton) findViewById(R.id.rb_tab_light);
        mButtonVoice = (RadioButton) findViewById(R.id.rb_tab_voice);
    }

    @Override
    protected void initListeners() {
        mRadioGroupControl.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.hide(mFragmentCount).hide(mFragmentLight).hide(mFragmentVocie);
                mButtonCount.setTextColor(getResources().getColor(R.color.colorPrimary));
                mButtonLight.setTextColor(getResources().getColor(R.color.colorPrimary));
                mButtonVoice.setTextColor(getResources().getColor(R.color.colorPrimary));
                switch (checkedId) {
                    case R.id.rb_tab_light:
                        mButtonLight.setTextColor(getResources().getColor(R.color.white));
                        if (mFragmentLight.isAdded()) {
                            ft.show(mFragmentLight);
                        } else {
                            ft.add(R.id.device_d8_frame,
                                    mFragmentLight,
                                    mFragmentLight.getClass().getSimpleName()).show(mFragmentLight);
                        }
                        break;
                    case R.id.rb_tab_voice:
                        mButtonVoice.setTextColor(getResources().getColor(R.color.white));
                        if (mFragmentVocie.isAdded()) {
                            ft.show(mFragmentVocie);
                        } else {
                            ft.add(R.id.device_d8_frame,
                                    mFragmentVocie,
                                    mFragmentLight.getClass().getSimpleName()).show(mFragmentVocie);
                        }
                        break;
                    case R.id.rb_tab_count:
                        mButtonCount.setTextColor(getResources().getColor(R.color.white));
                        if (mFragmentCount.isAdded()) {
                            ft.show(mFragmentCount);
                        } else {
                            ft.add(R.id.device_d8_frame,
                                    mFragmentCount,
                                    mFragmentLight.getClass().getSimpleName()).show(mFragmentCount);
                        }
                        sendCmd(0x8003);

                        break;
                }
                ft.commit();
            }
        });
    }

    private void sendCmd(int commandId) {
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", gatewayId);
            object.put("devID", deviceID);
            object.put("cluster", 0x0500);
            object.put("endpointNumber", 1);
            object.put("endpointType", 0x0402);
            object.put("commandType", 0);
            object.put("commandId", commandId);
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceInfoChanged(DeviceInfoChangedEvent event) {
        if (event.deviceInfoBean!=null) {
            if (TextUtils.equals(event.deviceInfoBean.devID, mDevice.devID)) {
                mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
                setTitleText(mDevice.name);
            }
        }
    }

}
