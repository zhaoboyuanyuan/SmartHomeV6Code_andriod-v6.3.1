package cc.wulian.smarthomev6.utils;

import android.view.View;
import com.wtt.frame.robotium.By;
import com.wtt.frame.robotium.Solo;
import com.wtt.frame.robotium.WebElement;
import com.wtt.runner.android.InstTestRunner;
//import com.wtt.runner.android.InstTestRunner;

import java.util.ArrayList;

/**
 * Created by 赵永健 on 2017/5/8.
 *
 * 封装输入操作基础类
 */
public class GetterUtils {

	public static final int KEY_ID = 0;
	public static final int KEY_NAME = 1;
	public static final int KEY_CSS = 2;
	public static final int KEY_XPATH = 3;
	public static final int KEY_TAG_NAME = 4;
	public static final int KEY_TEXT = 5;
	public static final int KEY_CLASS_NAME = 6;

	private Solo solo;

	public GetterUtils(Solo solo) {
		this.solo = solo;
	}

	public View getView(String id, int index) {
		try {
			return solo.getView(id, index);
		}catch (Error E){
			return null;
		}
	}

	public View getView(String id) {
		return getView(id, 0);
	}

	public WebElement getWebElementByXpath(String xpath){
	    return solo.getWebElement(By.xpath(xpath),0);
    }

    public WebElement getWebElementByXpath(String xpath,int index){
	    return solo.getWebElement(By.xpath(xpath),index);
    }

	public WebElement findElementByText(String text, String xpath) {
		ArrayList<WebElement> webElements = getWebElementsByXpath(xpath);
		if (null == webElements || webElements.isEmpty()) return null;
		for (WebElement webElement : webElements) {
			if (!text.equals(webElement.getText())) continue;
			return webElement;
		}

		return null;
	}

	public WebElement findElementInList(String id, String text, String textXpath) {
		String xpath = getXpath(id, text, textXpath);

		return solo.getWebElement(By.xpath(xpath), 0);
	}

	public String getWebElementText(String id, String text, String textXpath) {
		WebElement webElement = findElementInList(id, text, textXpath);

		return null == webElement ? null : webElement.getText();
	}

	public String getXpath(String currXpath, String text, String textXpath) {
//		if (null == currXpath || currXpath.isEmpty()) return null;
		if (!currXpath.contains("#index")) return null;
		int index = getWebElementIndex(text, textXpath);
		if (-1 == index) return null;

		return currXpath.replace("#index", Integer.toString(index));
	}

	public int getWebElementIndex(String taskName, String xpath) {
		ArrayList<WebElement> webElements = getWebElementsByXpath(xpath);
		if (null == webElements || webElements.isEmpty()) return -1;
		for (WebElement webElement : webElements) {
			if (!taskName.equals(webElement.getText())) continue;
			return webElements.indexOf(webElement);
		}

		return -1;
	}

	public String getWebElementText(String id) {
		WebElement webElement = getWebElementByXpath(id);

		return null == webElement ? null : webElement.getText();
	}

	public ArrayList<WebElement> getWebElementsById(String id) {
		return getWebElements(id, KEY_ID);
	}

	public ArrayList<WebElement> getWebElementsByName(String id) {
		return getWebElements(id, KEY_NAME);
	}

	public ArrayList<WebElement> getWebElementsByCss(String id) {
		return getWebElements(id, KEY_CSS);
	}

	public ArrayList<WebElement> getWebElementsByXpath(String id) {
		return getWebElements(id, KEY_XPATH);
	}

	public ArrayList<WebElement> getWebElementsByTagName(String id) {
		return getWebElements(id, KEY_TAG_NAME);
	}

	public ArrayList<WebElement> getWebElementsByText(String id) {
		return getWebElements(id, KEY_TEXT);
	}

	public ArrayList<WebElement> getWebElementsByClassName(String id) {
		return getWebElements(id, KEY_CLASS_NAME);
	}

	public WebElement getWebElementById(String id) {
		return getWebElement(id, KEY_ID, 0);
	}

	public WebElement getWebElementByName(String id) {
		return getWebElement(id, KEY_NAME, 0);
	}

	public WebElement getWebElementByCss(String id) {
		return getWebElement(id, KEY_CSS, 0);
	}

//	public WebElement getWebElementByXpath(String id) {
//		return getWebElement(id, KEY_XPATH, 0);
//	}

	public WebElement getWebElementByTagName(String id) {
		return getWebElement(id, KEY_TAG_NAME, 0);
	}

	public WebElement getWebElementByText(String id) {
		return getWebElement(id, KEY_TEXT, 0);
	}

	public WebElement getWebElementByClassName(String id) {
		return getWebElement(id, KEY_CLASS_NAME, 0);
	}

	public WebElement getWebElementById(String id, int index) {
		return getWebElement(id, KEY_ID, index);
	}

	public WebElement getWebElementByName(String id, int index) {
		return getWebElement(id, KEY_NAME, index);
	}

	public WebElement getWebElementByCss(String id, int index) {
		return getWebElement(id, KEY_CSS, index);
	}

//	public WebElement getWebElementByXpath(String id, int index) {
//		return getWebElement(id, KEY_XPATH, index);
//	}

	public WebElement getWebElementByTagName(String id, int index) {
		return getWebElement(id, KEY_TAG_NAME, index);
	}

	public WebElement getWebElementByText(String id, int index) {
		return getWebElement(id, KEY_TEXT, index);
	}

	public WebElement getWebElementByClassName(String id, int index) {
		return getWebElement(id, KEY_CLASS_NAME, index);
	}

	public WebElement getWebElement(String id, int byType, int index) {
		By by = setBy(id, byType);
		if (null == by) {
			MessageUtils.append("Get by failed! id - \"" + id + "\"!");
			return null;
		}

		return solo.getWebElement(by, index);
	}

	public ArrayList<WebElement> getWebElements(String id, int byType) {
		By by = setBy(id, byType);
		if (null == by) {
			MessageUtils.append("Get by failed! id - \"" + id + "\"!");
			return null;
		}

		return solo.getWebElements(by);
	}

	public By setBy(String id, int byType) {
//		String content = InstTestRunner.widgets.get(id);
//		if (null == content || content.isEmpty()) return null;

		switch (byType) {
			case KEY_ID:
				return By.id(id);
			case KEY_NAME:
				return By.name(id);
			case KEY_CSS:
				return By.cssSelector(id);
			case KEY_XPATH:
				return By.xpath(id);
			case KEY_TAG_NAME:
				return By.tagName(id);
			case KEY_TEXT:
				return By.textContent(id);
			case KEY_CLASS_NAME:
				return By.className(id);
			default:
				return null;
		}
	}
}
