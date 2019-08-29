
package cc.wulian.smarthomev6.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;


/**
 * @ClassName: LcConfigWifiModel
 */
public class LcConfigWifiModel implements Serializable {
    private String deviceId;
    private String deviceType;
    private String wifiName;
    private String wifiPwd;
    private String mMac;
    private String mIp;
    private int isAddDevice;
    private int WifiConnect;// wifi方式
    private int wiredConnect;// 有线方式
    private int mPort;
    private int mStatus;
    private String scanEntry;

    public String getScanEntry() {
        return scanEntry;
    }

    public void setScanEntry(String scanEntry) {
        this.scanEntry = scanEntry;
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

    public String getWifiPwd() {
        return wifiPwd;
    }

    public void setWifiPwd(String wifiPwd) {
        this.wifiPwd = wifiPwd;
    }

    public String getmMac() {
        return mMac;
    }

    public void setmMac(String mMac) {
        this.mMac = mMac;
    }

    public String getmIp() {
        return mIp;
    }

    public void setmIp(String mIp) {
        this.mIp = mIp;
    }

    public boolean getIsAddDevice() {
        return isAddDevice == 1;
    }

    public void setIsAddDevice(boolean isAddDevice) {
        this.isAddDevice = isAddDevice ? 1 : 0;
    }

    public boolean isWifiConnect() {
        return WifiConnect == 1;
    }

    public void setWifiConnect(boolean wifiConnect) {
        WifiConnect = wifiConnect ? 1 : 0;
    }

    public boolean isWiredConnect() {
        return wiredConnect == 1;
    }

    public void setWiredConnect(boolean wiredConnect) {
        this.wiredConnect = wiredConnect ? 1 : 0;
    }

    public int getmPort() {
        return mPort;
    }

    public void setmPort(int mPort) {
        this.mPort = mPort;
    }

    public int getmStatus() {
        return mStatus;
    }

    public void setmStatus(int mStatus) {
        this.mStatus = mStatus;
    }
}
