package cc.wulian.smarthomev6.proc;

import cc.wulian.smarthomev6.app.AccountInfo;
import cc.wulian.smarthomev6.app.ActivitiesName;
import cc.wulian.smarthomev6.app.ControlInfo;
import cc.wulian.smarthomev6.app.GatewayInfo;
import cc.wulian.smarthomev6.model.FeedbackModel;
import cc.wulian.smarthomev6.utils.MessageUtils;
import com.wtt.frame.robotium.Solo;

/**
 * Created by 赵永健 on 2017/9/12.
 */
public class FeedbackProc extends BaseProc<FeedbackModel> {
    private GatewayLoginProc gatewayLoginProc;
    public FeedbackProc(Solo solo) {
        super(solo);
        gatewayLoginProc =new GatewayLoginProc(solo);
    }


    /**
     *1.输入为空时，提交按钮不可点击
     */
    public void enterNull(){
        FeedbackModel model =new FeedbackModel(new int[]{0,6});
        model.setButtontxt("提交");
        baseProcess(model);
    }

    /**
     * 2.输入3个字符
     */
    public void enterBuffer3(){
        FeedbackModel model =new FeedbackModel(new int[]{0,1,2,4,3,5});
        model.setBuffer("123");
        model.setEmail("15951644332@qq.com");
        model.setRemainCount(297);
        model.setSearchtext(Msg.FeedbackSuccess);
        baseProcess(model);
    }

    /**
     * 3.输入超过300个字符
     */
    public void enterBufferMore300(){
        String a="你好吗";
        String b="";
        for(int i=0;i<=100;i++){
            b=b+a;
        }
        FeedbackModel model =new FeedbackModel(new int[]{0,1,2,3,5});
        model.setBuffer(b);
        model.setEmail("15951644332@qq.com");
        model.setSearchtext(Msg.FeedbackSuccess);
        baseProcess(model);
    }

    /**
     * 4.游客访问点击意见反馈跳转到登录页面
     */
    public void Visitor(){
        FeedbackModel model =new FeedbackModel(new int[]{7});
        baseProcess(model);
    }

    /**
     * 5.网关直接登录意见反馈被隐藏
     */
    public void feedBackHide(){
        FeedbackModel model =new FeedbackModel(new int[]{8,9});
        baseProcess(model);
    }


    public  static final class Msg{
        public static final String intoFeedbackActivityFail="进入意见反馈页面失败";
        public static final String FeedbackSuccess="提交成功";
        public static final String FeedbackHideFail="意见反馈没有被隐藏";
    }
    @Override
    public void process(FeedbackModel model, int action) {
        switch (action){
            case 0:
                clickToFeedback();
                break;
            case 1:
                enterMsg(model.getBuffer());
                break;
            case 2:
                enterMail(model.getEmail());
                break;
            case 3:
                clickSubmit();
                break;
            case 4:
                if(!remainCount(model.getRemainCount(),model.getBuffer())) return;
                break;
            case 5:
                if (solo.searchText(model.getSearchtext())==false){
                    MessageUtils.append("未显示"+model.getSearchtext()+"!");
                    return;
                }
                break;
            case 6:
                if(solo.isRadioButtonChecked(model.getButtontxt())) return;
                break;
            case 7:
                visit();
                break;
            case 8:
                if(!gatewayLoginInit()){
                    MessageUtils.append(Msg.FeedbackHideFail);
                }
                break;
            case 9:
                commonProc.quitLogin();
                break;
        }
    }

    @Override
    public void init() {
        commonProc.login(AccountInfo.Account1, AccountInfo.Password1);
    }

    public void clickToFeedback(){
        click.clickToAnotherActivity(ControlInfo.item_customer_feedback,
                ActivitiesName.FeedbackActivity, Msg.intoFeedbackActivityFail);
    }

    /**
     * 输入意见信息
     * @param content
     * @return
     */
    public void enterMsg(String content){
        enter.enterTextAsCopy(ControlInfo.feedback_edit_msg,content);
    }

    /**
     * 输入邮箱
     * @param content
     * @return
     */
    public void enterMail(String content){
        enter.enterText(ControlInfo.feedback_edit_email,content);
    }
    /**
     * 点击提交
     */
    public void clickSubmit(){
        solo.clickOnView(getter.getView(ControlInfo.feedback_button_submit));
    }

    /**
     * 判断剩余字符数是否正确
     * @param remainCount
     * @param buffer
     * @return
     */
    public boolean remainCount(int remainCount,String buffer){
        int count=300;
        int remainCount1=0;
        int i = Integer.parseInt(buffer);
        remainCount1 = count-i;
        if(remainCount==remainCount1){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 游客模式访问
     */
    public void visit(){
        commonProc.quitLogin();
        solo.goBack();
        solo.clickOnView(commonProc.getNavigationChild(4));
        solo.clickOnView(getter.getView(ControlInfo.item_customer_feedback));
        commonProc.waitForLogin(5000);
    }


    public boolean gatewayLoginInit() {
        commonProc.quitLogin();
        gatewayLoginProc.clickToGatewayLogin();
        gatewayLoginProc.enterGatewayNumber(GatewayInfo.Gateway2.number);
        gatewayLoginProc.enterPassword(GatewayInfo.Gateway2.password1);
        gatewayLoginProc.clickGatewayLoginBtn();
        commonProc.waitForHomePage(5000);
        if (solo.searchText("意见反馈")) {
            return true;
        } else {
            return false;
        }
    }
}
