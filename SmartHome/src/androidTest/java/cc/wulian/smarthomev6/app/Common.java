package cc.wulian.smarthomev6.app;

import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import cc.wulian.smarthomev6.utils.*;
import cc.wulian.wrecord.C;
import com.wtt.frame.robotium.By;
import com.wtt.frame.robotium.Solo;
import com.wtt.frame.robotium.WebElement;
import com.wtt.runner.android.InstTestRunner;

import static junit.framework.TestCase.assertTrue;


/**
 * Created by 赵永健 on 2017/12/27.
 * 通用操作类
 */
public class Common {
    private Solo solo;
    private GetterUtils getter;
    private WaitForUtils waitFor;
    private ClickUtils click;
    private ScrollUtils scroll;
    public Common(Solo solo) {
        this.solo = solo;
        getter=new GetterUtils(solo);
        waitFor=new WaitForUtils(solo);
        click=new ClickUtils(solo,getter,waitFor);
        scroll=new ScrollUtils(solo);
    }

    /**
     * 登录
     * @param account
     * @param password
     */
    public void login(String account, String password){
        //第一次启动时的滑动导航广告
//        if(getter.getView("home_indicator")!=null){
//            for(int i=0;i<=2;i++){
//                dragViewOnScreenLeftOrRight("home_indicator",0,-500);
//            }
//            solo.clickOnView(getter.getView("guide_tv"));
//        }

        if(solo.searchText("版本更新")){
            solo.clickOnView(getter.getView(ControlInfo.btn_negative));
        }
        if(solo.waitForText("我的")){
            solo.clickOnText("我的");
        }
        if(solo.searchText("登录/注册")){
            scrollDownInMine();
            solo.clickOnView(getter.getView(ControlInfo.item_setting));
            waitForLogin();
            EditText userName = (EditText) solo.getView(ControlInfo.username);
            EditText pwd = (EditText) solo.getView(ControlInfo.password);

            solo.clearEditText(userName);
            solo.typeText(userName,account);
            solo.typeText(pwd, password);
            solo.clickOnButton("登录");
            waitForHomePage(5000);
            if(solo.waitForView(getNavigationChild(4))){
                solo.clickOnText("我的");
            }
        }else{
            return;
        }
    }

    /**
     * 退出登录
     */
    public void quitLogin(){
//        if(solo.waitForText("我的")){
//            solo.clickOnText("我的");
//        }
        solo.clickOnView(getNavigationChild(4));
        scrollDownInMine();
        solo.clickOnView(getter.getView(ControlInfo.item_setting));
        if(solo.waitForActivity(ActivitiesName.SettingActivity)){
            solo.clickOnView(getter.getView(ControlInfo.item_setting_logout));
            scrollUpInMine();
            solo.clickOnView(getter.getView(ControlInfo.item_account_login));
            waitForLogin();
        }else{
            return;
        }

    }

    /**
     * 登录初始化
     */
    public void loginInit(){
        if(solo.searchText("版本更新")){
            solo.clickOnView(getter.getView(ControlInfo.btn_negative));
        }
        if(solo.waitForText("我的")){
            solo.clickOnText("我的");
        }
        if(!solo.searchText("登录/注册")) {
            scrollDownInMine();
            solo.clickOnText("设置");
            waitForSetting();
            solo.clickOnText("退出登录");
            scrollUpInMine();
            solo.clickOnView(solo.getView(ControlInfo.item_account_login_name));
            waitForLogin();
        }else{
            solo.clickOnText("设置");
            waitForLogin();
        }
    }

    public void unbindGateway(String account, String password) {
        login(account, password);
        solo.clickOnView(getter.getView(ControlInfo.item_gateway_center));
        if (!waitForGatewayCenter(5000)) return;

        RelativeLayout layout = (RelativeLayout) getter.getView(ControlInfo.item_gateway_center_setting);

        if (layout.isEnabled()) {  //查看控件，clickable是TRUE,但enable是false，所以暂时使用enable
            solo.clickOnView(layout);
            if (!waitForGatewaySetting(5000)) return;
            solo.clickOnView(getter.getView(ControlInfo.unbind_button));
            solo.clickOnView(getter.getView(ControlInfo.img_left));
        }else{
            solo.clickOnView(getter.getView(ControlInfo.img_left));
        }
    }

    /**
     * 判断APP是否成功启动"首页"
     *
     * @param timeout - 超时时间
     * @return        - 成功成功启动首页，返回{@code true}，否则返回{@code false}
     */
    public boolean waitForHomePage(int timeout) {
//        return waitFor.waitFor(ActivitiesName.HomeActivity, timeout, Msg.IntoHomePageFailed);
        solo.sleep(2000);
        if(getter.getView("base_img_right") != null) {
            return true;
        }else {
            return false;
        }
    }

    public boolean waitForGatewaySetting(int timeout) {
        return waitFor.waitFor(ActivitiesName.GatewaySettingActivity, timeout, Msg.IntoGatewaySettingFailed);
    }

    public boolean waitForGatewayCenter(int timeout) {
        return waitFor.waitFor(ActivitiesName.GatewayCenterActivity, timeout, Msg.IntoGatewayCenterFailed);
    }

    public void waitForLogin(){
        if(!solo.waitForActivity(ActivitiesName.SigninActivity, 3000)){
            MessageUtils.append(Msg.intoSigninActivityFailed);
        }
    }

    public void waitForLogin(int timeOut){
        if(!solo.waitForActivity(ActivitiesName.SigninActivity, timeOut)){
            MessageUtils.append(Msg.intoSigninActivityFailed);
        }
    }


    public void waitForSetting(){
        if(!solo.waitForActivity(ActivitiesName.SettingActivity,3000)){
            MessageUtils.append(Msg.intoSettingActivityFailed);
        }
    }

    public void waitForgotAccountActivity(){
//        assertTrue(Msg.intoForgotAccountActivityFailed,solo.waitForActivity(ActivitiesName.ForgotAccountActivity,3000));
        if(!solo.waitForActivity(ActivitiesName.ForgotAccountActivity,3000)){
            MessageUtils.append(Msg.intoForgotAccountActivityFailed);
        }
    }

    public boolean waitForRegister(int timeout) {
        return waitFor.waitFor(ActivitiesName.RegisterActivity, timeout, Msg.IntoRegisterFailed);
    }
    /**
     * 进入输入验证码页面
     *
     * @param timeout
     * @return
     */
    public boolean waitForForgotVerificationActivity(int timeout) {
        return waitFor.waitFor(ActivitiesName.ForgotVerificationActivity, timeout, Msg.intoForgotVerificationActivityFailed);
    }

    public boolean waitForForgetPassword(int timeout) {
        return waitFor.waitFor(ActivitiesName.ForgotPassWordActivity, timeout, Msg.IntoForgetPasswordFailed);
    }

    public boolean waitForChangePhoneNumber(int timeout) {
        return waitFor.waitFor(ActivitiesName.ChangePhoneNumberActivity, timeout, Msg.IntoChangePhoneNumberFailed);
    }

    public boolean waitForConfirmMail(int timeout) {
        return waitFor.waitFor(ActivitiesName.ConfirmMailBoxActivity, timeout, Msg.IntoConfirmMailFailed);
    }

    public boolean waitForAccountSecurity(int timeout) {
        return waitFor.waitFor(ActivitiesName.AccountSecurityActivity, timeout, Msg.IntoAccountSecurityFailed);
    }

    public boolean waitForGatewayList(int timeout) {
        return waitFor.waitFor(ActivitiesName.GatewayListActivity, timeout, Msg.IntoGatewayListFailed);
    }

    public boolean waitForgatewayBind(int timeout) {
        return waitFor.waitFor(ActivitiesName.GatewayBindActivity, timeout, Msg.IntoBindGatewayFailed);
    }

    public boolean waitForMessageCenterNew(int timeout) {
        return waitFor.waitFor(ActivitiesName.MessageCenterNewActivity, timeout, Msg.IntoMessageCenterFailed);
    }

    public boolean waitForAllScene() {
        return waitFor.waitFor(ActivitiesName.AllSceneActivity, 5000, Msg.IntoAllSceneFailed);
    }

    public boolean waitForAddScene() {
        return waitFor.waitFor(ActivitiesName.AddSceneActivity, 5000, Msg.IntoAddSceneFailed);
    }

    public boolean waitForEditScene() {
        return waitFor.waitFor(ActivitiesName.EditSceneActivity, 5000, Msg.IntoEditSceneFailed);
    }

    public boolean waitForSceneSort() {
        return waitFor.waitFor(ActivitiesName.SceneSortActivity, 5000, Msg.IntoSceneSortFailed);
    }

    public boolean waitForCustomScenePage() {
        return waitFor.waitForAsWeb(By.xpath(ControlInfo.customScene_sure)
                , 5000, Msg.IntoCustomScenePageFailed);
    }

    public boolean waitForEditScenePage() {
        return waitFor.waitForAsWeb(By.xpath(ControlInfo.edit_scene_finished)
                , 5000, Msg.IntoEditScenePageFailed);
    }
    public boolean waitForDeviceDetail() {
        return waitFor.waitFor(ActivitiesName.DeviceDetailActivity, 5000, Msg.IntoDeviceDetailFailed);
    }

    public boolean waitForDeviceDetailMore() {
        return waitFor.waitFor(ActivitiesName.DeviceMoreActivity, 5000, Msg.IntoDeviceMoreFailed);

    }

    public boolean waitForArea() {
        return waitFor.waitFor(ActivitiesName.AreaActivity, 5000, Msg.IntoAreaFailed);
    }

    public boolean waitForDetailMoreArea() {
        return waitFor.waitFor(ActivitiesName.DeviceDetailMoreAreaActivity, 5000, Msg.IntoDeviceMoreAreaFailed);
    }
    public boolean waitForMyInfo(int timeout) {
        return waitFor.waitFor(ActivitiesName.UserMassageActivity, timeout, Msg.IntoPersonalInfoFailed);
    }

    /**
     * 进入报警消息页面
     * @param timeout
     * @return
     */
    public boolean waitForMessageAlarm(int timeout){
        return waitFor.waitFor(ActivitiesName.MessageAlarmActivity,timeout,Msg.IntoMessageAlarmFailed);
    }

    /**
     * 进入报警消息页面
     * @param timeout
     * @return
     */
    public boolean waitForMessageLog(int timeout){
        return waitFor.waitFor(ActivitiesName.MessageLogActivity,timeout,Msg.IntoMessageLogFailed);
    }

    public boolean waitForDeviceListPage() {
        return waitFor.waitForAsWeb(By.xpath(ControlInfo.web_device_list_title)
                , 5000, Msg.IntoAddDevicePageFailed);
    }

    public boolean waitForDeviceStatePage() {
        return waitFor.waitForAsWeb(By.xpath(ControlInfo.web_set_device_state_1)
                , 5000, Msg.IntoDeviceStatePageFailed);
    }

    public boolean waitForGatewayLogin() {
        return waitFor.waitFor(ActivitiesName.GatewayLoginActivity, 5000, Msg.IntoGatewayLoginFailed);
    }

    public boolean waitForRegisterVerification() {
        return solo.waitForActivity("RegisterVerificationActivity",2000);
    }

    /**
     * 点击设防/停止报警
     */
    public void clickDeviceState1() {
        solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_set_device_state1));
    }

    /**
     * 点击撤防/通用报警
     */
    public void clickDeviceState2() {
        solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_set_device_state2));
    }

    /**
     * 点击火灾报警
     */
    public void clickDeviceState3() {
        solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_set_device_state3));
    }

    /**
     * 点击开启
     */
    public void clickDeviceStateAsOpen() {
        solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_set_device_state_open));
    }

    /**
     * 点击关闭
     */
    public void clickDeviceStateAsClose() {
        solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_set_device_state_close));
    }

    /**
     * 有漏水/有烟雾/有可燃气体泄漏/设防下有人经过
     */
    public void clickDeviceStateLeakage() {
        solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_set_device_state_leakage));
    }

    /**
     * 设防下恢复正常
     */
    public void clickDeviceStateLeakage1() {
        solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_set_device_state_leakage1));
    }

    /**
     * 撤防下有人经过
     */
    public void clickDeviceStateLeakage2() {
        solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_set_device_state_leakage2));
    }

    /**
     * 撤防下恢复正常
     */
    public void clickDeviceStateLeakage3() {
        solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_set_device_state_leakage3));
    }

    /**
     * 点击消警
     */
    public void clickDeviceStateAsFireAlarm() {
        solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_set_device_state_fire_alarm));
    }

    /**
     * 点击高于指定温度
     */
    public void clickDeviceStateAsTemperatureHigh() {
        solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_set_device_state_temperature_high));
        if (!waitForWebPage("选择温度")) {
            MessageUtils.append(Msg.LoadTemperatureFailed);
            return;
        }
//		solo.clickOnWebElement(By.textContent("确定"));
    }

    /**
     * 点击低于指定温度
     */
    public void clickDeviceStateAsTemperatureLow() {
        solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_set_device_state_temperature_low));
        if (!waitForWebPage("选择温度")) {
            MessageUtils.append(Msg.LoadTemperatureFailed);
            return;
        }
//		solo.clickOnWebElement(By.textContent("确定"));
    }

    /**
     * 点击高于指定湿度
     */
    public void clickDeviceStateAsHumidityHigh() {
        solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_set_device_state_humidity_high));
        if (!waitForWebPage("选择湿度")) {
            MessageUtils.append(Msg.LoadHumidityFailed);
            return;
        }
//		solo.clickOnWebElement(By.textContent("确定"));
    }

    /**
     * 点击低于指定湿度
     */
    public void clickDeviceStateAsHumidityLow() {
        solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_set_device_state_humidity_low));
        if (!waitForWebPage("选择湿度")) {
            MessageUtils.append(Msg.LoadHumidityFailed);
            return;
        }
//		solo.clickOnWebElement(By.textContent("确定"));
    }

    public boolean waitForWebPage(String title) {
        return solo.waitForWebElement(By.textContent(title), 0, 10000, false);
    }

    /**
     * 点击被打开
     */
    public void clickDeviceStateAsOpened() {
        solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_set_device_state_opened));
    }

    /**
     * 点击被关闭
     */
    public void clickDeviceStateAsClosed() {
        solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_my_manager_set_device_state_closed));
    }

    public boolean waitForAddDelayPage() {
        return waitFor.waitForAsWeb(By.xpath(ControlInfo.web_add_time_add)
                , 5000, Msg.IntoAddDelayPageFailed);
    }

    /**
     * 在添加延迟页面点击开关
     */
    public void clickSwitchDelayInDelay() {
        solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_add_time_add));
    }

    /**
     * 在添加延迟页面点击完成
     */
    public void clickFinishInDelay() {
        solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_add_time_finish));
    }

    /**
     * 添加延迟页面，设置时
     *
     * @param h
     */
    public void setDelayHour(int h) {
        scroll.scroll("set_delay_h", 0, h);
    }

    /**
     * 添加延迟页面，设置分
     *
     * @param m
     */
    public void setDelayMinute(int m) {
        scroll.scroll("set_delay_m", 1, m);
    }


    /**
     * 设置设备状态
     *
     * @param state - 设备状态
     * @return      - 设置成功返回{@code true}，否则返回{@code false}
     */
    public boolean selectDeviceState(int state) {
        switch (state) {
            case DeviceState.KEY_STATE1:
                clickDeviceState1();
                break;
            case DeviceState.KEY_STATE2:
                clickDeviceState2();
                break;
            case DeviceState.KEY_STATE3:
                clickDeviceState3();
                break;
            case DeviceState.KEY_OPEN:
                clickDeviceStateAsOpen();
                break;
            case DeviceState.KEY_CLOSE:
                clickDeviceStateAsClose();
                break;
            case DeviceState.KEY_LEAKAGE:
                clickDeviceStateLeakage();
                break;
            case DeviceState.KEY_FIRE_ALARM:
                clickDeviceStateAsFireAlarm();
                break;
            case DeviceState.KEY_TEMPERATURE_HIGH:
                clickDeviceStateAsTemperatureHigh();
                break;
            case DeviceState.KEY_TEMPERATURE_LOW:
                clickDeviceStateAsTemperatureLow();
                break;
            case DeviceState.KEY_HUMIDITY_HIGH:
                clickDeviceStateAsHumidityHigh();
                break;
            case DeviceState.KEY_HUMIDITY_LOW:
                clickDeviceStateAsHumidityLow();
                break;
            case DeviceState.KEY_OPENED:
                clickDeviceStateAsOpened();
                break;
            case DeviceState.KEY_CLOSED:
                clickDeviceStateAsClosed();
                break;
            case DeviceState.KEY_LEAKAGE1:
                clickDeviceStateLeakage1();
                break;
            case DeviceState.KEY_LEAKAGE2:
                break;
            case DeviceState.KEY_LEAKAGE3:
                break;
            case DeviceState.KEY_Drag:
                dragProgressBar();
                break;
            default:
                return false;
        }

        return true;
    }

    /**
     * 拉动页面元素到什么位置(窗帘电机)
     * @param webElement
     */
    public void dragOnWebElement(WebElement webElement){
        float x = webElement.getLocationX();
        float y = webElement.getLocationY();
        solo.drag(x,(x+200),y,y,15);
    }

    /**
     * 拉动窗帘
     */

    public void dragProgressBar(){
        dragOnWebElement(getter.getWebElementByXpath(ControlInfo.web_curtainBtn));
        solo.clickOnWebElement(By.textContent("确定"));
    }

    /**
     * 搜索字符串
     *
     * @param searchText - 搜索内容
     */
    public void searchText(String searchText, int timeout) {
        if (!solo.waitForText(searchText, 0, timeout, true)) {
            MessageUtils.append(Msg.SearchTextFailed + searchText + "'！");
        }
    }

    public void searchTast(String searchText, int timeout) {
        scrollInH(searchText);
    }

    public void keepScroll(String searchText) {
        for(int i=0;i<=100;i++){
            WebElement webElement=solo.getWebElement(By.textContent("我的管家"),0);
            int[] position = new int[2];
            webElement.getLocationOnScreen(position);
            float fromX = position[0];
            float fromY = position[1];
            solo.drag(fromX, fromX, fromY, fromY - 350, 20);
//            solo.drag(470,470,730,500,30);
            if(solo.searchText(searchText)){
                break;
            }else if (i==30){
                MessageUtils.append("已经滑动三十次");
            }
        }
    }

    /**
     * 在管家列表滑动，找text
     * @param searchText
     */
    public void scrollInH(String searchText){
        View view = getter.getView(ControlInfo.tv_houseKeeper,0);
        int[] position = new int[2];
        try{
            view.getLocationOnScreen(position);
        }catch (Exception e){
            MessageUtils.append("发生错误");
        }
        float fromX = position[0];
        float fromY = position[1];
       while(true){
           int i=0;
           solo.drag(fromX,fromX,fromY+400,fromY,40);
           if(solo.searchText(searchText)){
               break;
           }else if(i==50){
               MessageUtils.append(Msg.SearchTextFailed + searchText + "'！");
           }
           i++;
       }

    }

    /**
     * 删除管家任务
     */
    public void deleteTast(){
        View view = getter.getView(ControlInfo.tv_houseKeeper,0);
        int[] position = new int[2];
        try{
            view.getLocationOnScreen(position);
        }catch (Exception e){
            MessageUtils.append("发生错误");
        }
        float fromX = position[0];
        float fromY = position[1]+130;
        while(true){
            solo.drag(fromX,fromX-180,fromY,fromY,40);
            if(solo.searchText("删除")){
                solo.clickOnView(getter.getView("tv_delete"));
            }else{
                break;
            }
        }
    }


    /**
     * 搜索到不存在的字符串
     * @param searchText - 字符串
     */
    public void searchNoneText(String searchText){
        if (solo.waitForText(searchText, 0 ,2000, true)) {
            MessageUtils.append(Msg.SearchNoneTextFailed + searchText + "'！");
        }
    }
    /**
     * 点击某个控件后进入登录界面
     *
     * @param id - 被点击的控件的id
     * @return   - 进入登录界面，返回{@code true}，否则返回{@code false}
     */
    public void clickToLogin(String id) {
        click.clickToAnotherActivity(id, ActivitiesName.SigninActivity, Msg.IntoLoginFailed);
    }

    /**
     * 获取首页"导航栏"的子元素
     *
     * @param index - 子元素的索引，从{@code 0}开始
     * @return      - 获取子元素成功则返回子元素{@link android.view.View}，否则返回{@link java.lang.NullPointerException}
     */
    public View getNavigationChild(int index) {
        int indexTemp = 0 > index ? 0 : index;
        LinearLayout linearLayout = getNavigationArea();
        if (null == linearLayout || 4 > linearLayout.getChildCount()) {
            MessageUtils.append(Msg.GetNavigationBarFailed);
            return null;
        }

        switch (indexTemp) {
            case 0:
                return linearLayout.getChildAt(0);
            case 1:
                return linearLayout.getChildAt(1);
            case 2:
                return linearLayout.getChildAt(2);
            case 3:
                return linearLayout.getChildAt(3);
            case 4:
                return linearLayout.getChildAt(4);
            default:
                MessageUtils.append(Msg.GetNavigationBarIndexError);
                return null;
        }
    }
    /**
     * 进入设备列表强制下拉刷新
     */
    public void forceRefresh(){
        solo.clickOnView(getNavigationChild(1));
//		solo.drag(200,200,200,700,12);
        dragViewOnScreen(ControlInfo.lv_device,0,300);
        solo.clickOnView(getNavigationChild(4));
    }

    /**
     * 获取首页导航栏
     *
     * @return - 首页导航栏容器{@link android.widget.LinearLayout}
     */
    private LinearLayout getNavigationArea() {
        return (LinearLayout) getter.getView(ControlInfo.bottom_navigation_bar_item_container);
    }

    /**
     * 拖拽view在屏幕上下滑动
     * @param id
     * @param match
     * @param count
     */
    public void dragViewOnScreen(String id,int match,int count){
        View view = getter.getView(id,match);
        int[] position = new int[2];
        try{
            view.getLocationOnScreen(position);
        }catch (Exception e){
            MessageUtils.append("发生错误");
        }
        float fromX = position[0];
        float fromY = position[1];
        solo.drag(fromX,fromX,fromY,(fromY+count),40);
//        solo.drag(fromX,fromX,(fromY+count1),fromY+count2,40);
    }

    public void dragViewOnScreen(String id,int match,int count1,int count2){
        View view = getter.getView(id,match);
        int[] position = new int[2];
        try{
            view.getLocationOnScreen(position);
        }catch (Exception e){
            MessageUtils.append("发生错误");
        }
        float fromX = position[0];
        float fromY = position[1];
        solo.drag(fromX,fromX,(fromY+count1),(fromY+count2),40);
    }

    /**
     * 场景排序,拖动图标到相应的位置
     *
     * @param index
     */
    public void dragSceneSort(int index) {
        View view = getter.getView(ControlInfo.drag_handle, index);
        //将view的左上角坐标存入数组中.此坐标是相对当前activity而言
        int[] position = new int[2];
        view.getLocationOnScreen(position);
        float fromX = position[0];
        float fromY = position[1];
        solo.drag(fromX, fromX, fromY, fromY + 300, 40);
    }

    /**
     * 拖拽view在屏幕左右滑动
     *
     * @param id
     * @param match
     * @param count
     */
    public void dragViewOnScreenLeftOrRight(String id, int match, int count) {
        View view = getter.getView(id, match);
        int[] position = new int[2];
        view.getLocationOnScreen(position);
        float fromX = position[0];
        float fromY = position[1];
        solo.drag(fromX, (fromX + count), fromY, fromY, 40);
    }

    /**
     * 我的页面向上滑动
     */
    public void scrollUpInMine(){
        dragViewOnScreen(ControlInfo.item_gateway_center,0,500);
    }

    /**
     * 我的页面向下滑动
     */
    public void scrollDownInMine(){
        dragViewOnScreen(ControlInfo.item_gateway_center,0,-500);
    }

    /**
     * 点击通用返回按钮
     */
    public void clickNormalBack() {
        solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_more));
    }

    /**
     * 在添加延迟页面点击再次打开延迟开关
     */
    public void clickSwitchDelayAgainInDelay() {
        solo.clickOnWebElement(getter.getWebElementByXpath(ControlInfo.web_add_time_add_again));
    }

    /**
     * 输入验证码即输入安卓keycode
     */
    public void enterAndroidKeycode(String keycode){
        //"123456"
        for (int i=1;i<=keycode.length();i++){
            int num = Integer.parseInt(keycode.substring(i-1,i));
            switch (num) {
                case 0:
                    solo.sendKey(KeyEvent.KEYCODE_0); // 0
                    break;
                case 1:
                    solo.sendKey(KeyEvent.KEYCODE_1);// 1
                    break;
                case 2:
                    solo.sendKey(KeyEvent.KEYCODE_2);// 2
                    break;
                case 3:
                    solo.sendKey(KeyEvent.KEYCODE_3);// 3
                    break;
                case 4:
                    solo.sendKey(KeyEvent.KEYCODE_4);// 4
                    break;
                case 5:
                    solo.sendKey(KeyEvent.KEYCODE_5);// 5
                    break;
                case 6:
                    solo.sendKey(KeyEvent.KEYCODE_6);// 6
                    break;
                case 7:
                    solo.sendKey(KeyEvent.KEYCODE_7);// 7
                    break;
                case 8:
                    solo.sendKey(KeyEvent.KEYCODE_8);// 8
                    break;
                case 9:
                    solo.sendKey(KeyEvent.KEYCODE_9);// 9
                    break;
            }
        }
    }


    /**
     * 封装设备状态
     */
    public static final class DeviceState {
        /**
         * 设防/停止报警
         */
        public static final int KEY_STATE1 = 0;

        /**
         * 撤防/通用报警
         */
        public static final int KEY_STATE2 = 1;

        /**
         * 火灾报警
         */
        public static final int KEY_STATE3 = 2;

        /**
         * 打开
         */
        public static final int KEY_OPEN = 3;

        /**
         * 关闭
         */
        public static final int KEY_CLOSE = 4;

        /**
         * 有漏水/有烟雾/有可燃气体泄漏/设防下有人经过
         */
        public static final int KEY_LEAKAGE = 5;

        /**
         * 设防下恢复正常
         */
        public static final int KEY_LEAKAGE1 = 13;

        /**
         * 撤防下有人经过
         */
        public static final int KEY_LEAKAGE2 = 14;

        /**
         * 撤防下恢复正常
         */
        public static final int KEY_LEAKAGE3 = 15;

        /**
         * 消警
         */
        public static final int KEY_FIRE_ALARM = 6;

        /**
         * 高于指定温度
         */
        public static final int KEY_TEMPERATURE_HIGH = 7;

        /**
         * 低于指定温度
         */
        public static final int KEY_TEMPERATURE_LOW = 8;

        /**
         * 高于指定湿度
         */
        public static final int KEY_HUMIDITY_HIGH = 9;

        /**
         * 定于指定湿度
         */
        public static final int KEY_HUMIDITY_LOW = 10;

        /**
         * 被打开
         */
        public static final int KEY_OPENED = 11;

        /**
         * 被关闭
         */
        public static final int KEY_CLOSED = 12;

        /**
         * 拉动窗帘电机
         */
        public static final int KEY_Drag = 16;

    }
    /**
     * 错误信息
     */
    private final static class Msg {
        public static final String intoSigninActivityFailed="未进入登录页面！";
        public static final String intoSettingActivityFailed="未进入设置页面！";
        public static final String intoForgotAccountActivityFailed="未进入忘记密码页面！";
        public static final String IntoHomePageFailed = "进入首页失败！";
        public static final String IntoLoginFailed = "未进入登录界面！";
        public static final String IntoHomeEditFailed = "进入快捷方式界面！";
        public static final String GetNavigationBarFailed = "获取导航栏失败！";
        public static final String GetNavigationBarIndexError = "获取导航栏时索引大于4！";
        public static final String GetSourceMMSFailed = "获取原始短信失败！";
        public static final String GetNewMMSFailed = "获取新短信失败！";
        public static final String GetMMSCodeFailed = "获取短信验证码失败！";
        public static final String IntoRegisterFailed = "进入注册界面失败！";
        public static final String IntoForgetPasswordFailed = "进入忘记密码界面失败！";
        public static final String IntoConfirmPasswordFailed ="进入修改账号密码页面失败";
        public static final String IntoGatewayCenterFailed = "进入网关中心界面失败！";
        public static final String IntoGatewaySettingFailed = "进入网关设置界面失败！";
        public static final String IntoGatewayListFailed = "进入网关列表界面失败！";
        public static final String IntoBindGatewayFailed = "进入绑定网关界面失败！";
        public static final String IntoMessageCenterFailed = "进入消息中心界面失败！";
        public static final String IntoPersonalInfoFailed = "进入个人信息界面失败！";
        public static final String IntoChangePhoneNumberFailed = "进入更改手机号界面失败！";
        public static final String IntoBindMailFailed = "进入绑定邮箱界面失败！";
        public static final String IntoConfirmMailFailed = "进入验证邮箱界面失败！";
        public static final String IntoAccountSecurityFailed = "进入账号安全页失败！";
        public static final String IntoDeviceMoreFailed = "进入设备更多界面失败！";
        public static final String IntoDeviceMoreAreaFailed = "进入更多分区界面失败！";
        public static final String IntoDeviceDetailFailed = "进入设备详情界面失败！";
        public static final String IntoAreaFailed = "进入分区管理失败！";
        public static final String IntoAllSceneFailed = "进入全部场景界面失败！";
        public static final String IntoAddSceneFailed = "进入添加场景界面失败！";
        public static final String IntoEditSceneFailed = "进入编辑场景界面失败！";
        public static final String IntoSceneSortFailed = "进入场景排序界面失败！";
        public static final String IntoEditScenePageFailed = "未展示编辑场景页面！";
        public static final String IntoCustomScenePageFailed = "未展示自定义场景页面！";
        public static final String IntoAddDevicePageFailed = "未展示添加设备页面！";
        public static final String IntoDeviceStatePageFailed = "未展示设置设备状态页面！";
        public static final String IntoAddDelayPageFailed = "未展示添加延迟页面！";
        public static final String IntoGatewayLoginFailed = "进入网关登录界面失败！";
        public static final String IntoSplashFailed = "启动欢迎界面失败！";
        public static final String LoadTemperatureFailed = "加载选择温度页面失败！";
        public static final String LoadHumidityFailed = "加载选择湿度页面失败！";
        public static final String SearchTextFailed = "页面中未匹配到 '";
        public static final String SearchNoneTextFailed = "页面中匹配到 '";
        public static final String IntoMessageSettingFailed = "进入消息设置界面失败";
        public static final String IntoMessageAlarmFailed = "进入报警消息页面失败！";
        public static final String IntoMessageLogFailed = "进入日志页面失败！";
        public static final String IntoForgotAccountActivityFailed= "进入首页忘记密码页面失败！";
        public static final String intoForgotVerificationActivityFailed="进入输入验证码页面失败";
    }

}
