package cc.wulian.smarthomev6.support.event;


import cc.wulian.smarthomev6.support.core.mqtt.bean.SceneBean;

/**
 * Created by zbl on 2017/4/11.
 * 场景信息
 */

public class SceneInfoEvent {
    public SceneBean sceneBean;

    public SceneInfoEvent(SceneBean bean) {
        this.sceneBean = bean;
    }
}
