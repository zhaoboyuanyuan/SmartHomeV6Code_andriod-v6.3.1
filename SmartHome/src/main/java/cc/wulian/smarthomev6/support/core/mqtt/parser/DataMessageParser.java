package cc.wulian.smarthomev6.support.core.mqtt.parser;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.AreaManager;
import cc.wulian.smarthomev6.main.home.scene.SceneManager;
import cc.wulian.smarthomev6.main.home.widget.HomeWidgetManager;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.BgmCmd517Bean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.GatewayBackupRecoveryBean;
import cc.wulian.smarthomev6.support.core.cipher.CipherUtil;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceCache;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.device.Endpoint;
import cc.wulian.smarthomev6.support.core.device.GatewayManager;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTApiConfig;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.Device22KeyBean;
import cc.wulian.smarthomev6.support.core.mqtt.bean.Device22RemoteBean;
import cc.wulian.smarthomev6.support.core.mqtt.bean.DeviceForbiddenBean;
import cc.wulian.smarthomev6.support.core.mqtt.bean.DeviceInfoBean;
import cc.wulian.smarthomev6.support.core.mqtt.bean.GatewayConfigBean;
import cc.wulian.smarthomev6.support.core.mqtt.bean.GatewayEventReport;
import cc.wulian.smarthomev6.support.core.mqtt.bean.GatewayInfoBean;
import cc.wulian.smarthomev6.support.core.mqtt.bean.GatewayPasswordBean;
import cc.wulian.smarthomev6.support.core.mqtt.bean.IcamBindBean;
import cc.wulian.smarthomev6.support.core.mqtt.bean.MultiGatewayBean;
import cc.wulian.smarthomev6.support.core.mqtt.bean.ResponseBean;
import cc.wulian.smarthomev6.support.core.mqtt.bean.RoomBean;
import cc.wulian.smarthomev6.support.core.mqtt.bean.RoomListBean;
import cc.wulian.smarthomev6.support.core.mqtt.bean.SceneBean;
import cc.wulian.smarthomev6.support.core.mqtt.bean.SceneGroupListBean;
import cc.wulian.smarthomev6.support.core.mqtt.bean.SceneGroupSet;
import cc.wulian.smarthomev6.support.core.mqtt.bean.SceneListBean;
import cc.wulian.smarthomev6.support.core.mqtt.bean.SceneVolumeGroupSet;
import cc.wulian.smarthomev6.support.core.mqtt.bean.SignalStrengthBean;
import cc.wulian.smarthomev6.support.event.AddDeviceEvent;
import cc.wulian.smarthomev6.support.event.AppSetGwDebugEvent;
import cc.wulian.smarthomev6.support.event.AssignMasterGWEvent;
import cc.wulian.smarthomev6.support.event.CMD517Event;
import cc.wulian.smarthomev6.support.event.DeleteDeviceEvent;
import cc.wulian.smarthomev6.support.event.Device22ConfigEvent;
import cc.wulian.smarthomev6.support.event.DeviceControlEvent;
import cc.wulian.smarthomev6.support.event.DeviceForbiddenEvent;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.DreamFlowerBroadcastEvent;
import cc.wulian.smarthomev6.support.event.GatewayAccountEvent;
import cc.wulian.smarthomev6.support.event.GatewayBackupRecoveryEvent;
import cc.wulian.smarthomev6.support.event.GatewayConfigEvent;
import cc.wulian.smarthomev6.support.event.GatewayInfoEvent;
import cc.wulian.smarthomev6.support.event.GatewayPasswordChangedEvent;
import cc.wulian.smarthomev6.support.event.GetAutoProgramListEvent;
import cc.wulian.smarthomev6.support.event.GetAutoProgramTaskEvent;
import cc.wulian.smarthomev6.support.event.GetRoomListEvent;
import cc.wulian.smarthomev6.support.event.GetSceneBindingEvent;
import cc.wulian.smarthomev6.support.event.GetSceneGroupListEvent;
import cc.wulian.smarthomev6.support.event.GetSceneListEvent;
import cc.wulian.smarthomev6.support.event.MiniGatewayConfigEvent;
import cc.wulian.smarthomev6.support.event.MultiGatewayEvent;
import cc.wulian.smarthomev6.support.event.RoomInfoEvent;
import cc.wulian.smarthomev6.support.event.SceneGroupSetEvent;
import cc.wulian.smarthomev6.support.event.SceneInfoEvent;
import cc.wulian.smarthomev6.support.event.SceneVolumeGroupSetEvent;
import cc.wulian.smarthomev6.support.event.SetSceneBindingEvent;
import cc.wulian.smarthomev6.support.event.SignalStrengthEvent;
import cc.wulian.smarthomev6.support.event.TranslatorFuncEvent;
import cc.wulian.smarthomev6.support.event.VerifyGatewayPswEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.Trans2PinYin;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by zbl on 2017/3/3.
 * 设备数据消息解析处理
 */

public class DataMessageParser implements IMQTTMessageParser {

    private Context context;
    private String appId;

    public DataMessageParser(Context context, String appId) {
        this.context = context;
        this.appId = appId;
    }

    @Override
    public void parseMessage(int tag, String topic, String payload) {
        if (!topic.endsWith("/data")) {
            return;
        }
        try {
            ResponseBean bean = JSON.parseObject(payload, ResponseBean.class);
            if (bean.msgContent != null) {
                String msgContent = null;
                if (tag == MQTTManager.TAG_GATEWAY) {
                    msgContent = CipherUtil.decode(bean.msgContent, MQTTApiConfig.GW_AES_KEY);
                } else if (tag == MQTTManager.TAG_CLOUD) {
                    msgContent = CipherUtil.decode(bean.msgContent, ApiConstant.getAESKey());
                }
                WLog.i("MQTTUnit:Data:" + (tag == MQTTManager.TAG_GATEWAY ? "Gw" : "Cloud") + ":", msgContent);
                JSONObject jsonObject = JSON.parseObject(msgContent);
                String cmd = jsonObject.getString("cmd");

                String currentGatewayId = Preference.getPreferences().getCurrentGatewayID();
                String gwID = jsonObject.getString("gwID");
                if (gwID != null && !TextUtils.equals(gwID, currentGatewayId)) {
                    return;
                }
                if ("500".equals(cmd)) {//设备数据上报
                    onDeviceReport(msgContent);
                } else if ("502".equals(cmd)) {
                    onDeviceInfoChanged(msgContent);
                } else if ("503".equals(cmd)) {
                    onSceneInfoChanged(msgContent);
                } else if ("504".equals(cmd)) {
                    onGetSceneList(msgContent);
                } else if ("505".equals(cmd)) {
                    onRoomInfoChanged(msgContent);
                } else if ("506".equals(cmd)) {
                    onGetRoomList(msgContent);
                } else if ("507".equals(cmd)) {
                    onGetAutoProgramTask(msgContent);
                } else if ("508".equals(cmd)) {
                    onGetAutoProgramList(msgContent);
                } else if ("509".equals(cmd)) {
                    onGatewayPasswordChanged(msgContent);
                } else if ("511".equals(cmd)) {
                    onAddDevice(msgContent);
                } else if ("512".equals(cmd)) {
                    onGatewayInfoChanged(msgContent);
                } else if ("518".equals(cmd)) {
                    onTranslatorFunc(msgContent);
                } else if ("521".equals(cmd)) {
                    onGatewayConfigReport(msgContent);
                } else if ("520".equals(cmd)) {
                    onGatewayAccountReport(msgContent);
                } else if ("513".equals(cmd)) {
                    onSetSceneBindingEvent(msgContent);
                } else if ("514".equals(cmd)) {
                    onGetSceneBindingEvent(msgContent);
                } else if ("515".equals(cmd)) {
                    onMultiGatewayEvent(msgContent);
                } else if ("bindDevice".equals(cmd)) {
                    onIcamBindEvent(msgContent);
                } else if ("315".equals(cmd)) {
                    onAppSetGwDebug(msgContent);
                } else if ("330".equals(cmd)) {
                    onMiniGatewayWifiConfig(msgContent);
                } else if ("402".equals(cmd)) {
                    onDreamFlowerBroadcast(msgContent);
                } else if ("516".equals(cmd)) {
                    onDevice22ConfigReport(msgContent);
                } else if ("517".equals(cmd)) {
                    onDeviceDDConfigReport(msgContent);
                } else if ("501".equals(cmd)) {
                    onDeviceControlReport(msgContent);
                } else if ("524".equals(cmd)) {
                    onAssignnMasterGW(msgContent);
                } else if ("519".equals(cmd)) {
                    onVerifyGatewayPwdReport(msgContent);
                } else if ("527".equals(cmd)) {
                    onSetSceneGroup(msgContent);
                } else if ("528".equals(cmd)) {
                    onGetSceneGroupList(msgContent);
                } else if ("529".equals(cmd)) {
                    onVolumeSetSceneGroup(msgContent);
                } else if ("530".equals(cmd)) {
                    onGatewayEventReport(msgContent);
                } else if ("531".equals(cmd)) {
                    onDeviceForbidden(msgContent);
                } else if ("580".equals(cmd)) {
                    onSignalStrengthReport(msgContent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onDeviceReport(String msgContent) {
        final Device device = JSON.parseObject(msgContent, Device.class);
        device.data = msgContent;
        if (device.name != null) {
            device.name = DeviceInfoDictionary.getNameByDevice(device);
        }
        DeviceCache deviceCache = MainApplication.getApplication().getDeviceCache();
        if (device.mode == 3) {//删除设备
            deviceCache.remove(device);
        } else if (device.mode == 1) {
            deviceCache.add(device);
        } else if (device.mode == 0) {
            deviceCache.add(device);
        } else {
            deviceCache.add(device);
        }

        // 处理首页widget
        HomeWidgetManager.handleDeviceReport(device);

        EventBus.getDefault().post(new DeviceReportEvent(device));
    }

    private void onDeviceInfoChanged(String msgContent) {
        DeviceInfoBean deviceInfoBean = JSON.parseObject(msgContent, DeviceInfoBean.class);
        DeviceCache deviceCache = MainApplication.getApplication().getDeviceCache();
        if (deviceInfoBean.mode == 3) {//删除设备
            if (deviceInfoBean.retCode == 0) {
                deviceCache.remove(deviceInfoBean.devID);
            }
            onDeleteDevice(deviceInfoBean.retCode, deviceInfoBean.devID);
        } else if (deviceInfoBean.mode == 0) {//门磁等设备切换状态
            Device device = deviceCache.get(deviceInfoBean.devID);
            if (device != null && device.data != null) {
                try {
//                    JSONObject jsonData = JSON.parseObject(device.data);
//                    JSONArray endpoints = jsonData.getJSONArray("endpoints");
//                    for (int i = 0; i < endpoints.size(); i++) {
//                        JSONObject endpoint = endpoints.getJSONObject(i);
//                        int endpointNumber = endpoint.getInteger("endpointNumber");
//                        if (endpointNumber == deviceInfoBean.endpointNumber) {
//                            endpoint.put("endpointStatus", deviceInfoBean.endpointStatus);
//                            endpoints.remove(i);
//                            endpoints.add(endpoint);
//                            break;
//                        }
//                    }
//                    jsonData.put("endpoints", endpoints);
//                    device.data = jsonData.toJSONString();
//                    device.endpoints = JSON.parseArray(endpoints.toString(), Endpoint.class);
                    List<Endpoint> endpoints = device.endpoints;
                    for (int i = 0; i < endpoints.size(); i++) {
                        Endpoint endpoint = endpoints.get(i);
                        if (endpoint.endpointNumber == deviceInfoBean.endpointNumber) {
                            endpoint.endpointStatus = deviceInfoBean.endpointStatus;
                            break;
                        }
                    }
                    WLog.i("502 mergeResult:", JSON.toJSONString(device));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            Device device = deviceCache.get(deviceInfoBean.devID);
            if (device != null) {
                if (deviceInfoBean.name != null) {
                    device.name = deviceInfoBean.name;
                    device.sortStr = Trans2PinYin.trans2PinYin(DeviceInfoDictionary.getNameByDevice(device).trim()).toLowerCase();
                    try {
                        JSONObject jsonData = JSON.parseObject(device.data);
                        jsonData.put("name", deviceInfoBean.name);
                        device.data = jsonData.toJSONString();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (deviceInfoBean.roomID != null) {
                    device.roomID = deviceInfoBean.roomID;
                    try {
                        JSONObject jsonData = JSON.parseObject(device.data);
                        String roomName = MainApplication.getApplication().getRoomCache().getRoomName(device.roomID);
                        jsonData.put("roomID", deviceInfoBean.roomID);
                        jsonData.put("roomName", roomName);
                        device.data = jsonData.toJSONString();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (deviceInfoBean.endpointName != null) {
                    if (device.endpoints != null) {
                        try {
                            for (int i = 0; i < device.endpoints.size(); i++) {
                                Endpoint endpoint = device.endpoints.get(i);
                                int endpointNumber = endpoint.endpointNumber;
                                if (endpointNumber == deviceInfoBean.endpointNumber) {
                                    endpoint.endpointName = deviceInfoBean.endpointName;
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        // 处理首页widget
        HomeWidgetManager.handleDeviceInfoChanged(deviceInfoBean.mode, deviceInfoBean.devID, deviceInfoBean.type);

        EventBus.getDefault().post(new DeviceInfoChangedEvent(deviceInfoBean));
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

    private void onGetSceneList(String msgContent) {
        SceneListBean sceneListBean = JSON.parseObject(msgContent, SceneListBean.class);
        MainApplication.getApplication().getSceneCache().addAll(sceneListBean.data);
        MainApplication.getApplication().setSceneCached(true);
        SceneManager.saveSceneList();
        EventBus.getDefault().post(new GetSceneListEvent(sceneListBean));
    }

    private void onRoomInfoChanged(String msgContent) {
        RoomBean roomBean = JSON.parseObject(msgContent, RoomBean.class);
        if (roomBean.mode == 3) {
            MainApplication.getApplication().getRoomCache().remove(roomBean);
            // mode = 3   为删除 更新本地设备列表
            String roomID = roomBean.roomID;
            for (Device device : MainApplication.getApplication().getDeviceCache().getDevices()) {
                if (TextUtils.equals(roomID, device.roomID)) {
                    device.roomID = "";
                }
            }
        } else {
            MainApplication.getApplication().getRoomCache().add(roomBean);
        }
        AreaManager.saveRoomList();
        EventBus.getDefault().post(new RoomInfoEvent(roomBean));
        EventBus.getDefault().post(new DeviceReportEvent(null));
    }

    private void onGetRoomList(String msgContent) {
        RoomListBean roomListBean = JSON.parseObject(msgContent, RoomListBean.class);
        MainApplication.getApplication().getRoomCache().addAll(roomListBean.data);
        AreaManager.saveRoomList();
        EventBus.getDefault().post(new GetRoomListEvent(roomListBean));
    }

    private void onGetAutoProgramList(String msgContent) {
        EventBus.getDefault().post(new GetAutoProgramListEvent(msgContent));
    }

    private void onGetAutoProgramTask(String msgContent) {
        EventBus.getDefault().post(new GetAutoProgramTaskEvent(msgContent));
    }

    private void onGatewayPasswordChanged(String msgContent) {
        GatewayPasswordBean bean = JSON.parseObject(msgContent, GatewayPasswordBean.class);
        EventBus.getDefault().post(new GatewayPasswordChangedEvent(bean));
    }

    private void onAddDevice(String msgContent) {
        EventBus.getDefault().post(new AddDeviceEvent(msgContent));
    }

    private void onGatewayInfoChanged(String msgContent) {
        GatewayInfoBean currentGateway = null;
        JSONObject jsonObject = JSON.parseObject(msgContent);
        GatewayInfoBean gatewayInfoBean = JSON.parseObject(msgContent, GatewayInfoBean.class);
        if (!TextUtils.isEmpty(Preference.getPreferences().getCurrentGatewayInfo())) {
            currentGateway = JSON.parseObject(Preference.getPreferences().getCurrentGatewayInfo(), GatewayInfoBean.class);
            if (!StringUtil.isNullOrEmpty(currentGateway.gwType)) {
                gatewayInfoBean.gwType = currentGateway.gwType;
                jsonObject.put("gwType", currentGateway.gwType);
            }
            gatewayInfoBean.gwName = DeviceInfoDictionary.getNameByTypeAndName(gatewayInfoBean.gwType, gatewayInfoBean.gwName);
            jsonObject.put("gwName", gatewayInfoBean.gwName);
            if (TextUtils.isEmpty(gatewayInfoBean.gwName) && !TextUtils.isEmpty(currentGateway.gwName)) {
                gatewayInfoBean.gwName = currentGateway.gwName;
                jsonObject.put("gwName", currentGateway.gwName);
            }

            if (!TextUtils.isEmpty(gatewayInfoBean.master)) {
                jsonObject.put("hostFlag", gatewayInfoBean.master);
            }
        }

        GatewayManager gatewayManager = new GatewayManager();
        gatewayManager.saveGatewayInfo(gatewayInfoBean);
        new DeviceApiUnit(MainApplication.getApplication()).checkGatewaySoftwareUpdate();//收到512，网关版本信息刷新，检查版本是否更新
        Preference.getPreferences().saveCurrentGatewayInfo(jsonObject.toString());
        EventBus.getDefault().post(new GatewayInfoEvent(gatewayInfoBean));
    }

    private void onGatewayConfigReport(String msgContent) {
        GatewayConfigBean bean = JSON.parseObject(msgContent, GatewayConfigBean.class);
        byte[] decodeMsgContent = Base64.decode(bean.v, Base64.NO_WRAP);
        bean.v = new String(decodeMsgContent);
//        MainApplication.getApplication().getGatewayConfigCache().update(bean);
        MainApplication.getApplication().getGatewayConfigCache().put(bean);
        EventBus.getDefault().post(new GatewayConfigEvent(bean));
    }

    private void onGatewayAccountReport(String msgContent) {
        EventBus.getDefault().post(new GatewayAccountEvent(msgContent));
    }

    private void onSetSceneBindingEvent(String msgContent) {
        EventBus.getDefault().post(new SetSceneBindingEvent(msgContent));
    }

    private void onGetSceneBindingEvent(String msgContent) {
        EventBus.getDefault().post(new GetSceneBindingEvent(msgContent));
    }

    private void onMultiGatewayEvent(String msgContent) {
        MultiGatewayBean bean = JSON.parseObject(msgContent, MultiGatewayBean.class);
        EventBus.getDefault().post(new MultiGatewayEvent(bean, msgContent));
    }

    private void onIcamBindEvent(String msgContent) {
        IcamBindBean bean = new IcamBindBean(msgContent);
        EventBus.getDefault().post(bean);
    }

    private void onAppSetGwDebug(String msgContent) {
        EventBus.getDefault().post(new AppSetGwDebugEvent(msgContent));
    }

    private void onMiniGatewayWifiConfig(String msgContent) {
        EventBus.getDefault().post(new MiniGatewayConfigEvent(msgContent));
    }

    private void onDreamFlowerBroadcast(String msgContent) {
        EventBus.getDefault().post(new DreamFlowerBroadcastEvent(msgContent));
    }

    private void onDevice22ConfigReport(String msgContent) {
        int mode = 1;
        int operType = 1;
        String devId = null;
        String data = null;
        try {
            JSONObject object = JSONObject.parseObject(msgContent);
            mode = object.getInteger("mode").intValue();
            operType = object.getInteger("operType").intValue();
            devId = object.getString("devID");
            data = object.getString("data");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mode == 1) {
            Device22RemoteBean remoteBean = JSON.parseObject(msgContent, Device22RemoteBean.class);
            MainApplication.getApplication().getDevice22KeyCodeCache().putRemote(remoteBean);
        } else if (mode == 2 || mode == 3) {
            Device22KeyBean keyBean = JSON.parseObject(msgContent, Device22KeyBean.class);
            MainApplication.getApplication().getDevice22KeyCodeCache().putKeyCode(keyBean);
        }
        EventBus.getDefault().post(new Device22ConfigEvent(devId, mode, operType, data));
    }

    private void onDeviceDDConfigReport(String msgContent) {
        BgmCmd517Bean bean = JSON.parseObject(msgContent, BgmCmd517Bean.class);
        bean.data = msgContent;
        EventBus.getDefault().post(new CMD517Event(msgContent, bean));
    }

    private void onDeviceControlReport(String msgContent) {
        EventBus.getDefault().post(new DeviceControlEvent(msgContent));
    }

    private void onDeleteDevice(int retCode, String devID) {
        EventBus.getDefault().post(new DeleteDeviceEvent(retCode, devID));
    }

    private void onTranslatorFunc(String msgContent) {
        EventBus.getDefault().post(new TranslatorFuncEvent(msgContent));
    }

    private void onAssignnMasterGW(String msgContent) {
        EventBus.getDefault().post(new AssignMasterGWEvent(msgContent));
    }

    private void onVerifyGatewayPwdReport(String msgContent) {
        EventBus.getDefault().post(new VerifyGatewayPswEvent(msgContent));
    }

    private void onGetSceneGroupList(String msgContent) {
        SceneGroupListBean sceneGroupListBean = JSON.parseObject(msgContent, SceneGroupListBean.class);
        MainApplication.getApplication().getSceneCache().addAllGroups(sceneGroupListBean.getData());
        EventBus.getDefault().post(new GetSceneGroupListEvent(sceneGroupListBean));
    }

    private void onSetSceneGroup(String msgContent) {
        SceneGroupSet sceneGroupSet = JSON.parseObject(msgContent, SceneGroupSet.class);
        if (sceneGroupSet.getMode() == 2) {//删除分组
            MainApplication.getApplication().getSceneCache().removeGroup(sceneGroupSet.getGroupID());
        } else {
            //编辑分组、新增分组
            MainApplication.getApplication().getSceneCache().addGroup(sceneGroupSet);
        }
        EventBus.getDefault().post(new SceneGroupSetEvent(sceneGroupSet));
    }

    private void onVolumeSetSceneGroup(String msgContent) {
        SceneVolumeGroupSet volumeGroupSet = JSON.parseObject(msgContent, SceneVolumeGroupSet.class);
        EventBus.getDefault().post(new SceneVolumeGroupSetEvent(volumeGroupSet));
    }

    private void onGatewayEventReport(String msgContent) {
        GatewayEventReport gatewayEventReport = JSON.parseObject(msgContent, GatewayEventReport.class);
        if (gatewayEventReport != null) {
            if (gatewayEventReport.getEvent() == 3) {
                ToastUtil.show(R.string.Toast_Litegateway_full);
            }
        }

    }

    private void onDeviceForbidden(String msgContent) {
        MainApplication.getApplication().forbiddenDevice = msgContent;
        DeviceForbiddenBean deviceForbiddenBean = JSON.parseObject(msgContent, DeviceForbiddenBean.class);
        EventBus.getDefault().post(new DeviceForbiddenEvent(deviceForbiddenBean));
    }

    private void onSignalStrengthReport(String msgContent) {
        SignalStrengthBean signalStrengthBean = JSON.parseObject(msgContent, SignalStrengthBean.class);
        EventBus.getDefault().post(new SignalStrengthEvent(signalStrengthBean));
    }
}
