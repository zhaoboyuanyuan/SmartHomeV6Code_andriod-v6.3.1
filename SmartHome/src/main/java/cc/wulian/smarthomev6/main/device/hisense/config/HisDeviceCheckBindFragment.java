package cc.wulian.smarthomev6.main.device.hisense.config;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hismart.easylink.localjni.DevCallBack;
import com.hismart.easylink.localjni.DevInfoLocal;
import com.hismart.easylink.localjni.EasylinkUtil;
import com.hismart.easylink.localjni.WiFiInfo;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.main.device.hisense.bean.HisInfoBean;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.ICamCloudApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.IcamCloudCheckBindBean;
import cc.wulian.smarthomev6.support.utils.LogUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * created by huxc  on 2018/1/4.
 * func：海信绑定等待界面
 * email: hxc242313@qq.com
 */

public class HisDeviceCheckBindFragment extends WLFragment {
    private Context context;
    private static final long START_DELAY = 1000;
    private HisInfoBean configData;
    private ImageView ivIcon;
    private ImageView ivRightIcon;
    private ImageView ivAnimation;
    private TextView tvPrompt;
    private AnimationDrawable mAnimation;
    private List<WiFiInfo> hisDevList;
    private String wifiDid;
    private String deviceType;
    private boolean hasSearchWifiId;
    private EasylinkUtil easylinkUtil;

    private DeviceApiUnit deviceApiUnit;
    private ICamCloudApiUnit iCamCloudApiUnit;

    private FragmentManager manager;
    private FragmentTransaction ft;
    private Runnable queryRunnable;
    private Runnable failRunnable;

    private HisDeviceAlreadyBindFragment alreadyBindFragment;
    private HisDevAddSuccessFragment addSuccessFragment;
    private HisDevAddFailFragment addFailFragment;
    private Handler mDrawHandler = new Handler();
    private Handler handler = new Handler();
    private Runnable mRunnable = new Runnable() {
        public void run() {
            startAnimation(mAnimation);
        }
    };

    public static HisDeviceCheckBindFragment newInstance(HisInfoBean configData) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("configData", configData);
        HisDeviceCheckBindFragment checkBindFragment = new HisDeviceCheckBindFragment();
        checkBindFragment.setArguments(bundle);
        return checkBindFragment;
    }

    @Override
    public int layoutResID() {
        return R.layout.fragment_his_dev_check_bind;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        manager = getFragmentManager();
        Bundle bundle = getArguments();
        configData = (HisInfoBean) bundle.getSerializable("configData");
    }

    @Override
    public void initView(View v) {
        ivIcon = (ImageView) v.findViewById(R.id.iv_oval_left_device);
        ivRightIcon = (ImageView) v.findViewById(R.id.iv_oval_rigth_device);
        ivAnimation = (ImageView) v.findViewById(R.id.iv_config_wifi_step_state);
        tvPrompt = (TextView) v.findViewById(R.id.tv_prompt);
    }

    @Override
    protected void initTitle(View v) {
        super.initTitle(v);
        mTvTitle.setText(getString(R.string.adddevice_HS05));
        setLeftImg(R.drawable.icon_back);
    }

    @Override
    public void initListener() {
        super.initListener();
    }

    @Override
    protected void initData() {
        super.initData();
        mAnimation = (AnimationDrawable) ivAnimation.getDrawable();
        deviceType = configData.getDeviceType();
        deviceApiUnit = new DeviceApiUnit(getActivity());
        iCamCloudApiUnit = new ICamCloudApiUnit(getActivity());
        updateViewByDeviceId();
        startWifiServer();
        setDeviceWifiSetSsid(configData.getWifiName(), configData.getWifiPassword());
        failRunnable = new Runnable() {
            @Override
            public void run() {
                jumpToBindFailFragment();
            }
        };
        handler.postDelayed(failRunnable, 60 * 1000);


        if (configData.isHasBind()) {
            ivRightIcon.setImageResource(R.drawable.icon_wifi);
            tvPrompt.setText(getString(R.string.HS01_Configuring_Wifi));
        }
    }


    private void updateViewByDeviceId() {
        switch (deviceType) {
            case "HS01":
                ivIcon.setImageResource(R.drawable.device_hs01_icon);
                break;
            case "HS02":
                ivIcon.setImageResource(R.drawable.device_hs02_icon);
                break;
            case "HS03":
                ivIcon.setImageResource(R.drawable.device_hs03_icon);
                break;
            case "HS04":
                ivIcon.setImageResource(R.drawable.device_hs04_icon);
                break;
            case "HS05":
                ivIcon.setImageResource(R.drawable.device_hs05_icon);
                break;
            case "HS06":
                ivIcon.setImageResource(R.drawable.device_hs06_icon);
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
                if (hisDevList != null && hisDevList.size() != 0) {
                    LogUtil.WriteBcLog("1min查询，此时的hisDevList.size = " + hisDevList.size());
                    parseWifiInfoList();
                } else {
                    jumpToBindFailFragment();
                }
            }
        };

        handler.postDelayed(queryRunnable, 40 * 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopAnimation(mAnimation);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopWifiServer();
        mDrawHandler.removeCallbacksAndMessages(null);
        handler.removeCallbacksAndMessages(null);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDrawHandler.removeCallbacksAndMessages(null);
        handler.removeCallbacksAndMessages(null);
    }

    private void parseWifiInfoList() {
        for (WiFiInfo info : hisDevList
                ) {
            LogUtil.WriteBcLog("搜索到的列表为：" + "htype = " + info.HType + ",Did = " + info.DID);
            if (TextUtils.equals(configData.getDeviceType(), getRealType(info.devList.get(0).DevType))) {
                wifiDid = info.DID;
                hasSearchWifiId = true;
                configData.setWifiId(wifiDid);
                LogUtil.WriteBcLog("##wifi模块ID：" + wifiDid);
                if (configData.isHasBind()) {
                    jumpToBindSuccessFragment();
                } else {
                    iCamCloudApiUnit.doCheckBind(wifiDid, deviceType, null, configData.getDeviceId(), new ICamCloudApiUnit.IcamApiCommonListener<IcamCloudCheckBindBean>() {
                        @Override
                        public void onSuccess(IcamCloudCheckBindBean bean) {
                            LogUtil.WriteBcLog("doCheckBind = " + bean.boundRelation);
                            if (bean.boundRelation == 0) {
                                deviceApiUnit.doBindDevice(wifiDid + "#" + configData.getDeviceId(), "", deviceType, new DeviceApiUnit.DeviceApiCommonListener() {
                                    @Override
                                    public void onSuccess(Object bean) {
                                        LogUtil.WriteBcLog("海信设备绑定成功");
                                        jumpToBindSuccessFragment();

                                    }

                                    @Override
                                    public void onFail(int code, String msg) {

                                    }
                                });
                            } else {
                                ft = manager.beginTransaction();
                                alreadyBindFragment = HisDeviceAlreadyBindFragment.newInstance(bean.boundRelation, bean.boundUser, configData);
                                ft.replace(android.R.id.content, alreadyBindFragment);
                                ft.commit();
                            }
                        }

                        @Override
                        public void onFail(int code, String msg) {

                        }
                    });
                }

            }
        }
    }


    /**
     * 海信type映射关系
     *
     * @param HType
     * @return
     */
    private String getRealType(int HType) {
        String hisType = null;
        switch (HType) {
            case 1:
                hisType = "HS01";
                break;
            case 26:
                hisType = "HS02";
                break;
            case 35:
                hisType = "HS03";
                break;
            case 34:
                hisType = "HS04";
                break;
            case 2:
                hisType = "HS05";
                break;
            case 9:
                hisType = "HS06";
                break;

        }
        return hisType;
    }

    private void jumpToBindSuccessFragment() {
        handler.removeCallbacks(failRunnable);
        addSuccessFragment = HisDevAddSuccessFragment.newInstance(configData);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(android.R.id.content, addSuccessFragment);
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }

    private void jumpToBindFailFragment() {
        handler.removeCallbacks(null);
        addFailFragment = HisDevAddFailFragment.newInstance(configData);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(android.R.id.content, addFailFragment);
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }


    private void startWifiServer() {
        easylinkUtil = new EasylinkUtil();
        String deviceIP = getIPAddress(MainApplication.getApplication().getApplicationContext());
        if (!TextUtils.isEmpty(deviceIP)) {
            int starWifiServerFlag = easylinkUtil.easylink_StartStatck(deviceIP);//启动wifi服务
            if (starWifiServerFlag != 0) {
                WLog.i(TAG, "第1次启动wifi服务失败 result=" + starWifiServerFlag);
                LogUtil.WriteBcLog("第1次启动wifi服务失败 result=" + starWifiServerFlag);
                starWifiServerFlag = easylinkUtil.easylink_StopStatck();//停止WIFI服务
                if (starWifiServerFlag != 0) {
                    WLog.i(TAG, "第2次启动wifi服务失败 result=" + starWifiServerFlag);
                    LogUtil.WriteBcLog("第2次启动wifi服务失败 result=" + starWifiServerFlag);
                }
            }
            if (starWifiServerFlag == 0) {
                easylinkUtil.easylink_registerDevCallback(devCallback);//设备列表上下线监听函数
                WLog.i(TAG, "启动wifi服务成功");
                LogUtil.WriteBcLog("启动wifi服务成功");
            }
        } else {
            WLog.i(TAG, "startWifiServer: 未获取到IP");
            LogUtil.WriteBcLog("startWifiServer: 未获取到IP");
        }
    }

    public void stopWifiServer() {
        easylinkUtil.easylink_StopDeviceSmart();
//        easylinkUtil.easylink_deRegisterDevCallback(devCallback);
        easylinkUtil.easylink_StopStatck();

    }

    /**
     * 用于设备上下线的回调
     */
    DevCallBack devCallback = new DevCallBack() {
        @Override
        public void devCb(int i, WiFiInfo wiFiInfo) {
            StringBuilder strbuilder = new StringBuilder();
            strbuilder.append("*********设备上下线监听函数信息*********\r\n");
            strbuilder.append("标识:" + wiFiInfo.DID + "\r\n");//这个东西就是wifiid
            strbuilder.append("版本:" + wiFiInfo.RID + "\r\n");
            strbuilder.append("协议版本号:" + wiFiInfo.PLVer + "\r\n");
            strbuilder.append("厂家型号:" + wiFiInfo.CInfo + "\r\n");
            strbuilder.append("设备类型:" + wiFiInfo.HType + "\r\n");
            strbuilder.append("模块状态:" + wiFiInfo.HState + "\r\n");
            strbuilder.append("模块信息版本:" + wiFiInfo.HCause + "\r\n");
            strbuilder.append("描述或控制Uri:" + wiFiInfo.Uri + "\r\n");
            strbuilder.append("设备条形码:" + wiFiInfo.Barcode + "\r\n");

            strbuilder.append("设备ip:" + wiFiInfo.ip + "\r\n");
            strbuilder.append("TCP端口:" + wiFiInfo.ConnPort + "\r\n");

            int devCount = 0;
            if (wiFiInfo.devList != null) {
                devCount = wiFiInfo.devList.size();
                strbuilder.append("子设备数量:" + devCount + "\r\n");
                for (DevInfoLocal devInfoLocal : wiFiInfo.devList) {
                    strbuilder.append("----子设备devId:" + devInfoLocal.devId + "\r\n");
                    strbuilder.append("----子设备Barcode:" + devInfoLocal.Barcode + "\r\n");
                    strbuilder.append("----子设备DevType:" + devInfoLocal.DevType + "\r\n");
                    strbuilder.append("----子设备DState:" + devInfoLocal.DState + "\r\n");
                }
            }

            strbuilder.append("*********设备上下线监听函数信息*********\r\n");
            WLog.i(TAG, "devCallback: \r\n" + strbuilder);
            LogUtil.WriteBcLog("devCallback: \r\n" + strbuilder.toString());
            addWifiInfo(wiFiInfo);
            LogUtil.WriteBcLog("size = " + hisDevList.size());
        }
    };

    private void addWifiInfo(WiFiInfo wiFiInfo) {
        if (hisDevList == null) {
            LogUtil.WriteBcLog("此时hisDevList为null");
            hisDevList = new ArrayList<>();
        }
//        WiFiInfo arrValue = getWifiInfoFromArr(wiFiInfo.DID);
//        if (arrValue != null) {
        hisDevList.add(wiFiInfo);
        LogUtil.WriteBcLog("addWifiInfo: devType = " + wiFiInfo.devList.get(0).DevType);
//        }
    }

    private WiFiInfo getWifiInfoFromArr(String did) {
        WiFiInfo wiFiInfo = null;
        if (hisDevList.size() > 0) {
            for (WiFiInfo wfi : hisDevList) {
                if (!wfi.DID.equals(did)) {
                    wiFiInfo = wfi;
                    break;
                }
            }
        }
        return wiFiInfo;
    }

    /**
     * 设置wifi名称及密码
     *
     * @param wifiname wifi名称
     * @param wifipass wifi密码
     * @return
     */
    public void setDeviceWifiSetSsid(String wifiname, String wifipass) {

        String netMac = getSsidAdress(MainApplication.getApplication().getApplicationContext());
        if (!TextUtils.isEmpty(netMac)) {
            int configResult = easylinkUtil.easylink_StartDeviceSmartConfig(netMac, netMac.length(), wifipass, wifipass.length());
            if (configResult != 0) {
                WLog.i(TAG, "StartDeviceSmartConfig启动失败，resultCode=" + configResult);
                LogUtil.WriteBcLog("StartDeviceSmartConfig启动失败，resultCode=" + configResult);
            } else {
                WLog.i(TAG, "StartDeviceSmartConfig启动成功！");
                LogUtil.WriteBcLog("StartDeviceSmartConfig启动成功");
            }
        } else {
            WLog.i(TAG, "未获取到MAC地址！");
        }
    }


    /**
     * 获取IP地址,该IP地址是当前手机的Ip地址
     *
     * @param context
     * @return
     */
    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress()).replace("\"", "");//得到IPV4地址
                WLog.i("hxc", "getIPAddress: " + ipAddress);
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    /**
     * 获取手机的mac地址
     *
     * @param context
     * @return
     */
    private String getSsidAdress(Context context) {
        String netMac = "";
        WifiManager mWifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (mWifi.isWifiEnabled()) {
            WifiInfo wifiInfo = mWifi.getConnectionInfo();
            netMac = wifiInfo.getBSSID(); //获取被连接网络的mac地址
        }
        return netMac;
    }
}
