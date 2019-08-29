package cc.wulian.smarthomev6.support.core.mqtt.cmd;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;

import cc.wulian.smarthomev6.support.core.mqtt.bean.GatewayPasswordBean;
import cc.wulian.smarthomev6.support.event.GatewayPasswordChangedEvent;

/**
 * Created by Veev on 2017/4/14
 * Tel: 18365264930
 * QQ: 2355738466
 * Function: 获取场景列表
 */

public class Cmd_509 implements CmdHandle {
    @Override
    public void handle(String msgContent) {
        GatewayPasswordBean bean = JSON.parseObject(msgContent, GatewayPasswordBean.class);
        EventBus.getDefault().post(new GatewayPasswordChangedEvent(bean));
    }
}
