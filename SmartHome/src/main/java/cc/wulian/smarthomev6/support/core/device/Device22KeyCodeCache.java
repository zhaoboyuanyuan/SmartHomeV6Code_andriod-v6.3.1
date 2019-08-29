package cc.wulian.smarthomev6.support.core.device;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.mqtt.bean.Device22DetailBean;
import cc.wulian.smarthomev6.support.core.mqtt.bean.Device22KeyBean;
import cc.wulian.smarthomev6.support.core.mqtt.bean.Device22RemoteBean;

/**
 * Created by 上海滩小马哥 on 2017/12/12.
 * 红外转发22 缓存
 */

public class Device22KeyCodeCache {


    private ArrayMap<String, Device22DetailBean> map = new ArrayMap<>();
    private ArrayMap<String, String> catchMap = new ArrayMap<>();

    public Device22KeyCodeCache() {
    }

    public boolean isRemoteCache(String deviceId){
        String result = catchMap.get(deviceId);
        if (TextUtils.equals(result, "true")){
            return true;
        }else {
            return false;
        }
    }

    public void putRemote(@NonNull Device22RemoteBean bean) {
        if (bean.operType == 2) {
            if (bean.data == null){
                clear();
            }else {
                for (Device22RemoteBean.RemoteData remote : bean.data) {
                    map.remove(bean.devID + remote.index);
                }
            }
        } else {
            if (bean.operType == 4){
                catchMap.put(bean.devID, "true");
            }
            for (Device22RemoteBean.RemoteData remote : bean.data) {
                map.put(bean.devID + remote.index, new Device22DetailBean(remote.type, remote.index, remote.name, map.get(bean.devID + remote.index)==null?null:map.get(bean.devID + remote.index).data));
            }
        }
    }

    public void putKeyCode(Device22KeyBean bean) {
        if (bean.data == null || bean.data.size() == 0){
            return;
        }
        for (Device22KeyBean.KeyData keyData : bean.data) {
            Device22DetailBean oldbean = map.get(bean.devID + keyData.index);
            if (oldbean != null){
                //增
                if (bean.operType == 1 || bean.operType == 4){
                    oldbean.hasKeyCodeCatche = true;
                    if(oldbean.data == null){
                        oldbean.data = new ArrayList<>();
                    }
                    for (int i = 0; i < oldbean.data.size(); i++) {
                        if (TextUtils.equals(oldbean.data.get(i).key, keyData.key)){
                            oldbean.data.remove(i);
                            break;
                        }
                    }
                    oldbean.data.add(keyData);
                    map.put(bean.devID + keyData.index, oldbean);
                }
                //删
                else if (bean.operType == 2){
                    if(oldbean.data != null){
                        for (int i = 0; i < oldbean.data.size(); i++) {
                            if (TextUtils.equals(oldbean.data.get(i).key, keyData.key)){
                                oldbean.data.remove(i);
                                map.put(bean.devID + keyData.index, oldbean);
                            }
                        }
                    }
                }
                //改
                else if (bean.operType == 3){
                    if(oldbean.data != null){
                        for (int i = 0; i < oldbean.data.size(); i++) {
                            if (TextUtils.equals(oldbean.data.get(i).key, keyData.key)){
                                oldbean.data.get(i).name = keyData.name;
                                oldbean.data.get(i).index = keyData.index;
                                oldbean.data.get(i).key = keyData.key;
                                oldbean.data.get(i).code = keyData.code;
                                map.put(bean.devID + keyData.index, oldbean);
                            }
                        }
                    }
                }
            }
        }
    }

    public void clear() {
        map.clear();
        catchMap.clear();
    }

    public List<Device22DetailBean> getRemotes() {
        List<Device22DetailBean> remoteList = new ArrayList<>();
        remoteList.addAll(map.values());
        return remoteList;
    }

    public List<Device22DetailBean> getRemotesByDeviceId(String deviceId) {
        List<Device22DetailBean> remoteList = new ArrayList<>();
        for (String key:map.keySet()) {
            if (key.startsWith(deviceId)){
                remoteList.add(map.get(key));
            }
        }
        return remoteList;
    }

    public Device22DetailBean get(String deviceId, String index){
        return map.get(deviceId + index);
    }
}
