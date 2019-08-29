package cc.wulian.smarthomev6.entity;

import java.util.List;

/**
 * Created by Veev on 2017/7/17
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    UserPushInfo
 */

public class UserPushInfo {
    public List<UserPushInfoBean> userPushInfo;

    public static class UserPushInfoBean {
        /**
         * deviceId : DC43150B004B1200
         * gradeType : 2
         * logPushFlag : 0
         * pushFlag : 1
         * totalPushFlag : 2
         * uid : 26ce0a92ff86425cab5626c1a88272e8
         */

        public String deviceId;
        public int gradeType;
        public int logPushFlag;
        public int pushFlag;
        public int totalPushFlag;
        public String uid;
        public String name;
        public String type;
        public String pushTime;
        public String time;
        public String timeZone;

        public UserPushInfoBean(String deviceId,int pushFlag, String name) {
            this.deviceId = deviceId;
            this.name = name;
            this.pushFlag = pushFlag;
        }

        public UserPushInfoBean() {
        }
    }
}
