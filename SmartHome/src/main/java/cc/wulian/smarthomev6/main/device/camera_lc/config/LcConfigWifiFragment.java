package cc.wulian.smarthomev6.main.device.camera_lc.config;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lechange.opensdk.configwifi.LCOpenSDK_ConfigWifi;
import com.lechange.opensdk.media.DeviceInitInfo;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.LcConfigWifiModel;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.main.device.camera_lc.business.Business;
import cc.wulian.smarthomev6.support.utils.LogUtil;

/**
 * Created by hxc on 2017/7/7.
 * func:配网检查绑定获取配网是否成功界面
 */

public class LcConfigWifiFragment extends WLFragment implements View.OnClickListener {
    private Context context;
    private static final long START_DELAY = 1000;
    // 无线配置参数
    private static final int PROGRESS_TIMEOUT_TIME = 60 * 1000;
    private static final int PROGRESS_DELAY_TIME = 10 * 1000;
    private final int startPolling = 0x10;
    private final int successOnline = 0x11;
    private final int deviceOffline = 0x12;
    private final int successAddDevice = 0x13;
    private final int deviceInitSuccess = 0x18;
    private final int deviceInitFailed = 0x19;
    private final int deviceInitByIPFailed = 0x1A;
    private final int deviceSearchSuccess = 0x1B;
    private final int deviceSearchFailed = 0x1C;

    private final int INITMODE_UNICAST = 0;
    private final int INITMODE_MULTICAST = 1;
    private int curInitMode = INITMODE_MULTICAST;
    private int time = 25;
    private LcConfigWifiModel configData;
    private ImageView ivIcon;
    private ImageView ivRightIcon;
    private ImageView ivAnimation;
    private TextView tvPasswordError;
    private AnimationDrawable mAnimation;
    private boolean isAddDevice;//是否绑定
    private String scanType;//哪种入口扫描进入
    private LcInputDevicePwdFragment inputDevicePwdFragment;
    private LcInputWifiFragment inputWifiFragment;
    private LcConfigFailFragment failFragment;
    private Handler mDrawHandler = new Handler();
    private Handler mHandler;
    private FragmentTransaction ft;
    private FragmentManager fm;
    private Runnable mRunnable = new Runnable() {
        public void run() {
            startAnimation(mAnimation);
        }
    };

    public static LcConfigWifiFragment newInstance(LcConfigWifiModel configData) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("configData", configData);
        LcConfigWifiFragment configWifiFragment = new LcConfigWifiFragment();
        configWifiFragment.setArguments(bundle);
        return configWifiFragment;
    }

    @Override
    public int layoutResID() {
        return R.layout.fragment_lc_check_bind;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        fm = getFragmentManager();
        Bundle bundle = getArguments();
        configData = (LcConfigWifiModel) bundle.getSerializable("configData");
    }

    @Override
    public void initView(View v) {
        ivIcon = (ImageView) v.findViewById(R.id.iv_oval_left_device);
        ivRightIcon = (ImageView) v.findViewById(R.id.iv_oval_rigth_device);
        ivAnimation = (ImageView) v.findViewById(R.id.iv_config_wifi_step_state);
        tvPasswordError = v.findViewById(R.id.tv_password_error);
    }


    @Override
    protected void initTitle(View v) {
        super.initTitle(v);
        mTvTitle.setText(getString(R.string.Config_WiFi));
        setLeftImg(R.drawable.icon_back);
    }

    @Override
    public void initListener() {
        super.initListener();
        tvPasswordError.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mAnimation = (AnimationDrawable) ivAnimation
                .getDrawable();
        initHandler();
        startConfig();
    }

    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.d(LogUtil.LC_TAG, "msg.what" + msg.what);
                switch (msg.what) {
                    case LCOpenSDK_ConfigWifi.ConfigWifi_Event_Success:
                        Log.d(LogUtil.LC_TAG, "smartConfig success");
                        stopConfig();
                        mHandler.removeCallbacks(progressPoll);
//                        if (!mIsDeviceSearched) {
//                            mIsDeviceSearched = true;
                        searchDevice();
//                        }
                        break;
                    case deviceSearchSuccess:
                        configData.setmMac(((DeviceInitInfo) msg.obj).mMac);
                        configData.setmPort(((DeviceInitInfo) msg.obj).mPort);
                        configData.setmIp(((DeviceInitInfo) msg.obj).mIp);
                        configData.setmStatus(((DeviceInitInfo) msg.obj).mStatus);
                        Log.d(LogUtil.LC_TAG, "deviceSearchSuccess: ");
                        Log.d(LogUtil.LC_TAG, "mac: " + ((DeviceInitInfo) msg.obj).mMac);
                        Log.d(LogUtil.LC_TAG, "status: " + ((DeviceInitInfo) msg.obj).mStatus);
                        inputDevicePwdFragment = LcInputDevicePwdFragment.newInstance(configData);
                        ft = fm.beginTransaction();
                        ft.replace(android.R.id.content, inputDevicePwdFragment, inputDevicePwdFragment.getClass().getName());
                        ft.commit();
                        break;
                    case deviceSearchFailed:
                        Log.d(LogUtil.LC_TAG, "deviceSearchFailed:" + (String) msg.obj);
                        break;
                }
            }
        };
    }


    private void searchDevice() {
        Business.getInstance().searchDevice(configData.getDeviceId(), 15000, new Handler() {
            public void handleMessage(final Message msg) {
                if (msg.what < 0) {
                    if (msg.what == -2) {
                        mHandler.obtainMessage(deviceSearchFailed, "device not found").sendToTarget();
                    } else {
                        mHandler.obtainMessage(deviceSearchFailed, "StartSearchDevices failed").sendToTarget();
                    }
                    return;
                }

                mHandler.obtainMessage(deviceSearchSuccess, msg.obj).sendToTarget();
            }
        });
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
        mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 启动无线配对
     */
    private void startConfig() {

        String mCapabilities = getWifiCapabilities(configData.getWifiName());
        // 无线超时任务
        mHandler.postDelayed(progressRun, PROGRESS_TIMEOUT_TIME);
        // 10s开启轮询
        mHandler.postDelayed(progressPoll, PROGRESS_DELAY_TIME);
        // 调用接口，开始通过smartConfig匹配
        LCOpenSDK_ConfigWifi.configWifiStart(configData.getDeviceId(), configData.getWifiName(), configData.getWifiPwd(),
                mCapabilities, mHandler);
        Log.d(LogUtil.LC_TAG, "startConfig: id = " + configData.getDeviceId());
        Log.d(LogUtil.LC_TAG, "startConfig: ssid = " + configData.getWifiName());
        Log.d(LogUtil.LC_TAG, "startConfig: password = " + configData.getWifiPwd());
        Log.d(LogUtil.LC_TAG, "startConfig: security = " + mCapabilities);
    }

    /**
     * 关闭无线配对
     */
    private void stopConfig() {
        mHandler.removeCallbacks(progressRun);
        LCOpenSDK_ConfigWifi.configWifiStop();// 调用smartConfig停止接口
    }

    /**
     * 无线配对超时任务
     */
    private Runnable progressRun = new Runnable() {
        @Override
        public void run() {
            Log.i(LogUtil.LC_TAG, "超时配置失败 ");
            stopConfig();
            jumpToConfigFail();
        }
    };

    /**
     * 轮询定时启动任务
     */
    private Runnable progressPoll = new Runnable() {
        @Override
        public void run() {
            mHandler.obtainMessage(startPolling).sendToTarget();
        }
    };


    /**
     * 获取wifi加密信息
     */
    private String getWifiCapabilities(String ssid) {
        String mCapabilities = null;
        ScanResult mScanResult = null;
        WifiManager mWifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Activity.WIFI_SERVICE);
        if (mWifiManager != null) {
            WifiInfo mWifi = mWifiManager.getConnectionInfo();
            if (mWifi != null) {
                if (mWifi.getSSID() != null
                        && mWifi.getSSID().replaceAll("\"", "").equals(ssid)) {
                    List<ScanResult> mList = mWifiManager.getScanResults();
                    if (mList != null) {
                        for (ScanResult s : mList) {
                            if (s.SSID.replaceAll("\"", "").equals(ssid)) {
                                mScanResult = s;
                                break;
                            }
                        }
                    }
                }
            }
        }
        mCapabilities = mScanResult != null ? mScanResult.capabilities : null;
        return mCapabilities;
    }

    private void jumpToInputWifi() {
        inputWifiFragment = LcInputWifiFragment.newInstance(configData);
        ft = fm.beginTransaction();
        ft.replace(android.R.id.content, inputWifiFragment, inputWifiFragment.getClass().getName());
        ft.commit();
    }

    private void jumpToConfigFail() {
        failFragment = LcConfigFailFragment.newInstance(configData);
        ft = fm.beginTransaction();
        ft.replace(android.R.id.content, failFragment, failFragment.getClass().getName());
        ft.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDrawHandler.removeCallbacksAndMessages(null);
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_password_error:
                jumpToInputWifi();
                break;
        }
    }

}
