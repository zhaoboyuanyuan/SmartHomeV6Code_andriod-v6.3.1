package cc.wulian.smarthomev6.main.device.lookever;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wulian.oss.H264CustomeView;
import com.wulian.oss.Utils.OSSXMLHandler;
import com.wulian.oss.callback.ConnectDataCallBack;
import com.wulian.oss.model.FederationToken;
import com.wulian.oss.model.GetObjectDataModel;
import com.wulian.oss.service.WulianOssClient;
import com.wulian.sdk.android.ipc.rtcv2.IPCController;
import com.wulian.sdk.android.ipc.rtcv2.IPCMsgApiType;
import com.wulian.sdk.android.ipc.rtcv2.IPCMsgController;
import com.wulian.sdk.android.ipc.rtcv2.IPCResultCallBack;
import com.wulian.sdk.android.ipc.rtcv2.message.IPCcameraXmlMsgEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.VideoTimePeriod;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.cateye.album.AlbumGridActivity;
import cc.wulian.smarthomev6.main.device.lookever.util.XMLHandler;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.apiunit.ICamApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.ICamCloudApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamAliKeyBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamDeviceBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.customview.AngleMeter;
import cc.wulian.smarthomev6.support.customview.CameraHistorySeekBar;
import cc.wulian.smarthomev6.support.customview.MyHorizontalScrollView;
import cc.wulian.smarthomev6.support.utils.DisplayUtil;
import cc.wulian.smarthomev6.support.utils.FileUtil;
import cc.wulian.smarthomev6.support.utils.NetworkUtil;
import cc.wulian.smarthomev6.support.utils.SizeUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by zbl on 2017/6/12.
 * 随便看 回看界面
 */

public class LookeverReplayActivity extends BaseTitleActivity {

    private static final String KEY_ICAM_DEVICE_BEAN = "icam_device_bean";

    public static final String DEFAULT_ALIYUN_STS_OSS_URL_SUFFIX = ".aliyuncs.com";

    private final static int PLAY_BACK_VIDEO_MSG = 1;// 所选时间段处理信息
    private final static int PLAY_BACK_VIDEO_ACTIVE_MSG = 2;// 第一次默s认视频处理信息

    private final static int SHOW_VIDEO_PROGRESS_MSG = 4;// 展示视频中间进度的消息
//    private final static int SHOW_VIDEO_REPLAY_DATE_MSG = 5;// 展示视频当前文字信息
    private final static int CONTROL_SEEKBAR_PROGRESS_MSG = 6;// 控制Seekbar进度的信息
    private final static int REQUEST_NEXT_OBJECT_MSG = 7;// 40秒请求下次时间的信息
    private final static int PLAY_VIDEO_MSG = 9;// 直接播放的信息
    private final static int FILE_MOUNT_EXCEPTION = 10;
    private final static int TAKE_PICTURE_FAIL = 11;
    private final static int FILE_OK = 12;
    private final static int NO_SDCARD_MSG_CALLBACK = 20;
    private final static int NO_RECORD_VIDEO_MSG_CALLBACK = 21;
    private final static int NO_RIGHT_TO_SEE_MSG_CALLBACK = 22;
    private final static int REQUEST_TIMEOUT_MSG_CALLBACK = 23;
    private final static int REMOVE_PLAY_VIDEO_MSG = 100;
    private final static int NOTIFY_HISTORY_RECORD_HEARTBEAT_MSG = 200;

    private final static int STREAM_HANDLE_MSG = 1000;// 视频流过来处理信息
    ExecutorService pjSipThreadExecutor = Executors.newSingleThreadExecutor();

    private enum REPLAY_VIDEO_STATUS {
        INIT, SEEKBAR_PROGRESS, GET_VIDEO, PLAY_VIDEO, DESTROY
    }

    static {
        try {
            System.loadLibrary("openh264");
            System.loadLibrary("WulianICamOpenH264");
        } catch (Exception e) {

        }
    }

    private ICamCloudApiUnit iCamCloudApiUnit;
    private ICamApiUnit iCamApiUnit;
    private ICamDeviceBean iCamDeviceBean;
    private String deviceId;
    private String uniqueDeviceId;
    private String sdomain;
    private String suid;
    private String mSessionID = "";
    private WulianOssClient mClient;

    private SimpleDateFormat mDateAllFormat;

    private RelativeLayout layout_video_container;
    private FrameLayout main_container;
    private View layout_video_loading, layout_video_reload;
    private View btn_replay_back, btn_snapshot, btn_fullscreen;
    private ImageView iv_snapshot;
    private H264CustomeView view_video;
    private MyHorizontalScrollView hsv_container;
    private AngleMeter angleMeter;
    private CameraHistorySeekBar seekbar_history;
//    private TextView tv_play_date;

    //横竖屏切换相关view
    private View layout_portrait, layout_portrait_bottom, layout_landscape;
    private View btn_snapshot_landscape, btn_fullscreen_landscape;

    private SoundPool soundPool;
    private int snapshot_sound_id;

    private int minWidth, maxWidth;
    private int widthRatio = 16, heightRatio = 9;

    private GetObjectDataModel mNextObjectData;
    private REPLAY_VIDEO_STATUS mReplayVideoStatus;
    private boolean mIsFirstRequestRecord = true;
    private boolean mIsRequestVideo = true;
    private long mCurrentTimeStamp;// 当前Date实时显示时间戳
    private long mPlayProgressTimeStamp;// 当前选择播放进度显示时间戳
    private boolean mQueryHistory = true;
    private boolean mHasQueryData = false;
    private String region;
    private String bucketname;

    private List<Pair<Integer, Integer>> mRecordList = new ArrayList<>();

    private Bitmap saveLastBitmap;//保存当前图库图片的引用，方便更换的时候回收

    public static void start(Context context, String ssuid, String sdomain, ICamDeviceBean bean) {
        Intent intent = new Intent(context, LookeverReplayActivity.class);
        intent.putExtra(KEY_ICAM_DEVICE_BEAN, bean);
        intent.putExtra("suid", ssuid);
        intent.putExtra("sdomain", sdomain);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookever_replay, true);
        EventBus.getDefault().register(this);
    }

    @Override
    public boolean enableSwipeBack() {
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        showLastSnapshot();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        controlStopRecord();
        destroyOSS();
        super.onDestroy();
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        String title = getIntent().getStringExtra("title");
        if (TextUtils.isEmpty(title)) {
            setToolBarTitle(getString(R.string.Lookever));
        } else {
            setToolBarTitle(title);
        }
    }

    @Override
    protected void initView() {
        main_container = (FrameLayout) findViewById(R.id.main_container);
        layout_video_container = (RelativeLayout) findViewById(R.id.layout_video_container);
        layout_video_loading = findViewById(R.id.layout_video_loading);
        layout_video_reload = findViewById(R.id.layout_video_reload);
        updateLoadingState(0);

        hsv_container = (MyHorizontalScrollView) findViewById(R.id.hsv_container);
//        tv_play_date = (TextView) findViewById(R.id.tv_play_date);
        angleMeter = (AngleMeter) findViewById(R.id.anglemeter);
        angleMeter.setMaxAngle("100°");
        seekbar_history = (CameraHistorySeekBar) findViewById(R.id.seekbar_history);
        btn_fullscreen = findViewById(R.id.btn_fullscreen);
        btn_replay_back = findViewById(R.id.btn_replay_back);
        btn_snapshot = findViewById(R.id.btn_snapshot);
        iv_snapshot = (ImageView) findViewById(R.id.iv_snapshot);

        layout_portrait = findViewById(R.id.layout_portrait);
        layout_portrait_bottom = findViewById(R.id.layout_portrait_bottom);
        layout_landscape = findViewById(R.id.layout_landscape);
        btn_snapshot_landscape = findViewById(R.id.btn_snapshot_landscape);
        btn_fullscreen_landscape = findViewById(R.id.btn_fullscreen_landscape);

        view_video = new H264CustomeView(this);

        DisplayMetrics displayMetrics = SizeUtil.getScreenSize(getApplicationContext());
        int deviceHeight = displayMetrics.heightPixels;
        int cameraPreviewHeight = deviceHeight * 7 / 10;// 根据布局中的上下比例
        int cameraPreviewWidth = (int) ((float) cameraPreviewHeight
                / heightRatio * widthRatio);
        minWidth = displayMetrics.widthPixels;
        maxWidth = cameraPreviewWidth;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(cameraPreviewWidth, cameraPreviewHeight);
        layoutParams.gravity = Gravity.CENTER;
        layout_video_container.addView(view_video, 0, layoutParams);
        view_video.setKeepScreenOn(true);
    }

    @Override
    protected void initData() {
        Bundle bd = getIntent().getExtras();
        if (bd == null) {
            ToastUtil.show("Bundle is Empty!");
            return;
        }
        iCamApiUnit = new ICamApiUnit(this, ApiConstant.getUserID());
        iCamCloudApiUnit = new ICamCloudApiUnit(this);
        iCamDeviceBean = (ICamDeviceBean) bd.getSerializable(KEY_ICAM_DEVICE_BEAN);
        deviceId = iCamDeviceBean.did;
        uniqueDeviceId = iCamDeviceBean.uniqueDeviceId;
        sdomain = bd.getString("sdomain");
        suid = bd.getString("suid");
        Device device = MainApplication.getApplication().getDeviceCache().get(deviceId);
        setTitleText(DeviceInfoDictionary.getNameByTypeAndName(device.type,device.name));
        mDateAllFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        snapshot_sound_id = soundPool.load(this, R.raw.snapshot, 1);


        long initTime = System.currentTimeMillis() / 1000 - 60 * 60;
        initTime = (long) (60 * Math.round(initTime / (float) 60));
        mPlayProgressTimeStamp = initTime;
        // Log.d("PML",
        // "init mPlayProgressTimeStamp is:"+mPlayProgressTimeStamp);
        seekbar_history.setMidTimeStamp(initTime);
//        showDate(initTime);
        //初始化sip
        getSipAccount();
        // 初始化OSS
        initOSS();
    }

    @Override
    protected void initListeners() {
        btn_snapshot.setOnClickListener(this);
        iv_snapshot.setOnClickListener(this);
        btn_replay_back.setOnClickListener(this);
        layout_video_reload.setOnClickListener(this);
        btn_fullscreen.setOnClickListener(this);

        btn_snapshot_landscape.setOnClickListener(this);
        btn_fullscreen_landscape.setOnClickListener(this);


        hsv_container.setOnScrollChangedListener(new MyHorizontalScrollView.OnScrollChangedListener() {

            @Override
            public void onScrollChanged(HorizontalScrollView sv, int l, int t, int oldl, int oldt) {

                int newDeltaWidth = layout_video_container.getWidth() - minWidth;
                if (newDeltaWidth <= 0) {
                    angleMeter.refreshAngle(0);
                } else {
                    angleMeter.refreshAngle(sv.getScrollX() * 1.0 / newDeltaWidth);
                }
            }

        });

        seekbar_history.setHistroySeekChangeListener(new CameraHistorySeekBar.HistroySeekChangeListener() {
            @Override
            public void onChangeSeekBarTempAction(long timeStamp) {
                mReplayVideoStatus = REPLAY_VIDEO_STATUS.SEEKBAR_PROGRESS;
                mPlayVideoHandler.removeMessages(REQUEST_NEXT_OBJECT_MSG);
                mPlayVideoHandler.removeMessages(PLAY_VIDEO_MSG);
                mPlayVideoHandler.removeMessages(CONTROL_SEEKBAR_PROGRESS_MSG);
//                mPlayVideoHandler.removeMessages(SHOW_VIDEO_REPLAY_DATE_MSG);
                mPlayVideoHandler.removeMessages(REMOVE_PLAY_VIDEO_MSG);
                showReplayProgress();
//                showDate(timeStamp);
            }

            @Override
            public void onChangeSeekBarFinalAction(long timeStamp, boolean isRecord) {
                mPlayVideoHandler.removeMessages(CONTROL_SEEKBAR_PROGRESS_MSG);
//                mPlayVideoHandler.removeMessages(SHOW_VIDEO_REPLAY_DATE_MSG);
                mPlayVideoHandler.removeMessages(REMOVE_PLAY_VIDEO_MSG);
                mPlayVideoHandler.removeMessages(REQUEST_NEXT_OBJECT_MSG);
                mPlayVideoHandler.removeMessages(PLAY_VIDEO_MSG);
                WLog.d("PML", "onChangeSeekBarFinalAction is:" + timeStamp + ";isRecord is:" + isRecord);
                Message msg = mPlayVideoHandler.obtainMessage(PLAY_BACK_VIDEO_MSG);
                msg.arg1 = isRecord ? (int) timeStamp : -1;
                mPlayVideoHandler.sendMessageDelayed(msg, 500);
            }

            @Override
            public void onActionDownMessage() {
                mPlayVideoHandler.removeMessages(PLAY_BACK_VIDEO_MSG);
            }
        });

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == btn_snapshot || v == btn_snapshot_landscape) {
            Bitmap bitmap = view_video.getBitmap();
            if (bitmap != null) {
                Bitmap saveBitmap = Bitmap.createBitmap(bitmap);
                saveBitmap(saveBitmap);
            }
            btn_snapshot.setEnabled(false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    btn_snapshot.setEnabled(true);
                }
            }, 1000);
        } else if (v == iv_snapshot) {
            Intent intent = new Intent(this, AlbumGridActivity.class);
            intent.putExtra("devId", deviceId);
            startActivity(intent);
        } else if (v == btn_fullscreen) {
            performFullscreen();
        } else if (v == btn_fullscreen_landscape) {
            exitFullscreen();
        } else if (v == layout_video_reload) {
//            updateLoadingState(0);
//            makeCall();
        } else if (v == btn_replay_back) {
            finish();
        }
    }

    private void getSipAccount() {
        IPCResultCallBack ipcResultCallBack = new IPCResultCallBack() {
            @Override
            public void getResult(int i) {
                boolean isRegisterAccount = i == 0 ? true : false;
                WLog.i("**Sip-账号注册结果：" + isRegisterAccount);
                if (isRegisterAccount) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            requestDownloadReplay();
                        }
                    });
                }
            }
        };
        WLog.i("**Sip-账号注册");
        String userSipPwd = ICamApiUnit.getUserSipPwd(ApiConstant.getUserID());
        IPCController.registerAccountAsync(ipcResultCallBack, suid, userSipPwd, sdomain);
    }

    private void requestDownloadReplay() {
        iCamCloudApiUnit.doGetReplay(deviceId, sdomain, new ICamCloudApiUnit.IcamApiCommonListener<ICamAliKeyBean>() {
            @Override
            public void onSuccess(ICamAliKeyBean bean) {
                if (bean != null) {
                    ICamAliKeyBean dataBean = bean;
                    region = dataBean.Region;
                    region += DEFAULT_ALIYUN_STS_OSS_URL_SUFFIX;
                    bucketname = dataBean.Bucket;
                    FederationToken stsToken = new FederationToken();
                    stsToken.setRequestId(dataBean.RequestId);
                    stsToken.setAccessKeySecret(dataBean.AccessKeySecret);
                    stsToken.setAccessKeyId(dataBean.AccessKeyId);
                    stsToken.setSecurityToken(dataBean.SecurityToken);
                    stsToken.setExpiration(60 * 60 * 2);
                    mClient.setFederationToken(stsToken,region,bucketname);
                    controlStartRecord();
                } else {
                    showMsg(R.string.http_unknow_error);
                }
            }

            @Override
            public void onFail(int code, String msg) {
                InEnableReplay();
                showMsg(R.string.Service_Error);
            }
        });
//        iCamApiUnit.getReplayData(deviceId, sdomain, new ICamApiUnit.ICamApiCommonListener<ICamReplayBean>() {
//            @Override
//            public void onSuccess(ICamReplayBean bean) {
//                if (bean != null && bean.getData() != null) {
//                    ICamReplayDataBean dataBean = bean.getData();
//                    region = dataBean.getRegion();
//                    region += DEFAULT_ALIYUN_STS_OSS_URL_SUFFIX;
//                    bucketname = dataBean.getBucket();
//                    FederationToken stsToken = new FederationToken();
//                    stsToken.setRequestId(dataBean.getRequestId());
//                    stsToken.setAccessKeySecret(dataBean.getAccessKeySecret());
//                    stsToken.setAccessKeyId(dataBean.getAccessKeyId());
//                    stsToken.setSecurityToken(dataBean.getSecurityToken());
//                    stsToken.setExpiration(60 * 60 * 2);
//                    mClient.setFederationToken(stsToken,region,bucketname);
//                    controlStartRecord();
//                } else {
//                    showMsg(R.string.http_unknow_error);
//                }
//            }
//
//            @Override
//            public void onFail(int code, String msg) {
//                InEnableReplay();
//                showMsg(R.string.Service_Error);
//            }
//        });
    }

    ConnectDataCallBack mListener = new ConnectDataCallBack() {
        @Override
        public void onH264StreamMessage(byte[] data, int width, int height) {
            if (mPlayVideoHandler != null) {
                Bundle bd = new Bundle();
                bd.putByteArray("data", data);
                bd.putInt("width", width);
                bd.putInt("height", height);
                Message msg = mPlayVideoHandler.obtainMessage(STREAM_HANDLE_MSG);
                msg.setData(bd);
                mPlayVideoHandler.sendMessage(msg);
                WLog.i("replayStep","---step6--send");
                Log.e("H264", "返回h264消息");
            }
        }

        public void onRequestObjectEndFlag() {
            Log.e("H264", "结束");
        }

        @Override
        public void onError(Exception error) {
            Log.e("H264", "错误");
        }

        @Override
        public void onDisconnect(int code, String reason) {
            Log.e("H264", "断开");
        }

        @Override
        public void onRequestGetObjectResultOK(long timestamp) {
            // LibraryLoger.d("PML", "onRequestGetObjectResultOK timestamp is:"
            // + timestamp + ";mPlayProgressTimeStamp is:"
            // + mPlayProgressTimeStamp);
            // Log.d("PML", "onRequestGetObjectResultOK timestamp is:" +
            // timestamp
            // + ";mPlayProgressTimeStamp is:" + mPlayProgressTimeStamp);
            Log.e("H264", "确认当前视频已经过来了");
            if (timestamp == mPlayProgressTimeStamp
                    && mReplayVideoStatus == REPLAY_VIDEO_STATUS.GET_VIDEO) {
                mReplayVideoStatus = REPLAY_VIDEO_STATUS.PLAY_VIDEO;
                mCurrentTimeStamp = timestamp;
                mPlayVideoHandler.sendMessage(mPlayVideoHandler
                        .obtainMessage(SHOW_VIDEO_PROGRESS_MSG));
            }

            mCurrentTimeStamp = timestamp;
            mPlayVideoHandler.removeMessages(REQUEST_NEXT_OBJECT_MSG);
            mPlayVideoHandler.removeMessages(PLAY_VIDEO_MSG);
            mPlayVideoHandler.removeMessages(CONTROL_SEEKBAR_PROGRESS_MSG);
//            mPlayVideoHandler.removeMessages(SHOW_VIDEO_REPLAY_DATE_MSG);
            mPlayVideoHandler.removeMessages(REMOVE_PLAY_VIDEO_MSG);
//            mPlayVideoHandler.sendMessageDelayed(mPlayVideoHandler
//                    .obtainMessage(SHOW_VIDEO_REPLAY_DATE_MSG), 1000);
            mPlayVideoHandler.sendMessage(mPlayVideoHandler
                    .obtainMessage(CONTROL_SEEKBAR_PROGRESS_MSG));
            mPlayVideoHandler.sendMessageDelayed(
                    mPlayVideoHandler.obtainMessage(REQUEST_NEXT_OBJECT_MSG),
                    40000);
            mPlayVideoHandler.sendMessageDelayed(
                    mPlayVideoHandler.obtainMessage(PLAY_VIDEO_MSG), 60000);
        }
    };
    private void initOSS() {
        mClient = new WulianOssClient(mListener, getApplicationContext());
        mClient.initConfigData();
        mClient.disableLog();
    }

    private void destroyOSS() {
        if (mClient != null) {
            mClient.disconnect();
        }
    }

    private void connectOSS() {
        // 连接
        mClient.connect();
//        mClient.setIsReverse(mIsVideoInvert);
    }

    private void queryHistoryVideo() {
        mQueryHistory = true;
        mHasQueryData = false;
        IPCMsgController.MsgQueryHistoryRecord(uniqueDeviceId, sdomain);
        WLog.i("ReplayStep","---step2:查询历史记录");
        mPlayVideoHandler.sendEmptyMessageDelayed(NO_RECORD_VIDEO_MSG_CALLBACK, 10000);
    }

    private void playHistoryVideo(long time) {
        mPlayProgressTimeStamp = time;
        IPCMsgController.MsgControlHistoryRecordProgress(uniqueDeviceId, sdomain, mSessionID,
                time);
        WLog.i("ReplayStep","---step4:控制历史记录进度");
    }

    private long getDefaultAvaiableProgressTimeStamp(long time) {
        long result = -1;
        int size = mRecordList.size();
        // Log.d("PML",
        // "mRecordList size is:"+mRecordList.size()+";time is:"+time);
        Pair<Integer, Integer> pair = null;
        for (int i = size - 1; i >= 0; i--) {
            pair = mRecordList.get(i);
            // Log.d("PML",
            // "mRecordList i:"+i+"; pair is:left="+pair.first+";second="+pair.second);
            if (pair.second > time && pair.first >= time) {
                result = pair.first;
                // Log.d("PML", "AAAAAAAA:result is:"+result);
            }
            if (pair.second < time) {
                if (result == -1) {
                    if (pair.second - pair.first >= 60 * 60) {
                        return pair.second - 60 * 60;
                    } else {
                        return pair.first;
                    }
                } else {
                    return result;
                }
            }
        }
        return result;
    }

    private long getNextProgressTimeStamp(long initTime) {
        int size = mRecordList.size();
        for (int i = 0; i < size; i++) {
            Pair<Integer, Integer> pair = mRecordList.get(i);
            if (pair.first <= initTime && pair.second >= initTime) {
                if (initTime + 60 <= pair.second) {
                    return initTime + 60;
                } else {
                    if (i < size - 1) {
                        return mRecordList.get(i + 1).first;
                    } else {
                        return -1;
                    }
                }
            }
        }
        return -1;
    }

    private void controlStartRecord() {
        IPCMsgController.MsgControlStartRecord(uniqueDeviceId, sdomain,
                NetworkUtil.getImsi(getBaseContext()));
        WLog.i("ReplayStep","---step1:开始进行历史记录");
        mPlayVideoHandler.sendEmptyMessageDelayed(NO_SDCARD_MSG_CALLBACK, 10000);
    }

    private void controlStopRecord() {
        // Log.d("PML", "controlStopRecord");

        mPlayVideoHandler.removeMessages(NOTIFY_HISTORY_RECORD_HEARTBEAT_MSG);
        mPlayVideoHandler.removeMessages(REMOVE_PLAY_VIDEO_MSG);
        mPlayVideoHandler.removeMessages(NO_RIGHT_TO_SEE_MSG_CALLBACK);
        mPlayVideoHandler.removeMessages(NO_RECORD_VIDEO_MSG_CALLBACK);
        mPlayVideoHandler.removeMessages(NO_SDCARD_MSG_CALLBACK);
        mPlayVideoHandler.removeMessages(FILE_OK);
        mPlayVideoHandler.removeMessages(TAKE_PICTURE_FAIL);
        mPlayVideoHandler.removeMessages(FILE_MOUNT_EXCEPTION);
        mPlayVideoHandler.removeMessages(PLAY_VIDEO_MSG);
        mPlayVideoHandler.removeMessages(REQUEST_NEXT_OBJECT_MSG);
        mPlayVideoHandler.removeMessages(CONTROL_SEEKBAR_PROGRESS_MSG);
//        mPlayVideoHandler.removeMessages(SHOW_VIDEO_REPLAY_DATE_MSG);
        mPlayVideoHandler.removeMessages(SHOW_VIDEO_PROGRESS_MSG);
        mPlayVideoHandler.removeMessages(PLAY_BACK_VIDEO_ACTIVE_MSG);
        mPlayVideoHandler.removeMessages(PLAY_BACK_VIDEO_MSG);
        mPlayVideoHandler.removeMessages(STREAM_HANDLE_MSG);
        if (!TextUtils.isEmpty(mSessionID)) {
            IPCMsgController.MsgControlStopRecord(uniqueDeviceId,
                    sdomain, mSessionID);
        }
    }

    private void notifyRecordHeartBeat() {
        // Log.d("PML", "notifyRecordHeartBeat");
        if (!TextUtils.isEmpty(mSessionID)) {
            IPCMsgController
                    .MsgNotifyHistoryRecordHeartbeat(uniqueDeviceId,
                            sdomain, mSessionID);
        }
    }

    private Handler mPlayVideoHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PLAY_BACK_VIDEO_MSG:
                    long time = msg.arg1;
                    if (time >= 0) {
                        WLog.i("ReplayStep","---step3:所选时间段处理信息");
                        mCurrentTimeStamp = time;
                        mReplayVideoStatus = REPLAY_VIDEO_STATUS.GET_VIDEO;
                        showReplayProgress();
                        mIsRequestVideo = true;
                        playHistoryVideo(time);
                    } else {
                        showMsg(R.string.Time_No_Video);
                    }
                    break;
                case PLAY_BACK_VIDEO_ACTIVE_MSG:
                    WLog.i("ReplayStep","---step3:第一次默认视频处理信息");
                    if (seekbar_history.getIsMidRecord()
                            && seekbar_history.getTimeStamp() > 0) {
                        mPlayProgressTimeStamp = seekbar_history.getTimeStamp();
                        WLog.d("PML", "mPlayProgressTimeStamp seekbar_history is:"
                                + mPlayProgressTimeStamp);
                        mReplayVideoStatus = REPLAY_VIDEO_STATUS.GET_VIDEO;
//                        showDate(mPlayProgressTimeStamp);
                        showReplayProgress();
                        playHistoryVideo(seekbar_history.getTimeStamp());
                    } else {
                        // Log.d("PML",
                        // "before getDefaultAvaiableProgressTimeStamp mPlayProgressTimeStamp is:"+mPlayProgressTimeStamp);
                        long tempTime = getDefaultAvaiableProgressTimeStamp(mPlayProgressTimeStamp);
                        // Log.d("PML",
                        // "after getDefaultAvaiableProgressTimeStamp mPlayProgressTimeStamp is:"+tempTime);
                        if (tempTime > 0) {
                            mPlayProgressTimeStamp = tempTime;
                            WLog.d("PML", "mPlayProgressTimeStamp is:" + mPlayProgressTimeStamp);
                            // Log.d("PML",
                            // "inner getDefaultAvaiableProgressTimeStamp mPlayProgressTimeStamp is:"+mPlayProgressTimeStamp);
                            seekbar_history
                                    .setMidTimeStamp(mPlayProgressTimeStamp);
                            mReplayVideoStatus = REPLAY_VIDEO_STATUS.GET_VIDEO;
                            showReplayProgress();
//                            showDate(mPlayProgressTimeStamp);
                            playHistoryVideo(mPlayProgressTimeStamp);
                        }
                    }
                    break;
                case STREAM_HANDLE_MSG:
                    Bundle bd = msg.getData();
                    byte[] data = bd.getByteArray("data");
                    int width = bd.getInt("width");
                    int height = bd.getInt("height");
                    if (view_video != null) {
                        view_video.PlayVideo(data, width, height);
                        WLog.i("replayStep","--PlayVideo--");
                    }
                    break;
                case SHOW_VIDEO_PROGRESS_MSG:
                    showReplayProgress();
                    break;
//                case SHOW_VIDEO_REPLAY_DATE_MSG:
//                    mCurrentTimeStamp++;
//                    showDate(mCurrentTimeStamp);
//                    sendMessageDelayed(
//                            mPlayVideoHandler
//                                    .obtainMessage(SHOW_VIDEO_REPLAY_DATE_MSG),
//                            1000);
//                    break;
                case CONTROL_SEEKBAR_PROGRESS_MSG:
                    seekbar_history.setMidTimeStamp(mCurrentTimeStamp);
                    sendMessageDelayed(
                            mPlayVideoHandler.obtainMessage(CONTROL_SEEKBAR_PROGRESS_MSG),
                            5000);
                    break;
                case REMOVE_PLAY_VIDEO_MSG:
//                    mPlayVideoHandler.removeMessages(SHOW_VIDEO_REPLAY_DATE_MSG);
                    break;
                case REQUEST_NEXT_OBJECT_MSG:
                    // Log.d("PML", "before getNextProgressTimeStamp is:"
                    // + mPlayProgressTimeStamp);
                    mPlayProgressTimeStamp = getNextProgressTimeStamp(mPlayProgressTimeStamp);
                    // Log.d("PML",
                    // "REQUEST_NEXT_OBJECT_MSG mPlayProgressTimeStamp is:"+mPlayProgressTimeStamp);
                    // Log.d("PML", "after getNextProgressTimeStamp is:"
                    // + mPlayProgressTimeStamp);
                    if (mPlayProgressTimeStamp > 0) {
                        playHistoryVideo(mPlayProgressTimeStamp);
                    } else {
                        mPlayVideoHandler.removeMessages(REQUEST_NEXT_OBJECT_MSG);
                        mPlayVideoHandler.removeMessages(PLAY_VIDEO_MSG);
                        mPlayVideoHandler
                                .removeMessages(CONTROL_SEEKBAR_PROGRESS_MSG);
                        mPlayVideoHandler.sendMessageDelayed(mPlayVideoHandler
                                .obtainMessage(REMOVE_PLAY_VIDEO_MSG), 20000);
                    }
                    break;
                case PLAY_VIDEO_MSG:
                    if (mNextObjectData != null && mNextObjectData.getTimeStamp() > 0) {
                        WLog.i("ReplayStep","---step5:直接播放的信息");
                        WLog.i("ReplayStep","---step5:直接播放的信息"+mNextObjectData+"--"+mIsFirstRequestRecord);
                        mClient.playOSSObjectName(mNextObjectData,mIsFirstRequestRecord);
                        if (mIsFirstRequestRecord) {
                            mIsFirstRequestRecord = false;
                        }
                    }
                    break;
                case FILE_MOUNT_EXCEPTION:
                    showMsg(R.string.No_SD_Look_Back);
                    break;
                case TAKE_PICTURE_FAIL:
                    showMsg(R.string.Cateye_Screenshot_Failed);
                    break;
                case FILE_OK:
                    showMsg(R.string.Cateye_Screenshot_Success);
                    break;
                case NO_SDCARD_MSG_CALLBACK:
                    InEnableReplay();
                    showMsg(R.string.No_SD);
                    break;
                case NO_RIGHT_TO_SEE_MSG_CALLBACK:
                    InEnableReplay();
//                    showMsg(R.string.replay_one_people_to_see);
                    break;
                case REQUEST_TIMEOUT_MSG_CALLBACK:
                    InEnableReplay();
                    showMsg(R.string.Http_Time_Out);
                    break;
                case NO_RECORD_VIDEO_MSG_CALLBACK:
                    InEnableReplay();
//                    showMsg(R.string.replay_no_video_in_sdcard);
                    break;
                case NOTIFY_HISTORY_RECORD_HEARTBEAT_MSG:
                    mPlayVideoHandler
                            .removeMessages(NOTIFY_HISTORY_RECORD_HEARTBEAT_MSG);
                    notifyRecordHeartBeat();
                    mPlayVideoHandler.sendMessageDelayed(mPlayVideoHandler
                                    .obtainMessage(NOTIFY_HISTORY_RECORD_HEARTBEAT_MSG),
                            10000);
                    break;
                default:
                    break;
            }
        }
    };


    private void InEnableReplay() {
//        dismissBaseDialog();
//        if (mUpDownAnim != null && mUpDownAnim.hasStarted()) {
//            mUpDownAnim.cancel();
//        }
        seekbar_history.setActionEnable(false);
        seekbar_history.setRecordList(mRecordList);
//        ll_control_fullscreen_bar.setEnabled(false);
//        tv_play_date.setText("");
//        btn_control_snapshot_portrait.setEnabled(false);
//        cb_control_record_portrait.setEnabled(false);
    }

    private void EnableReplay() {
        seekbar_history.setActionEnable(true);
//        ll_control_fullscreen_bar.setEnabled(true);
//        btn_control_snapshot_portrait.setEnabled(true);
//        cb_control_record_portrait.setEnabled(true);
    }


    private void showReplayProgress() {
        switch (mReplayVideoStatus) {
            case INIT:
//                if (mUpDownAnim.hasEnded()) {
//                    mUpDownAnim.startNow();
//                }
                layout_video_loading.setVisibility(View.VISIBLE);
//                rl_replay_player.setBackgroundColor(getResources().getColor(R.color.transparent_deep));
//                tv_progress_video_tip.setText(R.string.common_loading);
                break;
            case SEEKBAR_PROGRESS:
//                if (mUpDownAnim.hasEnded()) {
//                    mUpDownAnim.startNow();
//                }
                layout_video_loading.setVisibility(View.VISIBLE);
//                rl_replay_player.setBackgroundColor(getResources().getColor(
//                        R.color.transparent_deep));
//                tv_progress_video_tip.setText(R.string.replay_moving_through_time);
                break;
            case GET_VIDEO:
//                if (mUpDownAnim.hasEnded()) {
//                    mUpDownAnim.startNow();
//                }
                layout_video_loading.setVisibility(View.VISIBLE);
//                rl_replay_player.setBackgroundColor(getResources().getColor(R.color.transparent_deep));
//                tv_progress_video_tip.setText(R.string.replay_fetching_videos);
                break;
            case PLAY_VIDEO:
//                if (mUpDownAnim.hasStarted()) {
//                    mUpDownAnim.cancel();
//                }
                layout_video_loading.setVisibility(View.GONE);
//                rl_replay_player.setBackgroundColor(getResources().getColor(R.color.transparent));
                break;
            default:
                break;
        }
    }

//    private void showDate(long time) {
//        tv_play_date.setText(mDateAllFormat.format(new Date(time * 1000)));
//    }

    /**
     * 设置loadingview 状态
     *
     * @param state 0 loading，1 断开，2 播放
     */
    private void updateLoadingState(int state) {
        if (state == 0) {
            layout_video_reload.setVisibility(View.GONE);
            layout_video_loading.setVisibility(View.VISIBLE);
        } else if (state == 1) {
            layout_video_reload.setVisibility(View.VISIBLE);
            layout_video_loading.setVisibility(View.GONE);
        } else {
            layout_video_reload.setVisibility(View.GONE);
            layout_video_loading.setVisibility(View.GONE);
        }
    }


    /**
     * 进入全屏
     */
    private void performFullscreen() {
        getmToolBarHelper().setToolBarVisible(false);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(params);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        layout_portrait.setVisibility(View.GONE);
        layout_portrait_bottom.setVisibility(View.GONE);
        btn_replay_back.setVisibility(View.GONE);
        layout_landscape.setVisibility(View.VISIBLE);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 全屏
    }

    /**
     * 退出全屏
     */
    private void exitFullscreen() {
        getmToolBarHelper().setToolBarVisible(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(params);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        layout_portrait.setVisibility(View.VISIBLE);
        layout_portrait_bottom.setVisibility(View.VISIBLE);
        btn_replay_back.setVisibility(View.VISIBLE);
        layout_landscape.setVisibility(View.GONE);

        DisplayMetrics displayMetrics = SizeUtil.getScreenSize(getApplicationContext());
        int deviceHeight = displayMetrics.heightPixels;
        int cameraPreviewHeight = deviceHeight * 7 / 9;// 根据布局中的上下比例
        int cameraPreviewWidth = (int) ((float) cameraPreviewHeight
                / heightRatio * widthRatio);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(cameraPreviewWidth, cameraPreviewHeight);
        view_video.setLayoutParams(layoutParams);
    }

    @Override
    public void onBackPressed() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            exitFullscreen();
        } else {
            super.onBackPressed();
        }
    }


    private void showLastSnapshot() {
        String savePath = FileUtil.getSnapshotPath() + "/" + deviceId;
        File savePathFile = new File(savePath);
        String[] bmpFiles = savePathFile.list();
        if (bmpFiles != null && bmpFiles.length > 0) {
            String bmpFile = bmpFiles[bmpFiles.length - 1];
            Bitmap bitmap = BitmapFactory.decodeFile(savePath + "/" + bmpFile);
            iv_snapshot.setImageBitmap(bitmap);
//            if (saveLastBitmap != null && !saveLastBitmap.isRecycled()) {
//                saveLastBitmap.recycle();
//            }
//            saveLastBitmap = bitmap;
        }else {
            iv_snapshot.setImageResource(R.drawable.icon_image_gallery);
        }
    }

    private void saveBitmap(final Bitmap bitmap) {
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
            DisplayUtil.snapAnimotion(this, main_container, view_video, iv_snapshot, bitmap, new DisplayUtil.onCompleteListener() {
                @Override
                public void onComplete() {
                    iv_snapshot.setImageBitmap(bitmap);
                }
            });
            if(soundPool != null){
                soundPool.play(snapshot_sound_id, 1.0f, 1.0f, 0, 0, 1);
            }
//            if (saveLastBitmap != null && !saveLastBitmap.isRecycled()) {
//                saveLastBitmap.recycle();
//            }
//            saveLastBitmap = bitmap;
        }
    }

    public void showMsg(int stringResId) {
        ToastUtil.show(stringResId);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(IPCcameraXmlMsgEvent event) {
        if (event.getCode() != 0) {
            SipDataReturn(false, event.getApiType(), event.getMessage(),
                    event.getDestURI(), String.valueOf(event.getCode()));
            WLog.i("sip", "fail---" + "apiType = " + event.getApiType() + "msg = " + event.getMessage());
        } else {
            SipDataReturn(true, event.getApiType(), event.getMessage(),
                    event.getDestURI(), String.valueOf(event.getCode()));
            WLog.i("sip", "success---" + "apiType = " + event.getApiType() + "msg = " + event.getMessage());
        }
    }

    protected void SipDataReturn(boolean isSuccess, IPCMsgApiType apiType, String xmlData, String from, String to) {
        if (isSuccess) {
            switch (apiType) {
                case QUERY_HISTORY_RECORD:
                    if (!mHasQueryData) {
                        mPlayVideoHandler
                                .removeMessages(NO_RECORD_VIDEO_MSG_CALLBACK);
                        EnableReplay();
                        if (mQueryHistory) {
                            mRecordList.clear();
                        }
                        mQueryHistory = false;
                        List<VideoTimePeriod> mTimeList = XMLHandler
                                .getHistoryRecordList(xmlData);
                        WLog.i("ReplayStep","---step2:查询历史记录返回数据"+xmlData);
                        for (VideoTimePeriod videoTimePeriod : mTimeList) {
                            Pair<Integer, Integer> pair = new Pair<Integer, Integer>(
                                    (int) videoTimePeriod.getTimeStamp(),
                                    (int) videoTimePeriod.getEndTimeStamp());
                            mRecordList.add(pair);
                        }
                        if (XMLHandler.parseXMLDataJudgeEnd(xmlData)) {
                            mHasQueryData = true;
//                            dismissBaseDialog();
                            if (mRecordList.size() != 0) {
                                if (mRecordList.size() == 1
                                        && mRecordList.get(0).first == 0
                                        && mRecordList.get(0).second == 0) {
                                    // Log.d("PML",
                                    // "QUERY_HISTORY_RECORD 0-------------0");
                                    mPlayVideoHandler
                                            .sendEmptyMessage(NO_RECORD_VIDEO_MSG_CALLBACK);
                                } else {
                                    sortTime(mRecordList);
                                    seekbar_history.setRecordList(mRecordList);
                                    // Log.d("PML",
                                    // "QUERY_HISTORY_RECORD sortTime(mRecordList)");
                                    mPlayVideoHandler
                                            .sendMessageDelayed(
                                                    mPlayVideoHandler
                                                            .obtainMessage(PLAY_BACK_VIDEO_ACTIVE_MSG),
                                                    200);
                                }
                            } else {
                                // Log.d("PML", "QUERY_HISTORY_RECORD else");
                                mPlayVideoHandler
                                        .sendEmptyMessage(NO_RECORD_VIDEO_MSG_CALLBACK);
                            }
                        }
                    }
                    break;
                case CONTROL_START_RECORD:
                    WLog.i("ReplayStep","---step1:开始进行历史记录数据返回");
                    mPlayVideoHandler.removeMessages(NO_SDCARD_MSG_CALLBACK);
                    EnableReplay();
                    try {
                        // Log.d("PML",
                        // "CONTROL_START_RECORD OK  xmlData IS:"+xmlData);
                        String status = XMLHandler.parseXMLDataGetStatus(xmlData);
                        String session = XMLHandler.parseXMLDataGetSessionID(xmlData);
                        if (status == null || session == null) {
                            // Log.d("PML",
                            // "CONTROL_START_RECORD OK  status == null || session == null");
                            InEnableReplay();
                            showMsg(R.string.Handled_Error);
                        } else {
                            if (status.equalsIgnoreCase("OK")) {
                                // Log.d("PML",
                                // "CONTROL_START_RECORD OK mSessionID is:"+session);
                                mSessionID = session;
                                connectOSS();
                                queryHistoryVideo();
                                mPlayVideoHandler.sendEmptyMessage(NOTIFY_HISTORY_RECORD_HEARTBEAT_MSG);
                            } else if (status.equalsIgnoreCase("404")) {
                                // Log.d("PML", "CONTROL_START_RECORD 404 500");
                                mPlayVideoHandler
                                        .removeMessages(NO_SDCARD_MSG_CALLBACK);
                                mPlayVideoHandler
                                        .sendEmptyMessage(NO_SDCARD_MSG_CALLBACK);
                            } else if (status.equalsIgnoreCase("-1")
                                    || status.equalsIgnoreCase("500")) {
                                InEnableReplay();
                                showMsg(R.string.Handled_Error);
                            } else if (status.equalsIgnoreCase("551")) {
                                // Log.d("PML", "CONTROL_START_RECORD 551");
                                mPlayVideoHandler
                                        .removeMessages(NO_RIGHT_TO_SEE_MSG_CALLBACK);
                                mPlayVideoHandler
                                        .sendEmptyMessage(NO_RIGHT_TO_SEE_MSG_CALLBACK);
                            } else {
                                // Log.d("PML", "CONTROL_START_RECORD else");
                                InEnableReplay();
                                showMsg(R.string.Handled_Error);
                            }
                        }
                    } catch (Exception e) {
                        // Log.d("PML", "Exception e");
                        mPlayVideoHandler.sendEmptyMessage(NO_SDCARD_MSG_CALLBACK);
                    }
                    break;
                case CONTROL_STOP_RECORD:
                    break;
                case CONTROL_HISTORY_RECORD_PROGRESS:
                    WLog.i("ReplayStep","---step4:控制历史记录数据返回"+xmlData);
                    String fileName = XMLHandler.parseXMLDataGetFilename(xmlData);
                    WLog.i("ReplayStep","---step4:fileName"+fileName.length());
                    if (!fileName.equalsIgnoreCase("OK")) {
                        if (fileName.equalsIgnoreCase("403")) {
                            InEnableReplay();
                            showMsg(R.string.AuthManager_No_Right);
                        } else if (fileName.equalsIgnoreCase("404")
                                || fileName.equalsIgnoreCase("403")
                                || fileName.equalsIgnoreCase("500")
                                || fileName.equalsIgnoreCase("1102")) {
                            InEnableReplay();
                            showMsg(R.string.Handled_Error);
                        } else if (fileName.equalsIgnoreCase("551")) {
                            mPlayVideoHandler
                                    .removeMessages(NO_RIGHT_TO_SEE_MSG_CALLBACK);
                            mPlayVideoHandler
                                    .sendEmptyMessage(NO_RIGHT_TO_SEE_MSG_CALLBACK);
                        } else if (fileName.length() > 10) {
                            GetObjectDataModel tempModel = OSSXMLHandler
                                    .getObjectData(xmlData, deviceId);
                            if (tempModel != null && tempModel.getFileSize() > 0) {
                                mNextObjectData = tempModel;
                                if (mIsRequestVideo) {
                                    WLog.i("ReplayStep","---step5:直接播放的信息");
                                    mPlayVideoHandler.sendMessage(mPlayVideoHandler
                                            .obtainMessage(PLAY_VIDEO_MSG));
                                    mIsRequestVideo = false;
                                }
                            } else {
                                InEnableReplay();
                                showMsg(R.string.Handled_Error);
                            }
                        }

                    }
                    break;
                case NOTIFY_HISTORY_RECORD_HEARTBEAT:
                    String status = XMLHandler.parseXMLDataGetStatus(xmlData);
                    // Log.d("PML",
                    // "parseXMLDataGetStatus NOTIFY_HISTORY_RECORD_HEARTBEAT status is:"+status);
                    if (!TextUtils.isEmpty(status)) {
                        if (status.equalsIgnoreCase("404")) {
                            mPlayVideoHandler
                                    .removeMessages(NO_SDCARD_MSG_CALLBACK);
                            mPlayVideoHandler
                                    .sendEmptyMessage(NO_SDCARD_MSG_CALLBACK);
                        }
                    }
                    break;
                default:
                    break;
            }
        } else {
            // Log.d("PML", "apiType wrong :apitype is:"+apiType.name());
            switch (apiType) {
                case QUERY_HISTORY_RECORD:
                    if (!mHasQueryData) {
                        mPlayVideoHandler
                                .removeMessages(REQUEST_TIMEOUT_MSG_CALLBACK);
                        mPlayVideoHandler
                                .sendEmptyMessage(REQUEST_TIMEOUT_MSG_CALLBACK);
                    }
                    break;
                case CONTROL_START_RECORD:
                    mPlayVideoHandler.removeMessages(REQUEST_TIMEOUT_MSG_CALLBACK);
                    mPlayVideoHandler
                            .sendEmptyMessage(REQUEST_TIMEOUT_MSG_CALLBACK);
                    break;
                case CONTROL_STOP_RECORD:
//                    dismissBaseDialog();
                    showMsg(R.string.Handled_Error);
                    break;
                case CONTROL_HISTORY_RECORD_PROGRESS:
//                    dismissBaseDialog();
                    showMsg(R.string.Handled_Error);
                    break;
                case NOTIFY_HISTORY_RECORD_HEARTBEAT:
                    break;
                default:
                    break;
            }
        }
    }

    public void sortTime(List<Pair<Integer, Integer>> needSortList) {
        Comparator<Pair<Integer, Integer>> itemComparator = new Comparator<Pair<Integer, Integer>>() {
            public int compare(Pair<Integer, Integer> left,
                               Pair<Integer, Integer> right) {
                return left.first > right.first ? 1 : -1;
            }
        };
        Collections.sort(needSortList, itemComparator);
    }
}
