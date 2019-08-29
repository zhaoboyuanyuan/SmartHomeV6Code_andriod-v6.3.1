package cc.wulian.smarthomev6.main.device.device_if02.bean;

import java.util.List;

/**
 * created by huxc  on 2018/6/26.
 * func：空调一键匹配按键
 * email: hxc242313@qq.com
 */

public class AirCodeLibsBean {

    public List<codeLibsBean> codeLibs;

    public static class codeLibsBean {
        public String codeLib;
        public String model;
        public List<commandBean> rcCommand;
        public boolean isSelected;//自定义列表是否选中
    }

    public static class commandBean {
        public String kn;
        public String src;
        public String order;
    }
}
