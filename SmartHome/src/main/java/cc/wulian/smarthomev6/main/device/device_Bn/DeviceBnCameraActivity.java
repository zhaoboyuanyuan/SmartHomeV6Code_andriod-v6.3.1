package cc.wulian.smarthomev6.main.device.device_Bn;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wulian.sdk.android.ipc.rtcv2.IPCController;
import com.wulian.sdk.android.ipc.rtcv2.IPCMsgApiType;
import com.wulian.sdk.android.ipc.rtcv2.IPCMsgController;
import com.wulian.sdk.android.ipc.rtcv2.IPCResultCallBack;
import com.wulian.sdk.android.ipc.rtcv2.message.IPCCallStateMsgEvent;
import com.wulian.sdk.android.ipc.rtcv2.message.IPCOnReceivedMsgEvent;
import com.wulian.sdk.android.ipc.rtcv2.message.IPCVideoFrameMsgEvent;
import com.wulian.sdk.android.ipc.rtcv2.message.IPCcameraXmlMsgEvent;
import com.wulian.sdk.android.ipc.rtcv2.message.messagestate.MsgCallState;
import com.wulian.sdk.android.ipc.rtcv2.message.messagestate.MsgReceivedType;
import com.wulian.sdk.android.ipc.rtcv2.utils.IPCGetFrameFunctionType;
import com.wulian.webrtc.ViEAndroidGLES20;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.DeviceDetailMsg;
import cc.wulian.smarthomev6.entity.LocalInfo;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.DeviceMoreActivity;
import cc.wulian.smarthomev6.main.device.UnlockAutoDialogActivity;
import cc.wulian.smarthomev6.main.device.cateye.CateyeVisitorActivity;
import cc.wulian.smarthomev6.main.device.cateye.album.AlbumGridActivity;
import cc.wulian.smarthomev6.main.device.device_Bn.config.DevBnWifiConfigActivity;
import cc.wulian.smarthomev6.main.home.scene.SceneListDialogActivity;
import cc.wulian.smarthomev6.main.message.alarm.BcAlarmActivity;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.ICamCloudApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.KeyValueBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.KeyValueListBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamGetSipInfoBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.CateyeDoorbellEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.LastFrameEvent;
import cc.wulian.smarthomev6.support.event.NetworkInfoEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.AESUtil;
import cc.wulian.smarthomev6.support.utils.CameraUtil;
import cc.wulian.smarthomev6.support.utils.DisplayUtil;
import cc.wulian.smarthomev6.support.utils.FileUtil;
import cc.wulian.smarthomev6.support.utils.LogUtil;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.VibratorUtil;
import cc.wulian.smarthomev6.support.utils.WLog;
import cc.wulian.smarthomev6.support.utils.XmlHandler;
import cc.wulian.smarthomev6.support.utils.biometric.BiometricListener;

/**
 * Created by hxc on 2017/7/7.
 * 皇冠可视锁Bn视频主界面
 */

public class DeviceBnCameraActivity extends BaseTitleActivity {

    private static final String KEY_CAMERA_ID = "icam_camera_id";
    private static final String PROCESS = "cameraLog";
    private static final String KEY_LOCK_DEVICE_ID = "lock_id";
    private static final String KEY_RING_CALL = "isRingCall";
    private static final String PERMISSION_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int SHOW_SPEED_INTERVAL = 3000;// 速度间隔为3秒
    private static final int CONNECT_INTERVAL = 20 * 1000;// 视频连接时间，超出时间显示连接失败
    private static final int PING_INTERVAL = 10 * 1000;//心跳间隔为10秒
    private static final int AUTO_HANG_UP_INTERVAL = 100 * 1000;//120s硬件下电挂断视频流

    private FrameLayout layout_video_container;
    private FrameLayout main_container;
    private View layout_video_loading, layout_video_reload, layout_video_offline;
    private LinearLayout llNetSpeed;
    private TextView tv_network_speed, tv_hold_speak;
    private TextView tvReloadTips;
    private ViEAndroidGLES20 view_video;
    private View btn_snapshot;
    private ImageView btn_sound_switch, btn_alarmlist, btn_visitor, btn_unlock, btn_scene, iv_snapshot;
    private View btn_hold_speak;
    private ImageView iv_hold_speak;

    private ImageView ivBattery;

    private ICamCloudApiUnit iCamCloudApiUnit;
    private DataApiUnit dataApiUnit;
    private ICamGetSipInfoBean iCamGetSipInfoBean;
    private SoundPool soundPool;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable connectRunnable;
    private Runnable autoHangUpRunnable;
    private WLDialog configDialog;
    private WLDialog.Builder builder;
    private Device device;

    public static boolean hasInit = false;
    private boolean isRadioOpen = false;
    private boolean isFirstCreate = false;
    private boolean isPlayAndRecord = false;
    private boolean isRingCall = false;
    private boolean isFirstCall = true;
    private boolean isShowLimitsDialog = false;
    private boolean flag = true;//当前页标志位
    private boolean needConfigWifi;//云端没有绑定关系标志位
    private boolean hasGetBattery;
    private int registerExpTime = 0;
    private int snapshot_sound_id;
    private int retryCount = 0;//门铃来电重呼次数
    private int videoPlayState;//0 loading，1 断开，2 播放, 3 离线
    private String wifiName;
    private String cameraId;
    private String lockDeviceId;
    private String bindResult;
    private long saveReceivedDataSize = 0;
    //    private String config = "{\"deviceID\":\"BC_DEVICEID\",\"data\":[{\"group\":\"bc_dev\",\"param\":[{\"key\":\"gwID\",\"type\":\"string\",\"value\":\"BC_GWID\"},{\"key\":\"devId\",\"type\":\"string\",\"value\":\"BC_DEVICEID\"},{\"key\":\"deviceId\",\"type\":\"string\",\"value\":\"BC_DEVICEID\"}],\"item\":[{\"type\":\"jump\",\"name\":\"BC_DEVICEINFO\",\"action\":\"jump:DevBn_deviceInfo\"}]},{\"group\":\"bc\",\"param\":[{\"key\":\"gwID\",\"type\":\"string\",\"value\":\"BC_GWID\"},{\"key\":\"devId\",\"type\":\"string\",\"value\":\"BC_DEVICEID\"},{\"key\":\"deviceId\",\"type\":\"string\",\"value\":\"BC_DEVICEID\"}],\"item\":[{\"type\":\"jump\",\"name\":\"BC_ALBUM\",\"action\":\"jump:DevBc_CaptureImages\"},{\"type\":\"jump\",\"name\":\"BC_VISITRECORD\",\"action\":\"jump:DevBc_VisitorsNotes\",\"param\":[{\"key\":\"devType\",\"type\":\"string\",\"value\":\"Bc\"}]},{\"type\":\"custom\",\"name\":\"BC_WIFICONFIG\",\"action\":\"custom:DevBc_wifiSetting\"}]},{\"group\":\"bc_ring_snapshot\",\"param\":[{\"key\":\"gwID\",\"type\":\"string\",\"value\":\"BC_GWID\"},{\"key\":\"devId\",\"type\":\"string\",\"value\":\"BC_DEVICEID\"},{\"key\":\"deviceId\",\"type\":\"string\",\"value\":\"BC_DEVICEID\"}],\"item\":[{\"type\":\"custom\",\"name\":\"BC_RING_SNAPSHOT\",\"action\":\"custom:door_ring_snapshot\"}]},{\"group\":\"bc_leavehome\",\"param\":[{\"key\":\"gwID\",\"type\":\"string\",\"value\":\"BC_GWID\"},{\"key\":\"devId\",\"type\":\"string\",\"value\":\"BC_DEVICEID\"},{\"key\":\"deviceId\",\"type\":\"string\",\"value\":\"BC_DEVICEID\"}],\"item\":[{\"type\":\"custom\",\"name\":\"BC_LEAVE\",\"action\":\"custom:LeaveHomeBtn\"}]},{\"group\":\"admin\",\"offLineDisable\":true,\"item\":[{\"type\":\"custom\",\"name\":\"BC_USERMANAGER\",\"action\":\"custom:DevBc_userManager\",\"param\":[{\"key\":\"gwID\",\"type\":\"string\",\"value\":\"BC_GWID\"},{\"key\":\"devId\",\"type\":\"string\",\"value\":\"BC_DEVICEID\"}]},{\"type\":\"jump\",\"name\":\"BC_MESSAGE\",\"action\":\"jump:Lock_Message\",\"showWithEnterType\":\"account\",\"showInLocale\":\"zh\",\"param\":[{\"key\":\"url\",\"type\":\"string\",\"value\":\"SMSNotification\\/smsDist\\/sms.html\"},{\"key\":\"gwID\",\"type\":\"string\",\"value\":\"BC_GWID\"},{\"key\":\"devId\",\"type\":\"string\",\"value\":\"BC_DEVICEID\"}]},{\"type\":\"custom\",\"name\":\"BC_USERMANAGER\",\"action\":\"custom:touchid_unlock\",\"param\":[{\"key\":\"gwID\",\"type\":\"string\",\"value\":\"BC_GWID\"},{\"key\":\"devId\",\"type\":\"string\",\"value\":\"BC_DEVICEID\"}]}]},{\"group\":\"log\",\"param\":[{\"key\":\"gwID\",\"type\":\"string\",\"value\":\"BC_GWID\"},{\"key\":\"devId\",\"type\":\"string\",\"value\":\"BC_DEVICEID\"},{\"key\":\"deviceID\",\"type\":\"string\",\"value\":\"BC_DEVICEID\"}],\"item\":[{\"type\":\"jump\",\"name\":\"BC_ALARMMESSAGE\",\"action\":\"jump:Alarm_Bc\",\"showWithEnterType\":\"account\",\"param\":[{\"key\":\"msgType\",\"type\":\"string\",\"value\":\"type_alarm\"}]},{\"type\":\"jump\",\"name\":\"BC_LOG\",\"action\":\"jump:Log\",\"showWithEnterType\":\"account\"},{\"offLineDisable\":true,\"type\":\"jump\",\"name\":\"BC_ALARMSETTING\",\"action\":\"jump:DevBc_AlarmSetting\"}]}],\"cameraId\":\"BC_CAMERAID\",\"cameraWifi\":\"BC_CAMERAWIFI\"}";
    private String config = "{\"deviceID\":\"BC_DEVICEID\",\"data\":[{\"group\":\"bc_dev\",\"param\":[{\"key\":\"gwID\",\"type\":\"string\",\"value\":\"BC_GWID\"},{\"key\":\"devId\",\"type\":\"string\",\"value\":\"BC_DEVICEID\"},{\"key\":\"deviceId\",\"type\":\"string\",\"value\":\"BC_DEVICEID\"}],\"item\":[{\"type\":\"jump\",\"name\":\"BC_DEVICEINFO\",\"action\":\"jump:DevBn_deviceInfo\"},{\"type\":\"custom\",\"name\":\"BC_SETTINGS\",\"action\":\"custom:DevBc_settings\"},{\"type\":\"custom\",\"name\":\"BC_USERMANAGER\",\"action\":\"custom:DevBc_userManager\"},{\"type\":\"custom\",\"name\":\"BC_USERMANAGER\",\"action\":\"custom:touchid_unlock\"},{\"type\":\"custom\",\"name\":\"BC_WIFICONFIG\",\"action\":\"custom:DevBc_wifiSetting\"}]}],\"cameraId\":\"BC_CAMERAID\",\"cameraWifi\":\"BC_CAMERAWIFI\"}";

    public static void startActivity(Context context, String lockDeviceId) {
        Intent intent = new Intent(context, DeviceBnCameraActivity.class);
        intent.putExtra(KEY_LOCK_DEVICE_ID, lockDeviceId);
        context.startActivity(intent);
    }

    public static void start(Context context, String deviceId, String cameraId, boolean isRingCall) {
        Intent intent = new Intent(context, DeviceBnCameraActivity.class);
        intent.putExtra(KEY_LOCK_DEVICE_ID, deviceId);
        intent.putExtra(KEY_CAMERA_ID, cameraId);
        intent.putExtra(KEY_RING_CALL, isRingCall);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_bn_camera, true);
        EventBus.getDefault().register(this);
        CameraUtil.setHasVideoActivityRunning(true);
    }

    @Override
    public boolean enableSwipeBack() {
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        showLastSnapshot();
        flag = true;
        if (videoPlayState != 2 && !isShowLimitsDialog) {
            getBindResult();
        }
    }

    @Override
    protected void onPause() {
        if (videoPlayState == 2) {
            IPCController.getRenderFrame("hello", IPCGetFrameFunctionType.FRAME_MAIN_THUNBNAIL);
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (isPlayAndRecord) {
            IPCController.stopPlayAndRecordAudioAsync(null);
            isPlayAndRecord = false;
        }
        tv_network_speed.setText("0KB/s");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        handler.removeCallbacksAndMessages(null);
        IPCController.closeAllVideoAsync(new IPCResultCallBack() {
            @Override
            public void getResult(int i) {
            }
        });
        IPCController.setRender("", null);
        layout_video_container.removeView(view_video);
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        DevBnCallToAnswerActivity.isReceived = false;
        CameraUtil.setHasVideoActivityRunning(false);
        super.onDestroy();
    }

    private void getSipInfo() {
        if (iCamGetSipInfoBean == null) {
            iCamCloudApiUnit.doGetSipInfo(cameraId, false, new ICamCloudApiUnit.IcamApiCommonListener<ICamGetSipInfoBean>() {
                @Override
                public void onSuccess(ICamGetSipInfoBean bean) {
                    if (bean != null) {
                        iCamGetSipInfoBean = bean;
                        if (isSipInfoIntegral()) {
                            LogUtil.WriteBcLog("sipDomain = " + bean.sipDomain + ";suid = " + bean.suid + ";passwprd = " + bean.spassword + ";deviceDomain = " + bean.deviceDomain);
                            startWork();
                        } else {
                            updateLoadingState(1);
                        }

                    }
                }

                @Override
                public void onFail(int code, String msg) {

                }
            });
        } else {
            startWork();
        }
    }

    private void startWork() {
        if (MainApplication.getApplication().hasRegisterSipAccount && TextUtils.equals(preference.getCurrentSipSuid(), iCamGetSipInfoBean.suid)) {
            awakeCamera();
            makeCall();
            handler.postDelayed(reWakeTask, SHOW_SPEED_INTERVAL);
        } else {
            initSip();
        }
    }

    private void awakeAndCall() {
        LogUtil.WriteBcLog("发送唤醒和呼叫命令");
        awakeCamera();
        makeCall();
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
            //这个标志位是因为关闭权限弹框是会走onResume回调，造成多次呼叫引发问题
            isShowLimitsDialog = true;
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                // Permission Denied
                ToastUtil.show(R.string.Toast_Permission_Denied);
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitleAndRightImg(getString(R.string.Device_List_Networklock), R.drawable.icon_more);
    }

    @Override
    protected void initView() {
        main_container = (FrameLayout) findViewById(R.id.main_container);
        layout_video_container = (FrameLayout) findViewById(R.id.layout_video_container);
        layout_video_loading = findViewById(R.id.layout_video_loading);
        layout_video_reload = findViewById(R.id.layout_video_reload);
        layout_video_offline = findViewById(R.id.layout_video_offline);
        llNetSpeed = (LinearLayout) findViewById(R.id.ll_net_speed);
        updateLoadingState(0);
        view_video = new ViEAndroidGLES20(this);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layout_video_container.addView(view_video, 0, layoutParams);
        view_video.setKeepScreenOn(true);

        tv_network_speed = (TextView) findViewById(R.id.tv_network_speed);
        tv_hold_speak = (TextView) findViewById(R.id.tv_hold_speek);
        tvReloadTips = (TextView) findViewById(R.id.tv_reload_tips);
        btn_snapshot = findViewById(R.id.btn_snapshot);
        iv_snapshot = (ImageView) findViewById(R.id.iv_snapshot);
        btn_sound_switch = (ImageView) findViewById(R.id.btn_sound_switch);
        btn_alarmlist = (ImageView) findViewById(R.id.btn_alarmlist);
        btn_visitor = (ImageView) findViewById(R.id.btn_visitor);
        btn_unlock = (ImageView) findViewById(R.id.btn_unlock);
        btn_scene = (ImageView) findViewById(R.id.btn_scene);
        btn_hold_speak = findViewById(R.id.btn_hold_speek);
        iv_hold_speak = (ImageView) findViewById(R.id.iv_hold_speek);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
    }

    @Override
    protected void initData() {
        isFirstCreate = true;
        Bundle bd = getIntent().getExtras();
        if (bd == null) {
            ToastUtil.show("Bundle is Empty!");
            return;
        }
        cameraId = bd.getString(KEY_CAMERA_ID);
        lockDeviceId = bd.getString(KEY_LOCK_DEVICE_ID);
        isRingCall = bd.getBoolean(KEY_RING_CALL);
        iCamCloudApiUnit = new ICamCloudApiUnit(this);
        dataApiUnit = new DataApiUnit(this);
        if (!isRingCall) {
            getCameraIdByCloud();
        }
        device = MainApplication.getApplication().getDeviceCache().get(lockDeviceId);
        if (device != null) {
            if (!TextUtils.isEmpty(device.name)) {
                setTitleText(DeviceInfoDictionary.getNameByTypeAndName(device.type, device.name));
            }
            if (device.isOnLine()) {
                updateLoadingState(0);
            } else {
                updateLoadingState(3);
            }
        }

        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        snapshot_sound_id = soundPool.load(this, R.raw.snapshot, 1);

        setRadioOpen(isRadioOpen);
        getLockBattery();
        checkPermission();
        showSnapshot();
        connectRunnable = new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateLoadingState(1);
                        tvReloadTips.setText(getString(R.string.Camera_Hint_ConnectFail));
                        handler.removeCallbacks(reWakeTask);
                    }
                });
            }
        };
        autoHangUpRunnable = new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.show(getString(R.string.bc_lock_camera_time_2min));
                    }
                });
            }
        };
        handler.postDelayed(connectRunnable, CONNECT_INTERVAL);
    }

    @Override
    protected void initListeners() {
        view_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        btn_snapshot.setOnClickListener(this);
        iv_snapshot.setOnClickListener(this);
        btn_sound_switch.setOnClickListener(this);
        btn_alarmlist.setOnClickListener(this);
        btn_visitor.setOnClickListener(this);
        btn_unlock.setOnClickListener(this);
        btn_scene.setOnClickListener(this);
        layout_video_reload.setOnClickListener(this);

        btn_hold_speak.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (videoPlayState == 2) {//播放状态才能点击
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            iv_hold_speak.setImageResource(R.drawable.icon_hold_speek_on);
                            tv_hold_speak.setText(R.string.Cateye_In_Call);
                            IPCController.recordAudioAsync(new IPCResultCallBack() {
                                @Override
                                public void getResult(int i) {
                                    WLog.i("recordAudioAsync result:" + i);
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            setRadioOpen(false);
                                        }
                                    });
                                    VibratorUtil.holdSpeakVibration();
                                }
                            });
                        }
                        break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL: {
                            iv_hold_speak.setImageResource(R.drawable.icon_hold_speek);
                            tv_hold_speak.setText(R.string.CateEye_Detail_Hold_Speek);
                            IPCController.stopRecordAudioAsync(new IPCResultCallBack() {
                                @Override
                                public void getResult(int i) {
                                    WLog.i("stopRecordAudioAsync result:" + i);
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            setRadioOpen(true);
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
    public void onClickView(View v) {
//        super.onClick(v);

        if (videoPlayState != 3) {//离线不能点击
            if (v == btn_snapshot) {
                if (videoPlayState == 2) {
                    IPCController.getRenderFrame("hello", IPCGetFrameFunctionType.FRAME_PLAY_THUMBNAIL);
                    btn_snapshot.setEnabled(false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btn_snapshot.setEnabled(true);
                        }
                    }, 1000);
                }
            } else if (v == btn_sound_switch) {
                if (videoPlayState == 2) {
                    setRadioOpen(!isRadioOpen);
                }
            } else if (v == layout_video_reload) {
                if (needConfigWifi) {
                    showConfigDialog();
                } else {
                    updateLoadingState(0);
                    handler.postDelayed(connectRunnable, CONNECT_INTERVAL);
                    if (isSipInfoIntegral()) {
                        handler.postDelayed(reWakeTask, SHOW_SPEED_INTERVAL);
                    } else {
                        updateLoadingState(1);
                    }
                }
            }
        }
        if (v == iv_snapshot) {
            Intent intent = new Intent(this, AlbumGridActivity.class);
            intent.putExtra("devId", lockDeviceId);
            startActivity(intent);
        } else if (v == btn_visitor) {
            CateyeVisitorActivity.start(this, lockDeviceId, cameraId, Preference.getPreferences().getCurrentGatewayID());
        } else if (v == btn_alarmlist) {
            BcAlarmActivity.start(this, lockDeviceId, BcAlarmActivity.TYPE_ALARM);
        } else if (v == btn_unlock) {
            String encryptPwd = Preference.getPreferences().getTouchIDunLock(lockDeviceId);
            LocalInfo localInfo = MainApplication.getApplication().getLocalInfo();
            String pwd = AESUtil.decrypt(encryptPwd, localInfo.appID, true);
            if (TextUtils.equals(encryptPwd, "")) {
                UnlockAutoDialogActivity.start(this, lockDeviceId);
            } else {
                BiometricListener biometricListener = new BiometricListener(DeviceBnCameraActivity.this, device, pwd);
                biometricListener.handle();
            }
        } else if (v.getId() == R.id.img_right) {
            if (device == null) {
                return;
            }
            if (device.isShared) {
                ToastUtil.show(R.string.Share_More_Tip);
                return;
            }
            flag = false;//这个地方置为false是为了避免设置页的521信息影响挡墙界面，造成一直发rewake的信息
            handler.removeCallbacks(reWakeTask);
            Intent intent = new Intent(this, DeviceMoreActivityForBn.class);
            intent.putExtra(DeviceMoreActivity.KEY_DEVICE_ID, lockDeviceId);
            intent.putExtra(DeviceMoreActivity.KEY_MORE_CONFIG, getSettingConfigData(config));
            startActivityForResult(intent, 1);
        } else if (v == btn_scene) {
            startActivity(new Intent(this, SceneListDialogActivity.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            this.finish();
        }
    }

    private String getSettingConfigData(String data) {
        String config = data.replaceAll("BC_GWID", device.gwID)
                .replaceAll("BC_DEVICEID", device.devID)
                .replaceAll("BC_ALBUM", getString(R.string.CateEye_Album_Tittle))
                .replaceAll("BC_VISITRECORD", getString(R.string.CateEye_Visitor_Record))
                .replaceAll("BC_WIFICONFIG", getString(R.string.Config_WiFi))
                .replaceAll("BC_RING_SNAPSHOT", getString(R.string.One_key_lock_Doorbellsnapshot))
                .replaceAll("BC_LEAVE", getString(R.string.Device_Vidicon_AwayButton))
                .replaceAll("BC_USERMANAGER", getString(R.string.Device_Vidicon_UserAdministrate))
                .replaceAll("BC_MESSAGE", "短信通知")
                .replaceAll("BC_ALARMMESSAGE", getString(R.string.Message_Center_AlarmMessage))
                .replaceAll("BC_LOG", getString(R.string.Message_Center_Log))
                .replaceAll("BC_ALARMSETTING", getString(R.string.Device_Vidicon_AlarmSetting))
                .replaceAll("BC_DEVICEINFO", getString(R.string.Device_Info));
//                .replaceAll("BC_CAMERAID", cameraId)
//                .replaceAll("BC_CAMERAWIFI", wifiName);

        if (TextUtils.isEmpty(cameraId)) {
            config = config.replaceAll("BC_CAMERAID", "--");
        } else {
            config = config.replaceAll("BC_CAMERAID", cameraId);
        }

        if (TextUtils.isEmpty(wifiName)) {
            config = config.replaceAll("BC_CAMERAWIFI", "--");
        } else {
            config = config.replaceAll("BC_CAMERAWIFI", wifiName);
        }
        WLog.i("hxc", "----" + config);
        return config;
    }


    /**
     * 判断sip信息是否完整，没有字段为空
     *
     * @return
     */
    private boolean isSipInfoIntegral() {
        if (iCamGetSipInfoBean != null
                && !TextUtils.isEmpty(iCamGetSipInfoBean.deviceDomain)
                && !TextUtils.isEmpty(iCamGetSipInfoBean.sipDomain)
                && !TextUtils.isEmpty(iCamGetSipInfoBean.suid)
                && !TextUtils.isEmpty(iCamGetSipInfoBean.spassword)) {
            return true;
        } else {
            return false;
        }
    }

    private void getLockBattery() {
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", device.gwID);
            object.put("devID", device.devID);
            object.put("clusterId", 0x0101);
            object.put("commandType", 1);
            object.put("commandId", 0x800A);
            object.put("endpointNumber", 1);
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取bn锁的摄像机id
     */
    private void getCameraIdByCloud() {
        dataApiUnit.doGetBcCameraId(lockDeviceId, "Bn", new DataApiUnit.DataApiCommonListener<String>() {
            @Override
            public void onSuccess(String bean) {
                if (!TextUtils.isEmpty(bean)) {
                    cameraId = bean;
                    LogUtil.WriteBcLog("获取云上存的摄像机id: " + cameraId);
                } else {
                    //摄像机Id没查到需要绑定
                    showConfigDialog();
                }
            }

            @Override
            public void onFail(int code, String msg) {


            }
        });
    }


    /**
     * 查询摄像头的绑定关系
     */
    private void getBindResult() {
        dataApiUnit.doGetDeviceKeyValue(lockDeviceId, "", new DataApiUnit.DataApiCommonListener<KeyValueListBean>() {
            @Override
            public void onSuccess(KeyValueListBean keyValueListBean) {
                WLog.i(PROCESS, "获取绑定关系");
                if (keyValueListBean != null && keyValueListBean.keyValues != null && keyValueListBean.keyValues.size() > 0) {
                    for (KeyValueBean bean :
                            keyValueListBean.keyValues) {
                        if (bean.key.equals("wifiName")) {
                            wifiName = bean.value;
                        }
                        if (bean.key.equals("bindResult")) {
                            bindResult = bean.value;
                        }
                    }
                }
                LogUtil.WriteBcLog("查询到的绑定信息-->WiFiName：" + wifiName + ",bindResult:" + bindResult);
                if (!TextUtils.isEmpty(wifiName) && TextUtils.equals("1", bindResult)) {
                    getSipInfo();
                } else {
                    showConfigDialog();
                }

            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(msg);

            }
        });

    }

    private void initSip() {
        if (!MainApplication.getApplication().hasInitSip) {
            IPCResultCallBack initRTCAsyncCallback = new IPCResultCallBack() {
                @Override
                public void getResult(int result) {
                    hasInit = result == 0 ? true : false;
                    if (hasInit) {
                        MainApplication.getApplication().hasInitSip = hasInit;
                    }
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startRegister();
                        }
                    }, 500);
                    handler.postDelayed(reWakeTask, SHOW_SPEED_INTERVAL);
                }
            };
            IPCController.initRTCAsync(initRTCAsyncCallback);
        } else {
            startRegister();
            handler.postDelayed(reWakeTask, SHOW_SPEED_INTERVAL);
        }
    }

    private void startRegister() {
        IPCResultCallBack ipcResultCallBack = new IPCResultCallBack() {
            @Override
            public void getResult(int i) {
                boolean isRegisterAccount = i == 0 ? true : false;
                if (isRegisterAccount) {
                    makeCall();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MainApplication.getApplication().hasRegisterSipAccount = true;
                            preference.saveCurrentSipSuid(iCamGetSipInfoBean.suid);
                            awakeCamera();
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

    //发命令唤醒摄像头
    private void awakeCamera() {
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", Preference.getPreferences().getCurrentGatewayID());
            object.put("devID", lockDeviceId);
            object.put("clusterId", 0x0101);
            object.put("commandType", 1);
            object.put("commandId", 0x801B);
            object.put("endpointNumber", 1);
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showConfigDialog() {
        builder = new WLDialog.Builder(this);
        builder.setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setMessage(getString(R.string.Device_Vidicon_WiFiConnection))
                .setPositiveButton(getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        configDialog.dismiss();
                        Intent intent = new Intent();
                        intent.setClass(DeviceBnCameraActivity.this, DevBnWifiConfigActivity.class);
                        intent.putExtra("gwID", device.gwID);
                        intent.putExtra("devID", lockDeviceId);
                        DeviceBnCameraActivity.this.startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onClickNegative(View view) {
                        needConfigWifi = true;
                        configDialog.dismiss();
//                        finish();

                    }
                });
        if (configDialog == null) {
            configDialog = builder.create();
            if (!configDialog.isShowing()) {
                configDialog.show();
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(IPCcameraXmlMsgEvent event) {
        if (event.getCode() != 0) {
            SipDataReturn(false, event.getApiType(), event.getMessage(),
                    event.getDestURI(), String.valueOf(event.getCode()));
        } else {
            SipDataReturn(true, event.getApiType(), event.getMessage(),
                    event.getDestURI(), String.valueOf(event.getCode()));
        }
    }

    protected void SipDataReturn(boolean isSuccess, IPCMsgApiType apiType,
                                 String xmlData, String from, String code) {
        if (isSuccess) {
            switch (apiType) {
                case QUERY_DEVICE_DESCRIPTION_INFO:
                    DeviceDetailMsg detailMsg = XmlHandler
                            .getDeviceDetailMsg(xmlData);
                    break;
            }
        }
    }

    /**
     * 心跳
     */
    private Runnable pingTask = new Runnable() {
        @Override
        public void run() {
            IPCMsgController.MsgWulianBellQueryNotifyHeartBeat(cameraId, iCamGetSipInfoBean.deviceDomain, 30);
            handler.postDelayed(this, PING_INTERVAL);
        }
    };

    private Runnable requestSpeedTask = new Runnable() {
        @Override
        public void run() {
            IPCController.getCallSpeedInfo();
            handler.postDelayed(this, SHOW_SPEED_INTERVAL);
        }
    };
    private Runnable reWakeTask = new Runnable() {
        @Override
        public void run() {
            awakeAndCall();
            handler.postDelayed(this, SHOW_SPEED_INTERVAL);
        }
    };

    private void showSpeed(String speedInfo) {
        if (!TextUtils.isEmpty(speedInfo)) {
            long dataSize = 0;
            long delatDataSize = 0;
            try {
                dataSize = Long.parseLong(speedInfo);
                delatDataSize = dataSize - saveReceivedDataSize;
                delatDataSize = (delatDataSize > 0 ? delatDataSize : 0)
                        / (SHOW_SPEED_INTERVAL / 1000);
                saveReceivedDataSize = dataSize;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            long speed = delatDataSize / 1000;
            tv_network_speed.setText("" + (speed > 0 ? speed : 0) + "KB/s");
        }
    }

    private void setRadioOpen(boolean isOpen) {
        this.isRadioOpen = isOpen;
        if (isOpen) {
            btn_sound_switch.setImageResource(R.drawable.icon_cateye_sound_on);
            IPCController.playAudioAsync(new IPCResultCallBack() {
                @Override
                public void getResult(int i) {
                    WLog.i("playAudioAsync result:" + i);
                }
            });
        } else {
            btn_sound_switch.setImageResource(R.drawable.icon_cateye_sound_off);
            IPCController.stopPlayAudioAsync(new IPCResultCallBack() {
                @Override
                public void getResult(int i) {
                    WLog.i("stopPlayAudioAsync result:" + i);
                }
            });
        }
    }

    /**
     * 视频背景显示上一次退出时的截图
     */
    private void showSnapshot() {
        String snapshotPath = FileUtil.getLastFramePath();
        String fileName = cameraId + ".jpg";
        String path = snapshotPath + "/" + fileName;
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                view_video.setBackground(new BitmapDrawable(getResources(), bitmap));
                return;
            }
        }
        view_video.setBackgroundResource(R.drawable.camera_default_bg3);
    }

    private void showLastSnapshot() {
        String savePath = FileUtil.getSnapshotPath() + "/" + lockDeviceId;
        File savePathFile = new File(savePath);
        String[] bmpFiles = savePathFile.list();
        if (bmpFiles != null && bmpFiles.length > 0) {
            String bmpFile = bmpFiles[bmpFiles.length - 1];
            Bitmap bitmap = BitmapFactory.decodeFile(savePath + "/" + bmpFile);
            iv_snapshot.setImageBitmap(bitmap);
        } else {
            iv_snapshot.setImageResource(R.drawable.eques_snapshot_default);
        }
    }

    private void saveBitmap(final Bitmap bitmap) {
        if (bitmap != null) {
            String savePath = FileUtil.getSnapshotPath() + "/" + lockDeviceId;
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
            if (soundPool != null) {
                soundPool.play(snapshot_sound_id, 1.0f, 1.0f, 0, 0, 1);
            }
        }
    }

    /**
     * 设置loadingview 状态
     *
     * @param state 0 loading，1 断开，2 播放
     */
    private void updateLoadingState(int state) {
        videoPlayState = state;
        if (state == 0) {
            layout_video_reload.setVisibility(View.GONE);
            layout_video_loading.setVisibility(View.VISIBLE);
            layout_video_offline.setVisibility(View.GONE);
        } else if (state == 1) {
            layout_video_reload.setVisibility(View.VISIBLE);
            layout_video_loading.setVisibility(View.GONE);
            layout_video_offline.setVisibility(View.GONE);
            handler.removeCallbacks(connectRunnable);
        } else if (state == 2) {
            layout_video_reload.setVisibility(View.GONE);
            layout_video_loading.setVisibility(View.GONE);
            layout_video_offline.setVisibility(View.GONE);
        } else if (state == 3) {
            layout_video_reload.setVisibility(View.GONE);
            layout_video_loading.setVisibility(View.GONE);
            layout_video_offline.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(IPCCallStateMsgEvent event) {
        MsgCallState callState = MsgCallState.getMsgCallState(event
                .getCallState());
        switch (callState) {
            case STATE_ESTABLISHED:
                retryCount = 0;
                handler.removeCallbacks(connectRunnable);
                handler.removeCallbacks(reWakeTask);
                break;
            case STATE_TERMINATED:
                tv_network_speed.setText("0KB/s");
                handler.removeCallbacks(requestSpeedTask);
                handler.removeCallbacks(pingTask);
                break;
            case STATE_VIDEO_INCOMING:
                updateLoadingState(2);
                view_video.setBackground(null);
                llNetSpeed.setVisibility(View.VISIBLE);
                handler.removeCallbacks(requestSpeedTask);
                handler.removeCallbacks(connectRunnable);
                handler.removeCallbacks(autoHangUpRunnable);
                handler.postDelayed(requestSpeedTask, SHOW_SPEED_INTERVAL);
                handler.postDelayed(autoHangUpRunnable, AUTO_HANG_UP_INTERVAL);
                if (!hasGetBattery) {
                    hasGetBattery = true;
                    getLockBattery();
                }
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(IPCOnReceivedMsgEvent event) {
        switch (MsgReceivedType.getMsgReceivedTypeByID(event.getRtcType())) {
            case HANDLE_RTC_CALL_SPEED_TYPE:
                showSpeed(event.getMessage());
                break;
            case HANDLE_RTC_CALL_DQ_TYPE:
                WLog.i("##处理DQ信息");
                WLog.i("DQ信息-->" + event.getMessage());
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
                Bitmap bitmap = event.getmVideoBitmap();
                if (bitmap != null && !bitmap.isRecycled()) {
                    String savePath = FileUtil.getLastFramePath();
                    String fileName = cameraId + ".jpg";
                    File savePathFile = new File(savePath);
                    if (!savePathFile.exists()) {
                        savePathFile.mkdirs();
                    }
                    FileUtil.saveBitmapToJpeg(event.getmVideoBitmap(), savePath, fileName);
                    EventBus.getDefault().post(new LastFrameEvent(cameraId, savePath + "/" + fileName));
                }
                break;
            case FRAME_PLAY_THUMBNAIL:

                WLog.i("#Thread-->" + Thread.currentThread().getName());
                WLog.i("收到抓拍图片");
                if (event.getmVideoBitmap() == null) {
                    WLog.i("抓拍图片为空");
                } else {
                    saveBitmap(event.getmVideoBitmap());
                }
                break;

        }
    }

    private void dealData(String data) {
        if (!TextUtils.isEmpty(data)) {
            try {
                JSONObject object = new JSONObject(data);
                JSONArray endpoints = object.getJSONArray("endpoints");
                JSONArray clusters = endpoints.getJSONObject(0).getJSONArray("clusters");
                JSONArray attributes = clusters.getJSONObject(0).getJSONArray("attributes");
                String attributeValue = attributes.getJSONObject(0).getString("attributeValue");
                int attributeId = attributes.getJSONObject(0).getInt("attributeId");
                // 更新状态
                updateState(attributeId, attributeValue, data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateState(int attributeId, String attributeValue, String data) {
        WLog.i(TAG, "attributeValue = " + attributeValue);
        if (attributeValue.isEmpty()) {
            return;
        }

        int value = 0;
        try {
            value = StringUtil.toInteger(attributeValue);
        } catch (Exception ex) {
            WLog.i(PROCESS, "转Int失败:attributeValue=" + attributeValue);
        }

        WLog.i(PROCESS, attributeId + " " + attributeValue);

        switch (attributeId) {
            case 0x8008: {//设置返回
                if (value == 4) {//开启摄像头成功
                    LogUtil.WriteBcLog("开启摄像头成功");
                    if (isSipInfoIntegral()) {
                        handler.postDelayed(pingTask, 1000);
                    }
                    if (isRingCall) {
                        isFirstCall = false;
                    }
                } else if (value == 5) {//开启摄像头失败
                    dealOpenFailErrorCode(data);
                } else if (value == 6) {//关闭摄像头成功

                } else if (value == 7) {//关闭摄像头失败

                } else if (value == 11) {//上报摄像头注册成功
                    LogUtil.WriteBcLog("上报摄像头注册成功");
                    isFirstCall = false;
                } else if (value == 17) {
                    LogUtil.WriteBcLog("上报摄像机的wifi连接状态: value = " + value);
                }
            }
            break;
            case 0x8005:
                updateBatteryLevel(attributeValue);
                break;
        }
    }

    private void updateBatteryLevel(String attributeValue) {
        String battery = null;
        if (attributeValue.length() > 9 && attributeValue.startsWith("1")) {
            battery = attributeValue.substring(7, 9);
            hasGetBattery = true;
        }
        switch (battery) {
            case "00":
                ivBattery.setImageResource(R.drawable.icon_battery_por_0);
                break;
            case "01":
                ivBattery.setImageResource(R.drawable.icon_battery_por_1);
                break;
            case "02":
                ivBattery.setImageResource(R.drawable.icon_battery_por_2);
                break;
            case "03":
                ivBattery.setImageResource(R.drawable.icon_battery_por_3);
                break;
        }
    }

    private void dealOpenFailErrorCode(String data) {
        updateLoadingState(1);
        String extData = null;
        try {
            JSONObject object = new JSONObject(data);
            extData = object.optString("extData");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(extData)) {
            tvReloadTips.setText(getString(R.string.Camera_Voltage_low_05));
        } else {
            switch (extData) {
                case "01":
                    tvReloadTips.setText(getString(R.string.bc_video_failure_emergency_power));
                    break;
                case "02":
                    tvReloadTips.setText(getString(R.string.bc_video_failure_Low_voltage));
                    break;
                case "03":
                    tvReloadTips.setText(getString(R.string.bc_video_failure_no_wifi));
                    break;

                default:
                    tvReloadTips.setText(getString(R.string.Camera_Voltage_low_05));
                    break;
            }
        }

    }


    private void setRender() {
        if (isFirstCreate) {
            isFirstCreate = false;
            LogUtil.WriteBcLog("设置渲染器");
            IPCController.setRender("", view_video);
            if (!TextUtils.isEmpty(iCamGetSipInfoBean.deviceDomain)) {
                IPCController.setRenderFlag(iCamGetSipInfoBean.deviceDomain);
            }
        }
    }

    private void makeCall() {
        setRender();
        IPCController.closeAllVideoAsyncRefresh(null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                IPCController.makeCallAsync(new IPCResultCallBack() {
                    @Override
                    public void getResult(final int i) {
                        LogUtil.WriteBcLog("发起视频呼叫: " + i);
                        if (i != 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isFirstCall) {
                                        if (i != -1 && i != 4) {
                                            updateLoadingState(1);
                                        } else if (i == 4 && registerExpTime < 5) {
                                            LogUtil.WriteBcLog("账号注册异常重新注册" + registerExpTime);
                                            registerExpTime++;
                                            startRegister();
                                        } else if (i == 4 && registerExpTime == 5) {
                                            LogUtil.WriteBcLog("账号注册异常重新注册次数超过5次需手动刷新" + registerExpTime);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ToastUtil.show(getString(R.string.Camera_sip_fail));
                                                    updateLoadingState(1);
                                                }
                                            });
                                            registerExpTime = 0;
                                        }
                                    }
                                }
                            });
                        }
                    }
                }, cameraId, iCamGetSipInfoBean.deviceDomain);
            }
        }, 500);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && lockDeviceId != null) {
            if (TextUtils.equals(event.device.devID, lockDeviceId)) {
                //处理门锁离线
                if (event.device.isOnLine()) {
                    if (videoPlayState == 3) {
                        updateLoadingState(0);
                        startWork();
                    }
                } else {
                    updateLoadingState(3);
                }
                //处理门锁改名
                setTitleText(DeviceInfoDictionary.getNameByTypeAndName(event.device.type, event.device.name));
                //处理门锁开启摄像头
                if (event.device.mode == 1) {
                    dealData(event.device.data);
                } else if (event.device.mode == 0) {
                    dealData(event.device.data);
                }
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCateyeDoorbellEvent(CateyeDoorbellEvent event) {
        //猫眼视频播放界面播放时来门铃，则退出
        if (event.cateyeDoorbellBean != null) {
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetworkInfoEvent(NetworkInfoEvent event) {
        if (event.networkInfo.isConnected()) {
            if (videoPlayState != 2 && !isShowLimitsDialog) {
                getBindResult();
            }
        } else {
            updateLoadingState(1);
        }
    }

}
