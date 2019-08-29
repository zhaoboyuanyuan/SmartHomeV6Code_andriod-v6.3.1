package cc.wulian.smarthomev6.model;

/**
 * 金属窗帘控制器
 * Created by 赵永健 on 2017/11/9.
 */
public class MetalCurtainModel extends BaseProcModel{
    private String deviceName;
    public MetalCurtainModel(int[] actions) {
        super(actions);
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
