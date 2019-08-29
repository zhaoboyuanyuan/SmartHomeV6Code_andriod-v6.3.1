package cc.wulian.smarthomev6.entity;

/**
 * Created by Veev on 2017/4/5
 *
 * 消息中心 数据格式
 */

public class MessageInfo {
    public String date;
    public String msg;

    public MessageInfo(String date, String msg) {
        this.date = date;
        this.msg = msg;
    }
}
