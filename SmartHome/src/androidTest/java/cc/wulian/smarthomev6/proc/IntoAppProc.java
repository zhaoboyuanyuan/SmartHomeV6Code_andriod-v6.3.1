package cc.wulian.smarthomev6.proc;

import cc.wulian.smarthomev6.app.AccountInfo;
import cc.wulian.smarthomev6.app.ActivitiesName;
import cc.wulian.smarthomev6.app.GatewayInfo;
import cc.wulian.smarthomev6.model.IntoAppModel;
import cc.wulian.smarthomev6.utils.EnterUtils;
import cc.wulian.smarthomev6.utils.MessageUtils;
import com.wtt.frame.robotium.Solo;

/**
 * Created by 赵永健 on 2017/5/9.
 *
 * 封装登录后进入APP的流程
 */
public class IntoAppProc extends BaseProc<IntoAppModel> {
	
	private VisitProc visitProc;

	
	public IntoAppProc(Solo solo) {
		super(solo);
		visitProc = new VisitProc(solo);
	}
	
	public void intoHomepage() {
		into(new int[] {0});
	}
	
	public void intoMessageCenter() {
		into(new int[] {0, 1, 10});
	}
	
	public void intoAsClickWhileScenes() {
		into(new int[] {0, 2});
	}
	
	public void intoAsClickDevice() {
		into(new int[] {0, 3});
	}
	
	public void intoAsClickFind(){
		into(new int[] {0, 4});
	}
	
	public void intoAsClickMine() {
		into(new int[] {0, 5, 6, 12, 7, 11,13});
	}
	
	private void into(int[] actions) {
		baseProcess(new IntoAppModel(actions));
	}
	
	public  void restoreAccountBindGateway() {
		clickGatewayBind();
		enterGatewayNumber();
		enterGatewayPassword(GatewayInfo.Gateway2.password1);
		clickToGatewayList();

	}

	public void clickToGatewayList(){
		 click.clickToAnotherActivity("btn_gateway_login", ActivitiesName.GatewayListActivity,
				Msg.IntoGatewayListFailed);
	}
	
	@Override
	public void process(IntoAppModel model, int action) {
		switch (action) {
			case 0:
				clickHomePage();
				break;
			case 1:
				clickMessageCenter();
				break;
			case 2:
				clickWholeScenes();
				break;
			case 3:
				clickDevice();
				break;
			case 4:
				clickFind();
				break;
			case 5:
				clickMine();
				break;
			case 6:
				clickGatewayCenter();
				break;
			case 7:
				clickGatewayList();
				break;
			case 8:
				if (!solo.waitForDialogToOpen(2000)) {
					MessageUtils.append(Msg.NotShownDialog);
					return;
				}
				solo.clickOnText("去绑定");
				break;
			case 9:
				if (!solo.searchText(Msg.NotBingGateway)) {
					MessageUtils.append("未显示'" + Msg.NotShownDialog + "'！");
					return;
				}
				break;
			case 10:
				if (!commonProc.waitForMessageCenterNew(5000)) return;
				break;
			case 11:
				if (!commonProc.waitForGatewayList(5000)) return;
				break;
			case 12:
				if (!checkClickableInGatewayCenter()) return;
				break;
			case 13:
				restoreAccountBindGateway();
				break;
		}
	}
	
	@Override
	public void init() {
		commonProc.unbindGateway(AccountInfo.Account1, AccountInfo.Password1);
	}
	
//	@Override
//	public void finalize() {
//		if (null != visitProc) {
//			try {
//				visitProc.finalize();
//			} catch (Throwable throwable) {
//				throwable.printStackTrace();
//			}
//			visitProc = null;
//		}
//	}
	
	private void clickHomePage() {
		solo.clickOnView(commonProc.getNavigationChild(0));
		solo.scrollToTop();
	}
	
	private void clickMessageCenter() {
		solo.clickOnView(getter.getView("base_img_right"));
	}
	
	private void clickWholeScenes() {
//		solo.clickOnView(visitProc.getScenes(7));
		solo.clickOnView(commonProc.getNavigationChild(3));
	}
	
	private void clickDevice() {
		solo.clickOnView(commonProc.getNavigationChild(1));
	}
	
	private void clickFind() {
		solo.clickOnView(commonProc.getNavigationChild(3));
	}
	
	private void clickMine() {
		solo.clickOnView(commonProc.getNavigationChild(4));
	}
	
	private void clickGatewayCenter() {
		solo.clickOnView(getter.getView("item_gateway_center"));
	}
	
	private void clickGatewayList() {
		solo.clickOnView(getter.getView("item_gateway_center_list"));
	}
	
	private boolean checkClickableInGatewayCenter() {
//		if (getter.getView("v6.item_gateway_center_auth").isEnabled()) {
//			//查看控件，clickable是TRUE,但enable是false，所以暂时使用enable
//			MessageUtils.append(Msg.AuthClickable);
//			return false;
//		}
		
		if (getter.getView("item_gateway_center_setting").isEnabled()) {
			MessageUtils.append(Msg.SettingClickable);
			return false;
		}
		
//		if (getter.getView("item_gateway_center_notifi").isEnabled()) {
//			MessageUtils.append(Msg.NotiffClickable);
//			return false;
//		}
		
		return true;
	}

	private void enterGatewayNumber() {
		enter.enterText("et_gateway_username", GatewayInfo.Gateway2.number);
	}

	private void enterGatewayPassword() {
		enterGatewayPassword(GatewayInfo.Gateway2.password1);
	}

	private void enterGatewayPassword(String password) {
		solo.clickOnView(getter.getView("et_gateway_username"));
		enter.enterText("et_gateway_password", password);
	}

	private void clickConfirmBtn() {
		solo.clickOnView(getter.getView("confirm_pwd_button"));
	}

	/**
	 * 点击右上角，绑定网关
	 *
	 * @return
	 */
	public void clickGatewayBind() {
		 click.clickToAnotherActivity("img_right",
				ActivitiesName.GatewayBindActivity,
				Msg.IntoGatewayBindFailed);
	}


//	/**
//	 * 绑定网关
//	 */
//	public void bindGateway(){
//		commonProc.getNavigationChild(3);
//		solo.clickOnView(getter.getView("v6.item_gateway_center"));
//		if(!commonProc.waitForGatewayCenter(3000)) return;
//		solo.clickOnView(getter.getView("v6.item_gateway_center_list"));
//		if(!commonProc.waitForGatewayList(3000)) return;
//		solo.clickOnView(getter.getView("v6.img_right"));
//		enterGatewayNumber();
//		enterGatewayPassword();
//		clickConfirmBtn();
//	}

	private static final class Msg {
		public static final String NotShownDialog = "未显示提示框！";
		public static final String NotBingGateway = "还没有绑定网关";
		public static final String AuthClickable = "授权管理可点击！";
		public static final String SettingClickable = "网关设置可点击！";
		public static final String NotiffClickable = "提醒设置可点击！";
		public static final String IntoGatewayListFailed = "未进入网关列表页面！";
		public static final String IntoGatewayBindFailed = "未进入网关绑定页面！";

	}
}
