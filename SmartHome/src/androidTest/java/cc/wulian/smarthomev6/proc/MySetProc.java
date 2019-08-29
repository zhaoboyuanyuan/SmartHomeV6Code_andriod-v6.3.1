package cc.wulian.smarthomev6.proc;

import android.view.View;
import cc.wulian.smarthomev6.app.AccountInfo;
import cc.wulian.smarthomev6.app.ActivitiesName;
import cc.wulian.smarthomev6.app.ControlInfo;
import cc.wulian.smarthomev6.model.MySetModel;
import cc.wulian.smarthomev6.utils.MessageUtils;
import com.wtt.frame.robotium.Solo;

/**
 * Created by 赵永健 on 2017/11/7.
 */
public class MySetProc extends BaseProc<MySetModel> {

    public MySetProc(Solo solo) {
        super(solo);
    }

    /**
     * 1.打开推送通知
     */
    public void openPushNotification(){
        MySetModel model=new MySetModel(new int[]{0,1});
        baseProcess(model);
    }

    /**
     * 2.关闭推送通知
     */
    public void closePushNotification(){
        MySetModel model=new MySetModel(new int[]{0,2});
        baseProcess(model);
    }

    /**
     * 3.打开报警振动
     */
    public void openAlarmVid(){
        MySetModel model=new MySetModel(new int[]{0,3});
        baseProcess(model);
    }

    /**
     * 4.关闭报警振动
     */
    public void closeAlarmVid(){
        MySetModel model=new MySetModel(new int[]{0,4});
        baseProcess(model);
    }

    /**
     * 5.打开报警语音
     */
    public void openAlarmVoice(){
        MySetModel model=new MySetModel(new int[]{0,5,6});
        baseProcess(model);
    }

    /**
     * 6.关闭报警语音
     */
    public void closeAlarmVoice(){
        MySetModel model=new MySetModel(new int[]{0,5,7});
        baseProcess(model);
    }

    /**
     * 7.切换语速
     */
    public void changeVoiceSpeed(){
        MySetModel model=new MySetModel(new int[]{0,5,8});
        model.setVoiceSpeed("慢");
        baseProcess(model);
    }
    /**
     * 8.切换语种
     */
    public void changeVoiceKind(){
        MySetModel model=new MySetModel(new int[]{0,5,9});
        model.setVoiceName("台湾话");
        baseProcess(model);
    }

    /**
     * 9.切换推送时间
     */
    public void changePushTime(){
        MySetModel model=new MySetModel(new int[]{0,10,11,12,13});
        model.setHourCount(200);
        model.setMinCount(200);
        model.setHourCount1(500);
        model.setMinCount1(-500);
        baseProcess(model);
    }

    /**
     * 10.清理缓存
     */
    public void clearCache(){
        MySetModel model=new MySetModel(new int[]{0,14});
        baseProcess(model);
    }

    /***
     * 错误信息
     */
    public static final class Msg{
        public static final String intoSettingActivityFail="未进入设置页面";
        public static final String intoAlarmVoiceActivityFail="未进入报警语音页面";
        public static final String intoPushTimeActivityFail="未进入推送时间页面";
        public static final String voiceSpeedFail="报警语速未被选中";
        public static final String voiceKindFail="报警语种未被选中";
    }

    @Override
    public void process(MySetModel model, int action) {
        switch(action){
            case 0:
                clickToSetting();
                break;
            case 1:
                clickPush();//打开推送通知
                break;
            case 2:
                clickPush();//关闭推送通知
                break;
            case 3:
                clickAlarmVib();//打开报警振动
                break;
            case 4:
                clickAlarmVib();//关闭报警振动
                break;
            case 5:
                clickToAlarmVoice();
                break;
            case 6:
                clickAlarmVoice();//打开报警语音
                break;
            case 7:
                clickAlarmVoice();//关闭报警语音
                break;
            case 8:
                if(!clickSpeed(model.getVoiceSpeed())){
                    MessageUtils.append(Msg.voiceSpeedFail);
                }
                break;
            case 9:
                if(!clickKind(model.getVoiceName())){
                   MessageUtils.append( Msg.voiceKindFail);
                }
                break;
            case 10:
                clickPushTime();
                break;
            case 11:
                clickFrom(model.getHourCount(),model.getMinCount());
                break;
            case 12:
                clickTo(model.getHourCount1(),model.getMinCount1());
                break;
            case 13:
                solo.clickOnView(getter.getView(ControlInfo.btn_right));
                break;
            case 14:
                clickClear();
                break;
        }
    }

    @Override
    public void init() {
        commonProc.login(AccountInfo.Account1, AccountInfo.Password1);
        commonProc.forceRefresh();
    }

    /**
     * 点击设置
     * @return
     */
    public void clickToSetting(){
        commonProc.scrollDownInMine();
        click.clickToAnotherActivity(ControlInfo.item_setting,
                ActivitiesName.SettingActivity, Msg.intoSettingActivityFail);
    }

    /**
     * 打开/关闭推送
     */
    public void clickPush(){
        solo.clickOnView(getter.getView(ControlInfo.item_setting_alarm_user));
    }

    /**
     * 打开/关闭报警振动
     */
    public void clickAlarmVib(){
        solo.clickOnView(getter.getView(ControlInfo.item_setting_alarm_shake));
    }

    /**
     * 点击进入报警语音页面
     * @return
     */
    public void clickToAlarmVoice(){
         click.clickToAnotherActivity(ControlInfo.item_setting_alarm_voice,
                ActivitiesName.AlarmVoiceActivity, Msg.intoAlarmVoiceActivityFail);
    }

    /**
     * 打开/关闭报警语音
     */
    public void clickAlarmVoice(){
        solo.clickOnView(getter.getView(ControlInfo.item_remind_alarm_voice));
    }

    /**
     * 点击语速,判断是否选中
     */
    public boolean clickSpeed(String voiceSpeed){
        solo.clickOnText(voiceSpeed);
        View view = getter.getView(ControlInfo.item_alarm_voice_2);
        if(view!=null){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 点击语种，判断是否选中
     * @return
     */
    public boolean clickKind(String voiceName){
        commonProc.dragViewOnScreen("item_alarm_voice_mandarin",0,300);
        solo.clickOnText(voiceName);
        View view = getter.getView(ControlInfo.item_check);
        if(view!=null){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 点击推送时间
     * @return
     */
    public void clickPushTime(){
        click.clickToAnotherActivity(ControlInfo.item_setting_push_time,
                ActivitiesName.PushTimeActivity, Msg.intoPushTimeActivityFail);
    }

    /**
     * 点击从-
     * @param hourCount
     * @param minCount
     */
    public void clickFrom(int hourCount,int minCount){
        solo.clickOnView(getter.getView(ControlInfo.item_push_time_start));
        commonProc.dragViewOnScreen(ControlInfo.time_hour,0,hourCount);
        commonProc.dragViewOnScreen(ControlInfo.time_min,0,minCount);
    }

    /**
     * 点击至-
     * @param hourCount1
     * @param minCount1
     */
    public void clickTo(int hourCount1,int minCount1){
        solo.clickOnView(getter.getView(ControlInfo.item_push_time_end));
        commonProc.dragViewOnScreen(ControlInfo.time_hour,0,hourCount1);
        commonProc.dragViewOnScreen(ControlInfo.time_min,0,minCount1);
    }

    /**
     * 清理缓存
     */
    public void clickClear(){
        solo.clickOnView(getter.getView(ControlInfo.item_setting_cache));
    }
}
