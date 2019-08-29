package cc.wulian.smarthomev6.support.event;

import cc.wulian.smarthomev6.support.core.mqtt.bean.SceneVolumeGroupSet;

/**
 * Created by huxc on 2019/02/25.
 * 场景批量分组设置
 */

public class SceneVolumeGroupSetEvent {
    public SceneVolumeGroupSet bean;

    public SceneVolumeGroupSetEvent(SceneVolumeGroupSet bean) {
        this.bean = bean;
    }
}
