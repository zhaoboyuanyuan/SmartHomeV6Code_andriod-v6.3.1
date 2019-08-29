package cc.wulian.smarthomev6.main.application;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.cloudrtc.service.PjSipService;
import com.iflytek.cloud.SpeechUtility;
import com.lechange.opensdk.api.LCOpenSDK_Api;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.store.PersistentCookieStore;
import com.lzy.okgo.model.HttpHeaders;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.wulian.sdk.android.ipc.rtcv2.IPCController;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.database.Database;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import cc.wulian.smarthomev6.BuildConfig;
import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.DaoMaster;
import cc.wulian.smarthomev6.entity.DaoSession;
import cc.wulian.smarthomev6.entity.LocalInfo;
import cc.wulian.smarthomev6.main.appwidget.AppWidgetTool;
import cc.wulian.smarthomev6.main.device.camera_lc.business.Business;
import cc.wulian.smarthomev6.main.login.SigninActivity;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.EquesApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.WidgetBean;
import cc.wulian.smarthomev6.support.core.device.ActivationCache;
import cc.wulian.smarthomev6.support.core.device.AlarmCountCache;
import cc.wulian.smarthomev6.support.core.device.Device22KeyCodeCache;
import cc.wulian.smarthomev6.support.core.device.DeviceCache;
import cc.wulian.smarthomev6.support.core.device.GatewayConfigCache;
import cc.wulian.smarthomev6.support.core.device.RoomCache;
import cc.wulian.smarthomev6.support.core.device.SceneCache;
import cc.wulian.smarthomev6.support.core.device.WifiDeviceCache;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.AccountEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GatewayInfoEvent;
import cc.wulian.smarthomev6.support.event.GatewayStateChangedEvent;
import cc.wulian.smarthomev6.support.event.GetSceneListEvent;
import cc.wulian.smarthomev6.support.record.NameMap;
import cc.wulian.smarthomev6.support.record.WLPageRecordCreateListener;
import cc.wulian.smarthomev6.support.service.MainService;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.TTSTool;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.utils.LanguageUtil;
import cc.wulian.wrecord.PostHandleAdapter;
import cc.wulian.wrecord.WRecord;

/**
 * Created by Administrator on 2017/2/10 0010.
 */

public class MainApplication extends MultiDexApplication {

    private static MainApplication mInstance = null;

    private MQTTManager mqttManager;

    private EquesApiUnit equesApiUnit;

    private DeviceCache deviceCache;

    private WifiDeviceCache wifiDeviceCache;

    private RoomCache roomCache;

    private SceneCache sceneCache;

    private GatewayConfigCache gatewayConfigCache;

    private Device22KeyCodeCache device22KeyCodeCache;

    private AlarmCountCache alarmCountCache;

    private WidgetBean mWidgetBean;

    private ActivationCache mActivationCache;

    private SkinManager skinManager;
    // 是否获取到过 场景列表
    private boolean sceneCached = false;

    private boolean isGwSelfLogin = false;

    //是否初始化sip
    public boolean hasInitSip = false;

    //是否注册了sip账号
    public boolean hasRegisterSipAccount = false;

    //是否sip账号注册中
    public boolean isRegistering = false;

    //是否需要保持摄像机日志
    public boolean saveCameraLog = true;

    public int WidgetEnvironmentPosition = 0;

    public int WidgetSecurityPosition = 0;

    private LocalInfo localInfo;

    private List<Activity> activities = new ArrayList<>();

    public String forbiddenDevice;

    private final static String THIRD_PARTY_WEIXIN_ID = "wxe9bce02b56e7331d";
    private final static String THIRD_PARTY_WEIXIN_SECRET = "7a631d533daf48fad000ba78a09c43ea";
    private final static String THIRD_PARTY_QQ_ID = "1106062262";
    private final static String THIRD_PARTY_QQ_SECRET = "J52PNMOAI0KDa8ed";
    private final static String THIRD_PARTY_WEIBO_ID = "4066086631";
    private final static String THIRD_PARTY_WEIBO_SECRET = "cbd65de3f80442e8c29cec501ccd5b19";
    private final static String THIRD_PARTY_WEIBO_REDIRECTURL = "https://api.weibo.com/oauth2/default.html";

    //友盟关于各个平台的配置
    {
        configThirdPartyAppKey();
    }

    public synchronized static MainApplication getApplication() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        //置入一个不设防的VmPolicy，解决给第三方应用传递file:///的Uri抛出异常
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
//        if (!BuildConfig.DEBUG) {
        //自定义崩溃处理
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
//        }
        // 初始化ImageLoader
        @SuppressWarnings("deprecation")
        DisplayImageOptions options = new DisplayImageOptions.Builder().showStubImage(R.drawable.icon_stub) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.icon_empty) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.icon_error) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
                // .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(options)
                .threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);

        // 切换环境
//        ApiConstant.applyBaseUrl();
        // 启用动态server
        ApiConstant.applyBaseServer();

        //初始化OkGo
        OkGo.init(this);
        // 是否开启日志
        if (BuildConfig.LOG_OKGO) {
            // 打开该调试开关,打印级别INFO,并不是异常,是为了显眼,不需要就不要加入该行
            // 最后的true表示是否打印okgo的内部异常，一般打开方便调试错误
            OkGo.getInstance().debug("WLHttp", Level.INFO, true);
        }
        //以下设置的所有参数是全局参数,同样的参数可以在请求的时候再设置一遍,那么对于该请求来讲,请求中的参数会覆盖全局参数
        //好处是全局参数统一,特定请求可以特别定制参数
        try {
            //以下都不是必须的，根据需要自行选择,一般来说只需要 debug,缓存相关,cookie相关的 就可以了
            OkGo.getInstance()
                    //如果使用默认的 60秒,以下三行也不需要传
                    .setConnectTimeout(getResources().getInteger(R.integer.http_timeout))  //全局的连接超时时间
                    .setReadTimeOut(OkGo.DEFAULT_MILLISECONDS)     //全局的读取超时时间
                    .setWriteTimeOut(OkGo.DEFAULT_MILLISECONDS)    //全局的写入超时时间

                    //可以全局统一设置缓存模式,默认是不使用缓存,可以不传,具体其他模式看 github 介绍 https://github.com/jeasonlzy/
                    .setCacheMode(CacheMode.NO_CACHE)

                    //可以全局统一设置缓存时间,默认永不过期,具体使用方法看 github 介绍
                    .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)

                    //可以全局统一设置超时重连次数,默认为三次,那么最差的情况会请求4次(一次原始请求,三次重连请求),不需要可以设置为0
                    .setRetryCount(1)

                    //如果不想让框架管理cookie（或者叫session的保持）,以下不需要
//                .setCookieStore(new MemoryCookieStore())            //cookie使用内存缓存（app退出后，cookie消失）
                    .setCookieStore(new PersistentCookieStore())        //cookie持久化存储，如果cookie不过期，则一直有效

                    //可以设置https的证书,以下几种方案根据需要自己设置
                    .setCertificates();                                  //方法一：信任所有证书,不安全有风险
//                    .setCertificates(new SafeTrustManager())            //方法二：自定义信任规则，校验服务端证书
//                    .setCertificates(getAssets().open("srca.cer"))      //方法三：使用预埋证书，校验服务端证书（自签名证书）
//                    //方法四：使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
//                    .setCertificates(getAssets().open("xxx.bks"), "123456", getAssets().open("yyy.cer"))//

            //配置https的域名匹配规则，详细看demo的初始化介绍，不需要就不要加入，使用不当会导致https握手失败
//                    .setHostnameVerifier(new SafeHostnameVerifier())

            //可以添加全局拦截器，不需要就不要加入，错误写法直接导致任何回调不执行
//                .addInterceptor(new Interceptor() {
//                    @Override
//                    public Response intercept(Chain chain) throws IOException {
//                        return chain.proceed(chain.request());
//                    }
//                })

            //这两行同上，不需要就不要加入
//                    .addCommonHeaders(headers)  //设置全局公共头
//                    .addCommonParams(params);   //设置全局公共参数

            HttpHeaders headers = OkGo.getInstance().getCommonHeaders();
            if (headers == null) {
                headers = new HttpHeaders();
            }
            headers.put("WL-PARTNER-ID", ApiConstant.APPID);
            headers.put("WL-TOKEN", ApiConstant.getAppToken());
            headers.put("WL-TID", ApiConstant.getTerminalId());
            OkGo.getInstance().addCommonHeaders(headers);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // GreenDao 初始化/////////////////////////////////////////////
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "wl-test-db");
        final Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
        //爱看sdk初始化/////////////////////////////////////////////
        //IPC sdk开启日志
        IPCController.setLog(BuildConfig.LOG_IPC);
        //罗格朗门禁sdk初始化
        if (!PjSipService.isready()) {  //start PjSipService
            PjSipService.startService(this);
        }
        //友盟SDK初始化///////////////////////////////////////////
        UMShareAPI.get(this);
        //TalkingData初始化///////////////////////////////////
        TCAgent.LOG_ON = false;
        // App ID: 在TalkingData创建应用后，进入数据报表页中，在“系统设置”-“编辑应用”页面里查看App ID。
        // 渠道 ID: 是渠道标识符，可通过不同渠道单独追踪数据。
        TCAgent.init(this);
        // 如果已经在AndroidManifest.xml配置了App ID和渠道ID，调用TCAgent.init(this)即可；或与AndroidManifest.xml中的对应参数保持一致。
        TCAgent.setReportUncaughtExceptions(false);
        //科大讯飞语音合成初始化
        SpeechUtility.createUtility(this, "appid=" + "596c7412");
        //乐橙SDK初始化
        LCOpenSDK_Api.setHost("openapi.lechange.cn:443");
        deviceCache = new DeviceCache();
        wifiDeviceCache = new WifiDeviceCache();
        roomCache = new RoomCache();
        sceneCache = new SceneCache();
        alarmCountCache = new AlarmCountCache();
        gatewayConfigCache = new GatewayConfigCache();
        device22KeyCodeCache = new Device22KeyCodeCache();
        skinManager = SkinManager.getInstance(this);
        mActivationCache = new ActivationCache();

        // 打点框架初始化
        WRecord.init(this);
        // 打点时间上传的数量 默认20
        // 云可以修改这个数量 详见 NoticeMessageParser #parseMessage
        WRecord.setRecordCount(Preference.getPreferences().getRecordCount());
        // 类名和页面名映射
        WRecord.setPageMap(NameMap.nameMap);
        // 拦截页面  做其他特殊操作
        WRecord.setPageRecordCreateListener(new WLPageRecordCreateListener());
        // 发送事件
        WRecord.setPostHandle(new PostHandleAdapter() {
            @Override
            public void onRecord(JSONArray array) {
                DataApiUnit.doRecordUploads(array);
            }
        });

        //桌面widget初始化
        AppWidgetTool.updateAppWidgets(MainApplication.getApplication());
    }


    private void configThirdPartyAppKey() {
        PlatformConfig.setWeixin(THIRD_PARTY_WEIXIN_ID, THIRD_PARTY_WEIXIN_SECRET);
        PlatformConfig.setQQZone(THIRD_PARTY_QQ_ID, THIRD_PARTY_QQ_SECRET);
        PlatformConfig.setSinaWeibo(THIRD_PARTY_WEIBO_ID, THIRD_PARTY_WEIBO_SECRET, THIRD_PARTY_WEIBO_REDIRECTURL);
    }

    @Override
    public void onTerminate() {
        getMqttManager().distroy();
        super.onTerminate();
    }

    private DaoSession daoSession;

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public void stopApplication() {
        Preference mPreference = Preference.getPreferences();
        mPreference.saveIsNormalQuit(true);
        stopServcie();
    }

    public void startService() {
        startService(new Intent(this, MainService.class));
    }

    public void stopServcie() {
        stopService(new Intent(this, MainService.class));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

    public void logout(boolean isForce) {
        getMqttManager().disconnect();
        //清除token
        ApiConstant.setAppToken("");
        HttpHeaders headers = OkGo.getInstance().getCommonHeaders();
        if (headers == null) {
            headers = new HttpHeaders();
        }
        headers.put("WL-TOKEN", "");
        OkGo.getInstance().addCommonHeaders(headers);

        Preference preference = Preference.getPreferences();
        preference.saveUserEnterType("");
        preference.saveCurrentGatewayID("");
        preference.saveCurrentGatewayInfo("");
        preference.saveThirdPartyType(0);
        preference.saveThirdPartyUid("");
        preference.saveIsLogin(false);
        setSceneCached(false);
        // 清除账号信息
        preference.saveCurrentAccountInfo("{}");

        // 清理缓存
        getDeviceCache().clear();
        mWidgetBean = null;
        WidgetEnvironmentPosition = 0;
        WidgetSecurityPosition = 0;
        getAlarmCountCache().clear();
        getSceneCache().clear();
        getRoomCache().clear();
        getGatewayConfigCache().clear();
        getDevice22KeyCodeCache().clear();
        getActivationCache().clear();

        getEquesApiUnit().getIcvss().equesUserLogOut();

        // 发送事件
        EventBus.getDefault().post(new DeviceReportEvent(null));
        EventBus.getDefault().post(new GetSceneListEvent(null));
        EventBus.getDefault().post(new GatewayStateChangedEvent(null));
        EventBus.getDefault().post(new AccountEvent(AccountEvent.ACTION_LOGOUT, null));
        TTSTool.getInstance().clearAllTask();
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancelAll();
        finshActivitys();
        if (isForce) {
            Intent intent = new Intent(this, SigninActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    //退出网关登录操作
    public void logoutGateway() {
        final Preference preference = Preference.getPreferences();
        getMqttManager().disconnectGateway();
        preference.saveUserEnterType("");
        preference.saveCurrentGatewayID("");
        preference.saveCurrentGatewayInfo("");
        preference.saveThirdPartyLogin(false);
        preference.saveAutoLogin(false);
        preference.saveIsLogin(false);
        setSceneCached(false);

        // 清理缓存
        getDeviceCache().clearZigBeeDevices();
        mWidgetBean = null;
        WidgetEnvironmentPosition = 0;
        WidgetSecurityPosition = 0;
        getAlarmCountCache().clear();
        getSceneCache().clear();
        getRoomCache().clear();
        getGatewayConfigCache().clear();
        getDevice22KeyCodeCache().clear();
        getActivationCache().clear();

        // 发送事件
        EventBus.getDefault().post(new GetSceneListEvent(null));
        EventBus.getDefault().post(new DeviceReportEvent(null));
        EventBus.getDefault().post(new GatewayStateChangedEvent(null));
        EventBus.getDefault().post(new AccountEvent(AccountEvent.ACTION_LOGOUT, null));
        TTSTool.getInstance().clearAllTask();
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancelAll();
    }

    public void clearCurrentGateway() {
        final Preference preference = Preference.getPreferences();
        getMqttManager().disconnectGateway();
        preference.saveCurrentGatewayID("");
        preference.saveCurrentGatewayInfo("");
        preference.saveGatewayRelationFlag("");
        preference.saveCurrentGatewayState("0");
        setSceneCached(false);

        // 清理缓存
        getDeviceCache().clearZigBeeDevices();
        mWidgetBean = null;
        WidgetEnvironmentPosition = 0;
        WidgetSecurityPosition = 0;
        getAlarmCountCache().clear();
        getSceneCache().clear();
        getRoomCache().clear();
        getGatewayConfigCache().clear();
        getDevice22KeyCodeCache().clear();
        getActivationCache().clear();

        // 发送事件
        EventBus.getDefault().post(new GatewayInfoEvent(null));
        EventBus.getDefault().post(new GetSceneListEvent(null));
        EventBus.getDefault().post(new DeviceReportEvent(null));
        EventBus.getDefault().post(new GatewayStateChangedEvent(null));
    }

    /**
     * 切换网关
     */
    public void switchGateway(DeviceBean bean, String password) {
        Preference preference = Preference.getPreferences();
        //清空当前网关信息
        clearCurrentGateway();
        //保存网关密码
        preference.saveGatewayPassword(bean.deviceId, password);
        preference.saveCurrentGatewayID(bean.deviceId);
        new DeviceApiUnit(this).doSwitchGatewayId(bean.deviceId);
        saveCurrentGatewayInfo(bean);
        preference.saveCurrentGatewayState(bean.state);
        preference.saveGatewayRelationFlag(bean.relationFlag);
        EventBus.getDefault().post(new GatewayStateChangedEvent(null));
        if (!bean.isPartialShared()) {
            getDeviceCache().loadDatabaseCache(bean.deviceId, bean.state);//加载设备列表缓存
        }
        EventBus.getDefault().post(new DeviceReportEvent(null));
        requestAllInfo(bean.deviceId);
    }

    //根据http接口先存储网关的基本信息，后面mqtt接口的信息刷新为详细
    private void saveCurrentGatewayInfo(DeviceBean bean) {
        Preference preference = Preference.getPreferences();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("gwID", bean.deviceId);
        jsonObject.put("gwVer", bean.version);
        jsonObject.put("gwName", bean.getName());
        jsonObject.put("gwType", bean.getType());
        jsonObject.put("hostFlag", bean.getHostFlag());
        preference.saveCurrentGatewayInfo(jsonObject.toJSONString());
    }

    private void requestAllInfo(String gatewayId) {
        MQTTManager mqttManager = getMqttManager();
        LocalInfo localInfo = getLocalInfo();
        mqttManager.sendGetDevicesInfoCmd(gatewayId, mqttManager.MODE_CLOUD_ONLY);
        mqttManager.publishEncryptedMessage(
                MQTTCmdHelper.createGatewayInfo(gatewayId, 0, localInfo.appID, null),
                MQTTManager.MODE_CLOUD_ONLY);
    }

    public MQTTManager getMqttManager() {
        if (mqttManager == null) {
            mqttManager = new MQTTManager(this);
        }
        return mqttManager;
    }

    public EquesApiUnit getEquesApiUnit() {
        if (equesApiUnit == null) {
            equesApiUnit = new EquesApiUnit(this);
        }
        return equesApiUnit;
    }

    public WidgetBean getWidgetBean() {
        return mWidgetBean;
    }

    public void setWidgetBean(WidgetBean widgetBean) {
        mWidgetBean = widgetBean;
    }

    public DeviceCache getDeviceCache() {
        return deviceCache;
    }

    public WifiDeviceCache getWifiDeviceCache() {
        return wifiDeviceCache;
    }

    public AlarmCountCache getAlarmCountCache() {
        return alarmCountCache;
    }

    public RoomCache getRoomCache() {
        return roomCache;
    }

    public SceneCache getSceneCache() {
        return sceneCache;
    }

    public GatewayConfigCache getGatewayConfigCache() {
        return gatewayConfigCache;
    }

    public Device22KeyCodeCache getDevice22KeyCodeCache() {
        return device22KeyCodeCache;
    }

    public SkinManager getSkinManager() {
        return skinManager;
    }

    public ActivationCache getActivationCache() {
        return mActivationCache;
    }

    public boolean isSceneCached() {
        return sceneCached;
    }

    public void setSceneCached(boolean b) {
        this.sceneCached = b;
    }

    public boolean isGwSelfLogin() {
        return isGwSelfLogin;
    }

    public void setGwSelfLogin(boolean gwAutoLogin) {
        isGwSelfLogin = gwAutoLogin;
    }

    public LocalInfo getLocalInfo() {
        return getLocalInfo(true);
    }

    public LocalInfo getLocalInfo(boolean readCache) {
        if (localInfo == null || !readCache) {
            localInfo = new LocalInfo();
            //获取设备唯一标识
            localInfo.imei = Preference.getPreferences().getDeviceID();
            if (TextUtils.isEmpty(localInfo.imei)) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_PHONE_STATE)
                        == PackageManager.PERMISSION_GRANTED) {
                    //在有权限的情况下，先尝试获取IMEI码
                    TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    localInfo.imei = manager.getDeviceId();
                }
                if (TextUtils.isEmpty(localInfo.imei)) {
                    //尝试获取SERIAL码
                    try {
                        localInfo.imei = android.os.Build.class.getField("SERIAL").get(null).toString();
                    } catch (Exception e) {
                        //发生错误的情况下，使用随机生成的UUID
                        localInfo.imei = UUID.randomUUID().toString();
                        e.printStackTrace();
                    }
                }
                Preference.getPreferences().saveDeviceID(localInfo.imei);
            }
            localInfo.appID = "android" + localInfo.imei;
            try {
                String pkName = getPackageName();
                localInfo.appVersion = getPackageManager().getPackageInfo(pkName, 0).versionName;
            } catch (Exception e) {
            }

            localInfo.appLang = LanguageUtil.getCloudLanguage();

            localInfo.osVersion = android.os.Build.VERSION.RELEASE;
            try {
                ApplicationInfo appInfo = getPackageManager()
                        .getApplicationInfo(getPackageName(),
                                PackageManager.GET_META_DATA);
                localInfo.market = appInfo.metaData.getString("UMENG_CHANNEL");
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return localInfo;
    }

    public void pushActivity(Activity activity) {
        activities.add(activity);
    }

    public void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void finshActivitys() {
        for (int i = (activities.size() - 1); i > 0; i--) {
            activities.get(i).finish();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
