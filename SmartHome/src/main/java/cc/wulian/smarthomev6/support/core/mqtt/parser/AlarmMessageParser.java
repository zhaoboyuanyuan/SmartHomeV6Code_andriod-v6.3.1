package cc.wulian.smarthomev6.support.core.mqtt.parser;

import android.text.TextUtils;

import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.home.scene.SceneManager;
import cc.wulian.smarthomev6.main.home.widget.HomeWidgetManager;
import cc.wulian.smarthomev6.support.core.apiunit.bean.GatewayBackupRecoveryBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.MessageBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceCache;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.bean.DoorbellBean;
import cc.wulian.smarthomev6.support.core.mqtt.bean.ResponseBean;
import cc.wulian.smarthomev6.support.core.mqtt.bean.SceneBean;
import cc.wulian.smarthomev6.support.event.ChangeLockNameEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.DoorbellEvent;
import cc.wulian.smarthomev6.support.event.GatewayBackupRecoveryEvent;
import cc.wulian.smarthomev6.support.event.GetAutoProgramTaskEvent;
import cc.wulian.smarthomev6.support.event.SceneInfoEvent;
import cc.wulian.smarthomev6.support.tools.MessageTool;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.WLog;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTApiConfig;
import cc.wulian.smarthomev6.support.core.cipher.CipherUtil;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.AlarmPushEvent;

/**
 * Created by zbl on 2017/3/3.
 * 报警消息解析处理
 */

public class AlarmMessageParser implements IMQTTMessageParser {

    @Override
    public void parseMessage(int tag, String topic, String payload) {
        if (!topic.endsWith("alarm")) {
            return;
        }
        if (tag == MQTTManager.TAG_CLOUD) {
            try {
                ResponseBean responseBean = JSON.parseObject(payload, ResponseBean.class);
                if (responseBean.msgContent != null) {
                    String msgContent = CipherUtil.decode(responseBean.msgContent, ApiConstant.getAESKey());
                    WLog.i("MQTTUnit:Alarm:" + (tag == MQTTManager.TAG_GATEWAY ? "Gw" : "Cloud") + ":", msgContent);
                    JSONObject jsonObject = JSON.parseObject(msgContent);
                    String currentGatewayId = Preference.getPreferences().getCurrentGatewayID();
                    String gwID = jsonObject.getString("gwID");
                    // 过滤非本网关数据
                    if (gwID != null && !TextUtils.equals(gwID, currentGatewayId)) {
                        String type = jsonObject.getString("type");
                        if (TextUtils.isEmpty(type) || !DeviceInfoDictionary.supportParseCmdData(type)) {
                            return;
                        }
                    }
                    String cmd = jsonObject.getString("cmd");
                    MessageBean.RecordListBean bean = JSON.parseObject(msgContent, MessageBean.RecordListBean.class);
                    // 只有是报警, 数量才会 +1
                    if (!TextUtils.isEmpty(bean.messageCode) && bean.messageCode.endsWith("1")) {
                        MainApplication.getApplication().getAlarmCountCache().alarmCountAtom(bean.devID);
                    }
                    if ("500".equals(cmd)) {//FIXME 摄像机类的设备处理
                        onDeviceReport(msgContent);
                    } else if ("525".equals(cmd)) {
                        onGatewayBackupRecovery(msgContent);
                    } else if ("503".equals(cmd)) {
                        onSceneInfoChanged(msgContent);
                    } else if ("507".equals(cmd)) {
                        onGetAutoProgramTask(msgContent);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (Preference.ENTER_TYPE_GW.equals(Preference.getPreferences().getUserEnterType())) {
            //在网关登录的情况下才接收网关推送的报警
            try {
                ResponseBean responseBean = JSON.parseObject(payload, ResponseBean.class);
                if (responseBean.msgContent != null) {
                    String msgContent = null;
                    if (tag == MQTTManager.TAG_GATEWAY) {
                        msgContent = CipherUtil.decode(responseBean.msgContent, MQTTApiConfig.GW_AES_KEY);
                    } else if (tag == MQTTManager.TAG_CLOUD) {
                        msgContent = CipherUtil.decode(responseBean.msgContent, ApiConstant.getAESKey());
                    }
                    WLog.i("MQTTUnit:Alarm:" + (tag == MQTTManager.TAG_GATEWAY ? "Gw" : "Cloud") + ":", msgContent);

                    JSONObject jsonObject = JSON.parseObject(msgContent);
//                    String currentGatewayId = Preference.getPreferences().getCurrentGatewayID();
//                    String gwID = jsonObject.getString("gwID");
//                    // 过滤非本网关数据
//                    if (gwID != null && !TextUtils.equals(gwID, currentGatewayId)) {
//                        return;
//                    }

                    MessageBean.RecordListBean bean = JSON.parseObject(msgContent, MessageBean.RecordListBean.class);
//                    String message = AlarmTool.getAlarmMessage(bean);
                    if (TextUtils.equals(bean.messageCode, "0103061")) {
                        WLog.i("MQTTUnit:Alarm:", "TAG_GATEWAY_ALARM: " + System.currentTimeMillis());
                        onDoorbellCall(msgContent);
                    }

                    String message = MessageTool.getMessage(bean);
                    EventBus.getDefault().post(new AlarmPushEvent(bean.messageCode, message));

                    // 只有是报警, 数量才会 +1
                    if (bean.messageCode.endsWith("1")) {
                        MainApplication.getApplication().getAlarmCountCache().alarmCountAtom(bean.devID);
                    }

                    String cmd = jsonObject.getString("cmd");

                    if ("500".equals(cmd)) {
                        onDeviceReport(msgContent);
                    } else if ("503".equals(cmd)) {
                        onSceneInfoChanged(msgContent);
                    } else if ("507".equals(cmd)) {
                        onGetAutoProgramTask(msgContent);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void onDeviceReport(String msgContent) {
        Device device = JSON.parseObject(msgContent, Device.class);
        device.data = msgContent;
        device.name = DeviceInfoDictionary.getNameByDevice(device);
        DeviceCache deviceCache = MainApplication.getApplication().getDeviceCache();
        if (device.mode == 3) {//删除设备
            deviceCache.remove(device);
        } else if (device.mode == 1) {
            deviceCache.add(device);
            // 门锁上线时, 获取一下设备配置
            if ("70".equals(device.type)) {
                MainApplication.getApplication().getMqttManager().publishEncryptedMessage(
                        MQTTCmdHelper.createGatewayConfig(
                                Preference.getPreferences().getCurrentGatewayID(),
                                3,
                                MainApplication.getApplication().getLocalInfo().appID,
                                device.devID,
                                null,
                                null,
                                null
                        ), MQTTManager.MODE_GATEWAY_FIRST
                );
            }
        } else if (device.mode == 0) {
            deviceCache.add(device);
            if ("70".equals(device.type)) {
                try {
                    org.json.JSONObject object = new org.json.JSONObject(device.data);
                    org.json.JSONArray endpoints = object.getJSONArray("endpoints");
                    org.json.JSONArray clusters = ((org.json.JSONObject) endpoints.get(0)).getJSONArray("clusters");
                    org.json.JSONArray attributes = ((org.json.JSONObject) clusters.get(0)).getJSONArray("attributes");
                    int attributeId = ((org.json.JSONObject) attributes.get(0)).getInt("attributeId");

                    if (attributeId == 0x8002) {
                        String extData = object.optString("extData");
                        if (TextUtils.isEmpty(extData)) {
                            EventBus.getDefault().post(new ChangeLockNameEvent(device.devID));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            deviceCache.add(device);
        }

        // 处理首页widget
        HomeWidgetManager.handleDeviceReport(device);

        EventBus.getDefault().post(new DeviceReportEvent(DeviceReportEvent.DEVICE_ALARM, device));
    }

    private void onDoorbellCall(String extras) {
        DoorbellBean bean = JSON.parseObject(extras, DoorbellBean.class);
        DoorbellEvent doorbellEvent = new DoorbellEvent(bean);
        doorbellEvent.data = extras;
        EventBus.getDefault().post(doorbellEvent);
    }

    private void onGatewayBackupRecovery(String msgContent) {
        GatewayBackupRecoveryBean bean = JSON.parseObject(msgContent, GatewayBackupRecoveryBean.class);
        EventBus.getDefault().post(new GatewayBackupRecoveryEvent(bean));
    }

    private void onSceneInfoChanged(String msgContent) {
        SceneBean sceneBean = JSON.parseObject(msgContent, SceneBean.class);
        sceneBean.data = msgContent;
        if (sceneBean.mode == 3) {
            MainApplication.getApplication().getSceneCache().remove(sceneBean);
        } else {
            if ("2".equals(sceneBean.status)) {
                for (SceneBean b : MainApplication.getApplication().getSceneCache().getScenes()) {
                    if (b.status.equals("2")) {
                        b.status = "1";
                    }
                }
            }
            MainApplication.getApplication().getSceneCache().add(sceneBean);
            MainApplication.getApplication().setSceneCached(true);
        }
        SceneManager.saveSceneList();
        EventBus.getDefault().post(new SceneInfoEvent(sceneBean));
    }

    private void onGetAutoProgramTask(String msgContent) {
        EventBus.getDefault().post(new GetAutoProgramTaskEvent(msgContent));
    }
}
