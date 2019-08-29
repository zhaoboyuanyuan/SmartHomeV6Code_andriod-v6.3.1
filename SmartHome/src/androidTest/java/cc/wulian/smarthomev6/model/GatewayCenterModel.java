package cc.wulian.smarthomev6.model;

/**
 * Created by 严君 on 2017/5/9.
 */
public class GatewayCenterModel extends BaseProcModel {
	
	/**
	 * 条目设备号
	 */
	private String deviceNumber;
	
	/**
	 * 条目中的选中按钮是否展示
	 */
	private boolean isShown;
	
	/**
	 * 构造器
	 *
	 * @param actions - 动作集{@link java.util.Arrays}
	 */
	public GatewayCenterModel(int[] actions) {
		super(actions);
	}
	
	/**
	 * 构造器
	 *
	 * @param deviceNumber - 条目设备号{@link String}
	 * @param isShown      - 选中按钮展示则参数为{@code true}，否则{@code false}
	 * @param actions      - 动作集{@link java.util.Arrays}
	 */
	public GatewayCenterModel(String deviceNumber, boolean isShown, int[] actions) {
		super(actions);
		this.deviceNumber = deviceNumber;
	}

	/**
	 * 获取设备号
	 *
	 * @return - 条目设备号{@link String}
	 */
	public String getDeviceNumber() {
		return deviceNumber;
	}

	/**
	 * 设置条目的设备号
	 *
	 * @param deviceNumber - 条目设备号{@link String}
	 */
	public void setDeviceNumber(String deviceNumber) {
		this.deviceNumber = deviceNumber;
	}
	
	/**
	 * 条目的选中按钮是否展示
	 *
	 * @return - 选中按钮展示则参数为{@code true}，否则{@code false}
	 */
	public boolean isShown() {
		return isShown;
	}
	
	/**
	 * 设置条目的选中按钮是否展示
	 *
	 * @param shown  - 选中按钮展示则参数为{@code true}，否则{@code false}
	 */
	public void setShown(boolean shown) {
		isShown = shown;
	}
}
