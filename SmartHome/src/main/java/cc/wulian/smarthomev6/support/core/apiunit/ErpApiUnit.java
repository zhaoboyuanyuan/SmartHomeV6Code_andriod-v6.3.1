package cc.wulian.smarthomev6.support.core.apiunit;

import android.content.Context;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.util.HashMap;

import cc.wulian.smarthomev6.support.core.apiunit.bean.FileBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.ResponseBean;
import cc.wulian.smarthomev6.support.core.cipher.CipherUtil;
import okhttp3.Call;
import okhttp3.Response;

/**
 * zbl
 * ERP系统登录扫码接口
 */
public class ErpApiUnit {

    private Context context;

    public ErpApiUnit(Context context) {
        this.context = context;
    }

    public interface CommonListener<T> {
        void onSuccess(T bean);

        void onFail(int code, String msg);
    }

    /**
     * 扫码后调用接口
     */
    public void doSendRequest(String url, final CommonListener listener) {
        OkGo.get(url)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .execute(new StringCallback() {

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        listener.onSuccess(null);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                    }
                });
    }
}
