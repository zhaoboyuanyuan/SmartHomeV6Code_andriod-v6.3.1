package cc.wulian.smarthomev6.support.tools;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zbl on 2017/11/6.
 */

public class NFCTool {
    public static boolean isSupport(Context context) {
        PackageManager pm = context.getPackageManager();
        // 获取是否支持电话
//        boolean telephony = pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
        // 是否支持GSM
//        boolean gsm = pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_GSM);
        // 是否支持CDMA
//        boolean cdma = pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_CDMA);
        //使用hasSystemFeature方法可以检查设备是否其他功能。如陀螺仪，NFC，蓝牙等等，
        boolean nfc = pm.hasSystemFeature(PackageManager.FEATURE_NFC);
        return nfc;
    }

    public static String getSceneJson(String gatewayId, String sceneId) {
        if (TextUtils.isEmpty(sceneId)) {
            return null;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("gatewayId", gatewayId);
            jsonObject.put("sceneId", sceneId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
