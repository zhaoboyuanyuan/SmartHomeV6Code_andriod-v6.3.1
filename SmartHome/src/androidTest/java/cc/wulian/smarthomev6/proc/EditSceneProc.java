package cc.wulian.smarthomev6.proc;

import android.view.View;

import cc.wulian.smarthomev6.app.ActivitiesName;
import cc.wulian.smarthomev6.app.Common;
import cc.wulian.smarthomev6.app.ControlInfo;
import cc.wulian.smarthomev6.model.EditSceneModel;
import cc.wulian.smarthomev6.model.TimeModel;
import cc.wulian.smarthomev6.utils.MessageUtils;
import cc.wulian.smarthomev6.utils.RandomUtils;
import com.wtt.frame.robotium.By;
import com.wtt.frame.robotium.Solo;
import com.wtt.frame.robotium.WebElement;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 赵永健 on 2017/6/23.
 *
 * 封装编辑场景的测试流程
 */
public class EditSceneProc extends BaseProc<EditSceneModel> {

	private String deviceName = "红外入侵";
	private int deviceState = Common.DeviceState.KEY_STATE1;
	String sceneName;
	private CreateSceneProc createSceneProc;
	private String oneSwitch = "开关1";

	public void commonProcess(EditSceneModel model) {
		baseProcess(model);
	}

	public EditSceneProc(Solo solo) {
		super(solo);
		createSceneProc = new CreateSceneProc(solo);
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public void setDeviceState(int deviceState) {
		this.deviceState = deviceState;
	}

	public void editSceneAsTime1() {
		editSceneWhole(23, 1, 1, 1, null);
	}

	public void editSceneAsTime99() {
		editSceneWhole(23, 1, 99, 59, null);
	}

	public void editSceneAsTime1SaveAndBack() {
		editSceneWhole(22, 1, 1, 1, "保存并退出");
	}

	public void editSceneAsTime99SaveAndBack() {
		editSceneWhole(22, 1, 99, 59, "保存并退出");
	}

	public void editSceneAsTime1Back() {
		editSceneWhole(22, 1, 1, 1, "退出");
	}

	public void editSceneAsTime99Back() {
		editSceneWhole(22, 1, 99, 59, "退出");
	}

	public void editSceneAsTime1Cancel() {
		editSceneWhole(22, 1, 1, 1, "取消");
	}

	public void editSceneAsTime99Cancel() {
		editSceneWhole(22, 1, 99, 59, "取消");
	}

	private void editSceneWhole(int save, int iconIndex, int hour, int minute, String backOperate) {
		EditSceneModel sceneModel = new EditSceneModel();
		String sceneName = Integer.toString(RandomUtils.randomFour());
		sceneModel.setSceneName(sceneName);	//6
		sceneModel.setSelectIconIndex(iconIndex);	//7
		sceneModel.setDeviceName(deviceName);	//11
		sceneModel.setDeviceState(deviceState);	//13
		sceneModel.setTimePicker("分");	//16
		sceneModel.setDelayTime(new TimeModel(hour, minute));	//17,18
		sceneModel.setDeviceTime(hour + "分" + minute + "秒后开始执行");	//21
		sceneModel.setDeleteButton("确定");
		if (save == 22) sceneModel.setBackOperate(backOperate);

		List<Integer> actionsList = getEditSceneWholeList(save, backOperate);
		Integer[] actions = new Integer[actionsList.size()];
		actionsList.toArray(actions);

		int[] actionsInt = ArrayUtils.toPrimitive(actions);
		sceneModel.setActions(actionsInt);

		baseProcess(sceneModel);
	}

	private List<Integer> getEditSceneWholeList(int save, String backOperate) {
		List<Integer> actions = new ArrayList<Integer>();
		actions.add(0);
		actions.add(1);actions.add(3);	//进入编辑页面
		actions.add(4);actions.add(5);	//进入自定义场景页面
		actions.add(7);actions.add(6);actions.add(8);actions.add(3);	//编辑场景信息
		actions.add(25);	//编辑场景页面查询
		actions.add(9);actions.add(10);actions.add(11);actions.add(12);actions.add(13);actions.add(14);	//设置设备状态
//		actions.add(15);actions.add(16);actions.add(17);actions.add(18);actions.add(19);actions.add(3);	//设置设备延时
		actions.add(19);actions.add(3);
//		actions.add(20);actions.add(21);	//编辑场景页面查询
		actions.add(20);
		actions.add(save);	//保存
//		actions.add(29);actions.add(0);actions.add(2);actions.add(31);//删除

		if (22 == save) {
			if (!"取消".equals(backOperate)) {
				actions.add(24);actions.add(25);	//全部场景查询
				actions.add(29);	//切换场景名
				actions.add(0);actions.add(1);actions.add(3);	//进入编辑页面
//				if ("退出".equals(backOperate)) {
//					actions.add(30);	//编辑页查询到设备
//				} else
                if ("保存并退出".equals(backOperate)) {
					actions.add(20);	//编辑页面查询不到设备
				}
			} else {
				actions.add(20);
//				actions.add(21);	//编辑页面查询
			}
		} else if (23 == save) {
			actions.add(24);
//			actions.add(25);	//全部场景查询
		}

		return actions;
	}

	public void editSceneNoneDelay() {
		editSceneNoneDelay(23);
	}

	public void editSceneNoneDelayAndBack() {
		editSceneNoneDelay(22);
	}

	private void editSceneNoneDelay(int save) {
		EditSceneModel sceneModel = new EditSceneModel(
				new int[] {
						0, 1, 3,  	//进入编辑页面
						4, 5, 	//进入自定义场景页面
						7, 6, 8, 3, 	//编辑场景信息
						25, 	//编辑场景页面查询
						9, 10, 11,12, 13, 14, 	//设置设备状态
						19, 3, 	//设置设备延时
						20, 21, 	//编辑场景页面查询
						save, 24, 	//保存
						25,	//全部场景页面查询
//						29, 0, 2, 31	//删除
				});
		String sceneName = Integer.toString(RandomUtils.randomFour());
		sceneModel.setSceneName(sceneName);	//6
		sceneModel.setSelectIconIndex(1);	//7
		sceneModel.setDeviceName(deviceName);	//11
		sceneModel.setDeviceState(deviceState);	//13
		sceneModel.setDeviceTime("立即执行");	//21
		sceneModel.setDeleteButton("确定");
		if (22 == save) sceneModel.setBackOperate("保存并退出");

		baseProcess(sceneModel);
	}

	public void deleteSceneCancel() {
		deleteScene("取消", 25);
	}

	public void deleteSceneConfirm() {
		deleteScene("确定", 33);
	}

	private void deleteScene(String deleteButton, int search) {
		EditSceneModel sceneModel = new EditSceneModel(
				new int[] {
						0, 1, 3,  	//进入编辑页面
						4, 5, 	//进入自定义场景页面
						7, 6, 8, 3, 	//编辑场景信息
						25, 	//编辑场景页面查询
						9, 10, 11, 12, 13, 14, 	//设置设备状态
						19, 3, 	//设置设备延时
						20, 21, 	//编辑场景页面查询
						23, 24, 	//保存
						25, 	//全部场景页面查询
						29, 0, 2, 31,	//删除
						search
				});
		String sceneName = Integer.toString(RandomUtils.randomFour());
		sceneModel.setSceneName(sceneName);	//6
		sceneModel.setSelectIconIndex(1);	//7
		sceneModel.setDeviceName(deviceName);	//11
		sceneModel.setDeviceState(deviceState);	//13
		sceneModel.setDeviceTime("立即执行");	//21
		sceneModel.setDeleteButton(deleteButton);

		baseProcess(sceneModel);
	}

	@Override
	public void process(EditSceneModel model, int action) {
		switch (action) {
			case 0:
				displayEditAndDelete(sceneName);
				break;
			case 1:
				clickEditScene();
				break;
			case 2:
				clickDeleteScene();
				break;
			case 3:
				if (!commonProc.waitForEditScenePage()) return;
				break;
			case 4:
				clickToEditBaseSceneInfo();
				break;
			case 5:
				if (!commonProc.waitForCustomScenePage()) return;
				break;
			case 6:
				createSceneProc.enterSceneName(model.getSceneName());
				break;
			case 7:
				createSceneProc.selectedIcon(model.getSelectIconIndex());
				break;
			case 8:
				solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.customScene_sure));
				break;
			case 9:
				clickAddDevice();
				break;
			case 10:
				if (!commonProc.waitForDeviceListPage()) return;
				break;
			case 11:
				addDevice(model.getDeviceName());
				break;
			case 12:
				if (!commonProc.waitForDeviceStatePage()) return;
				break;
			case 13:
				commonProc.selectDeviceState(model.getDeviceState());
				break;
			case 14:
				if (!commonProc.waitForAddDelayPage()) return;
				break;
			case 15:
				commonProc.clickSwitchDelayInDelay();
				break;
			case 16:
				commonProc.searchText(model.getTimePicker(), 2000);
				break;
			case 17:
				commonProc.setDelayHour(model.getDelayTime().getHour());
				break;
			case 18:
				commonProc.setDelayMinute(model.getDelayTime().getMinute());
				break;
			case 19:
				commonProc.clickFinishInDelay();
				break;
			case 20:
				commonProc.searchText(model.getDeviceName(), 5000);
				break;
			case 21:
				commonProc.searchText(model.getDeviceTime(), 2000);
				break;
			case 22:
				backTo(model.getBackOperate());
				break;
			case 23:
				finishToSave();
				break;
			case 24:
				if(!solo.waitForText("场景",0,2000)){
					MessageUtils.append("未进入全部场景页面");
				}
				break;
			case 25:
				commonProc.searchText(model.getSceneName(), 30000);
				break;
			case 26:
				scrollDeviceItem(model.getDeviceName(), -1);
				break;
			case 27:
				clickDeleteDevice();
				break;
			case 28:
				commonProc.searchNoneText(model.getSceneName());
				break;
			case 29:
				sceneName = model.getSceneName();
				break;
			case 30:
				commonProc.searchNoneText(model.getDeviceName());
				break;
			case 31:
				clickInDeleteDialog(model.getDeleteButton());
				break;
			case 32:
				if (!solo.waitForDialogToOpen(2000)) {
					MessageUtils.append(Msg.DeleteSceneNoneDisplayDialog);
					return;
				}
				break;
			case 33:
				commonProc.searchNoneText(model.getSceneName());
				break;
			case 34:
				solo.clickOnWebElement(By.textContent(oneSwitch));
//				commonProc.selectDeviceState(CommonProc.DeviceState.KEY_STATE1);
				break;
			case 35:
//				commonProc.selectDeviceState(CommonProc.DeviceState.KEY_STATE1);
				solo.clickOnWebElement(By.textContent("开关 1"));
				break;
			case 36:
				solo.clickOnWebElement(By.textContent("开"));
				break;
		}
	}

	@Override
	public void init() {
		sceneName =Integer.toString(RandomUtils.randomFour())+"test";
		createSceneProc.init();
		createSceneProc.selectedIcon(0);
		createSceneProc.enterSceneName(sceneName);
		createSceneProc.clickConfirm();
		if (!commonProc.waitForEditScenePage()) return;
		createSceneProc.clickBack();
		if(!solo.waitForText("场景",0,2000)){
			MessageUtils.append("未进入全部场景页面");
		}
	}

	private void clickAddDevice() {
		solo.clickOnWebElement(By.textContent("添加任务"));
	}

	private void addDevice(String deviceName) {
		solo.clickOnWebElement(By.textContent(deviceName));
	}

	private void backTo(String operate) {
		createSceneProc.clickBack();
		solo.clickOnWebElement(By.textContent(operate));
	}

	private void finishToSave() {
		solo.clickOnWebElement(By.textContent("保存"));
	}

	public void scrollDeviceItem(String deviceName, int coefficient) {
		int defaultCoefficient = 2;
		if (0 < coefficient) defaultCoefficient = coefficient;
		WebElement el = solo.getWebElement(By.textContent(deviceName), 0);
		solo.drag(el.getLocationX()*defaultCoefficient, el.getLocationX(), el.getLocationY()
				, el.getLocationY(), 50);
	}

	public void displayEditAndDelete(String sceneName) {
	    try{
            solo.clickLongOnText(sceneName);
        }catch(Error e) {
			View view = getter.getView(ControlInfo.tv_all_scene,0);
			int[] position = new int[2];
			try{
				view.getLocationOnScreen(position);
			}catch (Exception ex){
				MessageUtils.append("发生错误");
			}
			float fromX = position[0];
			float fromY = position[1];
			while(true){
				int i=0;
                solo.drag(fromX,fromX,fromY+400,fromY,40);
				if(solo.searchText(sceneName)){
					solo.clickLongOnText(sceneName);
					break;
				}else if(i==50) {
                    MessageUtils.append("搜索场景名失败！");
                }
                i++;
			}
        }
	}


	private void clickEditScene() {
        if(solo.searchText("您可以这样说：                       ")){
            solo.clickOnView(getter.getView("btn_exit"));
            solo.sleep(2000);
            solo.clickLongOnScreen(392,604);
        }else if(solo.searchText("网关中心") || solo.searchButton("rel_right") ){
            solo.clickOnView(commonProc.getNavigationChild(3));
            solo.sleep(2000);
            solo.clickLongOnScreen(392,604);
        }
        try{
            solo.clickOnView(getter.getView(ControlInfo.popup_edit_scene_text_edit));
        }catch (Exception e){
			solo.clickLongOnScreen(349,654);
            solo.clickOnView(getter.getView(ControlInfo.popup_edit_scene_text_edit));
        }
	}

	public void clickDeleteScene() {
		solo.clickOnView(getter.getView(ControlInfo.popup_edit_scene_text_delete));
	}

	private void clickDeleteDevice() {
		solo.clickOnWebElement(By.textContent("删除"));
	}

	private void clickToEditBaseSceneInfo() {
		    solo.clickOnWebElement(By.xpath("/html/body/section[1]/div/a/span"));
	}

	private void clickInDeleteDialog(String button) {
		solo.clickOnButton(button);
	}

	@Override
	public void finalize() {
		if (null != createSceneProc) {
			createSceneProc.finalize();
			createSceneProc = null;
		}
		try {
			super.finalize();
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}

	/**
	 * 作为执行任务（多个任务）
	 */
	public void editSceneNoneDelay1() {
		EditSceneModel sceneModel = new EditSceneModel(
				new int[] {
						0, 1, 3,  	//进入编辑页面
						4, 5, 	//进入自定义场景页面
						6, 7, 8, 3, 	//编辑场景信息
						25, 	//编辑场景页面查询
						9, 10, 11, 12, 13, 14, 	//设置设备状态
						19, 3,
						20, 21, 	//编辑场景页面查询

						9, 10, 11, 12, 13, 14, 	//设置设备状态
						19, 3,
						20, 21, 	//编辑场景页面查询

						23, 24, 	//保存
						25 	//全部场景页面查询
				});
		String sceneName = Integer.toString(RandomUtils.randomFour());
		sceneModel.setSceneName(sceneName);	//6
		sceneModel.setSelectIconIndex(1);	//7
		sceneModel.setDeviceName(deviceName);	//11
		sceneModel.setDeviceState(deviceState);	//13
		sceneModel.setDeviceTime("立即执行");	//21

		baseProcess(sceneModel);
	}

	public void editSceneNoneDelay2() {
		EditSceneModel sceneModel = new EditSceneModel(
				new int[] {
						0, 1, 3,  	//进入编辑页面
						4, 5, 	//进入自定义场景页面
						6, 7, 8, 3, 	//编辑场景信息
						25, 	//编辑场景页面查询
						9, 10, 11,34, 12, 13, 14, 	//设置设备状态
						19, 3, 	//设置设备延时
						20, 21, 	//编辑场景页面查询
						23, 24, 	//保存
						25 	//全部场景页面查询
				});
		String sceneName = Integer.toString(RandomUtils.randomFour());
		sceneModel.setSceneName(sceneName);	//6
		sceneModel.setSelectIconIndex(1);	//7
		sceneModel.setDeviceName(deviceName);	//11
		sceneModel.setDeviceState(deviceState);	//13
		sceneModel.setDeviceTime("立即执行");	//21

		baseProcess(sceneModel);
	}

	public void editSceneNoneDelay3() {
		EditSceneModel sceneModel = new EditSceneModel(
				new int[]{
						0, 1, 3,    //进入编辑页面
						4, 5,    //进入自定义场景页面
						6, 7, 8, 3,    //编辑场景信息
						25,    //编辑场景页面查询
						9, 10, 11, 34, 12, 13, 14,    //设置设备状态
						19, 3,
						20, 21,    //编辑场景页面查询

						9, 10, 11, 34, 12, 13, 14,    //设置设备状态
						19, 3,
						20, 21,    //编辑场景页面查询

						23, 24,    //保存
						25    //全部场景页面查询
				});
		String sceneName = Integer.toString(RandomUtils.randomFour());
		sceneModel.setSceneName(sceneName);    //6
		sceneModel.setSelectIconIndex(1);    //7
		sceneModel.setDeviceName(deviceName);    //11
		sceneModel.setDeviceState(deviceState);    //13
		sceneModel.setDeviceTime("立即执行");    //21

		baseProcess(sceneModel);
	}

	public void editSceneNoneDelay4() {
		EditSceneModel sceneModel = new EditSceneModel(
				new int[] {
						0, 1, 3,  	//进入编辑页面
						4, 5, 	//进入自定义场景页面
						6, 7, 8, 3, 	//编辑场景信息
						25, 	//编辑场景页面查询
						9, 10, 11,35, 12, 36, 14, 	//设置设备状态
						19, 3, 	//设置设备延时
						20, 21, 	//编辑场景页面查询
						23, 24, 	//保存
						25 	//全部场景页面查询
				});
		String sceneName = Integer.toString(RandomUtils.randomFour());
		sceneModel.setSceneName(sceneName);	//6
		sceneModel.setSelectIconIndex(1);	//7
		sceneModel.setDeviceName(deviceName);	//11
		sceneModel.setDeviceTime("立即执行");	//21

		baseProcess(sceneModel);
	}

	public void editSceneNoneDelay5() {
		EditSceneModel sceneModel = new EditSceneModel(
				new int[]{
						0, 1, 3,    //进入编辑页面
						4, 5,    //进入自定义场景页面
						6, 7, 8, 3,    //编辑场景信息
						25,    //编辑场景页面查询
						9, 10, 11, 35, 12, 36, 14,    //设置设备状态
						19, 3,
						20, 21,    //编辑场景页面查询

						9, 10, 11, 35, 12,36,14,   //设置设备状态
						19, 3,
						20, 21,    //编辑场景页面查询

						23, 24,    //保存
						25    //全部场景页面查询
				});
		String sceneName = Integer.toString(RandomUtils.randomFour());
		sceneModel.setSceneName(sceneName);    //6
		sceneModel.setSelectIconIndex(1);    //7
		sceneModel.setDeviceName(deviceName);    //11
		sceneModel.setDeviceTime("立即执行");    //21

		baseProcess(sceneModel);
	}

	private static final class Msg {
		public static final String TaskTimeNoneDisplay = "场景详情页面未显示设备时间！";
		public static final String TaskTimeDiffBefore = "场景详情页面设备显示的延时时间 '";
		public static final String TaskTimeDiffCenter= "' 和预期结果的时间 '";
		public static final String TaskTimeDiffAfter= "' 不一致！";
		public static final String TaskNameNoneDisplay = "场景详情页面未显示设备名称！";
		public static final String TaskNameDiffBefore = "场景详情页面设备显示的名称 '";
		public static final String TaskNameDiffCenter= "' 和预期结果的时间 '";
		public static final String TaskNameDiffAfter= "' 不一致！";
		public static final String DeleteSceneNoneDisplayDialog = "未显示删除提示框！";
	}
}
