package cc.wulian.smarthomev6.tst;

import cc.wulian.smarthomev6.app.StartApp;
import cc.wulian.smarthomev6.proc.IntoAppProc;
import com.wtt.runner.android.IAfterCondition;
import com.wtt.runner.android.IBeforeCondition;

/**
 * Created by 赵永健 on 2017/5/9.
 *
 * 封装账号登录进入App的测试用例
 */
public class IntoAppTest extends StartApp implements IBeforeCondition, IAfterCondition {

	private IntoAppProc proc;

	@Override
	public void before() {
		proc = new IntoAppProc(getSolo());
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
	 * 首页
	 */
	public void testCheckHomePage() {
		proc.intoHomepage();
	}

	/**
	 * 查看消息中心
	 */
	public void testCheckMessageCenter() {
		proc.intoMessageCenter();
	}

	/**
	 * 点击场景
	 */
	public void testClickScenes() {
		proc.intoAsClickWhileScenes();
	}

	/**
	 * 点击【设备】
	 */
	public void testClickDevice() {
		proc.intoAsClickDevice();
	}

	/**
	 * 点击【发现】
	 */
	public void testClickFind() {
		proc.intoAsClickFind();
	}

	/**
	 * 点击【我的】
	 */
	public void testDClickMine() {
		proc.intoAsClickMine();
	}
}
