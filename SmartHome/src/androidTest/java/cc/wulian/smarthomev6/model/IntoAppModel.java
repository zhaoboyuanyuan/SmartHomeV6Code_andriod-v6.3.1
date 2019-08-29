package cc.wulian.smarthomev6.model;

import java.util.Arrays;

/**
 * Created by 严君 on 2017/5/9.
 *
 * 封装登录进入APP测试流程的数据模型
 */
public class IntoAppModel extends BaseProcModel {
	
	/**
	 * 构造器
	 *
	 * @param actions - 动作集{@link Arrays}
	 */
	public IntoAppModel(int[] actions) {
		super(actions);
	}
}
