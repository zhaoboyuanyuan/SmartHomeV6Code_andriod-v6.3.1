package cc.wulian.smarthomev6.main.device.cateye;

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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.CateyeStatusEntity;
import cc.wulian.smarthomev6.entity.DeviceDetailMsg;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.UnlockDialogActivity;
import cc.wulian.smarthomev6.main.device.cateye.album.AlbumGridActivity;
import cc.wulian.smarthomev6.main.device.cateye.setting.CateyeSettingActivity;
import cc.wulian.smarthomev6.main.home.scene.SceneListDialogActivity;
import cc.wulian.smarthomev6.main.message.alarm.ICamAlarmActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.ICamCloudApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceRelationBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceRelationListBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamDeviceBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamGetSipInfoBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.event.CateyeDoorbellEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.LastFrameEvent;
import cc.wulian.smarthomev6.support.event.NetworkInfoEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.CameraUtil;
import cc.wulian.smarthomev6.support.utils.DisplayUtil;
import cc.wulian.smarthomev6.support.utils.FileUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.VibratorUtil;
import cc.wulian.smarthomev6.support.utils.WLog;
import cc.wulian.smarthomev6.support.utils.XmlHandler;

/**
 * Created by zbl on 2017/5/4.
 * 猫眼详情界面
 */

public class CateyeDetailActivity extends BaseTitleActivity {

    private static final String KEY_ICAM_DEVICE_BEAN = "icam_device_bean";
    private static final String PROCESS = "icamProcess";

    private static final String PERMISSION_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    private static final int PERMISSION_REQUEST_CODE = 1;

    private static final int SHOWSPEED_INTERVAL = 3000;// 速度间隔为3秒
    private static final int PING_INTERVAL = 10 * 1000;//心跳间隔为10秒

    private DeviceApiUnit deviceApiUnit;
    private ICamCloudApiUnit iCamCloudApiUnit;

    private ICamDeviceBean iCamDeviceBean;
    private ICamGetSipInfoBean iCamGetSipInfoBean;
    private String deviceId;

    private FrameLayout layout_video_container;
    private FrameLayout main_container;
    private View layout_video_loading, layout_video_reload, layout_video_offline;
    private TextView tv_network_speed, tv_hold_speek;
    private ViEAndroidGLES20 view_video;
    private SoundPool soundPool;
    private int snapshot_sound_id;

    private View btn_snapshot;
    private ImageView btn_sound_switch, btn_alarmlist, btn_visitor, btn_lock, btn_scene, iv_snapshot;
    private View btn_hold_speek;
    private ImageView iv_hold_speek;
    private ImageView ivBattery;

    private Handler handler = new Handler(Looper.getMainLooper());

    private Bitmap saveLastBitmap;//保存当前图库图片的引用，方便更换的时候回收

    public static boolean hasInit = false;
    private boolean isRadioOpen = false;
    private boolean isFirstCreate = false;
    private boolean isPlayAndRecord = false;
    private long saveReceivedDataSize = 0;
    private int registerExpTime = 0;
    private boolean isShowLimitsDialog = false;
    /**
     * 0 loading，1 断开，2 播放, 3 离线
     */
    private int videoPlayState;

    public boolean isShared = false;

    public static void start(Context context, ICamDeviceBean bean) {
        Intent intent = new Intent(context, CateyeDetailActivity.class);
        intent.putExtra(KEY_ICAM_DEVICE_BEAN, bean);
        context.startActivity(intent);
    }

    public static void start(Context context, Device bean) {
        Intent intent = new Intent(context, CateyeDetailActivity.class);
        ICamDeviceBean iCamDeviceBean = new ICamDeviceBean();
        iCamDeviceBean.did = bean.devID;
        try {
            JSONObject jsonObject = new JSONObject(bean.data);
            boolean online = bean.isOnLine();
            String name = DeviceInfoDictionary.getNameByDevice(bean);
            if (online) {
                iCamDeviceBean.online = 1;
            } else {
                iCamDeviceBean.online = 0;
            }
            iCamDeviceBean.nick = name;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        intent.putExtra(KEY_ICAM_DEVICE_BEAN, iCamDeviceBean);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cateye_detail, true);
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
        if (videoPlayState != 2 && !isShowLimitsDialog) {
            getSipInfo();
        }
    }

    @Override
    protected void onPause() {
        if (videoPlayState == 2) {
//            view_video.getRenderFrame(IPCGetFrameFunctionType.FRAME_MAIN_THUNBNAIL);
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
//        IPCController.closeAllVideoAsync(null);
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
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        CameraUtil.setHasVideoActivityRunning(false);
        super.onDestroy();
    }


    private void getSipInfo() {
        if (iCamGetSipInfoBean == null) {
            iCamCloudApiUnit.doGetSipInfo(deviceId, true, new ICamCloudApiUnit.IcamApiCommonListener<ICamGetSipInfoBean>() {
                @Override
                public void onSuccess(ICamGetSipInfoBean bean) {
                    if (bean != null) {
                        iCamGetSipInfoBean = bean;
                        initSip();
                    }
                }

                @Override
                public void onFail(int code, String msg) {

                }
            });
        } else {
            initSip();
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
//            initSip();
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
        setToolBarTitleAndRightImg(getString(R.string.Device_Default_Name_CMICA1), R.drawable.icon_cateye_setting);
    }

    @Override
    protected void initView() {
        main_container = (FrameLayout) findViewById(R.id.main_container);
        layout_video_container = (FrameLayout) findViewById(R.id.layout_video_container);
        layout_video_loading = findViewById(R.id.layout_video_loading);
        layout_video_reload = findViewById(R.id.layout_video_reload);
        layout_video_offline = findViewById(R.id.layout_video_offline);
        updateLoadingState(0);
        view_video = new ViEAndroidGLES20(this);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layout_video_container.addView(view_video, 0, layoutParams);
        view_video.setKeepScreenOn(true);

        tv_network_speed = (TextView) findViewById(R.id.tv_network_speed);
        tv_hold_speek = (TextView) findViewById(R.id.tv_hold_speek);
        btn_snapshot = findViewById(R.id.btn_snapshot);
        iv_snapshot = (ImageView) findViewById(R.id.iv_snapshot);
        btn_sound_switch = (ImageView) findViewById(R.id.btn_sound_switch);
        btn_alarmlist = (ImageView) findViewById(R.id.btn_alarmlist);
        btn_visitor = (ImageView) findViewById(R.id.btn_visitor);
        btn_lock = (ImageView) findViewById(R.id.btn_lock);
        btn_scene = (ImageView) findViewById(R.id.btn_scene);
        btn_hold_speek = findViewById(R.id.btn_hold_speek);
        iv_hold_speek = (ImageView) findViewById(R.id.iv_hold_speek);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
    }

    @Override
    protected void initData() {
        isFirstCreate = true;
        deviceApiUnit = new DeviceApiUnit(this);
        iCamCloudApiUnit = new ICamCloudApiUnit(this);
        Bundle bd = getIntent().getExtras();
        if (bd == null) {
            ToastUtil.show("Bundle is Empty!");
            return;
        }
        iCamDeviceBean = (ICamDeviceBean) bd.getSerializable(KEY_ICAM_DEVICE_BEAN);
        deviceId = iCamDeviceBean.did;

        Device device = MainApplication.getApplication().getDeviceCache().get(deviceId);
        if (device != null) {
            if (!TextUtils.isEmpty(device.name)) {
                setTitleText(DeviceInfoDictionary.getNameByTypeAndName(device.type, device.name));
            }
            updateLoadingState(0);
            //共享设备
            isShared = device.isShared;
        }

        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        snapshot_sound_id = soundPool.load(this, R.raw.snapshot, 1);

        setRadioOpen(isRadioOpen);

        checkPermission();

        showSnapshot();
    }

    @Override
    protected void initListeners() {
        view_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                makeCall();
            }
        });
        btn_snapshot.setOnClickListener(this);
        iv_snapshot.setOnClickListener(this);
        btn_sound_switch.setOnClickListener(this);
        btn_alarmlist.setOnClickListener(this);
        btn_visitor.setOnClickListener(this);
        btn_lock.setOnClickListener(this);
        btn_scene.setOnClickListener(this);
        layout_video_reload.setOnClickListener(this);

        btn_hold_speek.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (videoPlayState == 2) {//播放状态才能点击
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            iv_hold_speek.setImageResource(R.drawable.icon_hold_speek_on);
                            tv_hold_speek.setText(R.string.Cateye_In_Call);
                            btn_sound_switch.setImageResource(R.drawable.icon_cateye_sound_off);
                            IPCController.recordAudioAsync(new IPCResultCallBack() {
                                @Override
                                public void getResult(int i) {
                                    WLog.i("recordAudioAsync result:" + i);
                                    VibratorUtil.holdSpeakVibration();
                                }
                            });
                        }
                        break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL: {
                            iv_hold_speek.setImageResource(R.drawable.icon_hold_speek);
                            tv_hold_speek.setText(R.string.CateEye_Detail_Hold_Speek);
                            if (isRadioOpen) {
                                btn_sound_switch.setImageResource(R.drawable.icon_cateye_sound_off);
                            } else {
                                btn_sound_switch.setImageResource(R.drawable.icon_cateye_sound_on);
                            }
                            IPCController.stopRecordAudioAsync(new IPCResultCallBack() {
                                @Override
                                public void getResult(int i) {
                                    WLog.i("stopRecordAudioAsync result:" + i);
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
        if (videoPlayState != 3) {//离线不能点击
            if (v == btn_snapshot) {
                if (videoPlayState == 2) {
//                    view_video.getRenderFrame(IPCGetFrameFunctionType.FRAME_PLAY_THUMBNAIL);
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
                updateLoadingState(0);
                IPCController.closeAllVideoAsyncRefresh(new IPCResultCallBack() {
                    @Override
                    public void getResult(int i) {
                        if (i == 0) {
                            WLog.i(PROCESS, "关闭视频流并重置呼叫" + i);
                        }
                    }
                });
                makeCall();
            }
        }
        if (v == iv_snapshot) {
            Intent intent = new Intent(this, AlbumGridActivity.class);
            intent.putExtra("devId", deviceId);
            startActivity(intent);
        } else if (v == btn_visitor) {
            CateyeVisitorNewActivity.start(this, deviceId, iCamGetSipInfoBean.deviceDomain);
        } else if (v == btn_alarmlist) {
            ICamAlarmActivity.start(this, deviceId, iCamGetSipInfoBean.deviceDomain, "CMICA1");
        } else if (v.getId() == R.id.img_right) {
            iCamDeviceBean.sdomain = iCamGetSipInfoBean.deviceDomain;
            Intent intent = new Intent(this, CateyeSettingActivity.class);
            intent.putExtra("ICamDeviceBean", iCamDeviceBean);
            startActivityForResult(intent, 1);
        } else if (v == btn_lock) {
//            if (isShared) {
//                ToastUtil.show(R.string.Share_No_Permission);
//            } else {
            getLockData();
//            }
        } else if (v == btn_scene) {
            startActivity(new Intent(this, SceneListDialogActivity.class));
        }
    }

    private void getLockData() {
        deviceApiUnit.doGetBindLock(deviceId, "", new DeviceApiUnit.DeviceApiCommonListener<DeviceRelationListBean>() {
            @Override
            public void onSuccess(DeviceRelationListBean bean) {
                UnlockDialogActivity.startByCondition(CateyeDetailActivity.this, bean, isShared);
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(msg);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            this.finish();
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
        } else if (MainApplication.getApplication().hasInitSip && !TextUtils.equals(preference.getCurrentSipSuid(), iCamGetSipInfoBean.suid)) {
            WLog.i(PROCESS, "摄像机和锁之间切换，需要重新注册sip");
            startRegister();
        } else {
            IPCResultCallBack initRTCAsyncCallback = new IPCResultCallBack() {
                @Override
                public void getResult(int result) {
                    hasInit = result == 0 ? true : false;
                    WLog.i(PROCESS, "未初始化也未注册，此刻开始初始化" + hasInit);
                    if (hasInit) {
                        MainApplication.getApplication().hasInitSip = hasInit;
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
                            WLog.i(PROCESS, "注册成功后唤醒");
                            makeCall();
                        }
                    }, 500);
                }
            }
        };
        String suid = iCamGetSipInfoBean.suid;
        String userSipPwd = iCamGetSipInfoBean.spassword;
        String sdomain = iCamGetSipInfoBean.sipDomain;
        IPCController.registerAccountAsync(ipcResultCallBack, suid, userSipPwd, sdomain);
        IPCMsgController.MsgQueryDeviceDescriptionInfo(deviceId, sdomain);
    }

    private void setRender() {
        if (isFirstCreate) {
            isFirstCreate = false;
            WLog.i(PROCESS, "设置渲染器");
            IPCController.setRender("", view_video);
            IPCController.setRenderFlag(iCamGetSipInfoBean.deviceDomain);
        }
    }


    private void makeCall() {
//        if (videoPlayState != 3) {//离线不启动视频
        iCamCloudApiUnit.doAwakeDevice(deviceId, new ICamCloudApiUnit.IcamApiCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {
                WLog.i(PROCESS, "唤醒猫眼成功");
//                handler.postDelayed(pingTask, 2000);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setRender();
                        IPCController.makeCallAsync(new IPCResultCallBack() {
                            @Override
                            public void getResult(int i) {
                                WLog.i(PROCESS, "唤醒成功后呼叫:" + i);
                                if (i != 0 && i != 4) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            updateLoadingState(1);
                                        }
                                    });

                                } else if (i == 4 && registerExpTime < 5) {
                                    WLog.i(PROCESS, "账号注册异常重新注册" + registerExpTime);
                                    registerExpTime++;
                                    startRegister();
                                } else if (i == 4 && registerExpTime == 5) {
                                    WLog.i(PROCESS, "账号注册异常重新注册次数超过5次需手动刷新" + registerExpTime);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            updateLoadingState(1);
                                        }
                                    });
                                    registerExpTime = 0;
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
//        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(IPCcameraXmlMsgEvent event) {
        if (event.getCode() != 0) {
            SipDataReturn(false, event.getApiType(), event.getMessage(),
                    event.getDestURI(), String.valueOf(event.getCode()));
            Log.i("sip", "fail---" + "apiType = " + event.getApiType() + "msg = " + event.getMessage());
        } else {
            SipDataReturn(true, event.getApiType(), event.getMessage(),
                    event.getDestURI(), String.valueOf(event.getCode()));
            Log.i("sip", "success---" + "apiType = " + event.getApiType() + "msg = " + event.getMessage());
        }
    }

    protected void SipDataReturn(boolean isSuccess, IPCMsgApiType apiType,
                                 String xmlData, String from, String code) {
        if (isSuccess) {
            Log.i("hxc", apiType + "");
            switch (apiType) {
                case QUERY_DEVICE_DESCRIPTION_INFO:
                    DeviceDetailMsg detailMsg = XmlHandler
                            .getDeviceDetailMsg(xmlData);
                    if (detailMsg != null) {
//                        setDeviceInformation(detailMsg);
                    }
                    break;
                case BELL_QUERY_DEVICE_CONFIG_INFORMATION:
                    CateyeStatusEntity cateyeStatusEntity = CameraUtil.getPojoByXmlData(xmlData);
                    Preference.getPreferences().saveCateyeBattery(deviceId, cateyeStatusEntity.getBatteryLevel());
                    setBatteryLevel(cateyeStatusEntity.getBatteryLevel(), ivBattery);
                    break;
            }
        }
        Log.i("hxc", "----baseActivity");
    }

    private void setBatteryLevel(String value, ImageView view) {
        int battery = Integer.parseInt(value);
        if (battery < 25) {
            view.setImageResource(R.drawable.icon_battery_por_0);
        } else if (battery >= 25 && battery < 50) {
            view.setImageResource(R.drawable.icon_battery_por_1);
        } else if (battery >= 50 && battery < 75) {
            view.setImageResource(R.drawable.icon_battery_por_2);
        } else {
            view.setImageResource(R.drawable.icon_battery_por_3);
        }
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
        if (!TextUtils.isEmpty(speedInfo)) {
            long dataSize = 0;
            long delatDataSize = 0;
            try {
                dataSize = Long.parseLong(speedInfo);
                delatDataSize = dataSize - saveReceivedDataSize;
                delatDataSize = (delatDataSize > 0 ? delatDataSize : 0)
                        / (SHOWSPEED_INTERVAL / 1000);
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
                    WLog.i("playAudioAsync resalt:" + i);
                }
            });
        } else {
            btn_sound_switch.setImageResource(R.drawable.icon_cateye_sound_off);
            IPCController.stopPlayAudioAsync(new IPCResultCallBack() {
                @Override
                public void getResult(int i) {
                    WLog.i("stopPlayAudioAsync resalt:" + i);
                }
            });
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
                view_video.setBackground(new BitmapDrawable(getResources(), bitmap));
                return;
            }
        }
        view_video.setBackgroundResource(R.drawable.camera_default_bg2);
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
        } else {
            iv_snapshot.setImageResource(R.drawable.eques_snapshot_default);
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
     * @param state 0 loading，1 断开，2 播放，3 离线
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

    private int retryCount = 0;

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
                tv_network_speed.setText("0KB/s");
                if (retryCount < 3) {
                    updateLoadingState(0);
                    IPCController.closeAllVideoAsyncRefresh(null);
                    makeCall();
                    WLog.i(PROCESS, "sdk挂断重呼");
                    retryCount += 1;
                } else {
                    updateLoadingState(1);
                }
                handler.removeCallbacks(requestSpeedTask);
//                handler.removeCallbacks(pingTask);
                break;
            case STATE_VIDEO_INCOMING:
                updateLoadingState(2);
//                isVideoComing = true;
                WLog.i(PROCESS, "##视频流来了");
                view_video.setBackground(null);
                retryCount = 0;
                IPCMsgController.MsgWulianBellQueryDeviceConfigInformation(iCamDeviceBean.did, iCamGetSipInfoBean.deviceDomain);
                handler.removeCallbacks(requestSpeedTask);
                handler.postDelayed(requestSpeedTask, SHOWSPEED_INTERVAL);
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
                Bitmap bitmap = event.getmVideoBitmap();
                if (bitmap != null && !bitmap.isRecycled()) {
                    String savePath = FileUtil.getLastFramePath();
                    String fileName = deviceId + ".jpg";
                    File savePathFile = new File(savePath);
                    if (!savePathFile.exists()) {
                        savePathFile.mkdirs();
                    }
                    FileUtil.saveBitmapToJpeg(event.getmVideoBitmap(), savePath, fileName);
                    EventBus.getDefault().post(new LastFrameEvent(deviceId, savePath + "/" + fileName));
//                    bitmap.recycle();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && deviceId != null) {
            if (TextUtils.equals(event.device.devID, deviceId)) {
                if (event.device.isOnLine()) {
                    if (videoPlayState == 3) {
//                        updateLoadingState(0);
//                        initSip();
                    }
                } else if (event.device.mode == 3) {
                    finish();
                } else {
//                    updateLoadingState(3);
                }
                setTitleText(DeviceInfoDictionary.getNameByDevice(event.device));
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
                getSipInfo();
            }
        } else {
            updateLoadingState(1);
        }
    }
}
