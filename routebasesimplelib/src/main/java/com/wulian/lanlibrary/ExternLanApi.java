/**
 * Project Name:  RouteLibrary
 * File Name:     ExternLanApi.java
 * Package Name:  com.wulian.lanlibrary
 * @Date:         2014年11月7日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.lanlibrary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.SparseArray;

/**
 * @ClassName: ExternLanApi
 * @Function: 将WulianLANAPI暴露出来
 * @Date: 2014年11月7日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class ExternLanApi {
	/**
	 * @MethodName setLogLevel
	 * @Function 设备日志
	 * @author Puml
	 * @date: 2014年11月6日
	 * @email puml@wuliangroup.cn
	 * @param level
	 *            1: Log.e; 2: Log.e; 3: Log.i; 4: Log.d
	 */
	protected static void setLevel(int level) {
		WulianLANApi.setLogLevel(level);
	}

	/**
	 * @MethodName setLocalIpV4
	 * @Function 设置本地V4 Ip
	 * @author Puml
	 * @date: 2014年11月12日
	 * @email puml@wuliangroup.cn
	 * @param localIp
	 *            本地V4 Ip
	 */
	protected static void setLocalIpV4(String localIp) {
		WulianLANApi.setLocalIpV4(localIp);
	}

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
	protected static String getFourStringPassword(String fourStr) {
		return WulianLANApi.getFourStringPassword(fourStr);
	}
	
	protected static String EncodeMappingString(String needEncode) {
		return WulianLANApi.EncodeMappingString(needEncode, needEncode.length());
	}

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
	protected static String SearchAllDevice(String localMac) {
		String[] resultArray = WulianLANApi.SearchAllDevice(localMac);
		return changeStringArrayDateToJsonByMulticateData(resultArray);
	}

	/**
	 * @MethodName SearchCurentDevice
	 * @Function 搜索当前周围设备
	 * @author Puml
	 * @date: 2014年11月5日
	 * @email puml@wuliangroup.cn
	 * @param localMac
	 *            本地mac地址
	 * @return 字符串数组
	 */
	protected static String SearchCurrentDevice(String localMac) {
		String[] resultArray = WulianLANApi.SearchCurrentDevice(localMac);
		return changeStringArrayDateToJsonByMulticateData(resultArray);
	}

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
	protected static String getAllDeviceInformation(String localMac) {
		String[] resultArray = WulianLANApi.getAllDeviceInformation(localMac);
		return changeStringArrayDateToJsonByMulticateData(resultArray);
	}

	/**
	 * @MethodName getAllDeviceInformation
	 * @Function 获取当前设备信息
	 * @author Puml
	 * @date: 2014年11月5日
	 * @email puml@wuliangroup.cn
	 * @param localMac
	 *            本地mac地址
	 * @return 字符串数组
	 */
	protected static String getCurrentDeviceInformation(String localMac) {
		String[] resultArray = WulianLANApi
				.getCurrentDeviceInformation(localMac);
		return changeStringArrayDateToJsonByMulticateData(resultArray);
	}

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
	protected static String setWirelessWifiForDevice(String remoteIp,
			String localMac, String remoteMac, String ssid, String encryption,
			String key) {
		// String[] resultArray =
		WulianLANApi.setWirelessWifiForDevice(remoteIp, localMac, remoteMac,
				ssid, encryption, key);
		// return changeStringArrayDateToJson(resultArray);
		return getEmptyJsonCombination();
	}

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
	protected static String getWirelessWifiConnectInformationForDevice(
			String remoteIp, String localMac, String remoteMac) {
		String[] resultArray = WulianLANApi
				.getWirelessWifiConnectInformationForDevice(remoteIp, localMac,
						remoteMac);
		return changeStringArrayDateToJson(resultArray);
	}


	/******************** 系统工具 *************************/

	protected static String getSystemFramewareVersion(String remoteIp,
			String localMac, String remoteMac) {
		String[] resultArray = WulianLANApi.getSystemFramewareVersion(remoteIp,
				localMac, remoteMac);
		return changeStringArrayDateToJson(resultArray);
	}
	/**
	 * 
	 * @MethodName changeStringArrayDateToJson
	 * @Function 将数组转换为JSON格式的数据
	 * @author Puml
	 * @date: 2014年11月7日
	 * @email puml@wuliangroup.cn
	 * @param arrayData
	 * @return
	 */
	private static String changeStringArrayDateToJson(String[] arrayData) {
		JSONObject resultObj = new JSONObject();
		try {
			resultObj.put("status", 1);
			if (arrayData != null && arrayData.length > 0) {
				JSONArray array = new JSONArray();
				JSONObject object = new JSONObject();
				for (String item : arrayData) {
					object = new JSONObject();
					object.put("item", item);
					array.put(object);
				}
				resultObj.put("data", array);
				return resultObj.toString();
			} else {
				resultObj.put("data", "");
				return resultObj.toString();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			try {
				resultObj.put("status", 1);
				resultObj.put("data", "");
				return resultObj.toString();
			} catch (JSONException e1) {
				e1.printStackTrace();
				// Do nothing
			}
		}
		return "";
	}

	/**
	 * 
	 * @MethodName changeStringArrayDateToJson
	 * @Function 将数组转换为JSON格式的数据
	 * @author Puml
	 * @date: 2014年11月7日
	 * @email puml@wuliangroup.cn
	 * @param arrayData
	 * @return
	 */
	protected static String changeStringArrayDateToJsonByMulticateData(
			String[] arrayData) {
		JSONObject resultObj = new JSONObject();
		try {
			resultObj.put("status", 1);
			if (arrayData != null && arrayData.length > 0) {
				int size = arrayData.length;
				SparseArray<String> hm = new SparseArray<String>();
				StringBuilder sb = new StringBuilder();
				int hmSize;
				boolean isInclude = false;
				for (int i = 0; i < size; i += 2) {
					sb.delete(0, sb.length());
					sb.append(arrayData[i + 1]);
					// sb.append(arrayData[i + 1]);
					hmSize = hm.size();
					isInclude = false;
					for (int j = 0; j < hmSize; j += 2) {
						String hmStr = hm.get(j + 1);
						String sbStr = sb.toString();
						if (hmStr.equalsIgnoreCase(sbStr)) {
							isInclude = true;
							break;
						}
					}
					if (!isInclude) {
						hm.append(hmSize, arrayData[i]);
						hm.append(hmSize + 1, sb.toString());
					}
				}
				int jsonSize = hm.size();
				if (jsonSize > 0) {
					JSONArray array = new JSONArray();
					JSONObject object = new JSONObject();

					for (int i = 0; i < jsonSize; i += 2) {
						object = new JSONObject();
						object.put("item", hm.get(i));
						object.put("ip", hm.get(i + 1));
						array.put(object);
					}
					resultObj.put("data", array);
					return resultObj.toString();
				} else {
					resultObj.put("data", "");
					return resultObj.toString();
				}
			} else {
				resultObj.put("data", "");
				return resultObj.toString();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			try {
				resultObj.put("status", 1);
				resultObj.put("data", "");
				return resultObj.toString();
			} catch (JSONException e1) {
				e1.printStackTrace();
				// Do nothings
			}
		}
		return "";
	}

	private static String getEmptyJsonCombination() {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("status", 1);
			jsonObject.put("data", "");
			return jsonObject.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			// Do nothing
			return "";
		}
	}

	protected static String BindSeedSet(String remoteIp, String deviceID, String bind_seed) {
		String[] resultArray = WulianLANApi.BindSeed(remoteIp, deviceID, bind_seed);
		return changeStringArrayDateToJson(resultArray);
	}

}
