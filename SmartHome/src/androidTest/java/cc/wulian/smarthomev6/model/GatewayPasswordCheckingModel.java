package cc.wulian.smarthomev6.model;

/**
 * Created by Administrator on 2017/8/11.
 */
public class GatewayPasswordCheckingModel extends BaseProcModel {
    private String gatewayName;
    private String GatewayPassword;
    private String newPassword;
    private String confirmPassword;
    private String searchText;

    /**
     * 构造器
     *
     */
    public GatewayPasswordCheckingModel(int[] actions) {
        super(actions);
    }

    public String getGatewayName() {
        return gatewayName;
    }

    public void setGatewayName(String gatewayName) {
        this.gatewayName = gatewayName;
    }

    public String getGatewayPassword() {
        return GatewayPassword;
    }

    public void setGatewayPassword(String GatewayPassword) {
        this.GatewayPassword = GatewayPassword;
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
    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }
}
