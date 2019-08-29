package cc.wulian.smarthomev6.proc;

import cc.wulian.smarthomev6.app.AccountInfo;
import cc.wulian.smarthomev6.app.ActivitiesName;
import cc.wulian.smarthomev6.app.ControlInfo;
import cc.wulian.smarthomev6.model.SceneSortAndSearchModel;
import cc.wulian.smarthomev6.utils.EnterUtils;
import cc.wulian.smarthomev6.utils.MessageUtils;
import com.wtt.frame.robotium.Solo;

/**
 * 场景排序和搜索场景
 * Created by 赵永健 on 2017/11/6.
 */
public class SceneSortAndSearchProc extends BaseProc<SceneSortAndSearchModel> {
    public SceneSortAndSearchProc(Solo solo) {
        super(solo);
    }

    /**
     * 1.场景顺序排列
     */
    public void sceneSortAsSuccess(){
        SceneSortAndSearchModel model=new SceneSortAndSearchModel(new int[]{0,1,2});
        model.setIndex(0);
        baseProcess(model);
    }

    /**
     * 2.取消场景顺序排列
     */
    public void sceneSortAsCancel(){
        SceneSortAndSearchModel model=new SceneSortAndSearchModel(new int[]{0,1,3});
        model.setIndex(0);
        baseProcess(model);
    }

    /**
     * 3.恢复默认顺序
     */
    public void sceneSortAsReset(){
        SceneSortAndSearchModel model=new SceneSortAndSearchModel(new int[]{0,1,4,2});
        model.setIndex(0);
        baseProcess(model);
    }

    /**
     * 4.根据关键字搜索场景
     */
    public void searchSceneByName(){
        SceneSortAndSearchModel model=new SceneSortAndSearchModel(new int[]{5,6});
        model.setSceneName("回家");
        baseProcess(model);
    }

    /**
     * 5.根据首字母搜索场景
     */
    public void searchSceneByLetter(){
        SceneSortAndSearchModel model=new SceneSortAndSearchModel(new int[]{7,6});
        model.setLetter("h");
        model.setSceneName("回家");
        baseProcess(model);
    }

    /**
     * 6.取消搜索
     */
    public void searchSceneCancel(){
        SceneSortAndSearchModel model=new SceneSortAndSearchModel(new int[]{7,6,8});
        model.setLetter("h");
        model.setSceneName("回家");
        baseProcess(model);
    }

    /**
     * 错误信息
     */
    public static final class Msg{
        public static final String intoSceneSortActivityFail="进入场景搜索页面失败";
        public static final String searchSenceFail="搜索场景失败";

    }

    @Override
    public void process(SceneSortAndSearchModel model, int action) {
        switch (action){
            case 0:
                clickToSceneSort();
                break;
            case 1:
                commonProc.dragSceneSort(model.getIndex());
                break;
            case 2:
                solo.clickOnView(getter.getView(ControlInfo.img_left));//点击完成
                break;
            case 3:
                solo.clickOnView(getter.getView(ControlInfo.scene_sort_text_cancel));//点击取消
                break;
            case 4:
//                solo.scrollToBottom();
//                solo.clickOnView(getter.getView(ControlInfo.scene_sort_text_reset));//恢复默认顺序
                keepScroll();
                break;
            case 5:
                enterScene(model.getSceneName());
                break;
            case 6:
               if(!searchIt(model.getSceneName())){
                   MessageUtils.append(Msg.searchSenceFail);
               }
                break;
            case 7:
                enterScene(model.getLetter());
                break;
            case 8:
                solo.clickOnView(getter.getView(ControlInfo.all_scene_text_cancel));//取消搜索
                break;
        }

    }

    @Override
    public void init() {
        commonProc.login(AccountInfo.Account1, AccountInfo.Password1);
        commonProc.forceRefresh();
        solo.clickOnView(commonProc.getNavigationChild(3));
        solo.clickOnView(getter.getView(ControlInfo.tv_all_scene));
    }

    /**
     * 点击进入场景搜索页面
     * @return
     */
    public void clickToSceneSort(){
        click.clickToAnotherActivity((ControlInfo.iv_scene_setting),
                ActivitiesName.SceneSortActivity, Msg.intoSceneSortActivityFail);
    }

    /**
     * 输入场景名搜索场景
     * @param content
     * @return
     */
    public void enterScene(String content){
        enter.enterText(ControlInfo.all_scene_edit_search,content);
    }

    /**
     * 是否找到场景
     * @param sceneName
     * @return
     */
    public boolean searchIt(String sceneName){
        if(solo.searchText(sceneName)){
            return true;
        }else{
            return false;
        }
    }

    public void keepScroll(){
        for(int i=0;i<=4;i++){
            solo.scrollDown();
            if(solo.searchText("恢复默认顺序")){
                solo.clickOnText("恢复默认顺序");
                return;
            }
        }
    }

}
