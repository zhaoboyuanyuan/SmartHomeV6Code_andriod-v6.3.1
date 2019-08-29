package cc.wulian.smarthomev6.entity;

import java.io.Serializable;

/**
 * Created hxc Administrator on 2017/2/15.
 * func:新猫眼推送消息实体类
 */

public class CateyePushEntity implements Serializable {
    private String picUri;
    private String picIndex;
    private String originAccount;
    private String cmd;
    private String time;
    private String status;

    public CateyePushEntity(){

    }

    public CateyePushEntity(String picUri, String picIndex, String originAccount, String cmd, String time, String status) {
        this.picUri = picUri;
        this.picIndex = picIndex;
        this.originAccount = originAccount;
        this.cmd = cmd;
        this.time = time;
        this.status = status;
    }
    public String getPicUri() {
        return picUri;
    }

    public void setPicUri(String picUri) {
        this.picUri = picUri;
    }

    public String getPicIndex() {
        return picIndex;
    }

    public void setPicIndex(String picIndex) {
        this.picIndex = picIndex;
    }

    public String getOriginAccount() {
        return originAccount;
    }

    public void setOriginAccount(String originAccount) {
        this.originAccount = originAccount;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
