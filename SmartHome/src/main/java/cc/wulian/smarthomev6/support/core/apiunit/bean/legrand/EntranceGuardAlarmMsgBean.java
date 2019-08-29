package cc.wulian.smarthomev6.support.core.apiunit.bean.legrand;

import java.util.List;

public class EntranceGuardAlarmMsgBean {
    private List<LogListBean> logList;

    public List<LogListBean> getLogList() {
        return logList;
    }

    public void setLogList(List<LogListBean> logList) {
        this.logList = logList;
    }

    public static class LogListBean {
        /**
         * communityId : 70006363
         * id : 8
         * message : 有人经过wwww
         * time : 2019-07-24 10:32:32
         * uc : 1
         */

        private String communityId;
        private int id;
        private String message;
        private String time;
        private String uc;

        public String getCommunityId() {
            return communityId;
        }

        public void setCommunityId(String communityId) {
            this.communityId = communityId;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getUc() {
            return uc;
        }

        public void setUc(String uc) {
            this.uc = uc;
        }
    }
}
