package cc.wulian.smarthomev6.model;

import java.util.Arrays;

/**
 * Created by 严君 on 2017/5/15.
 *
 * 封装设备重命名测试流程的数据模型
 */
public class DeviceRenameModel extends BaseProcModel {
	
//	private int listIndex;
	private String oldName;
	private String deviceName;
	private String deviceNameCheck;
	private String buttonText;
	private boolean isRename;
	private String toMore;

	public String getToMore() {
		return toMore;
	}

	public void setToMore(String toMore) {
		this.toMore = toMore;
	}

	/**
	 * 构造器
	 *
	 * @param actions - 动作集{@link Arrays}
	 */
	public DeviceRenameModel(int[] actions) {
		super(actions);
	}
	
//	public int getListIndex() {
//		return listIndex;
//	}
//
//	public void setListIndex(int listIndex) {
//		this.listIndex = listIndex;
//	}
	
	public String getOldName() {
		return oldName;
	}
	
	public void setOldName(String oldName) {
		this.oldName = oldName;
	}
	
	public String getDeviceName() {
		return deviceName;
	}
	
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	
	public String getDeviceNameCheck() {
		return deviceNameCheck;
	}
	
	public void setDeviceNameCheck(String deviceNameCheck) {
		this.deviceNameCheck = deviceNameCheck;
	}
	
	public String getButtonText() {
		return buttonText;
	}
	
	public void setButtonText(String buttonText) {
		this.buttonText = buttonText;
	}
	
	public boolean isRename() {
		return isRename;
	}
	
	public void setRename(boolean rename) {
		isRename = rename;
	}
}
