//package cc.wulian.smarthomev6.tst;
//
//import cc.wulian.smarthomev6.proc.AccountSecurityProc;
//import cc.wulian.smarthomev6.runner.SmartHomeTestHelper;
//import com.wtt.runner.android.IAfterCondition;
//import com.wtt.runner.android.IBeforeCondition;
//
///**
// * Created by 严君 on 2017/5/10.
// *
// * 封装账号安全测试用例
// */
//public class AccountSecurityTest extends SmartHomeTestHelper implements IBeforeCondition, IAfterCondition {
//
//	private AccountSecurityProc proc;
//
//	@Override
//	public void before() {
//		proc = new AccountSecurityProc(getSolo());
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
////	/**
////	 * 手机号修改
////	 */
////	public void testChangeMobileNumber() {
////		proc.changeMobileNumber();
////	}
//
//	/**
//	 * 设置已被注册的手机号
//	 */
//	public void testChangeMobileNumberAsRegistered() {
//		proc.changeMobileNumberAsRegistered();
//	}
//
//	/**
//	 * 设置不正确手机号
//	 */
//	public void testChangeMobileNumberAsWrongType() {
//		proc.changeMobileNumberAsWrongType();
//	}
//
//	/**
//	 * 设置空的手机号
//	 */
//	public void testChangeMobileNumberAsEmpty() {
//		proc.changeMobileNumberAsEmpty();
//	}
//
//	/**
//	 * 输入错误的验证码
//	 */
//	public void testChangeMobileNumberAsWrongCode() {
//		proc.changeMobileNumberAsWrongCode();
//	}
//
//	/**
//	 * 输入超过6位错误的验证码
//	 */
//	public void testChangeMobileNumberAsCodeMoreThan6() {
//		proc.changeMobileNumberAsCodeMoreThan6();
//	}
//
////	/**
////	 * 绑定正确的邮箱
////	 */
////	public void testBindMail() {
////		proc.bindMail();
////	}
//
//	/**
//	 * 绑定格式不正确的邮箱
//	 */
//	public void testBindMailAsWrongType() {
//		proc.bindMailAsWrongType();
//	}
//
//	/**
//	 * 绑定已经被验证的邮箱
//	 */
//	public void testBindMailAsRegistered() {
//		proc.bindMailAsRegistered();
//	}
//
//	public void testAAA() {
//		getSolo().clickOnButton("确定");
//	}
//}
