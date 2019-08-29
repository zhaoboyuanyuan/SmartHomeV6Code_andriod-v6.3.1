package cc.wulian.smarthomev6.support.event;

import cc.wulian.smarthomev6.support.core.mqtt.bean.SceneGroupSet;

/**
 * Created by huxc on 2019/02/25.
 * 场景分组设置
 */

public class SceneGroupSetEvent {
    public SceneGroupSet bean;

    public SceneGroupSetEvent(SceneGroupSet bean) {
        this.bean = bean;
    }
}
