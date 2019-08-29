package cc.wulian.smarthomev6.support.core.device;

import java.util.List;

/**
 * Created by zbl on 2017/7/25.
 */

public class Endpoint {
    public List<Cluster> clusters;
    public int endpointNumber;
    public String endpointName;
    public String endpointStatus;
    public int endpointType;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Endpoint{");
        sb.append("clusters=").append(clusters);
        sb.append(", endpointNumber=").append(endpointNumber);
        sb.append(", endpointName='").append(endpointName).append('\'');
        sb.append(", endpointStatus='").append(endpointStatus).append('\'');
        sb.append(", endpointType=").append(endpointType);
        sb.append('}');
        return sb.toString();
    }
}
