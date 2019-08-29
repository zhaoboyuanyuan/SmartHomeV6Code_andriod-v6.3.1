package cc.wulian.smarthomev6.model;

/**
 * Created by 赵永健 on 2017/10/20.
 */
public class CurtainModel extends BaseProcModel {
    private String buttonTxt;
    private String deviceName;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getButtonTxt() {
        return buttonTxt;
    }

    public void setButtonTxt(String buttonTxt) {
        this.buttonTxt = buttonTxt;
    }

    public CurtainModel(int[] actions) {
        super(actions);
    }
}
