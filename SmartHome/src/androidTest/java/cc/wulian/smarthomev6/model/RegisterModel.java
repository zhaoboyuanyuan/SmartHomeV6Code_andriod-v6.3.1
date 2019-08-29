package cc.wulian.smarthomev6.model;

import java.util.Arrays;

/**
 * Created by Administrator on 2017/6/8.
 */
public class RegisterModel extends BaseProcModel{
    private String  MobileNum;
    private String VerificationCode;
    private String password;
    private String searchText;
    private int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getMobileNum() {
        return MobileNum;
    }

    public void setMobileNum(String mobileNum) {
        MobileNum = mobileNum;
    }

    public String getVerificationCode() {
        return VerificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        VerificationCode = verificationCode;
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

    /**
     * 构造器
     *
     * @param actions - 动作集{@link Arrays}
     */
    public RegisterModel(int[] actions) {
        super(actions);
    }
}
