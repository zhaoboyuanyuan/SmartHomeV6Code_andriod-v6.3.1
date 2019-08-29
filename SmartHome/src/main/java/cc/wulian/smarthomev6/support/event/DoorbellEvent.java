package cc.wulian.smarthomev6.support.event;

import cc.wulian.smarthomev6.support.core.mqtt.bean.DoorbellBean;

/**
 * Created by yuxx on 2017/6/12.
 */

public class DoorbellEvent {
    public DoorbellBean doorbellBean;
    public String data;
    public DoorbellEvent(DoorbellBean bean) {
        this.doorbellBean = bean;
    }
}
