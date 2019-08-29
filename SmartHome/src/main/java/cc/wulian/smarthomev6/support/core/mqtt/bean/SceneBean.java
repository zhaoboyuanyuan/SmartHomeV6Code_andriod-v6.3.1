package cc.wulian.smarthomev6.support.core.mqtt.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by zbl on 2017/4/11.
 * 场景
 */

public class SceneBean {
    public String sceneID;
    public String programID;
    public String name;
    public String icon;
    public String status;
    public String gwID;
    public String groupID;
    public int mode;

    @JSONField(serialize = false)
    public String data;
}
