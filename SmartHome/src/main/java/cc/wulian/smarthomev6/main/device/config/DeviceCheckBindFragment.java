package cc.wulian.smarthomev6.main.device.config;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.ConfigWiFiInfoModel;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.support.core.apiunit.ICamCloudApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.IcamCloudCheckBindBean;
import cc.wulian.smarthomev6.support.core.mqtt.bean.IcamBindBean;
import cc.wulian.smarthomev6.support.core.socket.GatewayBean;
import cc.wulian.smarthomev6.support.core.socket.GatewaySearchUnit;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.ConstantsUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by hxc on 2017/7/7.
 * func:配网检查绑定获取配网是否成功界面
 */

public class DeviceCheckBindFragment extends WLFragment implements View.OnClickListener {
    private Context context;
    private static final long TIMEOUT_TIME = 120 * 1000;
    private static final long START_DELAY = 1000;
    private ConfigWiFiInfoModel configData;
    private long saveStartTime;
    private ImageView ivIcon;
    private ImageView ivRightIcon;
    private ImageView ivAnimation;
    private TextView tvPrompt;
    private TextView tvWaitTip;
    private RelativeLayout rlWifiConfigNext;
    private Button btnWifiConfigNext;
    private Button btnSuccess;
    private Button btnFail;
    private AnimationDrawable mAnimation;
    private boolean isAddDevice;//是否绑定
    private String scanType;//哪种入口扫描进入
    private boolean wifiConfigSuccess = false;
    private DeviceConfigSuccessFragment deviceConfigSuccessFragment;
    private DeviceConfigFailFragment deviceConfigFailFragment;
    private Handler mDrawHandler = new Handler();
    private Handler handler = new Handler();
    private Runnable mRunnable = new Runnable() {
        public void run() {
            startAnimation(mAnimation);
        }
    };
    private Runnable configRunnable;
    private Runnable multiRunnable;
    private Runnable failRunnable;

    public static DeviceCheckBindFragment newInstance(ConfigWiFiInfoModel configData) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("configData", configData);
        DeviceCheckBindFragment deviceCheckBindFragment = new DeviceCheckBindFragment();
        deviceCheckBindFragment.setArguments(bundle);
        return deviceCheckBindFragment;
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
        EventBus.getDefault().register(this);
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
        btnWifiConfigNext = v.findViewById(R.id.btn_wifi_config_next_step);
        rlWifiConfigNext = v.findViewById(R.id.rl_wifi_config_next_step);
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
        mTvTitle.setText(getString(R.string.Connect_Device));
        setLeftImg(R.drawable.icon_back);
    }

    @Override
    public void initListener() {
        super.initListener();
        btnSuccess.setOnClickListener(this);
        btnFail.setOnClickListener(this);
        btnWifiConfigNext.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        saveStartTime = System.currentTimeMillis();
        isAddDevice = configData.isAddDevice();
        scanType = configData.getScanType();
        mAnimation = (AnimationDrawable) ivAnimation
                .getDrawable();
        updateViewByDeviceId();
        deviceConfigSuccessFragment = DeviceConfigSuccessFragment.newInstance(configData);
        deviceConfigFailFragment = DeviceConfigFailFragment.newInstance(configData);
        if (!isAddDevice) {
            if (configData.getScanType().equals(ConstantsUtil.GATEWAY_LOGIN_ENTRY)) {
                checkMulticastInfo();
                WLog.i("checkBind", "----Multicast---");
            } else {
                checkWifiConfigResult();//检查配网是否成功(不绑定)
//                setWifiConfiView();
                WLog.i("checkBind", "----configWifi---");
            }
        } else {
            failRunnable = new Runnable() {
                @Override
                public void run() {
                    doGetBindRelation();
                }
            };
            handler.postDelayed(failRunnable, 120 * 1000);
            WLog.i("checkBind", "----BindCamera---");
        }
    }

//    //随便看企鹅户外摄像机单纯的配网不绑定时让用户听语音提示进行下一步
//    private void setWifiConfiView() {
//        tvPrompt.setText("");
//        rlWifiConfigNext.setVisibility(View.VISIBLE);
//    }

    private void checkWifiConfigResult() {
        ivRightIcon.setImageResource(R.drawable.icon_wifi);
//        tvPrompt.setText(getString(R.string.Camera_disposing_Network));
        tvWaitTip.setVisibility(View.INVISIBLE);
        tvPrompt.setText(R.string.Bind_Camera_To_Account_02);
        rlWifiConfigNext.setVisibility(View.VISIBLE);
        configRunnable = new Runnable() {
            @Override
            public void run() {
                if (!wifiConfigSuccess) {
                    if (!deviceConfigFailFragment.isAdded()) {
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(android.R.id.content, deviceConfigFailFragment);
                        ft.addToBackStack(null);
                        ft.commitAllowingStateLoss();
                    }
                }

            }
        };
        handler.postDelayed(configRunnable, TIMEOUT_TIME);
    }

    private void checkMulticastInfo() {
        if (System.currentTimeMillis() - saveStartTime > TIMEOUT_TIME) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (!deviceConfigFailFragment.isAdded()) {
                ft.replace(android.R.id.content, deviceConfigFailFragment);
                ft.addToBackStack(null);
                ft.commitAllowingStateLoss();
            }
        } else {
            ivRightIcon.setImageResource(R.drawable.icon_wifi);
            tvPrompt.setText(getString(R.string.Camera_Gateway_Disposing_Network));
            new GatewaySearchUnit().startSearch(new GatewaySearchUnit.SearchGatewayCallback() {
                @Override
                public void result(List<GatewayBean> list) {
                    int a = list.size();
                    for (GatewayBean bean : list) {
                        if (bean.gwID.equalsIgnoreCase(configData.getDeviceId().substring(8, 20))) {
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            if (!deviceConfigSuccessFragment.isAdded()) {
                                handler.removeCallbacks(multiRunnable);
                                ft.replace(android.R.id.content, deviceConfigSuccessFragment);
                                ft.addToBackStack(null);
                                ft.commitAllowingStateLoss();
                            }
                            break;
                        } else {
                            multiRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    checkMulticastInfo();
                                }
                            };
                            handler.postDelayed(multiRunnable, 5 * 1000);
                        }
                    }
                }
            });
        }
    }

    /**
     * 2min内没有收到bindDevice的消息后主动查询绑定关系
     */
    private void doGetBindRelation() {
        ICamCloudApiUnit apiUnit = new ICamCloudApiUnit(getActivity());
        apiUnit.doCheckBind(configData.getDeviceId(), configData.getDeviceType(), null, null, new ICamCloudApiUnit.IcamApiCommonListener<IcamCloudCheckBindBean>() {
            @Override
            public void onSuccess(IcamCloudCheckBindBean bean) {
                if (bean.boundRelation == 2) {
                    jumpToBindSuccessFragment();
                } else {
                    jumpToBindFailFragment();
                }
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    private void jumpToBindSuccessFragment() {
        handler.removeCallbacks(failRunnable);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (!deviceConfigSuccessFragment.isAdded()) {
            ft.replace(android.R.id.content, deviceConfigSuccessFragment);
            ft.addToBackStack(null);
            ft.commitAllowingStateLoss();
        }
    }

    private void jumpToBindFailFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (!deviceConfigFailFragment.isAdded()) {
            ft.replace(android.R.id.content, deviceConfigFailFragment);
            ft.addToBackStack(null);
            ft.commitAllowingStateLoss();
        }
    }

    private void updateViewByDeviceId() {
        switch (configData.getDeviceType()) {
            case "CMICA1":
                ivIcon.setImageResource(R.drawable.icon_camera_1_bind_left);
                break;
            case "CMICA2":
                ivIcon.setImageResource(R.drawable.icon_camera_2_bind_left);
                mTvTitle.setText(getString(R.string.Connect_Camerea));
                break;
            case "CMICA3":
            case "CMICA6":
                ivIcon.setImageResource(R.drawable.icon_camera_3_bind_left);
                mTvTitle.setText(getString(R.string.Connect_Camerea));
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDrawHandler.removeCallbacksAndMessages(null);
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDrawHandler.removeCallbacksAndMessages(null);
        handler.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_wifi_config_next_step:
                wifiConfigSuccess = true;
                handler.removeCallbacks(configRunnable);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (!deviceConfigSuccessFragment.isAdded()) {
                    ft.replace(android.R.id.content, deviceConfigSuccessFragment, DeviceConfigSuccessFragment.class.getName());
                    ft.addToBackStack(null);
                    ft.commitAllowingStateLoss();
                }
                break;
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DeviceReportEvent event) {//配WiFi成功后收到设备上线的推送
        if (event.device != null) {
            WLog.i("DeviceReportEvent", event.device.data.toString());
            if (event.device.mode == 1) {
                wifiConfigSuccess = true;
                if (TextUtils.equals(event.device.devID, configData.getDeviceId())) {
                    handler.removeCallbacks(configRunnable);
//                    FragmentTransaction ft = getFragmentManager().beginTransaction();
//                    if (!deviceConfigSuccessFragment.isAdded()) {
//                        ft.replace(android.R.id.content, deviceConfigSuccessFragment, DeviceConfigSuccessFragment.class.getName());
//                        ft.addToBackStack(null);
//                        ft.commitAllowingStateLoss();
//                    }
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onIcamEvent(IcamBindBean event) {
        jumpToBindSuccessFragment();
    }
}
