package cc.wulian.smarthomev6.model;

/**
 * Created by Administrator on 2017/9/1.
 */
public class BDLockModel extends BaseProcModel {

    private String adminPassword;
    private String userName;
    private String userPassword;
    private String buttonTxt;
    private String searchtext;

    public String getSearchtext() {
        return searchtext;
    }

    public void setSearchtext(String searchtext) {
        this.searchtext = searchtext;
    }

    public String getButtonTxt() {
        return buttonTxt;
    }

    public void setButtonTxt(String buttonTxt) {
        this.buttonTxt = buttonTxt;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public BDLockModel(int[] actions) {
        super(actions);
    }
}
