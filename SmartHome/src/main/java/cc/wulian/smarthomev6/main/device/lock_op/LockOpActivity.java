package cc.wulian.smarthomev6.main.device.lock_op;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.utils.L;

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
import cc.wulian.smarthomev6.support.event.GatewayAccountEvent;
import cc.wulian.smarthomev6.support.tools.HttpUrlKey;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by Veev on 2017/6/7
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    玄武锁界面
 */

public class LockOpActivity extends H5BridgeActivity {

    private String OP_HOME = HttpUrlKey.URL_BASE_DEVICE + "/goldCodeLock_op/";
    private String url;

    private String deviceId;
    private Device device;

    @Override
    protected void init() {
        EventBus.getDefault().register(this);

        url = OP_HOME + getIntent().getStringExtra("url");

        deviceId = getIntent().getStringExtra("deviceID");

        device = MainApplication.getApplication().getDeviceCache().get(deviceId);
    }

    @Override
    protected String getUrl() {
        return url;
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
                new DeviceApiUnit(LockOpActivity.this).doQueryUserPushSetts(
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
                new DeviceApiUnit(LockOpActivity.this).doSaveUserPushSetts(
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

        /*mWebViewClient.registerHandler("newPage", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                try {
                    JSONObject json = new JSONObject((String) data);
                    String url = json.optString("url");
                    String deviceID = json.optString("deviceID");
                    Intent intent = new Intent(LockOpActivity.this, LockOpActivity.class);
                    intent.putExtra("url", url);
                    intent.putExtra("deviceID", deviceID);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });*/

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
    public void onGatewayAccountEvent(GatewayAccountEvent event) {
        newDataRefresh(event.msg);
        /*try {
            JSONObject json = new JSONObject(event.msg);
            String appID = json.optString("appID");
            String mAppID = MainApplication.getApplication().getLocalInfo().appID;
            if (TextUtils.equals(appID, mAppID)) {
                newDataRefresh(event.msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }
}
