package cc.wulian.smarthomev6.support.event;

import cc.wulian.smarthomev6.support.core.mqtt.bean.CameraOtherBindBean;

/**
 * created by huxc  on 2018/1/10.
 * func：摄像机绑定踢掉event
 * email: hxc242313@qq.com
 */

public class CameraBindEvent {
    public CameraOtherBindBean cameraOtherBindBean;
    public String data;
    public CameraBindEvent(CameraOtherBindBean bean) {
        this.cameraOtherBindBean = bean;
    }
}
