package cc.wulian.smarthomev6.model;

/**
 * Created by 赵永健 on 2017/11/3.
 */
public class DoorSensorModel extends BaseProcModel {
    private String deviceName;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public DoorSensorModel(int[] actions) {
        super(actions);
    }
}
