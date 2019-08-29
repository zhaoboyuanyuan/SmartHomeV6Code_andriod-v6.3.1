package cc.wulian.smarthomev6.tst;

import cc.wulian.smarthomev6.app.StartApp;
import cc.wulian.smarthomev6.proc.GatewayLoginProc;
import com.wtt.runner.android.IAfterCondition;
import com.wtt.runner.android.IBeforeCondition;

/**
 * Created by 赵永健 on 2017/5/10.
 *
 * 封装网关登录测试用例
 */
public class GatewayLoginTest extends StartApp implements IBeforeCondition, IAfterCondition {

	private GatewayLoginProc proc;

	@Override
	public void before() {
		proc = new GatewayLoginProc(getSolo());
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
	 * 网关登录
	 */
	public void testGatewayLoginSuccess() {
		proc.gatewayLoginSuccess();
	}

	/**
	 * 网关登录失败
	 */
	public void testGatewayLoginAsNumWrong() {
		proc.gatewayLoginAsNumWrong();
	}


	/**
	 * 空网关号登录
	 */
	public void testGatewayLoginAsEmptyNum() {
		proc.gatewayLoginAsEmptyNumber();
	}

	/**
	 * 空网关密码登录
	 */
	public void testGatewayLoginAsEmptyPassword() {
		proc.gatewayLoginAsEmptyPassword();
	}

	/**
	 * 网关密码少于6位登录
	 */
	public void testGatewayLoginAsPasswordLessThanSix() {
		proc.gatewayLoginAsPasswordLessThanSix();
	}

//	/**
//	 * 网关登录不成功
//	 */
//	public void testGatewayLoginAsNotNet() {
//		proc.gatewayLoginAsNotNet();
//	}
//
	/**
	 * 网关密码不匹配登录
	 */
	public void testGatewayLoginAsNotMatch() {
		proc.gatewayLoginAsWrongPassword();
	}
}
