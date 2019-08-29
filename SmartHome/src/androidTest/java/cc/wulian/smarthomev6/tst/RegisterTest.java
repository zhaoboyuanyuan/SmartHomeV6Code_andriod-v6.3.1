//package cc.wulian.smarthomev6.tst;
//
//import com.wtt.frame.robotium.Solo;
//import cc.wulian.smarthomev6.proc.CommonProc;
//import cc.wulian.smarthomev6.proc.RegisterProc;
//import cc.wulian.smarthomev6.runner.SmartHomeTestHelper;
//import cc.wulian.smarthomev6.utils.ClickUtils;
//import cc.wulian.smarthomev6.utils.EnterUtils;
//import cc.wulian.smarthomev6.utils.GetterUtils;
//import cc.wulian.smarthomev6.utils.WaitForUtils;
//import com.wtt.runner.android.IAfterCondition;
//import com.wtt.runner.android.IBeforeCondition;
//
///**
// * Created by 严君 on 2017/5/8.
// *
// * 注册测试用例
// */
//public class RegisterTest  extends SmartHomeTestHelper implements IBeforeCondition, IAfterCondition {
//
//	private RegisterProc registerProc;
//	private CommonProc commonProc;
//	private GetterUtils getter;
//	private EnterUtils enter;
//	private ClickUtils click;
//	private WaitForUtils waitFor;
//
//	@Override
//	public void before() {
//		Solo solo = getSolo();
//		getter = new GetterUtils(solo);
//		enter = new EnterUtils(solo, getter);
//		waitFor = new WaitForUtils(solo);
//		click = new ClickUtils(solo, getter, waitFor);
//		commonProc = new CommonProc(solo, click, waitFor, getter);
//		registerProc = new RegisterProc(solo, commonProc, getter, enter, click);
//
//		registerProc.registerInit();
//		registerProc.clearRegister("18915923526");
//	}
//
//	@Override
//	public void after() {
//		getter = null;
//		enter = null;
//		waitFor = null;
//		click = null;
//		commonProc = null;
//		registerProc = null;
//	}
//
//	@Override
//	public void setUp() throws Exception {
//		setBefore(this);
//		super.setUp();
//	}
//
//	@Override
//	public void tearDown() throws Exception {
//		setAfter(this);
//		super.tearDown();
//	}
//
//	/**
//	 * 账号注册---手机号注册密码6位数字
//	 */
//	public void testRegisterPasswordAsSixNum() {
//		registerProc.registerSuccess("18915923526", "123456");
//	}
//
//	/**
//	 * 账号注册---手机号注册，密码20位字母
//	 */
//	public void testRegisterPasswordAsTwentyLetters() {
//		registerProc.registerSuccess("18915923526", "aaaaaaaaaaaaaaaaaaaa");
//	}
//
//	/**
//	 * 账号注册---手机号注册，密码13位字母和数字混合
//	 */
//	public void testRegisterPasswordAsNumAndLetters() {
//		registerProc.registerSuccess("18915923526", "123456aaaaaaa");
//	}
//
//	/**
//	 * 账号注册---手机号注册注册过程中网络异常
//	 */
//	public void testRegisterAsNetException() {
//		registerProc.registerAsNetError("18915923526", "123456");
//	}
//
//	/**
//	 * 账号注册---手机号已被注册
//	 */
//	public void testRegisterAsAccountRegistered() {
//		registerProc.registerSuccess("18915923526", "123456");
//		registerProc.registerAsRegistered("18915923526");
//	}
//
//	/**
//	 * 账号注册---手机号注册验证码错误
//	 */
//	public void testRegisterAsVerificationCodeWrong() {
//		registerProc.registerAsVerCodeError("18915923526", "123456", "0000");
//	}
//
//	/**
//	 * 账号注册---手机号注册验证码超时
//	 */
//	public void testRegisterAsVerificationCodeTimeout() {
//		registerProc.registerAsVerCodeTimeout("18915923526", "123456");
//	}
//
//	/**
//	 * 账号注册---手机号注册密码不足6位
//	 */
//	public void testRegisterPasswordLessThanSix() {
//		registerProc.registerAsFail("18915923526", "12345", "密码格式为6~20位字母或数字");
//	}
//
//	/**
//	 * 账号注册---手机号注册密码多于20位
//	 */
//	public void testRegisterPasswordMoreThanTwenty() {
//		registerProc.registerAsFail("18915923526", "aaaaaaaaaaaaaaaaaaaa1"
//				, "密码格式为6~20位字母或数字");
//	}
//
//	/**
//	 * 账号注册---手机号注册密码中含有特殊字符
//	 */
//	public void testRegisterPasswordAsSpecialChar() {
//		registerProc.registerAsFail("18915923526", "~!@#$%&"
//				, "密码格式为6~20位字母或数字");
//	}
//
//	/**
//	 * 账号注册---手机号注册未勾选《使用条款与免责协议》
//	 */
//	public void testRegisterAsNonSelectPact() {
//		registerProc.registerAsNonSelectedPart("18915923526", "123456");
//	}
//
//	/**
//	 * 账号注册--查看“条款与免责协议”页面
//	 */
//	public void testRegisterAsCheckPact() {
//		registerProc.intoPart("18915923526", "123456");
//	}
//}
