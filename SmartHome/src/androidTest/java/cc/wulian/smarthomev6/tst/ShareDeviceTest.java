package cc.wulian.smarthomev6.tst;

import cc.wulian.smarthomev6.app.StartApp;
import cc.wulian.smarthomev6.proc.ShareDeviceProc;
import com.wtt.runner.android.IAfterCondition;
import com.wtt.runner.android.IBeforeCondition;

/**
 * Created by 赵永健 on 2017/9/5.
 */
public class ShareDeviceTest extends StartApp implements IBeforeCondition,IAfterCondition {
    private ShareDeviceProc proc;

    @Override
    public void before() {
        proc = new ShareDeviceProc(getSolo());
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
     * 1.取消分享成功
     */
    public void testCShareCancelSuccess() {
        proc.shareCancelSuccess();
    }

    /**
     * 2..取消分享
     */
    public void testBShareCancel(){
        proc.shareCancel();
    }

    /**
     * 3.分享成功
     */
    public void testAccountSuccess(){
      proc.accountSuccess();
    }

    /**
     * 4.用户手机号不正确
     */
    public void testDAccountFail(){
        proc.accountFail();
    }

    /**
     * 5.设备已与用户绑定
     */
    public void testBAccountUsed(){
        proc.accountUsed();
    }
    /**
     * 6.此账号不存在
     */
    public void testDAccountNone(){
        proc.accountNone();
    }


//    /**
//     * 7.取消分享WiFi设备成功
//     */
//    public void testShareWifiDeviceCancel(){
//        proc.shareWifiDeviceCancel();
//    }
//
//    /**
//     * 8.分享wifi设备成功
//     */
//    public void testShareWifiDeviceSuccess(){
//        proc.shareWifiDeviceSuccess();
//    }

    /**
     * 9.查看分享管理页面
     */
    public void testViewShareManager(){
        proc.viewShareManager();
    }

    /**
     * 10.查看分享列表
     */
    public void testViewShareList(){
        proc.viewShareList();
    }

}
