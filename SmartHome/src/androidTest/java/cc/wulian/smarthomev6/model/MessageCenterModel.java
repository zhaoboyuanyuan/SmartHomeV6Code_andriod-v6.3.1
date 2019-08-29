package cc.wulian.smarthomev6.model;

/**
 * Created by 赵永健 on 2017/11/3.
 */
public class MessageCenterModel extends BaseProcModel{
    private String deviceName1;
    private String deviceName2;
    private String searchText;

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public String getDeviceName1() {
        return deviceName1;
    }

    public void setDeviceName1(String deviceName1) {
        this.deviceName1 = deviceName1;
    }

    public String getDeviceName2() {
        return deviceName2;
    }

    public void setDeviceName2(String deviceName2) {
        this.deviceName2 = deviceName2;
    }

    public MessageCenterModel(int[] actions) {
        super(actions);
    }

}
