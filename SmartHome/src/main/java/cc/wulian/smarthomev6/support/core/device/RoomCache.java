package cc.wulian.smarthomev6.support.core.device;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.mqtt.bean.RoomBean;
import cc.wulian.smarthomev6.support.core.mqtt.bean.SceneBean;

/**
 * Created by zbl on 2017/4/5.
 * 区域信息缓存
 */

public class RoomCache {
    private LinkedHashMap<String, RoomBean> roomHashMap = new LinkedHashMap<>();

    public RoomCache() {
    }

    public void add(@NonNull RoomBean roomBean) {
        roomHashMap.put(roomBean.roomID, roomBean);
    }

    public void addAll(@NonNull List<RoomBean> list) {
        if (list != null) {
            Collections.sort(list, new Comparator<RoomBean>() {
                @Override
                public int compare(RoomBean o1, RoomBean o2) {
                    return o1.roomID.compareTo(o2.roomID);
                }
            });
            for (RoomBean bean : list) {
                roomHashMap.put(bean.roomID, bean);
            }
        }
    }

    public void remove(@NonNull RoomBean roomBean) {
        roomHashMap.remove(roomBean.roomID);
    }

    public RoomBean get(@NonNull String roomID) {
        return roomHashMap.get(roomID);
    }

    public String getRoomName(@NonNull String roomID) {

        String areaName = null;
        MainApplication mainApplication = MainApplication.getApplication();
        if (TextUtils.isEmpty(roomID)) {
            areaName = mainApplication.getString(R.string.Device_NoneArea);
        } else {
            RoomBean roomBean = get(roomID);
            if (roomBean != null) {
                areaName = roomBean.name;
            } else {
                areaName = "";
            }
        }
        return areaName;
    }

    public String getRoomName(Device device) {
        MainApplication mainApplication = MainApplication.getApplication();
        if (device == null) {
            return mainApplication.getString(R.string.Device_NoneArea);
        }

        if (TextUtils.isEmpty(device.roomID)) {
            return mainApplication.getString(R.string.Device_NoneArea);
        }

        RoomBean roomBean = get(device.roomID);
        if (roomBean == null) {
            return mainApplication.getString(R.string.Device_NoneArea);
        }

        if (TextUtils.isEmpty(roomBean.name)) {
            return mainApplication.getString(R.string.Device_NoneArea);
        }

        return roomBean.name;
    }

    public boolean hasRoom(String roomID) {
        return roomHashMap.containsKey(roomID);
    }

    public Collection<RoomBean> getDevices() {
        return roomHashMap.values();
    }

    public void clear() {
        roomHashMap.clear();
    }
}
