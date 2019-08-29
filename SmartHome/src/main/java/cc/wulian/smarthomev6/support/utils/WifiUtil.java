package cc.wulian.smarthomev6.support.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

/**
 * created by huxc  on 2017/8/29.
 * func：
 * email: hxc242313@qq.com
 */

public class WifiUtil {

    /**
     * 获取wifi加密方式
     *
     * @param cap
     * @return
     */
    public static int getIntEncryption(String encryptionStr) {
        int encryption =0;
        if (encryptionStr.startsWith("WPA P")) {
            encryption = 1;
        } else if (encryptionStr.startsWith("WPA2 P")) {
            encryption = 2;
        } else if (encryptionStr.startsWith("mixed")) {
            encryption = 3;
        } else if (encryptionStr.contains("WEP")) {
            encryption = 4;
        }
        return encryption;
    }


    /**
     * 根据频率获得信道
     *
     * @param frequency
     * @return
     */
    public static int getChannelByFrequency(int frequency) {
        int channel = frequency;
        switch (frequency) {
            case 2412:
                channel = 1;
                break;
            case 2417:
                channel = 2;
                break;
            case 2422:
                channel = 3;
                break;
            case 2427:
                channel = 4;
                break;
            case 2432:
                channel = 5;
                break;
            case 2437:
                channel = 6;
                break;
            case 2442:
                channel = 7;
                break;
            case 2447:
                channel = 8;
                break;
            case 2452:
                channel = 9;
                break;
            case 2457:
                channel = 10;
                break;
            case 2462:
                channel = 11;
                break;
            case 2467:
                channel = 12;
                break;
            case 2472:
                channel = 13;
                break;
            case 2484:
                channel = 14;
                break;
            case 5180:
                channel = 36;
                break;

            case 5200:
                channel = 40;
                break;
            case 5220:
                channel = 44;
                break;
            case 5240:
                channel = 48;
                break;
            case 5260:
                channel = 52;
                break;
            case 5280:
                channel = 56;
                break;
            case 5300:
                channel = 60;
                break;
            case 5320:
                channel = 64;
                break;
            case 5500:
                channel = 100;
                break;
            case 5520:
                channel = 104;
                break;
            case 5540:
                channel = 108;
                break;
            case 5560:
                channel = 112;
                break;
            case 5580:
                channel = 116;
                break;
            case 5600:
                channel = 120;
                break;
            case 5620:
                channel = 124;
                break;
            case 5640:
                channel = 128;
                break;
            case 5660:
                channel = 132;
                break;
            case 5680:
                channel = 136;
                break;
            case 5700:
                channel = 140;
                break;
            case 5745:
                channel = 149;
                break;
            case 5765:
                channel = 153;
                break;
            case 5785:
                channel = 157;
                break;
            case 5805:
                channel = 161;
                break;
            case 5825:
                channel = 165;
                break;
        }
        return channel;
    }
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }
}
