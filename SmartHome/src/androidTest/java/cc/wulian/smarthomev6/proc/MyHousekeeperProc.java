package cc.wulian.smarthomev6.proc;

import cc.wulian.smarthomev6.app.AccountInfo;
import cc.wulian.smarthomev6.app.Common;
import cc.wulian.smarthomev6.app.ControlInfo;
import cc.wulian.smarthomev6.model.MyHousekeeperModel;
import cc.wulian.smarthomev6.model.TimeModel;
import cc.wulian.smarthomev6.utils.MessageUtils;
import cc.wulian.smarthomev6.utils.RandomUtils;
import com.wtt.frame.robotium.By;
import com.wtt.frame.robotium.Solo;

/**
 * Created by 赵永健 on 2017/6/14.定时任务中，7,12，未限制字符数15个，所以是错的
 *
 * 封装我的管家的测试流程
 */
public class MyHousekeeperProc extends BaseMyHousekeeperProc<MyHousekeeperModel> {
	
	private String deviceName = "门磁";
	private int deviceState = Common.DeviceState.KEY_STATE1;
	private String sceneName = "回家";
	private String sensorName = "红外入侵";
	private int sensorState = Common.DeviceState.KEY_LEAKAGE;

	/**
	 * 公共接口
	 * @param model
	 */
	public void commonProcess(MyHousekeeperModel model){
		baseProcess(model);
	}

	public MyHousekeeperProc(Solo solo) {
		super(solo);
	}
	
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	
	public void setSceneName(String sceneName) {
		this.sceneName = sceneName;
	}
	
	public void setDeviceState(int deviceState) {
		this.deviceState = deviceState;
	}
	
	public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}
	
	public void setSensorState(int sensorState) {
		this.sensorState = sensorState;
	}
	
	/**
	 * 创建定时管家任务（默认时间每天9:00），名称小于20个字符
	 */
	public void createTimingTask_01() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 1, 4, 	//进入定时任务页面
						6, 12, 7, 13, 	//重命名
						22, 24, 25, 28, 29, 31, 32, 	//设置设备状态
						39, 4, 	//设置设备延时
						52, 	//定时任务页面查询
						47, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//7
		model.setRenameButton("确定");	//13
		model.setDeviceName(deviceName);	//28
		model.setDeviceStateInTask(deviceState);	//31
		
		baseProcess(model);
	}
	
	/**
	 * 创建定时管家任务(默认星期全选)，名称小于20个字符
	 */
	public void createTimingTask_02() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 1, 4, 	//进入定时任务页面
						6, 12, 7, 13, 	//重命名
						14, 15, 47, 4, 	//设置执行时间
						22, 24, 25, 28, 29, 31, 32, 	//设置设备状态
						39, 4, 	//设置设备延时
						52, 	//定时任务页面查询
						47, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//7
		model.setRenameButton("确定");	//13
		model.setDeviceName(deviceName);	//28
		model.setDeviceStateInTask(deviceState);	//31
		
		baseProcess(model);
	}
	
	/**
	 * 创建定时管家任务（默认星期全选，搜索设备）名称小于20个字符
	 */
	public void createTimingTask_03() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 1, 4, 	//进入定时任务页面
						6, 12, 7, 13, 	//重命名
						14, 15, 47, 4, 	//设置执行时间
						22, 24, 25, 28, 29, 31, 32, 	//设置设备状态
						34, 37, 38, 39, 4, 	//设置设备延时
						52, 	//定时任务页面查询
						47, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//7
		model.setRenameButton("确定");	//13
		model.setDeviceName(deviceName);	//28
		model.setDeviceStateInTask(deviceState);	//31
		model.setDelayTimeInTask(new TimeModel(1, 1));	//37,38
		
		baseProcess(model);
	}
	
	/**
	 * 创建定时管家延时任务(默认星期全选)，名称小于20个字符
	 */
	public void createTimingTask_04() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 1, 4, 	//进入定时任务页面
						6, 12, 7, 13, 	//重命名
						14, 15, 47, 4, 	//设置执行时间
						22, 24, 25, 28, 29, 31, 32, 	//设置设备状态
						34, 37, 38, 39, 4, 	//设置设备延时
						52, 	//定时任务页面查询
						47, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//7
		model.setRenameButton("确定");	//13
		model.setDeviceName(deviceName);	//28
		model.setDeviceStateInTask(deviceState);	//31
		model.setDelayTimeInTask(new TimeModel(1, 1));	//37,38
		
		baseProcess(model);
	}
	
	/**
	 * 创建定时管家延时任务(星期不全选)，名称小于20个字符
	 */
	public void createTimingTask_05() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 1, 4, 	//进入定时任务页面
						6, 12, 7, 13, 	//重命名
						14, 15, 16, 17, 18, 47, 4, 	//设置执行时间
						22, 24, 25, 28, 29, 31, 32, 	//设置设备状态
						34, 37, 38, 39, 4, 	//设置设备延时
						52, 	//定时任务页面查询
						47, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//7
		model.setRenameButton("确定");	//13
		model.setHourInTiming(1);	//16
		model.setMinuteInTiming(1);	//17
		model.setRepeatDate(Date.DATE_SUN);	//18
		model.setDeviceName(deviceName);	//28
		model.setDeviceStateInTask(deviceState);	//31
		model.setDelayTimeInTask(new TimeModel(1, 1));	//37,38
		
		baseProcess(model);
	}
	
	/**
	 * 创建定时管家延时任务（星期未选中）
	 */
	public void createTimingTask_06() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 1, 4, 	//进入定时任务页面
						6, 12, 7, 13, 	//重命名
						14, 15, 56, 47, 	//设置执行时间
						55 	//定时任务页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//7
		model.setRenameButton("确定");	//13
		model.setRepeatDataArray(new int[] {
				Date.DATE_SUN
				, Date.DATE_MON
				, Date.DATE_TUE
				, Date.DATE_WED
				, Date.DATE_THU
				, Date.DATE_FIR
				, Date.DATE_SAT});
		model.setSearchText("请至少选择一天！");	//56
		
		baseProcess(model);
	}
	
	/**
	 * 创建定时管家任务，名称多于15个字符
	 */
	public void createTimingTask_07() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 1, 4, 	//进入定时任务页面
						6, 12, 8, 13, 	//重命名
						14, 15, 47, 4, 	//设置执行时间
						22, 24, 25, 28, 29, 31, 32, 	//设置设备状态
						34, 37, 38, 39, 4, 	//设置设备延时
						52, 	//定时任务页面查询
						47, 46, 	//保存
						54 	//我的管家页面查询
				});
		String random = Integer.toString(RandomUtils.randomFour());
		model.setTaskName(random + "aaaaabbbbbc");	//7
		model.setTaskNameCheck(random + "aaaaabbbbbc");
		model.setRenameButton("确定");	//13
		model.setDeviceName(deviceName);	//28
		model.setDeviceStateInTask(deviceState);	//31
		model.setDelayTimeInTask(new TimeModel(1, 1));	//37,38
		
		baseProcess(model);
	}
	
	/**
	 * 创建定时任务（场景）（默认时间每天9:00），名称小于20个字符
	 */
	public void createTimingTask_08() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 1, 4, 	//进入定时任务页面
						6, 12, 7, 13, 	//重命名
						22, 26, 27, 40, 73, 32, 	//设置场景
						39, 4, 	//设置场景延时
						53, 	//定时任务页面查询
						47, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//7
		model.setRenameButton("确定");	//13
		model.setSceneName(sceneName);	//40
		
		baseProcess(model);
	}
	
	/**
	 * 创建定时任务（场景）（默认星期全选），名称小于20个字符
	 */
	public void createTimeTask_09() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 1, 4, 	//进入定时任务页面
						6, 12, 7, 13, 	//重命名
						14, 15, 47, 4, 	//设置执行时间
						22, 26, 27, 40, 73, 32, 	//设置场景
						39, 4, 	//设置场景延时
						53, 	//定时任务页面查询
						47, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//7
		model.setRenameButton("确定");	//13
		model.setSceneName(sceneName);	//40
		model.setDeviceStateInTask(deviceState);	//31
		
		baseProcess(model);
	}
	
	/**
	 * 创建定时管家延时任务（场景）(默认星期全选)，名称小于20个字符
	 */
	public void createTimeTask_10() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 1, 4, 	//进入定时任务页面
						6, 12, 7, 13, 	//重命名
						14, 15, 47, 4, 	//设置执行时间
						22, 26, 27, 40, 73, 32, 	//设置场景
						34, 37, 38, 39, 4, 	//设置场景延时
						53, 	//定时任务页面查询
						47, 46, 	//保存
						54}); 	//我的管家页面查询
		model.setTaskName("测试" + RandomUtils.randomFour());	//7
		model.setRenameButton("确定");	//13
		model.setSceneName(sceneName);	//40
		model.setDelayTimeInTask(new TimeModel(1, 1));	//37,38
		
		baseProcess(model);
	}
	
	/**
	 * 创建定时管家延时任务（场景）(星期不全选)，名称小于20个字符
	 */
	public void createTimeTask_11() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 1, 4, 	//进入定时任务页面
						6, 12, 7, 13, 	//重命名
						14, 15, 18, 47, 4, 	//设置执行时间
						22, 26, 27, 40, 73, 32, 	//设置场景
						34, 37, 38, 39, 4, 	//设置场景延时
						53, 	///定时任务页面查询
						47, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//7
		model.setRenameButton("确定");	//13
		model.setSceneName(sceneName);	//40
		model.setDelayTimeInTask(new TimeModel(1, 1));	//37,38
		model.setRepeatDataArray(new int[] {Date.DATE_SUN});	//18
		
		baseProcess(model);
	}
	
	/**
	 * 创建定时管家任务，名称多于15个字符
	 */
	public void createTimeTask_12() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 1, 4, 	//进入定时任务页面
						6, 12, 8, 13, 	//重命名
						14, 15, 47, 4, 	//设置执行时间
						22, 26, 27, 40, 73, 32, 	//设置场景
						39, 4, 	//设置场景延时
						53, 	//定时任务页面查询
						47, 46, 	//保存 74注掉
						54 	//我的管家页面查询
				});
		String random = Integer.toString(RandomUtils.randomFour());
		model.setTaskName(random + "aaaaabbbbbc");	//8
		model.setTaskNameCheck(random + "aaaaabbbbbc");	//8
		model.setRenameButton("确定");	//13
		model.setSceneName(sceneName);	//40
		
		baseProcess(model);
	}
	
	/**
	 * 创建定时任务（场景+设备）
	 */
	public void createTimeTask_13() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 1, 4, 	//进入定时任务页面
						6, 12, 7, 13, 	//重命名
						14, 15, 47, 4, 	//设置执行时间
						22, 24, 25, 28, 29, 31, 32, 	//设置设备状态
						39, 4, 	//设置设备延时
						22, 26, 27, 40, 73, 32, 	//设置场景
						39, 4, 	//设置场景延时
						52, 53, 	//定时任务页面查询
						47, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + Integer.toString(RandomUtils.randomFour()));	//7
		model.setRenameButton("确定");	//13
		model.setSceneName(sceneName);	//40
		model.setDeviceName(deviceName);	//28
		model.setDeviceStateInTask(deviceState); 	//31
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（默认名称，一个触发条件，默认生效时间，非延时任务（设备））
	 */
	public void createSceneTask_01() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						19, 20, 21, 29, 30, 5, 	//设置条件
						23, 24, 25, 28, 29, 31, 32, 	//设置设置状态
						39, 5 , 	//设置延时
						52, 	//情景任务页面查询
						48, 46, 	//保存
						75 	//我的管家页面查询
				});
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setDeviceName(deviceName);	//28
		model.setDeviceStateInTask(deviceState);	//31
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（一个触发条件，默认生效时间，非延时任务（设备））
	 */
	public void createSceneTask_02() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 	//设置条件
						23, 24, 25, 28, 29, 31, 32, 	//设置设备状态
						39, 5 , 	//设置延迟
						51, 52, 	//情景任务页面查询
						48, 46, 	//保存
						54 		//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setDeviceName(deviceName);	//28
		model.setDeviceStateInTask(deviceState);	//31
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（一个触发条件，默认生效时间，非延时任务（设备））
	 */
	public void createSceneTask_03() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5,	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 	//设置条件
						23, 24, 25, 28, 29, 31, 32, 	//设置设备状态
						39, 5 , 	//设置设备延迟
						51, 52, 	//情景任务页面查询
						48, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setDeviceName(deviceName);	//28
		model.setDeviceStateInTask(deviceState);	//31
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（一个触发条件，默认生效时间，非延时任务（设备））
	 */
	public void createSceneTask_04() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 	//设置条件
						23, 24, 25, 28, 29, 31, 32, 	//设置设备状态
						39, 5 , 	//设置设备延迟
						45, 57, 58, 5, 	//继续编辑
						51, 52 	//情景任务页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setDeviceName(deviceName);	//28
		model.setDeviceStateInTask(deviceState);	//31
		model.setCancelEditButton("取消");	//58
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（一个触发条件，默认生效时间，非延时任务（设备））
	 */
	public void createSceneTask_05() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 	//设置条件
						23, 24, 25, 28, 29, 31, 32, 	//设置设备状态
						39, 5 , 	//设置设备延迟
						45, 57, 58, 46, 	//取消编辑
						72 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setDeviceName(deviceName);	//28
		model.setDeviceStateInTask(deviceState);	//31
		model.setCancelEditButton("确定");	//58
		model.setSearchText("测试" + RandomUtils.randomFour()); 	//72
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（一个触发条件，非默认生效时间，非延时任务（设备））
	 */
	public void createSceneTask_06() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 	//设置条件
						59, 60, 61, 65, 66, 62, 67, 68, 69,76, 5,	//设置生效时段
						23, 24, 25, 28, 29, 31, 32, 	//设置设备状态
						39, 5 , 	//设置设备延时
						51, 52, 	//情景任务页面查询
						48, 46, 	//保存
						54	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setStartTimeInSetTime(new TimeModel(1, 1));	//65,66
		model.setEndTimeInSetTime(new TimeModel(2, 2));	//67,68
		model.setDeviceName(deviceName);	//28
		model.setDeviceStateInTask(deviceState);	//31
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（一个触发条件，非默认生效时间，非延时任务（设备））
	 */
	public void createSceneTask_07() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 	//设置条件
						59, 60, 61, 65, 66, 62, 67, 68, 69, 76, 5, 	//设置生效时段
						23, 24, 25, 28, 29, 31, 32, 	//设置设备状态
						39, 5 , 	//设置设备延时
						51, 52, 	//情景任务页面查询
						48, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setStartTimeInSetTime(new TimeModel(1, 1));	//65,66
		model.setEndTimeInSetTime(new TimeModel(2, 2));	//67,68
		model.setRepeatDateInSetTime(Date.DATE_MON);	//69
		model.setDeviceName(deviceName);	//28
		model.setDeviceStateInTask(deviceState);	//31
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（一个触发条件，非默认生效时间，非延时任务（设备））
	 */
	public void createSceneTask_08() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5,	//进入情景任务页面
						9, 12, 10, 13,	//重命名
						19, 20, 21, 29, 30, 5,	//设置条件
						59, 60, 61, 65, 66, 62, 67, 68, 76, //设置生效时段
						55 	//查询提示信息
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setStartTimeInSetTime(new TimeModel(1, 1));	//65,66
		model.setEndTimeInSetTime(new TimeModel(2, 2));	//67,68
		model.setRepeatDataArrayInSetTime(new int[] {
				Date.DATE_SUN
				, Date.DATE_MON
				, Date.DATE_TUE
				, Date.DATE_WED
				, Date.DATE_THU
				, Date.DATE_FIR
				, Date.DATE_SAT});	//70
		model.setSearchText("请至少选择一天！");	//55
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（一个触发条件，默认生效时间，延时任务（设备））
	 */
	public void createSceneTask_09() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 	//设置条件
						59, 60, 69,76, 5, 	//设置生效时段
						23, 24, 25, 28, 29, 31, 32, 	//设置设备状态
						34, 37, 38, 39, 5 , 	//设置设备延时
						51, 52, 	//情景任务页面查询
						48, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setDeviceName(deviceName);	//28
		model.setDeviceStateInTask(deviceState);	//31
		model.setDelayTimeInTask(new TimeModel(1, 1));	//37,38
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（一个触发条件，默认生效时间，延时任务（设备））
	 */
	public void createSceneTask_10() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 	//设置条件
						59, 60, 69,76, 5, 	//设置生效时段
						23, 24, 25, 28, 29, 31, 32, 	//设置设备状态
						34, 37, 38, 71, 39, 5 , 	//设置设备延时
						51, 52, 	//情景任务页面查询
						48, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setDeviceName(deviceName);	//28
		model.setDeviceStateInTask(deviceState);	//31
		model.setDelayTimeInTask(new TimeModel(1, 1));	//37,38
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（一个触发条件，非默认生效时间，延时任务（设备））
	 */
	public void createSceneTask_11() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 	//设置条件
						59, 60, 61, 65, 66, 62, 67, 68, 69,76, 5, 	//设置生效时段
						23, 24, 25, 28, 29, 31, 32, 	//设置设备状态
						34, 37, 38, 39, 5 , 	//设置设备延时
						51, 52, 	//情景任务页面查询
						48, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setStartTimeInSetTime(new TimeModel(1, 1));	//65,66
		model.setEndTimeInSetTime(new TimeModel(2, 2));	//67,68
		model.setDeviceName(deviceName);	//28
		model.setDeviceStateInTask(deviceState);	//31
		model.setDelayTimeInTask(new TimeModel(1, 1));	//37,38
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（一个触发条件，非默认生效时间，延时任务（设备））
	 */
	public void createSceneTask_12() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 	//设置条件
						59, 60, 61, 65, 66, 62, 67, 68, 69, 76, 5, 	//设置生效时段
						23, 24, 25, 28, 29, 31, 32, 	//设置设备状态
						34, 37, 38, 39, 5 , 	//设置设备延时
						51, 52, 	//情景任务页面查询
						48, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setStartTimeInSetTime(new TimeModel(1, 1));	//65,66
		model.setEndTimeInSetTime(new TimeModel(2, 2));	//67,68
		model.setRepeatDateInSetTime(Date.DATE_MON);	//69
		model.setDeviceName(deviceName);	//28
		model.setDeviceStateInTask(deviceState);	//31
		model.setDelayTimeInTask(new TimeModel(1, 1));	//37,38
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（一个触发条件，非默认生效时间，延时任务（设备））
	 */
	public void createSceneTask_13() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 	//设置条件
						59, 60, 61, 65, 66, 62, 67, 68, 69, 76, 5, 	//设置生效时段
						23, 24, 25, 28, 29, 31, 32, 	//设置设备状态
						34, 37, 38, 71, 39, 5 , 	//设置设备延时
						51, 52, 	//情景任务页面查询
						48, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setStartTimeInSetTime(new TimeModel(1, 1));	//65,66
		model.setEndTimeInSetTime(new TimeModel(2, 2));	//67,68
		model.setRepeatDateInSetTime(Date.DATE_MON);	//69
		model.setDeviceName(deviceName);	//28
		model.setDeviceStateInTask(deviceState);	//31
		model.setDelayTimeInTask(new TimeModel(1, 1));	//37,38
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（多个触发条件，默认生效时间，非延时任务（设备））
	 */
	public void createSceneTask_14() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 19, 20, 21, 29, 30, 5,//设置条件
						23, 24, 25, 28, 29, 31,	32, //设置设备状态
						39, 5 ,	//设置设备延时
						51, 52, 	//情景任务页面查询
						48, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setDeviceName(deviceName);	//28
		model.setDeviceStateInTask(deviceState);	//31
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（多个触发条件，默认生效时间，非延时任务（设备））
	 */
	public void createSceneTask_15() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 19, 20, 21, 29, 30, 5,//设置条件
						23, 24, 25, 28, 29, 31,	32, //设置设备状态
						39, 5 ,	//设置设备延时
						51, 52, 	//情景任务页面查询
						48, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setDeviceName(deviceName);	//28
		model.setDeviceStateInTask(deviceState);	//31
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（多个触发条件，非默认生效时间，非延时任务（设备））
	 */
	public void createSceneTask_16() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 19, 20, 21, 29, 30, 5, 	//设置条件
						59, 60, 61, 65, 66, 62, 67, 68,69, 76, 5, 	//设置生效时段
						23, 24, 25, 28, 29, 31,	32, 	//设置设备状态
						39, 5 , 	//设置设备延时
						51, 52, 	//情景任务页面查询
						48, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setStartTimeInSetTime(new TimeModel(1, 1));	//65,66
		model.setEndTimeInSetTime(new TimeModel(2, 2));	//67,68
		model.setDeviceName(deviceName);	//28
		model.setDeviceStateInTask(deviceState);	//31
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（多个触发条件，非默认生效时间，非延时任务（设备））
	 */
	public void createSceneTask_17() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 19, 20, 21, 29, 30, 5, 	//设置条件
						59, 60, 69, 76, 5, 	//设置生效时段
						23, 24, 25, 28, 29, 31,	32, 	//设置设备状态
						39, 5 , 	//设置设备延时
						51, 52, 	//情景任务页面查询
						48, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setRepeatDateInSetTime(Date.DATE_MON);	//69
		model.setDeviceName(deviceName);	//28
		model.setDeviceStateInTask(deviceState);	//31
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（多个触发条件，非默认生效时间，非延时任务（设备））
	 */
	public void createSceneTask_18() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 19, 20, 21, 29, 30, 5,//设置条件
						23, 24, 25, 28, 29, 31,	32, 	//设置设备状态
						39, 5, 	//设置设备延迟
						51, 52, 	//情景任务页面查询
						59, 60, 76, 55 	//查询提示信息
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setRepeatDataArrayInSetTime(new int[] {
				Date.DATE_SUN
				, Date.DATE_MON
				, Date.DATE_TUE
				, Date.DATE_WED
				, Date.DATE_THU
				, Date.DATE_FIR
				, Date.DATE_SAT});	//70
		model.setDeviceName(deviceName);	//28
		model.setDeviceStateInTask(deviceState);	//31
		model.setSearchText("请至少选择一天");	//55
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（多个触发条件，默认生效时间，延时任务（设备））
	 */
	public void createSceneTask_19() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 19, 20, 21, 29, 30, 5, 	//设置条件
						23, 24, 25, 28, 29, 31,	32, 	//设置设备状态
						34, 37, 38, 39, 5 , 	//设置设备延时
						51, 52, 	//情景任务页面查询
						48, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setDelayTimeInTask(new TimeModel(1, 1));	//37,38
		model.setDeviceName(deviceName);	//28
		model.setDeviceStateInTask(deviceState);	//31
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（多个触发条件，默认生效时间，延时任务（设备））
	 */
	public void createSceneTask_20() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 19, 20, 21, 29, 30, 5, 	//设置条件
						59, 60, 69,76, 5, 	//设置生效时段
						23, 24, 25, 28, 29, 31,	32, 	//设置设备状态
						34, 37, 38, 71, 39, 5 , 	//设置设备延时
						51, 52, 	//情景任务页面查询
						48, 46, 	//保存
						54,85	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setDelayTimeInTask(new TimeModel(1, 1));	//37,38
		model.setDeviceName(deviceName);	//28
		model.setDeviceStateInTask(deviceState);	//31
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（多个触发条件，非默认生效时间，延时任务（设备））
	 */
	public void createSceneTask_21() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 19, 20, 21, 29, 30, 5, 	//设置条件
						59, 60, 61, 65, 66, 62, 67, 68,69, 76, 5, 	//设置生效时段
						23, 24, 25, 28, 29, 31,	32, 	//设置设备状态
						34, 37, 38, 39, 5 , 	//设置设备延时
						51, 52, 	//情景任务页面查询
						48, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setStartTimeInSetTime(new TimeModel(1, 1));	//65,66
		model.setEndTimeInSetTime(new TimeModel(2, 2));	//67,68
		model.setDeviceName(deviceName);	//28
		model.setDeviceStateInTask(deviceState);	//31
		model.setDelayTimeInTask(new TimeModel(1, 1));	//37,38
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（多个触发条件，非默认生效时间，延时任务（设备））
	 */
	public void createSceneTask_22() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13,	//重命名
						19, 20, 21, 29, 30, 5, 19, 20, 21, 29, 30, 5, 	//设置条件
						59, 60, 61, 65, 66, 62, 67, 68, 69,76, 5, 	//设置生效时段
						23, 24, 25, 28, 29, 31,	32, 	//设置设备状态
						34, 37, 38, 71, 39, 5 , 	//设置设备延时
						51, 52, 	//情景任务页面查询
						48, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setStartTimeInSetTime(new TimeModel(1, 1));	//65,66
		model.setEndTimeInSetTime(new TimeModel(2, 2));	//67,68
		model.setDeviceName(deviceName);	//28
		model.setDeviceStateInTask(deviceState);	//31
		model.setDelayTimeInTask(new TimeModel(1, 1));	//37,38
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（一个触发条件，默认生效时间，非延时任务（场景））
	 */
	public void createSceneTask_23() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 	//设置条件
						23, 26, 27, 40, 73, 32,	//设置场景
						39, 5 , 	//设置场景延迟
						51, 53, 	//情景任务页面查询
						48, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setSceneName(sceneName);	//40
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（一个触发条件，默认生效时间，非延时任务（场景））
	 */
	public void createSceneTask_24() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 	//设置条件
						23, 26, 27, 40, 73, 32, 	//设置场景
						39, 5 , 	//设置场景延时
						45, 57, 58, 	//继续编辑
						51, 53 	//保存
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setSceneName(sceneName);	//40
		model.setCancelEditButton("取消");	//58
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（一个触发条件，默认生效时间，非延时任务（场景））
	 */
	public void createSceneTask_25() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 	//设置条件
						23, 26, 27, 40, 73, 32, 	//设置场景
						39, 5 , 	//设置场景延时
						45, 57, 58, 	//放弃编辑
						46, 72//保存
				});
		String name = "测试" + RandomUtils.randomFour();
		model.setTaskName(name);	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setSceneName(sceneName);	//40
		model.setCancelEditButton("确定");	//58
		model.setSearchText(name); 	//72
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（一个触发条件，非默认生效时间，非延时任务（场景））
	 */
	public void createSceneTask_26() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 	//设置条件
						59, 60, 61, 65, 66, 62, 67, 68,69, 76, 5, 	//设置生效时段
						23, 26, 27, 40, 73, 32, 	//设置场景
						39, 5 , 	//设置场景延时
						51, 53, 	//情景任务页面查询
						48, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setStartTimeInSetTime(new TimeModel(1, 1));	//65,66
		model.setEndTimeInSetTime(new TimeModel(2, 2));	//67,68
		model.setSceneName(sceneName);	//40
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（一个触发条件，非默认生效时间，非延时任务（场景））
	 */
	public void createSceneTask_27() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 	//设置条件
						59, 60, 61, 65, 66, 62, 67, 68, 69, 76, 5, 	//设置生效时段
						23, 26, 27, 40, 73, 32, 	//设置场景
						39, 5 , 	//设置场景延时
						51, 53, 	//情景任务页面查询
						48, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setStartTimeInSetTime(new TimeModel(1, 1));	//65,66
		model.setEndTimeInSetTime(new TimeModel(2, 2));	//67,68
		model.setRepeatDateInSetTime(Date.DATE_MON);	//69
		model.setSceneName(sceneName);	//40
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（一个触发条件，非默认生效时间，非延时任务（场景））
	 */
	public void createSceneTask_28() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 	//设置条件
						59, 60, 61, 65, 66, 62, 67, 68, 76, //设置生效时段
						55 	//查询提示信息
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setStartTimeInSetTime(new TimeModel(1, 1));	//65,66
		model.setEndTimeInSetTime(new TimeModel(2, 2));	//67,68
		model.setRepeatDataArrayInSetTime(new int[] {
				Date.DATE_SUN
				, Date.DATE_MON
				, Date.DATE_TUE
				, Date.DATE_WED
				, Date.DATE_THU
				, Date.DATE_FIR
				, Date.DATE_SAT});	//70
		model.setSearchText("请至少选择一天");
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（一个触发条件，默认生效时间，延时任务（场景））
	 */
	public void createSceneTask_29() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 	//设置条件
						23, 26, 27, 40, 73, 32, 	//设置场景
						34, 41, 42, 39, 5 , 	//设置场景延时
						51, 53, 	//情景任务页面查询
						48, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setDelayTimeInTask(new TimeModel(1, 1));	//41,42
		model.setSceneName(sceneName);	//40
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（一个触发条件，默认生效时间，延时任务（场景））
	 */
	public void createSceneTask_30() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 	//设置条件
						59, 60, 69,76, 5,	//设置生效时段
						23, 26, 27, 40, 73, 32, 	//设置场景
						34, 41, 42, 71, 39, 5 , 	//设置场景延时
						51, 53, 	//情景任务页面查询
						48, 46, 	//保存
						54,85 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setDelayTimeInTask(new TimeModel(1, 1));	//41,42
		model.setSceneName(sceneName);	//40
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（一个触发条件，非默认生效时间，延时任务（场景））
	 */
	public void createSceneTask_31() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 	//设置条件
						59, 60, 61, 65, 66, 62, 67, 68,69, 76, 5,	//设置生效时段
						23, 26, 27, 40, 73, 32, 	//设置场景
						34, 41, 42, 39, 5 , 	//设置场景延时
						51, 53, 	//情景任务页面查询
						48, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setStartTimeInSetTime(new TimeModel(1, 1));	//65,66
		model.setEndTimeInSetTime(new TimeModel(2, 2));	//67,68
		model.setDelayTimeInTask(new TimeModel(1, 1));	//41,42
		model.setSceneName(sceneName);	//40
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（一个触发条件，非默认生效时间，延时任务（场景））
	 */
	public void createSceneTask_32() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 	//设置条件
						59, 60, 61, 65, 66, 62, 67, 68, 69, 76, 5,	//设置生效时段
						23, 26, 27, 40, 73, 32, 	//设置场景
						34, 41, 42, 39, 5 , 	//设置场景延时
						51, 53, 	//情景任务页面查询
						48, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setStartTimeInSetTime(new TimeModel(1, 1));	//65,66
		model.setEndTimeInSetTime(new TimeModel(2, 2));	//67,68
		model.setRepeatDateInSetTime(Date.DATE_MON);	//69
		model.setDelayTimeInTask(new TimeModel(1, 1));	//41,42
		model.setSceneName(sceneName);	//40
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（一个触发条件，非默认生效时间，延时任务（场景））
	 */
	public void createSceneTask_33() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 	//设置条件
						59, 60, 61, 65, 66, 62, 67, 68,69, 76, 5,	//设置生效时段
						23, 26, 27, 40, 73, 32, 	//设置场景
						34, 41, 42, 71, 39, 5 , 	//设置场景延时
						51, 53, 	//情景任务页面查询
						48, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setStartTimeInSetTime(new TimeModel(1, 1));	//65,66
		model.setEndTimeInSetTime(new TimeModel(2, 2));	//67,68
		model.setDelayTimeInTask(new TimeModel(1, 1));	//41,42
		model.setSceneName(sceneName);	//40
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（多个触发条件，默认生效时间，非延时任务（场景））
	 */
	public void createSceneTask_34() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 19, 20, 21, 29, 30, 5,	//设置条件
						23, 26, 27, 40, 73, 32, 	//设置场景
						39, 5 , 	//设置场景延时
						51, 53, 	//情景任务页面查询
						48, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setSceneName(sceneName);	//40
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（多个触发条件，默认生效时间，非延时任务（场景））
	 */
	public void createSceneTask_35() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 19, 20, 21, 29, 30, 5,	//设置条件
						23, 26, 27, 40, 73, 32, 	//设置场景
						39, 5 , 	//设置场景延时
						51, 53, 	//情景任务页面查询
						48, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setSceneName(sceneName);	//40
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（多个触发条件，默认生效时间，非延时任务（场景））
	 */
	public void createSceneTask_36() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 19, 20, 21, 29, 30, 5,	//设置条件
						59, 60, 61, 65, 66, 62, 67, 68,69, 76, 5,	//设置生效时段
						23, 26, 27, 40, 73, 32, 	//设置场景
						39, 5 , 	//设置场景延时
						51, 53, 	//情景任务页面查询
						48, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setStartTimeInSetTime(new TimeModel(1, 1));	//65,66
		model.setEndTimeInSetTime(new TimeModel(2, 2));	//67,68
		model.setSceneName(sceneName);	//40
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（多个触发条件，非默认生效时间，非延时任务（场景））
	 */
	public void createSceneTask_37() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 19, 20, 21, 29, 30, 5,	//设置条件
						59, 60, 69, 76, 5,	//设置生效时段
						23, 26, 27, 40, 73, 32, 	//设置场景
						39, 5 , 	//设置场景延时
						51, 53, 	//情景任务页面查询
						48, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setRepeatDateInSetTime(Date.DATE_MON);	//69
		model.setSceneName(sceneName);	//40
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（多个触发条件，非默认生效时间，非延时任务（场景））
	 */
	public void createSceneTask_38() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 19, 20, 21, 29, 30, 5,	//设置条件
						23, 26, 27, 40, 73, 32, 	//设置场景
						39, 5 , 	//设置场景延时
						51, 53, 	//情景任务页面查询
						59, 60, 76, 55	//设置生效时段
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setSceneName(sceneName);	//40
		model.setRepeatDataArrayInSetTime(new int[] {
				Date.DATE_SUN
				, Date.DATE_MON
				, Date.DATE_TUE
				, Date.DATE_WED
				, Date.DATE_THU
				, Date.DATE_FIR
				, Date.DATE_SAT});	//70
		model.setSearchText("请至少选择一天");	//55
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（多个触发条件，默认生效时间，延时任务（场景））
	 */
	public void createSceneTask_39() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 19, 20, 21, 29, 30, 5,	//设置条件
						23, 26, 27, 40, 73, 32, 	//设置场景
						34, 41, 42, 39, 5 , 	//设置场景延时
						51, 53, 	//情景任务页面查询
						48, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setSceneName(sceneName);	//40
		model.setDelayTimeInTask(new TimeModel(1, 1));	//41,42
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（多个触发条件，默认生效时间，延时任务（场景））
	 */
	public void createSceneTask_40() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 19, 20, 21, 29, 30, 5,	//设置条件
						23, 26, 27, 40, 73, 32, 	//设置场景
						34, 41, 42, 71, 39, 5 , 	//设置场景延时
						51, 53, 	//情景任务页面查询
						48, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setSceneName(sceneName);	//40
		model.setDelayTimeInTask(new TimeModel(1, 1));	//41,42
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（多个触发条件，非默认生效时间，延时任务（场景））
	 */
	public void createSceneTask_41() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 19, 20, 21, 29, 30, 5,	//设置条件
						59, 60, 61, 65, 66, 62, 67, 68,69, 76, 5,	//设置生效时段
						23, 26, 27, 40, 73, 32, 	//设置场景
						34, 41, 42, 39, 5 , 	//设置场景延时
						51, 53, 	//情景任务页面查询
						48, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setStartTimeInSetTime(new TimeModel(1, 1));	//65,66
		model.setEndTimeInSetTime(new TimeModel(2, 2));	//67,68
		model.setSceneName(sceneName);	//40
		model.setDelayTimeInTask(new TimeModel(1, 1));	//41,42
		
		baseProcess(model);
	}
	
	/**
	 * 创建情景任务（多个触发条件，非默认生效时间，延时任务（场景））
	 */
	public void createSceneTask_42() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 19, 20, 21, 29, 30, 5,	//设置条件
						59, 60, 61, 65, 66, 62, 67, 68,69, 76, 5,	//设置生效时段
						23, 26, 27, 40, 73, 32, 	//设置场景
						34, 41, 42, 71, 39, 5 , 	//设置场景延时
						51, 53, 	//情景任务页面查询
						48, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setStartTimeInSetTime(new TimeModel(1, 1));	//65,66
		model.setEndTimeInSetTime(new TimeModel(2, 2));	//67,68
		model.setSceneName(sceneName);	//40
		model.setDelayTimeInTask(new TimeModel(1, 1));	//41,42
		
		baseProcess(model);
	}

	/**
	 * 恢复情景任务名称
	 * @param
	 */
	public void recoverTaskName(String ortaskname){
		openRenameDialogInSceneTask();
		enterNameOfSceneTask(ortaskname);
		clickInDialog("确定");
	}

	@Override
	public void process(MyHousekeeperModel model, int action) {
		switch (action) {
			case 0:	//点击“+”
				clickAddTask();
				break;
			case 1:	//点击“定时任务”
				clickTimingTask();
				break;
			case 2:	//点击“情景任务”
				clickSceneTask();
				break;
			case 3:	//点击"X"
				clickCloseCreateTask();
				break;
			case 4:	//等待加载定时任务界面
				if (!commonProc.waitForWebPage("定时任务")) {
					MessageUtils.append(Msg.LoadTimingTaskFailed);
				}
				break;
			case 5:	//等待加载情景任务界面
				if(!solo.waitForActivity("HouseKeeperActivity",3000)){
					MessageUtils.append(Msg.LoadSceneTaskFailed);
				}
				break;
			case 6:	//打开定时任务的重命名Dialog
				openRenameDialogInTimingTask();
				break;
			case 7:	//输入定时任务的任务名
				enterNameOfTimingTask(model.getTaskName());
				break;
			case 8:	//输入定时任务的任务名
				enterNameOfTimingTask(model.getTaskName(), model.getTaskNameCheck());
				break;
			case 9:	//打开情景任务的重命名Dialog
				openRenameDialogInSceneTask();
				break;
			case 10:	//输入情景任务的任务名
				enterNameOfSceneTask(model.getTaskName());
				break;
			case 11:	//输入情景任务的任务名
				enterNameOfSceneTask(model.getTaskName(), model.getTaskNameCheck());
				break;
			case 12:	//等待加载重名名Dialog
				if (!commonProc.waitForWebPage("名称")) {
					MessageUtils.append(Msg.LoadRenameDialogFailed);
				}
				break;
			case 13:	//点击重名对话框中的Dialog
				clickInDialog(model.getRenameButton());
				break;
			case 14:	//点击定时任务的执行时间
				clickSelectTimeInTimingTask();
				break;
			case 15:	//等在加载定时页面
				if (!commonProc.waitForWebPage("定时")) {
					MessageUtils.append(Msg.LoadTimingFailed);
				}
				break;
			case 16:	//定时页面设置时
				selectHour(model.getHourInTiming());
				break;
			case 17:	//定时页面设置分
				selectMinute(model.getMinuteInTiming());
				break;
			case 18:	//设置重复日期
				selectRepeatDate(model.getRepeatDate());
				break;
			case 19:	//情景任务点击添加条件
				clickAddConditionInSceneTask();
				break;
			case 20:	//等待加载选择传感器页面
				if (!commonProc.waitForWebPage("选择设备")) {
					MessageUtils.append(Msg.LoadSelectSensorFailed);
				}
				break;
			case 21:	//点击传感器
				clickSensor(model.getSensorName());
				break;
			case 22:	//定时任务页面点击添加任务
				clickAddTaskInTimingTask();
				break;
			case 23:	//情景任务页面点击添加任务
				clickAddTaskInSceneTask();
				break;
			case 24:	//点击添加要执行的设备
				clickExecuteDevice();
				break;
			case 25:	//等待加载选择设备页面
				if (!commonProc.waitForWebPage("选择设备")) {
					MessageUtils.append(Msg.LoadSelectDeviceFailed);
				}
				break;
			case 26:	//点击添加要执行的场景
				clickExecuteScene();
				break;
			case 27:	//等待加载选择场景页面
				if (!commonProc.waitForWebPage("选择场景")) {
					MessageUtils.append(Msg.LoadSelectSceneFailed);
				}
				break;
			case 28 : 	//选择设备页面点击设备
				clickDevice(model.getDeviceName());
				break;
			case 29:	//等待加载设置设备状态
				if (!commonProc.waitForWebPage("设置设备状态")) {
					MessageUtils.append(Msg.LoadSelectSceneFailed);
				}
				break;
			case 30:	//添加条件中设置设备状态
				if (!commonProc.selectDeviceState(model.getDeviceStateInCondition())) {
					MessageUtils.append(Msg.SetDeviceStateFailedAsCondition);
				}
				break;
			case 31:	//添加任务中设置设备状态
				if (!commonProc.selectDeviceState(model.getDeviceStateInTask())) {
					MessageUtils.append(Msg.SetDeviceStateFailedAsTask);
				}
				break;
			case 32:	//等待加载添加延迟页面
				if (!commonProc.waitForWebPage("添加延时")) {
					MessageUtils.append(Msg.LoadSelectSceneFailed);
				}
				break;
			case 34:	//点击延迟开关
				commonProc.clickSwitchDelayInDelay();
				break;
			case 35:	//添加条件过程中添加延迟页面设置时
				commonProc.setDelayHour(model.getDelayTimeInCondition().getHour());
				break;
			case 36:	//添加条件过程中添加延迟页面设置分
				commonProc.setDelayMinute(model.getDelayTimeInCondition().getMinute());
				break;
			case 37:	//添加任务过程中添加延迟页面设置时
				commonProc.setDelayHour(model.getDelayTimeInTask().getHour());
				break;
			case 38:	//添加任务过程中添加延迟页面设置分
				commonProc.setDelayMinute(model.getDelayTimeInTask().getMinute());
				break;
			case 39:	//添加延迟页面点击完成
				commonProc.clickFinishInDelay();
				break;
			case 40:	//点击场景
				clickScene(model.getSceneName());
				break;
			case 41:	//设置场景延迟时
				commonProc.setDelayHour(model.getDelayTimeInTask().getHour());
				break;
			case 42:	//设置场景延迟分
				commonProc.setDelayMinute(model.getDelayTimeInTask().getMinute());
				break;
			case 43:	//点击通用返回按钮
				commonProc.clickNormalBack();
				break;
			case 44:	//点击定时任务的返回按钮
				clickBackInTimingTask();
				break;
			case 45:	//点击情景任务的返回按钮
				clickBackInSceneTask();
				break;
			case 46:	//等待加载我的管家页面
//				if (!commonProc.waitForWebPage("我的管家")) {
//					MessageUtils.append(Msg.loadMyHousekeeperFailed);
//				}
				if(!solo.waitForText("管家",0,2000)){
					MessageUtils.append(Msg.loadMyHousekeeperFailed);
				}
				break;
			case 47:	//点击计时任务页面的保存按钮
				clickSaveInTimingTask();
				break;
			case 48:	//点击情景任务页面的保存按钮
				clickSaveInSceneTask();
				break;
			case 49:	//定时任务页面查询任务执行时间
				commonProc.searchText(model.getSearchExecuteTime(), 2000);
				break;
			case 50:	//定时任务页面查询重复日期
				commonProc.searchText(model.getSearchRepeatDate(), 2000);
				break;
			case 51:	//情景任务页面查询传感器
				commonProc.searchText(model.getSensorName(), 2000);
				break;
			case 52:	//查询添加的设备
				commonProc.searchText(model.getDeviceName(), 2000);
				break;
			case 53:	//查询添加的场景
				commonProc.searchText(model.getSceneName(), 2000);
				break;
			case 54:	//查询任务
				commonProc.searchTast(model.getTaskName(), 10000);
				break;
			case 55:	//查询提示信息
//				commonProc.searchText(model.getSearchText(), 1000);
                solo.searchText(model.getSearchText());
				break;
			case 56:	//多次操作重复日期
				selectRepeatDate(model.getRepeatDataArray());
				break;
			case 57:	//等待加载放弃编辑Dialog
				if (!commonProc.waitForWebPage("是否放弃编辑")) {
					MessageUtils.append(Msg.LoadCancelEditDialogFailed);
				}
				break;
			case 58:	//操作是否放弃编辑Dialog
				clickInDialog(model.getCancelEditButton());
				break;
			case 59:	//情景任务页面点击生效时段
				clickSetTime();
//				solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_time_factor));
				solo.clickOnText("生效时间条件");
				break;
			case 60:
				if (!commonProc.waitForWebPage("生效时段")) {
					MessageUtils.append(Msg.LoadSetTimeFailed);
				}
				break;
			case 61:	//生效时段页面中点击“从”
				clickSetTimeStart();
				break;
			case 62:	//生效时段页面点击“至”
				clickSetTimeEnd();
				break;
			case 65:	//生效时段页面设置“从”的时
				setHourInSetTime(model.getStartTimeInSetTime().getHour());
				break;
			case 66:	//生效时段页面设置“从”的分
				setMinuteInSetTime(model.getStartTimeInSetTime().getMinute());
				break;
			case 67:	//生效时段页面设置“至”的时
				setHourInSetTime(model.getEndTimeInSetTime().getHour());
				break;
			case 68:	//生效时段页面设置“至”的分
				setMinuteInSetTime(model.getEndTimeInSetTime().getMinute());
				break;
			case 69:	//生效时段页面中设置重复日期
				selectRepeatDateInSetTime(model.getRepeatDateInSetTime());
				break;
			case 70:	//生效时段页面中多次设置重复日期
				selectRepeatDateInSetTime(model.getRepeatDataArrayInSetTime());
				break;
			case 71:	//添加延迟界面再次打开延迟开关
				commonProc.clickSwitchDelayAgainInDelay();
				break;
			case 72: 	//查询到字符串就报错
				commonProc.searchNoneText(model.getSearchText());
				break;
			case 73:	//选择场景页面点击完成
				clickFinishInSelectScene();
				break;
			case 74:	//设置定时任务的默认名称
				model.setTaskName("定时任务");
				break;
			case 75:	//设置情景任务的默认名称
				model.setTaskName("情景任务");
				break;
			case 76:	//生效时段页面点击“确定”
				clickConfirmInSetTime();
				break;
			case 77:
				setTemperature(model.getTemperature());
				break;
			case 78:
				setHumidity(model.getHumidity());
				break;
			case 79:
				if(!commonProc.waitForWebPage("选择温度")){
					MessageUtils.append(Msg.LoadSelectTemperature);
				}
				break;
			case 80:
				if(!commonProc.waitForWebPage("选择湿度")){
					MessageUtils.append(Msg.LoadSelectHumidity);
				}
				break;
			case 81:
				recoverTaskName(model.getOrtaskname());
				break;
			case 82:
				solo.clickOnWebElement(By.textContent("开关1"));
				break;
			case 83:	//查询任务
				commonProc.searchText(model.getTaskNameCheck(), 30000);
				break;
			case 84:
				commonProc.dragProgressBar();//拉动窗帘
				break;
			case 85:
				commonProc.deleteTast();
				break;
		}
	}

	/**
	 * 温湿度测试，高于指定温度
	 */
	public void createSceneTask_110() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30,79,77,76, 5, 	//设置条件
						23, 24, 25, 28, 29, 31, 32, 	//设置设备状态
						39, 5 , 	//设置设备延时
						51, 52, 	//情景任务页面查询
						48, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(Common.DeviceState.KEY_TEMPERATURE_HIGH);	//30
		model.setDeviceName(deviceName);	//28
		model.setDeviceStateInTask(deviceState);	//31
		model.setTemperature(2);

		baseProcess(model);
	}
	/**
	 * 温湿度测试，低于指定温度
	 */
	public void createSceneTask_111() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30,79,77,76, 5, 	//设置条件
						23, 24, 25, 28, 29, 31, 32, 	//设置设备状态
						39, 5 , 	//设置设备延时
						51, 52, 	//情景任务页面查询
						48, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(Common.DeviceState.KEY_TEMPERATURE_LOW);	//30
		model.setDeviceName(deviceName);	//28
		model.setDeviceStateInTask(deviceState);	//31
		model.setTemperature(4);

		baseProcess(model);
	}

	/**
	 * 温湿度测试，高于指定湿度
	 */
	public void createSceneTask_112() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30,80,78,76, 5, 	//设置条件
						23, 24, 25, 28, 29, 31, 32, 	//设置设备状态
						39, 5 , 	//设置设备延时
						51, 52, 	//情景任务页面查询
						48, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(Common.DeviceState.KEY_HUMIDITY_HIGH);	//30
		model.setDeviceName(deviceName);	//28
		model.setDeviceStateInTask(deviceState);	//31
		model.setHumidity(2);

		baseProcess(model);
	}

	/**
	 * 温湿度测试，低于于指定湿度
	 */
	public void createSceneTask_113() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30,80,78,76, 5, 	//设置条件
						23, 24, 25, 28, 29, 31, 32, 	//设置设备状态
						39, 5 , 	//设置设备延时
						51, 52, 	//情景任务页面查询
						48, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(Common.DeviceState.KEY_HUMIDITY_LOW);	//30
		model.setDeviceName(deviceName);	//28
		model.setDeviceStateInTask(deviceState);	//31
		model.setHumidity(4);

		baseProcess(model);
	}

	@Override
	public void init() {
		commonProc.login(AccountInfo.Account1, AccountInfo.Password1);
		commonProc.forceRefresh();
		solo.clickOnView(commonProc.getNavigationChild(3));
		solo.clickOnView(getter.getView(ControlInfo.tv_houseKeeper));
	}


	private static final class Msg {
		public static final String LoadTimingTaskFailed = "加载定时任务页面失败！";
		public static final String LoadSceneTaskFailed = "加载情景任务页面失败！";
		public static final String EnterTimingTaskFailed = "输入定时任务的任务名失败！";
		public static final String EnterSceneTaskFailed = "输入定时任务的任务名失败！";
		public static final String LoadRenameDialogFailed = "加载重命名对话框失败！";
		public static final String LoadTimingFailed = "加载定时页面失败！";
		public static final String LoadSelectSensorFailed = "加载选择传感器页面失败！";
		public static final String LoadSelectDeviceFailed = "加载选择设备页面失败！";
		public static final String LoadSelectSceneFailed = "加载选择场景页面失败！";
		public static final String SetDeviceStateFailedAsCondition = "添加条件过程中，设置设备状态失败！";
		public static final String SetDeviceStateFailedAsTask= "添加任务过程中，设置设备状态失败！";
		public static final String LoadAddDelayFailed = "加载添加延迟页面失败！";
		public static final String loadMyHousekeeperFailed = "加载我的管家失败!";
		public static final String LoadCancelEditDialogFailed = "加载是否放弃编辑对话框失败！";
		public static final String LoadSetTimeFailed = "加载生效时段页面失败！";
		public static final String LoadSelectTemperature = "加载选择温度页面失败！";
		public static final String LoadSelectHumidity = "加载选择湿度页面失败！";
	}

	/**
	 * 内嵌二路开关管家 创建定时管家任务（默认时间每天9:00），名称小于20个字符
	 */
	public void switchCreateTimingTask_01() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 1, 4, 	//进入定时任务页面
						6, 12, 7, 13, 	//重命名
						22, 24, 25, 28, 82,29, 31, 32, 	//设置设备状态
						39, 4, 	//设置设备延时
						52, 	//定时任务页面查询
						47, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//7
		model.setRenameButton("确定");	//13
		model.setDeviceName(deviceName);	//28
		model.setDeviceStateInTask(deviceState);	//31

		baseProcess(model);
	}

	/**
	 * 内嵌二路开关管家 创建情景任务（默认名称，一个触发条件，默认生效时间，非延时任务（设备））
	 */
	public void switchCreateSceneTask_01() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						19, 20, 21, 29, 30, 5, 	//设置条件
						23, 24, 25, 28,82, 29, 31, 32, 	//设置设置状态
						39, 5 , 	//设置延时
						52, 	//情景任务页面查询
						48, 46, 	//保存
						75 	//我的管家页面查询
				});
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setDeviceName(deviceName);	//28
		model.setDeviceStateInTask(deviceState);	//31

		baseProcess(model);
	}

	/**
	 * 内嵌二路开关管家 创建情景任务（一个触发条件，非默认生效时间，延时任务（设备））
	 */
	public void switchCreateSceneTask_11() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						9, 12, 10, 13, 	//重命名
						19, 20, 21, 29, 30, 5, 	//设置条件
						59, 60, 61, 65, 66, 62, 67, 68, 76, 5, 	//设置生效时段
						23, 24, 25, 28,82,29, 31, 32, 	//设置设备状态
						34, 37, 38, 39, 5 , 	//设置设备延时
						51, 52, 	//情景任务页面查询
						48, 46, 	//保存
						54 	//我的管家页面查询
				});
		model.setTaskName("测试" + RandomUtils.randomFour());	//10
		model.setRenameButton("确定");	//13
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(sensorState);	//30
		model.setStartTimeInSetTime(new TimeModel(1, 1));	//65,66
		model.setEndTimeInSetTime(new TimeModel(2, 2));	//67,68
		model.setDeviceName(deviceName);	//28
		model.setDeviceStateInTask(deviceState);	//31
		model.setDelayTimeInTask(new TimeModel(1, 1));	//37,38

		baseProcess(model);
	}

	/**
	 * 创建情景任务（默认名称，一个触发条件，默认生效时间，非延时任务（设备））[紧急按钮消警]
	 */
	public void UrgentButtonCreateSceneTask() {
		MyHousekeeperModel model = new MyHousekeeperModel(
				new int[] {
						0, 2, 5, 	//进入情景任务页面
						19, 20, 21, 29, 30, 5, 	//设置条件
						23, 24, 25, 28, 29, 31, 32, 	//设置设置状态
						39, 5 , 	//设置延时
						52, 	//情景任务页面查询
						48, 46, 	//保存
						75 	//我的管家页面查询
				});
		model.setSensorName(sensorName);	//21
		model.setDeviceStateInCondition(Common.DeviceState.KEY_LEAKAGE1);	//30
		model.setDeviceName(deviceName);	//28
		model.setDeviceStateInTask(deviceState);	//31

		baseProcess(model);
	}

}
