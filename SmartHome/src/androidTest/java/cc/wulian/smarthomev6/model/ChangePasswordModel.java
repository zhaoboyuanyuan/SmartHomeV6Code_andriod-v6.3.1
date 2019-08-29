package cc.wulian.smarthomev6.model;

import java.util.Arrays;

/**
 * Created by 严君 on 2017/5/10.
 *
 * 封装修改账号密码测试流程的数据模型
 */
public class ChangePasswordModel extends BaseProcModel {
	
	private String oldPassword;
	private String newPassword;
	private String newPasswordCheck;
	private String confPassword;
	private String confPasswordCheck;
	private String searchText;
	private String VerificationCode;
	
	/**
	 * 构造器
	 *
	 * @param actions - 动作集{@link Arrays}
	 */
	public ChangePasswordModel(int[] actions) {
		super(actions);
	}

	public String getVerificationCode() {
		return VerificationCode;
	}

	public void setVerificationCode(String verificationCode) {
		VerificationCode = verificationCode;
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
	
	public void setNewPasswordCheck(String newPasswordCheck) {
		this.newPasswordCheck = newPasswordCheck;
	}
	
	public String getNewPasswordCheck() {
		return newPasswordCheck;
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
	
	public String getSearchText() {
		return searchText;
	}
	
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}
}
