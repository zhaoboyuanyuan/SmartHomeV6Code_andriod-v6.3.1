package cc.wulian.smarthomev6.model;

/**
 * Created by 赵永健 on 2017/5/9.
 *
 * 封装测试用例流程的基础数据模型
 */
public class BaseProcModel {
	
	/**
	 * 动作集
	 */
	private int[] actions;
	
	public BaseProcModel() {}
	
	/**
	 * 构造器
	 *
	 * @param actions - 动作集{@link java.util.Arrays}
	 */
	public BaseProcModel(int[] actions) {
		this.actions = actions;
	}
	
	/**
	 * 获取动作集
	 *
	 * @return - 动作集{@link java.util.Arrays}
	 */
	public int[] getActions() {
		return actions;
	}
	
	/**
	 * 设置动作集
	 *
	 * @param actions - 动作集{@link java.util.Arrays}
	 */
	public void setActions(int[] actions) {
		this.actions = actions;
	}
}
