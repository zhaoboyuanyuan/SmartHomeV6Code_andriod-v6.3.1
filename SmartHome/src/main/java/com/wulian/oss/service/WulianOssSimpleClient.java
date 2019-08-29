/**
 * Project Name:  WulianOSS
 * File Name:     WulianOssClient.java
 * Package Name:  com.wulian.oss.controller
 * @Date:         2015年10月17日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.oss.service;

import android.content.Context;

import com.alibaba.sdk.android.oss.common.OSSLog;
import com.wulian.oss.ConfigLibrary;
import com.wulian.oss.callback.ConnectDataCallBack;
import com.wulian.oss.model.GetObjectDataModel;
import com.wulian.oss.model.GetOssDataModel;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 采用生产者消费者模式FILO
 * 
 * @ClassName: WulianOssClient
 * @Function:
 * @Date: 2015年10月17日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class WulianOssSimpleClient {
	private static final String TAG = "WulianOssSimpleClient";
	private final static int START_STATE = 1;
	private final static int RUN_STATE = 2;
	private final static int STOP_STATE = 3;

	private ConnectDataCallBack mListener;
	private Thread mProductThread;
	private Thread mConsumeThread;
	private ProductOssDataSimple mProductOssData;
	private ConsumeOssDataSimple mConsumeOssData;

	private int mState;
	private BlockingQueue<GetOssDataModel> mSharedDataQueue = new LinkedBlockingQueue<GetOssDataModel>(
			ConfigLibrary.MAX_QUEUE_TASK_NUM);

	private Context mContext;

	private String mRequestSession;

	public WulianOssSimpleClient(ConnectDataCallBack listener, Context context) {
		this.mListener = listener;
		this.mContext = context;

	}

	public String getVersion() {
		return ConfigLibrary.VERSION;
	}

	public void setRequestSession(String requestSession) {
		this.mRequestSession = requestSession;
	}

	public String getRequestSession() {
		return this.mRequestSession;
	}

	public ConnectDataCallBack getListener() {
		return mListener;
	}

	public void enableLog() {
		OSSLog.enableLog();
	}

	public void disableLog() {
		OSSLog.disableLog();
	}

	public boolean isStop() {
		return mState == STOP_STATE;
	}

	public void initData() {
		if (mProductThread != null && mProductThread.isAlive()) {
			return;
		}
		if (mConsumeThread != null && mConsumeThread.isAlive()) {
			return;
		}

		mProductOssData = new ProductOssDataSimple(mSharedDataQueue, this);
		mProductThread = new Thread(mProductOssData);

		mConsumeOssData = new ConsumeOssDataSimple(mSharedDataQueue, this);
		mConsumeOssData.setDecoderParams(ConfigLibrary.MAX_H264_PICTURE_WIDTH,
				ConfigLibrary.MAX_H264_PICTURE_HEIGHT);

		mConsumeThread = new Thread(mConsumeOssData);
	}

	public void connect() {
		mState = RUN_STATE;
		mProductThread.start();
		mConsumeThread.start();
	}
	
	public void playOSSObjectName(String filePath) {
		GetObjectDataModel data = new GetObjectDataModel();
		data.setFilePath(filePath);
		mProductOssData.setData(data);
	}

	public void disconnect() {
		mState = STOP_STATE;
		if (mProductThread != null && mProductThread.isAlive()) {
			mProductThread.interrupt();
		}
		if (mConsumeThread != null && mConsumeThread.isAlive()) {
			mConsumeThread.interrupt();
		}
	}

	public void startRecord(String filePath) {

	}

	public void stopRecord() {

	}
}
