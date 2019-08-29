package cc.wulian.smarthomev6.model;

import java.util.Arrays;

/**
 * Created by 严君 on 2017/6/13.
 *
 * 封装我的管家基本操作的数据模型
 */
public class BaseMyHousekeeperModel extends BaseProcModel {
	/**
	 * 构造器
	 *
	 * @param actions - 动作集{@link Arrays}
	 */
	public BaseMyHousekeeperModel(int[] actions) {
		super(actions);
	}
}
