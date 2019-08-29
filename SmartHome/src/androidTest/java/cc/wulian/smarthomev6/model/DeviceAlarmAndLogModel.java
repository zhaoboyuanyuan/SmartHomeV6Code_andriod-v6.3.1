package cc.wulian.smarthomev6.model;

/**
 * Created by 赵永健 on 2017/11/2.
 */
public class DeviceAlarmAndLogModel extends BaseProcModel {
    private String deviceName;
    private String toMore;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getToMore() {
        return toMore;
    }

    public void setToMore(String toMore) {
        this.toMore = toMore;
    }

    public DeviceAlarmAndLogModel(int[] actions) {
        super(actions);
    }
}
