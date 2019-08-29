package cc.wulian.smarthomev6.proc;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cc.wulian.smarthomev6.app.AccountInfo;
import cc.wulian.smarthomev6.app.ControlInfo;
import cc.wulian.smarthomev6.app.GatewayInfo;
import cc.wulian.smarthomev6.model.GatewayCenterModel;
import cc.wulian.smarthomev6.utils.MessageUtils;
import cc.wulian.smarthomev6.utils.Result;
import com.wtt.frame.robotium.Solo;

/**
 * Created by 赵永健 on 2017/5/9.
 *
 * 网关中心
 */
public class GatewayCenterProc extends BaseProc<GatewayCenterModel> {
	
	public GatewayCenterProc(Solo solo) {
		super(solo);
	}
	
	public void checkGatewayCenterList() {
		GatewayCenterModel model = new GatewayCenterModel(GatewayInfo.Gateway2.number, true, new int[] {0});
		baseProcess(model);
	}
	
	public void changeGateway() {
		GatewayCenterModel model = new GatewayCenterModel(GatewayInfo.Gateway2.number
				, true, new int[] {0, 1, 2});
		baseProcess(model);
	}
	
	private boolean searchBindGateway() {
		return solo.searchText("已绑定网关");
	}
	
	private boolean searchAuthGateway() {
		return solo.searchText("已接受分享");
	}
	
	private void clickListChild(String gatewayNumber) {
		Result result = new Result();
		ListView listView = (ListView) getter.getView(ControlInfo.gateway_listView);
		
		Result childResult = getChildIndex(listView, gatewayNumber);
		if (!childResult.isSuccess()) {
			return;
		}
		
		solo.clickInList((int) childResult.getData() + 1);
	}
	
//	private Result isGatewaySelected(String gatewayNumber) {
//		Result result = new Result();
//		ListView listView = (ListView) getter.getView("v6.gateway_listView");
//
//		Result childResult = getChildIndex(listView, gatewayNumber);
//		if (!childResult.isSuccess()) {
//			return childResult;
//		}
//
//		return result.success(getGatewaySelected(listView, childResult.getData()).isShown());
//	}
	
	private Result getChildIndex(ListView listView, String match) {
		Result result = new Result();
		if (0 == listView.getChildCount()) {
			return result.fail("List child is null!");
		}
		
		if (null == match || match.isEmpty()) {
			return result.fail("Match condition is null or empty!");
		}
		
		for (int i = 0; i < listView.getChildCount(); i++) {
			String currMatch = getGatewayNumber(listView, i).getText().toString().replace("网关号", "");
			
			if (match.equals(currMatch)) {
				return result.success(i);
			}
		}
		
		if (!solo.scrollDownList(listView.getChildCount())) {
			return result.fail("No matched child is list!");
		}
		
		return getChildIndex(listView, match);
	}
	
	private TextView getGatewayNumber(ListView listView, int index) {
//		TextView tvNumnber=null;
//		if(listView!=null) {
//			View layoutItem01 = listView.getChildAt(index);
//			if (layoutItem01 instanceof ViewGroup) {
//				for (int i = 0; i < ((ViewGroup) layoutItem01).getChildCount(); i++) {
//					View layoutItem02 = ((ViewGroup) layoutItem01).getChildAt(i);
//					if(layoutItem02 instanceof  ViewGroup){
//						for(int j=0;j<((ViewGroup) layoutItem02).getChildCount();j++){
//							View layoutItem03 = ((ViewGroup) layoutItem02).getChildAt(j);
//							if(layoutItem03 instanceof TextView){
//								Log.d("getGatewayNumber",((TextView)layoutItem03).getText().toString());
//							}else if(layoutItem03 instanceof ViewGroup) {
//								for (int k = 0; k < ((ViewGroup) layoutItem03).getChildCount(); k++) {
//									View layoutItem04 = ((ViewGroup) layoutItem03).getChildAt(k);
//									if(layoutItem04 instanceof TextView){
//										tvNumnber=(TextView)layoutItem04;
//										Log.d("getGatewayNumber",((TextView)layoutItem04).getText().toString());
//									}
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//		return tvNumnber;
		return (TextView) ((LinearLayout)((LinearLayout) ((LinearLayout) listView.getChildAt(index))
				.getChildAt(0)).getChildAt(1)).getChildAt(0);
	}
	
	private ImageView getGatewaySelected(ListView listView, int index) {
		return (ImageView) ((LinearLayout) listView.getChildAt(index)).getChildAt(1);
	}
	
	@Override
	public void process(GatewayCenterModel model, int action) {
		switch (action) {
			case 0:
				if (!searchBindGateway()) {
					MessageUtils.append("");
					return;
				}
				if (!searchAuthGateway()) {
					MessageUtils.append("");
					return;
				}
				break;
			case 1:
				clickListChild(model.getDeviceNumber());
				break;
//			case 2:
//				Result result = isGatewaySelected(model.getDeviceNumber());
//				if (!result.isSuccess()) {
//					MessageUtils.append(result.getMessage());
//					return;
//				}
//				if (!result.getData().equals(model.isShown())) {
//					MessageUtils.append("按钮的选中状态错误");
//					return;
//				}
//				break;
		}
	}
	
	@Override
	public void init() {
		commonProc.login(AccountInfo.Account1, AccountInfo.Password1);
		solo.clickOnView(getter.getView(ControlInfo.item_gateway_center));
		if (!commonProc.waitForGatewayCenter(5000)) {
			return;
		}
		solo.clickOnView(getter.getView(ControlInfo.item_gateway_center_list));
		if (!commonProc.waitForGatewayList(5000)) {
			return;
		}
	}
}
