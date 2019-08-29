package cc.wulian.smarthomev6.main.device.device_if02.config;

import java.io.Serializable;

/**
 * created by fzm on 2018/6/13.
 * func：WIFI红外转发器配网信息
 * email: zhongming.feng@wuliangroup.com
 */

public class IF02InfoBean implements Serializable {
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
