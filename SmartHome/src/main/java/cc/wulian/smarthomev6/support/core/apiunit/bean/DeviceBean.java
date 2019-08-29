package cc.wulian.smarthomev6.support.core.apiunit.bean;

import android.os.Parcel;
import android.os.Parcelable;

import cc.wulian.smarthomev6.support.core.apiunit.bean.sso.SipInfoBean;

/**
 * Created by zbl on 2017/3/15.
 */
public class DeviceBean implements Parcelable {
    public String deviceId;
    public String model;
    public String version;
    public String softVersion;
    public String name;
    public String deviceDesc;
    public String type;
    public String alias;
    public String gatewayFlag;
    public String protocolType;
    public String protocolVersion;
    public String url;//设备图标
    public int loginFlag = 2; //1是选中，2未选中
    public String deviceIp;
    /**
     * 1 绑定，2 授权，3 授权，但是只包含部分子设备
     */
    public String relationFlag;
    /**
     * 1 正常，2 失效，密码已被修改
     */
    public String relationStatus;
    public String state;
    public String data;
    public String isPush;
    public boolean isSelect;
    public String updateTime;
    public String sdomain;
    public String location;
    public Mfr mfr;
    public SipInfoBean sipInfo;
    public String hostFlag;
    /**
     * 0 正常，1 弱密码（建议修改）
     */
    public int passwordStatus;

    //作为被分享设备的分享者信息
    public String granterUserEmail;
    public String granterUserId;
    public String granterUserNick;
    public String granterUserPhone;

    public DeviceBean() {
    }

    protected DeviceBean(Parcel in) {
        deviceId = in.readString();
        model = in.readString();
        version = in.readString();
        softVersion = in.readString();
        name = in.readString();
        deviceDesc = in.readString();
        type = in.readString();
        alias = in.readString();
        gatewayFlag = in.readString();
        protocolType = in.readString();
        protocolVersion = in.readString();
        url = in.readString();
        relationFlag = in.readString();
        state = in.readString();
        data = in.readString();
        isPush = in.readString();
        isSelect = in.readByte() != 0;
        updateTime = in.readString();
        sdomain = in.readString();
        location = in.readString();
        loginFlag = in.readInt();
        hostFlag = in.readString();
        relationStatus = in.readString();
    }

    public static final Creator<DeviceBean> CREATOR = new Creator<DeviceBean>() {
        @Override
        public DeviceBean createFromParcel(Parcel in) {
            return new DeviceBean(in);
        }

        @Override
        public DeviceBean[] newArray(int size) {
            return new DeviceBean[size];
        }
    };

    public boolean isShared() {
        return !"1".equals(relationFlag);
    }

    /**
     * @return 是否是部分子设备分享
     */
    public boolean isPartialShared() {
        return "3".equals(relationFlag);
    }

    public String getIsPush() {
        return isPush;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSoftVersion() {
        return softVersion;
    }

    public void setSoftVersion(String softVersion) {
        this.softVersion = softVersion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeviceDesc() {
        return deviceDesc;
    }

    public void setDeviceDesc(String deviceDesc) {
        this.deviceDesc = deviceDesc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getGatewayFlag() {
        return gatewayFlag;
    }

    public void setGatewayFlag(String gatewayFlag) {
        this.gatewayFlag = gatewayFlag;
    }

    public String getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(String protocolType) {
        this.protocolType = protocolType;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public String getRelationFlag() {
        return relationFlag;
    }

    public void setRelationFlag(String adminFlag) {
        this.relationFlag = adminFlag;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Mfr getMfr() {
        return mfr;
    }

    public void setMfr(Mfr mfr) {
        this.mfr = mfr;
    }

    public String getHostFlag() {
        return hostFlag;
    }

    public void setHostFlag(String hostFlag) {
        this.hostFlag = hostFlag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(deviceId);
        dest.writeString(model);
        dest.writeString(version);
        dest.writeString(softVersion);
        dest.writeString(name);
        dest.writeString(deviceDesc);
        dest.writeString(type);
        dest.writeString(alias);
        dest.writeString(gatewayFlag);
        dest.writeString(protocolType);
        dest.writeString(protocolVersion);
        dest.writeString(url);
        dest.writeString(relationFlag);
        dest.writeString(state);
        dest.writeString(data);
        dest.writeString(isPush);
        dest.writeByte((byte) (isSelect ? 1 : 0));
        dest.writeString(updateTime);
        dest.writeString(sdomain);
        dest.writeString(location);
        dest.writeInt(loginFlag);
        dest.writeString(hostFlag);
        dest.writeString(relationStatus);
    }

    public static class Mfr {
        public String mfrName;
    }

    public boolean getIsSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

    /**
     * 判断设备是否为网关
     *
     * @return
     */
    public boolean isGateway() {
        if (type != null) {
            return type.startsWith("GW");
        }
        return false;
    }
}
