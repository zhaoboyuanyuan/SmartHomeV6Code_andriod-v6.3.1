package cc.wulian.smarthomev6.support.core.apiunit.bean;


import java.io.Serializable;

public class LcCustomTimeBean implements Serializable {

    /**
     * 0 item, 1 group
     */
    public boolean isGroup;
    public String time;

    public LcCustomTimeBean(String time, boolean isGroup) {
        this.time = time;
        this.isGroup = isGroup;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
