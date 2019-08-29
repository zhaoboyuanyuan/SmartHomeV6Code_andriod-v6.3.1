package cc.wulian.smarthomev6.entity;

import android.text.TextUtils;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.bean.MessageCountBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.tools.MessageTool;

/**
 * 报警消息列表的数据格式  测试版
 */

public class AlarmInfoTest {

    public String deviceId;
    public String deviceName;
    public String alarmMessage;
    public String type;
    public int deviceIcon = R.drawable.icon_device_unknown;
    public int alarmCount;

    public AlarmInfoTest(Device device) {
        this.deviceId = device.devID;
        this.deviceName = DeviceInfoDictionary.getNameByTypeAndName(device.type, device.name);
        this.deviceIcon = DeviceInfoDictionary.getIconByType(device.type);
        this.alarmMessage = "";
        this.alarmCount = 0;
        this.type = device.type;
    }

    @Deprecated
    public AlarmInfoTest(String deviceId, String deviceName, int deviceIcon) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.deviceIcon = deviceIcon;
        this.alarmMessage = "";
        this.alarmCount = 0;
    }

    public AlarmInfoTest(MessageCountBean.ChildDevicesBean bean) {
        Device device = MainApplication.getApplication().getDeviceCache().get(bean.childDeviceId);
        if (device == null) {
            this.deviceId = bean.childDeviceId;
            this.deviceName = DeviceInfoDictionary.getNameByTypeAndName(bean.type, bean.name);
            this.deviceIcon = DeviceInfoDictionary.getIconByType(bean.type);
            this.type = bean.type;
        } else {
            this.deviceId = device.devID;
            this.deviceName = DeviceInfoDictionary.getNameByTypeAndName(device.type, device.name);
            this.deviceIcon = DeviceInfoDictionary.getIconByType(device.type);
            this.type = device.type;
        }
        alarmCount = bean.count;
        try {
            if (!TextUtils.isEmpty(bean.timestamp) && !TextUtils.isEmpty(bean.message)){
                String time = MessageTool.getMessageTime(Long.valueOf(bean.timestamp));
                alarmMessage = time  + " " + bean.message;
            }else {
                alarmMessage = bean.lastMessage;
            }
        }catch (Exception e){
            e.printStackTrace();
            alarmMessage = bean.lastMessage;
        }
    }

    @Override
    public String toString() {
        return "AlarmInfoTest{" +
                "deviceName='" + deviceName + '\'' +
                ", alarmMessage='" + alarmMessage + '\'' +
                ", alarmCount=" + alarmCount +
                '}';
    }
}
