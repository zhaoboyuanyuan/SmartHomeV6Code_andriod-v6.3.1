package cc.wulian.smarthomev6.main.device.camera_lc.setting;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.cateye.album.AlbumGridActivity;
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

public class NVRListSettingActivity extends BaseTitleActivity implements View.OnClickListener {

    private RelativeLayout rlDeviceName;
    private RelativeLayout rlDeviceInformation;
    private RelativeLayout rlAlbum;

    private TextView tvDeviceName;
    private Button btnDeviceUnbind;
    private WLDialog.Builder builder;
    private WLDialog dialog;
    private WLDialog unbindDialog;
    private static final String QUERY = "QUERY";
    private static final String UPDATE_NAME = "UPDATE_NAME";
    private static final String FORMAT = "FORMAT";
    private static final String UNBIND = "UNBIND";
    private static final String SEND_REQUEST = "SEND_REQUEST";
    private static final int REQUEST_ZONE = 1;
    private static final int MSG_FINISH = 1;
    private static final int MSG_EDIT_META = 2;
    private String deviceID;
    private String deviceName;
    private boolean isShared = false;
    private Device device;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nvr_list_setting, true);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initView() {
        super.initView();
        tvDeviceName = (TextView) findViewById(R.id.tv_device_name);
        rlDeviceName = (RelativeLayout) findViewById(R.id.rl_device_name);
        rlDeviceInformation = (RelativeLayout) findViewById(R.id.rl_device_information);
        rlAlbum = (RelativeLayout) findViewById(R.id.rl_album);
        btnDeviceUnbind = (Button) findViewById(R.id.btn_device_unbind);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getResources().getString(R.string.Home_Edit_More));
    }

    @Override
    protected void initData() {
        super.initData();
        deviceID = getIntent().getExtras().getString("deviceId");
        device = MainApplication.getApplication().getDeviceCache().get(deviceID);
        if (device == null) {
            return;
        } else {
            isShared = false;
//            isShared = device.isShared;
        }
        updateView();

    }

    private void updateView() {
        tvDeviceName.setText(DeviceInfoDictionary.getNameByTypeAndName(device.type, device.name));
        if (isShared) {
            rlAlbum.setVisibility(View.GONE);
            rlDeviceInformation.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        rlDeviceName.setOnClickListener(this);
        rlDeviceInformation.setOnClickListener(this);
        rlAlbum.setOnClickListener(this);
        btnDeviceUnbind.setOnClickListener(this);
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
                LcInformationActivity.start(this, deviceID);
                break;
            case R.id.rl_album:
                Intent intent = new Intent(this, AlbumGridActivity.class);
                intent.putExtra("devId", deviceID);
                startActivity(intent);
                break;
            case R.id.btn_device_unbind:
                unbindDeviceDialog();
                break;
        }
    }

    //修改设备名称
    private void showChangeNameDialog(final Context context) {
        builder = new WLDialog.Builder(this);
        builder.setTitle(getString(R.string.GatewaySetts_ReviseName))
                .setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setEditTextHint(getString(R.string.Input_Device_Nick))
                .setEditTextText(DeviceInfoDictionary.getNameByTypeAndName(device.type, device.name))
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
            iCamCloudApiUnit.doChangeDeviceName(deviceID, deviceName, null, new ICamCloudApiUnit.IcamApiCommonListener<Object>() {

                @Override
                public void onSuccess(Object bean) {
                    ProgressDialogManager.getDialogManager().dimissDialog(UPDATE_NAME, 0);
                    ToastUtil.show(R.string.Change_Success);
                    dialog.dismiss();
                    tvDeviceName.setText(deviceName);
                    Device device = MainApplication.getApplication().getDeviceCache().get(deviceID);
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
        tip = rs.getString(R.string.Delete_Camera_Tip);
        unbindDialog = DialogUtil.showCommonDialog(this,
                rs.getString(R.string.Delete_Camera), tip, getString(R.string.Sure), getString(R.string.Cancel),
                new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View var1, String msg) {
                        unbindDevice();
                        unbindDialog.dismiss();
                        ProgressDialogManager.getDialogManager().showDialog(UNBIND, NVRListSettingActivity.this, null, null, 10000);
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
        deviceApiUnit.doUnBindDevice(deviceID, new DeviceApiUnit.DeviceApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                // 删除这个设备的widget
                HomeWidgetManager.deleteWidget(deviceID);

                MainApplication.getApplication().getDeviceCache().remove(deviceID);
                EventBus.getDefault().post(new DeviceReportEvent(null));
                ProgressDialogManager.getDialogManager().dimissDialog(UNBIND, 0);
                ToastUtil.show(R.string.Message_Device_Deleted);
                setResult(RESULT_OK, null);
                NVRListSettingActivity.this.finish();
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(R.string.Home_Scene_DeleteScene_Failed);
            }
        });
    }

}
