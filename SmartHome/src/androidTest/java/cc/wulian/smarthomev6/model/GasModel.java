package cc.wulian.smarthomev6.model;

/**
 * Created by 赵永健 on 2017/12/8.
 */
public class GasModel extends BaseProcModel{
    private String deviceName;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public GasModel(int[] actions) {
        super(actions);
    }
}
