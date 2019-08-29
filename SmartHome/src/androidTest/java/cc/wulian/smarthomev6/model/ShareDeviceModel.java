package cc.wulian.smarthomev6.model;

/**
 * Created by Administrator on 2017/9/4.
 */
public class ShareDeviceModel extends BaseProcModel {

    private String userNumber;
    private String searchText;
    private String ButtonText;
    private int listIndex;
    private String selectIndex;
    private String gatewayName;

    public String getGatewayName() {
        return gatewayName;
    }

    public void setGatewayName(String gatewayName) {
        this.gatewayName = gatewayName;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public String getButtonText() {
        return ButtonText;
    }

    public void setButtonText(String buttonText) {
        ButtonText = buttonText;
    }

    public int getListIndex() {
        return listIndex;
    }

    public void setListIndex(int listIndex) {
        this.listIndex = listIndex;
    }

    public String getSelectIndex() {
        return selectIndex;
    }

    public void setSelectIndex(String selectIndex) {
        this.selectIndex = selectIndex;
    }

    public ShareDeviceModel(int[] actions) {
        super(actions);
    }
}
