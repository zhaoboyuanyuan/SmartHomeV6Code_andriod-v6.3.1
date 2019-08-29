/**
 * Project Name:  WulianOSS
 * File Name:     WulianOssClient.java
 * Package Name:  com.wulian.oss.controller
 * @Date:         2015年10月17日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.oss.service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.model.DeleteObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.wulian.oss.ConfigLibrary;
import com.wulian.oss.callback.ConnectDataCallBack;
import com.wulian.oss.model.FederationToken;
import com.wulian.oss.model.GetObjectDataModel;
import com.wulian.oss.model.GetOssDataModel;

import android.content.Context;

/**
 * 采用生产者消费者模式FILO
 * 
 * @ClassName: WulianOssClient
 * @Function:
 * @Date: 2015年10月17日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class WulianOssClient {
	private static final String TAG = "WulianOssClient";
	private final static int START_STATE = 1;
	private final static int RUN_STATE = 2;
	private final static int STOP_STATE = 3;

	private ConnectDataCallBack mListener;
	private Thread mProductThread;
	private Thread mConsumeThread;
	private ProductOssData mProductOssData;
	private ConsumeOssData mConsumeOssData;

	private int mState;
	private BlockingQueue<GetOssDataModel> mSharedDataQueue = new LinkedBlockingQueue<GetOssDataModel>(
			ConfigLibrary.MAX_QUEUE_TASK_NUM);
	// private BlockingQueue<GetOssDataModel> mSharedDataQueue = new
	// ArrayBlockingQueue<GetOssDataModel>(
	// ConfigLibrary.MAX_CONCURRENT_TASK_NUM,true);

	// OSS
	private OSS mOSSClient;
	private Context mContext;
	private ClientConfiguration conf;
	private GetObjectRequest mGetObjectRequest;
	private DeleteObjectRequest mDeleteObjectRequest;
	private String mRequestSession;
	private String mBucketName;
	private String mEndPoint;
	private String mRequestName;
	private String mObjectName;

	public WulianOssClient(ConnectDataCallBack listener, Context context) {
		this.mListener = listener;
		this.mContext = context;
		this.mEndPoint = null;
		this.mBucketName = null;
		this.mObjectName = null;
	}

	// public WulianOssClient(ConnectDataCallBack listener, Context context,
	// String endPoint, String bucketName) {
	// this.mListener = listener;
	// this.mContext = context;
	// this.mEndPoint = endPoint;
	// this.mBucketName = bucketName;
	// this.mObjectName = null;
	// }

	public String getVersion() {
		return ConfigLibrary.VERSION;
	}

	public void setRequestSession(String requestSession) {
		this.mRequestSession = requestSession;
	}

	public String getRequestSession() {
		return this.mRequestSession;
	}

	public void setRequestObjectName(String requestName) {
		this.mRequestName = requestName;
	}

	public String getRequestObjectName() {
		return this.mRequestName;
	}

	public String getEndPoint() {
		return this.mEndPoint;
	}

	public String getBucketName() {
		return this.mBucketName;
	}

	public void initConfigData() {
		conf = new ClientConfiguration();
		conf.setConnectionTimeout(ConfigLibrary.CONNECTION_TIMEOUT);
		conf.setSocketTimeout(ConfigLibrary.SOTIMEOUT);
		conf.setMaxConcurrentRequest(ConfigLibrary.MAX_CONCURRENT_TASK_NUM);
		conf.setMaxErrorRetry(ConfigLibrary.MAX_ERROR_RETRY); // 失败后最大重试次数，默认2次
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

	public OSS getOSSClient() {
		return mOSSClient;
	}

	public boolean isStop() {
		return mState == STOP_STATE;
	}

	public void setIsReverse(boolean isReverse) {
		if (mConsumeOssData != null) {
			mConsumeOssData.setIsReverse(isReverse);
		}
	}

	// public void setFederationToken(FederationToken token) {
	// OSSCredentialProvider credentialProvider;
	// credentialProvider = new OSSStsTokenCredentialProvider(
	// token.getAccessKeyId(), token.getAccessKeySecret(),
	// token.getSecurityToken());
	// mOSSClient = new OSSClient(mContext, mEndPoint, credentialProvider,
	// conf);
	// }

	public void setFederationToken(FederationToken token, String endPoint,
			String bucketName) {
		this.mEndPoint = endPoint;
		this.mBucketName = bucketName;
		OSSCredentialProvider credentialProvider;
		credentialProvider = new OSSStsTokenCredentialProvider(
				token.getAccessKeyId(), token.getAccessKeySecret(),
				token.getSecurityToken());
		mOSSClient = new OSSClient(mContext, mEndPoint, credentialProvider,
				conf);
	}

	// public void setFederationToken(OSSCredentialProvider credentialProvider)
	// {
	// mOSSClient = new OSSClient(mContext, mEndPoint, credentialProvider,
	// conf);
	// }

	public void connect() {
		mState = RUN_STATE;
		if (mProductThread != null && mProductThread.isAlive()) {
			return;
		}
		if (mConsumeThread != null && mConsumeThread.isAlive()) {
			return;
		}
		if (mOSSClient == null) {
			return;
		}
		mProductOssData = new ProductOssData(mSharedDataQueue, this);
		mProductThread = new Thread(mProductOssData);

		mConsumeOssData = new ConsumeOssData(mSharedDataQueue, this);
		mConsumeOssData.setDecoderParams(ConfigLibrary.MAX_H264_PICTURE_WIDTH,
				ConfigLibrary.MAX_H264_PICTURE_HEIGHT);
		// mConsumeOssData.initConsumeData();
		mConsumeThread = new Thread(mConsumeOssData);

		mProductThread.start();
		mConsumeThread.start();
	}

	public void playOSSObjectName(GetObjectDataModel data) {
		if (this.mObjectName != null) {
			mDeleteObjectRequest = new DeleteObjectRequest(mBucketName,
					mObjectName);
			mProductOssData.setDeleteRequest(mDeleteObjectRequest);
		}
		this.mObjectName = data.getObjectName();
		data.setBucketName(mBucketName);
		mProductOssData.setData(data);
	}

	public void playOSSObjectName(GetObjectDataModel data, boolean isFirst) {
		if (this.mObjectName != null) {
			mDeleteObjectRequest = new DeleteObjectRequest(mBucketName,
					mObjectName);
			mProductOssData.setDeleteRequest(mDeleteObjectRequest);
		}
		this.mObjectName = data.getObjectName();
		data.setBucketName(mBucketName);
		// mGetObjectRequest = new GetObjectRequest(mBucketName, mObjectName);
		if (isFirst) {
			mProductOssData.setFirstData(data);
		} else {
			mProductOssData.setData(data);
		}
	}

	protected void setClearConsumeOssData() {
		if (mConsumeOssData != null) {
			mConsumeOssData.setClearConsumeOssData();
		}
	}

	public void disconnect() {
		mState = STOP_STATE;
		if (mProductThread != null && mProductThread.isAlive()) {
			mProductThread.interrupt();
		}
		if (mConsumeThread != null && mConsumeThread.isAlive()) {
			mConsumeThread.interrupt();
		}
		if (this.mObjectName != null) {
			mDeleteObjectRequest = new DeleteObjectRequest(mBucketName,
					mObjectName);
			mOSSClient.asyncDeleteObject(mDeleteObjectRequest, null);
		}
		this.mObjectName = null;
		OSSLog.logD("disconnnect");
	}

	public void startRecord(String filePath) {

	}

	public void stopRecord() {

	}
}
