package com.wulian.routelibrary.utils;

import android.util.Base64;

/**
 * @author kaibing.zhang
 * @ClassName: Base64Util
 * @Function:
 * @date: 2017年5月16日
 * @email kaibing.zhang@wuliangroup.com
 */
public class Base64Util {

    public static String encode(byte[] input) {
        return new String(Base64.encode(input, Base64.NO_WRAP));
    }

    public static byte[] decode(String str) {
        return Base64.decode(str.getBytes(), Base64.NO_WRAP);
    }
}
