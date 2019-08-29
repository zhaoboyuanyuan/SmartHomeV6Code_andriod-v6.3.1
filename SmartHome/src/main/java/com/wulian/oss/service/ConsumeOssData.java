/**
 * Project Name:  WulianOSS
 * File Name:     ConsumeOssData.java
 * Package Name:  com.wulian.oss.controller
 * @Date:         2015年10月17日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.oss.service;

import java.util.concurrent.BlockingQueue;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.wulian.h264decoder.DecoderParser;
import com.wulian.oss.ConfigLibrary;
import com.wulian.oss.model.GetOssDataModel;

/**
 * @ClassName: ConsumeOssData
 * @Function: 消费Oss数据
 * @Date: 2015年10月17日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class ConsumeOssData implements Runnable {
	private boolean mStreamInit = false;
	private int mTrans = 0x0F0F0F0F;
	private int mMaxWidth;// 此处设定不同的分辨率
	private int mMaxHeight;// 默认值
	private int iTemp = 0;
	private int nalLen;
	private static final String TAG="ConsumeOssData";

	private boolean isJustDebug = false;
	private long mPreTimeStamp = -1;
	private long mBeginTimeStampOneMin = 0;
	private long mTotalFrameCounts = 0;

	private boolean bFirst = true;
	private boolean bFindPPS = true;
	private int NalBufUsed = 0;
	private int SockBufUsed = 0;
	private byte[] mPixel;

	// private byte[] NalBuf = new byte[81960]; // 80k
	private byte[] NalBuf = new byte[81960 * 5]; // 80k
	private int[] NalResolution = new int[2];//
	private boolean mIsClear=false;

	// byte[] SockBuf = new byte[ConfigLibrary.DEFAULT_GET_OBJECT_LENGTH];

	/***********************************************/
	private void initH264Stream() {
		DecoderParser.InitDecoder();
		mPixel = new byte[mMaxWidth * mMaxHeight * 2];
		int i = mPixel.length;
		for (i = 0; i < mPixel.length; i++) {
			mPixel[i] = (byte) 0x00;
		}
	}
	
	public void initConsumeData() {
		if (!mStreamInit) {
			initH264Stream();
			mStreamInit = true;
		}
	}

	private void handleStream(byte[] SockBuf, int readLength) {
		if (readLength <= 0) {
			return;
		}
		initConsumeData();
		SockBufUsed = 0;
		while (readLength - SockBufUsed > 0 && !mClient.isStop()&&!mIsClear) {
			nalLen = MergeBuffer(NalBuf, NalBufUsed, SockBuf, SockBufUsed,
					readLength - SockBufUsed);
			NalBufUsed += nalLen;
			SockBufUsed += nalLen;
			while (mTrans == 1 && !mClient.isStop()&&!mIsClear) {
				mTrans = 0xFFFFFFFF;
				if (bFirst == true) {
					bFirst = false;
				} else {
					if (bFindPPS == true) {
						if ((NalBuf[4] & 0x1F) == 7) {
							bFindPPS = false;
						} else {
							NalBuf[0] = 0;
							NalBuf[1] = 0;
							NalBuf[2] = 0;
							NalBuf[3] = 1;
							NalBufUsed = 4;
							break;
						}
					}
					NalResolution[0] = 0;// Width
					NalResolution[0] = 1;// Height
					iTemp = DecoderParser.DecoderNal(NalBuf, NalBufUsed - 4,mIsReverse,
							mPixel, NalResolution);

					if (iTemp > 0) {
						long timePeriod = CalcFrameTimestampPeriod();
						if (timePeriod > 0) {
							try {
								Thread.sleep(timePeriod, 0);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						mClient.getListener().onH264StreamMessage(mPixel,
								NalResolution[0], NalResolution[1]);
						mTotalFrameCounts++;
					}
				}
				NalBuf[0] = 0;
				NalBuf[1] = 0;
				NalBuf[2] = 0;
				NalBuf[3] = 1;
				NalBufUsed = 4;
			}
		}
	}

	protected void setClearConsumeOssData() {
		mTotalFrameCounts = 0;
//		NalBufUsed = 0;
//		SockBufUsed = 0;
//		for (int i = 0; i < mPixel.length; i++) {
//			mPixel[i] = (byte) 0x00;
//		}
		mIsClear=true;
		mBeginTimeStampOneMin = System.currentTimeMillis();
	}

	private long CalcFrameTimestampPeriod() {
		long result = 0;
		long nowTime = System.currentTimeMillis();
		int tempCount = (int) (mTotalFrameCounts % (ConfigLibrary.DEFAULT_VIDEO_FPS));
		long shouldTimeStamp = mBeginTimeStampOneMin
				+ (mTotalFrameCounts / (ConfigLibrary.DEFAULT_VIDEO_FPS))
				* 1000 + (tempCount == 0 ? ConfigLibrary.DEFAULT_LAST_FRAME_VIDEO_PERIOD_TIMESTAMP
				: tempCount * ConfigLibrary.DEFAULT_VIDEO_PERIOD_TIMESTAMP);
		
		result = shouldTimeStamp - nowTime;
//		Log.d("PML", "Now Time is:"+nowTime+";shouldTimeStamp is:"+shouldTimeStamp+";result is:"+result);
		return result;
	}

//	private long CalcFrameTimestampPeriod1() {
//		long result = 0;
//		long nowTime = System.currentTimeMillis();
////		Log.d("PML", "mTotalFrameCounts is:" + mTotalFrameCounts
////				+ "NowTime is:" + nowTime);
//		if (mPreTimeStamp > 0) {
//			result = (mTotalFrameCounts % ConfigLibrary.DEFAULT_VIDEO_FPS == 0 ? ConfigLibrary.DEFAULT_LAST_FRAME_VIDEO_PERIOD_TIMESTAMP
//					: ConfigLibrary.DEFAULT_VIDEO_PERIOD_TIMESTAMP)
//					- (nowTime - mPreTimeStamp);
//		}
//		mPreTimeStamp = nowTime;
//		return result;
//	}

	private int MergeBuffer(byte[] NalBuf, int NalBufUsed, byte[] SockBuf,
			int SockBufUsed, int SockRemain) {
		int i = 0;
		byte Temp;

		for (i = 0; i < SockRemain; i++) {
			Temp = SockBuf[i + SockBufUsed];
			NalBuf[i + NalBufUsed] = Temp;
			mTrans <<= 8;
			mTrans |= Temp;
			if (mTrans == 1) // 找到一个开始字
			{
				i++;
				break;
			}
		}
		return i;
	}

	private final BlockingQueue<GetOssDataModel> mSharedQueue;
	private WulianOssClient mClient;
	private boolean mIsReverse;

	public void setIsReverse(boolean isReverse) {
		mIsReverse=isReverse;
	}
	
	public ConsumeOssData(BlockingQueue<GetOssDataModel> sharedQueue,
			WulianOssClient client) {
		this.mSharedQueue = sharedQueue;
		this.mClient = client;
		this.mIsReverse=false;
	}

	public void setDecoderParams(int width, int height) {
		mMaxWidth = width;
		mMaxHeight = height;
	}

	@Override
	public void run() {
		while (!mClient.isStop() && !Thread.interrupted()) {
			try {
				GetOssDataModel data = mSharedQueue.take();
				if(isJustDebug) {
				//	Log.d(TAG, "getData Length is:"+data.getReadLength());
				}else {
					OSSLog.logD("getData Length is:"+data.getReadLength());
				}
				mIsClear=false;
				handleStream(data.getData(), data.getReadLength());
			} catch (InterruptedException e) {
//				e.printStackTrace();
			}
		}
		if (mStreamInit) {
			DecoderParser.UninitDecoder();
		}
		mClient.getListener().onDisconnect(0, "EOF");
	}

}
