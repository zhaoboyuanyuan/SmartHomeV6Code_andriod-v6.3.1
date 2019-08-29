package cc.wulian.smarthomev6.model;

import java.util.Arrays;

/**
 * Created by 严君 on 2017/5/12.
 *
 * 封装网关登录测试流程的数据模型
 */
public class GatewayLoginModel extends BaseProcModel {
	
	private String gatewayNumber;
	private String password;
	private String searchText;
	private String newPassword;
	private String confirmPassword;

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPasword) {
		this.newPassword = newPasword;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	/**
	 * 构造器
	 *
	 * @param actions - 动作集{@link Arrays}
	 */
	public GatewayLoginModel(int[] actions) {
		super(actions);
	}
	
	public String getGatewayNumber() {
		return gatewayNumber;
	}
	
	public void setGatewayNumber(String gatewayNumber) {
		this.gatewayNumber = gatewayNumber;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getSearchText() {
		return searchText;
	}
	
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}
}
