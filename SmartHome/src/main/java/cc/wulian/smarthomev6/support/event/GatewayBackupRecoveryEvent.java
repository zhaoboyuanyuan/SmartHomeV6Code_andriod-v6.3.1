package cc.wulian.smarthomev6.support.event;

import cc.wulian.smarthomev6.support.core.apiunit.bean.GatewayBackupRecoveryBean;

public class GatewayBackupRecoveryEvent {
    public GatewayBackupRecoveryBean bean;

    public GatewayBackupRecoveryEvent(GatewayBackupRecoveryBean bean) {
        this.bean = bean;
    }
}
