package cc.wulian.smarthomev6.model;

/**
 * 金属单路开关
 * Created by 赵永健 on 2017/11/13.
 */
public class SwitchAmModel extends BaseProcModel{
    private String searchText;
    private String deviceName;
    private String bindDeviceName;
    private String sceneName;
    private String titleName;
    private String buttonText;
    private String switchName;
    private String bindMode;
    private String widgetSwitchName;
    private String enterName;
    private String dialogButton;
    private int deviceIndex;

    public int getDeviceIndex() {
        return deviceIndex;
    }

    public void setDeviceIndex(int deviceIndex) {
        this.deviceIndex = deviceIndex;
    }

    public String getDialogButton() {
        return dialogButton;
    }

    public void setDialogButton(String dialogButton) {
        this.dialogButton = dialogButton;
    }

    public String getEnterName() {
        return enterName;
    }

    public void setEnterName(String enterName) {
        this.enterName = enterName;
    }

    public String getWidgetSwitchName() {
        return widgetSwitchName;
    }

    public void setWidgetSwitchName(String widgetSwitchName) {
        this.widgetSwitchName = widgetSwitchName;
    }

    public String getBindMode() {
        return bindMode;
    }

    public void setBindMode(String bindMode) {
        this.bindMode = bindMode;
    }

    public String getSwitchName() {
        return switchName;
    }

    public void setSwitchName(String switchName) {
        this.switchName = switchName;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getBindDeviceName() {
        return bindDeviceName;
    }

    public void setBindDeviceName(String bindDeviceName) {
        this.bindDeviceName = bindDeviceName;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public SwitchAmModel(int[] actions) {
        super(actions);
    }
}
