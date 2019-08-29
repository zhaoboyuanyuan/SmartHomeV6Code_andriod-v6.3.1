package cc.wulian.smarthomev6.support.event;


import cc.wulian.smarthomev6.support.core.mqtt.bean.MultiGatewayBean;

/**
 * Created by zbl on 2017/12/21.
 * 雾计算主机相关
 */

public class MultiGatewayEvent {
    public MultiGatewayBean bean;
    public String jsonData;

    public MultiGatewayEvent(MultiGatewayBean bean, String jsonData) {
        this.bean = bean;
        this.jsonData = jsonData;
    }
}
