package cc.wulian.smarthomev6.entity;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

import cc.wulian.smarthomev6.support.utils.ConstUtil;
import cc.wulian.smarthomev6.support.utils.StringUtil;

/**
 *
 */
public class DeviceIRInfo implements Serializable{
	/**
	 * 
	 */
	public static final String STATUS_STUDYED = "1";
	public static final String STATUS_NO_STUDY = "0";
	public static final String TYPE_GENERAL = "00";
	public static final String TYPE_AIR = "01";
	public static final String TYPE_STB = "02";
	private static final long serialVersionUID = 1L;
	private String gwID;
	private String deviceID;
	private String ep;
	private String keyset;
	private String irType;
	private String code;
	private String name;
	private String status;

	public DeviceIRInfo() {

	}

	public DeviceIRInfo(JSONObject jsonObj) {
		// 红外按键码
		keyset = jsonObj.getString(ConstUtil.KEY_KEYSET);
		// 红外类型
		irType = jsonObj.getString(ConstUtil.KEY_IR_TYPE);
		// 红外控制码
		code = jsonObj.getString(ConstUtil.KEY_CODE);
		name = jsonObj.getString(ConstUtil.KEY_NAME);
		status = jsonObj.getString(ConstUtil.KEY_STUS);
	}
	
	public DeviceIRInfo clone() {
		DeviceIRInfo newInfo = new DeviceIRInfo();
		newInfo.gwID = this.gwID;
		newInfo.deviceID = this.deviceID;
		newInfo.ep = this.ep;
		newInfo.keyset = this.keyset;
		newInfo.irType = this.irType;
		newInfo.code = this.code;
		newInfo.name = this.name;
		newInfo.status = this.status;
		return newInfo;
	}
	
	public void clear() {
		this.keyset = null;
		this.irType = null;
		this.code = null;
		this.name = null;
		this.status = null;
	}

	public String getKeyset() {
		return keyset;
	}

	public void setKeyset(String keyset) {
		this.keyset = keyset;
	}
	
	public String getIRType() {
		return irType;
	}
	
	public void setIRType(String irType) {
		this.irType = irType;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getGwID() {
		return gwID;
	}

	public void setGwID(String gwID) {
		this.gwID = gwID;
	}

	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public String getEp() {
		return ep;
	}

	public void setEp(String ep) {
		this.ep = ep;
	}
	public boolean isStudy(){
		return StringUtil.equals(this.status, STATUS_STUDYED);
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof DeviceIRInfo){
			DeviceIRInfo right = (DeviceIRInfo) obj;
			return this.gwID.equals(right.getGwID()) && this.deviceID.equals(right.getDeviceID()) && this.code.equals(right.getCode());
		}
		return false;
	}

}
