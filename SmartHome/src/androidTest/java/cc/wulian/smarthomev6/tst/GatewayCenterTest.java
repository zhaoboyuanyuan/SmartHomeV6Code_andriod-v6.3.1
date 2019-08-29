package cc.wulian.smarthomev6.tst;


import cc.wulian.smarthomev6.app.StartApp;
import cc.wulian.smarthomev6.proc.GatewayCenterProc;
import com.wtt.runner.android.IAfterCondition;
import com.wtt.runner.android.IBeforeCondition;

/**
 * Created by 赵永健 on 2017/5/9.
 *
 * 网关中心
 */
public class GatewayCenterTest extends StartApp implements IBeforeCondition, IAfterCondition {

	private GatewayCenterProc gatewayCenterProc;

	@Override
	public void before() {
		gatewayCenterProc = new GatewayCenterProc(getSolo());
		gatewayCenterProc.init();
	}

	@Override
	public void after() {
		if (null != gatewayCenterProc) {
			try {
				gatewayCenterProc.finalize();
			} catch (Throwable throwable) {
				throwable.printStackTrace();
			}
			gatewayCenterProc = null;
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
	 * 查看网关中心页面
	 */
	public void testCheckGatewayCenter() {
		gatewayCenterProc.checkGatewayCenterList();
	}

	/**
	 * 切换网关,这条用例重复
	 */
//	public void testChangeGateway() {
//		gatewayCenterProc.changeGateway();
//	}
}
