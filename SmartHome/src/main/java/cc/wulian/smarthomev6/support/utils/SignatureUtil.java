package cc.wulian.smarthomev6.support.utils;

import android.support.annotation.NonNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by zbl on 2017/3/27.
 * 通信中生成signature
 */

public class SignatureUtil {
    public static String getSignature(@NonNull String nonce, @NonNull String echoStr, @NonNull String timestamp, @NonNull String msgContent) {
        String signature = null;
        ArrayList<String> list = new ArrayList<>(4);
        list.add(nonce);
        list.add(echoStr);
        list.add(timestamp);
        if (msgContent != null) {
            list.add(msgContent);
        }
        Collections.sort(list);
        StringBuilder stringBuilder = new StringBuilder();
        for (String text : list) {
            stringBuilder.append(text);
        }
        signature = shaEncrypt(stringBuilder.toString());
        return signature;
    }

    /**
     * 获取云端的echoStr字段
     */
    public static String getCloudEchoStr(@NonNull String salt, @NonNull String token, @NonNull String userPassword) {
        String echoStr = null;
        String md5 = MD5Util.encrypt(token + userPassword);
        if (md5.length() > 16) { //取后16位
            md5 = md5.substring(md5.length() - 16);
        }
        echoStr = salt + md5;
        return echoStr;
    }

    /**
     * 获取网关的echoStr字段
     */
    public static String getGatewayEchoStr(@NonNull String salt, @NonNull String gwPassword) {
        String echoStr = null;
        String md5 = MD5Util.encrypt(gwPassword);
        if (md5.length() > 16) { //取后16位
            md5 = md5.substring(md5.length() - 16);
        }
        echoStr = salt + md5;
        return echoStr;
    }

    /**
     * SHA加密
     *
     * @param strSrc 明文
     * @return 加密之后的密文
     */
    public static String shaEncrypt(String strSrc) {
        MessageDigest md = null;
        String strDes = null;
        byte[] bt = strSrc.getBytes();
        try {
            md = MessageDigest.getInstance("SHA-256");// 将此换成SHA-1、SHA-512、SHA-384等参数
            md.update(bt);
            strDes = bytes2Hex(md.digest()); // to HexString
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        return strDes;
    }

    /**
     * byte数组转换为16进制字符串
     *
     * @param bts 数据源
     * @return 16进制字符串
     */
    public static String bytes2Hex(byte[] bts) {
        StringBuilder sb = new StringBuilder();
        String tmp = null;
        for (int i = 0; i < bts.length; i++) {
            tmp = (Integer.toHexString(bts[i] & 0xFF));
            if (tmp.length() == 1) {
                sb.append("0");
            }
            sb.append(tmp);
        }
        return sb.toString();
    }

//    private static byte[] getSHA256(String password) {
//        MessageDigest digest = null;
//        try {
//            digest = MessageDigest.getInstance("SHA-256");
//        } catch (NoSuchAlgorithmException e1) {
//            e1.printStackTrace();
//        }
//        digest.reset();
//        return digest.digest(password.getBytes());
//    }
//
//    public static String bin2hex(String strForEncrypt) {
//        byte[] data = getSHA256(strForEncrypt);
//        return String.format("%0" + (data.length * 2) + "X", new BigInteger(1, data));
//    }
}
