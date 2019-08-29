package cc.wulian.smarthomev6.support.core.device;

import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * Created by zbl on 2017/7/26.
 */

public class EndpointParser {

    public interface ParserCallback {
        /**
         *
         * @param endpoint
         * @param cluster
         * @param attribute
         * @return 中断模式，true则中断解析，false继续解析
         */
        void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute);
    }

    public static void parse(String deviceJsonString, ParserCallback callback) {
        Device device = JSON.parseObject(deviceJsonString, Device.class);
        parse(device, callback);
    }

    public static void parse(Device device, ParserCallback callback) {
        if (device != null) {
            parse(device.endpoints, callback);
        }
    }

    public static void parse(List<Endpoint> endpoints, ParserCallback callback) {
        if (endpoints != null && endpoints.size() > 0) {
            for (Endpoint endpoint : endpoints) {
                if (endpoint.clusters != null && endpoint.clusters.size() > 0) {
                    for (Cluster cluster : endpoint.clusters) {
                        if (cluster.attributes != null && cluster.attributes.size() > 0) {
                            for (Attribute attribute : cluster.attributes) {
                                callback.onFindAttribute(endpoint, cluster, attribute);
                            }
                        }
                    }
                }
            }
        }
    }
}
