package cc.wulian.smarthomev6.support.core.apiunit;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpHeaders;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.LocalInfo;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.bean.AccountBindInfoBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.ResponseBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.ThirdPartyBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.UserBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.sso.RegisterDeviceBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.sso.RegisterPhoneBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.sso.TokenToMqttBean;
import cc.wulian.smarthomev6.support.core.cipher.CipherUtil;
import cc.wulian.smarthomev6.support.event.AccountEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.RegularTool;
import cc.wulian.smarthomev6.support.utils.MD5Util;
import cc.wulian.smarthomev6.support.utils.WLog;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者: chao
 * 时间: 2017/6/20
 * 描述: 注册登录类接口（新）
 * 联系方式: 805901025@qq.com
 */

public class SsoApiUnit {
    private Context context;

    private LocalInfo localInfo;
    private UserApiUnit userApiUnit;

    public interface SsoApiCommonListener<T> {
        void onSuccess(T bean);

        void onFail(int code, String msg);
    }

    public SsoApiUnit(Context context) {
        this.context = context;
        localInfo = MainApplication.getApplication().getLocalInfo();
        userApiUnit = new UserApiUnit(context);
    }

    /**
     * 注册设备：第一次在手机上启动APP需要请求该接口到服务端进行认证，认证手机成功会返回一个terminalId
     * {
     * "deviceId": "85f235d5c7c1387b9f200148110a1eebaa5dcbee",
     * "appLang": "zh-Hans-US",
     * "apnsToken": "7c2fa4bf0e0ac5ae7e795ae97ffee0976724af3750e0ce44bba1081c9e796060",
     * "appVersion": "1.0.0",
     * "imei": "85f235d5c7c1387b9f200148110a1eebaa5dcbee",
     * "osType": "IOS",
     * "appType":2,
     * "osVersion": "10.3"
     * }
     */
    public void doRegisterDevice(final SsoApiCommonListener<RegisterDeviceBean> listener) {

        HashMap<String, String> table = new HashMap<>();
        table.put("deviceId", localInfo.appID);
        table.put("appLang", localInfo.appLang);
        table.put("appVersion", localInfo.appVersion);
        table.put("imei", localInfo.imei);
        table.put("osType", localInfo.os);
        table.put("osVersion", localInfo.osVersion);
        table.put("market", localInfo.market);
        JSONObject jsonObject = new JSONObject(table);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(jsonObject.toString(), time).toLowerCase();

        OkGo.post(ApiConstant.URL_SSO_REGISTER_DEVICE)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .connTimeOut(10000)      // 设置当前请求的连接超时时间
                .readTimeOut(10000)      // 设置当前请求的读取超时时间
                .writeTimeOut(10000)     // 设置当前请求的写入超时时间
                .upJson(jsonObject)
                .execute(new JsonCallback<ResponseBean<RegisterDeviceBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<RegisterDeviceBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            ApiConstant.setTerminalId(responseBean.data.terminalId);
                            HttpHeaders headers = OkGo.getInstance().getCommonHeaders();
                            if (headers == null) {
                                headers = new HttpHeaders();
                            }
                            headers.put("WL-TID", ApiConstant.getTerminalId());
                            OkGo.getInstance().addCommonHeaders(headers);
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(-1, responseBean.resultDesc);
                        }

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

    /**
     * 手机注册发送验证码
     * {
     * "phone": "18652915634",
     * "phoneCountryCode": "86",
     * }
     */
    public void doGetVerificationCode(String phone, String phoneCountryCode, final SsoApiCommonListener<Object> listener) {

        if (TextUtils.isEmpty(phoneCountryCode)) {
            phoneCountryCode = "86";
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("phone", phone);
        params.put("phoneCountryCode", phoneCountryCode);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);
        OkGo.get(ApiConstant.URL_SSO_GET_REGISTER_VERIFICATION_CODE)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new JsonCallback<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

    /**
     * 手机号注册
     * {
     * "phone": "18069812065",
     *  "phoneCountryCode": 86,
     *  "password": "e19d5cd5af0378da05f63f891c7467af",
     *  "authCode": "278486",
     * "terminalId":"****"
     * }
     */
    public void doRegisterPhone(String phone, String phoneCountryCode, String password, String authCode, final SsoApiCommonListener<RegisterPhoneBean> listener) {

        if (TextUtils.isEmpty(phoneCountryCode)) {
            phoneCountryCode = "86";
        }

        HashMap<String, String> table = new HashMap<>();
        table.put("phone", phone);
        table.put("phoneCountryCode", phoneCountryCode);
        table.put("password", MD5Util.encrypt(password));
        table.put("authCode", authCode);
        table.put("terminalId", ApiConstant.getTerminalId());
        JSONObject jsonObject = new JSONObject(table);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(jsonObject.toString(), time).toLowerCase();

        OkGo.post(ApiConstant.URL_SSO_REGISTER_PHONE)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .upJson(jsonObject)
                .execute(new JsonCallback<ResponseBean<RegisterPhoneBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<RegisterPhoneBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

    /**
     * 手机号/邮箱登录
     */
    public void doLogin(String phoneOrMail, String phoneCountryCode, String password, final SsoApiCommonListener<RegisterPhoneBean> listener) {
        if (RegularTool.isLegalEmailAddress(phoneOrMail)) {
            doLoginByMail(phoneOrMail, password, listener);
        } else if (RegularTool.isLegalChinaPhoneNumber(phoneOrMail)) {
            doLoginByPhone(phoneOrMail, phoneCountryCode, password, listener);
        }
    }

    //保存账号信息
    private void saveAccount(RegisterPhoneBean accountBean) {
        HttpHeaders headers = OkGo.getInstance().getCommonHeaders();
        if (headers == null) {
            headers = new HttpHeaders();
        }
        headers.put("WL-TID", ApiConstant.getTerminalId());
        headers.put("WL-TOKEN", accountBean.token);
        OkGo.getInstance().addCommonHeaders(headers);
        ApiConstant.setAppToken(accountBean.token);
        ApiConstant.setBaseServer(accountBean.server);
        ApiConstant.setUserID(accountBean.uId);
        ApiConstant.setSecretKey(accountBean.secretKey);
        //生成AES KEY
        String encrptyUid = MD5Util.encrypt(accountBean.uId);
        if (!TextUtils.isEmpty(accountBean.uId)) {
            ApiConstant.setAESKey(Base64.decode(accountBean.secretKey + encrptyUid.substring(encrptyUid.length() - 16, encrptyUid.length()) + "==", Base64.NO_WRAP));
        }
    }

    /**
     * 退出登录
     * }
     */
    public void doLogout(final SsoApiCommonListener<Object> listener) {

        HashMap<String, String> table = new HashMap<>();
        table.put("token", ApiConstant.getAppToken());
        JSONObject jsonObject = new JSONObject(table);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(jsonObject.toString(), time).toLowerCase();

        OkGo.post(ApiConstant.URL_SSO_LOGOU)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .upJson(jsonObject)
                .execute(new JsonCallback<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            MainApplication.getApplication().logout(false);
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

    /**
     * 修改密码发送手机验证码（手机/邮箱）
     */
    public void doGetChagnePwdVerificationCode(String account, final SsoApiCommonListener<Object> listener) {
        if (RegularTool.isLegalEmailAddress(account)) {
            doGetMailChagnePwdVerificationCode(account, listener);
        } else if (RegularTool.isLegalChinaPhoneNumber(account)) {
            doGetPhoneChagnePwdVerificationCode(account, null, listener);
        }
    }

    /**
     * 修改密码发送手机验证码
     * {
     * "phone": "18652915634",
     * "phoneCountryCode": "86",
     * }
     */
    public void doGetPhoneChagnePwdVerificationCode(String phone, String phoneCountryCode, final SsoApiCommonListener<Object> listener) {

        if (TextUtils.isEmpty(phoneCountryCode)) {
            phoneCountryCode = "86";
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("phone", phone);
        params.put("phoneCountryCode", phoneCountryCode);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);

        OkGo.get(ApiConstant.URL_GET_CHANGE_PWD_VERIFICATION_CODE)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new JsonCallback<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

    /**
     * 忘记密码时修改密码（邮箱/手机号）
     */
    public void doChangePwd(String account, String password, String authCode, final SsoApiCommonListener<Object> listener) {
        if (RegularTool.isLegalEmailAddress(account)) {
            doChangePwdByMail(account, authCode, password, listener);
        } else if (RegularTool.isLegalChinaPhoneNumber(account)) {
            doChangePwdByPhone(account, null, password, authCode, listener);
        }
    }

    /**
     * 手机号+验证码修改用户密码
     * {
     * "phone": "18069812065",
     *  "phoneCountryCode": 86,
     *  "password": "e19d5cd5af0378da05f63f891c7467af",
     * "terminalId":"****"
     * }
     */
    public void doChangePwdByPhone(String phone, String phoneCountryCode, String password, String authCode, final SsoApiCommonListener<Object> listener) {

        if (TextUtils.isEmpty(phoneCountryCode)) {
            phoneCountryCode = "86";
        }

        HashMap<String, String> table = new HashMap<>();
        table.put("phone", phone);
        table.put("phoneCountryCode", phoneCountryCode);
        table.put("password", MD5Util.encrypt(password));
        table.put("authCode", authCode);
        JSONObject jsonObject = new JSONObject(table);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(jsonObject.toString(), time).toLowerCase();

        OkGo.post(ApiConstant.URL_CHANGE_PWD_BY_PHONE)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .upJson(jsonObject)
                .execute(new JsonCallback<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

    /**
     * 用旧密码修改用户密码
     * }
     */
    public void doChangePwdByOld(String password, String newPassword, final SsoApiCommonListener<Object> listener) {

        HashMap<String, String> table = new HashMap<>();
        table.put("password", MD5Util.encrypt(password));
        table.put("newPassword", MD5Util.encrypt(newPassword));
        JSONObject jsonObject = new JSONObject(table);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(jsonObject.toString(), time).toLowerCase();

        OkGo.post(ApiConstant.URL_API_CHANGE_PWD_BY_OLDPWD)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .upJson(jsonObject)
                .execute(new JsonCallback<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }


    /**
     * 第三方登录
     * {
     * "thirdType": 第三方类型 1-微信 2-QQ 3-微博 默认为1
     *  "terminalId": 终端(手机)
     *  "openId": 第三方openId
     * "avatar":手机验证码
     * "nick":昵称
     * "gender":性别 0-男 1-女 默认0
     * }
     */
    public void doLoginTHIRD(ThirdPartyBean thirdPartyData, final SsoApiCommonListener<RegisterPhoneBean> listener) {
        String thirdType = thirdPartyData.getPartnerId() + "";
        String openId = thirdPartyData.getOpenId();
        String avatar = thirdPartyData.getAvatar();
        String unionId = thirdPartyData.getUnionid();
        String nick = thirdPartyData.getNick();
        int gender = thirdPartyData.getGender();
        if (TextUtils.isEmpty(thirdType)) {
            thirdType = "1";
        }
        if (gender != 1) {
            gender = 0;
        }

        HashMap<String, String> table = new HashMap<>();
        table.put("thirdType", thirdType);
        table.put("terminalId", ApiConstant.getTerminalId());
        table.put("openId", openId);
        table.put("unionId", unionId);
        table.put("avatar", avatar);
        table.put("nick", nick);
        table.put("gender", gender + "");
        JSONObject jsonObject = new JSONObject(table);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(jsonObject.toString(), time).toLowerCase();

        OkGo.post(ApiConstant.URL_SSO_LOGIN_THIRD)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .upJson(jsonObject)
                .execute(new JsonCallback<ResponseBean<RegisterPhoneBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<RegisterPhoneBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            saveAccount(responseBean.data);
                            userApiUnit.doLoginSuccess(responseBean.data.mqttInfo);
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

    /**
     * token换取mqtt连接信息
     * {
     * "token": *******
     * }
     */
    public void doTokenToMqtt(final SsoApiCommonListener<TokenToMqttBean> listener) {

        HashMap<String, String> params = new HashMap<>();
        params.put("token", ApiConstant.getAppToken());

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);

        OkGo.get(ApiConstant.URL_SSO_TOKEN_TO_MQTT)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new JsonCallback<ResponseBean<TokenToMqttBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<TokenToMqttBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }


    /**
     * 获取用户信息
     */
    public void doGetUserInfo(final SsoApiCommonListener<UserBean> listener) {

        HashMap<String, String> params = new HashMap<>();
        params.put("token", ApiConstant.getAppToken());

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);

        OkGo.get(ApiConstant.URL_API_USER_INFO)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new JsonCallback<ResponseBean<UserBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<UserBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            String jsonObject = JSON.toJSONString(responseBean.data);
                            Preference.getPreferences().saveCurrentAccountInfo(jsonObject);
                            EventBus.getDefault().post(new AccountEvent(AccountEvent.ACTION_LOGIN, responseBean.data));
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

    /**
     * 验证手机发送手机验证码
     * {
     * "phone": "18652915634",
     * "phoneCountryCode": "86",
     * }
     */
    public void doGetVerifyPhoneCode(String phone, String phoneCountryCode, final SsoApiCommonListener<Object> listener) {

        if (TextUtils.isEmpty(phoneCountryCode)) {
            phoneCountryCode = "86";
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("phone", phone);
        params.put("phoneCountryCode", phoneCountryCode);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);

        OkGo.get(ApiConstant.URL_API_GET_VERIFY_PHONE_CODE)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new JsonCallback<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

    /**
     * 验证用户手机号
     * {
     * "phone": "18652915634",
     * "phoneCountryCode": "86",
     * "authCode": "authCode",
     * }
     */
    public void doVerifyPhone(String phone, String phoneCountryCode, String authCode, final SsoApiCommonListener<Object> listener) {

        if (TextUtils.isEmpty(phoneCountryCode)) {
            phoneCountryCode = "86";
        }

        HashMap<String, String> table = new HashMap<>();
        table.put("phone", phone);
        table.put("phoneCountryCode", phoneCountryCode);
        table.put("authCode", authCode);
        JSONObject jsonObject = new JSONObject(table);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(jsonObject.toString(), time);

        OkGo.post(ApiConstant.URL_API_VERIFY_PHONE)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .upJson(jsonObject)
                .execute(new JsonCallback<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

    /**
     * 修改手机发送手机验证码
     * {
     * "phone": "18652915634",
     * "phoneCountryCode": "86",
     * }
     */
    public void doGetChagnePhoneVerificationCode(String phone, String phoneCountryCode, final SsoApiCommonListener<Object> listener) {

        if (TextUtils.isEmpty(phoneCountryCode)) {
            phoneCountryCode = "86";
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("phone", phone);
        params.put("phoneCountryCode", phoneCountryCode);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);

        OkGo.get(ApiConstant.URL_API_CHANGE_PHONE_VERIFICATION_CODE)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new JsonCallback<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

    /**
     * 修改用户手机号
     */
    public void doChangePhone(String phone, String phoneCountryCode, String authCode, final SsoApiCommonListener<Object> listener) {

        if (TextUtils.isEmpty(phoneCountryCode)) {
            phoneCountryCode = "86";
        }

        HashMap<String, String> table = new HashMap<>();
        table.put("phone", phone);
        table.put("phoneCountryCode", phoneCountryCode);
        table.put("authCode", authCode);
        JSONObject jsonObject = new JSONObject(table);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(jsonObject.toString(), time);

        OkGo.post(ApiConstant.URL_API_CHANGE_PHONE)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .upJson(jsonObject)
                .execute(new JsonCallback<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

    /**
     * 用户上传头像
     */
    public void doChangeAvatar(String path, final SsoApiCommonListener<Object> listener) {
        OkGo.post(ApiConstant.URL_API_CHANGE_AVATAR)
                .tag(this)
                .params("file", new File(path))
                .execute(new JsonCallback<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }

                    @Override
                    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        super.upProgress(currentSize, totalSize, progress, networkSpeed);
                        WLog.i("UserApi", "upProgress: " + progress);
                    }
                });
    }

    /**
     * 在线客服上传图片
     */
    public void doUploadServicePic(String path, final SsoApiCommonListener<Object> listener) {
        OkGo.post(ApiConstant.URL_APP_UPLOAD_FILE)
                .tag(this)
                .params("file", new File(path))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String responseBean, Call call, Response response) {
                        WLog.i("doUploadServicePic", "onSuccess: " + responseBean.toString());
                        listener.onSuccess(responseBean);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.i("doUploadServicePic", "onError: " + response);
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }

                    @Override
                    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        super.upProgress(currentSize, totalSize, progress, networkSpeed);
                        WLog.i("doUploadServicePic", "upProgress: " + progress);
                    }
                });
    }

    /**
     * 修改用户个人信息 <p>
     * (目前只有名称)
     */
    public void doChangeUserInfo(String nick, final SsoApiCommonListener<Object> listener) {

        HashMap<String, String> table = new HashMap<>();
        table.put("nick", nick);
        JSONObject jsonObject = new JSONObject(table);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(jsonObject.toString(), time).toLowerCase();

        OkGo.post(ApiConstant.URL_API_CHANGE_USER_INFO)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .upJson(jsonObject)
                .execute(new JsonCallback<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

    /**
     * 邮箱登录
     * {
     * "email": "email",
     *  "password": "e19d5cd5af0378da05f63f891c7467af",
     * "terminalId":"****"
     * }
     */
    public void doLoginByMail(String email, String password, final SsoApiCommonListener<RegisterPhoneBean> listener) {

        HashMap<String, String> table = new HashMap<>();
        table.put("email", email);
        table.put("password", MD5Util.encrypt(password));
        table.put("terminalId", ApiConstant.getTerminalId());
        JSONObject jsonObject = new JSONObject(table);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(jsonObject.toString(), time).toLowerCase();

        OkGo.post(ApiConstant.URL_SSO_LOGIN_EMAIL)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .upJson(jsonObject)
                .execute(new JsonCallback<ResponseBean<RegisterPhoneBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<RegisterPhoneBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            saveAccount(responseBean.data);
                            userApiUnit.doLoginSuccess(responseBean.data.mqttInfo);
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

    /**
     * 手机号登录
     * {
     * "phone": "18069812065",
     *  "phoneCountryCode": 86,
     *  "password": "e19d5cd5af0378da05f63f891c7467af",
     * "terminalId":"****"
     * }
     */
    public void doLoginByPhone(String phone, String phoneCountryCode, String password, final SsoApiCommonListener<RegisterPhoneBean> listener) {

        if (TextUtils.isEmpty(phoneCountryCode)) {
            phoneCountryCode = "86";
        }

        HashMap<String, String> table = new HashMap<>();
        table.put("phone", phone);
        table.put("phoneCountryCode", phoneCountryCode);
        table.put("password", MD5Util.encrypt(password));
        table.put("terminalId", ApiConstant.getTerminalId());
        JSONObject jsonObject = new JSONObject(table);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(jsonObject.toString(), time).toLowerCase();

        OkGo.post(ApiConstant.URL_SSO_LOGIN)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .upJson(jsonObject)
                .execute(new JsonCallback<ResponseBean<RegisterPhoneBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<RegisterPhoneBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            saveAccount(responseBean.data);
                            userApiUnit.doLoginSuccess(responseBean.data.mqttInfo);
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

    /**
     * 绑定邮箱
     * {
     * "email": "email",
     * }
     */
    public void doBindMail(String email, final SsoApiCommonListener listener) {

        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time).toLowerCase();

//        String url = "http://172.18.2.227:9098/api/user/email/update" + "?" + "email=" + email;
        String url = ApiConstant.URL_API_BIND_EMAIL + "?" + "email=" + email;
        OkGo.get(url)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .execute(new JsonCallback<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

    /**
     * 验证邮箱
     * {
     * "email": "email",
     * "authcode": "authcode",
     * }
     */
    public void doVerifyMail(String email, String authCode, final SsoApiCommonListener listener) {

        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("authCode", authCode);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time).toLowerCase();

//        String url = "http://172.18.2.227:9098/api/user/email/update/verify" + "?" + "email=" + email+ "&"+  "authCode="+ authCode;
        String url = ApiConstant.URL_API_EMAIL_VERIFY + "?" + "email=" + email + "&" + "authCode=" + authCode;
        OkGo.get(url)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .execute(new JsonCallback<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

//    /**
//     * 邮箱注册
//     * {
//     *  "email": "email",
//     *  "password": "e19d5cd5af0378da05f63f891c7467af",
//     * }
//     */
//    public void doRegisterByMail(String email, String password,final SsoApiCommonListener listener) {
//
//        HashMap<String, String> table = new HashMap<>();
//        table.put("email", email);
//        table.put("password", password);
//        JSONObject jsonObject = new JSONObject(table);
//
//        String time = System.currentTimeMillis() + "";
//        String sign = CipherUtil.getClondApiSign(jsonObject.toString(), time).toLowerCase();
//
//        OkGo.post(ApiConstant.URL_SSO_REGISTER_EMAIL)
//                .tag(this)
//                .headers("WL-PARTNER-ID", ApiConstant.APPID)
//                .headers("WL-TIMESTAMP", time)
//                .headers("WL-SIGN", sign)
//                .upJson(jsonObject)
//                .execute(new JsonCallback<ResponseBean<Object>>() {
//                    @Override
//                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
//                        if (responseBean.isSuccess()) {
//                            listener.onSuccess(responseBean);
//                        } else {
//                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
//                        }
//                    }
//
//                    @Override
//                    public void onError(Call call, Response response, Exception e) {
//                        listener.onFail(-1, context.getString(R.string.Service_Error));
//                    }
//                });
//    }

    /**
     * 邮箱修改密码获取验证码
     */
    public void doGetMailChagnePwdVerificationCode(String email, final SsoApiCommonListener listener) {

        HashMap<String, String> table = new HashMap<>();
        table.put("email", email);
        table.put("terminalId", ApiConstant.getTerminalId());
        JSONObject jsonObject = new JSONObject(table);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(jsonObject.toString(), time).toLowerCase();

        OkGo.post(ApiConstant.URL_API_CHANGE_PWD_EMAIL)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .upJson(jsonObject)
                .execute(new JsonCallback<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

    /**
     * 邮箱修改密码
     */
    public void doChangePwdByMail(String email, String authCode, String password, final SsoApiCommonListener<Object> listener) {

        HashMap<String, String> table = new HashMap<>();
        table.put("email", email);
        table.put("authCode", authCode);
        table.put("password", MD5Util.encrypt(password));
        JSONObject jsonObject = new JSONObject(table);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(jsonObject.toString(), time).toLowerCase();

        OkGo.post(ApiConstant.URL_API_CHANGE_PWD_EMAIL_VERIFY)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .upJson(jsonObject)
                .execute(new JsonCallback<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

    /**
     * 第三方账号绑定手机号获取验证码
     * {
     * "phone": "phone",
     * "phoneCountryCode": "e19d5cd5af0378da05f63f891c7467af",
     * "partnerId": "partnerId",
     * }
     */
    public void doThirdGetPhoneCode(String phone, String phoneCountryCode, final SsoApiCommonListener<Object> listener) {

        if (TextUtils.isEmpty(phoneCountryCode)) {
            phoneCountryCode = "86";
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("phone", phone);
        params.put("phoneCountryCode", phoneCountryCode);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);
        OkGo.get(ApiConstant.URL_API_THIRD_GET_PHONECODE)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new JsonCallback<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

    /**
     * 第三方账号绑定
     * {
     * "openId": "openId",
     *  "thirdType": "thirdType",
     *  "terminalId": "terminalId",
     *  "phone": "phone",
     *  "phoneCountryCode": "phoneCountryCode",
     *  "email": "email",
     *  "authCode": "authCode",
     *  "partnerId": "partnerId",
     * }
     */
    public void doThirdBind(String openId, String unionId, String thirdType, String phone, String phoneCountryCode, String email, String authCode, final SsoApiCommonListener<RegisterPhoneBean> listener) {
        if (TextUtils.isEmpty(phoneCountryCode)) {
            phoneCountryCode = "86";
        }

        HashMap<String, String> table = new HashMap<>();
        table.put("openId", openId);
        table.put("unionId", unionId);
        table.put("thirdType", thirdType);
        table.put("terminalId", ApiConstant.getTerminalId());
        table.put("phone", phone);
        table.put("phoneCountryCode", phoneCountryCode);
        table.put("authCode", authCode);
        if (!TextUtils.isEmpty(email)) {
            table.put("email", email);
        }
        JSONObject jsonObject = new JSONObject(table);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(jsonObject.toString(), time).toLowerCase();

        OkGo.post(ApiConstant.URL_SSO_THIRD_BIND)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .upJson(jsonObject)
                .execute(new JsonCallback<ResponseBean<RegisterPhoneBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<RegisterPhoneBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            saveAccount(responseBean.data);
                            userApiUnit.doLoginSuccess(responseBean.data.mqttInfo);
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

    /**
     * 第三方账号注册
     * {
     * "openId": "openId",
     *  "thirdType": "thirdType",
     *  "avatar": "avatar",
     *  "nick": "nick",
     *  "terminalId": "terminalId",
     *  "phone": "phone",
     *  "phoneCountryCode": "phoneCountryCode",
     *  "email": "email",
     *  "password": "password",
     *  "partnerId": "partnerId",
     * }
     */
    public void doThirdRegister(String openId, String unionId, String thirdType, String avatar, String nick, String phone, String phoneCountryCode, String email, String password, final SsoApiCommonListener<RegisterPhoneBean> listener) {

        if (TextUtils.isEmpty(phoneCountryCode)) {
            phoneCountryCode = "86";
        }

        HashMap<String, String> table = new HashMap<>();
        table.put("openId", openId);
        table.put("unionId", unionId);
        table.put("thirdType", thirdType);
        table.put("avatar", avatar);
        table.put("nick", nick);
        table.put("terminalId", ApiConstant.getTerminalId());
        table.put("phone", phone);
        table.put("phoneCountryCode", phoneCountryCode);
        table.put("password", MD5Util.encrypt(password));
        if (!TextUtils.isEmpty(email)) {
            table.put("email", email);
        }
        JSONObject jsonObject = new JSONObject(table);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(jsonObject.toString(), time).toLowerCase();

        OkGo.post(ApiConstant.URL_SSO_REGISTER_THIRD)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .upJson(jsonObject)
                .execute(new JsonCallback<ResponseBean<RegisterPhoneBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<RegisterPhoneBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            saveAccount(responseBean.data);
                            userApiUnit.doLoginSuccess(responseBean.data.mqttInfo);
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

    /**
     * 第三方账号绑定（手机号已绑定其他第三方账号）
     * {
     * "openId": "openId",
     *  "thirdType": "thirdType",
     *  "terminalId": "terminalId",
     *  "phone": "phone",
     *  "phoneCountryCode": "phoneCountryCode",
     *  "email": "email",
     *  "authCode": "authCode",
     *  "partnerId": "partnerId",
     * }
     */
    public void doThirdUpdate(String openId, String unionId, String thirdType, String phone, String phoneCountryCode, String email, String authCode, final SsoApiCommonListener<RegisterPhoneBean> listener) {

        if (TextUtils.isEmpty(phoneCountryCode)) {
            phoneCountryCode = "86";
        }

        HashMap<String, String> table = new HashMap<>();
        table.put("openId", openId);
        table.put("unionId", unionId);
        table.put("thirdType", thirdType);
        table.put("terminalId", ApiConstant.getTerminalId());
        table.put("phone", phone);
        table.put("phoneCountryCode", phoneCountryCode);
        table.put("authCode", authCode);
        if (!TextUtils.isEmpty(email)) {
            table.put("email", email);
        }
        JSONObject jsonObject = new JSONObject(table);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(jsonObject.toString(), time).toLowerCase();

        OkGo.post(ApiConstant.URL_SSO_REPLACE_THIRD)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .upJson(jsonObject)
                .execute(new JsonCallback<ResponseBean<RegisterPhoneBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<RegisterPhoneBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            saveAccount(responseBean.data);
                            userApiUnit.doLoginSuccess(responseBean.data.mqttInfo);
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

    /**
     * 用户中心查询三方账号
     * {
     * "uId": "uId",
     * "partnerId": "partnerId",
     * }
     */
    public void doUserCenterThirdGet(String uId, final SsoApiCommonListener<AccountBindInfoBean> listener) {

        HashMap<String, String> params = new HashMap<>();
        params.put("uId", uId);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);
        OkGo.get(ApiConstant.URL_API_USER_CENTER_GET)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new JsonCallback<ResponseBean<AccountBindInfoBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<AccountBindInfoBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

    /**
     * 用户中心绑定三方账号
     * {
     * "openId": "openId",
     *  "thirdType": "thirdType",
     *  "uId": "uId",
     *  "partnerId": "partnerId",
     * }
     */
    public void doUserCenterThirdBind(String openId, String unionId, final int thirdType, String uId, final SsoApiCommonListener<RegisterPhoneBean> listener) {

        HashMap<String, String> table = new HashMap<>();
        table.put("openId", openId);
        table.put("unionId", unionId);
        table.put("thirdType", thirdType + "");
        table.put("uId", uId);
        JSONObject jsonObject = new JSONObject(table);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(jsonObject.toString(), time).toLowerCase();

        OkGo.post(ApiConstant.URL_API_USER_CENTER_BIND)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .upJson(jsonObject)
                .execute(new JsonCallback<ResponseBean<RegisterPhoneBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<RegisterPhoneBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            if (responseBean.getResultCode() == 2000010) {
                                responseBean.resultDesc = context.getString(R.string.AccountSecurity_Bunded_Wechat);
                                if (thirdType == 1) {
                                    responseBean.resultDesc = context.getString(R.string.AccountSecurity_Bunded_Wechat);
                                } else if (thirdType == 2) {
                                    responseBean.resultDesc = context.getString(R.string.AccountSecurity_Bunded_QQ);
                                } else if (thirdType == 3) {
                                    responseBean.resultDesc = context.getString(R.string.AccountSecurity_Bunded_Weibo);
                                }
                            }
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

    /**
     * 用户中心解绑三方账号
     * {
     *  "thirdType": "thirdType",
     *  "uId": "uId",
     *  "partnerId": "partnerId",
     * }
     */
    public void doUserCenterThirdUnbind(int thirdType, String uId, final SsoApiCommonListener listener) {

        HashMap<String, String> table = new HashMap<>();
        table.put("thirdType", thirdType + "");
        table.put("uId", uId);
        JSONObject jsonObject = new JSONObject(table);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(jsonObject.toString(), time).toLowerCase();

        OkGo.post(ApiConstant.URL_API_USER_CENTER_UNBIND)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .upJson(jsonObject)
                .execute(new JsonCallback<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

    /**
     * 邮箱注册发送验证码
     * {
     * "email": "email",
     * "terminalId": "terminalId",
     * }
     */
    public void doGetVerificationCode(String email, final SsoApiCommonListener<Object> listener) {

        HashMap<String, String> table = new HashMap<>();
        table.put("email", email);
        table.put("terminalId", ApiConstant.getTerminalId());
        JSONObject jsonObject = new JSONObject(table);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(jsonObject.toString(), time).toLowerCase();
        OkGo.post(ApiConstant.URL_SSO_GET_MAIL_REGISTER_VERIFICATION_CODE)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .upJson(jsonObject)
                .execute(new JsonCallback<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

    /**
     * 邮箱注册
     * {
     * "email": "email",
     * "password": "e19d5cd5af0378da05f63f891c7467af",
     * "authCode": "278486",
     * "terminalId": "terminalId",
     * }
     */
    public void doRegisterMail(String email, String password, String authCode, final SsoApiCommonListener<RegisterPhoneBean> listener) {

        HashMap<String, String> table = new HashMap<>();
        table.put("email", email);
        table.put("password", MD5Util.encrypt(password));
        table.put("authCode", authCode);
        table.put("terminalId", ApiConstant.getTerminalId());
        JSONObject jsonObject = new JSONObject(table);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(jsonObject.toString(), time).toLowerCase();

        OkGo.post(ApiConstant.URL_SSO_REGISTER_MAIL)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .upJson(jsonObject)
                .execute(new JsonCallback<ResponseBean<RegisterPhoneBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<RegisterPhoneBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

    /**
     * 验证邮箱发送邮箱验证码
     * {
     * "email": "email",
     * }
     */
    public void doGetVerifyOldMailCode(String email, final SsoApiCommonListener<Object> listener) {

        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);

        OkGo.get(ApiConstant.URL_API_GET_VERIFY_OLD_MAIL_CODE)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new JsonCallback<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

    /**
     * 验证用户邮箱
     * {
     * "email": "email",
     * "authCode": "authCode",
     * }
     */
    public void doVerifyOldMail(String email, String authCode, final SsoApiCommonListener<Object> listener) {

        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("authCode", authCode);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);

        OkGo.get(ApiConstant.URL_API_VERIFY_OLD_MAIL)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new JsonCallback<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

    /**
     * 注册时检查手机或者邮箱验证码
     * {
     * "account": "账号/邮箱",
     * "phoneCountryCode": "86",
     * "authCode": "验证码",
     * }
     */
    public void doCheckVerificationCodeR(String account, String phoneCountryCode, String authCode, final SsoApiCommonListener<Object> listener) {

        if (TextUtils.isEmpty(account)) {
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        if (RegularTool.isLegalChinaPhoneNumber(account)) {
            if (TextUtils.isEmpty(phoneCountryCode)) {
                phoneCountryCode = "86";
            }
            params.put("phone", account);
            params.put("phoneCountryCode", phoneCountryCode);
        } else if (RegularTool.isLegalEmailAddress(account)) {
            params.put("email", account);
        }
        params.put("authCode", authCode);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);
        OkGo.get(ApiConstant.URL_SSO_CHECK_VERIFICATION_CODE_REGISTER)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new JsonCallback<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

    /**
     * 忘记密码时检查手机或者邮箱验证码
     * {
     * "account": "账号/邮箱",
     * "phoneCountryCode": "86",
     * "authCode": "验证码",
     * }
     */
    public void doCheckVerificationCodeF(String account, String phoneCountryCode, String authCode, final SsoApiCommonListener<Object> listener) {

        if (TextUtils.isEmpty(account)) {
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        if (RegularTool.isLegalChinaPhoneNumber(account)) {
            if (TextUtils.isEmpty(phoneCountryCode)) {
                phoneCountryCode = "86";
            }
            params.put("phone", account);
            params.put("phoneCountryCode", phoneCountryCode);
        } else if (RegularTool.isLegalEmailAddress(account)) {
            params.put("email", account);
        }
        params.put("authCode", authCode);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);
        OkGo.get(ApiConstant.URL_SSO_CHECK_VERIFICATION_CODE_FORGOT)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new JsonCallback<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

    /**
     * 手机号或邮箱注册，并自动登录
     * {
     * "phone": "18069812065",
     *  "phoneCountryCode": 86,
     *  "password": "e19d5cd5af0378da05f63f891c7467af",
     * "terminalId":"****"
     * }
     */
    public void doRegisterAndLogin(String account, String phoneCountryCode, String password, final SsoApiCommonListener<RegisterPhoneBean> listener) {

        if (TextUtils.isEmpty(account)) {
            return;
        }

        HashMap<String, String> table = new HashMap<>();
        if (RegularTool.isLegalChinaPhoneNumber(account)) {
            if (TextUtils.isEmpty(phoneCountryCode)) {
                phoneCountryCode = "86";
            }
            table.put("phone", account);
            table.put("phoneCountryCode", phoneCountryCode);
        } else if (RegularTool.isLegalEmailAddress(account)) {
            table.put("email", account);
        }
        table.put("password", MD5Util.encrypt(password));
        table.put("terminalId", ApiConstant.getTerminalId());
        JSONObject jsonObject = new JSONObject(table);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(jsonObject.toString(), time).toLowerCase();

        OkGo.post(ApiConstant.URL_SSO_REGISTER_AND_LOGIN)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .upJson(jsonObject)
                .execute(new JsonCallback<ResponseBean<RegisterPhoneBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<RegisterPhoneBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            saveAccount(responseBean.data);
                            userApiUnit.doLoginSuccess(responseBean.data.mqttInfo);
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

    /**
     * 手机号或邮箱修改密码，并自动登录
     * {
     * "phone": "18069812065",
     * "phoneCountryCode": 86,
     * "password": "e19d5cd5af0378da05f63f891c7467af",
     * "authCode": "278486",
     * "terminalId":"****"
     * }
     */
    public void doUpdateAndLoginByAccount(String account, String phoneCountryCode, String password, final SsoApiCommonListener<RegisterPhoneBean> listener) {

        if (TextUtils.isEmpty(account)) {
            return;
        }

        HashMap<String, String> table = new HashMap<>();
        if (RegularTool.isLegalChinaPhoneNumber(account)) {
            if (TextUtils.isEmpty(phoneCountryCode)) {
                phoneCountryCode = "86";
            }
            table.put("phone", account);
            table.put("phoneCountryCode", phoneCountryCode);
        } else if (RegularTool.isLegalEmailAddress(account)) {
            table.put("email", account);
        }
        table.put("password", MD5Util.encrypt(password));
        table.put("terminalId", ApiConstant.getTerminalId());
        JSONObject jsonObject = new JSONObject(table);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(jsonObject.toString(), time).toLowerCase();

        OkGo.post(ApiConstant.URL_SSO_UPDATE_AND_LOGIN_BY_ACCOUNT)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .upJson(jsonObject)
                .execute(new JsonCallback<ResponseBean<RegisterPhoneBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<RegisterPhoneBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            saveAccount(responseBean.data);
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

    /**
     * 通过旧密码修改用户密码并登录
     * {
     * "password": "旧密码",
     * "newPassword": "新密码"
     * }
     */
    public void doUpdateAndLoginByPwd(String password, String newPassword, final SsoApiCommonListener<RegisterPhoneBean> listener) {

        HashMap<String, String> table = new HashMap<>();
        table.put("oldPassword", MD5Util.encrypt(password));
        table.put("newPassword", MD5Util.encrypt(newPassword));
        table.put("uId", ApiConstant.getUserID());
        table.put("terminalId", ApiConstant.getTerminalId());
        JSONObject jsonObject = new JSONObject(table);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(jsonObject.toString(), time).toLowerCase();

        OkGo.post(ApiConstant.URL_SSO_UPDATE_AND_LOGIN_BY_PWD)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .upJson(jsonObject)
                .execute(new JsonCallback<ResponseBean<RegisterPhoneBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<RegisterPhoneBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            MainApplication.getApplication().logout(false);
                            saveAccount(responseBean.data);
                            userApiUnit.doLoginSuccess(responseBean.data.mqttInfo);
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

    /**
     * 短信登录获取手机验证码
     * {
     * "account": "18652915634",
     * "phoneCountryCode": "86",
     * }
     */
    public void doGetSmsLoginVerificationCode(String account, String phoneCountryCode, final SsoApiCommonListener<Object> listener) {

        HashMap<String, String> params = new HashMap<>();

        if (RegularTool.isLegalChinaPhoneNumber(account)) {
            params.put("phone", account);
            if (TextUtils.isEmpty(phoneCountryCode)) {
                phoneCountryCode = "86";
            }
            params.put("phoneCountryCode", phoneCountryCode);
        } else if (RegularTool.isLegalEmailAddress(account)) {
            params.put("email", account);
        }

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);

        OkGo.get(ApiConstant.URL_GET_SMS_LOGIN_VERIFICATION_CODE)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new JsonCallback<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

    /**
     * 手机验证码登录
     */
    public void doLoginBySms(String account, String phoneCountryCode, String authCode, final SsoApiCommonListener<RegisterPhoneBean> listener) {

        if (TextUtils.isEmpty(phoneCountryCode)) {
            phoneCountryCode = "86";
        }

        HashMap<String, String> table = new HashMap<>();
        if (RegularTool.isLegalChinaPhoneNumber(account)) {
            table.put("phone", account);
            table.put("phoneCountryCode", phoneCountryCode);
        } else if (RegularTool.isLegalEmailAddress(account)) {
            table.put("email", account);
        }
        table.put("authCode", authCode);
        table.put("terminalId", ApiConstant.getTerminalId());

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(table, time).toLowerCase();

        OkGo.get(ApiConstant.URL_SSO_LOGIN_SMS)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(table)
                .execute(new JsonCallback<ResponseBean<RegisterPhoneBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<RegisterPhoneBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            saveAccount(responseBean.data);
                            userApiUnit.doLoginSuccess(responseBean.data.mqttInfo);
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, context.getString(R.string.Service_Error));
                    }
                });
    }

}
