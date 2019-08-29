package cc.wulian.smarthomev6.support.core.mqtt;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cc.wulian.smarthomev6.entity.LocalInfo;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.cipher.CipherUtil;
import cc.wulian.smarthomev6.support.core.mqtt.parser.MiniGatewayConfigParser;
import cc.wulian.smarthomev6.support.event.MQTTEvent;
import cc.wulian.smarthomev6.support.event.MiniGatewayConnectedEvent;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * created by huxc  on 2017/8/30.
 * func：
 * email: hxc242313@qq.com
 */

public class MiniMQTTManger {
    private static final String TAG = "MiniMQTTManger";

    private Context context;
    private Handler handler;
    private MiniMQTTUnit gatewayUnit;
    public static String appID;
    private MiniGatewayConfigParser miniGatewayConfigParser;
    private static MiniMQTTManger mMiniMQTTManger;

    public static MiniMQTTManger getInstance(Context context){
        if(mMiniMQTTManger == null){
            mMiniMQTTManger = new MiniMQTTManger(context);
        }
        return mMiniMQTTManger;
    }

    private MiniMQTTManger(Context context) {
        this.context = context;
        EventBus.getDefault().register(this);
        handler = new Handler(Looper.getMainLooper());
        LocalInfo localInfo = MainApplication.getApplication().getLocalInfo();
        appID = localInfo.appID;
        gatewayUnit = new MiniMQTTUnit(context);
        miniGatewayConfigParser = new MiniGatewayConfigParser(context, appID);
        gatewayUnit.setScheme("tcp://");
    }

    public void publish(String topic, String data) {
        gatewayUnit.publish(topic, data);
    }

    public void connectGateway() {
        gatewayUnit.connect(MQTTApiConfig.gwUserName, MQTTApiConfig.gwUserPassword, MQTTApiConfig.GW_SERVER_URL);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleMqttEvent(MQTTEvent event) {

    }

    public void disconnectGateway() {
        gatewayUnit.disconnect();
    }

}
