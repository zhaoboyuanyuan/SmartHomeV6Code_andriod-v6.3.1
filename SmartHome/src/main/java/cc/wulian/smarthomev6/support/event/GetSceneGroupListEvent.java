package cc.wulian.smarthomev6.support.event;

import cc.wulian.smarthomev6.support.core.mqtt.bean.SceneGroupListBean;

/**
 * Created by zbl on 2017/4/13.
 * 获取场景分组列表
 */

public class GetSceneGroupListEvent {
    public SceneGroupListBean bean;

    public GetSceneGroupListEvent(SceneGroupListBean bean) {
        this.bean = bean;
    }
}
