package cc.wulian.smarthomev6.main.home.widget;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.SpannableBean;
import cc.wulian.smarthomev6.support.event.VideoEvent;
import cc.wulian.smarthomev6.support.utils.LanguageUtil;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by zbl on 2017/4/11.
 * update by luzx on 2017/06/17
 */

public class HomeWidgetVideo extends RelativeLayout implements IWidgetLifeCycle{


    private static final String TAG = HomeWidgetManager.class.getSimpleName();
    private SurfaceView surfaceview;
    private MediaPlayer mediaPlayer;
    private View layout_play_cover;
    private View mobileNetTip;//使用移动网提示
    private Animation animation;//加载动画
    private View loadingIcon;//加载动画view
    private View holderplaceIcon;//视频占位图
    private View iconBtnPlay;//播放按钮
    private boolean isPrepared = false;//视频是否准备
    private Context mContext;

    public HomeWidgetVideo(Context context) {
        super(context);
        mContext = context;
        initView(context);
    }

    public HomeWidgetVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context);
    }

    private void initView(final Context context) {
//        setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        View rootView = LayoutInflater.from(context).inflate(R.layout.view_homevideo, null);
        addView(rootView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));//DisplayUtil.dip2Pix(context, 300)));
        surfaceview = (SurfaceView) findViewById(R.id.videoview);
        surfaceview.getHolder().addCallback(new SurfaceHolder.Callback(){
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                WLog.i(TAG, "surfaceCreated");
                // 设置显示视频的SurfaceHolder
                if(mediaPlayer != null){
                    mediaPlayer.setDisplay(holder);//这一步是关键，制定用于显示视频的SurfaceView对象（通过setDisplay（））
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                WLog.i(TAG, "surfaceDestroyed");
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    pausePlay();
                }
            }
        });
        layout_play_cover = findViewById(R.id.layout_play_cover);
        mobileNetTip = findViewById(R.id.mobile_net_tip);
        loadingIcon = findViewById(R.id.loading_icon);
        holderplaceIcon = findViewById(R.id.holderplace_icon);
        TextView mobileFlowPalyTip = (TextView) findViewById(R.id.mobile_flow_paly_tip);
        View keepWatchBtn = findViewById(R.id.keep_watch_btn);
        StringUtil.addColorOrSizeorEvent(mobileFlowPalyTip,
                getResources().getString(R.string.Introducingvideo_Play_Trafficenvironment),
                new SpannableBean[]{new SpannableBean(getResources().getColor(R.color.v6_green),16,null)});
        View cancelBtn = findViewById(R.id.cancel_btn);
        iconBtnPlay = findViewById(R.id.icon_btn_play);
        keepWatchBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mobileNetTip.setVisibility(View.GONE);
                iconBtnPlay.setVisibility(View.GONE);
                startPlay();
            }
        });
        cancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mobileNetTip.setVisibility(View.GONE);
                iconBtnPlay.setVisibility(View.VISIBLE);
            }
        });
        this.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer == null){
                    initMediea();
                }
                if (mediaPlayer.isPlaying()) {
                    pausePlay();
                } else {
                    if(isPrepared){
                        startPlay();
                    }else{
                        int type = getNetype(context);
                        switch(type){
                            case -1:
                                ToastUtil.show(R.string.Introducingvideo_Play_Networkunavailable);
                                break;
                            case 1:
                                startPlay();
                                break;
                            case 2:
                                iconBtnPlay.setVisibility(View.GONE);
                                mobileNetTip.setVisibility(View.VISIBLE);
                                break;
                        }
                    }
                }
            }
        });
//        mVideoView.setVideoURI(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.film1));
//        MediaController mediaController = new MediaController(getContext());
//        mVideoView.setMediaController(mediaController);
    }

    //返回值 -1：没有网络  1：WIFI网络2：移动网络
    public int getNetype(Context context)
    {
        int netType = -1;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo==null)
        {
            return netType;
        }
        int nType = networkInfo.getType();
        if(nType == ConnectivityManager.TYPE_MOBILE)
        {
            netType = 2;
        }
        else if(nType == ConnectivityManager.TYPE_WIFI)
        {
            netType = 1;
        }
        return netType;
    }

//    private int currentPosition = 0;

    public void pausePlay() {
        if (isPrepared) {
            mediaPlayer.pause();
//            currentPosition = mediaPlayer.getCurrentPosition();
        }
        layout_play_cover.setVisibility(VISIBLE);
        iconBtnPlay.setVisibility(VISIBLE);
        mobileNetTip.setVisibility(GONE);
        this.setKeepScreenOn(false);
    }

    public void startPlay() {
        if(!isPrepared){
            initMediea();
            if(animation == null){
                mediaPlayer.prepareAsync();
                animation = AnimationUtils.loadAnimation(mContext, R.anim.progressdialog_anim);
                LinearInterpolator linearInterpolator = new LinearInterpolator();
                animation.setInterpolator(linearInterpolator);
                loadingIcon.startAnimation(animation);
            }
            loadingIcon.setVisibility(View.VISIBLE);
        }else{
            WLog.i(TAG, "start");
            mediaPlayer.start();
            loadingIcon.clearAnimation();
            animation = null;
            loadingIcon.setVisibility(View.GONE);
            holderplaceIcon.setVisibility(View.GONE);
            this.setKeepScreenOn(true);
        }
        layout_play_cover.setVisibility(GONE);
    }

    private void initMediea(){
        if(mediaPlayer == null){
            release();
            mediaPlayer = new MediaPlayer();
            //设置音频流类型
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    WLog.i(TAG, "onCompletion");
                    pausePlay();
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    WLog.i(TAG, "extra:" + extra + ";what:" + what);
                    ToastUtil.show(R.string.Introducingvideo_Play_Error);
                    release();
                    return false;
                }
            });
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    WLog.i(TAG, "onPrepared");
                    mp.seekTo(850);
                    //设置准备状态为true
                    isPrepared = true;
                    //自动播放前判断是否点击播放按钮
                    if(View.VISIBLE != layout_play_cover.getVisibility()){
                        startPlay();
                    }
                }
            });
            // 设置播放的视频源
            try {
//                String url = "http://video.wuliangroup.cn/v6/chinese.mp4";
                String url = "http://video.wuliangroup.cn/v6/wulian_cn.mp4";
                if(!LanguageUtil.isChina()){
//                    url = "http://video.wuliangroup.cn/v6/english.mp4";
                    url = "http://video.wuliangroup.cn/v6/wulian_en.mp4";
                }
                mediaPlayer.setDataSource(mContext, Uri.parse(url));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //重新创建surfaceview
        if(surfaceview != null){
            surfaceview.setVisibility(View.VISIBLE);
        }
//        int type = getNetype(mContext);
//        if(type == 1){//wifi情况下，自动加载
//            mediaPlayer.prepareAsync();
//        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void releaseVideo(VideoEvent event){
        release();
    }

    public void release(){
        //释放视频资源，设置准备状态为false
        isPrepared = false;
//        currentPosition = 0;
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
            AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            am.abandonAudioFocus(null);
        }
        //销毁surfaceview
        if(surfaceview != null){
            surfaceview.getHolder().getSurface().release();
            surfaceview.setVisibility(View.GONE);
        }
        loadingIcon.clearAnimation();
        animation = null;
        loadingIcon.setVisibility(View.GONE);
        mobileNetTip.setVisibility(GONE);
        holderplaceIcon.setVisibility(View.VISIBLE);
        layout_play_cover.setVisibility(VISIBLE);
        iconBtnPlay.setVisibility(VISIBLE);
        this.setKeepScreenOn(false);
    }

    @Override
    protected void onAttachedToWindow() {
        WLog.i(TAG, "onAttachedToWindow");
        this.setKeepScreenOn(false);
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        WLog.i(TAG, "onDetachedFromWindow");
        release();
        super.onDetachedFromWindow();
    }

    @Override
    public void onBindViewHolder(HomeItemBean bean) {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }
}
