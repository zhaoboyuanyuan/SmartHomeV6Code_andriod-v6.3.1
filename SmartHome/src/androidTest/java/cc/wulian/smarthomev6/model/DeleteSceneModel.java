package cc.wulian.smarthomev6.model;

import java.util.Arrays;

/**
 * Created by 严君 on 2017/5/17.
 */
public class DeleteSceneModel extends BaseProcModel {

	private String sceneName;
	private String buttonText;
	
	/**
	 * 构造器
	 *
	 * @param actions - 动作集{@link Arrays}
	 */
	public DeleteSceneModel(int[] actions) {
		super(actions);
	}
	
	public String getSceneName() {
		return sceneName;
	}
	
	public void setSceneName(String sceneName) {
		this.sceneName = sceneName;
	}
	
	
}
