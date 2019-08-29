package cc.wulian.smarthomev6.support.core.mqtt.parser;

/**
 * Created by zbl on 2017/3/3.
 */

public interface IMQTTMessageParser {
    void parseMessage(int tag, String topic, String payload);
}
