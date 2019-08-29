/**
 * Project Name:  WulianOSS
 * File Name:     ConnectDataCallBack.java
 * Package Name:  com.wulian.oss.callback
 * @Date:         2015年10月16日
 * Copyright (c)  2015, wulian All Rights Reserved.
*/

package com.wulian.oss.callback;
/**
 * @ClassName: ConnectDataCallBack
 * @Function:
 * @Date:      2015年10月16日
 * @author     Puml
 * @email      puml@wuliangroup.cn
 */
public interface ConnectDataCallBack {
	public void onRequestObjectEndFlag();//请求文件结尾标志
	
	public void onRequestGetObjectResultOK(long timestamp);//确认当前视频已经过来了
	
	public void onH264StreamMessage(byte[] data, int width, int height);// 返回H264流消息

	public void onDisconnect(int code, String reason);// 断开

	public void onError(Exception error);// 错误
}

