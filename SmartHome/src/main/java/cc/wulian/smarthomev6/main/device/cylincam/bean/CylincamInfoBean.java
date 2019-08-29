package cc.wulian.smarthomev6.main.device.cylincam.bean;

import java.io.Serializable;

/**
 * created by huxc  on 2017/9/18.
 * func：  小物摄像机设备详情bean
 * email: hxc242313@qq.com
 */

public class CylincamInfoBean implements Serializable {
    private String firmwareVersion;
    private String ipAddress;

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
