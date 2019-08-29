package cc.wulian.smarthomev6.support.core.mqtt.bean;

import com.alibaba.fastjson.JSONObject;

/**
 * 作者: chao
 * 时间: 2017/7/14
 * 描述:
 * 联系方式: 805901025@qq.com
 */

public class IcamBindBean {
    public String cmd;
    public String uId;
    public String devID;
    public String type;
    public String name;
    public String sdomain;
    public String location;

    public IcamBindBean(String msg) {
        JSONObject jsonObject = JSONObject.parseObject(msg);
        this.cmd = jsonObject.getString("cmd");
        this.uId = jsonObject.getString("uId");
        this.devID = jsonObject.getString("devID");
        this.type = jsonObject.getString("type");
        this.name = jsonObject.getString("name");
        this.sdomain = jsonObject.getString("sdomain");
        this.location = jsonObject.getString("location");
    }
}
