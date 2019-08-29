package cc.wulian.smarthomev6.main.device.eques.bean;

import java.util.List;

/**
 * 作者: chao
 * 时间: 2017/6/5
 * 描述: 查询移康猫眼列表
 * 联系方式: 805901025@qq.com
 */

public class BdylistBean {
    public String method;
    public String localupg;
    public List<Bdylist> bdylist;
    public List<Onlines> onlines;

    public static class Bdylist{
        public String name;
        public String nick;
        public String role;
        public String bid;
    }

    public static class Onlines{
        public String bid;
        public String uid;
        public String nid;
        public String status;
        public String remoteupg;
    }

}
