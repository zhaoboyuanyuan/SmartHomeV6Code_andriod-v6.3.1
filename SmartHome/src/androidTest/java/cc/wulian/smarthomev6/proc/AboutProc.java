package cc.wulian.smarthomev6.proc;

import cc.wulian.smarthomev6.app.AccountInfo;
import cc.wulian.smarthomev6.app.ActivitiesName;
import cc.wulian.smarthomev6.model.AboutModel;
import cc.wulian.smarthomev6.utils.MessageUtils;
import com.wtt.frame.robotium.Solo;

/**
 * Created by 赵永健 on 2017/11/7.
 */
public class AboutProc extends BaseProc<AboutModel> {

    public AboutProc(Solo solo) {
        super(solo);
    }

    /**
     * 1.功能介绍
     */
    public void viewIntroduction(){
        AboutModel model=new AboutModel(new int[]{0,1,3});
        model.setTitle("功能介绍");
        baseProcess(model);
    }

    /**
     * 2.关于物联
     */
    public void viewAboutUs(){
        AboutModel model=new AboutModel(new int[]{0,2,3});
        model.setTitle("关于");
        baseProcess(model);
    }

    /***
     * 错误信息
     */
    public static final class Msg{
        public static final String intoAboutActivityFail="未进入关于页面";
        public static final String intoIntroductionFail="未进入功能介绍页面";
        public static final String intoAboutUsFail="未进入关于页面";
    }

    @Override
    public void process(AboutModel model, int action) {
        switch(action){
            case 0:
                clickToAbout();
                break;
            case 1:
                clickToIntroduction();
                break;
            case 2:
                clickAboutWULIAN();
                break;
            case 3:
                if(!solo.searchText(model.getTitle())){
                    MessageUtils.append("未显示" + model.getTitle() + "!");
                }
                break;
        }
    }


    @Override
    public void init() {
        commonProc.login(AccountInfo.Account1, AccountInfo.Password1);
        commonProc.forceRefresh();
    }

    /**
     * 点击进入关于页面
     * @return
     */
    public void clickToAbout(){
        commonProc.scrollDownInMine();
        click.clickToAnotherActivity("item_about", ActivitiesName.AboutActivity, Msg.intoAboutActivityFail);
    }

    /**
     * 点击进入功能介绍页面
     * @return
     */
    public void clickToIntroduction(){
        click.clickToAnotherActivity("item_about_us_introduction",
                ActivitiesName.IntroductionActivity, Msg.intoIntroductionFail);
    }

    /**
     * 点击进入关于物联页面
     * @return
     */
    public void clickAboutWULIAN(){
        click.clickToAnotherActivity("item_about_us_about",
                ActivitiesName.AboutUsActivity, Msg.intoAboutUsFail);
    }
}
