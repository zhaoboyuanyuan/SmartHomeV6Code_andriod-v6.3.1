package cc.wulian.smarthomev6.support.event;

import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;

/**
 * Created by zbl on 2017/3/3.
 * MQTT连接状态变化
 * state 1 成功，0 失败，2 断开
 */

public class MQTTEvent {

    public static final int STATE_CONNECT_SUCCESS = 1;
    public static final int STATE_CONNECT_FAIL = 0;
    public static final int STATE_CONNECT_LOST = 2;
    public static final int STATE_CONNECT_DISCONNECT = 3;
    public static final int STATE_CONNECT_DISCONNECT_ALL = 4;

    public int state, tag = -1;

    public MQTTEvent(int state, @MQTTManager.GatewayTag int tag) {
        this.state = state;
        this.tag = tag;
    }
}
