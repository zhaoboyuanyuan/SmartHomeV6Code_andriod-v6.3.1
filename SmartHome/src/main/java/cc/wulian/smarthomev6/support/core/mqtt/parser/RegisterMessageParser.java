package cc.wulian.smarthomev6.support.core.mqtt.parser;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import cc.wulian.smarthomev6.support.core.mqtt.bean.RegisterResponseBean;

/**
 * Created by zbl on 2017/3/3.
 * 登陆网关消息解析处理
 */

public class RegisterMessageParser implements IMQTTMessageParser {

    private String appId;
    private RegisterMessageParserListener listener;

    public interface RegisterMessageParserListener {
        void onRegisterSuccess(RegisterResponseBean bean);

        void onRegisterFail(RegisterResponseBean bean);
    }

    public RegisterMessageParser(String appId, RegisterMessageParserListener listener) {
        this.appId = appId;
        this.listener = listener;
    }

    @Override
    public void parseMessage(int tag, String topic, String payload) {
        if (!topic.equals("gw/wuliancc")) {
            return;
        }
        try {
            RegisterResponseBean bean = JSON.parseObject(payload.trim(), RegisterResponseBean.class);
            if (TextUtils.equals(bean.appID, appId)) {
                if ("0".equals(bean.data)) {
                    listener.onRegisterSuccess(bean);
                } else if (bean.data != null && bean.data.length() > 4) {
                    //自己的消息回来了，忽略
                } else {
                    listener.onRegisterFail(bean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
