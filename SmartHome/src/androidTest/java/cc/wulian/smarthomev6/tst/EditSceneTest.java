package cc.wulian.smarthomev6.tst;

import cc.wulian.smarthomev6.app.StartApp;
import cc.wulian.smarthomev6.proc.EditSceneProc;
import com.wtt.runner.android.IAfterCondition;
import com.wtt.runner.android.IBeforeCondition;

/**
 * Created by 赵永健 on 2017/6/23.
 */
public class EditSceneTest extends StartApp implements IBeforeCondition, IAfterCondition {

	private EditSceneProc proc;

	@Override
	public void before() {
		proc = new EditSceneProc(getSolo());
		proc.init();
	}

	@Override
	public void after() {
		if (null != proc) {
			proc.finalize();
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

	public void testAEditSceneAsTime1() {
		proc.editSceneAsTime1();
	}

	public void testAEditSceneAsTime99() {
		proc.editSceneAsTime99();
	}

	public void testAEditSceneAsTime1SaveAndBack() {
		proc.editSceneAsTime1SaveAndBack();
	}

	public void testAEditSceneAsTime99SaveAndBack() {
		proc.editSceneAsTime99SaveAndBack();
	}

	public void testAEditSceneAsTime1Back() {
		proc.editSceneAsTime1Back();
	}

	public void testAEditSceneAsTime99Back() {
		proc.editSceneAsTime99Back();
	}

	public void testAEditSceneAsTime1Cancel() {
		proc.editSceneAsTime1Cancel();
	}

	public void testAEditSceneAsTime99Cancel() {
		proc.editSceneAsTime99Cancel();
	}

	//s
	public void testEditSceneNoneDelay() {
		proc.editSceneNoneDelay();
	}

	public void testEditSceneNoneDelayAndBack() {
		proc.editSceneNoneDelayAndBack();
	}

	//s
	public void testDeleteSceneCancel() {
		proc.deleteSceneCancel();
	}

	//s
	public void testDeleteSceneConfirm() {
		proc.deleteSceneConfirm();
	}
}
