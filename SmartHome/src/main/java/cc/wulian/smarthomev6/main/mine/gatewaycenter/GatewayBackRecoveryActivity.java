package cc.wulian.smarthomev6.main.mine.gatewaycenter;

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
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.ChildDeviceInfoBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.GatewayBackDataListBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.ResponseBean;
import cc.wulian.smarthomev6.support.core.device.GatewayDataEntity;
import cc.wulian.smarthomev6.support.core.device.GatewayManager;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.GatewayBackupRecoveryEvent;
import cc.wulian.smarthomev6.support.event.GatewayInfoEvent;
import cc.wulian.smarthomev6.support.tools.HttpUrlKey;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by Veev on 2017/6/7
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    公共H5 页面
 */
public class GatewayBackRecoveryActivity extends H5BridgeActivity {

    private String currentGatewayId;
    private DeviceApiUnit mDeviceApiUnit;
    private String recoverGatewayId;

    @Override
    protected void init() {
        EventBus.getDefault().register(this);
        currentGatewayId = preference.getCurrentGatewayID();
        mDeviceApiUnit = new DeviceApiUnit(GatewayBackRecoveryActivity.this);
    }

    @Override
    protected String getUrl() {
        return HttpUrlKey.URL_BASE + "/" + "Backup_and_Recovery/backups_Recovery.html";
    }

    @Override
    protected void registerHandler() {
        mWebViewClient.registerHandler("getGatewayBackupData", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, final WVJBWebViewClient.WVJBResponseCallback callback) {
                if (!TextUtils.isEmpty(data.toString())) {
                    try {
                        JSONObject json = new JSONObject(data.toString());
                        recoverGatewayId = json.optString("gatewayId");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    recoverGatewayId = currentGatewayId;
                }
                WLog.i(TAG, "查询备份数据：");
                mDeviceApiUnit.getGatewayBackupData(recoverGatewayId, new DeviceApiUnit.DeviceApiCommonListener<GatewayBackDataListBean>() {
                    @Override
                    public void onSuccess(GatewayBackDataListBean bean) {
                        String jsonObject = JSON.toJSONString(bean);
                        callback.callback(jsonObject);
                        WLog.i(TAG, "查询备份数据：" + jsonObject);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        callback.callback("");
                    }
                });
            }
        });

        mWebViewClient.registerHandler("backupGatewayData", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, final WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "通知网关备份接口：");
                new DeviceApiUnit(GatewayBackRecoveryActivity.this).backupGatewayData(currentGatewayId, new DeviceApiUnit.DeviceApiCommonListener() {
                    @Override
                    public void onSuccess(Object bean) {
                        ResponseBean result = (ResponseBean) bean;
                        callback.callback(result.resultDesc);
                        WLog.i(TAG, "通知网关备份接口：" + ((ResponseBean) bean).data);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        callback.callback("failed");
                    }
                });
            }
        });

        mWebViewClient.registerHandler("recoveryGatewayData", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, final WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "通知网关恢复接口：");
                String bid = null;
                if (data != null) {
                    try {
                        JSONObject json = new JSONObject(data.toString());
                        bid = json.optString("bid");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new DeviceApiUnit(GatewayBackRecoveryActivity.this).recoveryGatewayData(currentGatewayId, recoverGatewayId, bid, new DeviceApiUnit.DeviceApiCommonListener() {
                        @Override
                        public void onSuccess(Object bean) {
                            ResponseBean result = (ResponseBean) bean;
                            callback.callback(result.resultDesc);
                            WLog.i(TAG, "通知网关恢复接口：" + ((ResponseBean) bean).data);
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            callback.callback("failed");
                        }
                    });
                }
            }
        });


        /**
         * 1：请勿输入当前登录的网关账号
         * 2：您输入的网关与当前登录网关型号不一致，无法恢复
         * 3：您输入的网关处于在线正常可用状态，如需继续操作，请先将其断电或恢复出厂设置
         * 4：账号或密码错误，请重试
         * 5：验证成功
         */
        mWebViewClient.registerHandler("verifyGateway", new WVJBWebViewClient.WVJBHandler()

        {
            @Override
            public void request(Object data,
                                final WVJBWebViewClient.WVJBResponseCallback callback) {
                JSONObject object = (JSONObject) data;
                final String gatewayId = object.optString("gatewayId");
                final String password = object.optString("password");
                if (TextUtils.equals(gatewayId, currentGatewayId)) {
                    callback.callback(1);
                } else {
                    mDeviceApiUnit.doGetDeviceInfo(gatewayId, new DeviceApiUnit.DeviceApiCommonListener<ChildDeviceInfoBean>() {
                        @Override
                        public void onSuccess(ChildDeviceInfoBean bean) {
                            GatewayManager gatewayManager = new GatewayManager();
                            GatewayDataEntity gatewayDataEntity = gatewayManager.getGatewayInfo(currentGatewayId);
                            if (!TextUtils.equals(gatewayDataEntity.getType(), bean.type)) {
                                callback.callback(2);
                            } else if ("1".equals(bean.state)) {
                                callback.callback(3);
                            } else {
                                mDeviceApiUnit.doVerifyGwPwdAndSaveGwId(gatewayId, password, "1", new DeviceApiUnit.DeviceApiCommonListener() {
                                    @Override
                                    public void onSuccess(Object bean) {
                                        callback.callback(5);
                                    }

                                    @Override
                                    public void onFail(int code, String msg) {
                                        callback.callback(4);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            callback.callback(4);
                        }
                    });
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

    /**
     * 网关备份恢复结果通知
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGatewayBackupRecoveryEvent(GatewayBackupRecoveryEvent event) {
        if (currentGatewayId != null && currentGatewayId.equals(event.bean.gwID)) {
            newDataRefresh(JSON.toJSONString(event.bean));
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
