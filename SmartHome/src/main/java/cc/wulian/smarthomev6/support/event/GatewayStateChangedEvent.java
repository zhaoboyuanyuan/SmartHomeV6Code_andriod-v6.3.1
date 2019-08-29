package cc.wulian.smarthomev6.support.event;

import cc.wulian.smarthomev6.support.core.mqtt.bean.GatewayStateBean;

/**
 * Created by zbl on 2017/3/28.
 * 报警推送消息
 */

public class GatewayStateChangedEvent {

    public GatewayStateBean bean;

    public GatewayStateChangedEvent(GatewayStateBean bean) {
        this.bean = bean;
    }
}
