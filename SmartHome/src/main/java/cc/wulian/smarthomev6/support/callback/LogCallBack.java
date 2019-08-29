package cc.wulian.smarthomev6.support.callback;

/**
 * Created by Administrator on 2017/2/22 0022.
 */

public interface LogCallBack {
    int ERROR = 1;
    int WARN = 2;
    int INFO = 3;
    int DEBUG = 4;

    void onLog(LogCallBack.LogLevel var1, long var2, String var4);

    void onLog(LogCallBack.LogLevel var1, long var2, String var4, Throwable var5);

    public static enum LogLevel {
        ERROR("Error", 1),
        WARN("WARN", 2),
        INFO("INFO", 3),
        DEBUG("DEBUG", 4);

        private String name;
        private int level;

        private LogLevel(String name, int level) {
            this.name = name;
            this.level = level;
        }

        public String getName() {
            return this.name;
        }

        public int getLevel() {
            return this.level;
        }
    }
}
