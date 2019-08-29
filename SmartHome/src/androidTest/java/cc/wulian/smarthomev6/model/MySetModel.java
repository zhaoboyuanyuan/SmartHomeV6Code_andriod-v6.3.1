package cc.wulian.smarthomev6.model;

/**
 * Created by 赵永健 on 2017/11/6.
 */
public class MySetModel extends BaseProcModel{
    private String voiceName;
    private String voiceSpeed;
    private String id;
    private int hourCount;
    private int minCount;

    private int hourCount1;
    private int minCount1;

    public int getHourCount1() {
        return hourCount1;
    }

    public void setHourCount1(int hourCount1) {
        this.hourCount1 = hourCount1;
    }

    public int getMinCount1() {
        return minCount1;
    }

    public void setMinCount1(int minCount1) {
        this.minCount1 = minCount1;
    }

    public int getHourCount() {
        return hourCount;
    }

    public void setHourCount(int hourCount) {
        this.hourCount = hourCount;
    }

    public int getMinCount() {
        return minCount;
    }

    public void setMinCount(int minCount) {
        this.minCount = minCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVoiceName() {
        return voiceName;
    }

    public void setVoiceName(String voiceName) {
        this.voiceName = voiceName;
    }

    public String getVoiceSpeed() {
        return voiceSpeed;
    }

    public void setVoiceSpeed(String voiceSpeed) {
        this.voiceSpeed = voiceSpeed;
    }

    public MySetModel(int[] actions) {
        super(actions);
    }
}
