package cc.wulian.smarthomev6.main.device;

import android.util.ArrayMap;

import cc.wulian.smarthomev6.main.device.cateye.CateyeVisitorActivity;
import cc.wulian.smarthomev6.main.device.cateye.album.AlbumGridActivity;
import cc.wulian.smarthomev6.main.device.device_Bn.setting.DeviceBnDeviceInfoActivity;
import cc.wulian.smarthomev6.main.device.device_bc.settingmore.DevBc_AlarmSettingActivity;
import cc.wulian.smarthomev6.main.device.device_bc.settingmore.DevBc_deviceInfoActivity;
import cc.wulian.smarthomev6.main.device.hisense.config.AddHisenseDeviceActivity;
import cc.wulian.smarthomev6.main.device.lock_op.LockOpActivity;
import cc.wulian.smarthomev6.main.device.more.Dev69ChangePwdActivity;
import cc.wulian.smarthomev6.main.h5.H5BridgeCommonActivity;
import cc.wulian.smarthomev6.main.message.alarm.BcAlarmActivity;
import cc.wulian.smarthomev6.main.message.alarm.MessageAlarmActivity;
import cc.wulian.smarthomev6.main.message.log.MessageLogActivity;

/**
 * Created by Veev on 2017/6/6
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    设备更多页面路由
 */

public class DeviceMoreRouter {

    private static final ArrayMap<String, Class> map = new ArrayMap<>();

    static {
//        map.put("H5", MessageDetailActivity.class);
        map.put("Log", MessageLogActivity.class);
        map.put("Alarm", MessageAlarmActivity.class);
        map.put("Alarm_Bc",BcAlarmActivity.class);
        map.put("DevBc_CaptureImages",AlbumGridActivity.class);//Bc锁抓拍图库
        map.put("DevBc_VisitorsNotes",CateyeVisitorActivity.class);//Bc锁访客记录
        map.put("DevOP_Admin", LockOpActivity.class);//OP锁用户管理
        map.put("OP_Alarm_Setts", LockOpActivity.class);//OP锁用户管理
        map.put("DevBc_deviceInfo",DevBc_deviceInfoActivity.class);//Bc锁设备信息
        map.put("DevBn_deviceInfo",DeviceBnDeviceInfoActivity.class);//Bn锁设备信息
//        map.put("Dialog", MessageDetailActivity.class);
        map.put("DevBc_AlarmSetting",DevBc_AlarmSettingActivity.class);//Bc锁报警设置
        map.put("Lock_Message",LockMessageSettingActivity.class);//BC\BD\BF\BG门锁 短信通知
        map.put("H5BridgeCommon",H5BridgeCommonActivity.class);// H5 公共页面
        map.put("HisenseWiFi",AddHisenseDeviceActivity.class);// 海信设备配网
        map.put("Dev69_ChangePWD",Dev69ChangePwdActivity.class);// 玻璃门锁修改密码
    }

    public static Class getClass(String key) {
        return map.get(key);
    }
}
