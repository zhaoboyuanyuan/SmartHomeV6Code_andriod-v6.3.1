package cc.wulian.smarthomev6.tst;

import cc.wulian.smarthomev6.app.StartApp;
import cc.wulian.smarthomev6.proc.ChangePasswordProc;
import com.wtt.runner.android.IAfterCondition;
import com.wtt.runner.android.IBeforeCondition;

/**
 * Created by 赵永健 on 2017/5/10.
 *
 * 封装修改账号密码的测试用例
 */
public class ChangePasswordTest extends StartApp implements IBeforeCondition, IAfterCondition {

	private ChangePasswordProc proc;

	@Override
	public void before() {
		proc = new ChangePasswordProc(getSolo());
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

	/**
	 * 修改账号密码
	 */
	public void testChangePassword() {
		proc.changePassword();
	}

	/**
	 * 修改账号密码--密码至少有数字、字母或符号的2种组合
	 */
	public void testChangePasswordAsOldWrong() {
		proc.changePasswordAsNoMatch();
	}

	/**
	 * 修改账号密码--新密码少于6位
	 */
	public void testChangePasswordAsNewLessThan6() {
		proc.changePasswordAsNewLessThan6();
	}

	/**
	 * 修改账号密码--新密码多于15位
	 */
//	public void testChangePasswordAsNewMoreThan20() {
//		proc.changePasswordAsNewMoreThan20();
//	}
//
//	/**
//	 * 修改账号密码--新密码与原始密码相同
//	 */
//	public void testChangePasswordAsNewSameAsOld() {
//		proc.changePasswordAsNewSameAsOld();
//	}

//	/**
//	 * 修改账号密码--新密码与确认新密码两次输入不一致
//	 */
//	public void testChangePasswordAsDiffBetweenNewAndConf() {
//		proc.changePasswordAsDiffBetweenNewAndConf();
//	}

	/**
	 * 修改账号密码--新密码为空
	 */
	public void testChangePasswordAsNewEmpty() {
		proc.changePasswordAsNewEmpty();
	}
}
