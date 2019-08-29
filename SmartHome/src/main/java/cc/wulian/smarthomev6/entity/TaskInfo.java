package cc.wulian.smarthomev6.entity;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

import cc.wulian.smarthomev6.support.utils.ConstUtil;
import cc.wulian.smarthomev6.support.utils.StringUtil;

public class TaskInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String gwID;
	private String version;
	private String sceneID;
	private String devID;
	private String type;
	private String ep;
	private String epType;
	private String epData;
	private String mutilLinkage;
	private String contentID;
	private String available;
	private String sensorID;
	private String sensorEp;
	private String sensorType;
	private String sensorName;
	private String sensorCond;
	private String sensorData;
	private String delay;
	private String forward;
	private String time;
	private String weekday;
	private String taskMode;

	public TaskInfo() {

	}

	public TaskInfo(JSONObject jsonObj) {
		gwID = jsonObj.getString(ConstUtil.KEY_GW_ID);
		version = jsonObj.getString(ConstUtil.KEY_VERSION);
		sceneID = jsonObj.getString(ConstUtil.KEY_SCENE_ID);
		devID = jsonObj.getString(ConstUtil.KEY_DEV_ID);
		type = jsonObj.getString(ConstUtil.KEY_TYPE);
		ep = jsonObj.getString(ConstUtil.KEY_EP);
		mutilLinkage = jsonObj.getString(ConstUtil.KEY_TASK_MUTILLINKAGE);
		epType = jsonObj.getString(ConstUtil.KEY_EP_TYPE);
		epData = jsonObj.getString(ConstUtil.KEY_EP_DATA);
		contentID = jsonObj.getString(ConstUtil.KEY_CONTENT_ID);
		available = jsonObj.getString(ConstUtil.KEY_AVAILABLE);
		sensorID = jsonObj.getString(ConstUtil.KEY_SENSOR_ID);
		sensorEp = jsonObj.getString(ConstUtil.KEY_SENSOR_EP);
		sensorType = jsonObj.getString(ConstUtil.KEY_SENSOR_TYPE);
		sensorName = jsonObj.getString(ConstUtil.KEY_SENSOR_NAME);
		sensorCond = jsonObj.getString(ConstUtil.KEY_SENSOR_COND);
		sensorData = jsonObj.getString(ConstUtil.KEY_SENSOR_DATA);
		delay = jsonObj.getString(ConstUtil.KEY_DELAY);
		forward = jsonObj.getString(ConstUtil.KEY_FORWARD);
		time = jsonObj.getString(ConstUtil.KEY_TIME);
		weekday = jsonObj.getString(ConstUtil.KEY_WEEKDAY);
		taskMode = jsonObj.getString(ConstUtil.KEY_TASK_MODE);
	}
	
	public TaskInfo clone() {
		TaskInfo newInfo = new TaskInfo();
		newInfo.gwID = this.gwID;
		newInfo.version = this.version;
		newInfo.sceneID = this.sceneID;
		newInfo.devID = this.devID;
		newInfo.type = this.type;
		newInfo.ep = this.ep;
		newInfo.epType = this.epType;
		newInfo.epData = this.epData;
		newInfo.contentID = this.contentID;
		newInfo.available = this.available;
		newInfo.sensorID = this.sensorID;
		newInfo.sensorEp = this.sensorEp;
		newInfo.sensorType = this.sensorType;
		newInfo.sensorName = this.sensorName;
		newInfo.sensorCond = this.sensorCond;
		newInfo.sensorData = this.sensorData;
		newInfo.delay = this.delay;
		newInfo.forward = this.forward;
		newInfo.time = this.time;
		newInfo.weekday = this.weekday;
		newInfo.mutilLinkage = this.mutilLinkage;
		newInfo.taskMode = this.taskMode;
		return newInfo;
	}
	
	public void clear() {
		this.gwID = null;
		this.version = null;
		this.sceneID = null;
		this.devID = null;
		this.type = null;
		this.ep = null;
		this.epType = null;
		this.epData = null;
		this.contentID = null;
		this.available = null;
		this.sensorID = null;
		this.sensorEp = null;
		this.sensorType = null;
		this.sensorName = null;
		this.sensorCond = null;
		this.sensorData = null;
		this.delay = null;
		this.forward = null;
		this.time = null;
		this.weekday = null;
		this.taskMode = null;
		this.mutilLinkage = null;
	}

	@Override
	public boolean equals( Object obj ){
		if(obj instanceof TaskInfo){
			TaskInfo task = (TaskInfo) obj;
			return 
					StringUtil.equals(gwID, task.gwID)
			 && StringUtil.equals(sceneID, task.sceneID) 
			 && StringUtil.equals(devID, task.devID) 
			 && StringUtil.equals(type, task.type) 
			 && StringUtil.equals(ep, task.ep) 
			 && StringUtil.equals(epType, task.epType) 
			 && StringUtil.equals(contentID, task.contentID) 
			 ;
		}
		else {
			return super.equals(obj);
		}
	}

	public String getGwID() {
		return gwID;
	}

	public void setGwID(String gwID) {
		this.gwID = gwID;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getSceneID() {
		return sceneID;
	}

	public void setSceneID(String sceneID) {
		this.sceneID = sceneID;
	}

	public String getDevID() {
		return devID;
	}

	public void setDevID(String devID) {
		this.devID = devID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getEpData() {
		return epData;
	}

	public void setEpData(String epData) {
		this.epData = epData;
	}

	public String getContentID() {
		return contentID;
	}

	public void setContentID(String contentID) {
		this.contentID = contentID;
	}

	public String getAvailable() {
		return available;
	}

	public void setAvailable(String available) {
		this.available = available;
	}

	public String getSensorID() {
		return sensorID;
	}

	public void setSensorID(String sensorID) {
		this.sensorID = sensorID;
	}

	public String getSensorEp() {
		return sensorEp;
	}

	public void setSensorEp(String sensorEp) {
		this.sensorEp = sensorEp;
	}

	public String getSensorType() {
		return sensorType;
	}

	public void setSensorType(String sensorType) {
		this.sensorType = sensorType;
	}

	public String getSensorName() {
		return sensorName;
	}

	public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}

	public String getSensorCond() {
		return sensorCond;
	}

	public void setSensorCond(String sensorCond) {
		this.sensorCond = sensorCond;
	}

	public String getSensorData() {
		return sensorData;
	}

	public void setSensorData(String sensorData) {
		this.sensorData = sensorData;
	}

	public String getDelay() {
		return delay;
	}

	public void setDelay(String delay) {
		this.delay = delay;
	}

	public String getForward() {
		return forward;
	}

	public void setForward(String forward) {
		this.forward = forward;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getWeekday() {
		return weekday;
	}

	public void setWeekday(String weekday) {
		this.weekday = weekday;
	}

	public String getTaskMode() {
		return taskMode;
	}

	public void setTaskMode(String taskMode) {
		this.taskMode = taskMode;
	}

	public String getMutilLinkage() {
		return mutilLinkage;
	}

	public void setMutilLinkage(String mutilLinkage) {
		this.mutilLinkage = mutilLinkage;
	}
	
}
