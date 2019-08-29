package cc.wulian.smarthomev6.main.device.safeDog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
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
import cc.wulian.smarthomev6.main.device.DeviceInfoActivity;
import cc.wulian.smarthomev6.main.device.DeviceMoreActivity;
import cc.wulian.smarthomev6.main.message.alarm.MessageAlarmActivity;
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

public class SafeDogSettingActivity extends BaseTitleActivity {

    private static final String UPDATE_NAME = "UPDATE_NAME";
    private static final String UNBIND = "UNBIND";
    private RelativeLayout renameLayout, infoLayout, alarmLayout, resetLayout;
    private Button deleteButton;
    private TextView nameTextView;

    private WLDialog.Builder builder;
    private WLDialog dialog;
    private WLDialog unbindDialog;
    private WLDialog resetDialog;
    public Device device;
    public String devId;

    public static void start(Activity activity, String devId) {
        Intent intent = new Intent(activity, SafeDogSettingActivity.class);
        intent.putExtra("devId", devId);
        activity.startActivityForResult(intent, 1);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safedog_setting, true);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        devId = getIntent().getStringExtra("devId");
        device = mainApplication.getDeviceCache().get(devId);
        setToolBarTitle(getResources().getString(R.string.Mine_Setts));
    }

    @Override
    protected void initView() {
        renameLayout = (RelativeLayout) findViewById(R.id.item_safedog_rename);
        infoLayout = (RelativeLayout) findViewById(R.id.item_safedog_info);
        alarmLayout = (RelativeLayout) findViewById(R.id.item_safedog_alarm);
        resetLayout = (RelativeLayout) findViewById(R.id.item_safedog_reset);
        deleteButton = (Button) findViewById(R.id.btn_safedog_delete);
        nameTextView = (TextView) findViewById(R.id.tv_safedog_rename);
    }

    @Override
    protected void initData() {
        nameTextView.setText(DeviceInfoDictionary.getNameByDevice(device));
    }

    @Override
    protected void initListeners() {
        renameLayout.setOnClickListener(this);
        infoLayout.setOnClickListener(this);
        alarmLayout.setOnClickListener(this);
        resetLayout.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
    }

    @Override
    public void onClickView(View v) {
        super.onClickView(v);
        switch (v.getId()) {
            case R.id.item_safedog_rename:
                showChangeNameDialog(SafeDogSettingActivity.this);
                break;
            case R.id.item_safedog_info:
                Intent intent = new Intent(SafeDogSettingActivity.this, DeviceInfoActivity.class);
                intent.putExtra(DeviceMoreActivity.KEY_DEVICE_ID, devId);
                startActivity(intent);
                break;
            case R.id.item_safedog_alarm:
                MessageAlarmActivity.start(SafeDogSettingActivity.this, devId, "sd01");
                break;
            case R.id.item_safedog_reset:
                showResetDialog();
                break;
            case R.id.btn_safedog_delete:
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
                        if (!StringUtil.isNullOrEmpty(msg) && !msg.equals(DeviceInfoDictionary.getNameByDevice(device))) {
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

    private void updateDeviceName(final String msg) {
        if (msg.length() > 30) {
            ToastUtil.show(R.string.NickStr_Less_Limit_Length);
        } else {
            ICamCloudApiUnit iCamCloudApiUnit = new ICamCloudApiUnit(this);
            iCamCloudApiUnit.doChangeDeviceName(devId, msg,null, new ICamCloudApiUnit.IcamApiCommonListener<Object>() {

                @Override
                public void onSuccess(Object bean) {
                    ProgressDialogManager.getDialogManager().dimissDialog(UPDATE_NAME, 0);
                    ToastUtil.show(R.string.Change_Success);
                    dialog.dismiss();
                    nameTextView.setText(msg);
                    Device device = MainApplication.getApplication().getDeviceCache().get(devId);
                    if (device != null) {
                        device.setName(msg);
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
        tip = rs.getString(R.string.Device_DeleteDevice);
        unbindDialog = DialogUtil.showCommonDialog(this,
                rs.getString(R.string.Device_Delete), tip, getString(R.string.Sure), getString(R.string.Cancel),
                new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View var1, String msg) {
                        unbindDevice();
                        unbindDialog.dismiss();
                        ProgressDialogManager.getDialogManager().showDialog(UNBIND, SafeDogSettingActivity.this, null, null, 10000);
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
        deviceApiUnit.doUnBindDevice(devId, new DeviceApiUnit.DeviceApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                MainApplication.getApplication().getDeviceCache().remove(devId);
                EventBus.getDefault().post(new DeviceReportEvent(null));
                ProgressDialogManager.getDialogManager().dimissDialog(UNBIND, 0);
                ToastUtil.show(R.string.Message_Device_Deleted);
                setResult(RESULT_OK);
                SafeDogSettingActivity.this.finish();
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(R.string.Home_Scene_DeleteScene_Failed);
            }
        });
    }

    /**
     * 恢复出厂设置
     */
    private void showResetDialog() {
        builder = new WLDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.Device_Bm_Details_Restore))
                .setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {

                    }

                    @Override
                    public void onClickNegative(View view) {

                    }
                });
        resetDialog = builder.create();
        if (!resetDialog.isShowing()) {
            resetDialog.show();
        }
    }
}
