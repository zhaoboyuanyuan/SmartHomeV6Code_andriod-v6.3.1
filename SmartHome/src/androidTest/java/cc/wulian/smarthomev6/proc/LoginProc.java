package cc.wulian.smarthomev6.proc;

import android.widget.EditText;
import android.widget.Switch;
import cc.wulian.smarthomev6.app.ControlInfo;
import cc.wulian.smarthomev6.model.LoginModel;
import cc.wulian.smarthomev6.utils.EnterUtils;
import cc.wulian.smarthomev6.utils.MessageUtils;
import com.wtt.frame.robotium.By;
import com.wtt.frame.robotium.Solo;

/**
 * Created by 赵永健 on 2018/1/17.
 */
public class LoginProc extends BaseProc<LoginModel>{
    public LoginProc(Solo solo) {
        super(solo);
    }

    public void loginSuccess(){
        LoginModel model=new LoginModel(new int[]{0,1,2,4});
        model.setUserName("15951644332");
        model.setPassword("123456abcd");
        baseProcess(model);
    }

    // 用户名为空
    public void userNameNull(){
        LoginModel model=new LoginModel(new int[]{0,1,5});
        model.setUserName("");
        model.setPassword("123456abcd");
        baseProcess(model);
    }
    //  密码为空
    public void passwordNull(){
        LoginModel model=new LoginModel(new int[]{0,1,5});
        model.setUserName("15951644332");
        model.setPassword("");
        baseProcess(model);
    }

    //  用户不存在
    public void userNotExsit(){
        LoginModel model=new LoginModel(new int[]{0,1,2,3});
        model.setUserName("13851493871");
        model.setPassword("123456abcd");
        model.setSearchText("用户不存在");
        baseProcess(model);
    }

    //  错误的密码
    public void wrongPassword(){
        LoginModel model=new LoginModel(new int[]{0,1,2,3});
        model.setUserName("15951644332");
        model.setPassword("123456aaaa");
        model.setSearchText("用户密码错误");
        baseProcess(model);
    }
    /**
     * 手机号登录，连续3次输错密码，不找回密码
     */
    public void threeSuccessiveErrorsAsCancel(){
        LoginModel model=new LoginModel(new int[]{6,7});
        model.setUserName("15951644332");
        model.setPassword("1234");
        model.setButtonText("取消");
        baseProcess(model);
    }

    /**
     * 手机号登录，连续3次输错密码，找回密码
     */
    public void threeSuccessiveErrorsAsConfirm(){
        LoginModel model=new LoginModel(new int[]{6,7,8});
        model.setUserName("15951644332");
        model.setPassword("1234");
        model.setButtonText("找回密码");
        baseProcess(model);
    }


    @Override
    public void process(LoginModel model, int action) {
        switch(action){
            case 0:
                enterUserName(model.getUserName());
                break;
            case 1:
                enterPassword(model.getPassword());
                break;
            case 2:
                clickLoginButton();
                break;
            case 3:
                if(!solo.searchText(model.getSearchText())){
                    MessageUtils.append("未找到"+model.getSearchText()+"!");
                }
                break;
            case 4:
                commonProc.waitForLogin();
                break;
            case 5:
                if(buttonClick()){
                    MessageUtils.append("按钮可点击");
                }
            case 6:
                burstError(model.getUserName(),model.getPassword());
                break;
            case 7:
                solo.clickOnButton(model.getButtonText());
                break;
            case 8:
                commonProc.waitForgotAccountActivity();
                break;
        }
    }

    @Override
    public void init() {
        commonProc.loginInit();
    }

    public void enterUserName(String userName){
        enter.enterText(ControlInfo.username,userName);
    }

    public void enterPassword(String password){
        enter.enterText(ControlInfo.password,password);
    }

    public void clickLoginButton(){
        solo.clickOnButton("登录");
    }

    public boolean buttonClick(){
        return solo.getView(ControlInfo.login).isEnabled();
    }

    public void burstError(String account,String password){
        enterUserName(account);
        for(int i=0;i<4;i++){
            enterPassword(password);
            clickLoginButton();
        }
        if(!solo.waitForDialogToOpen(3000)){
            MessageUtils.append("");
        }
    }


}
