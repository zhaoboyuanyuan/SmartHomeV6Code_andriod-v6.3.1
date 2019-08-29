package cc.wulian.smarthomev6.support.core.apiunit.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by mamengchao on 2017/3/6 0006.
 * Tips:搜索到的账户信息
 */

public class SearchAccountBean {
    @JSONField(name = "user")
    public String xPhone;
    public String userId;
    public String userName;
    public String avatar;
    public String xEmail;
}
