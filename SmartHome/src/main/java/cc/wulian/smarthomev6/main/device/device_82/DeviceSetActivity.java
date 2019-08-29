package cc.wulian.smarthomev6.main.device.device_82;

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

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.SceneInfo;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.DeviceMoreActivity;
import cc.wulian.smarthomev6.main.device.device_bc.DeviceMoreActivityForBc;
import cc.wulian.smarthomev6.main.h5.H5BridgeActivity;
import cc.wulian.smarthomev6.main.h5.WVJBWebViewClient;
import cc.wulian.smarthomev6.main.home.scene.SceneManager;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GetSceneBindingEvent;
import cc.wulian.smarthomev6.support.event.LanguageChangeEvent;
import cc.wulian.smarthomev6.support.event.SetSceneBindingEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * 设备设置
 */
public class DeviceSetActivity extends H5BridgeActivity {

    public static final String URL = "url";
    public static final String KEY_DEVICE_ID = "key_device_id";
    public static final int KEY_DELETE_DEVICE = 0;


    public static void start(Context context, String deviceId, String url) {
        Intent intent = new Intent(context, DeviceSetActivity.class);
        intent.putExtra(URL, url);
        intent.putExtra(KEY_DEVICE_ID, deviceId);
        context.startActivity(intent);
    }

    protected Context context;
    protected String deviceId;
    protected String url;
    protected Device device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
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
        mWebViewClient.registerHandler("getDeviceInfo", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "请求 : 设备信息 " + data);
                if (TextUtils.isEmpty(device.data)) {
                    ToastUtil.show(R.string.Device_data_error);
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(device.data);
                    jsonObject.put("cmd","500");
                    callback.callback(jsonObject);
                    WLog.i(TAG, "设备信息：" + device.data);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                newDataRefresh(device.data);
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
        mWebViewClient.registerHandler("more", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "请求: 更多 " + data);
                if(device.isShared){
                    ToastUtil.show(R.string.Share_More_Tip);
                    return;
                }
                String config = "";
                // 避免空指针
                if (data != null) {
                    config = data.toString();
                }
                if("Bc".equals(device.type)){
                    /*之所以把Bc锁（网络智能锁）单独拎出来，是因为他的删除和其它不一样*/
                    Intent intent = new Intent(context, DeviceMoreActivityForBc.class);
                    intent.putExtra(DeviceMoreActivity.KEY_DEVICE_ID, deviceId);
                    intent.putExtra(DeviceMoreActivity.KEY_MORE_CONFIG, config);
                    startActivityForResult(intent, KEY_DELETE_DEVICE);
                    callback.callback("YES");
                }else {
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
                    new DeviceApiUnit(DeviceSetActivity.this).doGetDeviceStatistic(
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
        url = intent.getStringExtra(URL);
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
        return url;
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
    public void onSetSceneBindingEvent(SetSceneBindingEvent result){
        if(result!=null){
            newDataRefresh(result.jsonData);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSetSceneBindingEvent(GetSceneBindingEvent result){
        if(result!=null){
            newDataRefresh(result.jsonData);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLanguageChangeEvent(LanguageChangeEvent event) {
        finish();
    }
}
