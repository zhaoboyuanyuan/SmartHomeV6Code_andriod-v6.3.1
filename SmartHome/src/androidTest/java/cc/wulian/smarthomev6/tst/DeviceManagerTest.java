package cc.wulian.smarthomev6.tst;

import cc.wulian.smarthomev6.app.StartApp;
import cc.wulian.smarthomev6.proc.DeviceManagerProc;
import com.wtt.runner.android.IAfterCondition;
import com.wtt.runner.android.IBeforeCondition;

/**
 * Created by 赵永健 on 2017/5/16.
 *
 * 封装设备管理的测试用例,设备分区(测试时，分区的数量要尽量少，避免搜索时，发生错误)
 */
public class DeviceManagerTest extends StartApp implements IBeforeCondition, IAfterCondition {

	private DeviceManagerProc proc;

	@Override
	public void before() {
		proc = new DeviceManagerProc(getSolo());
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
	 * 编辑分区--2个字符
	 */
	public void testAEditAsEquals2() {
		proc.editAsEquals2();
	}

	/**
	 * 编辑分区--15个字符
	 */
	public void testAEditAsEquals15() {
		proc.editAsEquals15();
	}

	/**
	 * 取消编辑分区
	 */
	public void testAEditAsCancel() {
		proc.editAsCancel();
	}

	/**
	 * 删除分区
	 */
	public void testBDeleteAsConfirm() {
		proc.deleteAsConfirm();
	}

	/**
	 * 取消删除分区
	 */
	public void testBDeleteAsCancel() {
		proc.deleteAsCancel();
	}

	/**
	 * 新建分区，名称少于15个
	 */
	public void testCreateAsLessThan15() {
		proc.createAsLessThan15();
	}

	/**
	 * 新建分区，名称多于15个
	 */
	public void testACreateAsMoreThan15() {
		proc.createAsMoreThan15();
	}

	/**
	 * 取消新建分区
	 */
	public void testCreateAsCancel() {
		proc.createAsCancel();
	}

	/**
	 * 新建分区，名称为空
	 */
	public void testCreateAsNameEmpty() {
		proc.createAsNameEmpty();
	}

	/**
	 * 修改分区名称
	 */
	public void testUpdateAsConf() {
		proc.updateAsConf();
	}

	/**
	 * 取消修改分区名称
	 */
	public void testUpdateAsCancel() {
		proc.updateAsCancel();
	}

}
