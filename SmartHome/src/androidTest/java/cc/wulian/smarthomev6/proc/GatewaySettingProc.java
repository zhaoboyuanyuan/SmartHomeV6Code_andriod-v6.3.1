package cc.wulian.smarthomev6.proc;

import android.widget.TextView;
import cc.wulian.smarthomev6.app.AccountInfo;
import cc.wulian.smarthomev6.app.ActivitiesName;
import cc.wulian.smarthomev6.app.ControlInfo;
import cc.wulian.smarthomev6.app.GatewayInfo;
import cc.wulian.smarthomev6.model.GatewaySettingModel;
import cc.wulian.smarthomev6.utils.EnterUtils;
import cc.wulian.smarthomev6.utils.MessageUtils;
import com.wtt.frame.robotium.Solo;

import static junit.framework.Assert.assertTrue;

/**
 * Created by 赵永健 on 2017/5/31.
 *
 * 封装网关设置的测试流程
 */
public class GatewaySettingProc extends BaseProc<GatewaySettingModel> {
	
	private static String password = GatewayInfo.Gateway2.password1;
	
	public GatewaySettingProc(Solo solo) {
		super(solo);
	}
	
	public void changeGatewayPasswordAs6() {
		changePasswordSuccess("123456");
	}
	
	public void changeGatewayPasswordAs15() {
		changePasswordSuccess("aaaaabbbbb11111");
	}
	
	private void changePasswordSuccess(String newPassword) {
		GatewaySettingModel model = new GatewaySettingModel(new int[] {0 ,1, 2, 4, 6});
		model.setOldPassword(GatewayInfo.Gateway2.password1);
		model.setNewPassword(newPassword);
		model.setConfPassword(newPassword);
		
		baseProcess(model);
		if (MessageUtils.isEmpty() && !reducePassword(false)) {
			MessageUtils.append(Msg.ReducePasswordFailed + "当前密码 - " + password);
		}
	}
	
	public void changeGatewayPasswordAsGatewayLogin() {
		if (!LoginAsGateway()) {
			MessageUtils.append(Msg.LoginAsGatewayFailed);
			return;
		}
		GatewaySettingModel model = new GatewaySettingModel(new int[] {0 ,1, 2, 7});
		model.setOldPassword(GatewayInfo.Gateway2.password1);
		model.setNewPassword("123456aaaa");
		
		baseProcess(model);
		if (MessageUtils.isEmpty() && !reducePassword(true)) {
			MessageUtils.append(Msg.ReducePasswordFailed + "当前密码 - " + password);
		}
	}
	
	public void changeAsOldPasswordWrong() {
		changeGatewayPasswordAsFail(
				"123456qwert"
				, "111111aaaa"
				, Msg.originalPasswordFail);
	}
	
	public void changeAsNewPasswordLessThan6() {
		changeGatewayPasswordAsFail(
				GatewayInfo.Gateway2.password1
				, "11111"
				, Msg.PasswordLessThan6Char);
	}
	
	public void changeAsNewPasswordMoreThan20() {
		GatewaySettingModel model = new GatewaySettingModel(new int[] {0, 1, 3, 10, 6});
		model.setOldPassword(GatewayInfo.Gateway2.password1);
		model.setNewPassword("aaaaabbbbb11111222223");
		model.setNewPasswordCheck("aaaaabbbbb1111122222");
		model.setSearchText("新密码格式为6~20字母或数字");
		
		baseProcess(model);
		if (MessageUtils.isEmpty() && !reducePassword(true)) {
			MessageUtils.append(Msg.ReducePasswordFailed + "当前密码 - " + password);
		}
	}
	
//	public void changeAsDiffFormNewAndConf() {
//		changeGatewayPasswordAsFail(
//				GatewayInfo.Gateway2.password1
//				, "11111aaaaa"
//				, "111112"
//				, "两次密码不一致，请重新输入");
//	}
//
	public void changeAsNewSameAsOld() {
		changeGatewayPasswordAsFail(
				GatewayInfo.Gateway2.password1
				, GatewayInfo.Gateway2.password1
				, Msg.ErrorNewSameAsOld);
	}
	
	private void changeGatewayPasswordAsFail(String old, String curr, String searchText) {
		GatewaySettingModel model = new GatewaySettingModel(new int[] {0, 1, 2, 8, 9});
		model.setOldPassword(old);
		model.setNewPassword(curr);
		model.setSearchText(searchText);
		
		baseProcess(model);
	}
	
	public void changeNikeNameLessThan15() {
		changeNikeName("aaaaabbbbbcccc");
	}
	
	public void changeNikeNameEquals15() {
		changeNikeName("aaaaabbbbbccccc");
	}
	
	private void changeNikeName(String nikeName) {
		GatewaySettingModel model = new GatewaySettingModel(new int[] {11, 12, 13, 15, 16, 17});
		model.setNikeName(nikeName);
		model.setNikeButtonText("确定");
		
		baseProcess(model);
		
		if (MessageUtils.isEmpty() && !reduceNikeName()) {
			MessageUtils.append(Msg.ReduceNikeNameFailed);
		}
	}
	
	public void changeNikeNameMoreThan15() {
		GatewaySettingModel model = new GatewaySettingModel(new int[] {11, 12, 14, 15, 16, 18});
		model.setNikeName("aaaaabbbbbcccccd");
		model.setNikeNameCheck("aaaaabbbbbcccccd");
		model.setNikeButtonText("确定");
		
		baseProcess(model);
		if (MessageUtils.isEmpty() && !reduceNikeName()) {
			MessageUtils.append(Msg.ReduceNikeNameFailed);
		}
	}
	
	@Override
	public void process(GatewaySettingModel model, int action) {
		switch (action) {
			case 0:
				clickToChangePassword();
				break;
			case 1:
				enterOldPassword(model.getOldPassword());
				break;
			case 2:
				enterNewPassword(model.getNewPassword());
				break;
			case 3:
				enterNewPassword(model.getNewPassword(), model.getNewPasswordCheck());
				break;
			case 4:
				enterConfPassword(model.getConfPassword());
				break;
			case 5:
				enterConfPassword(model.getConfPassword(), model.getConfPasswordCheck());
				break;
			case 6:
				clickChangeConf(model.getNewPassword());
				break;
			case 7:
				clickChangeConfAsGatewayLogin(model.getNewPassword());
				break;
			case 8:
				clickConfirmBtn();
				break;
			case 9:
				if (solo.searchText(model.getSearchText())==false) {
					MessageUtils.append("未搜索到\"" + model.getSearchText() + "\"！");
					return;
				}
				break;
			case 10:
				model.setNewPassword(model.getNewPasswordCheck());
				break;
			case 11:
				clickChangeNikeName();
				break;
			case 12:
				if (!solo.waitForText("修改昵称", 0 ,2000)) {
					MessageUtils.append(Msg.NotDisplayChangeNameDialog);
					return;
				}
				break;
			case 13:
				enterNikeName(model.getNikeName());
				break;
			case 14:
				enterNikeName(model.getNikeName(), model.getNikeNameCheck());
				break;
			case 15:
				clickButtonInDialog(model.getNikeButtonText());
				break;
			case 16:
				solo.sleep(500);
				if (solo.waitForText("修改昵称", 0, 2000)) {
					MessageUtils.append(Msg.NotCloseChangeNameDialog);
					return;
				}
				break;
			case 17:
				if (!model.getNikeName().equals(getNikeName())) {
					MessageUtils.append(Msg.NikeNameDiffAfterChange);
					return;
				}
				break;
			case 18:
				if (!getNikeName().equals(model.getNikeNameCheck())) {
					MessageUtils.append(Msg.NikeNameDiffAfterChange);
					return;
				}
				break;
			case 19:
				commonProc.quitLogin();
				break;
		}
	}
	
	@Override
	public void init() {
		commonProc.login(AccountInfo.Account1, AccountInfo.Password1);
		clickToGatewayCenter();
		clickToGatewaySetting();
	}

	public void clickToGatewayCenter() {
		 click.clickToAnotherActivity(ControlInfo.item_gateway_center
				, ActivitiesName.GatewayCenterActivity, Msg.IntoGatewayCenterFailed);
	}

	public void  clickToGatewaySetting() {
		 click.clickToAnotherActivity(ControlInfo.item_gateway_center_setting
				, ActivitiesName.GatewaySettingActivity, Msg.IntoGatewaySettingFailed);
	}
	
	private void clickToChangePassword() {
		 click.clickToAnotherActivity(ControlInfo.item_gateway_setting_info
				, ActivitiesName.ChangePasswordActivity, Msg.IntoChangePasswordFailed);
	}
	
	private void enterOldPassword(String oldPassword) {
		enter.enterText(ControlInfo.old_pwd_editText, oldPassword);
	}
	
	private void enterNewPassword(String newPassword) {
		enter.enterText(ControlInfo.et_new_pwd, newPassword);
	}
	
	private void enterNewPassword(String newPassword, String check) {
		enter.enterText(ControlInfo.et_new_pwd, newPassword);
	}
	
	private void enterConfPassword(String confPassword) {
		enter.enterText(ControlInfo.confirm_pwd_editText, confPassword);
	}

	private void enterConfPassword(String confPassword, String check) {
		enter.enterText(ControlInfo.confirm_pwd_editText, confPassword);
	}
	
	private void clickConfirmBtn() {
		solo.clickOnView(getter.getView(ControlInfo.confirm_pwd_button));
	}
	
	private void clickChangeNikeName() {
		solo.clickOnView(getter.getView(ControlInfo.item_gateway_setting_name));
	}
	
	private void enterNikeName(String nikeName) {
		enter.enterText(ControlInfo.et_user_info, nikeName);
	}
	
	private void enterNikeName(String nikeName, String check) {
		enter.enterText(ControlInfo.et_user_info, nikeName);
	}
	
	private void clickButtonInDialog(String buttonText) {
		solo.clickOnButton(buttonText);
	}
	
	private String getNikeName() {
		return ((TextView) getter.getView(ControlInfo.tv_gateway_setting_name)).getText().toString();
	}
	
	private void clickChangeConf(String newPassword) {
		clickConfirmBtn();
		if (!solo.waitForText("请重新选择网关", 0, 5000)) {
			MessageUtils.append(Msg.NotDisplaySuccessDialog);
			return;
		}
		password = newPassword;
		solo.clickOnButton("确定");
		if (!commonProc.waitForGatewayList(2000)) return;
		solo.clickOnText(GatewayInfo.Gateway2.nikeName);
		if (!solo.waitForText("输入密码", 0, 5000)) {
			MessageUtils.append(Msg.NotDisplayEnterPasswordDialog);
			return;
		}
		enter.enterText(ControlInfo.et_user_info, password);
		solo.clickOnButton("确定");
		if (!solo.waitForText("我的", 0, 5000)) {
			MessageUtils.append(Msg.BackToMineFailed);
			return;
		}
	}
	
	private void clickChangeConfAsGatewayLogin(String newPassword) {
		clickConfirmBtn();
		if(solo.searchText("网关密码被修改")){
			solo.clickOnButton("确定");
		}
//		if (!commonProc.waitForGatewayLogin()) return;
		commonProc.quitLogin();
		solo.clickOnView(getter.getView(ControlInfo.tv_gateway_login));
		password = newPassword;
//		String number = ((EditText) getter.getView("v6.et_gateway_username")).getText().toString();
//		if (!GatewayInfo.Gateway2.number.equals(number)) {
//			MessageUtils.append(Msg.NumberDiffAsGatewayLogin);
//			return;
//		}
		enterGatewayNumber();
		enterGatewayPassword(password);
		solo.clickOnView(getter.getView(ControlInfo.btn_gateway_login));
		commonProc.waitForHomePage(5000);
		solo.clickOnText("我的");
//		if (!solo.waitForText("我的", 0, 5000)) {
//			MessageUtils.append(Msg.BackToMineFailed);
//			return;
//		}
	}
	
	private boolean reducePassword(boolean isGateWayLogin) {
		clickToGatewayCenter();
		clickToGatewaySetting();
		clickToChangePassword();
		enterOldPassword(password);
		enterNewPassword(GatewayInfo.Gateway2.password1);
		clickConfirmBtn();
		if (!isGateWayLogin) {
			if (!solo.waitForText("请重新选择网关", 0, 5000)) return false;
			password = GatewayInfo.Gateway2.password1;
			solo.clickOnButton("确定");
			if (!commonProc.waitForGatewayList(2000)) return false;
			solo.clickOnText(GatewayInfo.Gateway2.nikeName);
			if (!solo.waitForText("输入密码", 0, 5000)) return false;
			enter.enterText(ControlInfo.et_user_info, password);
			solo.clickOnButton("确定");
		} else {
			if(solo.searchText("网关密码被修改")){
				solo.clickOnButton("确定");
			}
//			password = GatewayInfo.Gateway2.password1;
//			if (!enterGatewayPassword(password)) return false;
//			solo.clickOnView(getter.getView("v6.btn_gateway_login"));
//			solo.clickOnView(commonProc.getNavigationChild(4));
		}
//		if (!solo.waitForText("我的", 0, 5000)) return false;
		commonProc.quitLogin();
		return true;
	}
	
	private boolean LoginAsGateway() {
		solo.clickOnView(getter.getView(ControlInfo.img_left));
		if (!commonProc.waitForGatewayCenter(2000)) return false;
		solo.clickOnView(getter.getView(ControlInfo.img_left));
		if (!commonProc.waitForHomePage(5000)) return false;
		commonProc.scrollDownInMine();
		solo.clickOnView(getter.getView(ControlInfo.item_setting));
		if (!solo.waitForActivity(ActivitiesName.SigninActivity, 2000)) {
			solo.clickOnView(getter.getView(ControlInfo.item_setting_logout));
			commonProc.scrollUpInMine();
			solo.clickOnView(getter.getView(ControlInfo.item_account_login));
			commonProc.waitForLogin(2000);
		}
		solo.clickOnView(getter.getView(ControlInfo.tv_gateway_login));
		enterGatewayNumber();
		enterGatewayPassword();
		solo.clickOnView(getter.getView(ControlInfo.btn_gateway_login));
		if (!commonProc.waitForHomePage(5000)) return false;
		solo.sleep(2000);
		solo.clickOnView(commonProc.getNavigationChild(4));
		clickToGatewayCenter();
		clickToGatewaySetting();
		
		return true;
	}
	
	public void enterGatewayNumber() {
		enter.enterText(ControlInfo.et_gateway_username, GatewayInfo.Gateway2.number);
	}

	public void enterGatewayPassword() {
		enterGatewayPassword(GatewayInfo.Gateway2.password1);
	}
	
	private void enterGatewayPassword(String password) {
		solo.clickOnView(getter.getView(ControlInfo.et_gateway_username));
		enter.enterText(ControlInfo.et_gateway_password, password);
	}
	
	private boolean reduceNikeName() {
		clickChangeNikeName();
		if (!solo.waitForText("修改昵称", 0 ,2000)) return false;
		enterNikeName(GatewayInfo.Gateway2.nikeName);
		clickButtonInDialog("确定");
		solo.sleep(500);
		if (solo.waitForText("修改昵称", 0 ,2000)) return false;
		
		return true;
	}
	
	private static final class Msg {
		public static final String IntoGatewayCenterFailed = "未进入网关中心界面！";
		public static final String IntoGatewaySettingFailed = "未进入网关设置界面！";
		public static final String IntoChangePasswordFailed = "未进入修改密码界面！";

		public static final String EnterOldPasswordFailed = "输入原始密码失败！";
		public static final String EnterNewPasswordFailed = "输入新密码失败！";
		public static final String EnterConfirmPasswordFailed = "输入确认密码失败！";
		public static final String NotDisplaySuccessDialog = "修改网关密码成功后，未弹出提示框提示用户网关密码修改成功！";
		public static final String NotDisplayEnterPasswordDialog =
				"修改网关密码成功后，网关列表界面选中被修改密码的网关，未弹出输入密码提示框！";
		public static final String BackToMineFailed =
				"修改网密码，输入新网关密码成功后，点击确定未关闭输入新密码提示框并回到'我的'界面！";
		public static final String ReducePasswordFailed = "还原密码失败！";
		public static final String LoginAsGatewayFailed = "网关直接登录失败！";
		public static final String NumberDiffAsGatewayLogin = "网关登录，修改网关密码后，重新登录界面的网关号和原始网关号不一致！";
		public static final String NotDisplayChangeNameDialog = "未展示修改昵称的提示框！";
		public static final String NotCloseChangeNameDialog = "未关闭修改昵称的提示框！";
		public static final String NikeNameDiffAfterChange = "修改昵称后展示的内容和用户输入内容不一致！";
		public static final String ReduceNikeNameFailed = "还原昵称失败！";

		public static final String PasswordLessThan6Char = "密码长度不能小于8个字符";
		public static final String PasswordAsNoMatch = "密码至少有数字、字母或符号的2种组合";
		public static final String passwordAsIllegalChar = "密码含非法字符或空格，请重新输入";
		public static final String PasswordAsWeak = "数字和字母组合，密码长度至少10位";
		public static final String PasswordNotSyn = "两次密码不一致，请重新输入";
		public static final String originalPasswordFail = "修改失败";//原始密码输入错误
		public static final String ErrorNewSameAsOld = "新密码不能和老密码相同";
	}
}
