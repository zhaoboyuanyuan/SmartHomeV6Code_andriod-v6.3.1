package cc.wulian.smarthomev6.main.device.eques.bean;

import java.io.Serializable;

/**
 * 作者: chao
 * 时间: 2017/6/9
 * 描述: 猫眼移动侦测信息
 * 联系方式: 805901025@qq.com
 */

public class EquesPirInfoBean implements Serializable {
    private static final long serialVersionUID = 1L;

    public String method;
    public String from;
    public String to;
    public int Capture_num;
    public int format;
    public int ringtone;
    public int sense_sensitivity;
    public int sense_time;
    public int volume;
}
