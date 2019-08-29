package cc.wulian.smarthomev6.support.core.apiunit;

import android.content.Context;
import android.text.TextUtils;

import com.lzy.okgo.OkGo;

import org.json.JSONObject;

import java.util.HashMap;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.support.core.apiunit.bean.PlayModeBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.ResponseBean;
import cc.wulian.smarthomev6.support.core.cipher.CipherUtil;
import cc.wulian.smarthomev6.support.utils.MD5Util;
import cc.wulian.smarthomev6.support.utils.WLog;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by 上海滩小马哥 on 2017/12/21.
 * 梦想之花接口
 */

public class EntApiUnit {

    private Context context;

    public EntApiUnit(Context context) {
        this.context = context;
    }

    public interface CommonListener<T> {
        void onSuccess(T bean);

        void onFail(int code, String msg);
    }

    /**
     * 播放(从未播放时用)
     * {
     * “deviceId”: “xxxxxxxxxxx”,   设备id
     * “uId”:”xxxxxxxx”,            用户id
     * “songId”:“xxxxxxxxx”         声音id
     * }
     */
    public void doPlay(String deviceId, String songId, final EntApiUnit.CommonListener listener) {

        if (TextUtils.isEmpty(songId)){
            return;
        }

        HashMap<String, String> table = new HashMap<>();
        table.put("deviceId", deviceId);
        table.put("songId", songId);
        table.put("uId", ApiConstant.getUserID());
        JSONObject jsonObject = new JSONObject(table);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(jsonObject.toString(), time).toLowerCase();

        OkGo.post(ApiConstant.URL_DREAM_FLOWER_PLAY)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TID", ApiConstant.getTerminalId())
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .upJson(jsonObject)
                .execute(new JsonCallback<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        WLog.e("doPlay:onSuccess", responseBean.toString());
                        String resultDesc = responseBean.resultDesc;
                        String resultCode = responseBean.resultCode;
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(null);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.e("doPlay:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 调整播放状态
     * {
     * “playStatus”:”playStatus”,           0 :播放,1 暂停,2: 停止
     * “deviceId”:“xxxxxxxxx”               网关ID
     * “childDeviceId”:“xxxxxxxxx”          设备id
     * }
     */
    public void doChangePlayMode(String playStatus, String deviceId, String childDeviceId, final EntApiUnit.CommonListener listener) {

        HashMap<String, String> table = new HashMap<>();
        table.put("playStatus", playStatus);
        table.put("deviceId", deviceId);
        table.put("childDeviceId", childDeviceId);
        JSONObject jsonObject = new JSONObject(table);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(jsonObject.toString(), time).toLowerCase();

        OkGo.post(ApiConstant.URL_DREAM_FLOWER_CHANGE_PLAY_MODE)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TID", ApiConstant.getTerminalId())
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .upJson(jsonObject)
                .execute(new JsonCallback<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        WLog.e("doChangePlayMode:onSuccess", responseBean.toString());
                        String resultDesc = responseBean.resultDesc;
                        String resultCode = responseBean.resultCode;
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(null);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.e("doChangePlayMode:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 上一首下一首
     * {
     * “deviceId”: “xxxxxxxxxxx”,       设备id
     * “uId”:”xxxxxxxx”,                用户id
     * “nextOrLast”:“xxxxxxxxx”         next"，下一首，"last",上一首
     * }
     */
    public void doNextOrLast(String deviceId, String nextOrLast, final EntApiUnit.CommonListener listener) {

        HashMap<String, String> table = new HashMap<>();
        table.put("deviceId", deviceId);
        table.put("nextOrLast", nextOrLast);
        table.put("uId", ApiConstant.getUserID());
        JSONObject jsonObject = new JSONObject(table);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(jsonObject.toString(), time).toLowerCase();

        OkGo.post(ApiConstant.URL_DREAM_FLOWER_NEXT_OR_LAST)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TID", ApiConstant.getTerminalId())
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .upJson(jsonObject)
                .execute(new JsonCallback<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        WLog.e("doNextOrLast:onSuccess", responseBean.toString());
                        String resultDesc = responseBean.resultDesc;
                        String resultCode = responseBean.resultCode;
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(null);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.e("doNextOrLast:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 获取播放模式
     * {
     * “deviceId”: “xxxxxxxxxxx”,       网关ID
     * “platformId”:”xxxxxxxx”,         平台id，0喜马拉雅,1华尔思,默认为0
     * }
     */
    public void doGetMode(String deviceId, String platformId, final EntApiUnit.CommonListener<PlayModeBean> listener) {

        HashMap<String, String> params = new HashMap<>();
        params.put("deviceId", deviceId);
        params.put("platformId", platformId);

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);

        OkGo.get(ApiConstant.URL_DREAM_FLOWER_GET_MODE)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TID", ApiConstant.getTerminalId())
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new JsonCallback<ResponseBean<PlayModeBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<PlayModeBean> responseBean, Call call, Response response) {
                        WLog.e("doGetMode:onSuccess", responseBean.toString());
                        String resultDesc = responseBean.resultDesc;
                        String resultCode = responseBean.resultCode;
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.e("doGetMode:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }
}
