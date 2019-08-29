package cc.wulian.smarthomev6.support.core.cipher;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;

import com.wulian.sec.wuliansec.WuLianEncrypt;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;

import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTApiConfig;
import cc.wulian.smarthomev6.support.utils.AESUtil;
import cc.wulian.smarthomev6.support.utils.SignatureUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by zbl on 2017/3/23.
 * MQTT加密逻辑
 */

public class CipherUtil {

    public static String decode(String content, byte[] aeskey) {
        byte[] decodeMsgContent = Base64.decode(content, Base64.NO_WRAP);
        String decodeContent = null;
        try {
            decodeContent = new String(AESUtil.decrypt(decodeMsgContent, aeskey), "utf-8").trim();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return decodeContent;
        }
    }

    public static String encode(String content, byte[] aeskey) {
        String encodeMsgContent = null;
        try {
            byte[] encodeContent = AESUtil.encrypt(content.getBytes("utf-8"), aeskey);
            byte[] base64EncodeContent = Base64.encode(encodeContent, Base64.NO_WRAP);
            encodeMsgContent = new String(base64EncodeContent, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return encodeMsgContent;
        }
    }

    /**
     * 生成网关消息体
     *
     * @param msgContent 未加密的命令包，例如{"cmd":"11","appID":"android355383067715137","gwID":"50294DFF1289"}
     * @return 网关消息体，格式如
     * {
     * signature:"",
     * nonce:"",
     * timestamp:"",
     * msgContent:"加密后的命令"
     * }
     */
    public static String createGatewayMessage(@NonNull String msgContent) {
        JSONObject json = new JSONObject();
        try {
//            String msgContentEncode = new String(Base64.encode(AESUtil.encrypt(msgContent.getBytes("utf-8"), MQTTApiConfig.GW_AES_KEY), Base64.NO_WRAP), "utf-8");
            String msgContentEncode = encode(msgContent, MQTTApiConfig.GW_AES_KEY);
            String timestamp = System.currentTimeMillis() + "";
            String nonce = new Random().nextInt(1000) + "";
            json.put("nonce", nonce);
            json.put("timestamp", timestamp);
            json.put("msgContent", msgContentEncode);
            json.put("signature", SignatureUtil.getSignature(nonce, SignatureUtil.getGatewayEchoStr(MQTTApiConfig.GW_SALT, MQTTApiConfig.gwPassword), timestamp, msgContentEncode));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /**
     * 生成云消息体
     *
     * @param msgContent 未加密的命令包，例如{"cmd":"11","appID":"android355383067715137","gwID":"50294DFF1289"}
     * @return 网关消息体，格式如
     * {
     * signature:"",
     * nonce:"",
     * timestamp:"",
     * msgContent:"加密后的命令"
     * }
     */
    public static String createCloudMessage(@NonNull String msgContent) {
        JSONObject json = new JSONObject();
        try {
            String msgContentEncode = encode(msgContent, ApiConstant.getAESKey());
            String timestamp = System.currentTimeMillis() + "";
            String nonce = new Random().nextInt(1000) + "";
            json.put("nonce", nonce);
            json.put("timestamp", timestamp);
            json.put("msgContent", msgContentEncode);

//            if (msgContentEncode != null && msgContentEncode.length() % 16 == 0) {
//                WLog.i("MQTTManager", "EncryptedData 16 :" + msgContentEncode +"\n" + msgContent);
//            }
//            json.put("signature", SignatureUtil.getSignature(nonce, ApiConstant.getSecretKey(), timestamp, msgContentEncode));//原方法，现在采用SSO登录
            json.put("signature", ApiConstant.getSecretKey());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /**
     * 获取Api请求中需要的签名sign字段
     *
     * @return
     */
    public static String getClondApiSign(String content, String time) {
//        String sign = getSha1(ApiConstant.APPID + "&" + content + "&" + ApiConstant.APPKEY + "&" + time).toLowerCase();
//        String sign_new = WuLianEncrypt.wlSignature(content, time);
        String sign_new = WuLianEncrypt.sha1Signature(content, time);
//        WLog.i("getClondApiSign isEquals:" + TextUtils.equals(sign, sign_new));
        return sign_new;
    }

    /**
     * 获取Api请求中需要的签名sign字段
     *
     * @return
     */
    public static String getClondApiSign(Map<String, String> params, String time) {
//        String sign = null;
        String sign_new;
        if (params != null && params.size() > 0) {
            String[] keyArray = params.keySet().toArray(new String[params.size()]);
            Arrays.sort(keyArray);
            StringBuilder sb = new StringBuilder();
            for (String key : keyArray) {
                sb.append(key);
                sb.append("=");
                sb.append(params.get(key));
                sb.append("&");
            }
            sb.setLength(sb.length() - 1);
//            sign = getSha1(ApiConstant.APPID + "&" + sb.toString() + "&" + ApiConstant.APPKEY + "&" + time).toLowerCase();
            sign_new = WuLianEncrypt.sha1Signature(sb.toString(), time);
//            WLog.i("getClondApiSign isEquals:" + TextUtils.equals(sign, sign_new));
        } else {
//            sign = getSha1(ApiConstant.APPID + "&" + ApiConstant.APPKEY + "&" + time).toLowerCase();
            sign_new = WuLianEncrypt.sha1Signature("", time);
//            WLog.i("getClondApiSign isEquals empty:" + TextUtils.equals(sign, sign_new));
        }

        return sign_new;
    }


    /**
     * sha1加密
     */
    public static String getSha1(String str) {
        if (null == str || 0 == str.length()) {
            return null;
        }
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes("UTF-8"));

            byte[] md = mdTemp.digest();
            int j = md.length;
            char[] buf = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(buf);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
