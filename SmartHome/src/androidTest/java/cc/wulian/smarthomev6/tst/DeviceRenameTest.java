package cc.wulian.smarthomev6.tst;

import cc.wulian.smarthomev6.app.StartApp;
import cc.wulian.smarthomev6.proc.DeviceRenameProc;
import com.wtt.runner.android.IAfterCondition;
import com.wtt.runner.android.IBeforeCondition;

/**
 * Created by 赵永健 on 2017/5/15.
 *
 * 封装设备重命名的测试用例
 */
public class DeviceRenameTest extends StartApp implements IBeforeCondition, IAfterCondition {

	private DeviceRenameProc proc;

	@Override
	public void before() {
		proc = new DeviceRenameProc(getSolo());
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
	 * 更多设置--重命名  15个字符数限制
	 */
	public void testRenameAsEquals20() {
		proc.renameAsEquals20();
	}

	/**
	 * 更多设置--重命名  超过15个字符数
	 */
	public void testRenameAsMoreThan20() {
		proc.renameAsMoreThan20();
	}

	/**
	 * 更多设置--重命名  等于14个字符数
	 */
	public void testRenameAsEquals19() {
		proc.renameAsEquals19();
	}

	/**
	 * 更多设置--子设备重命名
	 */
	public void testRenameAsChildDevice() {
		proc.renameAsChildDevice();
	}

	/**
	 * 更多设置--确定设备重命名
	 */
	public void testRenameAsConfirm() {
		proc.renameAsConfirm1();
	}
	/**
	 * 更多设置--取消设备重命名
	 */
	public void testRenameAsCancel() {
		proc.renameAsCancel();
	}
}
