package cc.wulian.smarthomev6.entity;

/**
 * 描述：广告信息</br>
 * @author Eden Cheng</br>
 * @version 2015年4月23日 上午11:32:53
 */

public class AdvertisementEntity {
    String id = "";
    String url = "";
    String res = "";
    String content = "";
    String type = "";
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getRes() {
        return res;
    }

    public void setRes(String res) {
        this.res = res;
    }
}
