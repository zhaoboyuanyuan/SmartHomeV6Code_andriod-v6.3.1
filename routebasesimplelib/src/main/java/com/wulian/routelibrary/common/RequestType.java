/**
 * Project Name:  RouteLibrary
 * File Name:     RequestType.java
 * Package Name:  com.wulian.routelibrary.common
 * @Date:         2014-9-6
 * Copyright (c)  2014, wulian All Rights Reserved.
*/

package com.wulian.routelibrary.common;
/**
 * @ClassName: RequestType
 * @Function:  请求类型
 * @Date:      2014-9-6
 * @author     Puml
 * @email      puml@wuliangroup.cn
 */
public enum RequestType {
	HTTP_GET,//http get请求
	HTTP_POST,//http post请求
	HTTP_STREAM,//http Stream请求
	HTTP_SERVER,//http Stream请求
	LAN_MULTICAST_REQUEST,//组播请求
	LAN_REQUEST//局域网请求
}

