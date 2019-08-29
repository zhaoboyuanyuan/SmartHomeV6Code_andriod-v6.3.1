package cc.wulian.smarthomev6.main.device.device_if02.config;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.main.home.HomeActivity;
import cc.wulian.smarthomev6.main.mine.gatewaycenter.GatewayBindActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.ICamCloudApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.UserApiUnit;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class IF02DevAddSuccessFragment extends WLFragment implements View.OnClickListener {
    private Button btnNextStep;
    private IF02InfoBean configData;
    private String type;
    private DeviceApiUnit deviceApiUnit;
    private Context context;

    private TextView tvTips;
    private TextView tvDeviceName;
    private RelativeLayout rlChangeName;
    private WLDialog.Builder builder;
    private WLDialog dialog;

    private Device device;
    private String deviceId;
    private String deviceName;
    private static final String UPDATE_NAME = "UPDATE_NAME";
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

    public static IF02DevAddSuccessFragment newInstance(IF02InfoBean configData) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("configData", configData);
        IF02DevAddSuccessFragment deviceConfigSuccessFragment = new IF02DevAddSuccessFragment();
        deviceConfigSuccessFragment.setArguments(bundle);
        return deviceConfigSuccessFragment;
    }

    @Override
    public int layoutResID() {
        return R.layout.fragment_if01_dev_add_success;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        Bundle bundle = getArguments();
        configData = (IF02InfoBean) bundle.getSerializable("configData");

    }

    @Override
    public void initView(View v) {
        btnNextStep = (Button) v.findViewById(R.id.btn_next_step);
        tvTips = (TextView) v.findViewById(R.id.tv_config_wifi_success);
        tvDeviceName = (TextView) v.findViewById(R.id.tv_config_wifi_success_tips);
        rlChangeName = v.findViewById(R.id.rl_device_wifi_config);
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
        deviceId = configData.getDeviceId();
        deviceApiUnit = new DeviceApiUnit(getActivity());
        if (TextUtils.equals("IF02", configData.getDeviceType())) {
            mTvTitle.setText(getString(R.string.IF_009));
            if(configData.isHasBind()){
                rlChangeName.setVisibility(View.INVISIBLE);
            }
        } else if (TextUtils.equals("GW14", configData.getDeviceType())) {
            mTvTitle.setText(getString(R.string.Config_WiFi));
            rlChangeName.setVisibility(View.INVISIBLE);
            tvTips.setText(R.string.WiFi_Config_Fail);
        }
        List<Device> list = new ArrayList<>(MainApplication.getApplication().getDeviceCache().getDevices());
        if (null != list) {
            for (Device currDevice : list) {
                if (currDevice.devID.equalsIgnoreCase(deviceId))
                    device = currDevice;
            }
        }
        if (null != device) {
            tvDeviceName.setText(DeviceInfoDictionary.getNameByTypeAndName(device.type, device.name));
        } else {
            tvDeviceName.setText("");
        }
        getWifiDevice();
        if (configData.isHasBind()) {
            tvTips.setText(getString(R.string.WiFi_Config_Fail));
//            tvDeviceName.setText();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
        EventBus.getDefault().post(new DeviceReportEvent(null));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_next_step:
                if (TextUtils.equals("IF02", configData.getDeviceType())) {
                    if (configData.isHasBind()) {
                        getActivity().finish();
                    } else {
                        UserApiUnit userApiUnit = new UserApiUnit(getActivity());
                        userApiUnit.getAllDevice(true, false);
                        startActivity(new Intent(getActivity(), HomeActivity.class));
                    }
                } else if (TextUtils.equals("GW14", configData.getDeviceType())) {
                    if (!configData.isHasBind()) {
                        GatewayBindActivity.start(getActivity(), null, false);
                        getActivity().finish();
                    } else {
                        getActivity().finish();
                    }
                }

                break;
            case R.id.base_img_back_fragment:
                getActivity().finish();
                break;
            case R.id.tv_config_wifi_success_tips:
                //启动一个dialog
                showChangeNameDialog(getActivity());
        }
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
}
