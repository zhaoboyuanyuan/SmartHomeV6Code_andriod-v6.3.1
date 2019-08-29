package cc.wulian.smarthomev6.main.device;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.LocalInfo;
import cc.wulian.smarthomev6.entity.SceneInfo;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.device_82.Controller82MoreActivity;
import cc.wulian.smarthomev6.main.device.device_Bn.DeviceMoreActivityForBn;
import cc.wulian.smarthomev6.main.device.device_bc.DeviceMoreActivityForBc;
import cc.wulian.smarthomev6.main.device.device_xw01.setting.WishBmgSettingActivity;
import cc.wulian.smarthomev6.main.device.hisense.setting.HisenseDeviceSettingActivity;
import cc.wulian.smarthomev6.main.h5.H5BridgeActivity;
import cc.wulian.smarthomev6.main.h5.WVJBWebViewClient;
import cc.wulian.smarthomev6.main.home.scene.HouseKeeperActivity;
import cc.wulian.smarthomev6.main.home.scene.SceneManager;
import cc.wulian.smarthomev6.main.message.alarm.MessageAlarmActivity;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.MessageBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.CMD517Event;
import cc.wulian.smarthomev6.support.event.DeviceControlEvent;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GetSceneBindingEvent;
import cc.wulian.smarthomev6.support.event.LanguageChangeEvent;
import cc.wulian.smarthomev6.support.event.SceneInfoEvent;
import cc.wulian.smarthomev6.support.event.SetSceneBindingEvent;
import cc.wulian.smarthomev6.support.event.TranslatorFuncEvent;
import cc.wulian.smarthomev6.support.tools.MessageTool;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.TimeLock;
import cc.wulian.smarthomev6.support.utils.AESUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.VibratorUtil;
import cc.wulian.smarthomev6.support.utils.WLog;
import cc.wulian.smarthomev6.support.utils.biometric.BiometricListener;

/**
 * 设备详情
 */
public class DeviceDetailActivity extends H5BridgeActivity {

    public static final String KEY_DEVICE_ID = "key_device_id";
    public static final int KEY_DELETE_DEVICE = 0;


    public static void start(Context context, String deviceID) {
        Intent intent = new Intent(context, DeviceDetailActivity.class);
        intent.putExtra(KEY_DEVICE_ID, deviceID);
        context.startActivity(intent);
    }

    protected Context context;
    protected String deviceId;
    protected Device device;
    private DeviceApiUnit mDeviceApiUnit;
    private DataApiUnit mDataApiUnit;
    private TimeLock timeLock = new TimeLock();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        mDeviceApiUnit = new DeviceApiUnit(this);
        mDataApiUnit = new DataApiUnit(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void registerHandler() {
        mWebViewClient.registerHandler("getCurrentAppID", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "getCurrentAppID: 请求APP ID - " + MainApplication.getApplication().getLocalInfo().appID);
                callback.callback(MainApplication.getApplication().getLocalInfo().appID);
            }
        });
        mWebViewClient.registerHandler("getDeviceInfo", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "请求 : 设备信息 " + data);
                if (device == null || TextUtils.isEmpty(device.data)) {
                    ToastUtil.show(R.string.Device_data_error);
                    return;
                }
                if (data != null && !TextUtils.isEmpty(data.toString()) && !("null".equalsIgnoreCase(data.toString()))) {
                    String devId = data.toString();
                    if (!TextUtils.isEmpty(devId)) {
                        Device tmpDevice = mainApplication.getDeviceCache().get(devId);
                        if (tmpDevice == null || TextUtils.isEmpty(tmpDevice.data)) {
                            return;
                        }
                        try {
                            String deviceJsonString = JSON.toJSONString(tmpDevice);
                            JSONObject jsonObject = new JSONObject(deviceJsonString);
                            jsonObject.put("cmd", "500");
                            callback.callback(jsonObject);
                            WLog.i(TAG, "设备信息：" + deviceJsonString);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
                        String deviceJsonString = JSON.toJSONString(device);
                        JSONObject jsonObject = new JSONObject(deviceJsonString);
                        jsonObject.put("cmd", "500");
                        callback.callback(jsonObject);
                        WLog.i(TAG, "设备信息：" + deviceJsonString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mWebViewClient.registerHandler("controlDevice", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "请求: 控制设备 " + data);
                if (data != null) {
                    ((MainApplication) getApplication())
                            .getMqttManager()
                            .publishEncryptedMessage(data.toString(), MQTTManager.MODE_GATEWAY_FIRST);
                }
                callback.callback("YES");
            }
        });
        mWebViewClient.registerHandler("isBiometricSupport", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "请求: 控制设备 " + data);
                if (data != null) {
                    String devId = String.valueOf(data);
//                    String pwd = Preference.getPreferences().getTouchIDunLock(devId);
                    String encryptPwd = Preference.getPreferences().getTouchIDunLock(devId);
                    LocalInfo localInfo = MainApplication.getApplication().getLocalInfo();
                    String pwd = AESUtil.decrypt(encryptPwd, localInfo.appID, true);
                    if (TextUtils.equals("", encryptPwd)) {
                        callback.callback("false");
                    } else {
                        BiometricListener biometricListener = new BiometricListener(DeviceDetailActivity.this, device, pwd);
                        biometricListener.handle();
                        callback.callback("true");
                    }
                } else {
                    callback.callback("false");
                }
            }
        });
        mWebViewClient.registerHandler("more", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "请求: 更多 " + data);
                if (device.isShared) {
                    if (!DeviceInfoDictionary.isWiFiDevice(device.type)) {
                        ToastUtil.show(R.string.Share_More_Tip);
                        return;
                    }
                }
                if (timeLock.isLock()) {
                    return;
                }
                timeLock.lock();
                String config = "";
                // 避免空指针
                if (data != null) {
                    config = data.toString();
                }
                if ("Bc".equals(device.type)) {
                    /*之所以把Bc锁（网络智能锁）单独拎出来，是因为他的删除和其它不一样*/
                    Intent intent = new Intent(context, DeviceMoreActivityForBc.class);
                    intent.putExtra(DeviceMoreActivity.KEY_DEVICE_ID, deviceId);
                    intent.putExtra(DeviceMoreActivity.KEY_MORE_CONFIG, config);
                    startActivityForResult(intent, KEY_DELETE_DEVICE);
                    callback.callback("YES");
                } else if ("Bn".equals(device.type)) {
                    Intent intent = new Intent(context, DeviceMoreActivityForBn.class);
                    intent.putExtra(DeviceMoreActivity.KEY_DEVICE_ID, deviceId);
                    intent.putExtra(DeviceMoreActivity.KEY_MORE_CONFIG, config);
                    startActivityForResult(intent, KEY_DELETE_DEVICE);
                    callback.callback("YES");
                } else if ("82".equals(device.type)) {
                    Intent intent = new Intent(context, Controller82MoreActivity.class);
                    intent.putExtra(Controller82MoreActivity.KEY_DEVICE_ID, deviceId);
                    startActivityForResult(intent, KEY_DELETE_DEVICE);
                    callback.callback("YES");
                } else if ("HS05".equals(device.type)) {
                    Intent intent = new Intent(context, HisenseDeviceSettingActivity.class);
                    intent.putExtra(HisenseDeviceSettingActivity.KEY_DEVICE_ID, deviceId);
                    startActivityForResult(intent, KEY_DELETE_DEVICE);
                    callback.callback("YES");
                } else if ("XW01".equals(device.type)) {
                    Intent intent = new Intent(context, WishBmgSettingActivity.class);
                    intent.putExtra(WishBmgSettingActivity.KEY_DEVICE_ID, deviceId);
                    startActivityForResult(intent, KEY_DELETE_DEVICE);
                    callback.callback("YES");
                } else {
                    Intent intent = new Intent(context, DeviceMoreActivity.class);
                    intent.putExtra(DeviceMoreActivity.KEY_DEVICE_ID, deviceId);
                    intent.putExtra(DeviceMoreActivity.KEY_MORE_CONFIG, config);
                    startActivityForResult(intent, KEY_DELETE_DEVICE);
                    callback.callback("YES");
                }
//                else {
//                    Intent intent = new Intent(context, DeviceDetailMoreActivity.class);
//                    intent.putExtra(DeviceDetailMoreActivity.KEY_DEVICE_ID, deviceId);
//                    startActivityForResult(intent, KEY_DELETE_DEVICE);
//                    callback.callback("YES");
//                }
            }
        });

        // 返回登录状态
        mWebViewClient.registerHandler("getLoginType", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                switch (preference.getUserEnterType()) {
                    case Preference.ENTER_TYPE_GW:
                        callback.callback("101");
                        break;
                    case Preference.ENTER_TYPE_ACCOUNT:
                        callback.callback("100");
                        break;
                }
            }
        });

        // TODO: 2017/5/8 待封装
        mWebViewClient.registerHandler("getStatistic", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, final WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "请求统计数据: " + data);
                try {
                    JSONObject object = (JSONObject) data;
                    String dataType = object.optString("dataType");
                    String startTime = object.optString("startTime");
                    String endTime = object.optString("endTime");
                    new DeviceApiUnit(DeviceDetailActivity.this).doGetDeviceStatistic(
                            preference.getCurrentGatewayID(),
                            device.type,
                            dataType,
                            device.devID,
                            startTime,
                            endTime,
                            new DeviceApiUnit.DeviceApiCommonListener() {
                                @Override
                                public void onSuccess(Object bean) {
                                    WLog.i(TAG, bean.toString());
                                    callback.callback(bean);
                                }

                                @Override
                                public void onFail(int code, String msg) {

                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // TODO: 2017/5/8 待封装
        mWebViewClient.registerHandler("getBloodPressureHistory", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, final WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "请求统计数据: " + data);
                try {
                    JSONObject object = (JSONObject) data;
                    String startTime = object.optString("startTime");
                    String endTime = object.optString("endTime");
                    String pageSize = object.optString("pageSize");
                    String userNumber = object.optString("userNumber");
                    new DeviceApiUnit(DeviceDetailActivity.this).doGetBloodPressureHistory(
                            preference.getCurrentGatewayID(),
                            device.devID,
                            startTime,
                            endTime,
                            pageSize,
                            userNumber,
                            new DeviceApiUnit.DeviceApiCommonListener() {
                                @Override
                                public void onSuccess(Object bean) {
                                    WLog.i(TAG, bean.toString());
                                    callback.callback(bean);
                                }

                                @Override
                                public void onFail(int code, String msg) {

                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mWebViewClient.registerHandler("bridgeGet", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, final WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "桥 get请求: " + data);
                try {
                    JSONObject json = new JSONObject(data.toString());
                    String url = json.optString("url");
                    JSONObject params = json.optJSONObject("params");
                    Iterator iterator = params.keys();
                    Map<String, String> map = new HashMap<>();
                    while (iterator.hasNext()) {
                        String k = (String) iterator.next();
                        map.put(k, params.opt(k).toString());
                    }
                    mDeviceApiUnit.doBridgeGet(url, map, new DeviceApiUnit.DeviceApiCommonListener() {
                        @Override
                        public void onSuccess(Object bean) {
                            WLog.i(TAG, bean.toString());
                            callback.callback(bean);
                        }

                        @Override
                        public void onFail(int code, String msg) {

                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        mWebViewClient.registerHandler("WLHttpGetBase", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, final WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "桥 get请求: " + data);
                try {
                    JSONObject json = new JSONObject(data.toString());
                    String url = json.optString("url");
                    if (url.contains("{uId}")) {
                        url = url.replace("{uId}", ApiConstant.getUserID());
                    }
                    if (url.contains("{token}")) {
                        url = url.replace("{token}", ApiConstant.getAppToken());
                    }
                    JSONObject params = json.optJSONObject("param");
                    Map<String, String> map = new HashMap<>();
                    if (params != null) {
                        Iterator iterator = params.keys();
                        while (iterator.hasNext()) {
                            String k = (String) iterator.next();
                            map.put(k, params.opt(k).toString());
                        }
                    }
                    if (url.startsWith("/iot/v2/users")) {
                        mDeviceApiUnit.doEncryptBridgeGet(url, map, new DeviceApiUnit.DeviceApiCommonListener() {

                            @Override
                            public void onSuccess(Object bean) {
                                callback.callback(bean);
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                callback.callback(msg);
                            }
                        });
                    } else {
                        mDeviceApiUnit.doBridgeGet(url, map, new DeviceApiUnit.DeviceApiCommonListener() {
                            @Override
                            public void onSuccess(Object bean) {
                                WLog.i(TAG, bean.toString());
                                callback.callback(bean);
                            }

                            @Override
                            public void onFail(int code, String msg) {

                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        mWebViewClient.registerHandler("bridgePost", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, final WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "桥 post请求: " + data);
                try {
                    JSONObject json = new JSONObject(data.toString());
                    String url = json.optString("url");
                    JSONObject params = json.optJSONObject("params");
                    mDeviceApiUnit.doBridgePost(url, params, new DeviceApiUnit.DeviceApiCommonListener() {
                        @Override
                        public void onSuccess(Object bean) {
                            WLog.i(TAG, bean.toString());
                            callback.callback(bean);
                        }

                        @Override
                        public void onFail(int code, String msg) {


                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        mWebViewClient.registerHandler("WLHttpPostBase", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, final WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "桥 post请求: " + data);
                try {
                    JSONObject json = new JSONObject(data.toString());
                    String url = json.optString("url");
                    if (url.contains("{uId}")) {
                        url = url.replace("{uId}", ApiConstant.getUserID());
                    }
                    if (url.contains("{token}")) {
                        url = url.replace("{token}", ApiConstant.getAppToken());
                    }
                    JSONObject params = json.optJSONObject("param");

                    if (url.startsWith("/iot/v2/users")) {
                        mDeviceApiUnit.doEncryptBridgePost(url, params, new DeviceApiUnit.DeviceApiCommonListener() {
                            @Override
                            public void onSuccess(Object bean) {
                                WLog.i(TAG, bean.toString());
                                callback.callback(bean);
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                callback.callback(msg);
                            }
                        });
                    } else {
                        params.put("uId", ApiConstant.getUserID());
                        mDeviceApiUnit.doBridgePost(url, params, new DeviceApiUnit.DeviceApiCommonListener() {
                            @Override
                            public void onSuccess(Object bean) {
                                WLog.i(TAG, bean.toString());
                                callback.callback(bean);
                            }

                            @Override
                            public void onFail(int code, String msg) {

                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

//        mWebViewClient.registerHandler("bridgePost", new WVJBWebViewClient.WVJBHandler() {
//            @Override
//            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
//
//            }
//        });


        mWebViewClient.registerHandler("getLastAlarmTime", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, final WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "获取最近报警时间: " + data);
                JSONObject json = null;
                String messageCode = "";
                try {
                    json = new JSONObject(data.toString());
                    messageCode = json.optString("messageCode");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if ("02".equals(device.type)) {
                    messageCode = "0102011";//红外有人经过
                } else if ("03".equals(device.type)) {
                    messageCode = "0102051";//门磁被打开
                }
                String startTime = "1";
                String endTime = "" + (System.currentTimeMillis() + 2 * 24 * 3600);
                mDataApiUnit.doGetDeviceDataHistory(
                        preference.getCurrentGatewayID(),
                        "1",
                        deviceId,
                        messageCode,
                        startTime,
                        endTime,
                        new DataApiUnit.DataApiCommonListener() {
                            @Override
                            public void onSuccess(Object bean) {
                                try {
                                    MessageBean messageBean = (MessageBean) bean;
                                    if (messageBean.recordList != null && messageBean.recordList.size() > 0) {
                                        MessageBean.RecordListBean recordListBean = messageBean.recordList.get(0);
                                        callback.callback("" + recordListBean.time);
                                    } else {
                                        callback.callback("");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    callback.callback("");
                                }
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                callback.callback("");
                            }
                        });
            }
        });

        mWebViewClient.registerHandler("getAlarmList", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, final WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "获取最近报警消息列表:" + data);
                JSONObject json = null;
                String messageCode = "";
                String dataType = "";
                String startTime = "";
                String endTime = "";
                try {
                    json = new JSONObject(data.toString());
                    messageCode = json.optString("messageCode");
                    dataType = json.optString("dataType");
                    startTime = json.optString("startTime");
                    endTime = json.optString("endTime");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mDataApiUnit.doGetDeviceDataHistory(
                        preference.getCurrentGatewayID(),
                        dataType,
                        deviceId,
                        messageCode,
                        startTime,
                        endTime,
                        new DataApiUnit.DataApiCommonListener() {
                            @Override
                            public void onSuccess(Object bean) {
                                JSONArray array = new JSONArray();
                                try {
                                    MessageBean messageBean = (MessageBean) bean;
                                    if (messageBean.recordList != null) {
                                        for (MessageBean.RecordListBean recordListBean : messageBean.recordList) {
                                            JSONObject recordObject = new JSONObject();
                                            recordObject.put("message", MessageTool.getMessage(recordListBean));
                                            recordObject.put("time", "" + recordListBean.time);
                                            array.put(recordObject);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                callback.callback(array);
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                JSONArray array = new JSONArray();
                                callback.callback(array);
                            }
                        });
            }
        });

        mWebViewClient.registerHandler("saveWeight", new WVJBWebViewClient.WVJBHandler() {

            @Override
            public void request(Object data, final WVJBWebViewClient.WVJBResponseCallback callback) {
                try {
                    JSONObject jsonObject = new JSONObject(data.toString());
                    long createTime = jsonObject.optLong("createTime");
                    String weight = jsonObject.optString("weight");
                    mDataApiUnit.doSaveWeight(deviceId, createTime, weight, new DataApiUnit.DataApiCommonListener<Object>() {
                        @Override
                        public void onSuccess(Object bean) {
                            callback.callback(bean);
                        }

                        @Override
                        public void onFail(int code, String msg) {

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mWebViewClient.registerHandler("getWeightRecords", new WVJBWebViewClient.WVJBHandler() {

            @Override
            public void request(Object data, final WVJBWebViewClient.WVJBResponseCallback callback) {
                mDataApiUnit.doGetWeightRecords(deviceId, new DataApiUnit.DataApiCommonListener<Object>() {
                    @Override
                    public void onSuccess(Object bean) {
                        callback.callback(bean);
                    }

                    @Override
                    public void onFail(int code, String msg) {

                    }
                });
            }
        });

        mWebViewClient.registerHandler("goAlarm", new WVJBWebViewClient.WVJBHandler() {

            @Override
            public void request(Object data, final WVJBWebViewClient.WVJBResponseCallback callback) {
                MessageAlarmActivity.start(context, deviceId, device.type);
                callback.callback("OK");
            }
        });

        //插座、窗帘自定义的图标
        mWebViewClient.registerHandler("getDeviceIconByType", new WVJBWebViewClient.WVJBHandler() {

            @Override
            public void request(Object data, final WVJBWebViewClient.WVJBResponseCallback callback) {
                String result = null;
                if (TextUtils.equals(device.type, "50")) {
                    result = preference.getSocketIconImg(deviceId);
                } else if (TextUtils.equals(device.type, "Ar") || TextUtils.equals(device.type, "65") || TextUtils.equals(device.type, "Ca") || TextUtils.equals(device.type, "Cb")) {
                    result = preference.getCurtainIconImg(deviceId);
                }
                callback.callback(result);
            }
        });

        mWebViewClient.registerHandler("jumpToHouseKeeper", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "请求: 跳转管家 " + data);
                String extraData = null;
                if (TextUtils.equals("Ca", data.toString())) {
                    extraData = "action_Ca_triggerTask.html";
                } else if (TextUtils.equals("Cb", data.toString())) {
                    extraData = "triggerTask_Cb.html";
                }
                callback.callback("YES");
                startActivity(new Intent(DeviceDetailActivity.this, HouseKeeperActivity.class).putExtra("url", extraData));
                finish();
            }
        });

        mWebViewClient.registerHandler("getGatewayIsShared", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "请求: 获取网关分享状态 " + data);
                callback.callback(preference.isAuthGateway() ? "YES" : "NO");
            }
        });
        mWebViewClient.registerHandler("isLockClick", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "请求: 门锁键盘点击 ");
                VibratorUtil.holdSpeakVibration();
            }
        });
    }

    protected void newDataRefresh(String data) {

        if (TextUtils.isEmpty(data)) {
            ToastUtil.show(R.string.Device_data_error);
            return;
        }
        try {
            WLog.i(TAG, "newDataRefresh: " + new JSONObject(data));
            mWebViewClient.callHandler("newDataRefresh", new JSONObject(data), new WVJBWebViewClient.WVJBResponseCallback() {
                @Override
                public void callback(Object data) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void init() {
        Intent intent = getIntent();
        deviceId = intent.getStringExtra(KEY_DEVICE_ID);
        WLog.i(TAG, "deviceId: " + deviceId);
        device = ((MainApplication) getApplication()).getDeviceCache().get(deviceId);
        if (device == null) {
            ToastUtil.show(R.string.Device_data_error);
            finish();
            return;
        }
    }

    @Override
    protected String getUrl() {
        if (device != null) {
            String url = DeviceInfoDictionary.getUrlByType(device.type);
            if (TextUtils.isEmpty(url)) {
                ToastUtil.show(R.string.Device_data_error);
            }
            if (TextUtils.equals(device.type, "45")) {
                return url + "?token=" + ApiConstant.getAppToken()
                        + "&uId=" + ApiConstant.getUserID();
            }
            return url;
        } else {
            ToastUtil.show(R.string.Device_data_error);
        }
        return null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(CMD517Event event) {
        if (event != null && event.bean != null) {
            WLog.i(TAG, "背景音乐: " + event.data);
            if (TextUtils.equals(event.bean.devID, deviceId)) {
                newDataRefresh(event.data);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null) {
            if (TextUtils.equals(event.device.devID, deviceId)) {
                if (event.device.mode == 3) {
                    finish();
                }
                newDataRefresh(event.device.data);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceInfoChanged(DeviceInfoChangedEvent event) {
        if (event.deviceInfoBean != null) {
            if (TextUtils.equals(event.deviceInfoBean.devID, deviceId)) {
                if (event.deviceInfoBean.mode == 3) {
                    finish();
                }
                newDataRefresh(JSON.toJSONString(event.deviceInfoBean));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == KEY_DELETE_DEVICE) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSetSceneBindingEvent(SetSceneBindingEvent result) {
        if (result != null) {
            newDataRefresh(result.jsonData);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSetSceneBindingEvent(GetSceneBindingEvent result) {
        if (result != null) {
            newDataRefresh(result.jsonData);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceControlEvent(DeviceControlEvent result) {
        if (result != null) {
            newDataRefresh(result.data);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSceneInfoEvent(SceneInfoEvent result) {
        if (result != null) {
            newDataRefresh(JSON.toJSONString(result));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLanguageChangeEvent(LanguageChangeEvent event) {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTranslatorFuncEvent(TranslatorFuncEvent event) {
        if (event.data != null) {
            newDataRefresh(event.data);
        }
    }
}
