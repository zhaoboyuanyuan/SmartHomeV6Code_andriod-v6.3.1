package cc.wulian.smarthomev6.tst;

import cc.wulian.smarthomev6.app.StartApp;
import cc.wulian.smarthomev6.proc.SceneSortAndSearchProc;
import com.wtt.runner.android.IAfterCondition;
import com.wtt.runner.android.IBeforeCondition;

/**
 * 场景排序和搜索场景测试用例
 * Created by 赵永健 on 2017/11/6.
 */
public class SceneSortAndSearchTest extends StartApp implements IBeforeCondition,IAfterCondition {
    private SceneSortAndSearchProc proc;

    @Override
    public void before() {
        proc = new SceneSortAndSearchProc(getSolo());
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
     * 1.场景顺序排列
     */
    public void testSceneSortAsSuccess(){
        proc.sceneSortAsSuccess();
    }

//    /**
//     * 2.取消场景顺序排列
//     */
//    public void testSceneSortAsCancel(){
//       proc.sceneSortAsCancel();
//    }

    /**
     * 3.恢复默认顺序
     */
    public void testSceneSortAsReset(){
       proc.sceneSortAsReset();
    }

//    /**
//     * 4.根据关键字搜索场景
//     */
//    public void testSearchSceneByName(){
//        proc.searchSceneByName();
//    }
//
//    /**
//     * 5.根据首字母搜索场景
//     */
//    public void testSearchSceneByLetter(){
//       proc.searchSceneByLetter();
//    }
//
//    /**
//     * 6.取消搜索
//     */
//    public void testSearchSceneCancel(){
//       proc.searchSceneCancel();
//    }


}
