package cc.wulian.smarthomev6.model;

import java.util.Arrays;

/**
 * Created by 严君 on 2017/5/17.
 *
 * 封装编辑场景测试流程的数据模型
 */
public class EditSceneModel extends BaseProcModel {
	
	private String sceneName;
	private int selectIconIndex;
	private int deviceState;
	private TimeModel delayTime;
	private String deviceTime;
	private String deviceName;
	private String timePicker;
	private String backOperate;
	private String searchSceneName;
	private String deleteButton;
	
	public EditSceneModel() {
		super();
	}
	
	/**
	 * 构造器
	 *
	 * @param actions - 动作集{@link Arrays}
	 */
	public EditSceneModel(int[] actions) {
		super(actions);
	}
	
	public String getSceneName() {
		return sceneName;
	}
	
	public void setSceneName(String sceneName) {
		this.sceneName = sceneName;
	}
	
	public int getSelectIconIndex() {
		return selectIconIndex;
	}
	
	public void setSelectIconIndex(int selectIconIndex) {
		this.selectIconIndex = selectIconIndex;
	}
	
	public int getDeviceState() {
		return deviceState;
	}
	
	public void setDeviceState(int deviceState) {
		this.deviceState = deviceState;
	}
	
	public TimeModel getDelayTime() {
		return delayTime;
	}
	
	public void setDelayTime(TimeModel delayTime) {
		this.delayTime = delayTime;
	}
	
	public String getDeviceTime() {
		return deviceTime;
	}
	
	public void setDeviceTime(String deviceTime) {
		this.deviceTime = deviceTime;
	}
	
	public String getDeviceName() {
		return deviceName;
	}
	
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	
	public String getTimePicker() {
		return timePicker;
	}
	
	public void setTimePicker(String timePicker) {
		this.timePicker = timePicker;
	}
	
	public String getBackOperate() {
		return backOperate;
	}
	
	public void setBackOperate(String backOperate) {
		this.backOperate = backOperate;
	}
	
	public String getSearchSceneName() {
		return searchSceneName;
	}
	
	public void setSearchSceneName(String searchSceneName) {
		this.searchSceneName = searchSceneName;
	}
	
	public String getDeleteButton() {
		return deleteButton;
	}
	
	public void setDeleteButton(String deleteButton) {
		this.deleteButton = deleteButton;
	}
}
