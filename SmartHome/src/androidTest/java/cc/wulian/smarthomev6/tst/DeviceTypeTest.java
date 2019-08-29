package cc.wulian.smarthomev6.tst;

import cc.wulian.smarthomev6.app.OpenApp;
import cc.wulian.smarthomev6.proc.DeviceTypeProc;
import com.wtt.runner.android.IAfterCondition;
import com.wtt.runner.android.IBeforeCondition;

/**
 * Created by 赵永健 on 2017/5/16.
 *
 * 封装设备类别的测试用例
 */
public class DeviceTypeTest extends OpenApp implements IBeforeCondition, IAfterCondition {

	private DeviceTypeProc proc;

	@Override
	public void before() {
		proc = new DeviceTypeProc(getSolo());
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
	 * 设备--全部类别
	 */
	public void testMatchAllType() {
		proc.matchAllType();
	}
}
