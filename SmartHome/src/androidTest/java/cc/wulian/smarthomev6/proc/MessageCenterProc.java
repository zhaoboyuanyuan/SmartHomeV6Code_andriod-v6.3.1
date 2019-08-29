package cc.wulian.smarthomev6.proc;

import cc.wulian.smarthomev6.app.AccountInfo;
import cc.wulian.smarthomev6.app.ActivitiesName;
import cc.wulian.smarthomev6.app.ControlInfo;
import cc.wulian.smarthomev6.app.GatewayInfo;
import cc.wulian.smarthomev6.model.MessageCenterModel;
import cc.wulian.smarthomev6.utils.MessageUtils;
import com.wtt.frame.robotium.Solo;

/**
 * Created by 赵永健 on 2017/11/3.
 */
public class MessageCenterProc extends BaseProc<MessageCenterModel> {
    private String deviceName="红外入侵";
    private GatewayLoginProc gatewayLoginProc;

    public MessageCenterProc(Solo solo) {
        super(solo);
        gatewayLoginProc =new GatewayLoginProc(solo);
    }

    /**
     * 1.查看消息中心--报警消息
     */
    public void viewAlarm(){
        MessageCenterModel model=new MessageCenterModel(new int[]{0,1});
        baseProcess(model);
    }

    /**
     * 2.查看消息中心--具体设备的报警消息
     */
    public void viewDeviceAlarm(){
        MessageCenterModel model=new MessageCenterModel(new int[]{0,1,3,5});
        model.setDeviceName1(deviceName);
        baseProcess(model);
    }

    /**
     * 3.查看消息中心--日志
     */
    public void viewLog(){
        MessageCenterModel model=new MessageCenterModel(new int[]{0,2});
        baseProcess(model);
    }

    /**
     * 4.查看消息中心--具体设备的报警消息
     */
    public void viewDeviceLog(){
        MessageCenterModel model=new MessageCenterModel(new int[]{0,2,4,5});
        model.setDeviceName2(deviceName);
        baseProcess(model);
    }

    /**
     * 5.点击首页铃铛--网关登录
     */
    public void gatewayLoginClick(){
        MessageCenterModel model=new MessageCenterModel(new int[]{6,7,8});
        model.setSearchText(Msg.GatewayLoginMessageFail);
        baseProcess(model);
    }


    /***
     * 错误信息
     */
    public static final class Msg{
        public static final String MessageCenterFail="未进入消息中心页面";
        public static final String MessageAlarmFail="未进入报警消息页面";
        public static final String MessageLogFail="未进入日志页面";
        public static final String GatewayLoginMessageFail="(?=网关登录下)";
//        public static final String GatewayLoginMessageFail="网关登陆下.*";
    }
    @Override
    public void process(MessageCenterModel model, int action) {
        switch(action){
            case 0:
                clickBell();
                break;
            case 1:
                clickAlarm();
                break;
            case 2:
                clickLog();
                break;
            case 3:
                clickDevice(model.getDeviceName1());
                break;
            case 4:
                clickDevice(model.getDeviceName2());
                break;
            case 5:
                solo.clickOnView(getter.getView(ControlInfo.img_left));
                break;
            case 6:
                gateway();
                break;
            case 7:
                if (!solo.searchText(model.getSearchText())){
                    MessageUtils.append("未显示"+model.getSearchText()+"!");
                    return;
                }
                break;
            case 8:
                commonProc.quitLogin();
                break;
        }
    }

    @Override
    public void init() {
        commonProc.login(AccountInfo.Account1, AccountInfo.Password1);
        commonProc.forceRefresh();
        solo.clickOnView(commonProc.getNavigationChild(0));
    }

    /**
     * 点击铃铛
     */
    public void clickBell(){
        click.clickToAnotherActivity(ControlInfo.base_img_right, ActivitiesName.MessageCenterNewActivity,
                Msg.MessageCenterFail);
    }

    /**
     * 点击进入报警消息页面
     * @return
     */
    public void clickAlarm(){
        click.clickToAnotherActivity(ControlInfo.view_back_alarm, ActivitiesName.MessageAlarmListActivity,
                Msg.MessageAlarmFail);
    }

    /**
     * 点击进入日志页面
     * @return
     */
    public void clickLog(){
        click.clickToAnotherActivity(ControlInfo.view_back_log, ActivitiesName.MessageLogListActivity,
                Msg.MessageLogFail);
    }

    /**
     * 点击设备查看
     * @param deviceName
     */
    public void clickDevice(String deviceName){
        solo.clickOnText(deviceName);
    }

    /**
     * 网关登录点击铃铛
     */
    public void gateway(){
        gatewayLoginProc.init();
        gatewayLoginProc.enterGatewayNumber(GatewayInfo.Gateway2.number);
        gatewayLoginProc.enterPassword(GatewayInfo.Gateway2.password1);
        gatewayLoginProc.clickGatewayLoginBtn();
        commonProc.waitForHomePage(10000);
        solo.clickOnView(commonProc.getNavigationChild(0));
        solo.clickOnView(getter.getView(ControlInfo.base_img_right));
    }
}
