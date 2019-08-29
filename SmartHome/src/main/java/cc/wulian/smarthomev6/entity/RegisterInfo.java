package cc.wulian.smarthomev6.entity;

import java.io.Serializable;

import cc.wulian.smarthomev6.support.utils.StringUtil;


public class RegisterInfo implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public static final String NET_TYPE_WIFI = "wifi";
	public static final String NET_TYPE_2G = "2G";
	public static final String NET_TYPE_3G = "3G";
	public static final String NET_TYPE_4G = "4G";
	
	public static final String VERSION_V3 = "V3";
	public static final String VERSION_V4 = "V4";
	public static final String VERSION_V5 = "V5";
	
	public static final String APP_TYPE_PC = "0";
	public static final String APP_TYPE_APHONE = "1";
	public static final String APP_TYPE_APAD = "2";
	public static final String APP_TYPE_TEST = "100";
	
	public static final String SDK_TOKEN_DEFAULT = "79e9484a01d27cbb1d3a4ea04214dcd9";
	/**
	 * must field and unique value,like imei
	 */
	String deviceId;
	String appID;
	String appType = APP_TYPE_APHONE;
	String appVersion = VERSION_V5 ;
	String sdkToken = SDK_TOKEN_DEFAULT;
	String netType = NET_TYPE_WIFI;
	String simId;
	String simSerialNo;
	String simCountryIso;
	String simOperatorName;
	String phoneType;
	String phoneOS;
	String lang;
	public RegisterInfo(String deviceId) {
		if(StringUtil.isNullOrEmpty(deviceId))
			throw new UnsupportedOperationException("devcieId 不可以为空");
		this.deviceId = deviceId;
		this.appID = "HD"+this.deviceId;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public String getSimId() {
		return simId;
	}

	public void setSimId(String simId) {
		this.simId = simId;
	}

	public String getSimSerialNo() {
		return simSerialNo;
	}

	public void setSimSerialNo(String simSerialNo) {
		this.simSerialNo = simSerialNo;
	}

	public String getSimCountryIso() {
		return simCountryIso;
	}

	public void setSimCountryIso(String simCountryIso) {
		this.simCountryIso = simCountryIso;
	}

	public String getSimOperatorName() {
		return simOperatorName;
	}

	public void setSimOperatorName(String simOperatorName) {
		this.simOperatorName = simOperatorName;
	}

	public String getSdkToken() {
		return sdkToken;
	}

	public void setSdkToken(String sdkToken) {
		this.sdkToken = sdkToken;
	}

	public String getAppType() {
		return appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getNetType() {
		return netType;
	}

	public void setNetType(String netType) {
		this.netType = netType;
	}

	public String getPhoneType() {
		return phoneType;
	}

	public void setPhoneType(String phoneType) {
		this.phoneType = phoneType;
	}

	public String getPhoneOS() {
		return phoneOS;
	}

	public void setPhoneOS(String phoneOS) {
		this.phoneOS = phoneOS;
	}

	public String getAppID() {
		return appID;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getLang() {
		return this.lang;
	}
}
