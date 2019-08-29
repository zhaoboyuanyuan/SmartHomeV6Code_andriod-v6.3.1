package cc.wulian.smarthomev6.proc;

import cc.wulian.smarthomev6.app.AccountInfo;
import cc.wulian.smarthomev6.app.ControlInfo;
import cc.wulian.smarthomev6.model.CreateSceneModel;
import cc.wulian.smarthomev6.utils.MessageUtils;
import cc.wulian.smarthomev6.utils.RandomUtils;

import com.wtt.frame.robotium.By;
import com.wtt.frame.robotium.Solo;
import com.wtt.frame.robotium.WebElement;

/**
 * Created by 赵永健 on 2017/5/17.
 *
 * 封装创建场景的测试流程
 */
public class CreateSceneProc extends BaseProc<CreateSceneModel> {
	
	private VisitProc proc;
	
	public CreateSceneProc(Solo solo) {
		super(solo);
		proc = new VisitProc(solo);
	}

	/**
	 *共有接口
	 */
	public void commonProcess(CreateSceneModel model) {
		baseProcess(model);
	}
	
	public void createSceneAsName10() {
		String name = RandomUtils.randomFour() + "abbbbb";
		createScene(name);
	}
	
	public void createSceneAsName20() {
		String name = RandomUtils.randomFour() + "abbbbb11111";
		createScene(name);
	}
	
	public void createSceneAsName25() {
		String name = RandomUtils.randomFour() + "abbbbb1111122222ccccc";
		createScene(name);
	}
	
	public void createScene(String sceneName) {
		CreateSceneModel model = new CreateSceneModel(new int[] {1, 0, 2, 3, 4, 5, 6});
		model.setSceneName(sceneName);
		model.setIconIndex(0);
		model.setSuccess(true);
		
		baseProcess(model);
	}
	
	public void createSceneAsNoneName() {
		CreateSceneModel model = new CreateSceneModel(new int[] {0, 1, 2});
		model.setSceneName("");
		model.setIconIndex(0);
		model.setTips(Msg.NameOrIconIsEmpty);
		baseProcess(model);
	}
	
	public void createSceneAsNoneIcon() {
		CreateSceneModel model = new CreateSceneModel(new int[] {0, 2});
		model.setSceneName(Integer.toString(RandomUtils.randomFour()));
		model.setTips(Msg.NameOrIconIsEmpty);
		
		baseProcess(model);
	}
	
	public void createSceneAsNameRepeat() {
		CreateSceneModel model = new CreateSceneModel(new int[] {0, 1, 2});
		model.setSceneName("测试");
		model.setIconIndex(0);
		model.setTips(Msg.NameOrIconIsEmpty);
		
		baseProcess(model);
	}
	
	@Override
	public void process(CreateSceneModel model, int action) {
		switch (action) {
			case 0:
				enterSceneName(model.getSceneName());
				break;
			case 1:
				selectedIcon(model.getIconIndex());
				break;
			case 2:
				clickConfirm();
				break;
			case 3:
				solo.sleep(2000);
				if(!commonProc.waitForEditScenePage())return;
				break;
			case 4:
				clickBack();
				break;
			case 5:
				if (!solo.waitForText("场景",0,2000)) return;
				break;
			case 6:
//				findScene(model.getSceneName(), model.isSuccess());
				commonProc.scrollInH(model.getSceneName());
				break;
			case 7:
				searchTips(model.getTips());
				break;
		}
	}
	
	@Override
	public void init() {
		commonProc.login(AccountInfo.Account1, AccountInfo.Password1);
		commonProc.forceRefresh();
		solo.clickOnView(commonProc.getNavigationChild(3));
		solo.clickOnView(getter.getView(ControlInfo.tv_all_scene));
		solo.clickOnView(getter.getView(ControlInfo.all_scene_image_add));
		if (!commonProc.waitForAddScene()) return;
	}
	
	@Override
	public void finalize() {
		if (null != proc) {
			try {
				proc.finalize();
			} catch (Throwable throwable) {
				throwable.printStackTrace();
			}
			proc = null;
		}
		
		try {
			super.finalize();
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}

	/**
	 * 创建场景并添加设备
	 * @param index
	 * @param sceneName
	 */
	public void createAdd(int index ,String sceneName,String deviceName){
		selectedIcon(index);
		enterSceneName(sceneName);
		clickConfirm();
		if(!commonProc.waitForEditScenePage()) return;
		clickAdd();
		addDevice(deviceName);
	}

	public void clickAdd(){
		solo.clickOnWebElement(By.textContent("添加任务"));
	}

	public void addDevice(String deviceName){
		if(!solo.waitForWebElement(By.textContent("添加设备"),3000,true)){
			MessageUtils.append(Msg.intoAddDeviceFailed);
		}
		solo.clickOnWebElement(By.textContent(deviceName));
		commonProc.clickDeviceState1();
		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.edit_scene_finishDelay));
		if(!commonProc.waitForEditScenePage()) return;
		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.edit_scene_finished));
		if(!solo.searchText("全部场景")){
			MessageUtils.append(Msg.backtoAS);
		}
	}
	
	public void enterSceneName(String sceneName) {
		 enter.enterTextAsWeb(ControlInfo.custom_scene_enter_name, sceneName);
	}
	
	public void selectedIcon(int index) {
		int indexTemp = index;
		if (0 > index) indexTemp = 0;
		if (8 < index) indexTemp = 8;
		
		switch (indexTemp) {
			case 0:
				solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.custom_scene_icon_1));
				break;
			case 1:
				solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.custom_scene_icon_2));
				break;
			case 2:
				solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.custom_scene_icon_3));
				break;
			case 3:
				solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.custom_scene_icon_4));
				break;
			case 4:
				solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.custom_scene_icon_5));
				break;
			case 5:
				solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.custom_scene_icon_6));
				break;
			case 6:
				solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.custom_scene_icon_7));
				break;
			case 7:
				solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.custom_scene_icon_8));
				break;
			case 8:
				solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.custom_scene_icon_9));
				break;
		}
	}
	
	public void clickConfirm() {
//		solo.sleep(1000);
		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.custom_scene_conf));
	}
	
	public void clickBack() {
		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.edit_scene_back));
	}
	
	public void findScene(String text, boolean isSuccess) {
		if (isSuccess && !search(text)) {
			MessageUtils.append(Msg.NotFindScene + text + "'！");
			return;
		} else if (!isSuccess && search(text)) {
			MessageUtils.append(Msg.FindScene + text + "'！");
			return;
		}
	}
	
	private void searchTips(String text) {
		solo.sleep(500);
		if (!solo.searchText(text)) {
			MessageUtils.append("未显示'" + text + "'！");
		}
	}
	
	private boolean search(String text) {
		return solo.searchText(text, 0, true, true);
	}
	
	private static final class Msg {
		public static final String NotFindScene = "创建成功时，未查询到 '";
		public static final String FindScene = "创建失败时，查询到 '";
		public static final String NameOrIconIsEmpty = "场景名称及场景图标不能为空或者场景名称重复，请重新命名或请选择图标";
		public static final String intoAddDeviceFailed = "进入添加设备页面失败";
		public static final String backtoAS = "返回全部场景页失败";
	}
}
