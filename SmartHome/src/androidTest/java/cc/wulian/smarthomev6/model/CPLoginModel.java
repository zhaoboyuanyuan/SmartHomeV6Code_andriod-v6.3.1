package cc.wulian.smarthomev6.model;

import java.util.Arrays;

/**
 * Created by 赵永健 on 2017/6/7.
 */
public class CPLoginModel extends BaseProcModel{

    private String account;
    private String password;
    private String searchText;
    private String newPassword;
    private String confirmPassword;
    private String buttonText;


    /**
     * 构造器
     *
     * @param actions - 动作集{@link Arrays}
     */
    public CPLoginModel(int[] actions) {
        super(actions);
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
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

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }
}
