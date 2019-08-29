package cc.wulian.smarthomev6.support.core.mqtt;

/**
 * Created by zbl on 2017/3/1.
 */

public class MQTTApiConfig {
    //心跳间隔（秒）
    public static final int KEEP_ALIVE_INTERVAL_TIME = 30;
    //链接超时(秒）
    public static final int CONNECT_TIMEOUT = 30;

    public static String CloudServerURL = "testv2.wulian.cc:52185";//"mqcn.wuliancloud.com";//
    public static String CloudUserName = "wltest";
    public static String CloudUserPassword = "wl168cloud";

    public static byte[] GW_AES_KEY;
    public static String GW_SALT = "";
    public static String GW_SERVER_URL = "";
    public static String GW_SERVER_PORT = "1883";
    public static String gwID = "";
    public static String gwPassword = "";//用于03连接命令的密码
    public static String gwUserName = "aaa";
    public static String gwUserPassword = "bbb";
}
