package cc.wulian.wrecord.utils;

import cc.wulian.wrecord.WRecord;

/**
 * Created by Veev on 2017/8/4
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    WLog
 */

public class WLog {

    private static boolean isPrint = WRecord.sCouldLog;

    public static void setIsPrint(boolean isPrint) {
        WLog.isPrint = isPrint;
    }

    public static void i(String tag, String msg) {
        if (isPrint) {
            android.util.Log.i(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (isPrint) {
            android.util.Log.e(tag, msg);
        }
    }

    /**
     * ******************** WLog json **************************
     */
    public static int json(String tag, String msg) {
        return isPrint && msg != null ? android.util.Log.i(tag, format(msg)) : -1;
    }

    public static String format(String jsonStr) {
        int level = 0;
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < jsonStr.length(); i++) {
            char c = jsonStr.charAt(i);
            if (level > 0 && '\n' == buffer.charAt(buffer.length() - 1)) {
                buffer.append(getLevelStr(level));
            }
            switch (c) {
                case '{':
                case '[':
                    buffer.append(c).append("\n");
                    level++;
                    break;
                case ',':
                    buffer.append(c).append("\n");
                    break;
                case '}':
                case ']':
                    buffer.append("\n");
                    level--;
                    buffer.append(getLevelStr(level));
                    buffer.append(c);
                    break;
                default:
                    buffer.append(c);
                    break;
            }
        }

        return buffer.toString();

    }

    private static String getLevelStr(int level) {
        StringBuilder buffer = new StringBuilder();
        for (int levelI = 0; levelI < level; levelI++) {
            buffer.append("\t");
        }
        return buffer.toString();
    }
}
