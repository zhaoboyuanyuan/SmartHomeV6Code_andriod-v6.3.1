package cc.wulian.smarthomev6.entity;

/**
 * Created by zbl on 2017/9/25.
 * 语音控制接口返回
 */

public class VoiceControlResultBean {
    public String sequence;
    public String versionid;
    public boolean is_end;
    public long timestamp;
    public VoiceControlDirectiveBean directive;
}
