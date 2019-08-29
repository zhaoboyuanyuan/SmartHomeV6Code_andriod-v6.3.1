package cc.wulian.smarthomev6.model;

import java.util.Arrays;

/**
 * Created by 严君 on 2017/5/16.
 *
 * 封装设备管理测试流程的数据模型
 */
public class DeviceManagerModel extends BaseProcModel {
	
	private String partitionName;
	private String partitionNameCheck;
	private String createButtonText;
	private String action;
	private String changeButtonText;
	private String deleteButtonText;
	private String itemName;
	private String dialogTitle;
	private String operateId;
	private String waitForText;
	
	/**
	 * 构造器
	 *
	 * @param actions - 动作集{@link Arrays}
	 */
	public DeviceManagerModel(int[] actions) {
		super(actions);
	}
	
	public String getPartitionName() {
		return partitionName;
	}
	
	public void setPartitionName(String partitionName) {
		this.partitionName = partitionName;
	}
	
	public String getPartitionNameCheck() {
		return partitionNameCheck;
	}
	
	public void setPartitionNameCheck(String partitionNameCheck) {
		this.partitionNameCheck = partitionNameCheck;
	}
	
	public String getCreateButtonText() {
		return createButtonText;
	}
	
	public void setCreateButtonText(String createButtonText) {
		this.createButtonText = createButtonText;
	}
	
	public String getAction() {
		return action;
	}
	
	public void setAction(String action) {
		this.action = action;
	}
	
	public String getChangeButtonText() {
		return changeButtonText;
	}
	
	public void setChangeButtonText(String changeButtonText) {
		this.changeButtonText = changeButtonText;
	}
	
	public String getDeleteButtonText() {
		return deleteButtonText;
	}
	
	public void setDeleteButtonText(String deleteButtonText) {
		this.deleteButtonText = deleteButtonText;
	}
	
	public String getItemName() {
		return itemName;
	}
	
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	
	public String getDialogTitle() {
		return dialogTitle;
	}
	
	public void setDialogTitle(String dialogTitle) {
		this.dialogTitle = dialogTitle;
	}
	
	public String getOperateId() {
		return operateId;
	}
	
	public void setOperateId(String operateId) {
		this.operateId = operateId;
	}
	
	public String getWaitForText() {
		return waitForText;
	}
	
	public void setWaitForText(String waitForText) {
		this.waitForText = waitForText;
	}
}
