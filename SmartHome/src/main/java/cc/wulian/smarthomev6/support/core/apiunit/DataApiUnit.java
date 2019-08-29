package cc.wulian.smarthomev6.support.core.apiunit;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.device_23.bean.ReportKeyBean;
import cc.wulian.smarthomev6.main.device.device_23.bean.TestCodeBean;
import cc.wulian.smarthomev6.main.device.device_if02.bean.AirCodeLibsBean;
import cc.wulian.smarthomev6.main.device.device_if02.bean.CodeLibBean;
import cc.wulian.smarthomev6.main.device.device_if02.bean.ControllerBean;
import cc.wulian.smarthomev6.main.device.device_if02.bean.ControllerBlocksBean;
import cc.wulian.smarthomev6.main.device.device_if02.bean.ControllerListBean;
import cc.wulian.smarthomev6.main.device.device_if02.bean.CustomKeyBean;
import cc.wulian.smarthomev6.main.device.device_if02.bean.MatchNextCmpKey;
import cc.wulian.smarthomev6.main.device.dreamFlower.bean.CityBean;
import cc.wulian.smarthomev6.main.device.dreamFlower.bean.ProvinceBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.AllMessageBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.BrandBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.CodeLibraryBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.CylincamPositionBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.HouseKeeperLogBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.ImageUrlBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.KeyValueListBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.LcAlarmBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.LiveStreamAddressBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.LiveStreamInfoBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.MessageBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.MessageCountBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.ResponseBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.SafeDogGetDevicesBeanList;
import cc.wulian.smarthomev6.support.core.apiunit.bean.SafeDogHomeDataBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.SafeDogIncidentsBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.SafeDogIncidentsBeanList;
import cc.wulian.smarthomev6.support.core.apiunit.bean.SafeDogProtectDeviceBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.SafeDogScheduleBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.SceneLogBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.UserPushAudienceBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.WidgetBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.WulianNoticeBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamOssInfoBean;
import cc.wulian.smarthomev6.support.core.cipher.CipherUtil;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.LanguageUtil;
import cc.wulian.smarthomev6.support.utils.WLog;
import cc.wulian.wrecord.WRecord;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者: mamengchao
 * 时间: 2017/4/6 0006
 * 描述: 资源查询类接口
 * 联系方式: 805901025@qq.com
 */

public class DataApiUnit {
    private Context context;

    public DataApiUnit(Context context) {
        this.context = context;
    }

    public interface DataApiCommonListener<T> {
        void onSuccess(T bean);

        void onFail(int code, String msg);
    }

    /**
     * 获取用户网关下子设备告警未读数
     *
     * @param deviceId 设备id
     * @param childId  子设备id
     * @param type     1 查询 2 清除
     */
    public void deGetAlarmCount(String deviceId, String childId, String type, final DataApiCommonListener listener) {
        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/devices/alarms";
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson.put("token", ApiConstant.getAppToken());
            msgContentJson.put("deviceId", deviceId);
            msgContentJson.put("childDeviceId", childId);
            msgContentJson.put("type", type);
            msgContentJson.put("msgType", 2);
            if ("3".equals(type)) {
                msgContentJson.put("isNeedRecord", 1);
            }
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<MessageCountBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<MessageCountBean> responseBean, Call call, Response response) {
                        WLog.e("deGetAlarmCount:onSuccess", response.toString());
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
                        WLog.e("deGetAlarmCount", "onError: " + response, e);
                    }
                });
    }

    /**
     * 获取用户网关下子设备告警未读数
     *
     * @param deviceId 设备id
     * @param childId  子设备id
     * @param type     1 查询 2 清除
     */
    public void deGetAlarmCount(String deviceId, String childId, String type, String msgType, final DataApiCommonListener listener) {
        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/devices/alarms";
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = childId;
        }
        try {
            msgContentJson.put("token", ApiConstant.getAppToken());
            msgContentJson.put("deviceId", deviceId);
            msgContentJson.put("childDeviceId", childId);
            msgContentJson.put("type", type);
            msgContentJson.put("msgType", msgType);
            if ("3".equals(type)) {
                msgContentJson.put("isNeedRecord", 1);
            }
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<MessageCountBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<MessageCountBean> responseBean, Call call, Response response) {
                        WLog.e("deGetAlarmCount:onSuccess", response.toString());
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
                        WLog.e("deGetAlarmCount", "onError: " + response, e);
                    }
                });
    }

    /**
     * 获取用户网关下子设备告警未读数
     *
     * @param deviceId 设备id
     * @param childId  子设备id
     * @param type     1 查询 2 清除
     */
    public void deGetAlarmCount(String deviceId, String childId, String type, String msgType, int pageNum, int pageSize, final DataApiCommonListener listener) {
        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/devices/alarms";
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = childId;
        }
        try {
            msgContentJson.put("token", ApiConstant.getAppToken());
            msgContentJson.put("deviceId", deviceId);
            msgContentJson.put("childDeviceId", childId);
            msgContentJson.put("type", type);
            msgContentJson.put("msgType", msgType);
            msgContentJson.put("pageNum", pageNum);
            msgContentJson.put("pageSize", pageSize);
            if ("3".equals(type)) {
                msgContentJson.put("isNeedRecord", 1);
            }
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<MessageCountBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<MessageCountBean> responseBean, Call call, Response response) {
                        WLog.e("deGetAlarmCount:onSuccess", response.toString());
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
                        WLog.e("deGetAlarmCount", "onError: " + response, e);
                    }
                });
    }

    public void deDeleteLog(final String deviceId, String gatewayId, final String startTime, String endTime, final DataApiCommonListener listener) {
        if (TextUtils.isEmpty(gatewayId)) {
            gatewayId = deviceId;
        }

        String url = String.format(ApiConstant.URL_RECORD_DELETE_MESSAGE, ApiConstant.getUserID(), gatewayId)
                + "?token=" + ApiConstant.getAppToken()
                + "&childDeviceId=" + deviceId
                + "&dataType=2";
        if (!TextUtils.isEmpty(startTime)) {
            url = url + "&startTime=" + startTime;
        }
        if (!TextUtils.isEmpty(endTime)) {
            url = url + "&endTime=" + endTime;
        }
        final String finalGatewayId = gatewayId;
        OkGo.delete(url)
                .tag(this)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        WLog.e("deDeleteLog:onSuccess", response.toString());
                        if (!TextUtils.isEmpty(startTime)) {
                            //startTime为空时不删除日志列设备表中的设备
                            listener.onSuccess(responseBean.data);
                        } else {
                            //同时删除日志设备列表中的设备
                            String url2 = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/devices/deleteAlarmInfo";

                            OkGo.get(url2)
                                    .tag(this)
                                    .params("token", ApiConstant.getAppToken())
                                    .params("childDeviceId", deviceId)
                                    .params("deviceId", finalGatewayId)
                                    .params("msgType", "2")
                                    .execute(new EncryptCallBack<ResponseBean<MessageCountBean>>() {
                                        @Override
                                        public void onSuccess(ResponseBean<MessageCountBean> responseBean, Call call, Response response) {
                                            WLog.e("deDeleteAlarm:onSuccess", response.toString());
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
                                            WLog.e("deDeleteAlarm", "onError: " + response, e);
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                        WLog.e("deDeleteAlarm", "onError: " + response, e);
                    }
                });
    }

    /**
     * 删除日志报警
     */
    public void deDeleteAlarm(final String deviceId, String gatewayId, final String startTime, String endTime, final DataApiCommonListener listener) {
        if (TextUtils.isEmpty(gatewayId)) {
            gatewayId = deviceId;
        }

        String url = String.format(ApiConstant.URL_RECORD_DELETE_MESSAGE, ApiConstant.getUserID(), gatewayId)
                + "?token=" + ApiConstant.getAppToken()
                + "&childDeviceId=" + deviceId
                + "&dataType=1";
        if (!TextUtils.isEmpty(startTime)) {
            url = url + "&startTime=" + startTime;
        }
        if (!TextUtils.isEmpty(endTime)) {
            url = url + "&endTime=" + endTime;
        }

        final String finalGatewayId = gatewayId;
        OkGo.delete(url)
                .tag(this)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        WLog.e("deDeleteLog:onSuccess", response.toString());
                        if (!TextUtils.isEmpty(startTime)) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            String url2 = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/devices/deleteAlarmInfo";

                            OkGo.get(url2)
                                    .tag(this)
                                    .params("token", ApiConstant.getAppToken())
                                    .params("childDeviceId", deviceId)
                                    .params("deviceId", finalGatewayId)
                                    .params("msgType", "1")
                                    .execute(new EncryptCallBack<ResponseBean<MessageCountBean>>() {
                                        @Override
                                        public void onSuccess(ResponseBean<MessageCountBean> responseBean, Call call, Response response) {
                                            WLog.e("deDeleteAlarm:onSuccess", response.toString());
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
                                            WLog.e("deDeleteAlarm", "onError: " + response, e);
                                        }
                                    });
                        }

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                        WLog.e("deDeleteAlarm", "onError: " + response, e);
                    }
                });
    }

    /**
     * 设备历史数据查询
     *
     * @param type //1：告警，2：控制请求, 3:是否需要记录
     */
    public void doGetDeviceDataHistory(String deviceId, String type, String childDeviceId, String alarmCode, String startTime, String endTime, final DataApiCommonListener listener) {
        Device childDevice = MainApplication.getApplication().getDeviceCache().get(childDeviceId);
        if (childDevice != null && !childDevice.isZigbee()) {
            deviceId = null;
        }
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = childDeviceId;
        }
        String url = ApiConstant.URL_DATA_RECORDS + "/" + ApiConstant.getUserID() + "/devices/" + deviceId;
        OkGo.get(url)
                .tag(this)
                .params("token", ApiConstant.getAppToken())
                .params("childDeviceId", childDeviceId)
                .params("dataType", type)
                .params("messageCode", alarmCode)
                .params("startTime", startTime)
                .params("endTime", endTime)
                .execute(new EncryptCallBack<ResponseBean<MessageBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<MessageBean> responseBean, Call call, Response response) {
                        WLog.e("doGetDeviceDataHistory:onSuccess", response.toString());
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.i("DataApi", "onError: " + response);
                        WLog.e("UnBindDevice:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 获取大华报警列表
     *
     * @param type //1：告警，2：控制请求, 3:是否需要记录
     */
    public void doGetLcAlarmList(String deviceId, String type, String childDeviceId, String alarmCode, String startTime, String endTime, String nextAlarmId, String channelId, int pageSize, final DataApiCommonListener listener) {
        Device childDevice = MainApplication.getApplication().getDeviceCache().get(childDeviceId);
        if (childDevice != null && !childDevice.isZigbee()) {
            deviceId = null;
        }
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = childDeviceId;
        }
        String url = ApiConstant.URL_DATA_RECORDS + "/" + ApiConstant.getUserID() + "/devices/" + deviceId;
        OkGo.get(url)
                .tag(this)
                .params("token", ApiConstant.getAppToken())
                .params("childDeviceId", childDeviceId)
                .params("dataType", type)
                .params("messageCode", alarmCode)
                .params("startTime", startTime)
                .params("endTime", endTime)
                .params("nextAlarmId", nextAlarmId)
                .params("channelId", channelId)
                .params("pageSize", 20)
                .execute(new EncryptCallBack<ResponseBean<LcAlarmBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<LcAlarmBean> responseBean, Call call, Response response) {
                        WLog.e("doGetDeviceDataHistory:onSuccess", response.toString());
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.i("DataApi", "onError: " + response);
                        WLog.e("UnBindDevice:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    public void doGetServiceHistory(String url, final DataApiCommonListener listener) {

        OkGo.get(url)
                .tag(this)
                .params("token", ApiConstant.getAppToken())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String o, Call call, Response response) {
                        WLog.i("doGetServiceHistory:onSuccess", response.toString());
                        listener.onSuccess(o);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.i("doGetServiceHistory", "onError: " + response);
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 获取报警消息和日志
     */
    public void doGetDeviceAlarmAndLog(String childDeviceId, final DataApiCommonListener listener) {
        String deviceId = Preference.getPreferences().getCurrentGatewayID();
        String url = ApiConstant.URL_DATA_RECORDS + "/" + ApiConstant.getUserID() + "/devices/" + deviceId + "/all";
        OkGo.get(url)
                .tag(this)
                .params("token", ApiConstant.getAppToken())
                .params("childDeviceId", childDeviceId)
                .execute(new EncryptCallBack<ResponseBean<AllMessageBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<AllMessageBean> responseBean, Call call, Response response) {
                        WLog.e("GetDeviceAlarmAndLog:onSuccess", response.toString());
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.i("DataApi", "onError: " + response);
                        WLog.e("GetDeviceAlarmAndLog:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    public void doGetWidgetSort(final DataApiCommonListener listener) {

        HashMap<String, String> params = new HashMap<>();
        params.put("uId", ApiConstant.getUserID());
        String gatewayId = Preference.getPreferences().getCurrentGatewayID();
        if (!TextUtils.isEmpty(gatewayId)) {
            params.put("topDeviceId", gatewayId);
        }

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);

        OkGo.get(ApiConstant.URL_API_GET_WIDGET_INFO)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new JsonCallback<ResponseBean<WidgetBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<WidgetBean> responseBean, Call call, Response response) {
                        WLog.e("GetWidgetSort:onSuccess", response.toString());
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(-1, responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.i("DataApi", "onError: " + response);
                        WLog.e("GetWidgetSort:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    public void doUpdateWidgetSort(String widgetsJson, final DataApiCommonListener listener) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", ApiConstant.getAppToken());
            jsonObject.put("widgets", new JSONArray(widgetsJson));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(jsonObject.toString(), time);

        OkGo.post(ApiConstant.URL_API_SAVE_WIDGET_INFO)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .upJson(jsonObject)
                .execute(new JsonCallback<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        WLog.e("doUpdateWidgetSort", "onSuccess: " + response.toString());
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
                        WLog.e("doUpdateWidgetSort", "onError: " + response, e);
                    }
                });
    }

    public void doSendFeedback(String feedbackContent, String email, String phone, final DataApiCommonListener listener) {
        JSONObject jsonObject = new JSONObject();
        try {
//            jsonObject.put("token", ApiConstant.getAppToken());
            jsonObject.put("uId", ApiConstant.getUserID());
            jsonObject.put("feedbackContent", feedbackContent);
            jsonObject.put("email", email);
            jsonObject.put("appVersion", MainApplication.getApplication().getLocalInfo().appVersion);
            jsonObject.put("phone", phone);
            jsonObject.put("phoneModel", android.os.Build.MODEL);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(jsonObject.toString(), time);

        OkGo.post(ApiConstant.URL_API_FEEDBACK_SAVE)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .upJson(jsonObject)
                .execute(new JsonCallback<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        WLog.e("doUpdateWidgetSort", "onSuccess: " + response.toString());
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
                        WLog.e("doUpdateWidgetSort", "onError: " + response, e);
                    }
                });
    }

    public static void doRecordUploads(final JSONArray array) {
        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(array.toString(), time);

        OkGo.post(ApiConstant.URL_APP_UPLOADS)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .headers("WL-TID", ApiConstant.getTerminalId())
                .headers("WL-TOKEN", ApiConstant.getAppToken())
                .upJson(array)
                .execute(new JsonCallback<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        WLog.e("doUpdateWidgetSort", "onSuccess: " + response.toString());
                        if (responseBean.isSuccess()) {
                            WRecord.postSuccess(array);
                        } else {
                            WRecord.postFailure(array);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        WLog.e("doRecordUploads", "onError: " + response, e);
                        WRecord.postFailure(array);
                    }
                });
    }

    /**
     * 查询某个设备类型的品牌
     */
    public void doGetUEIBrand(String type, String deviceType, final DataApiCommonListener<BrandBean> listener) {

        String url = String.format(ApiConstant.URL_UEI_BRAND, ApiConstant.getUserID());

        HashMap<String, String> params = new HashMap<>();
        params.put("token", ApiConstant.getAppToken());
        params.put("type", type);
        params.put("appLang", LanguageUtil.getWulianCloudLanguage());
        if (TextUtils.equals("IF02", deviceType)) {
            params.put("deviceType", deviceType);
        }

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);

        OkGo.get(url)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new EncryptCallBack<ResponseBean<BrandBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<BrandBean> responseBean, Call call, Response response) {
                        WLog.e("doGetAirConditioningBrand:onSuccess", response.toString());
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(-1, responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.i("doGetAirConditioningBrand", "onError: " + response);
                        WLog.e("doGetAirConditioningBrand:   onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 查询品牌下的码库编码
     */
    public void doGetBrandCodeList(String type, String brand, String userId, final DataApiCommonListener<BrandBean.CodeListBean> listener) {

        String url = String.format(ApiConstant.URL_UEI_CODE_LIST, ApiConstant.getUserID());

        HashMap<String, String> params = new HashMap<>();
        params.put("token", ApiConstant.getAppToken());
        params.put("type", type);
        params.put("brand", brand);
        params.put("userId", userId);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);

        OkGo.get(url)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new EncryptCallBack<ResponseBean<BrandBean.CodeListBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<BrandBean.CodeListBean> responseBean, Call call, Response response) {
                        WLog.i("doGetBrandCodeList:onSuccess", response.toString());
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(-1, responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.i("doGetBrandCodeList", "onError: " + response);
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 查询码库编码对应的码库
     */
    public void doGetBrandCode(String type, String code, String userId, final DataApiCommonListener<BrandBean.CodeBean> listener) {

        String url = String.format(ApiConstant.URL_UEI_CODE_PICKS, ApiConstant.getUserID());

        HashMap<String, String> params = new HashMap<>();
        params.put("token", ApiConstant.getAppToken());
        params.put("type", type);
        params.put("code", code);
        params.put("userId", userId);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);

        OkGo.get(url)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new EncryptCallBack<ResponseBean<BrandBean.CodeBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<BrandBean.CodeBean> responseBean, Call call, Response response) {
                        WLog.i("doGetBrandCodeList:onSuccess", response.toString());
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(-1, responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.i("doGetBrandCodeList", "onError: " + response);
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 查询码库编码对应的型号
     */
    public void doGetBrandCodeType(String type, String userId, String brand, String model, String pageNum, String deviceType, String brandId, final DataApiCommonListener<CodeLibraryBean> listener) {

        String url = String.format(ApiConstant.URL_UEI_CODE_MODEL, ApiConstant.getUserID());

        HashMap<String, String> params = new HashMap<>();
        params.put("token", ApiConstant.getAppToken());
        params.put("type", type);
        params.put("brand", brand);
        params.put("pageNum", pageNum);
        if (TextUtils.equals("IF02", deviceType)) {
            params.put("brandId", brandId);
            params.put("deviceType", deviceType);
        } else {
            params.put("model", model);
            params.put("userId", userId);
        }

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);

        OkGo.get(url)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new EncryptCallBack<ResponseBean<CodeLibraryBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<CodeLibraryBean> responseBean, Call call, Response response) {
                        WLog.i("doGetBrandCodeList:onSuccess", response.toString());
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(-1, responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.i("doGetBrandCodeList", "onError: " + response);
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 获取第一个测试码
     *
     * @param userId
     * @param brand
     * @param type
     * @param groupId
     * @param region
     * @param listener
     */
    public void deGetUeiTestCode(String userId, String brand, String type, String groupId, String region, final DataApiCommonListener listener) {
        String url = ApiConstant.getBaseServer() + "/iot/v2/uei/" + ApiConstant.getUserID() + "/loadOSM";

        HashMap<String, String> params = new HashMap<>();
        params.put("token", ApiConstant.getAppToken());
        params.put("type", type);
        params.put("userId", userId);
        params.put("brand", brand);
        params.put("groupId", groupId);
        params.put("region", region);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);
        OkGo.get(url)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new EncryptCallBack<ResponseBean<TestCodeBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<TestCodeBean> responseBean, Call call, Response response) {
                        WLog.i("deGetUeiTestCode:onSuccess", response.toString());
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(-1, responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.i("doGetBrandCodeList", "onError: " + response);
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 反馈测试结果并且获取下一个测试码
     *
     * @param ueiId
     * @param keyworked
     * @param state
     * @param listener
     */
    public void doReportOSMResultAndGetNextKey(String ueiId, boolean keyworked, String state, final DataApiCommonListener listener) {
        String url = ApiConstant.getBaseServer() + "/iot/v2/uei/" + ApiConstant.getUserID() + "/reportOSMResult";
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson.put("token", ApiConstant.getAppToken());
            msgContentJson.put("userId", ueiId);
            msgContentJson.put("keyworked", keyworked);
            msgContentJson.put("state", state);
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<ReportKeyBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<ReportKeyBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(-1, responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.e("DeviceApiUnit:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 将LoadOSMAndGetNextKey的返回结果放到 UeiHelper上
     *
     * @param code
     * @param testKeyId
     * @param codeData
     * @param listener
     */
    public void doSendToUeiHelper(String code, int testKeyId, String codeData, final DataApiCommonListener listener) {
        String url = "https://irt.wulian.cc:33445/";//正式服务器地址
        JSONObject data = new JSONObject();
        JSONObject param = new JSONObject();
        try {
            //用于匹配的地址
            data.put("method", "getKeyCodeData");
            data.put("jsonrpc", "2.0");
            data.put("id", "3");

            param.put("code", code);
            param.put("key", testKeyId);
            param.put("codeSetData", codeData);
            data.put("params", param);
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkGo.post(url)
                .headers("Content-Type", "application/json")
                .tag(this)
                .upJson(data.toString())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        listener.onSuccess(s);
                    }
                });
    }

    /**
     * 短信通知告警消息 <p>
     * 用户填入相应的号码，用来接收某些重要告警消息的短信
     */
    public void doPostUserPushAudience(String userId, String deviceId, String phone, String messageCode, final DataApiCommonListener<String> listener) {

        String url = String.format(ApiConstant.URL_USER_PUSH_AUDIENCE, ApiConstant.getUserID());

        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson.put("token", ApiConstant.getAppToken());
            msgContentJson.put("uId", userId);
            msgContentJson.put("deviceId", deviceId);
            msgContentJson.put("phone", phone);
            msgContentJson.put("messageCode", messageCode);
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(JSON.toJSONString(responseBean));
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

    /**
     * 短信通知告警消息 <p>
     * 查询需要短信通知的手机号
     */
    public void doGetUserPushAudience(String userId, String deviceId, String messageCode, final DataApiCommonListener<String> listener) {

        String url = String.format(ApiConstant.URL_USER_PUSH_AUDIENCE, ApiConstant.getUserID());

        HashMap<String, String> params = new HashMap<>();
        params.put("token", ApiConstant.getAppToken());
        params.put("uId", userId);
        params.put("deviceId", deviceId);
        params.put("messageCode", messageCode);

        OkGo.get(url)
                .tag(this)
                .params(params)
                .execute(new EncryptCallBack<ResponseBean<UserPushAudienceBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<UserPushAudienceBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(JSON.toJSONString(responseBean));
                        } else {
                            listener.onFail(-1, responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.i("doGetBrandCodeList", "onError: " + response);
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 短信通知告警消息 <p>
     * 清空所有手机号
     */
    public void doDeleteUserPushAudience(String userId, String deviceId, String messageCode, final DataApiCommonListener<Object> listener) {

        String url = String.format(ApiConstant.URL_USER_PUSH_AUDIENCE, ApiConstant.getUserID());


        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson.put("token", ApiConstant.getAppToken());
            msgContentJson.put("uId", userId);
            msgContentJson.put("deviceId", deviceId);
            msgContentJson.put("messageCode", messageCode);
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(jsonObject.toString(), time);

        OkGo.delete(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(null);
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


    /**
     * 查询梦想之花网关省份接口
     */
    public void doGetGatewayProvince(String countryCode, String userId, final DataApiCommonListener<ProvinceBean> listener) {

        String url = ApiConstant.URL_DREAM_FLOWER_PROVINCE;

        HashMap<String, String> params = new HashMap<>();
        params.put("uId", userId);
        params.put("countryCode", countryCode);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);

        OkGo.get(url)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new JsonCallback<ResponseBean<ProvinceBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<ProvinceBean> provinceBean, Call call, Response response) {
                        if (provinceBean.isSuccess()) {
                            listener.onSuccess(provinceBean.data);
                        }
                    }
                });
    }

    /**
     * 查询梦想之花网关城市接口
     */
    public void doGetGatewayCity(String countryCode, String userId, String provinceName, final DataApiCommonListener<CityBean> listener) {


        String url = ApiConstant.URL_DREAM_FLOWER_CITY;

        HashMap<String, String> params = new HashMap<>();
        params.put("uId", userId);
        params.put("token", ApiConstant.getAppToken());
        params.put("countryCode", countryCode);
        params.put("provinceName", provinceName);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);

        OkGo.get(url)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new JsonCallback<ResponseBean<CityBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<CityBean> cityBean, Call call, Response response) {
                        if (cityBean.isSuccess()) {
                            listener.onSuccess(cityBean.data);
                        }
                    }
                });
    }

    /**
     * 查询Bc锁cameraId
     * add by hxc 增加对Bn的支持
     */
    public void doGetBcCameraId(final String deviceId, String type, final DataApiCommonListener<String> listener) {

        String cameraId = Preference.getPreferences().getBcCameraId(deviceId);
        if (!TextUtils.isEmpty(cameraId)) {
            listener.onSuccess(cameraId);
            return;
        }

        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/getBcCameraId";

        HashMap<String, String> params = new HashMap<>();
        params.put("uId", ApiConstant.getUserID());
        params.put("token", ApiConstant.getAppToken());
        params.put("deviceId", deviceId);
        params.put("type", type);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);

        OkGo.get(url)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new EncryptCallBack<ResponseBean<String>>() {
                    @Override
                    public void onSuccess(ResponseBean<String> responseBean, Call call, Response response) {
                        WLog.e("doGetBcCameraId:onSuccess ", response.toString());
                        if (responseBean.isSuccess()) {
                            if (TextUtils.isEmpty(responseBean.data)) {
                                listener.onSuccess("");
                            } else {
                                try {
                                    JSONObject jsonObject = new JSONObject(responseBean.data);
                                    String cameraId = jsonObject.getString("cameraId");
                                    Preference.getPreferences().saveBcCameraId(deviceId, cameraId);
                                    listener.onSuccess(cameraId);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    listener.onSuccess("");
                                }
                            }
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.e("doGetBcCameraId:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 获取爱看OSS子账号数据
     */
    public void doGetIcamOssInfo(final DataApiCommonListener<ICamOssInfoBean> listener) {

        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/oss/ram/account";
        HashMap<String, String> params = new HashMap<>();
        params.put("uId", ApiConstant.getUserID());
        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);

        OkGo.get(url)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new JsonCallback<ResponseBean<ICamOssInfoBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<ICamOssInfoBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("Region", responseBean.data.Region);
                                jsonObject.put("Bucket", responseBean.data.Bucket);
                                jsonObject.put("AccessKeyId", responseBean.data.AccessKeyId);
                                jsonObject.put("AccessKeySecret", responseBean.data.AccessKeySecret);
                                Preference.getPreferences().saveICamOssInfo(jsonObject.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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

    /**
     * 获取爱看OSS子账号数据,优先从缓存
     */
    public void doGetIcamOssInfoFromCache(final DataApiCommonListener<ICamOssInfoBean> listener) {
        ICamOssInfoBean iCamOssInfoBean = JSON.parseObject(Preference.getPreferences().getICamOssInfo(), ICamOssInfoBean.class);
        if (!TextUtils.isEmpty(iCamOssInfoBean.Region)) {
            listener.onSuccess(iCamOssInfoBean);
            return;
        }

        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/oss/ram/account";
        HashMap<String, String> params = new HashMap<>();
        params.put("uId", ApiConstant.getUserID());
        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);

        OkGo.get(url)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new JsonCallback<ResponseBean<ICamOssInfoBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<ICamOssInfoBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("Region", responseBean.data.Region);
                                jsonObject.put("Bucket", responseBean.data.Bucket);
                                jsonObject.put("AccessKeyId", responseBean.data.AccessKeyId);
                                jsonObject.put("AccessKeySecret", responseBean.data.AccessKeySecret);
                                Preference.getPreferences().saveICamOssInfo(jsonObject.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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

    /**
     * 摄像机绑定通知此前用户
     */
    public void doPostIcamBindPush(String deviceId, final DataApiCommonListener<Object> listener) {

        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/user/push";

        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson.put("token", ApiConstant.getAppToken());
            msgContentJson.put("deviceId", deviceId);
            msgContentJson.put("uId", ApiConstant.getUserID());
            msgContentJson.put("messageCode", "0104042");
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        WLog.e("deGetAlarmCount:onSuccess", response.toString());
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
                        WLog.e("deGetAlarmCount", "onError: " + response, e);
                    }
                });
    }

    /**
     * 保存bc锁WiFi信息
     * wifiName
     * bindResult :1代表成功
     */
    public void doSaveDeviceKeyValue(String deviceId, String key, String value, final DataApiCommonListener<Object> listener) {

        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/saveDeviceKeyValue";

        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson.put("token", ApiConstant.getAppToken());
            msgContentJson.put("deviceId", deviceId);
            msgContentJson.put("key", key);
            msgContentJson.put("value", value);
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        WLog.e("deGetAlarmCount:onSuccess", response.toString());
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
                        WLog.e("deGetAlarmCount", "onError: " + response, e);
                    }
                });
    }

    /**
     * 查询Bc锁cameraId
     */
    public void doGetDeviceKeyValue(final String deviceId, String key, final DataApiCommonListener<KeyValueListBean> listener) {

        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/getDeviceKeyValue";

        HashMap<String, String> params = new HashMap<>();
        params.put("uId", ApiConstant.getUserID());
        params.put("token", ApiConstant.getAppToken());
        params.put("deviceId", deviceId);
        if (!TextUtils.isEmpty(key)) {
            params.put("key", key);
        }

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);

        OkGo.get(url)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new EncryptCallBack<ResponseBean<KeyValueListBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<KeyValueListBean> responseBean, Call call, Response response) {
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
     * 查询安全狗下受保护设备列表
     */
    public void doGetSafedogProtectedDevices(String deviceId, final DataApiCommonListener<List<SafeDogProtectDeviceBean>> listener) {

        String url = String.format(ApiConstant.URL_SAFE_DOG_PROTECTED_DEVICE, ApiConstant.getUserID(), deviceId);

        HashMap<String, String> params = new HashMap<>();
        params.put("uid", ApiConstant.getUserID());
        params.put("token", ApiConstant.getAppToken());
        params.put("safedogid", deviceId);
        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);

        OkGo.get(url)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new JsonCallback<ResponseBean<SafeDogGetDevicesBeanList>>() {
                    @Override
                    public void onSuccess(ResponseBean<SafeDogGetDevicesBeanList> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data.devices);
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
     * 查询安全狗下未受保护设备列表
     */
    public void doGetSafedogUnprotectedDevices(String deviceId, final DataApiCommonListener<List<SafeDogProtectDeviceBean>> listener) {

        String url = String.format(ApiConstant.URL_SAFE_DOG_UNPROTECTED_DEVICE, ApiConstant.getUserID(), deviceId);

        HashMap<String, String> params = new HashMap<>();
        params.put("uid", ApiConstant.getUserID());
        params.put("token", ApiConstant.getAppToken());
        params.put("safedogid", deviceId);
        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);

        OkGo.get(url)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new JsonCallback<ResponseBean<SafeDogGetDevicesBeanList>>() {
                    @Override
                    public void onSuccess(ResponseBean<SafeDogGetDevicesBeanList> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data.devices);
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
     * 查询安全狗扫描任务列表
     */
    public void doGetSafedogSchedules(String deviceId, final DataApiCommonListener<SafeDogScheduleBean> listener) {

        String url = String.format(ApiConstant.URL_SAFE_DOG_GET_SCHEDULES, ApiConstant.getUserID(), deviceId);

        HashMap<String, String> params = new HashMap<>();
        params.put("uid", ApiConstant.getUserID());
        params.put("token", ApiConstant.getAppToken());
        params.put("safedogid", deviceId);
        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);

        OkGo.get(url)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new JsonCallback<ResponseBean<SafeDogScheduleBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<SafeDogScheduleBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            if (responseBean.data != null) {
                                listener.onSuccess(responseBean.data);
                            } else {
                                listener.onSuccess(null);
                            }
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
     * 查询安全狗攻击原记录
     */
    public void doGetSafedogIncidents(String deviceId, final DataApiCommonListener<List<SafeDogIncidentsBean>> listener) {

        String url = String.format(ApiConstant.URL_SAFE_DOG_GET_INCIDENTS, ApiConstant.getUserID(), deviceId);

        HashMap<String, String> params = new HashMap<>();
        params.put("uid", ApiConstant.getUserID());
        params.put("token", ApiConstant.getAppToken());
        params.put("safedogid", deviceId);
        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);

        OkGo.get(url)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new JsonCallback<ResponseBean<SafeDogIncidentsBeanList>>() {
                    @Override
                    public void onSuccess(ResponseBean<SafeDogIncidentsBeanList> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data.incidents);
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
     * 查询安全狗首页信息
     */
    public void doGetSafedogHomeData(String deviceId, final DataApiCommonListener<SafeDogHomeDataBean> listener) {

        String url = String.format(ApiConstant.URL_SAFE_DOG_GET_HOME_DATA, ApiConstant.getUserID(), deviceId);

        HashMap<String, String> params = new HashMap<>();
        params.put("uid", ApiConstant.getUserID());
        params.put("token", ApiConstant.getAppToken());
        params.put("safedogid", deviceId);
        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);

        OkGo.get(url)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new JsonCallback<ResponseBean<SafeDogHomeDataBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<SafeDogHomeDataBean> responseBean, Call call, Response response) {
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
     * 安全狗主动扫描
     */
    public void doSafedogScan(String deviceId, final DataApiCommonListener<Object> listener) {

        String url = String.format(ApiConstant.URL_SAFE_DOG_SCAN, ApiConstant.getUserID(), deviceId);

        HashMap<String, String> params = new HashMap<>();
        params.put("uid", ApiConstant.getUserID());
        params.put("token", ApiConstant.getAppToken());
        params.put("safedogid", deviceId);
        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);

        OkGo.get(url)
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
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }


    /**
     * 查询直播流信息
     */
    public void doGetLiveStreamData(String deviceId, final DataApiCommonListener<LiveStreamInfoBean> listener) {

        String url = String.format(ApiConstant.URL_LIVE_STREAM, ApiConstant.getUserID(), deviceId);

        HashMap<String, String> params = new HashMap<>();
        params.put("uid", ApiConstant.getUserID());
        params.put("token", ApiConstant.getAppToken());
        params.put("deviceId", deviceId);
        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);

        OkGo.get(url)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new JsonCallback<ResponseBean<LiveStreamInfoBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<LiveStreamInfoBean> responseBean, Call call, Response response) {
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
     * 查询直播流地址
     */
    public void doGetLiveStreamAddress(String deviceId, final DataApiCommonListener<LiveStreamAddressBean> listener) {

        String url = String.format(ApiConstant.URL_LIVE_STREAM_ADDR, ApiConstant.getUserID(), deviceId);

        HashMap<String, String> params = new HashMap<>();
        params.put("uid", ApiConstant.getUserID());
        params.put("token", ApiConstant.getAppToken());
        params.put("deviceId", deviceId);
        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);

        OkGo.get(url)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new JsonCallback<ResponseBean<LiveStreamAddressBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<LiveStreamAddressBean> responseBean, Call call, Response response) {
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
     * 保存体重信息
     */
    public void doSaveWeight(String deviceId, long createTime, String weight, final DataApiCommonListener<Object> listener) {

        String url = String.format(ApiConstant.URL_SAVE_WEIGHT, ApiConstant.getUserID());

        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson.put("token", ApiConstant.getAppToken());
            msgContentJson.put("deviceId", deviceId);
            msgContentJson.put("uId", ApiConstant.getUserID());
            msgContentJson.put("createTime", createTime);
            msgContentJson.put("weight", weight);
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new MsgContentCallBack() {
                    @Override
                    public void onSuccess(Object o, Call call, Response response) {
                        WLog.e("doSaveWeight:onSuccess", response.toString());
                        try {
                            JSONObject object = new JSONObject(o.toString());
                            listener.onSuccess(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            listener.onFail(-1, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                        WLog.e("doSaveWeight", "onError: " + response, e);
                    }
                });
    }

    /**
     * 查询体重秤历史数据
     */
    public void doGetWeightRecords(String deviceId, final DataApiCommonListener<Object> listener) {

        String url = String.format(ApiConstant.URL_GET_WEIGHT_RECORDS, ApiConstant.getUserID(), deviceId);

        HashMap<String, String> params = new HashMap<>();
        params.put("token", ApiConstant.getAppToken());
        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);

        OkGo.get(url)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new MsgContentCallBack() {
                    @Override
                    public void onSuccess(Object o, Call call, Response response) {
                        WLog.e("doGetWeightRecords:onSuccess", response.toString());
                        try {
                            JSONObject object = new JSONObject(o.toString());
                            listener.onSuccess(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            listener.onFail(-1, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                        WLog.e("doSaveWeight", "onError: " + response, e);
                    }
                });
    }

    /**
     * 保存小物摄像机预置位
     */
    public void doSaveCylincamPrePosition(String deviceId, String name, String positionId, String imgUrl, final DataApiCommonListener<Object> listener) {

        String url = ApiConstant.URL_SAVE_CYLINCAM_POSITION;

        JSONObject msgContentJson = new JSONObject();
        try {
            msgContentJson.put("uId", ApiConstant.getUserID());
            msgContentJson.put("token", ApiConstant.getAppToken());
            msgContentJson.put("deviceId", deviceId);
            msgContentJson.put("name", name);
            msgContentJson.put("imgUrl", imgUrl);
            msgContentJson.put("positionId", positionId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkGo.post(url)
                .tag(this)
                .upJson(msgContentJson)
                .execute(new JsonCallback<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 查询小物预置位
     */
    public void doGetCylincamPosition(String deviceId, final DataApiCommonListener<List<CylincamPositionBean.PositionBean>> listener) {

        String url = ApiConstant.URL_GET_CYLINCAM_POSITION;

        HashMap<String, String> params = new HashMap<>();
        params.put("uId", ApiConstant.getUserID());
        params.put("token", ApiConstant.getAppToken());
        params.put("deviceId", deviceId);
        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);

        OkGo.get(url)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new JsonCallback<ResponseBean<CylincamPositionBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<CylincamPositionBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data.position);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                        WLog.e("doGetCylincamPosition", "onError: " + response, e);
                    }
                });


    }

    /**
     * 场景管家查询小物预置位
     */
    public void doGetPositionForH5(String deviceId, final DataApiCommonListener<Object> listener) {

        String url = ApiConstant.URL_GET_CYLINCAM_POSITION;

        HashMap<String, String> params = new HashMap<>();
        params.put("uId", ApiConstant.getUserID());
        params.put("token", ApiConstant.getAppToken());
        params.put("deviceId", deviceId);
        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);

        OkGo.get(url)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject object = null;
                        try {
                            object = new JSONObject(s);
                            listener.onSuccess(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });


    }

    /**
     * 删除小物预置位
     */
    public void doDeleteCylincamPosition(String deviceId, String positionId, final DataApiCommonListener<Object> listener) {

        String url = ApiConstant.URL_DELETE_CYLINCAM_POSITION;

        HashMap<String, String> params = new HashMap<>();
        params.put("uId", ApiConstant.getUserID());
        params.put("token", ApiConstant.getAppToken());
        params.put("deviceId", deviceId);
        params.put("positionId", positionId);
        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);

        OkGo.get(url)
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
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                    }
                });

    }


    /**
     * 通用上传图片
     *
     * @param file
     * @param listener
     */
    public void doPostImage(File file, final DataApiCommonListener<ImageUrlBean> listener) {
        OkGo.post(ApiConstant.URL_UPDATE_IMAGE)
                .tag(this)
                .headers("WL-TOKEN", ApiConstant.getAppToken())
                .params("file", file)
                .execute(new JsonCallback<ResponseBean<ImageUrlBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<ImageUrlBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            if (responseBean.data != null) {
                                listener.onSuccess(responseBean.data);
                            }
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, e.getMessage());

                    }

                    @Override
                    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        super.upProgress(currentSize, totalSize, progress, networkSpeed);
                    }
                });

    }


    /**
     * 获取wifi红外转发器码库
     */
    public void doGetWifiIRCodeLib(String type, String model, String deviceType, final DataApiCommonListener<CodeLibBean> listener) {

        String url = String.format(ApiConstant.URL_WIFI_IR_CODE_LIBRARY, ApiConstant.getUserID());

        HashMap<String, String> params = new HashMap<>();
        params.put("uId", ApiConstant.getUserID());
        params.put("token", ApiConstant.getAppToken());
        params.put("type", type);
        params.put("model", model);
        params.put("deviceType", deviceType);
        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);

        OkGo.get(url)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new EncryptCallBack<ResponseBean<CodeLibBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<CodeLibBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        listener.onFail(-1, e.getMessage());
                    }
                });

    }

    /**
     * 添加 wifi红外遥控器
     */
    public void doAddWifiIRController(String deviceId, String blockType, String blockName, String codeLib, final DataApiCommonListener<ControllerBean> listener) {

        String url = String.format(ApiConstant.URL_WIFI_IR_BLOCK, ApiConstant.getUserID());
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson.put("deviceId", deviceId);
            msgContentJson.put("token", ApiConstant.getAppToken());
            msgContentJson.put("blockType", blockType);
            msgContentJson.put("blockName", blockName);
            msgContentJson.put("codeLib", codeLib);
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<ControllerBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<ControllerBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }

    /**
     * 删除wifi红外设备
     *
     * @param deviceId 设备id
     * @param blockId  null表示删除所有遥控器
     * @param listener 回调
     */
    public void doDeleteWifiIRController(String deviceId, String blockId, final DataApiCommonListener listener) {
        String url = null;
        if (!TextUtils.isEmpty(blockId)) {
            url = String.format(ApiConstant.URL_WIFI_IR_BLOCK, ApiConstant.getUserID()) + "?token=" + ApiConstant.getAppToken() + "&deviceId=" + deviceId + "&blockId=" + blockId;
        } else {
            url = String.format(ApiConstant.URL_WIFI_IR_BLOCK, ApiConstant.getUserID()) + "?token=" + ApiConstant.getAppToken() + "&deviceId=" + deviceId;
        }

        OkGo.delete(url)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
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
                        super.onError(call, response, e);
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }


    /**
     * 修改wifi红外设备信息
     */
    public void doUpdateWifiIRController(String deviceId, String blockType, String blockName, String blockId, final DataApiCommonListener listener) {
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson.put("token", ApiConstant.getAppToken());
            msgContentJson.put("blockType", blockType);
            msgContentJson.put("blockName", blockName);
            msgContentJson.put("deviceId", deviceId);
            msgContentJson.put("blockId", blockId);
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = String.format(ApiConstant.URL_WIFI_IR_BLOCK, ApiConstant.getUserID());
        OkGo.put(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
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
                        super.onError(call, response, e);
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }


    /**
     * 获取wifi红外遥控器列表
     */
    public void doGetWifiIRControllerList(String deviceId, final DataApiCommonListener<ControllerListBean> listener) {

        String url = String.format(ApiConstant.URL_WIFI_IR_BLOCK, ApiConstant.getUserID()) + "?deviceId=" + deviceId + "&token=" + ApiConstant.getAppToken();

        OkGo.get(url)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .execute(new EncryptCallBack<ResponseBean<ControllerListBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<ControllerListBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }

    /**
     * 添加 wifi红外按键码
     */
    public void doAddWifiIRCode(String deviceId, String blockId, String keyName, final DataApiCommonListener<CustomKeyBean> listener) {

        String url = String.format(ApiConstant.URL_WIFI_IR_KEY, ApiConstant.getUserID());

        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson.put("deviceId", deviceId);
            msgContentJson.put("blockId", blockId);
            msgContentJson.put("keyName", keyName);
            msgContentJson.put("token", ApiConstant.getAppToken());
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<CustomKeyBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<CustomKeyBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }

    /**
     * 删除wifi红外按键码
     */
    public void doDeleteWifiIRCode(String deviceId, String blockId, String keyId, final DataApiCommonListener listener) {
        String encode = null;
        try {
            encode = URLEncoder.encode(keyId, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = String.format(ApiConstant.URL_WIFI_IR_KEY, ApiConstant.getUserID()) + "?deviceId=" + deviceId + "&token=" + ApiConstant.getAppToken() + "&blockId=" + blockId + "&keyId=" + encode;
        OkGo.delete(url)
                .tag(this)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
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
                        super.onError(call, response, e);
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }


    /**
     * 修改wifi红外按键码名称
     */
    public void doUpdateWifiIRCodeName(String deviceId, String keyId, String keyName, String blockId, final DataApiCommonListener listener) {
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson.put("keyId", keyId);
            msgContentJson.put("keyName", keyName);
            msgContentJson.put("deviceId", deviceId);
            msgContentJson.put("blockId", blockId);
            msgContentJson.put("token", ApiConstant.getAppToken());
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = String.format(ApiConstant.URL_WIFI_IR_KEY_NAME, ApiConstant.getUserID());
        OkGo.put(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
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
                        super.onError(call, response, e);
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }


    /**
     * 修改wifi红外按键码
     * code传空代表清空
     */
    public void doChangeWifiIRCode(String deviceId, String keyId, String code, String blockId, final DataApiCommonListener listener) {
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson.put("keyId", keyId);
            msgContentJson.put("code", code);
            msgContentJson.put("deviceId", deviceId);
            msgContentJson.put("blockId", blockId);
            msgContentJson.put("token", ApiConstant.getAppToken());
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = String.format(ApiConstant.URL_WIFI_IR_KEY_CODE, ApiConstant.getUserID());
        OkGo.put(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
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
                        super.onError(call, response, e);
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }

    /**
     * 获取wifi红外按键码
     */
    public void doGetWifiIRCodeList(String deviceId, String blockId, final DataApiCommonListener<ControllerBlocksBean> listener) {

        String url = String.format(ApiConstant.URL_WIFI_IR_KEY, ApiConstant.getUserID()) + "?deviceId=" + deviceId + "&blockId=" + blockId + "&token=" + ApiConstant.getAppToken();
        OkGo.get(url)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .execute(new EncryptCallBack<ResponseBean<ControllerBlocksBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<ControllerBlocksBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }

    /**
     * 设置wifi红外设备学习状态
     * status:1开启 2停止
     */
    public void setWifiIRLearningStatus(String deviceId, String status, final DataApiCommonListener<Object> listener) {


        String url = String.format(ApiConstant.URL_WIFI_IR_LEARN, ApiConstant.getUserID());

        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson.put("deviceId", deviceId);
            msgContentJson.put("token", ApiConstant.getAppToken());
            msgContentJson.put("status", status);
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
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
                        super.onError(call, response, e);
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }

    /**
     * 控制wifi红外转发器
     */
    public void doControlWifiIR(String deviceId, String codeType, String blockId, String keyId, String code, final DataApiCommonListener<Object> listener) {

        String url = String.format(ApiConstant.URL_WIFI_IR_CONTROL, ApiConstant.getUserID());
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson.put("deviceId", deviceId);
            msgContentJson.put("token", ApiConstant.getAppToken());
            msgContentJson.put("codeType", codeType);
            if (!TextUtils.isEmpty(blockId)) {
                msgContentJson.put("blockId", blockId);
            }
            if (!TextUtils.isEmpty(keyId)) {
                msgContentJson.put("keyId", keyId);
            }
            if (!TextUtils.isEmpty(code)) {
                msgContentJson.put("code", code);
            }
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
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
                        super.onError(call, response, e);
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }


    /**
     * 非空调一键匹配
     */
    public void doMatchWifiIRController(String deviceId, String type, String brandId, String cmpKey, String code, final DataApiCommonListener<MatchNextCmpKey> listener) {
        String url = String.format(ApiConstant.URL_WIFI_IR_MATCH, ApiConstant.getUserID());

        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson.put("deviceId", deviceId);
            msgContentJson.put("type", type);
            msgContentJson.put("brandId", brandId);
            msgContentJson.put("cmpKey", cmpKey);
            msgContentJson.put("code", code);
            msgContentJson.put("token", ApiConstant.getAppToken());
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<MatchNextCmpKey>>() {
                    @Override
                    public void onSuccess(ResponseBean<MatchNextCmpKey> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }

    /**
     * 空调遥控器一键匹配
     */
    public void doMatchAirController(String deviceId, String type, String brandId, String cmpKey, String code, final DataApiCommonListener<AirCodeLibsBean> listener) {
        String url = String.format(ApiConstant.URL_WIFI_IR_MATCH, ApiConstant.getUserID());

        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson.put("deviceId", deviceId);
            msgContentJson.put("type", type);
            msgContentJson.put("brandId", brandId);
            msgContentJson.put("cmpKey", cmpKey);
            msgContentJson.put("code", code);
            msgContentJson.put("token", ApiConstant.getAppToken());
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<AirCodeLibsBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<AirCodeLibsBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(Integer.parseInt(responseBean.resultCode), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }


    /**
     * wifi红外固件升级
     *
     * @param deviceId
     * @param listener
     */
    public void updateIf02Firmware(String deviceId, final DataApiCommonListener<Object> listener) {
        String url = String.format(ApiConstant.URL_WIFI_IR_UPGRADE, ApiConstant.getUserID());

        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson.put("deviceId", deviceId);
            msgContentJson.put("token", ApiConstant.getAppToken());
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }

    /**
     * 获取品牌下遥控器的部分码库
     */
    public void doGetWifiIRPartCodeLib(String deviceId, String type, String brandId, final DataApiCommonListener<AirCodeLibsBean> listener) {

        String url = String.format(ApiConstant.URL_WIFI_IR_PART_CODELIB, ApiConstant.getUserID()) + "?deviceId=" + deviceId + "&brandId=" + brandId + "&token=" + ApiConstant.getAppToken() + "&type=" + type;
        OkGo.get(url)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .execute(new EncryptCallBack<ResponseBean<AirCodeLibsBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<AirCodeLibsBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }

    /**
     * 获取管家日志
     *
     * @param deviceId
     * @param startTime
     * @param endTime
     * @param pageSize
     * @param listener
     */

    public void doGetHouseKeeperLog(String deviceId, String startTime, String endTime, int pageSize, final DataApiCommonListener<HouseKeeperLogBean> listener) {
        String url = ApiConstant.URL_DATA_RECORDS + "/" + ApiConstant.getUserID() + "/devices/seneschal";
        OkGo.get(url)
                .tag(this)
                .params("token", ApiConstant.getAppToken())
                .params("deviceId", deviceId)
                .params("startTime", startTime)
                .params("endTime", endTime)
                .params("pageSize", 10)
                .execute(new EncryptCallBack<ResponseBean<HouseKeeperLogBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<HouseKeeperLogBean> responseBean, Call call, Response response) {
                        WLog.e("goGetHouseKeeperLog:onSuccess", response.toString());
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.i("goGetHouseKeeperLog", "onError: " + response);
                        listener.onFail(-1, response.message());
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 获取场景日志
     *
     * @param deviceId
     * @param startTime
     * @param endTime
     * @param pageSize
     * @param listener
     */

    public void doGetSceneLog(String deviceId, String startTime, String endTime, int pageSize, final DataApiCommonListener<SceneLogBean> listener) {
        String url = ApiConstant.URL_DATA_RECORDS + "/" + ApiConstant.getUserID() + "/devices/scene";
        OkGo.get(url)
                .tag(this)
                .params("token", ApiConstant.getAppToken())
                .params("deviceId", deviceId)
                .params("startTime", startTime)
                .params("endTime", endTime)
                .params("pageSize", 10)
                .execute(new EncryptCallBack<ResponseBean<SceneLogBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<SceneLogBean> responseBean, Call call, Response response) {
                        WLog.e("goGetSceneLog:onSuccess", response.toString());
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.i("goGetSceneLog", "onError: " + response);
                        listener.onFail(-1, response.message());
                        super.onError(call, response, e);
                    }
                });
    }


    /**
     * 物联公告
     *
     * @param listener
     */
    public void doGetWulianNotice(final DataApiCommonListener<WulianNoticeBean> listener) {
        String url;
        if (LanguageUtil.isAllChina()) {
            url = ApiConstant.BASE_URL + "/api/app/notice" + "?lang=zh_cn";
        } else {
            url = ApiConstant.BASE_URL + "/api/app/notice" + "?lang=en";
        }

        OkGo.get(url)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .execute(new JsonCallback<ResponseBean<WulianNoticeBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<WulianNoticeBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        }
                    }
                });
    }

}
