package cc.wulian.smarthomev6.main.device.cylincam.bean;

import java.io.Serializable;

/**
 * created by huxc  on 2017/9/18.
 * func： 小物摄像机安全防护bean
 * email: hxc242313@qq.com
 */

public class CylincamProtectBean implements Serializable {
    public CylincamProtectBean(int switching, int sensitivity, int[] area, int defenceused, int week, byte[] moveTime) {
        this.switching = switching;
        this.sensitivity = sensitivity;
        this.area = area;
        this.defenceused = defenceused;
        this.week = week;
        this.moveTime = moveTime;
    }

    private int switching;
    private int sensitivity;
    private int[] area;
    private int defenceused;
    private int week;
    private byte[] moveTime;

    public int getSwitching() {
        return switching;
    }

    public void setSwitching(int switching) {
        this.switching = switching;
    }

    public int getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(int sensitivity) {
        this.sensitivity = sensitivity;
    }

    public int[] getArea() {
        return area;
    }

    public void setArea(int[] area) {
        this.area = area;
    }

    public int getDefenceused() {
        return defenceused;
    }

    public void setDefenceused(int defenceused) {
        this.defenceused = defenceused;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public byte[] getMoveTime() {
        return moveTime;
    }

    public void setMoveTime(byte[] moveTime) {
        this.moveTime = moveTime;
    }
}
