package cc.wulian.smarthomev6.support.core.apiunit;

import android.content.Context;

import com.lzy.okgo.OkGo;

import org.json.JSONObject;

import java.util.HashMap;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.support.core.apiunit.bean.GrantRelationBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.PlatformListBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.ResponseBean;
import cc.wulian.smarthomev6.support.core.cipher.CipherUtil;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 第三方控制平台接口
 */
public class ControlPlatformApiUnit {

    private Context context;

    public ControlPlatformApiUnit(Context context) {
        this.context = context;
    }

    public interface CommonListener<T> {
        void onSuccess(T bean);

        void onFail(int code, String msg);
    }

    /**
     * 查询授权平台列表
     */
    public void doGetPlatformList(final CommonListener<PlatformListBean> listener) {

        String url = ApiConstant.URL_PLATFORM_LIST;

        OkGo.get(url)
                .tag(this)
                .execute(new JsonCallback<ResponseBean<PlatformListBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<PlatformListBean> responseBean, Call call, Response response) {
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


    /**
     * 查询帐号是否已被授权
     */
    public void doGetAuthStatus(String userId, String thirdPartnerId, final CommonListener<GrantRelationBean> listener) {

        String url = ApiConstant.getBaseServer() + "/iot/v2/users/" + ApiConstant.getUserID() + "/verify-oauth-grant/" + ApiConstant.WHITEROBOT_CLIEND_ID;

        HashMap<String, String> params = new HashMap<>();
        params.put("token", ApiConstant.getAppToken());
        params.put("uId", userId);
        params.put("thirdPartnerId", thirdPartnerId);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);

        OkGo.get(url)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new EncryptCallBack<ResponseBean<GrantRelationBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<GrantRelationBean> responseBean, Call call, Response response) {
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

    /**
     * 解除授权
     */
    public void doDeleteAuthStatus(String userId, String thirdPartnerId, final CommonListener<Object> listener) {

        String url = ApiConstant.getBaseServer() + "/iot/v2/users/" + ApiConstant.getUserID() + "/oauth-grant/" + ApiConstant.WHITEROBOT_CLIEND_ID + "?token=" + ApiConstant.getAppToken();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", ApiConstant.getAppToken());
            jsonObject.put("uId", userId);
            jsonObject.put("thirdPartnerId", thirdPartnerId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(jsonObject.toString(), time);

        OkGo.delete(url)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
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


}
