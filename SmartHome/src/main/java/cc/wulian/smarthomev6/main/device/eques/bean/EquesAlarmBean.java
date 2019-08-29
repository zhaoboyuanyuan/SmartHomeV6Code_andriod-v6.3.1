package cc.wulian.smarthomev6.main.device.eques.bean;

import java.util.List;

/**
 * 作者: chao
 * 时间: 2017/6/13
 * 描述: 报警消息
 * 联系方式: 805901025@qq.com
 */

public class EquesAlarmBean {
    public int code;
    public String method;
    public int offset;
    public int limit;
    public int max;
    public List<EquesAlarmDetailBean> alarms;
}
