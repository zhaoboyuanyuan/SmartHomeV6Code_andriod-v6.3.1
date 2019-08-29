package cc.wulian.smarthomev6.proc;


import cc.wulian.smarthomev6.app.Common;
import cc.wulian.smarthomev6.model.BaseProcModel;
import cc.wulian.smarthomev6.utils.*;
import com.wtt.frame.robotium.Solo;
import junit.framework.Assert;

/**
 * Created by 赵永健 on 2017/5/8.
 *
 * 封装测试用例流程处理基础类
 */
public abstract class BaseProc<T extends BaseProcModel> {
	
	protected Solo solo;
	protected Common commonProc;
	public StringBuilder builder;
	protected GetterUtils getter;
	protected EnterUtils enter;
	protected ClickUtils click;
	protected WaitForUtils waitFor;
	protected ScrollUtils scroll;
	
	public BaseProc(Solo solo) {
		this.solo = solo;
		commonProc = new Common(solo);
		getter=new GetterUtils(solo);
		enter=new EnterUtils(solo,getter);
		waitFor=new WaitForUtils(solo);
		click=new ClickUtils(solo,getter,waitFor);
		scroll=new ScrollUtils(solo);

	}
	
	/**
	 * 测试用例基本流程处理
	 *
	 */
	protected void baseProcess(T model) {
		if (null == model) {
			builder.append("\"Object of BaseProcModel\" is null!");
			Assert.assertTrue(builder.toString(),false);
			return;
		}
		
		int[] actions = model.getActions();
		if (null == actions || 0 == actions.length) {
			builder.append("\"Arrays of actions\" is null or empty!");
			Assert.assertTrue(builder.toString(),false);
			return;
		}
		
		for (Integer action : actions) {
			process(model, action);
		}
	}
	
	/**
	 * 动作处理
	 *
	 * @param action - 动作{@link Integer}
	 */
	public abstract void process(T model, int action);

	/**
	 * 流程初始化
	 */
	public abstract void init();

	/**
	 * 垃圾回收
	 *
	 * @throws Throwable - {@link Throwable}
	 */
	@Override
	public void finalize() throws Throwable {
		commonProc = null;
		builder=null;
		getter=null;
		enter=null;
		click=null;
		scroll=null;
		waitFor=null;
		super.finalize();
	}
}
