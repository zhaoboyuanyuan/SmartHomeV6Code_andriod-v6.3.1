package cc.wulian.smarthomev6.support.core.apiunit;

import android.content.Context;

import cc.wulian.smarthomev6.support.core.apiunit.bean.AuthChildDevicesBean;
import cc.wulian.smarthomev6.support.core.cipher.CipherUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

import com.lzy.okgo.OkGo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.support.core.apiunit.bean.AuthAccountListBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.ResponseBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.UserBean;
import okhttp3.Call;
import okhttp3.Response;

import static cc.wulian.smarthomev6.support.core.apiunit.ApiConstant.BASE_URL_USER;

/**
 * 作者: mamengchao
 * 时间: 2017/4/6 0006
 * 描述: 授权类接口
 * 联系方式: 805901025@qq.com
 */

public class AuthApiUnit {
    private Context context;

    public AuthApiUnit(Context context) {
        this.context = context;
    }

    public interface AuthApiCommonListener<T> {
        void onSuccess(T bean);

        void onFail(int code, String msg);
    }


    /**
     * 设备授权
     * {
     * “token”:”xxxxxx”,
     * “deviceId”:”xxxxxxxxx”, 网关id
     * “granteeUid”:”xxxxxxx”  被授权者id
     * }
     */
    public void doAuthAccount(String deviceId, String granteeUid, final AuthApiCommonListener listener) {
        doAuthAccount(deviceId, granteeUid, "2", null, null, null, listener);
    }

    /**
     * 设备授权
     *
     * @param deviceId            网关id
     * @param granteeUid          被授权者id
     * @param grantLevel          分享级别，1 子设备级别分享，2 网关级别分享
     * @param grantChildDevices   分享的子设备id列表，可以为null
     * @param unGrantChildDevices 取消分享的子设备id列表，可以为null
     * @param childDeviceFlag     1 为更新操作，null 初始操作
     */
    public void doAuthAccount(String deviceId, String granteeUid,
                              String grantLevel, List<String> grantChildDevices, List<String> unGrantChildDevices, String childDeviceFlag,
                              final AuthApiCommonListener listener) {
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson.put("uId", ApiConstant.getUserID());
            msgContentJson.put("token", ApiConstant.getAppToken());
            msgContentJson.put("deviceId", deviceId);
            msgContentJson.put("granteeUid", granteeUid);
            if (grantLevel != null) {
                msgContentJson.put("grantLevel", grantLevel);
            }
            if (grantChildDevices != null) {
                JSONArray jsonArray = new JSONArray(grantChildDevices);
                msgContentJson.put("grantChildDevices", jsonArray);
            }
            if (unGrantChildDevices != null) {
                JSONArray jsonArray = new JSONArray(unGrantChildDevices);
                msgContentJson.put("unGrantChildDevices", jsonArray);
            }
            if (childDeviceFlag != null) {
                msgContentJson.put("childDeviceFlag", childDeviceFlag);
            }
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = BASE_URL_USER + "/" + ApiConstant.getUserID() + "/devices-grant";
        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(null);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
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
     * 获取分享出去的子设备列表
     *
     * @param deviceId   网关id
     * @param granteeUid 被分享者id
     */
    public void doGetAuthChildDevices(String deviceId, String granteeUid, final AuthApiCommonListener<List<String>> listener) {
        String url = BASE_URL_USER + "/" + ApiConstant.getUserID() + "/getGrantChildDevice";

        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson.put("token", ApiConstant.getAppToken());
            msgContentJson.put("deviceId", deviceId);
            msgContentJson.put("granteeUid", granteeUid);
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<AuthChildDevicesBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<AuthChildDevicesBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data.grantChildDevices);
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
     * 获取已授权用户
     */
    public void doGetAllAuthAccount(String deviceId, final AuthApiCommonListener<List<UserBean>> listener) {
        String url = BASE_URL_USER + "/" + ApiConstant.getUserID() + "/devices-grant/" + deviceId;
        OkGo.get(url)
                .tag(this)
                .params("token", ApiConstant.getAppToken())
                .execute(new EncryptCallBack<ResponseBean<AuthAccountListBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<AuthAccountListBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            List<UserBean> userBeanList = responseBean.data.grantUserList;
                            listener.onSuccess(userBeanList);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.e("GetAuthAccount:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 获取已绑定用户
     */
    public void doGetAllBindAccount(String deviceId, final AuthApiCommonListener<List<UserBean>> listener) {
        String url = BASE_URL_USER + "/" + ApiConstant.getUserID() + "/devices-grant/" + deviceId;
        OkGo.get(url)
                .tag(this)
                .params("token", ApiConstant.getAppToken())
                .execute(new EncryptCallBack<ResponseBean<AuthAccountListBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<AuthAccountListBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            WLog.e("doGetAllBindAccount:onSuccess");
                            List<UserBean> userBeanList = responseBean.data.boundUserList;
                            listener.onSuccess(userBeanList);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.e("doGetAllBindAccount:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 解除授权
     */
    public void doUnAuthAccount(String deviceId, String granteeUid, final AuthApiCommonListener listener) {
        String url = BASE_URL_USER + "/" + ApiConstant.getUserID() + "/devices-grant/" + deviceId + "/" + granteeUid + "?token=" + ApiConstant.getAppToken();

        OkGo.delete(url)
                .tag(this)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(null);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
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
}
