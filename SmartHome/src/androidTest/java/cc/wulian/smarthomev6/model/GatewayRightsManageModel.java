package cc.wulian.smarthomev6.model;

import java.util.Arrays;

/**
 * Created by Administrator on 2017/6/12.
 */
public class GatewayRightsManageModel extends BaseProcModel {
    private String mobileNum;
    private String searchText;
    private int index;

    private int listIndex;
    private String deviceName;
    private String deviceNameCheck;
    private String buttonText;
    private boolean isRename;
    private int area;
    private String ORDeviceName;

    public String getORDeviceName() {
        return ORDeviceName;
    }

    public void setORDeviceName(String ORDeviceName) {
        this.ORDeviceName = ORDeviceName;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceNameCheck() {
        return deviceNameCheck;
    }

    public void setDeviceNameCheck(String deviceNameCheck) {
        this.deviceNameCheck = deviceNameCheck;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public boolean isRename() {
        return isRename;
    }

    public void setRename(boolean rename) {
        isRename = rename;
    }

    public int getListIndex() {
        return listIndex;
    }

    public void setListIndex(int listIndex) {
        this.listIndex = listIndex;
    }



    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getMobileNum() {
        return mobileNum;
    }

    public void setMobileNum(String mobileNum) {
        this.mobileNum = mobileNum;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }
    /**
     * 构造器
     *
     * @param actions - 动作集{@link Arrays}
     */
    public GatewayRightsManageModel(int[] actions) {
        super(actions);
    }
}
