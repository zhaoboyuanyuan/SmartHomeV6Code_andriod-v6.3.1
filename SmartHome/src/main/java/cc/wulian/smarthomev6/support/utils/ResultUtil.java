package cc.wulian.smarthomev6.support.utils;

public class ResultUtil {
	public static final int RESULT_SUCCESS = 0;
	public static final int RESULT_FAILED = -1;
	public static final int RESULT_CHANGE_HOST= 1;
	public static final int RESULT_EXCEPTION = 2;
	public static final int RESULT_CONNECTING = 3;
	public static final int RESULT_DISCONNECT = 4;
	public static final int RESULT_HEARTBEAT_TIMEOUT = 5;

	public static final int EXC_GW_NO_PERMISSION = 10;
	public static final int EXC_GW_OFFLINE = 11;
	public static final int EXC_GW_USER_WRONG = 12;
	public static final int EXC_GW_PASSWORD_WRONG = 13;
	public static final int EXC_GW_REMOTE_SERIP = 14;
	public static final int EXC_GW_OVER_CONNECTION = 15;
	public static final int EXC_GW_NO_ACTIVE = 16;
	public static final int EXC_GW_OTHER = 99;
	public static final int EXC_NOT_FOUND_IP = 17;
	
	public static final int LOGIN_RUNING = 101;
}
