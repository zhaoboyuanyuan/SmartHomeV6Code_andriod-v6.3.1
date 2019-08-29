package cc.wulian.smarthomev6.main.device.device_if02;

import android.content.Context;
import android.text.TextUtils;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.device.device_23.tv.TvViewHelper;
import cc.wulian.smarthomev6.main.device.device_if02.fan.ControllerViewHelper;

/**
 * created by huxc  on 2018/9/4.
 * func：wifi红外
 * email: hxc242313@qq.com
 */

public class WifiIRManage {
    public static final String TYPE_CUSTOM = "0";
    public static final String TYPE_STB = "1";
    public static final String TYPE_TV = "2";
    public static final String TYPE_DVD = "3";
    public static final String TYPE_PROJECTOR = "5";
    public static final String TYPE_FAN = "6";
    public static final String TYPE_AIR = "7";
    public static final String TYPE_SOUND = "13";
    public static final String TYPE_IT_BOX = "10";
    public static final String MODE_CONTROL_SUCCESS = "IR_SUCESS";
    public static final String MODE_CONTROL_FAIL = "DATA_ERROR";
    public static final String MALLOC_FAIL2_6 = "MALLOC_FAIL2-6";
    public static final String CHECK_FAIL = "CHECK_FAIL";
    public static final String START_LEARN = "1";
    public static final String STOP_LEARN = "2";
    public static final String MODE_LEARN = "MODE_LEARN";
    public static final String MODE_CONTROL = "MODE_CONTROL";
    public static final String IR_START = "IR_START";
    public static final String IR_STOP = "IR_STOP";
    public static final String IR_TIMEROUT = "IR_TIMEROUT";

    public static String getDeviceTypeName(Context context, String type) {
        String deviceName = null;
        switch (type) {
            case TYPE_AIR:
                deviceName = context.getString(R.string.Infraredtransponder_Airconditioner_Remotecontrol);
                break;
            case TYPE_TV:
                deviceName = context.getString(R.string.Infraredrelay_Addremote_Television);
                break;
            case TYPE_STB:
                deviceName = context.getString(R.string.Infraredrelay_Addremote_Settopbox);
                break;
            case TYPE_FAN:
                deviceName = context.getString(R.string.IF_054);
                break;
            case TYPE_IT_BOX:
                deviceName = context.getString(R.string.IF_055);
                break;
            case TYPE_PROJECTOR:
                deviceName = context.getString(R.string.Infrared_ransponder_Projector);
                break;
            default:
                break;
        }
        return deviceName;
    }

    //风扇、自定义、投影仪新增或者重命名判断是否名称重复
    public static boolean isContainsKeyName(String keyName, List<ControllerViewHelper.keyItem> list) {
        boolean result = false;
        for (ControllerViewHelper.keyItem keyItem :
                list) {
            if (TextUtils.equals(keyName, keyItem.getKeyName())) {
                result = true;
            }
        }
        return result;
    }

    //电视机、机顶盒、互联网盒子新增或者重命名判断是否名称重复
    public static boolean containsKeyName(String keyName, List<TvViewHelper.TvItem> list) {
        boolean result = false;
        for (TvViewHelper.TvItem keyItem :
                list) {
            if (TextUtils.equals(keyName, keyItem.getKeyName())) {
                result = true;
            }
        }
        return result;
    }
}
