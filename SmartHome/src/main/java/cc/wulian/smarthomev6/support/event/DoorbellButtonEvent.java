package cc.wulian.smarthomev6.support.event;

import cc.wulian.smarthomev6.support.core.mqtt.bean.DoorbellBtnBean;

/**
 * created by huxc  on 2018/1/11.
 * func：门铃按钮联动4G企鹅机
 * email: hxc242313@qq.com
 */

public class DoorbellButtonEvent {
    public DoorbellBtnBean doorbellBtnBean;

    public DoorbellButtonEvent(DoorbellBtnBean bean) {
        this.doorbellBtnBean = bean;

    }
}

