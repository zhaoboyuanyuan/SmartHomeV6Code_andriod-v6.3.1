package cc.wulian.smarthomev6.app;

/**
 * Created by 赵永健 on 2017/6/22.
 */
public class DragLocation {
	
	public int toX = -1;
	public int toY = -1;
	public int fromX = -1;
	public int fromY = -1;
	public boolean isFirst = true;
	
	public int getToX() {
		return toX;
	}
	
	public void setToX(int toX) {
		this.toX = toX;
	}
	
	public int getToY() {
		return toY;
	}
	
	public void setToY(int toY) {
		this.toY = toY;
	}
	
	public int getFromX() {
		return fromX;
	}
	
	public void setFromX(int fromX) {
		this.fromX = fromX;
	}
	
	public int getFromY() {
		return fromY;
	}
	
	public void setFromY(int fromY) {
		this.fromY = fromY;
	}
	
	public boolean isFirst() {
		return isFirst;
	}
	
	public void setFirst(boolean first) {
		isFirst = first;
	}
}
