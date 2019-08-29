package cc.wulian.smarthomev6.support.core.apiunit.bean;

/**
 * Created by 上海滩小马哥 on 2018/01/24.
 * 安全狗首页信息
 */

public class SafeDogHomeDataBean {
    public int scanStatus;
    public int status;
    public Sum protectDevices;
    public Sum unprotectDevices;
    public Sum attacks;

    public static class Sum{
        public String sum;
    }
}
