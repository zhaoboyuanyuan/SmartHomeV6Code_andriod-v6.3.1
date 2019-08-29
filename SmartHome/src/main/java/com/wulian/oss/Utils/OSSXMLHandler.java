/**
 * Project Name:  iCam
 * File Name:     XMLHandler.java
 * Package Name:  com.wulian.icam.utils
 * @Date:         2014年12月4日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.oss.Utils;

import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.wulian.oss.model.GetObjectDataModel;

/**
 * @ClassName: XMLHandler
 * @Function: XML处理
 * @Date: 2014年12月4日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class OSSXMLHandler {
	public static GetObjectDataModel getObjectData(String xml, String deviceID) {
		GetObjectDataModel objectData = null;
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
					objectData = new GetObjectDataModel();
				}
					break;
				case XmlPullParser.START_TAG:
					localName = xmlPullParser.getName();
					if ("status".equalsIgnoreCase(localName)) {
						objectData.setObjectName(deviceID + "/"
								+ xmlPullParser.nextText());
					} else if ("size".equalsIgnoreCase(localName)) {
						int fileSize = -1;
						try {
							fileSize = Integer.parseInt(xmlPullParser
									.nextText().trim());
						} catch (Exception e) {
							fileSize = -1;
						}
						objectData.setFileSize(fileSize);
					} else if ("timestamp".equalsIgnoreCase(localName)) {
						long time = -1;
						try {
							time = Integer.parseInt(xmlPullParser.nextText()
									.trim());
						} catch (Exception e) {
							time = -1;
						}
						objectData.setTimeStamp(time);
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				eventType = xmlPullParser.next();
			}
		} catch (XmlPullParserException e) { // XmlPullParserFactory.newInstance
			e.printStackTrace();
			objectData = null;
		} catch (IllegalArgumentException e) { // xmlSerializer.setOutput
			e.printStackTrace();
			objectData = null;
		} catch (IllegalStateException e) { // xmlSerializer.setOutput
			e.printStackTrace();
			objectData = null;
		} catch (Exception e) {
			e.printStackTrace();
			objectData = null;
		}
		return objectData;
	}
}
