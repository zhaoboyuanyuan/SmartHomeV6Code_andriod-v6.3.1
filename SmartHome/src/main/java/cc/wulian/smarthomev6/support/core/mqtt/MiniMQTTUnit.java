package cc.wulian.smarthomev6.support.core.mqtt;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cc.wulian.smarthomev6.entity.LocalInfo;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.mqtt.parser.IMQTTMessageParser;
import cc.wulian.smarthomev6.support.core.mqtt.parser.MiniGatewayConfigParser;
import cc.wulian.smarthomev6.support.event.MQTTEvent;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * created by huxc  on 2017/8/31.
 * func：
 * email: hxc242313@qq.com
 */

public class MiniMQTTUnit {
    private static final String TAG = "MiniMQTTUnit";

    private Context context;
    private MqttAsyncClient client;
    private MqttConnectOptions options;
    private List<IMQTTMessageParser> messageParserList = Collections.synchronizedList(new ArrayList<IMQTTMessageParser>());
    private Handler handler;
    private static long ReconnectTimeGap = 10000;
    private long saveConnectionLostTime = 0;

    private String mScheme = "ssl://";
    private int tag = MQTTManager.TAG_GATEWAY;

    private String mUserName, mPassword, mServerUrl;
    private AlarmPingSender mAlarmPingSender;

    public MiniMQTTUnit(Context context) {
        this.context = context;
        handler = new Handler(Looper.getMainLooper());
        options = new MqttConnectOptions();
        options.setKeepAliveInterval(MQTTApiConfig.KEEP_ALIVE_INTERVAL_TIME);
        options.setConnectionTimeout(MQTTApiConfig.CONNECT_TIMEOUT);
        options.setMaxInflight(100);
        options.setAutomaticReconnect(false);
        options.setCleanSession(true);
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
    }

    public MiniMQTTUnit setScheme(@NonNull String scheme) {
        this.mScheme = scheme;
        return this;
    }


    /**
     * 连接MQTT服务器，在用户登录成功后调用
     *
     * @param userName  例如："wltest"
     * @param password  例如："wl168cloud"
     * @param serverUrl 例如："testv2.wulian.cc:52185"
     */
    public void connect(@NonNull String userName, @NonNull String password, @NonNull String serverUrl) {
        if (TextUtils.equals(userName, mUserName)
                && TextUtils.equals(password, mPassword)
                && TextUtils.equals(serverUrl, mServerUrl)
                && isConnected()) {
            return;//防止重复连接
        }
        if (TextUtils.isEmpty(serverUrl)) {
            disconnect();
            return;
        }
        if (client != null) {
            disconnect();
        }
        try {
            mUserName = userName;
            mPassword = password;
            mServerUrl = serverUrl;
            LocalInfo localInfo = MainApplication.getApplication().getLocalInfo();
            String clientID = localInfo.appID;
            mAlarmPingSender = new AlarmPingSender(context);
            client = new MqttAsyncClient(mScheme + mServerUrl, clientID, null, mAlarmPingSender);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    WLog.e(TAG, "connectionLost " + tag);
                    throwable.printStackTrace();
                    clearMessageParser();
                    EventBus.getDefault().post(new MQTTEvent(MQTTEvent.STATE_CONNECT_LOST, tag));
                    long reconnectTimeDelay = ReconnectTimeGap - (System.currentTimeMillis() - saveConnectionLostTime);
                    if (reconnectTimeDelay <= 0) {
                        reconnectTimeDelay = 200;
                    }
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            connect(mUserName, mPassword, mServerUrl);
                        }
                    }, reconnectTimeDelay);
                    saveConnectionLostTime = System.currentTimeMillis();
                }

                @Override
                public void messageArrived(final String topic, MqttMessage mqttMessage) throws Exception {
                    //处理收到的推送消息
                    if (mqttMessage.getPayload() != null && mqttMessage.getPayload().length > 0) {
                        final String payload = new String(mqttMessage.getPayload(), "utf-8");
                        WLog.i(TAG, "messageArrived:tag" + tag + ": " + topic + " : " + payload);
                        for (IMQTTMessageParser parser : messageParserList) {
                            parser.parseMessage(tag, topic, payload);
                        }
                    }

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                    //处理消息交付完成
                    WLog.i(TAG, "deliveryComplete: tag" + tag + " " + iMqttDeliveryToken.getMessageId());
                }
            });
            options.setUserName(mUserName);
            options.setPassword(mPassword.toCharArray());
            if (mScheme.startsWith("ssl")) {
                setupSSL(options);
            }
            client.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    WLog.i(TAG, "connect Success " + tag);
                    addMessageParser(new MiniGatewayConfigParser(context, MainApplication.getApplication().getLocalInfo().appID));
                    try {
                        client.subscribe("gw/" + MainApplication.getApplication().getLocalInfo().appID + "/config", 1,null, new IMqttActionListener() {
                             @Override
                             public void onSuccess(IMqttToken asyncActionToken) {
                                 WLog.i(TAG, "subscribe onSuccess: ");
                                 JSONObject object = new JSONObject();
                                 try {
                                     object.put("cmd", "330");
                                     object.put("appID", MainApplication.getApplication().getLocalInfo().appID);
                                     object.put("operType", "getWifiList");
                                     object.put("msgid", "2");
                                    publish("gw/config", object.toString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                             @Override
                             public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                 WLog.i(TAG, "subscribe onFail: ");

                            }
                        });
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                    WLog.e(TAG, "connect Failure : " + tag + throwable.toString());
                    EventBus.getDefault().post(new MQTTEvent(MQTTEvent.STATE_CONNECT_FAIL, tag));
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            connect(mUserName, mPassword, mServerUrl);
                        }
                    }, 5000);
                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        handler.removeCallbacksAndMessages(null);
        if(mAlarmPingSender != null){
            mAlarmPingSender.stop();
            mAlarmPingSender = null;
        }
        if (client != null) {
            client.setCallback(null);
            if (client.isConnected()) {
                try {
                    client.disconnect(null, new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            WLog.i(TAG, "disconnect success");
//                            EventBus.getDefault().post(new MQTTEvent(MQTTEvent.STATE_CONNECT_DISCONNECT, tag));
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            WLog.i(TAG, "disconnect Failure");
                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                } finally {
                    client = null;
                }
            }
        }
    }

    private IMqttActionListener subscribeListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken iMqttToken) {
            if (iMqttToken != null && iMqttToken.getTopics() != null) {
                for (String topic : iMqttToken.getTopics()) {
                    WLog.i(TAG, "subscribe success : " + topic);
                }
            }
        }

        @Override
        public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
            if (iMqttToken != null && iMqttToken.getTopics() != null) {
                for (String topic : iMqttToken.getTopics()) {
                    WLog.e(TAG, "subscribe fail : " + topic + " " + throwable.toString());
                }
            }
        }
    };


    public void unsubscribe(String topic) {
        unsubscribe(topic, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                if (asyncActionToken != null && asyncActionToken.getTopics() != null) {
                    for (String topic : asyncActionToken.getTopics()) {
                        WLog.i(TAG, "unsubscribe success : " + topic);
                    }
                }
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

            }
        });
    }

    public void unsubscribe(String topic, IMqttActionListener listener) {
        try {
            client.unsubscribe(topic, null, listener);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publish(String topic, String data) {
        publish(topic, data, 1, null);
    }

    public void publish(String topic, String data, IMqttActionListener listener) {
        publish(topic, data, 1, listener);
    }

    public void publish(String topic, String data, int qos, IMqttActionListener listener) {
        if (client != null && client.isConnected()) {
            try {
                MqttMessage message = new MqttMessage();
                message.setQos(qos);
                message.setRetained(false);
                message.setPayload(data.getBytes());
                if (listener == null) {
                    listener = new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken iMqttToken) {
                            WLog.i(TAG, "publish success : tag" + tag + " " + iMqttToken.getMessageId());
                        }

                        @Override
                        public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                            WLog.i(TAG, "publish fail : tag" + tag + " " + iMqttToken.getMessageId());
                        }
                    };
                }
                client.publish(topic, message, null, listener);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public void publish(String topic, byte[] data) {
        publish(topic, data, 1);
    }

    public void publish(String topic, byte[] data, int qos) {
        if (client != null && client.isConnected()) {
            try {
                MqttMessage message = new MqttMessage();
                message.setQos(qos);
                message.setRetained(false);
                message.setPayload(data);
                client.publish(topic, message, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken iMqttToken) {
                        WLog.i(TAG, "publish success : tag" + tag + " " + iMqttToken.getMessageId());
                    }

                    @Override
                    public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                        WLog.i(TAG, "publish fail : tag" + tag + " " + iMqttToken.getMessageId());
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public MiniMQTTUnit addMessageParser(IMQTTMessageParser parser) {
        messageParserList.add(parser);//推送处理
        return this;
    }

    public boolean removeMessageParser(IMQTTMessageParser parser) {
        return messageParserList.remove(parser);//取消监听
    }

    public void clearMessageParser() {
        messageParserList.clear();
    }

    public boolean isConnected() {
        if (client != null) {
            return client.isConnected();
        } else {
            return false;
        }
    }

    private void setupSSL(MqttConnectOptions options) {

        // 创建X509信任管理器
        X509TrustManager x509TrustManager = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] xcs, String string) {
            }

            public void checkServerTrusted(X509Certificate[] xcs, String string) {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        SSLContext ctx = null;
        try {
            ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[]{x509TrustManager}, null);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        SSLSocketFactory sslFactory = ctx.getSocketFactory();
        options.setSocketFactory(sslFactory);
    }

}
