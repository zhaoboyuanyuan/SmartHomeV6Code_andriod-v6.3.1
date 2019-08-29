package cc.wulian.smarthomev6.main.home.widget;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.WidgetBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceCache;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.event.HomeDeviceWidgetChangeEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.CollectionsUtil;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

import static cc.wulian.smarthomev6.main.home.adapter.HomeWidgetAdapter.TYPE_ADV;
import static cc.wulian.smarthomev6.main.home.adapter.HomeWidgetAdapter.TYPE_SCENE;
import static cc.wulian.smarthomev6.main.home.adapter.HomeWidgetAdapter.TYPE_VIDEO;

/**
 * Created by Veev on 2017/5/10
 * Tel: 18365264930
 * QQ: 2355738466
 * Function: 首页widget 管理类
 */

public class HomeWidgetManager {

    private static final String TAG = HomeWidgetManager.class.getSimpleName();
    private static String SECURITY_SENSOR_ID = "4";
    private static String SECURITY_SENSOR_TYPE = "security_sensor";
    private static String ENVIRONMENT_DETECTION_ID = "5";
    private static String ENVIRONMENT_DETECTION_TYPE = "environment_det";

    private static HomeItemBeanDao getWidgetDao() {
        return MainApplication.getApplication().getDaoSession().getHomeItemBeanDao();
    }

    /**
     * 首页widget缓存
     * <p>
     * widget加入首页后，会添加到缓存
     * <p>
     * 主要用途：向网关请求数据，该数据只需要请求一次
     * <p>
     * 用法：判断{@link #hasInCache(Device)}，否则请求数据，然后添加到缓存{@link #add2Cache(Device)}
     */
    private static Set<String> sWidgetCache = new HashSet<>();

    private static void checkCache(List<HomeItemBean> list) {
        Set<String> set = new HashSet<>();
        for (HomeItemBean h : list) {
            set.add(h.getWidgetID());
        }
        for (Object d : sWidgetCache.toArray()) {
            if (!set.contains(d.toString())) {
                sWidgetCache.remove(d.toString());
            }
        }
    }

    public static void add2Cache(Device device) {
        if (device != null) {
            sWidgetCache.add(device.devID);
        }
    }

    public static boolean hasInCache(Device device) {
        if (device != null) {
            return sWidgetCache.contains(device.devID);
        }

        return false;
    }

    public static void removeFromCache(Device device) {
        if (device != null) {
            sWidgetCache.remove(device.devID);
        }
    }

    /**
     * 获取要先显示的 widget
     */
    public synchronized static List<HomeItemBean> acquireWidget() {

        WLog.i(TAG, "user: " + getUserId());
        WLog.i(TAG, "gw: " + getGwID());

        // 1. 检查默认的widget
        // 如果没有默认的 widget 就要添加
        checkCommonWidget();

        // 2. 检查WiFi设备
        // 切换网关时, wifi设备不会再次上线, 因此要注意手动检查
        checkWifiDevice();

        // Finally. 检查设备列表
        checkDeviceWidget();

        List<HomeItemBean> list = getWidgetDao().queryBuilder()
                .where(HomeItemBeanDao.Properties.User.eq(getUserId()),
                        HomeItemBeanDao.Properties.IsAdd.eq(true),
                        HomeItemBeanDao.Properties.Show.eq(true),
                        getGwIdCondition())
                .orderAsc(HomeItemBeanDao.Properties.Sort, HomeItemBeanDao.Properties.Id)
                .list();
        List<HomeItemBean> l = CollectionsUtil.filter(list, new CollectionsUtil.CollectionFilter<HomeItemBean>() {
            @Override
            public boolean onFilter(HomeItemBean homeItemBean) {
                if (Scene.equals(homeItemBean.getType()) && "3".equals(Preference.getPreferences().getGatewayRelationFlag())) {
                    return false;
                }
                return DeviceInfoDictionary.getDeviceTypeID(homeItemBean.getType()) != -128;
            }
        });
        checkCache(l);
        return l;
    }

    /**
     * 网关离线, 下线所有的widget
     */
    public static void offLineWidgetInGw(String gwID) {
        // TODO: 2017/8/30 待验证
        if (true) {
            return;
        }
        List<HomeItemBean> list = acquireWidget();
        for (HomeItemBean bean : list) {
            if (TextUtils.equals(bean.getGwID(), gwID)
                    && StringUtil.notIn(bean.getType(), Banner, Video, Scene)) {
                bean.setShow(false);
            }
        }
        EventBus.getDefault().post(HomeDeviceWidgetChangeEvent.ALL());
    }

    /**
     * 检查并创建默认的 widget
     * 如果没有widget， 则保存默认的widget
     */
    private static void checkCommonWidget() {
        if (!hasCommonWidget()) {
            saveNewCommonWidget();
        }
    }

    /**
     * 选择是否要显示
     */
    private synchronized static void checkDeviceWidget() {
        List<HomeItemBean> list = getCurrentAllWidget();

        DeviceCache cache = MainApplication.getApplication().getDeviceCache();

        List<HomeItemBean> listDevice = new ArrayList<>(list);

        // 去掉默认widget
        for (HomeItemBean bean : list) {
            if (bean.getWidgetID().startsWith(CommonWidgetPre)) {
                listDevice.remove(bean);
            }
        }

        for (HomeItemBean bean : listDevice) {
            if (SECURITY_SENSOR_ID.equals(bean.getWidgetID())) {
                bean.setShow(hasOtherSecuritySensorDevice(null));
            } else if (ENVIRONMENT_DETECTION_ID.equals(bean.getWidgetID())) {
                bean.setShow(hasOtherEnvironmentDevice(null));
            } else {
                bean.setShow(cache.contains(bean.getWidgetID()));
            }
        }

        getWidgetDao().updateInTx(listDevice);
    }

    /**
     * 检查wifi设备widget
     */
    private static void checkWifiDevice() {
        for (Device device : MainApplication.getApplication().getDeviceCache().getWifiDevices()) {
            saveNewWifiWidget(device);
        }
    }

    /**
     * 处理500
     */
    public static void handleDeviceReport(Device device) {
        if (device == null) {
            return;
        }

        if (device.mode == 3) {
            // mode = 3 设备退网
            // 有没有这个设备
            // 有的话就移除
            removeOldDeviceWidget(device);
        } else if (device.mode == 1) {
            // 上线
            saveDeviceWidget(device);
//            updateLocalSort();
        } else {
            // 设备更新
            // 新建这个设备
            saveDeviceWidget(device);
        }
    }

    /**
     * 处理502
     * <p>
     * Tips: 快速删除一个设备  执行次方法, ( 3, replace your deviceID)
     */
    public static void handleDeviceInfoChanged(int mode, String devID, String type) {
        if (mode == 3) {
            // mode = 3 设备退网
            // 有没有这个设备
            // 有的话就移除
            removeOldDeviceWidget(devID, type);
        } else {
            Device device = MainApplication.getApplication().getDeviceCache().get(devID);
            // 设备更新
            // 新建这个设备
            saveDeviceWidget(device);
        }
    }

    /**
     * 删除widget
     */
    public static void deleteWidget(String deviceID) {
        removeOldDeviceWidget(deviceID);
    }

    /**
     * 判断是否添加过 默认的widget
     * 由于默认的widget不会删除
     * 这里判断最后一个widget是否添加
     */
    private synchronized static boolean hasCommonWidget() {
        return getWidgetDao().queryBuilder()
                .where(HomeItemBeanDao.Properties.User.eq(getUserId()),
                        HomeItemBeanDao.Properties.GwID.eq(getGwID()),
                        HomeItemBeanDao.Properties.Type.eq(Video))
                .unique() != null;
    }

    /**
     * 判断是否添加过 设备 widget
     */
    private synchronized static boolean hasDeviceWidget() {
        List<HomeItemBean> list = getWidgetDao().queryBuilder()
                .where(HomeItemBeanDao.Properties.User.eq(getUserId()),
                        HomeItemBeanDao.Properties.Show.eq(true),
                        getGwIdCondition())
                .list();

        return list.size() > 3;
    }

    /**
     * 获取 网关id的查询条件
     * <p>
     * 账号登录的  需要考虑WiFi设备
     */
    private static WhereCondition getGwIdCondition() {
        // 账号登录
        // 不仅要查询当前网关, 还要查询当前账号下WiFi的
        /*if (!Default_User.equals(getUserId())) {
            return getWidgetDao().queryBuilder().or(HomeItemBeanDao.Properties.GwID.eq(getGwID()),
                    HomeItemBeanDao.Properties.GwID.eq(WiFi_GW));
        }*/

        return HomeItemBeanDao.Properties.GwID.eq(getGwID());
    }

    /**
     * 获取要添加的widget的sort值
     * <p>
     * 先拿到最新的widget
     * 在拿sort
     * sort +1 就是将要添加的widget的sort
     */
    private synchronized static int getNewWidgetSort() {
        int newSort;
        List<HomeItemBean> leastBean = getWidgetDao().queryBuilder()
                .where(HomeItemBeanDao.Properties.User.eq(getUserId()),
                        getGwIdCondition())
                .orderDesc(HomeItemBeanDao.Properties.Sort)
                .limit(1)
                .list();
        // tips: 理论上来说, 这里的listBean 永不为空
        if (leastBean == null || leastBean.isEmpty()) {
            newSort = 0;
        } else {
            newSort = leastBean.get(0).getSort() + 1;
        }
        return newSort;
    }

    /**
     * 获取当前所有的 widget
     */
    private synchronized static List<HomeItemBean> getCurrentAllWidget() {

        checkCommonWidget();

        List<HomeItemBean> list = getWidgetDao().queryBuilder()
                .where(HomeItemBeanDao.Properties.User.eq(getUserId()),
                        getGwIdCondition())
                .orderAsc(HomeItemBeanDao.Properties.Sort, HomeItemBeanDao.Properties.Id)
                .list();

        List<HomeItemBean> l = CollectionsUtil.filter(list, new CollectionsUtil.CollectionFilter<HomeItemBean>() {
            @Override
            public boolean onFilter(HomeItemBean homeItemBean) {
                if (Scene.equals(homeItemBean.getType()) && "3".equals(Preference.getPreferences().getGatewayRelationFlag())) {
                    return false;
                }
                return DeviceInfoDictionary.getDeviceTypeID(homeItemBean.getType()) != -128;
            }
        });
        return l;
    }

    /**
     * 更新
     */
    public synchronized static void update(HomeItemBean bean) {
        getWidgetDao().update(bean);
    }

    public synchronized static HomeItemBean get(String widgetID) {
        return getWidgetDao()
                .queryBuilder()
                .where(HomeItemBeanDao.Properties.User.eq(getUserId()),
                        HomeItemBeanDao.Properties.GwID.eq(getGwID()),
                        HomeItemBeanDao.Properties.WidgetID.eq(widgetID))
                .unique();
    }

    /**
     * 获取当前所有的widget
     * 条件: 是否添加到首页
     */
    public synchronized static List<HomeItemBean> getCurrentWidgetWithAdd(boolean isAdd) {

        checkCommonWidget();

        List<HomeItemBean> list = getWidgetDao().queryBuilder()
                .where(HomeItemBeanDao.Properties.User.eq(getUserId()),
                        HomeItemBeanDao.Properties.IsAdd.eq(isAdd),
                        HomeItemBeanDao.Properties.Show.eq(true),
                        getGwIdCondition())
                .orderAsc(HomeItemBeanDao.Properties.Sort, HomeItemBeanDao.Properties.Id)
                .list();

        List<HomeItemBean> l = CollectionsUtil.filter(list, new CollectionsUtil.CollectionFilter<HomeItemBean>() {
            @Override
            public boolean onFilter(HomeItemBean homeItemBean) {
                if (Scene.equals(homeItemBean.getType()) && "3".equals(Preference.getPreferences().getGatewayRelationFlag())) {
                    return false;
                }
                return DeviceInfoDictionary.getDeviceTypeID(homeItemBean.getType()) != -128;
            }
        });
        return l;
    }

    private static final String Default_Gw = "Default_Gw";
    private static final String Default_User = "Default_User";

    private static String getUserId() {
        String user = Preference.getPreferences().getUserID();
        if (!Preference.getPreferences().getUserEnterType().equals(Preference.ENTER_TYPE_ACCOUNT)) {
            user = Default_User;
        }

        return user;
    }

    private static String getGwID() {
        String gwId = Preference.getPreferences().getCurrentGatewayID();
        if (gwId.isEmpty()) {
            gwId = Default_Gw;
        }

        return gwId;
    }

    /**
     * 清空所有的widget
     */
    private static void clearAll() {
        getWidgetDao().deleteAll();
    }

    /**
     * 清空其他的widget
     * <p>
     * 在用户清理缓存时, 如果当前有网关, 就调用这个方法
     * 如果当前没有网关, 就清理全部
     */
    public synchronized static void clearOtherWidgets() {
        // 备份当前
        List<HomeItemBean> list = getCurrentAllWidget();
        // 删除全部
        clearAll();
        // 保存当前
        getWidgetDao().saveInTx(list);
    }

    private static final String CommonWidgetPre = "CommonWidget_";
    public static final String Banner = "Banner";
    public static final String Scene = "Scene";
    public static final String Video = "Video";
    public static final String Security_sensor = "security_sensor";
    public static final String Environment_det = "environment_det";

    /**
     * 保存新的widget
     */
    private synchronized static void saveNewCommonWidget() {
        getWidgetDao().insert(new HomeItemBean(CommonWidgetPre + Banner, Banner, TYPE_ADV,
                MainApplication.getApplication().getString(R.string.Home_Module_Adv),
                true, true, 1, getUserId(), getGwID()));
        getWidgetDao().insert(new HomeItemBean(CommonWidgetPre + Scene, Scene, TYPE_SCENE,
                MainApplication.getApplication().getString(R.string.Home_Module_Scene),
                true, true, 2, getUserId(), getGwID()));
        getWidgetDao().insert(new HomeItemBean(CommonWidgetPre + Video, Video, TYPE_VIDEO,
                MainApplication.getApplication().getString(R.string.Home_Module_Video),
                true, true, 3, getUserId(), getGwID()));
    }

    /**
     * 保存新的 wifi 设备 widget
     */
    public static void saveNewWifiWidget(Device device) {
        saveDeviceWidget(device);
    }

    /**
     * 是否安防传感器
     */
    public static boolean isSecuritySensorDevice(String type) {
        switch (type) {
            case "06":
                return true;
            case "09":
                return true;
            case "43":
                return true;
            case "03":
                return true;
            case "02":
                return true;
            case "a1":
                return true;
//            case "Ad":
//                return true;
//            case "C0":
//                return true;
            default:
                return false;
        }
    }

    /**
     * 是否环境监测
     */
    public static boolean isEnvironmentDevice(String type) {
        switch (type) {
            case "42":
            case "17":
            case "A0":
            case "D4":
            case "D5":
            case "D6":
            case "44":
                return true;
            case "19":
                return true;
            case "Og":
                return true;
            default:
                return false;
        }
    }

    /**
     * 是否有除自己以外的安防传感器
     */
    private static boolean hasOtherSecuritySensorDevice(String type) {
        for (Device device : MainApplication.getApplication().getDeviceCache().getZigBeeDevices()) {
            if ((!device.type.equals(type) || type == null) && isSecuritySensorDevice(device.type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否有除自己以外的环境监测
     */
    private static boolean hasOtherEnvironmentDevice(String type) {
        for (Device device : MainApplication.getApplication().getDeviceCache().getZigBeeDevices()) {
            if ((!device.type.equals(type) || type == null) && isEnvironmentDevice(device.type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 保存 设备widget
     */
    private synchronized static void saveDeviceWidget(Device device) {
        if (device == null) {
            return;
        }

        if (device.mode == 3) {
            return;
        }

        //如果安防设备
        if (isSecuritySensorDevice(device.type)) {
            // 查找这个设备的widget
            HomeItemBean thisBean = getWidgetDao()
                    .queryBuilder()
                    .where(HomeItemBeanDao.Properties.User.eq(getUserId()),
                            HomeItemBeanDao.Properties.GwID.eq(getGwID()),
                            HomeItemBeanDao.Properties.Type.eq(SECURITY_SENSOR_TYPE))
                    .unique();
            if (thisBean == null) {
                // 确定之前的最大 sort
                int thisSort = getNewWidgetSort();
                HomeItemBean newBean = new HomeItemBean();
                newBean.setWidgetID(SECURITY_SENSOR_ID);
                newBean.setType(SECURITY_SENSOR_TYPE);
                newBean.setName(MainApplication.getApplication().getResources().getString(R.string.Home_Widget_AlarmUniversalSensor));
                newBean.setShow(true);
                newBean.setIsAdd(false);
                newBean.setUser(getUserId());
                newBean.setGwID(getGwID());
                // 获取同步的顺序
                int sort = getLocalSort(SECURITY_SENSOR_ID);
                if (sort == -1) {
                    sort = thisSort + 1;
                } else {
                    // 说明有排序的数据
                    // 之前加过这个widget
                    // 因此添加到首页
                    newBean.setIsAdd(true);
                }
                newBean.setSort(sort);
                getWidgetDao().insert(newBean);
                EventBus.getDefault().post(HomeDeviceWidgetChangeEvent.ADD(newBean));
            } else {
                // 更新
                thisBean.update(device);
                // 如果之前保存过, 并且要显示
                // 则不再保存， 返回
//                if (thisBean.isShow()) {
//                    return ;
//                }
                // 如果不显示, 则改为显示
                if (!thisBean.isShow()) {
                    thisBean.setShow(true);
                    getWidgetDao().update(thisBean);
                }
                EventBus.getDefault().post(HomeDeviceWidgetChangeEvent.ADD(thisBean));
            }
            return;
        }
        //如果环境监测
        if (isEnvironmentDevice(device.type)) {
            // 查找这个设备的widget
            HomeItemBean thisBean = getWidgetDao()
                    .queryBuilder()
                    .where(HomeItemBeanDao.Properties.User.eq(getUserId()),
                            HomeItemBeanDao.Properties.GwID.eq(getGwID()),
                            HomeItemBeanDao.Properties.Type.eq(ENVIRONMENT_DETECTION_TYPE))
                    .unique();
            if (thisBean == null) {
                // 确定之前的最大 sort
                int thisSort = getNewWidgetSort();
                HomeItemBean newBean = new HomeItemBean();
                newBean.setWidgetID(ENVIRONMENT_DETECTION_ID);
                newBean.setType(ENVIRONMENT_DETECTION_TYPE);
                newBean.setName(MainApplication.getApplication().getResources().getString(R.string.Environmental_Monitoring));
                newBean.setShow(true);
                newBean.setIsAdd(false);
                newBean.setUser(getUserId());
                newBean.setGwID(getGwID());
                // 获取同步的顺序
                int sort = getLocalSort(ENVIRONMENT_DETECTION_ID);
                if (sort == -1) {
                    sort = thisSort + 1;
                } else {
                    // 说明有排序的数据
                    // 之前加过这个widget
                    // 因此添加到首页
                    newBean.setIsAdd(true);
                }
                newBean.setSort(sort);
                getWidgetDao().insert(newBean);
                EventBus.getDefault().post(HomeDeviceWidgetChangeEvent.ADD(newBean));
            } else {
                // 更新
                thisBean.update(device);
                // 如果之前保存过, 并且要显示
                // 则不再保存， 返回
//                if (thisBean.isShow()) {
//                    return ;
//                }
                // 如果不显示, 则改为显示
                if (!thisBean.isShow()) {
                    thisBean.setShow(true);
                    getWidgetDao().update(thisBean);
                }
                EventBus.getDefault().post(HomeDeviceWidgetChangeEvent.ADD(thisBean));
            }
            return;
        }
        // 如果这个设备没有widget
        // 直接返回
        if (!DeviceInfoDictionary.hasWidget(device.type)) {
            return;
        }
        // 查找这个设备的widget
        HomeItemBean thisBean = getWidgetDao()
                .queryBuilder()
                .where(HomeItemBeanDao.Properties.User.eq(getUserId()),
                        HomeItemBeanDao.Properties.GwID.eq(getGwID()),
                        HomeItemBeanDao.Properties.WidgetID.eq(device.devID))
                .unique();

        // 确定之前的最大 sort
        int thisSort = getNewWidgetSort();

        if (thisBean != null) {
            // 更新
            thisBean.update(device);
            // 如果之前保存过, 并且要显示
//            // 则不再保存， 返回
//            if (thisBean.isShow()) {
//                return ;
//            }

            // 如果不显示, 则改为显示
            if (!thisBean.isShow()) {
                thisBean.setShow(true);
                getWidgetDao().update(thisBean);
            }
            EventBus.getDefault().post(HomeDeviceWidgetChangeEvent.ADD(thisBean));
            return;
        }

        // 已经添加过widget 就不添加到首页
        boolean isAdd = !hasDeviceWidget();

        // 获取同步的顺序
        int sort = getLocalSort(device.devID);
        if (sort == -1) {
            sort = thisSort + 1;
        } else {
            // 说明有排序的数据
            // 之前加过这个widget
            // 因此添加到首页
            isAdd = true;
        }

        HomeItemBean newBean = new HomeItemBean(
                device.devID,
                device.type,
                DeviceInfoDictionary.getDeviceTypeID(device.type),
                DeviceInfoDictionary.getNameByTypeAndName(device.type, device.name),
                isAdd,
                true, // 默认显示
                sort,
                getUserId(),
                getGwID());
        // 保存widget
        getWidgetDao().insert(newBean);
        EventBus.getDefault().post(HomeDeviceWidgetChangeEvent.ADD(newBean));
    }

    /**
     * 移除退网的 widget
     */
    private static void removeOldDeviceWidget(Device device) {
        if (device == null) {
            return;
        }

        if (device.mode != 3) {
            return;
        }
        //如果没有安防设备删除
        if (isSecuritySensorDevice(device.type) && !hasOtherSecuritySensorDevice(device.type)) {
            // 查找这个设备的widget
            HomeItemBean thisBean = getWidgetDao()
                    .queryBuilder()
                    .where(HomeItemBeanDao.Properties.User.eq(getUserId()),
                            HomeItemBeanDao.Properties.GwID.eq(getGwID()),
                            HomeItemBeanDao.Properties.Type.eq(SECURITY_SENSOR_TYPE))
                    .unique();
            if (thisBean != null) {
//                getWidgetDao().delete(thisBean);
                EventBus.getDefault().post(HomeDeviceWidgetChangeEvent.DELETE(thisBean));
            }
            return;
        }
        //如果没有环境监测设备删除
        if (isEnvironmentDevice(device.type) && !hasOtherEnvironmentDevice(device.type)) {
            // 查找这个设备的widget
            HomeItemBean thisBean = getWidgetDao()
                    .queryBuilder()
                    .where(HomeItemBeanDao.Properties.User.eq(getUserId()),
                            HomeItemBeanDao.Properties.GwID.eq(getGwID()),
                            HomeItemBeanDao.Properties.Type.eq(ENVIRONMENT_DETECTION_TYPE))
                    .unique();
            if (thisBean != null) {
//                getWidgetDao().delete(thisBean);
                EventBus.getDefault().post(HomeDeviceWidgetChangeEvent.DELETE(thisBean));
            }
            return;
        }
        // 如果这个设备没有widget
        // 直接返回
        if (!DeviceInfoDictionary.hasWidget(device.type)) {
            return;
        }

        removeOldDeviceWidget(device.devID);
    }

    /**
     * 移除退网的 widget
     */
    private static void removeOldDeviceWidget(String deviceID) {
        if (deviceID == null) {
            return;
        }

        // 查找这个设备的widget
        HomeItemBean thisBean = getWidgetDao()
                .queryBuilder()
                .where(HomeItemBeanDao.Properties.User.eq(getUserId()),
                        HomeItemBeanDao.Properties.GwID.eq(getGwID()),
                        HomeItemBeanDao.Properties.WidgetID.eq(deviceID))
                .unique();

        if (thisBean == null) {
            return;
        }

//        getWidgetDao().delete(thisBean);
        EventBus.getDefault().post(HomeDeviceWidgetChangeEvent.DELETE(thisBean));
    }

    /**
     * 移除退网的 widget
     */
    private static void removeOldDeviceWidget(String deviceID, String type) {
        if (deviceID == null) {
            return;
        }
        //如果没有安防设备删除
        if (isSecuritySensorDevice(type) && !hasOtherSecuritySensorDevice(type)) {
            // 查找这个设备的widget
            HomeItemBean thisBean = getWidgetDao()
                    .queryBuilder()
                    .where(HomeItemBeanDao.Properties.User.eq(getUserId()),
                            HomeItemBeanDao.Properties.GwID.eq(getGwID()),
                            HomeItemBeanDao.Properties.Type.eq(SECURITY_SENSOR_TYPE))
                    .unique();
            if (thisBean != null) {
//                getWidgetDao().delete(thisBean);
                EventBus.getDefault().post(HomeDeviceWidgetChangeEvent.DELETE(thisBean));
            }
            return;
        }
        //如果没有环境监测设备删除
        if (isEnvironmentDevice(type) && !hasOtherEnvironmentDevice(type)) {
            // 查找这个设备的widget
            HomeItemBean thisBean = getWidgetDao()
                    .queryBuilder()
                    .where(HomeItemBeanDao.Properties.User.eq(getUserId()),
                            HomeItemBeanDao.Properties.GwID.eq(getGwID()),
                            HomeItemBeanDao.Properties.Type.eq(ENVIRONMENT_DETECTION_TYPE))
                    .unique();
            if (thisBean != null) {
//                getWidgetDao().delete(thisBean);
                EventBus.getDefault().post(HomeDeviceWidgetChangeEvent.DELETE(thisBean));
            }
            return;
        }

        // 查找这个设备的widget
        HomeItemBean thisBean = getWidgetDao()
                .queryBuilder()
                .where(HomeItemBeanDao.Properties.User.eq(getUserId()),
                        HomeItemBeanDao.Properties.GwID.eq(getGwID()),
                        HomeItemBeanDao.Properties.WidgetID.eq(deviceID))
                .unique();

        if (thisBean == null) {
            return;
        }

//        getWidgetDao().delete(thisBean);
        EventBus.getDefault().post(HomeDeviceWidgetChangeEvent.DELETE(thisBean));
    }

    /**
     * 排序
     *
     * @param sort   要显示的
     * @param bottom 不显示的
     */
    public synchronized static void sort(List<HomeItemBean> sort, List<HomeItemBean> bottom) {
        if (sort != null) {
            int i = 1;
            for (HomeItemBean bean : sort) {
                bean.setSort(i++);
                bean.setIsAdd(true);
            }

            // 账号登录时, 才需要同步
            if (TextUtils.equals(Preference.ENTER_TYPE_ACCOUNT, Preference.getPreferences().getUserEnterType())) {
                saveLocalSort(sort);
            }
        }
        if (bottom != null) {
            int showCount = sort == null ? 0 : sort.size();
            int i = 1;
            for (HomeItemBean bean : bottom) {
                bean.setSort(showCount + (i++));
                bean.setIsAdd(false);
            }
        }

        getWidgetDao().updateInTx(sort);
        getWidgetDao().updateInTx(bottom);

        EventBus.getDefault().post(HomeDeviceWidgetChangeEvent.ALL());
    }

    /**
     * 同步云 widget顺序
     */
    private static void saveLocalSort(List<HomeItemBean> sort) {
        List<WidgetBean.WidgetsBean> widgetsBeen = new ArrayList<>();
        if (sort.isEmpty()) {
            widgetsBeen.add(new WidgetBean.WidgetsBean());
        } else {
            for (HomeItemBean bean : sort) {
                widgetsBeen.add(new WidgetBean.WidgetsBean(bean));
            }
        }

        new DataApiUnit(MainApplication.getApplication().getApplicationContext())
                .doUpdateWidgetSort(JSON.toJSONString(widgetsBeen), new DataApiUnit.DataApiCommonListener() {
                    @Override
                    public void onSuccess(Object bean) {
                        // 同步完成  更新本地的缓存
                        MainApplication.getApplication().setWidgetBean((WidgetBean) bean);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        ToastUtil.single(msg);
                    }
                });
    }

    public static void updateCloudSort() {
        // 非账号登录, 不发送同步widget请求
        if (!TextUtils.equals(Preference.getPreferences().getUserEnterType(), Preference.ENTER_TYPE_ACCOUNT)) {
            EventBus.getDefault().post(HomeDeviceWidgetChangeEvent.ALL());
            return;
        }
        new DataApiUnit(MainApplication.getApplication().getApplicationContext())
                .doGetWidgetSort(new DataApiUnit.DataApiCommonListener() {
                    @Override
                    public void onSuccess(Object bean) {
                        MainApplication.getApplication().setWidgetBean((WidgetBean) bean);
                        syncSort((WidgetBean) bean);
                    }

                    @Override
                    public void onFail(int code, String msg) {
//                        ToastUtil.single(msg);
                        EventBus.getDefault().post(HomeDeviceWidgetChangeEvent.ALL());
                    }
                });
    }

    /**
     * 同步本地的widget
     */
    public static void updateLocalSort() {
        WidgetBean bean = MainApplication.getApplication().getWidgetBean();
        if (bean != null) {
            syncSort(bean);
        }
    }

    /**
     * 获取同步widget的顺序 本地
     */
    private static int getLocalSort(String widgetID) {
        WidgetBean bean = MainApplication.getApplication().getWidgetBean();
        if (bean != null) {
            for (WidgetBean.WidgetsBean b : bean.widgets) {
                if (TextUtils.equals(widgetID, b.widgetId)) {
                    try {
                        return Integer.parseInt(b.sortNum);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        return -1;
    }

    private static void syncSort(WidgetBean widgetBean) {
        // 云端的widget的个数
        int count = widgetBean.widgets.size();

        // 说明之前没有同步过, 不处理
        if (count == 0) {
            return;
        }

        // 拿到本地widget的map
        Map<String, HomeItemBean> map = new ArrayMap<>();
        for (HomeItemBean h : getCurrentAllWidget()) {
            h.setSort(h.getSort() + count);
            h.setIsAdd(false);
            map.put(h.getWidgetID(), h);
        }

        // 说明之前同步数据为: 不显示任何数据
        // 不需要进入循环了
        // 直接返回
        if (count == 1) {
            WidgetBean.WidgetsBean w = widgetBean.widgets.get(0);
            if (TextUtils.equals(w.widgetId, "0")) {
                return;
            }
        }

        for (WidgetBean.WidgetsBean w : widgetBean.widgets) {
            String widgetID = w.widgetId;
            // 转一下 widgetId
            switch (w.widgetId) {
                case "1":
                    widgetID = CommonWidgetPre + Banner;
                    break;
                case "2":
                    widgetID = CommonWidgetPre + Scene;
                    break;
                case "3":
                    widgetID = CommonWidgetPre + Video;
                    break;
            }
            HomeItemBean h = map.get(widgetID);
            if (h == null) {
                // 如果没有这个设备, 就跳过
                continue;
            }
            h.setIsAdd(true);
            try {
                // 重设一下这个序号
                h.setSort(Integer.parseInt(w.sortNum));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        // 更新全部
        getWidgetDao().updateInTx(map.values());

        EventBus.getDefault().post(HomeDeviceWidgetChangeEvent.ALL());
    }
}
