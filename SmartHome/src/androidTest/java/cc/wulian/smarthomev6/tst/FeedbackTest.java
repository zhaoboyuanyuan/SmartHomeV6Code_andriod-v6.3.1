package cc.wulian.smarthomev6.tst;

import cc.wulian.smarthomev6.app.StartApp;
import cc.wulian.smarthomev6.proc.FeedbackProc;
import com.wtt.runner.android.IAfterCondition;
import com.wtt.runner.android.IBeforeCondition;

/**
 * Created by 赵永健 on 2017/9/12.
 */
public class FeedbackTest extends StartApp implements IAfterCondition,IBeforeCondition {
    private FeedbackProc proc;

    @Override
    public void before() {
        proc = new FeedbackProc(getSolo());
        proc.init();
    }

    @Override
    public void after() {
        if(proc!=null){
            try {
                proc.finalize();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        proc=null;
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
     *1.输入为空时，提交按钮不可点击
     */
    public void testAEnterNull(){
        proc.enterNull();
    }

    /**
     * 2.输入3个字符
     */
    public void testEnterBuffer3(){
        proc.enterBuffer3();
    }

    /**
     * 3.输入超过300个字符
     */
    public void testEnterBufferMore300(){
        proc.enterBufferMore300();
    }

    /**
     * 4.游客访问点击意见反馈跳转到登录页面
     */
    public void testVisitor(){
        proc.Visitor();
    }

    /**
     * 5.网关直接登录意见反馈被隐藏
     */
    public void testFeedBackHide(){
        proc.feedBackHide();
    }
}
