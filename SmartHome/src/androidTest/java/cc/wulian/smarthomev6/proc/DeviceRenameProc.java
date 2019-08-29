package cc.wulian.smarthomev6.proc;

import android.widget.TextView;
import cc.wulian.smarthomev6.app.AccountInfo;
import cc.wulian.smarthomev6.app.ControlInfo;
import cc.wulian.smarthomev6.model.DeviceRenameModel;
import cc.wulian.smarthomev6.utils.MessageUtils;
import com.wtt.frame.robotium.Solo;

/**
 * Created by 赵永健 on 2017/5/15.
 *
 * 封装设备重命名的测试流程
 */
public class DeviceRenameProc extends BaseProc<DeviceRenameModel> {
	
	private String oldName = "红外入侵";
	private String toMore= ControlInfo.web_more;

	public void setToMore(String toMore) {
		this.toMore = toMore;
	}

	public DeviceRenameProc(Solo solo) {
		super(solo);
	}
	
	public void setOldName(String oldName) {
		this.oldName = oldName;
	}

	/**
	 * 公共接口
	 * @param model
	 */
	public void commonProcess(DeviceRenameModel model){
		baseProcess(model);
	}
	
	public void renameAsEquals20() {
		renameAsConfirm(
				"aaaaabbbbb11111"
				, oldName
				, true
				,"确定"
		);
	}
	
	public void renameAsMoreThan20() {
		DeviceRenameModel model = new DeviceRenameModel(new int[] {0 ,1, 2, 3, 7, 5, 8});
		model.setDeviceName("aaaaabbbbb111112");
		model.setDeviceNameCheck("aaaaabbbbb11111");
		model.setOldName(oldName);
		model.setButtonText("确定");
		model.setRename(true);
		model.setToMore(toMore);
		
		baseProcess(model);
		if (!reduceInfraredName()) {
			MessageUtils.append(Msg.ReduceDeviceNameFailed);
		}
	}
	
	public void renameAsEquals19() {
		renameAsConfirm("aaaaabbbbb1111", oldName, true,"确定");
	}
	
	public void renameAsChildDevice() {
		renameAsConfirm("aaaaa11111", oldName, true,"确定");
	}
	
	public void renameAsCancel() {
		renameAsConfirm("aaaaa11111", oldName, false,"取消");
	}
	public void renameAsConfirm1() {
		renameAsConfirm("aaaaa11111", oldName, true,"确定");
	}
	
	public void renameAsConfirm(String content, String oldName, boolean isRename,String buttonText) {
		DeviceRenameModel model = new DeviceRenameModel(new int[] {0 ,1, 2, 3, 4, 5});
		model.setDeviceName(content);
		model.setOldName(oldName);
		model.setButtonText(buttonText);
		model.setRename(isRename);
		model.setToMore(toMore);
		
		baseProcess(model);
		if (!reduceInfraredName()) {
			MessageUtils.append(Msg.ReduceDeviceNameFailed);
		}
	}
	
	@Override
	public void process(DeviceRenameModel model, int action) {
		switch (action) {
			case 0:
				clickInList(model.getOldName());
				break;
			case 1:
				clickToMore(model.getToMore());
				break;
			case 2:
				clickRename();
				break;
			case 3:
				if (!solo.waitForDialogToOpen(2000)) {
					MessageUtils.append(Msg.NotOpenDialog);
					return;
				}
				break;
			case 4:
				enterDeviceName(model.getDeviceName());
				break;
			case 5:
				clickInDialog(model.getButtonText());
				break;
			case 6:
				checkName(model.getDeviceNameCheck(), model.isRename());
				break;
			case 7:
				enterDeviceName(model.getDeviceName(), model.getDeviceNameCheck());
				break;
			case 8:
				model.setDeviceName(model.getDeviceNameCheck());
				break;
			case 9:
				reduceName(model.getOldName());
				break;

		}
	}
	
	@Override
	public void init() {
		commonProc.login(AccountInfo.Account1, AccountInfo.Password1);
		commonProc.forceRefresh();
		solo.clickOnView(commonProc.getNavigationChild(1));
	}
	
	private void clickInList(String deviceName) {
		solo.clickOnText(deviceName,0);
	}
	
	public void clickRename() {
		solo.clickOnView(getter.getView(ControlInfo.item_device_more_rename));
	}

	public void enterDeviceName(String deviceName) {
		 enter.enterText(ControlInfo.et_user_info, deviceName);
	}

	public void enterDeviceName(String deviceName, String check) {
		enter.enterText(ControlInfo.et_user_info, deviceName);
	}

	public void clickInDialog(String text) {
		solo.clickOnButton(text);
	}

	public void checkName(String oldName, boolean isRename) {
		String name = ((TextView) getter.getView(ControlInfo.item_device_more_rename_name)).getText().toString();
		if (isRename && !oldName.equals(name)) {
			MessageUtils.append(Msg.RenameFailed
					+ "重命名之前的设备名称 - \"" + oldName + "\" | "
					+ "重命名之后的设备名称 - \"" + name + "\"！");
			return;
		}
		if (!isRename && oldName.equals(name)) {
			MessageUtils.append(Msg.CancelRenameFailed
					+ "重命名之前的设备名称 - \"" + oldName + "\" | "
					+ "重命名之后的设备名称 - \"" + name + "\"！");
			return;
		}
	}
	
	public boolean reduceInfraredName() {
		return reduceName(oldName);
	}
	
	public boolean reduceName(String reduceName) {
		clickRename();
		if (!solo.waitForDialogToOpen(2000)) return false;
		enterDeviceName(reduceName);
		clickInDialog("确定");
		
		return true;
	}

	public void clickToMore(String toMore){
		solo.clickOnWebElement(getter.getWebElementByXpath(toMore));
		if(!commonProc.waitForDeviceDetailMore()) return;
	}

	private final class Msg {
		public static final String NotOpenDialog = "未显示重命名提示框！";
		public static final String EnterDeviceNameFailed = "输入设备名称失败！";
		public static final String RenameFailed = "重命名失败！";
		public static final String CancelRenameFailed = "取消重命名失败！";
		public static final String ReduceDeviceNameFailed = "还原设备名称失败！";
	}
}
