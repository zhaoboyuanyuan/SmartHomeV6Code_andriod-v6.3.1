package cc.wulian.smarthomev6.entity;

import com.alibaba.fastjson.JSONArray;

import java.io.Serializable;

import cc.wulian.smarthomev6.support.utils.StringUtil;


public class AutoActionInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	private String sortNum;
	private String type;
	private String object;
	private String epData;
	private String description;
	private String delay;
	private String cancelDelay;
	private JSONArray etrDataArr;
	
	public AutoActionInfo(){
		
	}
	public AutoActionInfo clone(){
		AutoActionInfo actioninfo  = new AutoActionInfo();
		actioninfo.setSortNum(sortNum);
		actioninfo.setType(type);
		actioninfo.setObject(object);
		actioninfo.setEpData(epData);
		actioninfo.setDescription(description);
		actioninfo.setDelay(delay);
		actioninfo.setCancelDelay(cancelDelay);
		actioninfo.setEtrDataArr(etrDataArr);
		return actioninfo;
	}
	public void clear(){
		this.sortNum = null;
		this.type = null;
		this.object = null;
		this.epData = null;
		this.description = null;
		this.delay = null;
		this.cancelDelay = null;
		this.etrDataArr = null;
	}

	public String getSortNum() {
		return sortNum;
	}

	public void setSortNum(String sortNum) {
		this.sortNum = sortNum;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public String getEpData() {
		return epData;
	}

	public void setEpData(String epData) {
		this.epData = epData;
	}

	public String getDelay() {
		return delay;
	}

	public void setDelay(String delay) {
		this.delay = delay;
	}
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	public String getCancelDelay() {
		return cancelDelay;
	}
	
	public void setCancelDelay(String cancelDelay) {
		this.cancelDelay = cancelDelay;
	}
	
	public JSONArray getEtrDataArr() {
		return etrDataArr;
	}
	
	public void setEtrDataArr(JSONArray etrDataArr) {
		this.etrDataArr = etrDataArr;
	}
	
	public void changeEpAndEpType(String ep,String epType){
		if(!StringUtil.isNullOrEmpty(object)){
			String[] type = this.getObject().split(">");
			this.object = type[0] + ">" + type[1] + ">" + ep +">"+ epType;
		}
		
	}
	public String getEp(){
		if(!StringUtil.isNullOrEmpty(object)){
			String[] type = this.getObject().split(">");
			if(type.length >=3){
				return type[2];
			}
		}
		return null;
	}
	
}
