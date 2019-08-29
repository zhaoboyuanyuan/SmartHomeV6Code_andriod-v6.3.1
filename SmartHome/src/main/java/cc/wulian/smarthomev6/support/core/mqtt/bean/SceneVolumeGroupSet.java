package cc.wulian.smarthomev6.support.core.mqtt.bean;

import java.util.List;

public class SceneVolumeGroupSet {

    /**
     * cmd : cmd
     * gwID : gwID
     * appID : appID
     * groupID : groupID
     * data : ["sceneID1","sceneID2"]
     */

    private String cmd;
    private String gwID;
    private String appID;
    private String groupID;
    private List<String> data;

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

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
