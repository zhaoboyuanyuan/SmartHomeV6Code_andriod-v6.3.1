package cc.wulian.smarthomev6.main.device;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.LocalInfo;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.camera_lc.config.AddLcCameraGuideActivity;
import cc.wulian.smarthomev6.main.device.device_Bn.config.DevBnWifiConfigActivity;
import cc.wulian.smarthomev6.main.device.device_CG27.config.AddCG27GuideActivity;
import cc.wulian.smarthomev6.main.device.device_bc.config.DevBcWifiConfigActivity;
import cc.wulian.smarthomev6.main.device.device_if02.config.AddIF02DeviceActivity;
import cc.wulian.smarthomev6.main.device.device_xw01.config.WishBgmAddGuideActivity;
import cc.wulian.smarthomev6.main.device.eques.EquesAddGuideActivity;
import cc.wulian.smarthomev6.main.device.config.AddDeviceGuideActivity;
import cc.wulian.smarthomev6.main.device.hisense.config.AddHisenseDeviceActivity;
import cc.wulian.smarthomev6.main.device.safeDog.config.SDConfigActivity;
import cc.wulian.smarthomev6.main.device.wristband.AddWristbandActivity;
import cc.wulian.smarthomev6.main.h5.H5BridgeActivity;
import cc.wulian.smarthomev6.main.h5.WVJBWebViewClient;
import cc.wulian.smarthomev6.main.login.SigninActivity;
import cc.wulian.smarthomev6.main.mine.gatewaycenter.ConfirmGatewayPasswordActivity;
import cc.wulian.smarthomev6.main.mine.gatewaycenter.GatewayBindActivity;
import cc.wulian.smarthomev6.main.mine.gatewaycenter.GatewayListActivity;
import cc.wulian.smarthomev6.main.mine.platform.ControlPlatformActivity;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceIsPushBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.GatewaySupportDeviceBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.ResponseBean;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.AddDeviceEvent;
import cc.wulian.smarthomev6.support.event.CMD517Event;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GatewayStateChangedEvent;
import cc.wulian.smarthomev6.support.tools.HttpUrlKey;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.RegularTool;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.tools.zxing.activity.CaptureActivity;
import cc.wulian.smarthomev6.support.tools.zxing.activity.QRCodeActivity;
import cc.wulian.smarthomev6.support.utils.ConstantsUtil;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by syf on 2017/2/14
 */
public class AddDeviceActivity extends H5BridgeActivity {

    private WVJBWebViewClient.WVJBResponseCallback qrCodeCallback;
    private WVJBWebViewClient.WVJBResponseCallback qrScanCallback;

    private WVJBWebViewClient.WVJBResponseCallback setDeviceInfoCallback;
    private String setDeviceInfoId;

    @Override
    protected void init() {
        EventBus.getDefault().register(this);
    }

    @Override
    protected void registerHandler() {
        // 获取版本   公司
        mWebViewClient.registerHandler("getProject", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "请求: getProject");
                callback.callback(ApiConstant.getAppProject());
            }
        });
        // 前端请求扫码
        mWebViewClient.registerHandler("getQRCode", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "请求: 前端请求扫码");
                qrCodeCallback = callback;
                //打开扫描界面扫描条形码或二维码
                Intent openCameraIntent = new Intent(AddDeviceActivity.this, CaptureActivity.class);
                startActivityForResult(openCameraIntent, 0);
            }
        });

        // 开启网关待加网模式
        mWebViewClient.registerHandler("gatewayAddDevice", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "请求: 开启网关待加网模式");
                int time = 60;
                if (data != null) {
                    try {
                        String s = (String) data;
                        try {
                            time = Integer.parseInt(s);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                    }
                }
                MainApplication.getApplication()
                        .getMqttManager()
                        .publishEncryptedMessage(
                                MQTTCmdHelper.createAddDevice(
                                        preference.getCurrentGatewayID(),
                                        MainApplication.getApplication().getLocalInfo().appID,
                                        null,
                                        null,
                                        time),
                                MQTTManager.MODE_GATEWAY_FIRST);
                callback.callback(0);
            }
        });
        // 设备加网 根据type
        mWebViewClient.registerHandler("gatewayAddDeviceByType", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "请求: 设备加网 根据type");
                String type = data.toString();
                MainApplication.getApplication()
                        .getMqttManager()
                        .publishEncryptedMessage(
                                MQTTCmdHelper.createAddDevice(
                                        preference.getCurrentGatewayID(),
                                        MainApplication.getApplication().getLocalInfo().appID,
                                        null,
                                        type,
                                        60),
                                MQTTManager.MODE_GATEWAY_FIRST);
                callback.callback(0);
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

        mWebViewClient.registerHandler("getGatewayID", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "请求: 获取网关ID " + data);
                callback.callback(preference.getCurrentGatewayID());
            }
        });

        mWebViewClient.registerHandler("getGatewayIsShared", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "请求: 获取网关分享状态 " + data);
                callback.callback(preference.isAuthGateway() ? "YES" : "NO");
            }
        });

        // 前端请求绑定设备
        mWebViewClient.registerHandler("bindingDevice", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, final WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "bindingDevice: " + data);
                final DeviceApiUnit deviceUnit = new DeviceApiUnit(AddDeviceActivity.this);

                JSONObject object = (JSONObject) data;
                final String deviceId = object.optString("deviceId");
                final String devicePasswd = object.optString("devicePasswd");
                String deviceType = object.optString("deviceType");

                deviceUnit.doBindDevice(deviceId, devicePasswd, deviceType, new DeviceApiUnit.DeviceApiCommonListener() {
                    @Override
                    public void onSuccess(Object bean) {
                        WLog.i(TAG, "bindingDevice - onSuccess");

                        deviceUnit.doGetAllDevice(new DeviceApiUnit.DeviceApiCommonListener<List<DeviceBean>>() {
                            @Override
                            public void onSuccess(List<DeviceBean> deviceBeanList) {
                                for (DeviceBean deviceBean : deviceBeanList) {
                                    if (deviceBean.deviceId.equalsIgnoreCase(deviceId) && deviceBean.isGateway()) {
                                        switchGateway(deviceBean, devicePasswd, true);
                                        break;
                                    }
                                }

                                JSONObject jjj = new JSONObject();
                                try {
                                    jjj.put("resultCode", 0);
                                    jjj.put("resultDesc", "success");
                                    callback.callback(jjj);
                                } catch (JSONException e) {
                                    WLog.e(TAG, "onSuccess: to JsonObject", e);
                                }
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                WLog.i(TAG, "bindingDevice - onFail: " + msg);
                                JSONObject jjj = new JSONObject();
                                try {
                                    jjj.put("resultCode", code);
                                    jjj.put("resultDesc", msg);
                                    callback.callback(jjj);
                                } catch (JSONException e) {
                                    WLog.e(TAG, "onFail: to jsonObject", e);
                                }
                                WLog.i(TAG, "onFail: " + code + ", msg: " + msg);
                            }
                        });

                    }

                    @Override
                    public void onFail(int code, String msg) {
                        WLog.i(TAG, "bindingDevice - onFail: " + msg);
                        JSONObject jjj = new JSONObject();
                        try {
                            jjj.put("resultCode", code);
                            jjj.put("resultDesc", msg);
                            callback.callback(jjj);
                        } catch (JSONException e) {
                            WLog.e(TAG, "onFail: to jsonObject", e);
                        }
                        WLog.i(TAG, "onFail: " + code + ", msg: " + msg);
                    }
                });
            }
        });

        // 设置设备信息
        mWebViewClient.registerHandler("setDeviceInfo", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "请求: 设置设备信息");
                setDeviceInfoCallback = callback;
                try {
                    setDeviceInfoId = ((JSONObject) data).getString("devID");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MainApplication.getApplication().getMqttManager()
                        .publishEncryptedMessage(
                                data.toString(),
                                MQTTManager.MODE_GATEWAY_FIRST);
            }
        });

        // 是否绑定网关
        mWebViewClient.registerHandler("isBindGw", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                /*if (preference.getCurrentGatewayID().isEmpty()) {
                    callback.callback("NO");
                } else {
                    callback.callback("YES");
                }*/
                if (preference.getBindGatewayCount() > 0
                        || TextUtils.equals(preference.getUserEnterType(), Preference.ENTER_TYPE_GW)) {
                    callback.callback("YES");
                } else {
                    // 弹框
                    showBindGwDialog();
                }
            }
        });

        // 跳 网关列表
        mWebViewClient.registerHandler("goToBind", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                // do something...
                startActivity(new Intent(AddDeviceActivity.this, GatewayListActivity.class));
            }
        });

        // 跳扫码
        mWebViewClient.registerHandler("qrScan", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                // do something...
//                QRCodeActivity.start(AddDeviceActivity.this, ConstantsUtil.DEVICE_SCAN_ENTRY);
                qrScanCallback = callback;
                startActivityForResult(new Intent(AddDeviceActivity.this, QRCodeActivity.class).putExtra("scanType", ConstantsUtil.DEVICE_SCAN_ENTRY), 1);
//                finish();
            }
        });


        // 跳爱看设备
        mWebViewClient.registerHandler("startCamera", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                String type = (String) data;
                if (TextUtils.equals(type, "CMICA1")
                        || TextUtils.equals(type, "CMICA2")
                        || TextUtils.equals(type, "CMICA3")
                        || TextUtils.equals(type, "CMICA4")
                        || TextUtils.equals(type, "CMICA5")
                        || TextUtils.equals(type, "CMICA6")
                        ) {
                    AddDeviceGuideActivity.start(AddDeviceActivity.this, type);
                    finish();
                } else if (TextUtils.equals(type, "CMICY1")) {
                    EquesAddGuideActivity.start(AddDeviceActivity.this);
                } else if (DeviceInfoDictionary.isLcCamera(type)) {
                    QRCodeActivity.start(AddDeviceActivity.this, ConstantsUtil.CAMERA_ADD_ENTRY);
                    finish();
                } else if (TextUtils.equals("CG27", type)) {
                    AddCG27GuideActivity.start(AddDeviceActivity.this, type);
                    finish();
                }
            }
        });
        // 跳海信设备
        mWebViewClient.registerHandler("startHisense", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                String type = (String) data;
                if (TextUtils.equals(type, "HS01")
                        || TextUtils.equals(type, "HS02")
                        || TextUtils.equals(type, "HS03")
                        || TextUtils.equals(type, "HS04")
                        || TextUtils.equals(type, "HS05")
                        || TextUtils.equals(type, "HS06")
                        ) {
                    AddHisenseDeviceActivity.start(AddDeviceActivity.this, type, false);
//                    finish();
                }
            }
        });

        // 跳安全狗设备
        mWebViewClient.registerHandler("startSafeDog", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                String type = (String) data;
                if (TextUtils.equals(type, "sd01")) {
                    startActivity(new Intent(AddDeviceActivity.this, SDConfigActivity.class));
//                    finish();
                }
            }
        });

        // 添加手环
        mWebViewClient.registerHandler("addRing", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                startActivity(new Intent(AddDeviceActivity.this, AddWristbandActivity.class));
            }
        });
        // Bc加网跳配置WiFi
        mWebViewClient.registerHandler("toNativeController", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                try {
                    JSONObject jsonObject = new JSONObject(data.toString());
                    String view = jsonObject.optString("view");
                    JSONObject dataObj = jsonObject.getJSONObject("data");
                    String gwID = dataObj.getString("gwID");
                    String devID = dataObj.getString("devID");
                    String extData = dataObj.optString("extData");
                    String type = dataObj.getString("type");
                    if (TextUtils.equals("BcbindWifiView", view) && TextUtils.equals("Bc", type)) {
                        DevBcWifiConfigActivity.start(AddDeviceActivity.this, gwID, devID, extData);
                        finish();
                    } else if (TextUtils.equals("BnBindWifiView", view) && TextUtils.equals("Bn", type)) {
                        DevBnWifiConfigActivity.start(AddDeviceActivity.this, gwID, devID, extData);
                        finish();
                    } else {
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        // 向往背景音乐
        mWebViewClient.registerHandler("startWishBgm", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                String type = (String) data;
                if (TextUtils.equals(type, "XW01")) {
                    startActivity(new Intent(AddDeviceActivity.this, WishBgmAddGuideActivity.class).putExtra("type", type));
                }
            }
        });

        //wifi红外转发
        mWebViewClient.registerHandler("startWifiIR", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                //ToastUtil.show(data.toString());
                String type = (String) data;
                if (TextUtils.equals(type, "IF02")) {
                    AddIF02DeviceActivity.start(AddDeviceActivity.this, type, false);
                }
            }
        });
        // 智能控制平台
        mWebViewClient.registerHandler("startSmart", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                if (preference.isLogin()) {
                    startActivity(new Intent(AddDeviceActivity.this, ControlPlatformActivity.class));
                } else {
                    startActivity(new Intent(AddDeviceActivity.this, SigninActivity.class));
                }
            }
        });
        mWebViewClient.registerHandler("getCurrentGWInfo", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                callback.callback(Preference.getPreferences().getCurrentGatewayInfo());
                WLog.i(TAG, "getCurrentGWInfo: 获取当前网关信息 - " + Preference.getPreferences().getCurrentGatewayInfo());
            }
        });
        // 校验网关是否支持设备（lite网关）
        mWebViewClient.registerHandler("checkGatewaySupportDevice", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, final WVJBWebViewClient.WVJBResponseCallback callback) {
                if (data == null) {
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(data.toString());
                    String gatewayType = jsonObject.optString("gatewayType");
                    String deviceType = jsonObject.optString("deviceType");
                    DeviceApiUnit deviceApiUnit = new DeviceApiUnit(AddDeviceActivity.this);
                    deviceApiUnit.checkGatewaySupportDevice(gatewayType, deviceType, new DeviceApiUnit.DeviceApiCommonListener<GatewaySupportDeviceBean>() {
                        @Override
                        public void onSuccess(GatewaySupportDeviceBean bean) {
                            if (bean != null) {
                                callback.callback(bean.support);
                            }
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            ToastUtil.show(msg);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        //【V5升V6】校验网关密码（V5网关）
        mWebViewClient.registerHandler("V5ToV6CheckGWPassword", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, final WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "校验网关密码: " + data);
                try {
                    JSONObject object = (JSONObject) data;
                    String deviceId = object.optString("deviceId");
                    String password = object.optString("password");
                    DeviceApiUnit deviceApiUnit = new DeviceApiUnit(AddDeviceActivity.this);
                    deviceApiUnit.checkV5GatewayPassword(deviceId, password, new DeviceApiUnit.DeviceApiCommonListener<ResponseBean>() {
                        @Override
                        public void onSuccess(ResponseBean bean) {
                            try {
                                JSONObject object1 = new JSONObject(JSON.toJSONString(bean));
                                callback.callback(object1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            ToastUtil.show(R.string.V5_V6_upgrade_fail_V6);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //【V5升V6】网关升级到V5最新版本（V5网关）
        mWebViewClient.registerHandler("V5ToV6UpGradeGW", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, final WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "网关升级到V5最新版本: " + data);
                try {
                    JSONObject object = (JSONObject) data;
                    String deviceId = object.optString("deviceId");
                    String model = object.optString("model");
                    DeviceApiUnit deviceApiUnit = new DeviceApiUnit(AddDeviceActivity.this);
                    deviceApiUnit.updateV5GatewayVersion(deviceId, model, new DeviceApiUnit.DeviceApiCommonListener<ResponseBean>() {
                        @Override
                        public void onSuccess(ResponseBean bean) {
                            JSONObject object1 = null;
                            try {
                                object1 = new JSONObject(JSON.toJSONString(bean));
                                callback.callback(object1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            ToastUtil.show(msg);

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //【V5升V6】获取网关版本（V5网关）
        mWebViewClient.registerHandler("V5ToV6GetGWversion", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, final WVJBWebViewClient.WVJBResponseCallback callback) {
                WLog.i(TAG, "获取网关版本: " + data);
                try {
                    JSONObject object = (JSONObject) data;
                    String deviceId = object.optString("deviceId");
                    DeviceApiUnit deviceApiUnit = new DeviceApiUnit(AddDeviceActivity.this);
                    deviceApiUnit.getV5GwVersion(deviceId, new DeviceApiUnit.DeviceApiCommonListener<ResponseBean>() {
                        @Override
                        public void onSuccess(ResponseBean bean) {
                            JSONObject object1 = null;
                            try {
                                object1 = new JSONObject(JSON.toJSONString(bean));
                                callback.callback(object1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            ToastUtil.show(msg);

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //低成本网关
        mWebViewClient.registerHandler("startLiteGW", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                //ToastUtil.show(data.toString());
                String type = (String) data;
                if (TextUtils.equals(type, "GW14")) {
                    AddIF02DeviceActivity.start(AddDeviceActivity.this, type, false);
                }
            }
        });
        //v5升级v6
        mWebViewClient.registerHandler("goToBindGateway", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                //ToastUtil.show(data.toString());
                String gwId = (String) data;
                GatewayBindActivity.start(AddDeviceActivity.this, gwId, false);
                finish();
            }
        });

    }


    //切换网关。只有在云账号登录下才有这个功能。
    private void switchGateway(DeviceBean bean, String password, boolean isFinish) {
        //清空当前网关信息
        MainApplication.getApplication().clearCurrentGateway();
        //保存网关密码
        DeviceApiUnit deviceApiUnit = new DeviceApiUnit(AddDeviceActivity.this);
        preference.saveGatewayPassword(bean.deviceId, password);
        preference.saveCurrentGatewayID(bean.deviceId);
        deviceApiUnit.doSwitchGatewayId(bean.deviceId);
        saveCurrentGatewayInfo(bean);
        preference.saveCurrentGatewayState(bean.state);
        preference.saveGatewayRelationFlag(bean.relationFlag);
        EventBus.getDefault().post(new GatewayStateChangedEvent(null));
        MainApplication.getApplication().getDeviceCache().loadDatabaseCache(bean.deviceId, bean.state);//加载设备列表缓存
        EventBus.getDefault().post(new DeviceReportEvent(null));
        requestAllInfo(bean.deviceId);
        if ("1".equals(bean.getState())
                && !TextUtils.isEmpty(password)
                && (RegularTool.isNeedResetPwd(password) || TextUtils.equals(password, bean.deviceId.substring(bean.deviceId.length() - 6).toUpperCase()))) {
            Intent intent = new Intent(AddDeviceActivity.this, ConfirmGatewayPasswordActivity.class);
            intent.putExtra("oldPwd", preference.getGatewayPassword(bean.deviceId));
            startActivityForResult(intent, 1);
        }
    }

    //根据http接口先存储网关的基本信息，后面mqtt接口的信息刷新为详细
    private void saveCurrentGatewayInfo(DeviceBean bean) {
        com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
        jsonObject.put("gwID", bean.deviceId);
        jsonObject.put("gwVer", bean.version);
        jsonObject.put("gwName", bean.getName());
        jsonObject.put("gwType", bean.getType());
        jsonObject.put("hostFlag", bean.getHostFlag());
        preference.saveCurrentGatewayInfo(jsonObject.toJSONString());
    }

    private void requestAllInfo(String gatewayId) {
        getPushType(gatewayId);
        MQTTManager mqttManager = MainApplication.getApplication().getMqttManager();
        LocalInfo localInfo = MainApplication.getApplication().getLocalInfo();
        mqttManager.publishEncryptedMessage(
                MQTTCmdHelper.createGetAllDevices(gatewayId, localInfo.appID),
                mqttManager.MODE_CLOUD_ONLY);

        mqttManager.publishEncryptedMessage(
                MQTTCmdHelper.createGetAllRooms(gatewayId),
                MQTTManager.MODE_CLOUD_ONLY);

        mqttManager.publishEncryptedMessage(
                MQTTCmdHelper.createGetAllScenes(gatewayId),
                MQTTManager.MODE_CLOUD_ONLY);

        mqttManager.publishEncryptedMessage(
                MQTTCmdHelper.createGatewayInfo(gatewayId, 0, localInfo.appID, null),
                MQTTManager.MODE_CLOUD_ONLY);
    }

    private void getPushType(String gwId) {
        DeviceApiUnit deviceApiUnit = new DeviceApiUnit(AddDeviceActivity.this);
        deviceApiUnit.doGetIsPush(gwId, new DeviceApiUnit.DeviceApiCommonListener<DeviceIsPushBean>() {
            @Override
            public void onSuccess(DeviceIsPushBean bean) {
                if (StringUtil.equals(bean.isPush, "0")) {
                    preference.saveAlarmPush(false);
                } else if (StringUtil.equals(bean.isPush, "1")) {
                    preference.saveAlarmPush(true);
                }
            }

            @Override
            public void onFail(int code, String msg) {
            }
        });
    }

    private void showBindGwDialog() {
        WLDialog.Builder builder = new WLDialog.Builder(this);
        builder.setTitle(R.string.AddDevice_UnboundedGateway_Title)
                .setMessage(R.string.AddDevice_UnboundedGateway_Content)
                .setCancelOnTouchOutSide(false)
                .setPositiveButton(getResources().getString(R.string.Home_Scene_NoGateway_Tips_Ok))
                .setNegativeButton(getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        startActivity(new Intent(AddDeviceActivity.this, GatewayListActivity.class));
                    }

                    @Override
                    public void onClickNegative(View view) {

                    }
                });
        WLDialog dialog = builder.create();
        if (!dialog.isShowing()) {
            dialog.show();
        }
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
    public void onEvent(AddDeviceEvent event) {
        WLog.i(TAG, "AddDeviceEvent");
        try {
            JSONObject object = new JSONObject(event.jsonData);
            mWebViewClient.callHandler("newDataRefresh", object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        WLog.i(TAG, "待加网: +++++++++++++++++++++");
        if (event.device != null) {
            newDataRefresh(event.device.data);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(CMD517Event event) {
        if (event != null) {
            WLog.i(TAG, "背景音乐: " + event.data);
            newDataRefresh(event.data);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceInfoChangedEvent event) {
        WLog.i(TAG, "设置分区 & 名称: +++++++++++++++++++++");
        if (TextUtils.equals(setDeviceInfoId, event.deviceInfoBean.devID)) {
            setDeviceInfoCallback.callback(JSON.toJSONString(event.deviceInfoBean));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGatewayStateChanged(GatewayStateChangedEvent event) {
        if (event != null) {
            newDataRefresh(JSON.toJSONString(event.bean));
//            ToastUtil.show(JSON.toJSONString(event.bean));
        }
    }

    @Override
    protected String getUrl() {
        return HttpUrlKey.URL_ADD_DEVICE;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    Bundle bundle = data.getExtras();
                    String scanResult = bundle.getString("result");
                    if (scanResult.startsWith("ME") && scanResult.length() >= 11) {
                        scanResult = scanResult.substring(0, 11);
                    }
                    if (qrCodeCallback != null) {
                        // 将扫码结果传给前端
                        qrCodeCallback.callback(scanResult);
                    }
                    break;
                case 1:
                    String id = data.getStringExtra("id");
                    String type = data.getStringExtra("type");
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("type", type);
                        jsonObject.put("data", id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    qrScanCallback.callback(jsonObject);
                    break;
            }

        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack() && isUrlCanBack(mWebView.getUrl())) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 这些页面不可以返回上一级，直接退出
     */
    private static final String[] CANNOT_GOBACK_PAGES = {
            "addDeviceNew.html",//首页
            "addDevice_fail.html",//添加失败
            "addDevice_09Self_fail.html",
            "addDevice_43Self_fail.html",
            "addDevice_62_01_fail.html",
            "addDevice_80_G50_fail.html",
            "addDevice_Bf_fail.html",
            "addDevice_Bg_fail.html",
            "addDevice_Bq_fail.html",
            "DoorLock_Bcfail.html",
            "DoorLock_Bdfail.html"
    };

    private boolean isUrlCanBack(String url) {
        WLog.i("backUrl:" + url);
        try {
            Uri uri = Uri.parse(url);
            String lastPathSegment = uri.getLastPathSegment();
            WLog.i("uri.getLastPathSegment:" + lastPathSegment);
            if (lastPathSegment != null) {
                for (String page : CANNOT_GOBACK_PAGES) {
                    if (lastPathSegment.equals(page)) {
                        return false;
                    }
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }
}
