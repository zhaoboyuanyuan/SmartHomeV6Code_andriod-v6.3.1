package cc.wulian.smarthomev6.main.device.camera_lc;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lechange.opensdk.listener.LCOpenSDK_EventListener;
import com.lechange.opensdk.media.LCOpenSDK_PlayWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.device.camera_lc.business.Business;
import cc.wulian.smarthomev6.main.device.camera_lc.business.util.TimeHelper;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.LcDeviceInfoBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.customview.RecorderSeekBar;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.NetworkInfoEvent;
import cc.wulian.smarthomev6.support.utils.FileUtil;
import cc.wulian.smarthomev6.support.utils.SizeUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

public class PlayCameraBackActivity extends BaseTitleActivity {
    private static final String TAG = "LcOutdoorCameraActivity";
    private static final String PERMISSION_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    private static final int PERMISSION_REQUEST_CODE = 1;
    protected static final int retOK = 0;
    protected static final int retNG = -1;
    private static boolean sthls = false;

    private enum AudioTalkStatus {talk_close, talk_opening, talk_open}

    private enum PlayStatus {play_close, play_open, play_opening, play_pause}

    private PlayStatus playStatus = PlayStatus.play_close;
    private ViewGroup videoContainer;
    private FrameLayout landscapeLayout;
    private RelativeLayout bottomLayout;
    private LinearLayout layoutSbLandscape;
    private View layoutLoading;
    private View layoutReload;
    private View layoutOffline;
    private ImageView ivSoundSwitch;
    private ImageView ivSnapshot;
    private ImageView ivDownload;
    private ImageView ivFullSceen;
    private ImageView ivExitFullSceen;
    private ImageView ivPlay;
    private ImageView ivPlayBack;
    private ImageView ivPlayLandscape;
    private ImageView ivSoundSwitchLandscape;
    private ImageView ivSnapshotLandscape;
    private TextView tvBeginTime;
    private TextView tvEndTime;
    private TextView tvBeginTimeLandscape;
    private TextView tvEndTimeLandscape;

    private ViewGroup surfaceParentView;
    private RecorderSeekBar recorderSeekBar;
    private RecorderSeekBar recorderSeekBarLandscape;


    private SoundPool soundPool;
    private LCOpenSDK_PlayWindow playWindow = new LCOpenSDK_PlayWindow();
    private LCOpenSDK_EventListener listener;
    private Handler mHander = new Handler();
    private AudioTalkStatus talkStatus = AudioTalkStatus.talk_close; //语音对讲状态
    private DeviceApiUnit deviceApiUnit;
    private Device device;

    private int snapshotSoundId;
    private int channelId;
    private int progress;
    /**
     * 0 loading，1 断开，2 播放, 3 离线
     */
    private int videoPlayState;
    private boolean isOpenSound = false;//声音打开
    private String deviceId;
    private String token;
    private String beginTime;
    private String recordId;
    private String channelName;

    private long startTime;
    private long endTime;


    public static void start(Context context, String deviceId, String recordId, long startTime, long endTime, String channelName) {
        Intent intent = new Intent(context, PlayCameraBackActivity.class);
        intent.putExtra("deviceId", deviceId);
        intent.putExtra("recordId", recordId);
        intent.putExtra("startTime", startTime);
        intent.putExtra("endTime", endTime);
        intent.putExtra("channelName", channelName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lc_play_back, true);
        EventBus.getDefault().register(this);
    }


    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(R.string.addDevice_CG27_Playback_local);
    }

    @Override
    protected void initView() {
        super.initView();
        ivSnapshot = (ImageView) findViewById(R.id.btn_snapshot);
        ivSnapshotLandscape = (ImageView) findViewById(R.id.btn_snapshot_landscape);
        ivSoundSwitch = (ImageView) findViewById(R.id.btn_sound_switch);
        ivSoundSwitchLandscape = (ImageView) findViewById(R.id.btn_sound_landscape);
        ivDownload = (ImageView) findViewById(R.id.btn_download);
        ivFullSceen = (ImageView) findViewById(R.id.iv_fullscreen);
        ivPlay = (ImageView) findViewById(R.id.btn_play);
        ivPlayLandscape = (ImageView) findViewById(R.id.btn_play_landscape);
        ivPlayBack = (ImageView) findViewById(R.id.iv_back_play);
        ivExitFullSceen = (ImageView) findViewById(R.id.iv_fullscreen_landscape);
        tvBeginTime = (TextView) findViewById(R.id.tv_begin_time);
        tvBeginTimeLandscape = (TextView) findViewById(R.id.tv_begin_time_landscape);
        tvEndTime = (TextView) findViewById(R.id.tv_end_time);
        tvEndTimeLandscape = (TextView) findViewById(R.id.tv_end_time_landscape);
        videoContainer = (ViewGroup) findViewById(R.id.live_window_content);
        landscapeLayout = (FrameLayout) findViewById(R.id.layout_landscape);
        bottomLayout = (RelativeLayout) findViewById(R.id.layout_portrait_bottom);
        layoutLoading = findViewById(R.id.layout_video_loading);
        layoutReload = findViewById(R.id.layout_video_reload);
        layoutOffline = findViewById(R.id.layout_video_offline);
        layoutSbLandscape = (LinearLayout) findViewById(R.id.ll_sb_landscape);
        recorderSeekBar = (RecorderSeekBar) findViewById(R.id.sb_play);
        recorderSeekBarLandscape = (RecorderSeekBar) findViewById(R.id.sb_landscape);
        DisplayMetrics displayMetrics = SizeUtil.getScreenSize(getApplicationContext());
        int cameraPreviewWidth = displayMetrics.widthPixels;
        int cameraPreviewHeight = (int) ((float) cameraPreviewWidth
                * 9 / 16);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(cameraPreviewWidth, cameraPreviewHeight);
        layoutParams.gravity = Gravity.CENTER;
        videoContainer.setLayoutParams(layoutParams);
        playWindow.initPlayWindow(PlayCameraBackActivity.this, (ViewGroup) findViewById(R.id.live_window_content), 0);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean enableSwipeBack() {
        return false;
    }

    @Override
    protected void initData() {
        super.initData();
        deviceId = getIntent().getStringExtra("deviceId");
        recordId = getIntent().getStringExtra("recordId");
        channelName = getIntent().getStringExtra("channelName");
        startTime = getIntent().getLongExtra("startTime", 0);
        endTime = getIntent().getLongExtra("endTime", 0);

        device = mainApplication.getDeviceCache().get(deviceId);
        if (device != null) {
            if (device.isOnLine()) {
                updateLoadingState(0);
            } else {
                updateLoadingState(3);
            }
        }
        listener = new MyBaseWindowListener();
        deviceApiUnit = new DeviceApiUnit(PlayCameraBackActivity.this);
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        playWindow.setWindowListener(listener);
        playWindow.openTouchListener();
        snapshotSoundId = soundPool.load(this, R.raw.snapshot, 1);
        checkPermission();
        setCanSeekChanged(false);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        ivPlay.setOnClickListener(this);
        ivPlayLandscape.setOnClickListener(this);
        ivPlayBack.setOnClickListener(this);
        ivSoundSwitch.setOnClickListener(this);
        ivSoundSwitchLandscape.setOnClickListener(this);
        ivSnapshot.setOnClickListener(this);
        ivSnapshotLandscape.setOnClickListener(this);
        ivFullSceen.setOnClickListener(this);
        ivDownload.setOnClickListener(this);
        ivExitFullSceen.setOnClickListener(this);
        layoutReload.setOnClickListener(this);
        landscapeLayout.setOnClickListener(this);
        setSeekBarListener();

    }

    /**
     * 描述：设置拖动进度条是否能使用
     */
    public void setCanSeekChanged(boolean canSeek) {
        recorderSeekBar.setCanTouchAble(canSeek);
        recorderSeekBarLandscape.setCanTouchAble(canSeek);
    }

    private void setSeekBarListener() {
        recorderSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (recorderSeekBar.getMax() - PlayCameraBackActivity.this.progress <= 2) {
                    ToastUtil.show("未找到i帧");
                    seek(recorderSeekBar.getMax() >= 2 ? recorderSeekBar.getMax() - 2 : 0);
                } else {
                    seek(PlayCameraBackActivity.this.progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean byUser) {
                if (byUser) {
                    PlayCameraBackActivity.this.progress = progress;
                }
            }
        });

        recorderSeekBarLandscape.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (recorderSeekBarLandscape.getMax() - PlayCameraBackActivity.this.progress <= 2) {
                    ToastUtil.show("未找到i帧");
                    seek(recorderSeekBarLandscape.getMax() >= 2 ? recorderSeekBarLandscape.getMax() - 2 : 0);
                } else {
                    seek(PlayCameraBackActivity.this.progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean byUser) {
                if (byUser) {
                    PlayCameraBackActivity.this.progress = progress;
                }
            }
        });
    }


    public void seek(int index) {
        System.out.println("index:" + index);

        long seekTime = startTime / 1000 + index;
        //先暂存时间记录
        PlayCameraBackActivity.this.beginTime = TimeHelper.getTimeHMS(seekTime * 1000);
        PlayCameraBackActivity.this.progress = index;
        recorderSeekBar.setProgress(index);
        recorderSeekBarLandscape.setProgress(index);
        tvBeginTime.setText(PlayCameraBackActivity.this.beginTime);
        tvBeginTimeLandscape.setText(PlayCameraBackActivity.this.beginTime);

        playWindow.seek(index);

    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                PERMISSION_RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{PERMISSION_RECORD_AUDIO},
                    PERMISSION_REQUEST_CODE);
        } else {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            //这个标志位是因为关闭权限弹框是会走onresume回调，造成多次呼叫引发问题
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                // Permission Denied
                ToastUtil.singleCenter(R.string.Toast_Permission_Denied);
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoPlayState != 2) {
            getLcDeviceInfo();
            initSeekBarAndTime();
            updateLoadingState(0);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_sound_switch:
            case R.id.btn_sound_landscape:
                switchSound();
                break;
            case R.id.btn_snapshot:
            case R.id.btn_snapshot_landscape:
                capture();
                break;
            case R.id.iv_fullscreen:
                performFullscreen();
                break;
            case R.id.iv_fullscreen_landscape:
                exitFullscreen();
                break;
            case R.id.layout_video_reload:
                updateLoadingState(0);
                play();
                break;
            case R.id.btn_play:
            case R.id.btn_play_landscape:
                switchPlayAndPause();
                break;
            case R.id.iv_back_play:
                switch (device.type) {
                    case "CG22":
                    case "CG23":
                        LcOutdoorCameraActivity.start(this, deviceId);
                        break;

                    case "CG24":
                    case "CG25":
                        LcPTZCameraActivity.start(this, deviceId);
                        break;
                    case "CG26":
                        NVRDeviceDetailActivity.start(this, deviceId, token, channelId, channelName);
                        break;
                }
                finish();
                break;
            case R.id.layout_landscape:
                displayLandscapeView();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            this.finish();
        }
    }

    private void displayLandscapeView() {
        landscapeLayout.setVisibility(View.VISIBLE);
        ivExitFullSceen.setVisibility(View.VISIBLE);
        ivSnapshotLandscape.setVisibility(View.VISIBLE);
        ivSoundSwitchLandscape.setVisibility(View.VISIBLE);
        layoutSbLandscape.setVisibility(View.VISIBLE);
        ivPlayLandscape.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ivExitFullSceen.setVisibility(View.INVISIBLE);
                ivSnapshotLandscape.setVisibility(View.INVISIBLE);
                ivSoundSwitchLandscape.setVisibility(View.INVISIBLE);
                layoutSbLandscape.setVisibility(View.INVISIBLE);
                ivPlayLandscape.setVisibility(View.INVISIBLE);
            }
        }, 3000);
    }


    /**
     * 设置loadingView 状态
     *
     * @param state 0 loading，1 断开，2 播放,3 离线
     */
    private void updateLoadingState(int state) {
        videoPlayState = state;
        if (state == 0) {
            layoutReload.setVisibility(View.GONE);
            layoutLoading.setVisibility(View.VISIBLE);
            layoutOffline.setVisibility(View.GONE);
        } else if (state == 1) {
            layoutReload.setVisibility(View.VISIBLE);
            layoutLoading.setVisibility(View.GONE);
            layoutOffline.setVisibility(View.GONE);
        } else if (state == 2) {
            layoutReload.setVisibility(View.GONE);
            layoutLoading.setVisibility(View.GONE);
            layoutOffline.setVisibility(View.GONE);
        } else if (state == 3) {
            layoutReload.setVisibility(View.GONE);
            layoutLoading.setVisibility(View.GONE);
            layoutOffline.setVisibility(View.VISIBLE);
        }
    }

    //获取摄像机详细信息
    private void getLcDeviceInfo() {
        deviceApiUnit.getLcDeviceInfo(deviceId, new DeviceApiUnit.DeviceApiCommonListener<LcDeviceInfoBean>() {
            @Override
            public void onSuccess(LcDeviceInfoBean bean) {
                token = bean.getExtData().getToken();
                channelId = bean.getExtData().getChannels().get(0).getChannelId();
                play();
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    private void initSeekBarAndTime() {
        String mStartTime = TimeHelper.getTimeHMS(startTime);
        String mEndTime = TimeHelper.getTimeHMS(endTime);
        recorderSeekBar.setMax((int) ((endTime - startTime) / 1000));
        recorderSeekBarLandscape.setMax((int) ((endTime - startTime) / 1000));
        recorderSeekBar.setProgress(0);
        recorderSeekBarLandscape.setProgress(0);
        tvBeginTime.setText(mStartTime);
        tvBeginTimeLandscape.setText(mStartTime);
        tvEndTime.setText(mEndTime);
        tvEndTimeLandscape.setText(mEndTime);
    }

    private void switchPlayAndPause() {
        switch (playStatus) {
            case play_open:
                pause();
                break;
            case play_pause:
                resume();
                break;
            case play_opening:
                stop();  //因为准备播放过程异步执行,这个状态里,无法精准的控制停止操作
                break;
            case play_close:
                initSeekBarAndTime();
                play();
                break;
            default:
                break;
        }
    }


    //开始播放
    public void play() {
        stop();
        playStatus = PlayStatus.play_opening;
        playWindow.playRtspPlayback(token, deviceId, 0,
                deviceId, recordId,
                startTime, endTime, 0);
        setCanSeekChanged(true);
    }

    //停止播放
    public void stop() {
        playStatus = PlayStatus.play_close;
        ivPlay.setImageResource(R.drawable.icon_lc_pause);
        ivPlayLandscape.setImageResource(R.drawable.icon_lc_pause);
        if (isOpenSound) {
            closeAudio();// 关闭音频
            isOpenSound = false;
            ivSoundSwitch.setImageResource(R.drawable.icon_cateye_sound_off);
            ivSoundSwitchLandscape.setImageResource(R.drawable.icon_lc_sound_off_landscape);
        }
        playWindow.stopRtspPlayback();// 关闭视频
        setCanSeekChanged(false);
    }

    /**
     * 描述：继续播放
     */
    public void resume() {
        playWindow.resumeAsync();
        playStatus = PlayStatus.play_open;
        ivPlay.setImageResource(R.drawable.icon_lc_play);
        ivPlayLandscape.setImageResource(R.drawable.icon_lc_play);
    }

    /**
     * 描述：暂停播放
     */
    public void pause() {
        playWindow.pauseAsync();
        playStatus = PlayStatus.play_pause;
        ivPlay.setImageResource(R.drawable.icon_lc_pause);
        ivPlayLandscape.setImageResource(R.drawable.icon_lc_pause);
    }

    //切换声音
    private void switchSound() {
        if (talkStatus != AudioTalkStatus.talk_close || playStatus != PlayStatus.play_open) {
            ToastUtil.show("缓冲或对讲中，无法开启声音");
        } else {
            if (isOpenSound) {
                boolean result = closeAudio();
                if (result) {
                    ivSoundSwitch.setImageResource(R.drawable.icon_cateye_sound_off);
                    ivSoundSwitchLandscape.setImageResource(R.drawable.icon_lc_sound_off_landscape);
//                ToastUtil.show("声音关闭");
                    isOpenSound = false;
                }
            } else {
                boolean result = openAudio();
                if (result) {
                    ivSoundSwitch.setImageResource(R.drawable.icon_cateye_sound_on);
                    ivSoundSwitchLandscape.setImageResource(R.drawable.icon_lc_sound_on_landscape);
//                ToastUtil.show("打开");
                    isOpenSound = true;
                }
            }
        }
    }

    //打开声音
    public boolean openAudio() {
        return playWindow.playAudio() == retOK;
    }

    //关闭声音
    public boolean closeAudio() {
        return playWindow.stopAudio() == retOK;
    }


    //抓拍
    public void capture() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmsss");
        String time = simpleDateFormat.format(System.currentTimeMillis());
        String savePath = FileUtil.getSnapshotPath() + "/" + deviceId;
        File savePathFile = new File(savePath);
        if (!savePathFile.exists()) {
            savePathFile.mkdirs();
        }
        String fileName = savePath + "/" + time + ".jpg";
        int ret = playWindow.snapShot(fileName);
        if (ret == retOK) {
            if (soundPool != null) {
                soundPool.play(snapshotSoundId, 1.0f, 1.0f, 0, 0, 1);
            }
            ivSnapshot.setEnabled(false);
            ivSnapshotLandscape.setEnabled(false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ivSnapshot.setEnabled(true);
                    ivSnapshotLandscape.setEnabled(true);
                }
            }, 1000);
            final Bitmap bitmap = BitmapFactory.decodeFile(fileName);
//            DisplayUtil.snapAnimotion(this, portraitLayout, videoContainer, ivAlbum, bitmap, new DisplayUtil.onCompleteListener() {
//                @Override
//                public void onComplete() {
//                    ivAlbum.setImageBitmap(bitmap);
//                }
//            });
        } else {
            ToastUtil.show(R.string.Cateye_Screenshot_Failed);
        }
    }

    //进入全屏
    private void performFullscreen() {
        getmToolBarHelper().setToolBarVisible(false);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(params);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        portraitLayout.setVisibility(View.GONE);
        bottomLayout.setVisibility(View.GONE);
        landscapeLayout.setVisibility(View.VISIBLE);
        displayLandscapeView();
        DisplayMetrics displayMetrics = SizeUtil.getScreenSize(getApplicationContext());
        int cameraPreviewWidth = displayMetrics.widthPixels;
        int cameraPreviewHeight = (int) ((float) cameraPreviewWidth
                * 9 / 16);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(cameraPreviewWidth, cameraPreviewHeight);
        videoContainer.setLayoutParams(layoutParams);
    }

    //退出全屏
    private void exitFullscreen() {
        getmToolBarHelper().setToolBarVisible(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(params);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

//        portraitLayout.setVisibility(View.VISIBLE);
        bottomLayout.setVisibility(View.VISIBLE);
        landscapeLayout.setVisibility(View.GONE);
        DisplayMetrics displayMetrics = SizeUtil.getScreenSize(getApplicationContext());
        int cameraPreviewWidth = displayMetrics.widthPixels;
        int cameraPreviewHeight = (int) ((float) cameraPreviewWidth
                * 9 / 16);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(cameraPreviewWidth, cameraPreviewHeight);
        layoutParams.gravity = Gravity.CENTER;
        videoContainer.setLayoutParams(layoutParams);

    }

    class MyBaseWindowListener extends LCOpenSDK_EventListener {
        @Override
        public void onPlayerResult(int index, String code, int type) {
            if (type == Business.RESULT_SOURCE_OPENAPI) {
                if (mHander != null) {
                    mHander.post(new Runnable() {
                        public void run() {
                            updateLoadingState(1);
                        }
                    });
                }
            } else {
                if (code.equals(Business.PlayerResultCode.STATE_PACKET_FRAME_ERROR) ||
                        code.equals(Business.PlayerResultCode.STATE_RTSP_TEARDOWN_ERROR) ||
                        code.equals(Business.PlayerResultCode.STATE_RTSP_AUTHORIZATION_FAIL) ||
                        code.equals(Business.PlayerResultCode.STATE_RTSP_KEY_MISMATCH)) {
                    if (mHander != null) {
                        mHander.post(new Runnable() {
                            public void run() {
                                updateLoadingState(1);
                            }
                        });
                    }
                }
            }
        }

        @Override
        public void onPlayBegan(int index) {
            // TODO Auto-generated method stub
            playStatus = PlayStatus.play_open;
            // 建立码流,自动开启音频
            if (mHander != null) {
                mHander.post(new Runnable() {
                    @Override
                    public void run() {
                        ivPlay.setImageResource(R.drawable.icon_lc_play);
                        ivPlayLandscape.setImageResource(R.drawable.icon_lc_play);
                        updateLoadingState(2);
                    }
                });
            }
        }

        @Override
        public void onStreamCallback(int index, byte[] data, int len) {
            Log.d(TAG, "LCOpenSDK_EventListener::onStreamCallback-size : " + len);
            try {
                String path = Environment.getExternalStorageDirectory().getPath() + "/streamCallback.ts";
                FileOutputStream fout = new FileOutputStream(path, true);
                fout.write(data);
                fout.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        @Override
        public void onPlayerTime(int index, final long current) {
            super.onPlayerTime(index, current);
            long tmpTime;
            if (sthls) {
                tmpTime = 0;
            } else {
                tmpTime = startTime / 1000;
            }
            progress = (int) (current - tmpTime);
            if (mHander != null) {
                mHander.post(new Runnable() {
                    @Override
                    public void run() {
                        if (sthls) {
                            recorderSeekBar.setProgress(progress);
                            recorderSeekBarLandscape.setProgress(progress);
                            beginTime = TimeHelper.getTimeHMS(current * 1000);
                            int bh = (int) current / 3600;
                            int bm = (int) current / 60 % 60;
                            int bs = (int) current % 60;
                            beginTime = String.format(Locale.US, "%02d:%02d:%02d", bh, bm, bs);
                            System.out.println("onPlayerTimetest:" + beginTime);
                            tvBeginTime.setText(beginTime);
                            tvBeginTimeLandscape.setText(beginTime);
                        } else {
                            recorderSeekBar.setProgress(progress);
                            recorderSeekBarLandscape.setProgress(progress);
                            beginTime = TimeHelper.getTimeHMS(current * 1000);
                            tvBeginTime.setText(beginTime);
                            tvBeginTimeLandscape.setText(beginTime);
                        }
                    }
                });
            }
        }

        @Override
        public void onWindowLongPressBegin(int index, Direction dir, float dx, float dy) {
        }

        @Override
        public void onWindowLongPressEnd(int index) {
        }

        // 电子缩放
        @Override
        public void onZooming(int index, float dScale) {

        }

        // 云台缩放
        @Override
        public void onZoomEnd(int index, ZoomType zoom) {
        }

        // 滑动开始
        @Override
        public boolean onSlipBegin(int index, Direction dir, float dx, float dy) {
            return true;
        }


        //滑动中
        @Override
        public void onSlipping(int index, Direction dir, float prex, float prey, float dx, float dy) {
        }

        // 滑动结束
        @Override
        public void onSlipEnd(int index, Direction dir, float dx, float dy) {

        }

        public void onWindowDBClick(int index, float dx, float dy) {
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && deviceId != null) {
            if (TextUtils.equals(event.device.devID, deviceId)) {
                if (event.device.isOnLine()) {
                    if (videoPlayState == 3) {//离线变为在线，重新连接
                        updateLoadingState(0);
                        getLcDeviceInfo();
                    }
                } else if (event.device.mode == 3) {
                    finish();
                } else {
                    updateLoadingState(3);
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetworkInfoEvent(NetworkInfoEvent event) {
        if (event.networkInfo.isConnected()) {
            if (videoPlayState != 2) {
                getLcDeviceInfo();
            }
        } else {
            updateLoadingState(1);
        }
    }
}
