package cc.wulian.smarthomev6.support.core.device;

import android.text.TextUtils;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Veev on 2018/1/23.
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    AttributeFinder
 */
public class AttributeFinder {

    public static Attribute find(Device device, int attributeId) {
        return find(device, -1, -1, attributeId);
    }

    public static List<Attribute> find(Device device, int[] attrIds) {
        return find(device, -1, -1, attrIds);
    }

    public static Attribute find(Device device, int endpointNumber, int cclusterId, int attributeId) {
        List<Attribute> list = find(device, endpointNumber, cclusterId, new int[]{attributeId});
        if (list == null || list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    /**
     * 将 attribute 转成 map
     * 注意: 不适用与多端点
     * 注意: 不适用与多端点
     * 注意: 不适用与多端点
     *
     * 因为多端点中, 每个端点都有attribute, id会重复
     */
    public static SparseArray<String> toMap(Device device) {
        SparseArray<String> array = new SparseArray<>();

        if (device == null) {
            return array;
        }

        List<Endpoint> endpoints = device.endpoints;
        if (endpoints != null && endpoints.size() > 0) {
            for (Endpoint endpoint : endpoints) {
                if (endpoint.clusters != null && endpoint.clusters.size() > 0) {
                    for (Cluster cluster : endpoint.clusters) {
                        if (cluster.attributes != null && cluster.attributes.size() > 0) {
                            for (Attribute attribute : cluster.attributes) {
                                array.put(attribute.attributeId, attribute.attributeValue);
                            }
                        }
                    }
                }
            }
        }

        return array;
    }

    public static List<Attribute> find(Device device, int endpointNumber, int cclusterId, int[] attributeIds) {
        List<Attribute> list = new ArrayList<>();

        if (device == null) {
            return list;
        }

        List<Endpoint> endpoints = device.endpoints;
        if (endpoints != null && endpoints.size() > 0) {
            for (Endpoint endpoint : endpoints) {
                if (endpointNumber > 0) {
                    if (endpointNumber != endpoint.endpointNumber) {
                        continue;
                    }
                }
                if (endpoint.clusters != null && endpoint.clusters.size() > 0) {
                    for (Cluster cluster : endpoint.clusters) {
                        if (cclusterId > 0) {
                            if (cclusterId != cluster.clusterId) {
                                continue;
                            }
                        }
                        if (cluster.attributes != null && cluster.attributes.size() > 0) {
                            for (Attribute attribute : cluster.attributes) {
                                for (int attrId : attributeIds) {
                                    if (attribute.attributeId == attrId) {
                                        list.add(attribute);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return list;
    }
}
