package cc.wulian.smarthomev6.main.device;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cc.wulian.smarthomev6.entity.RoomInfo;
import cc.wulian.smarthomev6.entity.RoomInfoDao;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.device.RoomCache;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.RoomBean;
import cc.wulian.smarthomev6.support.tools.Preference;

/**
 * Created by 王伟 on 2017/3/17
 * Tel: 18365264930
 * QQ: 2355738466
 * Function: 场景管理类
 */

public class AreaManager {

    public static final String TAG = AreaManager.class.getSimpleName();

    private Context context;
    private RoomInfoDao roomDao;

    public AreaManager(Context context) {
        this.context = context;
        roomDao = MainApplication.getApplication().getDaoSession().getRoomInfoDao();
    }

    private static RoomInfoDao getDao() {
        return MainApplication.getApplication().getDaoSession().getRoomInfoDao();
    }

    /**
     * 收到场景列表时， 更新数据库
     *
     * 加锁, 避免数据不同步
     */
    public synchronized static void saveRoomList() {
        String gwId = Preference.getPreferences().getCurrentGatewayID();
        // 数据库中的数据
        List<RoomInfo> list = getDao()
                .queryBuilder()
                .where(RoomInfoDao.Properties.GwID.eq(gwId))
                .orderDesc(RoomInfoDao.Properties.Sort)
                .list();

        // 获取最后一个sort
        int lastSort = list.isEmpty() ? 0 : list.get(0).getSort();

        RoomCache roomCache = MainApplication.getApplication().getRoomCache();
        // 本地的数据
        Collection<RoomBean> cacheCollection = roomCache.getDevices();
        ArrayList<RoomBean> cacheList = new ArrayList<>(cacheCollection);
        // 删除cache列表中没有的数据
        for (RoomInfo info : list) {
            if (!roomCache.hasRoom(info.getRoomID())) {
                getDao().delete(info);
            }
        }
        // 将数据库中没有的本地数据, 存到数据库
        for (RoomBean bean : cacheList) {
            RoomInfo info = getDao().queryBuilder()
                                .where(RoomInfoDao.Properties.GwID.eq(gwId), RoomInfoDao.Properties.RoomID.eq(bean.roomID))
                                .unique();
            if (info == null) {
                info = new RoomInfo(bean);
                info.setGwID(gwId);
                info.setSort(lastSort++);
                getDao().insert(info);
            } else {
                // 需要更新时, 才会更新数据库
                if (info.update(bean)) {
                    getDao().update(info);
                }
            }
        }
    }

    /**
     * 删除分区
     */
    public boolean deleteRoom(RoomInfo room) {
        if (MainApplication.getApplication().isSceneCached()) {
            // MainApplication.getApplication().getSceneCache().add(scene.getBean());
            MainApplication.getApplication().getMqttManager()
                    .publishEncryptedMessage(
                            MQTTCmdHelper.createSetRoomInfo(
                                    Preference.getPreferences().getCurrentGatewayID(),
                                    3,
                                    room.getRoomID(),
                                    room.getName(),
                                    null),
                            MQTTManager.MODE_GATEWAY_FIRST);
        }

        return true;
    }

    /**
     * 加载分区
     */
    public List<RoomInfo> loadDiskRoom() {
        List<RoomInfo> list;
        // 只显示当前网管下的 场景
        String gwId = Preference.getPreferences().getCurrentGatewayID();
        list = roomDao.queryBuilder()
                .where(RoomInfoDao.Properties.GwID.eq(gwId))
                .orderAsc(RoomInfoDao.Properties.Sort)
                .list();
        return list;
    }


    public List<RoomInfo> acquireDefaultSortRoom() {
        // 只显示当前网关下的 场景
        String gwId = Preference.getPreferences().getCurrentGatewayID();
        return roomDao.queryBuilder()
                .where(RoomInfoDao.Properties.GwID.eq(gwId))
                .orderAsc(RoomInfoDao.Properties.Id)
                .list();
    }

    /**
     * 通过 分区id  获取分区
     */
    public RoomInfo getRoomById(String roomID) {
        String gwID = Preference.getPreferences().getCurrentGatewayID();

        // id 为空
        if (TextUtils.isEmpty(gwID) || TextUtils.isEmpty(roomID)) {
            return null;
        }

        List<RoomInfo> list = roomDao.queryBuilder().where(RoomInfoDao.Properties.GwID.eq(gwID),
                RoomInfoDao.Properties.RoomID.eq(roomID)).list();
        // 没有值
        if (list.isEmpty()) {
            return null;
        }

        return list.get(0);
    }

    /**
     * 保存场景顺序
     */
    public synchronized void saveOrder(List<RoomInfo> RoomInfos) {
        int i = 0;
        for (RoomInfo info : RoomInfos) {
            info.setSort(i++);
        }
        roomDao.updateInTx(RoomInfos);
    }
}
