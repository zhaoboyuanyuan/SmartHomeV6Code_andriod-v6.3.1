package cc.wulian.smarthomev6.proc;

import android.widget.TextView;
import cc.wulian.smarthomev6.app.AccountInfo;
import cc.wulian.smarthomev6.app.ActivitiesName;
import cc.wulian.smarthomev6.model.ChangePasswordModel;
import cc.wulian.smarthomev6.utils.MessageUtils;
import com.wtt.frame.robotium.Solo;

/**
 * Created by 赵永健 on 2017/5/10.
 *
 * 封装修改用户密码的测试流程
 */
public class ChangePasswordProc extends BaseProc<ChangePasswordModel> {
	
	private static String password = AccountInfo.Password;
	private AccountSecurityProc proc;
	
	public ChangePasswordProc(Solo solo) {
		super(solo);
		proc = new AccountSecurityProc(solo);
	}
	
	public void changePassword() {
		ChangePasswordModel model = new ChangePasswordModel(new int[] {11,12,13,15,3});
		model.setVerificationCode("123456");
		model.setNewPassword("123456abcd");

		baseProcess(model);
//		if (MessageUtils.isEmpty() && !reducePassword()) {
//			MessageUtils.append(Msg.ReducePasswordFailed);
//		}
	}
	
	public void changePasswordAsNoMatch() {
		changePassword(
				"123456"
				, "123456789"
				, Msg.PasswordAsNoMatch);
	}
	
	public void changePasswordAsNewLessThan6() {
		changePassword(
				"123456"
				, "11111"
				, Msg.PasswordLessThan6Char);
	}
	
	public void changePasswordAsNewMoreThan20() {
		ChangePasswordModel model = new ChangePasswordModel(new int[] {0 ,7, 3, 9,4});
		model.setOldPassword(AccountInfo.Password1);
		model.setNewPassword("aaaaabbbbb1111122222");
		model.setNewPasswordCheck("aaaaabbbbb111112");
		model.setConfPassword("aaaaabbbbb11111222223");
		model.setConfPasswordCheck("aaaaabbbbb111112");
		
		baseProcess(model);
		if (!reducePassword()) {
			MessageUtils.append(Msg.ReducePasswordFailed);
		}
	}
	
	public void changePasswordAsNewSameAsOld() {
		changePassword(
				"123456"
				, "123456abcd"
				, Msg.ErrorNewSameAsOld);
	}
	
	public void changePasswordAsDiffBetweenNewAndConf() {
		changePassword(
				AccountInfo.Password1
				, "111111aaaa"
//				, "222222aaaa"
				, Msg.ErrorDiffNewAndConf);
	}
	
	public void changePasswordAsNewEmpty() {
		ChangePasswordModel model = new ChangePasswordModel(new int[] {11,12,13,15,6});
		model.setVerificationCode("123456");
		model.setNewPassword("");
		baseProcess(model);
	}
	
	private void changePassword(String verificationCode, String newPassword, String searchText) {
		ChangePasswordModel model = new ChangePasswordModel(new int[] {11,12,13,15,3,5});
		model.setVerificationCode(verificationCode);
		model.setNewPassword(newPassword);
		model.setSearchText(searchText);
		baseProcess(model);
	}
	
	@Override
	public void process(ChangePasswordModel model, int action) {
		switch (action) {
			case 0:
				enterOldPassword(model.getOldPassword());
				break;
			case 1:
				enterNewPassword(model.getNewPassword());
				break;
			case 2:
				enterConfPassword(model.getConfPassword());
				break;
			case 3:
				clickConfirm();
				break;
			case 4:
				commonProc.quitLogin();
				solo.scrollToTop();
				solo.clickOnText("登录/注册");
				commonProc.waitForLogin();
				password = model.getNewPassword();
				break;
			case 5:
				if (!solo.waitForText(model.getSearchText(), 0, 1500)) {
					MessageUtils.append("未显示'" + model.getSearchText() + "'文案！");
					return;
				}
				break;
			case 6:
				if (isConfirmClickable()) {
					MessageUtils.append(Msg.ConfirmButtonClickable);
					return;
				}
				break;
			case 7:
				enterNewPassword(model.getNewPassword());
				break;
			case 8:
				enterConfPassword(model.getConfPassword());
				break;
			case 9:
				model.setNewPassword(model.getNewPasswordCheck());
				break;
			case 10:
				commonProc.quitLogin();
				break;
			case 11:
				clickVerificationCode();
				break;
			case 12:
				enterVerificationCode(model.getVerificationCode());
				break;
			case 13:
				clickOk();
				break;
			case 14:
				if(buttonClick()){
					MessageUtils.append(Msg.buttonClickFailed);
				}
				break;
			case 15:
				enterModifyPW(model.getNewPassword());
				break;
		}
	}
	
	@Override
	public void init() {
		proc.init();
		clickToChangePassword();
	}

	private void clickToChangePassword() {
		 click.clickToAnotherActivity(
				"item_account_security_account_info"
				, ActivitiesName.ChangePasswordConfirmActivity
				, Msg.IntoChangePasswordFailed);
	}
	
	private void enterOldPassword(String password) {
		 enter.enterText("old_pwd_editText", password);
	}
	
	private void enterNewPassword(String password) {
		enter.enterText("et_new_pwd", password);
	}
	
//	private void enterNewPassword(String password, String check) {
//		enter.enterText("et_new_pwd", password, check);
//	}
	
	private void enterConfPassword(String password) {
		enter.enterText("confirm_pwd_editText", password);
	}
	
//	private boolean enterConfPassword(String password, String check) {
//		return EnterUtils.KEY_SUCCESS == enter.enterText("confirm_pwd_editText", password, check);
//	}
	
	private void clickConfirm() {
		solo.clickOnView(getter.getView("confirm_pwd_button"));
	}
	
	private boolean isConfirmClickable() {
		return getter.getView("confirm_pwd_button").isEnabled();
	}


	/**
	 * 更改完密码后重新登录，修改为原始密码
	 * @return
	 */
	private boolean reducePassword() {

		TextView userNameTv = (TextView) getter.getView("username");
		if (null == userNameTv) return false;

		String account = userNameTv.getText().toString();
		if (!account.equals(AccountInfo.Account1)) return false;

		enter.enterText("password",password);

		solo.clickOnView(getter.getView("login"));
		if (!commonProc.waitForHomePage(5000)) return false;

		proc.clickToSetting();
		proc.clickToAccountSecurity();
		clickToChangePassword();
		enterOldPassword(password);
		enterNewPassword(AccountInfo.Password1);
//		if (!enterConfPassword(AccountInfo.Password1)) return false;
		clickConfirm();
		commonProc.waitForLogin();
		enter.enterText("password", AccountInfo.Password1);
		solo.clickOnView(getter.getView("login"));
		if (!commonProc.waitForHomePage(5000)) return false;
		
		return true;
	}

	/**
	 * 点击验证码
	 */
	private void clickVerificationCode(){
		solo.clickOnView(getter.getView("tv_get_verification"));
	}

	/**
	 * 输入验证码
	 * @param content
	 * @return
	 */
	private void enterVerificationCode(String content){
		enter.enterText("et_verification",content);
	}

	private void clickOk(){
		solo.clickOnText("验证成功后即可修改密码");
	}

	private boolean buttonClick(){
		return getter.getView("tv_confirm").isEnabled();
	}

	private void enterModifyPW(String content){
		enter.enterText("new_pwd_editText",content);
	}



	public static final class Msg {
		public static final String IntoChangePasswordFailed = "进入身份验证界面失败！";
		public static final String EnterOldPasswordFailed = "输入旧密码失败！";
		public static final String EnterNewPasswordFailed = "输入新密码失败！";
		public static final String EnterConfPasswordFailed = "输入确认密码失败！";
		public static final String ErrorOldPassword = "原始密码输入错误";
		public static final String ErrorNewPasswordTypeError = "新密码格式为8~20字母或数字";
		public static final String ErrorNewSameAsOld = "新密码不能和老密码相同";
		public static final String ErrorDiffNewAndConf = "两次密码不一致，请重新输入";
		public static final String ErrorNewPasswordEmpty = "新密码不能为空";
		public static final String ConfirmButtonClickable = "确定按钮未禁用！";
		public static final String ReducePasswordFailed = "还原密码失败！";
		public static final String PasswordLessThan6Char="密码长度不能小于8个字符";
		public static final String PasswordAsNoMatch="密码至少有数字、字母或符号的2种组合";
		public static final String PasswordAsIllegalChar="密码含非法字符或空格，请重新输入";
		public static final String PasswordAsWeak="数字和字母组合，密码长度至少10位";
		public static final String VerCodefailure="手机验证码失效";
		public static final String buttonClickFailed="验证成功后即可修改密码按钮未禁用";

	}
	/**
	 * 修改密码校验失败
	 */
	public  void changePasswordFail( String newPassword, String msg) {
		ChangePasswordModel model = new ChangePasswordModel(new int[] {0 ,1, 3, 5});
		model.setOldPassword(AccountInfo.Password4);
		model.setNewPassword(newPassword);
//		model.setConfPassword(newPassword);
		model.setSearchText(msg);

		baseProcess(model);
	}

	/**
	 * 修改密码校验，成功
	 */
	public void changePasswordAsSuccess(String newPassword,String newPasswordCheck) {
		ChangePasswordModel model = new ChangePasswordModel(new int[] {0 ,7, 3,9,4});
		model.setOldPassword(AccountInfo.Password4);
		model.setNewPassword(newPassword);
		model.setNewPasswordCheck(newPasswordCheck);
//		model.setConfPassword(newPasswordCheck);

		baseProcess(model);
		if (!reducePassword()) {
			MessageUtils.append(Msg.ReducePasswordFailed);
		}
	}
}
