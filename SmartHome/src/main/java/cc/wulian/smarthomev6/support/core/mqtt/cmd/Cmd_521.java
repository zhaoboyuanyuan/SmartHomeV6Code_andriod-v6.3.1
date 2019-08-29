package cc.wulian.smarthomev6.support.core.mqtt.cmd;

import android.util.Base64;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;

import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.mqtt.bean.GatewayConfigBean;
import cc.wulian.smarthomev6.support.event.GatewayConfigEvent;

/**
 * Created by Veev on 2017/4/14
 * Tel: 18365264930
 * QQ: 2355738466
 * Function: 获取场景列表
 */

public class Cmd_521 implements CmdHandle {
    @Override
    public void handle(String msgContent) {
        GatewayConfigBean bean = JSON.parseObject(msgContent, GatewayConfigBean.class);
        byte[] decodeMsgContent = Base64.decode(bean.v, Base64.NO_WRAP);
        bean.v = new String(decodeMsgContent);
//        MainApplication.getApplication().getGatewayConfigCache().update(bean);
        EventBus.getDefault().post(new GatewayConfigEvent(bean));
    }
}
