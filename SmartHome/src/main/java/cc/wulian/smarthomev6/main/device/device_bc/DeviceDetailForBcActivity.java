package cc.wulian.smarthomev6.main.device.device_bc;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.DeviceDetailActivity;
import cc.wulian.smarthomev6.main.device.device_bc.config.DevBcWifiConfigActivity;
import cc.wulian.smarthomev6.main.h5.WVJBWebViewClient;
import cc.wulian.smarthomev6.support.core.apiunit.ICamApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamLoginBean;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.GatewayConfigEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.StringUtil;

/**
 * Created by yuxiaoxuan on 2017/6/20.
 * 设备详情页--用于Bc锁（网络智能锁）
 */

public class DeviceDetailForBcActivity extends DeviceDetailActivity {
    private WVJBWebViewClient.WVJBResponseCallback callbackCmd521 = null;
    private String key521 = "";

    public static void startActivity(Context context, String deviceID) {
        Intent intent = new Intent(context, DeviceDetailForBcActivity.class);
        intent.putExtra(KEY_DEVICE_ID, deviceID);
        context.startActivity(intent);
    }

    @Override
    protected void init() {
        super.init();
        key521 = deviceId + "Bc";
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void registerHandler() {
        super.registerHandler();
        mWebViewClient.registerHandler("BcCameraList", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, final WVJBWebViewClient.WVJBResponseCallback callback) {
                final ICamApiUnit iCamApiUnit = new ICamApiUnit(DeviceDetailForBcActivity.this, Preference.getPreferences().getCurrentGatewayID());
                iCamApiUnit.loginForGw(Preference.getPreferences().getCurrentGatewayID(), new ICamApiUnit.ICamApiCommonListener<ICamLoginBean>() {
                    @Override
                    public void onSuccess(ICamLoginBean bean) {
                        iCamApiUnit.getICamListForH5(new ICamApiUnit.ICamApiCommonListener<String>() {
                            @Override
                            public void onSuccess(String data) {
                                try {
                                    callback.callback(new JSONObject(data));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFail(int code, String msg) {

                            }
                        });
                    }

                    @Override
                    public void onFail(int code, String msg) {

                    }
                });
            }
        });

            mWebViewClient.registerHandler("BcCameraStart", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, final WVJBWebViewClient.WVJBResponseCallback callback) {
                try {
                    DeviceBcCameraActivity.start(DeviceDetailForBcActivity.this, deviceId, data.toString(),false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mWebViewClient.registerHandler("cmd521", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, final WVJBWebViewClient.WVJBResponseCallback callback) {
                callbackCmd521 = callback;
                MainApplication.getApplication().getMqttManager().publishEncryptedMessage(
                        MQTTCmdHelper.createGatewayConfig(Preference.getPreferences().getCurrentGatewayID(),
                                3,
                                MainApplication.getApplication().getLocalInfo().appID,
                                deviceId,
                                key521,
                                null, null)
                        , MQTTManager.MODE_GATEWAY_FIRST
                );
            }
        });
        mWebViewClient.registerHandler("toWiFiSetting", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, final WVJBWebViewClient.WVJBResponseCallback callback) {
                Intent intent = new Intent();
                intent.setClass(DeviceDetailForBcActivity.this, DevBcWifiConfigActivity.class);
                intent.putExtra("gwID", device.gwID);
                intent.putExtra("devID", deviceId);
                DeviceDetailForBcActivity.this.startActivity(intent);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGatewayConfigEvent(GatewayConfigEvent event) {
        boolean isRight = event != null
                && event.bean != null
                && TextUtils.equals(event.bean.d, deviceId)
                && TextUtils.equals(event.bean.k, key521)
                && event.bean.m == 3;
        if (isRight) {
            if (callbackCmd521 != null) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("m", "3");
                    jsonObject.put("k", key521);
                    if (StringUtil.isNullOrEmpty(event.bean.v)) {
                        jsonObject.put("v", "");
                    } else {
                        JSONObject jsonV = new JSONObject(event.bean.v);
                        jsonObject.put("v", jsonV);
                    }
                    callbackCmd521.callback(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
