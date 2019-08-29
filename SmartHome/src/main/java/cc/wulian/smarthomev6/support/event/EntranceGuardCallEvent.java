package cc.wulian.smarthomev6.support.event;

import cc.wulian.smarthomev6.support.core.mqtt.bean.EntranceGuardCallBean;

/**
 * created by huxc  on 2018/1/25.
 * func：罗格朗门禁来电
 * email: hxc242313@qq.com
 */

public class EntranceGuardCallEvent {
    public EntranceGuardCallBean entranceGuardCallBean;

    public EntranceGuardCallEvent(EntranceGuardCallBean bean) {
        this.entranceGuardCallBean = bean;
    }
}
