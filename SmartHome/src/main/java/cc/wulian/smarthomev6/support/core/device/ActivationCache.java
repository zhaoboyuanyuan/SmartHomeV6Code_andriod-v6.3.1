package cc.wulian.smarthomev6.support.core.device;

import android.text.TextUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Veev on 2017/12/19.
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    设备激活缓存
 */

public class ActivationCache {
    private Set<String> mActivations = new HashSet<>();

    public void activeDevice(Device device) {
        if (device == null) {
            return;
        }
        mActivations.add(device.devID);
    }

    public void activeDevice(String devID) {
        if (TextUtils.isEmpty(devID)) {
            return;
        }
        mActivations.add(devID);
    }

    public boolean isActive(Device device) {
        if (device == null) {
            return false;
        }
        if (TextUtils.equals("DD", device.type)) {
            return isBgmActive(device);
        }
        return mActivations.contains(device.devID);
    }

    // 背景音乐是否激活
    private boolean isBgmActive(Device device) {
        if (mActivations.contains(device.devID)) {
            return true;
        }
        List<Endpoint> endpoints = device.endpoints;
        if (endpoints != null && endpoints.size() > 0) {
            for (Endpoint endpoint : endpoints) {
                if (endpoint.clusters != null && endpoint.clusters.size() > 0) {
                    for (Cluster cluster : endpoint.clusters) {
                        if (cluster.attributes != null && cluster.attributes.size() > 0) {
                            for (Attribute attribute : cluster.attributes) {
                                if (attribute.attributeId == 0x000E) {
                                    if (TextUtils.equals(attribute.attributeValue, "0")) {
                                        activeDevice(device);
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public void clear() {
        mActivations.clear();
    }
}
