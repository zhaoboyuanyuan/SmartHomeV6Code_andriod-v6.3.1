package cc.wulian.smarthomev6.model;

public class Cpabout extends BaseProcModel {
    public Cpabout(int[] actions) {
        super(actions);
    }
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
