package cc.wulian.smarthomev6.main.device.gateway_mini.device_d8;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.device.gateway_mini.config.MiniGatewayGuideFragment;
import cc.wulian.smarthomev6.main.device.gateway_mini.config.MiniGatewayWifiConfigFragment;
import cc.wulian.smarthomev6.support.core.mqtt.MiniMQTTManger;

/**
 * created by huxc  on 2017/8/21.
 * func： mini网关配网首页
 * email: hxc242313@qq.com
 */

public class DeviceD8WifiConfigActivity extends BaseTitleActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String json = intent.getStringExtra("json");
        String gwID = intent.getStringExtra("gwID");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        MiniGatewayWifiConfigFragment a = MiniGatewayWifiConfigFragment.newInstance(gwID, json, 1,"");
        ft.replace(android.R.id.content, a, MiniGatewayWifiConfigFragment.class.getName());
        ft.commit();
    }

    @Override
    protected void initTitle() {
        super.initTitle();
    }
}
