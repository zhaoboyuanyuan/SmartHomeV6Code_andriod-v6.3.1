package cc.wulian.smarthomev6.support.core.apiunit.bean;

import java.util.List;

/**
 * 作者: mamengchao
 * 时间: 2017/4/1 0001
 * 描述:
 * 联系方式: 805901025@qq.com
 */

public class AreaBean {
    public String groupId;
    public String groupName;
    public List<ChildDeviceInfoBean> devices;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AreaBean{");
        sb.append("groupId='").append(groupId).append('\'');
        sb.append(", groupName='").append(groupName).append('\'');
        sb.append(", devices=").append(devices);
        sb.append('}');
        return sb.toString();
    }
}
