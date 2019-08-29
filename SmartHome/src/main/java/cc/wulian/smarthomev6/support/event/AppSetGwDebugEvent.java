package cc.wulian.smarthomev6.support.event;


import org.json.JSONException;
import org.json.JSONObject;

import cc.wulian.smarthomev6.support.utils.StringUtil;

/**
 * Created by Administrator on 2017/8/25.
 */

public class AppSetGwDebugEvent {
    /*{"appID":"android862638039613322","cmd":"315","gwID":"50294D102313","status":"1"}*/
    private String jsonData;
    public AppSetGwDebugEvent(String jsonData){
        this.jsonData=jsonData;
        if(!StringUtil.isNullOrEmpty(this.jsonData)){
            try {
                JSONObject json=new JSONObject(this.jsonData);
                appID=json.getString("appID");
                gwID=json.getString("gwID");
                status=json.getString("status");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public String appID="";
    public String gwID="";
    public String status="";

}
