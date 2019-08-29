/**
 * Project Name:  RouteLibrary
 * File Name:     RouteLibraryErrorListener.java
 * Package Name:  com.wulian.routelibrary.controller
 * @Date:         2014-9-6
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.routelibrary.controller;

import com.wulian.routelibrary.common.ErrorCode;
import com.wulian.routelibrary.common.RouteApiType;

/**
 * @ClassName: TaskResultListener
 * @Function: 库文件网络返回事件接口
 * @Date: 2014-9-6
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public interface TaskResultListener {
	/**
	 * 
	 * @MethodName OnSuccess
	 * @Function 成功返回
	 * @author Puml
	 * @date: 2014-9-6
	 * @email puml@wuliangroup.cn
	 * @param apiType
	 *            Api类型
	 * @param json
	 *            成功返回的json
	 */
	void OnSuccess(RouteApiType apiType, String json);
	/**
	 * 
	 * @MethodName OnFail
	 * @Function 错误返回
	 * @author Puml
	 * @date: 2014-9-6
	 * @email puml@wuliangroup.cn
	 * @param apiType
	 *            Api类型
	 * @param code
	 *            错误返回的Code
	 */
	void OnFail(RouteApiType apiType, ErrorCode code);
}
