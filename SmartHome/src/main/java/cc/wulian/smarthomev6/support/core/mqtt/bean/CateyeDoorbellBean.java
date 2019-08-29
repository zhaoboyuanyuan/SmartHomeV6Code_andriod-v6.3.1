package cc.wulian.smarthomev6.support.core.mqtt.bean;

/**
 * Created by zbl on 2017/5/22.
 * 猫眼门铃来电
 */
public class CateyeDoorbellBean {
    public String messageCode;
    public String devID;
    public String extData;
    public ResourseData extData1;


    public static class ResourseData{
        public String bucket;
        public String region;
    }
}
