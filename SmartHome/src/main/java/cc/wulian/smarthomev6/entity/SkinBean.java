package cc.wulian.smarthomev6.entity;

/**
 * Created by zbl on 2017/10/16.
 */

public class SkinBean {
    public long createTime;
    public int id;
    public String imageUrl;
    public String themeUrl;
    public String title;
    public String themeId;
    public long startTime;
    public long endTime;
    public String state;
    public String size;
    /**
     * 0 未下载，1 已下载未使用，2 当前使用,3 正在下载中
     */
    public int status = 0;
}
