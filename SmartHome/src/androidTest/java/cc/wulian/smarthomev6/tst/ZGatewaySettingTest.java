package cc.wulian.smarthomev6.tst;

import cc.wulian.smarthomev6.app.StartApp;
import cc.wulian.smarthomev6.proc.GatewaySettingProc;
import com.wtt.runner.android.IAfterCondition;
import com.wtt.runner.android.IBeforeCondition;

/**
 * Created by 赵永健 on 2017/5/31.
 *
 * 封装网关设置测试用例
 */
public class ZGatewaySettingTest extends StartApp implements IBeforeCondition, IAfterCondition {

	private GatewaySettingProc proc;

	@Override
	public void before() {
		proc = new GatewaySettingProc(getSolo());
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
//	 * 网关密码修改--新密码6位
//	 */
//	public void testChangeGatewayPasswordAsSix() {
//		proc.changeGatewayPasswordAs6();
//	}

//	/**
//	 * 网关密码修改--新密码20位
//	 */
//	public void testChangeGatewayPasswordAs16() {
//		proc.changeGatewayPasswordAs15();
//	}

	/**
	 * 网关密码修改--直接网关登录
	 */
	public void testEChangeGatewayPasswordAsGatewayLogin() {
		proc.changeGatewayPasswordAsGatewayLogin();
	}

	/**
	 * 原始密码不正确
	 */
	public void testChangeAsOldPasswordWrong() {
		proc.changeAsOldPasswordWrong();
	}

	/**
	 * 新密码少于6位
	 */
	public void testChangeAsNewPasswordLessThan6() {
		proc.changeAsNewPasswordLessThan6();
	}

//	/**
//	 * 新密码多于20位
//	 */
//	public void testChangeAsNewPasswordMoreThan20() {
//		proc.changeAsNewPasswordMoreThan20();
//	}

//	/**
//	 * 两次密码不一致
//	 */
//	public void testChangeAsDiffFormNewAndConf() {
//		proc.changeAsDiffFormNewAndConf();
//	}

	/**
	 * 新密码与原密码相同
	 */
	public void testDChangeAsNewSameAsOld() {
		proc.changeAsNewSameAsOld();
	}

	/**
	 * 网关昵称修改--昵称少于15个字符
	 */
	public void testChangeNikeNameLessThan15() {
		proc.changeNikeNameLessThan15();
	}

	/**
	 * 网关昵称修改--昵称为15个字符
	 */
	public void testChangeNikeNameEquals15() {
		proc.changeNikeNameEquals15();
	}

	/**
	 * 网关昵称修改--昵称多于15个字符
	 */
	public void testChangeNikeNameMoreThan15() {
		proc.changeNikeNameMoreThan15();
	}
}
