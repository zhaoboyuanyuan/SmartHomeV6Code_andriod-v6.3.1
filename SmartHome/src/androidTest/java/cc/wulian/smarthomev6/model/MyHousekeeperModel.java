package cc.wulian.smarthomev6.model;

import java.util.Arrays;

/**
 * Created by 严君 on 2017/6/14.
 *
 * 封装我的管家测试流程的数据模型
 */
public class MyHousekeeperModel extends BaseMyHousekeeperModel {

	private String ortaskname;
	private String taskName;
	private String taskNameCheck;
	private String renameButton;
	private int hourInTiming;
	private int minuteInTiming;
	private int repeatDate;
	private int[] repeatDataArray;
	private String sensorName;
	private String deviceName;
	private int deviceStateInTask;
	private int deviceStateInCondition;
//	private int hourAsDelayInCondition;
//	private int minuteAsDelayInCondition;
	private TimeModel delayTimeInCondition;
//	private int hourAsDelayInTask;
//	private int minuteAsDelayInTask;
	private TimeModel delayTimeInTask;
	private String sceneName;
	private int hourAsTask;
	private int minuteAsTask;
	private String searchExecuteTime;
	private String searchRepeatDate;
	private String searchText;
	private String cancelEditButton;
	private TimeModel startTimeInSetTime;
	private TimeModel endTimeInSetTime;
	private int repeatDateInSetTime;
	private int[] repeatDataArrayInSetTime;
	private int humidity;
	private int temperature;
	
	/**
	 * 构造器
	 *
	 * @param actions - 动作集{@link Arrays}
	 */
	public MyHousekeeperModel(int[] actions) {
		super(actions);
	}

	public String getOrtaskname() {
		return ortaskname;
	}

	public void setOrtaskname(String ortaskname) {
		this.ortaskname = ortaskname;
	}

	public String getTaskName() {
		return taskName;
	}
	
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	
	public String getTaskNameCheck() {
		return taskNameCheck;
	}
	
	public void setTaskNameCheck(String taskNameCheck) {
		this.taskNameCheck = taskNameCheck;
	}
	
	public String getRenameButton() {
		return renameButton;
	}
	
	public void setRenameButton(String renameButton) {
		this.renameButton = renameButton;
	}
	
	public int getHourInTiming() {
		return hourInTiming;
	}
	
	public void setHourInTiming(int hourInTiming) {
		this.hourInTiming = hourInTiming;
	}
	
	public int getMinuteInTiming() {
		return minuteInTiming;
	}
	
	public void setMinuteInTiming(int minuteInTiming) {
		this.minuteInTiming = minuteInTiming;
	}
	
	public int getRepeatDate() {
		return repeatDate;
	}
	
	public void setRepeatDate(int repeatDate) {
		this.repeatDate = repeatDate;
	}
	
	public String getSensorName() {
		return sensorName;
	}
	
	public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}
	
	public String getDeviceName() {
		return deviceName;
	}
	
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	
	public int getDeviceStateInTask() {
		return deviceStateInTask;
	}
	
	public void setDeviceStateInTask(int deviceStateInTask) {
		this.deviceStateInTask = deviceStateInTask;
	}
	
	public int getDeviceStateInCondition() {
		return deviceStateInCondition;
	}
	
	public void setDeviceStateInCondition(int deviceStateInCondition) {
		this.deviceStateInCondition = deviceStateInCondition;
	}
	
//	public int getHourAsDelayInCondition() {
//		return hourAsDelayInCondition;
//	}
//
//	public void setHourAsDelayInCondition(int hourAsDelayInCondition) {
//		this.hourAsDelayInCondition = hourAsDelayInCondition;
//	}
//
//	public int getMinuteAsDelayInCondition() {
//		return minuteAsDelayInCondition;
//	}
//
//	public void setMinuteAsDelayInCondition(int minuteAsDelayInCondition) {
//		this.minuteAsDelayInCondition = minuteAsDelayInCondition;
//	}
//
//	public int getHourAsDelayInTask() {
//		return hourAsDelayInTask;
//	}
//
//	public void setHourAsDelayInTask(int hourAsDelayInTask) {
//		this.hourAsDelayInTask = hourAsDelayInTask;
//	}
//
//	public int getMinuteAsDelayInTask() {
//		return minuteAsDelayInTask;
//	}
//
//	public void setMinuteAsDelayInTask(int minuteAsDelayInTask) {
//		this.minuteAsDelayInTask = minuteAsDelayInTask;
//	}
	
	public TimeModel getDelayTimeInCondition() {
		return delayTimeInCondition;
	}
	
	public void setDelayTimeInCondition(TimeModel delayTimeInCondition) {
		this.delayTimeInCondition = delayTimeInCondition;
	}
	
	public TimeModel getDelayTimeInTask() {
		return delayTimeInTask;
	}
	
	public void setDelayTimeInTask(TimeModel delayTimeInTask) {
		this.delayTimeInTask = delayTimeInTask;
	}
	
	public String getSceneName() {
		return sceneName;
	}
	
	public void setSceneName(String sceneName) {
		this.sceneName = sceneName;
	}
	
	public int getHourAsTask() {
		return hourAsTask;
	}
	
	public void setHourAsTask(int hourAsTask) {
		this.hourAsTask = hourAsTask;
	}
	
	public int getMinuteAsTask() {
		return minuteAsTask;
	}
	
	public void setMinuteAsTask(int minuteAsTask) {
		this.minuteAsTask = minuteAsTask;
	}
	
	public String getSearchExecuteTime() {
		return searchExecuteTime;
	}
	
	public void setSearchExecuteTime(String searchExecuteTime) {
		this.searchExecuteTime = searchExecuteTime;
	}
	
	public String getSearchRepeatDate() {
		return searchRepeatDate;
	}
	
	public void setSearchRepeatDate(String searchRepeatDate) {
		this.searchRepeatDate = searchRepeatDate;
	}
	
	public String getSearchText() {
		return searchText;
	}
	
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}
	
	public int[] getRepeatDataArray() {
		return repeatDataArray;
	}
	
	public void setRepeatDataArray(int[] repeatDataArray) {
		this.repeatDataArray = repeatDataArray;
	}
	
	public String getCancelEditButton() {
		return cancelEditButton;
	}
	
	public void setCancelEditButton(String cancelEditButton) {
		this.cancelEditButton = cancelEditButton;
	}
	
	public TimeModel getStartTimeInSetTime() {
		return startTimeInSetTime;
	}
	
	public void setStartTimeInSetTime(TimeModel startTimeInSetTime) {
		this.startTimeInSetTime = startTimeInSetTime;
	}
	
	public TimeModel getEndTimeInSetTime() {
		return endTimeInSetTime;
	}
	
	public void setEndTimeInSetTime(TimeModel endTimeInSetTime) {
		this.endTimeInSetTime = endTimeInSetTime;
	}
	
	public int getRepeatDateInSetTime() {
		return repeatDateInSetTime;
	}
	
	public void setRepeatDataArrayInSetTime(int[] repeatDataArrayInSetTime) {
		this.repeatDataArrayInSetTime = repeatDataArrayInSetTime;
	}
	
	public int[] getRepeatDataArrayInSetTime() {
		return repeatDataArrayInSetTime;
	}
	
	public void setRepeatDateInSetTime(int repeatDateInSetTime) {
		this.repeatDateInSetTime = repeatDateInSetTime;
	}

	public int getHumidity() {
		return humidity;
	}

	public void setHumidity(int humidity) {
		this.humidity = humidity;
	}

	public int getTemperature() {
		return temperature;
	}

	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}
}
