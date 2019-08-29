package cc.wulian.smarthomev6.support.event;

/**
 * Created by zbl on 2017/3/28.
 * 报警推送消息
 */

public class AlarmPushEvent {

    public String alarmCode;
    public String msg;
    public String deviceId;

    public AlarmPushEvent(String alarmCode, String msg) {
        this.alarmCode = alarmCode;
        this.msg = msg;
    }

    public AlarmPushEvent(String alarmCode, String msg,  String deviceId) {
        this.alarmCode = alarmCode;
        this.msg = msg;
        this.deviceId = deviceId;
    }
}
