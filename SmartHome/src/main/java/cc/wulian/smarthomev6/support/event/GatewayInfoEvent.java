package cc.wulian.smarthomev6.support.event;

import cc.wulian.smarthomev6.support.core.mqtt.bean.GatewayInfoBean;

/**
 * Created by zbl on 2017/3/28.
 * 网关信息修改
 */

public class GatewayInfoEvent {

    public GatewayInfoBean bean;

    public GatewayInfoEvent(GatewayInfoBean bean) {
        this.bean = bean;
    }
}
