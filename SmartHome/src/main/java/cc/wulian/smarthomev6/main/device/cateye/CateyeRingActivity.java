package cc.wulian.smarthomev6.main.device.cateye;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wulian.sdk.android.ipc.rtcv2.IPCController;
import com.wulian.sdk.android.ipc.rtcv2.IPCMsgController;
import com.wulian.sdk.android.ipc.rtcv2.IPCResultCallBack;
import com.wulian.sdk.android.ipc.rtcv2.message.IPCCallStateMsgEvent;
import com.wulian.sdk.android.ipc.rtcv2.message.IPCOnReceivedMsgEvent;
import com.wulian.sdk.android.ipc.rtcv2.message.IPCVideoFrameMsgEvent;
import com.wulian.sdk.android.ipc.rtcv2.message.messagestate.MsgCallState;
import com.wulian.sdk.android.ipc.rtcv2.message.messagestate.MsgReceivedType;
import com.wulian.sdk.android.ipc.rtcv2.utils.IPCGetFrameFunctionType;
import com.wulian.webrtc.ViEAndroidGLES20;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.UnlockDialogActivity;
import cc.wulian.smarthomev6.main.device.penguin.PenguinDetailActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.ICamCloudApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceRelationBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceRelationListBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamGetSipInfoBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.DisplayUtil;
import cc.wulian.smarthomev6.support.utils.FileUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by zbl on 2017/5/8.
 * 猫眼来电界面
 */

public class CateyeRingActivity extends BaseActivity implements View.OnClickListener {

    private static final String KEY_DEST_ID = "DestID";
    private static final String KEY_PICTURE_URL = "pictureURL";
    private static final String PROCESS = "icamProcess";

    private static final String PERMISSION_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    private static final int PERMISSION_REQUEST_CODE = 1;

    private static final int SHOWSPEED_INTERVAL = 3000;// 速度间隔为3毫秒
    private static final int PING_INTERVAL = 10 * 1000;//心跳间隔为10秒

    private String deviceId;
    private String pictureUrl;
    private String bucket;
    private String region;
    private ICamGetSipInfoBean iCamGetSipInfoBean;
    private ICamCloudApiUnit iCamCloudApiUnit;
    private DeviceApiUnit deviceApiUnit;

    private FrameLayout layout_video_container;
    private FrameLayout main_container;
    private View layout_waiting, layout_playing;
    private View btn_ringing_hangup, btn_ringing_connect;
    private TextView tv_hold_speek, tv_timer;
    private ViEAndroidGLES20 view_video;
    private SoundPool soundPool;
    private MediaPlayer mMediaPlayer;
    private int snapshot_sound_id;

    private ImageView iv_bg;
    private View btn_snapshot, btn_hang_up,btn_lock;
    private View btn_hold_speek;
    private ImageView iv_hold_speek;

    private Handler handler = new Handler(Looper.getMainLooper());

    private Bitmap saveLastBitmap;//保存当前图库图片的引用，方便更换的时候回收

    public static boolean hasInit = false;
    private boolean isRadioOpen = true;
    private boolean isPlayAndRecord = false;
    private boolean isFirstCreate = false;
    private long saveReceivedDataSize = 0;
    private long saveStartTime;
    private SimpleDateFormat timerFormat = new SimpleDateFormat("HH:mm:ss");
    private Calendar calendar = Calendar.getInstance();
    private Timer timer;
    /**
     * 0 loading，1 断开，2 播放
     */
    private int videoPlayState;

    public static void start(Context context, String destID, String pictureURL, String bucket, String region) {
        Intent intent = new Intent(context, CateyeRingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_DEST_ID, destID);
        intent.putExtra(KEY_PICTURE_URL, pictureURL);
        intent.putExtra("bucket", bucket);
        intent.putExtra("region", region);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cateye_ring);

        EventBus.getDefault().register(this);
        initView();
        initData();
        initListeners();
        startRingCall();
    }

    @Override
    public boolean enableSwipeBack() {
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoPlayState != 2) {
            getSipInfo();
        }
    }

    @Override
    protected void onStop() {
        if (isPlayAndRecord) {
            IPCController.stopPlayAndRecordAudioAsync(null);
            isPlayAndRecord = false;
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stopRingCall();
        EventBus.getDefault().unregister(this);
        handler.removeCallbacksAndMessages(null);
        IPCController.closeAllVideoAsync(new IPCResultCallBack() {
            @Override
            public void getResult(int i) {
//                IPCController.unRegisterAccountAsync(new IPCResultCallBack() {
//                    @Override
//                    public void getResult(int i) {
//                        IPCController.destroyRTCAsync(new IPCResultCallBack() {
//                            @Override
//                            public void getResult(int i) {
//                                hasInit = false;
//                            }
//                        });
//                    }
//                });
            }
        });
        IPCController.setRender("", null);
        layout_video_container.removeView(view_video);
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        super.onDestroy();
    }

    private void getSipInfo(){
        if (iCamGetSipInfoBean == null){
            iCamCloudApiUnit.doGetSipInfo(deviceId, true, new ICamCloudApiUnit.IcamApiCommonListener<ICamGetSipInfoBean>() {
                @Override
                public void onSuccess(ICamGetSipInfoBean bean) {
                    if (bean != null){
                        iCamGetSipInfoBean = bean;
                        if (isFirstCreate){
                            isFirstCreate = false;
                            IPCController.setRender("", view_video);
                            IPCController.setRenderFlag(iCamGetSipInfoBean.deviceDomain);
                        }
                        startWork();
                    }
                }

                @Override
                public void onFail(int code, String msg) {

                }
            });
        }else {
            if (isFirstCreate){
                isFirstCreate = false;
                IPCController.setRender("", view_video);
                IPCController.setRenderFlag(iCamGetSipInfoBean.deviceDomain);
            }
            startWork();
        }
    }

    private void startWork() {
        initSip();
        handler.postDelayed(closeTask, 60 * 1000);//一分钟后不接听自动挂断
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                PERMISSION_RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{PERMISSION_RECORD_AUDIO},
                    PERMISSION_REQUEST_CODE);
        } else {
//            startWork();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                startWork();
            } else {
                // Permission Denied
                ToastUtil.show(R.string.Toast_Permission_Denied);
//                finish();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected void initView() {
        layout_video_container = (FrameLayout) findViewById(R.id.layout_video_container);
        main_container = (FrameLayout) findViewById(R.id.main_container);
        layout_waiting = findViewById(R.id.layout_waiting);
        layout_playing = findViewById(R.id.layout_playing);
        layout_waiting.setVisibility(View.VISIBLE);
        layout_playing.setVisibility(View.GONE);
        view_video = new ViEAndroidGLES20(this);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layout_video_container.addView(view_video, 0, layoutParams);
        view_video.setKeepScreenOn(true);

        iv_bg = (ImageView) findViewById(R.id.iv_bg);
        btn_ringing_hangup = findViewById(R.id.btn_ringing_hangup);
        btn_ringing_connect = findViewById(R.id.btn_ringing_connect);
        tv_hold_speek = (TextView) findViewById(R.id.tv_hold_speek);
        tv_timer = (TextView) findViewById(R.id.tv_timer);
        btn_snapshot = findViewById(R.id.btn_snapshot);
        btn_hang_up = findViewById(R.id.btn_hang_up);
        btn_hold_speek = findViewById(R.id.btn_hold_speek);
        iv_hold_speek = (ImageView) findViewById(R.id.iv_hold_speek);
        btn_lock = findViewById(R.id.btn_lock);
    }

    protected void initData() {
        isFirstCreate = true;
        Bundle bd = getIntent().getExtras();
        if (bd == null) {
            ToastUtil.show("Bundle is Empty!");
            return;
        }
        deviceId = bd.getString(KEY_DEST_ID);
        pictureUrl = bd.getString(KEY_PICTURE_URL);
        bucket = bd.getString("bucket");
        region = bd.getString("region");
        deviceApiUnit = new DeviceApiUnit(this);
        iCamCloudApiUnit = new ICamCloudApiUnit(this);
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        snapshot_sound_id = soundPool.load(this, R.raw.snapshot, 1);

        setRadioOpen(isRadioOpen);

        checkPermission();
        showBackground();
    }

    protected void initListeners() {
        view_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                makeCall();
            }
        });
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
                        case MotionEvent.ACTION_DOWN: {
                            iv_hold_speek.setImageResource(R.drawable.icon_hold_speek_on);
                            tv_hold_speek.setText(R.string.Cateye_In_Call);
                            IPCController.recordAudioAsync(new IPCResultCallBack() {
                                @Override
                                public void getResult(int i) {
                                    WLog.i("recordAudioAsync result:" + i);
                                }
                            });
                        }
                        break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL: {
                            iv_hold_speek.setImageResource(R.drawable.icon_hold_speek);
                            tv_hold_speek.setText(R.string.CateEye_Detail_Hold_Speek);
                            IPCController.stopRecordAudioAsync(new IPCResultCallBack() {
                                @Override
                                public void getResult(int i) {
                                    WLog.i("stopRecordAudioAsync result:" + i);
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            setRadioOpen(isRadioOpen);
                                        }
                                    });
                                }
                            });
                        }
                        break;
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == btn_snapshot) {
            if (videoPlayState == 2) {
//                view_video.getRenderFrame(IPCGetFrameFunctionType.FRAME_PLAY_THUMBNAIL);
                IPCController.getRenderFrame("hello", IPCGetFrameFunctionType.FRAME_PLAY_THUMBNAIL);
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
            stopRingCall();
            finish();
        } else if (v == btn_ringing_connect) {
            stopRingCall();
            layout_waiting.setVisibility(View.GONE);
            layout_playing.setVisibility(View.VISIBLE);
            handler.removeCallbacks(closeTask);
            startTimer();
            setRadioOpen(true);
        }else if(v==btn_lock){
            getLockData();
        }
    }


    //当已经注册sip账号且没有切换sip账号是不需要重新注册
    private void initSip() {
        if (MainApplication.getApplication().hasRegisterSipAccount &&
                TextUtils.equals(preference.getCurrentSipSuid(), iCamGetSipInfoBean.suid)) {
            makeCall();
            WLog.i(PROCESS, "已经注册sip账号，直接唤醒");
        } else if (MainApplication.getApplication().hasInitSip &&
                !MainApplication.getApplication().hasRegisterSipAccount) {
            WLog.i(PROCESS, "已经初始化sip，但未注册sip账号");
            startRegister();
        } else {
            IPCResultCallBack initRTCAsyncCallback = new IPCResultCallBack() {
                @Override
                public void getResult(int result) {
                    hasInit = result == 0 ? true : false;
                    WLog.i(PROCESS, "未初始化也未注册，此刻开始初始化" + hasInit);
                    if (hasInit) {
                    }
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startRegister();
                        }
                    }, 500);
                }
            };
            IPCController.initRTCAsync(initRTCAsyncCallback);
        }
    }

    private void startRegister() {
        IPCResultCallBack ipcResultCallBack = new IPCResultCallBack() {
            @Override
            public void getResult(int i) {
                boolean isRegisterAccount = i == 0 ? true : false;
                WLog.i(PROCESS, "注册sip账号: " + isRegisterAccount);
                if (isRegisterAccount) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MainApplication.getApplication().hasRegisterSipAccount = true;
                            preference.saveCurrentSipSuid(iCamGetSipInfoBean.suid);
                            makeCall();
                            WLog.i(PROCESS, "注册成功后唤醒");
                        }
                    }, 500);
                }
            }
        };
        String suid = iCamGetSipInfoBean.suid;
        String userSipPwd = iCamGetSipInfoBean.spassword;
        String sdomain = iCamGetSipInfoBean.sipDomain;
        IPCController.registerAccountAsync(ipcResultCallBack, suid, userSipPwd, sdomain);
    }


    private void makeCall() {
        iCamCloudApiUnit.doAwakeDevice(deviceId, new ICamCloudApiUnit.IcamApiCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {
                handler.postDelayed(pingTask, 2000);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        IPCController.makeCallAsync(new IPCResultCallBack() {
                            @Override
                            public void getResult(int i) {
                                WLog.i(PROCESS, "唤醒成功后呼叫:" + i);
                                if (i != 0) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            updateLoadingState(1);
                                        }
                                    });
                                }
                            }
                        }, deviceId, iCamGetSipInfoBean.deviceDomain);
                    }
                }, 2000);
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    private void reCall() {
        makeCall();
    }

    /**
     * 心跳
     */
    private Runnable pingTask = new Runnable() {
        @Override
        public void run() {
            IPCMsgController.MsgWulianBellQueryNotifyHeartBeat(deviceId, iCamGetSipInfoBean.deviceDomain, 30);
            handler.postDelayed(this, PING_INTERVAL);
        }
    };

    private Runnable requestSpeedTask = new Runnable() {
        @Override
        public void run() {
            IPCController.getCallSpeedInfo();
            handler.postDelayed(this, SHOWSPEED_INTERVAL);
        }
    };

    private void showSpeed(String speedInfo) {
//        if (!TextUtils.isEmpty(speedInfo)) {
//            long dataSize = 0;
//            long delatDataSize = 0;
//            try {
//                dataSize = Long.parseLong(speedInfo);
//                delatDataSize = dataSize - saveReceivedDataSize;
//                delatDataSize = (delatDataSize > 0 ? delatDataSize : 0)
//                        / (SHOWSPEED_INTERVAL / 1000);
//                saveReceivedDataSize = dataSize;
//            } catch (NumberFormatException e) {
//                e.printStackTrace();
//            }
//            long speed = delatDataSize / 1000;
//            tv_network_speed.setText("" + (speed > 0 ? speed : 1) + "KB/s");
//        }
    }

    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_timer.setText(timerFormat.format(calendar.getTimeInMillis() + System.currentTimeMillis() - saveStartTime));
                }
            });
        }
    };

    //显示计时
    private void startTimer() {
        saveStartTime = System.currentTimeMillis();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(timerTask, 0, 1000);
    }

    //挂断任务，一分钟不接听则挂断
    private Runnable closeTask = new Runnable() {
        @Override
        public void run() {
            finish();
        }
    };

    private void setRadioOpen(boolean isOpen) {
        this.isRadioOpen = isOpen;
        if (isOpen) {
            IPCController.playAudioAsync(new IPCResultCallBack() {
                @Override
                public void getResult(int i) {
                    WLog.i("playAudioAsync resalt:" + i);
                }
            });
        } else {
            IPCController.stopPlayAudioAsync(new IPCResultCallBack() {
                @Override
                public void getResult(int i) {
                    WLog.i("stopPlayAudioAsync resalt:" + i);
                }
            });
        }
    }

    private void saveBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            String savePath = FileUtil.getSnapshotPath() + "/" + deviceId;
            File savePathFile = new File(savePath);
            if (!savePathFile.exists()) {
                savePathFile.mkdirs();
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmsss");
            String time = simpleDateFormat.format(System.currentTimeMillis());
            String fileName = time + ".jpg";
            FileUtil.saveBitmapToJpeg(bitmap, savePath, fileName);
//            iv_snapshot.setImageBitmap(bitmap);
            DisplayUtil.snapAnimotion(this, main_container, view_video, btn_snapshot, bitmap, new DisplayUtil.onCompleteListener() {
                @Override
                public void onComplete() {
                }
            });
            if (soundPool != null) {
                soundPool.play(snapshot_sound_id, 1.0f, 1.0f, 0, 0, 1);
            }
//            if (saveLastBitmap != null && !saveLastBitmap.isRecycled()) {
//                saveLastBitmap.recycle();
//            }
//            saveLastBitmap = bitmap;
        }
    }

    /**
     * 设置loadingview 状态
     *
     * @param state 0 loading，1 断开，2 播放
     */
    private void updateLoadingState(int state) {
        videoPlayState = state;
//        if (state == 0) {
//            layout_video_reload.setVisibility(View.GONE);
//            layout_video_loading.setVisibility(View.VISIBLE);
//        } else if (state == 1) {
//            layout_video_reload.setVisibility(View.VISIBLE);
//            layout_video_loading.setVisibility(View.GONE);
//        } else {
//            layout_video_reload.setVisibility(View.GONE);
//            layout_video_loading.setVisibility(View.GONE);
//        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(IPCCallStateMsgEvent event) {
        MsgCallState callState = MsgCallState.getMsgCallState(event
                .getCallState());
        switch (callState) {
            case STATE_ESTABLISHED:
                WLog.i(PROCESS, "##建立连接了");
//                isConncted = true;
//                ll_linking_video_refresh.setVisibility(View.GONE);
                break;
            case STATE_TERMINATED:
                WLog.i(PROCESS, "##挂断了");
                updateLoadingState(1);
//                tv_network_speed.setText("0KB/s");
                reCall();
                WLog.i(PROCESS, "sdk挂断重呼");
                handler.removeCallbacks(requestSpeedTask);
                handler.removeCallbacks(pingTask);
                break;
            case STATE_VIDEO_INCOMING:
                iv_bg.setVisibility(View.GONE);
                updateLoadingState(2);
//                isVideoComing = true;
                WLog.i(PROCESS, "##视频流来了");
                handler.postDelayed(requestSpeedTask, SHOWSPEED_INTERVAL);
//                initMultiClickListeners();
//                ICamGlobal.isNeedRefreshSnap = true;
//                myHandler.sendEmptyMessage(VIDEO_STREAM_COMING);
//                myHandler.sendEmptyMessageDelayed(SENDSPEED_REQUEST, 1000);
//                videoCount++;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(IPCOnReceivedMsgEvent event) {
        switch (MsgReceivedType.getMsgReceivedTypeByID(event.getRtcType())) {
            case HANDLE_RTC_CALL_SPEED_TYPE:
//                WLog.i("##摄像头速率");
                showSpeed(event.getMessage());
                break;
            case HANDLE_RTC_CALL_DQ_TYPE:
                WLog.i("##处理DQ信息");
                WLog.i("DQ信息-->" + event.getMessage());
//                dq_message = event.getMessage();
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(IPCVideoFrameMsgEvent event) {
        WLog.d("PML", "End time is:" + System.currentTimeMillis());
        IPCGetFrameFunctionType type = event.getType();
        switch (type) {
            case FRAME_MAIN_THUNBNAIL:
//                WLog.i("#Thread-->" + Thread.currentThread().getName());
//                final Bitmap bitmap = event.getmVideoBitmap();
//                if (bitmap != null) {
//                    WLog.i(this + "接收到退出截屏图片"
//                            + device.getDid());
//                    WLog.i(device.getDid(), bitmap,
//                            PlayVideoActivity.this);
//                    WLog.i("bitmapSize1","bitmapSize = "+bitmap.getWidth()+"---"+bitmap.getHeight());
//                    if (bitmap != null && !bitmap.isRecycled()) {
//                        WLog.i("bitmap recycle");
//                        bitmap.recycle();
//                    }
//                }
                break;
            case FRAME_PLAY_THUMBNAIL:

                WLog.i("#Thread-->" + Thread.currentThread().getName());
                WLog.i("收到抓拍图片");
                if (event.getmVideoBitmap() == null) {
                    WLog.i("抓拍图片为空");
                } else {
                    WLog.i("抓拍图片不为空");
                }
                saveBitmap(event.getmVideoBitmap());
                break;

        }
    }

    private void showBackground() {
        if (TextUtils.isEmpty(pictureUrl)) {
            return;
        }
        iCamCloudApiUnit.doGetRingPic(pictureUrl, bucket, region, new ICamCloudApiUnit.IcamApiCommonListener<String>() {
            @Override
            public void onSuccess(String bean) {
                ImageLoader.getInstance().displayImage(bean, iv_bg);
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    private void getLockData() {
        deviceApiUnit.doGetBindLock(deviceId, "", new DeviceApiUnit.DeviceApiCommonListener<DeviceRelationListBean>() {
            @Override
            public void onSuccess(DeviceRelationListBean bean) {
                UnlockDialogActivity.startByCondition(CateyeRingActivity.this, bean, false);
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.singleCenter(msg);
            }
        });
    }

    /*来电铃声开启*/
    private void startRingCall() {
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(this, alert);  //后面的是try 和catch ，自动添加的
        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
        } catch (SecurityException e1) {
            e1.printStackTrace();
        } catch (IllegalStateException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
        mMediaPlayer.setLooping(true);    //循环播放开
        try {
            mMediaPlayer.prepare();     //后面的是try 和catch ，自动添加的
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();//开始播放
    }

    private void stopRingCall(){
        if (mMediaPlayer != null&&mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer=null;
        }
    }
}
