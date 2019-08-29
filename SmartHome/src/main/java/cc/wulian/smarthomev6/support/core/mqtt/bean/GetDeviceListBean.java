package cc.wulian.smarthomev6.support.core.mqtt.bean;

import java.util.List;

/**
 * Created by zbl on 2018/1/11.
 * 获取设备列表上报，返回结果
 */

public class GetDeviceListBean {
    public String cmd;
    public String gwID;
    public String appID;
    public String time;
    public int mode;
    public int devCount;
    public List<String> data;
}
