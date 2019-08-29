package cc.wulian.smarthomev6.main.device.hisense.bean;

import java.io.Serializable;

/**
 * created by huxc  on 2018/1/3.
 * func：海信配网信息
 * email: hxc242313@qq.com
 */

public class HisInfoBean implements Serializable {
    private String deviceType;
    private String deviceId;
    private String wifiName;
    private String wifiPassword;

    public boolean isHasBind() {
        return hasBind;
    }

    public void setHasBind(boolean hasBind) {
        this.hasBind = hasBind;
    }

    private String wifiId;
    private boolean hasBind;

    public String getWifiId() {
        return wifiId;
    }

    public void setWifiId(String wifiId) {
        this.wifiId = wifiId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getWifiName() {
        return wifiName;
    }

    public void setWifiName(String wifiName) {
        this.wifiName = wifiName;
    }

    public String getWifiPassword() {
        return wifiPassword;
    }

    public void setWifiPassword(String wifiPassword) {
        this.wifiPassword = wifiPassword;
    }
}
