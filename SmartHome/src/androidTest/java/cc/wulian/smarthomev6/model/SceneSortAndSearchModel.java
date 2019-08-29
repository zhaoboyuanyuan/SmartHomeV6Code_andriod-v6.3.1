package cc.wulian.smarthomev6.model;

/**
 * Created by 赵永健 on 2017/11/6.
 */
public class SceneSortAndSearchModel extends BaseProcModel{
    private String sceneName;
    private String letter;
    private int index;

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public SceneSortAndSearchModel(int[] actions) {
        super(actions);
    }
}
