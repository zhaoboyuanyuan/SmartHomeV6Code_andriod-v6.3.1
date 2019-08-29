package cc.wulian.smarthomev6.tst;

import com.wtt.runner.android.IAfterCondition;
import com.wtt.runner.android.IBeforeCondition;

import cc.wulian.smarthomev6.app.StartApp;
import cc.wulian.smarthomev6.model.SceneOptimizeModel;
import cc.wulian.smarthomev6.proc.SceneOptimizeProc;


public class SceneOptimizeTest extends StartApp implements IBeforeCondition,IAfterCondition {

    private SceneOptimizeProc proc;

    @Override
    public void before() {
        proc = new SceneOptimizeProc(getSolo());
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
     * 1、场景再次编辑成功
     */
    public void testSceneEditA(){
        proc.sceneEditA();
    }

    /**
     * 2、网关登录时场景再次编辑
     */
    public void testSceneEditAsGL()
    {
        proc.sceneEditAsGL();
    }

    /**
     * 3、场景批量添加成功
     */
    public void testBatchAdditionSucc(){
        proc.batchAdditionSucc();
    }

    /**
     * 4、点击全选变为全不选
     */
    public void testSelectAllAndNot(){
        proc.selectAllAndNot();
    }

    /**
     * 5、点击全不选变为全选
     */
    public void testCSAN(){
        proc.clickSelectAllAndNot();
    }

    /**
     * 6、点击我知道了
     */
    public void testNoneSelect(){
        proc.noneSelect();
    }
}
