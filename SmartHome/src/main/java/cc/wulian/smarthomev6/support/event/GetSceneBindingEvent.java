package cc.wulian.smarthomev6.support.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.support.core.mqtt.bean.SceneBindingBean;
import cc.wulian.smarthomev6.support.utils.StringUtil;

/**
 * Created by yuxx on 2017/6/7.
 */

public class GetSceneBindingEvent {
    public String jsonData;
    public GetSceneBindingEvent(String jsonData) {
        this.jsonData = jsonData;
    }

    public List<SceneBindingBean> getSceneBindingList() {
        List<SceneBindingBean> sceneBindingList = new ArrayList<>();
        if (!StringUtil.isNullOrEmpty(this.jsonData)) {
            JSONObject jsonObject= JSON.parseObject(this.jsonData);
            JSONArray jsonArray=jsonObject.getJSONArray("data");
            if(jsonArray!=null&&jsonArray.size()>0){
                for(int i=0;i<jsonArray.size();i++){
                    SceneBindingBean sceneBindingBean=new SceneBindingBean();
                    sceneBindingBean.sceneID=jsonArray.getJSONObject(i).getString("sceneID");
                    sceneBindingBean.endpointNumber=jsonArray.getJSONObject(i).getString("endpointNumber");
                    sceneBindingList.add(sceneBindingBean);
                }
            }
        }
        return sceneBindingList;
    }
    public String getDevID(){
        String devID="";
        if(!StringUtil.isNullOrEmpty(this.jsonData)){
            JSONObject jsonObject= JSON.parseObject(this.jsonData);
            devID=jsonObject.getString("devID");
        }
        return devID;
    }
}
