package cc.wulian.smarthomev6.support.core.apiunit.bean;

import java.util.List;

/**
 * Created by Veev on 2017/4/7
 */

public class MessageBean {


    public List<RecordListBean> recordList;

    public static class RecordListBean {
        /**
         * _id : {"counter":2909471,"date":1491994315000,"machineIdentifier":9513769,"processIdentifier":4859,"time":1491994315000,"timeSecond":1491994315,"timestamp":1491994315}
         * alarmCode : 1010301
         * cmd : 500
         * devID : 8178A803004B1200
         * endpoints : [{"clusters":[{"attributes":[{"attributeId":2,"attributeValue":"1"}],"clusterId":1280}],"endpointNumber":1,"endpointStatus":"1","endpointType":1026}]
         * gwID : 50294DFF1289
         * gwName : 小黑
         * mode : 0
         * name : #$default$#001
         * roomID : 2
         * roomName : 减肥
         * time : 1491994315078
         * type : 03
         */

        public IdBean _id;
        public String alarmCode;
        public String messageCode;
        public String cmd;
        public String devID;
        public String gwID;
        public String gwName;
        public int mode;
        public String status;
        public String name;
        public String roomID;
        public String roomName;
        public String extData;
        public String extData1;//新增，extData仍保留
        public long time;
        public String type;
        public String createDate;
        public String programName;
        public PictureBean picture;
        public List<EndpointsBean> endpoints;

        public String actCode;
        public String user;//随便看被其他用户绑定时用

        public static class PictureBean {
            public String pictureIndex;
            public String pictureURL;
        }

        public static class IdBean {
            /**
             * counter : 2909471
             * date : 1491994315000
             * machineIdentifier : 9513769
             * processIdentifier : 4859
             * time : 1491994315000
             * timeSecond : 1491994315
             * timestamp : 1491994315
             */

            public int counter;
            public long date;
            public int machineIdentifier;
            public int processIdentifier;
            public long time;
            public int timeSecond;
            public int timestamp;
        }

        public static class EndpointsBean {
            /**
             * clusters : [{"attributes":[{"attributeId":2,"attributeValue":"1"}],"clusterId":1280}]
             * endpointNumber : 1
             * endpointStatus : 1
             * endpointType : 1026
             */

            public int endpointNumber;
            public String endpointStatus;
            public String endpointName;
            public int endpointType;
            public List<ClustersBean> clusters;

            public static class ClustersBean {
                /**
                 * attributes : [{"attributeId":2,"attributeValue":"1"}]
                 * clusterId : 1280
                 */

                public int clusterId;
                public List<AttributesBean> attributes;

                public static class AttributesBean {
                    /**
                     * attributeId : 2
                     * attributeValue : 1
                     */

                    public int attributeId;
                    public String attributeValue;
                }
            }
        }
    }
}
