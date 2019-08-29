package cc.wulian.smarthomev6.support.core.apiunit;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpHeaders;
import com.wulian.routelibrary.common.ErrorCode;
import com.wulian.routelibrary.common.RequestType;
import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.routelibrary.common.RouteLibraryParams;
import com.wulian.routelibrary.controller.TaskResultListener;
import com.wulian.routelibrary.utils.AuthCode;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cc.wulian.smarthomev6.entity.CateyeRecordEntity;
import cc.wulian.smarthomev6.entity.CateyeVideoEntity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.home.widget.HomeWidgetManager;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamCheckBindBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamCheckBindResultBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamDevicesBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamLoginBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamRenameBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamReplayBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamUnbindBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamVersionInfoBean;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.CameraUtil;
import cc.wulian.smarthomev6.support.utils.MD5Util;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.WLog;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zbl on 2017/5/9.
 * 摄像头sdk相关接口
 */
@Deprecated
public class ICamApiUnit {
    /**
     * Https 常量
     **/
    public static final String HTTPS_PROTOCOL = "https://";
    /**
     * app类型（爱看体系中）
     */
    public static final String USER_AGENT = "fcsn";

    private Context context;
    private static String realHost = ApiConstant.ICAM_URL;
    private OSS mOSSClient;
    private ICamLoginBean iCamInfo;

    private String userID, pwdMD5;
    private static String user_agent;
    /**
     * 0 账号token登陆，1 网关号作为账号登陆
     */
    private int loginType;

    private boolean isPicComplete = false;
    private boolean isVideoComplete = false;


    public interface ICamApiCommonListener<T> {
        void onSuccess(T bean);

        void onFail(int code, String msg);
    }

    /**
     * @param context
     * @param userID  默认 loginType 为0，登录方式为账号token登陆
     */
    public ICamApiUnit(Context context, String userID) {
        this.context = context;
        this.userID = userID;
        this.loginType = 0;
    }

    /**
     * @param context
     * @param userID
     * @param loginType 0 账号token登陆，1 网关号作为账号登陆
     */
    public ICamApiUnit(Context context, String userID, int loginType) {
        this.context = context;
        this.userID = userID;
        this.loginType = loginType;
    }

    private ICamLoginBean getiCamInfo() {
        if (iCamInfo == null) {
            iCamInfo = Preference.getPreferences().getICamInfo(this.userID);
        }
        return iCamInfo;
    }

    private String getAuth() {
        String auth = "";
        ICamLoginBean iCamLoginBean = Preference.getPreferences().getICamInfo(this.userID);
        if (iCamLoginBean != null) {
            auth = iCamLoginBean.auth;
        }
        return auth;
    }

    /**
     * 初始化服务器域名 & 登录
     *
     * @param userID V6账号userID
     */
    public void initHostAndLogin(final String userID, final ICamApiCommonListener<ICamLoginBean> listener) {
        doOkgoRequest(
                context,
                RouteApiType.V3_SERVER,
                null,
                new TaskResultListener() {
                    @Override
                    public void OnSuccess(RouteApiType routeApiType, String s) {
                        WLog.i("ICamApiUnit:initHost:" + s);
                        if (!TextUtils.isEmpty(s)) {
                            try {
                                JSONObject serverJson = JSON.parseObject(s);
                                if (serverJson.getInteger("status") == 1) {
                                    realHost = serverJson.getString("server");
                                    if (loginType == 1) {
                                        loginForGw(userID, listener);
                                    } else {
//                                        loginSSO(userID, listener);
                                    }
                                } else {
                                    listener.onFail(-1, "");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                listener.onFail(-1, "");
                            }
                        } else {
                            listener.onFail(-1, "");
                        }
                    }

                    @Override
                    public void OnFail(RouteApiType routeApiType, com.wulian.routelibrary.common.ErrorCode errorCode) {
                        WLog.i("ICamApiUnit:initHost:" + errorCode);
                    }
                });

    }


    /**
     * 使用账号体系登录爱看服务器
     *
     * @param userID   V6账号userID
     * @param listener
     */
    private void loginSSO(final String userID, final ICamApiCommonListener<ICamLoginBean> listener) {
        this.userID = userID;
        HashMap<String, String> v3SSOmap = RouteLibraryParams.V3SmartRoomSSOLogin("sn-" + userID, ApiConstant.getAppToken(), "rTD8mfP2jo5B6geJ9l1aHv3cANAEQR0w");
        String timestamp = v3SSOmap.get("timestamp");
        String tokenEncode = v3SSOmap.get("token");
        String tokenDecode = AuthCode.decode(tokenEncode, timestamp);
        WLog.i("ICamApiUnit:V3SmartRoomSSOLogin:ssomap:" + v3SSOmap.toString());
        doOkgoRequest(
                context,
                RouteApiType.V3_SMARTROOM_SSO_LOGIN,
                v3SSOmap,
                new TaskResultListener() {
                    @Override
                    public void OnSuccess(RouteApiType routeApiType, String s) {
                        WLog.i("ICamApiUnit:V3SmartRoomSSOLogin:" + s);
                        if (!TextUtils.isEmpty(s)) {
                            try {
                                iCamInfo = JSON.parseObject(s, ICamLoginBean.class);
                                if (iCamInfo.status == 1) {
                                    listener.onSuccess(iCamInfo);
                                } else {
                                    listener.onFail(-1, "");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                listener.onFail(-1, "");
                            }
                        } else {
                            listener.onFail(-1, "");
                        }
                    }

                    @Override
                    public void OnFail(RouteApiType routeApiType, com.wulian.routelibrary.common.ErrorCode errorCode) {
                        WLog.i("ICamApiUnit:V3SmartRoomSSOLoginOnFail:" + errorCode);
                        listener.onFail(errorCode.getErrorCode(), "");
                    }
                });
    }

    /**
     * 使用网关ID登录爱看服务器
     *
     * @param gwID     网关ID
     * @param listener
     */
    public void loginForGw(final String gwID, final ICamApiCommonListener<ICamLoginBean> listener) {
        this.userID = gwID;
        this.pwdMD5 = getGwLoginPw(gwID);
        doOkgoRequest(
                context,
                RouteApiType.V3_SMARTROOM_LOGIN,
                RouteLibraryParams.V3SmartRoomLogin("sn-" + userID, pwdMD5, "rTD8mfP2jo5B6geJ9l1aHv3cANAEQR0w"),
                new TaskResultListener() {
                    @Override
                    public void OnSuccess(RouteApiType routeApiType, String s) {
                        WLog.i("ICamApiUnit:V3SmartRoomLogin:" + s);
                        if (!TextUtils.isEmpty(s)) {
                            try {
                                org.json.JSONObject jsonObject = new org.json.JSONObject(s);
                                int status = jsonObject.getInt("status");
                                if (status == 1) {
                                    iCamInfo = JSON.parseObject(s, ICamLoginBean.class);
                                    Preference.getPreferences().saveICamInfo(iCamInfo, ICamApiUnit.this.userID);
                                    listener.onSuccess(iCamInfo);
                                } else {
                                    String error_code = jsonObject.getString("error_code");
                                    String error_msg = jsonObject.getString("error_msg");
                                    listener.onFail(Integer.parseInt(error_code), error_msg);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                listener.onFail(-1, "");
                            }
                        } else {
                            listener.onFail(-1, "");
                        }
                    }

                    @Override
                    public void OnFail(RouteApiType routeApiType, com.wulian.routelibrary.common.ErrorCode errorCode) {
                        WLog.i("ICamApiUnit:V3SmartRoomLoginOnFail:" + errorCode);
                        listener.onFail(errorCode.getErrorCode(), "");
                    }
                });
    }

    /**
     * 查询设备是否已经被绑定
     *
     * @param deviceID
     * @param listener
     */
    public void checkBind(final String deviceID, final ICamApiCommonListener<ICamCheckBindBean> listener) {
        doOkgoRequest(
                context,
                RouteApiType.V3_BIND_CHECK,
                RouteLibraryParams.V3BindCheck(getiCamInfo().auth, deviceID),
                new TaskResultListener() {
                    @Override
                    public void OnSuccess(RouteApiType routeApiType, String s) {
                        WLog.i("V3BindCheck:" + s);
                        ICamCheckBindBean cb = JSON.parseObject(s, ICamCheckBindBean.class);
                        listener.onSuccess(cb);
                    }

                    @Override
                    public void OnFail(RouteApiType routeApiType, com.wulian.routelibrary.common.ErrorCode errorCode) {
                        WLog.i("V3BindCheckOnFail:" + errorCode);
                        listener.onFail(errorCode.getErrorCode(), "");
                    }
                });
    }

    /**
     * 查询设备绑定结果
     *
     * @param deviceID
     * @param listener
     */
    public void checkBindResult(final String deviceID, final ICamApiCommonListener<ICamCheckBindResultBean> listener) {
        doOkgoRequest(
                context,
                RouteApiType.V3_BIND_RESULT,
                RouteLibraryParams.V3BindResult(getiCamInfo().auth, deviceID),
                new TaskResultListener() {
                    @Override
                    public void OnSuccess(RouteApiType routeApiType, String s) {
                        WLog.i("V3BindResult:" + s);
                        ICamCheckBindResultBean bean = JSON.parseObject(s, ICamCheckBindResultBean.class);
                        listener.onSuccess(bean);
                    }

                    @Override
                    public void OnFail(RouteApiType routeApiType, com.wulian.routelibrary.common.ErrorCode errorCode) {
                        WLog.i("V3BindResult:" + errorCode);
                        listener.onFail(errorCode.getErrorCode(), "");
                    }
                });
    }

    /**
     * 获取爱看设备列表
     *
     * @param listener
     */
    public void getICamListForH5(final ICamApiCommonListener<String> listener) {
        doOkgoRequest(
                context,
                RouteApiType.V3_USER_DEVICES,
                RouteLibraryParams.V3UserDevices(getiCamInfo().auth, "cmic", 1, 100),
                new TaskResultListener() {
                    @Override
                    public void OnSuccess(RouteApiType routeApiType, String s) {
                        WLog.i("V3UserDevices:" + s);
                        listener.onSuccess(s);
                    }

                    @Override
                    public void OnFail(RouteApiType routeApiType, com.wulian.routelibrary.common.ErrorCode errorCode) {
                        WLog.i("V3UserDevices:" + errorCode);
                        listener.onFail(errorCode.getErrorCode(), "");
                    }
                });
    }

    /**
     * 修改设备名称
     * <p>
     * //     * @param device_id
     *
     * @param listener
     */
    public void changeDeviceName(final String deviceID, final String deviceName, final ICamApiCommonListener<ICamRenameBean> listener) {
        doOkgoRequest(
                context,
                RouteApiType.V3_USER_DEVICE,
                RouteLibraryParams.V3UserDevice(getiCamInfo().auth, deviceID, deviceName, ""),
                new TaskResultListener() {
                    @Override
                    public void OnSuccess(RouteApiType routeApiType, String s) {
                        WLog.i("V3UserDevice:" + s);
                        ICamRenameBean bean = JSON.parseObject(s, ICamRenameBean.class);
                        listener.onSuccess(bean);
                    }

                    @Override
                    public void OnFail(RouteApiType routeApiType, com.wulian.routelibrary.common.ErrorCode errorCode) {
                        WLog.i("V3UserDevices:" + errorCode);
                        listener.onFail(errorCode.getErrorCode(), "");
                    }
                });
    }


    /**
     * 解绑设备
     *
     * @param deviceID
     * @param listener
     */
    public void unbindDevice(final String deviceID, final ICamApiCommonListener<ICamUnbindBean> listener) {
        doOkgoRequest(
                context,
                RouteApiType.V3_BIND_UNBIND,
                RouteLibraryParams.V3BindUnbind(getiCamInfo().auth, deviceID),
                new TaskResultListener() {
                    @Override
                    public void OnSuccess(RouteApiType routeApiType, String s) {
                        WLog.i("V3BindUnbind:" + s);
                        ICamUnbindBean bean = JSON.parseObject(s, ICamUnbindBean.class);
                        MainApplication.getApplication().getDeviceCache().remove(deviceID);
                        // 删除这个设备的widget
                        HomeWidgetManager.deleteWidget(deviceID);
                        EventBus.getDefault().post(new DeviceReportEvent(null));
                        listener.onSuccess(bean);
                    }

                    @Override
                    public void OnFail(RouteApiType routeApiType, com.wulian.routelibrary.common.ErrorCode errorCode) {
                        WLog.i("V3BindUnbind:" + errorCode);
                        listener.onFail(errorCode.getErrorCode(), "");
                    }
                });
    }


    /**
     * 唤醒设备
     *
     * @param deviceID
     * @param listener
     */
    public void awakeDevice(final String deviceID, final ICamApiCommonListener<ICamUnbindBean> listener) {
        doOkgoRequest(
                context,
                RouteApiType.V3_APP_BELL,
                RouteLibraryParams.V3AppBell(getiCamInfo().auth, deviceID, "awake"),
                new TaskResultListener() {
                    @Override
                    public void OnSuccess(RouteApiType routeApiType, String s) {
                        WLog.i("V3AppBell:" + s);
                        ICamUnbindBean bean = JSON.parseObject(s, ICamUnbindBean.class);
                        listener.onSuccess(bean);
                    }

                    @Override
                    public void OnFail(RouteApiType routeApiType, com.wulian.routelibrary.common.ErrorCode errorCode) {
                        WLog.i("V3AppBell:" + errorCode);
                        listener.onFail(errorCode.getErrorCode(), "");
                    }
                });
    }

    /**
     * 获取报警记录
     *
     * @param deviceID
     * @param startTime
     * @param endTime
     * @param page
     * @param size
     * @param listener
     */
    public void getAlarmRecord(final String deviceID, int startTime, int endTime, int page, int size, final ICamApiCommonListener<List<CateyeRecordEntity>> listener) {
        final List<CateyeRecordEntity> alarmList = new ArrayList<>();
        doOkgoRequest(context, RouteApiType.V3_NOTICE_MOVE_LIST, RouteLibraryParams.V3NoticeMoveList(getiCamInfo().auth, deviceID, startTime, endTime, page, size), new TaskResultListener() {
            @Override
            public void OnSuccess(RouteApiType routeApiType, String s) {
                JSONObject jsonObject = JSONObject.parseObject(s);
                String status = jsonObject.getString("status");
                List<CateyeRecordEntity> tempList = new ArrayList<>();
                if (TextUtils.equals(status, "1")) {
                    if (deviceID.startsWith("cmic08")) {
                        tempList = selectAlarmPic(CameraUtil.jsonToAlarmList(s));
                    } else {
                        tempList = CameraUtil.jsonToAlarmList(s);
                    }
                    alarmList.addAll(tempList);
                    isPicComplete = true;

                    if (isVideoComplete) {
                        isPicComplete = false;
                        isVideoComplete = false;
                        listener.onSuccess(alarmList);
                    }
                } else {
                    listener.onFail(-1, "");
                }
            }

            @Override
            public void OnFail(RouteApiType routeApiType, ErrorCode errorCode) {
                isPicComplete = false;
                listener.onFail(errorCode.getErrorCode(), "");
            }
        });

        doOkgoRequest(context, RouteApiType.V3_NOTICE_VIDEO_LIST, RouteLibraryParams.V3NoticeVideoList(getiCamInfo().auth, deviceID, startTime, endTime, page, size), new TaskResultListener() {
            @Override
            public void OnSuccess(RouteApiType routeApiType, String s) {
                JSONObject jsonObject = JSONObject.parseObject(s);
                String status = jsonObject.getString("status");
                if (TextUtils.equals(status, "1")) {
                    List<CateyeRecordEntity> tempList = CameraUtil.jsonToAlarmList(s);
                    alarmList.addAll(tempList);
                    isVideoComplete = true;

                    if (isPicComplete) {
                        isPicComplete = false;
                        isVideoComplete = false;
                        listener.onSuccess(alarmList);
                    }
                } else {
                    listener.onFail(-1, "");
                }
            }

            @Override
            public void OnFail(RouteApiType routeApiType, ErrorCode errorCode) {
                isVideoComplete = false;
                listener.onFail(errorCode.getErrorCode(), "");
            }
        });
    }

    //从请求到的包含访问记录和报警记录的数据中筛选出访客记录
    private List<CateyeRecordEntity> selectAlarmPic(List<CateyeRecordEntity> list) {
        List<CateyeRecordEntity> tempList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).url.endsWith("PIR.jpg")) {
                tempList.add(list.get(i));
            }
        }
        return tempList;
    }

    /**
     * 获取图片
     *
     * @param url
     * @param listener
     */
    public void getPicture(final String url, final ICamApiCommonListener<String> listener) {
        String auth = getAuth();
        if (!StringUtil.isNullOrEmpty(auth)) {
            doOkgoRequest(context, RouteApiType.V3_TOKEN_DOWNLOAD_PIC, RouteLibraryParams.V3TokenDownloadPic(auth, url), new TaskResultListener() {
                @Override
                public void OnSuccess(RouteApiType routeApiType, String s) {
                    if (routeApiType == RouteApiType.V3_TOKEN_DOWNLOAD_PIC) {
                        try {
                            JSONObject jsonObject = JSON.parseObject(s);
                            JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                            if (jsonObject.getInteger("status") == 1) {
                                String bucketName = jsonObjectData.getString("Bucket");
                                String accessKeyId = jsonObjectData.getString("AccessKeyId");
                                String accessKeySecret = jsonObjectData.getString("AccessKeySecret");
                                String securityToken = jsonObjectData.getString("SecurityToken");
                                String region = jsonObjectData.getString("Region");

                                String endPoint = region + ".aliyuncs.com/";
                                OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(
                                        accessKeyId, accessKeySecret,
                                        securityToken);
                                mOSSClient = new OSSClient(context, endPoint, credentialProvider);
                                String URL = mOSSClient.presignConstrainedObjectURL(bucketName, url, 3600);
                                WLog.i("getPictureUrl", URL);
                                listener.onSuccess(URL);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            listener.onFail(-1, e.toString());
                        }
                    }
                }

                @Override
                public void OnFail(RouteApiType routeApiType, ErrorCode errorCode) {
                    listener.onFail(errorCode.getErrorCode(), "");
                }
            });
        } else {
            listener.onFail(-1, "auth获取失败!");
        }

    }

    /**
     * 获取视频url
     *
     * @param picUrl
     * @param videoUrl
     * @param listener
     */
    public void getVideo(final String picUrl, final String videoUrl, String id, String sipDomain, final ICamApiCommonListener<CateyeVideoEntity> listener) {
        String auth = getAuth();
        if (!StringUtil.isNullOrEmpty(auth)) {
            doOkgoRequest(context, RouteApiType.V3_TOKEN_DOWNLOAD_VIDEO, RouteLibraryParams.V3TokenDownloadVideo(Preference.getPreferences().getICamInfo(this.userID).auth, id, sipDomain), new TaskResultListener() {
                @Override
                public void OnSuccess(RouteApiType routeApiType, String s) {
                    if (routeApiType == RouteApiType.V3_TOKEN_DOWNLOAD_VIDEO) {
                        try {
                            String fileName = getFileName(videoUrl);
                            JSONObject jsonObject = JSON.parseObject(s);
                            JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                            if (jsonObject.getInteger("status") == 1) {
                                String bucketName = jsonObjectData.getString("Bucket");
                                String accessKeyId = jsonObjectData.getString("AccessKeyId");
                                String accessKeySecret = jsonObjectData.getString("AccessKeySecret");
                                String securityToken = jsonObjectData.getString("SecurityToken");
                                String region = jsonObjectData.getString("Region");

                                String endPoint = region + ".aliyuncs.com/";
                                OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(
                                        accessKeyId, accessKeySecret,
                                        securityToken);
                                mOSSClient = new OSSClient(context, endPoint, credentialProvider);
                                String pURL = mOSSClient.presignConstrainedObjectURL(bucketName, picUrl, 3600);//下载视频时下载对应图片url
                                String vUrl = mOSSClient.presignConstrainedObjectURL(bucketName, videoUrl, 3600);//下载视频的url

                                CateyeVideoEntity cateyeVideoEntity = new CateyeVideoEntity();
                                cateyeVideoEntity.picUrl = pURL;
                                cateyeVideoEntity.videoUrl = vUrl;
//                            downloadVideo(vUrl, fileName);
                                listener.onSuccess(cateyeVideoEntity);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            listener.onFail(-1, e.toString());
                        }
                    }
                }

                @Override
                public void OnFail(RouteApiType routeApiType, ErrorCode errorCode) {
                    listener.onFail(errorCode.getErrorCode(), "");
                }
            });
        } else {
            listener.onFail(-1, "auth值获取失败!");
        }
    }

    private String getFileName(String url) {
        String data[] = new String[2];
        data = url.split("/");
        String name = data[2];
        WLog.i("FileName", name);
        return name;
    }


    /**
     * 获取设备回看
     *
     * @param deviceID
     * @param listener
     */
    public void getReplayData(final String deviceID, final String sdomain, final ICamApiCommonListener<ICamReplayBean> listener) {
        doOkgoRequest(
                context,
                RouteApiType.V3_TOKEN_DOWNLOAD_REPLAY,
                RouteLibraryParams.V3TokenDownLoad(getiCamInfo().auth, sdomain, deviceID),
                new TaskResultListener() {
                    @Override
                    public void OnSuccess(RouteApiType routeApiType, String s) {
                        WLog.i("V3_TOKEN_DOWNLOAD_REPLAY:" + s);
                        ICamReplayBean bean = JSON.parseObject(s, ICamReplayBean.class);
                        listener.onSuccess(bean);
                    }

                    @Override
                    public void OnFail(RouteApiType routeApiType, com.wulian.routelibrary.common.ErrorCode errorCode) {
                        WLog.i("V3_TOKEN_DOWNLOAD_REPLAY fail:" + errorCode);
                        listener.onFail(errorCode.getErrorCode(), "");
                    }
                });
    }

    /**
     * 使用okgo请求接口
     */
    public void doOkgoRequest(final Context context, final RouteApiType type,
                              final HashMap<String, String> params, final TaskResultListener listener) {

        String serverURL = realHost;
        String URL = HTTPS_PROTOCOL + serverURL + type.getmURL();
        final ICamApiCommonListener<ICamLoginBean> onLoginSuccessListener = new ICamApiCommonListener<ICamLoginBean>() {
            @Override
            public void onSuccess(ICamLoginBean bean) {
                if (params != null) {
                    params.put("auth", bean.auth);
                }
                doOkgoRequest(context, type, params, listener);
            }

            @Override
            public void onFail(int code, String msg) {
                listener.OnFail(type, ErrorCode.INVALID_TOKEN);
            }
        };

        if (type.getRequestType() == RequestType.HTTP_GET) {
            OkGo.get(URL)
                    .tag(this)
                    .headers(HttpHeaders.HEAD_KEY_USER_AGENT, getPhoneInformation())
                    .params(params)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
//                            if (isAuthTimeout(s)) {
//                                if (loginType == 1) {
//                                    loginForGw(userID, onLoginSuccessListener);
//                                } else {
//                                    loginSSO(userID, onLoginSuccessListener);
//                                }
//                            } else {
                                listener.OnSuccess(type, s);
//                            }
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                            listener.OnFail(type, ErrorCode.NETWORK_ERROR);
                        }
                    });
        } else if (type.getRequestType() == RequestType.HTTP_POST) {
            org.json.JSONObject jsonObject = null;
            if (params != null) {
                jsonObject = new org.json.JSONObject(params);
            } else {
                jsonObject = new org.json.JSONObject();
            }
            OkGo.post(URL)
                    .tag(this)
                    .headers(HttpHeaders.HEAD_KEY_USER_AGENT, getPhoneInformation())
                    .upJson(jsonObject)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
//                            if (isAuthTimeout(s)) {
//                                if (loginType == 1) {
//                                    loginForGw(userID, onLoginSuccessListener);
//                                } else {
//                                    loginSSO(userID, onLoginSuccessListener);
//                                }
//                            } else {
                                listener.OnSuccess(type, s);
//                            }
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                            listener.OnFail(type, ErrorCode.NETWORK_ERROR);
                        }
                    });
        }
    }

    /**
     * 判断返回的json是否为auth过期
     *
     * @param json
     * @return
     */
    private boolean isAuthTimeout(String json) {
        try {
            org.json.JSONObject jsonObject = new org.json.JSONObject(json);
            if ("unauthorized.".equals(jsonObject.optString("error_msg"))
                    && 401 == jsonObject.optInt("error_code", 0)) {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void getVersionCheck(String search, String versionCode, final ICamApiCommonListener<ICamVersionInfoBean> listener) {
        doOkgoRequest(context, RouteApiType.V3_VERSION_STABLE, RouteLibraryParams.V3VersionCheck(search, versionCode), new TaskResultListener() {
            @Override
            public void OnSuccess(RouteApiType apiType, String json) {
                ICamVersionInfoBean versionInfoBean = null;
                if (!StringUtil.isNullOrEmpty(json)) {
                    versionInfoBean = JSON.parseObject(json, ICamVersionInfoBean.class);
                }
                listener.onSuccess(versionInfoBean);
            }

            @Override
            public void OnFail(RouteApiType apiType, ErrorCode errorCode) {
                listener.onFail(errorCode.getErrorCode(), "");
            }
        });
    }

    public static String getGwLoginPw(String gwID) {
        String pwmd5 = "";
        if (!StringUtil.isNullOrEmpty(gwID)) {
//            String gwPw= Preference.getPreferences().getGatewayPassword(gwID);
//            String gwPw = "1034E3";//Test code
            if (!StringUtil.isNullOrEmpty(gwID) && gwID.length() >= 6) {
                String prePw = gwID.substring(gwID.length() - 6);
                pwmd5 = MD5Util.encrypt(prePw).toLowerCase().substring(0, 16);
            }
        }
        return pwmd5;
    }

    public static String getUserSipPwd(String userID) {
        String pwdMD5 = MD5Util.encrypt(userID);
        if (pwdMD5.length() > 16) {
            pwdMD5 = pwdMD5.substring(0, 16);
        }
        return pwdMD5;
    }

    /**
     * @return
     * @MethodName getPhoneInformation
     * @Function 获取手机信息
     */
    public static String getPhoneInformation() {
        if (user_agent == null) {
            String version = "1.0.0";
            try {
                PackageInfo info = MainApplication.getApplication().getPackageManager().getPackageInfo(
                        MainApplication.getApplication().getPackageName(), 0);
                // 当前应用的版本名称
                version = info.versionName;
            } catch (Exception e) {
                e.printStackTrace();
            }
            user_agent = USER_AGENT + "/" + version + " "
                    + "android" + "/" + android.os.Build.VERSION.RELEASE + " mms/1.0.1";
        }
        return user_agent;
    }
}
