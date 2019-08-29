package cc.wulian.smarthomev6.support.core.apiunit;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCustomSignerCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.common.utils.OSSUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.CateyeVideoEntity;
import cc.wulian.smarthomev6.support.core.apiunit.bean.ResponseBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.ShareNoticeBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamAlarmUrlBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamAliKeyBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamGetSipInfoBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamOssInfoBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.IcamBindRelationBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.IcamCloudCheckBindBean;
import cc.wulian.smarthomev6.support.core.cipher.CipherUtil;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.CameraUtil;
import cc.wulian.smarthomev6.support.utils.FileUtil;
import cc.wulian.smarthomev6.support.utils.WLog;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者: chao
 * 时间: 2017/7/12
 * 描述: 摄像头sdk相关接口（新）
 * 联系方式: 805901025@qq.com
 */

public class ICamCloudApiUnit {
    private Context context;
    private OSS mOSSClient;

    private boolean isPicComplete = false;
    private boolean isVideoComplete = false;

    public ICamCloudApiUnit(Context context) {
        this.context = context;
    }

    public interface IcamApiCommonListener<T> {
        void onSuccess(T bean);

        void onFail(int code, String msg);
    }

    /**
     * 校验设备是否绑定
     *
     * @param deviceId         设备ID     (网络智能锁：网关id)
     * @param deviceType       设备类型
     * @param targetDeviceId   目标设备（网络智能锁：锁id）
     * @param compoundDeviceId 复合设备ID（网络智能锁：猫眼id）
     */
    public void doCheckBind(String deviceId, String deviceType, String targetDeviceId, String compoundDeviceId, final IcamApiCommonListener<IcamCloudCheckBindBean> listener) {
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson.put("token", ApiConstant.getAppToken());
            msgContentJson.put("deviceId", deviceId);
            msgContentJson.put("deviceType", deviceType);
            if (!TextUtils.isEmpty(targetDeviceId)) {
                msgContentJson.put("targetDeviceId", targetDeviceId);
            }
            if (!TextUtils.isEmpty(compoundDeviceId)) {
                msgContentJson.put("compoundDeviceId", compoundDeviceId);
            }
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/users-devices-verify";
        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<IcamCloudCheckBindBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<IcamCloudCheckBindBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 获取绑定关系码
     *
     * @param deviceId       设备ID
     * @param deviceType     设备类型
     * @param targetDeviceId 目标设备（例：网络锁下摄像头）
     */
    public void doGetBoundRelationCode(String deviceId, String deviceType, String targetDeviceId, final IcamApiCommonListener<IcamBindRelationBean> listener) {
        HashMap<String, String> params = new HashMap<>();
        params.put("token", ApiConstant.getAppToken());
        params.put("deviceType", deviceType);
        if (!TextUtils.isEmpty(targetDeviceId)) {
            params.put("targetDeviceId", targetDeviceId);
        }

        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/devices/" + deviceId + "/bound-relation-code";
        OkGo.get(url)
                .tag(this)
                .params(params)
                .execute(new EncryptCallBack<ResponseBean<IcamBindRelationBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<IcamBindRelationBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 获取sip信息
     *
     * @param deviceId 设备ID
     */
    public void doGetSipInfo(final String deviceId, final boolean isAccount, final IcamApiCommonListener<ICamGetSipInfoBean> listener) {
        if (TextUtils.isEmpty(deviceId)) {
            return;
        }
        ICamGetSipInfoBean iCamGetSipInfoBean = null;
        if (isAccount) {
            iCamGetSipInfoBean = Preference.getPreferences().getSipInfo(ApiConstant.getUserID(), deviceId);
            WLog.i("icamProcess", "获取缓存sip信息--->爱看摄像机");
        } else {
            iCamGetSipInfoBean = Preference.getPreferences().getSipInfo(Preference.getPreferences().getCurrentGatewayID(), deviceId);
            WLog.i("icamProcess", "获取缓存sip信息--->可视门锁");
        }

        if (iCamGetSipInfoBean != null && !TextUtils.isEmpty(iCamGetSipInfoBean.deviceDomain)
                && !TextUtils.isEmpty(iCamGetSipInfoBean.sipDomain)
                && !TextUtils.isEmpty(iCamGetSipInfoBean.suid)
                && !TextUtils.isEmpty(iCamGetSipInfoBean.spassword)) {
            listener.onSuccess(iCamGetSipInfoBean);
        } else {
            HashMap<String, String> params = new HashMap<>();
            params.put("token", ApiConstant.getAppToken());
            params.put("deviceId", deviceId);

            String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/sip";
            OkGo.get(url)
                    .tag(this)
                    .params(params)
                    .execute(new EncryptCallBack<ResponseBean<ICamGetSipInfoBean>>() {
                        @Override
                        public void onSuccess(ResponseBean<ICamGetSipInfoBean> responseBean, Call call, Response response) {
                            if (responseBean.isSuccess()) {
                                WLog.i("process", "调V6接口获取sip信息");
                                if (isAccount) {
                                    Preference.getPreferences().saveSipInfo(responseBean.data, ApiConstant.getUserID(), deviceId);
                                } else {
                                    Preference.getPreferences().saveSipInfo(responseBean.data, Preference.getPreferences().getCurrentGatewayID(), deviceId);
                                }

                                listener.onSuccess(responseBean.data);
                            } else {
                                listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                            }
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            String msg = context.getString(R.string.Service_Error);
                            listener.onFail(-1, msg);
                            super.onError(call, response, e);
                        }
                    });
        }
    }

    /**
     * 修改设备名称
     *
     * @param deviceId
     * @param deviceName
     */
    public void doChangeDeviceName(final String deviceId, final String deviceName, String channelId, final IcamApiCommonListener<Object> listener) {
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson.put("token", ApiConstant.getAppToken());
            msgContentJson.put("deviceId", deviceId);
            msgContentJson.put("name", deviceName);
            if (!TextUtils.isEmpty(channelId)) {
                msgContentJson.put("channelId", channelId);
            }
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/devices/info";
        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> bean, Call call, Response response) {
                        if (bean.isSuccess()) {
                            listener.onSuccess(null);
                        } else {
                            listener.onFail(bean.getResultCode(), bean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 唤醒设备
     *
     * @param deviceId 设备ID
     */
    public void doAwakeDevice(String deviceId, final IcamApiCommonListener<Object> listener) {
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson.put("token", ApiConstant.getAppToken());
            msgContentJson.put("deviceId", deviceId);
            msgContentJson.put("action", 0);    //awake/sleep
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/ctrl-device";
        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {

                    @Override
                    public void onSuccess(ResponseBean<Object> bean, Call call, Response response) {
                        if (bean.isSuccess()) {
                            listener.onSuccess(null);
                        } else {
                            listener.onFail(bean.getResultCode(), bean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }


    /**
     * 获取报警信息
     *
     * @param deviceId  设备ID
     * @param startTime
     * @param endTime
     * @param page
     * @param size
     * @param type      类型：0仅报警，1报警、门铃
     */
    public void doGetAlarmInfo(String cameraType, String deviceId, long startTime, long endTime, int page, int size, int type, final IcamApiCommonListener<List<ICamAlarmUrlBean>> listener) {
        final List<ICamAlarmUrlBean> alarmList = new ArrayList<>();
        doGetAlarmPicInfo(cameraType, deviceId, startTime, endTime, page, size, type, new IcamApiCommonListener<List<ICamAlarmUrlBean>>() {
            @Override
            public void onSuccess(List<ICamAlarmUrlBean> bean) {
                alarmList.addAll(bean);
                isPicComplete = true;

                if (isVideoComplete) {
                    isPicComplete = false;
                    isVideoComplete = false;
                    listener.onSuccess(alarmList);
                }
            }

            @Override
            public void onFail(int code, String msg) {
                isPicComplete = false;
                listener.onFail(code, msg);
            }
        });

        doGetAlarmVideoInfo(deviceId, startTime, endTime, page, size, new IcamApiCommonListener<List<ICamAlarmUrlBean>>() {
            @Override
            public void onSuccess(List<ICamAlarmUrlBean> bean) {
                alarmList.addAll(bean);
                isVideoComplete = true;

                if (isPicComplete) {
                    isPicComplete = false;
                    isVideoComplete = false;
                    listener.onSuccess(alarmList);
                }
            }

            @Override
            public void onFail(int code, String msg) {
                isVideoComplete = false;
                listener.onFail(code, msg);
            }
        });
    }

    //从请求到的包含访问记录和报警记录的数据中筛选出报警记录
    private List<ICamAlarmUrlBean> selectAlarmPic(List<ICamAlarmUrlBean> list) {
        List<ICamAlarmUrlBean> tempList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).url.endsWith("PIR.jpg")) {
                tempList.add(list.get(i));
            }
        }
        return tempList;
    }

    //从请求到的包含访问记录和报警记录的数据中筛选出访客记录
    private List<ICamAlarmUrlBean> selectRingPic(List<ICamAlarmUrlBean> list) {
        List<ICamAlarmUrlBean> tempList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).url.endsWith("Ring.jpg")) {
                tempList.add(list.get(i));
            }
        }
        return tempList;
    }

    /**
     * 获取报警信息（图片）
     *
     * @param deviceId  设备ID
     * @param startTime
     * @param endTime
     * @param page
     * @param size
     * @param type      类型：0仅报警，1报警、门铃
     */
    private void doGetAlarmPicInfo(final String cameraType, final String deviceId, long startTime, long endTime, int page, int size, final int type, final IcamApiCommonListener<List<ICamAlarmUrlBean>> listener) {
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson.put("token", ApiConstant.getAppToken());
            msgContentJson.put("deviceId", deviceId);
            msgContentJson.put("startTime", startTime);
            msgContentJson.put("endTime", endTime);
            msgContentJson.put("page", page);
            msgContentJson.put("size", size);
            msgContentJson.put("type", 0);
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/devices/alarm-Details";
        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<List<ICamAlarmUrlBean>>>() {

                    @Override
                    public void onSuccess(ResponseBean<List<ICamAlarmUrlBean>> bean, Call call, Response response) {
                        if (bean.isSuccess()) {
                            List<ICamAlarmUrlBean> tempList = null;
                            if ("CMICA1".equals(cameraType) && type == 0) {
                                tempList = selectAlarmPic(bean.data);
                            } else {
                                tempList = bean.data;
                            }
                            listener.onSuccess(tempList);
                        } else {
                            listener.onFail(bean.getResultCode(), bean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 获取报警信息（视频）
     *
     * @param deviceId  设备ID
     * @param startTime
     * @param endTime
     * @param page
     * @param size
     */
    private void doGetAlarmVideoInfo(String deviceId, long startTime, long endTime, int page, int size, final IcamApiCommonListener<List<ICamAlarmUrlBean>> listener) {
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson.put("token", ApiConstant.getAppToken());
            msgContentJson.put("deviceId", deviceId);
            msgContentJson.put("startTime", startTime);
            msgContentJson.put("endTime", endTime);
            msgContentJson.put("page", page);
            msgContentJson.put("size", size);
            msgContentJson.put("type", 1);
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/devices/alarm-Details";
        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<List<ICamAlarmUrlBean>>>() {

                    @Override
                    public void onSuccess(ResponseBean<List<ICamAlarmUrlBean>> bean, Call call, Response response) {
                        if (bean.isSuccess()) {
                            listener.onSuccess(bean.data);
                        } else {
                            listener.onFail(bean.getResultCode(), bean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 获取报警信息（图片）
     *
     * @param deviceId  设备ID
     * @param startTime
     * @param endTime
     * @param page
     * @param size
     */
    public void doGetRingPicInfo(final String deviceId, long startTime, long endTime, int page, int size, final IcamApiCommonListener<List<ICamAlarmUrlBean>> listener) {
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson.put("token", ApiConstant.getAppToken());
            msgContentJson.put("deviceId", deviceId);
            msgContentJson.put("startTime", startTime);
            msgContentJson.put("endTime", endTime);
            msgContentJson.put("page", page);
            msgContentJson.put("size", size);
            msgContentJson.put("type", 0);
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/devices/alarm-Details";
        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<List<ICamAlarmUrlBean>>>() {

                    @Override
                    public void onSuccess(ResponseBean<List<ICamAlarmUrlBean>> bean, Call call, Response response) {
                        if (bean.isSuccess()) {
                            List<ICamAlarmUrlBean> tempList = selectRingPic(bean.data);
                            listener.onSuccess(tempList);
                        } else {
                            listener.onFail(bean.getResultCode(), bean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 获取图片
     *
     * @param key
     * @param deviceId
     * @param time
     */
    public void doGetPic(final String deviceId, final long time, final String key, final String bucket, final String region, final IcamApiCommonListener<File> listener) {
        String baseURL = FileUtil.getICamImgPath();
        File file = new File(baseURL + "/" + deviceId + "/" + time + ".jpg");
        if (file.exists()) {
            listener.onSuccess(file);
            return;
        }

        final String finalBaseURL = baseURL;
        new DataApiUnit(context).doGetIcamOssInfoFromCache(new DataApiUnit.DataApiCommonListener<ICamOssInfoBean>() {
            @Override
            public void onSuccess(final ICamOssInfoBean bean) {
                try {
                    String endPoint = region + ".aliyuncs.com/";
                    OSSCredentialProvider credentialProvider = new OSSCustomSignerCredentialProvider() {
                        @Override
                        public String signContent(String content) {
                            return OSSUtils.sign(bean.AccessKeyId, bean.AccessKeySecret, content);
                        }
                    };
                    mOSSClient = new OSSClient(context, endPoint, credentialProvider);
                    String URL = mOSSClient.presignConstrainedObjectURL(bucket, key, 3600);
                    String path = finalBaseURL + "/" + deviceId;
                    OkGo.get(URL)
                            .execute(new FileCallback(path, time + ".jpg") {
                                @Override
                                public void onSuccess(File file, Call call, Response response) {
                                    listener.onSuccess(file);
                                }

                                @Override
                                public void onError(Call call, Response response, Exception e) {
                                    super.onError(call, response, e);
                                    listener.onFail(-1, e.toString());
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onFail(-1, e.toString());
                }
            }

            @Override
            public void onFail(int code, String msg) {
                listener.onFail(code - 1, msg);
            }
        });
    }

    private final String TAG = ICamCloudApiUnit.class.getSimpleName();

    /**
     * 获取视频
     *
     * @param key
     * @param deviceId
     * @param time
     */
    public void doGetPicVideo(final String deviceId, final long time, final String key, final String bucket, final String region, final IcamApiCommonListener<File> listener) {
        final String finalBaseURL = FileUtil.getICamVideoPath();

        File file = new File(finalBaseURL + "/" + deviceId + "/" + time + ".jpg");
        if (file.exists()) {
            listener.onSuccess(file);
            return;
        }

        new DataApiUnit(context).doGetIcamOssInfoFromCache(new DataApiUnit.DataApiCommonListener<ICamOssInfoBean>() {
            @Override
            public void onSuccess(final ICamOssInfoBean bean) {
                try {
                    String endPoint = region + ".aliyuncs.com/";

                    WLog.d(TAG, "\r\nRegion=" + bean.AccessKeyId
                            + "\r\nAccessKeySecret=" + bean.AccessKeySecret
                            + "\r\nBucket=" + bean.Bucket);
                    OSSCredentialProvider credentialProvider = new OSSCustomSignerCredentialProvider() {
                        @Override
                        public String signContent(String content) {
                            return OSSUtils.sign(bean.AccessKeyId, bean.AccessKeySecret, content);
                        }
                    };
                    mOSSClient = new OSSClient(context, endPoint, credentialProvider);
                    String URL = mOSSClient.presignConstrainedObjectURL(bucket, key, 3600);
                    String path = finalBaseURL + "/" + deviceId;
                    OkGo.get(URL)
                            .execute(new FileCallback(path, time + ".mp4") {
                                @Override
                                public void onSuccess(File file, Call call, Response response) {
                                    listener.onSuccess(file);
                                }

                                @Override
                                public void onError(Call call, Response response, Exception e) {
                                    super.onError(call, response, e);
                                    listener.onFail(-1, e.toString());
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onFail(-1, e.toString());
                }
            }

            @Override
            public void onFail(int code, String msg) {
                listener.onFail(code, msg);
            }
        });
    }

    /**
     * 获取来电时图片
     *
     * @param key
     */
    public void doGetRingPic(final String key, final String bucket, final String region, final IcamApiCommonListener<String> listener) {
        new DataApiUnit(context).doGetIcamOssInfoFromCache(new DataApiUnit.DataApiCommonListener<ICamOssInfoBean>() {
            @Override
            public void onSuccess(final ICamOssInfoBean bean) {
                try {
                    String endPoint = region + ".aliyuncs.com/";
                    WLog.d(TAG, "bean.data.AccessKeyId=" + bean.AccessKeyId
                            + " bean.data.AccessKeySecret=" + bean.AccessKeySecret);
                    OSSCredentialProvider credentialProvider = new OSSCustomSignerCredentialProvider() {
                        @Override
                        public String signContent(String content) {
                            return OSSUtils.sign(bean.AccessKeyId, bean.AccessKeySecret, content);
                        }
                    };
                    mOSSClient = new OSSClient(context, endPoint, credentialProvider);
                    String URL = mOSSClient.presignConstrainedObjectURL(bucket, key, 3600);
                    listener.onSuccess(URL);
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onFail(-1, e.toString());
                }
            }

            @Override
            public void onFail(int code, String msg) {
                listener.onFail(code, msg);
            }
        });
    }

    /**
     * 获取视频
     *
     * @param deviceId
     * @param sdomain
     */
    public void doGetVideo(final String deviceId, final String picUrl, final String videoUrl, String sdomain, final long time, final IcamApiCommonListener<CateyeVideoEntity> listener) {
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson.put("token", ApiConstant.getAppToken());
            msgContentJson.put("type", 2);
            msgContentJson.put("deviceId", deviceId);
            msgContentJson.put("sdomain", sdomain);
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        final String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/devices/resource-token";
        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<ICamAliKeyBean>>() {

                    @Override
                    public void onSuccess(ResponseBean<ICamAliKeyBean> bean, Call call, Response response) {
                        if (bean.isSuccess()) {
                            try {
                                String endPoint = bean.data.Region + ".aliyuncs.com/";
                                OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(
                                        bean.data.AccessKeyId, bean.data.AccessKeySecret,
                                        bean.data.SecurityToken);
                                mOSSClient = new OSSClient(context, endPoint, credentialProvider);
                                String pURL = mOSSClient.presignConstrainedObjectURL(bean.data.Bucket, picUrl, 3600);//下载视频时下载对应图片url
                                String vUrl = mOSSClient.presignConstrainedObjectURL(bean.data.Bucket, videoUrl, 3600);//下载视频的url
                                final CateyeVideoEntity cateyeVideoEntity = new CateyeVideoEntity();
                                cateyeVideoEntity.picUrl = pURL;
                                cateyeVideoEntity.videoUrl = vUrl;

                                File file = new File(FileUtil.getICamVideoPath() + "/" + deviceId + "/" + time + ".jpg");
                                if (file.exists()) {
                                    cateyeVideoEntity.picUrl = file.getAbsolutePath().trim();
                                    listener.onSuccess(cateyeVideoEntity);
                                } else {
                                    String path = FileUtil.getICamVideoPath() + "/" + deviceId;
                                    OkGo.get(pURL)
                                            .execute(new FileCallback(path, time + ".jpg") {
                                                @Override
                                                public void onSuccess(File file, Call call, Response response) {
                                                    listener.onSuccess(cateyeVideoEntity);
                                                }
                                            });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                listener.onFail(-1, e.toString());
                            }
                        } else {
                            listener.onFail(bean.getResultCode(), bean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 获取设备回看阿里云密钥
     *
     * @param deviceId
     * @param sdomain
     */
    public void doGetReplay(String deviceId, String sdomain, final IcamApiCommonListener<ICamAliKeyBean> listener) {
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson.put("token", ApiConstant.getAppToken());
            msgContentJson.put("type", 3);
            msgContentJson.put("deviceId", deviceId);
            msgContentJson.put("sdomain", sdomain);
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        final String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/devices/resource-token";
        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<ICamAliKeyBean>>() {

                    @Override
                    public void onSuccess(ResponseBean<ICamAliKeyBean> bean, Call call, Response response) {
                        if (bean.isSuccess()) {
                            listener.onSuccess(bean.data);
                        } else {
                            listener.onFail(bean.getResultCode(), bean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 获取设备版本信息
     *
     * @param search  应用/固件别名
     * @param version 内部版本号
     * @param type    1为获取稳定版本，0为获取开发版本
     */
    public void doGetVersionCheck(String search, String version, String type, final IcamApiCommonListener<Object> listener) {
        HashMap<String, String> params = new HashMap<>();
        params.put("search", search);
        params.put("type", type);
        if (!TextUtils.isEmpty(version)) {
            params.put("version", version);
        }

        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/devices/version";
        OkGo.get(url)
                .tag(this)
                .params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {

                    }
                });
//                .execute(new JsonCallback<ResponseBean<Object>>() {
//                    @Override
//                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
////                        if (responseBean.isSuccess()) {
////                            listener.onSuccess(responseBean.data);
////                        } else {
////                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
////                        }
//                        listener.onSuccess(responseBean);
//                    }
//
//                    @Override
//                    public void onError(Call call, Response response, Exception e) {
//                        String msg = context.getString(R.string.Service_Error);
//                        listener.onFail(-1, msg);
//                        super.onError(call, response, e);
//                    }
//                });
    }

    /**
     * 图片/视频分享到微信等第三方接口
     * 通过相对地址获取绝对地址
     *
     * @param pic   图片地址
     * @param video 视频地址
     */
    public void doPostShare(String deviceId, String pic, String video, final IcamApiCommonListener<ShareNoticeBean> listener) {

        String url = String.format(ApiConstant.URL_MEDIA_SHARE, ApiConstant.getUserID());
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = null;
        try {
            msgContentJson.put("token", ApiConstant.getAppToken());
            msgContentJson.put("deviceId", deviceId);
            msgContentJson.put("pic", pic);
            msgContentJson.put("video", video);
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<ShareNoticeBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<ShareNoticeBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(-1, responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }


}
