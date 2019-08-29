package cc.wulian.smarthomev6.entity;

/**
 * 作者: chao
 * 时间: 2017/5/17
 * 描述:
 * 联系方式: 805901025@qq.com
 */

public class VisitRecordEntity {
    public String date;
    public String url;
    public String msg;
    public long time;
    public String argUrl;//原始的地址
    public String bucket;
    public String region;
    public ExtraData1 extData1;

    public static class ExtraData1{
        public String bucket;
        public String region;
    }
}
