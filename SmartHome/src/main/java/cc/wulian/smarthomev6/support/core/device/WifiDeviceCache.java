package cc.wulian.smarthomev6.support.core.device;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Hashtable;

/**
 * 作者: Administrator
 * 时间: 2017/5/4
 * 描述: wifi设备管理（猫眼。。）
 * 联系方式: 805901025@qq.com
 */

public class WifiDeviceCache {
    private Hashtable<String, Device> wifiDevices = new Hashtable<>();

    public WifiDeviceCache() {
    }

    public void add(@NonNull Device device) {
        wifiDevices.put(device.devID, device);
    }

    public void remove(@NonNull Device device) {
        wifiDevices.remove(device.devID);
    }

    public void remove(@NonNull String devID) {
        wifiDevices.remove(devID);
    }

    public Device get(@NonNull String deviceId) {
        return wifiDevices.get(deviceId);
    }

    public Collection<Device> getDevices() {
        return wifiDevices.values();
    }

    public void clear() {
        wifiDevices.clear();
    }
}
