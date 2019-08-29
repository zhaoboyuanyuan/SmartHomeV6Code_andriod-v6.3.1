package cc.wulian.smarthomev6.tst;

import cc.wulian.smarthomev6.app.StartApp;
import cc.wulian.smarthomev6.proc.MySetProc;
import com.wtt.runner.android.IAfterCondition;
import com.wtt.runner.android.IBeforeCondition;

/**
 * 我的-设置，包涵推送通知，报警推送，报警语音，推送时间和清理缓存
 * Created by 赵永健 on 2017/11/7.
 */
public class MySetTest extends StartApp implements IBeforeCondition,IAfterCondition {
    private MySetProc proc;

    @Override
    public void before() {
        proc = new MySetProc(getSolo());
        proc.init();
    }

    @Override
    public void after() {
        if (null != proc) {
            try {
                proc.finalize();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            proc = null;
        }
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        before();
    }

    @Override
    public void tearDown() throws Exception {
        after();
        super.tearDown();

    }

    /**
     * 1.打开推送通知
     */
    public void testAOpenPushNotification(){
        proc.openPushNotification();
    }

    /**
     * 2.关闭推送通知
     */
    public void testAClosePushNotification(){
        proc.closePushNotification();
    }

    /**
     * 3.打开报警振动
     */
    public void testOpenAlarmVid(){
        proc.openAlarmVid();
    }

    /**
     * 4.关闭报警振动
     */
    public void testCloseAlarmVid(){
        proc.closeAlarmVid();
    }

    /**
     * 5.打开报警语音
     */
    public void testBOpenAlarmVoice(){
        proc.openAlarmVoice();
    }

    /**
     * 6.关闭报警语音
     */
    public void testBCloseAlarmVoice(){
        proc.closeAlarmVoice();
    }

    /**
     * 7.切换语速
     */
    public void testChangeVoiceSpeed(){
        proc.changeVoiceSpeed();
    }
    /**
     * 8.切换语种
     */
    public void testChangeVoiceKind(){
        proc.changeVoiceKind();
    }

    /**
     * 9.切换推送时间
     */
    public void testChangePushTime(){
        proc.changePushTime();
    }

    /**
     * 10.清理缓存
     */
    public void testClearCache(){
        proc.clearCache();
    }
}
