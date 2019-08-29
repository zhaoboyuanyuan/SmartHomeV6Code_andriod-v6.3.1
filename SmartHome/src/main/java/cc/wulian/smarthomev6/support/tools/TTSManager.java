package cc.wulian.smarthomev6.support.tools;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;

import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;

import cc.wulian.smarthomev6.entity.LocalInfo;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.utils.FileUtil;
import cc.wulian.smarthomev6.support.utils.Logger;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.WLog;
import okhttp3.Call;
import okhttp3.Response;


public class TTSManager {

    private MainApplication app = MainApplication.getApplication();
    private static TTSManager instance = new TTSManager();
    private Handler handler;
    private LinkedBlockingQueue<String> taskQueue = new LinkedBlockingQueue<>();
    private boolean isReading = false;

    private TTSManager() {
        handler = new Handler(Looper.getMainLooper());
    }

    public static TTSManager getInstance() {
        return instance;
    }

    public void addTTSReadTask(final String strRead) {
        if (!isReading) {
            new Thread() {
                @Override
                public void run() {
                    readTts(strRead);
                }
            }.start();
        } else {
            taskQueue.offer(strRead);
        }
    }

    public void clearAllTask() {
        handler.removeCallbacksAndMessages(null);
        taskQueue.clear();
    }

    private Runnable readTask = new Runnable() {
        @Override
        public void run() {
            final String readStr = taskQueue.poll();
            if (readStr != null) {
                new Thread() {
                    @Override
                    public void run() {
                        readTts(readStr);
                    }
                }.start();
            }
        }
    };

    private Runnable resetReadingStateTask = new Runnable() {
        @Override
        public void run() {
            isReading = false;
        }
    };

    public void readTts(String strRead) {
        isReading = true;
        handler.postDelayed(resetReadingStateTask, 10 * 1000);//播放器意外中断的补救措施
        getTTSProvider(Locale.getDefault()).readTts(strRead);
    }

    public void speak(int speed, String strRead) {
        getTTSProvider(Locale.getDefault()).speak(speed, strRead);
    }

    private TTSProvider getTTSProvider(Locale locale) {
//		if(LanguageUtil.isChina() || LanguageUtil.isEnglish() || LanguageUtil.isTaiWan()){
        return new BaiduOnlineTtsManager();
//		}else if(LanguageUtil.getLanguage().equals("ru") || LanguageUtil.getLanguage().equals("es") || LanguageUtil.getLanguage().equals("pt")){
//			FreeOnlineTtsManager freeTTS = FreeOnlineTtsManager.getInstance();
//			freeTTS.setLocale(locale.getLanguage());
//			return freeTTS;
//		}else{
//			GoogleTtsManager googleTTS = GoogleTtsManager.getInstance();
//			googleTTS.setLocale(locale.getLanguage());
//			return googleTTS;
//		}
    }

    public interface TTSProvider {
        public void readTts(String strRead);

        public void speak(int speed, String strRead);
    }

//	public static class GoogleTtsManager implements TTSProvider {
//		private MediaPlayer mediaPlayer = new MediaPlayer();
//		private static GoogleTtsManager instance = new GoogleTtsManager();
//		private String locale;
//		private GoogleTtsManager(){
//
//		}
//		public static GoogleTtsManager getInstance(){
//			return instance;
//		}
//
//		public String getLocale() {
//			return locale;
//		}
//		public void setLocale(String locale) {
//			this.locale = locale;
//		}
//
//		@Override
//		public void readTts(String strRead, String strFrom) {
//			speak(0, strRead);
//		}
//
//		@Override
//		public void speak(int speed, String strRead) {
//			String filePath = FileUtil.getMscPath()+"/" + strRead + ".mp3";
//			File file = new File(filePath);
//			if (file.exists()) {
//				readLocal(filePath);
//			}else {
//				readOnline(strRead, filePath);
//			}
//
//		}
//
//		private void readLocal(final String filePath) {
//			try {
//				if (mediaPlayer!=null && mediaPlayer.isPlaying()) {
//					mediaPlayer.stop();
//				}
//				mediaPlayer.reset();
//				mediaPlayer.setDataSource(filePath);
//				mediaPlayer.prepare();
//				mediaPlayer.start();
//			}
//			catch (Exception e) {
//				e.printStackTrace();
//			}
//
//		}
//
//		public void readOnline(final String strRead, final String filePath) {
//			try {
//				download(strRead, filePath);
//				readLocal(filePath);
//			}
//			catch (IOException e) {
//				return;
//			}
//		}
//
//		public void download(String strRead, String filePath) throws IOException {
//			String strMp3URL = getMp3URLByCountry(strRead,locale);
//			Logger.debug("mp3URL:"+strMp3URL);
//			if (StringUtil.isNullOrEmpty(strMp3URL)) return;
//			DownloadManager downloadManager = new DownloadManager(strMp3URL, filePath);
//			downloadManager.startDonwLoadFile();
//		}
//		private String getMp3URLByCountry(String strRead,String locale ) throws UnsupportedEncodingException {
//			if (!StringUtil.isNullOrEmpty(strRead)) {
//				return "http://translate.google.cn/translate_tts?ie=UTF-8&q=" + URLEncoder.encode(strRead, "UTF-8") + "&tl="+locale;
//			}
//			return null;
//		}
//		public boolean deleteMsc() {
//			boolean result = true;
//
//			try {
//				File file = new File(FileUtil.getMscPath());
//				File[] childFiles = file.listFiles();
//				if (childFiles == null || childFiles.length == 0) {
//					result =  file.delete();
//				}
//
//				for (int i = 0; i < childFiles.length; i++) {
//					result = childFiles[i].delete();
//				}
//				result =  file.delete();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//			return result;
//		}
//	    // GENERAL_PUNCTUATION 判断中文的“号
//	    // CJK_SYMBOLS_AND_PUNCTUATION 判断中文的。号
//	    // HALFWIDTH_AND_FULLWIDTH_FORMS 判断中文的，号
////	    private  final boolean isChinese(char c) {
////	        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
////	        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
////	                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
////	                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
////	                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
////	                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
////	                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
////	            return true;
////	        }
////	        return false;
////	    }
////
////	    public  final boolean isChinese(String strName) {
////	        char[] ch = strName.toCharArray();
////	        for (int i = 0; i < ch.length; i++) {
////	            char c = ch[i];
////	            if (isChinese(c)) {
////	                return true;
////	            }
////	        }
////	        return false;
////	    }
//
//	}
//
//	public static class FreeOnlineTtsManager implements TTSProvider{
//
//		private MediaPlayer mediaPlayer = new MediaPlayer();
//		private Preference preference = Preference.getPreferences();
//		private static FreeOnlineTtsManager instance = new FreeOnlineTtsManager();
//		private String locale;
//		private FreeOnlineTtsManager(){
//
//		}
//		public static FreeOnlineTtsManager getInstance(){
//			return instance;
//		}
//
//		public String getLocale() {
//			return locale;
//		}
//		public void setLocale(String locale) {
//			this.locale = locale;
//		}
//
//		@Override
//		public void readTts(String strRead, String strFrom) {
//			int speed = preference.getKeyAlarmVoiceSpeed();
//			if(speed >= 1 && speed <= 2){
//				speed = -1;
//			}else if(speed >= 3 && speed <= 5){
//				speed = 0;
//			}else if(speed >= 6 && speed <= 7){
//				speed = 1;
//			}else{
//				speed = 2;
//			}
//			speak(speed, strRead);
//		}
//
//		@Override
//		public void speak(int speed, String strRead) {
//			String filePath = FileUtil.getMscPath()+"/" + strRead + speed + ".mp3";
//			File file = new File(filePath);
//			if (file.exists()) {
//				readLocal(filePath);
//			}else {
//				readOnline(strRead, speed, filePath);
//			}
//
//		}
//
//		private void readLocal(final String filePath) {
//			try {
//				if (mediaPlayer!=null && mediaPlayer.isPlaying()) {
//					mediaPlayer.stop();
//				}
//				mediaPlayer.reset();
//				mediaPlayer.setDataSource(filePath);
//				mediaPlayer.prepare();
//				mediaPlayer.start();
//			}
//			catch (Exception e) {
//				e.printStackTrace();
//			}
//
//		}
//
//		public void readOnline(final String strRead,final int speed, final String filePath) {
//			try {
//				download(strRead, speed,filePath);
//				readLocal(filePath);
//			}
//			catch (IOException e) {
//				return;
//			}
//		}
//
//		public void download(String strRead, int speed, String filePath) throws IOException {
//			String strMp3URL = getMp3URLByCountry(strRead,speed,locale);
//			Logger.debug("mp3URL:"+strMp3URL);
//			if (StringUtil.isNullOrEmpty(strMp3URL)) return;
//			DownloadManager downloadManager = new DownloadManager(strMp3URL, filePath);
//			downloadManager.startDonwLoadFile();
//		}
//		private String getMp3URLByCountry(String strRead,int speed,String locale ) throws UnsupportedEncodingException {
//			String ttsUrl = "http://www.fromtexttospeech.com";
//			Map<String, String> paramters = new HashMap<String, String>();
//			paramters.put("input_text", strRead);
//			if(StringUtil.equals("ru", locale)){
//				paramters.put("language", "Russian");
//				paramters.put("voice", "IVONA Tatyana22 (Russian)");
//			}else if(StringUtil.equals("es", locale)){
//				paramters.put("language", "Spanish");
//				paramters.put("voice", "IVONA Conchita22 (Spanish [Modern])");
//			}else if(StringUtil.equals("pt", locale)){
//				paramters.put("voice", "IVONA Cristiano22 (Portuguese)");
//				paramters.put("language", "Portuguese");
//			}
//			paramters.put("speed", speed + "");
//			paramters.put("action", "process_text");
//			JSONObject object = HttpManager.getDefaultProvider().post(ttsUrl, paramters);
//			Pattern checkPattern = Pattern.compile("<BR><a href=(.*?)>Download audio file");
//			Matcher checContentm = checkPattern.matcher(object.toJSONString());
//			String mp3Str = "";
//			while(checContentm.find()){
//				mp3Str = checContentm.group(1);
//			}
//			if(!StringUtil.isNullOrEmpty(mp3Str)){
//				String mp3Url = ttsUrl  + mp3Str.substring(1, mp3Str.length()-1);
//				return mp3Url;
//			}else{
//				return null;
//			}
//		}
//		public boolean deleteMsc() {
//			boolean result = true;
//
//			try {
//				File file = new File(FileUtil.getMscPath());
//				File[] childFiles = file.listFiles();
//				if (childFiles == null || childFiles.length == 0) {
//					result =  file.delete();
//				}
//
//				for (int i = 0; i < childFiles.length; i++) {
//					result = childFiles[i].delete();
//				}
//				result =  file.delete();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//			return result;
//		}
//
//	}

    private class BaiduOnlineTtsManager implements TTSProvider {

        private MediaPlayer mediaPlayer;
        private String KEY_API = "KtbNn8tHED0xc2fqWG3G9Y0b";
        private String SECRET_API = "ee2e51b04614620157e4132ccb616586";
        private String GRANT_TYPE = "client_credentials";
        private long BAIDU_TOKEN_TIME_20_DAY = 1728000000;
        private Preference preference = Preference.getPreferences();

        private BaiduOnlineTtsManager() {

        }

        @Override
        public void readTts(String strRead) {
            int speed = preference.getKeyAlarmVoiceSpeed();
            speak(speed, strRead);
        }

        @Override
        public void speak(int speed, String strRead) {
            strRead = strRead.replace("/", "");
            String filePath = FileUtil.getMscPath() + "/" + strRead + speed + ".mp3";
            File file = new File(filePath);
            if (file.exists()) {
                readLocal(filePath);
            } else {
                readOnline(strRead, speed, filePath);
            }
        }

        private void readLocal(final String filePath) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                WLog.i("TTSMediaPlayer", "onCompletion:" + filePath);
                                isReading = false;
                                handler.removeCallbacks(resetReadingStateTask);
                                mp.release();
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
                        mediaPlayer.setDataSource(filePath);
                        mediaPlayer.prepareAsync();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        private void readOnline(String strRead, int speed, String filePath) {
            try {
                download(strRead, speed, filePath);
            } catch (IOException e) {
                return;
            }
        }

        public void download(String strRead, int speed, String filePath) throws IOException {
            String strUrl = getMp3URLByCountry(strRead, speed);
            Logger.debug("mp3URL:" + strUrl);
            if (StringUtil.isNullOrEmpty(strUrl)) return;
//            DownloadManager downloadManager = new DownloadManager(strUrl, filePath);
//            downloadManager.startDonwLoadFile();
            OkGo.get(strUrl)
                    .tag(this)
                    .execute(new FileCallback(FileUtil.getMscPath(), strRead + speed + ".mp3") {
                        @Override
                        public void onSuccess(File file, Call call, Response response) {
                            WLog.i("TTSManager", "download success:" + file.getName());
                            readLocal(file.getPath());
                        }
                    });
        }

        private String getMp3URLByCountry(String strRead, int speed) throws UnsupportedEncodingException {
            long time = preference.getBaiDuTokenTime();
            String baiduToken = null;
            if (time != 0) {
                long timePre = System.currentTimeMillis() - time;
                //20天
//				if(timePre > BAIDU_TOKEN_TIME_20_DAY){
//					baiduToken = getBaiduToken(baiduToken);
//				}
                if (timePre > BAIDU_TOKEN_TIME_20_DAY) {
                    getBaiduToken();
                }
            } else {
                getBaiduToken();
            }
            baiduToken = preference.getBaiDuToken();
            if (!StringUtil.isNullOrEmpty(baiduToken)) {
                LocalInfo localInfo = MainApplication.getApplication().getLocalInfo();
                // 设备的唯一标识
                String deviceId = localInfo.imei;
                return "http://tsn.baidu.com/text2audio" + "?" + "tex=" + URLEncoder.encode(strRead, "UTF-8") + "&lan=" + "zh" + "&tok=" + baiduToken + "&ctp=" + "1" + "&cuid=" + deviceId
                        + "&spd=" + speed + "&pit=" + "5" + "&vol=" + "5" + "&per=" + "0";
            }
            return null;
        }

        private void getBaiduToken() {
            String baiduTokenUrl = "https://openapi.baidu.com/oauth/2.0/token?" +
                    "grant_type=" + GRANT_TYPE + "&" +
                    "client_id=" + KEY_API + "&" +
                    "client_secret=" + SECRET_API;

            OkGo.post(baiduTokenUrl)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            JSONObject tokenObject = JSONObject.parseObject(s);
                            if (tokenObject != null) {
                                JSONObject jsonObject = JSONObject.parseObject(s);
                                String baiduToken = jsonObject.getString("access_token");
                                preference.saveBaiDuToken(baiduToken);
                                preference.saveBaiDuTokenTime(System.currentTimeMillis());
                            }
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                        }
                    });
        }
    }
}
