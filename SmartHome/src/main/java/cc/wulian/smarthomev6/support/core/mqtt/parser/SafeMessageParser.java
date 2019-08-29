package cc.wulian.smarthomev6.support.core.mqtt.parser;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;

import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.cipher.CipherUtil;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.ResponseBean;
import cc.wulian.smarthomev6.support.event.SafeDogInitEvent;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by 上海滩小马哥 on 2018/02/02.
 */

public class SafeMessageParser implements IMQTTMessageParser {

    @Override
    public void parseMessage(int tag, String topic, String payload) {
        if (!topic.endsWith("/safe")) {
            return;
        }
        if (tag == MQTTManager.TAG_GATEWAY){
            return;
        }
        try {
            ResponseBean bean = JSON.parseObject(payload, ResponseBean.class);
            if (bean.msgContent != null) {
                String msgContent = CipherUtil.decode(bean.msgContent, ApiConstant.getAESKey());
                WLog.i("MQTTUnit:Safe:" + "Cloud" + ":", msgContent);
                JSONObject jsonObject = JSON.parseObject(msgContent);
                String code = jsonObject.getString("code");

                if (TextUtils.equals("12070", code)) {
                    onSafeDogInitFinish(msgContent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onSafeDogInitFinish(String msgContent) {
        EventBus.getDefault().post(new SafeDogInitEvent());
    }
}
