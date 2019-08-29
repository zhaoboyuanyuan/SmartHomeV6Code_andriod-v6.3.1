package cc.wulian.smarthomev6.support.event;

/**
 * Created by zbl on 2018/6/21.
 * 提醒用户升级网关固件
 */

public class GatewaySoftwareUpdateEvent {

    /**
     * 0 不需要升级，1 需要升级
     */
    public int status;
    public String gatewayID;
    public String oldVersion;
    public String newVersion;
}
