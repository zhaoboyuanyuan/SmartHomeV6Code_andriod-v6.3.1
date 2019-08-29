/**
 * Project Name:  WulianOSS
 * File Name:     ConsumeOssData.java
 * Package Name:  com.wulian.oss.controller
 * @Date:         2015年10月17日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.oss.service;

import android.util.Log;

import com.wulian.h264decoder.DecoderParser;
import com.wulian.oss.ConfigLibrary;
import com.wulian.oss.model.GetOssDataModel;
import com.wulian.sdk.android.ipc.rtcv2.IPCController;

import java.util.concurrent.BlockingQueue;

/**
 * @ClassName: ConsumeOssData
 * @Function: 消费Oss数据
 * @Date: 2015年10月17日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class ConsumeOssDataSimple implements Runnable {
	private boolean mStreamInit = false;
	private int mTrans = 0x0F0F0F0F;
	private int mMaxWidth;// 此处设定不同的分辨率
	private int mMaxHeight;// 默认值
	private int iTemp = 0;
	private int nalLen;

	private long mPreTimeStamp = -1;
	private long mTotalFrameCounts = 0;

	private boolean isNewRtc = false;
	private boolean bFirst = true;
	private boolean bFindPPS = true;
	private int NalBufUsed = 0;
	private int SockBufUsed = 0;
	private byte[] mPixel;

	// private byte[] NalBuf = new byte[81960]; // 80k
	private byte[] NalBuf = new byte[81960 * 4]; // 80k
	private int[] NalResolution = new int[2];//

	// byte[] SockBuf = new byte[ConfigLibrary.DEFAULT_GET_OBJECT_LENGTH];

	/***********************************************/
	private void initH264Stream() {
		if (isNewRtc) {

		} else {
			DecoderParser.InitDecoder();
		}
		mPixel = new byte[mMaxWidth * mMaxHeight * 2];
		int i = mPixel.length;
		for (i = 0; i < mPixel.length; i++) {
			mPixel[i] = (byte) 0x00;
		}
	}

	private void handleStream(byte[] SockBuf, int readLength) {
		if (readLength <= 0) {
			return;
		}
		if (!mStreamInit) {
			initH264Stream();
			mStreamInit = true;
		}
		SockBufUsed = 0;
		while (readLength - SockBufUsed > 0) {
			nalLen = MergeBuffer(NalBuf, NalBufUsed, SockBuf, SockBufUsed,
					readLength - SockBufUsed);
			NalBufUsed += nalLen;
			SockBufUsed += nalLen;
			while (mTrans == 1) {
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
					if (isNewRtc) {
						iTemp = IPCController
								.DecoderNal(NalBuf, NalBufUsed - 4);
						Log.d("PML", "DecoderNal Length is:"+(NalBufUsed - 4)+";iTemp is:"+iTemp);
						if (iTemp >= 0) {
							long timePeriod = CalcFrameTimestampPeriod();
							if (timePeriod > 0) {
								try {
									Thread.sleep(timePeriod, 0);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							mTotalFrameCounts++;
						}
					} else {
						iTemp = DecoderParser.DecoderNal(NalBuf,
								NalBufUsed - 4, false, mPixel, NalResolution);
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
					// Log.d("PML", "NalResolution is:" + NalResolution[0]
					// + ";" + NalResolution[1]);

				}
				NalBuf[0] = 0;
				NalBuf[1] = 0;
				NalBuf[2] = 0;
				NalBuf[3] = 1;
				NalBufUsed = 4;
			}
		}
	}

	private long CalcFrameTimestampPeriod() {
		long result = 0;
		long nowTime = System.currentTimeMillis();
		if (mPreTimeStamp > 0) {
			result = (mTotalFrameCounts % ConfigLibrary.DEFAULT_VIDEO_FPS == 0 ? ConfigLibrary.DEFAULT_LAST_FRAME_VIDEO_PERIOD_TIMESTAMP
					: ConfigLibrary.DEFAULT_VIDEO_PERIOD_TIMESTAMP)
					- (nowTime - mPreTimeStamp);
		}
		mPreTimeStamp = nowTime;
		return result;
	}

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
	private WulianOssSimpleClient mClient;

	public ConsumeOssDataSimple(BlockingQueue<GetOssDataModel> sharedQueue,
                                WulianOssSimpleClient client) {
		this.mSharedQueue = sharedQueue;
		this.mClient = client;
	}

	public void setDecoderParams(int width, int height) {
		mMaxWidth = width;
		mMaxHeight = height;
	}

	@Override
	public void run() {
		while (!mClient.isStop()) {
			try {
				GetOssDataModel data = mSharedQueue.take();
				// Log.d("PML", "Consume byteCount is:" + data.getReadLength());
				handleStream(data.getData(), data.getReadLength());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (mStreamInit) {
			if (isNewRtc) {
				
			} else {
				DecoderParser.UninitDecoder();
			}
		}
		mClient.getListener().onDisconnect(0, "EOF");
	}
	
static {
	try {
		System.loadLibrary("WulianICamOpenH264");
		System.loadLibrary("openh264");
	}catch(UnsatisfiedLinkError e) {
		e.printStackTrace();
	}
}

}
