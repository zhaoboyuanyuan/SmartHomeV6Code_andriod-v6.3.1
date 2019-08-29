package cc.wulian.smarthomev6.tst;

import cc.wulian.smarthomev6.app.StartApp;
import cc.wulian.smarthomev6.proc.BindGatewayProc;
import com.wtt.runner.android.IAfterCondition;
import com.wtt.runner.android.IBeforeCondition;

/**
 * Created by 赵永健 on 2017/5/9.
 *
 * 封装绑定网关的测试用例，这个测试用例需要未绑定的账号下跑
 */
public class BindGatewayTest extends StartApp implements IBeforeCondition, IAfterCondition {

	private BindGatewayProc proc;

	@Override
	public void before() {
		proc = new BindGatewayProc(getSolo());
		proc.init();
	}

	@Override
	public void after() {
		if (null != proc) {
			proc.finalize();
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
	 * 绑定网关
	 */
	public void testBIntoGatewayList() {
		proc.intoGatewayList();
	}

	/**
	 * 绑定网关成功
	 */
	public void testCBindGatewaySuccess() {
		proc.bindGatewaySuccess();
	}

	/**
	 * 绑定网关失败 - 空ID
	 */
	public void testBindGatewayASEmptyNum() {
		proc.bindGatewayASEmptyNum();
	}

	/**
	 * 绑定网关失败 - 空密码
	 */
	public void testBindGatewayAsEmptyPassword() {
		proc.bindGatewayAsEmptyPassword();
	}

	/**
	 * 绑定网关失败 - 密码小于6位
	 */
	public void testBindGatewayAsPasswordLessThanSix() {
		proc.bindGatewayAsPasswordLessThanSix();
	}

	/**
	 * 绑定网关失败 - 密码错误
	 */
	public void testBindGatewayAsWrongPassword() {
		proc.bindGatewayAsWrongPassword();
	}
}
