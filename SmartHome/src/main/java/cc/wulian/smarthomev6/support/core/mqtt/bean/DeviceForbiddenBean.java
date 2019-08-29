package cc.wulian.smarthomev6.support.core.mqtt.bean;

import java.util.List;

public class DeviceForbiddenBean {
    /**
     * cmd : 531
     * gwID : xxxxx
     * devIDs : ["aaa","bbb"]
     * type : 0
     * status : 0
     */

    private String cmd;
    private String gwID;
    private int type;
    private int status;
    private List<String> devIDs;

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<String> getDevIDs() {
        return devIDs;
    }

    public void setDevIDs(List<String> devIDs) {
        this.devIDs = devIDs;
    }
}
