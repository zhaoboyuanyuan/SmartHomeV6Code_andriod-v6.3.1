package cc.wulian.smarthomev6.support.core.mqtt.cmd;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;

import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.bean.RoomBean;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.RoomInfoEvent;

/**
 * Created by Veev on 2017/4/14
 * Tel: 18365264930
 * QQ: 2355738466
 * Function: 获取场景列表
 */

public class Cmd_505 implements CmdHandle {
    @Override
    public void handle(String msgContent) {
        RoomBean roomBean = JSON.parseObject(msgContent, RoomBean.class);
        if (roomBean.mode == 3) {
            MainApplication.getApplication().getRoomCache().remove(roomBean);
            // mode = 3   为删除 更新本地设备列表
            String roomID = roomBean.roomID;
            for (Device device : MainApplication.getApplication().getDeviceCache().getDevices()) {
                if (TextUtils.equals(roomID, device.roomID)) {
                    device.roomID = "";
                }
            }
        } else {
            MainApplication.getApplication().getRoomCache().add(roomBean);
        }
        EventBus.getDefault().post(new RoomInfoEvent(roomBean));
        EventBus.getDefault().post(new DeviceReportEvent(null));
    }
}
