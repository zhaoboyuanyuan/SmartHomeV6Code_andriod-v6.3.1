package cc.wulian.smarthomev6.proc;

import android.widget.*;
import cc.wulian.smarthomev6.app.AccountInfo;
import cc.wulian.smarthomev6.app.ControlInfo;
import cc.wulian.smarthomev6.model.DeviceSetPartitionModel;
import com.wtt.frame.robotium.Solo;

/**
 * Created by 赵永健 on 2017/5/15.
 *
 * 封装测试分区的测试流程
 */
public class DeviceSetPartitionProc extends BaseProc<DeviceSetPartitionModel> {
	
	private String deviceName = "红外入侵";
	
	public DeviceSetPartitionProc(Solo solo) {
		super(solo);
	}
	
	public void setPartition() {
		baseProcess(new DeviceSetPartitionModel(new int[] {0}));
	}
	
	@Override
	public void process(DeviceSetPartitionModel model, int action) {
		switch (action) {
			case 0:
				changePartition();
				break;
		}
	}
	
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	
	@Override
	public void init() {
		commonProc.login(AccountInfo.Account1, AccountInfo.Password1);
		commonProc.forceRefresh();
		solo.clickOnView(commonProc.getNavigationChild(1));
		solo.clickOnText(deviceName);
		clickToMore();
		solo.clickOnView(getter.getView(ControlInfo.item_device_more_area));
		if (!commonProc.waitForDetailMoreArea()) return;
	}
	
	private void changePartition() {
		int currentIndex;
		String currentName;
		ListView list = (ListView) getter.getView(ControlInfo.device_detail_more_area_list);
		currentIndex = getCurrentIndex(list);
		if (-1 == currentIndex) {
			currentIndex = 1;
		} else {
			currentIndex = (currentIndex += 1) > 3 ? 1 : currentIndex;
		}
		
		currentName = getPartitionName(getRelativeLayout(list, currentIndex)).getText().toString();
		solo.clickOnView(list.getChildAt(currentIndex));
		solo.clickOnView(getter.getView(ControlInfo.img_left));
		if (!commonProc.waitForDeviceDetail()) return;
		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_back));
//		if (!solo.searchText(currentName)) {
//			MessageUtils.append(Msg.ChangePartitionFailed);
//		}
	}
	
	private int getCurrentIndex(ListView listView) {
		int currentIndex = -1;
		for (int i = 0; i < listView.getChildCount(); i++) {
			RelativeLayout relativeLayout = getRelativeLayout(listView, i);
			ImageView flag = getFlagImage(relativeLayout);
			if (flag.isShown()) {
				currentIndex = i;
			}
		}
		return currentIndex;
	}

	private RelativeLayout getRelativeLayout(ListView listView, int index) {
		return (RelativeLayout) ((LinearLayout) listView.getChildAt(index)).getChildAt(0);
	}
	
	private ImageView getFlagImage(RelativeLayout relativeLayout) {
		return (ImageView) relativeLayout.getChildAt(1);
	}
	
	private TextView getPartitionName(RelativeLayout relativeLayout) {
		return (TextView) relativeLayout.getChildAt(0);
	}
	
	private static final class Msg {
		public static final String GetSelectedPartitionFailed = "获取已标记的分区！";
		public static final String ChangePartitionFailed = "设备列表未显示新分区！";
		public static final String IntoDeviceMoreFailed= "进入设备更多界面失败！";

	}

	public void clickToMore(){
		solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_more));
		if(!commonProc.waitForDeviceDetailMore()) return;
	}

}
