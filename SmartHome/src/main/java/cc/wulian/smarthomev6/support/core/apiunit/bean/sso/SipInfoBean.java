package cc.wulian.smarthomev6.support.core.apiunit.bean.sso;

import java.util.List;

/**
 * Created by 上海滩小马哥 on 2017/8/17.
 */

public class SipInfoBean {
    public String sdomain;
    public String spassword;
    public String suid;
    public List<DeviceSip> deviceSips;

    public static class DeviceSip{
        public String deviceId;
        public String sdomain;
    }
}
