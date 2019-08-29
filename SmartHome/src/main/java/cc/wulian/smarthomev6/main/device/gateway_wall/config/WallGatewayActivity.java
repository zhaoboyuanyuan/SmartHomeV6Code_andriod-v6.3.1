package cc.wulian.smarthomev6.main.device.gateway_wall.config;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.device.AddDeviceActivity;
import cc.wulian.smarthomev6.support.core.mqtt.MiniMQTTManger;
import cc.wulian.smarthomev6.support.utils.ConstantsUtil;

/**
 * created by huxc  on 2017/11/8.
 * func： 墙面网关配网首页
 * email: hxc242313@qq.com
 */

public class WallGatewayActivity extends BaseTitleActivity {
    private String deviceId;
    private String scanEntry;
    private WallGatewayGuideFragment wallGatewayGuideFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_start_config, true);
        wallGatewayGuideFragment = WallGatewayGuideFragment.newInstance(deviceId, scanEntry);
        if (getSupportFragmentManager().findFragmentByTag("WallGatewayGuideFragment") == null) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(android.R.id.content, wallGatewayGuideFragment, wallGatewayGuideFragment.getClass().getName());
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
