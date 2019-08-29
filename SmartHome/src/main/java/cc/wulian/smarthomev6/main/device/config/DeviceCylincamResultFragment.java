package cc.wulian.smarthomev6.main.device.config;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.ConfigWiFiInfoModel;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.main.mine.gatewaycenter.GatewayBindActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceCache;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.ConstantsUtil;
import cc.wulian.smarthomev6.support.utils.DialogUtil;

/**
 * Created by hxc on 2017/9/14.
 * func:小物摄像机配网结果
 */

public class DeviceCylincamResultFragment extends WLFragment implements View.OnClickListener {
    private static final String BIND_CYLINCAM = "BIND_CYLINCAM";
    private Context context;
    private static final long START_DELAY = 1000;
    private ConfigWiFiInfoModel configData;
    private ImageView ivIcon;
    private ImageView ivRightIcon;
    private ImageView ivAnimation;
    private TextView tvPrompt;
    private TextView tvWaitTip;
    private Button btnSuccess;
    private Button btnFail;
    private LinearLayout llCylincam;
    private WLDialog bindSuccessDialog, bindFailDialog;
    private AnimationDrawable mAnimation;
    private Handler mDrawHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        public void run() {
            startAnimation(mAnimation);
        }
    };

    private boolean isShowBindDialog = false;//只有当账号下绑定摄像机设备时显示绑定结果dialog

    public static DeviceCylincamResultFragment newInstance(ConfigWiFiInfoModel configData) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("configData", configData);
        DeviceCylincamResultFragment cylincamResultFragmcent = new DeviceCylincamResultFragment();
        cylincamResultFragmcent.setArguments(bundle);
        return cylincamResultFragmcent;
    }

    @Override
    public int layoutResID() {
        return R.layout.activity_device_check_bind;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        Bundle bundle = getArguments();
        configData = bundle.getParcelable("configData");
    }

    @Override
    public void initView(View v) {
        ivIcon = (ImageView) v.findViewById(R.id.iv_oval_left_device);
        ivRightIcon = (ImageView) v.findViewById(R.id.iv_oval_rigth_device);
        tvPrompt = (TextView) v.findViewById(R.id.tv_prompt);
        tvWaitTip = (TextView) v.findViewById(R.id.tv_wait_tip);
        ivAnimation = (ImageView) v.findViewById(R.id.iv_config_wifi_step_state);
        btnSuccess = (Button) v.findViewById(R.id.btn_connect_success);
        btnFail = (Button) v.findViewById(R.id.btn_connect_fail);
        llCylincam = (LinearLayout) v.findViewById(R.id.ll_cylincam);
        llCylincam.setVisibility(View.VISIBLE);
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(btnSuccess, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(btnSuccess, SkinResouceKey.COLOR_BUTTON_TEXT);
        skinManager.setTextButtonColorAndBackground(btnFail, SkinResouceKey.COLOR_NAV);
    }

    @Override
    protected void initTitle(View v) {
        super.initTitle(v);
        mTvTitle.setText(getString(R.string.Connect_Camerea));
        setLeftImg(R.drawable.icon_back);
    }

    @Override
    public void initListener() {
        super.initListener();
        btnSuccess.setOnClickListener(this);
        btnFail.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mAnimation = (AnimationDrawable) ivAnimation
                .getDrawable();
        updateViewByDeviceId();
        if (configData.getScanType().equals(ConstantsUtil.CAMERA_ADD_ENTRY)
                || configData.getScanType().equals(ConstantsUtil.DEVICE_SCAN_ENTRY)) {
            isShowBindDialog = true;
        }
    }

    private void updateViewByDeviceId() {
        switch (configData.getDeviceType()) {
            case "CMICA4":
                ivIcon.setImageResource(R.drawable.icon_camera_4_bind_left);
                ivRightIcon.setImageResource(R.drawable.icon_wifi);
                tvWaitTip.setVisibility(View.INVISIBLE);
                tvPrompt.setText(getString(R.string.Minigateway_ConfiguringwiFi_Explain));
                break;
            default:
                break;
        }
    }

    protected void startAnimation(final AnimationDrawable animation) {
        if (animation != null && !animation.isRunning()) {
            animation.run();
        }
    }

    protected void stopAnimation(final AnimationDrawable animation) {
        if (animation != null && animation.isRunning())
            animation.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        mDrawHandler.postDelayed(mRunnable, START_DELAY);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopAnimation(mAnimation);
    }

    private void bindCylincam() {
        final DeviceApiUnit deviceApiUnit = new DeviceApiUnit(getActivity());
        ProgressDialogManager.getDialogManager().showDialog(BIND_CYLINCAM, getActivity(), null, null, null, 10000);
        deviceApiUnit.doBindDevice(configData.getDeviceId(), null, "CMICA4", new DeviceApiUnit.DeviceApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                ProgressDialogManager.getDialogManager().dimissDialog(BIND_CYLINCAM, 0);
                deviceApiUnit.doGetAllWifiDevice(new DeviceApiUnit.DeviceApiCommonListener<List<DeviceBean>>() {
                    @Override
                    public void onSuccess(List<DeviceBean> list) {
                        if (list != null && list.size() > 0) {
                            final DeviceCache deviceCache = MainApplication.getApplication().getDeviceCache();
                            for (DeviceBean device : list) {
                                Device wifiDevice = new Device(device);
//                                        wifiDevice.mode = 1;//小物摄像机设备不上报需手动设置成在线
                                deviceCache.add(wifiDevice);
                            }
                            EventBus.getDefault().post(new DeviceReportEvent(null));
                        }
                        bindSuccessDialog = DialogUtil.showTipsDialog(getActivity(), null,
                                getString(R.string.Cylincam_Added),
                                getString(R.string.Sure), new WLDialog.MessageListener() {
                                    @Override
                                    public void onClickPositive(View var1, String msg) {
                                        getActivity().finish();
                                        bindSuccessDialog.dismiss();
                                    }

                                    @Override
                                    public void onClickNegative(View var1) {

                                    }
                                });
                        if (isShowBindDialog) {
                            bindSuccessDialog.show();
                        } else {
                            if (TextUtils.equals(configData.getAsGateway(), ConstantsUtil.NEED_JUMP_BIND_GATEWAY_FLAG)) {
                                startActivity(new Intent(getActivity(), GatewayBindActivity.class).putExtra("deviceId", configData.getDeviceId()));
                            }
                            getActivity().finish();
                        }

                    }

                    @Override
                    public void onFail(int code, String msg) {

                    }
                });

            }

            @Override
            public void onFail(int code, String msg) {
                ProgressDialogManager.getDialogManager().dimissDialog(BIND_CYLINCAM, 0);
                showBindFailDialog();
            }
        });

    }

    private void showBindFailDialog() {
        WLDialog.Builder builder = new WLDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.Cylincam_Add_Failed))
                .setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setPositiveButton(getString(R.string.Sure))
                .setNegativeButton(getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        bindFailDialog.dismiss();
                        bindCylincam();
                    }

                    @Override
                    public void onClickNegative(View view) {
                        bindFailDialog.dismiss();
                        getActivity().finish();
                    }
                });
        bindFailDialog = builder.create();
        if (!bindFailDialog.isShowing()) {
            if (isShowBindDialog) {
                bindFailDialog.show();
            } else {
                getActivity().finish();
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_connect_success:
                if (TextUtils.equals(ConstantsUtil.CYLINCAME_SETTING_ENTRY, configData.getScanType())
                        || TextUtils.equals(ConstantsUtil.GATEWAY_LOGIN_ENTRY, configData.getScanType())) {
                    //设置页不绑定摄像机到账号，只配网
                    getActivity().finish();
                } else {
                    bindCylincam();
                }
                break;
            case R.id.btn_connect_fail:
                DeviceWelcomeFragment deviceWelcomeFragment = DeviceWelcomeFragment.newInstance(configData);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (!deviceWelcomeFragment.isAdded()) {
                    ft.replace(android.R.id.content, deviceWelcomeFragment, DeviceCylincamResultFragment.class.getName());
                    ft.addToBackStack(null);
                    ft.commit();
                }
                break;
        }
    }
}
