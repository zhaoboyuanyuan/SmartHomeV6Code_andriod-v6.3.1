//package cc.wulian.smarthomev6.tst;
//
//import cc.wulian.smarthomev6.proc.GatewaySettingProc;
//import cc.wulian.smarthomev6.runner.SmartHomeTestHelper;
//import com.wtt.runner.android.IAfterCondition;
//import com.wtt.runner.android.IBeforeCondition;
//
///**
// * Created by 严君 on 2017/5/31.
// *
// * 封装网关设置测试用例
// */
//public class GatewaySettingTest extends SmartHomeTestHelper implements IBeforeCondition, IAfterCondition {
//
//	private GatewaySettingProc proc;
//
//	@Override
//	public void before() {
//		proc = new GatewaySettingProc(getSolo());
//		proc.init();
//	}
//
//	@Override
//	public void after() {
//		if (null != proc) {
//			try {
//				proc.finalize();
//			} catch (Throwable throwable) {
//				throwable.printStackTrace();
//			}
//			proc = null;
//		}
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
//	 * 网关密码修改--新密码6位
//	 */
//	public void testChangeGatewayPasswordAsSix() {
//		proc.changeGatewayPasswordAs6();
//	}
//
//	/**
//	 * 网关密码修改--新密码20位
//	 */
//	public void testChangeGatewayPasswordAs20() {
//		proc.changeGatewayPasswordAs20();
//	}
//
//	/**
//	 * 网关密码修改--直接网关登录
//	 */
//	public void testChangeGatewayPasswordAsGatewayLogin() {
//		proc.changeGatewayPasswordAsGatewayLogin();
//	}
//
//	/**
//	 * 原始密码不正确
//	 */
//	public void testChangeAsOldPasswordWrong() {
//		proc.changeAsOldPasswordWrong();
//	}
//
//	/**
//	 * 新密码少于6位
//	 */
//	public void testChangeAsNewPasswordLessThan6() {
//		proc.changeAsNewPasswordLessThan6();
//	}
//
//	/**
//	 * 新密码多于20位
//	 */
//	public void testChangeAsNewPasswordMoreThan20() {
//		proc.changeAsNewPasswordMoreThan20();
//	}
//
//	/**
//	 * 两次密码不一致
//	 */
//	public void testChangeAsDiffFormNewAndConf() {
//		proc.changeAsDiffFormNewAndConf();
//	}
//
//	/**
//	 * 新密码与原密码相同
//	 */
//	public void testChangeAsNewSameAsOld() {
//		proc.changeAsNewSameAsOld();
//	}
//
//	/**
//	 * 网关昵称修改--昵称少于20个字符
//	 */
//	public void testChangeNikeNameLessThan20() {
//		proc.changeNikeNameLessThan20();
//	}
//
//	/**
//	 * 网关昵称修改--昵称为20个字符
//	 */
//	public void testChangeNikeNameEquals20() {
//		proc.changeNikeNameEquals20();
//	}
//
//	/**
//	 * 网关昵称修改--昵称多于20个字符
//	 */
//	public void testChangeNikeNameMoreThan20() {
//		proc.changeNikeNameMoreThan20();
//	}
//}
