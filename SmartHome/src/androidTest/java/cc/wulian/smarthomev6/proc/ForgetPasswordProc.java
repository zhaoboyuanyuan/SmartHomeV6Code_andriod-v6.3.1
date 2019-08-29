package cc.wulian.smarthomev6.proc;

import cc.wulian.smarthomev6.app.AccountInfo;
import cc.wulian.smarthomev6.app.ActivitiesName;
import cc.wulian.smarthomev6.app.ControlInfo;
import cc.wulian.smarthomev6.app.PasswordChecking;
import cc.wulian.smarthomev6.model.ForgetPasswordModel;
import cc.wulian.smarthomev6.utils.EnterUtils;
import cc.wulian.smarthomev6.utils.MessageUtils;
import com.wtt.frame.robotium.Solo;

/**
 * Created by 赵永健 on 2017/12/19.
 * 忘记密码
 */
public class ForgetPasswordProc extends BaseProc<ForgetPasswordModel> {
    private ForgetPasswordModel model;
    private String verificationCode="123456";
    public ForgetPasswordProc(Solo solo) {
        super(solo);
    }

    public void passwordFailed(String password,String message){
        model=new ForgetPasswordModel(new int[]{0,1,2,9,3,4,5,6,7});
        model.setMobileNumber(AccountInfo.Account1);
        model.setVerificationCode(verificationCode);
        model.setPassword(password);
        model.setSearchText(message);
        baseProcess(model);
    }

    public void passwordSuccess(String password){
        model=new ForgetPasswordModel(new int[]{0,1,2,9,3,4,5,6,7});
        model.setMobileNumber(AccountInfo.Account1);
        model.setVerificationCode(verificationCode);
        model.setPassword(password);
        model.setSearchText("手机验证码失效");
        baseProcess(model);
    }

    /**
     * 1.新密码设置成功
     */
    public void setPasswordSuccess(){
        model = new ForgetPasswordModel(new int[]{0,1,2,9,3,4,5,6});
        model.setMobileNumber(AccountInfo.Account1);
        model.setVerificationCode(verificationCode);
        model.setPassword("a123456789");
        baseProcess(model);
    }

    /**
     * 2.新密码设置失败--手机号格式不正确
     */
    public void mobileNumberWrong(){
        model = new ForgetPasswordModel(new int[]{0,1,2,7});
        model.setMobileNumber("123");
        model.setSearchText(Msg.MobileNumberFailed);
        baseProcess(model);
    }

    /**
     * 3.手机号未注册
     */
    public void mobileNumberNotExit(){
        model = new ForgetPasswordModel(new int[]{0,1,2,7});
        model.setMobileNumber("13851493888");
        model.setSearchText(Msg.UserNull);
        baseProcess(model);
    }

    /**
     * 4.验证码不正确
     */
    public void verificationWrong(){
        model = new ForgetPasswordModel(new int[]{0,1,2,9,3,7});
        model.setMobileNumber(AccountInfo.Account1);
        model.setVerificationCode("222222");
        model.setSearchText(Msg.verificationFailed);
        baseProcess(model);
    }

    /**
     * 5.输入5位密码
     */
    public void passwordLessThan8Char(){
        passwordFailed(PasswordChecking.Password1, Msg.passwordLess6Buffer);
    }

    /**
     * 6.6位纯数字
     */
    public void passwordAsNoMatch(){
        passwordFailed(PasswordChecking.Password2, Msg.passwordLess6Buffer);
    }
    /**
     * 7.密码中含有空格
     */
    public void passwordAsIllegalChar(){
        passwordFailed(PasswordChecking.Password3, Msg.PasswordAsIllegalChar);
    }
    /**
     * 8.密码强度弱
     */
    public void passwordAsWeak(){
        passwordFailed(PasswordChecking.Password4, Msg.PasswordAsWeak);
    }
    /**
     * 9.密码中含有非法字符（合法字符为西文键盘符号）
     */
    public void passwordAsIllegalChar1(){
        passwordFailed(PasswordChecking.Password5, Msg.PasswordAsIllegalChar);
    }
    /**
     * 10.6位纯字母
     */
    public void passwordAsNoMatch1(){
        passwordFailed(PasswordChecking.Password6, Msg.passwordLess6Buffer);
    }
    /**
     * 11.6位纯字符
     */
    public void passwordAsNoMatch2(){
        passwordFailed(PasswordChecking.Password7, Msg.passwordLess6Buffer);
    }


    /**
     * 12.10位密码，数字加字母
     */
    public void changePasswordAsSuccess(){
        passwordSuccess(PasswordChecking.Password8);
    }
    /**
     * 13.10位密码，数字加字符
     */
    public void changePasswordAsSuccess1(){
        passwordSuccess(PasswordChecking.Password9);
    }
    /**
     * 14.10位密码，数字加字符加字母
     */
    public void changePasswordAsSuccess2(){
        passwordSuccess(PasswordChecking.Password10);
    }
    /**
     * 15.15位密码，数字加字符加字母
     */
    public void changePasswordAsSuccess3(){
        passwordSuccess(PasswordChecking.Password11);
    }
    /**
     * 16.16位密码，数字加字符加字母
     */
    public void changePasswordAsSuccess4(){
        passwordSuccess(PasswordChecking.Password12);
    }
    /**
     * 17.20位密码，数字加字符加字母
     */
    public void changePasswordAsSuccess5(){
        passwordSuccess("1111122222aaaaabbbbb");
    }
    /**
     * 18.含有数字和符号两种，密码长度为6到16位
     */
    public void changePasswordAsSuccess6(){
        passwordSuccess(PasswordChecking.Password14);
    }
    /**
     * 19.含有字母和符号两种，密码长度为6到16位
     */
    public void changePasswordAsSuccess7(){
        passwordSuccess(PasswordChecking.Password15);
    }

    /**
     * 20.含有8位纯数字
     */
    public void passAsEight(){
        passwordFailed(PasswordChecking.Password18, Msg.PasswordNoMatch);
    }


    @Override
    public void process(ForgetPasswordModel model, int action) {
        switch (action){
            case 0:
                clickForget();
                break;
            case 1:
                enterNum(model.getMobileNumber());
                break;
            case 2:
                clickNext();
                break;
            case 3:
                enterVerification(model.getVerificationCode());
                break;
            case 4:
                if(!waitForSetPasswordActivity()) return;
                break;
            case 5:
                enterNewPassword(model.getPassword());
                break;
            case 6:
                clickFinish();
                break;
            case 7:
                if(!solo.searchText(model.getSearchText())){
                    MessageUtils.append("未显示"+model.getSearchText()+"!");
                }
                break;
            case 8:
                commonProc.waitForLogin(5000);
                break;
            case 9:
                if(!commonProc.waitForForgotVerificationActivity(5000)) return;
                break;

        }
    }

    @Override
    public void init() {
        commonProc.login(AccountInfo.Account1, AccountInfo.Password1);
        commonProc.quitLogin();
    }

    public class Msg{
        public static final String intoForgotAccountActivityFailed="进入忘记密码页面失败";
        public static final String ForgotSetPasswordActivityFailed="进入设置新密码页面失败";
        public static final String MobileNumberFailed="请输入正确的邮箱或手机号";
        public static final String UserNull="用户不存在";
        public static final String passwordLess6Buffer="密码长度不能小于8个字符";
        public static final String PasswordNoMatch="密码至少有数字、字母或符号的2种组合";
        public static final String PasswordAsIllegalChar="密码含非法字符或空格，请重新输入";
        public static final String PasswordAsWeak="数字和字母组合，密码长度至少10位";
        public static final String verificationFailed="验证码错误";
    }

    public void clickForget(){
        click.clickToAnotherActivity(ControlInfo.login_error, ActivitiesName.ForgotAccountActivity,
                Msg.intoForgotAccountActivityFailed);
    }

    public void enterNum(String number){
        enter.enterText(ControlInfo.et_account,number);
    }

    public void clickNext(){
//        return click.clickToAnotherActivity("v6.tv_get_verification",ActivitiesName.ForgotVerificationActivity,
//                Msg.intoForgotVerificationActivityFailed);
        solo.clickOnView(getter.getView(ControlInfo.tv_get_verification));
    }

    public void enterVerification(String verification){
        solo.clickOnView(getter.getView(ControlInfo.tv_verification_1));
        int [] code=new int[6];
        for(int i = 0; i<code.length;i++){
            code[i]=Character.getNumericValue(verification.charAt(i))+7;
            solo.sendKey(code[i]);
        }
    }

    public boolean waitForSetPasswordActivity(){
        return waitFor.waitFor(ActivitiesName.ForgotSetPasswordActivity, 5000,
                Msg.ForgotSetPasswordActivityFailed);
    }

    public void enterNewPassword(String password){
        enter.enterTextAsCopy(ControlInfo.et_pwd,password);
    }

    /**
     * 点击完成
     */
    public void clickFinish(){
        solo.clickOnView(getter.getView(ControlInfo.tv_sure));
    }


}
