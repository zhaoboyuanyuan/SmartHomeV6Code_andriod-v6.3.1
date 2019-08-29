package cc.wulian.smarthomev6.entity;

import java.io.Serializable;


public class AutoConditionInfo implements Serializable{

//	public static final String CONDITION_SCENE = "0";
//	public static final String CONDITION_TIME = "1";
//	public static final String CONDITION_DEVICE = "2";
	
	private static final long serialVersionUID = 1L;
	private String type;
	private String object;
	private String exp;
	private String des;
	
	public AutoConditionInfo(){
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

	public String getExp() {
		return exp;
	}

	public void setExp(String exp) {
		this.exp = exp;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}
}
