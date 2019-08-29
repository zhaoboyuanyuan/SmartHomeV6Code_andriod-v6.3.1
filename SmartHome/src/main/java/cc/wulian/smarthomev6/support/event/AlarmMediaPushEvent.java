package cc.wulian.smarthomev6.support.event;

/**
 * Created by zbl on 2018/2/27.
 * 摄像机带图片视频的报警推送消息
 */

public class AlarmMediaPushEvent {

    public String alarmCode;
    public String msg;
    public String deviceId;
    public String type;
    public String picUrl;

    public AlarmMediaPushEvent(String alarmCode, String msg, String deviceId, String type, String picUrl) {
        this.alarmCode = alarmCode;
        this.msg = msg;
        this.deviceId = deviceId;
        this.type = type;
        this.picUrl = picUrl;
    }
}
