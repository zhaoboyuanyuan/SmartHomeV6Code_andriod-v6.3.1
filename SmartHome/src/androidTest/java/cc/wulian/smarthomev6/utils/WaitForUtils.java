package cc.wulian.smarthomev6.utils;

import com.wtt.frame.robotium.By;
import com.wtt.frame.robotium.Solo;

/**
 * Created by 赵永健 on 2017/5/8.
 *
 * 封装加载等待支持类
 */
public class WaitForUtils {

	private Solo solo;

	public WaitForUtils(Solo solo) {
		this.solo = solo;
	}

	public boolean waitFor(String activity, int timeout, String message) {
		if (null == activity || activity.isEmpty())return false;
		return waitFor(activity, timeout, message, false, null);
	}

	public boolean waitForAsWeb(By by, int timeout, String message) {
		if (null == by) return false;
		return waitFor(null, timeout, message, true, by);
	}

	public boolean waitFor(String activity, int timeout, String message, boolean isWeb, By by) {
		if (0 > timeout) timeout = 5000;
		if (null == message || message.isEmpty()) return false;

		if (!isWeb) {
			if (!solo.waitForActivity(activity, timeout)) {
				MessageUtils.append(message);
				return false;
			}
		} else {
			if (!solo.waitForWebElement(by, 0, timeout, true)) {
				MessageUtils.append(message);
				return false;
			}
		}
		return true;
	}
}
