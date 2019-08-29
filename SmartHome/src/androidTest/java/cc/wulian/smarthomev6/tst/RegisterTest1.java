package cc.wulian.smarthomev6.tst;

import cc.wulian.smarthomev6.app.AccountInfo;
import cc.wulian.smarthomev6.app.StartApp;
import cc.wulian.smarthomev6.proc.CpRegisterProc;
import cc.wulian.smarthomev6.proc.LoginProc;
//import cc.wulian.smarthomev6.proc.RegisterProc;
//import cc.wulian.smarthomev6.proc.model.RegisterModel;
//import cc.wulian.smarthomev6.runner.SmartHomeTestHelper;
import com.wtt.runner.android.IAfterCondition;
import com.wtt.runner.android.IBeforeCondition;

/**
* 正式环境没有验证码
* Created by 赵永健 on 2017/6/8.账号注册验证码无法获取
 * */
public class RegisterTest1 extends StartApp implements IAfterCondition,IBeforeCondition {

   private CpRegisterProc proc;

    @Override
    public void before() {
        proc = new CpRegisterProc(getSolo());
        proc.init();
    }
    @Override
   public void after() {
       if (null != proc) {
           try {
              proc.finalize();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
           proc = null;
       }
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

    /**
     * 1.账号（手机号）格式错误
     */
    public void testMobileNumWrong(){
        proc.mobileNumWrong();
    }
//    /**
//     * 2.账号注册---手机号注册密码成功
//     */
//    public void testRegisterSuccess(){
//        proc.registerSuccess();
//    }
//    /**
//     * 3.账号注册---手机号注册，密码20位字母
//     */
//    public void testRegisterSuccess1() {
//        proc.registerSuccess1();
//    }
//    /**
//     * 4.账号注册---手机号注册，密码13位字母和数字混合
//     */
//    public void testRegisterSuccess2() {
//      proc.registerSuccess2();
//    }
    /**
     * 5.手机号码为空
     */
    public void testMobileNoneNum(){
        proc.mobileNoneNum();
    }
    /**
     * 6.手机号已经被注册,点击取消
     */
    public void testMobileNumUsed(){
       proc.mobileNumUsed();
    }
    /**
     * 7.网络连接错误,手机要断网  7.0以上的安卓系统会弹出系统权限弹窗
    */
//    public void testNetError(){
//       proc.netError();
//    }
    /**
     * 8.手机验证码错误
     */
    public void testVerCodeError(){
        proc.VerCodeError();
    }
//    /**
//     * 9.密码格式错误
//     */
////    public void testPasswordWrong(){
////        proc.passwordWrong();
////   }
//    /**
//     * 10.密码为空
//     */
//    public void testPasswordNone(){
//        proc.passwordNone();
//    }
//    /**
//     * 11.没有勾选注册复选框
//    */
//    public void testNoClickSelectedPart(){
//        proc.noClickSelectedPart();
//    }
    /**
     * 12.账号注册--查看“条款与免责协议”页面
    */
    public void testSetServer() {
        proc.setServer();
    }
//    /**
//     * * 13.手机号已经被注册,点击去登陆
//     */
//    public void testMobileNumUsed1(){
//       proc.mobileNumUsed1();
//    }
    /**
     * 14.账号注册---手机号注册验证码超时
     */
    public void testTimeOut(){
        proc.timeOut();
    }

}
