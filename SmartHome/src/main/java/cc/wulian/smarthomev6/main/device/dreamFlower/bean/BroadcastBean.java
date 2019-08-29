package cc.wulian.smarthomev6.main.device.dreamFlower.bean;

/**
 * created by huxc  on 2017/12/14.
 * func：  梦想之花播报bean
 * email: hxc242313@qq.com
 */

public class BroadcastBean  {
    public  String cmd;
    public  String gwID;
    public  String norVoice;
    public  String netVoice;
    public  String assistVoice;
    public  String local;
    public  String volum;
    public  String playTime;
    public String Switch;

    public String getAssistVoice() {
        return assistVoice;
    }

    public void setAssistVoice(String assistVoice) {
        this.assistVoice = assistVoice;
    }

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

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getNetVoice() {
        return netVoice;
    }

    public void setNetVoice(String netVoice) {
        this.netVoice = netVoice;
    }

    public String getNorVoice() {
        return norVoice;
    }

    public void setNorVoice(String norVoice) {
        this.norVoice = norVoice;
    }

    public String getplayTime() {
        return playTime;
    }

    public void setplayTime(String playTime) {
        this.playTime = playTime;
    }

    public String getSwitch() {
        return Switch;
    }

    public void setSwitch(String aSwitch) {
        Switch = aSwitch;
    }

    public String getVolum() {
        return volum;
    }

    public void setVolum(String volum) {
        this.volum = volum;
    }
}
