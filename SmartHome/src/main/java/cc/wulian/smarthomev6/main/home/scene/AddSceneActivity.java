package cc.wulian.smarthomev6.main.home.scene;

import cc.wulian.smarthomev6.entity.SceneInfo;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.CMD517Event;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GetAutoProgramListEvent;
import cc.wulian.smarthomev6.support.event.GetAutoProgramTaskEvent;
import cc.wulian.smarthomev6.support.event.UeiSceneEvent;
import cc.wulian.smarthomev6.support.event.WifiIRSceneEvent;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

import android.text.TextUtils;
import android.webkit.WebView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.h5.H5BridgeActivity;
import cc.wulian.smarthomev6.main.h5.WVJBWebViewClient;
import cc.wulian.smarthomev6.support.event.SceneInfoEvent;
import cc.wulian.smarthomev6.support.tools.HttpUrlKey;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.CustomProgressDialog;

public class AddSceneActivity extends H5BridgeActivity {

    // 避免重复创建
//    private boolean couldAddingScene = true;

    private WVJBWebViewClient.WVJBResponseCallback callback503;
    private WVJBWebViewClient.WVJBResponseCallback callback_507;

    private String name;

    @Override
    protected void init() {
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void registerHandler() {
        mWebViewClient.registerHandler("isSceneExist", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                SceneManager sceneManager = new SceneManager(AddSceneActivity.this);
                JSONObject j = (JSONObject) data;
                String sceneName = j.optString("name");
                String sceneID = j.optString("sceneID");
                boolean is = sceneManager.isExistScene(sceneName, sceneID);
                callback.callback(is ? "YES" : "NO");
            }
        });

        mWebViewClient.registerHandler("scene_503", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "scene_503: --- " + data);
                callback503 = callback;

                SceneManager s = new SceneManager(AddSceneActivity.this);
                s.snedMqttMessage((JSONObject) data);
                progressDialogManager.showDialog("Create", AddSceneActivity.this, null, new CustomProgressDialog.OnDialogDismissListener() {
                    @Override
                    public void onDismiss(CustomProgressDialog var1, int var2) {
                        if (var2 != 0) {
                            ToastUtil.show(R.string.Home_Scene_New_Failed);
//                                couldAddingScene = true;
                        }
                    }
                }, getResources().getInteger(R.integer.http_timeout));
            }
        });

        mWebViewClient.registerHandler("getCurrentGatewayID", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                callback.callback(Preference.getPreferences().getCurrentGatewayID());
            }
        });

        mWebViewClient.registerHandler("getCurrentAppID", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "getCurrentAppID: 请求APP ID - " + MainApplication.getApplication().getLocalInfo().appID);
                callback.callback(MainApplication.getApplication().getLocalInfo().appID);
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
                WLog.i(TAG, "getDeviceList: 请求设备列表 - " + data);
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

        mWebViewClient.registerHandler("getSTCameraPrz", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, final WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "getSTCameraPrz: 获取小物预置位");
                new DataApiUnit(AddSceneActivity.this).doGetPositionForH5(data.toString(), new DataApiUnit.DataApiCommonListener<Object>() {
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

        mWebViewClient.registerHandler("recommendSceneName", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "recommendSceneName:" + data);
                String sceneName = data.toString();
                String tmpName = data.toString();
                SceneManager sceneManager = new SceneManager(AddSceneActivity.this);
                List<SceneInfo> sceneList = sceneManager.acquireScene();
                int index = 1;
                for (SceneInfo sceneInfo :
                        sceneList) {
                    if (TextUtils.equals(sceneInfo.getName(), tmpName)) {
                        ++index;
                        tmpName = sceneName + index;
                        tmpName = repairSceneName(tmpName, sceneName, index, sceneList);
                        break;
                    }
                }
                callback.callback(tmpName);
            }
        });
    }

    /**
     * 当场景已存在时场景名称递增重新遍历直到场景名称不同(如回家1-->回家2)
     *
     * @param tmpName
     * @param sceneName
     * @param index
     * @return
     */
    private String repairSceneName(String tmpName, String sceneName, int index, List<SceneInfo> sceneList) {
        for (SceneInfo sceneInfo :
                sceneList) {
            if (TextUtils.equals(sceneInfo.getName(), tmpName)) {
                ++index;
                tmpName = sceneName + index;
                tmpName = repairSceneName(tmpName, sceneName, index, sceneList);
            }
        }
        return tmpName;
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
    public void onSceneUpdated(SceneInfoEvent event) {
        WLog.i(TAG, "onSceneUpdated: " + event);
        if (event.sceneBean.mode == 1 || event.sceneBean.mode == 2) {
            progressDialogManager.dimissDialog("Create", 0);

            name = event.sceneBean.name;
            ToastUtil.show(R.string.Home_Scene_New_Success);
            if (callback503 != null) {
                try {
                    callback503.callback(new JSONObject(event.sceneBean.data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            // finish();
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
                    String sceneId = object.optString("object");
                    String programID = jsonObject.optString("programID");
                    SceneManager manager = new SceneManager(AddSceneActivity.this);
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
            mWebViewClient.callHandler("infraredCallBack", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(CMD517Event event) {
        if (event != null) {
            WLog.i(TAG, "背景音乐: " + event.data);
            newDataRefresh(event.data);
        }
    }

    @Override
    protected void onPageDone(WebView view, String url) {
        super.onPageDone(view, url);

        if (HttpUrlKey.URL_ADD_SCENE.equals(url)) {
            // mWebView.loadUrl("javascript:window.localStorage.clear()");
        }
    }

    @Override
    protected String getUrl() {
        return HttpUrlKey.URL_ADD_SCENE;
    }
}
