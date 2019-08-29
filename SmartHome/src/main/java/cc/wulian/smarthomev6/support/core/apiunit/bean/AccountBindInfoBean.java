package cc.wulian.smarthomev6.support.core.apiunit.bean;

import java.util.List;

/**
 * 作者: chao
 * 时间: 2017/7/6
 * 描述: 查询第三方绑定信息
 * 联系方式: 805901025@qq.com
 */

public class AccountBindInfoBean {
    public List<BindInfo> userThirds;

    public static class BindInfo{
        public String thirdOpenId;
        public int thirdType;
    }
}
