package cc.wulian.smarthomev6.support.core.apiunit;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.callback.AbsCallback;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.bean.ResponseBean;
import okhttp3.Response;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/14
 * 描    述：默认将返回的数据解析成需要的Bean,可以是 BaseBean，String，List，Map
 * 修订历史：
 * -
 * -
 * -
 * -
 * -我的注释都已经写的不能再多了,不要再来问我怎么获取数据对象,怎么解析集合数据了,你只要会 gson ,就会解析
 * -
 * -如果你对这里的代码原理不清楚，可以看这里的详细原理说明：https://github.com/jeasonlzy/okhttp-OkGo/blob/master/README_JSONCALLBACK.md
 * -
 * -
 * ================================================
 */
public abstract class JsonCallback<T> extends AbsCallback<T> {

//    @Override
//    public void onBefore(BaseRequest request) {
//        super.onBefore(request);
//        // 主要用于在所有请求之前添加公共的请求头或请求参数
//        // 例如登录授权的 token
//        // 使用的设备信息
//        // 可以随意添加,也可以什么都不传
//        // 还可以在这里对所有的参数进行加密，均在这里实现
//        request.headers("header1", "HeaderValue1")//
//                .params("params1", "ParamsValue1")//
//                .params("token", "3215sdf13ad1f65asd4f3ads1f");
//    }

    /**
     * 该方法是子线程处理，不能做ui相关的工作
     * 主要作用是解析网络返回的 response 对象,生产onSuccess回调中需要的数据对象
     * 这里的解析工作不同的业务逻辑基本都不一样,所以需要自己实现,以下给出的时模板代码,实际使用根据需要修改
     * <pre>
     * OkGo.get(Urls.URL_METHOD)//
     *     .tag(this)//
     *     .execute(new DialogCallback<LzyResponse<ServerModel>>(this) {
     *          @Override
     *          public void onSuccess(LzyResponse<ServerModel> responseData, Call call, Response response) {
     *              handleResponse(responseData.data, call, response);
     *          }
     *     });
     * </pre>
     */
    @Override
    public T convertSuccess(Response response) throws Exception {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        Type type = params[0];
        if (!(type instanceof ParameterizedType)) throw new IllegalStateException("没有填写泛型参数");
        Type rawType = ((ParameterizedType) type).getRawType();
        Type typeArgument = ((ParameterizedType) type).getActualTypeArguments()[0];
        if (typeArgument == Object.class) {
            ResponseBean bean = JSON.parseObject(response.body().string(), type);
            bean.resultDesc = HttpResponseDicionary.getResultDescByCode(bean.resultCode, bean.resultDesc);
            response.close();
//            if (bean.getResultCode() == 1000004) {
//                MainApplication.getApplication().logout(true);
//            }
            return (T) bean;
        } else if (rawType == ResponseBean.class) {
            ResponseBean bean = JSON.parseObject(response.body().string(), type);
            bean.resultDesc = HttpResponseDicionary.getResultDescByCode(bean.resultCode, bean.resultDesc);
            response.close();
//            if (bean.getResultCode() == 1000004) {
//                MainApplication.getApplication().logout(true);
//            }
            return (T) bean;
        } else {
            response.close();
            throw new IllegalStateException("基类错误无法解析!");
        }
    }
}