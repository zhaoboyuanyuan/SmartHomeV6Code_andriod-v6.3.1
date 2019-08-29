package cc.wulian.smarthomev6.entity;

import cc.wulian.smarthomev6.R;

/**
 * Created by Administrator on 2017/2/15 0015.
 */

public class DeviceInfoTest {

    private String deviceName;
    private String deviceLine;
    private int deviceIcon = R.drawable.ic_launcher;
    private String devicePartition;

    public Boolean getLogin() {
        return isLogin;
    }

    public void setLogin(Boolean login) {
        isLogin = login;
    }

    private Boolean isLogin ;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceLine() {
        return deviceLine;
    }

    public void setDeviceLine(String deviceLine) {
        this.deviceLine = deviceLine;
    }

    public int getDeviceIcon() {
        return deviceIcon;
    }

    public void setDeviceIcon(int deviceIcon) {
        this.deviceIcon = deviceIcon;
    }

    public String getDevicePartition() {
        return devicePartition;
    }

    public void setDevicePartition(String devicePartition) {
        this.devicePartition = devicePartition;
    }
}
