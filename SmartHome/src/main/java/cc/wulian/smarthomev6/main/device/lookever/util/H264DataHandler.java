/**
 * Project Name:  WulianOSS
 * File Name:     ConsumeOssData.java
 * Package Name:  com.wulian.oss.controller
 * @Date:         2015年10月17日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package cc.wulian.smarthomev6.main.device.lookever.util;

import com.alibaba.sdk.android.oss.common.OSSLog;
import com.wulian.h264decoder.DecoderParser;
import com.wulian.oss.ConfigLibrary;
import com.wulian.oss.model.GetOssDataModel;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import cc.wulian.smarthomev6.support.customview.VideoTextureView;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * @ClassName: ConsumeOssData
 * @Function: 消费Oss数据
 * @Date: 2015年10月17日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class H264DataHandler implements Runnable {
	private static final String TAG = "H264DataHandler";
	private boolean isSkip;
    private boolean relesae;
	private H264StreamListener mH264StreamListener;
	private BlockingQueue<Map<Long, byte[]>> mSharedDataQueue = new LinkedBlockingQueue<>(1000);
    private long lastTime;
    private long skipTime;
    private VideoTextureView view;
    public void skip(long timeStamp) {
        hasSPS = false;//另一分钟的视频，重置
        mSharedDataQueue.clear();
        this.isSkip = true;
        this.skipTime = timeStamp;
    }

    public void setData(byte[] data, long timestamp){
        try {
            Map map = new HashMap<>();
            map.put(timestamp, data);
            mSharedDataQueue.put(map);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void relesae() {
        this.relesae = true;
        mSharedDataQueue.clear();
    }

    private byte[] framePattern = new byte[]{0, 0, 0, 1};
    private byte[] headerPattern = new byte[]{0, 0, 0, 1, 103};
    private byte[] lastBuff = null;
    private boolean hasSPS;
	private void handleStream(Map<Long, byte[]> map) {
        byte[] SockBuf = null;
        long timestamp = 0;
        for(long time : map.keySet()){
            SockBuf = map.get(time);
            timestamp = time;
        }
        if(isSkip){
            lastBuff = null;
            if(view != null){
                view.clear();
            }
            if(skipTime == timestamp){
                isSkip = false;
                skipTime = 0;
            }else{
                return;
            }
        }

		if (SockBuf == null || SockBuf.length <= 0) {
			return;
		}
        int start = -1;
        int nextIndex;
        byte[] pattern;
        while (!Thread.interrupted() && !relesae) {
//            if(lastTime != timestamp){
//                hasSPS = false;//另一分钟的视频，重置
//            }
            if(hasSPS){
                pattern = framePattern;
            }else{
                pattern = headerPattern;
            }
            lastTime = timestamp;
            byte[] resultBytes = null;
            if(SockBuf.length == 1 && SockBuf[0] == 0){
//                if (isSkip) {
//                    lastBuff = null;
//                    mSharedDataQueue.clear();
//                    isSkip = false;
//                    break;
//                }
                resultBytes = lastBuff;
                lastBuff = null;
                mH264StreamListener.onH264StreamMessage(resultBytes, 600, 480, timestamp);
                break;
            }
            if ((SockBuf.length == 0 && start >= SockBuf.length)) {
                break;
            }
            long a = System.currentTimeMillis();
            nextIndex = KMPMatch(pattern, SockBuf, start == -1 ? 0 : start + 2);
//            WLog.i("luzx", "aaa:" + (System.currentTimeMillis() - a) + "ms");
            if (nextIndex == 0) {
                if(SockBuf.length > nextIndex + 4 && SockBuf[nextIndex + 4] == 103){
                    hasSPS = true;
                }else{
                    if(!hasSPS){
                        continue;
                    }
                }
                if (lastBuff != null) {
                    resultBytes = lastBuff;
                    lastBuff = null;
                }
            } else if (nextIndex > 0) {
                if(SockBuf.length > nextIndex + 4 && SockBuf[nextIndex + 4] == 103){
                    hasSPS = true;
                }else{
                    if(!hasSPS){
                        continue;
                    }
                }
                if (start < 0) {
                    if (lastBuff != null) {
                        resultBytes = new byte[lastBuff.length + nextIndex];
                        for (int i = 0; i < resultBytes.length; i++) {
                            if (i < lastBuff.length) {
                                resultBytes[i] = lastBuff[i];
                            } else {
                                resultBytes[i] = SockBuf[i - lastBuff.length];
                            }
                        }
                        lastBuff = null;
                    }
                }else{
                    resultBytes = new byte[nextIndex - start];
                    System.arraycopy(SockBuf, start, resultBytes, 0, resultBytes.length);
                }
            } else {
                if (start < 0) {
                    if (lastBuff != null) {
                        byte[] temp = new byte[lastBuff.length + SockBuf.length];
                        for (int i = 0; i < temp.length; i++) {
                            if (i < lastBuff.length) {
                                temp[i] = lastBuff[i];
                            } else {
                                temp[i] = SockBuf[i - lastBuff.length];
                            }
                        }
                        lastBuff = temp;
                    }
                } else{
                    lastBuff = new byte[SockBuf.length - start];
                    for (int i = 0; i < lastBuff.length; i++) {
                        lastBuff[i] = SockBuf[start + i];
                    }
                }
            }
            if (isSkip) {
//                isSkip = false;
                lastBuff = null;
                break;
            }
            if (resultBytes != null && resultBytes.length > 0) {
                mH264StreamListener.onH264StreamMessage(resultBytes, 600, 480, timestamp);
//                WLog.d("luzx", "onH264StreamMessage:" + resultBytes.length);
            }
            if(nextIndex == -1){
                break;
            }
            start = nextIndex;
        }
//        WLog.i("luzx", "dealBytes:" + (System.currentTimeMillis() - s) + "ms");
	}

    int KMPMatch(byte[] pattern, byte[] bytes, int start) {
        int[] lsp = computeLspTable(pattern);

        int j = 0;  // Number of chars matched in pattern
        for (int i = start; i < bytes.length; i++) {
            while (j > 0 && bytes[i] != pattern[j]) {
                // Fall back in the pattern
                j = lsp[j - 1];  // Strictly decreasing
            }
            if (bytes[i] == pattern[j]) {
                // Next char matched, increment position
                j++;
                if (j == pattern.length)
                    return i - (j - 1);
            }
        }

        return -1;  // Not found
    }

    int[] computeLspTable(byte[] pattern) {
        int[] lsp = new int[pattern.length];
        lsp[0] = 0;  // Base case
        for (int i = 1; i < pattern.length; i++) {
            // Start by assuming we're extending the previous LSP
            int j = lsp[i - 1];
            while (j > 0 && pattern[i] != pattern[j])
                j = lsp[j - 1];
            if (pattern[i] == pattern[j])
                j++;
            lsp[i] = j;
        }
        return lsp;
    }

	@Override
	public void run() {
		while (!Thread.interrupted() && !relesae) {
			try {
				Map<Long, byte[]> map= mSharedDataQueue.take();
				handleStream(map);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

    public H264DataHandler(H264StreamListener listener, VideoTextureView view){
        mH264StreamListener = listener;
        this.view = view;
    }

	public interface H264StreamListener {
		void onH264StreamMessage(byte[] data, int width, int height, long time);// 返回H264流消息
	}
}
