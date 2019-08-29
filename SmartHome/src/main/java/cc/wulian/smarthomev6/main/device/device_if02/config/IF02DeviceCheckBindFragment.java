package cc.wulian.smarthomev6.main.device.device_if02.config;


import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.espressif.iot.esptouch.EsptouchTask;
import com.espressif.iot.esptouch.IEsptouchListener;
import com.espressif.iot.esptouch.IEsptouchResult;
import com.espressif.iot.esptouch.IEsptouchTask;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.ICamCloudApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.IcamCloudCheckBindBean;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.LogUtil;

public class IF02DeviceCheckBindFragment extends WLFragment implements View.OnClickListener {

    private Context context;
    private static final long START_DELAY = 1000;
    private IF02InfoBean configData;
    private ImageView ivIcon;
    private ImageView ivRightIcon;
    private ImageView ivAnimation;
    private Button btnCancel;
    private TextView tvPrompt;
    private AnimationDrawable mAnimation;
    //    private List<WiFiInfo> if01DevList;
//    private String wifiDid;
    private String deviceType;
    private IEsptouchTask mEsptouchTask;

    private DeviceApiUnit deviceApiUnit;
    private ICamCloudApiUnit iCamCloudApiUnit;

    private FragmentManager manager;
    private FragmentTransaction ft;
    private Runnable queryRunnable;
    private Runnable failRunnable;

    private IF02DeviceAlreadyBindFragment alreadyBindFragment;
    private IF02DevAddSuccessFragment addSuccessFragment;
    private IF02DevAddFailFragment addFailFragment;
    private Handler mDrawHandler = new Handler();
    private Handler handler = new Handler();
    private List<String> mEsptouchDeviceBssidList = new ArrayList<String>();
    private Runnable mRunnable = new Runnable() {
        public void run() {
            startAnimation(mAnimation);
        }
    };

    public static IF02DeviceCheckBindFragment newInstance(IF02InfoBean configData) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("configData", configData);
        IF02DeviceCheckBindFragment checkBindFragment = new IF02DeviceCheckBindFragment();
        checkBindFragment.setArguments(bundle);
        return checkBindFragment;
    }

    @Override
    public int layoutResID() {
        return R.layout.fragment_if01_device_check_bind;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        manager = getFragmentManager();
        Bundle bundle = getArguments();
        configData = (IF02InfoBean) bundle.getSerializable("configData");

    }

    @Override
    public void initView(View v) {
        ivIcon = (ImageView) v.findViewById(R.id.iv_oval_left_device);
        ivRightIcon = (ImageView) v.findViewById(R.id.iv_oval_rigth_device);
        ivAnimation = (ImageView) v.findViewById(R.id.iv_config_wifi_step_state);
        tvPrompt = (TextView) v.findViewById(R.id.tv_prompt);
        btnCancel = (Button) v.findViewById(R.id.btn_cancel);
    }

    @Override
    protected void initTitle(View v) {
        super.initTitle(v);
        setLeftImg(R.drawable.icon_back);
    }

    @Override
    public void initListener() {
        super.initListener();
        btnCancel.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mAnimation = (AnimationDrawable) ivAnimation.getDrawable();
        deviceType = configData.getDeviceType();
        deviceApiUnit = new DeviceApiUnit(getActivity());
        iCamCloudApiUnit = new ICamCloudApiUnit(getActivity());
        updateViewByDeviceId();
        doEspTouch();
        failRunnable = new Runnable() {
            @Override
            public void run() {
                jumpToBindFailFragment();
            }
        };
        handler.postDelayed(failRunnable, 60 * 1000);

    }


    private void updateViewByDeviceId() {
        switch (deviceType) {
            case "IF02":
                mTvTitle.setText(getString(R.string.IF_009));
                ivIcon.setImageResource(R.drawable.device_if01_icon);
                if (configData.isHasBind()) {
                    ivRightIcon.setImageResource(R.drawable.icon_wifi);
                    tvPrompt.setText(getString(R.string.HS01_Configuring_Wifi));
                }
                break;
            case "GW14":
                mTvTitle.setText(getString(R.string.Config_WiFi));
                ivIcon.setImageResource(R.drawable.icon_lite_left);
                ivRightIcon.setImageResource(R.drawable.icon_wifi);
                tvPrompt.setText(getString(R.string.HS01_Configuring_Wifi));
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
        queryRunnable = new Runnable() {
            @Override
            public void run() {
                if (null != mEsptouchDeviceBssidList && mEsptouchDeviceBssidList.size() > 0) {
                    //default get first, or choosed by user(not to achieve)
//                    configData.setDeviceId(mEsptouchDeviceBssidList.get(0));
//                    parseWifiDevList();
                } else {
                    jumpToBindFailFragment();
                }
            }
        };

        handler.postDelayed(queryRunnable, 60 * 1000);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_cancel:
                getActivity().finish();
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopAnimation(mAnimation);
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(btnCancel, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(btnCancel, SkinResouceKey.COLOR_BUTTON_TEXT);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        stopWifiServer();
        mDrawHandler.removeCallbacksAndMessages(null);
        handler.removeCallbacksAndMessages(null);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDrawHandler.removeCallbacksAndMessages(null);
        handler.removeCallbacksAndMessages(null);
    }

    private void initIF01Device(Context context) {
//        String ssid = getSsidAdress(context);
        String ssid = getWifiConnectedSsid(context);
        String bssid = getBssidAdress(context);
        boolean ishidden = isHiddenSsid(context);

        LogUtil.WriteBcLog("注册监听IF01设备");
        if ((!TextUtils.isEmpty(ssid)) && (!TextUtils.isEmpty(bssid))) {
            List<IEsptouchResult> esptouchResultList = doCommandDeviceEsptouch(1, ssid, bssid, configData.getWifiPassword(),
                    ishidden, mEsptouchListener);
        }
    }

    public String getWifiConnectedSsid(Context context) {
        WifiInfo mWifiInfo = getConnectionInfo(context);
        String ssid = null;
        if (mWifiInfo != null && mWifiInfo.getSSID() != null && isWifiConnected(context)) {
            int len = mWifiInfo.getSSID().length();
            // mWifiInfo.getBSSID() = "\"" + ssid + "\"";
            if (mWifiInfo.getSSID().startsWith("\"") && mWifiInfo.getSSID().endsWith("\"")) {
                ssid = mWifiInfo.getSSID().substring(1, len - 1);
            } else {
                ssid = mWifiInfo.getSSID();
            }

        }
        return ssid;
    }

    public boolean isWifiConnected(Context context) {
        NetworkInfo mWiFiNetworkInfo = getWifiNetworkInfo(context);
        boolean isWifiConnected = false;
        if (mWiFiNetworkInfo != null) {
            isWifiConnected = mWiFiNetworkInfo.isConnected();
        }
        return isWifiConnected;
    }

    private NetworkInfo getWifiNetworkInfo(Context context) {
        ConnectivityManager mConnectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWiFiNetworkInfo;
    }

    private WifiInfo getConnectionInfo(Context context) {
        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        return wifiInfo;
    }


    private IEsptouchListener mEsptouchListener = new IEsptouchListener() {

        @Override
        public void onEsptouchResultAdded(final IEsptouchResult result) {
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
//                    String bssid = BSSIDUtil.restoreBSSID(result.getBssid());
                    String bssid = result.getBssid();
                    if (null != bssid) {
                        bssid = bssid.toUpperCase();
                        mEsptouchDeviceBssidList.add(bssid);
                    }
                    configData.setDeviceId(bssid);
                    parseWifiDevList();
                }

            });
        }
    };

    public List<IEsptouchResult> doCommandDeviceEsptouch(int expectTaskResultCount, String apSsid, String apBssid,
                                                         String apPassword, boolean isSsidHidden, IEsptouchListener esptouchListener) {
        mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, isSsidHidden, MainApplication.getApplication().getApplicationContext());
        mEsptouchTask.setEsptouchListener(esptouchListener);
        return mEsptouchTask.executeForResults(expectTaskResultCount);
    }

    private void doEspTouch() {

        final AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {

            }

            @Override
            protected Boolean doInBackground(Void... params) {
                initIF01Device(MainApplication.getApplication().getBaseContext());
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {

            }
        };

        task.execute();
    }

    private void parseWifiDevList() {
        for (String bssid : mEsptouchDeviceBssidList) {
            LogUtil.WriteBcLog("搜索到的列表为：" + "type = IF02, bssid = " + bssid);
            if (deviceType.equalsIgnoreCase("IF02") && configData.getDeviceId().equalsIgnoreCase(bssid)) {
                if (configData.isHasBind()) {
                    jumpToBindSuccessFragment();
                } else {
                    iCamCloudApiUnit.doCheckBind(configData.getDeviceId(), deviceType, null, null, new ICamCloudApiUnit.IcamApiCommonListener<IcamCloudCheckBindBean>() {
                        @Override
                        public void onSuccess(IcamCloudCheckBindBean bean) {
                            LogUtil.WriteBcLog("doCheckBind = " + bean.boundRelation);
                            if (bean.boundRelation == 0) {
                                deviceApiUnit.doBindDevice(configData.getDeviceId(), "", deviceType, new DeviceApiUnit.DeviceApiCommonListener() {
                                    @Override
                                    public void onSuccess(Object bean) {
                                        LogUtil.WriteBcLog("wifi红外设备绑定成功");
                                        jumpToBindSuccessFragment();

                                    }

                                    @Override
                                    public void onFail(int code, String msg) {

                                    }
                                });
                            } else {
                                ft = manager.beginTransaction();
                                alreadyBindFragment = IF02DeviceAlreadyBindFragment.newInstance(bean.boundRelation, bean.boundUser, configData);
                                ft.replace(android.R.id.content, alreadyBindFragment);
                                ft.commit();
                            }
                        }

                        @Override
                        public void onFail(int code, String msg) {

                        }
                    });
                }
            } else if (deviceType.equalsIgnoreCase("GW14") && configData.getDeviceId().equalsIgnoreCase(bssid)) {
                jumpToBindSuccessFragment();
            }
        }

    }


    private void jumpToBindSuccessFragment() {
        handler.removeCallbacks(failRunnable);
        addSuccessFragment = IF02DevAddSuccessFragment.newInstance(configData);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(android.R.id.content, addSuccessFragment);
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }

    private void jumpToBindFailFragment() {
        handler.removeCallbacks(null);
        addFailFragment = IF02DevAddFailFragment.newInstance(configData);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(android.R.id.content, addFailFragment);
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }

    /**
     * 获取手机的mac地址
     *
     * @param context
     * @return
     */
    private String getBssidAdress(Context context) {
        String netMac = "";
        WifiManager mWifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (mWifi.isWifiEnabled()) {
            WifiInfo wifiInfo = mWifi.getConnectionInfo();
            netMac = wifiInfo.getBSSID(); //获取被连接网络的mac地址
        }
        return netMac;
    }

    /**
     * 获取手机的ssid
     *
     * @param context
     * @return
     */
    private String getSsidAdress(Context context) {
        String netMac = "";
        WifiManager mWifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (mWifi.isWifiEnabled()) {
            WifiInfo wifiInfo = mWifi.getConnectionInfo();
            netMac = wifiInfo.getSSID(); //获取被连接网络的ssid
        }
        return netMac;
    }

    /**
     * 获取手机的ssid是否被隐藏
     *
     * @param context
     * @return
     */
    private boolean isHiddenSsid(Context context) {
        boolean isHidden = false;
        WifiManager mWifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (mWifi.isWifiEnabled()) {
            WifiInfo wifiInfo = mWifi.getConnectionInfo();
            isHidden = wifiInfo.getHiddenSSID();
        }
        return isHidden;
    }
}
