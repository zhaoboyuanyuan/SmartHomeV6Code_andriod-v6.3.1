package cc.wulian.smarthomev6.tst;

import cc.wulian.smarthomev6.app.StartApp;
import cc.wulian.smarthomev6.proc.DeviceSetPartitionProc;
import com.wtt.runner.android.IAfterCondition;
import com.wtt.runner.android.IBeforeCondition;

/**
 * Created by 赵永健 on 2017/5/15.
 *
 * 封装设置分区的测试用例
 */
public class DeviceSetPartitionTest extends StartApp implements IBeforeCondition, IAfterCondition {

	private DeviceSetPartitionProc proc;

	@Override
	public void before() {
		proc = new DeviceSetPartitionProc(getSolo());
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
	 * 更多设置--设置分区
	 */
	public void testSetPartition() {
		proc.setPartition();
	}
}
