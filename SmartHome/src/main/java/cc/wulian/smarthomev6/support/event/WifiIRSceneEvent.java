package cc.wulian.smarthomev6.support.event;

/**
 * created by huxc  on 2018/9/11.
 * func：wifi红外场景管家
 * email: hxc242313@qq.com
 */

public class WifiIRSceneEvent {
    public String action;
    public String deviceId;
    public String codeType;
    public String blockId;
    public String keyId;
    public String blockName;

    public WifiIRSceneEvent(String action, String deviceId, String codeType, String blockId, String keyId, String blockName) {
        this.action = action;
        this.deviceId = deviceId;
        this.codeType = codeType;
        this.blockId = blockId;
        this.keyId = keyId;
        this.blockName = blockName;
    }
}
