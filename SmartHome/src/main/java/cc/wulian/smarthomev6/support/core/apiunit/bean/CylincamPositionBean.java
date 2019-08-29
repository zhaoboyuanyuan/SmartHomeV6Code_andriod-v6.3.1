package cc.wulian.smarthomev6.support.core.apiunit.bean;

import java.util.List;

/**
 * created by huxc  on 2018/5/9.
 * func：小物摄像机预置位
 * email: hxc242313@qq.com
 */

public class CylincamPositionBean {
    public List<PositionBean> position;

    public static class PositionBean{
        public String name;
        public String image;
        public String id;
    }
}
