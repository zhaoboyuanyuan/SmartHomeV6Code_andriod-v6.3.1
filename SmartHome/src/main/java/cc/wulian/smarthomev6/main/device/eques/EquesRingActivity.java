package cc.wulian.smarthomev6.main.device.eques;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.eques.icvss.api.ICVSSUserInstance;
import com.eques.icvss.core.module.user.BuddyType;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.SimpleDateFormat;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.UnlockDialogActivity;
import cc.wulian.smarthomev6.main.device.cateye.CateyeRingActivity;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesCallStatusBean;
import cc.wulian.smarthomev6.main.device.eques.bean.VideoPlayingBean;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.EquesApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceRelationBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceRelationListBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.DisplayUtil;
import cc.wulian.smarthomev6.support.utils.FileUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * 作者: chao
 * 时间: 2017/6/16
 * 描述: 移康猫眼来电界面
 * 联系方式: 805901025@qq.com
 */

public class EquesRingActivity extends BaseTitleActivity {
    private FrameLayout layout_video_container;
    private FrameLayout main_container;
    private View layout_waiting, layout_playing;
    private View btn_ringing_hangup, btn_ringing_connect;
    private TextView tv_hold_speek;
    private ImageView iv_bg;
    private View btn_snapshot, btn_hang_up,btn_lock;
    private View btn_hold_speek;
    private ImageView iv_hold_speek;
    private SurfaceView sv_play;
    private Chronometer timer;

    private SoundPool soundPool;
    private AudioManager audioManager;
    private ICVSSUserInstance icvss;

    private SnapshotObserver fileObserver;
    private DeviceApiUnit deviceApiUnit;

    private int snapshot_sound_id;
    private int currVolume;
    private int current;
    private String callId;
    private boolean isMute = true;

    private String deviceId;
    private boolean isOnline;
    private String fid;
    /**
     * 0 loading，1 断开，2 播放
     */
    private int videoPlayState = 0;

    public static void start(Context context, String deviceid, boolean isOnline, String sid) {
        Intent intent = new Intent(context, EquesRingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("deviceId", deviceid);
        intent.putExtra("isOnline", isOnline);
        intent.putExtra("fid", sid);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eques_ring, true);
        EventBus.getDefault().register(this);
        icvss = MainApplication.getApplication().getEquesApiUnit().getIcvss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hangUpCall();
        timer.stop();
        EventBus.getDefault().unregister(this);
        if (null != fileObserver){
            fileObserver.stopWatching();
        }
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Cateyemini_Push_Doorbellcalls));
    }

    @Override
    protected void initView() {
        layout_video_container = (FrameLayout) findViewById(R.id.layout_video_container);
        main_container = (FrameLayout) findViewById(R.id.main_container);
        layout_waiting = findViewById(R.id.layout_waiting);
        layout_playing = findViewById(R.id.layout_playing);
        layout_waiting.setVisibility(View.VISIBLE);
        layout_playing.setVisibility(View.GONE);

        iv_bg = (ImageView) findViewById(R.id.iv_bg);
        tv_hold_speek = (TextView) findViewById(R.id.tv_hold_speek);
        btn_ringing_hangup = findViewById(R.id.btn_ringing_hangup);
        btn_ringing_connect = findViewById(R.id.btn_ringing_connect);
        btn_snapshot = findViewById(R.id.btn_snapshot);
        btn_hang_up = findViewById(R.id.btn_hang_up);
        btn_hold_speek = findViewById(R.id.btn_hold_speek);
        iv_hold_speek = (ImageView) findViewById(R.id.iv_hold_speek);
        sv_play = (SurfaceView) findViewById(R.id.sv_play);
        timer = (Chronometer) findViewById(R.id.chronometer_timer);
        btn_lock = findViewById(R.id.btn_lock);
    }

    @Override
    protected void initData() {
        isOnline = getIntent().getBooleanExtra("isOnline", false);
        deviceId = getIntent().getStringExtra("deviceId");
        fid = getIntent().getStringExtra("fid");

        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        snapshot_sound_id = soundPool.load(this, R.raw.snapshot, 1);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current, 0);
        deviceApiUnit = new DeviceApiUnit(this);
        currVolume = current;

        MainApplication.getApplication().getEquesApiUnit().loadRingPic(fid, deviceId,
                fid, new EquesApiUnit.EquesFileDownloadListener() {
                    @Override
                    public void onSuccess(File file) {
                        ImageLoader.getInstance().displayImage("file://"+ file.getAbsolutePath().trim(), iv_bg);
                    }

                    @Override
                    public void onFail(int code, String msg) {

                    }
                });

        sv_play.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                callId = MainApplication.getApplication().getEquesApiUnit().getIcvss().equesOpenCall(deviceId, holder.getSurface());
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

        setVideoSize();
        setAudioMute(true);
        creatSnapshotFile();
    }

    @Override
    protected void initListeners() {
        btn_ringing_hangup.setOnClickListener(this);
        btn_ringing_connect.setOnClickListener(this);
        btn_snapshot.setOnClickListener(this);
        btn_hang_up.setOnClickListener(this);
        btn_lock.setOnClickListener(this);

        btn_hold_speek.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (videoPlayState == 2) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            iv_hold_speek.setImageResource(R.drawable.icon_hold_speek_on);
                            tv_hold_speek.setText(R.string.Cateye_In_Call);
                            callSpeakerSetting(true);
                            break;
                        case MotionEvent.ACTION_UP:
                            iv_hold_speek.setImageResource(R.drawable.icon_hold_speek);
                            tv_hold_speek.setText(R.string.CateEye_Detail_Hold_Speek);
                            callSpeakerSetting(false);
                            break;
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == btn_snapshot) {
            if (videoPlayState == 2) {
                snapshot();
                btn_snapshot.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btn_snapshot.setEnabled(true);
                    }
                }, 1000);
            }
        } else if (v == btn_hang_up) {
            finish();
        } else if (v == btn_ringing_hangup) {
            finish();
        } else if (v == btn_ringing_connect) {
            layout_waiting.setVisibility(View.GONE);
            layout_playing.setVisibility(View.VISIBLE);

            timer.setBase(SystemClock.elapsedRealtime());//计时器清零
            int hour = (int) ((SystemClock.elapsedRealtime() - timer.getBase()) / 1000 / 60);
            timer.setFormat("0"+String.valueOf(hour)+":%s");
            timer.start();

            setAudioMute(false);
        }else if(v == btn_lock){
            getLockData();
        }
    }

    private void creatSnapshotFile() {
        String savePath = FileUtil.getSnapshotPath() + "/" + deviceId;
        File savePathFile = new File(savePath);
        if (!savePathFile.exists()) {
            savePathFile.mkdirs();
        }
        fileObserver = new SnapshotObserver(savePath, FileObserver.CREATE);
        fileObserver.startWatching();
    }

    private void setVideoSize(){
        int verticalHeight;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        verticalHeight = (dm.widthPixels * 3) / 4;
        sv_play.getHolder().setFixedSize(dm.widthPixels, verticalHeight);
    }

    private void hangUpCall(){
        if (callId != null) {
            icvss.equesCloseCall(callId);
        }
    }

    public void openSpeaker() {
        try {
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            currVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (!audioManager.isSpeakerphoneOn()) {
                audioManager.setSpeakerphoneOn(true);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currVolume,
                        0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeSpeaker(){
        try {
            if (audioManager != null) {
                if (audioManager.isSpeakerphoneOn()) {
                    currVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    audioManager.setSpeakerphoneOn(false);
                    audioManager.setStreamVolume(
                            AudioManager.STREAM_MUSIC, currVolume,
                            0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAudioMute(boolean volumn){
        isMute = volumn;
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, volumn);

        if(volumn){
            if(callId != null){
                icvss.equesAudioPlayEnable(false, callId);
                icvss.equesAudioRecordEnable(false, callId);
            }
        }else{
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current, 0);
            callSpeakerSetting(false);
        }
    }

    private void callSpeakerSetting(boolean f) {
        if (f) {
            if (callId != null) {
                icvss.equesAudioRecordEnable(true, callId);
                icvss.equesAudioPlayEnable(false, callId);
            }
            closeSpeaker();
        } else {
            if (callId != null) {
                icvss.equesAudioPlayEnable(true, callId);
                icvss.equesAudioRecordEnable(false, callId);
            }
            openSpeaker();
        }
    }

    private void snapshot() {
        if (isOnline && callId != null) {
            String savePath = FileUtil.getSnapshotPath() + "/" + deviceId;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmsss");
            String time = simpleDateFormat.format(System.currentTimeMillis());
            String fileName = time + ".jpg";
            icvss.equesSnapCapture(BuddyType.TYPE_WIFI_DOOR_R22, savePath +"/" +fileName);
            soundPool.play(snapshot_sound_id, 1.0f, 1.0f, 0, 0, 1);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(VideoPlayingBean event) {
        iv_bg.setVisibility(View.GONE);
        videoPlayState = 2;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoStatus(EquesCallStatusBean event) {
        if (TextUtils.equals(event.from, deviceId)){
            if (TextUtils.equals(event.state, "close")){
                videoPlayState = 1;
                finish();
            }else if (TextUtils.equals(event.state, "try")){
                videoPlayState = 0;
            }
        }
    }

    private void showLastSnapshot() {
        final String savePath = FileUtil.getSnapshotPath() + "/" + deviceId;
        File savePathFile = new File(savePath);
        String[] bmpFiles = savePathFile.list();
        if (bmpFiles != null && bmpFiles.length > 0) {
            final String bmpFile = bmpFiles[bmpFiles.length - 1];
            final Bitmap bitmap = BitmapFactory.decodeFile(savePath + "/" + bmpFile);

            DisplayUtil.snapAnimotion(this, main_container, sv_play, btn_snapshot, bitmap, new DisplayUtil.onCompleteListener() {
                @Override
                public void onComplete() {
                }
            });
        }
    }
    private void getLockData() {
        deviceApiUnit.doGetBindLock(deviceId, "", new DeviceApiUnit.DeviceApiCommonListener<DeviceRelationListBean>() {
            @Override
            public void onSuccess(DeviceRelationListBean bean) {
                UnlockDialogActivity.startByCondition(EquesRingActivity.this, bean, false);
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.singleCenter(msg);
            }
        });
    }

    class SnapshotObserver extends FileObserver {

        public SnapshotObserver(String path, int mask) {
            super(path, mask);
        }

        @Override
        public void onEvent(int event, String path) {
            switch(event){
                case FileObserver.CREATE:
                    new Handler(getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showLastSnapshot();
                        }
                    }, 500);
                    break;
                default:
                    break;
            }
        }
    }
}
