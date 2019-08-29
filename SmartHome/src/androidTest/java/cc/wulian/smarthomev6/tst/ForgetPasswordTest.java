package cc.wulian.smarthomev6.tst;

import cc.wulian.smarthomev6.app.StartApp;
import cc.wulian.smarthomev6.proc.ForgetPasswordProc;
import com.wtt.runner.android.IAfterCondition;
import com.wtt.runner.android.IBeforeCondition;

/**
 * Created by 赵永健 on 2017/12/19.
 *
 * 封装忘记密码测试用例
 */
public class ForgetPasswordTest extends StartApp implements IBeforeCondition, IAfterCondition {

	private ForgetPasswordProc proc;

	@Override
	public void before() {
		proc = new ForgetPasswordProc(getSolo());
		proc.init();
	}

	@Override
	public void after() {
		if (null != proc) {
			try {
				proc.finalize();
			} catch (Throwable throwable) {
				throwable.printStackTrace();
			}
			proc = null;
		}
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		before();
	}

	@Override
	public void tearDown() throws Exception {
		after();
		super.tearDown();

	}

//	/**
//	 * 1.新密码设置成功
//	 */
//	public void testSetPasswordSuccess(){
//		proc.setPasswordSuccess();
//	}

	/**
	 * 2.新密码设置失败--手机号格式不正确
	 */
	public void testMobileNumberWrong(){
		proc.mobileNumberWrong();
	}

	/**
	 * 3.手机号未注册
	 */
	public void testMobileNumberNotExit(){
		proc.mobileNumberNotExit();
	}

	/**
	 * 4.验证码不正确
	 */
	public void testVerificationWrong(){
		proc.verificationWrong();
	}

	/**
	 * 5.输入5位密码
	 */
	public void testPasswordLessThan8Char(){
		proc.passwordLessThan8Char();
	}

	/**
	 * 6.6位纯数字
	 */
	public void testPasswordAsNoMatch(){
		proc.passwordAsNoMatch();
	}
	/**
	 * 7.密码中含有空格
	 */
	public void testPasswordAsIllegalChar(){
		proc.passwordAsIllegalChar();
	}
//	/**
//	 * 8.密码强度弱,交互已经改变
//	 */
//	public void testPasswordAsWeak(){
//		proc.passwordAsWeak();
//	}
	/**
	 * 9.密码中含有非法字符（合法字符为西文键盘符号）
	 */
	public void testPasswordAsIllegalChar1(){
		proc.passwordAsIllegalChar1();
	}
	/**
	 * 10.6位纯字母 fal
	 */
	public void testPasswordAsNoMatch1(){
		proc.passwordAsNoMatch1();
	}
	/**
	 * 11.6位纯字符
	 */
	public void testPasswordAsNoMatch2(){
		proc.passwordAsNoMatch2();
	}

	/**
	 * 20.含有8位纯数字
	 */
	public void testPassAsEight(){
		proc.passAsEight();
	}


//	/**
//	 * 12.10位密码，数字加字母
//	 */
//	public void testChangePasswordAsSuccess(){
//		proc.changePasswordAsSuccess();
//	}
//	/**
//	 * 13.10位密码，数字加字符
//	 */
//	public void testChangePasswordAsSuccess1(){
//		proc.changePasswordAsSuccess1();
//	}
//	/**
//	 * 14.10位密码，数字加字符加字母
//	 */
//	public void testChangePasswordAsSuccess2(){
//		proc.changePasswordAsSuccess2();
//	}
//	/**
//	 * 15.15位密码，数字加字符加字母
//	 */
//	public void testChangePasswordAsSuccess3(){
//		proc.changePasswordAsSuccess3();
//	}
//	/**
//	 * 16.16位密码，数字加字符加字母
//	 */
//	public void testChangePasswordAsSuccess4(){
//		proc.changePasswordAsSuccess4();
//	}
//	/**
//	 * 17.20位密码，数字加字符加字母
//	 */
//	public void testChangePasswordAsSuccess5(){
//		proc.changePasswordAsSuccess5();
//	}
//	/**
//	 * 18.含有数字和符号两种，密码长度为6到16位
//	 */
//	public void testChangePasswordAsSuccess6(){
//		proc.changePasswordAsSuccess6();
//	}
//	/**
//	 * 19.含有字母和符号两种，密码长度为6到16位
//	 */
//	public void testChangePasswordAsSuccess7(){
//		proc.changePasswordAsSuccess7();
//	}

}
