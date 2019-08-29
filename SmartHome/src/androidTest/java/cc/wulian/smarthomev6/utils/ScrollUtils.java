package cc.wulian.smarthomev6.utils;

import cc.wulian.smarthomev6.app.DragLocation;
import cc.wulian.smarthomev6.app.ScrollInfo;
import com.wtt.frame.robotium.By;
import com.wtt.frame.robotium.Solo;
import com.wtt.frame.robotium.WebElement;

/**
 * Created by 赵永健 on 2017/6/20.
 *
 * 封装网页滚动的操作
 */
public class ScrollUtils {

	public static final int TYPE_SET_TIME = 0;
	public static final int TYPE_SET_DELAY = 1;
	public static final int TYPE_SET_CONDITION = 2;

	private Solo solo;

	public ScrollUtils(Solo solo) {
		this.solo = solo;
	}

	public void scroll(String key, int index, int count) {
		if (ScrollInfo.isFirst(key)) { //是否包含这个值
			getScrollElements("00", "01", index, key);
		}
		scroll(ScrollInfo.getDragLocation(key), count, 20);
	}

	/**
	 * 温度
	 * @param key
	 * @param index
	 * @param count
	 */
	public void scroll1(String key, int index, int count) {
		if (ScrollInfo.isFirst(key)) { //是否包含这个值
			getScrollElements("-20", "-19", index, key);
		}
		scroll(ScrollInfo.getDragLocation(key), count, 20);
	}

	/**
	 * 湿度
	 * @param key
	 * @param index
	 * @param count
	 */
	public void scroll2(String key, int index, int count) {
		if (ScrollInfo.isFirst(key)) { //是否包含这个值
			getScrollElements("0", "1", index, key);
		}
		scroll(ScrollInfo.getDragLocation(key), count, 20);
	}

	/**
	 * Year
	 * @param key
	 * @param index
	 * @param count
	 */
	public void scroll3(String key, int index, int count) {
		if (ScrollInfo.isFirst(key)) { //是否包含这个值
			getScrollElements("2017", "2018", index, key);
		}
		scroll(ScrollInfo.getDragLocation(key), count, 20);
	}

	private void scroll(DragLocation dragLocation, int count, int stepCount) {
		for (int i = 0; i < count; i++) {
			solo.drag(dragLocation.fromX, dragLocation.toX, dragLocation.fromY, dragLocation.toY, stepCount);
		}
	}

	private void getScrollElements(String to, String from, int index, String key) {
		WebElement toEl = solo.getWebElement(By.textContent(to), index);
		WebElement fromEl = solo.getWebElement(By.textContent(from), index);

		DragLocation dragLocation = new DragLocation();
		dragLocation.fromX = toEl.getLocationX();
		dragLocation.toX = dragLocation.fromX;
		dragLocation.fromY = fromEl.getLocationY();
		dragLocation.toY = toEl.getLocationY();
		dragLocation.isFirst = false;

		ScrollInfo.replace(key, dragLocation);
	}

	public void scrollInTiming(int count, int index) {
		for (int i = 0; i < count; i++) {
			String to = (i < 10 ? "0" : "") + Integer.toString(i);
			String from = ((i + 1) < 10 ? "0" : "") + Integer.toString(i + 1);
			scrollTimeInWebPage(to, from, index, 20);
		}
	}

	private void scrollTimeInWebPage(String to, String from, int index, int stepCount) {
		WebElement toEl = solo.getWebElement(By.textContent(to), index);
		WebElement fromEl = solo.getWebElement(By.textContent(from), index);
		solo.drag(toEl.getLocationX(), toEl.getLocationX(), fromEl.getLocationY(), toEl.getLocationY(), stepCount);
	}
}
