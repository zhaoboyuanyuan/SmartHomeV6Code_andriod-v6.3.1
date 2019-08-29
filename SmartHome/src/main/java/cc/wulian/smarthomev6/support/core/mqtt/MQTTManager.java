package cc.wulian.smarthomev6.support.core.mqtt;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;

import com.alibaba.fastjson.JSON;

import cc.wulian.smarthomev6.support.core.cipher.CipherUtil;
import cc.wulian.smarthomev6.support.core.mqtt.bean.RegisterResponseBean;
import cc.wulian.smarthomev6.support.core.mqtt.parser.PushMessageParser;
import cc.wulian.smarthomev6.support.core.mqtt.parser.SafeMessageParser;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.utils.WLog;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import cc.wulian.smarthomev6.entity.LocalInfo;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.mqtt.parser.AlarmMessageParser;
import cc.wulian.smarthomev6.support.core.mqtt.parser.DataMessageParser;
import cc.wulian.smarthomev6.support.core.mqtt.parser.RegisterMessageParser;
import cc.wulian.smarthomev6.support.core.mqtt.parser.NoticeMessageParser;
import cc.wulian.smarthomev6.support.core.mqtt.parser.StateMessageParser;
import cc.wulian.smarthomev6.support.core.mqtt.parser.WillMessageParser;
import cc.wulian.smarthomev6.support.core.socket.GatewayBean;
import cc.wulian.smarthomev6.support.core.socket.GatewaySearchUnit;
import cc.wulian.smarthomev6.support.event.MQTTEvent;
import cc.wulian.smarthomev6.support.event.MQTTRegisterEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.MD5Util;
import cc.wulian.smarthomev6.support.utils.SignatureUtil;

/**
 * Created by zbl on 2017/3/16.
 * MQTT连接管理，处理云连接和网关直连
 */

public class MQTTManager {
    public static final int TAG_CLOUD = 1;
    public static final int TAG_GATEWAY = 2;

    @IntDef({TAG_CLOUD, TAG_GATEWAY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GatewayTag {
    }

    public static final int MODE_CLOUD_ONLY = 1;
    public static final int MODE_GATEWAY_ONLY = 2;
    public static final int MODE_GATEWAY_FIRST = 3;

    @IntDef({MODE_CLOUD_ONLY, MODE_GATEWAY_ONLY, MODE_GATEWAY_FIRST})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PublishMode {
    }

    private Context context;
    private Handler handler;
    private MQTTUnit cloudUnit, gatewayUnit;
    private boolean isCloudConnected, isGatewayConnected;
    public static String appID;

    private RegisterMessageParser registerMessageParser;
    private AlarmMessageParser alarmMessageParser;
    private PushMessageParser pushMessageParser;
    private NoticeMessageParser noticeMessageParser;
    private StateMessageParser stateMessageParser;
    private DataMessageParser dataMessageParser;
    private WillMessageParser willMessageParser;
    private SafeMessageParser safeMessageParser;

    public MQTTManager(Context context) {
        this.context = context;
        EventBus.getDefault().register(this);
        handler = new Handler(Looper.getMainLooper());
        LocalInfo localInfo = MainApplication.getApplication().getLocalInfo();
        appID = localInfo.appID;
        cloudUnit = new MQTTUnit(context, TAG_CLOUD);
        gatewayUnit = new MQTTUnit(context, TAG_GATEWAY);

        alarmMessageParser = new AlarmMessageParser();
        pushMessageParser = new PushMessageParser();
        noticeMessageParser = new NoticeMessageParser(context, appID);
        stateMessageParser = new StateMessageParser(context, appID);
        dataMessageParser = new DataMessageParser(context, appID);
        willMessageParser = new WillMessageParser(context, appID);
        safeMessageParser = new SafeMessageParser();

        gatewayUnit.setScheme("tcp://");
    }

    public void distroy() {
        EventBus.getDefault().unregister(this);
    }

    public void connect() {
        connectGateway();
        connectCloud();
    }

    public void connectGateway() {
        gatewayUnit.setWill("gw/" + appID + "/will", MQTTCmdHelper.createGatewayWill(appID), 1, false);
        gatewayUnit.connect(MQTTApiConfig.gwUserName, MQTTApiConfig.gwUserPassword, MQTTApiConfig.GW_SERVER_URL);
    }

    public void connectCloud() {
        //设置遗嘱
        String will = CipherUtil.createCloudMessage(MQTTCmdHelper.createCloudWill(ApiConstant.getAppToken()));
        cloudUnit.setWill("wl/user/" + ApiConstant.getUserID() + "/will", will, 1, false);
        cloudUnit.connect(MQTTApiConfig.CloudUserName, MQTTApiConfig.CloudUserPassword, MQTTApiConfig.CloudServerURL);
    }

    public void reconnectCloud() {
        cloudUnit.reconnect();
    }

    public void connectGatewayInCloud() {
        final String gwID = Preference.getPreferences().getCurrentGatewayID();
        final String password = Preference.getPreferences().getGatewayPassword(gwID);
        if (!TextUtils.isEmpty(gwID) && !TextUtils.isEmpty(password)) {
            new GatewaySearchUnit().startSearch(new GatewaySearchUnit.SearchGatewayCallback() {
                @Override
                public void result(List<GatewayBean> list) {
                    for (final GatewayBean bean : list) {
                        if (TextUtils.equals(bean.gwID, gwID)) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    //登录局域网内的网关 取密码
                                    MQTTApiConfig.GW_SERVER_URL = bean.host + ":" + MQTTApiConfig.GW_SERVER_PORT;
                                    MQTTApiConfig.gwID = bean.gwID;
                                    MQTTApiConfig.gwPassword = password;
                                    MQTTApiConfig.gwUserName = "a" + System.currentTimeMillis();
                                    MQTTApiConfig.gwUserPassword = "b";//etGatewayPassword.getText().toString().trim();
                                    connectGateway();
                                }
                            });
                            break;
                        }
                    }
                }
            });
        }
    }

    public void disconnect() {
        cloudUnit.disconnect();
        gatewayUnit.disconnect();
        EventBus.getDefault().post(new MQTTEvent(MQTTEvent.STATE_CONNECT_DISCONNECT_ALL, TAG_CLOUD));
    }

    public void disconnectGateway() {
        gatewayUnit.disconnect();
    }

    public void disconnectCloud() {
        cloudUnit.disconnect();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleMqttEvent(MQTTEvent event) {
        if (event.tag == TAG_GATEWAY) {//网关事件
            //连接成功
            if (event.state == MQTTEvent.STATE_CONNECT_SUCCESS) {
                //进行注册
                registerMessageParser = new RegisterMessageParser(appID, new RegisterMessageParser.RegisterMessageParserListener() {
                    @Override
                    public void onRegisterSuccess(final RegisterResponseBean bean) {
                        WLog.i("MQTTManager", bean.toString());
                        MQTTApiConfig.GW_SALT = bean.salt;
                        //生成AES KEY
                        String echoStr = SignatureUtil.getGatewayEchoStr(MQTTApiConfig.GW_SALT, MQTTApiConfig.gwPassword);
                        try {
                            WLog.i("MQTTManager", "echoStr:" + echoStr);
                            MQTTApiConfig.GW_AES_KEY = Base64.decode(echoStr + "==", Base64.NO_WRAP);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                gatewayUnit.removeMessageParser(registerMessageParser);
                                gatewayUnit.unsubscribe("gw/wuliancc");
                                doRegisterSuccess();
                                EventBus.getDefault().post(new MQTTRegisterEvent(MQTTRegisterEvent.STATE_REGISTER_SUCCESS, "0"));
                            }
                        });
                    }

                    @Override
                    public void onRegisterFail(RegisterResponseBean bean) {
                        EventBus.getDefault().post(new MQTTRegisterEvent(MQTTRegisterEvent.STATE_REGISTER_FAIL, bean.data));
                    }
                });
                gatewayUnit.subscribe("gw/wuliancc", 1, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        gatewayUnit.addMessageParser(registerMessageParser);
                        final String registerMessage = MQTTCmdHelper.createRegisterMessage(context, MQTTApiConfig.gwID, MD5Util.encrypt(MQTTApiConfig.gwPassword));
                        gatewayUnit.publish("gw/wuliancc", registerMessage);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                    }
                });
            }
        } else if (event.tag == TAG_CLOUD) {
            if (event.state == MQTTEvent.STATE_CONNECT_SUCCESS) {//云MQTT登录成功后执行
                String currentGatewayId = Preference.getPreferences().getCurrentGatewayID();
                if (!TextUtils.isEmpty(currentGatewayId)) {
                    String state = Preference.getPreferences().getCurrentGatewayState();
                    if (!"3".equals(Preference.getPreferences().getGatewayRelationFlag())) {
                        MainApplication.getApplication().getDeviceCache().loadDatabaseCache(currentGatewayId, state);
                    }
                    EventBus.getDefault().post(new DeviceReportEvent(null));
                }//加载设备列表缓存
                cloudUnit.clearMessageParser();
                cloudUnit
                        .addTopic("wl/user/" + ApiConstant.getUserID() + "/alarm", 1)
                        .addTopic("wl/user/" + ApiConstant.getUserID() + "/push/alarm", 1)
                        .addTopic("wl/user/" + ApiConstant.getUserID() + "/state", 1)
                        .addTopic("wl/user/" + ApiConstant.getUserID() + "/" + appID + "/notice", 1)
//                        .addTopic("wl/user/public", 1) 这个主题没有用，暂时去掉
                        .addTopic("wl/user/" + ApiConstant.getUserID() + "/safe", 1)
                        .addMessageParser(pushMessageParser)
                        .addMessageParser(alarmMessageParser)
                        .addMessageParser(noticeMessageParser)
                        .addMessageParser(stateMessageParser)
                        .addMessageParser(dataMessageParser)
                        .addMessageParser(safeMessageParser)
                        .subscribeAll();
                cloudUnit.subscribe("wl/user/" + ApiConstant.getUserID() + "/data", 1, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                String currentGatewayId = Preference.getPreferences().getCurrentGatewayID();
                                if (!TextUtils.isEmpty(currentGatewayId)) {
                                    sendGetDevicesInfoCmd(currentGatewayId, MODE_CLOUD_ONLY);

                                    publishEncryptedMessage(
                                            MQTTCmdHelper.createGatewayInfo(currentGatewayId, 0, appID, null),
                                            MQTTManager.MODE_CLOUD_ONLY);
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                    }
                });
            }
        }
    }

    private void doRegisterSuccess() {//注册动作成功后执行
        gatewayUnit.clearMessageParser();
        gatewayUnit
                .addTopic("gw/" + appID + "/alarm", 1)
                .addTopic("gw/" + appID + "/state", 1)
                .addTopic("gw/will", 1)
                .addMessageParser(alarmMessageParser)
                .addMessageParser(stateMessageParser)
                .addMessageParser(dataMessageParser)
                .addMessageParser(willMessageParser)
                .subscribeAll();
        gatewayUnit.subscribe("gw/" + appID + "/data", 1, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        sendGetDevicesInfoCmd(MQTTApiConfig.gwID, MODE_GATEWAY_FIRST);

                        publishEncryptedMessage(
                                MQTTCmdHelper.createGatewayInfo(MQTTApiConfig.gwID, 0, appID, null),
                                MQTTManager.MODE_GATEWAY_FIRST);
                    }
                });
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

            }
        });
    }

    /**
     * 发布消息
     *
     * @param data
     * @param mode like MQTTManager.MODE_CLOUD_ONLY
     * @return 是否发送成功
     */
    public boolean publish(@NonNull String data, @PublishMode int mode) {
        WLog.i("MQTTManager", "publish " + data);
        getConnectState();
        int realMode = getRealMode(mode);// 0 网关，1 云
        String topic = getPublishTopic(realMode);
        if (realMode == 0) {
            gatewayUnit.publish(topic, data);
        } else if (realMode == 1) {
            cloudUnit.publish(topic, data);
        } else {
            WLog.e("MQTTManager", "没有MQTT连接,发送取消");
            return false;
        }
        return true;
    }

    /**
     * 发送加密的消息体
     *
     * @param data
     * @param mode like MQTTManager.MODE_CLOUD_ONLY
     * @return 是否发送成功
     */
    public boolean publishEncryptedMessage(@NonNull String data, @PublishMode int mode) {
        return publishEncryptedMessage(data, mode, null);
    }

    /**
     * 发送加密的消息体
     *
     * @param data
     * @param mode like MQTTManager.MODE_CLOUD_ONLY
     * @return 是否发送成功
     */
    public boolean publishEncryptedMessage(@NonNull String data, @PublishMode int mode, IMqttActionListener listener) {
        JSONObject dataObject = null;
        try {
            dataObject = new JSONObject(data);
            dataObject.put("userID", ApiConstant.getUserID());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WLog.i("MQTTManager", "publishEncrypted " + dataObject.toString());

        getConnectState();
        int realMode = getRealMode(mode);// 0 网关，1 云
        String topic = getPublishTopic(realMode);
        if (realMode == 0) {
            data = CipherUtil.createGatewayMessage(dataObject.toString());
            gatewayUnit.publish(topic, data, listener);
        } else if (realMode == 1) {
            data = CipherUtil.createCloudMessage(dataObject.toString());
            cloudUnit.publish(topic, data, listener);
        } else {
            WLog.e("MQTTManager", "没有MQTT连接,发送取消");
            return false;
        }
        return true;
    }

    private int getRealMode(int mode) {
        int realMode = -1;// 0 网关，1 云
        if (mode == MODE_GATEWAY_ONLY) {
            if (isGatewayConnected) {
                realMode = 0;
            }
        } else if (mode == MODE_CLOUD_ONLY) {
//            if (isCloudConnected) {
            realMode = 1;
//            }
        } else if (mode == MODE_GATEWAY_FIRST) {
            if (isGatewayConnected) {
                realMode = 0;
            } else {//if (isCloudConnected) {
                realMode = 1;
            }
        }
        return realMode;
    }

    private String getPublishTopic(int realMode) {
        String topic = null;
        if (realMode == 0) {
            topic = "gw/" + appID + "/req";
        } else {
            topic = "wl/user/" + ApiConstant.getUserID() + "/req";
        }
        return topic;
    }

    private void getConnectState() {
        isCloudConnected = cloudUnit.isConnected();
        isGatewayConnected = gatewayUnit.isConnected();
    }

    public void sendGetDevicesInfoCmd(String gatewayId, int mode) {
        publishEncryptedMessage(
                MQTTCmdHelper.createGetAllScenes(gatewayId),
                mode);

        publishEncryptedMessage(
                MQTTCmdHelper.createGetAllDevices(gatewayId, appID),
                mode);

        publishEncryptedMessage(
                MQTTCmdHelper.createGetAllRooms(gatewayId),
                mode);

    }
}
