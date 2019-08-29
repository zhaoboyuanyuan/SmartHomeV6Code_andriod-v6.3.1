package cc.wulian.smarthomev6.proc;

import android.app.Instrumentation;
import android.graphics.Bitmap;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import cc.wulian.smarthomev6.app.Activities;
import cc.wulian.smarthomev6.app.ActivitiesName;
import cc.wulian.smarthomev6.model.VisitModel;
import cc.wulian.smarthomev6.utils.MessageUtils;
import com.wtt.frame.robotium.Solo;

import java.util.Arrays;

/**
 * Created by 赵永健 on 2017/5/8.
 *
 * 游客模式登录流程
 */
public class VisitProc extends BaseProc<VisitModel> {
	
	private Instrumentation inst;
	
	public VisitProc(Solo solo) {
		super(solo);
	}
	
	public void loginByVisitor() {
		baseProcess(new VisitModel(new int[] {15}));
	}
	
	public void checkNoticeByVisitor() {
		baseProcess(new VisitModel(new int[] {0}));
	}
	
	public void clickToMessageCenterByVisitor() {
		baseProcess(new VisitModel(new int[] {4, 14}));
	}
	
	public void clickToScenesByVisitor() {
		baseProcess(new VisitModel(new int[] {5, 14}));
	}
	
	public void clickToDeviceByVisitor() {
		baseProcess(new VisitModel(new int[] {6, 14}));
	}
	
	public void clickToFindByVisitor() {
		baseProcess(new VisitModel(new int[] {7}));
	}
	
	public void clickToMineByVisitor() {
		baseProcess(new VisitModel(new int[] {8}));
	}
	
	public void clickToLoginByVisitor() {
		baseProcess(new VisitModel(new int[] {8, 9, 14}));
	}
	
	public void clickToGatewayCenterByVisitor() {
		baseProcess(new VisitModel(new int[] {8, 10, 14}));
	}
	
	public void clickToCustomerServiceByVisitor() {
		baseProcess(new VisitModel(new int[] {8, 11, 14}));
	}
	
	public void clickToMyManagerByVisitor() {
		baseProcess(new VisitModel(new int[] {8, 12, 14}));
	}
	
	public void clickBySettingsByVisitor() {
		baseProcess(new VisitModel(new int[] {8, 13, 14}));
	}
	
	public void addShortcutByVisitor() {
		baseProcess(new VisitModel(new int[] {1, 3, 2}));
	}
	
	public void reduceShortcutByVisitor() {
		baseProcess(new VisitModel(new int[] {1, 2, 3}));
	}
	
	@Override
	public void process(VisitModel model, int action) {
		switch (action) {
			case 0:
				checkAdvertising();
				break;
			case 1:
				clickToHomeEdit();
				break;
			case 2:
				if (!addShortcut()) return;
				break;
			case 3:
				if (!deleteShortcut()) return;
				break;
			case 4:
				clickMessageCenter();
				break;
			case 5:
				clickScenes();
				break;
			case 6:
				clickDevice();
				break;
			case 7:
				clickFind();
				break;
			case 8:
				clickMine();
				break;
			case 9:
				clickLoginOrRegister();
				break;
			case 10:
				clickGatewayCenter();
				break;
			case 11:
				clickService();
				break;
			case 12:
				clickManager();
				break;
			case 13:
				clickSetting();
				break;
			case 14:
				commonProc.waitForLogin();
				break;
			case 15:
				isLoginActivity();
				break;
		}
	}
	
	@Override
	public void init() {
		commonProc.loginInit();
		solo.goBack();
		solo.clickOnView(commonProc.getNavigationChild(0));
	}
	
	private void clickToHomeEdit() {
		click.clickToAnotherActivity("item_change"
				, ActivitiesName.HomeEditActivity
				, Msg.IntoHomeEditFailed);
	}
	
	private void clickMessageCenter() {
		solo.clickOnView(getter.getView("base_img_right"));
	}
	
	private void clickScenes() {
		solo.clickOnView(getScenes(0));
	}
	
	private void clickDevice() {
		solo.clickOnView(commonProc.getNavigationChild(1));
	}
	
	private void clickFind() {
		solo.clickOnView(commonProc.getNavigationChild(3));
	}
	
	private void clickMine() {
		solo.clickOnView(commonProc.getNavigationChild(4));
	}
	
	private void clickLoginOrRegister() {
		commonProc.scrollUpInMine();
		solo.clickOnView(getter.getView("item_account_login"));
	}
	
	private void clickGatewayCenter() {
		solo.clickOnView(getter.getView("item_gateway_center"));
	}
	
	private void clickService() {
		solo.clickOnView(getter.getView("item_customer_service"));
	}
	
	private void clickManager() {
		solo.clickOnView(commonProc.getNavigationChild(3));
	}
	
	private void clickSetting() {
		commonProc.scrollDownInMine();
		solo.clickOnView(getter.getView("item_setting"));
	}
	
	private void backToHome() {
		commonProc.clickToLogin("btn_left");
	}
	
	private void sleep(int timeout) {
		solo.sleep(timeout);
	}
	
	private ViewPager getAdvertising() {
		return (ViewPager) getter.getView("pager_banner");
	}
	
	private ImageView getAdvertisingChild(int index) {
		int indexTemp;
		if (0 > index) indexTemp = 0;
		else if (3 < index) indexTemp = 3;
		else indexTemp = index;
		
		ViewPager viewPager = getAdvertising();
		if (null == viewPager) return null;
		
		switch (indexTemp) {
			case 0:
				return (ImageView) viewPager.getChildAt(0);
			case 1:
				return (ImageView) viewPager.getChildAt(1);
			case 2:
				return (ImageView) viewPager.getChildAt(2);
			case 3:
				return (ImageView) viewPager.getChildAt(3);
			default:
				return null;
		}
	}
	
	public void  checkAdvertising() {
		solo.scrollToTop();
		ViewPager advertising = getAdvertising();
		if (null == advertising) return;
		
		ImageView one = (ImageView) getAdvertisingChild(0);
		if (null == one) {
			MessageUtils.append(Msg.GetAdvertisingChildFailedIndex0);
			return;
		}
		
		if (!one.isClickable()) {
			MessageUtils.append(Msg.AdvertisingCanClick);
			return;
		}
		
		solo.sleep(3000);
		ImageView two = (ImageView) getAdvertisingChild(1);
		if (null == two) {
			MessageUtils.append(Msg.GetAdvertisingChildFailedIndex1);
			return;
		}
		
		CheckAdvertisingImage checkAdvertisingImage = new CheckAdvertisingImage(one, two);
		solo.getCurrentActivity().runOnUiThread(checkAdvertisingImage);
		while (true) {
			if (checkAdvertisingImage.isRunning()) {
				break;
			}
		}
	}
	
	private class CheckAdvertisingImage implements Runnable {
		
		private boolean running = false;
		private ImageView one;
		private ImageView two;
		
		public CheckAdvertisingImage(ImageView one, ImageView two) {
			this.one = one;
			this.two = two;
		}
		
		@Override
		public void run() {
			one.setDrawingCacheEnabled(true);
			int[] pixEnd = getBitMapPixels(Bitmap.createBitmap(one.getDrawingCache()));
			one.setDrawingCacheEnabled(false);
			Arrays.sort(pixEnd);
			
			two.setDrawingCacheEnabled(true);
			int[] pixCurr = getBitMapPixels(Bitmap.createBitmap(two.getDrawingCache()));
			two.setDrawingCacheEnabled(false);
			Arrays.sort(pixCurr);
			
			if (Arrays.equals(pixEnd, pixCurr)) {
				MessageUtils.append(Msg.NotChangeAdvertising);
			}
			running = true;
		}
		
		public boolean isRunning() {
			return running;
		}
	}
	
	private boolean deleteShortcut() {
		ListView listView = getHasAddList();
		if (null == listView || !listView.isShown()) {
			MessageUtils.append(Msg.GetHasAddListFailed);
			return false;
		}
		
		solo.clickOnView(listView.getChildAt(0));
		ViewGroup viewGroup = getWillAddList();
		if (null == viewGroup) {
			MessageUtils.append(Msg.GetWillAddListFailedAfterDel);
			return false;
		}
		if (!viewGroup.isShown()) {
			MessageUtils.append(Msg.WillAddListNotShownAfterDel);
			return false;
		}
		
		return true;
	}
	
	private boolean addShortcut() {
		ImageView imageView = getAddIconInWillAddList();
		if (null == imageView || !imageView.isShown()) {
			MessageUtils.append(Msg.GetAddIconFailed);
			return false;
		}
		
		solo.clickOnView(imageView);
		ListView listView = getHasAddList();
		if (null == listView) {
			MessageUtils.append(Msg.GetHasAddListFailedAfterDel);
			return false;
		}
		if (!listView.isShown()) {
			MessageUtils.append(Msg.HasAddListNotShownAfterDel);
			return false;
		}
		
		return true;
	}
	
	private ListView getHasAddList() {
		return (ListView) getter.getView("list_drag_the_item");
	}
	
	private ViewGroup getWillAddList() {
		return (ViewGroup) getter.getView("home_edit_add_recyclerview");
	}
	
	private ImageView getAddIconInWillAddList() {
		ViewGroup viewGroup = getWillAddList();
		if (null == viewGroup || 0 == viewGroup.getChildCount()) return null;
		
		return (ImageView) viewGroup.getChildAt(0);
	}
	
	private ViewGroup getScenesArea() {
		return (ViewGroup) getter.getView("home_scene_recyclerview");
	}
	
	public View getScenes(int index) {
		int indexTemp = 0 > index ? 0 : index;
		ViewGroup viewGroup = getScenesArea();
		if (null == viewGroup || !viewGroup.isShown()) return null;
		
		return viewGroup.getChildAt(indexTemp);
	}
	
	private int[] getBitMapPixels(Bitmap bitmap) {
		int[] pixels = new int[bitmap.getWidth()*bitmap.getHeight()];
		bitmap.getPixels(pixels,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
		
		return pixels;
	}
	
	private void isLoginActivity() {
		if (!Activities.HomeActivity.equals(solo.getCurrentActivity().getClass().getName()))
			MessageUtils.append(Msg.NotCurrLoginActivity);
	}
	
	private static final class Msg {
		public static final String IntoHomeEditFailed = "未进入快捷方式界面！";
		public static final String IntoMessageCenterFailed = "进入消息中心失败！";
		public static final String IntoAllSceneFailed = "进入全部场景界面失败！";
		public static final String GetAdvertisingFailed = "获取广告图片失败！";
		public static final String GetHasAddListFailed = "获取已添加的快捷方式列表失败！";
		public static final String GetWillAddListFailedAfterDel = "删除快捷方式后，获取待添加的快捷方式列表失败！";
		public static final String WillAddListNotShownAfterDel = "删除快捷方式后，待添加的快捷方式列表未展示！";
		public static final String GetAddIconFailed = "获取已添加的快捷方式列表中的添加按钮失败！";
		public static final String GetHasAddListFailedAfterDel = "删除快捷方式后，获取已添加的快捷方式列表失败！";
		public static final String HasAddListNotShownAfterDel = "删除快捷方式后，已添加的快捷方式列表未展示！";
		public static final String GetAdvertisingChildFailedIndex0 = "获取广告图片失败！index - 0！";
		public static final String AdvertisingCanClick = "广告可以点击!";
		public static final String GetAdvertisingChildFailedIndex1 = "获取广告图片失败！index - 1！";
		public static final String NotChangeAdvertising = "3秒钟未切换广告!";
		public static final String NotCurrLoginActivity = "当前界面不是'登录'界面！";
	}
}
