package cc.wulian.smarthomev6.support.core.apiunit.bean;

import java.util.List;

/**
 * created by huxc  on 2018/5/30.
 * func：海信子设备
 * email: hxc242313@qq.com
 */

public class HisenseChildDeviceBean {
    public List<ChildDevices> childDevices;

    public static class ChildDevices{
        public String deviceId;
        public List<ChildDevicesData> endpoints;
        public String deviceType;
        public String state;
    }

    public static class ChildDevicesData{
        public String endpointNum;
        public List<ChildDevicesProperties> properties;
        public String endpointName;
    }

    public static class ChildDevicesProperties{
        public OnOffBean onOffBean;
        public PercentageBean percentageBean;
    }

    public static class OnOffBean{
        public String value;
    }
    public static class PercentageBean{
        public String value;
    }


}
