package cc.wulian.smarthomev6.support.core.mqtt.parser;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;

import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesRingPicBean;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.CameraHouseKeeperBean;
import cc.wulian.smarthomev6.support.core.mqtt.bean.CameraOtherBindBean;
import cc.wulian.smarthomev6.support.core.mqtt.bean.CateyeDoorbellBean;
import cc.wulian.smarthomev6.support.core.mqtt.bean.DoorbellBean;
import cc.wulian.smarthomev6.support.core.mqtt.bean.DoorbellBtnBean;
import cc.wulian.smarthomev6.support.core.mqtt.bean.EntranceGuardCallBean;
import cc.wulian.smarthomev6.support.core.mqtt.bean.PushBean;
import cc.wulian.smarthomev6.support.event.AlarmMediaPushEvent;
import cc.wulian.smarthomev6.support.event.AlarmPushEvent;
import cc.wulian.smarthomev6.support.event.CameraBindEvent;
import cc.wulian.smarthomev6.support.event.CameraHouseKeeperEvent;
import cc.wulian.smarthomev6.support.event.CateyeDoorbellEvent;
import cc.wulian.smarthomev6.support.event.CustomerServiceEvent;
import cc.wulian.smarthomev6.support.event.DoorbellButtonEvent;
import cc.wulian.smarthomev6.support.event.DoorbellEvent;
import cc.wulian.smarthomev6.support.event.EntranceGuardCallEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.CameraUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by zbl on 2017/5/22.
 * 推送消息解析处理
 */

public class PushMessageParser implements IMQTTMessageParser {

    @Override
    public void parseMessage(int tag, String topic, String payload) {
        if (!topic.endsWith("push/alarm")) {
            return;
        }
        if (tag == MQTTManager.TAG_CLOUD) {

            try {
                PushBean pushBean = JSON.parseObject(payload, PushBean.class);
                if (!TextUtils.isEmpty(pushBean.alert)) {
                    WLog.i("MQTTUnit:Push:" + (tag == MQTTManager.TAG_GATEWAY ? "Gw" : "Cloud") + ":", payload);
                    if (!TextUtils.isEmpty(pushBean.extras)) {
                        JSONObject jsonObject = JSON.parseObject(pushBean.extras);

                        String currentGatewayId = Preference.getPreferences().getCurrentGatewayID();
                        String gwID = jsonObject.getString("gwID");
                        // 过滤非本网关数据
                        if (gwID != null && !TextUtils.equals(gwID, currentGatewayId)) {
                            String type = jsonObject.getString("type");
                            if (TextUtils.isEmpty(type) || !DeviceInfoDictionary.supportParseCmdData(type)) {
                                return;
                            }
                        }

                        String messagecode = jsonObject.getString("messageCode");
                        if ("0104021".equals(messagecode)) {//门铃来电
                            onCateyeDoorbellCall(pushBean.extras);
                            sendAlarmPushEvent(messagecode, pushBean.alert);
                        } else if ("0204021".equals(messagecode)) {//移康猫眼门铃来电
                            onEquesDoorbellCall(pushBean.extras);
                        } else if ("0103061".equals(messagecode)) {//Bc、Bn锁门铃来电
                            OnBcDoorbellCall(pushBean.extras);
                            sendAlarmPushEvent(messagecode, pushBean.alert);
                        } else if ("onlineServiceMsg".equals(messagecode)) { // 在线客服推送, 此处建议云用Notice主题, 但是云说实现不了
                            // TODO: 2017/9/1 等待后续操作与跳转的需求
                            EventBus.getDefault().post(new CustomerServiceEvent(pushBean.alert));
                        } else if ("0104042".equals(messagecode)) {//绑定用户踢掉提示
                            if (CameraUtil.isAppIsInBackground(MainApplication.getApplication().getApplicationContext())) {
                                sendAlarmPushEvent(messagecode, pushBean.alert);
                            }
                            onCameraOtherBindCall(pushBean.extras);
                        } else if ("0103081".equals(messagecode)) {//门铃按钮联动4G企鹅机
                            sendAlarmPushEvent(messagecode, pushBean.alert);
                            onDoorbellBtnCall(pushBean.extras);
                        } else if ("0104051".equals(messagecode)) {//摄像机做管家执行条件
                            sendAlarmPushEvent(messagecode, pushBean.alert);
                            onHousekeeperCameraCall(pushBean.extras);
                        } else if ("0109061".equals(messagecode)) {//安全狗扫描完毕
                            String deviceId = jsonObject.getString("deviceId");
                            String deviceName = MainApplication.getApplication().getDeviceCache().get(deviceId).getName();
                            sendAlarmPushEvent(messagecode, deviceName + pushBean.alert, deviceId);
                        } else if ("0404012".equals(messagecode)) {//罗格朗门禁主机来电
                            onEntranceGuardCall(pushBean.extras);
                            sendAlarmPushEvent(messagecode, pushBean.alert);
                        } else {
                            //弹出带媒体图片的报警
                            JSONObject extData1 = jsonObject.getJSONObject("extData1");
                            if (extData1 != null) {
                                String picSigned = extData1.getString("picSigned");
                                if (!TextUtils.isEmpty(picSigned)) {
                                    String deviceId = jsonObject.getString("devID");
                                    String type = jsonObject.getString("type");
                                    sendAlarmMediaPushEvent(messagecode, pushBean.alert, deviceId, type, picSigned);
                                    return;
                                }
                            }
                            //弹出其它报警
                            sendAlarmPushEvent(messagecode, pushBean.alert);
                        }
                    } else {
                        sendAlarmPushEvent(pushBean.alert);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendAlarmPushEvent(String text) {
        EventBus.getDefault().post(new AlarmPushEvent("", text));
    }

    private void sendAlarmPushEvent(String messageCode, String text) {
        EventBus.getDefault().post(new AlarmPushEvent(messageCode, text));
    }

    private void sendAlarmPushEvent(String messageCode, String text, String deviceId) {
        EventBus.getDefault().post(new AlarmPushEvent(messageCode, text, deviceId));
    }

    private void sendAlarmMediaPushEvent(String messageCode, String text, String deviceId, String type, String picUrl) {
        EventBus.getDefault().post(new AlarmMediaPushEvent(messageCode, text, deviceId, type, picUrl));
    }

    private void onCateyeDoorbellCall(String extras) {
        CateyeDoorbellBean bean = JSON.parseObject(extras, CateyeDoorbellBean.class);
        EventBus.getDefault().post(new CateyeDoorbellEvent(bean));
    }

    private void onEquesDoorbellCall(String extras) {
        EquesRingPicBean equesRingPicBean = JSON.parseObject(extras, EquesRingPicBean.class);
        EventBus.getDefault().post(equesRingPicBean);
    }

    private void OnBcDoorbellCall(String extras) {
        DoorbellBean bean = JSON.parseObject(extras, DoorbellBean.class);
        DoorbellEvent doorbellEvent = new DoorbellEvent(bean);
        doorbellEvent.data = extras;
        EventBus.getDefault().post(doorbellEvent);
    }

    private void onCameraOtherBindCall(String extras) {
        CameraOtherBindBean bean = JSON.parseObject(extras, CameraOtherBindBean.class);
        CameraBindEvent event = new CameraBindEvent(bean);
        event.data = extras;
        EventBus.getDefault().post(event);
    }

    private void onDoorbellBtnCall(String extras) {
        DoorbellBtnBean bean = JSON.parseObject(extras, DoorbellBtnBean.class);
        EventBus.getDefault().post(new DoorbellButtonEvent(bean));
    }

    private void onHousekeeperCameraCall(String extras) {
        CameraHouseKeeperBean bean = JSON.parseObject(extras, CameraHouseKeeperBean.class);
        EventBus.getDefault().post(new CameraHouseKeeperEvent(bean));
    }

    private void onEntranceGuardCall(String extras) {
        EntranceGuardCallBean bean = JSON.parseObject(extras, EntranceGuardCallBean.class);
        EventBus.getDefault().post(new EntranceGuardCallEvent(bean));
    }
}
