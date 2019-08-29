package cc.wulian.smarthomev6.proc;


import cc.wulian.smarthomev6.app.ControlInfo;
import cc.wulian.smarthomev6.model.BaseMyHousekeeperModel;
import cc.wulian.smarthomev6.utils.EnterUtils;
import cc.wulian.smarthomev6.utils.MessageUtils;
import com.wtt.frame.robotium.By;
import com.wtt.frame.robotium.Solo;
import com.wtt.frame.robotium.WebElement;

/**
 * Created by 赵永健 on 2017/6/13.
 *
 * 封装我的管家的基本操作
 */
abstract class BaseMyHousekeeperProc<T extends BaseMyHousekeeperModel> extends BaseProc<T> {
	
	public BaseMyHousekeeperProc(Solo solo) {
		super(solo);
	}
	
	@Override
	public abstract void process(T model, int action);
	
	@Override
	public abstract void init();
	
	/**
	 * 点击我的管家页面中“返回”按钮
	 */
	public void clickBack() {
		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_back));
	}
	
	/**
	 * 获取我的管家页面的标题
	 *
	 * @return - 标题
	 */
	public String getTitle() {
		return getter.getWebElementText(ControlInfo.web_my_manager_title);
	}
	
	/**
	 * 获取我的管家页面无任务时的展示内容
	 *
	 * @return - 无任务时的展示内容
	 */
	public String getNoTaskTips() {
		return getter.getWebElementText(ControlInfo.web_my_manager_title);
	}
	
	/**
	 * 点击我的管家页面中“添加任务”按钮
	 */
	public void clickAddTask() {
//		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_more));
		solo.clickOnView(getter.getView(ControlInfo.all_scene_image_add));
	}
	
	/**
	 * 点击我的管家页面中“添加计时任务”按钮
	 */
	public void clickTimingTask() {
//		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_timing_task));
		solo.clickOnView(getter.getView(ControlInfo.ll_auto_task_timer));
	}
	
	/**
	 * 点击我的管家页面中“添加情景任务”按钮
	 */
	public void clickSceneTask() {
//		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_scene_task));
		solo.clickOnView(getter.getView(ControlInfo.ll_auto_task_scene));
	}
	
	/**
	 * 点击我的管家页面中添加任务Dialog的“关闭”按钮
	 */
	public void clickCloseCreateTask() {
		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_close_task_div));
	}
	
	/**
	 * 点击我的管家页面中任务列表的某个条目
	 *
	 * @param taskName - 任务名称
	 */
	private void clickTask(String taskName) {
		WebElement webElement = getter.findElementByText(taskName, ControlInfo.web_my_manager_task_name_list);
		if (null == webElement) {
			MessageUtils.append(Msg.GetTaskFailed + taskName + "' 的任务！");
			return;
		}
		solo.clickOnWebElement(webElement);
	}
	
	/**
	 * 点击我的管家页面中任务列表条目的执行按钮
	 *
	 * @param taskName - 任务名称
	 */
	private void clickExecuteSwitchOfTaskItem(String taskName) {
		clickOnTaskItem(
				ControlInfo.web_my_manager_task_switch_list
				, taskName
				, ControlInfo.web_my_manager_task_name_list);
	}
	
	/**
	 * 点击我的管家页面中任务列表条目的某个控件
	 *
	 * @param id            - 控件id
	 * @param taskName      - 任务名称
	 * @param taskNameXpath - 任务名称的xpath
	 */
	private void clickOnTaskItem(String id, String taskName, String taskNameXpath) {
		String switchXpath = getter.getXpath(id, taskName, taskNameXpath);
		if (null == switchXpath) return;
		solo.clickOnWebElement(By.xpath(switchXpath));
	}
	
	/**
	 * 获取我的管家页面中任务列表中定时任务的执行时间
	 *
	 * @param taskName - 任务名称
	 * @return         - 执行时间
	 */
	public String getTaskTime(String taskName) {
		return getter.getWebElementText(
				ControlInfo.web_my_manager_task_time_list
				, taskName
				, ControlInfo.web_my_manager_task_name_list);
	}
	
	/**
	 * 获取我的管家页面中任务列表中定时任务的重复日期
	 *
	 * @param taskName - 任务名称
	 * @return         - 重复日期
	 */
	public String getTaskDate(String taskName) {
		return getter.getWebElementText(
				ControlInfo.web_my_manager_task_date_list
				, taskName
				, ControlInfo.web_my_manager_task_name_list);
	}
	
	/**
	 * 点击Dialog中的按钮
	 *
	 * @param text - 按钮文案
	 */
	public void clickInDialog(String text) {
		solo.clickOnWebElement(By.textContent(text));
	}
	
	/**
	 * 点击定时任务页面的返回按钮
	 */
	public void clickBackInTimingTask() {
		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_timing_task_back));
	}
	
	/**
	 * 获取定时任务页面的标题
	 *
	 * @return - 标题
	 */
	public String getTitleInTimingTask() {
		return getter.getWebElementText(ControlInfo.web_my_manager_timing_task_title);
	}
	
	/**
	 * 点击定时任务页面的保存按钮
	 */
	public void clickSaveInTimingTask() {
		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_timing_task_save));
	}
	
	/**
	 * 获取定时任务页面的任务名称
	 *
	 * @return - 任务名称
	 */
	public String getNameOfTimingTask() {
		return getter.getWebElementText(ControlInfo.web_my_manager_timing_task_name);
	}
	
	/**
	 * 打开定时任务页面的重命名对话框
	 */
	public void openRenameDialogInTimingTask() {
		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_timing_enter_task_name_dialog));
	}
	
	/**
	 * 加载重命名Dialog
	 *
	 * @return - 加载成功返回{@code true}，否则返回{@code false}
	 */
	public boolean waitForRenameDialog() {
		return solo.waitForWebElement(By.textContent("重命名"), 0, 2000, false);
	}
	
	/**
	 * 在定时任务页面的重命名对话框中输入任务名称
	 *
	 * @param enterName - 任务名称
	 * @return          - 输入成功返回{@code true}，否则返回{@code false}
	 */
	public void enterNameOfTimingTask(String enterName) {
		enter.enterTextAsWeb(ControlInfo.web_my_manager_timing_task_enter_name, enterName);
	}
	
	/**
	 * 在定时任务页面的重命名对话框中输入任务名称
	 *
	 * @param enterName - 任务名称
	 * @param checkName - 任务名称校验内容
	 * @return          - 输入成功返回{@code true}，否则返回{@code false}
	 */
	public void enterNameOfTimingTask(String enterName, String checkName) {
		 enter.enterTextAsWeb(ControlInfo.web_my_manager_timing_task_enter_name, enterName);
	}
	
	/**
	 * 获取定时任务页面中重命名对话框的标题
	 *
	 * @return - 标题
	 */
	public String getRenameDialogTitleInTimingTask() {
		return getter.getWebElementText(ControlInfo.web_my_manager_timing_task_enter_name_dialog_title);
	}
	
	/**
	 * 点击定时任务页面中“任务时间”区域
	 */
	public void clickSelectTimeInTimingTask() {
		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_timing_task_select_time));
	}
	
	/**
	 * 获取定时任务页面中的任务执行时间
	 *
	 * @return - 任务执行时间
	 */
	public String getTimeOfTimingTask() {
		return getter.getWebElementText(ControlInfo.web_my_manager_timing_task_time);
	}
	
	/**
	 * 获取定时任务页面中的任务重复日期
	 *
	 * @return - 任务重复日期
	 */
	public String getDateOfTimingTask() {
		return getter.getWebElementText(ControlInfo.web_my_manager_timing_task_date);
	}
	
	/**
	 * 点击定时任务页面中添加任务
	 */
	public void clickAddTaskInTimingTask() {
		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_timing_task_add));
	}
	
	/**
	 * 点击情景任务页面的返回按钮
	 */
	public void clickBackInSceneTask() {
		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_scene_task_back));
	}
	
	/**
	 * 获取情景任务页面的标题
	 *
	 * @return - 标题
	 */
	public String getTitleInSceneTask() {
		return getter.getWebElementText(ControlInfo.web_my_manager_scene_task_title);
	}
	
	/**
	 * 点击情景任务页面的保存按钮
	 */
	public void clickSaveInSceneTask() {
		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_scene_task_save));
	}
	
	/**
	 * 获取情景任务页面的任务名称
	 *
	 * @return - 任务名称
	 */
	public String getNameOfSceneTask() {
		return getter.getWebElementText(ControlInfo.web_my_manager_scene_task_name);
	}
	
	/**
	 * 打开情景任务页面的重命名对话框
	 */
	public void openRenameDialogInSceneTask() {
		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_scene_task_enter_name_dialog));
	}
	
	/**
	 * 在情景任务页面的重命名对话框中输入任务名称
	 *
	 * @param enterName - 任务名称
	 * @return          - 输入成功返回{@code true}，否则返回{@code false}
	 */
	public void enterNameOfSceneTask(String enterName) {
		 enter.enterTextAsWeb(ControlInfo.web_my_manager_scene_task_enter_name, enterName);
	}
	
	/**
	 * 在情景任务页面的重命名对话框中输入任务名称
	 *
	 * @param enterName - 任务名称
	 * @param checkName - 任务名称校验内容
	 * @return          - 输入成功返回{@code true}，否则返回{@code false}
	 */
	public void enterNameOfSceneTask(String enterName, String checkName) {
		 enter.enterTextAsWeb(ControlInfo.web_my_manager_scene_task_enter_name, enterName);
	}
	
	/**
	 * 在情景任务页面中点击生效时段
	 */
	public void clickSetTime() {
		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_scene_task_set_time));
	}
	
	/**
	 * 点击情景任务页面中添加条件
	 */
	public void clickAddConditionInSceneTask() {
		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_scene_task_add_condition));
	}
	
	/**
	 * 点击传感器
	 *
	 * @param sensorName - 传感器名称
	 */
	public void clickSensor(String sensorName) {
		solo.clickOnWebElement(By.textContent(sensorName));
	}
	
	/**
	 * 点击情景任务页面中添加任务
	 */
	public void clickAddTaskInSceneTask() {
		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_scene_task_add_task));
	}
	
	/**
	 * 点击“添加要执行的设备”
	 */
	public void clickExecuteDevice() {
		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_select_task_device));
	}
	
	/**
	 * 点击设备
	 *
	 * @param deviceName - 设备名
	 */
	public void clickDevice(String deviceName) {
		solo.clickOnWebElement(By.textContent(deviceName));
	}
	
	/**
	 * 点击“添加要执行的场景”
	 */
	public void clickExecuteScene() {
		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_select_task_scene));
	}
	
	/**
	 * 点击“星期天”
	 */
	public void clickSun() {
		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_select_task_sun));
	}
	
	/**
	 * 点击“星期一”
	 */
	public void clickMon() {
		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_select_task_mon));
	}
	
	/**
	 * 点击“星期二”
	 */
	public void clickTue() {
		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_select_task_tue));
	}
	
	/**
	 * 点击“星期三”
	 */
	public void clickWed() {
		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_select_task_wed));
	}
	
	/**
	 * 点击“星期四”
	 */
	public void clickThu() {
		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_select_task_thu));
	}
	
	/**
	 * 点击“星期五”
	 */
	public void clickFir() {
		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_select_task_fir));
	}
	
	/**
	 * 点击“星期六”
	 */
	public void clickSat() {
		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_select_task_sat));
	}
	
	/**
	 * 设置时间，时
	 *
	 * @param h - 时
	 */
	public void selectHour(int h) {
		scroll.scroll("set_condition_h", 0, h);
	}
	
	/**
	 * 设置时间，分
	 *
	 * @param m - 分
	 */
	public void selectMinute(int m) {
		scroll.scroll("set_condition_m", 1, m);
	}
	
	/**
	 * 点击添加场景
	 *
	 * @param sceneName - 场景
	 */
	public void clickScene(String sceneName) {
		solo.clickOnWebElement(By.textContent(sceneName));
	}
	
	/**
	 * 向左滑动任务列表的条目信息
	 */
	public void scrollTaskItem(String taskName, int coefficient) {
		int defaultCoefficient = 2;
		if (0 < coefficient) defaultCoefficient = coefficient;
		WebElement el = solo.getWebElement(By.textContent(taskName), 0);
		solo.drag(el.getLocationX()*defaultCoefficient, el.getLocationX(), el.getLocationY()
				, el.getLocationY(), 50);
	}
	
	/**
	 * 在我的管家页面中点击任务列表条目的编辑按钮
	 *
	 * @param taskName - 任务名称
	 */
	public void clickEditTask(String taskName) {
		clickOnTaskItem(
				ControlInfo.web_my_manager_task_edit_list
				, taskName
				, ControlInfo.web_my_manager_task_name_list);
	}
	
	/**
	 * 在我的管家页面中点击任务列表条目的删除按钮
	 *
	 * @param taskName - 任务名称
	 */
	public void openDeleteTaskDialog(String taskName) {
		clickOnTaskItem(
				ControlInfo.web_my_manager_task_delete_list
				, taskName
				, ControlInfo.web_my_manager_task_name_list);
	}
	
	/**
	 * 在定时页面中选择多个重复日期
	 *
	 * @param actions - 多个重复日期
	 */
	public void selectRepeatDate(int[] actions) {
		for (Integer action : actions) {
			selectRepeatDate(action);
		}
	}
	
	/**
	 * 在定时页面中选择重复日期
	 *
	 * @param date - 重复日期
	 */
	public void selectRepeatDate(int date) {
		switch (date) {
			case Date.DATE_SUN:
				clickSun();
				break;
			case Date.DATE_MON:
				clickMon();
				break;
			case Date.DATE_TUE:
				clickTue();
				break;
			case Date.DATE_WED:
				clickWed();
				break;
			case Date.DATE_THU:
				clickThu();
				break;
			case Date.DATE_FIR:
				clickFir();
				break;
			case Date.DATE_SAT:
				clickSat();
				break;
			default:
				MessageUtils.append(Msg.DateKeyError);
		}
	}
	/**
	 *
	 * @param t 温湿度传感器 温度
	 */
	public void setTemperature(int t){
		scroll.scroll1("set_temperature",0,t);
	}

	/**
	 *
	 * @param h 温湿度传感器 湿度
	 */
	public void setHumidity(int h){
		scroll.scroll2("set_humidity",0,h);
	}

	/**
	 * 生效时段中点击“确定” 选择温度确定
	 */
	public void clickConfirmInSetTime() {
		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_set_time_save));
	}
	
	/**
	 * 生效时段中点击“从”
	 */
	public void clickSetTimeStart() {
		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_set_time_start));
	}
	
	/**
	 * 生效时段中点击“至”
	 */
	public void clickSetTimeEnd() {
		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_set_time_end));
	}
	
	/**
	 * 生效时段中设置时
	 *
	 * @param h - 时
	 */
	public void setHourInSetTime(int h) {
//		scroll.scroll(0, ScrollUtils.TYPE_SET_TIME, h);
		scroll.scroll("set_time_h", 0, h);
	}
	
	/**
	 * 生效时段中设置分
	 *
	 * @param m - 分
	 */
	public void setMinuteInSetTime(int m) {
//		scroll.scroll(1, ScrollUtils.TYPE_SET_TIME, m);
		scroll.scroll("set_time_m", 1, m);
	}
	
	/**
	 * 在生效时段页面中选择多个重复日期
	 *
	 * @param actions - 多个重复日期
	 */
	public void selectRepeatDateInSetTime(int[] actions) {
		for (Integer action : actions) {
			selectRepeatDateInSetTime(action);
		}
	}
	
	/**
	 * 在生效时段页面中选择重复日期
	 *
	 * @param date - 重复日期
	 */
	public void selectRepeatDateInSetTime(int date) {
		switch (date) {
			case Date.DATE_SUN:
				clickSunInSetTime();
				break;
			case Date.DATE_MON:
				clickMonInSetTime();
				break;
			case Date.DATE_TUE:
				clickTueInSetTime();
				break;
			case Date.DATE_WED:
				clickWedInSetTime();
				break;
			case Date.DATE_THU:
				clickThuInSetTime();
				break;
			case Date.DATE_FIR:
				clickFirInSetTime();
				break;
			case Date.DATE_SAT:
				clickSatInSetTime();
				break;
			default:
				MessageUtils.append(Msg.DateKeyError);
		}
	}
	
	/**
	 * 生效时段中点击“星期天”
	 */
	public void clickSunInSetTime() {
		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_set_time_sun));
	}
	
	/**
	 * 生效时段中点击“星期一”
	 */
	public void clickMonInSetTime() {
		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_set_time_mon));
	}
	
	/**
	 * 生效时段中点击“星期二”
	 */
	public void clickTueInSetTime() {
		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_set_time_tue));
	}
	
	/**
	 * 生效时段中点击“星期三”
	 */
	public void clickWedInSetTime() {
		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_set_time_wed));
	}
	
	/**
	 * 生效时段中点击“星期四”
	 */
	public void clickThuInSetTime() {
		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_set_time_thu));
	}
	
	/**
	 * 生效时段中点击“星期五”
	 */
	public void clickFirInSetTime() {
		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_set_time_fir));
	}
	
	/**
	 * 生效时段中点击“星期六”
	 */
	public void clickSatInSetTime() {
		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_set_time_sat));
	}
	
	/**
	 * 选择场景页面点击“完成”
	 */
	public void clickFinishInSelectScene() {
		solo.clickOnWebElement(By.textContent("完成"));
	}
	
	/**
	 * 封装重复日期
	 */
	public static final class Date {
		/**
		 * 周日
		 */
		public static final int DATE_SUN = 0;
		
		/**
		 * 周一
		 */
		public static final int DATE_MON = 1;
		
		/**
		 * 周二
		 */
		public static final int DATE_TUE = 2;
		
		/**
		 * 周三
		 */
		public static final int DATE_WED = 3;
		
		/**
		 * 周四
		 */
		public static final int DATE_THU = 4;
		
		/**
		 * 周五
		 */
		public static final int DATE_FIR = 5;
		
		/**
		 * 周六
		 */
		public static final int DATE_SAT = 6;
	}

	private static final class Msg {
		public static final String GetTaskFailed = "任务列表中未找到任务名称为 '";
		public static final String DateKeyError = "选择的重复时段错误！";
	}
}
