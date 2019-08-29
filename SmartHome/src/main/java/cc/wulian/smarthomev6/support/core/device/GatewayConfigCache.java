package cc.wulian.smarthomev6.support.core.device;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Base64;

import com.alibaba.fastjson.JSON;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import cc.wulian.smarthomev6.entity.UeiConfig;
import cc.wulian.smarthomev6.support.core.mqtt.bean.GatewayConfigBean;
import cc.wulian.smarthomev6.support.utils.StringUtil;

/**
 * Created by Veev on 2017/5/19
 * Tel: 18365264930
 * QQ: 2355738466
 * Function:  网关配置
 */

public class GatewayConfigCache {

    private ArrayMap<String, GatewayConfigBean> map = new ArrayMap<>();
    private Map<String, Integer> ueiCustomKeyNum = new ArrayMap<>();

    public GatewayConfigCache() {
    }

    public void put(@NonNull GatewayConfigBean value) {
        // m = 4 为已删除的缓存数据, 不使用
        // 同时清除这个key
        if (value.m == 4) {
            map.remove(value.d + value.k);
            return;
        }
        map.put(value.d + value.k, value);
        if (TextUtils.equals(value.k, "list")){
            saveUeiCustomKeyNum(value.d, value);
        }
    }

    public GatewayConfigBean get(String deviceId, String key) {
        return map.get(deviceId + key);
    }

    public int getMaxNumByDeviceId(String deviceId){
        if (ueiCustomKeyNum.get(deviceId) == null){
            return 0;
        }
        return ueiCustomKeyNum.get(deviceId);
    }

    public void setMaxNumByDeviceId(String deviceId, int num){
        ueiCustomKeyNum.put(deviceId, num);
    }

    public void clear() {
        map.clear();
    }

    private void saveUeiCustomKeyNum(String deviceId, GatewayConfigBean value){
        if (value != null && !TextUtils.isEmpty(value.v)){
            List<UeiConfig> configs = JSON.parseArray(value.v, UeiConfig.class);
            int maxValue = 0;
            for (UeiConfig ueiConfig : configs) {
                if (TextUtils.isEmpty(ueiConfig.pick)){
                    Map<String, String> learnKeyCodeArr = ueiConfig.learnKeyCodeDic;
                    for (String key:learnKeyCodeArr.keySet()){
                        if (!TextUtils.isEmpty(learnKeyCodeArr.get(key))){
                            int num = Integer.valueOf(learnKeyCodeArr.get(key), 16);
                            maxValue = maxValue>num?maxValue:num;
                        }
                    }
                }
            }
            ueiCustomKeyNum.put(deviceId, maxValue);
        }else {
            ueiCustomKeyNum.put(value.d, 0);
        }
    }
}
