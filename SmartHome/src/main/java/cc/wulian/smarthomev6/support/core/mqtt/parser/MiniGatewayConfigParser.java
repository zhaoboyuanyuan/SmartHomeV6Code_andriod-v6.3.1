package cc.wulian.smarthomev6.support.core.mqtt.parser;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import cc.wulian.smarthomev6.support.core.mqtt.bean.ResponseBean;
import cc.wulian.smarthomev6.support.event.MiniGatewayConfigEvent;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * created by huxc  on 2017/8/31.
 * func：  mini网关局域网配网解析
 * email: hxc242313@qq.com
 */

public class MiniGatewayConfigParser implements IMQTTMessageParser {

    private Context context;
    private String appId;

    public MiniGatewayConfigParser(Context context, String appId) {
        this.context = context;
        this.appId = appId;
    }

    @Override
    public void parseMessage(int tag, String topic, String payload) {
        if (!topic.endsWith("/config")) {
            return;
        }
        try {
            org.json.JSONObject jsonObject = new org.json.JSONObject(payload);
            String cmd = jsonObject.getString("cmd");
            if ("330".equals(cmd)) {
                onMiniGatewayWifiConfig(payload);
            }
        } catch (JSONException e) {

        }

    }

    private void onMiniGatewayWifiConfig(String msgContent) {
        EventBus.getDefault().post(new MiniGatewayConfigEvent(msgContent));
    }
}
