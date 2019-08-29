package cc.wulian.smarthomev6.support.core.device;

import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceBean;
import cc.wulian.smarthomev6.support.core.mqtt.bean.GatewayInfoBean;

/**
 * 封装网关信息缓存的操作
 */
public class GatewayManager {
    public void saveGatewayInfo(DeviceBean deviceBean) {
        GatewayDataEntityDao dao = MainApplication.getApplication().getDaoSession().getGatewayDataEntityDao();
        GatewayDataEntity gatewayDataEntity = dao.queryBuilder()
                .where(GatewayDataEntityDao.Properties.GwID.eq(deviceBean.deviceId))
                .build()
                .unique();
        if (gatewayDataEntity == null) {
            gatewayDataEntity = new GatewayDataEntity(deviceBean);
            dao.insert(gatewayDataEntity);
        } else {
            gatewayDataEntity.setData(deviceBean);
            dao.update(gatewayDataEntity);
        }
    }

    public void saveGatewayInfo(GatewayInfoBean gatewayInfoBean) {
        GatewayDataEntityDao dao = MainApplication.getApplication().getDaoSession().getGatewayDataEntityDao();
        GatewayDataEntity gatewayDataEntity = dao.queryBuilder()
                .where(GatewayDataEntityDao.Properties.GwID.eq(gatewayInfoBean.gwID))
                .build()
                .unique();
        if (gatewayDataEntity == null) {
            gatewayDataEntity = new GatewayDataEntity(gatewayInfoBean);
            dao.insert(gatewayDataEntity);
        } else {
            gatewayDataEntity.setData(gatewayInfoBean);
            dao.update(gatewayDataEntity);
        }
    }

    public GatewayDataEntity getGatewayInfo(String gwID) {
        GatewayDataEntityDao dao = MainApplication.getApplication().getDaoSession().getGatewayDataEntityDao();
        GatewayDataEntity gatewayDataEntity = dao.queryBuilder()
                .where(GatewayDataEntityDao.Properties.GwID.eq(gwID))
                .build()
                .unique();
        return gatewayDataEntity;
    }
}
