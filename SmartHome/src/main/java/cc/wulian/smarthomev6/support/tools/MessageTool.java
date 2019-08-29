package cc.wulian.smarthomev6.support.tools;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.bean.MessageBean;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.mqtt.bean.GatewayInfoBean;

/**
 * Created by Veev on 2017/6/12
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    日志与报警的解析
 */

public class MessageTool {

    public static String getFormatString(String format, Object... values) {
        if (format != null) {
            try {
                return String.format(format, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static String getLogMessage(MessageBean.RecordListBean r) {
        String messageCode = r.messageCode;

        if (messageCode != null) {
            return getMessage(r);
        }

        return DeviceLogTool.getLogMessage(r);
    }

    public static String getAlarmMessage(MessageBean.RecordListBean r) {
        String messageCode = r.messageCode;
        if (TextUtils.equals("0104101", messageCode) && r.extData != null) {
            try {
                JSONObject jsonObject = new JSONObject(r.extData);
                return jsonObject.optString("massage");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (messageCode != null) {
            return getMessage(r);
        }

        return AlarmTool.getAlarmMessage(r);
    }

    public static String getMessageTime(long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return sdf.format(date);
    }

    public static String getDate(long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(date);
    }

    public static String getWidgetMessage(MessageBean.RecordListBean r) {
        String cmd = r.cmd;
        String messageCode = r.messageCode;
        String message = cmd;

//        String comID = messageCode.substring(0, 2);
//        String mainType = messageCode.substring(2, 4);
//        String num = messageCode.substring(4, 6);
//        String sType = messageCode.substring(6, 7);

        MainApplication app = MainApplication.getApplication();

        if (messageCode == null) {
            return message;
        }
        switch (messageCode) {
            case "0101011":
                message = app.getString(R.string.MessageCode_Widget_Key_0101011);
                break;
            case "0101021":
                message = app.getString(R.string.MessageCode_Widget_Key_0101021);
                break;
            case "0101012":
                message = app.getString(R.string.MessageCode_Widget_Key_0101012);
                break;
            case "0101022":
                message = app.getString(R.string.MessageCode_Widget_Key_0101022);
                break;
            case "0103012":
                String name = r.extData;
                String type = getLockType(r.endpoints.get(0));
                if (name == null) {
                    name = type.substring(0, 2) + r.endpoints.get(0).clusters.get(0).attributes.get(0).attributeValue.substring(2);
                }
                message = getFormatString(app.getString(R.string.MessageCode_Widget_Key_0103012), name, type);
                break;
            case "0103022":
                message = app.getString(R.string.MessageCode_Widget_Key_0103022);
                break;//管家名
            case "0103032":
                message = app.getString(R.string.MessageCode_Widget_Key_0103032);
                break;
            case "0103042":
                message = app.getString(R.string.MessageCode_Widget_Key_0103042);
                break;
            case "0103052":
                message = app.getString(R.string.MessageCode_Widget_Key_0103052);
                break;
            case "0103062":
                message = app.getString(R.string.MessageCode_Widget_Key_0103062);
                break;
            case "0103072":
                message = app.getString(R.string.MessageCode_Widget_Key_0103072);
                break;
            case "0103082":
                message = app.getString(R.string.MessageCode_Widget_Key_0103082);
                break;
            case "0103092":
                message = app.getString(R.string.MessageCode_Widget_Key_0103092);
                break;
            case "0103102":
                message = app.getString(R.string.MessageCode_Widget_Key_0103102);
                break;
            case "0103011":
                message = app.getString(R.string.MessageCode_Widget_Key_0103011);
                break;
            case "0103021":
                message = app.getString(R.string.MessageCode_Widget_Key_0103021);
                break;
            case "0103031":
                message = app.getString(R.string.MessageCode_Widget_Key_0103031);
                break;
            case "0103041":
                message = app.getString(R.string.MessageCode_Widget_Key_0103041);
                break;
            case "0103061":
                message = app.getString(R.string.MessageCode_Widget_Key_0103061);
                break;
            case "0103071":
                message = app.getString(R.string.MessageCode_Widget_Key_0103071);
                break;
            case "0104011":
                message = app.getString(R.string.MessageCode_Widget_Key_0104011);
                break;
            case "0104021":
                message = app.getString(R.string.MessageCode_Widget_Key_0104021);
                break;
            case "0104031":
                message = app.getString(R.string.MessageCode_Widget_Key_0104031);
                break;
            case "0204011":
                message = app.getString(R.string.MessageCode_Widget_Key_0204011);
                break;
            case "0204021":
                message = app.getString(R.string.MessageCode_Widget_Key_0204021);
                break;
            case "0103081":
                message = app.getString(R.string.MessageCode_Widget_Key_0103081);
                break;
            case "0103091":
                message = app.getString(R.string.MessageCode_Widget_Key_0103091);
                break;
            case "0101031":
                message = app.getString(R.string.MessageCode_Widget_Key_0101031);
                break;
            case "0103152":
                message = app.getString(R.string.MessageCode_Widget_Key_0103152);
                break;
            case "0103162":
                message = app.getString(R.string.MessageCode_Widget_Key_0103162);
                break;
            case "0103142":
                message = app.getString(R.string.MessageCode_Widget_Key_0103142);
                break;
            case "0103051":
                message = app.getString(R.string.MessageCode_Widget_Key_0103051);
                break;
            case "0103101":
                message = app.getString(R.string.MessageCode_Widget_Key_0103101);
                break;
            case "0103111":
                message = app.getString(R.string.MessageCode_Widget_Key_0103111);
                break;
            case "0103121":
                message = app.getString(R.string.MessageCode_Widget_Key_0103121);
                break;
            case "0103172":
                message = app.getString(R.string.MessageCode_Widget_Key_0103172);
                break;
            default:
                message = "[" + messageCode + "]";
                break;
        }
        return message;
    }

    public static String getMessage(MessageBean.RecordListBean r) {
        String cmd = r.cmd;
        String messageCode = r.messageCode;
        String programName = null;
        String message = cmd;
        GatewayInfoBean gatewayInfoBean = null;
        if (!TextUtils.isEmpty(Preference.getPreferences().getCurrentGatewayInfo())) {
            gatewayInfoBean = JSON.parseObject(Preference.getPreferences().getCurrentGatewayInfo(), GatewayInfoBean.class);
        }

        String gwName = null;
        if (gatewayInfoBean != null) {
            gwName = gatewayInfoBean.gwName;
        }
//        String comID = messageCode.substring(0, 2);
//        String mainType = messageCode.substring(2, 4);
//        String num = messageCode.substring(4, 6);
//        String sType = messageCode.substring(6, 7);

        MainApplication app = MainApplication.getApplication();
        String roomName = r.roomName;
        // 产品 & 领导 要求  不显示分区名 2017年9月27日17:28:19

        if (TextUtils.isEmpty(roomName)) {
            roomName = "";
        } else {
            roomName = "[" + roomName + "]";
        }
        String deviceName = DeviceInfoDictionary.getNameByTypeAndName(r.type, r.name);

        // 如果是账号登录, 不显示设备名和分区名
        // 网关登录则带出
        if (TextUtils.equals(Preference.getPreferences().getUserEnterType(), Preference.ENTER_TYPE_ACCOUNT)) {
            deviceName = "";
            roomName = "";
        }

        Object roomAndDevice[] = {roomName, deviceName};

        String endpointName = "";
        try {
            endpointName = r.endpoints.get(0).endpointName;
            if (TextUtils.isEmpty(endpointName)) {
                endpointName = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int endpointNumber = 1;
        try {
            endpointNumber = r.endpoints.get(0).endpointNumber;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (messageCode == null) {
            return message;
        }
        switch (messageCode) {
            case "0101011":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0101011), roomAndDevice);
                break;
            case "0101021":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0101021), roomAndDevice);
                break;
            case "0101012":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0101012), roomAndDevice);
                break;
            case "0101022":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0101022), roomAndDevice);
                break;
            case "0101032":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0101032), roomAndDevice);
                break;//场景名
            case "0101042":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0101042), roomAndDevice);
                break;//管家名
            case "0102011":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0102011), roomAndDevice);
                break;
            case "0102021":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0102021), roomAndDevice);
                break;
            case "0102031":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0102031), roomAndDevice);
                break;
            case "0102041":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0102041), roomAndDevice);
                break;
            case "0102051":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0102051), roomAndDevice);
                break;
            case "0103012":
                String name;
                if (TextUtils.isEmpty(r.extData)) {
                    name = getLockTypeName(r.endpoints.get(0));
                } else {
                    name = r.extData;
                }
                String type = getLockType(r.endpoints.get(0));
                message = getFormatString(app.getString(R.string.MessageCode_Key_0103012), name, type);
                break;
            case "0103022":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0103022), roomAndDevice);
                break;
            case "0103032":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0103032), roomAndDevice);
                break;
            case "0103042":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0103042), roomAndDevice);
                break;
            case "0103052":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0103052), roomAndDevice);
                break;
            case "0103062":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0103062), roomAndDevice);
                break;
            case "0103101":
                message = app.getString(R.string.MessageCode_Key_0103101);
                break;
            case "0103111":
                message = app.getString(R.string.MessageCode_Key_0103111);
                break;
            case "0103121":
                message = app.getString(R.string.MessageCode_Key_0103121);
                break;
            case "0103162":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0103162), roomAndDevice);
                break;
            case "0103152":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0103152), roomAndDevice);
                break;
            case "0103072":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0103072), roomName, deviceName, getFormatUser(r.extData));
                break;
            case "0103082":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0103082), roomName, deviceName, getFormatUser(r.extData));
                break;
            case "0103092":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0103092), roomName, deviceName, getFormatUser(r.extData));
                break;
            case "0103102":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0103102), roomAndDevice);
                break;
            case "0103011":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0103011), roomAndDevice);
                break;
            case "0103021":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0103021), roomAndDevice);
                break;
            case "0103031":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0103031), roomAndDevice);
                break;
            case "0103041":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0103041), roomAndDevice);
                break;
            case "0103051":
                message = app.getString(R.string.MessageCode_Key_0103051);
                break;
            case "0104011":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0104011), roomAndDevice);
                break;
            case "0104021":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0104021), deviceName);
                break;
            case "0104031":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0104031), deviceName);
                break;
            case "0204011":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0204011), deviceName);
                break;
            case "0204021":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0204021), deviceName);
                break;
            case "0103061":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0103061), roomAndDevice);
                break;
            case "0103071":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0103071), roomAndDevice);
                break;
            case "0105011":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0105011), r.gwName);
                break;
            case "0103081":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0103081), roomAndDevice);
                break;
            case "0103091":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0103091), roomAndDevice);
                break;
            case "0102012":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0102012), roomAndDevice);
                break;
            case "0102022":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0102022), roomAndDevice);
                break;
            case "0102032":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0102032), roomAndDevice);
                break;
            case "0102042":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0102042), roomAndDevice);
                break;
            case "0102052":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0102052), roomAndDevice);
                break;
            case "0106012":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0106012), roomName, deviceName, endpointName);
                break;
            case "0106022":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0106022), roomName, deviceName, endpointName);
                break;
            case "0106032":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0106032), roomAndDevice);
                break;
            case "0106042":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0106042), roomAndDevice);
                break;
            case "0106052":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0106052), roomAndDevice);
                break;
            case "0106011":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0106011), roomAndDevice);
                break;
            case "0101031":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0101031), roomAndDevice);
                break;
            case "0101041":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0101041), roomAndDevice);
                break;
            case "0102061":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0102061), roomAndDevice);
                break;
            case "0103112":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0103112), roomName, deviceName, getFormatUser(r.extData));
                break;
            case "0103122":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0103122), roomName, deviceName, getFormatUser(r.extData));
                break;
            case "0103132":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0103132), roomName, deviceName, getFormatUser(r.extData));
                break;
            case "0103142":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0103142), roomName, deviceName, getFormatUser(r.extData));
                break;
            case "0107011":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0107011), roomName, deviceName);
                break;
            case "0107021":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0107021), roomName, deviceName);
                break;
            case "0102071":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0102071), roomName, deviceName);
                break;
            case "0104042":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0104042), deviceName, r.user);
                break;
            case "0109011":
                message = getFormatString(app.getString(R.string.Message_sd01_01), deviceName);
                break;
            case "0109021":
                try {
                    JSONObject extData = new JSONObject(r.extData);
                    message = getFormatString(app.getString(R.string.Message_sd01_02), deviceName, extData.optString("portNumber"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "0109031":
                message = getFormatString(app.getString(R.string.Message_sd01_03), deviceName);
                break;
            case "0109041":
                message = getFormatString(app.getString(R.string.Message_sd01_04), deviceName);
                break;
            case "0109051":
                message = getFormatString(app.getString(R.string.Message_sd01_05), deviceName);
                break;
            case "0110012":
                endpointName = app.getString(R.string.Socket) + r.endpoints.get(0).endpointNumber;
                message = getFormatString(app.getString(R.string.MessageCode_Key_0110012), roomName, deviceName, endpointName);
                break;
            case "0110022":
                endpointName = app.getString(R.string.Socket) + r.endpoints.get(0).endpointNumber;
                message = getFormatString(app.getString(R.string.MessageCode_Key_0110022), roomName, deviceName, endpointName);
                break;
            case "0103131":
                message = app.getString(R.string.MessageCode_Key_0103131);
                break;
            case "0103141":
                message = app.getString(R.string.MessageCode_Key_0103141);
                break;
            case "0103151":
                message = app.getString(R.string.MessageCode_Key_0103151);
                break;
            case "0111022":
                String highPressure = "";
                String lowPressure = "";
                String pulseRate = "";
                for (MessageBean.RecordListBean.EndpointsBean.ClustersBean.AttributesBean attribute : r.endpoints.get(0).clusters.get(0).attributes) {
                    if (attribute.attributeId == 0x0004) {
                        highPressure = attribute.attributeValue;
                    } else if (attribute.attributeId == 0x0005) {
                        lowPressure = attribute.attributeValue;
                    } else if (attribute.attributeId == 0x0006) {
                        pulseRate = attribute.attributeValue;
                    }
                }
                message = getFormatString(app.getString(R.string.MessageCode_Key_0111022
                ), deviceName, highPressure, lowPressure, pulseRate);
                break;
            case "0111032":
                String bloodOxygen = "";
                for (MessageBean.RecordListBean.EndpointsBean.ClustersBean.AttributesBean attribute : r.endpoints.get(0).clusters.get(0).attributes) {
                    if (attribute.attributeId == 0x0008) {
                        bloodOxygen = attribute.attributeValue;
                        break;
                    }
                }
                message = getFormatString(app.getString(R.string.MessageCode_Key_0111032
                ), deviceName, bloodOxygen);
                break;
            case "0103161":
                message = app.getString(R.string.MessageCode_Key_0103161);
                break;
            case "0111012":
                String weight = "";
                for (MessageBean.RecordListBean.EndpointsBean.ClustersBean.AttributesBean attribute : r.endpoints.get(0).clusters.get(0).attributes) {
                    if (attribute.attributeId == 0x8001) {
                        weight = attribute.attributeValue;
                        break;
                    }
                }
                message = getFormatString(app.getString(R.string.MessageCode_Key_0111012
                ), deviceName, weight);
                break;
            case "0102081":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0102081), deviceName, roomName);
                break;
            case "0102091":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0102091), deviceName, roomName);
                break;
            case "0102101":
                String endpointText = endpointName;
                if (TextUtils.isEmpty(endpointText)) {
                    endpointText = getFormatString(app.getString(R.string.device_A2_way), endpointNumber);
                }
                message = getFormatString(app.getString(R.string.MessageCode_Key_0102101), deviceName, roomName, endpointText);
                break;
            case "0106062":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0106062), deviceName, roomName);
                break;
            case "0106072":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0106072), deviceName, roomName);
                break;
            case "0103172":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0103172), deviceName, roomName);
                break;
            case "0107031":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0107031), deviceName, roomName);
                break;
            case "0107041":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0107041), deviceName, roomName);
                break;
            case "0107051":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0107051), deviceName, roomName);
                break;
            case "0107061":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0107061), deviceName, roomName);
                break;
            case "0107071":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0107071), deviceName, roomName);
                break;
            case "0101051":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0101051), gwName);
                break;
            case "0101061":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0101061), gwName);
                break;
            case "0101071":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0101071), gwName);
                break;
            case "0101081":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0101081), gwName);
                break;
            case "0101052":
                programName = r.programName;
                message = getFormatString(app.getString(R.string.MessageCode_Key_0101052), programName);
                break;
            case "0102111":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0102111), deviceName, roomName);
                break;
            case "0112011":
                endpointName = getFormatString(app.getString(R.string.device_A2_way), r.endpoints.get(0).endpointNumber + "");
                message = getFormatString(app.getString(R.string.MessageCode_Key_0112011), roomName, deviceName, endpointName);
                break;
            case "0112012":
                if (TextUtils.equals("Cb", r.type)) {
                    endpointName = getFormatString(app.getString(R.string.device_A2_way), r.endpoints.get(0).endpointNumber + "");
                } else {
                    endpointName = "";
                }
                message = getFormatString(app.getString(R.string.MessageCode_Key_0112012), roomName, deviceName, endpointName);
                break;
            case "0112022":
                if (TextUtils.equals("Cb", r.type)) {
                    endpointName = getFormatString(app.getString(R.string.device_A2_way), r.endpoints.get(0).endpointNumber + "");
                } else {
                    endpointName = "";
                }
                message = getFormatString(app.getString(R.string.MessageCode_Key_0112022), roomName, deviceName, endpointName);
                break;
            case "0112032":
                if (TextUtils.equals("Cb", r.type)) {
                    endpointName = getFormatString(app.getString(R.string.device_A2_way), r.endpoints.get(0).endpointNumber + "");
                } else {
                    endpointName = "";
                }
                message = getFormatString(app.getString(R.string.MessageCode_Key_0112032), roomName, deviceName, endpointName);
                break;
            case "0112042":
                if (TextUtils.equals("Cb", r.type)) {
                    endpointName = getFormatString(app.getString(R.string.device_A2_way), r.endpoints.get(0).endpointNumber + "");
                } else {
                    endpointName = "";
                }
                message = getFormatString(app.getString(R.string.MessageCode_Key_0112042), roomName, deviceName, endpointName);
                break;
            case "0112052":
                if (TextUtils.equals("Cb", r.type)) {
                    endpointName = getFormatString(app.getString(R.string.device_A2_way), r.endpoints.get(0).endpointNumber + "");
                } else {
                    endpointName = "";
                }
                message = getFormatString(app.getString(R.string.MessageCode_Key_0112052), roomName, deviceName, endpointName);
                break;
            case "0404012":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0404012), roomName, deviceName);
                break;
            case "0404022":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0404022), roomName, deviceName);
                break;
            case "0404032":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0404032), roomName, deviceName);
                break;
            case "0404042":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0404042), roomName, deviceName);
                break;
            case "0404052":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0404052), roomName, deviceName);
                break;
            case "0404062":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0404062), roomName, deviceName);
                break;
            case "0404072":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0404072), roomName, deviceName);
                break;
            case "0404082":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0404082), roomName, deviceName);
                break;
            case "0404092":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0404092), roomName, deviceName);
                break;
            case "0404102":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0404102), roomName, deviceName);
                break;
            case "0404112":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0404112), roomName, deviceName);
                break;
            case "0404122":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0404122), roomName, deviceName);
                break;
            case "0404132":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0404132), roomName, deviceName);
                break;
            case "0404142":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0404142), roomName, deviceName);
                break;
            case "0404152":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0404152), roomName, deviceName);
                break;
            case "0404162":
                message = getFormatString(app.getString(R.string.MessageCode_Key_0404162), roomName, deviceName);
                break;
            default:
                message = null;
                break;
        }
        return message;
    }

    private static String getFormatUser(String extData) {
        if (!TextUtils.isEmpty(extData)) {
            return extData.replace("#$manage$#", MainApplication.getApplication().getString(R.string.Device_Smart_lock_Admin))
                    .replace("#$normal$#", MainApplication.getApplication().getString(R.string.Device_Smart_lock_Ordinary))
                    .replace("#$temp$#", MainApplication.getApplication().getString(R.string.Device_Smart_lock_Temporary));
        }
        return "";
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

    public static String getLockTypeName(MessageBean.RecordListBean.EndpointsBean endpointsBean) {
        String attributeValue = endpointsBean.clusters.get(0).attributes.get(0).attributeValue;
        int value = 0;
        try {
            value = Integer.parseInt(attributeValue);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return getLockTypeName(value);
    }

    public static String getLockType(int attributeValue) {
        MainApplication app = MainApplication.getApplication();

        if (attributeValue > 1000 && attributeValue < 2000) {
            return app.getString(R.string.BindGateway_Pwd);
        }
        /*if (value > 2000 && value < 3000) {
            return app.getString(R.string.Message_Center_Fingerprint_Unlock);
        }*/
        if (attributeValue > 3000 && attributeValue < 4000) {
            return app.getString(R.string.Device_Dooropening_Fingerprint);
        }
        if (attributeValue > 4000 && attributeValue < 5000) {
            return app.getString(R.string.Device_Dooropening_Radiofrequencycard);
        }
        if (attributeValue > 5000 && attributeValue < 6000) {
            return app.getString(R.string.Device_Dooropening_Key);
        }
        if (attributeValue > 6000 && attributeValue < 10000) {
            return app.getString(R.string.Device_Dooropening_Phone);
        }

        return app.getString(R.string.BindGateway_Pwd);
    }

    public static String getLockTypeName(int attributeValue) {
        MainApplication app = MainApplication.getApplication();

        if (attributeValue > 1000 && attributeValue < 2000) {
            return getFormatString(app.getString(R.string.Message_Center_Password_Num), attributeValue % 100);
        }
        /*if (value > 2000 && value < 3000) {
            return app.getString(R.string.Message_Center_Fingerprint_Unlock);
        }*/
        if (attributeValue > 3000 && attributeValue < 4000) {
            return getFormatString(app.getString(R.string.Message_Center_Fingerprint_Num), attributeValue % 100);
        }
        if (attributeValue > 4000 && attributeValue < 5000) {
            return getFormatString(app.getString(R.string.Message_Center_Card_Num), attributeValue % 100);
        }
        if (attributeValue > 5000 && attributeValue < 6000) {
            return getFormatString(app.getString(R.string.Message_Center_Key_Num), attributeValue % 100);
        }
        if (attributeValue > 6000 && attributeValue < 10000) {
            return getFormatString(app.getString(R.string.Message_Center_Phone_Num), attributeValue % 100);
        }

        return getFormatString(app.getString(R.string.Message_Center_Password_Num), attributeValue % 100);
    }

}
