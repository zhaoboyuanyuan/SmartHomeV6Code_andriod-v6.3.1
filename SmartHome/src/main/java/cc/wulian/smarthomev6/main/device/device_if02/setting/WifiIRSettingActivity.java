package cc.wulian.smarthomev6.main.device.device_if02.setting;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.device_if02.config.AddIF02DeviceActivity;
import cc.wulian.smarthomev6.main.home.widget.HomeWidgetManager;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.ICamCloudApiUnit;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.DialogUtil;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * created by huxc  on 2018/6/12.
 * func：wifi红外转发设置页
 * email: hxc242313@qq.com
 */

public class WifiIRSettingActivity extends BaseTitleActivity {
    private static final String UPDATE_NAME = "update";
    private static final String UNBIND = "unbind";
    private static final String CLEAR = "clear";

    private RelativeLayout rlDeviceName;
    private RelativeLayout rlDeviceInfo;
    private RelativeLayout rlClearCode;
    private RelativeLayout rlWifiConfig;
    private Button btnDelete;
    private TextView tvName;
    private TextView tvClearCode;

    private WLDialog renameDialog;
    private WLDialog clearDialog;
    private WLDialog deleteDialog;
    private WLDialog.Builder builder;

    private Device device;
    private String deviceName;
    private String deviceId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_if01_setting, true);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initView() {
        super.initView();
        rlDeviceName = (RelativeLayout) findViewById(R.id.rl_device_name);
        rlDeviceInfo = (RelativeLayout) findViewById(R.id.rl_device_information);
        rlClearCode = (RelativeLayout) findViewById(R.id.rl_device_clear_code);
        rlWifiConfig = (RelativeLayout) findViewById(R.id.rl_device_wifi_config);
        tvClearCode = (TextView) findViewById(R.id.tv_clear_code);
        tvName = (TextView) findViewById(R.id.tv_device_name);
        btnDelete = (Button) findViewById(R.id.btn_device_unbind);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(R.string.Home_Edit_More);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        rlDeviceName.setOnClickListener(this);
        rlDeviceInfo.setOnClickListener(this);
        rlClearCode.setOnClickListener(this);
        rlWifiConfig.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_device_name:
                showChangeNameDialog(this);
                break;
            case R.id.rl_device_information:
                WifiIRInformationActivity.start(this, deviceId);
                break;
            case R.id.rl_device_clear_code:
                showClearCodeDialog(this);

                break;
            case R.id.rl_device_wifi_config:
                AddIF02DeviceActivity.start(this, "IF02", true);
                break;
            case R.id.btn_device_unbind:
                showDeleteDeviceDialog(this);
                break;
        }
    }


    @Override
    protected void initData() {
        super.initData();
        deviceId = getIntent().getStringExtra("deviceId");
        device = MainApplication.getApplication().getDeviceCache().get(deviceId);
        if (device == null) {
            return;
        } else {
            updateMode(device);
            if (device.isShared) {
                rlClearCode.setVisibility(View.GONE);
                rlDeviceInfo.setVisibility(View.GONE);
                rlWifiConfig.setVisibility(View.GONE);
            }
        }


    }

    private void showChangeNameDialog(final Context context) {
        builder = new WLDialog.Builder(this);
        builder.setTitle(getString(R.string.GatewaySetts_ReviseName))
                .setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setEditTextHint(getString(R.string.Input_Device_Nick))
                .setEditTextText(DeviceInfoDictionary.getNameByTypeAndName("IF02", device.name))
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        if (!StringUtil.isNullOrEmpty(msg) && !msg.equals(device.name)) {
                            updateDeviceName(msg);
                            ProgressDialogManager.getDialogManager().showDialog(UPDATE_NAME, context, null, null, 10000);
                        }
                    }

                    @Override
                    public void onClickNegative(View view) {
                        renameDialog.dismiss();
                    }
                });
        renameDialog = builder.create();
        if (!renameDialog.isShowing()) {
            renameDialog.show();
        }
    }

    private void updateDeviceName(String msg) {
        deviceName = msg;
        if (deviceName.length() > 20) {
            ToastUtil.show(R.string.NickStr_Less_Limit_Length);
        } else {
            ICamCloudApiUnit iCamCloudApiUnit = new ICamCloudApiUnit(this);
            iCamCloudApiUnit.doChangeDeviceName(deviceId, deviceName,null, new ICamCloudApiUnit.IcamApiCommonListener<Object>() {

                @Override
                public void onSuccess(Object bean) {
                    ProgressDialogManager.getDialogManager().dimissDialog(UPDATE_NAME, 0);
                    ToastUtil.show(R.string.Change_Success);
                    renameDialog.dismiss();
                    tvName.setText(deviceName);
                    Device device = MainApplication.getApplication().getDeviceCache().get(deviceId);
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


    private void showClearCodeDialog(Context context) {
        String tip;
        Resources rs = getResources();
        tip = rs.getString(R.string.Infraredrelay_More_Emptylibrary_Confirm);
        clearDialog = DialogUtil.showCommonDialog(context,
                rs.getString(R.string.Infraredrelay_More_Emptylibrary), tip, getString(R.string.Sure), getString(R.string.Cancel),
                new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View var1, String msg) {
                        clearCode();
                        clearDialog.dismiss();
                        ProgressDialogManager.getDialogManager().showDialog(CLEAR, WifiIRSettingActivity.this, null, null, 10000);
                    }

                    @Override
                    public void onClickNegative(View var1) {
                        clearDialog.dismiss();
                    }
                });
        if (!clearDialog.isShowing()) {
            clearDialog.show();
        }
    }

    private void showDeleteDeviceDialog(Context context) {
        String tip;
        Resources rs = getResources();
        tip = rs.getString(R.string.Device_Delete);
        deleteDialog = DialogUtil.showCommonDialog(context,
                rs.getString(R.string.Device_DeleteDevice), tip, getString(R.string.Sure), getString(R.string.Cancel),
                new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View var1, String msg) {
                        deleteDevice();
                        deleteDialog.dismiss();
                        ProgressDialogManager.getDialogManager().showDialog(UNBIND, WifiIRSettingActivity.this, null, null, 10000);
                    }

                    @Override
                    public void onClickNegative(View var1) {
                        deleteDialog.dismiss();
                    }
                });
        if (!deleteDialog.isShowing()) {
            deleteDialog.show();
        }

    }

    private void deleteDevice() {
        DeviceApiUnit deviceApiUnit = new DeviceApiUnit(this);
        deviceApiUnit.doUnBindDevice(deviceId, new DeviceApiUnit.DeviceApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                // 删除这个设备的widget
                HomeWidgetManager.deleteWidget(deviceId);

                MainApplication.getApplication().getDeviceCache().remove(deviceId);
                EventBus.getDefault().post(new DeviceReportEvent(null));
                ProgressDialogManager.getDialogManager().dimissDialog(UNBIND, 0);
                ToastUtil.show(getString(R.string.Message_Device_Deleted));
                setResult(RESULT_OK, null);
                WifiIRSettingActivity.this.finish();
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(getString(R.string.Home_Scene_DeleteScene_Failed));
            }
        });
    }

    private void clearCode() {
        DataApiUnit dataApiUnit = new DataApiUnit(this);
        dataApiUnit.doDeleteWifiIRController(deviceId, null, new DataApiUnit.DataApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                EventBus.getDefault().post(new DeviceReportEvent(device));
                ProgressDialogManager.getDialogManager().dimissDialog(CLEAR, 0);
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }


    private void updateMode(Device device) {
        switch (device.mode) {
            case 0:
            case 1:
            case 4:
                tvClearCode.setAlpha(1f);
                rlClearCode.setEnabled(true);
                break;
            case 2:
                tvClearCode.setAlpha(0.54f);
                rlClearCode.setEnabled(false);
                break;
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && device != null) {
            if (TextUtils.equals(event.device.devID, device.devID)) {
                updateMode(event.device);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
