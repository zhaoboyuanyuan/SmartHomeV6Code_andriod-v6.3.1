package cc.wulian.smarthomev6.proc;

import cc.wulian.smarthomev6.app.AccountInfo;
import cc.wulian.smarthomev6.app.ActivitiesName;
import cc.wulian.smarthomev6.app.GatewayInfo;
import cc.wulian.smarthomev6.model.BindGatewayModel;
import cc.wulian.smarthomev6.utils.EnterUtils;
import cc.wulian.smarthomev6.utils.MessageUtils;
import com.wtt.frame.robotium.Solo;

/**
 * Created by 赵永健 on 2017/5/9.
 *
 * 封装绑定网关的测试流程
 */
public class BindGatewayProc extends BaseProc<BindGatewayModel> {
	
	private VisitProc visitProc;
	private IntoAppProc intoAppProc;
	
	public BindGatewayProc(Solo solo) {
		super(solo);
		visitProc = new VisitProc(solo);
		intoAppProc =new IntoAppProc(solo);
	}
	
	public void intoGatewayList() {
		baseProcess(new BindGatewayModel(new int[] {0, 1}));
	}
	
	public void bindGatewaySuccess() {
		BindGatewayModel model = new BindGatewayModel(new int[] {0, 1, 2, 3, 4, 6,8});
		model.setGatewayNumber(GatewayInfo.Gateway2.number);
		model.setPassword(GatewayInfo.Gateway2.password1);
		baseProcess(model);
	}
	
	public void bindGatewayASEmptyNum() {
		BindGatewayModel model = new BindGatewayModel(new int[] {0, 1, 2,3, 4, 7});
		model.setGatewayNumber("");
		model.setPassword("123456aaaa");
		baseProcess(model);
	}
	
	public void bindGatewayAsEmptyPassword() {
		bindGatewayAsFailed(GatewayInfo.Gateway1.number, "");
	}
	
	public void bindGatewayAsPasswordLessThanSix() {
		bindGatewayAsFailed(GatewayInfo.Gateway1.number, "12345");
	}
	
	private void bindGatewayAsFailed(String number, String password) {
		BindGatewayModel model = new BindGatewayModel(new int[] {0, 1, 2,3, 4, 7});
		model.setGatewayNumber(number);
		model.setPassword(password);
		
		baseProcess(model);
	}
	
	public void bindGatewayAsWrongPassword() {
		BindGatewayModel model = new BindGatewayModel(new int[] {0, 1, 2, 3, 4, 6, 9});
		model.setGatewayNumber(GatewayInfo.Gateway1.number);
		model.setPassword("asdfgh");
		model.setSearchText(Msg.DevicePasswordError);
		
		baseProcess(model);
	}
	
	@Override
	public void process(BindGatewayModel model, int action) {
		switch (action) {
			case 0:
				clickWholeScenes();
				break;
			case 1:
				if (!solo.waitForDialogToOpen(2000)) {
					MessageUtils.append(Msg.NotDisplayBindDialog);
					return;
				}
				clickBindText();
				commonProc.waitForGatewayList(5000);
				break;
			case 2:
				clickBindGateway();
				solo.sleep(2000);
				commonProc.waitForgatewayBind(5000);
				break;
			case 3:
				enterGatewayNumber(model.getGatewayNumber());
				break;
			case 4:
				enterPassword(model.getPassword());
				break;
			case 5:
				clickAutoTv();
				break;
			case 6:
				clickGatewayLoginBtn();
				break;
			case 7:
				if (isLoginBtnClickable()) {
					MessageUtils.append(Msg.DisplayBindButton);
					return;
				}
				break;
			case 8:
//				if(!commonProc.waitForGatewayList(5000)){
//					MessageUtils.append(Msg.BindGatewayFailed);
//				}
				solo.sleep(3000);
				break;
			case 9:
				if (!solo.searchText(model.getSearchText())) {
					MessageUtils.append("未搜索到\"" + model.getSearchText() + "\"");
					return;
				}
				break;
			case 10:
				intoAppProc.restoreAccountBindGateway();
				break;
		}
	}
	
	@Override
	public void init() {
		commonProc.unbindGateway(AccountInfo.Account1, AccountInfo.Password1);
		solo.clickOnView(commonProc.getNavigationChild(0));
		commonProc.waitForHomePage(5000);
	}
	
	@Override
	public void finalize() {
		if (null != visitProc) {
			try {
				visitProc.finalize();
			} catch (Throwable throwable) {
				throwable.printStackTrace();
			}
			visitProc = null;
		}
	}

	private void clickWholeScenes() {
		solo.clickOnView(commonProc.getNavigationChild(3));
	}

	/**
	 * 点击去登陆
	 */
	private void clickBindText() {
		solo.clickOnScreen(392,1205);
	}
	
	private void clickBindGateway() {
		solo.clickOnView(getter.getView("img_right"));
	}
	
	private void enterGatewayNumber(String gatewayNumber) {
		enter.enterText("et_gateway_username", gatewayNumber);
	}
	
	private void enterPassword(String password) {
		solo.clickOnView(getter.getView("et_gateway_username"));
		enter.enterText("et_gateway_password", password);
	}
	
	private void clickAutoTv() {
		solo.clickOnView(getter.getView("btn_auto"));
	}
	
	private void clickGatewayLoginBtn() {
		solo.clickOnView(getter.getView("btn_gateway_login"));
	}
	
	private boolean isLoginBtnClickable() {
		return getter.getView("btn_gateway_login").isEnabled();
	}
	
	private static final class Msg {
		public static final String BindSuccess = "网关绑定成功";
		public static final String DevicePasswordError = "设备密码错误";
		public static final String NotDisplayBindDialog = "未展示绑定网关提示框！";
		public static final String EnterNumberFailed = "输入网关号失败！";
		public static final String EnterPasswordFailed = "输入网关密码失败！";
		public static final String DisplayBindButton = "绑定网关按钮未禁用！";
		public static final String BindGatewayFailed = "绑定网关未成功！";
	}
}
