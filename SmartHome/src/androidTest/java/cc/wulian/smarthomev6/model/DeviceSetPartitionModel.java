package cc.wulian.smarthomev6.model;

import java.util.Arrays;

/**
 * Created by 严君 on 2017/5/15.
 *
 * 封装设置分区测试流程的数据模型
 */
public class DeviceSetPartitionModel extends BaseProcModel {
	
	/**
	 * 构造器
	 *
	 * @param actions - 动作集{@link Arrays}
	 */
	public DeviceSetPartitionModel(int[] actions) {
		super(actions);
	}
}
