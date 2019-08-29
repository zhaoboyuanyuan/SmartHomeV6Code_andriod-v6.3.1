package cc.wulian.smarthomev6.proc;

import android.view.View;

import com.wtt.frame.robotium.Solo;

import java.util.Random;

import cc.wulian.smarthomev6.app.AccountInfo;
import cc.wulian.smarthomev6.app.ActivitiesName;
import cc.wulian.smarthomev6.app.ControlInfo;
import cc.wulian.smarthomev6.model.RegisterModel;
import cc.wulian.smarthomev6.utils.EnterUtils;
import cc.wulian.smarthomev6.utils.MessageUtils;
import cc.wulian.smarthomev6.utils.RandomUtils;

/**
 * Created by 赵永健 on 2017/6/8.
 */
public class CpRegisterProc extends BaseProc<RegisterModel> {
    private RegisterModel model;
    public CpRegisterProc(Solo solo) {
        super(solo);
    }
    private String account = "1368569"+RandomUtils.randomFour();
    private String rightPassword="123456abcd";

    /**
     * 初始化注册页面
     */
    @Override
    public void init() {
        commonProc.loginInit();
        solo.clickOnView(getter.getView(ControlInfo.register));
        if (!commonProc.waitForRegister(5000)) {
            return;
        }
    }
    /**
     * 输入电话号码
     */
    public void enterMoblicNum(String content){
//        return EnterUtils.KEY_SUCCESS == super.enter.enterText("v6.et_register_phone_number",content);
        enter.enterTextAsCopy(ControlInfo.et_register_phone_number,content);
    }
    /**
     * 点击下一步
     */
    public void getVerificationCode(){
        solo.clickOnView(super.getter.getView(ControlInfo.tv_get_verification));
    }

    /**
     *  是否弹出手机已注册验证
     */
    public void yanZhen(){
        if(model.getIndex()==0){
            solo.clickOnText("取消");
        }else if(model.getIndex()==1){
            solo.clickOnText("去登录");
            solo.clickOnView(getter.getView(ControlInfo.register));
            if (!commonProc.waitForRegister(5000)) {
                return;
            }
        }
    }

    /**
     * 输入验证码
     */
    public void enterVerificationCode(String content){
        if(!commonProc.waitForRegisterVerification()){
            MessageUtils.append(Msg.intoRVFailed);
        }
        commonProc.enterAndroidKeycode(content);
    }
    /**
     * 输入密码
     */
    public void enterPassword(String content){
//        return EnterUtils.KEY_SUCCESS == super.enter.enterText("v6.et_set_password",content);
        enter.enterText(ControlInfo.et_set_password,content);
    }
    /**
     * 勾选注册协议
     */
    public void clickSelectedPart(){
        solo.clickOnView(super.getter.getView(ControlInfo.cb_read_agreement));
    }

    /**
     * 查看服务条款
     */
    public void viewService(){
        solo.clickOnView(super.getter.getView(ControlInfo.tv_terms_of_use));
        if(solo.waitForActivity(ActivitiesName.DisclaimerActivity)==false) return;
        solo.scrollToBottom();
        solo.scrollToTop();
        solo.clickOnView(getter.getView(ControlInfo.img_left));
    }

    /**
     * 点击注册
     */
    public void clickRegist(){
        solo.clickOnView(super.getter.getView(ControlInfo.tv_register_button));
    }

    /**
     * 注册按钮是否可用
     */
    private void isRegisterBtnClickable() {
        View view = super.getter.getView(ControlInfo.tv_register_button);
        if (view.isClickable()){
            MessageUtils.append(Msg.registerBtnShown);
        }
    }

    /**
     * 等待一分钟
     */
    public void waitForMinutes(){
        solo.sleep(60000);
    }


    public  static final class Msg{
        public static final String wrongNumber="手机号格式错误";
        public static final String NoneNumber="手机号不能为空";
        public static final String NumUsed="手机号已注册时，点击获取验证码，提示信息错误！";
        public static final String netError="无法连接到服务器，请检查您的网络或稍后再试";
        public static final String VerCodeError="验证码错误";
        public static final String passwordWrong="密码格式为6~20位字母或数字";
        public static final String PasswordNone = "请输入密码";
        public static final String registerBtnShown = "'注册'按钮未禁用！";
        public static final String VerCodeSuccess="获取验证码成功";
        public static final String PasswordLessThan6Char="密码长度不能小于6个字符";
        public static final String PasswordAsNoMatch="密码至少有数字、字母或符号的2种组合";
        public static final String passwordAsIllegalChar="密码含非法字符或空格，请重新输入";
        public static final String PasswordAsWeak="数字和字母组合，密码长度至少10位";
        public static final String RegisterSuccess="注册成功";
        public static final String VerCodefailure="手机验证码失效";
        public static final String phoneUsed="用户已存在";
        public static final String reSend="重新发送";
        public static final String intoRVFailed="未进入验证码页面";

    }
    @Override
    public void process(RegisterModel model, int action) {
        switch (action){
            case 0:
                enterMoblicNum(model.getMobileNum());
                break;
            case 1:
                getVerificationCode();
                break;
            case 2:
                enterVerificationCode(model.getVerificationCode());
                break;
            case 3:
                enterPassword(model.getPassword());
                break;
            case 4:
                clickSelectedPart();
                break;
            case 5:
                viewService();
                break;
            case 6:
                clickRegist();
                break;
            case 7:
                if(commonProc.waitForHomePage(5000)==false) return;

                break;
            case 8:
                if (solo.searchText(model.getSearchText())==false){
                    MessageUtils.append("未显示"+model.getSearchText()+"!");
                    return;
                }
                break;
            case 9:
                isRegisterBtnClickable();
                break;
            case 10:
                yanZhen();
                break;
            case 11:
                waitForMinutes();
                break;
            case 12:
                solo.setWiFiData(false);
                break;
            case 13:
                solo.setWiFiData(true);
                break;
        }
    }

     /**
      * 1.账号注册---手机号注册密码
      */
    public void registerSuccess(){
        model =new RegisterModel(new int[]{0,1,2,3,4,6,7});
        model.setMobileNum(account);
        model.setVerificationCode("123456");
        model.setPassword(rightPassword);
        super.baseProcess(model);
    }
    /**
     * 2.账号注册---手机号注册，密码20位字母
     */
    public void registerSuccess1() {
        model = new RegisterModel(new int[]{0, 1, 2, 3, 4, 6, 7});
        model.setMobileNum(account);
        model.setVerificationCode("123456");
        model.setPassword("123456789012345abcde");
        super.baseProcess(model);
    }
    /**
     * 3.账号注册---手机号注册，密码13位字母和数字混合
     */
    public void registerSuccess2() {
        model = new RegisterModel(new int[]{0, 1, 2, 3, 4, 6, 7});
        model.setMobileNum(account);
        model.setVerificationCode("123456");
        model.setPassword("abcdefghigklm1234567");
        super.baseProcess(model);
    }
    /**
     *4.错误的手机格式
     */
    public void mobileNumWrong(){
        model=new RegisterModel(new int[]{0,1,8});
        model.setMobileNum("123");
        model.setSearchText(Msg.wrongNumber);
        super.baseProcess(model);
    }

    /**
     * 5.手机号码为空
     */
    public void mobileNoneNum(){
        model=new RegisterModel(new int[]{0,1});
        model.setMobileNum("");
        model.setSearchText(Msg.NoneNumber);
        super.baseProcess(model);
    }
    /**
     * 6.手机号已经被注册,点击取消
     */
    public void mobileNumUsed(){
        model=new RegisterModel(new int[]{0,1,8});
        model.setMobileNum(AccountInfo.Account);
//        model.setIndex(0);
        model.setSearchText(Msg.phoneUsed);
        super.baseProcess(model);
    }
    /**
     * 7.网络连接错误,手机要断网
     */
    public void netError(){
        solo.setWiFiData(false);
        model=new RegisterModel(new int[]{0,1,8});
        model.setMobileNum(account);
        model.setSearchText(Msg.netError);
        super.baseProcess(model);
        solo.setWiFiData(true);
        if(solo.searchText("允许")){
            solo.clickOnButton("允许");
        }
    }

    /**
     * 8.手机验证码错误
     */
    public void VerCodeError(){
        model=new RegisterModel(new int[]{0,1,2,8});
        model.setMobileNum(account);
        model.setVerificationCode("123456");
        model.setSearchText(Msg.VerCodeError);
        super.baseProcess(model);
    }
    /**
     * 9.密码格式错误
     */
//    public void passwordWrong(){
//        model=new RegisterModel(new int[]{0,2,3,4,5,6,8});
//        model.setMobileNum(account);
//        model.setVerificationCode("123456");
//        model.setPassword("1234");
//        model.setSearchText(Msg.passwordWrong);
//        super.baseProcess(model);
//    }

    /**
     * 10.密码为空
     */
    public void passwordNone(){
        model=new RegisterModel(new int[]{0,1,2,3,4,5,6,8});
        model.setMobileNum(account);
        model.setVerificationCode("123456");
        model.setPassword("");
        model.setSearchText(Msg.PasswordNone);
        super.baseProcess(model);
    }
    /**
     * 11.没有勾选注册复选框
     */
    public void noClickSelectedPart(){
        model=new RegisterModel(new int[]{0,1,2,3,5,6});
        model.setMobileNum(account);
        model.setVerificationCode("123456");
        model.setPassword(rightPassword);
        super.baseProcess(model);
    }
    /**
     * 12.账号注册--查看“条款与免责协议”页面
     */
    public void setServer(){
        model=new RegisterModel(new int[]{5});
        super.baseProcess(model);
    }
    /**
     * 13.手机号已经被注册,点击去登陆
     */
    public void mobileNumUsed1(){
        model=new RegisterModel(new int[]{0,1,10});
        model.setMobileNum(AccountInfo.Account);
        model.setIndex(1);
        super.baseProcess(model);
    }
    /**
     * 14.账号注册---手机号注册验证码超时
     */
    public void timeOut(){
        model=new RegisterModel(new int[]{0,1,11,8});
        model.setMobileNum(account);
        model.setSearchText(Msg.reSend);
        super.baseProcess(model);
    }

    /**
     * 15.注册密码,错误校验
     */
    public void registerCheckFail(String password, String msg) {
        model = new RegisterModel(new int[]{0 ,2, 3, 4, 6, 8});
        model.setMobileNum(account);
        model.setVerificationCode("123456");
        model.setPassword(password);
        model.setSearchText(msg);
        super.baseProcess(model);
    }

    /**
     * 16.账号注密码注册成功校验
     * 在没有注册的手机号时，密码验证成功后，会提示验证码失效。暂时使用验证码失效作为注册成功的提示
     * 暂时省略了1,7，获取验证码和进入首页的步骤
     */
    public void registerCheckSuccess(String password){
        model =new RegisterModel(new int[]{0,2,3,4,6,8});
        model.setMobileNum(account);
        model.setVerificationCode("123456");
        model.setPassword(password);
        model.setSearchText(Msg.VerCodefailure);
        super.baseProcess(model);
    }

}
