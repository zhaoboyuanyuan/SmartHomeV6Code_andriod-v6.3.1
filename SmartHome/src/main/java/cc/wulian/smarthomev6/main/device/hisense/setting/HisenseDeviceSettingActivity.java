package cc.wulian.smarthomev6.main.device.hisense.setting;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.AddDeviceActivity;
import cc.wulian.smarthomev6.main.device.DeviceInfoActivity;
import cc.wulian.smarthomev6.main.device.hisense.config.AddHisenseDeviceActivity;
import cc.wulian.smarthomev6.main.device.hisense.config.HisDevStartConfigActivity;
import cc.wulian.smarthomev6.main.home.widget.HomeWidgetManager;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.ICamCloudApiUnit;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceCache;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.DialogUtil;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * created by huxc  on 2018/6/1.
 * func： 海信设置页
 * email: hxc242313@qq.com
 */

public class HisenseDeviceSettingActivity extends BaseTitleActivity implements View.OnClickListener {
    public static final String KEY_DEVICE_ID = "key_device_id";
    private static final String QUERY = "QUERY";
    private static final String UPDATE_NAME = "UPDATE_NAME";
    private static final String UNBIND = "UNBIND";
    private RelativeLayout rlDeviceName;
    private RelativeLayout rlDeviceInformation;
    private RelativeLayout rlDeviceWifiConfig;

    private TextView tvDeviceName;
    private Button btnDeviceUnbind;
    private WLDialog.Builder builder;
    private WLDialog dialog;
    private WLDialog unbindDialog;

    private Device device;
    private String wifiId;
    private String deviceId;
    private String deviceName;
    private boolean isShared = false;
    private DeviceApiUnit deviceApiUnit;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_his_device_setting, true);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(R.string.Home_Edit_More);
    }

    @Override
    protected void initView() {
        super.initView();
        tvDeviceName = (TextView) findViewById(R.id.tv_device_name);
        rlDeviceName = (RelativeLayout) findViewById(R.id.rl_device_name);
        rlDeviceInformation = (RelativeLayout) findViewById(R.id.rl_device_information);
        rlDeviceWifiConfig = (RelativeLayout) findViewById(R.id.rl_device_wifi_config);
        btnDeviceUnbind = (Button) findViewById(R.id.btn_device_unbind);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        rlDeviceName.setOnClickListener(this);
        rlDeviceInformation.setOnClickListener(this);
        rlDeviceWifiConfig.setOnClickListener(this);
        btnDeviceUnbind.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        wifiId = getIntent().getStringExtra(KEY_DEVICE_ID);
        device = MainApplication.getApplication().getDeviceCache().get(wifiId);
        deviceApiUnit = new DeviceApiUnit(HisenseDeviceSettingActivity.this);
        tvDeviceName.setText(DeviceInfoDictionary.getNameByTypeAndName(device.type, device.name));
        if (device != null) {
            isShared = device.isShared;
        }
        doGetDeviceId();
        if (isShared) {
            rlDeviceInformation.setVisibility(View.GONE);
            rlDeviceWifiConfig.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_device_name:
                if (!isShared) {
                    showChangeNameDialog(this);
                }
                break;
            case R.id.rl_device_information:
                Intent intent = new Intent(HisenseDeviceSettingActivity.this, DeviceInfoActivity.class);
                intent.putExtra(KEY_DEVICE_ID, wifiId);
                startActivity(intent);
                break;
            case R.id.rl_device_wifi_config:
                jumpToConfigWiFi();
                break;
            case R.id.btn_device_unbind:
                unbindDeviceDialog();
                break;
        }
    }


    /**
     * 根据wifiID获取设备id
     */
    private void doGetDeviceId() {
        deviceApiUnit.doGetHisenseChildDevicesInfo(device.devID, new DeviceApiUnit.DeviceApiCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {
                com.alibaba.fastjson.JSONArray array = (com.alibaba.fastjson.JSONArray) bean;
                com.alibaba.fastjson.JSONObject obj = (com.alibaba.fastjson.JSONObject) array.get(0);
                deviceId = obj.getString("deviceId");
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }


    /**
     * 修改设备名称
     */

    private void showChangeNameDialog(final Context context) {
        builder = new WLDialog.Builder(this);
        builder.setTitle(getString(R.string.GatewaySetts_ReviseName))
                .setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setEditTextHint(getString(R.string.Input_Device_Nick))
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        if (!StringUtil.isNullOrEmpty(msg)) {
                            updateDeviceName(msg);
                            ProgressDialogManager.getDialogManager().showDialog(UPDATE_NAME, context, null, null, 10000);
                        }
                    }

                    @Override
                    public void onClickNegative(View view) {

                    }
                });
        dialog = builder.create();
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    private void updateDeviceName(String msg) {
        deviceName = msg;
        if (deviceName.length() > 30) {
            ToastUtil.show(getString(R.string.NickStr_Less_Limit_Length));
        } else {
            ICamCloudApiUnit iCamCloudApiUnit = new ICamCloudApiUnit(this);
            iCamCloudApiUnit.doChangeDeviceName(wifiId, deviceName,null, new ICamCloudApiUnit.IcamApiCommonListener<Object>() {

                @Override
                public void onSuccess(Object bean) {
                    ProgressDialogManager.getDialogManager().dimissDialog(UPDATE_NAME, 0);
                    ToastUtil.show(getString(R.string.Change_Success));
                    dialog.dismiss();
                    tvDeviceName.setText(deviceName);
                    if (device != null) {
                        device.setName(deviceName);
                        EventBus.getDefault().post(new DeviceReportEvent(device));
                    }
                }

                @Override
                public void onFail(int code, String msg) {
                    ToastUtil.show(getString(R.string.Change_Fail));
                }
            });
        }

    }


    /**
     * 解绑设备
     */
    private void unbindDeviceDialog() {
        String tip;
        Resources rs = getResources();
        tip = rs.getString(R.string.Device_Delete);
        unbindDialog = DialogUtil.showCommonDialog(this,
                rs.getString(R.string.Device_DeleteDevice), tip, getString(R.string.Sure), getString(R.string.Cancel),
                new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View var1, String msg) {
                        unbindDevice();
                        unbindDialog.dismiss();
                        ProgressDialogManager.getDialogManager().showDialog(UNBIND, HisenseDeviceSettingActivity.this, null, null, 10000);
                    }

                    @Override
                    public void onClickNegative(View var1) {
                        unbindDialog.dismiss();
                    }
                });
        unbindDialog.show();
    }

    private void unbindDevice() {
        DeviceApiUnit deviceApiUnit = new DeviceApiUnit(this);
        deviceApiUnit.doUnBindDevice(deviceId, new DeviceApiUnit.DeviceApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                // 删除这个设备的widget
                HomeWidgetManager.deleteWidget(wifiId);

                MainApplication.getApplication().getDeviceCache().remove(wifiId);
                EventBus.getDefault().post(new DeviceReportEvent(null));
                ProgressDialogManager.getDialogManager().dimissDialog(UNBIND, 0);
                ToastUtil.show(getString(R.string.Message_Device_Deleted));
                setResult(RESULT_OK, null);
                HisenseDeviceSettingActivity.this.finish();
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(getString(R.string.Home_Scene_DeleteScene_Failed));
            }
        });
    }


    private void jumpToConfigWiFi() {
        AddHisenseDeviceActivity.start(HisenseDeviceSettingActivity.this, device.type, true);
    }

}
