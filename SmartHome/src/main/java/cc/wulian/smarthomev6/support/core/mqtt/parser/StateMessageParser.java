package cc.wulian.smarthomev6.support.core.mqtt.parser;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import cc.wulian.smarthomev6.main.home.widget.HomeWidgetManager;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.bean.GetDeviceListBean;
import cc.wulian.smarthomev6.support.core.mqtt.bean.ResponseBean;
import cc.wulian.smarthomev6.support.event.HomeDeviceWidgetChangeEvent;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceCache;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTApiConfig;
import cc.wulian.smarthomev6.support.core.cipher.CipherUtil;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.GatewayStateBean;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GatewayStateChangedEvent;
import cc.wulian.smarthomev6.support.tools.Preference;

/**
 * Created by zbl on 2017/3/3.
 * 设备状态消息解析处理
 */

public class StateMessageParser implements IMQTTMessageParser {

    private Context context;
    private String appId;

    public StateMessageParser(Context context, String appId) {
        this.context = context;
        this.appId = appId;
    }

    @Override
    public void parseMessage(int tag, String topic, String payload) {
        if (!topic.endsWith("/state")) {
            return;
        }
        try {
            ResponseBean bean = JSON.parseObject(payload, ResponseBean.class);
            if (bean.msgContent != null) {
                String msgContent = null;
                if (tag == MQTTManager.TAG_GATEWAY) {
                    msgContent = CipherUtil.decode(bean.msgContent, MQTTApiConfig.GW_AES_KEY);
                } else if (tag == MQTTManager.TAG_CLOUD) {
                    msgContent = CipherUtil.decode(bean.msgContent, ApiConstant.getAESKey());
                }
                WLog.i("MQTTUnit:State:" + (tag == MQTTManager.TAG_GATEWAY ? "Gw" : "Cloud") + ":", msgContent);
                JSONObject jsonObject = JSON.parseObject(msgContent);
                String cmd = jsonObject.getString("cmd");

                if ("01".equals(cmd)) {//网关上线
                    onGatewayStateChanged(msgContent);
                } else if ("15".equals(cmd)) {//网关下线
                    onGatewayStateChanged(msgContent);
                } else if ("500".equals(cmd)) {
                    String currentGatewayId = Preference.getPreferences().getCurrentGatewayID();
                    String gwID = jsonObject.getString("gwID");
                    String type = jsonObject.getString("type");
                    if (gwID != null) {
                        if (TextUtils.equals(gwID, currentGatewayId) || DeviceInfoDictionary.supportParseCmdData(type)) {
                            onDeviceReport(msgContent);
                        }
                    } else if ("CMICA1".equals(type) ||
                            "CMICA2".equals(type) || "CMICA3".equals(type)
                            || "CMICA6".equals(type)
                            || "CMICA5".equals(type) || "IF02".equals(type)
                            || "XW01".equals(type)) {
                        //智能猫眼和爱看摄像机上下线
                        onWifiDeviceStateChanged(jsonObject);
                    }
                } else if ("510".equals(cmd)) {//获取设备列表返回结果
                    onDeviceListGetSuccess(msgContent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onGatewayStateChanged(String msgContent) {
        GatewayStateBean bean = JSON.parseObject(msgContent, GatewayStateBean.class);
        Preference preference = Preference.getPreferences();
        final String currentGatewayId = preference.getCurrentGatewayID();
        //对于v5升级v6网关上线后直接传给h5不走其他逻辑
        if (!TextUtils.isEmpty(bean.userID)) {
            EventBus.getDefault().post(new GatewayStateChangedEvent(bean));
            return;
        }
        // 非当前网关
        // 不做处理
        if (!TextUtils.equals(currentGatewayId, bean.gwID)) {
            return;
        }

        if ("01".equals(bean.cmd)) {

            preference.saveCurrentGatewayState("1");

            // 网关上线， 重新请求数据
            MainApplication.getApplication()
                    .getMqttManager()
                    .sendGetDevicesInfoCmd(currentGatewayId, MQTTManager.MODE_GATEWAY_FIRST);

            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(
                            MQTTCmdHelper.createGatewayInfo(currentGatewayId, 0, MainApplication.getApplication().getLocalInfo().appID, null),
                            MQTTManager.MODE_GATEWAY_FIRST);

            EventBus.getDefault().post(new GatewayStateChangedEvent(bean));
//            String text = String.format(context.getString(R.string.Toast_Gateway_Online), bean.gwID);
//            Looper.prepare();
//            ToastUtil.show(text);
//            Looper.loop();
        } else if ("15".equals(bean.cmd)) {
            DeviceCache deviceCache = MainApplication.getApplication().getDeviceCache();

            preference.saveCurrentGatewayState("0");
            // 下线所有设备
//            deviceCache.clearZigBeeDevices();
            //网关下线的时候该网关下设备全部离线
            Collection<Device> devices = deviceCache.getZigBeeDevices();
            for (Device device : devices) {
                if (TextUtils.equals(device.gwID, bean.gwID)) {
                    device.mode = 2;
                    EventBus.getDefault().post(new DeviceReportEvent(device));
                }
            }
            // 下线所有widget
//            HomeWidgetManager.offLineWidgetInGw(bean.gwID);

            EventBus.getDefault().post(new GatewayStateChangedEvent(bean));
//            String text = String.format(context.getString(R.string.Toast_Gateway_Offline), bean.gwID);
//            Looper.prepare();
//            ToastUtil.show(text);
//            Looper.loop();
        }
    }

    private void onDeviceReport(String msgContent) {
        Device device = JSON.parseObject(msgContent, Device.class);
        device.data = msgContent;
        device.name = DeviceInfoDictionary.getNameByDevice(device);
        DeviceCache deviceCache = MainApplication.getApplication().getDeviceCache();
        if (device.mode == 3) {//删除设备
            deviceCache.remove(device);
        } else if (device.mode == 1) {
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

    private void onWifiDeviceStateChanged(JSONObject jsonObject) {
        String devID = jsonObject.getString("devID");
        Device device = MainApplication.getApplication().getDeviceCache().get(devID);
        if (device != null) {
            device.mode = jsonObject.getInteger("mode");
            EventBus.getDefault().post(new DeviceReportEvent(device));
        }
    }

    private void onDeviceListGetSuccess(String msgContent) {
        GetDeviceListBean getDeviceListBean = JSON.parseObject(msgContent, GetDeviceListBean.class);
        String currentGatewayId = Preference.getPreferences().getCurrentGatewayID();
        if (TextUtils.equals(currentGatewayId, getDeviceListBean.gwID)) {
            //删除不在设备列表中的设备
            if (getDeviceListBean.mode == 1 && getDeviceListBean.data != null) {
                DeviceCache deviceCache = MainApplication.getApplication().getDeviceCache();
                HashMap<String, Device> deviceHashMap = new HashMap<>();
                List<Device> zigBeeDevices = deviceCache.getZigBeeDevices();
                for (Device device : zigBeeDevices) {
                    deviceHashMap.put(device.devID, device);
                }
                for (String devID : getDeviceListBean.data) {
                    deviceHashMap.remove(devID);
                }
                for (String devID : deviceHashMap.keySet()) {
                    deviceCache.remove(devID);
                }
                EventBus.getDefault().post(new DeviceReportEvent(null));
                EventBus.getDefault().post(HomeDeviceWidgetChangeEvent.ALL());
            }
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    DeviceCache deviceCache = MainApplication.getApplication().getDeviceCache();
                    deviceCache.saveDatabaseCache();
                }
            }, 5000);
        }
    }
}
