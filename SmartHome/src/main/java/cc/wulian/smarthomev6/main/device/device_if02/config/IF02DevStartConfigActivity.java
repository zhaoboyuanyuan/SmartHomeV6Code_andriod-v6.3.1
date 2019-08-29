package cc.wulian.smarthomev6.main.device.device_if02.config;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;

public class IF02DevStartConfigActivity extends BaseTitleActivity {
    private String deviceType;
    private String deviceId;
    private boolean hasBind;
    private IF02InfoBean configData;

    private IF02DevInputWifiFragment inputWifiFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_if01_dev_start_config, true);
    }

    @Override
    protected void initData() {
        super.initData();
        //需要使用第三方库获取deviceID
        deviceId = "";
        deviceType = getIntent().getStringExtra("deviceType");
        hasBind = getIntent().getBooleanExtra("hasBind",false);
        configData = new IF02InfoBean();
        configData.setDeviceId(deviceId);
        configData.setDeviceType(deviceType);
        configData.setHasBind(hasBind);
        inputWifiFragment = IF02DevInputWifiFragment.newInstance(configData);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(android.R.id.content, inputWifiFragment, inputWifiFragment.getClass().getName());
        ft.commit();

    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.IF_009));
    }
}
