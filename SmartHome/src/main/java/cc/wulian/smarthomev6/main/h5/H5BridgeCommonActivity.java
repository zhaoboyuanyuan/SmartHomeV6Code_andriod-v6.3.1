package cc.wulian.smarthomev6.main.h5;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

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
import cc.wulian.smarthomev6.main.home.scene.EditSceneActivity;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.HttpUrlKey;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by Veev on 2017/6/7
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    公共H5 页面
 */
public class H5BridgeCommonActivity extends H5BridgeActivity {

    private String url;

    private String deviceId;
    private Device device;
    private boolean isAll = false;//是否返回所有设备信息
    private DeviceApiUnit mDeviceApiUnit;
    @Override
    protected void init() {
        EventBus.getDefault().register(this);
        mDeviceApiUnit = new DeviceApiUnit(this);
        url = getIntent().getStringExtra("url");
        deviceId = getIntent().getStringExtra("deviceID");
        if (!TextUtils.isEmpty(deviceId)) {
            device = MainApplication.getApplication().getDeviceCache().get(deviceId);
        }
    }

    @Override
    protected String getUrl() {
        return HttpUrlKey.URL_BASE + "/" + url;
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
                if (null != data){
                    String newDevId = String.valueOf(data);
                    try{
                        Device temp = MainApplication.getApplication().getDeviceCache().get(newDevId);
                        if (null != temp){
//                            device = temp;
                            callback.callback(new JSONObject(JSON.toJSONString(temp)));
                            WLog.i(TAG, "设备信息：" + new JSONObject(JSON.toJSONString(temp)));
                            return;
                        }
                    }catch (Exception e){
                        WLog.i(TAG, "请求 : 设备信息 data 不是设备id。");
                    }
                }
                if (device == null || TextUtils.isEmpty(device.data)) {
                    ToastUtil.show(R.string.Device_data_error);
                    return;
                }
                try {
                    callback.callback(new JSONObject(JSON.toJSONString(device)));
                    WLog.i(TAG, "设备信息：" + new JSONObject(JSON.toJSONString(device)));
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
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

        mWebViewClient.registerHandler("openShare", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, final WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "分享: " + data);
                Intent intent1 = new Intent(Intent.ACTION_SEND);
                intent1.putExtra(Intent.EXTRA_TEXT, (String) data);
                intent1.setType("text/plain");
                startActivity(Intent.createChooser(intent1, "share"));
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
                    new DeviceApiUnit(H5BridgeCommonActivity.this).doBridgeGet(url, map, new DeviceApiUnit.DeviceApiCommonListener() {
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

        mWebViewClient.registerHandler("openReservoirValve", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, final WVJBWebViewClient.WVJBResponseCallback callback) {
                if("YES".equals(data)){
                    isAll = true;
                }else{
                    isAll = false;
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
                        params.put("uId",ApiConstant.getUserID());
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


    }

    private void newDataRefresh(String data) {

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
            if (isAll || TextUtils.equals(event.device.devID, deviceId)) {
                if (event.device.mode == 3) {
                    finish();
                }
                newDataRefresh(event.device.data);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceInfoChanged(DeviceInfoChangedEvent event) {
        if (isAll || event.deviceInfoBean != null) {
            if (TextUtils.equals(event.deviceInfoBean.devID, deviceId)) {
                if (event.deviceInfoBean.mode == 3) {
                    finish();
                }
                newDataRefresh(JSON.toJSONString(event.deviceInfoBean));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }
}
