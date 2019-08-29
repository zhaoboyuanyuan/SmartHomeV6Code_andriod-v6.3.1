package cc.wulian.smarthomev6.support.event;


import cc.wulian.smarthomev6.support.core.mqtt.bean.RoomBean;

/**
 * Created by zbl on 2017/4/6.
 * 分区信息
 */

public class RoomInfoEvent {
    public RoomBean roomBean;

    public RoomInfoEvent(RoomBean bean) {
        this.roomBean = bean;
    }
}
