package cc.wulian.smarthomev6.main.device;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.h5.H5BridgeActivity;
import cc.wulian.smarthomev6.main.h5.WVJBWebViewClient;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.UserPushAudienceBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GatewayAccountEvent;
import cc.wulian.smarthomev6.support.tools.HttpUrlKey;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by zbl on 2017/12/15.
 * BC\BD\BF\BG门锁 短信通知
 */

public class LockMessageSettingActivity extends H5BridgeActivity {
    private String gwID;
    private String devID;
    private String url;
    private Device device;
    private DataApiUnit dataApiUnit;

    @Override
    protected String getUrl() {
        return HttpUrlKey.URL_BASE + "/" + url;
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void init() {
        super.init();
        if (getIntent() != null) {
            gwID = getIntent().getStringExtra("gwID");
            devID = getIntent().getStringExtra("devId");
            device = MainApplication.getApplication().getDeviceCache().get(devID);
            url = getIntent().getStringExtra("url");
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        dataApiUnit = new DataApiUnit(this);
    }

    @Override
    protected void registerHandler() {
        super.registerHandler();
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
                    ToastUtil.single(R.string.Device_data_error);
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
        mWebViewClient.registerHandler("getSmsPhone", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, final WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "查询需要短信通知的手机号: " + data);
                String messageCode = null;
                try {
                    JSONObject jsonObject = new JSONObject(data.toString());
                    messageCode = jsonObject.optString("messageCode");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dataApiUnit.doGetUserPushAudience(
                        ApiConstant.getUserID(),
                        devID,
                        messageCode,
                        new DataApiUnit.DataApiCommonListener<String>() {
                            @Override
                            public void onSuccess(String json) {
                                WLog.i(TAG, json);
                                try {
                                    JSONObject jsonObject = new JSONObject(json);
                                    callback.callback(jsonObject);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                ToastUtil.show(msg);
                            }
                        });
            }
        });
        mWebViewClient.registerHandler("setSmsPhone", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, final WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "设置需要短信通知的手机号: " + data);
                String messageCode = null;
                String phone = null;
                try {
                    JSONObject jsonObject = new JSONObject(data.toString());
                    messageCode = jsonObject.optString("messageCode");
                    phone = jsonObject.optString("phone");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dataApiUnit.doPostUserPushAudience(
                        ApiConstant.getUserID(),
                        devID,
                        phone,
                        messageCode,
                        new DataApiUnit.DataApiCommonListener<String>() {
                            @Override
                            public void onSuccess(String json) {
                                WLog.i(TAG, json);
                                try {
                                    JSONObject jsonObject = new JSONObject(json);
                                    callback.callback(jsonObject);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                ToastUtil.show(msg);
                            }
                        });
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReportEvent(DeviceReportEvent event) {
        if (event != null && event.device != null) {
            if (TextUtils.equals(event.device.devID, devID)) {
                newDataRefresh(event.device.data);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGatewayAccountEvent(GatewayAccountEvent event) {
        if (event != null && event.msg != null) {
            newDataRefresh(event.msg);
        }
    }

    private void newDataRefresh(String data) {

        if (TextUtils.isEmpty(data)) {
            ToastUtil.single(R.string.Device_data_error);
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
}
