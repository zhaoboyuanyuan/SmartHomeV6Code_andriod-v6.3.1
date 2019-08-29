package cc.wulian.smarthomev6.main.home.scene;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.webkit.WebView;

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
import cc.wulian.smarthomev6.support.event.SceneInfoEvent;
import cc.wulian.smarthomev6.support.event.TranslatorFuncEvent;
import cc.wulian.smarthomev6.support.event.UeiSceneEvent;
import cc.wulian.smarthomev6.support.event.WifiIRSceneEvent;
import cc.wulian.smarthomev6.support.tools.HttpUrlKey;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.CustomProgressDialog;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

public class EditRecSceneActivity extends H5BridgeActivity {

    private String sceneName, id;
    private JSONObject sceneJson;

    private WVJBWebViewClient.WVJBResponseCallback callback503;
    private WVJBWebViewClient.WVJBResponseCallback callback_507;
//    private WVJBWebViewClient.WVJBResponseCallback callback_508;

    @Override
    protected void init() {
        EventBus.getDefault().register(this);

        Intent intent = getIntent();

        sceneName = intent.getStringExtra("sceneName");
        id = intent.getStringExtra("id");

        Map<String, String> map = new HashMap<>();
        map.put("sceneName", sceneName);
        map.put("id", id);

        sceneJson = new JSONObject(map);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    public static void start(Context context, String sceneName, String id) {
        Intent intent = new Intent(context, EditRecSceneActivity.class);
        intent.putExtra("sceneName", sceneName);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    @Override
    protected void onPageDone(WebView view, String url) {
        // 这里不需要super
        mWebViewClient.callHandler("onReady", sceneJson);
        WLog.i(TAG, "onPageDone: " + sceneJson);
    }

    @Override
    protected void registerHandler() {
        mWebViewClient.registerHandler("isSceneExist", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                SceneManager sceneManager = new SceneManager(EditRecSceneActivity.this);
                JSONObject j = (JSONObject) data;
                String sceneName = j.optString("name");
                String sceneID = j.optString("sceneID");
                boolean is = sceneManager.isExistScene(sceneName, sceneID);
                callback.callback(is ? "YES" : "NO");
            }
        });
        mWebViewClient.registerHandler("getCurrentAppID", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "getCurrentAppID: 请求APP ID - " + MainApplication.getApplication().getLocalInfo().appID);
                callback.callback(MainApplication.getApplication().getLocalInfo().appID);
            }
        });

        mWebViewClient.registerHandler("scene_503", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                callback503 = callback;
                WLog.i(TAG, "scene_503: 修改场景 --- " + data);
                SceneManager s = new SceneManager(EditRecSceneActivity.this);
                s.snedMqttMessage((JSONObject) data);
                progressDialogManager.showDialog("Change", EditRecSceneActivity.this, null, new CustomProgressDialog.OnDialogDismissListener() {
                    @Override
                    public void onDismiss(CustomProgressDialog var1, int var2) {
                        if (var2 != 0) {
                            ToastUtil.show(R.string.Home_Scene_New_Failed);
                            mWebView.loadUrl("javascript:window.localStorage.clear()");
                        }
                    }
                }, getResources().getInteger(R.integer.http_timeout));
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
                Device device = MainApplication.getApplication().getDeviceCache().get(data.toString());
                try {
                    JSONObject jsonObject = new JSONObject(JSON.toJSONString(device));
                    jsonObject.put("cmd", "500");
                    callback.callback(jsonObject);
                    WLog.i(TAG, "设备信息：" + JSON.toJSONString(device));
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
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
                    JSONObject params = json.optJSONObject("param");
                    Map<String, String> map = new HashMap<>();
                    if (params != null) {
                        Iterator iterator = params.keys();
                        while (iterator.hasNext()) {
                            String k = (String) iterator.next();
                            map.put(k, params.opt(k).toString());
                        }
                    }
                    new DeviceApiUnit(EditRecSceneActivity.this).doBridgeGet(url, map, new DeviceApiUnit.DeviceApiCommonListener() {
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
                WLog.i(TAG, "getSTCameraPrz: 获取小物预置位");
                new DataApiUnit(EditRecSceneActivity.this).doGetPositionForH5(data.toString(), new DataApiUnit.DataApiCommonListener<Object>() {
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

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSceneUpdated(SceneInfoEvent event) {
        WLog.i(TAG, "onSceneUpdated: +++++++++++++++++++++++++++");
        if (event.sceneBean.mode == 2 || event.sceneBean.mode == 1) {
            progressDialogManager.dimissDialog("Change", 0);
            if (callback503 != null) {
                try {
                    callback503.callback(new JSONObject(event.sceneBean.data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if (event.sceneBean.mode == 3) {
            // 当前场景被删除了
            if (this.id.equals(event.sceneBean.sceneID)) {
                ToastUtil.show(R.string.Home_Scene_Has_Delete);
                finish();
            }
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
            mWebViewClient.callHandler("DataRefresh_508", jsonObject);
//            callback_508.callback(jsonObject);
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
            String operType = jsonObject.getString("operType");
            if ("C".equals(operType)) {
                JSONObject object = jsonObject.getJSONArray("triggerArray").getJSONObject(0);
                String type = object.getString("type");
                if ("0".equals(type)) {
                    String sceneId = object.getString("object");
                    String programID = jsonObject.getString("programID");
                    SceneManager manager = new SceneManager(EditRecSceneActivity.this);
                    manager.setSceneProgramId(sceneId, programID);
                }
            }
            WLog.i(TAG, "onEvent: " + jsonObject);
            WLog.i(TAG, "onEvent: 3333333333");
        } catch (JSONException e) {
            WLog.i(TAG, "onEvent: 4444444444");
            e.printStackTrace();
        }
    }

    @Override
    protected String getUrl() {
        return HttpUrlKey.URL_RECOMMEND_SCENE;
    }

    @Override
    public void onBackPressed() {
        if (mWebViewClient != null) {
            mWebViewClient.callHandler("androidBack");
        } else {
            super.onBackPressed();
        }
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
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null) {
            newDataRefresh(event.device.data);
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
