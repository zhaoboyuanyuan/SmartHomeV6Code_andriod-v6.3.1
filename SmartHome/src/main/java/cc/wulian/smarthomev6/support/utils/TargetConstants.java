package cc.wulian.smarthomev6.support.utils;

import cc.wulian.smarthomev6.support.callback.LogCallBack;

/**
 * Created by yanzy on 2016-6-29
 * Copyright wulian group 2008-2016 All rights reserved. http://www.wuliangroup.com
 **/
public class TargetConstants {
	public static int currentMode = Logger.DEBUG;

	public static boolean printSTDOUT = true;

	public TargetConstants() {
	}

	public static final int getLogLevel() {
		return LogCallBack.LogLevel.DEBUG.getLevel();
	}

}
