package cc.wulian.smarthomev6.support.core.apiunit.bean;

import java.io.Serializable;

/**
 * 获取某类型网关最新版本号
 */

public class GatewayLastVersionBean implements Serializable {
    public String type;
    public String version;
    public String latestVersion;
}
