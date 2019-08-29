package cc.wulian.wrecord.record.page;

import android.text.TextUtils;
import android.util.ArrayMap;

import java.util.Map;

/**
 * Created by Veev on 2017/8/4
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    PageMap
 */

public class PageMap {

    private static Map<String, String> pageMap = new ArrayMap<>();

    static {
        /*pageMap.put("cc.wulian.smarthomev6.main.login.SigninActivity", "登录页");
        pageMap.put("cc.wulian.smarthomev6.main.login.RegisterActivity", "注册页");
        pageMap.put("cc.wulian.smarthomev6.main.login.ForgotPassWordActivity", "忘记密码页");
        pageMap.put("cc.wulian.smarthomev6.main.login.GatewayLoginActivity", "网关登录页");
//        pageMap.put("cc.wulian.smarthomev6.main.home.HomeActivity", "首页");
//        pageMap.put("cc.wulian.smarthomev6.main.home.HomeActivity", "设备列表页");
        pageMap.put("cc.wulian.smarthomev6.main.device.DeviceDetailActivity", "设备详情页");
//        pageMap.put("cc.wulian.smarthomev6.main.home.HomeActivity", "发现页");
//        pageMap.put("cc.wulian.smarthomev6.main.home.HomeActivity", "我的");
        pageMap.put("cc.wulian.smarthomev6.main.message.MessageCenterNewActivity", "消息中心页");
        pageMap.put("cc.wulian.smarthomev6.main.message.alarm.MessageAlarmListActivity", "报警消息列表页");
        pageMap.put("cc.wulian.smarthomev6.main.message.alarm.MessageAlarmActivity", "报警消息详情页");
        pageMap.put("cc.wulian.smarthomev6.main.message.log.MessageLogListActivity", "日志列表页");
        pageMap.put("cc.wulian.smarthomev6.main.message.log.MessageLogActivity", "日志详情页");
        pageMap.put("cc.wulian.smarthomev6.main.message.setts.MessageSettingsActivity", "推送设置页");
        pageMap.put("cc.wulian.smarthomev6.main.message.setts.MessageSettingsDetailActivity", "推送设置详情页");
        pageMap.put("cc.wulian.smarthomev6.main.mine.gatewaycenter.GatewayCenterActivity", "网关中心");
        pageMap.put("cc.wulian.smarthomev6.main.mine.gatewaycenter.GatewayListActivity", "网关列表");
        pageMap.put("cc.wulian.smarthomev6.main.mine.gatewaycenter.GatewayBindActivity", "绑定网关");
        pageMap.put("cc.wulian.smarthomev6.main.mine.gatewaycenter.RightsManageActivity", "授权管理");
        pageMap.put("cc.wulian.smarthomev6.main.mine.gatewaycenter.AuthAccountActivity", "授权用户");
        pageMap.put("cc.wulian.smarthomev6.main.mine.gatewaycenter.RemindSetActivity", "提醒设置");
        pageMap.put("cc.wulian.smarthomev6.main.mine.gatewaycenter.GatewaySettingActivity", "网关设置");
        pageMap.put("cc.wulian.smarthomev6.main.home.scene.HouseKeeperActivity", "我的管家");
        pageMap.put("cc.wulian.smarthomev6.main.mine.setting.AccountSecurityActivity", "账号安全");
        pageMap.put("cc.wulian.smarthomev6.main.mine.setting.AlarmVoiceActivity", "报警语音");*/
    }

    public static void setPageMap(Map<String, String> map) {
        pageMap = map;
    }

    /**
     * 从列表中获取别名
     * 如果列表中不存, 则返回空
     */
    public static String getNameFromMap(String name) {
        return pageMap.get(name);
    }

    /**
     * 通过包名获取别名
     * 如果别名不存在, 则返回包名
     */
    public static String getNameByPackage(String name) {
        String pName = pageMap.get(name);
        if (TextUtils.isEmpty(pName)) {
            return name;
        }

        return pName;
    }

    public static String put(String ppackageName, String name) {
        return pageMap.put(ppackageName, name);
    }
}
