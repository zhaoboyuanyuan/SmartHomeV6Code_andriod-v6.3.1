package cc.wulian.smarthomev6.main.device.eques.bean;

/**
 * 作者: chao
 * 时间: 2017/6/6
 * 描述: 绑定移康猫眼返回结果
 * 联系方式: 805901025@qq.com
 */

public class OnAddbdyResultBean {
    public String method;
    public String code;
    public Info added_bdy;

    public static class Info{
        public String bid;
    }
}
