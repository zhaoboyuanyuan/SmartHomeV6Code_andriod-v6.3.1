package com.wtt.runner.android;

//import cc.wulian.smarthomev6.app.Common;
import cc.wulian.smarthomev6.app.StartApp;
import com.wtt.frame.robotium.By;
import com.wtt.frame.robotium.Solo;

/**
 * Created by Administrator on 2017/12/27.
 */
public class deviceTest extends StartApp {
    private Solo solo;

    @Override
    public void setUp() throws Exception {
        solo=new Solo(getInstrumentation(),getActivity());
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testDevice(){
//        common.Login("15951644332","123456abcd");
        solo.clickOnText("设备");
        solo.clickOnText("红外入侵");
        assertTrue("未进入设备详情页面",solo.waitForActivity("DeviceDetailActivity",3000));
        solo.clickOnWebElement(solo.getWebElement(By.xpath("//*[@id=\'UnDefense\']"),0));
    }
}
