package cc.wulian.smarthomev6.tst;

import cc.wulian.smarthomev6.app.StartApp;
import cc.wulian.smarthomev6.proc.MyInfoProc;
import com.wtt.runner.android.IAfterCondition;
import com.wtt.runner.android.IBeforeCondition;

/**
 * Created by 赵永健 on 2017/5/9.
 *
 * 封装个人信息测试用例
 */
public class MyInfoTest  extends StartApp implements IBeforeCondition, IAfterCondition {

	private MyInfoProc proc;

	@Override
	public void before() {
		proc = new MyInfoProc(getSolo());
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
	 * 修改姓名--少于20个字符
	 */
	public void testChangeNameLessThanTwenty() {
		proc.changeNameLessThanTwenty();
	}

	/**
	 * 取消修改姓名
	 */
	public void testChangeNameCancel() {
		proc.changeNameCancel();
	}

	/**
	 * 修改姓名--多于20个字符
	 */
	public void testChangeNameMoreThanTwenty() {
		proc.changeNameMoreThanTwenty();
	}

	/**
	 * 修改姓名--姓名为空
	 */
	public void testChangeNameAsNotEnter() {
		proc.changeNameAsNotEnter();
	}
}
