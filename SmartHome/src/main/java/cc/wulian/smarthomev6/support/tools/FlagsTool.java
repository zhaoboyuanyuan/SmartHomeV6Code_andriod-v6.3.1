package cc.wulian.smarthomev6.support.tools;

import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.utils.VersionUtil;

/**
 * 解析标志位
 * Created by zbl on 2017/9/4.
 */

public class FlagsTool {
    private static final int EFFECT_VERSIONCODE = 6;
    private static int version_code = 0;

    public static final int NEW_HOMEMINE_MASK = 1;
    public static final int NEW_SHAREMANAGE_MASK = 1 << 1;
    public static final int NEW_WULIANMEMBER_MASK = 1 << 2;

    static {
        version_code = VersionUtil.getVersionCode(MainApplication.getApplication());
    }

    public static void setFlag(int maskType, boolean flag) {
        Preference preference = Preference.getPreferences();
        int data = preference.getNewFlagsData();
        data = data | ((flag ? 0xffffffff : 0) & maskType);
        if (data == (NEW_SHAREMANAGE_MASK | NEW_WULIANMEMBER_MASK)){
            data = data | NEW_HOMEMINE_MASK;
        }
        preference.saveNewFlagsData(data);
    }

    public static boolean getFlag(int maskType) {
        if (EFFECT_VERSIONCODE != version_code) {
            return true;
        }
        Preference preference = Preference.getPreferences();
        int data = preference.getNewFlagsData();
        return (data & maskType) != 0;
    }
}
