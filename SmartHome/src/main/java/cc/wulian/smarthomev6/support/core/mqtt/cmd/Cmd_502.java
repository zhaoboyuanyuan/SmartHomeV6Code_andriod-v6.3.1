package cc.wulian.smarthomev6.support.core.mqtt.cmd;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;

import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.home.widget.HomeWidgetManager;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceCache;
import cc.wulian.smarthomev6.support.core.mqtt.bean.DeviceInfoBean;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;

/**
 * Created by Veev on 2017/4/14
 * Tel: 18365264930
 * QQ: 2355738466
 * Function: 设备信息改变
 */

public class Cmd_502 implements CmdHandle {
    @Override
    public void handle(String msgContent) {
        DeviceInfoBean deviceInfoBean = JSON.parseObject(msgContent, DeviceInfoBean.class);
        DeviceCache deviceCache = MainApplication.getApplication().getDeviceCache();
        if (deviceInfoBean.mode == 3) {//删除设备
            deviceCache.remove(deviceInfoBean.devID);
        } else if (deviceInfoBean.mode == 0) {//门磁等设备切换状态
            Device device = deviceCache.get(deviceInfoBean.devID);
            if (device != null && device.data != null) {
                try {
                    JSONObject jsonData = JSON.parseObject(device.data);
                    JSONArray endpoints = jsonData.getJSONArray("endpoints");
                    for (int i = 0; i < endpoints.size(); i++) {
                        JSONObject endpoint = endpoints.getJSONObject(i);
                        int endpointNumber = endpoint.getInteger("endpointNumber");
                        if (endpointNumber == deviceInfoBean.endpointNumber) {
                            endpoint.put("endpointStatus", deviceInfoBean.endpointStatus);
                            endpoints.remove(i);
                            endpoints.add(endpoint);
                            break;
                        }
                    }
                    jsonData.put("endpoints", endpoints);
                    device.data = jsonData.toJSONString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            Device device = deviceCache.get(deviceInfoBean.devID);
            if (device != null) {
                if (deviceInfoBean.name != null) {
                    device.name = deviceInfoBean.name;
                }
                if (deviceInfoBean.roomID != null) {
                    device.roomID = deviceInfoBean.roomID;
                }
            }
        }

        // 处理首页widget
        HomeWidgetManager.handleDeviceInfoChanged(deviceInfoBean.mode, deviceInfoBean.devID, deviceInfoBean.type);

        EventBus.getDefault().post(new DeviceInfoChangedEvent(deviceInfoBean));
    }
}
