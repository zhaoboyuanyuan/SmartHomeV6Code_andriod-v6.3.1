package cc.wulian.smarthomev6.support.core.mqtt.cmd;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;

import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.mqtt.bean.GatewayInfoBean;
import cc.wulian.smarthomev6.support.event.GatewayInfoEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.StringUtil;

/**
 * Created by Veev on 2017/4/14
 * Tel: 18365264930
 * QQ: 2355738466
 * Function: 获取场景列表
 */

public class Cmd_512 implements CmdHandle {
    @Override
    public void handle(String msgContent) {
        GatewayInfoBean gatewayInfoBean = JSON.parseObject(msgContent, GatewayInfoBean.class);
        GatewayInfoBean currentGateway = JSON.parseObject(Preference.getPreferences().getCurrentGatewayInfo(), GatewayInfoBean.class);

        JSONObject jsonObject = JSON.parseObject(msgContent);
        if (!StringUtil.isNullOrEmpty(currentGateway.gwType)){
            gatewayInfoBean.gwType = currentGateway.gwType;
            jsonObject.put("gwType", currentGateway.gwType);
        }
        gatewayInfoBean.gwName = DeviceInfoDictionary.getNameByTypeAndName(gatewayInfoBean.gwType, gatewayInfoBean.gwName);
        jsonObject.put("gwName", gatewayInfoBean.gwName);
        if (TextUtils.isEmpty(gatewayInfoBean.gwName) && !TextUtils.isEmpty(currentGateway.gwName)){
            gatewayInfoBean.gwName = currentGateway.gwName;
            jsonObject.put("gwName", currentGateway.gwName);
        }
        Preference.getPreferences().saveCurrentGatewayInfo(jsonObject.toString());
        EventBus.getDefault().post(new GatewayInfoEvent(gatewayInfoBean));
    }
}
