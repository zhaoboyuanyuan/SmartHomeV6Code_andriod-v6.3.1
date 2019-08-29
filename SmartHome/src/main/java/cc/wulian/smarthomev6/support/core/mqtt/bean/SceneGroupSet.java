package cc.wulian.smarthomev6.support.core.mqtt.bean;

public class SceneGroupSet {

    /**
     * cmd : cmd
     * gwID : gwID
     * mode : mode
     * groupID : groupID
     * name : name
     */

    private String cmd;
    private String gwID;
    private int mode;
    private String groupID;
    private String name;

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

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

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
