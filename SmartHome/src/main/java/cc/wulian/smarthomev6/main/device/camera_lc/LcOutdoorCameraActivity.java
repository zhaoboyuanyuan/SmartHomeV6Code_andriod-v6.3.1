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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lechange.opensdk.listener.LCOpenSDK_EventListener;
import com.lechange.opensdk.media.LCOpenSDK_PlayWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.device.UnlockDialogActivity;
import cc.wulian.smarthomev6.main.device.camera_lc.business.Business;
import cc.wulian.smarthomev6.main.device.camera_lc.setting.LcCameraSettingActivity;
import cc.wulian.smarthomev6.main.device.cateye.album.AlbumGridActivity;
import cc.wulian.smarthomev6.main.home.scene.SceneListDialogActivity;
import cc.wulian.smarthomev6.main.message.alarm.LcCameraAlarmActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceRelationListBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.LcDeviceInfoBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.NetworkInfoEvent;
import cc.wulian.smarthomev6.support.utils.DisplayUtil;
import cc.wulian.smarthomev6.support.utils.FileUtil;
import cc.wulian.smarthomev6.support.utils.SizeUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

public class LcOutdoorCameraActivity extends BaseTitleActivity {
    private static final String TAG = "LcOutdoorCameraActivity";
    private static final String PERMISSION_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    private static final int PERMISSION_REQUEST_CODE = 1;
    protected static final int MediaMain = 0; //主码流
    protected static final int MediaAssist = 1;//辅码流
    protected static final int retOK = 0;
    protected static final int retNG = -1;

    private enum AudioTalkStatus {talk_close, talk_opening, talk_open}

    private ViewGroup videoContainer;
    private FrameLayout portraitLayout;
    private FrameLayout landscapeLayout;
    private RelativeLayout bottomLayout;
    private View layoutLoading;
    private View layoutReload;
    private View layoutOffline;
    private TextView tvDefinition;
    private ImageView ivSoundSwitch;
    private ImageView ivAlarmList;
    private ImageView ivLock;
    private ImageView ivScene;
    private ImageView ivSnapshot;
    private ImageView ivAlbum;
    private ImageView ivReplay;
    private ImageView ivFullSceen;
    private ImageView ivExitFullSceen;

    private ViewGroup surfaceParentView;


    private SoundPool soundPool;
    private LCOpenSDK_PlayWindow playWindow = new LCOpenSDK_PlayWindow();
    private LCOpenSDK_EventListener listener;
    private Handler mHander = new Handler();
    private AudioTalkStatus talkStatus = AudioTalkStatus.talk_close; //语音对讲状态
    private DeviceApiUnit deviceApiUnit;
    private Device device;

    private int snapshotSoundId;
    private int bateMode = MediaMain; //状态值
    private int channelId;
    /**
     * 0 loading，1 断开，2 播放, 3 离线
     */
    private int videoPlayState;
    private boolean isPlaying;//正在播放
    private boolean isOpenSound = false;//声音打开
    private boolean isShared;
    private boolean isLandscape;
    private String deviceId;
    private String token;


    public static void start(Context context, String deviceId) {
        Intent intent = new Intent(context, LcOutdoorCameraActivity.class);
        intent.putExtra("deviceId", deviceId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lc_outdoor_detail, true);
        EventBus.getDefault().register(this);
    }


    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitleAndRightImg(getString(R.string.Device_Default_Name_CG22), R.drawable.icon_cateye_setting);
    }

    @Override
    protected void initView() {
        super.initView();
        ivSnapshot = (ImageView) findViewById(R.id.btn_snapshot);
        ivSoundSwitch = (ImageView) findViewById(R.id.btn_sound_switch);
        ivAlarmList = (ImageView) findViewById(R.id.btn_alarmlist);
        ivLock = (ImageView) findViewById(R.id.btn_lock);
        ivScene = (ImageView) findViewById(R.id.btn_scene);
        ivAlbum = (ImageView) findViewById(R.id.iv_album);
        ivReplay = (ImageView) findViewById(R.id.btn_replay);
        ivFullSceen = (ImageView) findViewById(R.id.iv_fullscreen);
        ivExitFullSceen = (ImageView) findViewById(R.id.iv_fullscreen_landscape);
        tvDefinition = (TextView) findViewById(R.id.tv_definition);
        videoContainer = (ViewGroup) findViewById(R.id.live_window_content);
        portraitLayout = (FrameLayout) findViewById(R.id.layout_portrait);
        landscapeLayout = (FrameLayout) findViewById(R.id.layout_landscape);
        bottomLayout = (RelativeLayout) findViewById(R.id.layout_portrait_bottom);
        layoutLoading = findViewById(R.id.layout_video_loading);
        layoutReload = findViewById(R.id.layout_video_reload);
        layoutOffline = findViewById(R.id.layout_video_offline);
        DisplayMetrics displayMetrics = SizeUtil.getScreenSize(getApplicationContext());
        int cameraPreviewWidth = displayMetrics.widthPixels;
        int cameraPreviewHeight = (int) ((float) cameraPreviewWidth
                * 9 / 16);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(cameraPreviewWidth, cameraPreviewHeight);
        layoutParams.gravity = Gravity.CENTER;
        videoContainer.setLayoutParams(layoutParams);
        playWindow.initPlayWindow(LcOutdoorCameraActivity.this, (ViewGroup) findViewById(R.id.live_window_content), 0);

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
        device = mainApplication.getDeviceCache().get(deviceId);
        if (device != null) {
            setTitleText(DeviceInfoDictionary.getNameByTypeAndName(device.type, device.name));
            if (device.isOnLine()) {
                updateLoadingState(0);
            } else {
                updateLoadingState(3);
            }
            //共享设备
            isShared = device.isShared;
        }
        listener = new MyBaseWindowListener();
        deviceApiUnit = new DeviceApiUnit(LcOutdoorCameraActivity.this);
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        playWindow.setWindowListener(listener);
        playWindow.openTouchListener();
        snapshotSoundId = soundPool.load(this, R.raw.snapshot, 1);
        checkPermission();

    }

    @Override
    protected void initListeners() {
        super.initListeners();
        tvDefinition.setOnClickListener(this);
        ivSoundSwitch.setOnClickListener(this);
        ivSnapshot.setOnClickListener(this);
        ivAlbum.setOnClickListener(this);
        ivLock.setOnClickListener(this);
        ivScene.setOnClickListener(this);
        ivReplay.setOnClickListener(this);
        ivAlarmList.setOnClickListener(this);
        ivFullSceen.setOnClickListener(this);
        ivExitFullSceen.setOnClickListener(this);
        layoutReload.setOnClickListener(this);
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
            case R.id.tv_definition:
                switchDefinition();
                break;
            case R.id.btn_sound_switch:
                switchSound();
                break;
            case R.id.btn_snapshot:
                capture();
                break;
            case R.id.iv_album:
                Intent intent = new Intent(this, AlbumGridActivity.class);
                intent.putExtra("devId", deviceId);
                startActivity(intent);
                break;
            case R.id.btn_alarmlist:
                LcCameraAlarmActivity.start(this, deviceId);
                break;
            case R.id.btn_replay:
                stop();
                LcHistoryRecordActivity.start(this, deviceId,channelId);
                break;
            case R.id.btn_lock:
                bindLock();
                break;
            case R.id.btn_scene:
                startActivity(new Intent(this, SceneListDialogActivity.class));
                break;
            case R.id.iv_fullscreen:
                performFullscreen();
                break;
            case R.id.iv_fullscreen_landscape:
                exitFullscreen();
                break;
            case R.id.img_right:
                Intent it = new Intent(this, LcCameraSettingActivity.class);
                it.putExtra("deviceId", deviceId);
                it.putExtra("channelId", String.valueOf(channelId));
                startActivityForResult(it, 1);
                break;
            case R.id.layout_video_reload:
                updateLoadingState(0);
                play(0);
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
                play(0);
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    //切换清晰度
    private void switchDefinition() {
        if (isPlaying) //播放是个异步的,多次点击会使停止播放顺序乱掉
            // 高清切换到流畅
            if (bateMode == MediaMain) {
                bateMode = MediaAssist;
                tvDefinition.setText(R.string.Standard_Definition);
                play(1);
            }// 流畅切换到高清
            else if (bateMode == MediaAssist) {
                bateMode = MediaMain;
                tvDefinition.setText(R.string.High_Definition);
                play(0);
            }
    }

    //开始播放
    public void play(int bateMode) {
        if (isPlaying) {
            stop();
        }
        playWindow.playRtspReal(token, deviceId,
                deviceId, channelId, bateMode);
    }

    //停止播放
    public void stop() {
        isPlaying = false;
        updateLoadingState(-1);
        if (isOpenSound) {
            closeAudio();// 关闭音频
            isOpenSound = false;
            ivSoundSwitch.setImageResource(R.drawable.icon_cateye_sound_off);
        }
        playWindow.stopRtspReal();// 关闭视频
    }

    //切换声音
    private void switchSound() {
        if (talkStatus != AudioTalkStatus.talk_close || !isPlaying) {
            ToastUtil.show("缓冲或对讲中，无法开启声音");
        } else {
            if (isOpenSound) {
                boolean result = closeAudio();
                if (result) {
                    ivSoundSwitch.setImageResource(R.drawable.icon_cateye_sound_off);
//                ToastUtil.show("声音关闭");
                    isOpenSound = false;
                }
            } else {
                boolean result = openAudio();
                if (result) {
                    ivSoundSwitch.setImageResource(R.drawable.icon_cateye_sound_on);
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
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ivSnapshot.setEnabled(true);
                }
            }, 1000);
            final Bitmap bitmap = BitmapFactory.decodeFile(fileName);
            DisplayUtil.snapAnimotion(this, portraitLayout, videoContainer, ivAlbum, bitmap, new DisplayUtil.onCompleteListener() {
                @Override
                public void onComplete() {
                    ivAlbum.setImageBitmap(bitmap);
                }
            });
        } else {
            ToastUtil.show(R.string.Cateye_Screenshot_Failed);
        }
    }

    //门锁绑定
    private void bindLock() {
        deviceApiUnit.doGetBindLock(deviceId, "", new DeviceApiUnit.DeviceApiCommonListener<DeviceRelationListBean>() {
            @Override
            public void onSuccess(DeviceRelationListBean bean) {
                UnlockDialogActivity.startByCondition(LcOutdoorCameraActivity.this, bean, isShared);
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.singleCenter(msg);
            }
        });
    }

    //进入全屏
    private void performFullscreen() {
        isLandscape = true;
        getmToolBarHelper().setToolBarVisible(false);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(params);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        portraitLayout.setVisibility(View.GONE);
        bottomLayout.setVisibility(View.GONE);
        landscapeLayout.setVisibility(View.VISIBLE);
        DisplayMetrics displayMetrics = SizeUtil.getScreenSize(getApplicationContext());
        int cameraPreviewWidth = displayMetrics.widthPixels;
        int cameraPreviewHeight = (int) ((float) cameraPreviewWidth
                * 9 / 16);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(cameraPreviewWidth, cameraPreviewHeight);
        videoContainer.setLayoutParams(layoutParams);
    }

    //退出全屏
    private void exitFullscreen() {
        isLandscape = false;
        getmToolBarHelper().setToolBarVisible(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(params);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        portraitLayout.setVisibility(View.VISIBLE);
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
            isPlaying = true;
            // 建立码流,自动开启音频
            if (mHander != null) {
                mHander.post(new Runnable() {
                    @Override
                    public void run() {
//                        if (isAdded()) {
//                            onClick(mLiveSound);
//                        }
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
                setTitleText(DeviceInfoDictionary.getNameByTypeAndName(event.device.type, event.device.name));
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
