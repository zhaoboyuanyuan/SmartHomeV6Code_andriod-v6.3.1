package cc.wulian.smarthomev6.support.core.mqtt.bean;

/**
 * Created by yuxx on 2017/6/7.
 * 场景开关绑定信息，该类用于513，514命令传递参数
 */

public class SceneBindingBean {
    /**
     * 发起设备的端口号
     */
    public String endpointNumber;
    /**
     * 被绑定的场景ID，从场景列表中选择
     */
    public String sceneID;
}
