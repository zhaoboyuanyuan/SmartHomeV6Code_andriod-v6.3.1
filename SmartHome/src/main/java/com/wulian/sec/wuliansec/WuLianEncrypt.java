package com.wulian.sec.wuliansec;

/**
 * Created by zbl on 2017/10/23.
 */

public class WuLianEncrypt {
    static {
        System.loadLibrary("wulianEncrypt");
    }
    public static native String sha1Signature(String paramJsonString, String timestamp);

    public static native String getSoVersion();

}
