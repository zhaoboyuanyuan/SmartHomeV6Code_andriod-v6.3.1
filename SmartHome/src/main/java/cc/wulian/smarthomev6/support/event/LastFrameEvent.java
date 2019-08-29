package cc.wulian.smarthomev6.support.event;

/**
 * 作者: chao
 * 时间: 2017/6/16
 * 描述: 最后一帧event
 * 联系方式: 805901025@qq.com
 */

public class LastFrameEvent {
    public String deviceId;
    public String path;

    public LastFrameEvent(String deviceId, String path) {
        this.deviceId = deviceId;
        this.path = path;
    }
}
