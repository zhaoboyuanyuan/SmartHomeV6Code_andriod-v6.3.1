package cc.wulian.smarthomev6.support.core.apiunit;

import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.cipher.CipherUtil;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.callback.AbsCallback;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import cc.wulian.smarthomev6.support.core.apiunit.bean.EncryptBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.ResponseBean;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者: mamengchao
 * 时间: 2017/3/27 0027
 * 描述:增加解密
 * 联系方式: 805901025@qq.com
 */

public abstract class EncryptCallBack<T> extends AbsCallback<T> {

    @Override
    public T convertSuccess(Response response) throws Exception {
        EncryptBean confirmBean = null;
        final String respBody = response.body().string();
        try {
            confirmBean = JSON.parseObject(respBody, EncryptBean.class);
        } catch (Exception ex) {
            WLog.e("WLHttp", "EncryptCallBack: json解析失败!");
            if (ex != null) {
                WLog.e("WLHttp", ex.toString());
            }
        }
        response.close();
        if (confirmBean != null && !StringUtil.isNullOrEmpty(confirmBean.msgContent)) {
            String msgContent = CipherUtil.decode(confirmBean.msgContent, ApiConstant.getAESKey()).trim();
            WLog.i("WLHttp", "EncryptCallBack: " + msgContent);

            Type genType = getClass().getGenericSuperclass();
            Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
            Type type = params[0];
            if (!(type instanceof ParameterizedType)) throw new IllegalStateException("没有填写泛型参数");
            Type rawType = ((ParameterizedType) type).getRawType();
            Type typeArgument = ((ParameterizedType) type).getActualTypeArguments()[0];
            if (typeArgument == Object.class) {
                ResponseBean bean = null;
                try {
                    bean = JSON.parseObject(msgContent, type);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new TokenInvalidException();
                }
                bean.resultDesc = HttpResponseDicionary.getResultDescByCode(bean.resultCode, bean.resultDesc);
                return (T) bean;
            } else if (rawType == ResponseBean.class) {
                ResponseBean bean = null;
                try {
                    bean = JSON.parseObject(msgContent, type);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new TokenInvalidException();
                }
                bean.resultDesc = HttpResponseDicionary.getResultDescByCode(bean.resultCode, bean.resultDesc);
                return (T) bean;
            } else {
                throw new IllegalStateException("基类错误无法解析!");
            }
        } else {
            throw new IllegalStateException("JSON转换失败或confirmBean.msgContent为空！");
        }

    }

    @Override
    public void onError(Call call, Response response, Exception e) {
        super.onError(call, response, e);
        if (e != null && e instanceof TokenInvalidException) {
            MainApplication.getApplication().logout(true);
        }
    }
}
