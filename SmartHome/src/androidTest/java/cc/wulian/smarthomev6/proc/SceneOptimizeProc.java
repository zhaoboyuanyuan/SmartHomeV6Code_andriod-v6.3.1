package cc.wulian.smarthomev6.proc;

import com.wtt.frame.robotium.By;
import com.wtt.frame.robotium.Solo;
import com.wtt.frame.robotium.WebElement;

import java.util.zip.CRC32;

import cc.wulian.smarthomev6.app.AccountInfo;
import cc.wulian.smarthomev6.app.ActivitiesName;
import cc.wulian.smarthomev6.app.ControlInfo;
import cc.wulian.smarthomev6.model.SceneOptimizeModel;
import cc.wulian.smarthomev6.utils.MessageUtils;
import cc.wulian.smarthomev6.utils.RandomUtils;

/**
 * 场景优化：包括场景再编辑，设备批量添加
 */
public class SceneOptimizeProc extends BaseProc<SceneOptimizeModel>{

    private CreateSceneProc createSceneProc;
    private GatewaySettingProc gatewaySettingProc;
    private String deviceName="红外入侵";
    private String sceneName= "回家";
    public SceneOptimizeProc(Solo solo) {
        super(solo);
        createSceneProc=new CreateSceneProc(solo);
        gatewaySettingProc=new GatewaySettingProc(solo);
    }
    /**
     * 场景再次编辑成功
     */
    public void sceneEditA(){
        SceneOptimizeModel model=new SceneOptimizeModel(new int[]{0,1,2,3,4});
        model.setSceneName(sceneName);
        model.setDeviceName(deviceName);
        baseProcess(model);
    }

    /**
     * 网关登录时场景再次编辑
     */
    public void sceneEditAsGL(){
        SceneOptimizeModel model=new SceneOptimizeModel(new int[]{5,0,1,2,3,4,6});
        model.setDeviceName(deviceName);
        baseProcess(model);
    }

    /**
     * 场景批量添加成功
     */
    public void batchAdditionSucc(){
        SceneOptimizeModel model=new SceneOptimizeModel(new int[]{0,1,2,7,8,9,10,11,12,13,17,14});
        model.setWebText("全选");
        baseProcess(model);
    }

    /**
     * 点击全选变为全不选
     */
    public void selectAllAndNot(){
        SceneOptimizeModel model=new SceneOptimizeModel(new int[]{0,1,2,20,7,8,9,10,11,16});
        model.setWebText("全选");
        model.setSearchText("全不选");
        baseProcess(model);
    }

    /**
     * 点击全不选变为全选
     */
    public void clickSelectAllAndNot(){
        SceneOptimizeModel model=new SceneOptimizeModel(new int[]{0,1,2,20,7,8,9,10,11,19,16});
        model.setWebText("全选");
        model.setSearchText("全选");
        baseProcess(model);
    }

    /**
     * 点击我知道了
     */
    public void noneSelect(){
        SceneOptimizeModel model=new SceneOptimizeModel(new int[]{0,1,2,7,8,9,10,11,12,17,16,18});
        model.setWebText("全选");
        model.setSearchText("我知道了");
        baseProcess(model);
    }




    @Override
    public void process(SceneOptimizeModel model, int action) {
        switch (action){
            case 0:
                intoAll();
                break;
            case 1:
                clickLong();
                break;
            case 2:
                clickEdit();
                break;
            case 3:
                clickDeviceName(model.getDeviceName());
                break;
            case 4:
                againEdit();
                break;
            case 5:
                if(!loginAsGateway()){
                    MessageUtils.append(Msg.gatewayLoginFailed);//网关登录
                }
                break;
            case 6:
//                solo.clickOnView(getter.getView(ControlInfo.all_scene_image_back));
                commonProc.quitLogin();
                break;
            case 7:
                createSceneProc.clickAdd();
                break;
            case 8:
                if(!solo.waitForWebElement(By.textContent("添加设备"),5000,true)){
                    MessageUtils.append(Msg.intoAddDeviceFailed);
                }
                break;
            case 9:
                clickBatch();
                break;
            case 10:
                if(!solo.waitForWebElement(By.textContent("批量添加"),3000,true)){
                    MessageUtils.append(Msg.intoBatchFailed);
                }
            case 11:
                clickSelectAll(model.getWebText());
                break;
            case 12:
                clickfinish();
                break;
            case 13:
                clickOpen();
                break;
            case 14:
                finishAndSave();
                break;
            case 15:
                if(!solo.searchText(model.getSearchText())){
                    MessageUtils.append("未找到"+model.getSearchText()+"!");
                }
                break;
            case 16:
                if(!searchSelect(model.getSearchText())){
                    MessageUtils.append("未找到"+model.getSearchText()+"!");
                }
            case 17:
                clickFinish1();
                break;
            case 18:
                solo.clickOnWebElement(By.textContent(model.getSearchText()));//点击我知道了
                break;
            case 19:
                clickSelectAll("全不选");
                break;
            case 20:
                commonProc.waitForEditScenePage();
                break;
        }
    }

    @Override
    public void init() {
        commonProc.login(AccountInfo.Account,AccountInfo.Password1);
        commonProc.forceRefresh();
    }

    /**
     * 进入全部场景
     */
    public void intoAll(){
        solo.clickOnView(commonProc.getNavigationChild(3));
        solo.clickOnView(getter.getView(ControlInfo.tv_all_scene));
        if (!solo.searchText("场景")) return;
        solo.sleep(2000);
    }

    public void clickLong(){
//        solo.clickLongOnText(sceneName);
        solo.clickLongOnScreen(115,299);
    }

    public void clickEdit(){
        solo.clickOnView(getter.getView(ControlInfo.popup_edit_scene_text_edit));
    }

    public void clickDeviceName(String deviceName){
        solo.clickOnWebElement(By.textContent(deviceName));
    }

    public void againEdit(){
        commonProc.clickDeviceState1();
        solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.edit_scene_finishDelay));
        if(!commonProc.waitForEditScenePage()) return;
        solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.edit_scene_finished));
        if(!solo.searchText("场景")){
            MessageUtils.append(Msg.findAllSceneFailed);
        }
    }

    private boolean loginAsGateway() {
        commonProc.scrollDownInMine();
        solo.clickOnView(getter.getView(ControlInfo.item_setting));
        if (!solo.waitForActivity(ActivitiesName.SigninActivity, 2000)) {
            solo.clickOnView(getter.getView(ControlInfo.item_setting_logout));
            commonProc.scrollUpInMine();
            solo.clickOnView(getter.getView(ControlInfo.item_account_login));
            commonProc.waitForLogin(2000);
        }
        solo.clickOnView(getter.getView(ControlInfo.tv_gateway_login));
        gatewaySettingProc.enterGatewayNumber();
        gatewaySettingProc.enterGatewayPassword();
        solo.clickOnView(getter.getView(ControlInfo.btn_gateway_login));
        if (!commonProc.waitForHomePage(5000)) return false;
        solo.sleep(2000);
        return true;
    }

    /**
     * 点击批量添加
     */
    private void clickBatch(){
        solo.clickOnWebElement(By.textContent("批量添加"));
    }

    /**
     * 点击全选
     */
    private void clickSelectAll(String text){
//        solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.selectedAllBtn));

        try{
            solo.clickOnWebElement(By.textContent(text));
        }catch (Error e){
            return;
        }
    }

    /**
     * 点击完成
     */
    private void clickfinish(){
        solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.finishScene));
//        solo.clickOnWebElement(By.textContent("完成"));
    }

    /**
     * 点击开
     */
    private void clickOpen(){
        solo.clickOnWebElement(By.textContent("开"));
    }

    /**
     * 延时页面完成且保存
     */
    private void finishAndSave(){
        solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.edit_scene_finishDelay));
        if(!commonProc.waitForEditScenePage()) return;
        solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.edit_scene_finished));
        if(!solo.searchText("场景")){
            MessageUtils.append(Msg.findAllSceneFailed);
        }
    }

    /**
     * 找
     * @param searchText
     * @return
     */
    private boolean searchSelect(String searchText){
        try {
            WebElement webElement = solo.getWebElement(By.textContent(searchText), 0);
        }catch (Error e){
            return false;
        }
        return true;
    }


    /**
     * 设置设备状态完成
     */
    private void clickFinish1(){
        solo.clickOnWebElement(By.textContent("完成"));
    }




    public static final class Msg{
        public static final String findAllSceneFailed = "进入全部场景失败";
        public static final String gatewayLoginFailed = "网关登录失败";
        public static final String intoAddDeviceFailed = "进入添加设备失败";
        public static final String intoBatchFailed = "进入批量添加失败";

    }
}
