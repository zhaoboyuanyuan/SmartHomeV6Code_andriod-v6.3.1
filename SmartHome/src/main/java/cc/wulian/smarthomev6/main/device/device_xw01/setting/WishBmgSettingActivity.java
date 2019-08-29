package cc.wulian.smarthomev6.main.device.device_xw01.setting;

import android.content.Context;
import android.content.Intent;
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

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.DeviceInfoActivity;
import cc.wulian.smarthomev6.main.device.DeviceMoreActivity;
import cc.wulian.smarthomev6.main.h5.H5BridgeCommonActivity;
import cc.wulian.smarthomev6.main.home.widget.HomeWidgetManager;
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
 * created by huxc  on 2018/6/1.
 * func： 向往背景音乐更多界面
 * email: hxc242313@qq.com
 */

public class WishBmgSettingActivity extends BaseTitleActivity implements View.OnClickListener {
    public static final String KEY_DEVICE_ID = "key_device_id";
    private static final String QUERY = "QUERY";
    private static final String UPDATE_NAME = "UPDATE_NAME";
    private static final String UNBIND = "UNBIND";
    private RelativeLayout rlDeviceName;
    private RelativeLayout rlDeviceInformation;
    private RelativeLayout rlDeviceChooseVoice;

    private TextView tvDeviceName;
    private Button btnDeviceUnbind;
    private WLDialog.Builder builder;
    private WLDialog dialog;
    private WLDialog unbindDialog;

    private Device device;
    private String deviceId;
    private String deviceName;
    private boolean isShared = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_bgm_setting, true);
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
        rlDeviceChooseVoice = (RelativeLayout) findViewById(R.id.rl_device_choose_voice);
        btnDeviceUnbind = (Button) findViewById(R.id.btn_device_unbind);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        rlDeviceName.setOnClickListener(this);
        rlDeviceInformation.setOnClickListener(this);
        rlDeviceChooseVoice.setOnClickListener(this);
        btnDeviceUnbind.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        deviceId = getIntent().getStringExtra(KEY_DEVICE_ID);
        device = MainApplication.getApplication().getDeviceCache().get(deviceId);
        tvDeviceName.setText(DeviceInfoDictionary.getNameByTypeAndName(device.type, device.name));
        if (device != null) {
            isShared = device.isShared;
        }
        if (isShared) {
            rlDeviceInformation.setVisibility(View.GONE);
            rlDeviceChooseVoice.setVisibility(View.GONE);
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
                Intent intent = new Intent(WishBmgSettingActivity.this, DeviceInfoActivity.class);
                intent.putExtra(KEY_DEVICE_ID, deviceId);
                startActivity(intent);
                break;
            case R.id.rl_device_choose_voice:
                startActivity(new Intent(this,H5BridgeCommonActivity.class)
                .putExtra("url","device/music_XW01/choose_source.html")
                .putExtra("deviceID",deviceId));
                break;
            case R.id.btn_device_unbind:
                unbindDeviceDialog();
                break;
        }
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
            ToastUtil.show(R.string.NickStr_Less_Limit_Length);
        } else {
            ICamCloudApiUnit iCamCloudApiUnit = new ICamCloudApiUnit(this);
            iCamCloudApiUnit.doChangeDeviceName(deviceId, deviceName,null, new ICamCloudApiUnit.IcamApiCommonListener<Object>() {

                @Override
                public void onSuccess(Object bean) {
                    ProgressDialogManager.getDialogManager().dimissDialog(UPDATE_NAME, 0);
                    ToastUtil.show(R.string.Change_Success);
                    dialog.dismiss();
                    tvDeviceName.setText(deviceName);
                    if (device != null) {
                        device.setName(deviceName);
                        EventBus.getDefault().post(new DeviceReportEvent(device));
                    }
                }

                @Override
                public void onFail(int code, String msg) {
                    ToastUtil.show(R.string.Change_Fail);
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
                        ProgressDialogManager.getDialogManager().showDialog(UNBIND, WishBmgSettingActivity.this, null, null, 10000);
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
                HomeWidgetManager.deleteWidget(deviceId);

                MainApplication.getApplication().getDeviceCache().remove(deviceId);
                EventBus.getDefault().post(new DeviceReportEvent(null));
                ProgressDialogManager.getDialogManager().dimissDialog(UNBIND, 0);
                ToastUtil.show( R.string.Message_Device_Deleted);
                setResult(RESULT_OK, null);
                WishBmgSettingActivity.this.finish();
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show( R.string.Home_Scene_DeleteScene_Failed);
            }
        });
    }

}
