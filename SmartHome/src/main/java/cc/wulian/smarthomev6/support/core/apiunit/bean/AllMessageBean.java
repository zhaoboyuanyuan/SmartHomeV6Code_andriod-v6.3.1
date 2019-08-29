package cc.wulian.smarthomev6.support.core.apiunit.bean;

import java.util.List;

/**
 * Created by Veev on 2017/6/14
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    AllMessageBean
 */

public class AllMessageBean {


    public List<MessageBean.RecordListBean> actionRecords;
    public List<MessageBean.RecordListBean> alarmRecords;

    /*public static class ActionRecordsBean {
        *//**
         * cmd : 500
         * createDate : 2017-06-14 18:20:19
         * devID : 9D9BF50E004B1200
         * endpoints : [{"clusters":[{"attributes":[{"attributeId":32770,"attributeValue":"6001"}],"clusterId":257}],"endpointNumber":1,"endpointStatus":"1","endpointType":10}]
         * gwID : 50294D20B3CC
         * gwName :
         * messageCode : 0103072
         * mode : 0
         * name : 玄武锁
         * roomID :
         * roomName :
         * time : 1497435619331
         * traceId : adb65f549dda717
         * type : OP
         *//*

        public String cmd;
        public String createDate;
        public String devID;
        public String gwID;
        public String gwName;
        public String messageCode;
        public int mode;
        public String name;
        public String roomID;
        public String roomName;
        public long time;
        public String traceId;
        public String type;
        public List<EndpointsBean> endpoints;

        public static class EndpointsBean {
            *//**
             * clusters : [{"attributes":[{"attributeId":32770,"attributeValue":"6001"}],"clusterId":257}]
             * endpointNumber : 1
             * endpointStatus : 1
             * endpointType : 10
             *//*

            public int endpointNumber;
            public String endpointStatus;
            public int endpointType;
            public List<ClustersBean> clusters;

            public static class ClustersBean {
                *//**
                 * attributes : [{"attributeId":32770,"attributeValue":"6001"}]
                 * clusterId : 257
                 *//*

                public int clusterId;
                public List<AttributesBean> attributes;

                public static class AttributesBean {
                    *//**
                     * attributeId : 32770
                     * attributeValue : 6001
                     *//*

                    public int attributeId;
                    public String attributeValue;
                }
            }
        }
    }

    public static class AlarmRecordsBean {
        *//**
         * cmd : 500
         * createDate : 2017-06-14 16:43:30
         * devID : 9D9BF50E004B1200
         * endpoints : [{"clusters":[{"attributes":[{"attributeId":0,"attributeValue":"7"}],"clusterId":257}],"endpointNumber":1,"endpointStatus":"1","endpointType":10}]
         * gwID : 50294D20B3CC
         * gwName :
         * messageCode : 0103021
         * mode : 0
         * name : 玄武锁
         * roomID :
         * roomName :
         * time : 1497429810317
         * traceId : 8dbb3fd07df6785f
         * type : OP
         *//*

        public String cmd;
        public String createDate;
        public String devID;
        public String gwID;
        public String gwName;
        public String messageCode;
        public int mode;
        public String name;
        public String roomID;
        public String roomName;
        public long time;
        public String traceId;
        public String type;
        public List<EndpointsBeanX> endpoints;

        public static class EndpointsBeanX {
            *//**
             * clusters : [{"attributes":[{"attributeId":0,"attributeValue":"7"}],"clusterId":257}]
             * endpointNumber : 1
             * endpointStatus : 1
             * endpointType : 10
             *//*

            public int endpointNumber;
            public String endpointStatus;
            public int endpointType;
            public List<ClustersBeanX> clusters;

            public static class ClustersBeanX {
                *//**
                 * attributes : [{"attributeId":0,"attributeValue":"7"}]
                 * clusterId : 257
                 *//*

                public int clusterId;
                public List<AttributesBeanX> attributes;

                public static class AttributesBeanX {
                    *//**
                     * attributeId : 0
                     * attributeValue : 7
                     *//*

                    public int attributeId;
                    public String attributeValue;
                }
            }
        }
    }*/
}
