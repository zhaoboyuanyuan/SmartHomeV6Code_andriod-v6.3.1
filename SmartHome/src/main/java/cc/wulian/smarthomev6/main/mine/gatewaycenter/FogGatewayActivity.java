package cc.wulian.smarthomev6.main.mine.gatewaycenter;

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
import cc.wulian.smarthomev6.entity.SceneInfo;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.DeviceMoreActivity;
import cc.wulian.smarthomev6.main.device.device_82.Controller82MoreActivity;
import cc.wulian.smarthomev6.main.device.device_bc.DeviceMoreActivityForBc;
import cc.wulian.smarthomev6.main.h5.H5BridgeActivity;
import cc.wulian.smarthomev6.main.h5.WVJBWebViewClient;
import cc.wulian.smarthomev6.main.home.scene.SceneManager;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.AssignMasterGWEvent;
import cc.wulian.smarthomev6.support.event.CMD517Event;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GetSceneBindingEvent;
import cc.wulian.smarthomev6.support.event.LanguageChangeEvent;
import cc.wulian.smarthomev6.support.event.MultiGatewayEvent;
import cc.wulian.smarthomev6.support.event.SetSceneBindingEvent;
import cc.wulian.smarthomev6.support.tools.HttpUrlKey;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * 雾计算主机
 */
public class FogGatewayActivity extends H5BridgeActivity {

    public static void start(Context context) {
        Intent intent = new Intent(context, FogGatewayActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
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
        mWebViewClient.registerHandler("getCurrentGatewayID", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "getCurrentGatewayID: 请求网关 ID - " + MainApplication.getApplication().getLocalInfo().appID);
                callback.callback(Preference.getPreferences().getCurrentGatewayID());
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

        mWebViewClient.registerHandler("managerGWName", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "managerGWName: 获取网关默认名称 - " + data);
                callback.callback(getString(DeviceInfoDictionary.getDefaultNameByType(data.toString())));
            }
        });

        mWebViewClient.registerHandler("getCurrentGWInfo", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                callback.callback(Preference.getPreferences().getCurrentGatewayInfo());
                WLog.i(TAG, "getCurrentGWInfo: 获取当前网关信息 - " + Preference.getPreferences().getCurrentGatewayInfo());
            }
        });
        mWebViewClient.registerHandler("getSubGWDevices", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "getSubGWDevices: 获取子网关设备信息 - " + data);

                JSONArray array = new JSONArray();
                for (Device device : MainApplication.getApplication().getDeviceCache().getDevices()) {
                    if (TextUtils.equals(data.toString(), device.subGwid)) {
                        try {
                           com.alibaba.fastjson.JSONObject jsonData = JSON.parseObject(device.data);
                            jsonData.put("name", DeviceInfoDictionary.getNameByTypeAndName(jsonData.getString("type"),jsonData.getString("name")));
                            device.data = jsonData.toJSONString();
                            array.put(new JSONObject(device.data));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                callback.callback(array);
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
        //
    }

    @Override
    protected String getUrl() {
        return HttpUrlKey.URL_FOG_GATEWAY;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null) {
//            if (TextUtils.equals(event.device.devID, deviceId)) {
//                if (event.device.mode == 3) {
//                    finish();
//                }
//                newDataRefresh(event.device.data);
//            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceInfoChanged(DeviceInfoChangedEvent event) {
        if (event.deviceInfoBean != null) {
//            if (TextUtils.equals(event.deviceInfoBean.devID, deviceId)) {
//                if (event.deviceInfoBean.mode == 3) {
//                    finish();
//                }
//                newDataRefresh(JSON.toJSONString(event.deviceInfoBean));
//            }
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
    public void onLanguageChangeEvent(LanguageChangeEvent event) {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MultiGatewayEvent event) {
        if (event.jsonData != null) {
            newDataRefresh(event.jsonData);
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(AssignMasterGWEvent event) {
        if (event.data != null) {
            newDataRefresh(event.data);
        }
    }
}
