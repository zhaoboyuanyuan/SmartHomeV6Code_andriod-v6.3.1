/**
 * 应用程序执行任务线程组
 */
package cc.wulian.smarthomev6.support.utils;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
public class TaskExecutor {
	private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
	private static final int CORE_POOL_SIZE = CPU_COUNT * 2;
	private static TaskExecutor instance = new TaskExecutor();
	private ScheduledExecutorService service = Executors.newScheduledThreadPool(CORE_POOL_SIZE);
	private Map<Runnable,ScheduledFuture<?>> futures = new ConcurrentHashMap<Runnable,ScheduledFuture<?>>();
	private TaskExecutor(){
		
	}
	
	public boolean isAllTaskFinished() {
		return service.isTerminated();
	}
	public void addScheduled(Runnable command, long initialDelay, long period, TimeUnit unit){
		if(!futures.containsKey(command)){
			ScheduledFuture<?> future = service.scheduleAtFixedRate(command, initialDelay, period, unit);
			futures.put(command, future);
		}
	}
	public ScheduledFuture<?> addScheduled(Callable<?> command, long initialDelay, long period, TimeUnit unit){
		return addScheduled(command, initialDelay, period, unit);
	}
	public void removeScheduled(Runnable r){
		ScheduledFuture<?> f = this.futures.get(r);
		if(f != null){
			f.cancel(true);
			this.futures.remove(r);
		}
	}
	public Future<?> execute(Runnable r){
		return this.service.submit(r);
	}
	public Future<Object> executeCallable(Callable<Object> r){
		return this.service.submit(r);
	}
	public Future<?> executeDelay(Runnable r,long delayTime,TimeUnit unit){
		return this.service.schedule(r, delayTime, TimeUnit.MILLISECONDS);
	}
	public void executeDelay(Runnable r,long delayTime){
		this.service.schedule(r, delayTime, TimeUnit.MILLISECONDS);
	}
	public static TaskExecutor getInstance(){
		return instance;
	}
	
	public void shutdown(){
		for(Runnable r :futures.keySet()){
			removeScheduled(r);
		}
		futures.clear();
		this.service.shutdown();
	}
}
