package cc.wulian.smarthomev6.main.device.config;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.ConfigWiFiInfoModel;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by hxc on 2017/7/6.
 * func:Icam摄像机配网起始页
 */

public class DeviceStartConfigActivity extends BaseTitleActivity {
    private String deviceId;
    private String deviceType;
    private String scanType;
    private boolean isBindDevice;
    private String asGateway;
    private ConfigWiFiInfoModel configData;

    private DeviceIdQueryFragment deviceIdQueryFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_start_config, true);
        deviceIdQueryFragment = DeviceIdQueryFragment.newInstance(configData);
        if (getSupportFragmentManager().findFragmentByTag("DeviceIdQueryFragment") == null) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(android.R.id.content, deviceIdQueryFragment, deviceIdQueryFragment.getClass().getName());
            ft.commit();
        }
    }

    @Override
    protected void initData() {
        super.initData();
        deviceType = getIntent().getStringExtra("deviceType");
        deviceId = getIntent().getStringExtra("deviceId");
        scanType = getIntent().getStringExtra("scanType");
        isBindDevice = getIntent().getBooleanExtra("isBindDevice", false);
        asGateway = getIntent().getStringExtra("asGateway");

        if (deviceId.length() > 16 && deviceId.startsWith("AV08")) {
            deviceId = deviceId.substring(0, 16);
        }

        configData = new ConfigWiFiInfoModel();
        configData.setScanType(scanType);
        configData.setAddDevice(isBindDevice);
        configData.setAsGateway(asGateway);
        configData.setDeviceId(deviceId);
        configData.setDeviceType(deviceType);

    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Config_Query));
    }

}
