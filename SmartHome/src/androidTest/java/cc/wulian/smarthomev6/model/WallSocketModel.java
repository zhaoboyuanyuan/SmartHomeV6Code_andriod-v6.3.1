package cc.wulian.smarthomev6.model;

/**
 * 墙面插座model
 * Created by 赵永健 on 2017/11/8.
 */
public class WallSocketModel extends BaseProcModel{
    private String deviceName;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public WallSocketModel(int[] actions) {
        super(actions);
    }
}
