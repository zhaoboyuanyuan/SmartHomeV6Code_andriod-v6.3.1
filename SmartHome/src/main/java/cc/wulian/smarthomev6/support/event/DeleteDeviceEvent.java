package cc.wulian.smarthomev6.support.event;

/**
 * 删除设备event
 */
public class DeleteDeviceEvent {
    public int retCode;
    public String devID;

    public DeleteDeviceEvent(int retCode, String devID) {
        this.retCode = retCode;
        this.devID = devID;
    }
}
