package cc.wulian.smarthomev6.model;

import java.util.Arrays;

/**
 * Created by 严君 on 2017/5/8.
 *
 * 封装游客模式的数据模型
 */
public class VisitModel extends BaseProcModel {
	
	private int advertisingEndIndex;
	private int advertisingCurrIndex;
	private String activityName;
	private String message;
	
	/**
	 * 构造器
	 *
	 * @param actions - 动作集{@link Arrays}
	 */
	public VisitModel(int[] actions) {
		super(actions);
	}
	
	public int getAdvertisingEndIndex() {
		return advertisingEndIndex;
	}
	
	public void setAdvertisingEndIndex(int advertisingIndex) {
		this.advertisingEndIndex = advertisingIndex;
	}
	
	public int getAdvertisingCurrIndex() {
		return advertisingCurrIndex;
	}
	
	public void setAdvertisingCurrIndex(int advertisingCurrIndex) {
		this.advertisingCurrIndex = advertisingCurrIndex;
	}
	
	public String getActivityName() {
		return activityName;
	}
	
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
}
