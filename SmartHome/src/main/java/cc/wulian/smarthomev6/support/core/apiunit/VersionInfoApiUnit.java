package cc.wulian.smarthomev6.support.core.apiunit;

import android.content.Context;
import com.lzy.okgo.OkGo;

import java.util.HashMap;

import cc.wulian.smarthomev6.support.core.apiunit.bean.FileBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.ResponseBean;
import cc.wulian.smarthomev6.support.core.cipher.CipherUtil;
import cc.wulian.smarthomev6.support.utils.LanguageUtil;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：luzx on 2017/6/12 09:51
 * 邮箱：zhengxiang.lu@wuliangroup.com
 */
public class VersionInfoApiUnit {

    private Context context;

    public VersionInfoApiUnit(Context context) {
        this.context = context;
    }

    public interface VersionUpdateCommonListener<T> {
        void onSuccess(T bean);

        void onFail(int code, String msg);
    }

    /**
     * 获取最新版本信息
     */
    public void doGetNewestVersion(final VersionUpdateCommonListener listener) {

        HashMap<String, String> params = new HashMap<>();
        params.put("os", "3");
        params.put("appType", "1");
        if(LanguageUtil.isAllChina()){
            params.put("language", "CN");
        }else{
            params.put("language", "EN");
        }
        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);

        OkGo.get(ApiConstant.URL_API_APP_INFO)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .params(params)
                .execute(new JsonCallback<ResponseBean<FileBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<FileBean> responseBean, Call call, Response response) {
                        String resultDesc = responseBean.resultDesc;
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                    }
                });
    }
}
