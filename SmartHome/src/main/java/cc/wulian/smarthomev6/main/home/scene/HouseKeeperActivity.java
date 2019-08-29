package cc.wulian.smarthomev6.main.home.scene;

import android.net.Uri;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.h5.H5BridgeActivity;
import cc.wulian.smarthomev6.main.h5.WVJBWebViewClient;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.CMD517Event;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GetAutoProgramListEvent;
import cc.wulian.smarthomev6.support.event.GetAutoProgramTaskEvent;
import cc.wulian.smarthomev6.support.event.TranslatorFuncEvent;
import cc.wulian.smarthomev6.support.event.UeiSceneEvent;
import cc.wulian.smarthomev6.support.event.WifiIRSceneEvent;
import cc.wulian.smarthomev6.support.tools.HttpUrlKey;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.VersionUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

public class HouseKeeperActivity extends H5BridgeActivity {

    private WVJBWebViewClient.WVJBResponseCallback callback_507;
    //    private WVJBWebViewClient.WVJBResponseCallback callback_508;
    private String url;

    @Override
    protected void init() {
        EventBus.getDefault().register(this);
        String stringExtra = getIntent().getStringExtra("url");
        if (TextUtils.isEmpty(stringExtra)) {
            url = HttpUrlKey.URL_HOUSEKEEPER;
        } else {
            url = HttpUrlKey.URL_AUTOTASK + stringExtra;
        }
        nativeStorage.setItem("V6Housekeeper", "version", VersionUtil.getVersionName(this));
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

        mWebViewClient.registerHandler("getCurrentGatewayID", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "getCurrentGatewayID: 请求网关ID - " + Preference.getPreferences().getCurrentGatewayID());
                callback.callback(Preference.getPreferences().getCurrentGatewayID());
            }
        });

        mWebViewClient.registerHandler("houserkeep_508", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "houserkeep_508: 请求508 " + data.toString());
//                callback_508 = callback;

                MainApplication.getApplication().getMqttManager()
                        .publishEncryptedMessage(
                                data.toString(),
                                MQTTManager.MODE_GATEWAY_FIRST);
            }
        });

        mWebViewClient.registerHandler("houserkeep_507", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "houserkeep_507: 请求507 - " + data);
                callback_507 = callback;
                MainApplication.getApplication().getMqttManager()
                        .publishEncryptedMessage(
                                data.toString(),
                                MQTTManager.MODE_GATEWAY_FIRST);
            }
        });

        mWebViewClient.registerHandler("getDeviceInfo", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "getDeviceInfo: 请求设备信息 - " + data);
                Device thedevice = null;
                for (Device device : MainApplication.getApplication().getDeviceCache().getDevices()) {
                    if (TextUtils.equals(data.toString(), device.devID)) {
                        thedevice = device;
                        break;
                    }
                }
                try {
                    callback.callback(new JSONObject(JSON.toJSONString(thedevice)));
                } catch (JSONException e) {
                    e.printStackTrace();
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

        mWebViewClient.registerHandler("WLHttpGetBase", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, final WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "桥 get请求: " + data);
                try {
                    JSONObject json = new JSONObject(data.toString());
                    String url = json.optString("url");
                    JSONObject params = json.optJSONObject("param");
                    Map<String, String> map = new HashMap<>();
                    if (params != null) {
                        Iterator iterator = params.keys();
                        while (iterator.hasNext()) {
                            String k = (String) iterator.next();
                            map.put(k, params.opt(k).toString());
                        }
                    }
                    new DeviceApiUnit(HouseKeeperActivity.this).doBridgeGet(url, map, new DeviceApiUnit.DeviceApiCommonListener() {
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

        mWebViewClient.registerHandler("getSTCameraPrz", new WVJBWebViewClient.WVJBHandler() {

            @Override
            public void request(Object data, final WVJBWebViewClient.WVJBResponseCallback callback) {
                new DataApiUnit(HouseKeeperActivity.this).doGetPositionForH5(data.toString(), new DataApiUnit.DataApiCommonListener<Object>() {
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

    }




    @Override
    protected String getUrl() {
//        return HttpUrlKey.URL_AUTOTASK + url;
        return url;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null) {
            newDataRefresh(event.device.data);
        }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GetAutoProgramListEvent event) {
        WLog.i(TAG, "onEvent: ---------------------------");
//        if (callback_508 == null) {
//            return;
//        }
        try {
            JSONObject jsonObject = new JSONObject(event.jsonData);
//            callback_508.callback(jsonObject);
            mWebViewClient.callHandler("DataRefresh_508", jsonObject);
            WLog.i(TAG, "onEvent: " + jsonObject);
            WLog.i(TAG, "onEvent: 1111111111");
        } catch (JSONException e) {
            WLog.i(TAG, "onEvent: 2222222222");
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GetAutoProgramTaskEvent event) {
        WLog.i(TAG, "onEvent: ***************************");
        if (callback_507 == null) {
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(event.jsonData);
            callback_507.callback(jsonObject);
            WLog.i(TAG, "onEvent: " + jsonObject);
            WLog.i(TAG, "onEvent: 3333333333");
        } catch (JSONException e) {
            WLog.i(TAG, "onEvent: 4444444444");
            e.printStackTrace();
        }
    }

//    @Override
//    public void onBackPressed() {
//        if (mWebView.canGoBack() && isUrlCanBack(mWebView.getUrl())) {
//            mWebView.goBack();
//        } else {
//            super.onBackPressed();
//        }
//
//    }

    @Override
    public void onBackPressed() {
        if (mWebViewClient != null) {
            mWebViewClient.callHandler("androidBack");
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 这些页面不可以返回上一级，直接退出
     */
    private static final String[] CANNOT_GOBACK_PAGES = {
            "stewardIndex.html"//首页
    };

    private boolean isUrlCanBack(String url) {
        WLog.i("backUrl:" + url);
        try {
            Uri uri = Uri.parse(url);
            String lastPathSegment = uri.getLastPathSegment();
            WLog.i("uri.getLastPathSegment:" + lastPathSegment);
            if (lastPathSegment != null) {
                for (String page : CANNOT_GOBACK_PAGES) {
                    if (lastPathSegment.equals(page)) {
                        return false;
                    }
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UeiSceneEvent event) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("deviceState", event.deviceState);
            jsonObject.put("deviceEpData", event.deviceEpData);
            mWebViewClient.callHandler("infraredCallBack", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WifiIRSceneEvent event) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("action", event.action);
            jsonObject.put("deviceId", event.deviceId);

            JSONObject extDataObj = new JSONObject();
            extDataObj.put("codeType", event.codeType);
            extDataObj.put("blockId", event.blockId);
            extDataObj.put("keyId", event.keyId);
            extDataObj.put("blockName", event.blockName);
            jsonObject.put("extraData", extDataObj);

            JSONObject data = new JSONObject();
            data.put("deviceState", event.blockName);
            data.put("deviceEpData", jsonObject);
            mWebViewClient.callHandler("infraredCallBack", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTranslatorFuncEvent(TranslatorFuncEvent event) {
        if (event.data != null) {
            newDataRefresh(event.data);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(CMD517Event event) {
        if (event != null) {
            WLog.i(TAG, "背景音乐: " + event.data);
            newDataRefresh(event.data);
        }
    }
}
