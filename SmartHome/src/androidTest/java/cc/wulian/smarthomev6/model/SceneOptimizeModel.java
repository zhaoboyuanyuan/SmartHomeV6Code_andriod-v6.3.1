package cc.wulian.smarthomev6.model;

/**
 * 场景优化数据
 */
public class SceneOptimizeModel extends BaseProcModel {
    private String deviceName;
    private String searchText;
    private int index;
    private String sceneName;
    private String webText;

    public SceneOptimizeModel(int[] actions) {
        super(actions);
    }

    public String getWebText() {
        return webText;
    }

    public void setWebText(String webText) {
        this.webText = webText;
    }

    public String getSceneName() {
        return sceneName;
    }


    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }
}
