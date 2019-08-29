package cc.wulian.smarthomev6.main.device.config;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.realtek.simpleconfiglib.wulian.SimpleConfigController;
import com.wulian.lanlibrary.ExternLanApi;
import com.wulian.lanlibrary.LanController;
import com.wulian.routelibrary.common.ErrorCode;
import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.routelibrary.common.RouteLibraryParams;
import com.wulian.routelibrary.controller.TaskResultListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.ConfigWiFiInfoModel;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.main.device.lookever.util.XMLHandler;
import cc.wulian.smarthomev6.main.device.outdoor.bean.DeviceDescriptionModel;
import cc.wulian.smarthomev6.support.core.mqtt.bean.IcamBindBean;
import cc.wulian.smarthomev6.support.tools.WiFiLinker;
import cc.wulian.smarthomev6.support.utils.ConstantsUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * created by huxc  on 2017/10/10.
 * func：户外摄像机wifi和有线直连界面
 * email: hxc242313@qq.com
 */

public class DeviceWifiDirectFragment extends WLFragment {
    private static final String TAG = "DeviceWifiDirectFragment";
    private static final int STEP1 = 1;
    private static final int STEP2 = 2;
    private static final int STEP3 = 3;
    private static final int MSG_STOP_SCLIB = 1;
    private static final int MSG_UPDATE_VIEW = 3;

    private DeviceConfigSuccessFragment deviceConfigSuccessFragment;
    private DeviceConfigFailFragment deviceConfigFailFragment;
    private boolean isInitRtk = false;
    private boolean bStartMultcase = false;
    private ImageView ivAnimation;
    private Context context;
    private ConfigWiFiInfoModel configData;
    private SimpleConfigController mSimpleConfigController;
    private WiFiLinker mWiFiLinker;
    private DeviceWifiDirectFragment deviceWifiDirectFragment;
    private int currentStep;
    private AnimationDrawable mAnimation;
    private int mCurrentNum = 0;
    private String deviceRemoteIP;

    private Handler mDrawHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        public void run() {
            startAnimation(mAnimation);
        }
    };
    private Runnable failRunnable;
    private Handler handler = new Handler();
    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_STOP_SCLIB:
                    startMultcast();
                    break;
                case MSG_UPDATE_VIEW:
//                    showStepView();
                    break;
                default:
                    break;
            }
        }
    };
    private Handler countDownHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case STEP1:
                    mCurrentNum += 1;
                    if (mCurrentNum >= 30) {
                        myHandler.sendEmptyMessage(MSG_STOP_SCLIB);
                        countDownHandler.removeMessages(STEP1);
                    } else {
                        countDownHandler.sendEmptyMessageDelayed(STEP1, 1000);
                    }
                    break;
                case STEP2:
                    break;
            }
        }
    };

    public static DeviceWifiDirectFragment newInstance(ConfigWiFiInfoModel configData) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("configData", configData);
        DeviceWifiDirectFragment deviceWifiDirectFragment = new DeviceWifiDirectFragment();
        deviceWifiDirectFragment.setArguments(bundle);
        return deviceWifiDirectFragment;
    }

    @Override
    public int layoutResID() {
        return R.layout.activity_device_wifi_direct;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        context = getActivity();
        Bundle bundle = getArguments();
        configData = bundle.getParcelable("configData");
    }

    @Override
    public void initView(View view) {
        ivAnimation = (ImageView) view.findViewById(R.id.iv_config_wifi_step_state);

    }

    @Override
    protected void initTitle(View v) {
        super.initTitle(v);
        mTvTitle.setText(getString(R.string.Connect_Camerea));
        setLeftImg(R.drawable.icon_back);
    }

    @Override
    protected void initData() {
        super.initData();
        isInitRtk = false;
        bStartMultcase = false;
        mAnimation = (AnimationDrawable) ivAnimation
                .getDrawable();
        deviceConfigSuccessFragment = DeviceConfigSuccessFragment.newInstance(configData);
        deviceConfigFailFragment = DeviceConfigFailFragment.newInstance(configData);
        mSimpleConfigController = new SimpleConfigController();
        mWiFiLinker = new WiFiLinker();
        mWiFiLinker.WifiInit(context);
        currentStep = STEP1;
        if (configData.getConfigWiFiType() == ConstantsUtil.CONFIG_WIRED_THIRD_BIND_SETTING) {
            currentStep = STEP2;
        } else if (configData.getConfigWiFiType() == ConstantsUtil.CONFIG_DIRECT_WIFI_THIRD_BIND_SETTING) {
            currentStep = STEP1;
        }
        if (currentStep == STEP1) {
            startWifiDirect();
        } else if (currentStep == STEP2) {
            startMultcast();
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        mDrawHandler.postDelayed(mRunnable, 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopAnimation(mAnimation);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(failRunnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSCLib();
        stopMultcase();
        EventBus.getDefault().unregister(this);
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

    private void startWifiDirect() {
        startSCLib();
        myHandler.sendEmptyMessage(MSG_UPDATE_VIEW);
        mCurrentNum = 0;
        countDownHandler.sendEmptyMessage(STEP1);
    }

    private void startMultcast() {
        configFailCount();//120s绑定不是跳失败界面
        bStartMultcase = true;
        currentStep = STEP2;
        if (mWiFiLinker.isWiFiEnable()) {
            String localMac = "";
            WifiInfo wifiInfo = mWiFiLinker.getWifiInfo();
            if (wifiInfo != null) {
                localMac = wifiInfo.getMacAddress();
                WLog.i(TAG, "startMultcast: localMac = " + localMac);
                if (!TextUtils.isEmpty(localMac)) {
                    deviceRemoteIP = "";
                    LanController.executeRequest(RouteApiType.getAllDeviceInformationByMulticast, RouteLibraryParams.getAllDeviceInformation(localMac), new TaskResultListener() {
                        @Override
                        public void OnSuccess(RouteApiType apiType, String json) {
                            String uniqueDeviceId = null;
                            boolean result = false;
                            WLog.i(TAG, "OnSuccess: apiType = " + json);
                            if (!TextUtils.isEmpty(deviceRemoteIP)) {
                                return;
                            }
                            List<DeviceDescriptionModel> tempDeviceDesList = XMLHandler
                                    .getDeviceList(json);
                            for (DeviceDescriptionModel item : tempDeviceDesList) {
                                String deviceId = item.getSipaccount();
                                if (!deviceId.contains("cmhw")) {
                                    if (deviceId.startsWith("sip:cmic")) {
                                        uniqueDeviceId = configData.getDeviceId();
                                        int start = deviceId.indexOf("cmic");
                                        int end = deviceId.indexOf("@");
                                        deviceId = deviceId.substring(start, end);
                                        if (deviceId.equalsIgnoreCase(uniqueDeviceId)) {
                                            result = true;
                                            deviceRemoteIP = item.getRemoteIP();
                                            WLog.i(TAG, "OnSuccess: deviceRemoteIP = " + deviceRemoteIP);
                                            stopSCLib();
                                            stopMultcase();
                                            break;
                                        }
                                    } else if (deviceId.startsWith("sip:CG")) {
                                        int start = deviceId.indexOf("CG");
                                        int end = deviceId.indexOf("@");
                                        deviceId = deviceId.substring(start, end);
                                        uniqueDeviceId = configData.getDeviceId().substring(0,11);
                                        if (deviceId.equalsIgnoreCase(uniqueDeviceId)) {
                                            result = true;
                                            deviceRemoteIP = item.getRemoteIP();
                                            WLog.i(TAG, "OnSuccess: deviceRemoteIP = " + deviceRemoteIP);
                                            stopSCLib();
                                            stopMultcase();
                                            break;
                                        }
                                    }

                                }
                            }


                            if (result) {
                                if (configData.isAddDevice()) {
                                    String seedBase64 = new String(Base64.encode(configData.getSeed().getBytes(), Base64.DEFAULT));
                                    WLog.i(TAG, "OnSuccess: seedBase64 = " + seedBase64);
                                    WLog.i(TAG, "OnSuccess: ip = " + deviceRemoteIP);
                                    String setResult = LanController.executeRequest(RouteApiType.BindSeedSet, RouteLibraryParams.BindSeedSet(deviceRemoteIP, uniqueDeviceId, seedBase64));
                                    WLog.i(TAG, "OnSuccess: setResult = " + setResult);
                                } else {
                                    jumpToSuccessResult();
                                }
                            }

                        }

                        @Override
                        public void OnFail(RouteApiType apiType, ErrorCode code) {

                        }
                    });
                    bStartMultcase = true;
                } else {
                    ToastUtil.show("localMac is null");
                }
            } else {
                ToastUtil.show("wifi info is null");
            }
        }
    }

    private void configFailCount() {
        failRunnable = new Runnable() {
            @Override
            public void run() {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (!deviceConfigFailFragment.isAdded()) {
                    ft.replace(android.R.id.content, deviceConfigFailFragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }
        };
        handler.postDelayed(failRunnable, 90 * 1000);
    }

    private void startSCLib() {
        if (isInitRtk)
            return;
        mSimpleConfigController.initData(context);
        mSimpleConfigController.StartConfig(configData.getWifiName(), configData.getWifiPwd(), configData.getBssid());
        isInitRtk = true;
    }

    private void stopSCLib() {
        if (isInitRtk) {
            mSimpleConfigController.stopConfig();
        }
    }

    private void stopMultcase() {
        if (bStartMultcase) {
            LanController.stopRequest();
            bStartMultcase = false;
        }
    }

    private void jumpToSuccessResult() {
        handler.removeCallbacks(failRunnable);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (!deviceConfigSuccessFragment.isAdded()) {
            ft.replace(android.R.id.content, deviceConfigSuccessFragment);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onIcamEvent(IcamBindBean event) {
        if (event.devID.equals(configData.getDeviceId())) {
            jumpToSuccessResult();
        }
    }

}
