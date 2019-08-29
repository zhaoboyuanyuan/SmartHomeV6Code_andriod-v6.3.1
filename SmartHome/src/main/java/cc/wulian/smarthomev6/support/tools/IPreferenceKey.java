package cc.wulian.smarthomev6.support.tools;

public interface IPreferenceKey {
    // custom key preference.xml
    public static final String P_KEY_PREFERENCE = "preference";
    public static final String P_KEY_IS_LOGIN = "is_login";
    public static final String P_KEY_BASE_SERVER = "p_key_base_server";
    public static final String P_KEY_BASE_URL = "p_key_base_url";
    public static final String P_KEY_IS_BASE_URL_CHANGED = "p_key_is_base_url_changed";
    public static final String P_KEY_CURRENT_GATEWAY_ID = "current_gateway_id";
    public static final String P_KEY_CURRENT_GATEWAY_LIST = "current_gateway_list";
    public static final String P_KEY_LAST_CONNECTED_GATEWAY_ID = "last_connected_gateway_id";
    public static final String P_KEY_BIND_GATEWAY_COUNT = "bind_gateway_count";
    public static final String P_KEY_CURRENT_GATEWAY_STATE = "current_gateway_state";
    public static final String P_KEY_RECORD_COUNT = "record_count";
    public static final String P_KEY_CURRENT_GATEWAY_INFO = "current_gateway_password_info";
    public static final String P_KEY_VERIFY_GATEWAY_PASSWORD = "verify_gateway_password";
    public static final String P_KEY_GATEWAY_PASSWORD_STORAGE = "gateway_password_storage";
    public static final String P_KEY_APPWIDGET_STORAGE = "appwidget_storage";
    public static final String P_KEY_CURRENT_ACCOUNT_INFO = "current_account_info";
    public static final String P_KEY_CURRENT_ACCOUNT_ID = "current_account_id";
    public static final String P_KEY_CURRENT_ACCOUNT_PWD = "current_account_pwd";
    public static final String P_KEY_LANGUAGE = "key_language";
    public static final String P_KEY_DEVICE_ID = "key_device_id";
    public static final String P_KEY_NEW_FLAGS_DATA = "new_flags_data";
    public static final String P_KEY_CURRENT_SKIN = "current_skin";
    public static final String P_KEY_COMMON_SKIN = "common_skin";
    public static final String P_KEY_AUTO_CHANGE_SKIN = "auto_change_skin";
    public static final String P_KEY_ADV_INDEX = "adv_index";
    //账号
    public static final String P_KEY_GATEWAY_RELATION_FLAG = "gateway_relation_flag";
    public static final String P_KEY_APP_TOKEN = "app_token";
    public static final String P_KEY_USER_ID = "user_id";
    public static final String P_KEY_TERMINAL_ID = "terminal_Id";
    public static final String P_KEY_CLOUD_SECRETKEY = "cloud_secret_key";
    public static final String P_KEY_USER_PASSWORD_MD5 = "user_password_md5";
    public static final String P_KEY_CLOUD_AES_KEY = "cloud_aes_key";
    public static final String P_KEY_SIP_SUID = "sip_suid";
    //爱看登录信息
    public static final String P_KEY_ICAM_LOGIN_INFO = "icam_login_info";
    public static final String P_KEY_ICAM_SIP_INFO = "icam_sip_info";

    public static final String P_KEY_NORMAL_QUIT = "P_KEY_NORMAL_START";


    public static final String P_KEY_PUSH = "P_KEY_PUSH";
    public static final String P_KEY_SHAKE = "P_KEY_SHAKE";
    public static final String P_KEY_VOICE = "P_KEY_VOICE";
    public static final String P_KEY_VOICE_SPEED = "P_KEY_VOICE_SPEED";
    public static final String P_KEY_VOICE_CLASSIFICATION = "P_KEY_VOICE_CLASSIFICATION";
    public static final String P_KEY_BAIDU_TOKEN = "P_KEY_BAIDU_TOKEN";
    public static final String P_KEY_BAIDU_TOKEN_TIME = "P_KEY_BAIDU_TOKEN_TIME";

    public static final String P_KEY_GUIDE = "P_GUIDE";
    public static final String P_KEY_ISFRIST = "P_KEY_ISFRIST";
    public static final String P_KEY_ISFRIST_VOICE_ASSISTANT_TIP = "P_KEY_ISFRIST";
    public static final String P_KEY_CHECK_ACCOUNT_ENTER_TYPE = "P_KEY_CHECK_ACCOUNT_ENTER_TYPE"; //判断通过网关直接登录还是账号登录
    public static final String P_KEY_MAIN_THEME = "p_key_main_theme";//是否是蓝色主题的颜色
    public static final String P_KEY_CLICK_SKIN = "p_key_click_skin";//是否点击主题条目
    public static final String P_KEY_THIRD_PARTY_TYPE = "P_KEY_THIRD_PARTY_TYPE";//第三方登录的类型
    public static final String P_KEY_THIRD_PARTY_UID = "P_KEY_THIRD_PARTY_UID";//第三方登录的uid
    public static final String P_KEY_THIRD_PARTY_LOGIN = "P_KEY_THIRD_PARTY_LOGIN";//是否是第三方登录
    public static final String P_KEY_AUTO_LOGIN = "P_KEY_AUTO_LOGIN";//是否自动登录
    public static final String P_KEY_PLAY_VIDEO_4G = "P_KEY_PLAY_VIDEO_4G";//是否使用移动网络播放视频
    public static final String P_KEY_APPISSHOWGWDEBUG="P_KEY_APPISSHOWGWDEBUG";//App端是否已调出网关调试功能
    public static final String P_KEY_CYLINCAM_ENVIRONMENT="P_KEY_CYLINCAM_ENVIRONMENT";//小物摄像机环境监测显示功能
    public static final String P_KEY_CYLINCAM_PROTECT_AREA="P_KEY_CYLINCAM_PROTECT_AREA";//小物摄像机安全防护区域
    public static final String P_KEY_CYLINCAM_PRE_POSITION="P_KEY_CYLINCAM_PRE_POSITION_";//小物摄像机预置位
    public static final String P_KEY_INSTRUCTION_FOR_UEI_ADD_DEVICE = "P_KEY_INSTRUCTION_FOR_UEI_ADD_DEVICE";//添加遥控器
    public static final String P_KEY_CATEYE_BATTERY = "P_KEY_CATEYE_BATTERY";//智能猫眼电量
    public static final String P_KEY_ISFRIST_CAMERA_FULL_SCREEN = "P_KEY_ISFRIS_CAMERA_FULL_SCREEN";//摄像机全屏音量调节蒙层
    public static final String P_KEY_ISFRIST_LOCAL_POSITION = "P_KEY_ISFRIST_LOCAL_POSITION";//小物摄像机第一次查询本地预置位
    public static final String P_KEY_TOUCHID_UNLOCK = "P_KEY_TOUCHID_UNLOCK";//门锁指纹解锁
    public static final String P_KEY_WIFI_INFO = "P_KEY_WIFI_INFO";//wifi名称和密码
    public static final String P_KEY_SOCKET_ICON = "P_KEY_SOCKET_ICON";//插座图标
    public static final String P_KEY_CURTAIN_ICON = "P_KEY_CURTAIN_ICON";//窗帘图标
    public static final String P_KEY_SCENE_SHOW_LAYOUT= "P_KEY_SCENE_SHOW_LAYOUT";//场景显示布局格式
    public static final String P_KEY_SHOW_GUIDE_PAGE= "P_KEY_SHOW_GUIDE_PAGE";//h5引导页显示
    public static final String P_KEY_NEED_DEFAULT_SKIN= "P_KEY_NEED_DEFAULT_SKIN";//需要换回默认皮肤
    public static final String P_KEY_WULIAN_NOTICE= "P_KEY_WULIAN_NOTICE";//物联通告
}