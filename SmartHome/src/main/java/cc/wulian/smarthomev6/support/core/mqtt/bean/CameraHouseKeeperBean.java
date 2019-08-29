package cc.wulian.smarthomev6.support.core.mqtt.bean;

/**
 * created by huxc  on 2018/1/25.
 * func：
 * email: hxc242313@qq.com
 */

public class CameraHouseKeeperBean {
    public String messageCode;
    public String devID;
    public String gwID;
    public ExtData1 extData1;


    public static class ExtData1{
        public String cmd;
        public String devID;
        public String gwID;
        public String messageCode;
        public String mode;
        public String name;
        public String time;
        public String type;
    }
}
