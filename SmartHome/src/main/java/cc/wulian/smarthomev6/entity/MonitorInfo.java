package cc.wulian.smarthomev6.entity;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

import cc.wulian.smarthomev6.support.utils.ConstUtil;

public class MonitorInfo implements Serializable{
	/**
	 */
	private static final long serialVersionUID = 1L;
	private String gwID;
	private String monitorID;
	private String type;
	private String name;
	private String host;
	private String port;
	private String user;
	private String pwd;
	private String stream;

	public MonitorInfo() {

	}

	public MonitorInfo(JSONObject jsonObj) {
		gwID = jsonObj.getString(ConstUtil.KEY_GW_ID);
		monitorID = jsonObj.getString(ConstUtil.KEY_MONITOR_ID);
		type = jsonObj.getString(ConstUtil.KEY_TYPE);
		name = jsonObj.getString(ConstUtil.KEY_NAME);
		host = jsonObj.getString(ConstUtil.KEY_HOST);
		port = jsonObj.getString(ConstUtil.KEY_PORT);
		user = jsonObj.getString(ConstUtil.KEY_USER);
		pwd = jsonObj.getString(ConstUtil.KEY_PWD);
		stream = jsonObj.getString(ConstUtil.KEY_STREAM);
	}
	
	public MonitorInfo clone() {
		MonitorInfo newInfo = new MonitorInfo();
		newInfo.gwID = this.gwID;
		newInfo.monitorID = this.monitorID;
		newInfo.type = this.type;
		newInfo.name = this.name;
		newInfo.host = this.host;
		newInfo.port = this.port;
		newInfo.user = this.user;
		newInfo.pwd = this.pwd;
		newInfo.stream = this.stream;
		return newInfo;
	}
	
	public void clear() {
		this.gwID = null;
		this.monitorID = null;
		this.type = null;
		this.name = null;
		this.host = null;
		this.port = null;
		this.user = null;
		this.pwd = null;
		this.stream = null;
	}

	public String getGwID() {
		return gwID;
	}

	public void setGwID(String gwID) {
		this.gwID = gwID;
	}

	public String getMonitorID() {
		return monitorID;
	}

	public void setMonitorID(String monitorID) {
		this.monitorID = monitorID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getStream() {
		return stream;
	}

	public void setStream(String stream) {
		this.stream = stream;
	}

}
