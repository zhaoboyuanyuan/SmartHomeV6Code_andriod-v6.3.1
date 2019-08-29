package cc.wulian.smarthomev6.main.device.device_Bn.config;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.wulian.routelibrary.common.RouteLibraryParams;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.ConfigWiFiInfoModel;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.ICamCloudApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.IcamBindRelationBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.IcamCloudCheckBindBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.IcamBindBean;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.utils.LogUtil;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * created by huxc  on 2017/7/3.
 * func：bn摄像机绑定等待界面
 * email: hxc242313@qq.com
 */

public class DevBnCheckBindFragment extends WLFragment {
    private String gwId;
    private String devId;
    private String cameraId;
    private String seed;
    private int boundRelation;
    private String errorCode;
    private ConfigWiFiInfoModel configData;

    private Context context;
    private static final long TIMEOUT_TIME = 120 * 1000;
    private static final long START_DELAY = 1000;
    private ImageView ivAnimation;
    private ImageView leftImg;
    private Device device;
    private AnimationDrawable mAnimation;
    private Handler mDrawHandler = new Handler();
    private Handler handler = new Handler();
    private Runnable mRunnable = new Runnable() {
        public void run() {
            startAnimation(mAnimation);
        }
    };
    private Runnable failRunnable;
    private ICamCloudApiUnit iCamCloudApiUnit;
    private BnConfigSuccessFragment successFragment;
    private BnConfigFailFragment failFragment;

    public static DevBnCheckBindFragment newInstance(String devId, String gwId, ConfigWiFiInfoModel configData) {
        Bundle bundle = new Bundle();
        bundle.putString("devID", devId);
        bundle.putString("gwID", gwId);
        bundle.putParcelable("configData", configData);
        DevBnCheckBindFragment bindWaitFragment = new DevBnCheckBindFragment();
        bindWaitFragment.setArguments(bundle);
        return bindWaitFragment;
    }

    @Override
    public int layoutResID() {
        return R.layout.activity_dev_bc_wait_bind;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        devId = getArguments().getString("devID");
        gwId = getArguments().getString("gwID");
        configData = getArguments().getParcelable("configData");
        EventBus.getDefault().register(this);
    }

    @Override
    public void initView(View v) {
        ivAnimation = (ImageView) v.findViewById(R.id.iv_config_wifi_step_state);
        leftImg = (ImageView) v.findViewById(R.id.iv_oval_left_device);
    }


    @Override
    protected void initTitle(View v) {
        super.initTitle(v);
        mTvTitle.setText(getString(R.string.Cateye_Connect));
        setLeftImg(R.drawable.icon_back);
    }

    @Override
    protected void initData() {
        super.initData();
        mAnimation = (AnimationDrawable) ivAnimation
                .getDrawable();
        successFragment = BnConfigSuccessFragment.newInstance(configData);
        failRunnable = new Runnable() {
            @Override
            public void run() {
                jumpToFailFragment();
            }
        };
        handler.postDelayed(failRunnable, TIMEOUT_TIME);
        device = MainApplication.getApplication().getDeviceCache().get(devId);
        if (device == null) {
            return;
        }
        if (device.type.equals("Bn")) {
            leftImg.setImageResource(R.drawable.icon_bn_config_left_img);
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


    /**
     * 下发配网命令获取摄像机Id
     */
    private void sendConfigWifiCmd() {
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", gwId);
            object.put("devID", devId);
            object.put("clusterId", 0x0101);
            object.put("commandType", 1);
            object.put("commandId", 0x8019);
            object.put("endpointNumber", 1);
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 调绑定接口检查是否绑定
     * 此处有三种情况
     */
    private void doCheckBind() {
        iCamCloudApiUnit = new ICamCloudApiUnit(getActivity());
        iCamCloudApiUnit.doCheckBind(gwId, "Bn", devId, cameraId, new ICamCloudApiUnit.IcamApiCommonListener<IcamCloudCheckBindBean>() {
            @Override
            public void onSuccess(IcamCloudCheckBindBean bean) {
                boundRelation = bean.boundRelation;
                if (bean.boundRelation == 0) {
                    saveWifiInfoToCloud("wifiName", "");
                    saveWifiInfoToCloud("bindResult", "");
                    startGetSeedInfo(true);
                    configData.setAddDevice(true);
                    LogUtil.WriteBcLog("未被绑定,走绑定流程");
                } else if (bean.boundRelation == 1) {
                    saveWifiInfoToCloud("wifiName", "");
                    saveWifiInfoToCloud("bindResult", "");
                    startGetSeedInfo(true);
                    configData.setAddDevice(true);
                    LogUtil.WriteBcLog("已被" + bean.boundUser + "绑定,走绑定流程");
                } else if (bean.boundRelation == 2) {
                    startGetSeedInfo(false);
                    configData.setAddDevice(false);
                    saveWifiInfoToCloud("wifiName", "");
                    saveWifiInfoToCloud("bindResult", "1");
                    WLog.i(TAG, "被自己绑定,走配网流程");
                }
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    /**
     * 获取seed值
     *
     * @param isNeedSeed
     */
    private void startGetSeedInfo(boolean isNeedSeed) {
        if (isNeedSeed) {
            iCamCloudApiUnit.doGetBoundRelationCode(gwId, "Bn", cameraId, new ICamCloudApiUnit.IcamApiCommonListener<IcamBindRelationBean>() {
                @Override
                public void onSuccess(IcamBindRelationBean bean) {
                    if (!StringUtil.isNullOrEmpty(bean.seed)) {
                        seed = bean.seed;
                        WLog.i(TAG, "从服务器获取seed值" + bean.seed);
                    }
                    sendConfigInfoToGateway(seed);
                }

                @Override
                public void onFail(int code, String msg) {
                }
            });
        } else {
            //此处为从设置页进来
            sendConfigInfoToGateway("");
        }

    }

    /**
     * 将WiFi信息和seed值发给网关进行绑定
     *
     * @param seed
     */
    private void sendConfigInfoToGateway(String seed) {
        String data = getWifiCodeCommon(seed, configData.getWifiName(), configData.getWifiPwd());
        String wifiCode = "160" + ((char) data.length()) + data;

        LogUtil.WriteBcLog("将WiFi信息和seed值发给网关" + '\n' + wifiCode);
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", gwId);
            object.put("devID", devId);
            object.put("clusterId", 257);
            object.put("commandType", 1);
            object.put("commandId", 0x8017);
            object.put("endpointNumber", 1);
            JSONArray array = new JSONArray();
            array.put(wifiCode);
            object.put("parameter", array);
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //    /**
//     * WiFi信息拼装
//     *
//     * @param seed
//     * @param originSSid
//     * @param originSecurity
//     * @param pwd
//     * @return
//     */
//    private String getWifiCodeCommon(String seed, String originSSid, String originSecurity, String pwd) {
//        StringBuilder sb = new StringBuilder();
//        int secType = DirectUtils.getTypeSecurityByCap(originSecurity);
//        if (secType == 4 || StringUtil.isNullOrEmpty(pwd)) {//开放网络 add by hxc
//            sb.append("0" + "\n");
//            sb.append(originSSid);
//        } else {
//            sb.append("0" + "\n");
//            sb.append(originSSid + "\n");
//            String pwdEncode = RouteLibraryParams.EncodeMappingString(pwd);
//            sb.append(pwdEncode);
//        }
//
//        if (!StringUtil.isNullOrEmpty(seed)) {
//            sb.append("\n");
//            sb.append(RouteLibraryParams.EncodeMappingString(seed));
//            WLog.i(TAG, "seed值加密前: " + seed);
//            WLog.i(TAG, "seed值加密后: " + RouteLibraryParams.EncodeMappingString(seed));
//        }
//        return sb.toString();
//    }
    public String getWifiCodeCommon(String seed, String ssid, String pwd) {
        StringBuilder sb = new StringBuilder();
        if (!StringUtil.isNullOrEmpty(pwd)) {
            sb.append("0\n" + ssid);
            sb.append("\n" + new String(RouteLibraryParams.EncodeMappingString(pwd)));
        } else if (StringUtil.isNullOrEmpty(pwd)) {
            sb.append("4\n" + ssid);
        } else if (StringUtil.isNullOrEmpty(ssid) && StringUtil.isNullOrEmpty(pwd)) {
            sb.append("9\n" + ssid);
        }
        if (!StringUtil.isNullOrEmpty(seed)) {
            sb.append("\n" + new String(RouteLibraryParams.EncodeMappingString(seed)));
            WLog.i(TAG, "seed值加密前: " + seed);
            WLog.i(TAG, "seed值加密后: " + RouteLibraryParams.EncodeMappingString(seed));
        }
        return sb.toString();
    }


    /**
     * 处理WiFi配置结果
     *
     * @param attributeValue
     */
    private void dealWifiResultCode(String attributeValue) {
        String strPre = attributeValue.substring(0, 2);
        String strLast = attributeValue.substring(2, 4);
        LogUtil.WriteBcLog("上报摄像头wifi连接状态为" + strLast);
        if (TextUtils.equals(strPre, "17")) {
            if (TextUtils.equals(strLast, "01")) {
                LogUtil.WriteBcLog("上报连接成功");
                saveWifiInfoToCloud("wifiName", configData.getWifiName());
                if (boundRelation == 2) {
                    saveWifiInfoToCloud("bindResult", "1");
                    jumpToSuccessFragment();
                }
            } else {
                errorCode = strLast;
                jumpToFailFragment();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null
                && TextUtils.equals(event.device.devID, devId)
                && event.device.mode == 1) {
        } else {
            boolean isRight = event != null
                    && event.device != null
                    && TextUtils.equals(event.device.devID, devId)
                    && event.device.mode == 0;
            if (isRight) {
                JSONObject object = null;
                try {
                    object = new JSONObject(event.device.data);
                    JSONArray endpoints = object.getJSONArray("endpoints");
                    JSONArray clusters = ((JSONObject) endpoints.get(0)).getJSONArray("clusters");
                    JSONArray attributes = ((JSONObject) clusters.get(0)).getJSONArray("attributes");
                    int clusterId = ((JSONObject) clusters.get(0)).getInt("clusterId");
                    if (clusterId == 257 && attributes != null && attributes.length() > 0) {
                        JSONObject attributesJson = attributes.getJSONObject(0);
                        int attributeId = attributesJson.getInt("attributeId");
                        String attributeValue = attributesJson.getString("attributeValue");
                        if (attributeId == 0x8007) {
                            if (!TextUtils.isEmpty(attributeValue) && attributeValue.length() > 2) {
                                String preValue = attributeValue.substring(0, 2);
                                if (TextUtils.equals(preValue, "05")) {
                                    cameraId = attributeValue.substring(2);
                                    LogUtil.WriteBcLog("下发配网命令拿到摄像机id: " + cameraId);
                                    doCheckBind();
                                }
                            }
                        } else if (attributeId == 0x8008) {
                            if (TextUtils.equals(attributeValue, "11")) {
                                LogUtil.WriteBcLog("上报摄像头注册成功");
                                saveWifiInfoToCloud("wifiName", configData.getWifiName());
                                if (boundRelation == 2) {
                                    saveWifiInfoToCloud("bindResult", "1");
                                    jumpToSuccessFragment();
                                }
                            } else if (attributeValue.length() == 4) {
                                WLog.i(TAG, "处理WiFi配置结果");
                                dealWifiResultCode(attributeValue);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void jumpToSuccessFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (!successFragment.isAdded()) {
            ft.replace(android.R.id.content, successFragment);
            ft.addToBackStack(null);
            ft.commitAllowingStateLoss();
        }
    }

    private void jumpToFailFragment() {
        failFragment = BnConfigFailFragment.newInstance(configData, errorCode);
        handler.removeCallbacks(null);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (!failFragment.isAdded()) {
            ft.replace(android.R.id.content, failFragment);
            ft.addToBackStack(null);
            ft.commitAllowingStateLoss();
        }
    }

    private void saveWifiInfoToCloud(String key, String value) {
        DataApiUnit dataApiUnit = new DataApiUnit(getActivity());
        dataApiUnit.doSaveDeviceKeyValue(configData.getDeviceId(), key, value, new DataApiUnit.DataApiCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {
                WLog.i(TAG, "保存bn锁WiFi信息到云");
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(msg);
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onIcamEvent(IcamBindBean event) {
        LogUtil.WriteBcLog("收到bindCamera成功的消息");
        handler.removeCallbacks(null);
        saveWifiInfoToCloud("bindResult", "1");
        jumpToSuccessFragment();
    }


    @Override
    public void onResume() {
        super.onResume();
        mDrawHandler.postDelayed(mRunnable, START_DELAY);
        sendConfigWifiCmd();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopAnimation(mAnimation);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(failRunnable);
        handler.removeCallbacks(null);
        EventBus.getDefault().unregister(this);
    }
}
