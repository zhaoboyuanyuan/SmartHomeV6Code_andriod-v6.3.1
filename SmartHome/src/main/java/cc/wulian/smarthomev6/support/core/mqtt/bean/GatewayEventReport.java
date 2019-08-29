package cc.wulian.smarthomev6.support.core.mqtt.bean;

public class GatewayEventReport {
    /**
     * cmd : cmd
     * gwID : gwID
     * event : event
     */

    private String cmd;
    private String gwID;
    private int event;

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

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }
}
