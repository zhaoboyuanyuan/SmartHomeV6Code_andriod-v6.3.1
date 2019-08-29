package cc.wulian.smarthomev6.support.core.apiunit;

import android.content.Context;
import android.text.TextUtils;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;

import java.io.File;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.SkinListBean;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.bean.ResponseBean;
import cc.wulian.smarthomev6.support.utils.FileUtil;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 皮肤接口
 */
public class SkinApiUnit {

    private Context context;

    public SkinApiUnit(Context context) {
        this.context = context;
    }

    public interface CommonListener<T> {
        void onSuccess(T bean);

        void onFail(int code, String msg);
    }


    /**
     * 获取皮肤列表
     */
    public void getSkinList(final CommonListener<SkinListBean> listener) {
        String appLang = MainApplication.getApplication().getLocalInfo().appLang;
        if (TextUtils.equals(appLang, "zh-cn")) {
            appLang = "zh_cn";
        } else if (TextUtils.equals(appLang, "zh-tw")) {
            appLang = "zh_tw";
        }

        String url = ApiConstant.URL_SKIN_LIST + "?" + "appLang=" + appLang + "&" + "appVersion=" + MainApplication.getApplication().getLocalInfo().appVersion;
        OkGo.get(url)
                .tag(this)
//                .upJson(jsonObject)
                .execute(new JsonCallback<ResponseBean<SkinListBean>>() {

                    @Override
                    public void onSuccess(ResponseBean<SkinListBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            String msg = context.getString(R.string.Service_Error);
                            listener.onFail(-1, msg);
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
     * 下载皮肤资源包
     */
    public void downloadSkin(String skinID, String downloadUrl, final CommonListener<File> listener) {
        try {
            String url = downloadUrl;
            OkGo.get(url)
                    .tag(this)
                    .execute(new FileCallback(FileUtil.getSkinPath() + "/", skinID + ".zip") {

                        @Override
                        public void onSuccess(File file, Call call, Response response) {
                            listener.onSuccess(file);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            listener.onFail(-1, "");
        }
    }
}
