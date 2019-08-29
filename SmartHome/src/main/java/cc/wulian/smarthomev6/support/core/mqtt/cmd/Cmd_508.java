package cc.wulian.smarthomev6.support.core.mqtt.cmd;

import org.greenrobot.eventbus.EventBus;

import cc.wulian.smarthomev6.support.event.GetAutoProgramListEvent;

/**
 * Created by Veev on 2017/4/14
 * Tel: 18365264930
 * QQ: 2355738466
 * Function: 获取场景列表
 */

public class Cmd_508 implements CmdHandle {
    @Override
    public void handle(String msgContent) {
        EventBus.getDefault().post(new GetAutoProgramListEvent(msgContent));
    }
}
