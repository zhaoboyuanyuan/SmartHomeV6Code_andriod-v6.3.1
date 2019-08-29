package cc.wulian.smarthomev6.support.event;

import cc.wulian.smarthomev6.support.core.mqtt.bean.CameraHouseKeeperBean;

/**
 * created by huxc  on 2018/1/25.
 * func：
 * email: hxc242313@qq.com
 */

public class CameraHouseKeeperEvent {
    public CameraHouseKeeperBean cameraHouseKeeperBean;

    public CameraHouseKeeperEvent(CameraHouseKeeperBean bean) {
        this.cameraHouseKeeperBean = bean;
    }
}
