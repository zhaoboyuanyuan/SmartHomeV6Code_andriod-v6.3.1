package cc.wulian.smarthomev6.model;

/**
 * Created by 赵永健 on 2017/10/17.
 */
public class ScenceKeyModel extends BaseProcModel {
    private String scenceName;
    private String buttonText;
    private String deviceName;
    private String partitionName;
    private String scenceName1;
    private int  area;
    private String orDeviceName;

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public String getOrDeviceName() {
        return orDeviceName;
    }

    public void setOrDeviceName(String orDeviceName) {
        this.orDeviceName = orDeviceName;
    }

    public String getScenceName1() {
        return scenceName1;
    }

    public void setScenceName1(String scenceName1) {
        this.scenceName1 = scenceName1;
    }

    public String getScenceName() {
        return scenceName;
    }

    public void setScenceName(String scenceName) {
        this.scenceName = scenceName;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getPartitionName() {
        return partitionName;
    }

    public void setPartitionName(String partitionName) {
        this.partitionName = partitionName;
    }

    public ScenceKeyModel(int[] actions) {
        super(actions);
    }
}
