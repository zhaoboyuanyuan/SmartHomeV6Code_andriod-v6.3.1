package cc.wulian.smarthomev6.tst;

import cc.wulian.smarthomev6.app.StartApp;
import cc.wulian.smarthomev6.proc.VisitProc;
import com.wtt.runner.android.IAfterCondition;
import com.wtt.runner.android.IBeforeCondition;

/**
 * Created by 赵永健 on 2017/5/8.
 *
 * 封装游客模式测试用例
 */
public class VisitTest extends StartApp implements IBeforeCondition, IAfterCondition {

	private VisitProc proc;

	@Override
	public void before() {
		proc = new VisitProc(getSolo());
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
	 * 以游客模式登录首页
	 */
	public void testLoginByVisitor() {
		proc.loginByVisitor();
	}

	/**
	 * 以游客模式点击消息中心
	 */
	public void testClickMessageCenterByVisitor() {
		proc.clickToMessageCenterByVisitor();
	}

	/**
	 * 以游客模式查看横幅广告
	 */
	public void testCheckNoticeByVisitor() {
		proc.checkAdvertising();
	}

	/**
	 * 以游客模式点击场景
	 */
	public void testClickScenesByVisitor() {
		proc.clickToScenesByVisitor();
	}

	/**
	 * 以游客点击设备
	 */
	public void testClickDeviceByVisitor() {
		proc.clickToDeviceByVisitor();
	}

	/**
	 * 以游客模式点击发现
	 */
	public void testClickFindByVisitor() {
		proc.clickToFindByVisitor();
	}

	/**
	 * 以游客模式点击我的
	 */
	public void testClickMineByVisitor() {
		proc.clickToMineByVisitor();
	}

	/**
	 * 以游客模式点击登录/注册
	 */
	public void testClickLoginByVisitor() {
		proc.clickToLoginByVisitor();
	}

	/**
	 * 以游客模式点击网关中心
	 */
	public void testClickGatewayCenterByVisitor() {
		proc.clickToGatewayCenterByVisitor();
	}

	/**
	 * 以游客模式点击客服
	 */
	public void testClickCustomerServiceByVisitor() {
		proc.clickToCustomerServiceByVisitor();
	}

	/**
	 * 以游客模式点击我的管家
	 */
	public void testClickMyManagerByVisitor() {
		proc.clickToMyManagerByVisitor();
	}

	/**
	 * 以游客模式点击设置
	 */
	public void testClickBySettingsByVisitor() {
		proc.clickBySettingsByVisitor();
	}

//	/**
//	 * 以游客模式点击底部【编辑】--增加项目
//	 */
//	public void testAddShortcutByVisitor() {
//		proc.addShortcutByVisitor();
//	}
//
//	/**
//	 * 以游客模式点击底部【编辑】--减少项目
//	 */
//	public void testReduceShortcutByVisitor() {
//		proc.reduceShortcutByVisitor();
//	}
}
