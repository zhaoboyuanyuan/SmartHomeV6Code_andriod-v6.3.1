package cc.wulian.smarthomev6.model;

import java.util.Arrays;

/**
 * Created by 严君 on 2017/5/9.
 *
 * 封装网关设置的数据模型
 */
public class GatewaySettingModel extends BaseProcModel {
	
	private String nikeName;
	private String nikeNameCheck;
	private String oldPassword;
	private String newPassword;
	private String newPasswordCheck;
	private String confPassword;
	private String confPasswordCheck;
	private String nikeButtonText;
	private String gatewayPassword;
	private String gatewayButtonText;
	private String searchText;
	
	/**
	 * 构造器
	 *
	 * @param actions - 动作集{@link Arrays}
	 */
	public GatewaySettingModel(int[] actions) {
		super(actions);
	}
	
	public String getNikeName() {
		return nikeName;
	}
	
	public void setNikeName(String nikeName) {
		this.nikeName = nikeName;
	}
	
	public String getNikeNameCheck() {
		return nikeNameCheck;
	}
	
	public void setNikeNameCheck(String nikeNameCheck) {
		this.nikeNameCheck = nikeNameCheck;
	}
	
	public String getOldPassword() {
		return oldPassword;
	}
	
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	
	public String getNewPassword() {
		return newPassword;
	}
	
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	
	public String getNewPasswordCheck() {
		return newPasswordCheck;
	}
	
	public void setNewPasswordCheck(String newPasswordCheck) {
		this.newPasswordCheck = newPasswordCheck;
	}
	
	public String getConfPassword() {
		return confPassword;
	}
	
	public void setConfPassword(String confPassword) {
		this.confPassword = confPassword;
	}
	
	public String getConfPasswordCheck() {
		return confPasswordCheck;
	}
	
	public void setConfPasswordCheck(String confPasswordCheck) {
		this.confPasswordCheck = confPasswordCheck;
	}
	
	public String getNikeButtonText() {
		return nikeButtonText;
	}
	
	public void setNikeButtonText(String nikeButtonText) {
		this.nikeButtonText = nikeButtonText;
	}
	
	public String getGatewayPassword() {
		return gatewayPassword;
	}
	
	public void setGatewayPassword(String gatewayPassword) {
		this.gatewayPassword = gatewayPassword;
	}
	
	public String getGatewayButtonText() {
		return gatewayButtonText;
	}
	
	public void setGatewayButtonText(String gatewayButtonText) {
		this.gatewayButtonText = gatewayButtonText;
	}
	
	public String getSearchText() {
		return searchText;
	}
	
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}
}
