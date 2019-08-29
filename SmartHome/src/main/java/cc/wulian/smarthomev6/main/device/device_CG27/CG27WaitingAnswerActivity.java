package cc.wulian.smarthomev6.main.device.device_CG27;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.cloudrtc.service.PjSipService;
import com.cloudrtc.sipsdk.SipApCallPeer;
import com.cloudrtc.sipsdk.SipCallConnectedListener;
import com.cloudrtc.sipsdk.SipRegisterListener;

import java.io.IOException;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.legrand.EntranceGuardAddressBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.legrand.EntranceGuardHostInfo;
import cc.wulian.smarthomev6.support.core.apiunit.bean.legrand.EntranceGuardRegisterBean;
import cc.wulian.smarthomev6.support.utils.CameraUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

public class CG27WaitingAnswerActivity extends BaseTitleActivity implements SipRegisterListener, SipCallConnectedListener {
    private static final String PERMISSION_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private ImageView ivHangUp;
    private ImageView ivCallAnswer;
    private MediaPlayer mMediaPlayer;
    private Handler handler;
    private DeviceApiUnit deviceApiUnit;

    private String communityId;
    private String uc;//分机uc
    private String mainUc;//主机uc

    private Runnable closeTask = new Runnable() {
        @Override
        public void run() {
            finish();
        }
    };

    public static void start(Context context, String communityId, String uc) {
        Intent intent = new Intent(context, CG27WaitingAnswerActivity.class);
        intent.putExtra("communityId", communityId);
        intent.putExtra("uc", uc);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legrand_call_waiting, false);
//        if (PjSipService.isready()) {
//            PjSipService.instance().setSipCallConnectedListener(this);
//            PjSipService.instance().setSipCallDisConnectListener(this);
//        }
    }

    @Override
    protected void initView() {
        super.initView();
        ivHangUp = (ImageView) findViewById(R.id.iv_hang_up);
        ivCallAnswer = (ImageView) findViewById(R.id.iv_call_answer);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        ivCallAnswer.setOnClickListener(this);
        ivHangUp.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        if (PjSipService.isready()) {
            PjSipService.instance().setSipCallConnectedListener(this);
        }
        communityId = getIntent().getStringExtra("communityId");
        uc = getIntent().getStringExtra("uc");
        handler = new Handler();
        deviceApiUnit = new DeviceApiUnit(this);
        checkPermission();
        getHostUc();
        initSip();
        startRingCall();
    }

    private void initSip() {
        PjSipService.instance().loadConfig(false, null, null, null, null, "H264", "PCMA");
        PjSipService.instance().setSipRegisterListener(this);
//        PjSipService.instance().setSipIncomingListener(this);
//        PjSipService.instance().setSipOutgoingListener(this);
        if (PjSipService.instance().IsRegistered()) {
            PjSipService.instance().UnRegisterSipAccount();
        }
//        deviceApiUnit.entranceGuardSipRegister(communityId, mainUc, new DeviceApiUnit.DeviceApiCommonListener<EntranceGuardRegisterBean>() {
//            @Override
//            public void onSuccess(EntranceGuardRegisterBean bean) {
//                Log.i("hxc", "sipNumber: " + bean.getSipNumber() + ",sipPassword:" + bean.getPassword() + ",serverIp:" + bean.getSipServerIp());
//                PjSipService.instance().RegisterSipAccount(bean.getSipNumber(), bean.getPassword(), bean.getSipServerIp(), "udp");
//            }
//
//            @Override
//            public void onFail(int code, String msg) {
//                ToastUtil.show(msg);
//            }
//        });
        PjSipService.instance().RegisterSipAccount("7550006801001010101000", "2345", "112.74.130.67", "udp");

    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(closeTask, 30 * 1000);
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, PERMISSION_CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, PERMISSION_RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{PERMISSION_CAMERA, PERMISSION_RECORD_AUDIO}, PERMISSION_REQUEST_CODE);
        } else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            boolean hasPermission = true;
            if (grantResults != null) {
                for (int grantResult : grantResults) {
                    hasPermission = hasPermission & (grantResult == PackageManager.PERMISSION_GRANTED);
                }
            }
            if (hasPermission) {

            } else {
                // Permission Denied
                ToastUtil.show(R.string.Toast_Permission_Denied);
                finish();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_call_answer:
                handler.removeCallbacks(closeTask);
                PjSipService.instance().AnswerCall(true);
                break;
            case R.id.iv_hang_up:
                remoteHangUp();
                finish();
                break;
        }

    }

    /**
     * 远程开锁
     */
    private void remoteHangUp() {
        DeviceApiUnit deviceApiUnit = new DeviceApiUnit(this);
        deviceApiUnit.remoteHangUp(communityId, mainUc, new DeviceApiUnit.DeviceApiCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {
                Log.i(TAG, "远程挂断成功 ");
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(msg);
            }
        });
    }

    /**
     * 获取主机uc信息
     */
    private void getHostUc() {
        deviceApiUnit.getEntranceGuardAddressInfo(communityId, uc, new DeviceApiUnit.DeviceApiCommonListener<EntranceGuardAddressBean>() {
            @Override
            public void onSuccess(EntranceGuardAddressBean bean) {
                if (bean != null && bean.getCommunityAddressVO() != null) {
                    String dd = bean.getCommunityAddressVO().get(0).getDd();
                    String bb = bean.getCommunityAddressVO().get(0).getBb();
                    String rr = bean.getCommunityAddressVO().get(0).getRr();
                    deviceApiUnit.getEntranceGuardHostInfo(communityId, dd, bb, rr, new DeviceApiUnit.DeviceApiCommonListener<EntranceGuardHostInfo>() {
                        @Override
                        public void onSuccess(EntranceGuardHostInfo bean) {
                            if (bean != null && bean.getCommunityAddressVO() != null) {
                                mainUc = bean.getCommunityAddressVO().getUc();
                            }
                        }

                        @Override
                        public void onFail(int code, String msg) {

                        }
                    });
                }

            }


            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

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

    private void stopRingCall() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRingCall();
        CameraUtil.setHasVideoActivityRunning(false);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        PjSipService.instance().Hangup();
    }

    @Override
    public boolean enableSwipeBack() {
        return false;
    }


    @Override
    public void OnRegistrationProgress(int i) {

    }

    @Override
    public void OnRegistrationSuccess(int i) {
        Log.i("hxc", "注册成功: ");
        PjSipService.instance().SetLoudspeakerStatus(true);
    }

    @Override
    public void OnRegisterationFailed(int i, int i1, String s) {

    }

    @Override
    public void OnRegistrationCleared(int i) {

    }

    @Override
    public void onCallConnected(int i, SipApCallPeer sipApCallPeer) {
        VideoScreenActivity.startRing(this, communityId, uc, mainUc);
    }
}
