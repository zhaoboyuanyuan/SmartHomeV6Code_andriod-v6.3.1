package cc.wulian.smarthomev6.support.record;

import android.util.ArrayMap;

import java.util.Map;

/**
 * Created by Veev on 2017/8/10
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    NameMap
 */

public class NameMap {

    public static Map<String, String> nameMap = new ArrayMap<>();

    static {
        nameMap.put("cc.wulian.smarthomev6.main.login.SigninActivity", "login");
        nameMap.put("cc.wulian.smarthomev6.main.login.RegisterActivity", "register");
        nameMap.put("cc.wulian.smarthomev6.main.login.ForgotPassWordActivity", "forgetpassword");
        nameMap.put("cc.wulian.smarthomev6.main.login.GatewayLoginActivity", "gatewaylogin");
//        nameMap.put("cc.wulian.smarthomev6.main.home.HomeActivity", "home");
//        nameMap.put("cc.wulian.smarthomev6.main.home.HomeActivity", "devicelist");
        nameMap.put("cc.wulian.smarthomev6.main.device.DeviceDetailActivity", "device");
//        nameMap.put("cc.wulian.smarthomev6.main.home.HomeActivity", "discovery");
//        nameMap.put("cc.wulian.smarthomev6.main.home.HomeActivity", "mine");
        nameMap.put("cc.wulian.smarthomev6.main.message.MessageCenterNewActivity", "messagecenter");
        nameMap.put("cc.wulian.smarthomev6.main.message.alarm.MessageAlarmListActivity", "alarmlist");
        nameMap.put("cc.wulian.smarthomev6.main.message.alarm.MessageAlarmActivity", "alarm");
        nameMap.put("cc.wulian.smarthomev6.main.message.alarm.ICamAlarmActivity", "alarm");
        nameMap.put("cc.wulian.smarthomev6.main.message.alarm.EquesAlarmActivity", "alarm");
        nameMap.put("cc.wulian.smarthomev6.main.message.log.MessageLogListActivity", "loglist");
        nameMap.put("cc.wulian.smarthomev6.main.message.log.MessageLogActivity", "log");
        nameMap.put("cc.wulian.smarthomev6.main.message.setts.MessageSettingsActivity", "pushlist");
        nameMap.put("cc.wulian.smarthomev6.main.message.setts.MessageSettingsDetailActivity", "push");
        nameMap.put("cc.wulian.smarthomev6.main.mine.gatewaycenter.GatewayCenterActivity", "gatewaycenter");
        nameMap.put("cc.wulian.smarthomev6.main.mine.gatewaycenter.GatewayListActivity", "gatewaylist");
        nameMap.put("cc.wulian.smarthomev6.main.mine.gatewaycenter.GatewayBindActivity", "gatewayblind");
        nameMap.put("cc.wulian.smarthomev6.main.mine.gatewaycenter.RightsManageActivity", "gatewayauthorization");
        nameMap.put("cc.wulian.smarthomev6.main.mine.gatewaycenter.AuthAccountActivity", "gatewayauthorizationuser");
        nameMap.put("cc.wulian.smarthomev6.main.mine.gatewaycenter.RemindSetActivity", "remindset");
        nameMap.put("cc.wulian.smarthomev6.main.mine.gatewaycenter.GatewaySettingActivity", "gatewayset");
        nameMap.put("cc.wulian.smarthomev6.main.home.scene.HouseKeeperActivity", "myhousekeeper");
        nameMap.put("cc.wulian.smarthomev6.main.mine.setting.AccountSecurityActivity", "accountsecurity");
        nameMap.put("cc.wulian.smarthomev6.main.mine.setting.AlarmVoiceActivity", "alarmvoice");
    }
}
