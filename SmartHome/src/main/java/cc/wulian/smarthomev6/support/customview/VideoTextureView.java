package cc.wulian.smarthomev6.support.customview;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import cc.wulian.smarthomev6.main.device.lookever.util.H264DataHandler;
import cc.wulian.smarthomev6.support.utils.WLog;


public class VideoTextureView extends TextureView implements TextureView.SurfaceTextureListener {
    private Surface mSurface;
    private BlockingQueue<Map<Long, byte[]>> mSharedDataQueue = new LinkedBlockingQueue<>(1000);
    private DecodeThread mDecodeThread;
    private H264DataHandler mH264DataHandler;
    private MediaCodec mCodec;
    private MediaFormat mediaformat;
    private boolean mStopFlag = false;
    private int width = 1280;
    private int height = 720;
    private OnPlayListener mOnPlayListener;
    private final static int TIME_INTERNAL = 100;
    private boolean isPause = false;
    private boolean skip = false;
    private long lastTimeStamp;
    private long skipTime;
//    private int mCount = 1;

    public VideoTextureView(Context context) {
        super(context);
        setSurfaceTextureListener(this);
    }

    public VideoTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSurfaceTextureListener(this);
    }

    private void init() {
        try {
            //通过多媒体格式名创建一个可用的解码器
            mCodec = MediaCodec.createDecoderByType("video/avc");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //获取h264中的pps及sps数据
        byte[] header_sps = {0,0,0,1,103,66,0,31,-107,-88,20,1,110,64};
        byte[] header_pps = {0,0,0,1,104,-50,60,-128};
//        byte[] header_sps = {0, 0, 0, 1, 103, 66, 0, 42, (byte) 149, (byte) 168, 30, 0, (byte) 137, (byte) 249, 102, (byte) 224, 32, 32, 32, 64};
//        byte[] header_pps = {0, 0, 0, 1, 104, (byte) 206, 60, (byte) 128, 0, 0, 0, 1, 6, (byte) 229, 1, (byte) 151, (byte) 128};
//        int width = (H264SPSPaser.ue(header_sps,34) + 1)*16;
//        int height = (H264SPSPaser.ue(header_sps,-1) + 1)*16;
//        WLog.i("lzx", "width:" + width + " height:" + height);
        //初始化编码器
        mediaformat = MediaFormat.createVideoFormat("video/avc", width, height);
        mediaformat.setByteBuffer("csd-0", ByteBuffer.wrap(header_sps));
        mediaformat.setByteBuffer("csd-1", ByteBuffer.wrap(header_pps));
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        width = MeasureSpec.getSize(widthMeasureSpec);
//        height = MeasureSpec.getSize(heightMeasureSpec);
//    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mSurface = new Surface(surface);
        if (mCodec == null) {
            init();
            mCodec.configure(mediaformat, mSurface, null, 0);
            mCodec.start();
        }
        isPause = false;
        if (mDecodeThread != null) {
            mDecodeThread.go();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if(mCodec != null){
            mCodec.release();
            mCodec = null;
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    public interface OnPlayListener {
        void onStart(long time);

        void onBuffer();
    }

    public void setFrameData(byte[] data, long timestamp){
        try {
            if(skip && skipTime != timestamp){
                return;
            }
            Map map = new HashMap<>();
            map.put(timestamp, data);
            mSharedDataQueue.put(map);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setData(byte[] data, long timestamp){
        if(mH264DataHandler != null){
            if(skip && skipTime != timestamp){
                return;
            }
            Map map = new HashMap<>();
            map.put(timestamp, data);
            mH264DataHandler.setData(data, timestamp);
        }
    }

    public void setOnPlayListener(OnPlayListener onPlayListener) {
        mOnPlayListener = onPlayListener;
    }

    public void startPlay() {
        if(mH264DataHandler == null){
            mH264DataHandler = new H264DataHandler(new H264DataHandler.H264StreamListener() {
                @Override
                public void onH264StreamMessage(byte[] data, int width, int height, long time) {
                    if(mDecodeThread == null){
                        mDecodeThread = new DecodeThread();
                        mDecodeThread.start();
                    }
                    setFrameData(data, time);
                }
            }, this);
            new Thread(mH264DataHandler).start();
        }
    }

    /**
     * @author ldm
     * @description 解码线程
     * @time 2016/12/19 16:36
     */
    private class DecodeThread extends Thread {

        @Override
        public void run() {
            try {
                decodeLoop();
            } catch (Exception e) {
                e.printStackTrace();
                WLog.e("luzx", e.getMessage());
            }
        }

        public synchronized void go() {
            this.notify();
        }

        private synchronized void decodeLoop() {
            lastTimeStamp = 0;
            //解码后的数据，包含每一个buffer的元数据信息，例如偏差，在相关解码器中有效的数据大小
            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            long timeoutUs = 10000;
//            byte[] marker0 = new byte[]{0, 0, 0, 1};
            int bytes_cnt;
            byte[] streamBuffer = null;
            long timestamp = 0;
            while (mStopFlag == false && !Thread.interrupted()) {
                try {
                    if(isPause){
                        this.wait();
                    }
                    if (mSharedDataQueue != null) {
                        if (mSharedDataQueue.size() == 0 && mOnPlayListener != null) {
//                            start = true;
                            mOnPlayListener.onBuffer();
                            WLog.i("lzx","onBuffer");
                        }
                        Map<Long, byte[]> map = mSharedDataQueue.take();
                        for(long time : map.keySet()){
                            streamBuffer = map.get(time);
                            timestamp = time;
                        }
                        if(skip){
                            if(skipTime == timestamp){
                                skip = false;
                                skipTime = 0;
                            }else{
                                continue;
                            }
                        }
//                        WLog.i("luzx", "take:" + "timestamp:" + timestamp + "-" + streamBuffer.length + " " + streamBuffer[0] + " " + streamBuffer[1] + " " + streamBuffer[2] + " " + streamBuffer[3] + " " + streamBuffer[4]);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bytes_cnt = streamBuffer == null ? 0 : streamBuffer.length;
                if (bytes_cnt != 0) {
//                    int startIndex = 0;
//                    int nextFrameStart = 0;
//                    int remaining = bytes_cnt;
                    //存放目标文件的数据
//                    while (!mStopFlag && mSharedDataQueue != null) {
                        try {
//                            if (remaining == 0 || startIndex >= remaining) {
//                                break;
//                            }
//                            nextFrameStart = KMPMatch(marker0, streamBuffer, startIndex + 2, remaining);
//                            if (nextFrameStart == -1) {
//                                nextFrameStart = remaining;
//                            }
//                            byte[] data = getData(streamBuffer);
                            int inIndex = -1;
                            ByteBuffer[] inputBuffers = null;
                            if (mCodec != null) {
                                inIndex = mCodec.dequeueInputBuffer(timeoutUs);
                                if (inputBuffers == null) {
                                    inputBuffers = mCodec.getInputBuffers();
                                }
                            }
                            WLog.i("inIndex:","inIndex:" + inIndex);
                            if (inIndex >= 0 && inputBuffers != null && inputBuffers.length > 0) {
                                ByteBuffer byteBuffer = inputBuffers[inIndex];
                                byteBuffer.clear();
//                                byteBuffer.put(data);
                                byteBuffer.put(streamBuffer, 0, streamBuffer.length);
                                byte[] data = streamBuffer;
//                                System.arraycopy(streamBuffer, startIndex, data, 0, data.length);
                                String s = "{";
//                                for (int i = 0; i < (data.length > 20 ? 20 : data.length); i++) {
//                                    s += data[i] + ",";
//                                }
//                                WLog.i("luzx", (data[4] & 0x1f) + "  " + s + "}");
//                                byteBuffer.put(data);
//                                if (mCodec != null) {
//                                    //在给指定Index的inputbuffer[]填充数据后，调用这个函数把数据传给解码器\
                                    switch (data[4] & 0x1f) {
                                        case 7:
                                            for (int i = 0; i < data.length; i++) {
                                                s += data[i] + ",";
                                            }
                                            WLog.i("take", (data[4] & 0x1f) + "：" + s + "}");
                                            mCodec.queueInputBuffer(inIndex, 0, streamBuffer.length, 0, MediaCodec.BUFFER_FLAG_CODEC_CONFIG);
                                            break;
                                        case 8:
                                            for (int i = 0; i < data.length; i++) {
                                                s += data[i] + ",";
                                            }
                                            WLog.i("take", (data[4] & 0x1f) + "：" + s + "}");
                                            mCodec.queueInputBuffer(inIndex, 0, streamBuffer.length, 0, MediaCodec.BUFFER_FLAG_CODEC_CONFIG);
                                            break;
                                        default:
                                            mCodec.queueInputBuffer(inIndex, 0, streamBuffer.length, 0, 0);
//                                            mCodec.queueInputBuffer(inIndex, 0, streamBuffer.length, mCount * TIME_INTERNAL, 0);
//                                            mCount++;
                                            break;
                                    }
//
//                                }
//                                startIndex = nextFrameStart;
                            } else {
                                continue;
                            }
//                            //帧控制是不在这种情况下工作，因为没有PTS H264是可用的
//                            while (info.presentationTimeUs >= System.currentTimeMillis() - lastTimeStamp) {
//                                try {
//                                    Thread.sleep(10);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                            }
                            int outIndex = -1;
                            if (mCodec != null) {
                                outIndex = mCodec.dequeueOutputBuffer(info, timeoutUs);
                            }
                            WLog.i("outIndex:","outIndex:" + outIndex);
                            if (outIndex >= 0) {
                                //对outputbuffer的处理完后，调用这个函数把buffer重新返回给codec类。
                                if (skip) {
                                    if (mCodec != null) {
                                        mCodec.releaseOutputBuffer(outIndex, false);
                                    }
                                } else {
                                    boolean doRender = (info.size != 0);
                                    if (mCodec != null) {
                                        long interval = System.currentTimeMillis() - lastTimeStamp;
//                                      WLog.i("luzx", "take:interval:" + interval);
                                        if(interval < TIME_INTERNAL){
                                            Thread.sleep(TIME_INTERNAL - interval);
                                        }
                                        mCodec.releaseOutputBuffer(outIndex, doRender);
                                        lastTimeStamp = System.currentTimeMillis();
                                    }
                                    if (mOnPlayListener != null) {
                                        mOnPlayListener.onStart(timestamp);
                                        WLog.i("lzx","onStart");
                                    }
                                }
                            } else if (outIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
//                                outputBuffers = mCodec.getOutputBuffers();
                            } else if (outIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                                // Subsequent data will conform to new format.
                                MediaFormat format = mCodec.getOutputFormat();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//                    }
                }
            }
        }
    }

    public void stop(){
        this.isPause = true;
    }

    public void skip(long timeStamp) {
        if(mCodec != null){
            mCodec.flush();
        }
        this.skipTime = timeStamp;
        this.skip = true;
        if(mH264DataHandler != null){
            mH264DataHandler.skip(timeStamp);
        }
    }

    public void clear(){
        mSharedDataQueue.clear();
    }

    public void relesae() {
        mStopFlag = true;
        if(mCodec != null){
            mCodec.release();
            mCodec = null;
        }
        if(mH264DataHandler != null){
            mH264DataHandler.relesae();
        }
        if(mDecodeThread != null){
            mDecodeThread.interrupt();
            mDecodeThread = null;
        }
        mSharedDataQueue.clear();
    }

//    int KMPMatch(byte[] pattern, byte[] bytes, int start, int remain) {
//        try {
//            Thread.sleep(TIME_INTERNAL);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        int[] lsp = computeLspTable(pattern);
//
//        int j = 0;  // Number of chars matched in pattern
//        for (int i = start; i < remain; i++) {
//            while (j > 0 && bytes[i] != pattern[j]) {
//                // Fall back in the pattern
//                j = lsp[j - 1];  // Strictly decreasing
//            }
//            if (bytes[i] == pattern[j]) {
//                // Next char matched, increment position
//                j++;
//                if (j == pattern.length)
//                    return i - (j - 1);
//            }
//        }
//
//        return -1;  // Not found
//    }

//    int[] computeLspTable(byte[] pattern) {
//        int[] lsp = new int[pattern.length];
//        lsp[0] = 0;  // Base case
//        for (int i = 1; i < pattern.length; i++) {
//            // Start by assuming we're extending the previous LSP
//            int j = lsp[i - 1];
//            while (j > 0 && pattern[i] != pattern[j])
//                j = lsp[j - 1];
//            if (pattern[i] == pattern[j])
//                j++;
//            lsp[i] = j;
//        }
//        return lsp;
//    }

}
