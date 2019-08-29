package cc.wulian.smarthomev6.support.core.apiunit;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.eques.icvss.api.ICVSSListener;
import com.eques.icvss.api.ICVSSUserInstance;
import com.eques.icvss.utils.Method;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.eques.bean.BatteryStatusBean;
import cc.wulian.smarthomev6.main.device.eques.bean.BdylistBean;
import cc.wulian.smarthomev6.main.device.eques.bean.DevstBean;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesAlarmBean;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesBean;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesCallStatusBean;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesDeleteBean;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesInfoBean;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesPirInfoBean;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesPirSwitchBean;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesRebootBean;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesSetNickBean;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesSetPirBean;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesUpdateBean;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesVisitorBean;
import cc.wulian.smarthomev6.main.device.eques.bean.LightEnableBean;
import cc.wulian.smarthomev6.main.device.eques.bean.LowPowerAlarmBean;
import cc.wulian.smarthomev6.main.device.eques.bean.OnAddbdyReqBean;
import cc.wulian.smarthomev6.main.device.eques.bean.OnAddbdyResultBean;
import cc.wulian.smarthomev6.main.device.eques.bean.OnbdyRemovedBean;
import cc.wulian.smarthomev6.main.device.eques.bean.VideoPlayingBean;
import cc.wulian.smarthomev6.main.home.widget.HomeWidgetManager;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.event.AlarmPushEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.utils.FileUtil;
import cc.wulian.smarthomev6.support.utils.WLog;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者: chao
 * 时间: 2017/6/1
 * 描述: 移康sdk调用
 * 联系方式: 805901025@qq.com
 */

public class EquesApiUnit implements ICVSSListener {
    public static final String TAG = "EquesApiUnit";
    public ICVSSUserInstance icvss;
    public Context context;
    private DeviceApiUnit deviceApiUnit;

    public interface EquesFileDownloadListener {
        void onSuccess(File file);

        void onFail(int code, String msg);
    }

    public EquesApiUnit(Context context) {
        this.context = context;
        icvss = ICVSSUserModule.getInstance(this).getIcvss();
        deviceApiUnit = new DeviceApiUnit(context);
    }

    public ICVSSUserInstance getIcvss() {
        return icvss;
    }

    @Override
    public void onDisconnect(int code) {
        WLog.i(TAG, " onDisconnect..Code: ", code);
        icvss.equesLogin(context, ApiConstant.EQUES_URL, ApiConstant.getUserID(), ApiConstant.EQUES_APPKEY);
    }

    @Override
    public void onPingPong(int code) {
        WLog.i(TAG, " onPingPong..Code: ", code);
    }

    @Override
    public void onMeaasgeResponse(JSONObject jsonObject) {
        String method = jsonObject.optString(Method.METHOD);
        WLog.i(TAG, "onMeaasgeResponse: ", jsonObject);
        switch (method) {
            case Method.METHOD_EQUES_SDK_LOGIN://登录

                String code = jsonObject.optString("code");
                if (TextUtils.equals(code, "4000")) {
                    icvss.equesGetDeviceList();
                }

                break;
            case Method.METHOD_ONADDBDY_REQ://绑定设备确认

                OnAddbdyReqBean onAddbdyReqBean = JSON.parseObject(jsonObject.toString(), OnAddbdyReqBean.class);
                if (onAddbdyReqBean.extra == null) {
                    icvss.equesAckAddResponse(onAddbdyReqBean.reqId, 1);
                } else {
                    EventBus.getDefault().post(onAddbdyReqBean);
                }

                break;
            case Method.METHOD_ONADDBDY_RESULT:// 绑定设备

                OnAddbdyResultBean addbdyResultBean = JSON.parseObject(jsonObject.toString(), OnAddbdyResultBean.class);
                if (TextUtils.equals(addbdyResultBean.code, "4000")) {
                    updateToCloud(addbdyResultBean.added_bdy.bid);
                }
                EventBus.getDefault().post(addbdyResultBean);
                icvss.equesGetDeviceList();

                break;
            case Method.METHOD_RMBDY_RESULT:// 删除设备

                EquesDeleteBean equesDeleteBean = JSON.parseObject(jsonObject.toString(), EquesDeleteBean.class);
                EventBus.getDefault().post(equesDeleteBean);

                MainApplication.getApplication().getDeviceCache().remove(equesDeleteBean.bid);
                Device deleteDevice = new Device();
                deleteDevice.devID = equesDeleteBean.bid;
                // 删除widget
                HomeWidgetManager.deleteWidget(equesDeleteBean.bid);
                EventBus.getDefault().post(new DeviceReportEvent(DeviceReportEvent.DEVICE_DELETE, deleteDevice));

                break;
            case Method.METHOD_BDYLIST://获取设备列表

                BdylistBean bdylistBean = JSON.parseObject(jsonObject.toString(), BdylistBean.class);
                if (!bdylistBean.bdylist.isEmpty()) {
                    for (BdylistBean.Bdylist listBean : bdylistBean.bdylist) {
                        String deviceId = listBean.bid;
                        EquesBean equesBean = new EquesBean();
                        equesBean.bid = deviceId;
                        equesBean.name = listBean.name;
                        equesBean.nick = listBean.nick;
                        equesBean.role = listBean.role;
                        equesBean.localupg = bdylistBean.localupg;
                        if (!bdylistBean.onlines.isEmpty()) {
                            for (BdylistBean.Onlines online : bdylistBean.onlines) {
                                if (TextUtils.equals(deviceId, online.bid)) {
                                    equesBean.uid = online.uid;
                                    equesBean.nid = online.nid;
                                    equesBean.status = online.status;
                                    equesBean.remoteupg = online.remoteupg;

                                    icvss.equesGetDeviceInfo(equesBean.uid);
                                    break;
                                }
                            }
                        }
                        if (TextUtils.isEmpty(equesBean.status)) {
                            equesBean.status = "0";
                        }
                        Device device = new Device(equesBean);
                        MainApplication.getApplication().getDeviceCache().add(device);
                        HomeWidgetManager.saveNewWifiWidget(device);
                    }
                }

                break;
            case Method.METHOD_DEVICEINFO_RESULT://获取设备详情

                EquesInfoBean equesInfoBean = JSON.parseObject(jsonObject.toString(), EquesInfoBean.class);
                EventBus.getDefault().post(equesInfoBean);

                Device device = MainApplication.getApplication().getDeviceCache().get(equesInfoBean.from);
                if (device != null) {
                    try {
                        JSONObject dataObject = new JSONObject(device.data);
                        dataObject.put("equesInfoBean", jsonObject);
                        device.data = dataObject.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                break;
            case Method.METHOD_SETNICK://修改设备昵称

                EquesSetNickBean equesSetNickBean = JSON.parseObject(jsonObject.toString(), EquesSetNickBean.class);
                EventBus.getDefault().post(equesSetNickBean);

                break;
            case Method.METHOD_ALARM_ENABLE_RESULT://人体侦测开关

                EquesPirSwitchBean equesPirSwitchBean = JSON.parseObject(jsonObject.toString(), EquesPirSwitchBean.class);
                EventBus.getDefault().post(equesPirSwitchBean);

                break;
            case Method.METHOD_DB_LIGHT_ENABLE_RESULT://门铃灯开关

                LightEnableBean lightEnableBean = JSON.parseObject(jsonObject.toString(), LightEnableBean.class);
                EventBus.getDefault().post(lightEnableBean);

                break;
            case Method.METHOD_SET_DOORBELL_RING_RESULT://设置门铃声
                break;
            case Method.METHOD_SYNC_DOORBELL_SETTINGS://门铃声同步消息
                break;
            case Method.METHOD_ALARM_GET_RESULT://获取PIR 详情

                EquesPirInfoBean equesPirInfo = JSON.parseObject(jsonObject.toString(), EquesPirInfoBean.class);
                EventBus.getDefault().post(equesPirInfo);

                break;
            case Method.METHOD_ALARM_SET_RESULT://设置PIR参数

                EquesSetPirBean equesSetPirBean = JSON.parseObject(jsonObject.toString(), EquesSetPirBean.class);
                EventBus.getDefault().post(equesSetPirBean);

                break;
            case Method.METHOD_ALARM_ALMLIST://获取报警消息列表

                EquesAlarmBean equesAlarmBean = JSON.parseObject(jsonObject.toString(), EquesAlarmBean.class);
                EventBus.getDefault().post(equesAlarmBean);

                break;
            case Method.METHOD_DELETE_ALARM://删除报警
                break;
            case Method.METHOD_ALARM_RINGLIST://获取访客记录列表

                EquesVisitorBean equesVisitorBean = JSON.parseObject(jsonObject.toString(), EquesVisitorBean.class);
                EventBus.getDefault().post(equesVisitorBean);

                break;
            case Method.METHOD_DELETE_RING://删除访客记录
                break;
            case Method.METHOD_CALL://挂断视频

                EquesCallStatusBean statusBean = JSON.parseObject(jsonObject.toString(), EquesCallStatusBean.class);
                EventBus.getDefault().post(statusBean);

                break;
            case Method.METHOD_RESTART_DEVICE_RESULT://远程重启

                EquesRebootBean equesRebootBean = JSON.parseObject(jsonObject.toString(), EquesRebootBean.class);
                EventBus.getDefault().post(equesRebootBean);

                break;
            case Method.METHOD_UPGRADE_RESULT://通知设备端远程升级

                EquesUpdateBean equesUpdateBean = JSON.parseObject(jsonObject.toString(), EquesUpdateBean.class);
                EventBus.getDefault().post(equesUpdateBean);

                break;
            //服务器主动推送消息
            case Method.METHOD_BATTERY_STATUS://电池电量信息

                BatteryStatusBean batteryStatusBean = JSON.parseObject(jsonObject.toString(), BatteryStatusBean.class);
                Device batteryDevice = MainApplication.getApplication().getDeviceCache().get(batteryStatusBean.from);
                if (batteryDevice != null) {
                    try {
                        JSONObject json = new JSONObject(batteryDevice.data);
                        JSONObject equesInfoBeanJson = json.optJSONObject("equesInfoBean");
                        if (equesInfoBeanJson == null) {
                            equesInfoBeanJson = new JSONObject();
                        }
                        equesInfoBeanJson.put("battery_level", batteryStatusBean.level);
                        equesInfoBeanJson.put("battery_status", batteryStatusBean.status);
                        batteryDevice.data = json.toString();
                        EventBus.getDefault().post(batteryStatusBean);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                break;
            case Method.METHOD_DEVST://设备在线状态变更

                DevstBean devstBean = JSON.parseObject(jsonObject.toString(), DevstBean.class);
                Device devstDevice = MainApplication.getApplication().getDeviceCache().get(devstBean.bid);
                devstDevice.mode = "1".equals(devstBean.status) ? 1 : 2;
                EventBus.getDefault().post(new DeviceReportEvent(devstDevice));

                break;
            case Method.METHOD_ALARM_NEWALM://设备发生报警时的消息通知
                break;
            case Method.METHOD_ONBDY_REMOVED://设备在其他客户端被删除

                OnbdyRemovedBean removedBean = JSON.parseObject(jsonObject.toString(), OnbdyRemovedBean.class);
                MainApplication.getApplication().getDeviceCache().remove(removedBean.removed_bdy.bid);
                Device offLineDevice = new Device();
                offLineDevice.devID = removedBean.removed_bdy.bid;
                // 删除widget
                HomeWidgetManager.deleteWidget(offLineDevice.devID);
                EventBus.getDefault().post(new DeviceReportEvent(DeviceReportEvent.DEVICE_DELETE, offLineDevice));

                break;
            case Method.METHOD_RING://门铃事件
                break;
            case Method.METHOD_PREVIEW://门铃事件图片
                break;
            case Method.METHOD_BATTERY_LOW://低电量警告

                LowPowerAlarmBean lowPowerAlarmBean = JSON.parseObject(jsonObject.toString(), LowPowerAlarmBean.class);
                Device eques = MainApplication.getApplication().getDeviceCache().get(lowPowerAlarmBean.from);
                EventBus.getDefault().post(new AlarmPushEvent("", String.format(context.getString(R.string.MessageCode_Key_0101011), "", eques.name)));

                break;
            case Method.METHOD_WIFI_STATUS://设备端wifi信号强度上报
                break;
            case Method.METHOD_STORAGE_LOW://设备端剩余存储空间警告
                break;
            case Method.METHOD_VIDEOPLAY_STATUS_PLAYING://视频画面开始播放
                EventBus.getDefault().post(new VideoPlayingBean());
                break;
            default:
                break;
        }
    }

    public void loadRingPic(String fid, String deviceId, String time, final EquesFileDownloadListener listener) {
        File file = new File(FileUtil.getEquesRingPath() + "/" + deviceId + "/" + time + ".jpg");
        if (file.exists()) {
            listener.onSuccess(file);
            return;
        }
        URL url = icvss.equesGetRingPicture(fid, deviceId);
        String path = FileUtil.getEquesRingPath() + "/" + deviceId;
        OkGo.get(url.toString())
                .execute(new FileCallback(path, time + ".jpg") {
                    @Override
                    public void onSuccess(File file, Call call, Response response) {
                        listener.onSuccess(file);
                    }
                });
    }

    public void loadPvidPic(String pvid, String deviceId, String time, final EquesFileDownloadListener listener) {
        File file = new File(FileUtil.getEquesTempPath() + "/" + deviceId + "/" + time + ".jpg");
        if (file.exists()) {
            listener.onSuccess(file);
            return;
        }
        URL url = icvss.equesGetThumbUrl(pvid, deviceId);
        String path = FileUtil.getEquesTempPath() + "/" + deviceId;
        OkGo.get(url.toString())
                .execute(new FileCallback(path, time + ".jpg") {
                    @Override
                    public void onSuccess(File file, Call call, Response response) {
                        listener.onSuccess(file);
                    }
                });
    }

    public void loadAlarmFile(String fid, String deviceId, int type, long time, final EquesFileDownloadListener listener) {
        String suffix = null;
        if (type == 3) {
            suffix = ".jpg";
        } else if (type == 4) {
            suffix = ".zip";
        } else if (type == 5) {
            suffix = ".mp4";
        }

        File file = new File(FileUtil.getEquesAlarmPath() + "/" + deviceId + "/" + time + suffix);
        if (file.exists()) {
            listener.onSuccess(file);
            return;
        }

        URL url = icvss.equesGetAlarmfileUrl(fid, deviceId);
        String path = FileUtil.getEquesAlarmPath() + "/" + deviceId;
        OkGo.get(url.toString())
                .execute(new FileCallback(path, time + suffix) {
                    @Override
                    public void onSuccess(File file, Call call, Response response) {
                        listener.onSuccess(file);
                    }
                });
    }

    private void updateToCloud(String deviceId) {
        deviceApiUnit.doBindDevice(deviceId, null, "CMICY1", new DeviceApiUnit.DeviceApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {

            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }
}
