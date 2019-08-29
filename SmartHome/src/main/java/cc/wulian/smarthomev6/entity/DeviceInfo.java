package cc.wulian.smarthomev6.entity;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import cc.wulian.smarthomev6.support.utils.ConstUtil;

public class DeviceInfo implements Serializable{
	/**
	 * 
	 */
	private static final String KEY_FLOWER = "KEY_FLOWER";
	
	private static final long serialVersionUID = 1L;
	private String gwID;
	private String devID;
	private String type;
	private String category;
	private String name;
	private String roomID;
	private String isOnline;
	
	private DeviceEPInfo devEPInfo;
	private Map<String, DeviceEPInfo> devEPInfoMap = new TreeMap<String, DeviceEPInfo>();
	
	private Map<String, String> parameters = new TreeMap<String, String>();
	
	public DeviceInfo() {

	}

	public DeviceInfo(JSONObject jsonObj) {
		gwID = jsonObj.getString(ConstUtil.KEY_GW_ID);
		devID = jsonObj.getString(ConstUtil.KEY_DEV_ID);
		type = jsonObj.getString(ConstUtil.KEY_TYPE);
		category = jsonObj.getString(ConstUtil.KEY_DEV_CAT);
		name = jsonObj.getString(ConstUtil.KEY_NAME);
		roomID = jsonObj.getString(ConstUtil.KEY_ROOM_ID);
		
		String isDreamFlowerDevice = jsonObj.getString(ConstUtil.KEY_DREAM_FLOWER_DEVICE);
		if ("1".equals(isDreamFlowerDevice) ){
			parameters.put(KEY_FLOWER, isDreamFlowerDevice);
		}
	}
	
	@Override
	public DeviceInfo clone() {
		DeviceInfo newInfo = new DeviceInfo();
		newInfo.gwID = this.gwID;
		newInfo.devID = this.devID;
		newInfo.type = this.type;
		newInfo.category = this.category;
		newInfo.name = this.name;
		newInfo.roomID = this.roomID;
		return newInfo;
	}
	
	public void clear() {
		this.gwID = null;
		this.devID = null;
		this.type = null;
		this.category = null;
		this.name = null;
		this.roomID = null;
		if (this.devEPInfo != null) this.devEPInfo.clear();
		if (this.devEPInfoMap != null) this.devEPInfoMap.clear();
	}

	public String getGwID() {
		return gwID;
	}

	public void setGwID(String gwID) {
		this.gwID = gwID;
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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRoomID() {
		return roomID;
	}

	public void setRoomID(String roomID) {
		this.roomID = roomID;
	}


	public DeviceEPInfo getDevEPInfo() {
		return devEPInfo;
	}

	public void setDevEPInfo(DeviceEPInfo devEPInfo) {
		this.devEPInfo = devEPInfo;
		//putDevEPInfoByEP(devEPInfo.getEp(), devEPInfo);
	}

	public void setDevEPInfoMap(Set<DeviceEPInfo> devEPInfoSet) {
		Iterator<DeviceEPInfo> iterator = devEPInfoSet.iterator();
		while (iterator.hasNext()){
			DeviceEPInfo devEPInfo = iterator.next();
			putDevEPInfoByEP(devEPInfo.getEp(), devEPInfo);
		}
	}
	
	public DeviceEPInfo getDevEPInfoByEP(String ep){
		return devEPInfoMap.get(ep);
	}
	
	public Map<String, DeviceEPInfo> getDeviceEPInfoMap(){
		return devEPInfoMap;
	}
	
	public void putDevEPInfoByEP(String ep, DeviceEPInfo deviceEPInfo){
		devEPInfoMap.put(ep, deviceEPInfo);
	}

	public String getIsOnline() {
		return isOnline;
	}

	public void setIsOnline(String isOnline) {
		this.isOnline = isOnline;
	}

	public boolean isFlowerDevice() {
		if("1".equals(parameters.get(KEY_FLOWER))){
			return true;
		}
		return false;
	}
}
