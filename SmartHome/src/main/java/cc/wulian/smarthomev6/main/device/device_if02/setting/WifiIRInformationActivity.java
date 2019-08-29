package cc.wulian.smarthomev6.main.device.device_if02.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.GatewayLastVersionBean;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.LanguageUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by hxc on 2018/6/12.
 * wifi红外设备信息
 */

public class WifiIRInformationActivity extends BaseTitleActivity {
    private TextView tvDeviceName;
    private TextView tvFirmwareVersion;
    private TextView tvConnectWifi;
    private TextView tvWifiStrength;
    private TextView tvIpAddress;
    private TextView tvMacAddress;
    private ImageView ivUpdateVersion;
    private ImageView ivDeviceIcon;
    private WLDialog updateDialog;
    private String deviceId;
    private int defaultIconRes;
    private String currentVersion;
    private String latestVersion;

    private DeviceApiUnit deviceApiUnit;

    public static void start(Context context, String deviceId) {
        Intent intent = new Intent(context, WifiIRInformationActivity.class);
        intent.putExtra("deviceId", deviceId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_if01_information, true);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Device_Detail));
    }

    @Override
    protected void initView() {
        super.initView();
        tvDeviceName = (TextView) findViewById(R.id.tv_device_name);
        tvFirmwareVersion = (TextView) findViewById(R.id.tv_firmware_version);
        tvConnectWifi = (TextView) findViewById(R.id.tv_connect_wifi);
        tvWifiStrength = (TextView) findViewById(R.id.tv_wifi_strength);
        tvIpAddress = (TextView) findViewById(R.id.tv_ip_address);
        tvMacAddress = (TextView) findViewById(R.id.tv_mac_address);
        ivUpdateVersion = (ImageView) findViewById(R.id.iv_new_version);
        ivDeviceIcon = (ImageView) findViewById(R.id.device_icon);
        tvFirmwareVersion.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initData() {
        super.initData();
        deviceId = getIntent().getStringExtra("deviceId");
        deviceApiUnit = new DeviceApiUnit(this);
        if (LanguageUtil.isChina()) {
            defaultIconRes = R.drawable.default_device_icon;
        } else {
            defaultIconRes = R.drawable.default_device_cn_icon;
        }
        ivDeviceIcon.setImageResource(defaultIconRes);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getZigBeeDeviceInfo();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_firmware_version:
                showUpdateDialog(latestVersion);
                break;
        }
    }

    private void showUpdateDialog(final String latestVersion) {
        String msg = String.format(getString(R.string.IF_004), latestVersion);
        WLDialog.Builder builder = new WLDialog.Builder(this);
        builder.setMessage(msg)
                .setCancelOnTouchOutSide(false)
                .setNegativeButton(getString(R.string.Cancel))
                .setPositiveButton(getString(R.string.Sure))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View var1, String msg) {
                        WifiIRUpdateFirmwareActivity.start(WifiIRInformationActivity.this, deviceId, latestVersion);

                    }

                    @Override
                    public void onClickNegative(View var1) {

                    }
                });
        updateDialog = builder.create();
        if (!updateDialog.isShowing()) {
            updateDialog.show();
        }
    }

    private void getZigBeeDeviceInfo() {
        deviceApiUnit.doGetZigBeeDeviceInfo(deviceId, new DeviceApiUnit.DeviceApiCommonListener<DeviceBean>() {
            @Override
            public void onSuccess(DeviceBean bean) {
                if (bean != null) {
                    if (!TextUtils.isEmpty(bean.url)) {
                        DisplayImageOptions options = new DisplayImageOptions.Builder()
                                .showImageOnLoading(defaultIconRes) // 设置图片下载期间显示的图片
                                .showImageForEmptyUri(defaultIconRes) // 设置图片Uri为空或是错误的时候显示的图片
                                .showImageOnFail(defaultIconRes) // 设置图片加载或解码过程中发生错误显示的图片
                                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                                .cacheOnDisk(false) // 设置下载的图片是否缓存在SD卡中
                                .build(); // 创建配置过得DisplayImageOption对象
                        ImageLoader.getInstance().displayImage(bean.url, ivDeviceIcon, options);
                    }
                    if (!TextUtils.isEmpty(bean.version)) {
                        currentVersion = bean.version;
                        tvFirmwareVersion.setText(currentVersion);
                        getLatestVersionVersion();
                    }
                    if (!TextUtils.isEmpty(bean.deviceIp)) {
                        tvIpAddress.setText(bean.deviceIp);
                    }
                }
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(getString(R.string.Picture_Failure_Toast));
            }
        });
    }


    private void getLatestVersionVersion() {
        deviceApiUnit.doGetGatewayVersion("IF02", new DeviceApiUnit.DeviceApiCommonListener<GatewayLastVersionBean>() {
            @Override
            public void onSuccess(GatewayLastVersionBean bean) {
                if (bean != null) {
                    latestVersion = bean.latestVersion;
                    if (!TextUtils.isEmpty(latestVersion) && !TextUtils.isEmpty(currentVersion) && !TextUtils.equals(latestVersion, currentVersion)) {
                        ivUpdateVersion.setVisibility(View.VISIBLE);
                        tvFirmwareVersion.setEnabled(true);
                    }
                }
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }
}
