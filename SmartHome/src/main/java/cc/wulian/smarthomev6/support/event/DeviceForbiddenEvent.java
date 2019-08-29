package cc.wulian.smarthomev6.support.event;

import cc.wulian.smarthomev6.support.core.mqtt.bean.DeviceForbiddenBean;

public class DeviceForbiddenEvent {
    public DeviceForbiddenBean bean;

    public DeviceForbiddenEvent(DeviceForbiddenBean bean) {
        this.bean = bean;
    }
}
