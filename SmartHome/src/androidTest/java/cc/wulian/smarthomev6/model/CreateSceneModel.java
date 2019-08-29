package cc.wulian.smarthomev6.model;

import java.util.Arrays;

/**
 * Created by 严君 on 2017/5/17.
 *
 * 封装创建场景测试流程的数据模型
 */
public class CreateSceneModel extends BaseProcModel {
	
	private String sceneName;
	private int iconIndex;
	private boolean isSuccess;
	private String tips;
	
	/**
	 * 构造器
	 *
	 * @param actions - 动作集{@link Arrays}
	 */
	public CreateSceneModel(int[] actions) {
		super(actions);
	}
	
	public String getSceneName() {
		return sceneName;
	}
	
	public void setSceneName(String sceneName) {
		this.sceneName = sceneName;
	}
	
	public int getIconIndex() {
		return iconIndex;
	}
	
	public void setIconIndex(int iconIndex) {
		this.iconIndex = iconIndex;
	}
	
	public boolean isSuccess() {
		return isSuccess;
	}
	
	public void setSuccess(boolean success) {
		isSuccess = success;
	}
	
	public String getTips() {
		return tips;
	}
	
	public void setTips(String tips) {
		this.tips = tips;
	}
}
