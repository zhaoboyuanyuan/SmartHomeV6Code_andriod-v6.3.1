//package cc.wulian.smarthomev6.runner;
//
//import android.app.Activity;
//import android.app.Instrumentation;
//import android.test.ActivityInstrumentationTestCase2;
//import com.wtt.runner.android.IAfterCondition;
//import com.wtt.runner.android.IBeforeCondition;
//import com.wtt.runner.android.InstTestRunner;
//import com.wtt.frame.robotium.Solo;
//import com.wtt.frame.robotium.Solo.Config;
//import cc.wulian.smarthomev6.app.Activities;
//
///**
// * Created by 严君 on 2017/5/8.
// *
// * 测试用例公用测试集
// */
//public class SmartHomeTestHelper extends ActivityInstrumentationTestCase2 {
//
//	private static Class<?> lunchClass;
//
//	static {
//		try {
//			lunchClass = Class.forName(Activities.SplashActivity);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * Robotium实例
//	 */
//	private Solo solo;
//
//	/**
//	 * 被测试APP的启动界面
//	 */
//	private Activity activity;
//
//	/**
//	 * Robotium配置信息
//	 */
//	private Config config;
//
//	/**
//	 * 程序监听器
//	 */
//	private Instrumentation inst;
//
//	/**
//	 * 测试初始化接口
//	 */
//	private IBeforeCondition before;
//
//	/**
//	 * 垃圾回收接口
//	 */
//	private IAfterCondition after;
//
//	/**
//	 * 错误信息
//	 */
//	public static StringBuilder builder;
//
//	/**
//	 * 添加测试用例的执行过程中的错误信息
//	 *
//	 * @param msg - 测试用例的执行过程中的错误信息{@link java.lang.String}
//	 */
//	public static synchronized void append(String msg) {
//		builder.append(msg);
//	}
//
//	/**
//	 * 获取测试用例执行过程中所有的错误信息
//	 *
//	 * @return - 测试用例执行过程中所有的错误信息{@link java.lang.String}， 如果没有错误信息则返回{@code ''}
//	 */
//	private static synchronized String getMessage() {
//		return builder.toString();
//	}
//
//	/**
//	 * 判断错误信息是否为空
//	 *
//	 * @return - 没有错误信息返回{@code true}，否则返回{@code false}
//	 */
//	private static synchronized boolean isEmpty() {
//		return builder.toString().isEmpty();
//	}
//
//	/**
//	 * 构造器
//	 */
//	public SmartHomeTestHelper() {
//		super(lunchClass);
//	}
//
//	/**
//	 * 设置测试执行的前置条件
//	 *
//	 * @param before - 前置条件{@link IBeforeCondition}
//	 */
//	public void setBefore(IBeforeCondition before) {
//		this.before = before;
//	}
//
//	/**
//	 * 设置测试执行的后置条件
//	 *
//	 * @param after - 后置条件{@link IAfterCondition}
//	 */
//	public void setAfter(IAfterCondition after) {
//		this.after = after;
//	}
//
//	/**
//	 * 获取Robotium实例
//	 *
//	 * @return - {@link Solo}
//	 */
//	public Solo getSolo() {
//		return solo;
//	}
//
//	/**
//	 * 测试集的前置条件
//	 *
//	 * @throws Exception - 异常{@link java.lang.Exception}
//	 */
//	@Override
//	public void setUp() throws Exception {
//		builder = new StringBuilder();
//		if (!initConfig()) tearDown();
//		inst = getInstrumentation();
//		activity = getActivity();
//		solo = new Solo(inst, config, activity);
//		if (null != before) before.before();
//
//		super.setUp();
//	}
//
//	/**
//	 * 测试集的后置条件
//	 *
//	 * @throws Exception - 异常{@link java.lang.Exception}
//	 */
//	@Override
//	public void tearDown() throws Exception {
//		if (null != solo) {
//			solo.finishOpenedActivities();
//			try {
//				solo.finalize();
//			} catch (Throwable throwable) {
//				throwable.printStackTrace();
//			}
//			solo = null;
//		}
//
//		if (null != inst) inst = null;
//		if (null != activity) activity = null;
//		if (null != after) {
//			after.after();
//			after = null;
//		}
//
//		String msg = getMessage();
//		assertTrue(getMessage(), isEmpty());
//		builder = null;
//
//		super.tearDown();
//	}
//
//	/**
//	 * 初始化Robotium的配置信息
//	 *
//	 * @return - 成功返回{@code true}，否则返回{@link false}
//	 */
//	private boolean initConfig() {
//		config = new Config();
//		setTimeoutSmall();
//		setTimeoutLarge();
//		setScreenShotFileType();
//		setShouldScroll();
//		setUseJavaScriptToClickWebElements();
//		setTrackActivities();
//		setWebFrame();
//		setCommandLogging();
//		setCommandLoggingTag();
//		setSleepDuration();
//		setSleepMiniDuration();
//		setScreenShotSavePath();
//
//		return builder.toString().isEmpty();
//	}
//
//	/**
//	 * 设置最小超时时间
//	 */
//	private void setTimeoutSmall() {
//		try {
//			config.timeout_small = InstTestRunner.settings.getInt("timeout_small");
//		} catch (Exception e) {
//			builder.append(e.getMessage() + "\n");
//		}
//	}
//
//	/**
//	 * 设置最大超时时间
//	 */
//	private void setTimeoutLarge() {
//		try {
//			config.timeout_large = InstTestRunner.settings.getInt("timeout_large");
//		} catch (Exception e) {
//			builder.append(e.getMessage() + "\n");
//		}
//	}
//
//	/**
//	 * 设置截图后保存的图片的格式
//	 */
//	private void setScreenShotFileType() {
//		String fileType = InstTestRunner.settings.getString("screen_shot_file_type");
//		if ("JPEG".equals(fileType)) {
//			config.screenshotFileType = Config.ScreenshotFileType.JPEG;
//		} else if ("PNG".equals(fileType)) {
//			config.screenshotFileType = Config.ScreenshotFileType.PNG;
//		} else {
//			builder.append("Config of screenShotFileType is wrong!It is - " + fileType + "\n");
//		}
//	}
//
//	/**
//	 * 设置查询相关操作时是否要滚动界面
//	 */
//	private void setShouldScroll() {
//		try {
//			config.shouldScroll = InstTestRunner.settings.getBoolean("should_scroll");
//		} catch (Exception e) {
//			builder.append(e.getMessage() + "\n");
//		}
//	}
//
//	/**
//	 * 设置对WebView中网页元素进行操作做时是否使用Robotium注入的js
//	 */
//	private void setUseJavaScriptToClickWebElements() {
//		try {
//			config.useJavaScriptToClickWebElements
//					= InstTestRunner.settings.getBoolean("use_java_script_to_click_web_elements");
//		} catch (Exception e) {
//			builder.append(e.getMessage() + "\n");
//		}
//	}
//
//	/**
//	 * 设置是否启动Activity堆栈管理
//	 */
//	private void setTrackActivities() {
//		try {
//			config.trackActivities = InstTestRunner.settings.getBoolean("track_activities");
//		} catch (Exception e) {
//			builder.append(e.getMessage() + "\n");
//		}
//	}
//
//	/**
//	 * 设置WebFrame
//	 */
//	private void setWebFrame() {
//		try {
//			config.webFrame = InstTestRunner.settings.getString("web_frame");
//		} catch (Exception e) {
//			builder.append(e.getMessage() + "\n");
//		}
//	}
//
//	/**
//	 * 设置执行Robotium中API时是否输出日志
//	 */
//	private void setCommandLogging() {
//		try {
//			config.commandLogging = InstTestRunner.settings.getBoolean("command_logging");
//		} catch (Exception e) {
//			builder.append(e.getMessage() + "\n");
//		}
//	}
//
//	/**
//	 * 设置日志Tag
//	 */
//	private void setCommandLoggingTag() {
//		try {
//			config.commandLoggingTag = InstTestRunner.settings.getString("command_logging_tag");
//		} catch (Exception e) {
//			builder.append(e.getMessage() + "\n");
//		}
//	}
//
//	/**
//	 * 设置自动休眠时间
//	 */
//	private void setSleepDuration() {
//		try {
//			config.sleepDuration = InstTestRunner.settings.getInt("sleep_duration");
//		} catch (Exception e) {
//			builder.append(e.getMessage() + "\n");
//		}
//	}
//
//	/**
//	 * 设置最小自动休眠时间
//	 */
//	private void setSleepMiniDuration() {
//		try {
//			config.sleepMiniDuration = InstTestRunner.settings.getInt("sleep_duration");
//		} catch (Exception e) {
//			builder.append(e.getMessage() + "\n");
//		}
//	}
//
//	/**
//	 * 设置截图保存的路径
//	 */
//	private void setScreenShotSavePath() {
//		config.screenshotSavePath = InstTestRunner.baseScreenShotFilePath;
//	}
//}
