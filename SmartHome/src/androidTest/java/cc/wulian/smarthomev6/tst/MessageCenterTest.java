package cc.wulian.smarthomev6.tst;

import cc.wulian.smarthomev6.app.StartApp;
import cc.wulian.smarthomev6.proc.MessageCenterProc;
import com.wtt.runner.android.IAfterCondition;
import com.wtt.runner.android.IBeforeCondition;

/**
 * 消息中心测试用例，包涵报警消息和日志
 * Created by 赵永健 on 2017/11/3.
 */
public class MessageCenterTest extends StartApp implements IBeforeCondition,IAfterCondition {
    private MessageCenterProc proc;

    @Override
    public void before() {
        proc = new MessageCenterProc(getSolo());
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
     * 1.查看消息中心--报警消息
     */
    public void testViewAlarm(){
        proc.viewAlarm();
    }

    /**
     * 2.查看消息中心--具体设备的报警消息
     */
    public void testViewDeviceAlarm(){
        proc.viewDeviceAlarm();
    }

    /**
     * 3.查看消息中心--日志
     */
    public void testViewLog(){
        proc.viewLog();
    }

    /**
     * 4.查看消息中心--具体设备的报警消息
     */
    public void testViewDeviceLog(){
        proc.viewDeviceLog();
    }

    /**
     * 5.点击首页铃铛--网关登录
     */
    public void testGatewayLoginClick(){
        proc.gatewayLoginClick();
    }

}
