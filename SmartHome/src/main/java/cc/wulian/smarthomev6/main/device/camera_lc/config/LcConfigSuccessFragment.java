package cc.wulian.smarthomev6.main.device.camera_lc.config;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.ConfigWiFiInfoModel;
import cc.wulian.smarthomev6.entity.LcConfigWifiModel;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.main.device.AddDeviceActivity;
import cc.wulian.smarthomev6.main.mine.gatewaycenter.GatewayBindActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.ICamApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.ICamCloudApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamInfoBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.ConstantsUtil;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by hxc on 2017/7/7.
 * func:绑定或配网成功界面
 */

public class LcConfigSuccessFragment extends WLFragment implements View.OnClickListener {
    private static final String UPDATE_NAME = "UPDATE_NAME";
    private Button btnNextStep;
    private LcConfigWifiModel configData;
    private TextView tvConfigWifiSuccess;
    private TextView tvDeviceName;
    private RelativeLayout rlChangeName;
    private String type;
    private String deviceName;
    private Context context;
    private WLDialog.Builder builder;
    private WLDialog dialog;
    private Device device;
    private DeviceApiUnit deviceApiUnit;
    private static final int GET_WIFI_DEVICE_OK = 0x8000;
    private Handler mhandle = new Handler() {

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

    public static LcConfigSuccessFragment newInstance(LcConfigWifiModel configData) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("configData", configData);
        LcConfigSuccessFragment successFragment = new LcConfigSuccessFragment();
        successFragment.setArguments(bundle);
        return successFragment;
    }

    @Override
    public int layoutResID() {
        return R.layout.fragment_lc_config_success;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        Bundle bundle = getArguments();
        configData = (LcConfigWifiModel) bundle.getSerializable("configData");

    }

    @Override
    public void initView(View v) {
        btnNextStep = (Button) v.findViewById(R.id.btn_next_step);
        rlChangeName = v.findViewById(R.id.rl_device_wifi_config);
        tvConfigWifiSuccess = (TextView) v.findViewById(R.id.tv_config_wifi_success);
        tvDeviceName = v.findViewById(R.id.tv_config_wifi_success_tips);
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(btnNextStep, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(btnNextStep, SkinResouceKey.COLOR_BUTTON_TEXT);
    }

    @Override
    protected void initTitle(View v) {
        super.initTitle(v);
        mTvTitle.setText(getString(R.string.Config_Add_Success));
        setLeftImg(R.drawable.icon_back);
    }

    @Override
    public void initListener() {
        super.initListener();
        btnNextStep.setOnClickListener(this);
        mImgBack.setOnClickListener(this);
        tvDeviceName.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        deviceApiUnit = new DeviceApiUnit(getActivity());
        device = MainApplication.getApplication().getDeviceCache().get(configData.getDeviceId());
        if (null != device) {
            tvDeviceName.setText(DeviceInfoDictionary.getNameByTypeAndName(device.type, device.name));
        } else {
            tvDeviceName.setText("");
        }
        getWifiDevice();
        if (!configData.getIsAddDevice()) {
            tvConfigWifiSuccess.setText(getString(R.string.WiFi_Config_Fail));
        }
    }

    public void getWifiDevice() {
        deviceApiUnit.doGetAllWifiDevice(new DeviceApiUnit.DeviceApiCommonListener<List<DeviceBean>>() {
            @Override
            public void onSuccess(List<DeviceBean> list) {
                if (list != null && list.size() > 0) {
                    for (DeviceBean currdevice : list) {
                        Device wifiDevice = new Device(currdevice);
                        if (configData.getDeviceId().equalsIgnoreCase(currdevice.deviceId)) {
                            device = wifiDevice;
                            MainApplication.getApplication().getDeviceCache().add(device);
                            mhandle.sendEmptyMessage(GET_WIFI_DEVICE_OK);
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
                startActivity(new Intent(getActivity(), AddDeviceActivity.class));
                getActivity().finish();
                break;
            case R.id.base_img_back_fragment:
                startActivity(new Intent(getActivity(), AddDeviceActivity.class));
                getActivity().finish();
                break;
            case R.id.tv_config_wifi_success_tips:
                //启动一个dialog
                showChangeNameDialog(getActivity());
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
            ICamCloudApiUnit iCamCloudApiUnit = new ICamCloudApiUnit(getActivity());
            iCamCloudApiUnit.doChangeDeviceName(configData.getDeviceId(), deviceName, null, new ICamCloudApiUnit.IcamApiCommonListener<Object>() {

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
