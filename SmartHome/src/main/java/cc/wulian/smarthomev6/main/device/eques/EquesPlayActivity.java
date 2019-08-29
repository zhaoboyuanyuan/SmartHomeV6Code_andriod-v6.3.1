package cc.wulian.smarthomev6.main.device.eques;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import com.eques.icvss.api.ICVSSUserInstance;
import com.eques.icvss.core.module.user.BuddyType;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.UnlockDialogActivity;
import cc.wulian.smarthomev6.main.device.cateye.album.AlbumGridActivity;
import cc.wulian.smarthomev6.main.device.eques.bean.BatteryStatusBean;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesCallStatusBean;
import cc.wulian.smarthomev6.main.device.eques.bean.VideoPlayingBean;
import cc.wulian.smarthomev6.main.home.scene.SceneListDialogActivity;
import cc.wulian.smarthomev6.main.message.alarm.EquesAlarmActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceRelationBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceRelationListBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.LastFrameEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.DisplayUtil;
import cc.wulian.smarthomev6.support.utils.FileUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.VibratorUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * 作者: chao
 * 时间: 2017/6/6
 * 描述: 视频界面
 * 联系方式: 805901025@qq.com
 */

public class EquesPlayActivity extends BaseTitleActivity {
    private static final String PERMISSION_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    private static final int PERMISSION_REQUEST_CODE = 1;

    private FrameLayout layout_video_container;
    private FrameLayout main_container;
    private View layout_video_loading, layout_video_reload, layout_video_offline;
    private SurfaceView surfaceView;
    private Chronometer timer;

    private View btn_snapshot;
    private ImageView btn_sound_switch, btn_alarmlist, btn_visitor, btn_scene, iv_snapshot;
    private TextView tv_hold_speek;
    private View btn_hold_speek;
    private ImageView iv_hold_speek;
    private ImageView btn_lock;
    private ImageView ivRight;
    private ImageView iv_battery;
    private WLDialog.Builder builder;
    private WLDialog dialog;

    private Bitmap saveLastBitmap;
    private boolean isOnline = false;
    private boolean isSnap = false;
    private String uid;
    private String deviceId;
    private String callId;
    private boolean isRadioOpen = true;
    private int currVolume;
    private int current;
    private int snapshot_sound_id;
    private DeviceApiUnit deviceApiUnit;

    private SoundPool soundPool;
    //    private AudioManager audioManager;
    private SnapshotObserver fileObserver;
    private Handler handler = new Handler(Looper.getMainLooper());
    private ICVSSUserInstance icvss;
    private Device device;

    private int videoPlayState;

    public static void start(Context context, Device bean) {
        boolean isOnline = bean.isOnLine();
        String deviceId = bean.devID;
        String uid = null;
        try {
            JSONObject jsonObject = new JSONObject(bean.data);
            uid = jsonObject.optString("uid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(uid)) {
            uid = bean.devID;
        }
        Intent intent = new Intent(context, EquesPlayActivity.class);
        intent.putExtra("isOnline", isOnline);
        intent.putExtra("deviceId", deviceId);
        intent.putExtra("uid", uid);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceId = getIntent().getStringExtra("deviceId");
        device = MainApplication.getApplication().getDeviceCache().get(deviceId);
        setContentView(R.layout.activity_eques_play, true);
        EventBus.getDefault().register(this);
        icvss = MainApplication.getApplication().getEquesApiUnit().getIcvss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isSnap = false;
        EventBus.getDefault().unregister(this);
        hangUpCall();
        timer.stop();

//        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
//        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current, 0);

//        closeSpeaker();

        if (null != fileObserver) {
            fileObserver.stopWatching();
        }
        if (soundPool != null) {
            soundPool.release();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (TextUtils.isEmpty(device.name)) {
            setToolBarTitleAndRightImg(getString(R.string.Cateyemini_Adddevice_Cateyemini), R.drawable.icon_cateye_setting);
        } else {
            setToolBarTitleAndRightImg(DeviceInfoDictionary.getNameByDevice(device), R.drawable.icon_cateye_setting);
        }

        if (isOnline && callId != null) {
            callId = icvss.equesOpenCall(uid, surfaceView.getHolder().getSurface());
            timer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        hangUpCall();
//        setRadioOpen(false);
    }

    @Override
    public void onBackPressed() {
        hangUpCall();
        super.onBackPressed();
    }

    @Override
    protected void initView() {
        btn_lock = (ImageView) findViewById(R.id.btn_lock);
        btn_scene = (ImageView) findViewById(R.id.btn_scene);
        main_container = (FrameLayout) findViewById(R.id.main_container);
        layout_video_container = (FrameLayout) findViewById(R.id.layout_video_container);
        layout_video_loading = findViewById(R.id.layout_video_loading);
        layout_video_reload = findViewById(R.id.layout_video_reload);
        layout_video_offline = findViewById(R.id.layout_video_offline);
        surfaceView = (SurfaceView) findViewById(R.id.sv_play);
        timer = (Chronometer) findViewById(R.id.chronometer_timer);
        iv_battery = (ImageView) findViewById(R.id.iv_battery);

        tv_hold_speek = (TextView) findViewById(R.id.tv_hold_speek);
        btn_snapshot = findViewById(R.id.btn_snapshot);
        iv_snapshot = (ImageView) findViewById(R.id.iv_snapshot);
        btn_sound_switch = (ImageView) findViewById(R.id.btn_sound_switch);
        btn_alarmlist = (ImageView) findViewById(R.id.btn_alarmlist);
        btn_visitor = (ImageView) findViewById(R.id.btn_visitor);
        btn_hold_speek = findViewById(R.id.btn_hold_speek);
        iv_hold_speek = (ImageView) findViewById(R.id.iv_hold_speek);
    }

    @Override
    protected void initData() {
        isOnline = getIntent().getBooleanExtra("isOnline", false);
        uid = getIntent().getStringExtra("uid");
        deviceApiUnit = new DeviceApiUnit(this);
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        snapshot_sound_id = soundPool.load(this, R.raw.snapshot, 1);

//        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current, 0);
//        currVolume = current;

//        boolean bo = audioManager.isWiredHeadsetOn();
//        if(!bo){
//            openSpeaker();
//        }
        checkPermission();
        if (isOnline) {
            updateLoadingState(0);
        } else {
            updateLoadingState(3);
        }

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (isOnline) {
                    callId = MainApplication.getApplication().getEquesApiUnit().getIcvss().equesOpenCall(uid, holder.getSurface());
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

        setVideoSize();
        setPower();
//        setAudioMute();

        creatSnapshotFile();
        showSnapshot();
        showLastSnapshot();
    }

    @Override
    protected void initListeners() {
        btn_lock.setOnClickListener(this);
        btn_scene.setOnClickListener(this);
        btn_snapshot.setOnClickListener(this);
        iv_snapshot.setOnClickListener(this);
        btn_sound_switch.setOnClickListener(this);
        btn_alarmlist.setOnClickListener(this);
        btn_visitor.setOnClickListener(this);
        layout_video_reload.setOnClickListener(this);
        iv_hold_speek.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (videoPlayState == 2) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            iv_hold_speek.setImageResource(R.drawable.icon_hold_speek_on);
                            tv_hold_speek.setText(R.string.Cateye_In_Call);
                            btn_sound_switch.setImageResource(R.drawable.icon_cateye_sound_off);
                            callSpeakerSetting(true);
                            VibratorUtil.holdSpeakVibration();
                            break;

                        case MotionEvent.ACTION_UP:
                            iv_hold_speek.setImageResource(R.drawable.icon_hold_speek);
                            tv_hold_speek.setText(R.string.CateEye_Detail_Hold_Speek);
                            if (isRadioOpen) {
                                btn_sound_switch.setImageResource(R.drawable.icon_cateye_sound_off);
                            } else {
                                btn_sound_switch.setImageResource(R.drawable.icon_cateye_sound_on);
                            }
                            callSpeakerSetting(false);
                            break;
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.btn_snapshot:
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
                break;
            case R.id.iv_snapshot:
                Intent intent = new Intent(this, AlbumGridActivity.class);
                intent.putExtra("devId", deviceId);
                startActivity(intent);
                break;
            case R.id.btn_sound_switch:
                if (videoPlayState == 2) {
                    if (callId != null) {
                        isRadioOpen = !isRadioOpen;
                        if (isRadioOpen) {
                            btn_sound_switch.setImageResource(R.drawable.icon_cateye_sound_off);
                        } else {
                            btn_sound_switch.setImageResource(R.drawable.icon_cateye_sound_on);
                        }
                        setAudioMute();//设置静音
                    }
                }
                break;
            case R.id.btn_alarmlist:
                EquesAlarmActivity.start(this, deviceId);
                break;
            case R.id.btn_visitor:
                EquesVisitorActivity.start(this, deviceId);
                break;
            case R.id.img_right:
                Intent settingIntent = new Intent(this, EquesSettingActivity.class);
                settingIntent.putExtra("uid", uid);
                settingIntent.putExtra("deviceId", deviceId);
                startActivityForResult(settingIntent, 0);
                break;
            case R.id.layout_video_reload:
                updateLoadingState(0);
                if (isOnline) {
                    callId = icvss.equesOpenCall(uid, surfaceView.getHolder().getSurface());
                }
                break;
            case R.id.btn_lock:
                getLockData();
                break;
            case R.id.btn_scene:
                startActivity(new Intent(this, SceneListDialogActivity.class));
                break;
            default:
                break;
        }
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

    private void setVideoSize() {
        int verticalHeight;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        verticalHeight = (dm.widthPixels * 3) / 4;
        surfaceView.getHolder().setFixedSize(dm.widthPixels, verticalHeight);
    }

    private void hangUpCall() {
        if (callId != null) {
            icvss.equesCloseCall(callId);
        }
    }

    /**
     * 设置loadingview 状态
     *
     * @param state 0 loading，1 断开，2 播放，3 离线
     */
    private void updateLoadingState(int state) {
        if (state == 0) {
            videoPlayState = 0;
            layout_video_reload.setVisibility(View.GONE);
            layout_video_loading.setVisibility(VISIBLE);
            layout_video_offline.setVisibility(View.GONE);
        } else if (state == 1) {
            videoPlayState = 1;
            layout_video_reload.setVisibility(VISIBLE);
            layout_video_loading.setVisibility(View.GONE);
            layout_video_offline.setVisibility(View.GONE);
        } else if (state == 2) {
            videoPlayState = 2;
            layout_video_reload.setVisibility(View.GONE);
            layout_video_loading.setVisibility(View.GONE);
            layout_video_offline.setVisibility(View.GONE);
        } else {
            videoPlayState = 3;
            layout_video_reload.setVisibility(View.GONE);
            layout_video_loading.setVisibility(View.GONE);
            layout_video_offline.setVisibility(VISIBLE);
        }
    }

    private void setAudioMute() {
        if (isRadioOpen) {
            if (callId != null) {
                icvss.equesAudioPlayEnable(false, callId);
                icvss.equesAudioRecordEnable(false, callId);
            }
        } else {
            callSpeakerSetting(false);
        }
    }

    private void callSpeakerSetting(boolean f) {
        if (f) {
            if (callId != null) {
                icvss.equesAudioRecordEnable(true, callId);
                icvss.equesAudioPlayEnable(false, callId);
            }
        } else {
            if (callId != null) {
                icvss.equesAudioRecordEnable(false, callId);
                if (isRadioOpen) {
                    icvss.equesAudioPlayEnable(false, callId);
                } else {
                    icvss.equesAudioPlayEnable(true, callId);
                }
            }
        }
    }

    private void snapshot() {
        if (isOnline && callId != null) {
            isSnap = true;
            String savePath = FileUtil.getSnapshotPath() + "/" + deviceId;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmsss");
            String time = simpleDateFormat.format(System.currentTimeMillis());
            String fileName = time + ".jpg";
            icvss.equesSnapCapture(BuddyType.TYPE_WIFI_DOOR_R22, savePath + "/" + fileName);
            soundPool.play(snapshot_sound_id, 1.0f, 1.0f, 0, 0, 1);
        }
    }

    private void saveLastFrame() {
        if (isOnline && callId != null) {
            final String savePath = FileUtil.getLastFramePath();
            final String fileName = deviceId + ".jpg";
            icvss.equesSnapCapture(BuddyType.TYPE_WIFI_DOOR_R22, savePath + "/" + fileName);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    EventBus.getDefault().post(new LastFrameEvent(deviceId, savePath + "/" + fileName));
                }
            }, 2000);

            WLog.i(TAG, "saveLastFrame: ");
        }
    }

    /**
     * 视频背景显示上一次退出时的截图
     */
    private void showSnapshot() {
        String snapshotPath = FileUtil.getLastFramePath();
        String fileName = deviceId + ".jpg";
        String path = snapshotPath + "/" + fileName;
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                surfaceView.setBackground(new BitmapDrawable(getResources(), bitmap));
                return;
            }
        }
        surfaceView.setBackgroundResource(R.drawable.camera_default_bg1);
    }

    private void showLastSnapshot() {
        final String savePath = FileUtil.getSnapshotPath() + "/" + deviceId;
        File savePathFile = new File(savePath);
        String[] bmpFiles = savePathFile.list();
        if (bmpFiles != null && bmpFiles.length > 0) {
            final String bmpFile = bmpFiles[bmpFiles.length - 1];
            final Bitmap bitmap = BitmapFactory.decodeFile(savePath + "/" + bmpFile);
            if (isSnap) {
                DisplayUtil.snapAnimotion(this, main_container, surfaceView, iv_snapshot, bitmap, new DisplayUtil.onCompleteListener() {
                    @Override
                    public void onComplete() {
                        iv_snapshot.setImageBitmap(bitmap);
                    }
                });
            } else {
                iv_snapshot.setImageBitmap(bitmap);
                if (saveLastBitmap != null && !saveLastBitmap.isRecycled()) {
                    saveLastBitmap.recycle();
                }
                saveLastBitmap = bitmap;
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

    private void getLockData() {
        deviceApiUnit.doGetBindLock(deviceId, "", new DeviceApiUnit.DeviceApiCommonListener<DeviceRelationListBean>() {
            @Override
            public void onSuccess(DeviceRelationListBean bean) {
                UnlockDialogActivity.startByCondition(EquesPlayActivity.this, bean, false);
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(msg);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(VideoPlayingBean event) {
        updateLoadingState(2);
        surfaceView.setBackground(null);
        timer.setBase(SystemClock.elapsedRealtime());//计时器清零
        int hour = (int) ((SystemClock.elapsedRealtime() - timer.getBase()) / 1000 / 60);
        timer.setFormat("0" + String.valueOf(hour) + ":%s");
        timer.start();
        setAudioMute();
        saveLastFrame();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && TextUtils.equals(event.device.devID, deviceId)) {
            if (!event.device.isOnLine()) {
                updateLoadingState(3);
            } else if (event.type == DeviceReportEvent.DEVICE_DELETE) {
                finish();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoStatus(EquesCallStatusBean event) {
        if (TextUtils.equals(event.from, deviceId)) {
            if (TextUtils.equals(event.state, "close")) {
//                finish();
//                updateLoadingState(1);
            } else if (TextUtils.equals(event.state, "try")) {
                updateLoadingState(0);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            finish();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBatteryStatusBean(BatteryStatusBean bean) {
        setPower();
    }

    private void setPower() {
        if (device.isOnLine()) {
            iv_battery.setVisibility(VISIBLE);
        } else {
            iv_battery.setVisibility(INVISIBLE);
            return;
        }
        try {
            JSONObject json = new JSONObject(device.data);
            JSONObject equesInfoBeanJson = json.optJSONObject("equesInfoBean");

            if (equesInfoBeanJson == null) {
                return;
            }
            String levStr = equesInfoBeanJson.optString("battery_level");
            try {
                int lev = Integer.parseInt(levStr);
                if (lev > 80) {
                    iv_battery.setImageResource(R.drawable.icon_eques_power_100);
                } else if (lev > 60) {
                    iv_battery.setImageResource(R.drawable.icon_eques_power_80);
                } else if (lev > 40) {
                    iv_battery.setImageResource(R.drawable.icon_eques_power_60);
                } else if (lev > 20) {
                    iv_battery.setImageResource(R.drawable.icon_eques_power_40);
                } else {
                    iv_battery.setImageResource(R.drawable.icon_eques_power_20);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
