package cc.wulian.smarthomev6.support.utils;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cc.wulian.smarthomev6.BuildConfig;

public class LogUtil {
    private static final String TAG = LogUtil.class.getSimpleName();
    private static final String CAMERALOG = "cameraLog";
    public static final String LC_TAG = "LC_CAMERA";//乐橙摄像机

    // FIXME modify "isDebug = false" when publishing
    public static final boolean isDebug = false;

    public static void log(Object msg) {
        if (isDebug) log(TAG, msg);
    }

    public static void log(String tag, Object msg) {
        if (isDebug) WLog.d(tag, String.valueOf(msg));
    }

    public static void logWarn(Object msg) {
        if (isDebug) logWarn(TAG, msg);
    }

    public static void logWarn(String tag, Object msg) {
        if (isDebug) WLog.w(tag, String.valueOf(msg));
    }

    public static void logErr(Object msg) {
        if (isDebug) logErr(TAG, msg);
    }

    public static void logErr(String tag, Object msg) {
        if (isDebug) WLog.e(tag, String.valueOf(msg));
    }

    public static void logException(String msg, Throwable throwable) {
        if (isDebug) WLog.w(TAG, msg, throwable);
    }

    public static String ms2Date(long _ms) {
        Date date = new Date(_ms);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return format.format(date);
    }

    public static void WriteBcLog(String content) {
        WLog.i(CAMERALOG, content);
        if (BuildConfig.DEBUG) {
            File sd = Environment.getExternalStorageDirectory();
            String path = sd.getPath() + "/wulian";
            File file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }
            String fileName = path + "/lockLog.txt";
            try {
                FileWriter fw = new FileWriter(fileName, true);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(ms2Date(System.currentTimeMillis()) + ":");
                bw.write(content);// 往已有的文件上添加字符串
                bw.write("\n");
                bw.close();
                fw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}