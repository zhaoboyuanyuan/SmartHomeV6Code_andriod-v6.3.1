package cc.wulian.smarthomev6.support.event;

/**
 * Created by 上海滩小马哥 on 2017/12/12.
 * 红外转发22 事件
 */

public class Device22ConfigEvent {
    public String deviceId;
    public int mode;
    public int operType;
    public String data;

    public Device22ConfigEvent(String deviceId, int mode, int operType, String data) {
        this.deviceId = deviceId;
        this.mode = mode;
        this.operType = operType;
        this.data = data;
    }
}
