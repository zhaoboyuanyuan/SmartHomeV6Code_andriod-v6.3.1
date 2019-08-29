package cc.wulian.smarthomev6.entity;

import cc.wulian.smarthomev6.support.core.apiunit.bean.SafeDogSchedulesDetailBean;

/**
 * Created by 上海滩小马哥 on 2018/01/26.
 * 安全狗扫描任务，SafedogScanningAdapter用
 */

public class SafeDogScaningInfo {
    private String schedulesType;  //任务类型
    private String deviceId;
    private String deviceType;
    private int status;         //扫描状态
    private long createTime;
    private long updateTime;

    public boolean isTittle;
    public boolean isEnd;
    private int tittleStatus;         // 完成 1  进行中2  失败 3   完成，有异常 4;
    private String ip;
    private String type;
    private String hostname;

    public SafeDogScaningInfo(SafeDogSchedulesDetailBean bean) {
        this.schedulesType = bean.scheduleType;
        this.deviceId = bean.deviceId;
        this.deviceType = bean.deviceType;
        this.status = bean.status;
        this.createTime = bean.createTime;
        this.updateTime = bean.updateTime;
        this.ip = bean.ip;
        this.type = bean.type;
        this.hostname = bean.hostname;
        this.isTittle = false;
        this.isEnd = false;
        tittleStatus = 2;
    }

    public SafeDogScaningInfo(String schedulesType) {
        this.schedulesType = schedulesType;
        this.isTittle = true;
        this.isEnd = false;
        tittleStatus = 2;
    }

    public void updateItem(SafeDogSchedulesDetailBean bean){
        this.schedulesType = bean.scheduleType;
        this.deviceId = bean.deviceId;
        this.deviceType = bean.deviceType;
        this.status = bean.status;
        this.createTime = bean.createTime;
        this.updateTime = bean.updateTime;
        this.ip = bean.ip;
        this.type = bean.type;
        this.hostname = bean.hostname;
    }

    public String getSchedulesType() {
        return schedulesType;
    }

    public void setSchedulesType(String schedulesType) {
        this.schedulesType = schedulesType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getTittleStatus() {
        return tittleStatus;
    }

    public void setTittleStatus(int tittleStatus) {
        this.tittleStatus = tittleStatus;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
}
