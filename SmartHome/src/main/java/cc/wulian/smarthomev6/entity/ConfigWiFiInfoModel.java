
package cc.wulian.smarthomev6.entity;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * @ClassName: ConfigWiFiInfoModel
 */
public class ConfigWiFiInfoModel implements Parcelable {
    private String deviceId;
    private String deviceType;
    private String seed;
    private String wifiName;
    private String wifiPwd;
    private String bssid;
    private String security;
    private int isAddDevice;
    // private int retry;
    // private int needReset;
    private int smartConnect;// 直连方式
    private int qrConnect;// 二维码方式
    private int wiredConnect;// 有线方式
    private int apConnect;// 软AP方式
    private int configWiFiType;// 参看iCamConstants
    private String scanType;//判断从哪个入口进（网关扫描添加还是设备扫描添加 0：网关 1：设备）
    private String asGateway;//判断是否是账号下扫描二维码作为网关使用，该参数用于最终的跳转到绑定网关界面
    private int isUseGatewayWifi;

    @Override
    public int describeContents() {
        return 0;
    }

    public ConfigWiFiInfoModel() {
        deviceId = "";
        seed = "";
        wifiName = "";
        wifiPwd = "";
        bssid = "";
        security = "";
        isAddDevice = 0;
        smartConnect = 0;
        qrConnect = 0;
        wiredConnect = 0;
        apConnect = 0;
        scanType="";
        asGateway ="";
        deviceType = "";
        isUseGatewayWifi = 0;
    }

    public static final Parcelable.Creator<ConfigWiFiInfoModel> CREATOR = new Creator<ConfigWiFiInfoModel>() {

        @Override
        public ConfigWiFiInfoModel createFromParcel(Parcel source) {
            ConfigWiFiInfoModel data = new ConfigWiFiInfoModel();
            data.deviceId = source.readString();
            data.seed = source.readString();
            data.wifiName = source.readString();
            data.wifiPwd = source.readString();
            data.bssid = source.readString();
            data.security = source.readString();
            data.isAddDevice = source.readInt();
            // data.retry = source.readInt();
            // data.needReset = source.readInt();
            data.smartConnect = source.readInt();
            data.qrConnect = source.readInt();
            data.wiredConnect = source.readInt();
            data.apConnect = source.readInt();
            data.configWiFiType = source.readInt();
            data.scanType = source.readString();
            data.asGateway = source.readString();
            data.deviceType = source.readString();
            data.isUseGatewayWifi = source.readInt();
            return data;
        }

        @Override
        public ConfigWiFiInfoModel[] newArray(int size) {
            return new ConfigWiFiInfoModel[size];
        }

    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(deviceId);
        dest.writeString(seed);
        dest.writeString(wifiName);
        dest.writeString(wifiPwd);
        dest.writeString(bssid);
        dest.writeString(security);
        dest.writeInt(isAddDevice);
        // dest.writeInt(retry);
        // dest.writeInt(needReset);
        dest.writeInt(smartConnect);
        dest.writeInt(qrConnect);
        dest.writeInt(wiredConnect);
        dest.writeInt(apConnect);
        dest.writeInt(configWiFiType);
        dest.writeString(scanType);
        dest.writeString(asGateway);
        dest.writeString(deviceType);
        dest.writeInt(isUseGatewayWifi);
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
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

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getSecurity() {
        return security;
    }

    public void setSecurity(String security) {
        this.security = security;
    }

    public int getConfigWiFiType() {
        return configWiFiType;
    }

    public void setConfigWiFiType(int configWiFiType) {
        this.configWiFiType = configWiFiType;
    }

    public boolean isAddDevice() {
        return isAddDevice == 1 ? true : false;
    }

    public void setAddDevice(boolean isAddDevice) {// 是新加入设备则设为true，如果是配置wifi，设为false
        this.isAddDevice = isAddDevice ? 1 : 0;
    }


    public void setSmartConnect(boolean isSmartConnect) {
        this.smartConnect = isSmartConnect ? 1 : 0;
    }

    public boolean getSmartConnect() {
        return smartConnect == 1 ? true : false;
    }

    public void setQrConnect(boolean isQrConnect) {
        this.qrConnect = isQrConnect ? 1 : 0;
    }

    public boolean isQrConnect() {
        return qrConnect == 1 ? true : false;
    }

    public void setWiredConnect(boolean isWiredConnect) {
        this.wiredConnect = isWiredConnect ? 1 : 0;
    }

    public boolean getWiredConnect() {
        return wiredConnect == 1 ? true : false;
    }

    public void setApConnect(boolean isApConnect) {
        this.apConnect = isApConnect ? 1 : 0;
    }

    public boolean isApConnect() {
        return apConnect == 1 ? true : false;
    }

    public String getScanType() {
        return scanType;
    }

    public void setScanType(String scanType) {
        this.scanType = scanType;
    }
    public String getAsGateway() {
        return asGateway;
    }

    public void setAsGateway(String asGateway) {
        this.asGateway = asGateway;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public boolean isUseGatewayWifi() {
        return isUseGatewayWifi == 1 ? true : false;
    }

    public void setUseGatewayWifi(boolean isUseGatewayWifi) {
        this.isUseGatewayWifi = isUseGatewayWifi ? 1 : 0;
    }
}
