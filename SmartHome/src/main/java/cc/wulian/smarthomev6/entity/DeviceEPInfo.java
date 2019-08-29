package cc.wulian.smarthomev6.entity;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

import cc.wulian.smarthomev6.support.utils.ConstUtil;

/**
 */
public class DeviceEPInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ep;
	private String epType;
	private String epName;
	private String epData;
	private String epStatus;
	private String epMsg;
	private String time;

	public DeviceEPInfo() {

	}

	public DeviceEPInfo(JSONObject jsonObj) {
		ep = jsonObj.getString(ConstUtil.KEY_EP);
		epType = jsonObj.getString(ConstUtil.KEY_EP_TYPE);
		epName = jsonObj.getString(ConstUtil.KEY_EP_NAME);
		epData = jsonObj.getString(ConstUtil.KEY_EP_DATA);
		epStatus = jsonObj.getString(ConstUtil.KEY_EP_STUS);
		epMsg = jsonObj.getString(ConstUtil.KEY_EP_MSG);
		time = jsonObj.getString(ConstUtil.KEY_TIME);
	}
	
	public DeviceEPInfo clone() {
		DeviceEPInfo newInfo = new DeviceEPInfo();
		newInfo.ep = this.ep;
		newInfo.epType = this.epType;
		newInfo.epName = this.epName;
		newInfo.epData = this.epData;
		newInfo.epStatus = this.epStatus;
		newInfo.epMsg = this.epMsg;
		newInfo.time = this.time;
		return newInfo;
	}
	
	public void clear() {
		this.ep = null;
		this.epType = null;
		this.epName = null;
		this.epData = null;
		this.epStatus = null;
		this.epMsg = null;
		this.time = null;
	}

	public String getEp() {
		return ep;
	}

	public void setEp(String ep) {
		this.ep = ep;
	}

	public String getEpType() {
		return epType;
	}

	public void setEpType(String epType) {
		this.epType = epType;
	}

	public String getEpName() {
		return epName;
	}

	public void setEpName(String epName) {
		this.epName = epName;
	}

	public String getEpData() {
		return epData;
	}

	public void setEpData(String epData) {
		this.epData = epData;
	}

	public String getEpStatus() {
		return epStatus;
	}

	public void setEpStatus(String epStatus) {
		this.epStatus = epStatus;
	}

	public String getEpMsg() {
		return epMsg;
	}

	public void setEpMsg(String epMsg) {
		this.epMsg = epMsg;
	}
	
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
