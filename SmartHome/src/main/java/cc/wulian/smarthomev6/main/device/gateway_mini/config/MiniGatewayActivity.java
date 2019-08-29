package cc.wulian.smarthomev6.main.device.gateway_mini.config;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.support.core.mqtt.MiniMQTTManger;

/**
 * created by huxc  on 2017/8/21.
 * func： mini网关配网首页
 * email: hxc242313@qq.com
 */

public class MiniGatewayActivity extends BaseTitleActivity {
    private String deviceId;
    private String scanEntry;
    private MiniGatewayGuideFragment miniGatewayGuideFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_start_config, true);
        miniGatewayGuideFragment = MiniGatewayGuideFragment.newInstance(deviceId,scanEntry);
        if (getSupportFragmentManager().findFragmentByTag("MiniGatewayGuideFragment") == null) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(android.R.id.content, miniGatewayGuideFragment, miniGatewayGuideFragment.getClass().getName());
            ft.commit();
        }
    }

    @Override
    protected void initData() {
        super.initData();
        deviceId = getIntent().getStringExtra("deviceId");
        scanEntry = getIntent().getStringExtra("scanEntry");

    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Config_Query));
    }

    @Override
    protected void onDestroy() {
        MiniMQTTManger.getInstance(this).disconnectGateway();
        super.onDestroy();
    }
}
