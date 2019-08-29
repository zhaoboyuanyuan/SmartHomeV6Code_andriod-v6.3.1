package cc.wulian.smarthomev6.tst;

import android.util.Log;

import com.wtt.runner.android.IAfterCondition;
import com.wtt.runner.android.IBeforeCondition;

import cc.wulian.smarthomev6.app.StartApp;
import cc.wulian.smarthomev6.proc.CpaboutPro;

public class CpaboutTest extends StartApp implements IBeforeCondition,IAfterCondition {
    private CpaboutPro proc;

    @Override
    public void before() {
        proc=new CpaboutPro(super.getSolo());
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
     * 查看功能介绍页面
     */
    public void testViewFunction(){
        proc.function();
    }

    /**
     * 查看关于物联页面
     */
    public void testViewYboutUs(){
        proc.aboutWulian();
    }

//    public void testOne(){
//        throw new RuntimeException("运行错误");
//    }
}
