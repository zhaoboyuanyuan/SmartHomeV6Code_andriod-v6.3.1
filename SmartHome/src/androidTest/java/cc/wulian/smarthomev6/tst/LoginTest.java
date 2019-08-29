package cc.wulian.smarthomev6.tst;

import cc.wulian.smarthomev6.app.StartApp;
import cc.wulian.smarthomev6.model.LoginModel;
import cc.wulian.smarthomev6.proc.LoginProc;
import com.wtt.runner.android.IAfterCondition;
import com.wtt.runner.android.IBeforeCondition;

/**
 * Created by 赵永健 on 2018/1/17.
 * 登录测试
 */
public class LoginTest  extends StartApp implements IBeforeCondition,IAfterCondition{
    private LoginProc proc;

    @Override
    public void before() {
        proc=new LoginProc(getSolo());
        proc.init();
    }

    @Override
    public void after() {
        if(proc!=null){
            try {
                proc.finalize();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        proc=null;
    }


    @Override
    public void setUp() throws Exception {
        super.setUp();
        before();
    }

    @Override
    public void tearDown() throws Exception {
        after();
        super.tearDown();
    }



    // 1.用户名为空
    public void testUserNameNull(){
        proc.userNameNull();
    }

    //  2.密码为空
    public void testPasswordNull(){
        proc.passwordNull();
    }

    //  3.用户不存在
    public void testUserNotExsit(){
       proc.userNotExsit();
    }

    //  4.错误的密码
    public void testWrongPassword() {
        proc.wrongPassword();
    }

//    //5.手机号登录，连续3次输错密码，不找回密码
//    public void testThreeSuccessiveErrorsAsCancel(){
//        proc.threeSuccessiveErrorsAsCancel();
//    }
//
//    //6.手机号登录，连续3次输错密码，找回密码
//    public void testThreeSuccessiveErrorsAsConfirm(){
//        proc.threeSuccessiveErrorsAsConfirm();
//    }

    //7.登录成功
    public void testLoginSuccess(){
        proc.loginSuccess();
    }

}
