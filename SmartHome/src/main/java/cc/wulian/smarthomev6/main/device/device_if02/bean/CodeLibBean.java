package cc.wulian.smarthomev6.main.device.device_if02.bean;

import java.util.List;

/**
 * created by huxc  on 2018/6/21.
 * func：单个码库bean
 * email: hxc242313@qq.com
 */

public class CodeLibBean {
    public List<codeBean> picks;

    public static class codeBean {
        public String keyId;
        public String keyName;
        public String shortCode;
    }
}
