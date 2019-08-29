package cc.wulian.smarthomev6.support.event;

import cc.wulian.smarthomev6.support.core.mqtt.bean.GatewayConfigBean;

/**
 * Created by Veev on 2017/5/19
 * Tel: 18365264930
 * QQ: 2355738466
 * Function: cmd  521 事件
 */

public class GatewayConfigEvent {

    public GatewayConfigBean bean;

    public GatewayConfigEvent(GatewayConfigBean bean) {
        this.bean = bean;
    }
}
