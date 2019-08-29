package cc.wulian.smarthomev6.proc;

import cc.wulian.smarthomev6.app.AccountInfo;
import cc.wulian.smarthomev6.app.ActivitiesName;
import cc.wulian.smarthomev6.app.ControlInfo;
import cc.wulian.smarthomev6.app.GatewayInfo;
import cc.wulian.smarthomev6.model.GatewayLoginModel;
import cc.wulian.smarthomev6.utils.MessageUtils;
import com.wtt.frame.robotium.Solo;

import java.util.regex.Pattern;

/**
 * Created by 赵永健 on 2017/5/10.
 *
 * 封装网关登录的测试流程
 */
public class GatewayLoginProc extends BaseProc<GatewayLoginModel> {
	
	public GatewayLoginProc(Solo solo) {
		super(solo);
	}
	
	public void gatewayLoginSuccess() {
		GatewayLoginModel model = new GatewayLoginModel(new int[] {0,1,3,15,14});
		model.setGatewayNumber(GatewayInfo.Gateway2.number);
		model.setPassword(GatewayInfo.Gateway2.password1);
		baseProcess(model);
	}
	
	public void gatewayLoginAsNumWrong() {
		gatewayLoginAsInfoWrongWithToast("123"
				, GatewayInfo.Gateway2.password
				, Msg.GatewayNumberError);
	}
	
	public void gatewayLoginAsWrongPassword() {
		gatewayLoginAsInfoWrongWithToast(GatewayInfo.Gateway2.number
				, "000124"
				, Msg.GatewayPasswordWrong);
		
	}
	
	private void gatewayLoginAsInfoWrongWithToast(String gatewayNumber, String password, String searchText) {
		GatewayLoginModel model = new GatewayLoginModel(new int[] {0,1,3,7});
		model.setGatewayNumber(gatewayNumber);
		model.setPassword(password);
		model.setSearchText(searchText);
		
		baseProcess(model);
	}
	
	public void gatewayLoginAsEmptyNumber() {
		gatewayLoginAsInfoWrong("", GatewayInfo.Gateway2.password);
	}
	
	public void gatewayLoginAsEmptyPassword() {
		gatewayLoginAsInfoWrong(GatewayInfo.Gateway2.number, "");
	}
	
	public void gatewayLoginAsPasswordLessThanSix() {
		gatewayLoginAsInfoWrong(GatewayInfo.Gateway2.number, "00012");
	}
	
	private void gatewayLoginAsInfoWrong(String gatewayNumber, String password) {
		GatewayLoginModel model = new GatewayLoginModel(new int[] {0,1,8});
		model.setGatewayNumber(gatewayNumber);
		model.setPassword(password);
		
		baseProcess(model);
	}
	
	public void gatewayLoginAsNotNet() {
		GatewayLoginModel model = new GatewayLoginModel(new int[] {6,0,1,3,7,5});
		model.setGatewayNumber(GatewayInfo.Gateway2.number);
		model.setPassword("123456");
		model.setSearchText(Msg.NotNet);
		
		baseProcess(model);
	}
	
	@Override
	public void process(GatewayLoginModel model, int action) {
		switch (action) {
			case 0:
				enterGatewayNumber(model.getGatewayNumber());
				break;
			case 1:
				enterPassword(model.getPassword());
				break;
			case 2:
				clickAutoTv();
				break;
			case 3:
				clickGatewayLoginBtn();
				break;
			case 4:
				commonProc.waitForHomePage(5000);
				break;
			case 5:
				solo.setWiFiData(true);
				break;
			case 6:
				solo.setWiFiData(false);
				break;
			case 7:
				if (!solo.searchText(model.getSearchText())) {
					MessageUtils.append("未显示'" + model.getSearchText() + "'!");
					return;
				}
				break;
			case 8:
				if (isLoginBtnClickable()) {
					MessageUtils.append(Msg.CaseUseLoginButton);
					return;
				}
				break;
			case 9:
				clickToConfirmPassword();
				break;
			case 10:
				enterNewPassword(model.getNewPassword());
				break;
			case 11:
				enterConfirmNewPassword(model.getConfirmPassword());
				break;
			case 12:
				clickConfirm();
				break;
			case 13:
				if(!commonProc.waitForGatewayLogin()){
					MessageUtils.append(Msg.IntoGatewayLoginFailed);
					return;
				}
				break;
			case 14:
				commonProc.quitLogin();//退出登录
				break;
			case 15:
				commonProc.waitForHomePage(10000);
				break;
		}
	}
	
	@Override
	public void init() {
		commonProc.login(AccountInfo.Account1,AccountInfo.Password1);
		commonProc.quitLogin();
		clickToGatewayLogin();
		
	}

	public void clickToGatewayLogin() {
		 click.clickToAnotherActivity(ControlInfo.tv_gateway_login
				, ActivitiesName.GatewayLoginActivity
				, Msg.IntoGatewayLoginFailed);
	}

	public void enterGatewayNumber(String gatewayNumber) {
		enter.enterText(ControlInfo.et_gateway_username, gatewayNumber);
	}

	public void enterPassword(String password) {
		solo.clickOnView(getter.getView(ControlInfo.et_gateway_username));
		enter.enterText(ControlInfo.et_gateway_password, password);
	}
	
	private void clickAutoTv() {
		solo.clickOnView(getter.getView(ControlInfo.btn_auto));
	}

	public void clickGatewayLoginBtn() {
		solo.clickOnView(getter.getView(ControlInfo.btn_gateway_login));
	}
	
	private boolean isLoginBtnClickable() {
		return getter.getView(ControlInfo.btn_gateway_login).isEnabled();
	}

	/**
	 * 点击进入修改网关密码页面
	 */
	public void clickToConfirmPassword() {
		 click.clickToAnotherActivity(ControlInfo.btn_gateway_login,
				ActivitiesName.ConfirmGatewayPasswordActivity,
				Msg.IntoConfirmGatewayPasswordFailed);
	}

	/**
	 * 输入新的密码
	 */
	private void enterNewPassword(String newPassword) {
		enter.enterText(ControlInfo.new_pwd_editText, newPassword);
	}

	/**
	 * 确定新密码
	 */
	private void enterConfirmNewPassword(String confirmPassword) {
		super.enter.enterText(ControlInfo.confirm_pwd_editText, confirmPassword);
	}

	/**
	 * 点击确定
	 */
	private void clickConfirm() {
		solo.clickOnView(super.getter.getView(ControlInfo.confirm_pwd_button));
	}

	/**
	 * 正则表达式匹配
	 */
	private boolean parent(String content){
		String pattern="^局域网.*$";
		return Pattern.matches(pattern,content);
	}


	
	public static final class Msg {
		public static final String IntoGatewayLoginFailed = "未进入网关登录界面！";
		public static final String EnterGatewayNumberFailed = "输入网关号失败！";
		public static final String EnterGatewayPasswordFailed = "输入网关密码失败！";
		public static final String CaseUseLoginButton = "登录按钮未禁用！";
		public static final String GatewayNumberError = "^局域网.*$";
		public static final String GatewayPasswordWrong = "网关.*";
		public static final String NotNet = "无法连接网络，请检查网络或稍后再试";
		public static final String IntoConfirmGatewayPasswordFailed="未进入修改网关密码页面！";

		public static final String PasswordLessThan6Char = "密码长度不能小于6个字符";
		public static final String PasswordAsNoMatch = "密码至少有数字、字母或符号的2种组合";
		public static final String passwordAsIllegalChar = "密码含非法字符或空格，请重新输入";
		public static final String PasswordAsWeak = "数字和字母组合，密码长度至少10位";
		public static final String PasswordNotSyn = "两次密码不一致，请重新输入";
		public static final String originalPasswordFail = "原始密码输入错误";
	}

	/**
	 * 网关验证失败的情况
	 */
	public void gatewayLoginAsWrong(String newPassword,String confirmPassword,String msg){
		GatewayLoginModel model = new GatewayLoginModel(new int[]{0,1,9,10,12,7});
		model.setGatewayNumber(GatewayInfo.Gateway2.number);
		model.setPassword(GatewayInfo.Gateway2.password);
		model.setNewPassword(newPassword);
//		model.setConfirmPassword(confirmPassword);
		model.setSearchText(msg);
		baseProcess(model);
	}

	/**
	 * 网关验证成功的情况,网关密码修改后需要在硬件上set8次，所以默认设置两次密码不一致
	 */
	public void gatewayLoginAsSuccess(String newPassword,String confirmPassword,String msg){
		GatewayLoginModel model = new GatewayLoginModel(new int[]{0,1,9,10,12});
		model.setGatewayNumber(GatewayInfo.Gateway2.number);
		model.setPassword(GatewayInfo.Gateway2.password);
		model.setNewPassword(newPassword);
//		model.setConfirmPassword(confirmPassword);
		model.setSearchText(msg);
		baseProcess(model);
	}

}
