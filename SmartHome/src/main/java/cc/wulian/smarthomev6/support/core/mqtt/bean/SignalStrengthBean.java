package cc.wulian.smarthomev6.support.core.mqtt.bean;

public class SignalStrengthBean {

    /**
     * cmd : cmd
     * gwID : gwID
     * data : data
     * value : value
     */



    private String cmd;
    private String gwID;
    private String data;
    private int value;

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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
