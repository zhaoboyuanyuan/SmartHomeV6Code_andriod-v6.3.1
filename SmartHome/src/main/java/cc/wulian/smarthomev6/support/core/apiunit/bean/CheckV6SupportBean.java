package cc.wulian.smarthomev6.support.core.apiunit.bean;

import java.util.List;

/**
 * created by huxc  on 2018/3/2.
 * func：校验v6是否支持bean
 * email: hxc242313@qq.com
 */

public class CheckV6SupportBean {
    public List<SupportDevice> device;

    public static class SupportDevice {
        public int deviceFlag;//1表示网关类，2表示摄像机类，3表示其他类
        public int supportV6Flag;//0不支持 1支持
        public String type;
        public String deviceId;
        public String uniqueDeviceId;//唯一id
    }
}
