package cc.wulian.smarthomev6.main.device.eques.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 作者: chao
 * 时间: 2017/6/13
 * 描述: 报警消息列表
 * 联系方式: 805901025@qq.com
 */

public class EquesAlarmDetailBean implements Serializable{

    private static final long serialVersionUID = 1L;

    public String uid;
    public String aid;
    public long time;
    public List<String> fid;  //报警图片 id 列表
    public String bid;
    public int type;
    public List<String> pvid;   //预览图片(缩略图)id 列表
    public String name;
    public long create;
    public int recordType = 0;    //消息中心用。0：报警，1：门铃来电
}
