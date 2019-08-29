package cc.wulian.smarthomev6.support.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import cc.wulian.smarthomev6.BuildConfig;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamGetSipInfoBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamLoginBean;
import cc.wulian.smarthomev6.support.event.GetGatewayListEvent;
import cc.wulian.smarthomev6.support.event.LoginEvent;
import cc.wulian.smarthomev6.support.utils.StringUtil;

/**
 * 系统参数
 */
public class Preference {
    public static final String ENTER_TYPE_ACCOUNT = "account";
    public static final String ENTER_TYPE_GW = "gateway";
    public static final String ISADMIN_TRUE = "1";
    public static final String ISADMIN_FALSE = "0";
    /**
     * 默认皮肤ID，根据版本需要进行调整
     */
    public static final String DEFAULT_SKIN_ID = "001";

    private final Context mContext;
    private final SharedPreferences mPreferences;
    private final Editor mEditor;

    private static Preference mInstance;

    public static Preference getPreferences() {
        if (mInstance == null)
            mInstance = new Preference();
        return mInstance;
    }

    private Preference() {
        mContext = MainApplication.getApplication();
        mPreferences = mContext.getSharedPreferences(
                IPreferenceKey.P_KEY_PREFERENCE, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    public void saveIsNormalQuit(boolean normalQuit) {
        mEditor.putBoolean(IPreferenceKey.P_KEY_NORMAL_QUIT, normalQuit)
                .commit();
    }

    public void clearAllData() {
        mEditor.clear().commit();
    }


    public void setTheme(boolean is) {
        mEditor.putBoolean(IPreferenceKey.P_KEY_MAIN_THEME, is)
                .commit();
    }

    /**
     * 设置 server地址
     */
    public void saveBaseUrl(String server) {
        mEditor.putString(IPreferenceKey.P_KEY_BASE_URL, server)
                .commit();
    }

    public boolean isBaseUrlChanged() {
        return mPreferences.getBoolean(IPreferenceKey.P_KEY_IS_BASE_URL_CHANGED, false);
    }

    /**
     * 设置 是否修改 domain
     */
    public void setBaseUrlChanged(boolean c) {
        mEditor.putBoolean(IPreferenceKey.P_KEY_IS_BASE_URL_CHANGED, c);
    }

    public String getBaseUrl() {
        return mPreferences.getString(IPreferenceKey.P_KEY_BASE_URL, BuildConfig.BASE_DOMAIN);
    }

    /**
     * 设置 server地址
     */
    public void saveBaseServer(String server) {
        mEditor.putString(IPreferenceKey.P_KEY_BASE_SERVER, server)
                .commit();
    }

    public String getBaseServer() {
        return mPreferences.getString(IPreferenceKey.P_KEY_BASE_SERVER, BuildConfig.BASE_DOMAIN);
    }

    /**
     * 得到主题
     */
    public boolean getTheme() {
        return mPreferences.getBoolean(IPreferenceKey.P_KEY_MAIN_THEME, false);
    }

    public void setIsClickSkin(boolean is) {
        mEditor.putBoolean(IPreferenceKey.P_KEY_CLICK_SKIN, is)
                .commit();
    }

    /**
     * 得到主题
     */
    public boolean getIsClickSkin() {
        return mPreferences.getBoolean(IPreferenceKey.P_KEY_CLICK_SKIN, false);
    }

    //账号登陆相关信息///////////////////////////////////

    public void saveAppToken(String value) {
        mEditor.putString(IPreferenceKey.P_KEY_APP_TOKEN, value)
                .commit();
    }

    public String getAppToken() {
        return mPreferences.getString(IPreferenceKey.P_KEY_APP_TOKEN, "");
    }

    /**
     * userID
     */
    public void saveUserID(String value) {
        mEditor.putString(IPreferenceKey.P_KEY_USER_ID, value)
                .commit();
    }

    public String getUserID() {
        return mPreferences.getString(IPreferenceKey.P_KEY_USER_ID, "");
    }

    /**
     * Cloud echostr
     */
    public void saveCloudSecretKey(String value) {
        mEditor.putString(IPreferenceKey.P_KEY_CLOUD_SECRETKEY, value)
                .commit();
    }

    public String getCloudSecretKey() {
        return mPreferences.getString(IPreferenceKey.P_KEY_CLOUD_SECRETKEY, "");
    }

    /**
     * user Password with MD5
     */
    public void saveUserPasswordMD5(String value) {
        mEditor.putString(IPreferenceKey.P_KEY_USER_PASSWORD_MD5, value)
                .commit();
    }

    public String getUserPasswordMD5() {
        return mPreferences.getString(IPreferenceKey.P_KEY_USER_PASSWORD_MD5, "");
    }

    /**
     * cloud AES Key
     */
    public void saveCloudAESKey(byte[] value) {
        if (value != null) {
            String text = null;
            char[] chars = new char[value.length];
            for (int i = 0; i < value.length; i++) {
                chars[i] = (char) (value[i] + 0x80);
            }
            text = new String(chars);
            mEditor.putString(IPreferenceKey.P_KEY_CLOUD_AES_KEY, text)
                    .commit();
        }
    }

    public byte[] getCloudAESKey() {
        char[] chars = mPreferences.getString(IPreferenceKey.P_KEY_CLOUD_AES_KEY, "").toCharArray();
        byte[] value = new byte[chars.length];
        for (int i = 0; i < value.length; i++) {
            value[i] = (byte) ((chars[i] - 0x80) & 0xff);
        }
        return value;
    }

    //保存语言环境，以便对比检测语言环境变化
    public void saveLanguage(String value) {
        mEditor.putString(IPreferenceKey.P_KEY_LANGUAGE, value)
                .commit();
    }

    public String getLanguage() {
        return mPreferences.getString(IPreferenceKey.P_KEY_LANGUAGE, "");
    }

    //保存唯一标识码，解决某些设备IMEI码获取不到的问题
    public void saveDeviceID(String value) {
        mEditor.putString(IPreferenceKey.P_KEY_DEVICE_ID, value)
                .commit();
    }

    public String getDeviceID() {
        return mPreferences.getString(IPreferenceKey.P_KEY_DEVICE_ID, "");
    }

    /**
     * 判断网关登录还是账号登录
     *
     * @param str
     */
    public void saveUserEnterType(String str) {
        mEditor.putString(IPreferenceKey.P_KEY_CHECK_ACCOUNT_ENTER_TYPE, str)
                .commit();
    }

    public String getUserEnterType() {
        return mPreferences.getString(IPreferenceKey.P_KEY_CHECK_ACCOUNT_ENTER_TYPE, "");
    }

    /**
     * 当前连接的网关号
     */
    public void saveCurrentGatewayID(String id) {
        if (!TextUtils.isEmpty(id)) {
            saveLastConnectedGatewayID(id);
        }
        mEditor.putString(IPreferenceKey.P_KEY_CURRENT_GATEWAY_ID, id)
                .commit();
    }

    public String getCurrentGatewayID() {
        return mPreferences.getString(IPreferenceKey.P_KEY_CURRENT_GATEWAY_ID, "");
    }

    /**
     * 当前账号下的网关列表
     */
    public void saveCurrentGatewayList(@NonNull List<String> list) {
        Log.i("huxctest", "saveCurrentGatewayList: " + list.size());
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new GetGatewayListEvent());
            }
        }, 500);
        mEditor.putString(IPreferenceKey.P_KEY_CURRENT_GATEWAY_LIST, JSON.toJSONString(list))
                .commit();
    }

    public List<String> getCurrentGatewayList() {
        String json = mPreferences.getString(IPreferenceKey.P_KEY_CURRENT_GATEWAY_LIST, "[]");
        List<String> list = null;
        try {
            list = JSON.parseArray(json, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public void removeGatewayFromList(String gatewayID) {
        List<String> list = getCurrentGatewayList();
        list.remove(gatewayID);
        saveCurrentGatewayList(list);
    }

    public void saveLastConnectedGatewayID(String id) {
        mEditor.putString(IPreferenceKey.P_KEY_LAST_CONNECTED_GATEWAY_ID, id)
                .commit();
    }

    public String getLastConnectedGatewayID() {
        return mPreferences.getString(IPreferenceKey.P_KEY_LAST_CONNECTED_GATEWAY_ID, "");
    }

    /**
     * 当前绑定的网关的数量
     */
    public void saveBindGatewayCount(int count) {
        mEditor.putInt(IPreferenceKey.P_KEY_BIND_GATEWAY_COUNT, count)
                .commit();
    }

    public int getBindGatewayCount() {
        return mPreferences.getInt(IPreferenceKey.P_KEY_BIND_GATEWAY_COUNT, 0);
    }

    /**
     * 保存打点次数
     */
    public void saveRecordCount(int count) {
        mEditor.putInt(IPreferenceKey.P_KEY_RECORD_COUNT, count)
                .commit();
    }

    /**
     * 获取打点次数, 默认5
     */
    public int getRecordCount() {
        return mPreferences.getInt(IPreferenceKey.P_KEY_RECORD_COUNT, 20);
    }

    /**
     * 当前连接的网关上下线状态
     */
    public void saveCurrentGatewayState(String state) {
        mEditor.putString(IPreferenceKey.P_KEY_CURRENT_GATEWAY_STATE, state)
                .commit();
    }

    public String getCurrentGatewayState() {
        return mPreferences.getString(IPreferenceKey.P_KEY_CURRENT_GATEWAY_STATE, "0");
    }

    /**
     * 网关密码仓库
     */
    private void saveGatewayPasswordStorage(String value) {//TODO 要加密处理
        mEditor.putString(IPreferenceKey.P_KEY_GATEWAY_PASSWORD_STORAGE, value)
                .commit();
    }

    private String getGatewayPasswordStorage() {
        return mPreferences.getString(IPreferenceKey.P_KEY_GATEWAY_PASSWORD_STORAGE, "{}");
    }

    public void saveGatewayPassword(String gwID, String password) {
        JSONObject passwordStorage = JSON.parseObject(getGatewayPasswordStorage());
        passwordStorage.put(gwID, password);
        saveGatewayPasswordStorage(passwordStorage.toJSONString());
    }

    public String getGatewayPassword(String gwID) {
        JSONObject passwordStorage = JSON.parseObject(getGatewayPasswordStorage());
        return passwordStorage.getString(gwID);
    }

    /**
     * 网关密码,验证密码是否是自己修改的时候用
     */
    public void saveVerifyGatewayPassword(String value) {
        mEditor.putString(IPreferenceKey.P_KEY_VERIFY_GATEWAY_PASSWORD, value)
                .commit();
    }

    public String getVerifyGatewayPassword() {
        return mPreferences.getString(IPreferenceKey.P_KEY_VERIFY_GATEWAY_PASSWORD, "");
    }

    public void showInstructionForUeiAddDevice(String deviceId) {
        mEditor.putBoolean(IPreferenceKey.P_KEY_INSTRUCTION_FOR_UEI_ADD_DEVICE + deviceId, true)
                .commit();
    }

    public boolean isShownInstructionForUeiAddDevice(String deviceId) {
        return mPreferences.getBoolean(IPreferenceKey.P_KEY_INSTRUCTION_FOR_UEI_ADD_DEVICE + deviceId, false);
    }

    /**
     * 当前登录的账号
     */
    public void saveCurrentAccountID(String value) {//TODO 要加密处理
        mEditor.putString(IPreferenceKey.P_KEY_CURRENT_ACCOUNT_ID, value)
                .commit();
    }

    public String getCurrentAccountID() {
        return mPreferences.getString(IPreferenceKey.P_KEY_CURRENT_ACCOUNT_ID, "");
    }

    /**
     * 当前登录的账号信息
     */
    public void saveCurrentAccountInfo(String value) {//TODO 要加密处理
        mEditor.putString(IPreferenceKey.P_KEY_CURRENT_ACCOUNT_INFO, value)
                .commit();
    }

    public String getCurrentAccountInfo() {
        return mPreferences.getString(IPreferenceKey.P_KEY_CURRENT_ACCOUNT_INFO, "{}");
    }

    /**
     * 当前登录的网关信息
     */
    public void saveCurrentGatewayInfo(String value) {//TODO 要加密处理
        mEditor.putString(IPreferenceKey.P_KEY_CURRENT_GATEWAY_INFO, value)
                .commit();
    }

    public String getCurrentGatewayInfo() {
        return mPreferences.getString(IPreferenceKey.P_KEY_CURRENT_GATEWAY_INFO, "");
    }


    /**
     * 报警语音
     */
    public void saveKeyAlarmVoice(boolean value) {
        mEditor.putBoolean(IPreferenceKey.P_KEY_VOICE, value)
                .commit();
    }

    public boolean getKeyAlarmVoice() {
        return mPreferences.getBoolean(IPreferenceKey.P_KEY_VOICE, true);
    }

    /**
     * 报警语音语种
     */
    public void saveKeyAlarmVoiceClassificationLanguage(int value) {
        mEditor.putInt(IPreferenceKey.P_KEY_VOICE_CLASSIFICATION, value)
                .commit();
    }

    public int getKeyAlarmVoiceClassificationLanguage() {
        return mPreferences.getInt(IPreferenceKey.P_KEY_VOICE_CLASSIFICATION, 0);
    }

    /**
     * 报警语音速度
     */
    public void saveKeyAlarmVoiceSpeed(int value) {
        mEditor.putInt(IPreferenceKey.P_KEY_VOICE_SPEED, value)
                .commit();
    }

    public int getKeyAlarmVoiceSpeed() {
        return mPreferences.getInt(IPreferenceKey.P_KEY_VOICE_SPEED, 2);
    }

    /**
     * 百度语音token
     */
    public void saveBaiDuToken(String value) {
        mEditor.putString(IPreferenceKey.P_KEY_BAIDU_TOKEN, value)
                .commit();
    }

    public String getBaiDuToken() {
        return mPreferences.getString(IPreferenceKey.P_KEY_BAIDU_TOKEN, "");
    }

    /**
     * 百度语音时间
     */
    public void saveBaiDuTokenTime(Long value) {
        mEditor.putLong(IPreferenceKey.P_KEY_BAIDU_TOKEN_TIME, value)
                .commit();
    }

    public Long getBaiDuTokenTime() {
        return mPreferences.getLong(IPreferenceKey.P_KEY_BAIDU_TOKEN_TIME, 0);
    }

    /**
     * 报警振动
     */
    public void saveAlarmShake(boolean value) {
        mEditor.putBoolean(IPreferenceKey.P_KEY_SHAKE, value)
                .commit();
    }

    public Boolean getAlarmShake() {
        return mPreferences.getBoolean(IPreferenceKey.P_KEY_SHAKE, true);
    }

    /**
     * 报警推送
     */
    public void saveAlarmPush(boolean value) {
        mEditor.putBoolean(IPreferenceKey.P_KEY_PUSH, value)
                .commit();
    }

    public Boolean getAlarmPush() {
        return mPreferences.getBoolean(IPreferenceKey.P_KEY_PUSH, true);
    }

    /**
     * 设置登录状态
     */
    public void saveIsLogin(boolean value) {
        mEditor.putBoolean(IPreferenceKey.P_KEY_IS_LOGIN, value)
                .commit();
        EventBus.getDefault().post(new LoginEvent());
    }

    public Boolean isLogin() {
        return mPreferences.getBoolean(IPreferenceKey.P_KEY_IS_LOGIN, false);
    }

    /**
     * 网关授权状态
     */
    public void saveGatewayRelationFlag(String relationFlag) {
        mEditor.putString(IPreferenceKey.P_KEY_GATEWAY_RELATION_FLAG, relationFlag)
                .commit();
    }

    public String getGatewayRelationFlag() {
        return mPreferences.getString(IPreferenceKey.P_KEY_GATEWAY_RELATION_FLAG, "");
    }

    public Boolean isAuthGateway() {
        String relationFlag = getGatewayRelationFlag();
        return "2".equals(relationFlag) || "3".equals(relationFlag);
    }

    /**
     * 当前皮肤
     */
    public void saveCurrentSkin(String skin) {
        if (TextUtils.isEmpty(skin)) {
            mEditor.remove(IPreferenceKey.P_KEY_CURRENT_SKIN).commit();
        } else {
            mEditor.putString(IPreferenceKey.P_KEY_CURRENT_SKIN, skin).commit();
        }
    }

    public String getCurrentSkin() {
        return mPreferences.getString(IPreferenceKey.P_KEY_CURRENT_SKIN, DEFAULT_SKIN_ID);
    }


    /**
     * 节假日自动换肤之前的默认皮肤
     *
     * @param skin
     */
    public void saveCommonSkin(String skin) {
        if (TextUtils.isEmpty(skin)) {
            mEditor.remove(IPreferenceKey.P_KEY_COMMON_SKIN).commit();
        } else {
            mEditor.putString(IPreferenceKey.P_KEY_COMMON_SKIN, skin).commit();
        }
    }

    public String getCommonSkin() {
        return mPreferences.getString(IPreferenceKey.P_KEY_COMMON_SKIN, DEFAULT_SKIN_ID);
    }


    /**
     * 是否需要自动换肤（节假日期间换回普通皮肤后不再自动换肤）
     *
     * @param flag
     */
    public void setAutoChangeSkin(boolean flag) {
        mEditor.putBoolean(IPreferenceKey.P_KEY_AUTO_CHANGE_SKIN, flag).commit();
    }

    public boolean getAutoChangeSkin() {
        return mPreferences.getBoolean(IPreferenceKey.P_KEY_AUTO_CHANGE_SKIN, true);
    }

    /**
     * 是否开启引导页
     */
    public void setCurrentVersion(int version) {
        mEditor.putInt(IPreferenceKey.P_KEY_GUIDE, version)
                .commit();
    }

    public int getCurrentVersion() {
        return mPreferences.getInt(IPreferenceKey.P_KEY_GUIDE, 0);
    }

    /**
     * 是否第一次登录
     */
    public void setIsFristLogin() {
        mEditor.putBoolean(IPreferenceKey.P_KEY_ISFRIST, false)
                .commit();
    }

    public boolean getIsFristLogin() {
        return mPreferences.getBoolean(IPreferenceKey.P_KEY_ISFRIST, true);
    }

    /**
     * 是否第一次提示语音助手
     */
    public void setIsFristVoiceAssistantTip() {
        mEditor.putBoolean(IPreferenceKey.P_KEY_ISFRIST_VOICE_ASSISTANT_TIP, false)
                .commit();
    }

    public boolean getIsFristVoiceAssistantTip() {
        return mPreferences.getBoolean(IPreferenceKey.P_KEY_ISFRIST_VOICE_ASSISTANT_TIP, true);
    }

    /**
     * 存储爱看登录信息
     */
    public void saveICamInfo(ICamLoginBean bean, String userID) {
        mEditor.putString(IPreferenceKey.P_KEY_ICAM_LOGIN_INFO + "_" + userID, JSON.toJSONString(bean))
                .commit();
    }

    public ICamLoginBean getICamInfo(String userID) {
        String json = mPreferences.getString(IPreferenceKey.P_KEY_ICAM_LOGIN_INFO + "_" + userID, "");
        if (TextUtils.isEmpty(json)) {
            return null;
        } else {
            return JSON.parseObject(json, ICamLoginBean.class);
        }
    }

    /**
     * 存储sip信息
     */
    public void saveSipInfo(ICamGetSipInfoBean bean, String userID, String cameraID) {
        mEditor.putString(IPreferenceKey.P_KEY_ICAM_SIP_INFO + "_" + userID + "_" + cameraID, JSON.toJSONString(bean))
                .commit();
    }

    public ICamGetSipInfoBean getSipInfo(String userID, String cameraID) {
        String json = mPreferences.getString(IPreferenceKey.P_KEY_ICAM_SIP_INFO + "_" + userID + "_" + cameraID, "");
        if (TextUtils.isEmpty(json)) {
            return null;
        } else {
            return JSON.parseObject(json, ICamGetSipInfoBean.class);
        }
    }

    /**
     * 第三方登录的类型
     */
    public void saveThirdPartyType(int type) {
        mEditor.putInt(IPreferenceKey.P_KEY_THIRD_PARTY_TYPE, type)
                .commit();
    }

    public int getThirdPartyType() {
        return mPreferences.getInt(IPreferenceKey.P_KEY_THIRD_PARTY_TYPE, 1);
    }

    /**
     * 第三方登录的uid
     */
    public void saveThirdPartyUid(String uid) {
        mEditor.putString(IPreferenceKey.P_KEY_THIRD_PARTY_UID, uid)
                .commit();
    }

    public String getThirdPartyUid() {
        return mPreferences.getString(IPreferenceKey.P_KEY_THIRD_PARTY_UID, "");
    }

    /**
     * 判断是否是hi第三方登录
     */
    public void saveThirdPartyLogin(boolean isThirdPartyLogin) {
        mEditor.putBoolean(IPreferenceKey.P_KEY_THIRD_PARTY_LOGIN, isThirdPartyLogin)
                .commit();
    }

    public boolean isThirdPartyLogin() {
        return mPreferences.getBoolean(IPreferenceKey.P_KEY_THIRD_PARTY_LOGIN, false);
    }

    /**
     * 判断是否自动登录
     */
    public void saveAutoLogin(boolean isAutoLogin) {
        mEditor.putBoolean(IPreferenceKey.P_KEY_AUTO_LOGIN, isAutoLogin)
                .commit();
    }

    public boolean isAutoLogin() {
        return mPreferences.getBoolean(IPreferenceKey.P_KEY_AUTO_LOGIN, false);
    }

    //保存下载ID
    public void saveDownloadId(String key, long id) {
        mEditor.putLong(key, id).commit();
    }

    //获取下载ID
    public long getDownloadId(String key) {
        return mPreferences.getLong(key, 0);
    }

    /**
     * terminalId
     */
    public void saveTerminalId(String value) {
        mEditor.putString(IPreferenceKey.P_KEY_TERMINAL_ID, value)
                .commit();
    }

    public String getTerminalId() {
        return mPreferences.getString(IPreferenceKey.P_KEY_TERMINAL_ID, "");
    }

    /**
     * 保存NEW红点标志位数据
     */
    public void saveNewFlagsData(int data) {
        mEditor.putInt(IPreferenceKey.P_KEY_NEW_FLAGS_DATA, data)
                .commit();
    }

    /**
     * 获取NEW红点标志位数据
     */
    public int getNewFlagsData() {
        return mPreferences.getInt(IPreferenceKey.P_KEY_NEW_FLAGS_DATA, 0);
    }

    /**
     * 保存开屏广告指针（用于顺序播放开屏广告）
     */
    public void saveAdvIndex(int data) {
        mEditor.putInt(IPreferenceKey.P_KEY_ADV_INDEX, data)
                .commit();
    }

    /**
     * 获取开屏广告指针（用于顺序播放开屏广告）
     */
    public int getAdvIndex() {
        return mPreferences.getInt(IPreferenceKey.P_KEY_ADV_INDEX, 0);
    }

    /**
     * currentSipSuid
     */
    public void saveCurrentSipSuid(String value) {
        mEditor.putString(IPreferenceKey.P_KEY_SIP_SUID, value)
                .commit();
    }

    public String getCurrentSipSuid() {
        return mPreferences.getString(IPreferenceKey.P_KEY_SIP_SUID, "");
    }

    /**
     * AppWidget仓库
     */
    private void saveAppWidgetStorage(String value) {
        mEditor.putString(IPreferenceKey.P_KEY_APPWIDGET_STORAGE, value)
                .commit();
    }

    private String getAppWidgetStorage() {
        return mPreferences.getString(IPreferenceKey.P_KEY_APPWIDGET_STORAGE, "{}");
    }

    /**
     * 存储appwidgetid对应的类型
     */
    public void saveAppWidgets(int[] appWidgetIds, int appWidgetType) {
        JSONObject storage = JSON.parseObject(getAppWidgetStorage());
        for (int appWidgetId : appWidgetIds) {
            storage.put(String.valueOf(appWidgetId), appWidgetType);
        }
        saveAppWidgetStorage(storage.toJSONString());
    }

    /**
     * 获取对应类型所有的id
     *
     * @param appWidgetType widget类型
     * @return
     */
    public int[] getAppWidgets(int appWidgetType) {
        JSONObject storage = JSON.parseObject(getAppWidgetStorage());
        ArrayList<Integer> list = new ArrayList<>();
        for (String key : storage.keySet()) {
            if (storage.getInteger(key) == appWidgetType) {
                list.add(Integer.parseInt(key));
            }
        }
        int size = list.size();
        int[] appWidgetIds = new int[size];
        for (int i = 0; i < size; i++) {
            appWidgetIds[i] = list.get(i);
        }
        return appWidgetIds;
    }

    /**
     * 删除appwidgetid
     */
    public void deleteAppWidgets(int appWidgetId) {
        JSONObject storage = JSON.parseObject(getAppWidgetStorage());
        storage.remove(String.valueOf(appWidgetId));
        saveAppWidgetStorage(storage.toJSONString());
    }

    /**
     * App是否显示出网关调试功能
     *
     * @param
     */
    public void saveAppIsShowGwDebug(boolean isShow) {
        String strIsShow = "0";
        if (isShow) {
            strIsShow = "1";
        }
        mEditor.putString(IPreferenceKey.P_KEY_APPISSHOWGWDEBUG, strIsShow).commit();
    }

    public boolean getAppIsShowGwDebug() {
        boolean isShow = false;
        String strIsShow = mPreferences.getString(IPreferenceKey.P_KEY_APPISSHOWGWDEBUG, "");
        if (!StringUtil.isNullOrEmpty(strIsShow) && TextUtils.equals(strIsShow, "1")) {
            isShow = true;
        }
        return isShow;
    }


    /**
     * 小物摄像机环境监测功能
     *
     * @param isShow
     */
    public void saveCylincamEnvironment(boolean isShow, String deviceId) {
        mEditor.putBoolean(IPreferenceKey.P_KEY_CYLINCAM_ENVIRONMENT + deviceId, isShow).commit();
    }

    public boolean getCylincamEnvironmentSet(String deviceId) {
        boolean isShow = mPreferences.getBoolean(IPreferenceKey.P_KEY_CYLINCAM_ENVIRONMENT + deviceId, true);
        return isShow;
    }


    /**
     * 小物摄像机安全防护区域提示
     */


    public boolean cylincamIsFirstSetArea() {
        boolean isShow = mPreferences.getBoolean(IPreferenceKey.P_KEY_APPISSHOWGWDEBUG, true);
        mEditor.putBoolean(IPreferenceKey.P_KEY_APPISSHOWGWDEBUG, false).commit();
        return isShow;
    }

    /**
     * 小物摄像机预置位信息
     */
    public void saveCylincamPrePosition(String devId, int position, String name) {
        mEditor.putString(IPreferenceKey.P_KEY_CYLINCAM_PRE_POSITION + devId + position, name)
                .commit();
    }

    public String getCylincamPrePosition(String devId, int position) {
        return mPreferences.getString(IPreferenceKey.P_KEY_CYLINCAM_PRE_POSITION + devId + position, null);
    }

    /**
     * 局域网登录 当前登录的网关IP地址
     */
    public void saveCurrentGatewayHost(String value) {
        mEditor.putString("GatewayHost", value)
                .commit();
    }

    public String getCurrentGatewayHost() {
        return mPreferences.getString("GatewayHost", "");
    }

    /**
     * bc锁cameraId
     */
    public void saveBcCameraId(String deviceId, String cameraId) {
        mEditor.putString("BcCameraId" + deviceId, cameraId)
                .commit();
    }

    public String getBcCameraId(String deviceId) {
        return mPreferences.getString("BcCameraId" + deviceId, "");
    }

    /**
     * 爱看OSS子账号数据
     */
    public void saveICamOssInfo(String value) {
        mEditor.putString("ICamOssInfo", value)
                .commit();
    }

    public String getICamOssInfo() {
        return mPreferences.getString("ICamOssInfo", "{}");
    }

    /**
     * 智能猫眼电池电量缓存
     */
    public void saveCateyeBattery(String deviceId, String batteryValue) {
        mEditor.putString(IPreferenceKey.P_KEY_CATEYE_BATTERY + deviceId, batteryValue)
                .commit();
    }

    public String getCateyeBattery(String deviceId) {
        return mPreferences.getString(IPreferenceKey.P_KEY_CATEYE_BATTERY + deviceId, "");
    }


    /**
     * 是否第一次进入摄像机全屏
     */
    public void setIsFrisCameraFullScreen(boolean flag) {
        mEditor.putBoolean(IPreferenceKey.P_KEY_ISFRIST_CAMERA_FULL_SCREEN, flag)
                .commit();
    }

    public boolean getIsFrisCameraFullScreen() {
        return mPreferences.getBoolean(IPreferenceKey.P_KEY_ISFRIST_CAMERA_FULL_SCREEN, true);
    }

    /**
     * 是否第一次获取小物摄像机本地预置位
     */
    public void setLocalPositionTip(boolean flag) {
        mEditor.putBoolean(IPreferenceKey.P_KEY_ISFRIST_LOCAL_POSITION, flag)
                .commit();
    }

    public boolean getLocalPositionTip() {
        return mPreferences.getBoolean(IPreferenceKey.P_KEY_ISFRIST_LOCAL_POSITION, false);
    }

    /**
     * 获取指纹开锁预置位
     */
    public void setTouchIDunLock(String deviceID, String pwd) {
        mEditor.putString(IPreferenceKey.P_KEY_TOUCHID_UNLOCK + deviceID, pwd)
                .commit();
    }

    public String getTouchIDunLock(String deviceID) {
        return mPreferences.getString(IPreferenceKey.P_KEY_TOUCHID_UNLOCK + deviceID, "");
    }


    /**
     * 保存wifi名称和对应的密码
     *
     * @param wifiName
     * @param password
     */
    public void setWifiNameAndPassword(String wifiName, String password) {
        mEditor.putString(IPreferenceKey.P_KEY_WIFI_INFO + wifiName, password)
                .commit();
    }


    public String getWifiPassword(String wifiName) {
        return mPreferences.getString(IPreferenceKey.P_KEY_WIFI_INFO + wifiName, "");
    }

    /**
     * 设置插座的图标信息
     *
     * @param deviceId
     * @param iconId
     */
    public void setSocketIconImg(String deviceId, String iconId) {
        mEditor.putString(IPreferenceKey.P_KEY_SOCKET_ICON + deviceId, iconId)
                .commit();
    }


    public String getSocketIconImg(String deviceId) {
        return mPreferences.getString(IPreferenceKey.P_KEY_SOCKET_ICON + deviceId, "1");
    }

    /**
     * 设置窗帘的图标信息
     *
     * @param deviceId
     * @param iconId
     */
    public void setCurtainIconImg(String deviceId, String iconId) {
        mEditor.putString(IPreferenceKey.P_KEY_CURTAIN_ICON + deviceId, iconId)
                .commit();
    }


    public String getCurtainIconImg(String deviceId) {
        return mPreferences.getString(IPreferenceKey.P_KEY_CURTAIN_ICON + deviceId, "1");
    }

    /**
     * 设置场景显示的布局格式
     *
     * @param tag
     */
    public void setSceneShowLayout(String tag) {
        mEditor.putString(IPreferenceKey.P_KEY_SCENE_SHOW_LAYOUT, tag)
                .commit();
    }


    public String getSceneShowLayout() {
        return mPreferences.getString(IPreferenceKey.P_KEY_SCENE_SHOW_LAYOUT, "sudoku_3");
    }

    /**
     * h5是否显示引导页
     *
     * @param tag
     */
    public void setH5ShowGuidePage(String tag, String value) {
        mEditor.putString(IPreferenceKey.P_KEY_SHOW_GUIDE_PAGE + tag, value)
                .commit();
    }


    public String getH5ShowGuidePage(String tag) {
        return mPreferences.getString(IPreferenceKey.P_KEY_SHOW_GUIDE_PAGE + tag, "YES");
    }

    /**
     * 是否需要换回默认皮肤
     */
    public void setNeedDefaultSkin(boolean value) {
        mEditor.putBoolean(IPreferenceKey.P_KEY_NEED_DEFAULT_SKIN, value)
                .commit();
    }

    public Boolean getIsNeedDefaultSkin() {
        return mPreferences.getBoolean(IPreferenceKey.P_KEY_NEED_DEFAULT_SKIN, true);
    }

    /**
     * 物联通告
     */
    public void setWulianNoticeId(int value) {
        mEditor.putInt(IPreferenceKey.P_KEY_WULIAN_NOTICE, value)
                .commit();
    }

    public int getWulianNoticeId() {
        return mPreferences.getInt(IPreferenceKey.P_KEY_WULIAN_NOTICE, -1);
    }
}