package com.wtt.runner.android;

import android.widget.EditText;
import cc.wulian.smarthomev6.app.Common;
import cc.wulian.smarthomev6.app.StartApp;
import com.wtt.frame.robotium.Solo;

/**
 * Created by 赵永健 on 2017/12/25.
 */
public class AloginTest extends StartApp {
    private Solo solo;
    private Common common;

    @Override
    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(),getActivity());
        common =new Common(solo);
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testLoginSuccess() {
        solo.clickOnText("设备");

        assertTrue("未进入登录页面", solo.waitForActivity("SigninActivity", 3000));

        if(!solo.waitForActivity("SigninActivity", 3000)){
            solo.takeScreenshot();
            assertTrue("未进入登录页面",false);
        }

        EditText userName = (EditText) solo.getView("username");
        EditText password = (EditText) solo.getView("password");

        solo.clearEditText(userName);
        solo.typeText(userName, "15951644332");
        solo.typeText(password, "123456abcd");
        solo.clickOnButton("登录");
        solo.sleep(3000);
    }
//        common.Login("15951644332","123456abcd");
//    }
}
