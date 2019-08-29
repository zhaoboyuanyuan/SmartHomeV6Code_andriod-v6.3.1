package cc.wulian.smarthomev6.proc;

import com.wtt.frame.robotium.Solo;

import cc.wulian.smarthomev6.app.AccountInfo;
import cc.wulian.smarthomev6.app.Activities;
import cc.wulian.smarthomev6.app.ActivitiesName;
import cc.wulian.smarthomev6.app.ControlInfo;
import cc.wulian.smarthomev6.model.BaseProcModel;
import cc.wulian.smarthomev6.model.Cpabout;
import cc.wulian.smarthomev6.utils.MessageUtils;

public class CpaboutPro extends BaseProc<Cpabout> {
    public CpaboutPro(Solo solo) {
        super(solo);
    }

    /**
     * 功能介绍
     */
    public void function(){
        Cpabout model=new Cpabout(new int[]{0,1,3,4});
        model.setTitle("功能介绍");
        baseProcess(model);
    }

    /**
     * 关于物联
     */
    public void aboutWulian(){
        Cpabout model=new Cpabout(new int[]{0,2,3,4});
        model.setTitle("关于");
        baseProcess(model);
    }

    /**
     * 点击进入关于页面
     */
    public void clickAbout(){
        commonProc.scrollDownInMine();
        click.clickToAnotherActivity(ControlInfo.item_about, ActivitiesName.AboutActivity,Msg.intoAboutActivityFail);
    }

    /**
     * 点击进入功能介绍页面
     */
    public void clickFunction(){
//        throw new RuntimeException("运行错误");
        click.clickToAnotherActivity(ControlInfo.item_about_us_introduction,ActivitiesName.IntroductionActivity,Msg.intoIntroductionActivityFail);
    }

    /**
     * 点击进入关于物联页面
     */
    public void clickWu(){
        click.clickToAnotherActivity(ControlInfo.item_about_us_about,ActivitiesName.AboutUsActivity,Msg.intoAboutUsActivityFail);
    }



    @Override
    public void process(Cpabout model, int action) {
        switch (action){
            case 0:
                clickAbout();
                break;
            case 1:
                clickFunction();
                break;
            case 2:
                clickWu();
                break;
            case 3:
                if(!solo.searchText(model.getTitle())){
                    MessageUtils.append("未找到"+model.getTitle()+"!");
                }
                break;
            case 4:
                commonProc.dragViewOnScreen("title",0,500,0);
                break;

        }
    }

    @Override
    public void init() {
        commonProc.login(AccountInfo.Account1,AccountInfo.Password1);
        commonProc.forceRefresh();
    }

    public static final class Msg{
        public static final String intoAboutActivityFail="进入关于页面失败";
        public static final String intoIntroductionActivityFail="进入功能介绍页面失败";
        public static final String intoAboutUsActivityFail="进入关于我们页面失败";
    }
}
