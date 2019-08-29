package cc.wulian.smarthomev6.model;

/**
 * Created by 赵永健 on 2018/1/17.
 */
public class LoginModel extends BaseProcModel{
    private String userName;
    private String password;
    private String searchText;
    private String buttonText;
    public LoginModel(int[] actions) {
        super(actions);
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }
}
