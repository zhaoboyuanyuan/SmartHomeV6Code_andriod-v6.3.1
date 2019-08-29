package cc.wulian.smarthomev6.main.device.hisense.config;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.device.hisense.bean.HisInfoBean;

/**
 * created by huxc  on 2018/1/3.
 * func： 海信设备配网起始页
 * email: hxc242313@qq.com
 */

public class HisDevStartConfigActivity extends BaseTitleActivity {
    private String deviceType;
    private String deviceId;
    private boolean hasBind;
    private HisInfoBean configData;

    private HisDevInputWifiFragment inputWifiFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_start_config, true);
    }

    @Override
    protected void initData() {
        super.initData();
        deviceId = getIntent().getStringExtra("deviceId");
        deviceType = getIntent().getStringExtra("hisType");
        hasBind = getIntent().getBooleanExtra("hasBind",false);
        configData = new HisInfoBean();
        configData.setDeviceId(deviceId);
        configData.setDeviceType(deviceType);
        configData.setHasBind(hasBind);
        inputWifiFragment = HisDevInputWifiFragment.newInstance(configData);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(android.R.id.content, inputWifiFragment, inputWifiFragment.getClass().getName());
        ft.commit();
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Config_Query));
    }
}
