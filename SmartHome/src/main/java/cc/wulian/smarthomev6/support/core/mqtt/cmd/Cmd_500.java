package cc.wulian.smarthomev6.support.core.mqtt.cmd;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;

import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.home.widget.HomeWidgetManager;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceCache;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.Preference;

/**
 * Created by Veev on 2017/4/14
 * Tel: 18365264930
 * QQ: 2355738466
 * Function: 获取场景列表
 */

public class Cmd_500 implements CmdHandle {
    @Override
    public void handle(String msgContent) {
        Device device = JSON.parseObject(msgContent, Device.class);
        device.data = msgContent;
        device.name = DeviceInfoDictionary.getNameByDevice(device);
        DeviceCache deviceCache = MainApplication.getApplication().getDeviceCache();
        if (device.mode == 3) {//删除设备
            deviceCache.remove(device);
        } else if (device.mode == 1){
            deviceCache.add(device);
            // 门锁上线时, 获取一下设备配置
            if ("70".equals(device.type)) {
                MainApplication.getApplication().getMqttManager().publishEncryptedMessage(
                        MQTTCmdHelper.createGatewayConfig(
                                Preference.getPreferences().getCurrentGatewayID(),
                                3,
                                MainApplication.getApplication().getLocalInfo().appID,
                                device.devID,
                                null,
                                null,
                                null
                        ), MQTTManager.MODE_GATEWAY_FIRST
                );
            }
        } else {
            deviceCache.add(device);
        }

        // 处理首页widget
        HomeWidgetManager.handleDeviceReport(device);

        EventBus.getDefault().post(new DeviceReportEvent(device));
    }
}
