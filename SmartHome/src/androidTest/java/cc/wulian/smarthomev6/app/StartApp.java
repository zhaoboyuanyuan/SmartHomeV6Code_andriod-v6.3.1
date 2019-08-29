package cc.wulian.smarthomev6.app;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.OperationApplicationException;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import cc.wulian.smarthomev6.main.welcome.SplashActivity;
import cc.wulian.smarthomev6.support.utils.LogUtil;
import com.wtt.frame.robotium.By;
import com.wtt.frame.robotium.Solo;
import com.wtt.runner.android.InstTestRunner;

import junit.framework.AssertionFailedError;

/**
 * Created by 赵永健 on 2017/12/23.
 * 启动APP类
 */
public class StartApp extends ActivityInstrumentationTestCase2 <SplashActivity> {
    private Solo solo;
    public static StringBuilder builder;
    /**
     * 添加测试用例的执行过程中的错误信息
     *
     * @param msg - 测试用例的执行过程中的错误信息{@link java.lang.String}
     */
    public static synchronized void append(String msg) {
            builder.append(msg);
    }

    /**
     * 获取测试用例执行过程中所有的错误信息
     *
     * @return - 测试用例执行过程中所有的错误信息{@link java.lang.String}， 如果没有错误信息则返回{@code ''}
     */
    private static synchronized String getMessage() {
        return builder.toString();
    }

    /**
     * 判断错误信息是否为空
     *
     * @return - 没有错误信息返回{@code true}，否则返回{@code false}
     */
    private static synchronized boolean isEmpty() {
        return builder.toString().isEmpty();
    }


    public StartApp() {
        super("cc.wulian.smarthomev6", SplashActivity.class);
    }

    public Solo getSolo() {
        return solo;
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(),getActivity());
//        assertTrue("未启动APP",solo.waitForActivity("SplashActivity",3000));
        assertEquals("未启动APP",solo.waitForActivity("SplashActivity",3000),true);
        builder=new StringBuilder();
    }

    @Override
    public void tearDown() throws Exception {
//        Log.i("i","tearDown");
        if(solo!=null) {
            solo.sleep(2000);
            solo.finishOpenedActivities();
            try {
                solo.finalize();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            solo = null;
        }
        builder=null;
        super.tearDown();
    }

//    出现错误,重跑机制
//    @Override
//    public void runTest() throws Throwable {
//        int times = 2;//重试次数
//        while (true) {
//            try {
//                super.runTest();
//                Log.e("测试tag",this.getClass().getSimpleName() + "/"+getName()+":测试成功");
//                break;
//            } catch (Exception e) {//测试过程发生Exception后的处理
//                if (times > 0) {
//                    times--;
//                    tearDown();//释放资源
//                    Runtime run = Runtime.getRuntime();
//                    try {
//                        //启动被测app
//                        // run.exec("am start -a cc.wulian.smarthomev6.main -n cc.wulian.smarthomev6/cc.wulian.smarthomev6.main.welcome.SplashActivity");
//                        //拼接命令行，使其每次发生错误都能重跑发生错误的那条用例2次
//                        String c="cc.wulian.smarthomev6.tst."+getClass().getSimpleName()+"#"+getName();
//                        String commd="am instrument -w -r   -e debug false -e class"+" '"+ c +"'"+ " cc.wulian.smarthomev6.test/com.wtt.runner.android.InstTestRunner";
//                        run.exec(commd);
//                    } catch (Exception e1) {
//                        throw new RuntimeException(e1);
//                    }
//                    setUp();
//                    continue;
//                } else {
//                    solo.takeScreenshot(this.getClass().getSimpleName() + "/"+getName());
//                    Log.e("测试tag",this.getClass().getSimpleName() + "/"+ getName() + ":测试执行过程中出现异常");
//                    throw new RuntimeException(e);
//                }
//            } catch (AssertionFailedError e2) {
//                Log.e("测试tag", this.getClass().getSimpleName() + "/"+getName() + ":测试失败");
//                solo.takeScreenshot(this.getClass().getSimpleName() + "/"+getName());
////                throw new OperationErrorException(e2);
//                throw new OperationApplicationException(e2);
//            }
//        }
//    }

}
