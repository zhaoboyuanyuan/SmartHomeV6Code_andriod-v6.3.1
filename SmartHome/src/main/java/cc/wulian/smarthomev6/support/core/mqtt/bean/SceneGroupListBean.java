package cc.wulian.smarthomev6.support.core.mqtt.bean;

import java.util.List;

public class SceneGroupListBean {

    /**
     * cmd : cmd
     * gwID : gwID
     * data : [{"groupID":"groupID","name":"name"}]
     */

    private String cmd;
    private String gwID;
    private List<DataBean> data;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getGwID() {
        return gwID;
    }

    public void setGwID(String gwID) {
        this.gwID = gwID;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * groupID : groupID
         * name : name
         */

        private String groupID;
        private String name;

        public String getGroupID() {
            return groupID;
        }

        public void setGroupID(String groupID) {
            this.groupID = groupID;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
