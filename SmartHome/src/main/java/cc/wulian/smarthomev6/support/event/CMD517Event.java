package cc.wulian.smarthomev6.support.event;

import cc.wulian.smarthomev6.support.core.apiunit.bean.BgmCmd517Bean;

/**
 * 背景音乐 加网 517
 */

public class CMD517Event {
    public String data;
    public BgmCmd517Bean bean;

    public CMD517Event(String data, BgmCmd517Bean bean) {
        this.data = data;
        this.bean = bean;
    }
}
