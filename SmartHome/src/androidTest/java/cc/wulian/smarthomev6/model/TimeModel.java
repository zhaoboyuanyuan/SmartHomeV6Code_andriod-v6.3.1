package cc.wulian.smarthomev6.model;

/**
 * Created by 严君 on 2017/6/16.
 *
 * 封装时间的数据模型
 */
public class TimeModel {

	private int hour;
	private int minute;
	
	public TimeModel() {}
	
	public TimeModel(int hour, int minute) {
		this.hour = hour;
		this.minute = minute;
	}
	
	public int getHour() {
		return hour;
	}
	
	public void setHour(int hour) {
		this.hour = hour;
	}
	
	public int getMinute() {
		return minute;
	}
	
	public void setMinute(int minute) {
		this.minute = minute;
	}
}
