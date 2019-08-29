/**
 * Project Name:  WulianOSS
 * File Name:     ProductOssData.java
 * Package Name:  com.wulian.oss.controller
 *
 * @Date: 2015年10月17日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.oss.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.BlockingQueue;


import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.DeleteObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.HeadObjectRequest;
import com.alibaba.sdk.android.oss.model.HeadObjectResult;
import com.alibaba.sdk.android.oss.model.Range;
import com.wulian.oss.ConfigLibrary;
import com.wulian.oss.model.GetObjectDataModel;
import com.wulian.oss.model.GetOssDataModel;

/**
 * @author Puml
 * @ClassName: ProductOssData
 * @Function: 生产Oss数据
 * @Date: 2015年10月17日
 * @email puml@wuliangroup.cn
 */
public class ProductOssData implements Runnable {
    private final BlockingQueue<GetOssDataModel> mSharedQueue;
    private WulianOssClient mClient;
    // private GetObjectRequest mOssData;
    private GetObjectDataModel mGetObjectData;
    private int mBeginSizeFlag;
    private int mEndSizeFlag;
    private int mNotFoundCount;
    private int mNotGetStreamCount;
    private DeleteObjectRequest mDeleteObjectRequest;
    private byte[] mSockBuf;
    private boolean isChange;
    private boolean isGetStreamTimeOut;
    private boolean isGetData;
    private boolean isJustDebug = true;
    private final static String TAG = "ProductOssData";
    private int mRequestCount;

    // FileOutputStream fs = null;

    public ProductOssData(BlockingQueue<GetOssDataModel> sharedQueue,
                          WulianOssClient client) {
        this.mSharedQueue = sharedQueue;
        this.mClient = client;
        // this.mOssData = null;
        this.mGetObjectData = null;
        this.mDeleteObjectRequest = null;
        this.isChange = false;
    }

    public void setData(GetObjectDataModel data) {
        // this.mOssData = request;
        this.mGetObjectData = data;
        this.isChange = true;
        this.mRequestCount = ConfigLibrary.DEFAUTL_WAIT_TIME_OUT_COUNTS;
    }

    public void setFirstData(GetObjectDataModel data) {
        // this.mOssData = request;
        this.mGetObjectData = data;
        this.isChange = true;
        this.mRequestCount = ConfigLibrary.DEFAUTL_FIRST_WAIT_TIME_OUT_COUNTS;
    }

    public void setDeleteRequest(DeleteObjectRequest deleteData) {
        this.mDeleteObjectRequest = deleteData;
        if (mDeleteObjectRequest != null) {
            mClient.getOSSClient()
                    .asyncDeleteObject(mDeleteObjectRequest, null);
        }
    }
//
//	public void run1() {
//		FileInputStream fileIS = null;
//		// String PathFileName =
//		// "/mnt/sdcard/iCam/H264/20160114_18-25-44_1452767144_time.h264";
//		// String PathFileName =
//		// "/mnt/sdcard/iCam/H264/20160204_21-26-12_1454592372_time.h264";
//		String PathFileName = "/mnt/sdcard/iCam/H264/2016-03-21-10-54-time.h264";
//		// String PathFileName = "/mnt/sdcard/iCam/H264/MR1_BT_A.h264";
//		try {
//			fileIS = new FileInputStream(PathFileName);
//		} catch (IOException e) {
//			e.printStackTrace();
//			return;
//		}
//		mSockBuf = new byte[ConfigLibrary.DEFAULT_GET_OBJECT_LENGTH];
//		isGetData = false;
//		try {
//			fileIS.skip(0);
//		} catch (IOException e2) {
//			e2.printStackTrace();
//		}
//		while (!mClient.isStop()) {
//			if (mGetObjectData != null) {
//				try {
//					int bytecount = fileIS.read(mSockBuf, 0,
//							ConfigLibrary.DEFAULT_GET_OBJECT_LENGTH);
//					if (bytecount <= 0)
//						break;
//					if (!isGetData) {
//						mSharedQueue.clear();
//						mClient.setClearConsumeOssData();
//						mClient.getListener().onRequestGetObjectResultOK(
//								mGetObjectData.getTimeStamp());
//					}
//					isGetData = true;
//					GetOssDataModel ossData = new GetOssDataModel(mSockBuf,
//							mSockBuf.length);
//					mSharedQueue.put(ossData);
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				} catch (NullPointerException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		mClient.getListener().onDisconnect(0, "EOF");
//	}

    public void run() {
        mSockBuf = new byte[ConfigLibrary.DEFAULT_GET_OBJECT_LENGTH];
        while (!mClient.isStop() && !Thread.interrupted()) {
            if (mGetObjectData != null && isChange) {
                mNotFoundCount = 0;
                isChange = false;
                isGetData = false;
                isGetStreamTimeOut = false;
                while (!mClient.isStop() && !isGetData
                        && mNotFoundCount < mRequestCount && !isChange
                        && !Thread.interrupted() && !isGetStreamTimeOut) {
                    try {
                        if (mClient.getOSSClient().doesObjectExist(
                                mGetObjectData.getBucketName(),
                                mGetObjectData.getObjectName())) {
                            GetObjectRequest mOssData = new GetObjectRequest(
                                    mGetObjectData.getBucketName(),
                                    mGetObjectData.getObjectName());
                            HeadObjectRequest mHeadData = new HeadObjectRequest(
                                    mGetObjectData.getBucketName(),
                                    mGetObjectData.getObjectName());
                            int size = mGetObjectData.getFileSize();
                            long AllContentLength = 0;
                            mBeginSizeFlag = 0;
                            mEndSizeFlag = 0;
                            while (!mClient.isStop()
                                    && mBeginSizeFlag <= size - 1 && !isChange
                                    && !Thread.interrupted()
                                    && !isGetStreamTimeOut) {
                                HeadObjectResult mHeadObjectResult = mClient
                                        .getOSSClient().headObject(mHeadData);
                                long tempAllContentLength = mHeadObjectResult
                                        .getMetadata().getContentLength();
                                Log.d(TAG, "tempAllContentLength is:" + tempAllContentLength + ";mBeginSizeFlag is:" + mBeginSizeFlag + ";size is:" + size);
                                if (tempAllContentLength <= mBeginSizeFlag + 1) {
                                    if (tempAllContentLength == AllContentLength) {
                                        mNotGetStreamCount++;
                                    } else {
                                        mNotGetStreamCount = 0;
                                    }
                                    Log.d(TAG, "mNotGetStreamCount is:" + mNotGetStreamCount);
                                    if (mNotGetStreamCount == ConfigLibrary.DEFAUTL_WAIT_TIME_OUT_COUNTS) {
                                        isGetStreamTimeOut = true;
                                    } else {
                                        AllContentLength = tempAllContentLength;
                                        Thread.sleep(ConfigLibrary.DEFAULT_TIME_OUT_PER_REQUEST);
                                    }
                                } else {
                                    while (!mClient.isStop() && !isChange && tempAllContentLength > mBeginSizeFlag + 1) {
                                        mEndSizeFlag = mBeginSizeFlag
                                                + ConfigLibrary.DEFAULT_GET_OBJECT_RANGE_LENGTH;
                                        if (mEndSizeFlag > tempAllContentLength) {
                                            mEndSizeFlag = (int) (tempAllContentLength - 1);
                                        }
                                        if (mEndSizeFlag > size - 1) {
                                            mEndSizeFlag = size - 1;
                                        }
                                        mOssData.setRange(new Range(
                                                mBeginSizeFlag, mEndSizeFlag));
                                        Log.d(TAG, "mOssData.setRange mBeginSizeFlag is:" + mBeginSizeFlag + ";mEndSizeFlag is:" + mEndSizeFlag);
                                        GetObjectResult getResult = mClient
                                                .getOSSClient().getObject(
                                                        mOssData);
                                        Log.d(TAG, "getResult.getStatusCode():" + getResult.getStatusCode());
                                        if (getResult.getStatusCode() == 206) {
                                            if (!isGetData) {
                                                mSharedQueue.clear();
                                                mClient.setClearConsumeOssData();
                                                mClient.getListener()
                                                        .onRequestGetObjectResultOK(
                                                                mGetObjectData
                                                                        .getTimeStamp());
                                            }
                                            isGetData = true;
                                            Map<String, String> map = getResult
                                                    .getResponseHeader();
                                            Log.d(TAG, "mapis :" + map.get("Content-Length"));
                                            int endPosition = 0;
                                            try {
												endPosition = Integer
														.parseInt(map
																.get("x-oss-next-append-position"));
//                                                endPosition = Integer
//                                                        .parseInt(map
//                                                                .get("Content-Length"));
                                                Log.d(TAG, "endPositionis:" + endPosition);
                                            } catch (NumberFormatException e) {
                                                e.printStackTrace();
                                            }
                                            if (endPosition > mEndSizeFlag) {
                                                mBeginSizeFlag += getResult
                                                        .getContentLength();
                                                InputStream inputStream = getResult
                                                        .getObjectContent();
                                                Log.d(TAG, "InputStream inputStream = getResult mBeginSizeFlag is:" + mBeginSizeFlag);
                                                int len;
                                                while (!isChange
                                                        && !mClient.isStop()
                                                        && !Thread
                                                        .interrupted()) {
                                                    len = inputStream
                                                            .read(mSockBuf);
                                                    if (len <= 0
                                                            || len > ConfigLibrary.DEFAULT_GET_OBJECT_LENGTH) {
//														OSSLog.logD(" inputStream.read not good");
                                                        break;
                                                    }
                                                    mSharedQueue
                                                            .put(new GetOssDataModel(
                                                                    mSockBuf,
                                                                    len));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            Thread.sleep(ConfigLibrary.DEFAULT_TIME_OUT_PER_REQUEST);
                            mNotFoundCount++;
//							Log.d(TAG, "mNotFoundCount is:"+mNotFoundCount);
                        }
                    } catch (ClientException e2) {
                        // 本地异常如网络异常等
                        e2.printStackTrace();
                    } catch (ServiceException e2) {
                        e2.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
