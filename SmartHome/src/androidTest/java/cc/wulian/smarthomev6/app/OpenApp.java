package cc.wulian.smarthomev6.app;

import android.test.ActivityInstrumentationTestCase2;
import cc.wulian.smarthomev6.main.welcome.SplashActivity;
import com.wtt.frame.robotium.Solo;

/**
 * Created by 赵永健 on 2018/1/23.
 * APP的另一种启动方式,反射
 */
public class OpenApp extends ActivityInstrumentationTestCase2{
    public static final String LAUNCHER_ACTIVITY_FULL_CLASSNAME=Activities.SplashActivity;
    private Solo solo;
    private static Class<?> launcherActivityClass;

    public Solo getSolo() {
        return solo;
    }

    static{
        //通过反射的方式获取的
        try {
            launcherActivityClass = Class.forName(LAUNCHER_ACTIVITY_FULL_CLASSNAME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public OpenApp() {
        super(launcherActivityClass);
    }


    @Override
    public void setUp() throws Exception {
        super.setUp();
        solo=new Solo(getInstrumentation(),getActivity());
        assertTrue("未启动APP",solo.waitForActivity("SplashActivity",3000));
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
        solo=null;
        super.tearDown();
    }

//    public void testLogin(){
//        if(solo.waitForText("我的")){
//            solo.clickOnText("我的");
//        }
//    }

}
