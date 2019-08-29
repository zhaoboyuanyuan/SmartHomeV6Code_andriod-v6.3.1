package cc.wulian.smarthomev6.app;

import junit.framework.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 赵永健 on 2017/6/22.
 */
public class ScrollInfo {

	private static Map<String, DragLocation> dragLocation = new HashMap<String, DragLocation>();
	
	static {
		dragLocation.put("set_time_h", new DragLocation());
		dragLocation.put("set_time_m", new DragLocation());
		dragLocation.put("set_delay_h", new DragLocation());
		dragLocation.put("set_delay_m", new DragLocation());
		dragLocation.put("set_condition_h", new DragLocation());
		dragLocation.put("set_condition_m", new DragLocation());
		dragLocation.put("set_temperature",new DragLocation());
		dragLocation.put("set_humidity",new DragLocation());
		dragLocation.put("set_year",new DragLocation());

	}
	
	public static Map<String, DragLocation> getDragLocationMap() {
		return dragLocation;
	}
	
	public static void setDragLocation(Map<String, DragLocation> scrollInfo) {
		ScrollInfo.dragLocation = scrollInfo;
	}
	
	public static DragLocation getDragLocation(String key) {
		if (!dragLocation.containsKey(key)) assertFailed(key);
		
		return dragLocation.get(key);
	}
	
	public static boolean isFirst(String key) {
		if (!dragLocation.containsKey(key)) assertFailed(key);
		
		return dragLocation.get(key).isFirst();
	}
	
	public static int getToX(String key) {
		if (!dragLocation.containsKey(key)) assertFailed(key);
		return dragLocation.get(key).getToX();
	}
	
	public static int getToY(String key) {
		if (!dragLocation.containsKey(key)) assertFailed(key);
		return dragLocation.get(key).getToY();
	}
	
	public static int getFromX(String key) {
		if (!dragLocation.containsKey(key)) assertFailed(key);
		return dragLocation.get(key).getFromX();
	}
	
	public static int getFormY(String key) {
		if (!dragLocation.containsKey(key)) assertFailed(key);
		return dragLocation.get(key).getFromY();
	}
	
	public static void put(String key, DragLocation drag) {
		dragLocation.put(key, drag);
	}
	
	public static void replace(String key, DragLocation drag) {
		if (dragLocation.containsKey(key)) {
			dragLocation.remove(key);
		}
		put(key, drag);
	}
	
	private static void assertFailed(String key) {
		Assert.assertTrue("Key '" + key + "' is no exist!", false);
	}
}
