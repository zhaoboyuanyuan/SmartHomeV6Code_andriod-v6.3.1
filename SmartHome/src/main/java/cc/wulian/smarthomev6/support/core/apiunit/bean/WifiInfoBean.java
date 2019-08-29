package cc.wulian.smarthomev6.support.core.apiunit.bean;

/**
 * created by huxc  on 2017/8/29.
 * func： mini网关wifi信息Bean
 * email: hxc242313@qq.com
 */

public class WifiInfoBean {
    public String essid;
    public String encryption;
    public String address;
    public String quality;
    public String mode;
    public String channel;
    public String signal;


    public WifiInfoBean(String essid, String encryption, String address, String quality, String mode, String channel, String signal) {
        this.essid = essid;
        this.encryption = encryption;
        this.address = address;
        this.quality = quality;
        this.mode = mode;
        this.channel = channel;
        this.signal = signal;
    }

    public WifiInfoBean() {

    }
}
