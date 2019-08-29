package cc.wulian.smarthomev6.support.core.mqtt.bean;

import java.util.List;

/**
 * Created by 上海滩小马哥 on 2017/12/12.
 * 红外转发type:22   遥控器
 */

public class Device22RemoteBean {
    public String cmd;
    public String gwID;
    public String appID;
    public String devID;
    public int operType;   //1.增 2.删 3.改 4.查
    public int mode;   //1.遥控器 2.单个按键 3.所有按键
    public List<RemoteData> data;

    public static class RemoteData {
        public String type;
        public String index;
        public String name;
    }
}
