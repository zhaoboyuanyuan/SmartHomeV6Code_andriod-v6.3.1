package cc.wulian.smarthomev6.proc;

import cc.wulian.smarthomev6.app.AccountInfo;
import cc.wulian.smarthomev6.app.ControlInfo;
import cc.wulian.smarthomev6.model.DeviceAlarmAndLogModel;
import com.wtt.frame.robotium.Solo;

/**
 * 设备更多页面的查看日志和报警消息
 * Created by 赵永健 on 2017/11/2.
 */
public class DeviceAlarmAndLogProc extends BaseProc<DeviceAlarmAndLogModel> {
    private String deviceName ="红外入侵";
    private String toMore= ControlInfo.web_more;

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setToMore(String toMore) {
        this.toMore = toMore;
    }

    public DeviceAlarmAndLogProc(Solo solo) {
        super(solo);
    }
    /**
     *点击报警消息，查看消息
     */
    public void viewMessageAlarm(){
        DeviceAlarmAndLogModel model=new DeviceAlarmAndLogModel(new int[]{0,1,3});
        model.setDeviceName(deviceName);
        model.setToMore(toMore);
        baseProcess(model);
    }
    /**
     * 点击日志，查看消息
     */
    public void viewDaily(){
        DeviceAlarmAndLogModel model=new DeviceAlarmAndLogModel(new int[]{0,1,2});
        model.setDeviceName(deviceName);
        model.setToMore(toMore);
        baseProcess(model);
    }


    @Override
    public void process(DeviceAlarmAndLogModel model, int action) {
        switch (action){
            case 0:
                clickDevice(model.getDeviceName());
                break;
            case 1:
                clickToMore(model.getToMore());
                break;
            case 2:
                clickDaily();
                break;
            case 3:
                clickMessageAlarm();
                break;
        }
    }

    @Override
    public void init() {
        commonProc.login(AccountInfo.Account1, AccountInfo.Password1);
        commonProc.forceRefresh();
        solo.clickOnView(commonProc.getNavigationChild(1));
    }

    public void clickDevice(String deviceName){
        solo.clickOnText(deviceName);
        if(!commonProc.waitForDeviceDetail()) return;
    }

    public void clickToMore(String toMore){
        solo.clickOnWebElement(getter.getWebElementByXpath(toMore));
        if(!commonProc.waitForDeviceDetailMore()) return;
    }

    /**
     * 点击日志，查看消息
     */
    public void clickDaily(){
        solo.clickOnText("日志");
        commonProc.waitForMessageLog(5000);
        solo.clickOnView(getter.getView(ControlInfo.img_left));
    }

    /**
     *点击报警消息，查看消息
     */
    public void clickMessageAlarm(){
        solo.clickOnText("报警消息");
        commonProc.waitForMessageAlarm(5000);
        solo.scrollUp();
        solo.clickOnView(getter.getView(ControlInfo.img_left));
    }

}
