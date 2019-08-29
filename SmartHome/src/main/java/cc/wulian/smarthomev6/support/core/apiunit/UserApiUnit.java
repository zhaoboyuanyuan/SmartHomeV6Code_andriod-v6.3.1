package cc.wulian.smarthomev6.support.core.apiunit;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.lzy.okgo.OkGo;
import com.wulian.sdk.android.ipc.rtcv2.IPCController;
import com.wulian.sdk.android.ipc.rtcv2.IPCResultCallBack;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.LocalInfo;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.home.widget.HomeWidgetManager;
import cc.wulian.smarthomev6.main.mine.gatewaycenter.ConfirmGatewayPasswordActivity;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceIsPushBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceListBeanAll;
import cc.wulian.smarthomev6.support.core.apiunit.bean.ResponseBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.UserBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamGetSipInfoBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamOssInfoBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.sso.MqttInfoBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceCache;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.device.GatewayManager;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTApiConfig;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.GatewayPasswordBean;
import cc.wulian.smarthomev6.support.event.ConfirmPasswordEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GatewayInfoEvent;
import cc.wulian.smarthomev6.support.event.GatewayPasswordChangedEvent;
import cc.wulian.smarthomev6.support.event.GatewayStateChangedEvent;
import cc.wulian.smarthomev6.support.event.GetSceneListEvent;
import cc.wulian.smarthomev6.support.event.NotifyInputGatewayPasswordEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.RegularTool;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.WLog;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zbl on 2017/3/1.
 * 用户接入类
 */
public class UserApiUnit {

    private Context context;

    private DeviceApiUnit deviceApiUnit;

    private LocalInfo localInfo;
    private Handler handler = new Handler(Looper.getMainLooper());
    private int totalCount;

    public final static String USER_DEFAULT_PASSWORD = "defaultpass";

    public interface UserApiCommonListener<T> {
        void onSuccess(T bean);

        void onFail(int code, String msg);
    }

    public interface getPageDeviceListener {
        void onSuccess(List<DeviceBean> list);
    }

    public interface getAllDeviceListener {
        void onSuccess(List<DeviceBean> list);
    }

    public UserApiUnit(Context context) {
        this.context = context;
        deviceApiUnit = new DeviceApiUnit(context);
        localInfo = MainApplication.getApplication().getLocalInfo();
    }

    public void doLoginSuccess(MqttInfoBean mqttInfoBean) {
        Preference preference = Preference.getPreferences();
        preference.saveUserEnterType(Preference.ENTER_TYPE_ACCOUNT);
        preference.saveIsLogin(true);
        //初始化移康猫眼sdk,登录,查询移康猫眼设备列表
        MainApplication.getApplication().getEquesApiUnit().getIcvss().equesLogin(context, ApiConstant.EQUES_URL, ApiConstant.getUserID(), ApiConstant.EQUES_APPKEY);
//                EquesApiUnit.getInstance().getIcvss().equesGetDeviceList();
        connectMQTT(mqttInfoBean);
//        loginWulianIcam();
//        getWifiDevice();

        new DataApiUnit(context).doGetIcamOssInfo(new DataApiUnit.DataApiCommonListener<ICamOssInfoBean>() {
            @Override
            public void onSuccess(ICamOssInfoBean bean) {

            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    /**
     * 云账号登录后连接MQTT服务器
     *
     * @param mqttInfoBean
     */
    private void connectMQTT(MqttInfoBean mqttInfoBean) {
        //从accountBean取云MQTT的地址、账号、密码
        if (mqttInfoBean != null) {
            MQTTApiConfig.CloudServerURL = mqttInfoBean.host + ":" + mqttInfoBean.port;
            MQTTApiConfig.CloudUserName = mqttInfoBean.user;
            MQTTApiConfig.CloudUserPassword = mqttInfoBean.passwd;
            MainApplication.getApplication().getMqttManager().connectCloud();

            getAllDevice(true, true);
        }
    }


    public void getAllDevice(final boolean requestWifiDevice, final boolean requestGw) {
        final List<DeviceBean> allDeviceList = new ArrayList<>();
        getAllDevice(new getAllDeviceListener() {
            @Override
            public void onSuccess(List<DeviceBean> list) {
                Log.i("UserApiUnit", "all： " + list.size());
                allDeviceList.addAll(list);
                if (requestWifiDevice) {
                    getWifiDevice(allDeviceList);
                }
                if (requestGw) {
                    findGatewayID(allDeviceList);
                }

            }
        });


    }

    private void getWifiDevice(List<DeviceBean> allDeviceList) {
        final List<DeviceBean> deviceBeanList = new ArrayList<>();
        for (DeviceBean deviceBean :
                allDeviceList) {
            if (TextUtils.isEmpty(deviceBean.getType())
                    || !(deviceBean.getType().startsWith("GW") ||
                    TextUtils.equals(deviceBean.getType(), "CMICY1"))) {
                deviceBeanList.add(deviceBean);
            }
        }
        if (deviceBeanList != null && deviceBeanList.size() > 0) {
            final DeviceCache deviceCache = MainApplication.getApplication().getDeviceCache();
            for (DeviceBean device : deviceBeanList) {
                Device wifiDevice = new Device(device);
                deviceCache.add(wifiDevice);
                HomeWidgetManager.saveNewWifiWidget(wifiDevice);
            }
            if (!MainApplication.getApplication().hasInitSip) {
                doSomeSipAction(deviceBeanList);
            }
            EventBus.getDefault().post(new DeviceReportEvent(null));
        }
    }

    /**
     * 登录后匹配网关
     */
    private void findGatewayID(List<DeviceBean> allDeviceList) {
        final GatewayManager gatewayManager = new GatewayManager();
        final List<DeviceBean> deviceList = new ArrayList<>();
        for (DeviceBean deviceBean : allDeviceList) {
            if (deviceBean.isGateway()) {
                deviceBean.setName((DeviceInfoDictionary.getNameByTypeAndName(deviceBean.getType(), deviceBean.getName())));
                deviceList.add(deviceBean);
                gatewayManager.saveGatewayInfo(deviceBean);
            }
        }
        // 保存 绑定 的 网关的 数量
        if (deviceList.isEmpty()) {
            Preference.getPreferences().saveBindGatewayCount(0);
        } else {
            Preference.getPreferences().saveBindGatewayCount(deviceList.size());
        }
        List<String> gatewayIdList = new ArrayList<>();
        for (DeviceBean deviceBean : deviceList) {
            gatewayIdList.add(deviceBean.deviceId);
        }
        Preference.getPreferences().saveCurrentGatewayList(gatewayIdList);

        Preference preference = Preference.getPreferences();
        final String currentGatewayId = preference.getCurrentGatewayID();
        if (deviceList != null && deviceList.size() > 0) {
            DeviceBean selectedDeviceBean = null;
            if (TextUtils.isEmpty(currentGatewayId)) {
                for (DeviceBean deviceBean : deviceList) {
                    if (deviceBean.loginFlag == 1) {
                        selectedDeviceBean = deviceBean;
                        break;
                    }
                }
            } else {
                for (DeviceBean deviceBean : deviceList) {
                    if (TextUtils.equals(deviceBean.deviceId, currentGatewayId)) {
                        selectedDeviceBean = deviceBean;
                        break;
                    }
                }
            }
            if (selectedDeviceBean == null) {
                for (DeviceBean deviceBean : deviceList) {
                    if (deviceBean.isGateway()) {
                        selectedDeviceBean = deviceBean;
                        break;
                    }
                }
            }
            if (selectedDeviceBean == null) {
                MainApplication.getApplication().logoutGateway();
                return;
            }
            if (!selectedDeviceBean.isShared() && "2".equals(selectedDeviceBean.relationStatus)) {
                checkGatewayPassword(selectedDeviceBean);
            } else {
                switchGateway(selectedDeviceBean, "");
            }
        } else {
            Preference.getPreferences().saveCurrentGatewayID("");
            Preference.getPreferences().saveCurrentGatewayState("0");
            Preference.getPreferences().saveCurrentGatewayInfo("");
        }
        EventBus.getDefault().post(new GatewayStateChangedEvent(null));
    }


    private void getAllDevice(final getAllDeviceListener getAllDeviceListener) {
        final List<DeviceBean> currentDeviceList;
        final List<DeviceBean> allDeviceList;
        allDeviceList = new ArrayList<>();
        currentDeviceList = new ArrayList<>();
        deviceApiUnit.getWifiDeviceList("1", "10", new DeviceApiUnit.DeviceApiCommonListener<DeviceListBeanAll>() {
            @Override
            public void onSuccess(DeviceListBeanAll bean) {
                if (bean != null && bean.devices != null) {
                    currentDeviceList.addAll(bean.devices);
                }
                int allCount = Integer.parseInt(bean.totalCount);
                int size = Integer.parseInt("10");
                //总共需要分页的次数
                final int times = allCount % size == 0 ? allCount / size : allCount / size + 1;
                if (times <= 1) {
                    allDeviceList.addAll(currentDeviceList);
                    getAllDeviceListener.onSuccess(allDeviceList);
                } else {
                    //需要开启的线程总数
                    totalCount = times - 1;
                    Log.i("UserApiUnit", "totalCount =  " + totalCount);
                    allDeviceList.addAll(currentDeviceList);
                    createThreadPoolTask(totalCount, new getPageDeviceListener() {
                        @Override
                        public void onSuccess(List<DeviceBean> list) {
                            allDeviceList.addAll(list);
                            getAllDeviceListener.onSuccess(allDeviceList);
                            Log.i("UserApiUnit", "--->总设备数量" + allDeviceList.size());
                        }
                    });
                }
            }


            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    private void createThreadPoolTask(int times, final getPageDeviceListener listener) {
        totalCount = times;
        final List<DeviceBean> deviceList;
        final List<DeviceBean> totalList;
        deviceList = new ArrayList<>();
        totalList = new ArrayList<>();
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
        for (int i = 0; i < times; i++) {
            final int page = i + 2;//第二页开始
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        deviceApiUnit.getWifiDeviceList(String.valueOf(page), "10", new DeviceApiUnit.DeviceApiCommonListener<DeviceListBeanAll>() {
                            @Override
                            public void onSuccess(DeviceListBeanAll bean) {
                                --totalCount;
                                Log.i("UserApiUnit", "获取第" + bean.currentPage + "页数据，" + "剩余线程数：" + totalCount);
                                deviceList.addAll(bean.devices);
                                Log.i("UserApiUnit", "分页总设备数量 ：" + deviceList.size());
                                if (totalCount == 0) {
                                    totalList.addAll(deviceList);
                                    if (listener != null) {
                                        listener.onSuccess(totalList);
                                        Log.i("UserApiUnit", "---->分页获取到的总数 " + totalList.size());
                                    }
                                }
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                --totalCount;
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        fixedThreadPool.shutdown();
    }


    //根据http接口先存储网关的基本信息，后面mqtt接口的信息刷新为详细
    private void saveCurrentGatewayInfo(DeviceBean bean) {
        com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
        jsonObject.put("gwID", bean.deviceId);
        jsonObject.put("gwVer", bean.version);
        jsonObject.put("gwName", bean.getName());
        jsonObject.put("gwType", bean.getType());
        Preference.getPreferences().saveCurrentGatewayInfo(jsonObject.toJSONString());
    }

    private void checkGatewayPassword(final DeviceBean deviceBean) {
//        final Preference preference = Preference.getPreferences();
//        String currentGatewayId = deviceBean.deviceId;//preference.getCurrentGatewayID();
//        //查询网关密码
//        final String password = preference.getGatewayPassword(currentGatewayId);
//        if (TextUtils.isEmpty(password)) {
        EventBus.getDefault().postSticky(new NotifyInputGatewayPasswordEvent(deviceBean));
//        } else {
//            new DeviceApiUnit(context).doVerifyGwPwdAndSaveGwId(currentGatewayId, password, new DeviceApiUnit.DeviceApiCommonListener() {
//                @Override
//                public void onSuccess(Object bean) {
//                    switchGateway(deviceBean, password);
//                    if ("1".equals(deviceBean.getState())) {
//                        EventBus.getDefault().postSticky(new ConfirmPasswordEvent(deviceBean.deviceId, password));
//                    }
//                }
//
//                @Override
//                public void onFail(int code, String msg) {
//                    GatewayPasswordBean bean = new GatewayPasswordBean();
//                    bean.data = "0";
//                    EventBus.getDefault().postSticky(new GatewayPasswordChangedEvent(bean));
//                }
//            });
//        }
    }


    //切换网关。只有在云账号登录下才有这个功能。
    private void switchGateway(DeviceBean bean, String password) {
        final Preference preference = Preference.getPreferences();
        if (!TextUtils.isEmpty(password)) {
            //保存网关密码
            preference.saveGatewayPassword(bean.deviceId, password);
        }
        String currentGatewayId = preference.getCurrentGatewayID();
        preference.saveCurrentGatewayID(bean.deviceId);
        preference.saveGatewayRelationFlag(bean.relationFlag);
        getPushType(bean.deviceId);
        saveCurrentGatewayInfo(bean);
        preference.saveCurrentGatewayState(bean.state);
        EventBus.getDefault().post(new GatewayInfoEvent(null));
        EventBus.getDefault().post(new GatewayStateChangedEvent(null));
        if ("1".equals(bean.getState()) && bean.passwordStatus == 1) {
//            Intent intent = new Intent(context, ConfirmGatewayPasswordActivity.class);
//            intent.putExtra("oldPwd", preference.getGatewayPassword(bean.deviceId));
//            context.startActivity(intent);
            EventBus.getDefault().postSticky(new ConfirmPasswordEvent(bean.deviceId));
        }
        if (TextUtils.equals(bean.deviceId, currentGatewayId)) {
            if ("1".equals(bean.state)) {
                //这里的逻辑：如果网关在线，则已经在mqtt连接成功的回调里发送了设备请求，不执行下面的代码
                //如果网关离线，则需要继续执行下面的代码，加载缓存中的内容。
                return;
            }
        } else {
            new DeviceApiUnit(context).doSwitchGatewayId(bean.deviceId);
        }
        MainApplication.getApplication().getDeviceCache().clearZigBeeDevices();
        if (!bean.isPartialShared()) {
            MainApplication.getApplication().getDeviceCache().loadDatabaseCache(bean.deviceId, bean.state);//加载设备列表缓存
        }
        MainApplication.getApplication().getSceneCache().clear();
        MainApplication.getApplication().setSceneCached(false);
        MainApplication.getApplication().getRoomCache().clear();
        EventBus.getDefault().post(new GetSceneListEvent(null));
        EventBus.getDefault().post(new DeviceReportEvent(null));
//        MainApplication.getApplication().getMqttManager().disconnectGateway();
//        MainApplication.getApplication().getMqttManager().connectGatewayInCloud();
        MainApplication.getApplication().getMqttManager().sendGetDevicesInfoCmd(bean.deviceId, MQTTManager.MODE_GATEWAY_FIRST);
        MainApplication.getApplication().getMqttManager().publishEncryptedMessage(
                MQTTCmdHelper.createGetAllDevices(bean.deviceId, localInfo.appID),
                MQTTManager.MODE_GATEWAY_FIRST);
    }

    /**
     * 搜索用户/获取待授权用户基础信息
     */
    public void doSearchUser(String account, final UserApiCommonListener<UserBean> listener) {
        String key = null;
        if (StringUtil.isNullOrEmpty(account)) {
            return;
        }
        if (RegularTool.isLegalChinaPhoneNumber(account)) {
            key = "phone";
        } else if (RegularTool.isLegalEmailAddress(account)) {
            key = "email";
        }

        String url = ApiConstant.URL_USER_SEARCH_USERS_INFO + "/" + ApiConstant.getUserID();
        OkGo.get(url)
                .tag(this)
                .params("token", ApiConstant.getAppToken())
                .params(key, account)
                .execute(new EncryptCallBack<ResponseBean<UserBean>>() {

                    @Override
                    public void onSuccess(ResponseBean<UserBean> responseBean, Call call, Response response) {
                        WLog.e("doSearchUser:onSuccess", responseBean.toString());
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.e("doSearchUser:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    public void cancelRequest() {
        OkGo.getInstance().cancelTag(this);
    }

    private void getPushType(String gwId) {
        deviceApiUnit.doGetIsPush(gwId, new DeviceApiUnit.DeviceApiCommonListener<DeviceIsPushBean>() {
            @Override
            public void onSuccess(DeviceIsPushBean bean) {
                if (StringUtil.equals(bean.isPush, "0")) {
                    Preference.getPreferences().saveAlarmPush(false);
                } else if (StringUtil.equals(bean.isPush, "1")) {
                    Preference.getPreferences().saveAlarmPush(true);
                }
            }

            @Override
            public void onFail(int code, String msg) {
            }
        });
    }

    /**
     * 该方法的功能是获取到摄像机列表后直接进行sip初始化和注册,每次打开app只需注册一次(可视门锁除外)
     *
     * @param deviceBeanList
     */
    private void doSomeSipAction(List<DeviceBean> deviceBeanList) {
        for (DeviceBean device : deviceBeanList
                ) {
            ICamGetSipInfoBean iCamGetSipInfoBean = new ICamGetSipInfoBean();
            if (device.sipInfo != null && device.sipInfo.sdomain != null && device.sipInfo.spassword != null
                    && device.sipInfo.suid != null && device.sdomain != null) {
                iCamGetSipInfoBean.sipDomain = device.sipInfo.sdomain;
                iCamGetSipInfoBean.spassword = device.sipInfo.spassword;
                iCamGetSipInfoBean.suid = device.sipInfo.suid;
                iCamGetSipInfoBean.deviceDomain = device.sdomain;
                Preference.getPreferences().saveSipInfo(iCamGetSipInfoBean, ApiConstant.getUserID(), device.deviceId);
            }
        }
        for (DeviceBean device : deviceBeanList
                ) {
            if (!device.getType().equals("CMICA4") && !DeviceInfoDictionary.isLcCamera(device.type)) {
                ICamGetSipInfoBean bean = Preference.getPreferences().getSipInfo(ApiConstant.getUserID(), device.deviceId);
                if (!MainApplication.getApplication().hasRegisterSipAccount && bean != null) {
                    initSip(bean);
                }
                break;
            }

        }


    }


    private void initSip(final ICamGetSipInfoBean iCamGetSipInfoBean) {
        IPCResultCallBack initRTCAsyncCallback = new IPCResultCallBack() {
            @Override
            public void getResult(int result) {
                boolean hasInitSip = result == 0 ? true : false;
                WLog.i("icamProcess", "设备列表后初始化sip: " + hasInitSip);
                if (hasInitSip) {
                    MainApplication.getApplication().hasInitSip = hasInitSip;
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startRegister(iCamGetSipInfoBean);
                    }
                }, 500);
            }
        };
        IPCController.initRTCAsync(initRTCAsyncCallback);
    }


    private void startRegister(final ICamGetSipInfoBean iCamGetSipInfoBean) {
        IPCResultCallBack ipcResultCallBack = new IPCResultCallBack() {
            @Override
            public void getResult(int i) {
                boolean isRegisterAccount = i == 0 ? true : false;
                MainApplication.getApplication().isRegistering = false;
                WLog.i("icamProcess", "设备列表后注册sip账号: " + isRegisterAccount);
                if (isRegisterAccount) {
                    MainApplication.getApplication().hasRegisterSipAccount = true;
                    Preference.getPreferences().saveCurrentSipSuid(iCamGetSipInfoBean.suid);
                }
            }
        };
        String suid = iCamGetSipInfoBean.suid;
        String userSipPwd = iCamGetSipInfoBean.spassword;
        String sdomain = iCamGetSipInfoBean.sipDomain;
        IPCController.registerAccountAsync(ipcResultCallBack, suid, userSipPwd, sdomain);
        MainApplication.getApplication().isRegistering = true;
    }
}
