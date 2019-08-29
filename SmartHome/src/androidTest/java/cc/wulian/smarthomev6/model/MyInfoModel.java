package cc.wulian.smarthomev6.model;

import java.util.Arrays;

/**
 * Created by 严君 on 2017/5/26.
 *
 * 封装个人信息测试流程的数据模型
 */
public class MyInfoModel extends BaseProcModel {
	
	private String name;
	private String buttonText;
	
	/**
	 * 构造器
	 *
	 * @param actions - 动作集{@link Arrays}
	 */
	public MyInfoModel(int[] actions) {
		super(actions);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getButtonText() {
		return buttonText;
	}
	
	public void setButtonText(String buttonText) {
		this.buttonText = buttonText;
	}
}
