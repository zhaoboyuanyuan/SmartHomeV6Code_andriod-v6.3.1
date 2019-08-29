package cc.wulian.smarthomev6.support.core.mqtt.cmd;

import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;

import java.util.Collection;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceCache;
import cc.wulian.smarthomev6.support.core.mqtt.bean.GatewayStateBean;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GatewayStateChangedEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by Veev on 2017/4/14
 * Tel: 18365264930
 * QQ: 2355738466
 * Function: 网关下线
 */

public class Cmd_15 implements CmdHandle {

    @Override
    public void handle(String msgContent) {
        GatewayStateBean bean = JSON.parseObject(msgContent, GatewayStateBean.class);
        if ("01".equals(bean.cmd)) {
            Preference preference = Preference.getPreferences();
            final String currentGatewayId = preference.getCurrentGatewayID();
            if (TextUtils.equals(currentGatewayId, bean.gwID)) {
                preference.saveCurrentGatewayState("1");
            }
            EventBus.getDefault().post(new GatewayStateChangedEvent(bean));

            String text = String.format(MainApplication.getApplication().getString(R.string.Toast_Gateway_Online), bean.gwID);
            ToastUtil.show(text);
        } else if ("15".equals(bean.cmd)) {
            DeviceCache deviceCache = MainApplication.getApplication().getDeviceCache();
//            deviceCache.romoveByGatewayID(bean.gwID);
            //网关下线的时候该网关下设备全部离线
            Collection<Device> devices = deviceCache.getDevices();
            for (Device device : devices) {
                if (TextUtils.equals(device.gwID, bean.gwID)) {
                    device.mode = 2;
                }
            }
            EventBus.getDefault().post(new DeviceReportEvent(null));

            Preference preference = Preference.getPreferences();
            final String currentGatewayId = preference.getCurrentGatewayID();
            if (TextUtils.equals(currentGatewayId, bean.gwID)) {
                preference.saveCurrentGatewayState("0");
            }
            EventBus.getDefault().post(new GatewayStateChangedEvent(bean));
            String text = String.format(MainApplication.getApplication().getString(R.string.Toast_Gateway_Offline), bean.gwID);
            ToastUtil.show(text);
        }
    }
}
