package cc.wulian.smarthomev6.utils;

import android.view.View;
import com.wtt.frame.robotium.Solo;
import com.wtt.frame.robotium.WebElement;

import static junit.framework.Assert.assertTrue;

/**
 * Created by 赵永健 on 2017/5/8.
 *
 * 封装点击操作基础类
 */
public class ClickUtils {

	private Solo solo;
	private GetterUtils getter;
	private WaitForUtils waitFor;

	public ClickUtils(Solo solo, GetterUtils getter,WaitForUtils waitFor) {
		this.solo = solo;
		this.getter = getter;
		this.waitFor = waitFor;
	}

    public void clickToAnotherActivity(String id,String activityName,String message){
        solo.clickOnView(getter.getView(id));
        assertTrue(message,solo.waitForActivity(activityName,3000));
    }

//	public boolean clickToAnotherActivity(String id, String activity, String message) {
//		return clickToAnotherActivity(id, 0, false, activity, 5000, message);
//	}
//
//	public boolean clickToAnotherActivity(String id, int index, String activity, String message) {
//		return clickToAnotherActivity(id, index, false, activity, 5000, message);
//	}
//
//
//	public boolean clickToAnotherActivity(String id, int index, boolean immediately, String activity
//			, int timeout, String message) {
//		View view = getter.getView(id, index);
//
//		return clickToAnotherActivity(view, immediately, activity, timeout, message);
//	}
//
	public boolean clickToAnotherActivity(View view, String activity, String message) {
		return clickToAnotherActivity(view, false, activity, 5000, message);
	}

	public boolean clickToAnotherActivity(View view, boolean immediately, String activity
			, int timeout, String message) {
		if (null == view) return false;
		solo.clickOnView(view, immediately);

		return waitFor.waitFor(activity, timeout, message);
	}
//
//	public boolean clickToAnotherActivity(String id, int byType, String activity, int timeout, String message) {
//		WebElement webElement = getter.getWebElement(id, byType, 0);
//
//		return clickToAnotherActivity(webElement, activity, timeout, message);
//	}
//
//	public boolean clickToAnotherActivity(WebElement webElement, String activity, int timeout, String message) {
//		if (null == webElement) return false;
//		solo.clickOnWebElement(webElement);
//
//		return waitFor.waitFor(activity, timeout, message);
//	}
}
