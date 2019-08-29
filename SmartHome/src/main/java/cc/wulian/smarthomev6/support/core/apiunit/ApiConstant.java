package cc.wulian.smarthomev6.support.core.apiunit;

import android.text.TextUtils;

import cc.wulian.smarthomev6.BuildConfig;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.tools.Preference;

/**
 * Created by zbl on 2017/3/1
 */

public class ApiConstant {
    //开发 https://testv2.wulian.cc:50090
    //测试 https://testv6.wulian.cc
    //正式 https://iot.wuliancloud.com:443
//    public static  String BASE_URL = BuildConfig.BASE_DOMAIN;
    public static final String BASE_URL = "https://iot.wuliancloud.com:443";//修改成正式服务器用于测试正式环境
//    public static final String BASE_URL = "https://testv6.wulian.cc";
    private static String sBaseServer = BASE_URL;


//    public static final String BASE_URL = "https://testv6.wulian.cc";
    public static String BASE_URL_USER = sBaseServer + "/iot/v2/users";
    //开发 test.wuliangroup.cn
    //测试 hblc.wuliangroup.cn 河北服务器
    //正式 shlc.wuliangroup.cn 上海服务器
//    public static final String ICAM_URL = BuildConfig.ICAM_DOMAIN;

      public static final String ICAM_URL = "shlc.wuliangroup.cn";//修改成正式服务器用于测试正式环境

    public static final String EQUES_URL = "thirdparty.ecamzone.cc:8443";
    public static final String APPKEY = "fb1bbde01c9a4d45d82d5f5107b1f4dd7c105af06c928ce14878cdda03874dcc";
    public static final String APPID = "wulian_app";
    private static String APP_PROJECT = APPID;
    //移康猫眼appkey/keyid
    public static final String EQUES_APPKEY = "FNXiNhNZnRar5QbAWYJ2QX4PNfdkwNNP";
    public static final String EQUES_KEYID = "a9048a3c38de2d7a";
    //控制平台
    public static final String WHITEROBOT_CLIEND_ID = "4950b3e9ac857553";//小白机器人第三方ID

    private static String appToken;
    private static String userID;
    private static String secretKey;
    private static byte[] AESKey;
    private static String userPassword;
    private static String terminalId;
    public static String BUCKET;
    public static String REGION;

    public static String getAppProject() {
        return APP_PROJECT;
    }

    public static void setAppProject(String project) {
        APP_PROJECT = project;
    }

//    /**
//     * 修改 BASE_URL
//     */
//    public static void setBaseUrl(String baseUrl) {
//        ApiConstant.BASE_URL = baseUrl;
//        Preference.getPreferences().saveBaseUrl(BASE_URL);
//        Preference.getPreferences().setBaseUrlChanged(true);
//        MainApplication.getApplication().logout(true);
//        applyBaseUrl();
//    }
//
//    /**
//     * 重置 BASE_URL
//     */
//    public static void resetBaseUrl() {
//        ApiConstant.BASE_URL = BuildConfig.BASE_DOMAIN;
//        Preference.getPreferences().saveBaseUrl(BASE_URL);
//        Preference.getPreferences().setBaseUrlChanged(false);
//        MainApplication.getApplication().logout(true);
//        applyBaseUrl();
//    }
//
//    /**
//     * 应用 baseUrl
//     */
//    public static void applyBaseUrl() {
//        // 正式环境不能切换系统
//        if ("release".equals(BuildConfig.BUILD_TYPE)) {
//            ApiConstant.BASE_URL = BuildConfig.BASE_DOMAIN;
//            loadBaseServer();
//        } else {
//            if (Preference.getPreferences().isBaseUrlChanged()) {
//                ApiConstant.BASE_URL = Preference.getPreferences().getBaseUrl();
//                loadBaseServer();
//            } else {
//                ApiConstant.BASE_URL = BuildConfig.BASE_DOMAIN;
//                loadBaseServer();
//            }
//        }
//    }

    /**
     * 获取 baseServer
     */
    public static String getBaseServer() {
        if (sBaseServer == null) {
            sBaseServer = Preference.getPreferences().getBaseServer();
        }
        return sBaseServer;
    }

    /**
     * 修改 baseServer
     */
    public static void setBaseServer(String baseServer) {
        ApiConstant.sBaseServer = baseServer;
        Preference.getPreferences().saveBaseServer(sBaseServer);
        applyBaseServer();
    }

    /**
     * 应用 baseServer
     */
    public static void applyBaseServer() {
        ApiConstant.sBaseServer = Preference.getPreferences().getBaseServer();
        if (sBaseServer.endsWith("/")) {
            sBaseServer = sBaseServer.substring(0, sBaseServer.length() - 1);
        }
        loadBaseServer();
    }

    public static String getAppToken() {
        if (appToken == null) {
            appToken = Preference.getPreferences().getAppToken();
        }
        return appToken;
    }

    public static String getUserID() {
        if (userID == null) {
            userID = Preference.getPreferences().getUserID();
        }
        return userID;
    }

    public static String getSecretKey() {
        if (secretKey == null) {
            secretKey = Preference.getPreferences().getCloudSecretKey();
        }
        return secretKey;
    }

    public static byte[] getAESKey() {
        if (AESKey == null) {
            AESKey = Preference.getPreferences().getCloudAESKey();
        }
        return AESKey;
    }

    public static String getUserPassword() {
        if (userPassword == null) {
            userPassword = Preference.getPreferences().getUserPasswordMD5();
        }
        return userPassword;
    }

    public static String getTerminalId() {
        if (terminalId == null) {
            terminalId = Preference.getPreferences().getTerminalId();
        }
        return terminalId;
    }

    public static void setAppToken(String appToken) {
        ApiConstant.appToken = appToken;
        Preference.getPreferences().saveAppToken(appToken);
    }

    public static void setUserID(String userID) {
        ApiConstant.userID = userID;
        Preference.getPreferences().saveUserID(userID);
    }

    public static void setSecretKey(String secretKey) {
        ApiConstant.secretKey = secretKey;
        Preference.getPreferences().saveCloudSecretKey(secretKey);
    }

    public static void setAESKey(byte[] aeskey) {
        AESKey = aeskey;
        Preference.getPreferences().saveCloudAESKey(aeskey);
    }

    public static void setUserPassword(String userPassword) {
        ApiConstant.userPassword = userPassword;
        Preference.getPreferences().saveUserPasswordMD5(userPassword);
    }

    public static void setTerminalId(String terminalId) {
        ApiConstant.terminalId = terminalId;
        Preference.getPreferences().saveTerminalId(terminalId);
    }

    // 账号相关
    public static String URL_USER_SEARCH_USERS_INFO = BASE_URL_USER + "/users-simple-info";
    public static String URL_DATA_RECORDS = sBaseServer + "/iot/v2/records";
    public static String URL_BLOOD_PRESSURE_DATA_RECORDS = sBaseServer + "/log/records";
    public static String URL_VOICE_CONTROL = sBaseServer + "/iot/v2/voicebox/service";// 语音控制

    // 账号相关(新) - 1
    public static String URL_SSO_BASE = BASE_URL + "/sso";
    public static String URL_SSO_REGISTER_DEVICE = URL_SSO_BASE + "/terminal/reg";
    public static String URL_SSO_GET_REGISTER_VERIFICATION_CODE = URL_SSO_BASE + "/reg/phonecode";
    public static String URL_SSO_REGISTER_PHONE = URL_SSO_BASE + "/reg/byphone";
    public static String URL_SSO_LOGIN = URL_SSO_BASE + "/login/byphone";
    public static String URL_SSO_LOGIN_SMS = URL_SSO_BASE + "/login/phone/email/login";
    // 账号相关(新) - 2
    public static String URL_SSO_LOGIN_THIRD = URL_SSO_BASE + "/login/bythird";
    public static String URL_SSO_LOGOU = URL_SSO_BASE + "/login/logout";
    public static String URL_SSO_TOKEN_TO_MQTT = URL_SSO_BASE + "/token/mqtt";
    public static String URL_SSO_LOGIN_EMAIL = URL_SSO_BASE + "/login/byemail";
    public static String URL_SSO_THIRD_BIND = URL_SSO_BASE + "/login/third/bind";
    // 账号相关(新) - 3
    public static String URL_SSO_REGISTER_THIRD = URL_SSO_BASE + "/reg/bythird";
    public static String URL_SSO_REPLACE_THIRD = URL_SSO_BASE + "/login/third/update";
    public static String URL_API_CHANGE_PWD_EMAIL = URL_SSO_BASE + "/user/updatepass/sendemailcode";
    public static String URL_API_CHANGE_PWD_EMAIL_VERIFY = URL_SSO_BASE + "/user/updatepass/byemailcode";
    public static String URL_SSO_GET_MAIL_REGISTER_VERIFICATION_CODE = URL_SSO_BASE + "/reg/byemailcode";
    // 账号相关(新) - 4
    public static String URL_SSO_REGISTER_MAIL = URL_SSO_BASE + "/reg/byemailvalidate";
    public static String URL_SSO_CHECK_VERIFICATION_CODE_REGISTER = URL_SSO_BASE + "/reg/iot/v3/registCheckCode";
    public static String URL_SSO_CHECK_VERIFICATION_CODE_FORGOT = URL_SSO_BASE + "/user/iot/v3/updatePassCheckCode";
    public static String URL_SSO_REGISTER_AND_LOGIN = URL_SSO_BASE + "/reg/iot/v3/registByEmailAndPhone";
    public static String URL_SSO_UPDATE_AND_LOGIN_BY_ACCOUNT = URL_SSO_BASE + "/user/iot/v3/updatePassAndLogin";
    // 账号相关(新) - 5
    public static String URL_SSO_UPDATE_AND_LOGIN_BY_PWD = URL_SSO_BASE + "/user/iot/v3/updatePassByOldPass";
    public static String URL_GET_CHANGE_PWD_VERIFICATION_CODE = URL_SSO_BASE + "/user/updatepass/phonecode";
    public static String URL_API_THIRD_GET_PHONECODE = URL_SSO_BASE + "/user/bindthird/phonecode";
    public static String URL_GET_SMS_LOGIN_VERIFICATION_CODE = URL_SSO_BASE + "/login/phone/email/sendCode";

    // Api - 1
    public static String URL_API_BASE = sBaseServer + "/api";
    public static String URL_API_APP_INFO = URL_API_BASE + "/app/getAppInfo";
    public static String URL_CHANGE_PWD_BY_PHONE = URL_API_BASE + "/user/updatepass/byphone";
    public static String URL_API_USER_INFO = URL_API_BASE + "/user/info";
    public static String URL_API_CHANGE_PWD_BY_OLDPWD = URL_API_BASE + "/user/updatepass";
    // Api - 2
    public static String URL_API_CHANGE_PHONE_VERIFICATION_CODE = URL_API_BASE + "/user/update/phone/phonecode";
    public static String URL_API_CHANGE_PHONE = URL_API_BASE + "/user/phone/update";
    public static String URL_API_CHANGE_AVATAR = URL_API_BASE + "/user/avatar/update";
    public static String URL_API_CHANGE_USER_INFO = URL_API_BASE + "/user/info/update";
    public static String URL_API_GET_WIDGET_INFO = URL_API_BASE + "/widget/getUserWidgetInfo";
    // Api - 3
    public static String URL_API_SAVE_WIDGET_INFO = URL_API_BASE + "/widget/saveUserWidgetInfo";
    public static String URL_API_BIND_EMAIL = URL_API_BASE + "/user/email/update";
    public static String URL_API_USER_CENTER_GET = URL_API_BASE + "/user/third/get";
    public static String URL_API_USER_CENTER_BIND = URL_API_BASE + "/user/third/bind";
    public static String URL_API_USER_CENTER_UNBIND = URL_API_BASE + "/user/third/unbind";
    // Api - 4
    public static String URL_API_EMAIL_VERIFY = URL_API_BASE + "/user/email/update/verify";
    public static String URL_API_GET_VERIFY_PHONE_CODE = URL_API_BASE + "/user/update/phone/oldphonecode";
    public static String URL_API_VERIFY_PHONE = URL_API_BASE + "/user/update/phone/verifyoldphone";
    public static String URL_API_GET_VERIFY_OLD_MAIL_CODE = URL_API_BASE + "/user/update/email/oldemailcode";
    public static String URL_API_VERIFY_OLD_MAIL = URL_API_BASE + "/user/update/email/checkoldemailcode";
    // Api - 5
    public static String URL_API_FEEDBACK_SAVE = URL_API_BASE + "/feedback/saveFeedback";
    public static String URL_APP_UPLOAD_FILE = sBaseServer + "/OSS/staff/appUploadFile.do";
    public static String URL_APP_UPLOADS = URL_API_BASE + "/app/uploads";
    public static String URL_UEI_BRAND = sBaseServer + "/iot/v2/uei/%s/brand";
    public static String URL_UEI_CODE_LIST = sBaseServer + "/iot/v2/uei/%s/code";
    // Api - 6
    public static String URL_UEI_CODE_PICKS = sBaseServer + "/iot/v2/uei/%s/picks";
    public static String URL_UEI_CODE_MODEL = sBaseServer + "/iot/v2/uei/%s/model";
    public static String URL_RECORD_DELETE_MESSAGE = sBaseServer + "/iot/v2/records/%s/devices/%s";
    public static String URL_SKIN_LIST = URL_API_BASE + "/theme/theme/app/getAllThemes";//皮肤列表
    public static String URL_BANNERS = URL_API_BASE + "/slideshow/getAllSlides";//首页banner数据
    public static String URL_ADVS = URL_API_BASE + "/luban/adv/info";//广告页数据
    public static String URL_PLATFORM_LIST = URL_API_BASE + "/user/icp";//授权平台列表数据
    // Api - 7
    public static String URL_USER_PUSH_AUDIENCE = sBaseServer + "/iot/v2/users/%s/user-push-audience";//短信通知告警
    public static String URL_MEDIA_SHARE = sBaseServer + "/iot/v2/users/%s/notice-share";//图片/视频分享到微信等第三方接口
    public static String URL_DEVICE_ACTIVE = sBaseServer + "/iot/v2/users/%s/device/active"; // 设备激活
    public static String URL_DREAM_FLOWER_PROVINCE = URL_API_BASE + "/area/province";//梦想之花网关省份
    public static String URL_DREAM_FLOWER_CITY = URL_API_BASE + "/area/city";//梦想之花网关城市
    public static String URL_GET_GATEWAY_VERSION = sBaseServer + "/iot/v2/users/%s/gateway/version"; // 获取网关版本号
    //ent
    public static String URL_DREAM_FLOWER_ENT_BASE = sBaseServer + "/ent";
    public static String URL_DREAM_FLOWER_PLAY = URL_DREAM_FLOWER_ENT_BASE + "/music/playMusic";
    public static String URL_DREAM_FLOWER_CHANGE_PLAY_MODE = URL_DREAM_FLOWER_ENT_BASE + "/music/set/play/status";
    public static String URL_DREAM_FLOWER_NEXT_OR_LAST = URL_DREAM_FLOWER_ENT_BASE + "/music/nextOrLast";
    public static String URL_DREAM_FLOWER_GET_MODE = URL_DREAM_FLOWER_ENT_BASE + "/music/get/playMode";
    //安全狗
    public static String URL_SAFE_DOG_PROTECTED_DEVICE = sBaseServer + "/safe/v2/users/%s/safedogs/%s/protect-devices";
    public static String URL_SAFE_DOG_UNPROTECTED_DEVICE = sBaseServer + "/safe/v2/users/%s/safedogs/%s/unprotect-devices";
    public static String URL_SAFE_DOG_GET_SCHEDULES = sBaseServer + "/safe/v2/users/%s/safedogs/%s/schedules";
    public static String URL_SAFE_DOG_GET_INCIDENTS = sBaseServer + "/safe/v2/users/%s/safedogs/%s/incidents";
    public static String URL_SAFE_DOG_GET_HOME_DATA = sBaseServer + "/safe/v2/users/%s/safedogs/%s/data";
    public static String URL_SAFE_DOG_SCAN = sBaseServer + "/safe/v2/users/%s/safedogs/%s/scan";
    //视频直播流
    public static String URL_LIVE_STREAM = sBaseServer + "/iot/v2/live/%s/%s/stream/status";
    public static String URL_LIVE_STREAM_ADDR = sBaseServer + "/iot/v2/live/%s/%s/stream/address/pull";
    //体重秤
    public static String URL_SAVE_WEIGHT = sBaseServer + "/iot/v2/users/%s/weight/save";
    public static String URL_GET_WEIGHT_RECORDS = sBaseServer + "/iot/v2/users/%s/weight/%s";
    //小物摄像机预置位
    public static String URL_SAVE_CYLINCAM_POSITION = URL_API_BASE + "/preset/position/save";
    public static String URL_GET_CYLINCAM_POSITION = URL_API_BASE + "/preset/position/list";
    public static String URL_DELETE_CYLINCAM_POSITION = URL_API_BASE + "/preset/position/delete";
    //通用上传图片
    public static String URL_UPDATE_IMAGE = URL_API_BASE + "/user/image/upload";
    //向往背景音乐
    public static String URL_WISH_ACTIVATE = sBaseServer + "/iot/v2/users/%s/devices/activeCode/status";
    //海信子设备信息
    public static String URL_HISENSE_CHILD_DEVICE = sBaseServer + "/iot/v2/users/%s/target-devices/%s";
    //wifi红外转发器
    public static String URL_WIFI_IR_CODE_LIBRARY = sBaseServer + "/iot/v2/uei/%s/codelib";
    public static String URL_WIFI_IR_BLOCK = sBaseServer + "/iot/v2/uei/%s/ir/block";
    public static String URL_WIFI_IR_KEY = sBaseServer + "/iot/v2/uei/%s/ir/key";
    public static String URL_WIFI_IR_KEY_NAME = sBaseServer + "/iot/v2/uei/%s/ir/key/name";
    public static String URL_WIFI_IR_KEY_CODE = sBaseServer + "/iot/v2/uei/%s/ir/key/code";
    public static String URL_WIFI_IR_LEARN = sBaseServer + "/iot/v2/uei/%s/ir/learning-status";
    public static String URL_WIFI_IR_CONTROL = sBaseServer + "/iot/v2/uei/%s/ir/control";
    public static String URL_WIFI_IR_MATCH = sBaseServer + "/iot/v2/uei/%s/ir/match";
    public static String URL_WIFI_IR_UPGRADE = sBaseServer + "/iot/v2/uei/%s/ir/upgrade";
    public static String URL_WIFI_IR_PART_CODELIB = sBaseServer + "/iot/v2/uei/%s/ir/part-codelib";
    //体验网关
    public static String URL_EXPERIENCE_GW = BASE_URL_USER + "/experienceGWState";
    //获取WiFi设备列表以及网关列表
    public static String URL_WIFI_DEVICE_LIST = BASE_URL_USER + "/%s/devices/paging";
    //校验网关是否支持设备（lite网关）
    public static String URL_GATEWAY_SUPPORT_DEVICE = BASE_URL_USER + "/%s/device/gateway/support";
    //v5升级v6
    public static String URL_V5_GATEWAY_PWD_CHECK = sBaseServer + "/iot/v2/devices/%s/v5/gw/password/check";
    public static String URL_V5_GATEWAY_UPGRADE = sBaseServer + "/iot/v2/devices/%s/v5/gw/upgrade";
    //乐橙摄像机
    public static String URL_SET_REVERSE_STATUS = sBaseServer + "/iot/v2/devices/%s/camera/frame-reverse-status";
    public static String URL_SET_DETECTION_SWITCH = sBaseServer + "/iot/v2/devices/%s/alarm/status";
    public static String URL_DEVICE_UPGRADE = sBaseServer + "/iot/v2/devices/%s/version/upgrade";
    public static String URL_SD_FORMAT = sBaseServer + "/iot/v2/devices/%s/camera/sd/recover";
    public static String URL_CAMERA_PTZ = sBaseServer + "/iot/v2/devices/%s/camera/ptz";
    public static String URL_SET_DETECTION_PAN = sBaseServer + "/iot/v2/devices/%s/alarm/info";
    public static String URL_LOCAL_RECORD = sBaseServer + "/iot/v2/records/%s/camera/local";
    //罗格朗门禁
    public static String URL_ENTRANCE_GUARD_UNLOCK = sBaseServer + "/iot/v2/devices/%s/legrand/door/open";
    public static String URL_REMOTE_HANG_UP = sBaseServer + "/iot/v2/devices/%s/legrand/remotehangup";
    public static String URL_ENTRANCE_GUARD_SIP_REG = BASE_URL_USER + "/%s/legrand/sip/reg";
    public static String URL_ENTRANCE_GUARD_PROVINCE = BASE_URL_USER + "/%s/legrand/community/info/province";
    public static String URL_ENTRANCE_GUARD_CITY = BASE_URL_USER + "/%s/legrand/community/info/province/city";
    public static String URL_ENTRANCE_GUARD_CITY_DISTRICT = BASE_URL_USER + "/%s/legrand/community/info/province/city/district";
    public static String URL_ENTRANCE_GUARD_COMMUNITY = BASE_URL_USER + "/%s/legrand/community/info/province/city/district/community";
    public static String URL_ENTRANCE_GUARD_DISTRICT = BASE_URL_USER + "/%s/legrand/community/address/info/district";
    public static String URL_ENTRANCE_GUARD_BUILDING = BASE_URL_USER + "/%s/legrand/community/address/info/district/building";
    public static String URL_ENTRANCE_GUARD_UNIT = BASE_URL_USER + "/%s/legrand/community/address/info/district/building/unit";
    public static String URL_ENTRANCE_GUARD_FLOOR = BASE_URL_USER + "/%s/legrand/community/address/info/district/building/unit/floor";
    public static String URL_ENTRANCE_GUARD_HOUSE = BASE_URL_USER + "/%s/legrand/community/address/info/district/building/unit/floor/house";
    public static String URL_ENTRANCE_GUARD_DEVICE_INFO = BASE_URL_USER + "/%s/legrand/community/address/info/district/building/unit/floor/house/device";
    public static String URL_ENTRANCE_GUARD_ADDRESS_INFO = BASE_URL_USER + "/%s/legrand/community/address/info";
    public static String URL_ENTRANCE_GUARD_COMMUNITY_INFO = BASE_URL_USER + "/%s/legrand/community/info";
    public static String URL_ENTRANCE_GUARD_HOST_INFO = sBaseServer + "/iot/v2/devices/%s/legrand/host/info";
    public static String URL_ENTRANCE_GUARD_VERIFY_CODE = sBaseServer + "/iot/v2/devices/%s/legrand/verificatioCode";
    public static String URL_ENTRANCE_GUARD_PROPERTY_INFO = sBaseServer + "/iot/v2/devices/%s/legrand/property/info";


    /**
     * 重新加载 BaseServer
     */
    private static void loadBaseServer() {
        BASE_URL_USER = sBaseServer + "/iot/v2/users";

        // 账号相关
        URL_USER_SEARCH_USERS_INFO = BASE_URL_USER + "/users-simple-info";
        URL_DATA_RECORDS = sBaseServer + "/iot/v2/records";
        URL_BLOOD_PRESSURE_DATA_RECORDS = sBaseServer + "/log/records";
        URL_VOICE_CONTROL = sBaseServer + "/iot/v2/voicebox/service";

        // Api - 1
        URL_API_BASE = sBaseServer + "/api";
        URL_API_APP_INFO = URL_API_BASE + "/app/getAppInfo";
        URL_CHANGE_PWD_BY_PHONE = URL_API_BASE + "/user/updatepass/byphone";
        URL_API_USER_INFO = URL_API_BASE + "/user/info";
        URL_API_CHANGE_PWD_BY_OLDPWD = URL_API_BASE + "/user/updatepass";
        // Api - 2
        URL_API_CHANGE_PHONE_VERIFICATION_CODE = URL_API_BASE + "/user/update/phone/phonecode";
        URL_API_CHANGE_PHONE = URL_API_BASE + "/user/phone/update";
        URL_API_CHANGE_AVATAR = URL_API_BASE + "/user/avatar/update";
        URL_API_CHANGE_USER_INFO = URL_API_BASE + "/user/info/update";
        URL_API_GET_WIDGET_INFO = URL_API_BASE + "/widget/getUserWidgetInfo";
        // Api - 3
        URL_API_SAVE_WIDGET_INFO = URL_API_BASE + "/widget/saveUserWidgetInfo";
        URL_API_BIND_EMAIL = URL_API_BASE + "/user/email/update";
        URL_API_USER_CENTER_GET = URL_API_BASE + "/user/third/get";
        URL_API_USER_CENTER_BIND = URL_API_BASE + "/user/third/bind";
        URL_API_USER_CENTER_UNBIND = URL_API_BASE + "/user/third/unbind";
        // Api - 4
        URL_API_EMAIL_VERIFY = URL_API_BASE + "/user/email/update/verify";
        URL_API_GET_VERIFY_PHONE_CODE = URL_API_BASE + "/user/update/phone/oldphonecode";
        URL_API_VERIFY_PHONE = URL_API_BASE + "/user/update/phone/verifyoldphone";
        URL_API_GET_VERIFY_OLD_MAIL_CODE = URL_API_BASE + "/user/update/email/oldemailcode";
        URL_API_VERIFY_OLD_MAIL = URL_API_BASE + "/user/update/email/checkoldemailcode";
        // Api - 5
        URL_API_FEEDBACK_SAVE = URL_API_BASE + "/feedback/saveFeedback";
        URL_APP_UPLOAD_FILE = sBaseServer + "/OSS/staff/appUploadFile.do";
        URL_APP_UPLOADS = URL_API_BASE + "/app/uploads";
        URL_UEI_BRAND = sBaseServer + "/iot/v2/uei/%s/brand";
        URL_UEI_CODE_LIST = sBaseServer + "/iot/v2/uei/%s/code";
        // Api - 6
        URL_UEI_CODE_PICKS = sBaseServer + "/iot/v2/uei/%s/picks";
        URL_UEI_CODE_MODEL = sBaseServer + "/iot/v2/uei/%s/model";
        URL_RECORD_DELETE_MESSAGE = sBaseServer + "/iot/v2/records/%s/devices/%s";
        URL_SKIN_LIST = URL_API_BASE + "/theme/theme/app/getAllThemes";
        URL_BANNERS = URL_API_BASE + "/slideshow/getAllSlides";
//        URL_ADVS = URL_API_BASE + "/luban/adv/info";
        URL_PLATFORM_LIST = URL_API_BASE + "/user/icp";//授权平台列表数据
        // Api - 7
        URL_USER_PUSH_AUDIENCE = sBaseServer + "/iot/v2/users/%s/user-push-audience";
        URL_MEDIA_SHARE = sBaseServer + "/iot/v2/users/%s/notice-share";
        URL_DEVICE_ACTIVE = sBaseServer + "/iot/v2/users/%s/device/active"; // 设备激活
        URL_DREAM_FLOWER_PROVINCE = URL_API_BASE + "/area/province";//梦想之花网关省份
        URL_DREAM_FLOWER_CITY = URL_API_BASE + "/area/city";//梦想之花网关城市
        URL_GET_GATEWAY_VERSION = sBaseServer + "/iot/v2/users/%s/gateway/version"; // 获取网关版本号


        // 账号相关(新) - 1
        URL_SSO_BASE = BASE_URL + "/sso";
        URL_SSO_REGISTER_DEVICE = URL_SSO_BASE + "/terminal/reg";
        URL_SSO_GET_REGISTER_VERIFICATION_CODE = URL_SSO_BASE + "/reg/phonecode";
        URL_SSO_REGISTER_PHONE = URL_SSO_BASE + "/reg/byphone";
        URL_SSO_LOGIN = URL_SSO_BASE + "/login/byphone";
        // 账号相关(新) - 2
        URL_SSO_LOGIN_THIRD = URL_SSO_BASE + "/login/bythird";
        URL_SSO_LOGOU = URL_SSO_BASE + "/login/logout";
        URL_SSO_TOKEN_TO_MQTT = URL_SSO_BASE + "/token/mqtt";
        URL_SSO_LOGIN_EMAIL = URL_SSO_BASE + "/login/byemail";
        URL_SSO_THIRD_BIND = URL_SSO_BASE + "/login/third/bind";
        // 账号相关(新) - 3
        URL_SSO_REGISTER_THIRD = URL_SSO_BASE + "/reg/bythird";
        URL_SSO_REPLACE_THIRD = URL_SSO_BASE + "/login/third/update";
        URL_API_CHANGE_PWD_EMAIL = URL_SSO_BASE + "/user/updatepass/sendemailcode";
        URL_API_CHANGE_PWD_EMAIL_VERIFY = URL_SSO_BASE + "/user/updatepass/byemailcode";
        URL_SSO_GET_MAIL_REGISTER_VERIFICATION_CODE = URL_SSO_BASE + "/reg/byemailcode";
        // 账号相关(新) - 4
        URL_SSO_REGISTER_MAIL = URL_SSO_BASE + "/reg/byemailvalidate";
        URL_SSO_CHECK_VERIFICATION_CODE_REGISTER = URL_SSO_BASE + "/reg/iot/v3/registCheckCode";
        URL_SSO_CHECK_VERIFICATION_CODE_FORGOT = URL_SSO_BASE + "/user/iot/v3/updatePassCheckCode";
        URL_SSO_REGISTER_AND_LOGIN = URL_SSO_BASE + "/reg/iot/v3/registByEmailAndPhone";
        URL_SSO_UPDATE_AND_LOGIN_BY_ACCOUNT = URL_SSO_BASE + "/user/iot/v3/updatePassAndLogin";
        // 账号相关(新) - 5
        URL_SSO_UPDATE_AND_LOGIN_BY_PWD = URL_SSO_BASE + "/user/iot/v3/updatePassByOldPass";
        URL_GET_CHANGE_PWD_VERIFICATION_CODE = URL_SSO_BASE + "/user/updatepass/phonecode";
        URL_API_THIRD_GET_PHONECODE = URL_SSO_BASE + "/user/bindthird/phonecode";
        //梦想之花
        URL_DREAM_FLOWER_ENT_BASE = sBaseServer + "/ent";
        URL_DREAM_FLOWER_PLAY = URL_DREAM_FLOWER_ENT_BASE + "/music/playMusic";
        URL_DREAM_FLOWER_CHANGE_PLAY_MODE = URL_DREAM_FLOWER_ENT_BASE + "/music/set/play/status";
        URL_DREAM_FLOWER_NEXT_OR_LAST = URL_DREAM_FLOWER_ENT_BASE + "/music/nextOrLast";
        URL_DREAM_FLOWER_GET_MODE = URL_DREAM_FLOWER_ENT_BASE + "/music/get/playMode";

        //安全狗
        URL_SAFE_DOG_PROTECTED_DEVICE = sBaseServer + "/safe/v2/users/%s/safedogs/%s/protect-devices";
        URL_SAFE_DOG_UNPROTECTED_DEVICE = sBaseServer + "/safe/v2/users/%s/safedogs/%s/unprotect-devices";
        URL_SAFE_DOG_GET_SCHEDULES = sBaseServer + "/safe/v2/users/%s/safedogs/%s/schedules";
        URL_SAFE_DOG_GET_INCIDENTS = sBaseServer + "/safe/v2/users/%s/safedogs/%s/incidents";
        URL_SAFE_DOG_GET_HOME_DATA = sBaseServer + "/safe/v2/users/%s/safedogs/%s/data";
        URL_SAFE_DOG_SCAN = sBaseServer + "/safe/v2/users/%s/safedogs/%s/scan";
        //视频直播流
        URL_LIVE_STREAM = sBaseServer + "/iot/v2/live/%s/%s/stream/status";
        URL_LIVE_STREAM_ADDR = sBaseServer + "/iot/v2/live/%s/%s/stream/address/pull";
        //体重秤
        URL_SAVE_WEIGHT = sBaseServer + "/iot/v2/users/%s/weight/save";
        URL_GET_WEIGHT_RECORDS = sBaseServer + "/iot/v2/users/%s/weight/%s";
        //小物摄像机预置位
        URL_SAVE_CYLINCAM_POSITION = URL_API_BASE + "/preset/position/save";
        URL_GET_CYLINCAM_POSITION = URL_API_BASE + "/preset/position/list";
        URL_DELETE_CYLINCAM_POSITION = URL_API_BASE + "/preset/position/delete";
        //通用上传图片
        URL_UPDATE_IMAGE = URL_API_BASE + "/user/image/upload";
        //向往背景音乐
        URL_WISH_ACTIVATE = sBaseServer + "/iot/v2/users/%s/devices/activeCode/status";
        //海信子设备
        URL_HISENSE_CHILD_DEVICE = sBaseServer + "/iot/v2/users/%s/target-devices/%s";
        //wifi红外转发器
        URL_WIFI_IR_CODE_LIBRARY = sBaseServer + "/iot/v2/uei/%s/codelib";
        URL_WIFI_IR_BLOCK = sBaseServer + "/iot/v2/uei/%s/ir/block";
        URL_WIFI_IR_KEY = sBaseServer + "/iot/v2/uei/%s/ir/key";
        URL_WIFI_IR_KEY_NAME = sBaseServer + "/iot/v2/uei/%s/ir/key/name";
        URL_WIFI_IR_LEARN = sBaseServer + "/iot/v2/uei/%s/ir/learning-status";
        URL_WIFI_IR_CONTROL = sBaseServer + "/iot/v2/uei/%s/ir/control";
        URL_WIFI_IR_MATCH = sBaseServer + "/iot/v2/uei/%s/ir/match";
        URL_WIFI_IR_KEY_CODE = sBaseServer + "/iot/v2/uei/%s/ir/key/code";
        URL_WIFI_IR_UPGRADE = sBaseServer + "/iot/v2/uei/%s/ir/upgrade";
        URL_WIFI_IR_PART_CODELIB = sBaseServer + "/iot/v2/uei/%s/ir/part-codelib";
        //体验网关
        URL_EXPERIENCE_GW = BASE_URL_USER + "/experienceGWState";
        URL_WIFI_DEVICE_LIST = BASE_URL_USER + "/%s/devices/paging";
        //校验网关是否支持设备
        URL_GATEWAY_SUPPORT_DEVICE = BASE_URL_USER + "/%s/device/gateway/support";
        //v5升级v6
        URL_V5_GATEWAY_PWD_CHECK = sBaseServer + "/iot/v2/devices/%s/v5/gw/password/check";
        URL_V5_GATEWAY_UPGRADE = sBaseServer + "/iot/v2/devices/%s/v5/gw/upgrade";
        //乐橙摄像机
        URL_SET_REVERSE_STATUS = sBaseServer + "/iot/v2/devices/%s/camera/frame-reverse-status";
        URL_SET_DETECTION_SWITCH = sBaseServer + "/iot/v2/devices/%s/alarm/status";
        URL_DEVICE_UPGRADE = sBaseServer + "/iot/v2/devices/%s/version/upgrade";
        URL_SD_FORMAT = sBaseServer + "/iot/v2/devices/%s/camera/sd/recover";
        URL_CAMERA_PTZ = sBaseServer + "/iot/v2/devices/%s/camera/ptz";
        URL_SET_DETECTION_PAN = sBaseServer + "/iot/v2/devices/%s/alarm/info";
        URL_LOCAL_RECORD = sBaseServer + "/iot/v2/records/%s/camera/local";
        //罗格朗门禁
        URL_ENTRANCE_GUARD_UNLOCK = sBaseServer + "/iot/v2/devices/%s/legrand/door/open";
        URL_REMOTE_HANG_UP = sBaseServer + "/iot/v2/devices/%s/legrand/remotehangup";
        URL_ENTRANCE_GUARD_SIP_REG = BASE_URL_USER + "/%s/legrand/sip/reg";
        URL_ENTRANCE_GUARD_PROVINCE = BASE_URL_USER + "/%s/legrand/community/info/province";
        URL_ENTRANCE_GUARD_CITY = BASE_URL_USER + "/%s/legrand/community/info/province/city";
        URL_ENTRANCE_GUARD_CITY_DISTRICT = BASE_URL_USER + "/%s/legrand/community/info/province/city/district";
        URL_ENTRANCE_GUARD_COMMUNITY = BASE_URL_USER + "/%s/legrand/community/info/province/city/district/community";
        URL_ENTRANCE_GUARD_DISTRICT = BASE_URL_USER + "/%s/legrand/community/address/info/district";
        URL_ENTRANCE_GUARD_BUILDING = BASE_URL_USER + "/%s/legrand/community/address/info/district/building";
        URL_ENTRANCE_GUARD_UNIT = BASE_URL_USER + "/%s/legrand/community/address/info/district/building/unit";
        URL_ENTRANCE_GUARD_FLOOR = BASE_URL_USER + "/%s/legrand/community/address/info/district/building/unit/floor";
        URL_ENTRANCE_GUARD_HOUSE = BASE_URL_USER + "/%s/legrand/community/address/info/district/building/unit/floor/house";
        URL_ENTRANCE_GUARD_DEVICE_INFO = BASE_URL_USER + "/%s/legrand/community/address/info/district/building/unit/floor/house/device";
        URL_ENTRANCE_GUARD_ADDRESS_INFO = BASE_URL_USER + "/%s/legrand/community/address/info";
        URL_ENTRANCE_GUARD_COMMUNITY_INFO = BASE_URL_USER + "/%s/legrand/community/info";
        URL_ENTRANCE_GUARD_HOST_INFO = sBaseServer + "/iot/v2/devices/%s/legrand/host/info";
        URL_ENTRANCE_GUARD_VERIFY_CODE = sBaseServer + "/iot/v2/devices/%s/legrand/verificatioCode";
        URL_ENTRANCE_GUARD_PROPERTY_INFO = sBaseServer + "/iot/v2/devices/%s/legrand/property/info";
    }
}
