package cc.wulian.smarthomev6.main.device.device_if02.bean;

import java.util.List;

/**
 * created by huxc  on 2018/6/19.
 * func：遥控器红外按键码bean
 * email: hxc242313@qq.com
 */

public class ControllerBlocksBean {
    public List<blocksBean> blocks;

    public static class blocksBean {
        public String blockType;
        public String blockName;
        public String blockId;
        public List<keyBean> keys;
    }

    public static class keyBean {
        public String keyId;
        public String code;
        public String keyName;

    }
}
