package cc.wulian.smarthomev6.main.device.cylincam;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.MediaCodecMonitor;
import com.yuantuo.netsdk.TKCamHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.CylincamDefinitionBean;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.UnlockDialogActivity;
import cc.wulian.smarthomev6.main.device.cateye.album.AlbumGridActivity;
import cc.wulian.smarthomev6.main.device.cylincam.bean.IOTCDevChPojo;
import cc.wulian.smarthomev6.main.device.cylincam.server.IotSendOrder;
import cc.wulian.smarthomev6.main.device.cylincam.server.helper.CameraHelper;
import cc.wulian.smarthomev6.main.device.cylincam.setting.CylincamSettingActivity;
import cc.wulian.smarthomev6.main.device.cylincam.utils.IotUtil;
import cc.wulian.smarthomev6.main.device.lookever.LookeverDetailActivity;
import cc.wulian.smarthomev6.main.home.scene.SceneListDialogActivity;
import cc.wulian.smarthomev6.main.message.alarm.CylincamAlarmActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.ChildDeviceInfoBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceRelationListBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.ImageUrlBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.customview.AngleMeter;
import cc.wulian.smarthomev6.support.customview.CylincamDefinitionChoosePop;
import cc.wulian.smarthomev6.support.customview.CylincamYuntaiButton;
import cc.wulian.smarthomev6.support.customview.CylincamYuntaiButtonLandscape;
import cc.wulian.smarthomev6.support.customview.MarqueeText;
import cc.wulian.smarthomev6.support.customview.PinchLayout;
import cc.wulian.smarthomev6.support.customview.popupwindow.PrepositionPopupWindow;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.LastFrameEvent;
import cc.wulian.smarthomev6.support.tools.CameraGestureListener;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.Config;
import cc.wulian.smarthomev6.support.utils.DisplayUtil;
import cc.wulian.smarthomev6.support.utils.FileUtil;
import cc.wulian.smarthomev6.support.utils.KeyboardUtil;
import cc.wulian.smarthomev6.support.utils.LanguageUtil;
import cc.wulian.smarthomev6.support.utils.SizeUtil;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.VibratorUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by 上海滩小马哥 on 2017/9/19.
 */

public class CylincamPlayActivity extends BaseTitleActivity {

    private static final String PERMISSION_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int YUNTAI_CONTROL = 1;

    private static final String QUERY_SD = "QUERY_SD";
    private PinchLayout videoContainerLayout;
    private FrameLayout main_container;
    private LinearLayout linkLayout, refreshLayout, offlineLayout;
    private RelativeLayout layout_portrait_bottom;
    private MediaCodecMonitor mediaCodecMonitor = null;
    private MarqueeText tvEnvironment;
    private ImageView iv_sound, iv_replay, iv_alarmlist, iv_collect, btn_lock, btn_scene, iv_arrows, iv_album, iv_snapshot, iv_hold_speek;
    private TextView tv_hold_speek;
    private TextView tv_definition;
    private ImageView iv_fullscreen;
    private CylincamYuntaiButton yt_cylincam;
    private AngleMeter angleMeter;
    //横屏相关控件
    private FrameLayout landscapeLayout;
    private TextView tv_definition_landscape;
    private FrameLayout layoutBrightness;
    private Button btnIknown;
    private ImageView ivBrightness;
    private ImageView iv_sound_landscape, iv_snapshot_landscape, iv_hold_speek_landscape, iv_fullscreen_landscape;
    private CylincamYuntaiButtonLandscape yt_cylincam_landscape;

    private CylincamDefinitionChoosePop definitionChoosePop;

    private CylincamPlayActivity instance = null;
    public static CameraHelper cameaHelper = null;
    private GestureDetector gestureDetector;
    private SoundPool soundPool;
    private SnapshotObserver fileObserver;
    private CylincamYuntaiButton.Direction curDirection;
    private CylincamYuntaiButtonLandscape.Direction curDirectionLandscape;
    private WLDialog mDialog;
    private WLDialog deleteDialog;
    private Device device;
    private PrepositionPopupWindow prepositionPopupWindow;
    private String tutkUid;
    private String tutkPwd;
    private String deviceId;
    private String environmentInfo;
    private final int mAVChannel = 0;
    private int snapshot_sound_id;
    private int decodingWay = -1;
    private int definitionValue = 1;
    private int minWidth;
    private int widthRatio = 16, heightRatio = 9;
    private boolean isRadioOpen = false;
    private boolean isSpeak = false;
    private boolean isSnap = false;
    private boolean landscape = false;
    private boolean isShowExistSdToast = true;
    private boolean isShowEnvironment = true;
    private boolean canAdjustBrightness = false;

    private DataApiUnit dataApiUnit;

    private Runnable autoPullRunnable;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Handler ytHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case YUNTAI_CONTROL:
                    curDirection = (CylincamYuntaiButton.Direction) msg.obj;
                    switch (curDirection) {
                        case left:
                            startRotate(1);
                            break;
                        case right:
                            startRotate(2);
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    };

    Handler ytHandlerLandscape = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case YUNTAI_CONTROL:
                    curDirectionLandscape = (CylincamYuntaiButtonLandscape.Direction) msg.obj;
                    switch (curDirectionLandscape) {
                        case left:
                            startRotate(1);
                            break;
                        case right:
                            startRotate(2);
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public static void start(Context context, String deviceId) {
        Intent intent = new Intent(context, CylincamPlayActivity.class);
        intent.putExtra("deviceId", deviceId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        instance = this;
        deviceId = getIntent().getStringExtra("deviceId");
        TKCamHelper.init();
        setContentView(R.layout.activity_cylincam_play, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isShowExistSdToast = true;
        updateState(1);
        isShowEnvironment = Preference.getPreferences().getCylincamEnvironmentSet(deviceId);
        if (isShowEnvironment) {
            tvEnvironment.setVisibility(View.VISIBLE);
            DeviceApiUnit deviceApiUnit = new DeviceApiUnit(this);
            deviceApiUnit.doGetEnvironmentInfo(deviceId, new DeviceApiUnit.DeviceApiCommonListener<HashMap>() {
                @Override
                public void onSuccess(HashMap bean) {
                    String value_17_1 = (String) bean.get("17_1");
                    String value_17_2 = (String) bean.get("17_2");
                    String value_D5_1 = (String) bean.get("D5_1");
                    String value_D6_1 = (String) bean.get("D6_1");
                    String value_A0_1 = (String) bean.get("A0_1");
                    String value_D4_1 = (String) bean.get("D4_1");
                    environmentInfo = String.format(getString(R.string.Cylincam_Environmental_Data), StringUtil.isNullOrEmpty(value_17_1) ? "--" : value_17_1,
                            StringUtil.isNullOrEmpty(value_17_2) ? "--" : value_17_2,
                            StringUtil.isNullOrEmpty(value_D5_1) ? "--" : value_D5_1,
                            StringUtil.isNullOrEmpty(value_D6_1) ? "--" : value_D6_1,
                            StringUtil.isNullOrEmpty(value_A0_1) ? "--" : value_A0_1,
                            StringUtil.isNullOrEmpty(value_D4_1) ? "--" : value_D4_1);
                    tvEnvironment.setText(environmentInfo);
                }

                @Override
                public void onFail(int code, String msg) {

                }
            });
        } else {
            tvEnvironment.setVisibility(View.GONE);
        }
        startPlaySurfaceView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        WLog.i(TAG, "onStart: ");
//        startPlaySurfaceView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlaySurfaceView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isShowExistSdToast = false;
        autoCatchLastFrame();
    }

    @Override
    protected void onDestroy() {

        EventBus.getDefault().unregister(this);
        isSnap = false;
        if (cameaHelper != null) {
            cameaHelper.detach(iotcDevConnCallback);
            cameaHelper.detach(observer);
            cameaHelper.destroyVideoStream();
            cameaHelper.destroyCameraHelper();
            WLog.i("hxc", "destroyCameraHelper");
        }
        cameaHelper = null;

        if (null != fileObserver) {
            fileObserver.stopWatching();
        }
        if (soundPool != null) {
            soundPool.release();
        }
        uninitIOTSDK();
        super.onDestroy();
    }

    private void uninitIOTSDK() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                TKCamHelper.uninit();
                return null;
            }
        }.execute();
    }

    @Override
    public boolean enableSwipeBack() {
        return false;
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitleAndRightImg(getString(R.string.Cylincam), R.drawable.icon_cateye_setting);
    }

    @Override
    protected void initView() {
        layout_portrait_bottom = (RelativeLayout) findViewById(R.id.layout_portrait_bottom);
        main_container = (FrameLayout) findViewById(R.id.main_container);
        videoContainerLayout = (PinchLayout) findViewById(R.id.rl_video);
        linkLayout = (LinearLayout) findViewById(R.id.ll_linking_video);
        refreshLayout = (LinearLayout) findViewById(R.id.ll_linking_video_refresh);
        offlineLayout = (LinearLayout) findViewById(R.id.layout_video_offline);
//        mediaCodecMonitor = (MediaCodecMonitor) findViewById(R.id.monitor);
        tvEnvironment = (MarqueeText) findViewById(R.id.tv_environment);
        iv_sound = (ImageView) findViewById(R.id.iv_sound);
        iv_replay = (ImageView) findViewById(R.id.iv_replay);
        iv_alarmlist = (ImageView) findViewById(R.id.iv_alarmlist);
        iv_collect = (ImageView) findViewById(R.id.iv_collect);
        btn_lock = (ImageView) findViewById(R.id.btn_lock);
        btn_scene = (ImageView) findViewById(R.id.btn_scene);
        iv_arrows = (ImageView) findViewById(R.id.iv_arrows);
        iv_album = (ImageView) findViewById(R.id.iv_album);
        iv_snapshot = (ImageView) findViewById(R.id.iv_snapshot);
        iv_hold_speek = (ImageView) findViewById(R.id.iv_hold_speek);
        tv_hold_speek = (TextView) findViewById(R.id.tv_hold_speek);
        yt_cylincam = (CylincamYuntaiButton) findViewById(R.id.yt_cylincam);
        tv_definition = (TextView) findViewById(R.id.tv_definition);
        iv_fullscreen = (ImageView) findViewById(R.id.iv_fullscreen);
        tv_definition.setAlpha(0.5f);
        iv_fullscreen.setAlpha(0.5f);

        angleMeter = (AngleMeter) findViewById(R.id.anglemeter);
        angleMeter.setMaxAngle("100°");
        layoutBrightness = (FrameLayout) findViewById(R.id.layout_brightness_tips);
        ivBrightness = (ImageView) findViewById(R.id.iv_brightness_tip);
        btnIknown = (Button) findViewById(R.id.btn_i_known);

        landscapeLayout = (FrameLayout) findViewById(R.id.layout_landscape);
        tv_definition_landscape = (TextView) findViewById(R.id.tv_definition_landscape);
        iv_sound_landscape = (ImageView) findViewById(R.id.iv_sound_landscape);
        iv_snapshot_landscape = (ImageView) findViewById(R.id.iv_snapshot_landscape);
        iv_hold_speek_landscape = (ImageView) findViewById(R.id.iv_hold_speek_landscape);
        iv_fullscreen_landscape = (ImageView) findViewById(R.id.iv_fullscreen_landscape);
        yt_cylincam_landscape = (CylincamYuntaiButtonLandscape) findViewById(R.id.yt_cylincam_landscape);

        mediaCodecMonitor = new MediaCodecMonitor(this);
        DisplayMetrics displayMetrics = SizeUtil.getScreenSize(getApplicationContext());
//        int deviceHeight = displayMetrics.heightPixels;
//        int cameraPreviewHeight = deviceHeight * 2/3;// 根据布局中的上下比例
//        int cameraPreviewWidth = (int) ((float) cameraPreviewHeight
//                / heightRatio * widthRatio);
        int deviceWidth = displayMetrics.widthPixels;
        int cameraPreviewWidth = deviceWidth;// 根据布局中的上下比例
        int cameraPreviewHeight = (int) ((float) cameraPreviewWidth
                * heightRatio / widthRatio);
        minWidth = displayMetrics.widthPixels;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(cameraPreviewWidth, cameraPreviewHeight);
        layoutParams.gravity = Gravity.CENTER;
        videoContainerLayout.addView(mediaCodecMonitor, 0, layoutParams);
    }

    @Override
    protected void initData() {
        device = MainApplication.getApplication().getDeviceCache().get(deviceId);
        dataApiUnit = new DataApiUnit(this);
        if (device != null) {
            setTitleText(DeviceInfoDictionary.getNameByTypeAndName(device.type, device.name));
        }
        setDecodingWay(IotUtil.selectDecode());
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        snapshot_sound_id = soundPool.load(this, R.raw.snapshot, 1);


        autoPullRunnable = new Runnable() {
            @Override
            public void run() {
                pullDownAnimation();
            }
        };
        handler.postDelayed(autoPullRunnable, 1000);
        creatSnapshotFile();
        showSnapshot();
        showLastSnapshot();
        gestureDetector = new GestureDetector(this, new CameraGestureListener(this, new CameraGestureListener.MyGestureListener() {
            @Override
            public void OnBrightChanged(float brightness) {
                if (canAdjustBrightness) {
                    setBrightness(brightness);
                }
            }

            @Override
            public void onSingleTouchConfirmed() {
            }
        }));
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        iv_sound.setOnClickListener(this);
        iv_replay.setOnClickListener(this);
        iv_alarmlist.setOnClickListener(this);
        iv_collect.setOnClickListener(this);
        btn_lock.setOnClickListener(this);
        btn_scene.setOnClickListener(this);
        iv_arrows.setOnClickListener(this);
        iv_album.setOnClickListener(this);
        iv_snapshot.setOnClickListener(this);
        iv_hold_speek.setOnClickListener(this);
        refreshLayout.setOnClickListener(this);
        tv_definition.setOnClickListener(this);
        iv_fullscreen.setOnClickListener(this);
        yt_cylincam.setOnDirectionLisenter(new MyDirection());
        yt_cylincam_landscape.setOnDirectionLisenter(new MyDirectionLandscape());

        tv_definition_landscape.setOnClickListener(this);
        iv_sound_landscape.setOnClickListener(this);
        iv_snapshot_landscape.setOnClickListener(this);
        iv_hold_speek_landscape.setOnClickListener(this);
        iv_fullscreen_landscape.setOnClickListener(this);
        btnIknown.setOnClickListener(this);

        videoContainerLayout.setOnChildViewLocationChangedListener(new PinchLayout.OnChildViewLocationChangedListener() {
            @Override
            public void childViewMoveScaleX(float ratio) {
                angleMeter.refreshAngle(ratio);
            }

            @Override
            public void minSize(boolean isMinSize) {
                angleMeter.setVisibility(isMinSize ? View.GONE : View.VISIBLE);
            }
        });

        landscapeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);// 手势双击
                return true;// 自定义方向判断
            }
        });

    }

    /**
     * 横屏
     */
    private final void goLandscape() {
        boolean isFirstFullScreen = preference.getIsFrisCameraFullScreen();
        if (isFirstFullScreen) {
            preference.setIsFrisCameraFullScreen(false);
            if (!LanguageUtil.isChina()) {
                ivBrightness.setImageResource(R.drawable.icon_brightness_tip_en);
                btnIknown.setBackgroundResource(R.drawable.icon_i_know_en);
            } else {
                ivBrightness.setImageResource(R.drawable.icon_brightness_tip_cn);
                btnIknown.setBackgroundResource(R.drawable.icon_i_know_cn);
            }
            setLandscapeViewEnable(false);
            layoutBrightness.setVisibility(View.VISIBLE);
        } else {
            canAdjustBrightness = true;
            layoutBrightness.setVisibility(View.GONE);
        }
        videoContainerLayout.setScaleEnable(false);
        landscape = true;
        getmToolBarHelper().setToolBarVisible(false);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(params);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        landscapeLayout.setVisibility(View.VISIBLE);
//                content.setVisibility(View.GONE);
        layout_portrait_bottom.setVisibility(View.GONE);
        tv_definition.setVisibility(View.GONE);
        iv_fullscreen.setVisibility(View.GONE);
        angleMeter.setVisibility(View.GONE);

        if (definitionChoosePop != null && definitionChoosePop.isShowing()) {
            definitionChoosePop.dismiss();
        }

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 全屏

//        DisplayMetrics displayMetrics = SizeUtil.getScreenSize(getApplicationContext());
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels);
//        mediaCodecMonitor.setLayoutParams(layoutParams);
    }

    /**
     * 竖屏
     */
    private final void goPortrait() {
        videoContainerLayout.setScaleEnable(true);
        landscape = false;
        getmToolBarHelper().setToolBarVisible(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(params);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        landscapeLayout.setVisibility(View.GONE);
        layout_portrait_bottom.setVisibility(View.VISIBLE);
        tv_definition.setVisibility(View.VISIBLE);
        iv_fullscreen.setVisibility(View.VISIBLE);
//        angleMeter.setVisibility(View.VISIBLE);

        videoContainerLayout.setChildViewLocationCenter();

//        DisplayMetrics displayMetrics = SizeUtil.getScreenSize(getApplicationContext());
////        int deviceHeight = displayMetrics.heightPixels;
////        int cameraPreviewHeight = deviceHeight * 2 / 3;// 根据布局中的上下比例
////        int cameraPreviewWidth = (int) ((float) cameraPreviewHeight
////                / heightRatio * widthRatio);
//        int deviceWidth = displayMetrics.widthPixels;
//        int cameraPreviewWidth = deviceWidth;// 根据布局中的上下比例
//        int cameraPreviewHeight = (int) ((float) cameraPreviewWidth
//                * heightRatio / widthRatio);
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(cameraPreviewWidth, cameraPreviewHeight);
//        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
//        mediaCodecMonitor.setLayoutParams(layoutParams);

        if (isShowEnvironment) {
            tvEnvironment.setVisibility(View.VISIBLE);
            DeviceApiUnit deviceApiUnit = new DeviceApiUnit(this);
            deviceApiUnit.doGetEnvironmentInfo(deviceId, new DeviceApiUnit.DeviceApiCommonListener<HashMap>() {
                @Override
                public void onSuccess(HashMap bean) {
                    String value_17_1 = (String) bean.get("17_1");
                    String value_17_2 = (String) bean.get("17_2");
                    String value_D5_1 = (String) bean.get("D5_1");
                    String value_D6_1 = (String) bean.get("D6_1");
                    String value_A0_1 = (String) bean.get("A0_1");
                    String value_D4_1 = (String) bean.get("D4_1");
                    environmentInfo = String.format(getString(R.string.Cylincam_Environmental_Data), StringUtil.isNullOrEmpty(value_17_1) ? "--" : value_17_1,
                            StringUtil.isNullOrEmpty(value_17_2) ? "--" : value_17_2,
                            StringUtil.isNullOrEmpty(value_D5_1) ? "--" : value_D5_1,
                            StringUtil.isNullOrEmpty(value_D6_1) ? "--" : value_D6_1,
                            StringUtil.isNullOrEmpty(value_A0_1) ? "--" : value_A0_1,
                            StringUtil.isNullOrEmpty(value_D4_1) ? "--" : value_D4_1);
                    tvEnvironment.setText(environmentInfo);
                }

                @Override
                public void onFail(int code, String msg) {

                }
            });
        } else {
            tvEnvironment.setVisibility(View.GONE);
        }
    }

    private void setLandscapeViewEnable(boolean flag) {
        iv_snapshot_landscape.setEnabled(flag);
        iv_sound_landscape.setEnabled(flag);
        iv_hold_speek_landscape.setEnabled(flag);
        iv_fullscreen_landscape.setEnabled(flag);
        tv_definition_landscape.setEnabled(flag);
    }

    public void setBrightness(float brightness) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = lp.screenBrightness + brightness / 255.0f;
        if (lp.screenBrightness > 1) {
            lp.screenBrightness = 1;
        } else if (lp.screenBrightness < 0.2) {
            lp.screenBrightness = (float) 0.2;
        }
        getWindow().setAttributes(lp);
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

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                PERMISSION_RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{PERMISSION_RECORD_AUDIO},
                    PERMISSION_REQUEST_CODE);
        } else {
            setIsSpeaking(!isSpeak);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean hasPermission = true;
            if (grantResults != null) {
                for (int grantResult : grantResults) {
                    hasPermission = hasPermission & (grantResult == PackageManager.PERMISSION_GRANTED);
                }
            }
            if (hasPermission) {
                setIsSpeaking(!isSpeak);
            } else {
                // Permission Denied
                ToastUtil.show(R.string.Toast_Permission_Denied);
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void startPlaySurfaceView() {
        new DeviceApiUnit(this).doGetDeviceInfo(deviceId, new DeviceApiUnit.DeviceApiCommonListener<ChildDeviceInfoBean>() {
            @Override
            public void onSuccess(ChildDeviceInfoBean bean) {
                if (!TextUtils.isEmpty(bean.tutkidPwd)) {
                    tutkPwd = bean.tutkidPwd;
                    tutkUid = bean.tutkid;
                }
                if (!TextUtils.isEmpty(tutkPwd)) {
                    if (cameaHelper == null) {
                        cameaHelper = CameraHelper.getInstance(getIotcDevChPojo());
                        cameaHelper.attach(iotcDevConnCallback);
                        cameaHelper.attach(observer);
                        cameaHelper.registerstIOTCLiener();
                    }
                    cameaHelper.register();
                } else {
                    updateState(2);
                }
            }

            @Override
            public void onFail(int code, String msg) {
                updateState(2);
            }
        });
    }

    public void stopPlaySurfaceView() {
        destroyWailThread();
        if (cameaHelper != null) {
            if (cameaHelper.getmCamera() != null) {
                cameaHelper.getmCamera().stopListening(0);
                cameaHelper.destroyVideoCarrier(mediaCodecMonitor);
            }
        }
    }

    private void destroyWailThread() {
        if (createSessionWaitThread != null) {
            createSessionWaitThread.stopThread();
            createSessionWaitThread = null;
        }
        if (createAvChannelWaitThread != null) {
            createAvChannelWaitThread.stopThread();
            createAvChannelWaitThread = null;
        }
    }

    private IOTCDevChPojo getIotcDevChPojo() {
        return new IOTCDevChPojo(tutkUid, tutkPwd, Camera.IOTC_Connect_ByUID, "CAMERA");
    }

    @Override
    public void onClickView(View v) {
//        super.onClick(v);
        if (v.getId() == R.id.iv_sound || v.getId() == R.id.iv_sound_landscape) {
            setRadioOpen(!isRadioOpen);
        } else if (v.getId() == R.id.iv_hold_speek || v.getId() == R.id.iv_hold_speek_landscape) {
            checkPermission();
        } else if (v.getId() == R.id.tv_definition || v.getId() == R.id.tv_definition_landscape) {
            if (definitionChoosePop == null) {
                definitionChoosePop = new CylincamDefinitionChoosePop(this, new CylincamDefinitionChoosePop.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(CylincamDefinitionBean bean) {
                        definitionValue = bean.value;
                        tv_definition.setText(bean.name);
//                            btn_definition_landscape.setText(bean.name);
                        cameaHelper.getmCamera().sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                                AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_PREVIEW_MODE_REQ,
                                AVIOCTRLDEFs.SMsgAVIoctrlSetPreviewModeReq.parseContent(definitionValue));
                    }
                });
            }
            if (landscape) {
                definitionChoosePop.showUpRise(tv_definition_landscape, definitionValue);
            } else {
                definitionChoosePop.showUpRise(tv_definition, definitionValue);
            }
        } else if (v.getId() == R.id.iv_snapshot || v.getId() == R.id.iv_snapshot_landscape) {
            WLog.d(this.getClass().getSimpleName(), mediaCodecMonitor.getMeasuredWidth() + "/" + mediaCodecMonitor.getMeasuredHeight() + " ");
            snapshot();
            iv_snapshot.setEnabled(false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    iv_snapshot.setEnabled(true);
                }
            }, 1000);
        } else if (v.getId() == R.id.img_right) {
            Intent settingIntent = new Intent(this, CylincamSettingActivity.class);
            settingIntent.putExtra("tutkUid", tutkUid);
            settingIntent.putExtra("tutkPwd", tutkPwd);
            settingIntent.putExtra("deviceId", deviceId);
            startActivityForResult(settingIntent, 1);
        } else if (v.getId() == R.id.iv_replay) {
            IotSendOrder.findSdCodeStatus(cameaHelper.getmCamera());
            ProgressDialogManager.getDialogManager().showDialog(QUERY_SD, this, null, null, 10000);
        } else if (v.getId() == R.id.iv_alarmlist) {
            CylincamAlarmActivity.start(CylincamPlayActivity.this, deviceId);
        } else if (v.getId() == R.id.iv_collect) {
            if (prepositionPopupWindow == null) {
                prepositionPopupWindow = new PrepositionPopupWindow(this, deviceId);
                prepositionPopupWindow.setOnClickListener(listener);
            }
            prepositionPopupWindow.showParent(layout_portrait_bottom);
        } else if (v.getId() == R.id.iv_arrows) {
            if (yt_cylincam.getVisibility() == View.GONE) {
                handler.removeCallbacks(autoPullRunnable);
                pullDownAnimation();
            } else {
                pushupAnimation();
            }
        } else if (v.getId() == R.id.iv_album) {
            Intent intent = new Intent(this, AlbumGridActivity.class);
            intent.putExtra("devId", deviceId);
            startActivity(intent);
        } else if (v.getId() == R.id.ll_linking_video_refresh) {
            updateState(1);
            cameaHelper = null;
            startPlaySurfaceView();
        } else if (v.getId() == R.id.iv_fullscreen) {
            goLandscape();
        } else if (v.getId() == R.id.iv_fullscreen_landscape) {
            goPortrait();
        } else if (v == btnIknown) {
            canAdjustBrightness = true;
            setLandscapeViewEnable(true);
            layoutBrightness.setVisibility(View.GONE);
        } else if (v == btn_scene) {
            startActivity(new Intent(this, SceneListDialogActivity.class));
        }else if(v == btn_lock){
            getLockData();
        }
    }

    private void getLockData() {
        new DeviceApiUnit(this).doGetBindLock(deviceId, "", new DeviceApiUnit.DeviceApiCommonListener<DeviceRelationListBean>() {
            @Override
            public void onSuccess(DeviceRelationListBean bean) {
                UnlockDialogActivity.startByCondition(CylincamPlayActivity.this, bean, device.isShared);
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.singleCenter(msg);
            }
        });
    }

    public PrepositionPopupWindow.PrePositionListener listener = new PrepositionPopupWindow.PrePositionListener() {
        @Override
        public void roateToPosition(int position) {
            byte OperTyepFinal, positionFinal = -1;
            OperTyepFinal = (byte) AVIOCTRLDEFs.AVIOCTRL_PTZ_GOTO_POINT;
            positionFinal = (byte) (position);
            cameaHelper.getmCamera().sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                    AVIOCTRLDEFs.IOTYPE_USER_IPCAM_PTZ_COMMAND,
                    AVIOCTRLDEFs.SMsgAVIoctrlPtzCmd.parseContent(OperTyepFinal, (byte) 0x8, positionFinal, (byte) 0, (byte) 0, (byte) 0));
        }

        @Override
        public void addPosition(int position) {
            showSavePrePositionDialog(position);
        }

        @Override
        public void removePosition(int position, String name) {
            showDeletePresetDialog(position, name);
        }
    };

    private void showSavePrePositionDialog(final int position) {
        if (mDialog != null && mDialog.isShowing()) {
            return;
        }

        WLDialog.Builder builder = new WLDialog.Builder(this);
        builder.setTitle(this.getResources().getString(R.string.Save_Position))
                .setCancelOnTouchOutSide(false)
                .setEditTextHint(R.string.Position_Name)
                .setCancelable(false)
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, final String msg) {
                        File willFile = new File(FileUtil.getPrePositionPath() + "/" + deviceId + "/" + msg + ".jpg");
//                        if (willFile.exists()) {
//                            ToastUtil.single(R.string.Cylincam_Name_Repeated);
//                        }
                        preference.saveCylincamPrePosition(deviceId, position, msg);
                        boolean saveSuccess = IotUtil.saveBitmapToJpeg(mediaCodecMonitor.getBitmapSnap(), FileUtil.getPrePositionPath() + "/" + deviceId, msg + ".jpg");
                        if (saveSuccess) {
                            byte OperTyepFinal, positionFinal = -1;
                            OperTyepFinal = (byte) AVIOCTRLDEFs.AVIOCTRL_PTZ_SET_POINT;
                            positionFinal = (byte) (position);
                            cameaHelper.getmCamera().sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                                    AVIOCTRLDEFs.IOTYPE_USER_IPCAM_PTZ_COMMAND,
                                    AVIOCTRLDEFs.SMsgAVIoctrlPtzCmd.parseContent(OperTyepFinal, (byte) 0x8, positionFinal, (byte) 0, (byte) 0, (byte) 0));
                            KeyboardUtil.hideKeyboard(CylincamPlayActivity.this, view);
                            mDialog.dismiss();
                            dataApiUnit.doPostImage(willFile, new DataApiUnit.DataApiCommonListener<ImageUrlBean>() {
                                @Override
                                public void onSuccess(ImageUrlBean bean) {
                                    savePresetPosition(msg, position, bean.url);
                                }

                                @Override
                                public void onFail(int code, String msg) {
                                    ToastUtil.show(msg);
                                }
                            });
                        } else {
                            ToastUtil.show(R.string.xiaowu_set_Preset_position);
                        }

                    }

                    @Override
                    public void onClickNegative(View view) {
                        KeyboardUtil.hideKeyboard(CylincamPlayActivity.this, view);
                        mDialog.dismiss();
                    }
                });
        mDialog = builder.create();
        mDialog.show();
    }

    private void savePresetPosition(String msg, int position, String url) {
        dataApiUnit.doSaveCylincamPrePosition(deviceId, msg, "0" + position, url, new DataApiUnit.DataApiCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {
                prepositionPopupWindow.refresh();
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    private void showDeletePresetDialog(final int position, final String name) {
        if (deleteDialog != null && deleteDialog.isShowing()) {
            return;
        }

        WLDialog.Builder builder = new WLDialog.Builder(this);
        builder.setCancelOnTouchOutSide(false)
                .setCancelable(false)
                .setMessage(getString(R.string.delete_Preset_Position))
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, final String msg) {

                        File file = new File(FileUtil.getPrePositionPath() + "/" + deviceId);
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        IotUtil.delFilePassWay(FileUtil.getPrePositionPath() + "/" + deviceId, name + ".jpg");
                        preference.saveCylincamPrePosition(deviceId, position, null);

                        byte OperTyepFinal, positionFinal = -1;
                        OperTyepFinal = (byte) AVIOCTRLDEFs.AVIOCTRL_PTZ_CLEAR_POINT;
                        positionFinal = (byte) (position);
                        cameaHelper.getmCamera().sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                                AVIOCTRLDEFs.IOTYPE_USER_IPCAM_PTZ_COMMAND,
                                AVIOCTRLDEFs.SMsgAVIoctrlPtzCmd.parseContent(OperTyepFinal, (byte) 0x8, positionFinal, (byte) 0, (byte) 0, (byte) 0));

                        dataApiUnit.doDeleteCylincamPosition(deviceId, "0" + position, new DataApiUnit.DataApiCommonListener<Object>() {
                            @Override
                            public void onSuccess(Object bean) {
                                prepositionPopupWindow.refresh();
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                ToastUtil.show(msg);

                            }
                        });

                    }

                    @Override
                    public void onClickNegative(View view) {
                        deleteDialog.dismiss();
                    }
                });
        deleteDialog = builder.create();
        deleteDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            this.finish();
        }
    }

    private void setDecodingWay(int decodingWay) {
        this.decodingWay = decodingWay;
    }

    private int getDecodingWay() {
        return decodingWay;
    }

    /**
     * 0:播放 1：等待 2：重新加载 -1:不在线
     */
    private void updateState(int state) {
        if (state == 0) {
            linkLayout.setVisibility(View.GONE);
            refreshLayout.setVisibility(View.GONE);
            offlineLayout.setVisibility(View.GONE);
        } else if (state == 1) {
            linkLayout.setVisibility(View.VISIBLE);
            refreshLayout.setVisibility(View.GONE);
            offlineLayout.setVisibility(View.GONE);
        } else if (state == 2) {
            stopPlaySurfaceView();
            linkLayout.setVisibility(View.GONE);
            refreshLayout.setVisibility(View.VISIBLE);
            offlineLayout.setVisibility(View.GONE);
        } else if (state == -1) {
            linkLayout.setVisibility(View.GONE);
            refreshLayout.setVisibility(View.GONE);
            offlineLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setRadioOpen(boolean isOpen) {
        this.isRadioOpen = isOpen;
        if (isOpen) {
            iv_sound_landscape.setImageResource(R.drawable.btn_sound_fullscreen_on);
            iv_sound.setImageResource(R.drawable.icon_cateye_sound_on);
            cameaHelper.getmCamera().startListening(0, false);
        } else {
            iv_sound_landscape.setImageResource(R.drawable.btn_sound_fullscreen_off);
            iv_sound.setImageResource(R.drawable.icon_cateye_sound_off);
            cameaHelper.getmCamera().stopListening(0);
        }
    }

    private void setIsSpeaking(boolean isSpeak) {
        this.isSpeak = isSpeak;
        if (isSpeak) {
            VibratorUtil.holdSpeakVibration();
            iv_hold_speek_landscape.setImageResource(R.drawable.btn_hold_fullscreen_pre);
            iv_hold_speek.setImageResource(R.drawable.icon_hold_speek_on);
            cameaHelper.getmCamera().startSpeaking(0);
        } else {
            iv_hold_speek_landscape.setImageResource(R.drawable.btn_hold_fullscreen);
            iv_hold_speek.setImageResource(R.drawable.icon_hold_speek);
            cameaHelper.getmCamera().stopSpeaking(0);
        }
    }

    private void snapshot() {
        isSnap = true;
        String savePath = FileUtil.getSnapshotPath() + "/" + deviceId;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmsss");
        String time = simpleDateFormat.format(System.currentTimeMillis());
        String fileName = time + ".jpg";
        IotUtil.saveBitmapToJpeg(mediaCodecMonitor.getBitmapSnap(), savePath, fileName);
        soundPool.play(snapshot_sound_id, 1.0f, 1.0f, 0, 0, 1);
    }

    //自动抓拍最后一帧作为首页widget背景
    private void autoCatchLastFrame() {
        String savePath = FileUtil.getLastFramePath();
        String fileName = deviceId + ".jpg";
        boolean success = IotUtil.saveBitmapToJpeg(mediaCodecMonitor.getBitmapSnap(), savePath, fileName);
//        ToastUtil.show(success+"");
        EventBus.getDefault().post(new LastFrameEvent(deviceId, savePath + "/" + fileName));
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
                mediaCodecMonitor.setBackground(new BitmapDrawable(getResources(), bitmap));
                return;
            }
        }
        mediaCodecMonitor.setBackgroundResource(R.drawable.camera_default_bg1);
    }

    private void showLastSnapshot() {
        final String savePath = FileUtil.getSnapshotPath() + "/" + deviceId;
        File savePathFile = new File(savePath);
        String[] bmpFiles = savePathFile.list();
        if (bmpFiles != null && bmpFiles.length > 0) {
            final String bmpFile = bmpFiles[bmpFiles.length - 1];
            final Bitmap bitmap = BitmapFactory.decodeFile(savePath + "/" + bmpFile);
            if (isSnap) {
                DisplayUtil.snapAnimotion(this, main_container, mediaCodecMonitor, iv_album, bitmap, new DisplayUtil.onCompleteListener() {
                    @Override
                    public void onComplete() {
                        iv_album.setImageBitmap(bitmap);
                    }
                });
            } else {
                iv_album.setImageBitmap(bitmap);
            }
        } else {
            iv_album.setImageResource(R.drawable.eques_snapshot_default);
        }
    }

    private void pullDownAnimation() {
        iv_hold_speek.setVisibility(View.GONE);
        tv_hold_speek.setVisibility(View.GONE);
        yt_cylincam.setVisibility(View.VISIBLE);
        TranslateAnimation animation = new TranslateAnimation(0, 0, -200, 0);
        animation.setDuration(1000);//设置动画持续时间
        yt_cylincam.startAnimation(animation);
//        iv_hold_speek.startAnimation(animation);
//        tv_hold_speek.startAnimation(animation);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                iv_arrows.setImageResource(R.drawable.arrow_up_white);
            }
        }, 1000);
    }

    private void pushupAnimation() {
        yt_cylincam.setVisibility(View.GONE);
        iv_hold_speek.setVisibility(View.VISIBLE);
        tv_hold_speek.setVisibility(View.VISIBLE);
        TranslateAnimation animation = new TranslateAnimation(0, 0, 200, 0);
        animation.setDuration(1000);//设置动画持续时间
//        yt_penguin.startAnimation(animation);
        iv_hold_speek.startAnimation(animation);
        tv_hold_speek.startAnimation(animation);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                iv_arrows.setImageResource(R.drawable.arrow_down_white);
            }
        }, 1000);
    }

    class MyDirection implements CylincamYuntaiButton.OnDirectionLisenter {
        @Override
        public void directionLisenter(CylincamYuntaiButton.Direction direction) {
            startRotate(0);
            ytHandler.removeMessages(YUNTAI_CONTROL);
            if (direction != CylincamYuntaiButton.Direction.none) {
                ytHandler.sendMessageDelayed(
                        Message.obtain(ytHandler, YUNTAI_CONTROL, direction),
                        500);
            }
        }
    }

    class MyDirectionLandscape implements CylincamYuntaiButtonLandscape.OnDirectionLisenter {
        @Override
        public void directionLisenter(CylincamYuntaiButtonLandscape.Direction direction) {
            startRotate(0);
            ytHandlerLandscape.removeMessages(YUNTAI_CONTROL);
            if (direction != CylincamYuntaiButtonLandscape.Direction.none) {
                ytHandlerLandscape.sendMessageDelayed(
                        Message.obtain(ytHandlerLandscape, YUNTAI_CONTROL, direction),
                        500);
            }
        }
    }

    /**
     * 横屏滑动事件 add by hxc
     * direction: 0.停止 1.左转 2.右转
     */
    private void startRotate(int direction) {
        byte location = -1;
        if (cameaHelper.getmCamera() != null && mAVChannel >= 0) {
            switch (direction) {
                case 0://停止
                    location = (byte) AVIOCTRLDEFs.AVIOCTRL_PTZ_STOP;
                    break;
                case 1://右
                    location = (byte) AVIOCTRLDEFs.AVIOCTRL_PTZ_RIGHT;
                    break;
                case 2://左
                    location = (byte) AVIOCTRLDEFs.AVIOCTRL_PTZ_LEFT;
                    break;
            }
            cameaHelper.getmCamera().sendIOCtrl(mAVChannel,
                    AVIOCTRLDEFs.IOTYPE_USER_IPCAM_PTZ_COMMAND,
                    AVIOCTRLDEFs.SMsgAVIoctrlPtzCmd.parseContent(
                            location,
                            (byte) 0x8, (byte) 0, (byte) 0, (byte) 0,
                            (byte) 0));
        }
    }

    private void checkExistSD(Map<String, Integer> sdkMap) {
        int status = sdkMap.get(Config.status);
        int sdexist = sdkMap.get(Config.sdexist);
        if (status == 0 && sdexist == 0) {
            if (isShowExistSdToast) {
                //sd卡 存在， 可读可写
                Intent it = new Intent(this, CylincamReplayActivity.class);
                it.putExtra("deviceId", deviceId);
                it.putExtra("tutkPwd", tutkPwd);
                startActivity(it);
            }
        } else if (status == -1 && sdexist == 0) {//sd卡 存在 ，但不可用，因此 需要格式化
        } else if (sdexist == -1) {//sd卡不存在
            if (isShowExistSdToast) {
                ToastUtil.show(getString(R.string.No_SD_Look_Back));
            }
        }
    }

    private CameraHelper.IOTCDevConnCallback iotcDevConnCallback = new CameraHelper.IOTCDevConnCallback() {
        @Override
        public void success() {
            Log.i(TAG, "===success===");
            IotSendOrder.settingsResolution(cameaHelper.getmCamera(), definitionValue);
            IotSendOrder.connect(cameaHelper.getmCamera());
            cameaHelper.createVideoStream(getDecodingWay());//硬解码
            cameaHelper.createVideoCarrier(mediaCodecMonitor);
        }

        @Override
        public void session() {
            Log.i(TAG, "===session===");
            if (createSessionWaitThread == null) {
                createSessionWaitThread = new CreateSessionWaitThread();
                createSessionWaitThread.start();
            }
        }

        @Override
        public void avChannel() {
            Log.i(TAG, "===avChannel===");
            createAvChannelWaitThread = new CreateAvChannelWaitThread();
            createAvChannelWaitThread.start();
        }
    };

    private CameraHelper.Observer observer = new CameraHelper.Observer() {
        @Override
        public void avIOCtrlOnLine() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateState(0);
                    mediaCodecMonitor.setBackground(null);
                }
            });
        }

        @Override
        public void avIOCtrlDataSource(final byte[] data, final int avIOCtrlMsgType) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (avIOCtrlMsgType) {
                        case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_SDCARD_STATUS_RESP://sd卡状态
                            ProgressDialogManager.getDialogManager().dimissDialog(QUERY_SD, 0);
                            checkExistSD(IotUtil.byteArrayToMap(data));
                            break;
                    }
                }
            });
        }

        @Override
        public void avIOCtrlMsg(final int resCode, String method) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    ToastUtil.single(IOTResCodeUtil.transformStr(resCode,CylincamPlayActivity.this));
                    switch (resCode) {
                        case Camera.CONNECTION_NOT_INITIALIZED:
                            updateState(2);
                            WLog.d(CylincamPlayActivity.this.getClass().getSimpleName(), "通道没有初始化,请退出重试");
                            return;
                        case Camera.CONNECTION_STATE_IOTC_WAKE_UP://唤醒
                            WLog.d(CylincamPlayActivity.this.getClass().getSimpleName(), "设备唤醒中请稍后");
                            return;
                        case Camera.CONNECTION_ER_DEVICE_OFFLINE://设备不在线
                            updateState(-1);
                            WLog.d(CylincamPlayActivity.this.getClass().getSimpleName(), "设备不在线");
                            return;
                        case Camera.CONNECTION_STATE_CONNECTED://连接成功
                            WLog.d(CylincamPlayActivity.this.getClass().getSimpleName(), "设备通道连接成功");
                            return;
                        case Camera.CONNECTION_EXCEED_MAX_SESSION://会话超出限制
                            updateState(2);
                            WLog.d(CylincamPlayActivity.this.getClass().getSimpleName(), "设备会话超过最大限制");
                            return;
                        case Camera.CONNECTION_DEVICE_NOT_LISTENING://设备连接异常
                            updateState(2);
                            WLog.d(CylincamPlayActivity.this.getClass().getSimpleName(), "设备连接异常请稍后再试");
                            return;
                        case Camera.CONNECTION_STATE_TIMEOUT:
                        case Camera.CONNECTION_STATE_IOTC_INVALID_SID:
                        case Camera.CONNECTION_STATE_IOTC_SESSION_CLOSE_BY_REMOTE:
                        case Camera.CONNECTION_STATE_CONNECT_FAILED:
                            updateState(2);
                            WLog.d(CylincamPlayActivity.this.getClass().getSimpleName(), "网络通道会话异常,请返回加载列表重试");
                            return;
                        case Camera.CONNECTION_STATE_IOTC_CLIENT_MAX:
                            updateState(2);
                            WLog.d(CylincamPlayActivity.this.getClass().getSimpleName(), "连接人数超限制");
                            return;
                        case Camera.CONNECTION_STATE_IOTC_NETWORK_IS_POOR:
                            updateState(2);
                            WLog.d(CylincamPlayActivity.this.getClass().getSimpleName(), "当前网络差，请等待");
                            return;
                        default:
                            WLog.d(CylincamPlayActivity.this.getClass().getSimpleName(), "Other");
                            return;
                    }
                }
            });
        }
    };

    private CreateSessionWaitThread createSessionWaitThread = null;

    private class CreateSessionWaitThread extends Thread {
        private boolean mIsRunning = true;

        public void stopThread() {
            mIsRunning = false;
        }

        @Override
        public void run() {
            mIsRunning = true;
            while (mIsRunning) {
                if (cameaHelper != null) {
                    if (cameaHelper.checkSession()) {
                        cameaHelper.register();
                        mIsRunning = false;
                    }
                }
            }
        }
    }

    private CreateAvChannelWaitThread createAvChannelWaitThread = null;

    private class CreateAvChannelWaitThread extends Thread {
        private boolean mIsRunning = true;

        public void stopThread() {
            mIsRunning = false;
        }

        @Override
        public void run() {
            mIsRunning = true;
            while (mIsRunning) {
                if (cameaHelper != null) {
                    if (cameaHelper.checkAvChannel()) {
                        cameaHelper.register();
                        mIsRunning = false;
                    }
                }
            }
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
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && deviceId != null) {
            if (TextUtils.equals(event.device.devID, deviceId)) {
                setTitleText(DeviceInfoDictionary.getNameByTypeAndName(event.device.type, event.device.name));
            }
        }
    }

}
