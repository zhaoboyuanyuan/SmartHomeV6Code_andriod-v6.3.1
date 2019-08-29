/**
 * Project Name:  WulianOSS
 * File Name:     ProductOssData.java
 * Package Name:  com.wulian.oss.controller
 * @Date:         2015年10月17日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.oss.service;

import android.text.TextUtils;

import com.wulian.oss.ConfigLibrary;
import com.wulian.oss.model.GetObjectDataModel;
import com.wulian.oss.model.GetOssDataModel;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * @ClassName: ProductOssData
 * @Function: 生产Oss数据
 * @Date: 2015年10月17日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class ProductOssDataSimple implements Runnable {
	private final BlockingQueue<GetOssDataModel> mSharedQueue;
	private WulianOssSimpleClient mClient;
	// private GetObjectRequest mOssData;
	private GetObjectDataModel mGetObjectData;

	private byte[] mSockBuf;

	private boolean isGetData;

	private final static String TAG = "ProductOssData";

	public ProductOssDataSimple(BlockingQueue<GetOssDataModel> sharedQueue,
                                WulianOssSimpleClient client) {
		this.mSharedQueue = sharedQueue;
		this.mClient = client;
		this.mGetObjectData = null;

	}

	public void setData(GetObjectDataModel data) {
		this.mGetObjectData = data;
	}

	public void run() {
		FileInputStream fileIS = null;
		if(mGetObjectData==null||TextUtils.isEmpty(mGetObjectData.getFilePath())) {
			mClient.getListener().onDisconnect(0, "EOF");
			return ;
		}
		String PathFileName = mGetObjectData.getFilePath();
		try {
			fileIS = new FileInputStream(PathFileName);
		} catch (IOException e) {
			mClient.getListener().onDisconnect(0, "EOF");
			e.printStackTrace();
			return;
		}
		mSockBuf = new byte[ConfigLibrary.DEFAULT_GET_OBJECT_LENGTH];
		isGetData = false;
		while (!mClient.isStop()) {
			if (mGetObjectData != null) {
				try {
					int bytecount = fileIS.read(mSockBuf, 0,
							ConfigLibrary.DEFAULT_GET_OBJECT_LENGTH);
					if (bytecount <= 0)
						break;
					if (!isGetData) {
						mSharedQueue.clear();
						mClient.getListener().onRequestGetObjectResultOK(
								mGetObjectData.getTimeStamp());
					}
					isGetData = true;
					GetOssDataModel ossData = new GetOssDataModel(mSockBuf,
							mSockBuf.length);
					mSharedQueue.put(ossData);
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			}
		}
		mClient.getListener().onDisconnect(0, "EOF");
	}

}
