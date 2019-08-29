package cc.wulian.smarthomev6.support.core.mqtt.cmd;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;

import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.mqtt.bean.RoomListBean;
import cc.wulian.smarthomev6.support.event.GetRoomListEvent;

/**
 * Created by Veev on 2017/4/14
 * Tel: 18365264930
 * QQ: 2355738466
 * Function: 获取场景列表
 */

public class Cmd_506 implements CmdHandle {
    @Override
    public void handle(String msgContent) {
        RoomListBean roomListBean = JSON.parseObject(msgContent, RoomListBean.class);
        MainApplication.getApplication().getRoomCache().addAll(roomListBean.data);
        EventBus.getDefault().post(new GetRoomListEvent(roomListBean));
    }
}
