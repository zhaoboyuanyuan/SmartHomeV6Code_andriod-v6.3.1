package cc.wulian.smarthomev6.tst;

import cc.wulian.smarthomev6.app.StartApp;
import cc.wulian.smarthomev6.proc.AboutProc;
import com.wtt.runner.android.IAfterCondition;
import com.wtt.runner.android.IBeforeCondition;

/**
 * 关于页面
 * Created by 赵永健 on 2017/11/7.
 */
public class AboutTest extends StartApp implements IBeforeCondition,IAfterCondition {
    private AboutProc proc;

    @Override
    public void before() {
        proc =new AboutProc(super.getSolo());
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
     * 1.功能介绍
     */
    public void testViewIntroduction(){
        proc.viewIntroduction();
    }

    /**
     * 2.关于物联
     */
    public void testViewAboutUs(){
        proc.viewAboutUs();
    }
}
