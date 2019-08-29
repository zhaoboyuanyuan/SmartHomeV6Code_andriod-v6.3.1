package cc.wulian.smarthomev6.main.device.device_bc.config;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.ConfigWiFiInfoModel;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.device.hisense.config.AddHisenseDeviceActivity;
import cc.wulian.smarthomev6.support.utils.LogUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * created by huxc  on 2017/11/20.
 * func：bc锁WiFi配置起始页
 * email: hxc242313@qq.com
 */

public class DevBcWifiConfigActivity extends BaseTitleActivity {
    private String gwId;
    private String devId;
    private Context context;
    private boolean isAddDevice;
    private String extData;
    private ConfigWiFiInfoModel configData;
    private DevBcInputWifiFragment devBcInputWifiFragment;
    private DevBcCheckBindFragment checkBindFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_start_config, true);
    }

    public static void start(Context context, String gwID, String devID, String extData) {
        Intent it = new Intent();
        it.putExtra("gwID", gwID);
        it.putExtra("devID", devID);
        it.putExtra("extData", extData);
        it.setClass(context, DevBcWifiConfigActivity.class);
        context.startActivity(it);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Config_WiFi));
    }

    @Override
    protected void initData() {
        super.initData();
        context = DevBcWifiConfigActivity.this;
        gwId = getIntent().getStringExtra("gwID");
        devId = getIntent().getStringExtra("devID");
        extData = getIntent().getStringExtra("extData");
        isAddDevice = getIntent().getBooleanExtra("isAddDevice", false);
        configData = new ConfigWiFiInfoModel();
        configData.setDeviceId(devId);
        LogUtil.WriteBcLog("网络智能锁ID" + devId);
        if (TextUtils.isEmpty(extData)) {
            WLog.i(TAG, "需手动输入WiFi配置");
            showInputWifiFragment();
            configData.setUseGatewayWifi(false);
        } else {
            WLog.i(TAG, "网关WiFi信息可使用：" + extData);
            showCheckBindFragment();
            configData.setUseGatewayWifi(true);
        }
    }

    private void showInputWifiFragment() {
        devBcInputWifiFragment = DevBcInputWifiFragment.newInstance(gwId, devId, isAddDevice);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(android.R.id.content, devBcInputWifiFragment, devBcInputWifiFragment.getClass().getName());
        ft.commit();

    }

    private void showCheckBindFragment() {
        String array[] = extData.split("\n");
        if (array.length == 3) {
            configData.setWifiName(array[0]);
            configData.setWifiPwd(array[1]);
            configData.setSecurity(array[2]);
        }
        checkBindFragment = DevBcCheckBindFragment.newInstance(devId, gwId, configData);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(android.R.id.content, checkBindFragment);
        ft.commit();
    }
}
