package cc.wulian.smarthomev6.support.core.mqtt.cmd;

/**
 * Created by Veev on 2017/4/14
 * Tel: 18365264930
 * QQ: 2355738466
 * Function:
 */

public interface CmdHandle {
    void handle(String msgContent) throws Exception;
}
