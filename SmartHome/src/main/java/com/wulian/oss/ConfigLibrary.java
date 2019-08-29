/**
 * Project Name:  WulianOSS
 * File Name:     ConfigLibrary.java
 * Package Name:  com.wulian.oss
 * @Date:         2015年10月16日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.oss;

/**
 * @ClassName: ConfigLibrary
 * @Function: 配置
 * @Date: 2015年10月16日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class ConfigLibrary {
	public static final String VERSION="1.0.0";
	/** The network connection overtime 10000 ms **/
	public static final int TIMEOUT = 4000;
	/** The network connection overtime 10000 ms **/
	public static final int CONNECTION_TIMEOUT = 4000;
	/** The network connection overtime 10000 ms **/
	public static final int SOTIMEOUT = 20000;
	// 替换设置最大连接数接口，设置全局最大并发任务数，默认为20
	public static final int MAX_QUEUE_TASK_NUM = 5;
	public static final int MAX_CONCURRENT_TASK_NUM = 5;
	public static final int MAX_ERROR_RETRY = 1;
	// 是否使用https，默认为false
	public static final boolean IS_SECURITY_TUNNEL_REQUIRED = false;
	// 每次读取Object的字节长度
	public static final int DEFAULT_GET_OBJECT_LENGTH = 2048;
	// 每次读取Object的范围长度
	public static final int DEFAULT_GET_OBJECT_RANGE_LENGTH = 50*1024-1;
	// 默认帧率
	public static final int DEFAULT_VIDEO_FPS = 15;
	// 默认平均时间间隔
	public static final int DEFAULT_VIDEO_PERIOD_TIMESTAMP = 1000 / DEFAULT_VIDEO_FPS;
	// 默认最后一帧时间间隔
	public static final int DEFAULT_LAST_FRAME_VIDEO_PERIOD_TIMESTAMP = 1000
			% DEFAULT_VIDEO_FPS + DEFAULT_VIDEO_PERIOD_TIMESTAMP;

	// 默认的H264图片宽度
	public static final int MAX_H264_PICTURE_WIDTH = 1280;
	// 默认的H264图片高度
	public static final int MAX_H264_PICTURE_HEIGHT = 720;
	
	//默认的等待超时时间
	public static final int DEFAUTL_WAIT_TIME_OUT=10000;
	//默认的首次等待超时时间
	public static final int DEFAUTL_FIRST_WAIT_TIME_OUT=15000;
//	//默认的每次请求时间
//	public static final int DEFAULT_TIME_PER_REQUEST=1000;
	//默认的每次请求时间
	public static final int DEFAULT_TIME_OUT_PER_REQUEST=400;
	 //默认的等待超时时间次数
	 public static final int DEFAUTL_WAIT_TIME_OUT_COUNTS=DEFAUTL_WAIT_TIME_OUT/DEFAULT_TIME_OUT_PER_REQUEST;
	 //默认的首次等待超时时间次数
	 public static final int DEFAUTL_FIRST_WAIT_TIME_OUT_COUNTS=DEFAUTL_FIRST_WAIT_TIME_OUT/DEFAULT_TIME_OUT_PER_REQUEST;
}
