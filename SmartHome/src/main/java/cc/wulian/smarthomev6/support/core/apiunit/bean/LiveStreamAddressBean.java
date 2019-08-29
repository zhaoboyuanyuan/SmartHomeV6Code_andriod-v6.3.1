package cc.wulian.smarthomev6.support.core.apiunit.bean;

/**
 * created by huxc  on 2018/1/29.
 * func： 直播流地址
 * email: hxc242313@qq.com
 */

public class LiveStreamAddressBean {

    public int expire;
    public VideoBean video;
    public int status;

   public static class VideoBean {
        public String pic;
        public String rtmp;
        public String hls;
        public String flv;
    }
}
