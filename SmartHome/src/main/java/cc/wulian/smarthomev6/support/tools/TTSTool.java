package cc.wulian.smarthomev6.support.tools;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;

import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.utils.FileUtil;
import cc.wulian.smarthomev6.support.utils.LanguageUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by zbl on 2017/7/14.
 * 科大讯飞语音合成
 */

public class TTSTool {

    private static final String TAG = "TTSTool";
    public static final int MANDARIN = 0;
    public static final int CANTONESE = 1;
    public static final int DIALECT_NORTHEAST_AREA = 2;
    public static final int HENAN_DIALECT = 3;
    public static final int SICHUAN_DIALECT = 4;
    public static final int FORMOSAN = 5;
    public static final int QIN_DYNASTY_DIALECT = 6;
    private static TTSTool instance;
    private SpeechSynthesizer mTts;
    private MediaPlayer mediaPlayer;
    private Handler handler;
    private LinkedBlockingQueue<String> taskQueue = new LinkedBlockingQueue<>();
    private boolean isReading = false;

    // 默认发音人
    private String voicer = "vimary";//"xiaoyan";
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    //是否已经初始化
    private boolean hasInit = false;

    private TTSTool() {
        handler = new Handler(Looper.getMainLooper());
        mTts = SpeechSynthesizer.createSynthesizer(MainApplication.getApplication(), new InitListener() {
            @Override
            public void onInit(int i) {
                setParam();
                hasInit = true;
                handler.post(readTask);
            }
        });
    }

    public static TTSTool getInstance() {
        if (instance == null) {
            instance = new TTSTool();
        }
        return instance;
    }

    /**
     * 调用语音合成播放入口
     *
     * @param strRead
     */
    public void addTTSReadTask(final String strRead) {
        String language = MainApplication.getApplication().getLocalInfo().appLang;
        if (!TextUtils.isEmpty(language)) {//语言环境过滤，被过滤的语言不做语音播报
            switch (language) {
                case "ja":
                case "he":
                case "ko":
                    return;
            }
        }
        if (!isReading && hasInit) {
            readTts(strRead);
        } else {
            taskQueue.offer(strRead);
        }
    }

    public void clearAllTask() {
        isReading = false;
        handler.removeCallbacksAndMessages(null);
        taskQueue.clear();
    }

    public void stopReading() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        if (mTts != null && mTts.isSpeaking()) {
            mTts.stopSpeaking();
        }
    }

    private Runnable readTask = new Runnable() {
        @Override
        public void run() {
            final String readStr = taskQueue.poll();
            if (readStr != null) {
                readTts(readStr);
            }
        }
    };

    private void readTts(String strRead) {
        isReading = true;
        handler.postDelayed(resetReadingStateTask, 10 * 1000);//播放器意外中断的补救措施

        int speed = Preference.getPreferences().getKeyAlarmVoiceSpeed();
        int classificationLanguage = Preference.getPreferences().getKeyAlarmVoiceClassificationLanguage();
        strRead = strRead.replace("/", "");
        String language = LanguageUtil.isChina() ? "zh" : "en";
        String filePath = null;
        if (LanguageUtil.isChina()) {
            filePath = FileUtil.getMscPath() + "/" + strRead + speed + language + classificationLanguage + ".wav";
        } else {
            filePath = FileUtil.getMscPath() + "/" + strRead + speed + language + ".wav";
        }
        File file = new File(filePath);
        if (file.exists()) {
            readLocal(strRead, speed, classificationLanguage, filePath);
        } else {
            readOnline(strRead, speed, classificationLanguage, filePath);
        }
    }

    private void readLocal(final String strRead, final int speed, final int classificationLanguage, final String filePath) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    WLog.i("TTSMediaPlayer", "onCompletion:" + filePath);
                    isReading = false;
                    handler.removeCallbacks(resetReadingStateTask);
                    mp.release();
                    mediaPlayer = null;
                    handler.post(readTask);
                }
            });
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    WLog.i("TTSMediaPlayer", "onPrepared:" + filePath);
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mp.release();
                    mediaPlayer = null;
                    readOnline(strRead, speed, classificationLanguage, filePath);
                    return true;
                }
            });
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readOnline(String strRead, int speed, int classificationLanguage, String filePath) {
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, filePath);
        //判断语言环境
//        voicer = LanguageUtil.isChina() ? "xiaoyan" : "vimary";
        if (LanguageUtil.isAllChina()) {
            switch (classificationLanguage) {
                case MANDARIN:
                    voicer = "xiaoyan";
                    break;
                case CANTONESE:
                    voicer = "vixm";
                    break;
                case DIALECT_NORTHEAST_AREA:
                    voicer = "vixyun";
                    break;
                case HENAN_DIALECT:
                    voicer = "vixk";
                    break;
                case SICHUAN_DIALECT:
                    voicer = "vixr";
                    break;
                case FORMOSAN:
                    voicer = "vixl";
                    break;
                case QIN_DYNASTY_DIALECT:
                    voicer = "vixying";
                    break;
            }
        } else {
            voicer = "vimary";
        }

        mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
        //语速
        mTts.setParameter(SpeechConstant.SPEED, "" + (speed * 25));

        int code = mTts.startSpeaking(strRead, mTtsListener);
//			/**
//			 * 只保存音频不进行播放接口,调用此接口请注释startSpeaking接口
//			 * text:要合成的文本，uri:需要保存的音频全路径，listener:回调接口
//			*/
//			String path = Environment.getExternalStorageDirectory()+"/tts.pcm";
//			int code = mTts.synthesizeToUri(text, path, mTtsListener);

        if (code != ErrorCode.SUCCESS) {
            if (code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
                //未安装则跳转到提示安装页面
//                mInstaller.install();
            } else {
                WLog.i(TAG, "语音合成失败,错误码: " + code);
            }
        }
    }

    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            WLog.i(TAG, "开始播放");
        }

        @Override
        public void onSpeakPaused() {
            WLog.i(TAG, "暂停播放");
        }

        @Override
        public void onSpeakResumed() {
            WLog.i(TAG, "继续播放");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            // 合成进度
//            mPercentForBuffering = percent;
//            showTip(String.format(getString(R.string.tts_toast_format),
//                    mPercentForBuffering, mPercentForPlaying));
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
//            mPercentForPlaying = percent;
//            showTip(String.format(getString(R.string.tts_toast_format),
//                    mPercentForBuffering, mPercentForPlaying));
        }

        @Override
        public void onCompleted(SpeechError error) {
            isReading = false;
            handler.removeCallbacks(resetReadingStateTask);
            handler.post(readTask);
            if (error == null) {
                WLog.i(TAG, "播放完成");
            } else if (error != null) {
                WLog.i(TAG, "播放异常结束:" + error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    private Runnable resetReadingStateTask = new Runnable() {
        @Override
        public void run() {
            isReading = false;
        }
    };

    /**
     * 参数设置
     */
    private void setParam() {
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        // 根据合成引擎设置相应参数
        if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            // 设置在线合成发音人
            mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
            //语言
            //mTts.setParameter(SpeechConstant.LANGUAGE, "en");
            //设置合成语速
            mTts.setParameter(SpeechConstant.SPEED, "50");//mSharedPreferences.getString("speed_preference", "50"));
            //设置合成音调
            mTts.setParameter(SpeechConstant.PITCH, "50");//mSharedPreferences.getString("pitch_preference", "50"));
            //设置合成音量
            mTts.setParameter(SpeechConstant.VOLUME, "50");//mSharedPreferences.getString("volume_preference", "50"));
        } else {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            // 设置本地合成发音人 voicer为空，默认通过语记界面指定发音人。
            mTts.setParameter(SpeechConstant.VOICE_NAME, "");
            /**
             * 本地合成不设置语速、音调、音量，默认使用语记设置
             * 开发者如需自定义参数，请参考在线合成参数设置
             */
        }
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");//mSharedPreferences.getString("stream_preference", "3"));
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, FileUtil.getMscPath() + "/tts.wav");
    }
}
