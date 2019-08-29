package cc.wulian.smarthomev6.proc;

import android.widget.TextView;
import cc.wulian.smarthomev6.app.AccountInfo;
import cc.wulian.smarthomev6.app.ActivitiesName;
import cc.wulian.smarthomev6.model.AccountSecurityModel;
import cc.wulian.smarthomev6.utils.EnterUtils;
import cc.wulian.smarthomev6.utils.MessageUtils;
import com.wtt.frame.robotium.Solo;

/**
 * Created by 严君 on 2017/5/10.
 *
 * 封装账号安全的测试流程
 */
public class AccountSecurityProc extends BaseProc<AccountSecurityModel> {
	
	public AccountSecurityProc(Solo solo) {
		super(solo);
	}
	
	public void changeMobileNumber() {
		AccountSecurityModel model = new AccountSecurityModel(new int[] {0, 1, 2, 3, 4, 5, 11, 12});
		model.setMobileNumber(Info.Number);
		model.setVerificationCode("123456");
		
		baseProcess(model);
	}
	
	public void changeMobileNumberAsRegistered() {
		changeAsMobileNumberError(AccountInfo.Account, Msg.NumberRegistered);
	}
	
	public void changeMobileNumberAsWrongType() {
		changeAsMobileNumberError("123", Msg.WrongNumberType);
	}
	
	public void changeMobileNumberAsEmpty() {
		changeAsMobileNumberError("", Msg.NumberNotEmpty);
	}
	
	private void changeAsMobileNumberError(String mobileNumber, String searchText) {
		AccountSecurityModel model = new AccountSecurityModel(new int[] {0, 1, 2, 3, 10});
		model.setMobileNumber(mobileNumber);
		model.setSearchText(searchText);
		
		baseProcess(model);
	}
	
	public void changeMobileNumberAsWrongCode() {
		changeAsWrongVerificationCode("1", Msg.WrongCode);
	}
	
	public void changeMobileNumberAsCodeMoreThan6() {
		changeAsWrongVerificationCode("1234567", Msg.WrongCode);
	}
	
	private void changeAsWrongVerificationCode(String verificationCode, String searchText) {
		AccountSecurityModel model = new AccountSecurityModel(new int[] {0, 1, 2, 3, 4, 5, 10});
		model.setMobileNumber(Info.RegisteredNumber);
		model.setVerificationCode(verificationCode);
		model.setSearchText(searchText);
		
		baseProcess(model);
	}
	
	public void bindMail() {
		AccountSecurityModel model = new AccountSecurityModel(new int[] {6, 7, 8, 9});
		model.setMailAddress(Info.Mail);
		
		baseProcess(model);
	}
	
	public void bindMailAsWrongType() {
		bindMailFailed("a", Msg.MailWrongType);
	}
	
	public void bindMailAsRegistered() {
		bindMailFailed(Info.Mail, Msg.MailRegistered);
	}
	
	private void bindMailFailed(String mail, String searchText) {
		AccountSecurityModel model = new AccountSecurityModel(new int[] {6, 7, 8, 10});
		model.setMailAddress(mail);
		model.setSearchText(searchText);
		
		baseProcess(model);
	}
	
	@Override
	public void process(AccountSecurityModel model, int action) {
		switch (action) {
			case 0:
				clickMobileNumber();
				break;
			case 1:
				if (!commonProc.waitForChangePhoneNumber(5000)) return;
				break;
			case 2:
				enterMobileNumber(model.getMobileNumber());
				break;
			case 3:
				clickGetVerificationCode();
				break;
			case 4:
				enterVerVerificationCode(model.getVerificationCode());
				break;
			case 5:
				clickChangeMobileNum();
				break;
			case 6:
				clickMail();
				break;
			case 7:
				enterMail(model.getMailAddress());
				break;
			case 8:
				clickMailConf();
				break;
			case 9:
				if (!commonProc.waitForConfirmMail(5000)) return;
			case 10:
				if (!solo.searchText(model.getSearchText())) {
					MessageUtils.append("未显示'" + model.getSearchText() + "'文案！");
					return;
				}
				break;
				
			case 11:
				if (!commonProc.waitForAccountSecurity(5000)) return;
				break;
			case 12:
				if (!model.getMobileNumber().equals(getMobileNum())) {
					MessageUtils.append(Msg.MobileNumberDisplayedFailed);
					return;
				}
				break;
		}
	}
	
	@Override
	public void init() {
		commonProc.login(AccountInfo.Account1, AccountInfo.Password1);
		clickToSetting();
		clickToAccountSecurity();
	}
	
	public void clickToSetting() {
		commonProc.scrollDownInMine();
		click.clickToAnotherActivity("item_setting", ActivitiesName.SettingActivity
				, Msg.IntoSettingFailed);
	}
	
	public void clickToAccountSecurity() {
		click.clickToAnotherActivity("item_setting_security", ActivitiesName.AccountSecurityActivity
				, Msg.IntoAccountSecurityFailed);
	}
	
	private void clickMobileNumber() {
		solo.clickOnView(getter.getView("item_account_security_phone"));
	}
	
	private String getMobileNum() {
		return ((TextView) getter.getView("item_account_security_phone_tv")).getText().toString();
	}
	
	private void enterMobileNumber(String num) {
		enter.enterText("et_register_phone_number", num);
	}
	
	private void enterVerVerificationCode(String code) {
		enter.enterText("et_verification", code);
	}
	
	private void clickGetVerificationCode() {
		solo.clickOnView(getter.getView("tv_get_verification"));
	}
	
	private void clickChangeMobileNum() {
		solo.clickOnView(getter.getView("tv_register_button"));
	}
	
	private void clickMail() {
		solo.clickOnView(getter.getView("item_account_security_mail"));
	}
	
	private void enterMail(String mail) {
		enter.enterText("mailbox_editBox", mail);
	}
	
	private void clickMailConf() {
		solo.clickOnView(getter.getView("confirm_mailbox"));
	}
	
	private static final class Msg {
		public static final String IntoSettingFailed = "进入设置界面失败！";
		public static final String IntoAccountSecurityFailed ="进入账号安全界面失败！";
		public static final String EnterMobileNumberFailed = "输入手机号失败！";
		public static final String EnterVerificationCodeFailed = "输入验证码失败！";
		public static final String MobileNumberDisplayedFailed = "更改手机号后，账号安全界面未正确显示更改后的手机号！";
		public static final String WrongCode = "手机验证码错误";
		public static final String WrongNumberType = "手机号格式错误";
		public static final String NumberNotEmpty = "手机号不能为空";
		public static final String NumberRegistered = "手机号已被注册";
		public static final String MailWrongType = "邮箱地址不正确：请输入正确的邮箱地址";
		public static final String MailRegistered = "邮箱已被注册";
	}
	
	private static final class Info {
		public static final String Number = "18168020465";
		public static final String RegisteredNumber = "18012965357";
		public static final String Mail = "1309817607@qq.com";
		public static final String RegisteredMail = "18012965357@163.com";
	}
}
