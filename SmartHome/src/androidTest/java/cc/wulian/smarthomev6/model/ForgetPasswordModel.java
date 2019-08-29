package cc.wulian.smarthomev6.model;

import java.util.Arrays;

/**
 * Created by 严君 on 2017/5/9.
 *
 * 封装忘记密码测试流程的数据模型
 */
public class ForgetPasswordModel extends BaseProcModel {
	
	private String mobileNumber;
	private String password;
	private String passwordCheck;
	private String searchText;
	private String verificationCode;
	private String verificationCodeCheck;
	
	/**
	 * 构造器
	 *
	 * @param actions - 动作集{@link Arrays}
	 */
	public ForgetPasswordModel(int[] actions) {
		super(actions);
	}
	
	public String getMobileNumber() {
		return mobileNumber;
	}
	
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getPasswordCheck() {
		return passwordCheck;
	}
	
	public void setPasswordCheck(String passwordCheck) {
		this.passwordCheck = passwordCheck;
	}
	
	public String getSearchText() {
		return searchText;
	}
	
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}
	
	public String getVerificationCode() {
		return verificationCode;
	}
	
	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}
	
	public String getVerificationCodeCheck() {
		return verificationCodeCheck;
	}
	
	public void setVerificationCodeCheck(String verificationCodeCheck) {
		this.verificationCodeCheck = verificationCodeCheck;
	}
}
