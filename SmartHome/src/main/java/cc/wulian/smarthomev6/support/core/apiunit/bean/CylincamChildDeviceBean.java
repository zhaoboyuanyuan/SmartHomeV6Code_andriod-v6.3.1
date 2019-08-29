package cc.wulian.smarthomev6.support.core.apiunit.bean;

import java.util.List;

/**
 * Created by 上海滩小马哥 on 2017/9/29.
 */

public class CylincamChildDeviceBean {
    public List<ChildDevices> childDevices;

    public static class ChildDevices{
        public String deviceId;
        public List<CylincamChildDeviceDataBean> endpointDatas;
        public String name;
        public String state;
        public String type;
    }
}
