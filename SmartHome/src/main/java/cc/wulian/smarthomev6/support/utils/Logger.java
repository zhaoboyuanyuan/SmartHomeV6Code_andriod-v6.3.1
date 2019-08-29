package cc.wulian.smarthomev6.support.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import cc.wulian.smarthomev6.support.callback.LogCallBack;
import cc.wulian.smarthomev6.support.callback.LogCallBack.LogLevel;


public class Logger {
	private static LogCallBack callback = null;
	public static String TAG = Logger.class.getName();
	public static final int ERROR =1;
	public static final int WARN = 2;
	public static final int INFO = 3;
	public static final int DEBUG = 4;

	public static void setCallback(LogCallBack callback) {
		callback = callback;
	}

	public static void error(String str) {
		if(isErrorEnable()) {
			if(str == null) {
				str = getStackTraceString(new Throwable("unkown error, this throwable is not caused by program, we just use to print call stack:"));
			}

			fireLog(LogLevel.ERROR, str);
		}

	}

	private static void fireLog(LogLevel level, String str) {
		if(callback != null) {
			callback.onLog(level, (new Date()).getTime(), str);
		} else if(TargetConstants.printSTDOUT) {
			System.out.println(str);
		}

	}

	public static String getStackTraceString(Throwable tr) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		tr.printStackTrace(pw);
		pw.flush();
		return sw.toString();
	}

	public static void error(Throwable e) {
		if(isErrorEnable() && e != null) {
			if(callback != null) {
				callback.onLog(LogLevel.ERROR, (new Date()).getTime(), "", e);
			} else {
				error(getStackTraceString(e));
			}
		}

	}

	public static boolean isErrorEnable() {
		return TargetConstants.getLogLevel() >= 1;
	}

	public static void warn(String str) {
		if(isWarnEnable()) {
			if(str == null) {
				str = "unknown warn";
			}

			fireLog(LogLevel.WARN, str);
		}

	}

	public static boolean isWarnEnable() {
		return TargetConstants.getLogLevel() >= 2;
	}

	public static void info(String str) {
		if(isInfoEnable()) {
			if(str == null) {
				str = "unknown info";
			}

			fireLog(LogLevel.INFO, str);
		}

	}

	public static boolean isInfoEnable() {
		return TargetConstants.getLogLevel() >= 3;
	}

	public static void debug(String str) {
		if(isDebugEnalbe()) {
			if(str == null) {
				str = "unknown debug info";
			}

			fireLog(LogLevel.DEBUG, str);
		}

	}

	public static boolean isDebugEnalbe() {
		return TargetConstants.getLogLevel() >= 4;
	}
}
