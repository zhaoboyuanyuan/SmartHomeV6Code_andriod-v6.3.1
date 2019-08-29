package cc.wulian.smarthomev6.support.core.apiunit.bean;

/**
 * Created by 上海滩小马哥 on 2018/01/24.
 * 安全狗扫描任务detail
 */

public class SafeDogSchedulesDetailBean {
    public String scheduleType;
    public String safedogId;
    public String deviceId;
    public String deviceType;
    public int status;              //任务状态// 完成 1;// 进行中2;// 失败 3；// 完成，有异常 4;
    public long createTime;
    public long updateTime;
    public String ip;
    public String type;             //设备类型，0:wifi 1:zb 2:ble（蓝牙） 3:protocal（协议）
    public String hostname;
}
