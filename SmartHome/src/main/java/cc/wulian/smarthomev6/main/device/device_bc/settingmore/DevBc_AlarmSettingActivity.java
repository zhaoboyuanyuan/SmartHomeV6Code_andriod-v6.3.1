package cc.wulian.smarthomev6.main.device.device_bc.settingmore;

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
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.HttpUrlKey;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by yuxiaoxuan on 2017/6/19.
 * Bc锁的报警设置界面
 */

public class DevBc_AlarmSettingActivity extends H5BridgeActivity {
    private String gwID;
    private String devID;
    private String alarmSettingUrl = HttpUrlKey.URL_BASE_DEVICE + "/doorLock_Bc/alarmSetting.html";
    private String bdUrl = HttpUrlKey.URL_BASE_DEVICE + "/doorLock_Bd/alarmSet.html";
    private String bfUrl=HttpUrlKey.URL_BASE_DEVICE + "/doorLock_Bf/alarmSet.html";
    private String bgUrl=HttpUrlKey.URL_BASE_DEVICE + "/doorLock_Bg/alarmSet.html";
    private String bqUrl=HttpUrlKey.URL_BASE_DEVICE + "/doorLock_Bq/alarmSet.html";
    private Device device;

    @Override
    protected String getUrl() {
        if (TextUtils.equals(device.type, "Bd")) {
            alarmSettingUrl = bdUrl;
        }else if(TextUtils.equals(device.type,"Bf")){
            alarmSettingUrl=bfUrl;
        }else if(TextUtils.equals(device.type,"Bg")){
            alarmSettingUrl=bgUrl;
        }else if(TextUtils.equals(device.type,"Bq")){
            alarmSettingUrl=bqUrl;
        }
        nativeStorage.setItem("Info_Bc", "gwID", gwID);
        nativeStorage.setItem("Info_Bc", "devID", devID);
        return alarmSettingUrl;
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
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void registerHandler() {
        super.registerHandler();
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
        mWebViewClient.registerHandler("queryUserPushInfo", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, final WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "查询用户推送消息: " + data);
                new DeviceApiUnit(DevBc_AlarmSettingActivity.this).doQueryUserPushSetts(
                        data.toString(),
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
            }
        });
        mWebViewClient.registerHandler("saveUserPushInfo", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, final WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "保存用户推送消息: " + data);
                new DeviceApiUnit(DevBc_AlarmSettingActivity.this).doSaveUserPushSetts(
                        data.toString(),
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
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReportEvent(DeviceReportEvent event) {
        if (event != null && event.device != null) {
            if (TextUtils.equals(event.device.devID, devID)) {
                newDataRefresh(event.device.data);
            }
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
