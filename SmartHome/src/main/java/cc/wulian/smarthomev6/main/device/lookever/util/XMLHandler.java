/**
 * Project Name:  iCam
 * File Name:     XMLHandler.java
 * Package Name:  com.wulian.icam.utils
 * @Date:         2014年12月4日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package cc.wulian.smarthomev6.main.device.lookever.util;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cc.wulian.smarthomev6.entity.VideoTimePeriod;
import cc.wulian.smarthomev6.main.device.outdoor.bean.DeviceDescriptionModel;

/**
 * @ClassName: XMLHandler
 * @Function: XML处理
 * @Date: 2014年12月4日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class XMLHandler {
	private static final String XML_KEY = "context-text:\n";
	/**
	 * @MethodName: getHistoryRecordList
	 * @Function: 解析获取获取视频回看时间段
	 * @author: yuanjs
	 * @date: 2015年10月22日
	 * @email: jiansheng.yuan@wuliangroup.com
	 * @param xml
	 *            xml文件
	 * @return
	 */
	public static List<VideoTimePeriod> getHistoryRecordList(String xml) {
		List<VideoTimePeriod> recordList = null;
		StringReader xmlReader = new StringReader(xml);
		XmlPullParserFactory pullFactory;
		try {
			pullFactory = XmlPullParserFactory.newInstance();
			XmlPullParser xmlPullParser = pullFactory.newPullParser();
			xmlPullParser.setInput(xmlReader); // 保存创建的xml
			int eventType = xmlPullParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String localName = null;
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT: {
					recordList = new ArrayList<>();
				}
					break;
				case XmlPullParser.START_TAG:
					localName = xmlPullParser.getName();
					if ("time".equalsIgnoreCase(localName)) {
						try {
							long StartTime = Integer.valueOf(xmlPullParser
									.getAttributeValue(0));
							long EndTime = Integer.valueOf(xmlPullParser
									.getAttributeValue(1));
							if (StartTime > 0 && EndTime > 0) {
								VideoTimePeriod vtp = new VideoTimePeriod();
								if (StartTime >= EndTime) {
									vtp.setTimeStamp(EndTime);
									vtp.setEndTimeStamp(StartTime);
								} else {
									vtp.setTimeStamp(StartTime);
									vtp.setEndTimeStamp(EndTime);
								}
								vtp.setFileName("");
								recordList.add(vtp);
							}
						} catch (Exception e) {

						}
						// String startTime =
						// millisecondTransformToYMD(xmlPullParser
						// .getAttributeValue(0));
						// String endTime =
						// millisecondTransformToYMD(xmlPullParser
						// .getAttributeValue(1));
						// recordList.add(startTime + "#" + endTime);
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				eventType = xmlPullParser.next();
			}
		} catch (XmlPullParserException e) { // XmlPullParserFactory.newInstance
			e.printStackTrace();
			recordList = null;
		} catch (IllegalArgumentException e) { // xmlSerializer.setOutput
			e.printStackTrace();
			recordList = null;
		} catch (IllegalStateException e) { // xmlSerializer.setOutput
			e.printStackTrace();
			recordList = null;
		} catch (Exception e) {
			e.printStackTrace();
			recordList = null;
		} finally {
			if (xmlReader != null) {
				xmlReader.close();
			}
		}
		return recordList;
	}

	/**
	 * @MethodName: getHistoryRecordList
	 * @Function: 解析获取获取视频回看时间段
	 * @author: yuanjs
	 * @date: 2015年10月22日
	 * @email: jiansheng.yuan@wuliangroup.com
	 * @param xml
	 *            xml文件
	 * @return
	 */
	public static List<String> getHistoryRecordList1(String xml) {
		List<String> recordList = null;
		StringReader xmlReader = new StringReader(xml);
		XmlPullParserFactory pullFactory;
		try {
			pullFactory = XmlPullParserFactory.newInstance();
			XmlPullParser xmlPullParser = pullFactory.newPullParser();
			xmlPullParser.setInput(xmlReader); // 保存创建的xml
			int eventType = xmlPullParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String localName = null;
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT: {
					recordList = new ArrayList<String>();
				}
					break;
				case XmlPullParser.START_TAG:
					localName = xmlPullParser.getName();
					if ("time".equalsIgnoreCase(localName)) {
						String startTime = millisecondTransformToYMD(xmlPullParser
								.getAttributeValue(0));
						String endTime = millisecondTransformToYMD(xmlPullParser
								.getAttributeValue(1));
						recordList.add(startTime + "#" + endTime);
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				eventType = xmlPullParser.next();
			}
		} catch (XmlPullParserException e) { // XmlPullParserFactory.newInstance
			e.printStackTrace();
			recordList = null;
		} catch (IllegalArgumentException e) { // xmlSerializer.setOutput
			e.printStackTrace();
			recordList = null;
		} catch (IllegalStateException e) { // xmlSerializer.setOutput
			e.printStackTrace();
			recordList = null;
		} catch (Exception e) {
			e.printStackTrace();
			recordList = null;
		} finally {
			if (xmlReader != null) {
				xmlReader.close();
			}
		}
		return recordList;
	}

	// 解析XML数据
		public static String parseXMLDataGetStatus(String xmlData) {
			String regEx = "<status>[^>]*</status>";
			String result = "null";
			Pattern pattern = Pattern.compile(regEx);
			Matcher m = pattern.matcher(xmlData);
			if (m.find()) {
				result = m.group();
			}
			int start = "<status>".length();
			int end = result.length() - "</status>".length();
			if (!result.equals("null")) {
				result = result.substring(start, end);
				return result;
			}
			return null;
		}
	
	// 解析XML数据
	public static String parseXMLDataGetSessionID(String xmlData) {
		String regEx = "<sessionID>[^>]*</sessionID>";
		String result = "null";
		Pattern pattern = Pattern.compile(regEx);
		Matcher m = pattern.matcher(xmlData);
		if (m.find()) {
			result = m.group();
		}
		int start = "<sessionID>".length();
		int end = result.length() - "</sessionID>".length();
		if (!result.equals("null")) {
			result = result.substring(start, end);
			return result;
		}
		return null;
	}

	// 解析XML数据
	public static String parseXMLDataGetFilename(String xmlData) {
		String regEx = "<status>[^>]*</status>";
		String result = "null";
		Pattern pattern = Pattern.compile(regEx);
		Matcher m = pattern.matcher(xmlData);
		if (m.find()) {
			result = m.group();
		}
		int start = "<status>".length();
		int end = result.length() - "</status>".length();
		if (!result.equals("null")) {
			result = result.substring(start, end);
			return result;
		}
		return null;
	}

	public static String parseXMLDataGetFileSize(String xmlData) {
		String regEx = "<size>[^>]*</size>";
		String result = "null";
		Pattern pattern = Pattern.compile(regEx);
		Matcher m = pattern.matcher(xmlData);
		if (m.find()) {
			result = m.group();
		}
		int start = "<size>".length();
		int end = result.length() - "</size>".length();
		if (!result.equals("null")) {
			result = result.substring(start, end);
			return result;
		}
		return null;
	}

	// 解析XML数据
	public static boolean parseXMLDataJudgeEnd(String xmlData) {
		StringReader xmlReader = new StringReader(xmlData);
		XmlPullParserFactory pullFactory;
		try {
			pullFactory = XmlPullParserFactory.newInstance();
			XmlPullParser xmlPullParser = pullFactory.newPullParser();
			xmlPullParser.setInput(xmlReader); // 保存创建的xml
			int eventType = xmlPullParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String localName = null;
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT: {

				}
					break;
				case XmlPullParser.START_TAG:
					localName = xmlPullParser.getName();
					if ("history".equalsIgnoreCase(localName)) {
						int tailValue = Integer.valueOf(xmlPullParser
								.getAttributeValue(1));
						if (tailValue == 1) {
							return true;
						}
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				eventType = xmlPullParser.next();
			}
		} catch (XmlPullParserException e) { // XmlPullParserFactory.newInstance
			e.printStackTrace();
		} catch (IllegalArgumentException e) { // xmlSerializer.setOutput
			e.printStackTrace();
		} catch (IllegalStateException e) { // xmlSerializer.setOutput
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (xmlReader != null) {
				xmlReader.close();
			}
		}
		return false;
	}

	// 将毫秒转换成"yyyy-MM-dd HH:mm:ss"格式
	private static String millisecondTransformToYMD(String millisecond) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String result = "";
		if (millisecond != null) {
			result = df.format(new Date(Long.parseLong(millisecond)));
		}
		return result;
	}


	public static List<DeviceDescriptionModel> getDeviceList(String json) {
		List<DeviceDescriptionModel> data = new ArrayList<DeviceDescriptionModel>();
		try {
			JSONObject jsonObj = new JSONObject(json);
			int status = jsonObj.getInt("status");// 肯定会有的
			if (status == 1) {
				String dataJson = jsonObj.getString("data");// 肯定会有
				if (!TextUtils.isEmpty(dataJson)) {
					JSONArray jsonArray = new JSONArray(dataJson);
					int size = jsonArray.length();
					for (int i = 0; i < size; i++) {
						JSONObject itemJson = jsonArray.getJSONObject(i);
						String remoteIp = itemJson.getString("ip");// 肯定会有
						String itemData = itemJson.getString("item");// 肯定会有
						DeviceDescriptionModel device = handleDeviceDescriptionXML(
								itemData, remoteIp);
						if (device != null) {
							data.add(device);
						}
					}
				}
			}
		} catch (JSONException e) {
			data.clear();
		}
		return data;
	}

	public static DeviceDescriptionModel handleDeviceDescriptionXML(
			String data, String remoteIP) {
		String realData = data;
		int startIndex = realData.indexOf(XML_KEY);
		if (startIndex > 0) {
			realData = realData.substring(startIndex + XML_KEY.length());
		}
		DeviceDescriptionModel mDeviceDescriptionModel = null;
		try {
			StringReader xmlReader = new StringReader(realData);
			XmlPullParserFactory pullFactory = XmlPullParserFactory
					.newInstance();
			XmlPullParser xmlPullParser = pullFactory.newPullParser();
			xmlPullParser.setInput(xmlReader); // 保存创建的xml
			int eventType = xmlPullParser.getEventType();
			boolean isDone = false;// 具体解析xml
			while ((eventType != XmlPullParser.END_DOCUMENT)
					&& (isDone != true)) {
				String localName = null;
				switch (eventType) {
					case XmlPullParser.START_DOCUMENT: {
						mDeviceDescriptionModel = new DeviceDescriptionModel();
						mDeviceDescriptionModel.setRemoteIP(remoteIP);
					}
					break;
					case XmlPullParser.START_TAG:
						localName = xmlPullParser.getName();
						if ("local_mac".equalsIgnoreCase(localName)) {
							mDeviceDescriptionModel.setLocal_mac((xmlPullParser
									.nextText()));
						} else if ("model".equalsIgnoreCase(localName)) {
							mDeviceDescriptionModel.setLocal_mac((xmlPullParser
									.nextText()));
						} else if ("model".equalsIgnoreCase(localName)) {
							mDeviceDescriptionModel.setModel((xmlPullParser
									.nextText()));
						} else if ("serialnum".equalsIgnoreCase(localName)) {
							mDeviceDescriptionModel.setSerialnum((xmlPullParser
									.nextText()));
						} else if ("version".equalsIgnoreCase(localName)) {
							mDeviceDescriptionModel.setVersion((xmlPullParser
									.nextText()));
						} else if ("hardware".equalsIgnoreCase(localName)) {
							mDeviceDescriptionModel.setHardware((xmlPullParser
									.nextText()));
						} else if ("sipaccount".equalsIgnoreCase(localName)) {
							mDeviceDescriptionModel.setSipaccount((xmlPullParser
									.nextText()));
						} else if ("video_port".equalsIgnoreCase(localName)) {
							Integer video_port = -1;
							try {
								String video_portStr = xmlPullParser.nextText();
								video_port = Integer.parseInt(video_portStr, 10);
							} catch (NumberFormatException e) {
								video_port = -1;
							}
							mDeviceDescriptionModel.setVideo_port(video_port);
						}
						break;
					case XmlPullParser.END_TAG:
						break;
				}
				eventType = xmlPullParser.next();
			}
		} catch (XmlPullParserException e) { // XmlPullParserFactory.newInstance
			e.printStackTrace();
			mDeviceDescriptionModel = null;
		} catch (IllegalArgumentException e) { // xmlSerializer.setOutput
			e.printStackTrace();
			mDeviceDescriptionModel = null;
		} catch (IllegalStateException e) { // xmlSerializer.setOutput
			e.printStackTrace();
			mDeviceDescriptionModel = null;
		} catch (Exception e) {
			e.printStackTrace();
			mDeviceDescriptionModel = null;
		}
		return mDeviceDescriptionModel;
	}

}
