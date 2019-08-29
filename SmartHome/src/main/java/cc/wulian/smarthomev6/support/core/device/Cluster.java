package cc.wulian.smarthomev6.support.core.device;

import java.util.List;

/**
 * Created by zbl on 2017/7/25.
 */

public class Cluster {
    public List<Attribute> attributes;
    public int clusterId;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Cluster{");
        sb.append("attributes=").append(attributes);
        sb.append(", clusterId=").append(clusterId);
        sb.append('}');
        return sb.toString();
    }
}
