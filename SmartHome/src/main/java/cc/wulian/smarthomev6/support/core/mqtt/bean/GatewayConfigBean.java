package cc.wulian.smarthomev6.support.core.mqtt.bean;

/**
 * Created by zbl on 2017/4/5.
 * 网关信息
 */

public class GatewayConfigBean {
    public String cmd;
    public String gwID;
    public String appID;
    public String d;            // 设备ID
    /**
     * 模式：(1:新增,2:更新,3:获取,4:删除,5获取自增值,6创建自增变量,7删除该设备下所有的数据)
     */
    public int m;
    public String t;
    public String k;
    public String v;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GatewayConfigBean{");
        sb.append("cmd='").append(cmd).append('\'');
        sb.append(", gwID='").append(gwID).append('\'');
        sb.append(", appID='").append(appID).append('\'');
        sb.append(", d='").append(d).append('\'');
        sb.append(", m=").append(m);
        sb.append(", t='").append(t).append('\'');
        sb.append(", k='").append(k).append('\'');
        sb.append(", v='").append(v).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
