package cc.wulian.smarthomev6.main.mine.setting;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import java.io.File;
import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.tools.TTSTool;
import cc.wulian.smarthomev6.support.utils.FileUtil;
import cc.wulian.smarthomev6.support.utils.LanguageUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by mamengchao on 2017/3/1 0001.
 * Tips:报警语音
 */
public class AlarmVoiceActivity extends BaseTitleActivity {
    private View contentView;
    private RelativeLayout itemAlarmVoiceVerySlow;
    private RelativeLayout itemAlarmVoiceSlow;
    private RelativeLayout itemAlarmVoiceNormal;
    private RelativeLayout itemAlarmVoiceFast;
    private RelativeLayout itemAlarmVoiceFaster;
    private RecyclerView classificationLanguageList;
    private ToggleButton item_remind_alarm_voice;
    private ViewGroup selector_container;
    private View in_voice_statement_view;
    private View out_voice_statement_view;
    private int[] classificationLanguages;
    private SpeechSynthesizer mSpeechSynthesizer;
    private MediaPlayer mMediaPlayer;
    private MyAdapater mAdapater;
    private ImageView iv_play;
    private boolean isReadLocal = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_voice, true);
        if(LanguageUtil.isAllChina()){
            classificationLanguages = new int[]{R.string.Mandarin, R.string.Cantonese, R.string.Dialect_Northeast_Area, R.string.Henan_Dialect, R.string.Sichuan_Dialect, R.string.Formosan, R.string.Qin_Dynasty_Dialect};
        }else{
            classificationLanguages = new int[]{R.string.English};
        }
    }

    @Override
    protected void initTitle() {
        setToolBarTitle(getString(R.string.Setting_Voice_Alarm));
    }

    @Override
    protected void initView() {
        contentView = getmToolBarHelper().getmUserView();
        itemAlarmVoiceVerySlow = (RelativeLayout) findViewById(R.id.item_alarm_voice_very_slow);
        itemAlarmVoiceSlow = (RelativeLayout) findViewById(R.id.item_alarm_voice_slow);
        itemAlarmVoiceNormal = (RelativeLayout) findViewById(R.id.item_alarm_voice_normal);
        itemAlarmVoiceFast = (RelativeLayout) findViewById(R.id.item_alarm_voice_fast);
        itemAlarmVoiceFaster = (RelativeLayout) findViewById(R.id.item_alarm_voice_faster);

        classificationLanguageList = (RecyclerView) findViewById(R.id.classification_language_list);
        item_remind_alarm_voice = (ToggleButton) findViewById(R.id.item_remind_alarm_voice);
        selector_container = (ViewGroup) findViewById(R.id.selector_container);
        in_voice_statement_view = findViewById(R.id.in_voice_statement_view);
        out_voice_statement_view = findViewById(R.id.out_voice_statement_view);
        itemAlarmVoiceFaster = (RelativeLayout) findViewById(R.id.item_alarm_voice_faster);
        mAdapater = new MyAdapater();
        classificationLanguageList.setFocusable(false);
        classificationLanguageList.setAdapter(mAdapater);
        mAdapater.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ImageView iv_check = (ImageView) view.findViewById(R.id.item_check);
                iv_check.setVisibility(View.VISIBLE);
                mAdapater.notifyDataSetChanged();
                preference.saveKeyAlarmVoiceClassificationLanguage(position);
            }
        });
        if(preference.getKeyAlarmVoice()){
            item_remind_alarm_voice.setChecked(true);
            selector_container.setVisibility(View.VISIBLE);
        }else{
            item_remind_alarm_voice.setChecked(false);
            selector_container.setVisibility(View.GONE);
        }

        refereshSpeedSelect();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(!hasFocus){
            return;
        }
        if(selector_container.getHeight() < selector_container.getChildAt(0).getHeight()){
            in_voice_statement_view.setVisibility(View.VISIBLE);
            out_voice_statement_view.setVisibility(View.GONE);
        }else{
            in_voice_statement_view.setVisibility(View.GONE);
            out_voice_statement_view.setVisibility(View.VISIBLE);
        }
        super.onWindowFocusChanged(hasFocus);
    }

    class MyAdapater extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private OnItemClickListener mOnItemClickListener;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View itemView = layoutInflater.inflate(R.layout.item_classification_language, parent, false);
            return new ItemHolder(itemView, mOnItemClickListener);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            ((ItemHolder) holder).iv_play_pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stop();
                    if(v.getTag() == null){
                        ((ImageView)v).setImageResource(R.drawable.icon_pause);
                        v.setTag("1");
                        read(getString(R.string.Voice_Content), position, preference.getKeyAlarmVoiceSpeed());
                        if(iv_play!= null && v != iv_play){
                            iv_play.setImageResource(R.drawable.icon_play);
                            iv_play.setTag(null);
                        }
                    }else{
                        ((ImageView)v).setImageResource(R.drawable.icon_play);
                        v.setTag(null);
                    }
                    iv_play = (ImageView) v;
                }
            });
            Object state = ((ItemHolder) holder).iv_play_pause.getTag();
            if(state == null){
                ((ItemHolder) holder).iv_play_pause.setImageResource(R.drawable.icon_play);
            }else{
                ((ItemHolder) holder).iv_play_pause.setImageResource(R.drawable.icon_pause);
            }
            ((ItemHolder) holder).tv_classification_language_name.setText(classificationLanguages[position]);
            int index = LanguageUtil.isAllChina() ? preference.getKeyAlarmVoiceClassificationLanguage() : 0;
            if(position == index){
                ((ItemHolder) holder).iv_check.setVisibility(View.VISIBLE);
            }else{
                ((ItemHolder) holder).iv_check.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return classificationLanguages.length;
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.mOnItemClickListener = listener;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        private ImageView iv_play_pause;
        private TextView tv_classification_language_name;
        private ImageView iv_check;

        public ItemHolder(View itemView, final OnItemClickListener itemClickListener) {
            super(itemView);
            iv_play_pause = (ImageView) itemView.findViewById(R.id.icon_play_pause);
            tv_classification_language_name = (TextView) itemView.findViewById(R.id.classification_language_name);
            iv_check = (ImageView) itemView.findViewById(R.id.item_check);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(itemClickListener != null){
                        itemClickListener.onItemClick(v, getAdapterPosition());
                    }
                }
            });
        }
    }

    @Override
    protected void initListeners() {
        itemAlarmVoiceVerySlow.setOnClickListener(this);
        itemAlarmVoiceSlow.setOnClickListener(this);
        itemAlarmVoiceNormal.setOnClickListener(this);
        itemAlarmVoiceFast.setOnClickListener(this);
        itemAlarmVoiceFaster.setOnClickListener(this);
        item_remind_alarm_voice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    preference.saveKeyAlarmVoice(true);
                    selector_container.setVisibility(View.VISIBLE);
                }else {
                    stop();
                    if(iv_play!= null){
                        iv_play.setImageResource(R.drawable.icon_play);
                        iv_play.setTag(null);
                    }
                    preference.saveKeyAlarmVoice(false);
                    selector_container.setVisibility(View.GONE);
                }
            }
        });
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            iv_play.setImageResource(R.drawable.icon_play);
            iv_play.setTag(null);
            if(msg.what == 1){
            }
        }
    };

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.item_alarm_voice_very_slow:
                preference.saveKeyAlarmVoiceSpeed(0);
                refereshSpeedSelect();
                break;
            case R.id.item_alarm_voice_slow:
                preference.saveKeyAlarmVoiceSpeed(1);
                refereshSpeedSelect();
                break;
            case R.id.item_alarm_voice_normal:
                preference.saveKeyAlarmVoiceSpeed(2);
                refereshSpeedSelect();
                break;
            case R.id.item_alarm_voice_fast:
                preference.saveKeyAlarmVoiceSpeed(3);
                refereshSpeedSelect();
                break;
            case R.id.item_alarm_voice_faster:
                preference.saveKeyAlarmVoiceSpeed(4);
                refereshSpeedSelect();
                break;
            default:
                break;
        }
    }

    private void refereshSpeedSelect(){
        int select = preference.getKeyAlarmVoiceSpeed();
        for (int i = 0; i < 5; i++) {
            String tag = "item_alarm_voice_" + i;
            ImageView viewSelect = (ImageView) contentView.findViewWithTag(tag);
            if (i == select){
                viewSelect.setVisibility(View.VISIBLE);
            }else {
                viewSelect.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void setParam(){
        // 清空参数
        mSpeechSynthesizer.setParameter(SpeechConstant.PARAMS, null);
        // 根据合成引擎设置相应参数
        mSpeechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        //设置合成音调
        mSpeechSynthesizer.setParameter(SpeechConstant.PITCH, "50");//mSharedPreferences.getString("pitch_preference", "50"));
        //设置合成音量
        mSpeechSynthesizer.setParameter(SpeechConstant.VOLUME, "50");//mSharedPreferences.getString("volume_preference", "50"));
        //设置播放器音频流类型
        mSpeechSynthesizer.setParameter(SpeechConstant.STREAM_TYPE, "3");//mSharedPreferences.getString("stream_preference", "3"));
        // 设置播放合成音频打断音乐播放，默认为true
        mSpeechSynthesizer.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mSpeechSynthesizer.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
    }

    private void read(String strRead, int classificationLanguage, int speed){
        String language = LanguageUtil.isAllChina() ? "zh" : "en";
        String filePath = FileUtil.getMscPath() + "/" + strRead + speed + classificationLanguage + language + ".wav";
        File file = new File(filePath);
        if (file.exists()) {
            readLocal(strRead, speed, classificationLanguage, filePath);
        } else {
            readOnline(strRead, speed, classificationLanguage, filePath);
        }
    }

    private void stop(){
        if(isReadLocal){
            if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
                mMediaPlayer.stop();
                mMediaPlayer.reset();
                mMediaPlayer = null;
            }
        }else{
            if(mSpeechSynthesizer != null && mSpeechSynthesizer.isSpeaking()){
                mSpeechSynthesizer.stopSpeaking();
            }
        }
    }

    private void readLocal(final String strRead, final int speed, final int classificationLanguage, final String filePath) {
        isReadLocal = true;
        try {
            if(mMediaPlayer == null){
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mHandler.sendEmptyMessage(0);
                        mp.release();
                        mMediaPlayer = null;
                    }
                });
                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });
                mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        WLog.e("luzx", "mMediaPlayer onError");
                        mp.reset();
                        mMediaPlayer = null;
                        readOnline(strRead, speed, classificationLanguage,  filePath);
                        return true;
                    }
                });
            }
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readOnline(String strRead, int speed, int classificationLanguage, String filePath){
        isReadLocal = false;
        if(mSpeechSynthesizer == null){
            mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(MainApplication.getApplication(), new InitListener() {
                @Override
                public void onInit(int i) {
                    setParam();
                }
            });
        }
        mSpeechSynthesizer.setParameter(SpeechConstant.TTS_AUDIO_PATH, filePath);
        //判断语言环境
        String voicer = null;
        if(LanguageUtil.isAllChina()){
            switch (classificationLanguage){
                case TTSTool.MANDARIN:
                    voicer = "xiaoyan";
                    break;
                case TTSTool.CANTONESE:
                    voicer = "vixm";
                    break;
                case TTSTool.DIALECT_NORTHEAST_AREA:
                    voicer = "vixyun";
                    break;
                case TTSTool.HENAN_DIALECT:
                    voicer = "vixk";
                    break;
                case TTSTool.SICHUAN_DIALECT:
                    voicer = "vixr";
                    break;
                case TTSTool.FORMOSAN:
                    voicer = "vixl";
                    break;
                case TTSTool.QIN_DYNASTY_DIALECT:
                    voicer = "vixying";
                    break;
            }
        }else{
            voicer = "vimary";
        }
        mSpeechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, voicer);
        //语速
        mSpeechSynthesizer.setParameter(SpeechConstant.SPEED, "" + (speed * 25));

        int code = mSpeechSynthesizer.startSpeaking(strRead, mTtsListener);
        if (code != ErrorCode.SUCCESS) {
            if (code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {

            } else {
                WLog.i(TAG, "语音合成失败,错误码: " + code);
                mHandler.sendEmptyMessage(1);
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
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                mHandler.sendEmptyMessage(0);
                WLog.i(TAG, "播放完成");
            } else if (error != null) {
                mHandler.sendEmptyMessage(1);
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

    @Override
    protected void onDestroy() {
        if(mMediaPlayer != null){
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if(mSpeechSynthesizer != null){
            mSpeechSynthesizer.destroy();
            mSpeechSynthesizer = null;
        }
        super.onDestroy();
    }
}