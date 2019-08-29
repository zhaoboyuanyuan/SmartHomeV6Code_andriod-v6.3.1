//package cc.wulian.smarthomev6.proc;
//
//import android.net.Uri;
//import android.os.SystemClock;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import com.wtt.frame.robotium.By;
//import com.wtt.runner.android.InstTestRunner;
////import com.wtt.common.callback.Result;
////import com.wtt.common.utils.SmsUtils;
//import com.wtt.frame.robotium.Solo;
//import cc.wulian.smarthomev6.app.ActivitiesName;
//import cc.wulian.smarthomev6.runner.SmartHomeTestHelper;
//import cc.wulian.smarthomev6.utils.ClickUtils;
//import cc.wulian.smarthomev6.utils.GetterUtils;
//import cc.wulian.smarthomev6.utils.WaitForUtils;
//
//import java.util.List;
//
///**
// * Created by 严君 on 2017/5/4.
// *
// * 公用业务流程
// */
//public class CommonProc {
//
//	private Solo solo;
//	private ClickUtils click;
//	private WaitForUtils waitFor;
//	private GetterUtils getter;
//
//	/**
//	 * 构造器
//	 *
//	 * @param solo    - {@link Solo}
//	 * @param click   - {@link ClickUtils}
//	 * @param waitFor - {@link WaitForUtils}
//	 * @param getter  - {@link GetterUtils}
//	 */
//	public CommonProc(Solo solo, ClickUtils click, WaitForUtils waitFor, GetterUtils getter) {
//		this.solo = solo;
//		this.click = click;
//		this.waitFor = waitFor;
//		this.getter = getter;
//	}
//
//	/**
//	 * 退出登录
//	 *
//	 */
//	public void quitLogin() {
//		solo.clickOnView(getNavigationChild(3));
//		solo.clickOnView(getter.getView("v6.item_setting"));
//		if (!solo.waitForActivity(ActivitiesName.SigninActivity, 2000)) {
//			Button button = (Button) getter.getView("v6.item_setting_logout");
//			solo.clickOnView(button);
//			solo.clickOnView(getter.getView("v6.item_account_login"));
//		}
//	}
//
//	/**
//	 * 判断APP是否成功启动"首页"
//	 *
//	 * @param timeout - 超时时间
//	 * @return        - 成功成功启动首页，返回{@code true}，否则返回{@code false}
//	 */
//	public boolean waitForHomePage(int timeout) {
//		return waitFor.waitFor(ActivitiesName.HomeActivity, timeout, Msg.IntoHomePageFailed);
//	}
//
//	/**
//	 * 判断APP是否成功启动"登录界面"
//	 *
//	 * @param timeout - 超时时间
//	 * @return        - 成功启动登录界面，返回{@code true}，否则返回{@code false}
//	 */
//	public boolean waitForLogin(int timeout) {
//		return waitFor.waitFor(ActivitiesName.SigninActivity, timeout, Msg.IntoLoginFailed);
//	}
//
//	/**
//	 * 判断APP是否成功启动"快捷方式编辑界面"
//	 *
//	 * @param timeout - 超时时间
//	 * @return        - 成功启动快捷方式编辑界面，返回{@code true}，否则返回{@code false}
//	 */
//	public boolean waitForHomeEdit(int timeout) {
//		return waitFor.waitFor(ActivitiesName.HomeEditActivity, timeout, Msg.IntoHomeEditFailed);
//	}
//
//	/**
//	 * 点击某个控件后进入登录界面
//	 *
//	 * @param id - 被点击的控件的id
//	 * @return   - 进入登录界面，返回{@code true}，否则返回{@code false}
//	 */
//	public boolean clickToLogin(String id) {
//		return click.clickToAnotherActivity(id, ActivitiesName.SigninActivity, Msg.IntoLoginFailed);
//	}
//
//	/**
//	 * 点击某个控件后进入登录界面
//	 *
//	 * @param view - 被点击的控件{@link android.view.View}
//	 * @return     - 进入登录界面，返回{@code true}，否则返回{@code false}
//	 */
//	public boolean clickToLogin(View view) {
//		if (null == view) {
//			return false;
//		}
//
//		return waitForLogin(5000);
//	}
//
//	/**
//	 * 点击"我的"界面中的某个控件，进入登录界面
//	 *
//	 * @param id - 被点击的控件的id
//	 * @return   - 进入登录界面，返回{@code true}，否则返回{@code false}
//	 */
//	public boolean clickToLoginAsMine(String id) {
//		solo.clickOnView(getNavigationChild(3));
//		solo.clickOnView(getter.getView(id));
//
//		return waitForLogin(5000);
//	}
//
//	/**
//	 * 获取首页导航栏
//	 *
//	 * @return - 首页导航栏容器{@link android.widget.LinearLayout}
//	 */
//	private LinearLayout getNavigationArea() {
//		return (LinearLayout) getter.getView("v6.bottom_navigation_bar_item_container");
//	}
//
//	/**
//	 * 获取首页"导航栏"的子元素
//	 *
//	 * @param index - 子元素的索引，从{@code 0}开始
//	 * @return      - 获取子元素成功则返回子元素{@link android.view.View}，否则返回{@link java.lang.NullPointerException}
//	 */
//	public View getNavigationChild(int index) {
//		int indexTemp = 0 > index ? 0 : index;
//		LinearLayout linearLayout = getNavigationArea();
//		if (null == linearLayout || 4 > linearLayout.getChildCount()) {
//			SmartHomeTestHelper.builder.append(Msg.GetNavigationBarFailed);
//			return null;
//		}
//
//		switch (indexTemp) {
//			case 0:
//				return linearLayout.getChildAt(0);
//			case 1:
//				return linearLayout.getChildAt(1);
//			case 2:
//				return linearLayout.getChildAt(2);
//			case 3:
//				return linearLayout.getChildAt(3);
//			default:
//				SmartHomeTestHelper.builder.append(Msg.GetNavigationBarIndexError);
//				return null;
//		}
//	}
//
//	/**
//	 * 账号登录
//	 */
//	public void loginInit() {
//		if (waitForHomePage(5000)) {
//			if (solo.searchButton("确定", 0 ,true)) {
//				solo.clickOnButton("确定");
//			}
//		}
//
//		quitLogin();
//	}
//
//	/**
//	 * 获取物联的短信验证码信息
//	 *
//	 * @return - 短信验证码信息{@link java.util.List}
//	 */
//	public List<String> getVerificationCodeMsg() {
//		return SmsUtils.getAllMessage(InstTestRunner.context
//				, Uri.parse("content://sms/"), "1069086");
//	}
//
//	/**
//	 * 获取最新的短信验证码
//	 *
//	 * @param oldSms  - 原始短信{@link java.util.List}
//	 * @param timeout - 获取新短信的超时时间{@link java.lang.Integer}
//	 * @return        - 获取新短信结果{@link Result}
//	 */
//	public Result getCurrentVerificationCode(List<String> oldSms, int timeout) {
//		Result result = new Result();
//		String code = null;
//		if (null == oldSms || oldSms.isEmpty()) {
//			return result.fail(Msg.GetSourceMMSFailed);
//		}
//
//		long endTime = SystemClock.currentThreadTimeMillis() + timeout;
//		while (SystemClock.currentThreadTimeMillis() <= endTime) {
//			List<String> newSms = getVerificationCodeMsg();
//			if (null == newSms || newSms.isEmpty()) {
//				return result.fail(Msg.GetNewMMSFailed);
//			}
//
//			if (!oldSms.contains(newSms.get(0))) {
//				code = newSms.get(0);
//				break;
//			}
//		}
//
//		result.setSuccess(null == code || code.isEmpty());
//		if (result.isSuccess()) {
//			result.setData(code);
//		} else {
//			result.append(Msg.GetMMSCodeFailed);
//		}
//
//		return result;
//	}
//
//	/**
//	 * 获取短信验证码
//	 *
//	 * @param body - 短信内容{@link java.lang.String}
//	 * @return     - 短信验证码{@link java.lang.String}
//	 */
//	private String getVerificationCodeNum(String body) {
//		try {
//			String[] sp = body.split(":");
//			String[] codeSp = sp[1].split("。");
//			return codeSp[0];
//		} catch (Exception e) {
//			return null;
//		}
//	}
//
//	/**
//	 * 判断APP是否成功启动"注册"界面
//	 *
//	 * @param timeout - 超时时间
//	 * @return        - 成功成功启动首页，返回{@code true}，否则返回{@code false}
//	 */
//	public boolean waitForRegister(int timeout) {
//		return waitFor.waitFor(ActivitiesName.RegisterActivity, timeout, Msg.IntoRegisterFailed);
//	}
//
//	public boolean waitForForgetPassword(int timeout) {
//		return waitFor.waitFor(ActivitiesName.ForgotPassWordActivity, timeout, Msg.IntoForgetPasswordFailed);
//	}
//
//	public boolean waitForGatewayCenter(int timeout) {
//		return waitFor.waitFor(ActivitiesName.GatewayCenterActivity, timeout, Msg.IntoGatewayCenterFailed);
//	}
//
//	public boolean waitForGatewaySetting(int timeout) {
//		return waitFor.waitFor(ActivitiesName.GatewaySettingActivity, timeout, Msg.IntoGatewaySettingFailed);
//	}
//
//	public boolean waitForGatewayList(int timeout) {
//		return waitFor.waitFor(ActivitiesName.GatewayListActivity, timeout, Msg.IntoGatewayListFailed);
//	}
//
//	public boolean waitForgatewayBind(int timeout) {
//		return waitFor.waitFor(ActivitiesName.GatewayBindActivity, timeout, Msg.IntoBindGatewayFailed);
//	}
//
//	public boolean waitForMessageCenter(int timeout) {
//		return waitFor.waitFor(ActivitiesName.MessageCenterActivity, timeout, Msg.IntoMessageCenterFailed);
//	}
//
//	public void login(String accountText, String passwordText) {
//		if (waitForHomePage(5000)) {
//			if (solo.searchButton("确定", 0 ,true)) {
//				solo.clickOnButton("确定");
//			}
//		}
//		solo.clickOnView(getNavigationChild(3));
//		solo.clickOnView(getter.getView("v6.item_setting"));
//		if (solo.waitForActivity(ActivitiesName.SigninActivity, 1500)) {
//			EditText account = (EditText) getter.getView("v6.username");
//			EditText password = (EditText) getter.getView("v6.password");
//			if (!"".equals(account.getText().toString())) {
//				solo.clearEditText(account);
//			}
//			if (!"".equals(password.getText().toString())) {
//				solo.clearEditText(password);
//			}
//			solo.enterText(account, accountText);
//			solo.enterText(password, passwordText);
//
//			solo.clickOnView(getter.getView("v6.login"));
//			waitForHomePage(5000);
//		} else {
//			solo.clickOnView(getter.getView("v6.img_left"));
//		}
//	}
//
//	public void unbindGateway(String account, String password) {
//		login(account, password);
//		solo.clickOnView(getter.getView("v6.item_gateway_center"));
//		if (!waitForGatewayCenter(5000)) return;
//
//		RelativeLayout layout = (RelativeLayout) getter.getView("v6.item_gateway_center_setting");
//		if (layout.isClickable()) {
//			solo.clickOnView(layout);
//			if (!waitForGatewaySetting(5000)) return;
//			solo.clickOnView(getter.getView("v6.unbind_button"));
//		}
//		solo.clickOnView(getter.getView("v6.img_left"));
//	}
//
//	public boolean waitForMyInfo(int timeout) {
//		return waitFor.waitFor(ActivitiesName.UserMassageActivity, timeout, Msg.IntoPersonalInfoFailed);
//	}
//
//	public boolean waitForChangePhoneNumber(int timeout) {
//		return waitFor.waitFor(ActivitiesName.ChangePhoneNumberActivity, timeout, Msg.IntoChangePhoneNumberFailed);
//	}
//
//	public boolean waitForBindMail(int timeout) {
//		return waitFor.waitFor(ActivitiesName.BindMailBoxActivity, timeout, Msg.IntoBindMailFailed);
//	}
//
//	public boolean waitForConfirmMail(int timeout) {
//		return waitFor.waitFor(ActivitiesName.ConfirmMailBoxActivity, timeout, Msg.IntoConfirmMailFailed);
//	}
//
//	public boolean waitForAccountSecurity(int timeout) {
//		return waitFor.waitFor(ActivitiesName.AccountSecurityActivity, timeout, Msg.IntoAccountSecurityFailed);
//	}
//
//	public boolean clickWebMore() {
//		return click.clickToAnotherActivity("v6.web_more"
//				, GetterUtils.KEY_XPATH, ActivitiesName.DeviceDetailActivity
//				, 5000, Msg.IntoDeviceMoreFailed);
//	}
//
//	public boolean waitForDetailMoreArea() {
//		return waitFor.waitFor(ActivitiesName.DeviceDetailMoreAreaActivity, 5000, Msg.IntoDeviceMoreAreaFailed);
//	}
//
//	public boolean waitForDeviceDetail() {
//		return waitFor.waitFor(ActivitiesName.DeviceDetailActivity, 5000, Msg.IntoDeviceDetailFailed);
//	}
//
//	public boolean waitForDeviceDetailMore() {
//		return waitFor.waitFor(ActivitiesName.DeviceDetailMoreActivity, 5000, Msg.IntoDeviceMoreAreaFailed);
//
//	}
//
//	public boolean waitForArea() {
//		return waitFor.waitFor(ActivitiesName.AreaActivity, 5000, Msg.IntoAreaFailed);
//	}
//
//	public boolean waitForAllScene() {
//		return waitFor.waitFor(ActivitiesName.AllSceneActivity, 5000, Msg.IntoAllSceneFailed);
//	}
//
//	public boolean waitForAddScene() {
//		return waitFor.waitFor(ActivitiesName.AddSceneActivity, 5000, Msg.IntoAddSceneFailed);
//	}
//
//	public boolean waitForEditScene() {
//		return waitFor.waitFor(ActivitiesName.EditSceneActivity, 5000, Msg.IntoEditSceneFailed);
//	}
//
//	public boolean waitForSceneSort() {
//		return waitFor.waitFor(ActivitiesName.SceneSortActivity, 5000, Msg.IntoSceneSortFailed);
//	}
//
//	public boolean waitForEditScenePage() {
//		return waitFor.waitForAsWeb(getter.setBy("v6.edit_scene_finished", GetterUtils.KEY_XPATH)
//				, 5000, Msg.IntoEditScenePageFailed);
//	}
//
//	public boolean waitForCustomScenePage() {
//		return waitFor.waitForAsWeb(getter.setBy("v6.custom_scene_conf", GetterUtils.KEY_XPATH)
//				, 5000, Msg.IntoCustomScenePageFailed);
//	}
//
//	public boolean waitForDeviceListPage() {
//		return waitFor.waitForAsWeb(getter.setBy("v6.web_device_list_title", GetterUtils.KEY_XPATH)
//				, 5000, Msg.IntoAddDevicePageFailed);
//	}
//
//	public boolean waitForDeviceStatePage() {
//		return waitFor.waitForAsWeb(getter.setBy("v6.web_set_device_state_1", GetterUtils.KEY_XPATH)
//				, 5000, Msg.IntoDeviceStatePageFailed);
//	}
//
//	public boolean waitForAddDelayPage() {
//		return waitFor.waitForAsWeb(getter.setBy("v6.web_add_time_add", GetterUtils.KEY_XPATH)
//				, 5000, Msg.IntoAddDelayPageFailed);
//	}
//
//	public boolean waitForGatewayLogin() {
//		return waitFor.waitFor(ActivitiesName.GatewayLoginActivity, 5000, Msg.IntoGatewayLoginFailed);
//	}
//
//	public boolean waitForWebPage(String title) {
//		return solo.waitForWebElement(By.textContent(title), 0, 2000, false);
//	}
//
//	private final static class Msg {
//		public static final String IntoHomePageFailed = "进入首页失败！";
//		public static final String IntoLoginFailed = "未进入登录界面！";
//		public static final String IntoHomeEditFailed = "进入快捷方式界面！";
//		public static final String GetNavigationBarFailed = "获取导航栏失败！";
//		public static final String GetNavigationBarIndexError = "获取导航栏时索引大于4！";
//		public static final String GetSourceMMSFailed = "获取原始短信失败！";
//		public static final String GetNewMMSFailed = "获取新短信失败！";
//		public static final String GetMMSCodeFailed = "获取短信验证码失败！";
//		public static final String IntoRegisterFailed = "进入注册界面失败！";
//		public static final String IntoForgetPasswordFailed = "进入忘记密码界面失败！";
//		public static final String IntoGatewayCenterFailed = "进入网关中心界面失败！";
//		public static final String IntoGatewaySettingFailed = "进入网关设置界面失败！";
//		public static final String IntoGatewayListFailed = "进入网关列表界面失败！";
//		public static final String IntoBindGatewayFailed = "进入绑定网关界面失败！";
//		public static final String IntoMessageCenterFailed = "进入消息中心界面失败！";
//		public static final String IntoPersonalInfoFailed = "进入个人信息界面失败！";
//		public static final String IntoChangePhoneNumberFailed = "进入更改手机号界面失败！";
//		public static final String IntoBindMailFailed = "进入绑定邮箱界面失败！";
//		public static final String IntoConfirmMailFailed = "进入验证邮箱界面失败！";
//		public static final String IntoAccountSecurityFailed = "进入账号安全页失败！";
//		public static final String IntoDeviceMoreFailed = "进入设备更多界面失败！";
//		public static final String IntoDeviceMoreAreaFailed = "进入更多分区界面失败！";
//		public static final String IntoDeviceDetailFailed = "进入设备详情界面失败！";
//		public static final String IntoAreaFailed = "进入分区管理失败！";
//		public static final String IntoAllSceneFailed = "进入全部场景界面失败！";
//		public static final String IntoAddSceneFailed = "进入添加场景界面失败！";
//		public static final String IntoEditSceneFailed = "进入编辑场景界面失败！";
//		public static final String IntoSceneSortFailed = "进入场景排序界面失败！";
//		public static final String IntoEditScenePageFailed = "未展示编辑场景页面！";
//		public static final String IntoCustomScenePageFailed = "未展示自定义场景页面！";
//		public static final String IntoAddDevicePageFailed = "未展示添加设备页面！";
//		public static final String IntoDeviceStatePageFailed = "未展示设置设备状态页面！";
//		public static final String IntoAddDelayPageFailed = "未展示添加延迟页面！";
//		public static final String IntoGatewayLoginFailed = "进入网关登录界面失败！";
//	}
//}
