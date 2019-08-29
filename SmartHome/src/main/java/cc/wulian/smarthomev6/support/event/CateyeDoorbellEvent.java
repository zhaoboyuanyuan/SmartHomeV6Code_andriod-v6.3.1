package cc.wulian.smarthomev6.support.event;

import cc.wulian.smarthomev6.support.core.mqtt.bean.CateyeDoorbellBean;

/**
 * Created by zbl on 2017/5/22.
 * 门铃来电事件
 */

public class CateyeDoorbellEvent {

    public CateyeDoorbellBean cateyeDoorbellBean;

    public CateyeDoorbellEvent(CateyeDoorbellBean bean) {
        this.cateyeDoorbellBean = bean;
    }
}
