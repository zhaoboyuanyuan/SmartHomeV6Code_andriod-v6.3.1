package cc.wulian.smarthomev6.support.core.apiunit.bean;

import java.util.List;

public class LcLocalRecordBean {
    private List<RecordsBean> records;

    public List<RecordsBean> getRecords() {
        return records;
    }

    public void setRecords(List<RecordsBean> records) {
        this.records = records;
    }

    public static class RecordsBean {
        /**
         * recordId : /mnt/sd/2016-05-19/001/dav/00/00.30.59-00.31.36[M][0@0][0].mp4
         * fileLength : 713883
         * channelId : 0
         * beginTime : 2016-05-19 00:30:59
         * endTime : 2016-05-19 00:31:36
         * type : Event
         */

        private String recordId;
        private long fileLength;
        private String channelId;
        private String beginTime;
        private String endTime;
        private String type;

        public String getRecordId() {
            return recordId;
        }

        public void setRecordId(String recordId) {
            this.recordId = recordId;
        }

        public long getFileLength() {
            return fileLength;
        }

        public void setFileLength(long fileLength) {
            this.fileLength = fileLength;
        }

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public String getBeginTime() {
            return beginTime;
        }

        public void setBeginTime(String beginTime) {
            this.beginTime = beginTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
