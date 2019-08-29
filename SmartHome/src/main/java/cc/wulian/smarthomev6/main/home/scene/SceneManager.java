package cc.wulian.smarthomev6.main.home.scene;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.SceneInfo;
import cc.wulian.smarthomev6.entity.SceneInfoDao;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.device.SceneCache;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.SceneBean;
import cc.wulian.smarthomev6.support.event.SceneInfoEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by 王伟 on 2017/3/17
 * Tel: 18365264930
 * QQ: 2355738466
 * Function: 场景管理类
 */

public class SceneManager {

    public static final String TAG = SceneManager.class.getSimpleName();

    private static final String ICON_TYPE_NORMAL = "scene_normal_";
    private static final String ICON_TYPE_QUICK = "scene_quick_";
    private Context context;
    private SceneInfoDao sceneDao;

    public SceneManager(Context context) {
        this.context = context;
        sceneDao = MainApplication.getApplication().getDaoSession().getSceneInfoDao();
    }

    private static SceneInfoDao getDao() {
        return MainApplication.getApplication().getDaoSession().getSceneInfoDao();
    }

    /**
     * 收到场景列表时， 更新数据库
     * <p>
     * 加锁, 避免数据不同步
     */
    public synchronized static void saveSceneList() {
        String gwId = Preference.getPreferences().getCurrentGatewayID();
        // 数据库中的数据
        List<SceneInfo> list = getDao()
                .queryBuilder()
                .where(SceneInfoDao.Properties.GwID.eq(gwId))
                .orderDesc(SceneInfoDao.Properties.Sort)
                .list();

        // 获取最后一个sort
        int lastSort = list.isEmpty() ? 0 : list.get(0).getSort();

        SceneCache sceneCache = MainApplication.getApplication().getSceneCache();
        // 本地的数据
        Collection<SceneBean> cacheCollection = sceneCache.getScenes();
        ArrayList<SceneBean> cacheList = new ArrayList<>(cacheCollection);

        // 删除cache列表中没有的数据
        for (SceneInfo info : list) {
            if (!sceneCache.hasScene(info.getSceneID())) {
                getDao().delete(info);
            }
        }

        // 将数据库中没有的本地数据, 存到数据库
        for (SceneBean bean : cacheList) {
            SceneInfo info = getDao().queryBuilder()
                    .where(SceneInfoDao.Properties.GwID.eq(gwId), SceneInfoDao.Properties.SceneID.eq(bean.sceneID))
                    .unique();
            if (info == null) {
                info = new SceneInfo(bean);
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
     * 搜索功能
     *
     * @param key 关键字
     * @return 搜索结果
     */
    public List<SceneInfo> searchScenes(String key) {
        // do search here
        // return new list

        String g = Preference.getPreferences().getCurrentGatewayID();
        return sceneDao.queryBuilder()
                .where(SceneInfoDao.Properties.Name.like("%" + key + "%"),
                        SceneInfoDao.Properties.GwID.eq(g)).list();
    }

    /**
     * 恢复默认排序
     * <p>
     * 注意点：数据分为两批
     * 一批为默认数据，这部分数据要恢复默认排序，并且要考虑默认数据的删改情况
     * 一批为新添数据，这部分数据按照时间排序
     */
    public void resetOrder() {
        // 伪代码
        // listOrigin = getOriginDataBySQLite() {
        //                  where data is origin
        //                  esc sceneId
        //              }
        // listNew = getNewDataBySQLite() {
        //              where data not origin
        //              desc updatedAt
        //           };
        // listReturn = listOrigin + listNew
        //
        // resetOrder(listReturn)
    }

    /**
     * 删除场景
     */
    public boolean deleteScene(SceneInfo scene) {
        if (MainApplication.getApplication().isSceneCached()) {
            // MainApplication.getApplication().getSceneCache().add(scene.getBean());
            MainApplication.getApplication().getMqttManager()
                    .publishEncryptedMessage(
                            MQTTCmdHelper.createSetSceneInfo(
                                    Preference.getPreferences().getCurrentGatewayID(),
                                    3,
                                    scene.getSceneID(),
                                    scene.getName(),
                                    scene.getIcon(),
                                    null,
                                    scene.getGroupID()),
                            MQTTManager.MODE_GATEWAY_FIRST);
        }

        return true;
    }

    /**
     * 获取场景
     */
    public List<SceneInfo> acquireScene() {
        // 1、查询本地是否有【场景】，有则返回，无则返回默认场景
        // 2、获取云【场景】，与本地【场景】作比较：
        //      若云端较新，重设本地并刷新列表；
        //      若本地较新，更新云端

        boolean isLogin = Preference.getPreferences().isLogin();
        boolean emptyGateway = TextUtils.isEmpty(Preference.getPreferences().getCurrentGatewayID());
        boolean offLine = TextUtils.equals(Preference.getPreferences().getCurrentGatewayState(), "0");
        // 如果加载过，就先从缓存加载，若缓存没有，就从数据库获取；否则，清空数据库，并且加载默认
        if (isLogin) {
            if (emptyGateway || offLine) {
                return getDefaultScene();
            }

            // 加载本地的场景
            return loadDiskScene();
        } else {
            // 加载默认场景,清空数据库
            return getDefaultScene();
        }
    }

    public List<SceneInfo> acquireDefaultSortScene() {
        // 只显示当前网关下的 场景
        String gwId = Preference.getPreferences().getCurrentGatewayID();
        // 网关离线时, 返回默认场景
        if (TextUtils.equals(Preference.getPreferences().getCurrentGatewayState(), "0")) {
            return getDefaultScene();
        }
        return sceneDao.queryBuilder()
                .where(SceneInfoDao.Properties.GwID.eq(gwId))
                .orderAsc(SceneInfoDao.Properties.SceneIDInt)
                .list();
    }

    /**
     * 加载场景
     */
    private List<SceneInfo> loadDiskScene() {
        List<SceneInfo> list;
        // 只显示当前网管下的 场景
        String gwId = Preference.getPreferences().getCurrentGatewayID();
        list = sceneDao.queryBuilder()
                .where(SceneInfoDao.Properties.GwID.eq(gwId))
                .orderAsc(SceneInfoDao.Properties.Sort)
                .list();
//        list = sceneDao.queryBuilder().list();
        return list;
    }

    /**
     * 设置场景的 programId
     */
    public synchronized void setSceneProgramId(String sceneId, String programId) {
        String gwId = Preference.getPreferences().getCurrentGatewayID();
        if (gwId.isEmpty()) {
            return;
        }
        SceneInfo sceneInfo = sceneDao
                .queryBuilder()
                .where(SceneInfoDao.Properties.GwID.eq(gwId),
                        SceneInfoDao.Properties.SceneID.eq(sceneId))
                .unique();
        sceneInfo.setProgramID(programId);
        WLog.i(sceneInfo);
        sceneDao.save(sceneInfo);
        // 同步修改缓存
        MainApplication.getApplication().getSceneCache().get(sceneId).programID = programId;
        SceneBean bean = MainApplication.getApplication().getSceneCache().get(sceneId);
        bean.mode = 2;
        EventBus.getDefault().post(new SceneInfoEvent(bean));
    }

    /**
     * 是否存在这个 name 的场景
     * 场景不允许重复名称
     *
     * @param name    场景名称
     * @param sceneID 场景 ID
     * @return true        存在
     * false       不存在
     */
    public boolean isExistScene(String name, String sceneID) {
        if (TextUtils.isEmpty(name)) {
            return false;
        }
        String gwID = Preference.getPreferences().getCurrentGatewayID();

        // 如果sceneID不为空
        if (!TextUtils.isEmpty(sceneID)) {
            List<SceneInfo> list = sceneDao.queryBuilder().where(SceneInfoDao.Properties.GwID.eq(gwID), SceneInfoDao.Properties.SceneID.eq(sceneID)).list();
            boolean ii = false;
            for (SceneInfo info : list) {
                if (TextUtils.equals(info.getName(), name)) {
                    ii = true;
                }
            }
            // 说明没有改名字
            if (ii) {
                return true;
            }
        }

        return sceneDao.queryBuilder().where(SceneInfoDao.Properties.GwID.eq(gwID),
                SceneInfoDao.Properties.Name.eq(name)).list().size() == 0;
    }

    /**
     * 获取默认场景
     * <p>
     * 在数据库没有数据的情况下
     * 才会加载默认场景
     * 因此
     * 加载完默认场景要保存到数据库
     */
    private List<SceneInfo> getDefaultScene() {
        List<SceneInfo> list = new ArrayList<>();
        list.add(new SceneInfo("default_01", context.getResources().getString(R.string.Home_Scene_LJ), "01", "1"));
        list.add(new SceneInfo("default_02", context.getResources().getString(R.string.Home_Scene_HJ), "02", "1"));
        list.add(new SceneInfo("default_03", context.getResources().getString(R.string.Home_Scene_SJ), "03", "1"));
        list.add(new SceneInfo("default_04", context.getResources().getString(R.string.Home_Scene_QY), "04", "1"));
        list.add(new SceneInfo("default_05", context.getResources().getString(R.string.Home_Scene_QC), "05", "1"));
        list.add(new SceneInfo("default_06", context.getResources().getString(R.string.Home_Scene_HK), "06", "1"));
        list.add(new SceneInfo("default_07", context.getResources().getString(R.string.Home_Scene_JC), "07", "1"));
        list.add(new SceneInfo("default_08", context.getResources().getString(R.string.Home_Scene_YY), "08", "1"));
        list.add(new SceneInfo("default_09", context.getResources().getString(R.string.Home_Scene_YD), "09", "1"));
        /*for (int i = 1; i <= 9; i++) {
            list.add(new SceneInfo("default_0" + i,
                    context.getResources().getStringArray(R.array.Home_Scene)[i - 1],
                    "0" + i,
                    "1"));
        }*/

        // 仅仅给数据, 不清空数据库
//        saveScene(list);
//        Preference.getPreferences().setLoadDefaultScene();
        return list;
    }

    /**
     * 通过 场景id  获取场景
     */
    public SceneInfo getSceneById(String sceneID) {
        String gwID = Preference.getPreferences().getCurrentGatewayID();

        // id 为空
        if (TextUtils.isEmpty(gwID) || TextUtils.isEmpty(sceneID)) {
            return null;
        }

        List<SceneInfo> list = sceneDao.queryBuilder().where(SceneInfoDao.Properties.GwID.eq(gwID),
                SceneInfoDao.Properties.SceneID.eq(sceneID)).list();
        // 没有值
        if (list.isEmpty()) {
            return null;
        }

        return list.get(0);
    }

    /**
     * 打开场景
     * <p>
     * 先更改状态
     * 然后保存结果
     */
    public void toggleScene(SceneInfo scene) {
        String newStatus = scene.getStatus().equals("2") ? "1" : "2";
//        scene.setStatus(newStatus);
        // TODO: 2017/12/21 程序被杀, 重启后未获取网关列表
        if (MainApplication.getApplication().isSceneCached() || true) {
            // MainApplication.getApplication().getSceneCache().add(scene.getBean());
            MainApplication.getApplication().getMqttManager()
                    .publishEncryptedMessage(
                            MQTTCmdHelper.createSetSceneInfo(
                                    Preference.getPreferences().getCurrentGatewayID(),
                                    0,
                                    scene.getSceneID(),
                                    scene.getName(),
                                    scene.getIcon(),
                                    newStatus,
                                    scene.getGroupID()),
                            MQTTManager.MODE_GATEWAY_FIRST);
        }
        // sceneDao.update(scene);
    }

    public void snedMqttMessage(JSONObject obj) {
        MainApplication.getApplication().getMqttManager()
                .publishEncryptedMessage(
                        obj.toString(),
                        MQTTManager.MODE_GATEWAY_FIRST);
    }

    /**
     * 关闭当前所有打开的场景
     * 理论上只有一个
     */
    public void closeOpeningScenes() {
        List<SceneInfo> list = new ArrayList<>();
        if (MainApplication.getApplication().isSceneCached()) {
            SceneCache cache = MainApplication.getApplication().getSceneCache();
            for (SceneBean bean : cache.getScenes()) {
                if ("2".equals(bean.status)) {
                    bean.status = "1";
                    // cache.add(bean);
                    MainApplication.getApplication().getMqttManager()
                            .publishEncryptedMessage(
                                    MQTTCmdHelper.createSetSceneInfo(
                                            Preference.getPreferences().getCurrentGatewayID(),
                                            0,
                                            bean.sceneID,
                                            bean.name,
                                            bean.icon,
                                            "1",
                                            bean.groupID),
                                    MQTTManager.MODE_GATEWAY_FIRST);
                }
                list.add(new SceneInfo(bean));
                // TODO: 2017/4/12 这里需要同步更新数据库
            }
        } /*else {
            list = sceneDao.queryBuilder().where(SceneInfoDao.Properties.Status.eq("2")).list();
            for (SceneInfo info : list) {
                info.setStatus("1");
                sceneDao.save(info);
            }
        }*/
    }

    /**
     * 保存场景顺序
     */
    public synchronized void saveOrder(List<SceneInfo> sceneInfos) {
        int i = 0;
        for (SceneInfo info : sceneInfos) {
            info.setSort(i++);
        }
        sceneDao.updateInTx(sceneInfos);
    }

    public static Drawable getSceneIconNormal(Context context, String sceneIcon) {
        return getSceneIcon(context, sceneIcon, ICON_TYPE_NORMAL);
    }

    public static Drawable getSceneIconQuick(Context context, String sceneIcon) {
        return getSceneIcon(context, sceneIcon, ICON_TYPE_QUICK);
    }

    public static int getSceneIconResIdNormal(Context context, String sceneIcon) {
        return getSceneIconResId(context, sceneIcon, ICON_TYPE_NORMAL);
    }

    public static int getSceneIconResIdQuick(Context context, String sceneIcon) {
        return getSceneIconResId(context, sceneIcon, ICON_TYPE_QUICK);
    }

    public static String getRecommendSceneId(int position) {
        String iconId = null;
        switch (position) {
            case 0:
                iconId = "02";
                break;
            case 1:
                iconId = "01";
                break;
            case 2:
                iconId = "10";
                break;
            case 3:
                iconId = "11";
                break;
            case 4:
                iconId = "14";
                break;
            case 5:
                iconId = "15";
                break;
            case 6:
                iconId = "05";
                break;
            case 7:
                iconId = "03";
                break;
            case 8:
                iconId = "04";
                break;
        }
        return iconId;

    }

    private static int getSceneIconResId(Context context, String sceneIcon, String type) {
        StringBuilder resName = new StringBuilder();
        resName.append(type);
        if (sceneIcon.isEmpty()) {
            sceneIcon = "24";
        }
        resName.append(sceneIcon);
        String icon = resName.toString();
        int iconRes = context.getResources().getIdentifier(icon,
                "drawable", context.getPackageName());
        return iconRes;
    }

    private static Drawable getSceneIcon(Context context, String sceneIcon, String type) {
        int iconRes = getSceneIconResId(context, sceneIcon, type);
        Drawable drawable;
        try {
            drawable = context.getResources().getDrawable(iconRes);
        } catch (Resources.NotFoundException e) {
            if (TextUtils.equals(type, ICON_TYPE_NORMAL)) {
                drawable = context.getResources().getDrawable(R.drawable.scene_normal_24);
            } else {
                drawable = context.getResources().getDrawable(R.drawable.scene_quick_24);
            }
        }
        return drawable;
    }

    public void getSceneGroupList(String gwID) {
        MainApplication.getApplication().getMqttManager()
                .publishEncryptedMessage(MQTTCmdHelper.getSceneGroupList(gwID),
                        MQTTManager.MODE_GATEWAY_FIRST);
    }

    public void setSceneGroup(String gwID, int mode, String groupID, String name) {
        MainApplication.getApplication().getMqttManager()
                .publishEncryptedMessage(MQTTCmdHelper.setSceneGroup(gwID, mode, groupID, name),
                        MQTTManager.MODE_GATEWAY_FIRST);
    }

    public void VolumeSetSceneGroup(String gwID, String groupID, JSONArray data) {
        MainApplication.getApplication().getMqttManager()
                .publishEncryptedMessage(MQTTCmdHelper.volumeSetSceneGroup(gwID, groupID, data),
                        MQTTManager.MODE_GATEWAY_FIRST);
    }
}
