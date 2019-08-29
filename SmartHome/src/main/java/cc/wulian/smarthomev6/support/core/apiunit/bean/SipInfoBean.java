package cc.wulian.smarthomev6.support.core.apiunit.bean;

import java.util.List;

/**
 * Created by 上海滩小马哥 on 2017/9/19.
 */

public class SipInfoBean {
    public String sdomain;
    public String spassword;
    public String suid;
    public List<DeviceDomain> deviceSips;

    public static class DeviceDomain{
        public String deviceId;
        public String sdomain;
    }
}
