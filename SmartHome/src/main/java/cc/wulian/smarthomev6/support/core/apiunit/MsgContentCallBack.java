package cc.wulian.smarthomev6.support.core.apiunit;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.callback.AbsCallback;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import cc.wulian.smarthomev6.support.core.apiunit.bean.EncryptBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.ResponseBean;
import cc.wulian.smarthomev6.support.core.cipher.CipherUtil;
import cc.wulian.smarthomev6.support.utils.WLog;
import okhttp3.Response;

/**
 * 作者: mamengchao
 * 时间: 2017/3/27 0027
 * 描述:增加解密
 * 联系方式: 805901025@qq.com
 */

public abstract class MsgContentCallBack extends AbsCallback {

    @Override
    public String convertSuccess(Response response) throws Exception {
        EncryptBean confirmBean = JSON.parseObject(response.body().string(), EncryptBean.class);
        response.close();
        String msgContent = CipherUtil.decode(confirmBean.msgContent, ApiConstant.getAESKey()).trim();
        WLog.i("EncryptCallBack", msgContent);

        return msgContent;
    }

}
