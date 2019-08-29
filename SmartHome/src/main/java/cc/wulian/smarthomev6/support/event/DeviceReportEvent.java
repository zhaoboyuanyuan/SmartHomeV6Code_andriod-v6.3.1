package cc.wulian.smarthomev6.support.event;

import cc.wulian.smarthomev6.support.core.device.Device;

/**
 * Created by zbl on 2017/3/28.
 */

public class DeviceReportEvent {
    public static int DEVICE_DELETE = -1; //被删除
    public static int DEVICE_ALARM = -2; //推送
    public Device device;
    public int type;

    /**
     * 设备信息上报。如果device为空，则表示需要刷新所有设备状态
     * @param device
     */
    public DeviceReportEvent(Device device) {
        this.device = device;
    }

    public DeviceReportEvent(int type, Device device) {
        this.type = type;
        this.device = device;
    }
}
