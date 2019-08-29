package cc.wulian.smarthomev6.support.core.mqtt.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by zbl on 2017/4/5.
 * 报警消息
 * bean不统一   这个弃用
 * @see cc.wulian.smarthomev6.support.core.apiunit.bean.MessageBean
 */
@Deprecated
public class AlarmMessageBean {
    public String alarmCode;
    public String devID;
    public String gwID;
    public String gwName;
    public String name;
    public String roomName;
    public String type;
    public int mode;
    public String extData;

    @JSONField(serialize = false)
    public String data;
}
