/**
 * Project Name:  WulianLANlLibrary
 * File Name:     WulianLANApi.java
 * Package Name:  com.wulian.lanllibrary
 * @Date:         2014年10月27日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.lanlibrary;

/**
 * @ClassName: WulianLANApi
 * @Function: 物联摄像头局域网
 * @Date: 2014年10月27日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class WulianLANApi {
	static {
		System.loadLibrary("wulianlan");
	}
	/******************** 设备日志等级 *************************/
	/**
	 * @MethodName setLogLevel
	 * @Function 设备日志
	 * @author Puml
	 * @date: 2014年11月6日
	 * @email puml@wuliangroup.cn
	 * @param level
	 *            1: Log.e; 2: Log.e; 3: Log.i; 4: Log.d
	 */
	protected static native void setLogLevel(int level);

	/**
	 * @MethodName setLocalIpV4
	 * @Function 设置本地V4 Ip
	 * @author Puml
	 * @date: 2014年11月12日
	 * @email puml@wuliangroup.cn
	 * @param localIp
	 *            本地V4 Ip
	 */
	protected static native void setLocalIpV4(String localIp);

	/********************* 设置4位密码 ************************/
	/**
	 * @MethodName getFourStringPassword
	 * @Function 返回8位密码
	 * @author Puml
	 * @date: 2014年12月3日
	 * @email puml@wuliangroup.cn
	 * @param fourStr
	 *            4位字符串
	 * @return 返回8位密码
	 */
	protected static native String getFourStringPassword(String fourStr);

	protected static native String getVersion();
	
	/**
	 * @MethodName  EncodeMappingString
	 * @Function    编码字符
	 * @author      Puml
	 * @date:       2016年4月26日
	 * @email       puml@wuliangroup.cn
	 * @param needEncode 待编码字符串
	 * @param needEncodeLength 待编码字符串长度
	 * @return
	 */
	protected static native String EncodeMappingString(String needEncode,int needEncodeLength);
	
	/******************** 设备发现 *************************/
	/**
	 * @MethodName SearchAllDevice
	 * @Function 搜索周围设备
	 * @author Puml
	 * @date: 2014年11月5日
	 * @email puml@wuliangroup.cn
	 * @param localMac
	 *            本地mac地址
	 * @return 字符串数组
	 */
	protected static native String[] SearchAllDevice(String localMac);

	/**
	 * @MethodName SearchCurrentDevice
	 * @Function 搜索当前周围设备
	 * @author Puml
	 * @date: 2014年11月5日
	 * @email puml@wuliangroup.cn
	 * @param localMac
	 *            本地mac地址
	 * @return 字符串数组
	 */
	protected static native String[] SearchCurrentDevice(String localMac);

	/**
	 * @MethodName getAllDeviceInformation
	 * @Function 获取设备信息
	 * @author Puml
	 * @date: 2014年11月5日
	 * @email puml@wuliangroup.cn
	 * @param localMac
	 *            本地mac地址
	 * @return 字符串数组
	 */
	protected static native String[] getAllDeviceInformation(String localMac);

	/**
	 * @MethodName getAllDeviceInformationCallBack
	 * @Function 获取设备信息回调
	 * @author Puml
	 * @date: 2014年11月5日
	 * @email puml@wuliangroup.cn
	 * @param localMac
	 *            本地mac地址
	 * @return 字符串数组
	 */
	protected static native void getAllDeviceInformationCallBack(String localMac,int continueTime);

	/**
	 * @MethodName stopCallBack
	 * @Function 停止回调
	 * @author Puml
	 * @date: 2014年11月5日
	 * @email puml@wuliangroup.cn
	 * @param localMac
	 *            本地mac地址
	 * @return 字符串数组
	 */
	public static native void stopCallBack();
	
	/**
	 * @MethodName getCurrentDeviceInformation
	 * @Function 获取当前设备信息
	 * @author Puml
	 * @date: 2014年11月5日
	 * @email puml@wuliangroup.cn
	 * @param localMac
	 *            本地mac地址
	 * @return 字符串数组
	 */
	protected static native String[] getCurrentDeviceInformation(String localMac);
	/******************** wireless配置 *************************/
	/**
	 * @MethodName setWirelessWifiForDevice
	 * @Function 配置wifi
	 * @author Puml
	 * @date: 2014年11月5日
	 * @email puml@wuliangroup.cn
	 * @param localMac
	 *            本地mac地址
	 * @param remoteMac
	 *            远端mac地址
	 * @param ssid
	 *            AP的名称
	 * @param encryption
	 *            AP的加密方式：none, wep, psk, psk2, wpa, wpa2
	 * @param key
	 *            使用psk, psk2, wpa, wpa2加密时， AP的密码
	 * @return 字符串数组
	 */
	protected static native void setWirelessWifiForDevice(String remoteIp,
			String localMac, String remoteMac, String ssid, String encryption,
			String key);

	/**
	 * @MethodName getWirelessWifiConnectInformationForDevice
	 * @Function 获取连接信息
	 * @author Puml
	 * @date: 2014年11月5日
	 * @email puml@wuliangroup.cn
	 * @param localMac
	 *            本地mac地址
	 * @param remoteMac
	 *            远端mac地址
	 * @return 字符串数组
	 */
	protected static native String[] getWirelessWifiConnectInformationForDevice(
			String remoteIp, String localMac, String remoteMac);

	/**
	 * @MethodName getSystemFramewareVersion
	 * @Function 获取固件版本
	 * @author Puml
	 * @date: 2015年1月22日
	 * @email puml@wuliangroup.cn
	 * @param localMac
	 *            本地mac地址
	 * @param remoteMac
	 *            远端mac地址
	 * @return
	 */
	protected static native String[] getSystemFramewareVersion(String remoteIp,
			String localMac, String remoteMac);
	public static native String[] BindSeed(String var0, String var1, String var2);

}
