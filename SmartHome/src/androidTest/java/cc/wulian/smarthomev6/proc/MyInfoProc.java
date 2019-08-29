package cc.wulian.smarthomev6.proc;

import android.widget.TextView;
import cc.wulian.smarthomev6.app.AccountInfo;
import cc.wulian.smarthomev6.app.ControlInfo;
import cc.wulian.smarthomev6.model.MyInfoModel;
import cc.wulian.smarthomev6.utils.EnterUtils;
import cc.wulian.smarthomev6.utils.MessageUtils;
import com.wtt.frame.robotium.Solo;

/**
 * Created by 赵永健 on 2017/5/9.
 *
 * 封装个人信息的测试流程
 */
public class MyInfoProc extends BaseProc<MyInfoModel> {
	
	String oldName = null;
	String currName = null;
	
	public MyInfoProc(Solo solo) {
		super(solo);
	}
	
	public void changeNameLessThanTwenty() {
		changeName("测试", "确定", new int[] {0, 1, 2, 3, 4});
	}
	
	public void changeNameCancel() {
		changeName("测试", "取消", new int[] {0, 1, 2, 3, 5});
	}
	
	
	public void changeNameMoreThanTwenty() {
		changeName("aaaaabbbbb11111c", "确定", new int[] {0, 1, 7, 3, 4});
	}
	
	public void changeNameAsNotEnter() {
		changeName(null, "确定", new int[] {0, 1, 6});
	}
	
	private void changeName(String name, String text, int[] actions) {
		MyInfoModel myInfoModel = new MyInfoModel(actions);
		myInfoModel.setName(name);
		myInfoModel.setButtonText(text);
		
		baseProcess(myInfoModel);
		reset();
	}
	
	@Override
	public void process(MyInfoModel model, int action) {
		switch (action) {
			case 0:
				oldName = getNikeName();
				clickNameLy();
				break;
			case 1:
				if (!solo.waitForDialogToOpen(2000)) {
					MessageUtils.append(Msg.NoneDisplayChangeNameDialog);
					return;
				}
				break;
			case 2:
				enterName(model.getName());
				break;
			case 3:
				clickInDialog(model.getButtonText());
				break;
			case 4:
				if (!getNikeName().equals(model.getName())) {
					MessageUtils.append(Msg.NameDiffAfterChanged);
					return;
				}
				break;
			case 5:
				if (!getNikeName().equals(oldName)) {
					MessageUtils.append(Msg.NotChangeNameButNameChanged);
					return;
				}
				break;
			case 6:
				if (solo.getButton(model.getButtonText()).isClickable()) {
					MessageUtils.append(Msg.ConfirmCanClickable);
					return;
				}
				break;
			case 7:
				enterName(model.getName(), model.getName().replace("c", ""));
				break;
			case 8:
				if (!getNikeName().equals(model.getName().replace("c", ""))) {
					MessageUtils.append(Msg.NameDiffAfterChanged);
					return;
				}
				break;
		}
	}
	
	@Override
	public void init() {
		commonProc.login(AccountInfo.Account1, AccountInfo.Password1);
		solo.clickOnView(getter.getView(ControlInfo.item_account_login));
		if(commonProc.waitForMyInfo(5000)) return;
	}
	
	private void clickNameLy() {
		solo.clickOnView(getter.getView(ControlInfo.setting_manager_item_name));
	}
	
	private String getNikeName() {
		return ((TextView) getter.getView(ControlInfo.setting_manager_item_name_tv)).getText().toString();
	}
	
	private void enterName(String name) {
		enter.enterText(ControlInfo.et_user_info, name);
	}
	
	private void enterName(String name, String content) {
		enter.enterText(ControlInfo.et_user_info,name);
	}
	
	private void clickInDialog(String buttonText) {
		solo.clickOnButton(buttonText);
		solo.sleep(2000);
	}
	
	private void reset() {
		if (null != oldName) oldName = null;
		if (null != currName) currName = null;
	}
	
	private static final class Msg {
		public static final String NoneDisplayChangeNameDialog = "未显示修改姓名的提示框！";
		public static final String ChangeNameFailed = "修改名称失败！";
		public static final String NameDiffAfterChanged = "修改后展示的名称和用户输入的名称不一致！";
		public static final String NotChangeNameButNameChanged = "未修改名称但是名称发生了改变！";
		public static final String ConfirmCanClickable = "未输入名称时确定按钮可点击！";
	}
}
