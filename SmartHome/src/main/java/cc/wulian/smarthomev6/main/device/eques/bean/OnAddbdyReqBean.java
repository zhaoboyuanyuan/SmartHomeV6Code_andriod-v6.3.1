package cc.wulian.smarthomev6.main.device.eques.bean;

/**
 * 作者: chao
 * 时间: 2017/6/5
 * 描述: 绑定设备确认
 * 联系方式: 805901025@qq.com
 */

public class OnAddbdyReqBean {
    public String reqId;
    public String bdyName;
    public Extra extra;

    public static class Extra{
        public String oldbdy;
    }
}
