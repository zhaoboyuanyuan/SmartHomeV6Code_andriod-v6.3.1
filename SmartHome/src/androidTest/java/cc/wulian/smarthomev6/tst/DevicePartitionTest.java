package cc.wulian.smarthomev6.tst;

import cc.wulian.smarthomev6.app.StartApp;
import cc.wulian.smarthomev6.proc.DevicePartitionProc;
import com.wtt.runner.android.IAfterCondition;
import com.wtt.runner.android.IBeforeCondition;

/**
 * Created by 赵永健 on 2017/5/15.
 *
 * 封装设备分区的测试用例
 */
public class DevicePartitionTest extends StartApp implements IBeforeCondition, IAfterCondition {

	private DevicePartitionProc proc;

	@Override
	public void before() {
		proc = new DevicePartitionProc(getSolo());
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
	 * 设备--全部分区
	 */
	public void testMatchPartition() {
		proc.matchPartition();
	}

	/**
	 * 设备--分区较多
	 */
	public void testMatchMorePartition() {
		proc.matchMorePartition();
	}
}
