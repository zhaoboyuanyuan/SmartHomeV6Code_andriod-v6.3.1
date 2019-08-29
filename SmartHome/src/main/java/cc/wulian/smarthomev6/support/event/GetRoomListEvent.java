package cc.wulian.smarthomev6.support.event;


import cc.wulian.smarthomev6.support.core.mqtt.bean.RoomListBean;

/**
 * Created by zbl on 2017/3/28.
 */

public class GetRoomListEvent {
    public RoomListBean roomListBean;

    public GetRoomListEvent(RoomListBean bean) {
        this.roomListBean = bean;
    }
}
