package cc.wulian.smarthomev6.main.device.camera_lc.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.greenrobot.eventbus.EventBus;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.ICamCloudApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.LcRevertStatusBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.event.NvrChildDeviceNameEvent;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.DialogUtil;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

public class NVRDetailSettingActivity extends BaseTitleActivity implements View.OnClickListener {

    private RelativeLayout rlDeviceName;
    private RelativeLayout rlRecordStorage;
    private RelativeLayout rlSafeProtect;

    private TextView tvDeviceName;
    private TextView tvHasSDCard;
    private WLDialog.Builder builder;
    private WLDialog dialog;
    private static final String QUERY = "QUERY";
    private static final String UPDATE_NAME = "UPDATE_NAME";
    private static final String FORMAT = "FORMAT";
    private static final String SEND_REQUEST = "SEND_REQUEST";
    private static final int REQUEST_ZONE = 1;
    private static final int MSG_FINISH = 1;
    private static final int MSG_EDIT_META = 2;
    private String deviceID;
    private String channelId;
    private String channelName;
    private boolean hasSDCard;
    private boolean isShared = false;
    private DataApiUnit dataApiUnit;
    private DeviceApiUnit deviceApiUnit;
    private Device device;
    private WLDialog formatDialog;

    public static void start(Context context, String deviceId, String channelId, String channelName) {
        Intent intent = new Intent(context, NVRDetailSettingActivity.class);
        intent.putExtra("deviceId", deviceId);
        intent.putExtra("channelId", channelId);
        intent.putExtra("channelName", channelName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nvr_detail_setting, true);
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
        tvHasSDCard = (TextView) findViewById(R.id.tv_has_SD_card);
        rlDeviceName = (RelativeLayout) findViewById(R.id.rl_device_name);
        rlRecordStorage = (RelativeLayout) findViewById(R.id.rl_record_storage);
        rlSafeProtect = (RelativeLayout) findViewById(R.id.rl_safe_protect);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getResources().getString(R.string.Mine_Setts));
    }

    @Override
    protected void initData() {
        super.initData();
        deviceID = getIntent().getStringExtra("deviceId");
        channelName = getIntent().getStringExtra("channelName");
        channelId = getIntent().getStringExtra("channelId");
        device = MainApplication.getApplication().getDeviceCache().get(deviceID);
        dataApiUnit = new DataApiUnit(this);
        deviceApiUnit = new DeviceApiUnit(this);
        if (device == null) {
            return;
        } else {
            isShared = device.isShared;
        }
        updateView();

    }

    private void updateView() {
        tvDeviceName.setText(channelName);
        if (isShared) {
            rlRecordStorage.setVisibility(View.GONE);
            rlSafeProtect.setVisibility(View.GONE);
        }
    }


    @Override
    protected void initListeners() {
        super.initListeners();
        rlDeviceName.setOnClickListener(this);
        rlRecordStorage.setOnClickListener(this);
        rlSafeProtect.setOnClickListener(this);

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
            case R.id.rl_record_storage:
                showFormatSDDialog();
                break;
            case R.id.rl_safe_protect:
                LcSafeProtectActivity.start(this, deviceID, channelId);
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
                .setEditTextText(channelName)
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        if (!StringUtil.isNullOrEmpty(msg) && !msg.equals(channelName)) {
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
        channelName = msg;
        if (channelName.length() > 30) {
            ToastUtil.show(R.string.NickStr_Less_Limit_Length);
        } else {
            ICamCloudApiUnit iCamCloudApiUnit = new ICamCloudApiUnit(this);
            iCamCloudApiUnit.doChangeDeviceName(deviceID, channelName, channelId, new ICamCloudApiUnit.IcamApiCommonListener<Object>() {

                @Override
                public void onSuccess(Object bean) {
                    ProgressDialogManager.getDialogManager().dimissDialog(UPDATE_NAME, 0);
                    ToastUtil.show(R.string.Change_Success);
                    dialog.dismiss();
                    tvDeviceName.setText(channelName);
                    EventBus.getDefault().post(new NvrChildDeviceNameEvent(channelName));
                }

                @Override
                public void onFail(int code, String msg) {
                    ToastUtil.show(R.string.Change_Fail);
                }
            });
        }

    }


    private void showFormatSDDialog() {
        formatDialog = DialogUtil.showCommonDialog(this,
                getString(R.string.Format), getString(R.string.deviceLC_format), getString(R.string.Sure), getString(R.string.Cancel),
                new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View var1, String msg) {
                        formatSDCard();
                        formatDialog.dismiss();
                        progressDialogManager.showDialog(FORMAT, NVRDetailSettingActivity.this, getResources().getString(R.string.Formatting), null);
                    }

                    @Override
                    public void onClickNegative(View var1) {
                        formatDialog.dismiss();
                    }
                });
        formatDialog.show();
    }

    private void formatSDCard() {
        deviceApiUnit.setSDCardFormat(deviceID, channelId, new DeviceApiUnit.DeviceApiCommonListener<LcRevertStatusBean>() {
            @Override
            public void onSuccess(LcRevertStatusBean bean) {
                progressDialogManager.dimissDialog("FORMAT", 0);
                if (bean != null) {
                    switch (bean.getStatus()) {
                        case 0:
                            ToastUtil.show(R.string.deviceLC_format_hint_01);
                            break;
                        case 1:
                            ToastUtil.show(R.string.Backsee_No_SDcard);
                            break;
                        case 2:
                            ToastUtil.show(R.string.deviceLC_format_hint_02);
                            break;
                        case 3:
                            ToastUtil.show(R.string.deviceLC_format_hint_03);
                            break;
                    }
                }
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(msg);
            }
        });
    }

}
