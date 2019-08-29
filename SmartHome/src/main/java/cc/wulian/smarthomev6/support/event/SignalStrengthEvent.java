package cc.wulian.smarthomev6.support.event;

import cc.wulian.smarthomev6.support.core.mqtt.bean.SignalStrengthBean;

public class SignalStrengthEvent {
    public SignalStrengthBean bean;

    public SignalStrengthEvent(SignalStrengthBean bean) {
        this.bean = bean;
    }
}
