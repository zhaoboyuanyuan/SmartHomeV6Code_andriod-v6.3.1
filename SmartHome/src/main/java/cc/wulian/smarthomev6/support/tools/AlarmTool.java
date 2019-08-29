package cc.wulian.smarthomev6.support.tools;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.bean.MessageBean;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.mqtt.bean.AlarmMessageBean;

/**
 * Created by zbl on 2017/4/5.
 * 用 alarmID 获取报警信息
 */

public class AlarmTool {

    public static String getFormatString(String format, Object... values) {
        if (format != null) {
            return String.format(format, values);
        } else {
            return "";
        }
    }

    public static String getAlarmMessage(AlarmMessageBean r) {
        return getMessage(r.alarmCode, r.name, r.type, r.roomName, r.extData);
    }

    public static String getAlarmMessage(MessageBean.RecordListBean r) {
        String cmd = r.cmd;
        String alarmCode = r.alarmCode;
        String message = cmd;

        if (!"500".equals(cmd)) {
            return message;
        }

        if (r.alarmCode == null) {
            return message;
        }

        MainApplication app = MainApplication.getApplication();
//        String roomName = r.roomName;
        // 产品 & 领导 要求  不显示设备、分区名 2017年9月27日17:28:19
        String roomName = "";
        String deviceName = "";
//        String deviceName = DeviceInfoDictionary.getNameByTypeAndName(r.type, r.name);
        Object roomAndDevice[] = {roomName, deviceName};

        switch (alarmCode) {
            case "1000001":
                message = getFormatString(app.getString(R.string.Message_Center_HasQianYa), roomAndDevice);
                break;
            case "1000002":
                message = getFormatString(app.getString(R.string.Message_Center_HasRemovedAlarm), roomAndDevice);
                break;
            case "1000003":
                message = getFormatString(app.getString(R.string.Message_Center_OnLine), roomAndDevice);
                break;
            case "1000004":
                message = getFormatString(app.getString(R.string.Message_Center_OffLine), roomAndDevice);
                break;
            case "1010301":
                message = getFormatString(app.getString(R.string.Message_Center_HasOpen), roomAndDevice);
                break;
            case "1010601":
                message = getFormatString(app.getString(R.string.Message_Center_06_01), roomAndDevice);
                break;
            case "1010201":
                message = getFormatString(app.getString(R.string.Message_Center_02_01), roomAndDevice);
                break;
            case "1010901":
                message = getFormatString(app.getString(R.string.Message_Center_09_01), roomAndDevice);
                break;
            case "1014301":
                message = getFormatString(app.getString(R.string.Message_Center_43_01), roomAndDevice);
                break;

            case "1037001":
                String name = r.extData;
                String type = getLockType(r.endpoints.get(0));
                if (name == null) {
                    name = type.substring(0, 2)  + r.endpoints.get(0).clusters.get(0).attributes.get(0).attributeValue.substring(2);
                }
                message = getFormatString(app.getString(R.string.Message_Center_70_01), roomName, deviceName, name, type);
                break;
            case "1037002":
                message = getFormatString(app.getString(R.string.Message_Center_70_02), roomAndDevice);
                break;
            case "1037003":
                message = getFormatString(app.getString(R.string.Message_Center_70_03), roomAndDevice);
                break;
            case "1037004":
                message = getFormatString(app.getString(R.string.Message_Center_70_04), roomAndDevice);
                break;
            case "1037005":
                message = getFormatString(app.getString(R.string.Message_Center_70_05), roomAndDevice);
                break;
            case "1037006":
                message = getFormatString(app.getString(R.string.Message_Center_70_06), roomAndDevice);
                break;
            case "1037007":
                message = getFormatString(app.getString(R.string.Message_Center_70_07), roomAndDevice);
                break;
            default:
                message = null;
                break;
        }

        return message;
    }

    public static String getLockType(MessageBean.RecordListBean.EndpointsBean endpointsBean) {
        String attributeValue = endpointsBean.clusters.get(0).attributes.get(0).attributeValue;
        int value = 0;
        try {
            value = Integer.parseInt(attributeValue);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return getLockType(value);
    }

    public static String getLockType(int attributeValue) {
        MainApplication app = MainApplication.getApplication();

        if (attributeValue > 1000 && attributeValue < 2000) {
            return app.getString(R.string.Message_Center_Password_Unlock);
        }
        /*if (value > 2000 && value < 3000) {
            return app.getString(R.string.Message_Center_Fingerprint_Unlock);
        }*/
        if (attributeValue > 3000 && attributeValue < 4000) {
            return app.getString(R.string.Message_Center_Fingerprint_Unlock);
        }
        if (attributeValue > 4000 && attributeValue < 5000) {
            return app.getString(R.string.Message_Center_Card_Unlock);
        }
        if (attributeValue == 5001) {
            return app.getString(R.string.Message_Center_Key_Unlock);
        }
        if (attributeValue > 6000 && attributeValue < 10000) {
            return app.getString(R.string.Message_Center_Phone_Unlock);
        }

        return app.getString(R.string.Message_Center_Password_Unlock);
    }

    private static String getMessage(String alarmCode, String name, String type, String roomName, String extData) {
        MainApplication app = MainApplication.getApplication();
        Object roomAndDevice[] = {roomName, DeviceInfoDictionary.getNameByTypeAndName(type, name)};

        String message = alarmCode;
        switch (alarmCode) {
            case "1000001":
                message = getFormatString(app.getString(R.string.Message_Center_HasQianYa), roomAndDevice);
                break;
            case "1000002":
                message = getFormatString(app.getString(R.string.Message_Center_HasRemovedAlarm), roomAndDevice);
                break;
            case "1000003":
                message = getFormatString(app.getString(R.string.Message_Center_OnLine), roomAndDevice);
                break;
            case "1000004":
                message = getFormatString(app.getString(R.string.Message_Center_OffLine), roomAndDevice);
                break;
            case "1010301":
                message = getFormatString(app.getString(R.string.Message_Center_HasOpen), roomAndDevice);
                break;
            case "1010601":
                message = getFormatString(app.getString(R.string.Message_Center_06_01), roomAndDevice);
                break;
            case "1010201":
                message = getFormatString(app.getString(R.string.Message_Center_02_01), roomAndDevice);
                break;
            case "1010901":
                message = getFormatString(app.getString(R.string.Message_Center_09_01), roomAndDevice);
                break;
            case "1014301":
                message = getFormatString(app.getString(R.string.Message_Center_43_01), roomAndDevice);
                break;

            /*case "1037001":
                message = getFormatString(app.getString(R.string.Message_Center_70_01), roomAndDevice);
                break;*/
            case "1037002":
                message = getFormatString(app.getString(R.string.Message_Center_70_02), roomAndDevice);
                break;
            case "1037003":
                message = getFormatString(app.getString(R.string.Message_Center_70_03), roomAndDevice);
                break;
            case "1037004":
                message = getFormatString(app.getString(R.string.Message_Center_70_04), roomAndDevice);
                break;
            case "1037005":
                message = getFormatString(app.getString(R.string.Message_Center_70_05), roomAndDevice);
                break;
            case "1037006":
                message = getFormatString(app.getString(R.string.Message_Center_70_06), roomAndDevice);
                break;
            case "1037007":
                message = getFormatString(app.getString(R.string.Message_Center_70_07), roomAndDevice);
                break;
        }

        return message;
    }
}
