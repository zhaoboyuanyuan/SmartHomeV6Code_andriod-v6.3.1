package cc.wulian.smarthomev6.support.core.apiunit.bean;

import java.util.List;

/**
 * Created by Veev on 2017/4/10
 */

public class MessageCountBean {
    /**
     * totalCount : 0
     * childDevices : [{"childDeviceId":"xx","count":2,"lastMessage":"xx"}]
     * deviceId : 50294DFF1289
     */

    public int totalCount;
    public String deviceId;
    public List<ChildDevicesBean> childDevices;

    public static class ChildDevicesBean {
        /**
         * childDeviceId : xx
         * count : 2
         * lastMessage : xx
         */

        public String childDeviceId;
        public int count;
        public String lastMessage;
        public String name;
        public String type;
        public String timestamp;        //时间戳（注意6.1.5新增返回两个字段，一个时间戳和message，app端需要根据时间戳解析到对应的手机时区，然后把两个字段拼起来）
        public String message;          //最后一条告警消息（注意6.1.5新增返回两个字段）
    }
}
