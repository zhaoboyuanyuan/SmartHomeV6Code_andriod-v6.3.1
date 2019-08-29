package cc.wulian.smarthomev6.support.event;

import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceBean;

/**
 * Created by zbl on 2017/6/28.
 * 提醒用户输入网关密码
 */

public class NotifyInputGatewayPasswordEvent {
    public DeviceBean deviceBean;

    public NotifyInputGatewayPasswordEvent(DeviceBean deviceBean) {
        this.deviceBean = deviceBean;
    }
}
