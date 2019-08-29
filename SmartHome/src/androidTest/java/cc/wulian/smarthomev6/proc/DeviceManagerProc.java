package cc.wulian.smarthomev6.proc;

import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cc.wulian.smarthomev6.app.AccountInfo;
import cc.wulian.smarthomev6.app.ControlInfo;
import cc.wulian.smarthomev6.model.DeviceManagerModel;
import cc.wulian.smarthomev6.utils.MessageUtils;
import cc.wulian.smarthomev6.utils.RandomUtils;
import com.wtt.frame.robotium.Solo;

/**
 * Created by 赵永健 on 2017/5/16.
 *
 * 封装设备管理的测试流程
 */
public class DeviceManagerProc extends BaseProc<DeviceManagerModel> {
	
	private static final int KEY_CHANGE = 0;
	private static final int KEY_DELETE = 1;
	
	public DeviceManagerProc(Solo solo) {
		super(solo);
	}
	
	public void createAsLessThan15() {
		createPartition(Integer.toString(RandomUtils.randomFour()), "确定");
	}
	
	public void createAsMoreThan15() {
		String number = Integer.toString(RandomUtils.randomFour());
		DeviceManagerModel model = new DeviceManagerModel(new int[] {0, 1, 13, 3, 4, 14, 5,15});
		model.setPartitionName(number + "abbbbb1111122222c");
		model.setPartitionNameCheck(number + "abbbbb1111122222");
		model.setAction("创建");
		model.setCreateButtonText("确定");
		model.setDialogTitle("新建分区");
		
		baseProcess(model);
	}
	
	public void createAsCancel() {
		DeviceManagerModel model = new DeviceManagerModel(new int[] {0, 1, 2, 3, 4, 6});
		model.setPartitionName(Integer.toString(RandomUtils.randomFour()));
		model.setCreateButtonText("取消");
		model.setDialogTitle("新建分区");
		
		baseProcess(model);
	}
	
	public void createAsNameEmpty() {
		DeviceManagerModel model = new DeviceManagerModel(new int[] {0, 1, 2, 12});
		model.setPartitionName("");
		model.setAction("创建");
		model.setDialogTitle("新建分区");
		
		baseProcess(model);
	}
	
	private void createPartition(String partitionName, String buttonText) {
		DeviceManagerModel model = new DeviceManagerModel(new int[] {0, 1, 2, 3, 4,5});
		model.setPartitionName(partitionName);
		model.setAction("创建");
		model.setCreateButtonText(buttonText);
		model.setDialogTitle("新建分区");
		baseProcess(model);

	}

	private void createPartition1(String partitionName, String buttonText) {
		DeviceManagerModel model = new DeviceManagerModel(new int[] {0, 1, 2, 3,4});
		model.setPartitionName(partitionName);
		model.setAction("创建");
		model.setCreateButtonText(buttonText);
		model.setDialogTitle("新建分区");

		baseProcess(model);
	}

	public void editAsEquals2() {
		String name = Integer.toString(RandomUtils.randomFour());
		editPartition(
				"22"
				, name
				, "确定"
				, "确定");
	}
	
	public void editAsEquals15() {
		String name = Integer.toString(RandomUtils.randomFour());
		editPartition(
				"aaaaabbbbb11111"
				, name
				, "确定"
				, "确定");
	}
	
	public void editAsCancel() {
		String name = Integer.toString(RandomUtils.randomFour());
		createPartition1(name, "确定");
//		if (!MessageUtils.isEmpty()) return;
		
		DeviceManagerModel model = new DeviceManagerModel(new int[] {9, 2, 10, 4});
		model.setPartitionName("33");
		model.setAction("编辑");
		model.setItemName(name);
		model.setChangeButtonText("取消");
		model.setDialogTitle("修改分区");
		model.setOperateId(ControlInfo.popup_edit_area_text_edit);
		model.setWaitForText("修改分区");
		
		baseProcess(model);
	}
	
	private void editPartition(String partitionName, String itemName, String createButtonText, String editButtonText) {
		createPartition1(itemName, createButtonText);
//		if (!MessageUtils.isEmpty()) return;
		
		DeviceManagerModel model = new DeviceManagerModel(new int[] {9, 2, 10,4, 15});
		model.setPartitionName(partitionName);
		model.setAction("编辑");
		model.setItemName(itemName);
		model.setChangeButtonText(editButtonText);
		model.setDialogTitle("修改分区");
		model.setOperateId(ControlInfo.popup_edit_area_text_edit);
		model.setWaitForText("修改分区");
		
		baseProcess(model);
	}
	private void editPartition1(String partitionName, String itemName, String createButtonText, String editButtonText) {
		createPartition1(itemName, createButtonText);
//		if (!MessageUtils.isEmpty()) return;

		DeviceManagerModel model = new DeviceManagerModel(new int[] {9, 2, 10, 4});
		model.setPartitionName(partitionName);
		model.setAction("编辑");
		model.setItemName(itemName);
		model.setChangeButtonText(editButtonText);
		model.setDialogTitle("修改分区");
		model.setOperateId(ControlInfo.popup_edit_area_text_edit);
		model.setWaitForText("修改分区");

		baseProcess(model);
	}
	
	public void deleteAsConfirm() {
		String name = Integer.toString(RandomUtils.randomFour());
		deletePartition(
				6
				, null
				, name
				, name,
				"确定"
				, "确定");
	}
	
	public void deleteAsCancel() {
		String name = Integer.toString(RandomUtils.randomFour());
		deletePartition(
				5,
				"删除"
				, name
				, name
				, "确定"
				, "取消");
	}
	
	private void deletePartition(int action, String actionStr, String partitionName
			, String itemName, String createButtonText, String deleteButtonText) {
		createPartition1(itemName, createButtonText);
		DeviceManagerModel model = new DeviceManagerModel(new int[] {9, 11, 4, action});
		model.setPartitionName(partitionName);
		model.setItemName(itemName);
		model.setDeleteButtonText(deleteButtonText);
		model.setOperateId(ControlInfo.popup_edit_area_text_delete);
		model.setWaitForText("温馨提示");
		model.setDialogTitle("温馨提示");
		
		baseProcess(model);
	}

	/**
	 * 修改分区名称
	 */
	public void updateAsConf() {
		String name = Integer.toString(RandomUtils.randomFour());
		editPartition(
				"大汉"
				, name
				, "确定"
				, "确定");
	}

	/**
	 * 取消修改分区名称
	 */
	public void updateAsCancel() {
		String name = Integer.toString(RandomUtils.randomFour());
		editPartition1(
				"大汉"
				, name
				, "确定"
				, "取消");

	}

	@Override
	public void process(DeviceManagerModel model, int action) {
		switch (action) {
			case 0:
				clickCreatePartition();
				break;
			case 1:
				if (!solo.waitForDialogToOpen(2000)) {
					MessageUtils.append(Msg.NotOpenDialog);
					return;
				}
				break;
			case 2:
				enterPartitionName(model.getPartitionName());
				break;
			case 3:
				clickOnButtonInDialog(model.getCreateButtonText());
				break;
			case 4:
				waitForDialogToClosed(model.getDialogTitle());
				break;
			case 5:
				searchText(model.getPartitionName(), model.getAction());
				break;
			case 6:
				searchText1(model.getPartitionName());
				break;
			case 7:
				clickChangePartition();
				break;
			case 8:
				clickDeletePartition();
				break;
			case 9:
				clickOnText(model.getItemName(), model.getOperateId(), model.getWaitForText());
				break;
			case 10:
				clickOnButtonInDialog(model.getChangeButtonText());
				break;
			case 11:
				clickOnButtonInDialog(model.getDeleteButtonText());
				break;
			case 12:
				getButtonState();
				break;
			case 13:
				enterPartitionName(model.getPartitionName(), model.getPartitionNameCheck());
				break;
			case 14:
				model.setPartitionName(model.getPartitionNameCheck());
				break;
			case 15:
				deleteInit(model.getPartitionName());
				break;
		}
	}
	
	@Override
	public void init() {
		commonProc.login(AccountInfo.Account1, AccountInfo.Password1);
		commonProc.forceRefresh();
		solo.clickOnView(commonProc.getNavigationChild(1));
		solo.clickOnView(getter.getView(ControlInfo.base_img_back_fragment));
//		solo.clickOnText("管理分区");
		if (!commonProc.waitForArea()) return;
	}
	
	private void clickCreatePartition() {
		solo.clickOnView(getter.getView(ControlInfo.img_right));
	}
	
	private void enterPartitionName(String name) {
		enter.enterTextAsCopy(ControlInfo.et_user_info, name);
	}
	
	private void enterPartitionName(String name, String check) {
		enter.enterText(ControlInfo.et_user_info, name,check);
	}
	
	private void clickOnButtonInDialog(String buttonText) {
		solo.clickOnButton(buttonText);
	}
	
	private void searchText(String partitionName, String action) {
		if (!solo.waitForText(partitionName, 0, 5000)) {
			MessageUtils.append(
					"未显示"
					+ (null == action || action.isEmpty() ? "" : action + "的")
					+ "分区'" + partitionName + "'！");
		}
	}
	
	private void searchText1(String partitionName) {
		if (solo.searchText(partitionName, true)) {
			MessageUtils.append("仍然显示分区'" + partitionName + "'！");
		}
	}
	
	private boolean getButtonState() {
		solo.sleep(1000);
		return solo.getButton("确定").isClickable();
	}
	
	private void clickChangePartition() {
		solo.clickOnView(getter.getView(ControlInfo.popup_edit_area_text_edit));
	}
	
	private void clickDeletePartition() {
		solo.clickOnView(getter.getView(ControlInfo.popup_edit_area_text_delete));
	}
	
	private void clickOnText(String partitionName, String operateId, String waitForText) {
		boolean isClick = false;
		solo.scrollUp();
		if (!solo.searchText(partitionName)) {
			MessageUtils.append(Msg.NotDisplayItem);
			return;

		}
		ListView listView = (ListView) getter.getView(ControlInfo.area_listView);
		if (null == listView) {
			MessageUtils.append(Msg.GetPartitionListFailed);
			return;
		}
		for (int i = 0; i < listView.getChildCount(); i++) {
			LinearLayout first = (LinearLayout) listView.getChildAt(i);
			TextView partitionNameTv = (TextView) ((LinearLayout) first.getChildAt(0))
					.getChildAt(0);
			String text = partitionNameTv.getText().toString();
			if (!text.equals(partitionName)) continue;
			solo.clickOnView(first.getChildAt(1));
			isClick = true;
			break;
		}
		if (!isClick) MessageUtils.append(Msg.NotMatchItem);
		solo.clickOnView(getter.getView(operateId));
		if (!solo.waitForText(waitForText, 0, 2000)) {
			MessageUtils.append(Msg.NotOpenDialog);
		}
	}
	
	private void waitForDialogToClosed(String dialogTitle) {
		solo.sleep(1000);
		if (solo.waitForText(dialogTitle, 0 ,1000)) {
			MessageUtils.append(Msg.NotCloseDialog);
			return;
		}
	}

	/**
	 * 删除还原
	 * @param partition
	 */
	public void deleteInit(String partition){
		clickOnText(partition,ControlInfo.popup_edit_area_text_delete,"温馨提示");
		clickOnButtonInDialog("确定");
	}

	private static final class Msg {
		public static final String NotOpenDialog = "未打开Dialog！";
		public static final String NotCloseDialog = "未关闭Dialog！";
		public static final String GetPartitionListFailed = "获取分区列表失败！";
		public static final String NotDisplayItem = "列表中未显示要编辑的分区！";
		public static final String NotMatchItem = "列表中未匹配的要编辑的分区的编辑图标！";
	}
}
