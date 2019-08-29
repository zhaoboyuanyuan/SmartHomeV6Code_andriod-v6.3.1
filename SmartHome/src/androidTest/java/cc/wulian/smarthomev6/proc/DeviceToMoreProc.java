package cc.wulian.smarthomev6.proc;

import android.widget.ListView;
import cc.wulian.smarthomev6.app.AccountInfo;
import cc.wulian.smarthomev6.app.ActivitiesName;
import cc.wulian.smarthomev6.app.ControlInfo;
import cc.wulian.smarthomev6.model.Switch16AjModel;
import com.wtt.frame.robotium.Solo;

/**
 * Created by 赵永健 on 2017/11/3.
 */
public class DeviceToMoreProc extends BaseProc<Switch16AjModel> {

    private String switchName="门磁";
    private String toMore= ControlInfo.web_more;

    public void setToMore(String toMore) {
        this.toMore = toMore;
    }

    public void setSwitchName(String switchName) {
        this.switchName = switchName;
    }

    public DeviceToMoreProc(Solo solo) {
        super(solo);
    }

    /**
     * 1.设备重命名
     */
    public void deviceRename(){
        Switch16AjModel model=new Switch16AjModel(new int[]{0,1,2,3,4,5,9});
        model.setDeviceName("内嵌一路");
        model.setToMore(toMore);
        model.setButtonTxt("确定");
        model.setOrDeviceName(switchName);
        baseProcess(model);
    }
    /**
     * 2.取消设备重命名
     */
    public void deviceRenameAsCancel(){
        Switch16AjModel model=new Switch16AjModel(new int[]{0,1,2,3,4,5});
        model.setDeviceName("内嵌一路");
        model.setToMore(toMore);
        model.setButtonTxt("取消");
        model.setOrDeviceName(switchName);
        baseProcess(model);
    }

    /**
     *3.设置分区
     */
    public void setAreas(){
        Switch16AjModel model = new Switch16AjModel(new int[]{0,1,2,6,7});
        model.setToMore(toMore);
        model.setArea(1);
        baseProcess(model);
    }

    /**
     * 4.设备信息
     */
    public void deviceMessage(){
        Switch16AjModel model = new Switch16AjModel(new int[]{0,1,2,10});
        model.setToMore(toMore);
        baseProcess(model);
    }

    /**
     * 5.累计电量清零
     */
    public void clearElectricity(){
        Switch16AjModel model = new Switch16AjModel(new int[]{0,1,2,11});
        model.setToMore(toMore);
        baseProcess(model);
    }

    /**
     * 6.开启绑定/解绑状态
     */
    public void bindORNoBind(){
        Switch16AjModel model = new Switch16AjModel(new int[]{0,1,2,12});
        model.setToMore(toMore);
        baseProcess(model);
    }

    /**
     * 7.日志
     */
    public void seeDaily(){
        Switch16AjModel model = new Switch16AjModel(new int[]{0,1,2,13});
        model.setToMore(toMore);
        baseProcess(model);
    }

    /**
     * 8.报警消息
     */
    public void messageAlarm(){
        Switch16AjModel model = new Switch16AjModel(new int[]{0,1,2,14});
        model.setToMore(toMore);
        baseProcess(model);
    }

    /**
     * 9.删除设备
     */
    public void deleteDevice(){
        Switch16AjModel model = new Switch16AjModel(new int[]{0,1,2,8,5});
        model.setToMore(toMore);
        model.setButtonTxt("取消");
        baseProcess(model);
    }

    /**
     * 10.取消删除
     */
    public void deleteDeviceAsCancel(){
        Switch16AjModel model = new Switch16AjModel(new int[]{0,1,2,8,5});
        model.setToMore(toMore);
        model.setButtonTxt("取消");
        baseProcess(model);
    }

    /**
     * 错误信息
     */
    public  static final class Msg{
        public static final String intoDeviceInfoActivityFail="进入设备信息页面失败";
        public static final String DeviceDetailMoreAreaFailed = "未进入分区界面！";
    }

    @Override
    public void process(Switch16AjModel model, int action) {
        switch (action){
            case 0:
                solo.clickOnView(commonProc.getNavigationChild(1));//进入设备列表
                break;
            case 1:
                clickToDeviceDetail();
                break;
            case 2:
                clickToMore(model.getToMore());
                break;
            case 3:
                clickRename();
                break;
            case 4:
                enterDeviceName(model.getDeviceName());
                break;
            case 5:
                clickInDialog(model.getButtonTxt());
                break;
            case 6:
                clickArea();
                break;
            case 7:
                chooseArea(model.getArea());
                break;
            case 8:
                delete();
                break;
            case 9:
                recoverDeviceName(model.getOrDeviceName());
                break;
            case 10:
                viewDeviceInfo();
                break;
            case 11:
                clickTPClear();
                break;
            case 12:
                openBindAndcloseBind();
                break;
            case 13:
                clickDaily();
                break;
            case 14:
                clickMessageAlarm();
                break;
        }
    }

    @Override
    public void init() {
        commonProc.login(AccountInfo.Account1, AccountInfo.Password1);
            solo.clickOnView(commonProc.getNavigationChild(0));
    }

    /**
     * 点击进入设备详情页面
     */
    public void clickToDeviceDetail(){
        solo.clickOnText(switchName);
        if(!commonProc.waitForDeviceDetail()) return;
    }

    /**
     * 点击进入设备信息
     * @return
     */
    public void clickToDeviceInf(){
       click.clickToAnotherActivity(ControlInfo.item_device_more_info, ActivitiesName.DeviceInfoActivity,
                Msg.intoDeviceInfoActivityFail);
    }

    /**
     * 判断设备信息元素
     * @return
     */
    public boolean checkDeviceInfo(){
        if(solo.searchText("产品名称")&&solo.searchText("固件版本")){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 查看设备信息
     */
    public void viewDeviceInfo(){
        clickToDeviceInf();
        if(!checkDeviceInfo()) return;
        solo.clickOnView(getter.getView(ControlInfo.img_left));
    }

    /**
     * 累计电量清零
     */
    public void clickTPClear(){
        solo.clickOnText("累计电量清零");
    }

    /**
     *开启绑定/解绑模式
     */
    public void openBindAndcloseBind(){
        solo.clickOnText("开启绑定/解绑模式");
        solo.clickOnButton("我知道了");
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

    /**
     * 点击进入更多页面
     * @param toMore
     */
    public void clickToMore(String toMore){
        solo.clickOnWebElement(getter.getWebElementByXpath(toMore));
        if(!commonProc.waitForDeviceDetailMore()) return;
    }

    /**
     * 点击重命名
     */
    public void clickRename() {
        solo.clickOnView(getter.getView(ControlInfo.item_device_more_rename));
    }

    /**
     *输入设备名称
     */
    public void enterDeviceName(String deviceName) {
        enter.enterText(ControlInfo.et_user_info, deviceName);
    }

    /**
     *点击取消或确定
     */
    public void clickInDialog(String buttonText) {
        solo.clickOnButton(buttonText);
    }
    /**
     * 点击分区
     */
    public void clickArea(){
         click.clickToAnotherActivity(ControlInfo.item_device_more_area,ActivitiesName.DeviceDetailMoreAreaActivity,
                Msg.DeviceDetailMoreAreaFailed);
    }
    /**
     * 选择分区
     */
    public void chooseArea(int area){
        ListView view = (ListView) super.getter.getView(ControlInfo.device_detail_more_area_list);
        solo.clickOnView(view.getChildAt(area));
    }
    /**
     * 点击删除
     */
    public void delete(){
        solo.clickOnView(super.getter.getView(ControlInfo.item_device_more_delete));
    }

    public void recoverDeviceName(String ORDeviceName){
        clickRename();
        enterDeviceName(ORDeviceName);
        clickInDialog("确定");
    }


}
