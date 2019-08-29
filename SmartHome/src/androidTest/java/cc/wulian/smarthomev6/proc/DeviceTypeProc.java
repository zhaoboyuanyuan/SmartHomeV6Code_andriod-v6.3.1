package cc.wulian.smarthomev6.proc;

import android.util.Log;

import cc.wulian.smarthomev6.app.AccountInfo;
import cc.wulian.smarthomev6.app.ControlInfo;
import cc.wulian.smarthomev6.model.DeviceTypeModel;
import cc.wulian.smarthomev6.utils.MessageUtils;
import com.wtt.frame.robotium.Solo;

/**
 * Created by 赵永健 on 2017/5/16.
 *
 * 封装设备类别的测试流程
 */
public class DeviceTypeProc extends BaseProc<DeviceTypeModel> {
	private String[] oldAllType = new String[] {"全部类别", "监控设备", "照明管理", "电器控制",
			"安全防护", "环境调节", "健康报告", "综合服务"};
	private String[] newAllType = new String[] {"全部类别","智能门锁","摄像机","开关照明","插座",
			"安防设备","环境监测","窗帘","智能遥控","中继器","控制器","背景音乐","健康管理","家用电器"};
	
	public DeviceTypeProc(Solo solo) {
		super(solo);
	}
	
	public void matchAllType() {
		checkType(newAllType, new String[] {"红外入侵"}, "安防设备");
	}
	
	private void checkType(String[] allType, String[] devices, String clickType) {
		DeviceTypeModel model = new DeviceTypeModel(new int[] {0 , 1, 2, 3});
		model.setAllType(allType);
		model.setDeviceList(devices);
		model.setClickType(clickType);
		
		baseProcess(model);
	}
	
	@Override
	public void process(DeviceTypeModel model, int action) {
		switch (action) {
			case 0:
				clickAllType();
				break;
			case 1:
				searchMoreText(model.getAllType());
				break;
			case 2:
				clickInPartitionList(model.getClickType());
				break;
			case 3:
				searchMoreText(model.getDeviceList());
				break;
		}
	}
	
	@Override
	public void init() {
		commonProc.login(AccountInfo.Account1, AccountInfo.Password1);
		solo.clickOnView(commonProc.getNavigationChild(1));
	}
	
	private void clickAllType() {
		solo.clickOnView(getter.getView(ControlInfo.iv_device_all_category));
	}
	
	private void searchMoreText(String[] searchText) {
		for (String str : searchText) {
			if(str.equals("窗帘")){
				//强制滑屏，太不听话
				solo.drag(660,660,990,360,20);
			}

			if (!solo.searchText(str)) {
				MessageUtils.append("\"全部类别\"列表中未显示\"" + str + "\"！");
				return;
			}
		}
	}
	
	private void clickInPartitionList(String clickText) {
		solo.drag(660,660,360,990,20);
		solo.clickOnText(clickText);
	}
}
