package cc.wulian.smarthomev6.support.core.mqtt.cmd;

import org.greenrobot.eventbus.EventBus;

import cc.wulian.smarthomev6.support.event.GetSceneBindingEvent;

/**
 * Created by yuxx on 2017/6/7.
 */

public class Cmd_514 implements CmdHandle {
    @Override
    public void handle(String msgContent) throws Exception {
        EventBus.getDefault().post(new GetSceneBindingEvent(msgContent));
    }
}
