package cc.wulian.smarthomev6.support.core.apiunit.bean;

import java.util.List;

/**
 * created by huxc  on 2018/12/10.
 * func：场景日志Bean
 * email: hxc242313@qq.com
 */

public class HouseKeeperLogBean {
    public List<HouseKeeperData> seneschalData;

    public static class HouseKeeperData {
        public String icon;
        public String name;
        public String time;
    }
}
