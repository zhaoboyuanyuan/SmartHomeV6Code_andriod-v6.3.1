package cc.wulian.smarthomev6.support.event;

/**
 * Created by zbl on 2017/11/9.
 * 分享设备状态变化
 */

public class ShareDeviceStatusChangedEvent {
    public int type = 0;

    /**
     * @param type 0 网关分享被取消，1 分享设备数量有变化（部分子设备被分享），2 子设备分享转换为整个网关分享，3 当前网关被解绑，4 体验网关自动解绑
     */
    public ShareDeviceStatusChangedEvent(int type) {
        this.type = type;
    }
}
