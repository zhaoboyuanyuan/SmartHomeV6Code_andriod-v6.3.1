package cc.wulian.smarthomev6.support.event;

import cc.wulian.smarthomev6.support.core.mqtt.bean.GatewayPasswordBean;

/**
 * Created by zbl on 2017/4/1.
 * 提醒用户重新输入网关密码
 */

public class GatewayPasswordChangedEvent {
    public GatewayPasswordBean bean;

    public GatewayPasswordChangedEvent(GatewayPasswordBean bean) {
        this.bean = bean;
    }
}
