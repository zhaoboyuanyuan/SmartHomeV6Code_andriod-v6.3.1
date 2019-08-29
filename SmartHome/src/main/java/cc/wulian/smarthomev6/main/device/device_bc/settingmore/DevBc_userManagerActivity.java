package cc.wulian.smarthomev6.main.device.device_bc.settingmore;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

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
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GatewayAccountEvent;
import cc.wulian.smarthomev6.support.tools.HttpUrlKey;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by yuxiaoxuan on 2017/6/7.
 * 用户管理
 */

public class DevBc_userManagerActivity extends H5BridgeActivity {
    private String gwID;
    private String devID;
    private String token;
    private String userManagerUrl = HttpUrlKey.URL_BASE_DEVICE + "/doorLock_Bc/accountManage.html";
    private String userManagerUrlForBg = HttpUrlKey.URL_BASE_DEVICE + "/doorLock_Bc/accountManageForBg.html";
    private String userManagerUrlForBq = HttpUrlKey.URL_BASE_DEVICE + "/doorLock_Bc/accountManageForBq.html";
    private String userManagerUrlForBf = HttpUrlKey.URL_BASE_DEVICE + "/doorLock_Bc/accountManageForBf.html";
    private Device device;

    @Override
    protected void init() {
        super.init();
        if (getIntent() != null) {
            gwID = getIntent().getStringExtra("gwID");
            devID = getIntent().getStringExtra("devID");
            token = getIntent().getStringExtra("token");
            nativeStorage.setItem("Token_Bc", "Token_Bc", token);
            device = MainApplication.getApplication().getDeviceCache().get(devID);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected String getUrl() {
        if (device != null && TextUtils.equals(device.type, "Bg")) {
            userManagerUrl = userManagerUrlForBg;
        }
        if (device != null && TextUtils.equals(device.type, "Bq")) {
            userManagerUrl = userManagerUrlForBq;
        }
        if (device != null && TextUtils.equals(device.type, "Bf")) {
            userManagerUrl = userManagerUrlForBf;
        }
        return userManagerUrl;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void registerHandler() {
        super.registerHandler();
        mWebViewClient.registerHandler("getDeviceInfo", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "请求 : 设备信息 " + data);
                if (TextUtils.isEmpty(device.data)) {
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
        mWebViewClient.registerHandler("newDataRefresh", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {

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
        newDataRefresh(event.msg);
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
}
