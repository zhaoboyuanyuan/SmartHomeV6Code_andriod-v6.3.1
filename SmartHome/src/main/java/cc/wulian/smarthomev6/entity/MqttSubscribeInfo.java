package cc.wulian.smarthomev6.entity;

/**
 * Created by Administrator on 2017/2/15 0015.
 */

public class MqttSubscribeInfo {
    private String topic;
    private String object;

    public MqttSubscribeInfo(){

    }

    public MqttSubscribeInfo(String topic, String object){
        this.topic = topic;
        this.object = object;
    }

    public String gettopic() {
        return topic;
    }

    public void settopic(String topic) {
        this.topic = topic;
    }

    public String getobject() {
        return object;
    }

    public void setobject(String object) {
        this.object = object;
    }
}
