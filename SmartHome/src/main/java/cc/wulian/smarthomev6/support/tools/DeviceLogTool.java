package cc.wulian.smarthomev6.support.tools;

import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.utils.WLog;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.bean.MessageBean;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;

/**
 * Created by 王伟 on 2017/4/13
 * Tel: 18365264930
 * QQ: 2355738466
 * Function: 解析日志消息
 */
public class DeviceLogTool {

    public static final String TAG = DeviceLogTool.class.getClass().getSimpleName();

    public static String getFormatString(String format, Object... values) {
        if (format != null) {
            return String.format(format, values);
        } else {
            return "";
        }
    }

    public static String getLogMessage(MessageBean.RecordListBean r) {
        MainApplication mainApplication = MainApplication.getApplication();

        String cmd = r.cmd;
        String devID = r.devID;
        Device device = mainApplication.getDeviceCache().get(devID);
        String roomName = MainApplication.getApplication().getString(R.string.Device_NoneArea) + " ";
        if (device != null) {
            String roomID = device.roomID;
            if (roomID != null) {
                roomName = mainApplication.getRoomCache().getRoomName(roomID) + " ";
            }
        }

//        int mode = r.mode;

        if (!"501".equals(cmd)) {
            WLog.i(TAG, "getLogMessage: 0");
            return cmd;
        }

        String actCode = r.actCode;
        if (actCode != null) {
            String acts[] = actCode.split("-");
            String type = actCode.substring(0, actCode.indexOf("-"));
            String act = actCode.substring(actCode.indexOf("-") + 1);
            act = act.substring(act.indexOf("-") + 1);
            String deviceName = device == null
                    ? DeviceInfoDictionary.getNameByTypeAndName(type, r.name)
                    : DeviceInfoDictionary.getNameByTypeAndName(type, device.name);

            WLog.i(TAG, "getLogMessage: act " + actCode + ", type " + type + ", name " + deviceName + ", acts " + acts);
            switch (act) {
                case "6-1":
                    return roomName + getFormatString(mainApplication.getString(R.string.Log_Device_IsOpen), deviceName);
                case "6-0":
                    return roomName + getFormatString(mainApplication.getString(R.string.Log_Device_IsClosed), deviceName);
                case "1280-0":
                    return roomName + getFormatString(mainApplication.getString(R.string.Message_Center_01_0_StopAlarm), deviceName);
                case "1280-1":
                    return roomName + getFormatString(mainApplication.getString(R.string.Message_Center_01_1_NorAlarm), deviceName);
                case "1280-2":
                    return roomName + getFormatString(mainApplication.getString(R.string.Message_Center_01_2_FireAlarm), deviceName);
            }
        }

        return cmd;

        /*switch (cmd) {
            case "501":
                String actCode = r.actCode;
                String type = null, act = null;
                if (actCode != null) {
                    String[] data = actCode.split("-");
                    if (data != null && data.length >= 4) {
                        type = data[0];
                        act = data[1] + "-" + data[2] + "-" + data[3];
                    }
                }
                deviceName = DeviceInfoDictionary.getNameByTypeAndName(type, r.name);
                try {
//                    actCode = r.endpoints.get(0).endpointNumber + "-" + r.endpoints.get(0).clusters.get(0).clusterId + "-" + r.cmd;
                    switch (act) {
                        case "1-6-1":
                            return getFormatString(mainApplication.getString(R.string.Log_Device_IsOpen), deviceName);
                        case "1-6-0":
                            return getFormatString(mainApplication.getString(R.string.Log_Device_IsClosed), deviceName);
                    }
                } catch (Exception e) {
                    WLog.e("DeviceLogTool", "getLogMessage: ", e);
                }
                return getFormatString("控制设备 %1$s", deviceName);
            case "502":
                deviceName = DeviceInfoDictionary.getNameByTypeAndName(r.type, r.name);
                switch (mode) {
                    case 0:
                        return getFormatString("%1$s 切换状态", deviceName);
                    case 3:
                        return getFormatString("删除设备：%1$s", deviceName);
                    case 2:
                        return getFormatString("修改%1$s信息", deviceName);
                    default:
                }
            case "503":
                String sceneName = r.name;
                String status = r.status;
                switch (mode) {
                    case 0:
                        return getFormatString("%1$s场景 %2$s", "2".equals(status) ? "取消激活" : "激活", sceneName);
                    case 1:
                        return getFormatString("新增场景 %1$s", sceneName);
                    case 2:
                        return getFormatString("修改场景 %1$s", sceneName);
                    case 3:
                        return getFormatString("删除场景 %1$s", sceneName);
                    default:
                }
            case "504":
                return "请求场景列表";
            case "505":
            case "506":
                return "请求分区列表";
            case "508":
                return "请求任务列表";
            case "510":
                return "请求设备列表";
            case "512":
                switch (mode) {
                    case 0:
                        return "请求网关列表";
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    default:
                }
            default:
                return cmd;
        }*/
    }
}
