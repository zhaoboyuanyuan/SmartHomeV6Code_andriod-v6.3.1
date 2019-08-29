package cc.wulian.smarthomev6.support.event;


import cc.wulian.smarthomev6.support.core.mqtt.bean.DeviceInfoBean;

/**
 * Created by zbl on 2017/3/28.
 */

public class DeviceInfoChangedEvent {
    public DeviceInfoBean deviceInfoBean;

    public DeviceInfoChangedEvent(DeviceInfoBean bean) {
        this.deviceInfoBean = bean;
    }
}
