package cc.wulian.smarthomev6.model;

/**
 * Created by Administrator on 2017/9/13.
 */
public class Switch16AjModel extends BaseProcModel {

    private String deviceName;
    private String searchText;
    private String devicePartition;
    private String buttonTxt;
    private int area;
    private String orDeviceName;
    private String AswitchName;
    private String toMore;
    private int deviceIndex;

    public void setDeviceIndex(int deviceIndex) {
        this.deviceIndex = deviceIndex;
    }

    public int getDeviceIndex() {
        return deviceIndex;
    }

    public String getToMore() {
        return toMore;
    }

    public void setToMore(String toMore) {
        this.toMore = toMore;
    }

    public String getAswitchName() {
        return AswitchName;
    }

    public void setAswitchName(String aswitchName) {
        AswitchName = aswitchName;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public String getOrDeviceName() {
        return orDeviceName;
    }

    public void setOrDeviceName(String orDeviceName) {
        this.orDeviceName = orDeviceName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public String getDevicePartition() {
        return devicePartition;
    }

    public void setDevicePartition(String devicePartition) {
        this.devicePartition = devicePartition;
    }

    public String getButtonTxt() {
        return buttonTxt;
    }

    public void setButtonTxt(String buttonTxt) {
        this.buttonTxt = buttonTxt;
    }

    public Switch16AjModel(int[] actions) {
        super(actions);
    }
}
