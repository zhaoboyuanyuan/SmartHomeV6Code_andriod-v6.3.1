package cc.wulian.smarthomev6.utils;

import android.widget.EditText;
import com.wtt.frame.robotium.By;
import com.wtt.frame.robotium.Solo;
import com.wtt.frame.robotium.WebElement;

/**
 * Created by 赵永健 on 2017/5/8.
 *
 * 封装输入操作基础类
 */
public class EnterUtils {

	public static final int KEY_SUCCESS = 0;
	public static final int KEY_GET_VIEW_FAILED = 1;
	public static final int KEY_ENTER_DIFF = 2;

	private Solo solo;
	private GetterUtils getter;

	public EnterUtils(Solo solo, GetterUtils getter) {
		this.solo = solo;
		this.getter = getter;
	}

    /**
     * 原生页面输入
     * @param id
     * @param text
     */
    public void enterText(String id,String text){
        EditText editText=(EditText) solo.getView(id);
        solo.clearEditText(editText);
        solo.enterText(editText,text);
        if(!solo.searchText(text)) MessageUtils.append("输入错误！");
    }

    /**
     * 原生页面输入
     * @param id
     * @param text
     */
    public void enterText(String id,String text,String check){
        EditText editText=(EditText) solo.getView(id);
        solo.clearEditText(editText);
        solo.enterText(editText,text);
        if(!solo.searchText(check)) MessageUtils.append("检查输入错误！");
    }

    /**
     * 原生页面输入
     * @param id
     * @param text
     */
    public void enterTextAsCopy(String id,String text){
        EditText editText=(EditText) solo.getView(id);
        solo.clearEditText(editText);
        solo.enterText(editText,text);
    }

    public void enterTextAsType(String id,String text){
        EditText editText=(EditText) solo.getView(id);
        solo.clearEditText(editText);
        solo.typeText(editText,text);
    }

    /**
     * web页面输入
     * @param xpath
     * @param text
     */
    public void enterTextAsWeb(String xpath,String text){
//        WebElement webElement = solo.getWebElement(By.xpath(xpath),0);
//        solo.typeTextInWebElement(webElement,text);
        solo.enterTextInWebElement(By.xpath(xpath),text);
    }



    //	public int enterText(String id, String content) {
//		return enterText(id, content, true, 0 ,null);
//	}
//
//	/**
//	 *
//	 * @param xpath
//	 * @param content 自定义
//	 * @return
//	 */
//	public int enterText1(String xpath, String content) {
//		return enterText(xpath, content, true, 0 ,null);
//	}
//	private int enterText(String xpath, String content, boolean isCheck, int checkType, String checkContent) {
//		String contentTemp = null == content ? "" : content;
//		EditText editText = (EditText) getter.getView(xpath);
//		if (null == editText) return KEY_GET_VIEW_FAILED;
//		if (!editText.getText().toString().isEmpty()) solo.clearEditText(editText);
//
//		solo.enterText(editText, contentTemp);
//
//		if (isCheck) {
//			switch (checkType) {
//				case 0:
//					if (!contentTemp.equals(editText.getText().toString())) return KEY_ENTER_DIFF;
//					break;
//				case 1:
//					String temp = checkContent;
//					if (null == temp) temp = "";
//					if (!temp.equals(editText.getText().toString())) return KEY_ENTER_DIFF;
//					break;
//			}
//		}
//
//		return KEY_SUCCESS;
//	}
//
//	/**
//	 *
//	 * @param id
//	 * @param content
//	 * @param checkContent
//	 * @return
//	 */
//	public int enterText(String id, String content, String checkContent) {
//		return enterText(id, content, true, 1, checkContent);
//	}
//
//	private int enterText(String id, String content, boolean isCheck, int checkType, String checkContent) {
//		String contentTemp = null == content ? "" : content;
//		EditText editText = (EditText) getter.getView(id);
//		if (null == editText) return KEY_GET_VIEW_FAILED;
//		if (!editText.getText().toString().isEmpty()) solo.clearEditText(editText);
//
//		solo.enterText(editText, contentTemp);
//
//		if (isCheck) {
//			switch (checkType) {
//				case 0:
//					if (!contentTemp.equals(editText.getText().toString())) return KEY_ENTER_DIFF;
//					break;
//				case 1:
//					String temp = checkContent;
//					if (null == temp) temp = "";
//					if (!temp.equals(editText.getText().toString())) return KEY_ENTER_DIFF;
//					break;
//			}
//		}
//
//		return KEY_SUCCESS;
//	}
//
//	public int enterTextAsWeb(String id, String content) {
//		return enterTextAsWeb(id, GetterUtils.KEY_XPATH, 0, content, true, 0, null);
//	}
//
//	public int enterTextAsWeb(String id, String content, String check) {
//		return enterTextAsWeb(id, GetterUtils.KEY_XPATH, 0, content, true, 1, check);
//	}
//
//	public int enterTextAsWeb(String id, int byType, int index, String content
//			, boolean isCheck, int checkType, String checkContent) {
//		String contentTemp = null == content ? "" : content;
////		WebElement element = getter.getWebElement(id, byType, index);
////		if (null == element) return KEY_GET_VIEW_FAILED;
//		By by = getter.setBy(id, byType);
////		if (null == by) return KEY_GET_VIEW_FAILED;
//		solo.clearTextInWebElement(by);
//		solo.enterTextInWebElement(by, content);
//
//		if (isCheck) {
//			switch (checkType) {
//				case 0:
//					if (!contentTemp.equals(getter.getWebElement(id, byType, index).getText())) return KEY_ENTER_DIFF;
//					break;
//				case 1:
//					String temp = checkContent;
//					if (null == temp) temp = "";
//					if (!temp.equals(getter.getWebElement(id, byType, index).getText())) return KEY_ENTER_DIFF;
//					break;
//			}
//		}
//
//		return KEY_SUCCESS;
//	}
}
