package cc.wulian.smarthomev6.model;

/**
 * Created by Administrator on 2017/9/12.
 */
public class FeedbackModel extends BaseProcModel {
    private String buttontxt;
    private String buffer;
    private String searchtext;
    private int remainCount;
    private String email;


    public FeedbackModel(int[] actions) {
        super(actions);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getButtontxt() {
        return buttontxt;
    }

    public void setButtontxt(String buttontxt) {
        this.buttontxt = buttontxt;
    }

    public String getBuffer() {
        return buffer;
    }

    public void setBuffer(String buffer) {
        this.buffer = buffer;
    }

    public String getSearchtext() {
        return searchtext;
    }

    public void setSearchtext(String searchtext) {
        this.searchtext = searchtext;
    }

    public int getRemainCount() {
        return remainCount;
    }

    public void setRemainCount(int remainCount) {
        this.remainCount = remainCount;
    }
}
