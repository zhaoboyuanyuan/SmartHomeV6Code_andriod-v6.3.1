package cc.wulian.smarthomev6.tst;

import cc.wulian.smarthomev6.app.StartApp;
import cc.wulian.smarthomev6.proc.CreateSceneProc;
import com.wtt.runner.android.IAfterCondition;
import com.wtt.runner.android.IBeforeCondition;

/**
 * Created by 赵永健 on 2017/5/17.
 *
 * 封装创建场景的测试用例
 */
public class CreateSceneTest extends StartApp implements IBeforeCondition, IAfterCondition {

	private CreateSceneProc proc;

	@Override
	public void before() {
		proc = new CreateSceneProc(getSolo());
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
	 * 新建自定义场景--名称长度为10位
	 */
	public void testCreateSceneAsName10() {
		proc.createSceneAsName10();
	}

	/**
	 * 新建自定义场景--名称长度为20位
	 */
	public void testCreateSceneAsName20() {
		proc.createSceneAsName20();
	}
//
//	/**
//	 * 新建自定义场景--名称长度为25位
//	 */
//	public void testCreateSceneAsName25() {
//		proc.createSceneAsName25();
//	}

	/**
	 * 新建自定义场景--不输入名称
	 */
	public void testCreateSceneAsNoneName() {
		proc.createSceneAsNoneName();
	}

	/**
	 * 新建自定义场景--未选择图标
	 */
	public void testCreateSceneAsNoneIcon() {
		proc.createSceneAsNoneIcon();
	}

	/**
	 * 场景名称重复命名
	 */
	public void testCreateSceneAsNameRepeat() {
		proc.createSceneAsNameRepeat();
	}
}
