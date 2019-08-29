package cc.wulian.smarthomev6.support.core.mqtt.parser;

import android.content.Context;
import android.text.TextUtils;

import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.bean.ResponseBean;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;

import java.util.Collection;

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
 * 网关遗嘱消息解析处理
 */

public class WillMessageParser implements IMQTTMessageParser {

    private Context context;
    private String appId;

    public WillMessageParser(Context context, String appId) {
        this.context = context;
        this.appId = appId;
    }

    @Override
    public void parseMessage(int tag, String topic, String payload) {
        if (!topic.endsWith("/will")) {
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
                WLog.i("MQTTUnit:Will:" + (tag == MQTTManager.TAG_GATEWAY ? "Gw" : "Cloud") + ":", msgContent);
                onGatewayStateChanged(msgContent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onGatewayStateChanged(String msgContent) {
        GatewayStateBean bean = JSON.parseObject(msgContent, GatewayStateBean.class);
        if ("01".equals(bean.cmd)) {
            Preference preference = Preference.getPreferences();
            final String currentGatewayId = preference.getCurrentGatewayID();
            if (TextUtils.equals(currentGatewayId, bean.gwID)) {
                preference.saveCurrentGatewayState("1");
            }

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

            String text = String.format(context.getString(R.string.Toast_Gateway_Online), bean.gwID);
            ToastUtil.show(text);
        } else if ("15".equals(bean.cmd)) {
            DeviceCache deviceCache = MainApplication.getApplication().getDeviceCache();
//            deviceCache.romoveByGatewayID(bean.gwID);
            /*//网关下线的时候该网关下设备全部离线
            Collection<Device> devices = deviceCache.getDevices();
            for (Device device : devices) {
                if (TextUtils.equals(device.gwID, bean.gwID)) {
                    device.mode = 2;
                }
            }*/
            // 网关下线, 设备全部下线
            deviceCache.clearZigBeeDevices();
            EventBus.getDefault().post(new DeviceReportEvent(null));

            Preference preference = Preference.getPreferences();
            final String currentGatewayId = preference.getCurrentGatewayID();
            if (TextUtils.equals(currentGatewayId, bean.gwID)) {
                preference.saveCurrentGatewayState("0");
            }
            EventBus.getDefault().post(new GatewayStateChangedEvent(bean));
            String text = String.format(context.getString(R.string.Toast_Gateway_Offline), bean.gwID);
            ToastUtil.show(text);
        }
    }
}
