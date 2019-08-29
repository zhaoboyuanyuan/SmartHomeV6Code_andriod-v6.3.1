package cc.wulian.smarthomev6.proc;

import android.widget.EditText;
import android.widget.ListView;
import cc.wulian.smarthomev6.app.AccountInfo;
import cc.wulian.smarthomev6.app.ActivitiesName;
import cc.wulian.smarthomev6.app.ControlInfo;
import cc.wulian.smarthomev6.app.GatewayInfo;
import cc.wulian.smarthomev6.model.ShareDeviceModel;
import cc.wulian.smarthomev6.utils.EnterUtils;
import cc.wulian.smarthomev6.utils.MessageUtils;
import com.wtt.frame.robotium.Solo;

/**
 * Created by 赵永健 on 2017/9/4.
 */
public class ShareDeviceProc extends BaseProc<ShareDeviceModel> {
    public ShareDeviceProc(Solo solo) {
        super(solo);
    }

    /**
     * 取消分享成功
     */
    public void shareCancelSuccess(){
        ShareDeviceModel model=new ShareDeviceModel(new int[]{0,1,2,3,4});
        model.setListIndex(0);
//        model.setGatewayName("网关号:50294DFFFFFB");
        model.setButtonText("确定");
        baseProcess(model);
    }

    /**
     * 取消分享
     */
    public void shareCancel(){
        ShareDeviceModel model=new ShareDeviceModel(new int[]{0,1,2,3,4});
        model.setListIndex(0);
        model.setButtonText("取消");
        baseProcess(model);
    }

    /**
     * 分享成功
     */
    public void accountSuccess(){
        ShareDeviceModel model=new ShareDeviceModel(new int[]{0,1,5,6,7,8,3,4,10,11,14});
        model.setListIndex(0);
        model.setUserNumber(AccountInfo.Account3);
        model.setButtonText("确定");
        baseProcess(model);
    }


    /**
     * 用户手机号不正确
     */
    public void accountFail(){
        ShareDeviceModel model=new ShareDeviceModel(new int[]{0,1,5,6,7,9});
        model.setListIndex(0);
        model.setUserNumber("158");
        model.setSearchText(Msg.AccountFail);
        baseProcess(model);
    }

    /**
     * 设备已与用户绑定
     */
    public void accountUsed(){
        ShareDeviceModel model=new ShareDeviceModel(new int[]{0,1,5,6,7,8,3,4,9});
        model.setListIndex(0);
        model.setUserNumber(AccountInfo.Account3);
        model.setSearchText(Msg.AccountUsed);
        model.setButtonText("确定");
        baseProcess(model);
    }
    /**
     * 此账号不存在
     */
    public void accountNone(){
        ShareDeviceModel model=new ShareDeviceModel(new int[]{0,1,5,6,7,9});
        model.setListIndex(0);
        model.setUserNumber("15951644333");
        model.setSearchText(Msg.AccountNone);
        baseProcess(model);
    }

    /**
     * 取消分享WiFi设备成功
     */
    public void shareWifiDeviceCancel(){
        ShareDeviceModel model=new ShareDeviceModel(new int[]{0,1,2,3,4});
        model.setListIndex(1);
        model.setButtonText("确定");
        baseProcess(model);
    }

    /**
     * 分享wifi设备成功
     */
    public void shareWifiDeviceSuccess(){
        ShareDeviceModel model=new ShareDeviceModel(new int[]{0,1,5,6,7,8,3,4,10,12,14});
        model.setListIndex(1);
        model.setUserNumber(AccountInfo.Account3);
        model.setButtonText("确定");
        baseProcess(model);
    }

    /**
     * 查看分享管理页面
     */
    public void viewShareManager(){
        ShareDeviceModel model=new ShareDeviceModel(new int[]{0,13});
        baseProcess(model);
    }

    /**
     * 查看分享列表
     */
    public void viewShareList(){
        ShareDeviceModel model=new ShareDeviceModel(new int[]{0,1});
        model.setListIndex(0);
        baseProcess(model);
    }



    public final static class Msg{
        public static final String ShareDeviceAccountListFail= "进入分享列表页面失败！";
        public static final String CancelShareDialogFail= "取消分享对话框失败！";
        public static final String ShareDeviceSearchAccountFail= "进入添加分享用户页面失败！";
        public static final String AccountFail = "请输入正确的邮箱或手机号";
        public static final String AccountUsed = "设备已分享";
        public static final String AccountNone = "此账号不存在";
        public static final String SearchWifiDeviceFail = "搜索WiFi设备失败";
        public static final String SearchGatewayFail = "搜索网关失败";

    }

    @Override
    public void process(ShareDeviceModel model, int action) {
        switch(action){
            case 0:
                clickShareDevice();
                break;
            case 1:
//                if(!clickGateway(model.getListIndex())) return;
                solo.clickOnText("网关号:"+GatewayInfo.Gateway2.number);
                break;
            case 2:
                clickCancelShare();
                break;
            case 3:
                if(!solo.waitForDialogToOpen(3000)){
                    MessageUtils.append(Msg.CancelShareDialogFail);
                }
                break;
            case 4:
                solo.clickOnButton(model.getButtonText());
                break;
            case 5:
                clickToSearchAccount();
                break;
            case 6:
                enterUserNumber(model.getUserNumber());
                break;
            case 7:
                clickSearch();
                break;
            case 8:
                clickShare();
                break;
            case 9:
                if(!solo.searchText(model.getSearchText())){
                    MessageUtils.append("未显示"+model.getSearchText()+"!");
                    return;
                }
                break;
            case 10:
                if(!bindedAccount()) return;
                break;
            case 11:
                if(!shareGatewaySuccess()) return;
                break;
            case 12:
                if(!shareWifiSuccess()){
                    MessageUtils.append(Msg.SearchWifiDeviceFail);
                }
                break;
            case 13:
                if(!gateway()){
                    MessageUtils.append(Msg.SearchGatewayFail);
                }
                break;
            case 14:
                commonProc.quitLogin();
                break;
        }
    }


    @Override
    public void init() {
        commonProc.login(AccountInfo.Account1, AccountInfo.Password1);
        solo.clickOnView(commonProc.getNavigationChild(4));
    }

    /**
     * 点击分享管理
     */
    public void clickShareDevice(){
        solo.clickOnView(getter.getView(ControlInfo.item_sharedevice));
    }

    /**
     * 点击网关
     * @return
     */
    public boolean clickGateway(int listIndex){
        ListView listView= (ListView) getter.getView(ControlInfo.listView);
        return click.clickToAnotherActivity(listView.getChildAt(listIndex),
                ActivitiesName.ShareDeviceAccountListActivity, Msg.ShareDeviceAccountListFail);
    }

    /**
     * 点击取消分享
     */
    public void clickCancelShare(){
        commonProc.dragViewOnScreenLeftOrRight(ControlInfo.tv_account_name,0,-200);
        solo.clickOnText("取消分享");
    }

    /**
     * 点击右上角
     * @return
     */
    public void clickToSearchAccount(){
         click.clickToAnotherActivity(getter.getView(ControlInfo.img_right),
                ActivitiesName.ShareDeviceSearchAccountActivity, Msg.ShareDeviceSearchAccountFail);
    }

    /**
     *输入手机号
     * @param userNumber
     * @return
     */
    public void enterUserNumber(String userNumber){
        enter.enterText(ControlInfo.search_account_editText,userNumber);
    }

    public void clickSearch(){
        solo.clickOnView(getter.getView(ControlInfo.search_account_button));
    }

    /**
     * 点击分享
     */
    public void clickShare(){
        solo.clickOnView(getter.getView(ControlInfo.auth_current_gateway_button));
    }

    /**
     *
     * 进入绑定账号
     */
    public boolean bindedAccount(){
        solo.clickOnView(super.getter.getView(ControlInfo.img_left));
        if(solo.searchText(AccountInfo.Account3)){
            solo.clickOnView(super.getter.getView(ControlInfo.img_left));
            solo.clickOnView(getter.getView(ControlInfo.img_left));
            commonProc.quitLogin();
            login(AccountInfo.Account3, AccountInfo.Password3);
            return true;
        }else{
            return false;
        }
    }

    /**
     * 登录账号
     * @param accountText
     * @param passwordText
     */
    public void login(String accountText,String passwordText){
        EditText account = (EditText) getter.getView(ControlInfo.username);
        EditText password = (EditText) getter.getView(ControlInfo.password);
        if (!"".equals(account.getText().toString())) solo.clearEditText(account);
        if (!"".equals(password.getText().toString())) solo.clearEditText(password);
        solo.enterText(account, accountText);
        solo.enterText(password, passwordText);
        solo.clickOnView(getter.getView(ControlInfo.login));
    }

    /**
     * 判断是否分享网关成功
     * @return
     */
    public boolean shareGatewaySuccess(){
        solo.sleep(2000);
        solo.clickOnView(commonProc.getNavigationChild(4));
        solo.clickOnView(getter.getView(ControlInfo.item_gateway_center));
        commonProc.waitForGatewayCenter(3000);
        solo.clickOnView(getter.getView(ControlInfo.item_gateway_center_list));
        commonProc.waitForGatewayList(3000);
        solo.clickOnText("已接受分享");
        if(solo.searchText("网关号:"+ GatewayInfo.Gateway2.number)){
            solo.clickOnView(super.getter.getView(ControlInfo.img_left));
            solo.clickOnView(getter.getView(ControlInfo.img_left));
            return true;
        }else{
            return false;
        }
    }

    /**
     * 判断设备列表里有没有WiFi设备
     * @return
     */
    public boolean shareWifiSuccess(){
        solo.clickOnView(commonProc.getNavigationChild(1));
        if(solo.searchText("来自"+ AccountInfo.Account+"的分享")){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 查看网关
     * @return
     */
    public boolean gateway(){
        if(solo.searchText("网关号:"+ GatewayInfo.Gateway2.number)){
            return  true;
        }else{
            return false;
        }
    }
}
