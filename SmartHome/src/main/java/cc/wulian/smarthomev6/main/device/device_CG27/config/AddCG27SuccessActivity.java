package cc.wulian.smarthomev6.main.device.device_CG27.config;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.LcConfigWifiModel;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.AddDeviceActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.ICamCloudApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

public class AddCG27SuccessActivity extends BaseTitleActivity {
    private static final String UPDATE_NAME = "UPDATE_NAME";
    private Button btnNextStep;
    private TextView tvConfigWifiSuccess;
    private TextView tvDeviceName;
    private RelativeLayout rlChangeName;
    private String deviceName;
    private String deviceId;
    private Context context;
    private WLDialog.Builder builder;
    private WLDialog dialog;
    private Device device;
    private DeviceApiUnit deviceApiUnit;
    private static final int GET_WIFI_DEVICE_OK = 0x8000;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case GET_WIFI_DEVICE_OK:
                    tvDeviceName.setText(DeviceInfoDictionary.getNameByTypeAndName(device.type, device.name));
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };


    public static void start(Context context, String deviceId) {
        Intent it = new Intent(context, AddCG27SuccessActivity.class);
        it.putExtra("deviceId", deviceId);
        context.startActivity(it);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cg27_success, true);
    }

    @Override
    protected void initView() {
        super.initView();
        btnNextStep = (Button) findViewById(R.id.btn_next_step);
        rlChangeName = (RelativeLayout) findViewById(R.id.rl_device_wifi_config);
        tvConfigWifiSuccess = (TextView) findViewById(R.id.tv_config_wifi_success);
        tvDeviceName = (TextView) findViewById(R.id.tv_config_wifi_success_tips);
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(btnNextStep, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(btnNextStep, SkinResouceKey.COLOR_BUTTON_TEXT);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setTitleText(getString(R.string.Config_Add_Success));
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        btnNextStep.setOnClickListener(this);
        tvDeviceName.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        deviceId = getIntent().getStringExtra("deviceId");
        deviceApiUnit = new DeviceApiUnit(this);
        device = MainApplication.getApplication().getDeviceCache().get(deviceId);
        if (null != device) {
            tvDeviceName.setText(DeviceInfoDictionary.getNameByTypeAndName(device.type, device.name));
        } else {
            tvDeviceName.setText("");
        }
        getWifiDevice();
    }

    public void getWifiDevice() {
        deviceApiUnit.doGetAllWifiDevice(new DeviceApiUnit.DeviceApiCommonListener<List<DeviceBean>>() {
            @Override
            public void onSuccess(List<DeviceBean> list) {
                if (list != null && list.size() > 0) {
                    for (DeviceBean currdevice : list) {
                        Device wifiDevice = new Device(currdevice);
                        if (deviceId.equalsIgnoreCase(currdevice.deviceId)) {
                            device = wifiDevice;
                            MainApplication.getApplication().getDeviceCache().add(device);
                            handler.sendEmptyMessage(GET_WIFI_DEVICE_OK);
                        }
                    }
                }
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(new DeviceReportEvent(null));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_next_step:
                startActivity(new Intent(this, AddDeviceActivity.class));
                finish();
                break;
            case R.id.img_left:
                startActivity(new Intent(this, AddDeviceActivity.class));
                finish();
                break;
            case R.id.tv_config_wifi_success_tips:
                showChangeNameDialog(this);
        }
    }

    /**
     * 修改设备名称
     */

    private void showChangeNameDialog(final Context context) {
        builder = new WLDialog.Builder(context);
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
            iCamCloudApiUnit.doChangeDeviceName(deviceId, deviceName, null, new ICamCloudApiUnit.IcamApiCommonListener<Object>() {

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
}