package cc.wulian.smarthomev6.model;

import java.util.Arrays;

/**
 * Created by 严君 on 2017/5/16.
 *
 * 封装设备类别测试流程的数据模型
 */
public class DeviceTypeModel extends BaseProcModel {
	
	private String[] allType;
	private String clickType;
	private String[] deviceList;
	
	/**
	 * 构造器
	 *
	 * @param actions - 动作集{@link Arrays}
	 */
	public DeviceTypeModel(int[] actions) {
		super(actions);
	}
	
	public String[] getAllType() {
		return allType;
	}
	
	public void setAllType(String[] allType) {
		this.allType = allType;
	}
	
	public String getClickType() {
		return clickType;
	}
	
	public void setClickType(String clickType) {
		this.clickType = clickType;
	}
	
	public String[] getDeviceList() {
		return deviceList;
	}
	
	public void setDeviceList(String[] deviceList) {
		this.deviceList = deviceList;
	}
}
