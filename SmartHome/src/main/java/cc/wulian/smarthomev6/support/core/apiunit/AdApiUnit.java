package cc.wulian.smarthomev6.support.core.apiunit;

import android.content.Context;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.request.BaseRequest;

import java.io.File;
import java.util.HashMap;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.AdvInfoListBean;
import cc.wulian.smarthomev6.entity.BannerListBean;
import cc.wulian.smarthomev6.entity.SkinListBean;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.bean.ResponseBean;
import cc.wulian.smarthomev6.support.utils.FileUtil;
import cc.wulian.smarthomev6.support.utils.LanguageUtil;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 广告接口
 */
public class AdApiUnit {

    private Context context;

    public AdApiUnit(Context context) {
        this.context = context;
    }

    public interface CommonListener<T> {
        void onSuccess(T bean);

        void onFail(int code, String msg);
    }

    private volatile boolean isGetBannerListSuccess;

    /**
     * 获取banner图片列表 <p>
     * 先获取缓存返回结果，然后从网络获取，返回结果，两个都失败，才调用onFail
     */
    public void getBannerList(final CommonListener<BannerListBean> listener) {
        String url = ApiConstant.URL_BANNERS;
        OkGo.get(url)
                .tag(this)
//                .upJson(jsonObject)
                .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)
                .execute(new JsonCallback<ResponseBean<BannerListBean>>() {

                    @Override
                    public void onBefore(BaseRequest request) {
                        isGetBannerListSuccess = false;
                    }

                    @Override
                    public void onCacheSuccess(ResponseBean<BannerListBean> responseBean, Call call) {
                        if (responseBean.isSuccess() && responseBean.data.slideshowVOs != null) {
                            listener.onSuccess(responseBean.data);
                            isGetBannerListSuccess = true;
                        }
                    }

                    @Override
                    public void onSuccess(ResponseBean<BannerListBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess() && responseBean.data.slideshowVOs != null) {
                            listener.onSuccess(responseBean.data);
                            isGetBannerListSuccess = true;
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                    }

                    @Override
                    public void onAfter(ResponseBean<BannerListBean> responseBean, Exception e) {
                        if (!isGetBannerListSuccess) {
                            listener.onFail(-1, "");
                        }
                    }
                });
    }


    public interface AdvListener {
        void onGetCache(AdvInfoListBean bean);

        void onSuccess(AdvInfoListBean bean);

        void onFail(int code, String msg);
    }

    /**
     * 获取广告页图片列表 <p>
     * 先获取缓存返回结果，然后从网络获取，返回结果，两个都失败，才调用onFail
     */
    public void getAdvList(final AdvListener listener) {
        String url = ApiConstant.URL_ADVS;

        HashMap<String, String> params = new HashMap<>();
        params.put("language", LanguageUtil.isChina() ? "zh_CN" : "en");
        params.put("partnerId", ApiConstant.APPID);
        params.put("state", "0");

        OkGo.get(url)
                .tag(this)
                .params(params)
                .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)
                .execute(new JsonCallback<ResponseBean<AdvInfoListBean>>() {

                    @Override
                    public void onBefore(BaseRequest request) {
                    }

                    @Override
                    public void onCacheSuccess(ResponseBean<AdvInfoListBean> responseBean, Call call) {
                        if (responseBean.isSuccess() && responseBean.data.advInfo != null) {
                            listener.onGetCache(responseBean.data);
                        }
                    }

                    @Override
                    public void onSuccess(ResponseBean<AdvInfoListBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess() && responseBean.data.advInfo != null) {
                            listener.onSuccess(responseBean.data);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                    }

                    @Override
                    public void onAfter(ResponseBean<AdvInfoListBean> responseBean, Exception e) {
                    }
                });
    }
}
