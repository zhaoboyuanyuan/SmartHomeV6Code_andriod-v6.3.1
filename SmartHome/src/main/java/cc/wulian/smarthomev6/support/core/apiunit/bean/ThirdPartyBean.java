package cc.wulian.smarthomev6.support.core.apiunit.bean;

import java.io.Serializable;

/**
 * Created by Wensheng Sun on 2017/5/16.
 */

public class ThirdPartyBean implements Serializable{
    private static final long serialVersionUID = 1L;
    private int partnerId;
    private String openId;
    private String avatar;
    private String nick;
    private int gender;
    private boolean fromUser;
    private String unionid;

    public ThirdPartyBean(int partnerId,String openId,String unionid, String avatar,String nick,int gender,boolean fromUser) {
        this.partnerId=partnerId;
        this.openId = openId;
        this.unionid = unionid;
        this.avatar = avatar;
        this.nick = nick;
        this.gender = gender;
        this.fromUser=fromUser;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isFromUser() {
        return fromUser;
    }

    public void setFromUser(boolean fromUser) {
        this.fromUser = fromUser;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public int getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(int partnerId) {
        this.partnerId = partnerId;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }
}
