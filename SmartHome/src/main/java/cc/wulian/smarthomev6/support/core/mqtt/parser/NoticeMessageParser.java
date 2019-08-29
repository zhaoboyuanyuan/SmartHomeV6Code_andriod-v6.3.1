package cc.wulian.smarthomev6.support.core.mqtt.parser;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.LocalInfo;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.home.scene.SceneManager;
import cc.wulian.smarthomev6.main.home.widget.HomeWidgetManager;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.apiunit.AuthApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.GatewayInfoBean;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GetSceneListEvent;
import cc.wulian.smarthomev6.support.event.ShareDeviceStatusChangedEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;
import cc.wulian.wrecord.WRecord;

/**
 * Created by zbl on 2017/3/3.
 * 云端设备通知消息解析处理
 */

public class NoticeMessageParser implements IMQTTMessageParser {

    private Context context;
    private String appId;
    private Handler handler;

    public NoticeMessageParser(Context context, String appId) {
        this.context = context;
        this.appId = appId;
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void parseMessage(int tag, String topic, String payload) {
        if (!topic.endsWith("/notice")) {
            return;
        }
        try {
            WLog.i("MQTTUnit:Notice:" + (tag == MQTTManager.TAG_GATEWAY ? "Gw" : "Cloud") + ":", payload);
            JSONObject jsonObject = JSON.parseObject(payload);
            String cmd = jsonObject.getString("cmd");
            //处理账号在其它手机上登录
            if ("login".equals(cmd)) {
                String uId = jsonObject.getString("uId");
                if (TextUtils.equals(ApiConstant.getUserID(), uId)) {
                    onAccountLoginOtherPhone(null);
                }
            } else if ("unbind".equals(cmd)) {//{ “cmd”：“unbind”，“deviceId”：""，“time”：“”}
                String deviceId = jsonObject.getString("deviceId");
                onDeviceUnbind(deviceId);
            } else if ("updateGrantDevice".equals(cmd)) {//{ “cmd”：“unbind”，“deviceId”：""，“time”：“”}
                String deviceId = jsonObject.getString("deviceId");
                String relationFlag = jsonObject.getString("relationFlag");
                onUpdateGrantDevice(deviceId, relationFlag);
            } else if ("setRecordCount".equals(cmd)) {
                // 设置打点上传数量
                int count = jsonObject.getInteger("RecordCount");
                Preference.getPreferences().saveRecordCount(count);
                WRecord.setRecordCount(Preference.getPreferences().getRecordCount());
            } else if ("m-point".equals(cmd)) {
                String msg = jsonObject.getString("msg");
                onAddPoint(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onAccountLoginOtherPhone(String msgContent) {
        //登出操作
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                ToastUtil.show(R.string.Login_On_Other_Phone);
                MainApplication.getApplication().logout(true);
            }
        });
    }

    //网关被解绑
    private void onDeviceUnbind(final String deviceId) {
        final Preference preference = Preference.getPreferences();
        String currentGatewayId = preference.getCurrentGatewayID();
        if (TextUtils.equals(currentGatewayId, deviceId)) {//网关被解绑
            boolean isShared = preference.isAuthGateway();
            MainApplication.getApplication().getDeviceCache().clearDatabaseCache(deviceId);
            MainApplication.getApplication().clearCurrentGateway();
            if (isShared) {
                EventBus.getDefault().post(new ShareDeviceStatusChangedEvent(0));
            } else {
                if (TextUtils.equals(deviceId, "000000000000")) {
                    //体验网关自动解绑
                    EventBus.getDefault().post(new ShareDeviceStatusChangedEvent(4));
                } else {
                    EventBus.getDefault().post(new ShareDeviceStatusChangedEvent(3));
                }
            }

        } else {//设备被解绑
            Device device = MainApplication.getApplication().getDeviceCache().get(deviceId);
            if (device != null) {
                // 删除这个设备的widget
                HomeWidgetManager.deleteWidget(deviceId);
                MainApplication.getApplication().getDeviceCache().remove(device);
                device.mode = 3;
                EventBus.getDefault().post(new DeviceReportEvent(device));
            }
        }
    }

    //网关下子设备权限变动
    private void onUpdateGrantDevice(String deviceId, String relationFlag) {
        final Preference preference = Preference.getPreferences();
        final String currentGatewayId = preference.getCurrentGatewayID();
        if (TextUtils.equals(currentGatewayId, deviceId)) {//是当前网关
            Preference.getPreferences().saveGatewayRelationFlag(relationFlag);
            if ("2".equals(relationFlag)) {//子设备分享变为网关分享，直接重新请求设备列表
                MainApplication.getApplication().getMqttManager().sendGetDevicesInfoCmd(currentGatewayId, MQTTManager.MODE_CLOUD_ONLY);
                EventBus.getDefault().post(new ShareDeviceStatusChangedEvent(2));
            } else {//子设备分享状态变化
                AuthApiUnit authApiUnit = new AuthApiUnit(context);
                authApiUnit.doGetAuthChildDevices(deviceId, ApiConstant.getUserID(), new AuthApiUnit.AuthApiCommonListener<List<String>>() {
                    @Override
                    public void onSuccess(List<String> bean) {
                        if (bean != null) {
                            HashSet<String> grandDevicesSet = new HashSet<>();
                            grandDevicesSet.addAll(bean);

                            List<Device> devices = MainApplication.getApplication().getDeviceCache().getZigBeeDevices();
                            for (Device device : devices) {
                                if (!grandDevicesSet.contains(device.devID)) {
                                    MainApplication.getApplication().getDeviceCache().remove(device);
                                    device.mode = 3;
                                    // 处理首页widget
                                    HomeWidgetManager.handleDeviceReport(device);
                                    EventBus.getDefault().post(new DeviceReportEvent(device));
                                }
                            }
                            LocalInfo localInfo = MainApplication.getApplication().getLocalInfo();
                            MainApplication.getApplication().getMqttManager().publishEncryptedMessage(
                                    MQTTCmdHelper.createGetAllDevices(currentGatewayId, localInfo.appID),
                                    MQTTManager.MODE_CLOUD_ONLY);
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {

                    }
                });
                MainApplication.getApplication().getSceneCache().clear();
                SceneManager.saveSceneList();
                EventBus.getDefault().post(new GetSceneListEvent(null));
                EventBus.getDefault().post(new ShareDeviceStatusChangedEvent(1));
            }
        }
    }

    private void onAddPoint(final String msgContent) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ToastUtil.singleCenter(msgContent);
            }
        }, 2000);
    }
}
