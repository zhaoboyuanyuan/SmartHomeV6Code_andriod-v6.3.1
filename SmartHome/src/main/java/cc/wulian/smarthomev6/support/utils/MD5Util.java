package cc.wulian.smarthomev6.support.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MD5Util {
    public static String encrypt(String strIN) {
        MessageDigest alg;
        try {
            alg = MessageDigest.getInstance("MD5");
            alg.update(strIN.getBytes());
            byte[] bytes = alg.digest();
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                String temp = Integer.toHexString(0xFF & bytes[i]);
                if (temp.length() == 1) {
                    hexString.append("0");
                    hexString.append(temp);
                } else if (temp.length() == 2) {
                    hexString.append(temp);
                } else {
                    WLog.w("MD5Util", "error str:" + strIN);
                }
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            Logger.error(e);
        }
        return strIN;
    }
}