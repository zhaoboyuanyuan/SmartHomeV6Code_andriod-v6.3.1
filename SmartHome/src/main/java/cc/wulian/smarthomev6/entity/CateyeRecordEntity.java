package cc.wulian.smarthomev6.entity;

import java.io.Serializable;

/**
 * Created by hxc on 2017/3/6.
 * func:访客/报警记录实体类
 * 值得注意的是url为获取图片的返回参数，picUrl和videoUrl是获取视频的返回的参数
 */

public class CateyeRecordEntity implements Serializable {
    public String url;
    public int isRead;
    private int createdat;
    public String url_pic;
    public String url_video;

    public int getCreatedat() {
        return createdat;
    }

    public void setCreatedat(int createdat) {
        this.createdat = createdat;
    }
}
