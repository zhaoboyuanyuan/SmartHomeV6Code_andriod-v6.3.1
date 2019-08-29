package cc.wulian.smarthomev6.proc;

import cc.wulian.smarthomev6.app.AccountInfo;
import cc.wulian.smarthomev6.app.ControlInfo;
import cc.wulian.smarthomev6.model.DevicePartitionModel;
import cc.wulian.smarthomev6.utils.MessageUtils;
import com.wtt.frame.robotium.Solo;

/**
 * Created by 赵永健 on 2017/5/15.
 *
 * 封装设备分区的测试流程
 */
public class DevicePartitionProc extends BaseProc<DevicePartitionModel> {

	public DevicePartitionProc(Solo solo) {
		super(solo);
	}

	public void matchPartition() {
		checkPartition(new String[] {"全部", "未分区" , "客厅"}, new String[] {"客厅"}, "客厅");
	}

	public void matchMorePartition() {
		checkPartition(new String[] {"全部", "未分区","厨房","客厅"}
		, new String[] {"客厅"}, "客厅");
	}

	private void checkPartition(String[] allPartition, String[] partition, String clickText) {
		DevicePartitionModel model = new DevicePartitionModel(new int[] {0, 1, 2});
		model.setAllPartitionList(allPartition);
		model.setPartitionList(partition);
		model.setClickText(clickText);

		baseProcess(model);
	}

	@Override
	public void process(DevicePartitionModel model, int action) {
		switch (action) {
			case 0:
				clickAllPartition();
				break;
			case 1:
				searchMoreText(model.getAllPartitionList());
				break;
			case 2:
				clickInPartitionList(model.getClickText());
				break;
			case 3:
				searchMoreText(model.getPartitionList());
				break;
			case 4:
				if(!search(model.getClickText())){
					MessageUtils.append("未找到该分区");
				}
				break;
		}
	}

	@Override
	public void init() {
		commonProc.login(AccountInfo.Account1,AccountInfo.Password1);
		commonProc.forceRefresh();
		solo.clickOnView(commonProc.getNavigationChild(1));
	}

	private void clickAllPartition() {
		solo.clickOnView(getter.getView(ControlInfo.ll_all_partition));
	}

	private void searchMoreText(String[] searchText) {
		for (String string : searchText) {
			if (!solo.searchText(string)) {
				MessageUtils.append("\"全部分区\"列表中未显示\"" + string + "\"！");
				return;
			}
		}
	}

	private void clickInPartitionList(String clickText) {
		solo.clickOnText(clickText);
	}

	private boolean search(String text){
		return solo.searchText(text);
	}
}
