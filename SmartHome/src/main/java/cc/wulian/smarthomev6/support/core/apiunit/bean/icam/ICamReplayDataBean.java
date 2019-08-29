
package cc.wulian.smarthomev6.support.core.apiunit.bean.icam;

import com.alibaba.fastjson.annotation.JSONField;

public class ICamReplayDataBean {

    @JSONField(name = "AccessKeyId")
    private String mAccessKeyId;
    @JSONField(name = "AccessKeySecret")
    private String mAccessKeySecret;
    @JSONField(name = "Bucket")
    private String mBucket;
    @JSONField(name = "Region")
    private String mRegion;
    @JSONField(name = "RequestId")
    private String mRequestId;
    @JSONField(name = "SecurityToken")
    private String mSecurityToken;

    public String getAccessKeyId() {
        return mAccessKeyId;
    }

    public void setAccessKeyId(String AccessKeyId) {
        mAccessKeyId = AccessKeyId;
    }

    public String getAccessKeySecret() {
        return mAccessKeySecret;
    }

    public void setAccessKeySecret(String AccessKeySecret) {
        mAccessKeySecret = AccessKeySecret;
    }

    public String getBucket() {
        return mBucket;
    }

    public void setBucket(String Bucket) {
        mBucket = Bucket;
    }

    public String getRegion() {
        return mRegion;
    }

    public void setRegion(String Region) {
        mRegion = Region;
    }

    public String getRequestId() {
        return mRequestId;
    }

    public void setRequestId(String RequestId) {
        mRequestId = RequestId;
    }

    public String getSecurityToken() {
        return mSecurityToken;
    }

    public void setSecurityToken(String SecurityToken) {
        mSecurityToken = SecurityToken;
    }

}
