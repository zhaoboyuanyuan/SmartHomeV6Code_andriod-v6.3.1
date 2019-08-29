package cc.wulian.smarthomev6.model;

/**
 * 关于页面
 * Created by 赵永健 on 2017/11/7.
 */
public class AboutModel extends BaseProcModel{
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public AboutModel(int[] actions) {
        super(actions);
    }
}
