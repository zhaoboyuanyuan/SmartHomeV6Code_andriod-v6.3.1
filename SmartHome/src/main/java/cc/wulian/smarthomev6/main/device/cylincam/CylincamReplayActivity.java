package cc.wulian.smarthomev6.main.device.cylincam;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.MediaCodecMonitor;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.VideoTimePeriod;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.cateye.album.AlbumGridActivity;
import cc.wulian.smarthomev6.main.device.cylincam.server.IotSendOrder;
import cc.wulian.smarthomev6.main.device.cylincam.server.helper.CameraHelper;
import cc.wulian.smarthomev6.main.device.cylincam.utils.DateUtil;
import cc.wulian.smarthomev6.main.device.cylincam.utils.IotUtil;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.customview.CylincamHistorySeekBar;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.utils.Config;
import cc.wulian.smarthomev6.support.utils.DisplayUtil;
import cc.wulian.smarthomev6.support.utils.FileUtil;
import cc.wulian.smarthomev6.support.utils.SizeUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

import static cc.wulian.smarthomev6.main.device.cylincam.CylincamPlayActivity.cameaHelper;

/**
 * created by huxc  on 2017/9/20.
 * func： 小物摄像机回看
 * email: hxc242313@qq.com
 */

public class CylincamReplayActivity extends BaseTitleActivity implements SurfaceHolder.Callback, View.OnClickListener {
    private final static int PLAY_BACK_VIDEO_MSG = 1;// 所选时间段处理信息
    private final static int PLAY_BACK_VIDEO_ACTIVE_MSG = 2;// 第一次默认视频处理信息

    private static final int RECEIVE_FILE_SUCCESS = 10;
    private final static int FILE_OK = 12;
    private final static int NO_SDCARD_MSG_CALLBACK = 20;
    private final static int NO_RECORD_VIDEO_MSG_CALLBACK = 21;
    private final static int NO_RIGHT_TO_SEE_MSG_CALLBACK = 22;
    private final static int REMOVE_PLAY_VIDEO_MSG = 100;
    private final static int NOTIFY_HISTORY_RECORD_HEARTBEAT_MSG = 200;

    //文件列表
    private List<String> fileList = Collections.synchronizedList(new ArrayList());
    private List<VideoTimePeriod> mTimeList = new ArrayList<>();
    private List<Pair<Integer, Integer>> mRecordList;
    private REPLAY_VIDEO_STATUS mReplayVideoStatus;

    private PlayServerVideo playServerVideo;
    private StopServerVideo stopServerVideo;

    private MediaCodecMonitor mediaCodecVideoMonitor = null;
    private CylincamHistorySeekBar replay_historyseek;
    private View album;
    private View snapshot;
    private RelativeLayout layout_video_container;
    //横竖屏切换相关view
    private View layout_portrait, layout_portrait_bottom, layout_landscape;
    private View btn_snapshot_landscape, btn_fullscreen_landscape;
    private View btn_replay_back, btn_snapshot, btn_fullscreen;
    private ImageView iv_snapshot;
    private FrameLayout main_container;

    private String CameraUserName;
    private String CameraPassword;
    private String deviceId;
    private long mPlayProgressTimeStamp;// 当前选择播放进度显示时间戳
    private int mSelectedHistoryChannel = -1;
    private int currentTotalFile;
    private boolean PLAY = true;// 判断是否播放
    private int minWidth, maxWidth;
    private int widthRatio = 16, heightRatio = 9;
    private int snapshot_sound_id;
    private boolean isSnap = false;
    private SoundPool soundPool;
    private SnapshotObserver fileObserver;
    private byte[] startTimeAck;
    private byte[] endTime;

    private enum REPLAY_VIDEO_STATUS {
        INIT, SEEKBAR_PROGRESS, GET_VIDEO, PLAY_VIDEO, DESTROY
    }

    private CameraHelper.Observer observer = new CameraHelper.Observer() {
        @Override
        public void avIOCtrlOnLine() {
        }

        @Override
        public void avIOCtrlDataSource(final byte[] data, final int avIOCtrlMsgType) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (avIOCtrlMsgType) {
                        case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SEARCH_PLAYBACK_FILE_ACK:
                            Log.i("hxc", "length = " + data.length + "");
                            if (data.length > 8) {
                                parsePlayBackFileInfo(data);
                            }

                            break;
                        case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_PLAYBACK_STREAM_ACK:// 接收视频数据
                            if (data.length > 0) {
//                                release();// 如果本地视频在播放需要清空
                                if ((mSelectedHistoryChannel = data[0]) > 0) {
                                    if (PLAY) {
                                        startPaly();
                                    }
                                    mediaCodecVideoMonitor.setSurfaceReady();
                                }
                                mediaCodecVideoMonitor.cleanFrameQueue();// 清除播放缓冲队列
                            }
                            break;
                        case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_PLAYBACK_OVER_ACK:// 停止回放
                            break;

                    }
                }
            });
        }

        @Override
        public void avIOCtrlMsg(int resCode, String method) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cylincam_history, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cameaHelper != null) {
            cameaHelper.attach(observer);
        }
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(R.string.Cylincam);
    }

    @Override
    protected void initView() {
        super.initView();
        mediaCodecVideoMonitor = new MediaCodecMonitor(this);
//        mediaCodecVideoMonitor = (MediaCodecMonitor) findViewById(R.id.sv);
        replay_historyseek = (CylincamHistorySeekBar) findViewById(R.id.seekbar_history);
        layout_video_container = (RelativeLayout) findViewById(R.id.layout_video_container);
        btn_replay_back = findViewById(R.id.btn_replay_back);
        main_container = (FrameLayout) findViewById(R.id.main_container);
        layout_portrait = findViewById(R.id.layout_portrait);
        layout_portrait_bottom = findViewById(R.id.layout_portrait_bottom);
        layout_landscape = findViewById(R.id.layout_landscape);
        btn_fullscreen = findViewById(R.id.btn_fullscreen);
        btn_fullscreen_landscape = findViewById(R.id.btn_fullscreen_landscape);
        btn_snapshot = findViewById(R.id.btn_snapshot);
        iv_snapshot = (ImageView) findViewById(R.id.iv_snapshot);
        btn_snapshot_landscape = findViewById(R.id.btn_snapshot_landscape);


        DisplayMetrics displayMetrics = SizeUtil.getScreenSize(getApplicationContext());
        int deviceHeight = displayMetrics.heightPixels;
        int cameraPreviewHeight = deviceHeight * 7 / 10;// 根据布局中的上下比例
        int cameraPreviewWidth = (int) ((float) cameraPreviewHeight
                / heightRatio * widthRatio);
        minWidth = displayMetrics.widthPixels;
        maxWidth = cameraPreviewWidth;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(cameraPreviewWidth, cameraPreviewHeight);
        layoutParams.gravity = Gravity.CENTER;
        layout_video_container.addView(mediaCodecVideoMonitor, 0, layoutParams);

        addSurfaseCallBack();
    }

    @Override
    protected void initData() {
        super.initData();
        CameraUserName = "admin";
        CameraPassword = getIntent().getStringExtra("tutkPwd");
        deviceId = getIntent().getStringExtra("deviceId");
        Device device = MainApplication.getApplication().getDeviceCache().get(deviceId);
        setTitleText(DeviceInfoDictionary.getNameByTypeAndName(device.type, device.name));
        mReplayVideoStatus = REPLAY_VIDEO_STATUS.INIT;
        mRecordList = new ArrayList<Pair<Integer, Integer>>();

        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        snapshot_sound_id = soundPool.load(this, R.raw.snapshot, 1);


        long initTime = System.currentTimeMillis() / 1000 - 60 * 60;
        initTime = (long) (60 * Math.round(initTime / (float) 60));
        mPlayProgressTimeStamp = initTime;
        Log.d(TAG, "init mPlayProgressTimeStamp is:" + mPlayProgressTimeStamp);
        replay_historyseek.setMidTimeStamp(initTime);
        //开启线程开始获取  数据应该。
        ProgressDialogManager.getDialogManager().showDialog("replay", this, null, null, null, 30000);
        byte[] temp = new byte[]{0x1f, 0x00, 0x00, 0x00};
        startTimeAck = new byte[]{18, 1, 1, 0, 0, 0, 0, 0};
        endTime = new byte[]{38, 12, 31, 0, 0, 0, 0, 0};
        IotSendOrder.sendIoctrlGetPlayBackFile(cameaHelper.getmCamera(), temp, startTimeAck, endTime);
        showLastSnapshot();
        creatSnapshotFile();
    }


    @Override
    protected void initListeners() {
        super.initListeners();
        btn_fullscreen.setOnClickListener(this);
        btn_fullscreen_landscape.setOnClickListener(this);
        btn_snapshot.setOnClickListener(this);
        iv_snapshot.setOnClickListener(this);
        btn_replay_back.setOnClickListener(this);
        btn_snapshot_landscape.setOnClickListener(this);

        replay_historyseek
                .setHistroySeekChangeListener(new CylincamHistorySeekBar.HistroySeekChangeListener() {
                    @Override
                    public void onChangeSeekBarTempAction(long timeStamp) {

                        mReplayVideoStatus = REPLAY_VIDEO_STATUS.SEEKBAR_PROGRESS;
                        handler.removeMessages(REMOVE_PLAY_VIDEO_MSG);
                        Log.i(TAG, "----------xxxxxxxx-----11");
                        //进度条有移动的返回值。
//                        showDate(timeStamp);

                    }

                    @Override
                    public void onChangeSeekBarFinalAction(long timeStamp,
                                                           boolean isRecord, int num) {

//                        cameaHelper.cleanVideoFrameQuery(mSelectedHistoryChannel); //add hhy 清理缓存

                        //可能是结束时间，要认真理解。
                        Log.d("PML", "--------------onChangeSeekBarFinalAction is:"
                                + timeStamp + ";isRecord is:" + isRecord);

                        //处理播放前要做的动作例如关闭声音
                        if (isRecord) {
//                            forplayvideo();
                        }
                        Message msg = handler.obtainMessage(PLAY_BACK_VIDEO_MSG);
                        msg.arg1 = isRecord ? (int) timeStamp : -1;
                        msg.arg2 = num;
                        handler.sendMessageDelayed(msg, 500);

                    }

                    @Override
                    public void onActionDownMessage() {
                        handler.removeMessages(PLAY_BACK_VIDEO_MSG);
                    }
                });


    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_replay_back:
                finish();
                break;
            case R.id.btn_fullscreen:
                performFullscreen();
                break;
            case R.id.btn_fullscreen_landscape:
                exitFullscreen();
                break;
            case R.id.btn_snapshot:
            case R.id.btn_snapshot_landscape:
                snapshot();
                break;
            case R.id.iv_snapshot:
                Intent intent = new Intent(this, AlbumGridActivity.class);
                intent.putExtra("devId", deviceId);
                startActivity(intent);
                break;

        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


    // add syf
    private void addSurfaseCallBack() {
        mediaCodecVideoMonitor.getHolder().addCallback(this);
    }

    private final Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            int avChannel = bundle.getInt("avChannel");
            byte[] data = bundle.getByteArray("data");

            switch (msg.what) {
                case RECEIVE_FILE_SUCCESS:
                    ProgressDialogManager.getDialogManager().dimissDialog("replay", 0);
                    //开始处理列表中的信息文件信息。
                    mTimeList = getHistoryRecordList(fileList);

                    for (VideoTimePeriod videoTimePeriod : mTimeList) {
                        Pair<Integer, Integer> pair = new Pair<Integer, Integer>(
                                (int) videoTimePeriod.getTimeStamp(),
                                (int) videoTimePeriod.getEndTimeStamp());
                        mRecordList.add(pair);
                    }

                    sortTime(mRecordList);
                    replay_historyseek.setRecordList(mRecordList);
                    replay_historyseek.setMidTimeStamp(mRecordList.get(mRecordList.size() - 1).first);
//                    Message msg2 = handler.obtainMessage(PLAY_BACK_VIDEO_MSG);
//                    msg2.arg2 = mRecordList.get(mRecordList.size()-1).first;
//                    handler.sendMessageDelayed(msg2, 500);
                    Log.i(TAG, "-------------数据发送成功");
                    break;
                case REMOVE_PLAY_VIDEO_MSG:

                    Log.i(TAG, "-------------REMOVE_PLAY_VIDEO_MSG");

                    break;
                case PLAY_BACK_VIDEO_MSG:
                    long time = msg.arg1;
                    if (time >= 0) {
                        mReplayVideoStatus = REPLAY_VIDEO_STATUS.GET_VIDEO;
                        // String timeStamp = DateUtil.timeStamp2Date(Long.toString(time), "yyyy-MM-ddHH:mm:ss");
                        String filaName = mTimeList.get(msg.arg2).getFileName();
                        byte[] startTime = new byte[8];
                        startTime[0] = Integer.valueOf(filaName.substring(2, 4)).byteValue();
                        startTime[1] = Integer.valueOf(filaName.substring(4, 6)).byteValue();
                        startTime[2] = Integer.valueOf(filaName.substring(6, 8)).byteValue();
                        startTime[3] = 0;
                        startTime[4] = Integer.valueOf(filaName.substring(9, 11)).byteValue();
                        startTime[5] = Integer.valueOf(filaName.substring(11, 13)).byteValue();
                        startTime[6] = Integer.valueOf(filaName.substring(13, 15)).byteValue();
                        startTime[7] = 0;
                        Log.i("IOTCamera", "---------------" + filaName + "---------");
                        IotSendOrder.sendIoctrlSetPlayBackFileNowReq(cameaHelper.getmCamera(), startTime, filaName);
                    } else {
                        //这里没有视频。
                        ToastUtil.show(getString(R.string.Time_No_Video));
                    }
                    break;
                case FILE_OK:
                    break;
                case NO_SDCARD_MSG_CALLBACK:
                    InEnableReplay();
                    break;
                case NO_RIGHT_TO_SEE_MSG_CALLBACK:
                    InEnableReplay();
                    break;
                case NO_RECORD_VIDEO_MSG_CALLBACK:
                    InEnableReplay();
                    ToastUtil.show("sd卡中无录像");
                    break;
                case NOTIFY_HISTORY_RECORD_HEARTBEAT_MSG:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void parsePlayBackFileInfo(byte[] data) {
        int total_num;        //文件总数
        int cur_num;            //当前文件数

        total_num = DateUtil.bytesToInt(data, 0);
        Log.i("IOTCamera", "------------total_num" + total_num);
        if (total_num == 0) {
            handler.sendEmptyMessage(NO_RECORD_VIDEO_MSG_CALLBACK);

        }
        cur_num = DateUtil.bytesToInt(data, 4);
        Log.i("IOTCamera", "------------cur_num" + cur_num);
        //获取设备FileName
        //
        byte[] ipFileName = new byte[36];
        for (int i = 0; i < cur_num; i++) {
            //VideotapeInfo vInfo =new VideotapeInfo();
            System.arraycopy(data, 8 + 36 * i, ipFileName, 0, 33);
            String fileName = DateUtil.bytesToStying(ipFileName);
            Log.i("IOTCamera", "----------------fileName" + fileName);
            fileList.add(fileName);
            //vInfo.setFileName(fileName);
            //videos.add(vInfo);
        }
        currentTotalFile += cur_num;
        if (total_num == currentTotalFile) {

            currentTotalFile = 0;
            handler.sendEmptyMessage(RECEIVE_FILE_SUCCESS);

        }
    }

    public static List<VideoTimePeriod> getHistoryRecordList(List<String> fileList) {
        List<VideoTimePeriod> recordList = new ArrayList<>();
        if (fileList.size() <= 0)
            return recordList;
        long lastTimeStamp = 0;
        long StartTime = 0;
        long EndTime = 0;
        for (String temp : fileList) {

            lastTimeStamp = StartTime;
            StartTime = DateUtil.date2TimeStamplong(temp.substring(0, 13), "yyyyMMdd-HHmm");

            EndTime = StartTime + 60;

            if (StartTime != lastTimeStamp) {

                if (StartTime > 0 && EndTime > 0) {

                    VideoTimePeriod vtp = new VideoTimePeriod();
                    if (StartTime >= EndTime) {
                        vtp.setTimeStamp(EndTime);
                        vtp.setEndTimeStamp(StartTime);
                    } else {
                        vtp.setTimeStamp(StartTime);
                        vtp.setEndTimeStamp(EndTime);
                    }
                    vtp.setFileName(temp);
                    recordList.add(vtp);

                }
            }
        }
        return recordList;

    }

    public void sortTime(List<Pair<Integer, Integer>> needSortList) {
        Comparator<Pair<Integer, Integer>> itemComparator = new Comparator<Pair<Integer, Integer>>() {
            public int compare(Pair<Integer, Integer> left,
                               Pair<Integer, Integer> right) {

                if (left.first > right.first)
                    return 1;
                else if (left.first < right.first)
                    return -1;
                else
                    return 0;
            }
        };

    }

    private void InEnableReplay() {
        replay_historyseek.setActionEnable(false);
        replay_historyseek.setRecordList(mRecordList);
        btn_fullscreen.setEnabled(false);
    }

    private static class SMsgAVIoctrl {
        /**
         * 停止历史录像
         */
        public static void sendIoctrlSetHistoryStop() {
            cameaHelper.getmCamera().sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                    AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_PLAYBACK_OVER_REQ,
                    AVIOCTRLDEFs.SMsgAVIoctrlSetHistoryStop.parseContent(0));
        }
    }

    /***************************
     * 线程操作
     ******************************/
    private class PlayServerVideo extends Thread {
        @Override
        public void run() {
            if (cameaHelper.getmCamera() != null) {
                cameaHelper.destroyVideoStream();
                cameaHelper.getmCamera().startPlayBack(mSelectedHistoryChannel, "admin",
                        CameraPassword);
                cameaHelper.getmCamera().startPlayBackShow(mSelectedHistoryChannel, true, false);
            }
            if (mediaCodecVideoMonitor != null) {
                mediaCodecVideoMonitor.attachCamera(cameaHelper.getmCamera(), 0);
            }
            PLAY = false;
        }
    }

    private class StopServerVideo extends Thread {
        @Override
        public void run() {
            if (mSelectedHistoryChannel >= 0) {
                if (cameaHelper.getmCamera() != null) {
                    cameaHelper.getmCamera().stopPlayBack(mSelectedHistoryChannel);
                    cameaHelper.getmCamera().stopPlayBackShow(mSelectedHistoryChannel);
                }
            }
            SMsgAVIoctrl.sendIoctrlSetHistoryStop();
            mSelectedHistoryChannel = -1;
        }
    }

    /***************************
     * 线程操作
     *******************************/

    private final void startPaly() {
        playServerVideo = new PlayServerVideo();
        playServerVideo.setPriority(Thread.MAX_PRIORITY);
        playServerVideo.start();
    }

    private final void stopPlay() {
        stopServerVideo = new StopServerVideo();
        stopServerVideo.setPriority(Thread.MAX_PRIORITY);
        stopServerVideo.start();
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
        mediaCodecVideoMonitor.setLayoutParams(layoutParams);
    }

    private void snapshot() {
        isSnap = true;
        String savePath = FileUtil.getSnapshotPath() + "/" + deviceId;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmsss");
        String time = simpleDateFormat.format(System.currentTimeMillis());
        String fileName = time + ".jpg";
        IotUtil.saveBitmapToJpeg(mediaCodecVideoMonitor.getBitmapSnap(), savePath, fileName);
        soundPool.play(snapshot_sound_id, 1.0f, 1.0f, 0, 0, 1);
    }

    private void showLastSnapshot() {
        final String savePath = FileUtil.getSnapshotPath() + "/" + deviceId;
        File savePathFile = new File(savePath);
        String[] bmpFiles = savePathFile.list();
        if (bmpFiles != null && bmpFiles.length > 0) {
            final String bmpFile = bmpFiles[bmpFiles.length - 1];
            final Bitmap bitmap = BitmapFactory.decodeFile(savePath + "/" + bmpFile);
            if (isSnap) {
                DisplayUtil.snapAnimotion(this, main_container, mediaCodecVideoMonitor, iv_snapshot, bitmap, new DisplayUtil.onCompleteListener() {
                    @Override
                    public void onComplete() {
                        iv_snapshot.setImageBitmap(bitmap);
                    }
                });
            } else {
                iv_snapshot.setImageBitmap(bitmap);
//                if (saveLastBitmap != null && !saveLastBitmap.isRecycled()) {
//                    saveLastBitmap.recycle();
//                }
//                saveLastBitmap = bitmap;
            }
        } else {
            iv_snapshot.setImageResource(R.drawable.eques_snapshot_default);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isSnap = false;
        stopPlay();
        cameaHelper.detach(observer);
//        release();
        if (mediaCodecVideoMonitor != null) {
            cameaHelper.destroyVideoCarrier(mediaCodecVideoMonitor);
        }
        if (soundPool != null) {
            soundPool.release();
        }
    }

    @Override
    public void onBackPressed() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            exitFullscreen();
        } else {
            super.onBackPressed();
        }
    }

    class SnapshotObserver extends FileObserver {

        public SnapshotObserver(String path, int mask) {
            super(path, mask);
        }

        @Override
        public void onEvent(int event, String path) {
            switch (event) {
                case FileObserver.CREATE:
                    handler.postDelayed(new Runnable() {
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
