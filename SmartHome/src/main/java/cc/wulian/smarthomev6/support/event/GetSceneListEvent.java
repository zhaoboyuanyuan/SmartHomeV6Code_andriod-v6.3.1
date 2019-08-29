package cc.wulian.smarthomev6.support.event;


import cc.wulian.smarthomev6.support.core.mqtt.bean.SceneListBean;

/**
 * Created by zbl on 2017/3/28.
 */

public class GetSceneListEvent {
    public SceneListBean bean;

    public GetSceneListEvent(SceneListBean bean) {
        this.bean = bean;
    }
}
