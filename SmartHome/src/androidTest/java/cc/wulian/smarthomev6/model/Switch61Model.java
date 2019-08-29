package cc.wulian.smarthomev6.model;

/**
 * 一路开关
 * Created by 赵永健 on 2018/1/11.
 */
public class Switch61Model extends BaseProcModel{
    public Switch61Model(int[] actions) {
        super(actions);
    }
    private String deviceName;
    private String webMore;
    private String sceneName;
    private String newOneName;
    private String buttonText;

    public String getNewOneName() {
        return newOneName;
    }

    public void setNewOneName(String newOneName) {
        this.newOneName = newOneName;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getWebMore() {
        return webMore;
    }

    public void setWebMore(String webMore) {
        this.webMore = webMore;
    }
}
