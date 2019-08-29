package cc.wulian.smarthomev6.support.core.device;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.async.AsyncSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.event.HomeDeviceWidgetChangeEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.Trans2PinYin;

/**
 * Created by zbl on 2017/3/15.
 * 设备信息缓存
 */

public class DeviceCache {
    private Hashtable<String, Device> hashTable = new Hashtable<>();
    private DeviceDataEntityDao deviceDataEntityDao;
//    private AsyncSession asyncSession;

    public DeviceCache() {
        deviceDataEntityDao = MainApplication.getApplication().getDaoSession().getDeviceDataEntityDao();
//        asyncSession = deviceDataEntityDao.getSession().startAsyncSession();
    }

    public void add(@NonNull Device device) {
        Device deviceOrigin = hashTable.get(device.devID);
        if (deviceOrigin == null) {
            device.endpointCache = device.endpoints;
            hashTable.put(device.devID, device);
            deviceOrigin = device;
            if (deviceOrigin.name != null) {
                deviceOrigin.sortStr = Trans2PinYin.trans2PinYin(DeviceInfoDictionary.getNameByDevice(device).trim()).toLowerCase();
            } else {
                deviceOrigin.sortStr = "";
            }
        } else {
            mergeDeviceInfo(deviceOrigin, device);
//            mergeEndpoint(device);
        }
        if (deviceOrigin.isZigbee()) {
            deviceOrigin.isShared = Preference.getPreferences().isAuthGateway();
        }
        if (deviceOrigin.cat == 0) {
            deviceOrigin.cat = DeviceInfoDictionary.getCategoryByType(deviceOrigin.type);
        }
        //持久化
//        addDatabaseCache(deviceOrigin);
    }

    private void mergeEndpoint(Device device) {
        Device deviceOrigin = hashTable.get(device.devID);
        if (deviceOrigin == null) {
            hashTable.put(device.devID, device);
            return;
        }
        if (deviceOrigin.endpointCache == null) {
            deviceOrigin.endpointCache = device.endpointCache;
            deviceOrigin.data = device.data;
            return;
        }
        Map<Integer, Endpoint> m = new HashMap<>();
        for (Endpoint endpoint : deviceOrigin.endpointCache) {
            m.put(endpoint.endpointNumber, endpoint);
        }
        for (Endpoint endpoint : device.endpoints) {
            m.put(endpoint.endpointNumber, endpoint);
        }
        device.endpoints.clear();
        device.endpoints.addAll(m.values());
        deviceOrigin.endpointCache = device.endpoints;
        deviceOrigin.data = JSON.toJSONString(device);
        hashTable.put(device.devID, deviceOrigin);
    }

    //合并Device信息
    private void mergeDeviceInfo(Device deviceOrigin, Device device) {
        //合并基础信息
        if (!TextUtils.equals(deviceOrigin.name, device.name)) {
            if (deviceOrigin.name != null) {
                if (device.name != null) {
                    deviceOrigin.sortStr = Trans2PinYin.trans2PinYin(DeviceInfoDictionary.getNameByDevice(device).trim()).toLowerCase();
                }
            } else {
                deviceOrigin.sortStr = "";
            }
        }
        //向往背景音乐500上报的name字段为空，不需要使用默认名称合并 add by huxc at 2019/01/28
        if (!TextUtils.isEmpty(device.name) && !TextUtils.equals(device.type, "XW01")) {
            deviceOrigin.name = device.name;
        }
        //deviceOrigin.type = device.type;
        deviceOrigin.gwID = device.gwID;
        deviceOrigin.subGwid = device.subGwid;
        deviceOrigin.roomID = device.roomID;
        deviceOrigin.roomName = device.roomName;
        deviceOrigin.mode = device.mode;
        deviceOrigin.inner = device.inner;
        deviceOrigin.extData = device.extData;
        // mode = 0 时, 表示状态改变, 缓存时间戳
        // mode = 1 时, 表示设备上线, 时间戳没有实际意义
        if (device.mode == 0) {
            deviceOrigin.time = device.time;
        }
        //deviceOrigin.deviceDesc = device.deviceDesc;
        //deviceOrigin.isZigbee = device.isZigbee;
        //合并data
        if (TextUtils.isEmpty(deviceOrigin.data)) {
            deviceOrigin.data = device.data;
            deviceOrigin.endpoints = device.endpoints;
        } else if (!TextUtils.isEmpty(device.data)) {
            //合并endpoints
            if (deviceOrigin.endpoints == null || deviceOrigin.endpoints.size() == 0) {
                deviceOrigin.endpoints = device.endpoints;
            } else if (device.endpoints != null && device.endpoints.size() > 0) {
                for (Endpoint newEndpoint : device.endpoints) {
                    Endpoint endpoint = null;
                    for (Endpoint originEndpoint : deviceOrigin.endpoints) {
                        if (originEndpoint.endpointNumber == newEndpoint.endpointNumber) {
                            endpoint = originEndpoint;
                            break;
                        }
                    }
                    if (endpoint == null) {
                        deviceOrigin.endpoints.add(newEndpoint);
                    } else {
                        endpoint.endpointStatus = newEndpoint.endpointStatus;
                        endpoint.endpointName = newEndpoint.endpointName;
                        endpoint.endpointType = newEndpoint.endpointType;
                        //合并clusters
                        if (endpoint.clusters == null || endpoint.clusters.size() == 0) {
                            endpoint.clusters = newEndpoint.clusters;
                        } else if (newEndpoint.clusters != null && newEndpoint.clusters.size() > 0) {
                            for (Cluster newCluster : newEndpoint.clusters) {
                                Cluster cluster = null;
                                for (Cluster originCluster : endpoint.clusters) {
                                    if (originCluster.clusterId == newCluster.clusterId) {
                                        cluster = originCluster;
                                        break;
                                    }
                                }
                                if (cluster == null) {
                                    endpoint.clusters.add(newCluster);
                                } else {
                                    //合并attributes
                                    if (cluster.attributes == null || cluster.attributes.size() == 0) {
                                        cluster.attributes = newCluster.attributes;
                                    } else if (newCluster.attributes != null && newCluster.attributes.size() > 0) {
                                        for (Attribute newAttribute : newCluster.attributes) {
                                            Attribute attribute = null;
                                            for (Attribute originAttribute : cluster.attributes) {
                                                if (originAttribute == null) {
                                                    continue;
                                                }
                                                if (originAttribute.attributeId == newAttribute.attributeId) {
                                                    attribute = originAttribute;
                                                    break;
                                                }
                                            }
                                            if (attribute == null) {
                                                cluster.attributes.add(newAttribute);
                                            } else {
                                                attribute.attributeValue = newAttribute.attributeValue;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Collections.sort(deviceOrigin.endpoints, new Comparator<Endpoint>() {
                    public int compare(Endpoint o1, Endpoint o2) {
                        Integer i1 = o1.endpointNumber; //order表示文件名列表，在方法体外引入
                        Integer i2 = o2.endpointNumber;
                        return i1.compareTo(i2);
                    }
                });
            }
            //保存data字段
            // TODO: 2017/7/26 为避免原生出现bug, 暂时不用合并后的data
            deviceOrigin.data = device.data;//JSON.toJSONString(deviceOrigin);
        }
    }

    public void remove(@NonNull Device device) {
        remove(device.devID);
    }

    public void remove(@NonNull String devID) {
        deleteDatabaseCache(hashTable.get(devID));
        hashTable.remove(devID);
    }

    public void romoveByGatewayID(@NonNull String gwID) {
        for (Device device : getDevices()) {
            if (!TextUtils.equals(device.gwID, gwID)) {
                hashTable.remove(device.devID);
            }
        }
    }

    public Device get(@NonNull String deviceId) {
        return hashTable.get(deviceId);
    }

    public Collection<Device> getDevices() {
        return hashTable.values();
    }

    public boolean contains(String deviceID) {
        return get(deviceID) != null;
    }

    public List<Device> getWifiDevices() {
        List<Device> devices = new ArrayList<>();
        for (Device device : getDevices()) {
            if (!device.isZigbee()) {
                devices.add(device);
            }
        }
        return devices;
    }

    public List<Device> getZigBeeDevices() {
        List<Device> devices = new ArrayList<>();
        for (Device device : getDevices()) {
            if (device.isZigbee()) {
                devices.add(device);
            }
        }
        return devices;
    }

    public void clear() {
        hashTable.clear();
    }

    public void clearZigBeeDevices() {
        List<Device> devices = new ArrayList<>(hashTable.values());
        for (Device device : devices) {
            if (device.isZigbee()) {
                hashTable.remove(device.devID);
            }
        }
    }

    public void clearWifiDevices() {
        List<Device> devices = new ArrayList<>(hashTable.values());
        for (Device device : devices) {
            if (!device.isZigbee()) {
                hashTable.remove(device.devID);
            }
        }
    }

    public void clearWifiDevice(String deviceId) {
        List<Device> devices = new ArrayList<>(hashTable.values());
        for (Device device : devices) {
            if (TextUtils.equals(device.devID, deviceId)) {
                hashTable.remove(device.devID);
            }
        }
    }

    /////////设备缓存持久化相关方法

    /**
     * 加载持久化的设备列表缓存
     *
     * @param gwID
     * @param state 网关在线状态
     */
    public void loadDatabaseCache(String gwID, String state) {
        clearZigBeeDevices();
        List<DeviceDataEntity> deviceDataEntityList = deviceDataEntityDao.queryBuilder()
                .where(DeviceDataEntityDao.Properties.GwID.eq(gwID))
                .build()
                .list();
        if (deviceDataEntityList != null && deviceDataEntityList.size() > 0) {
            int mode = "1".equals(state) ? 0 : 2;
            for (DeviceDataEntity entity : deviceDataEntityList) {
                Device device = JSON.parseObject(entity.getData(), Device.class);
                device.data = entity.getData();
                if (mode == 2) {
                    device.mode = mode;
                }
                if (device.name != null) {
                    if (TextUtils.isEmpty(device.sortStr)) {
                        device.sortStr = Trans2PinYin.trans2PinYin(DeviceInfoDictionary.getNameByDevice(device).trim()).toLowerCase();
                    }
                }
                hashTable.put(device.devID, device);
            }
        }

        // 更新 widget
        EventBus.getDefault().post(HomeDeviceWidgetChangeEvent.ALL());
    }

    /**
     * 删除网关下设备缓存，如果网关ID为空，则删除全部缓存
     *
     * @param gwID
     */
    public void clearDatabaseCache(String gwID) {
        if (TextUtils.isEmpty(gwID)) {
            deviceDataEntityDao.deleteAll();
        } else {
            List<DeviceDataEntity> deviceDataEntityList = deviceDataEntityDao.queryBuilder()
                    .where(DeviceDataEntityDao.Properties.GwID.eq(gwID))
                    .build()
                    .list();
            if (deviceDataEntityList != null && deviceDataEntityList.size() > 0) {
                deviceDataEntityDao.deleteInTx(deviceDataEntityList);
            }
        }
    }

    /**
     * 持久化当前设备缓存
     */
    public void saveDatabaseCache() {
        List<Device> devices = new ArrayList<>(hashTable.values());
        for (Device device : devices) {
            if (device.isZigbee()) {
                addDatabaseCache(device);
            }
        }
    }

    /**
     * 持久化一个设备缓存
     *
     * @param device
     */
    public void addDatabaseCache(Device device) {
        if (device != null && device.isZigbee()) {
            DeviceDataEntity deviceDataEntity = deviceDataEntityDao.queryBuilder()
                    .where(DeviceDataEntityDao.Properties.GwID.eq(device.gwID), DeviceDataEntityDao.Properties.DevID.eq(device.devID))
                    .build()
                    .unique();
            if (deviceDataEntity == null) {
                deviceDataEntity = new DeviceDataEntity(device);
                deviceDataEntityDao.insert(deviceDataEntity);
            } else {
                deviceDataEntity.setDeviceData(device);
                deviceDataEntityDao.update(deviceDataEntity);
            }
        }
    }

    /**
     * 删除一个设备缓存
     *
     * @param device
     */
    public void deleteDatabaseCache(Device device) {
        if (device != null && device.devID != null && device.gwID != null && device.isZigbee()) {
            DeviceDataEntity entity = deviceDataEntityDao.queryBuilder()
                    .where(DeviceDataEntityDao.Properties.DevID.eq(device.devID), DeviceDataEntityDao.Properties.GwID.eq(device.gwID))
                    .build()
                    .unique();
            if (entity != null) {
                deviceDataEntityDao.delete(entity);

            }
        }
    }
}
