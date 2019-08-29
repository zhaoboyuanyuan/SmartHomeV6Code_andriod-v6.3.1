package cc.wulian.smarthomev6.support.core.apiunit.bean;

import java.util.List;

public class LcAlarmBean {
    /**
     * recordList : [{"time":1464277740,"channelId":"0","name":"device name","alarmId":123456,"localDate":"2016-05-19 00:00:00","type":1,"deviceId":"2342sdfl-df323-23","picurlArray":["http://lechangecloud/20160519"],"thumbUrl":"http://lechangecloud/20160519"}]
     * nextAlarmId : 123456
     */

    private long nextAlarmId;
    private List<RecordListBean> recordList;

    public long getNextAlarmId() {
        return nextAlarmId;
    }

    public void setNextAlarmId(long nextAlarmId) {
        this.nextAlarmId = nextAlarmId;
    }

    public List<RecordListBean> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<RecordListBean> recordList) {
        this.recordList = recordList;
    }

    public static class RecordListBean {
        /**
         * time : 1464277740
         * channelId : 0
         * name : device name
         * alarmId : 123456
         * localDate : 2016-05-19 00:00:00
         * type : 1
         * deviceId : 2342sdfl-df323-23
         * picurlArray : ["http://lechangecloud/20160519"]
         * thumbUrl : http://lechangecloud/20160519
         */

        private long time;
        private String channelId;
        private String name;
        private long alarmId;
        private String localDate;
        private int type;
        private String deviceId;
        private String thumbUrl;
        private List<String> picurlArray;

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getAlarmId() {
            return alarmId;
        }

        public void setAlarmId(long alarmId) {
            this.alarmId = alarmId;
        }

        public String getLocalDate() {
            return localDate;
        }

        public void setLocalDate(String localDate) {
            this.localDate = localDate;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getThumbUrl() {
            return thumbUrl;
        }

        public void setThumbUrl(String thumbUrl) {
            this.thumbUrl = thumbUrl;
        }

        public List<String> getPicurlArray() {
            return picurlArray;
        }

        public void setPicurlArray(List<String> picurlArray) {
            this.picurlArray = picurlArray;
        }
    }
}
