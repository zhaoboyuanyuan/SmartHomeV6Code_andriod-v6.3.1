package cc.wulian.smarthomev6.model;

import java.util.Arrays;

/**
 * Created by 严君 on 2017/5/15.
 *
 * 封装设备分区测试流程的数据模型
 */
public class DevicePartitionModel extends BaseProcModel {
	
	private String[] allPartitionList;
	private String[] partitionList;
	private String clickText;
	
	/**
	 * 构造器
	 *
	 * @param actions - 动作集{@link Arrays}
	 */
	public DevicePartitionModel(int[] actions) {
		super(actions);
	}
	
	public String[] getAllPartitionList() {
		return allPartitionList;
	}
	
	public void setAllPartitionList(String[] allPartitionList) {
		this.allPartitionList = allPartitionList;
	}
	
	public String[] getPartitionList() {
		return partitionList;
	}
	
	public void setPartitionList(String[] partitionList) {
		this.partitionList = partitionList;
	}
	
	public String getClickText() {
		return clickText;
	}
	
	public void setClickText(String clickText) {
		this.clickText = clickText;
	}
}
