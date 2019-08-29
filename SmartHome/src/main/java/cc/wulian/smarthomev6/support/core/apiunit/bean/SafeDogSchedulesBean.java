package cc.wulian.smarthomev6.support.core.apiunit.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 上海滩小马哥 on 2018/01/24.
 *
 */

public class SafeDogSchedulesBean {
    @JSONField(name = "12030")
    public List<SafeDogSchedulesDetailBean> schedulesType1;
    @JSONField(name = "12060")
    public List<SafeDogSchedulesDetailBean> schedulesType2;
    @JSONField(name = "12040")
    public List<SafeDogSchedulesDetailBean> schedulesType3;
    @JSONField(name = "12020")
    public List<SafeDogSchedulesDetailBean> schedulesType4;
    @JSONField(name = "12010")
    public List<SafeDogSchedulesDetailBean> schedulesType5;


    public List<ArrayList<SafeDogSchedulesDetailBean>> getData(){
        List data = new ArrayList();
        data.add(schedulesType1);
        data.add(schedulesType2);
        data.add(schedulesType3);
        data.add(schedulesType4);
        data.add(schedulesType5);
        return data;
    }
}
