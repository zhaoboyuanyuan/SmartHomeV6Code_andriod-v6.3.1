package cc.wulian.smarthomev6.main.device.device_CG27;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.cloudrtc.service.PjSipService;
import com.cloudrtc.sipsdk.SipApCallPeer;
import com.cloudrtc.sipsdk.SipCallConnectedListener;
import com.cloudrtc.sipsdk.SipCallDisConnectListener;
import com.cloudrtc.sipsdk.SipIncomingListener;
import com.cloudrtc.sipsdk.SipOutgoingListener;
import com.cloudrtc.sipsdk.SipRegisterListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.device.device_CG27.setting.CG27SettingActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.legrand.EntranceGuardAddressBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.legrand.EntranceGuardHostInfo;
import cc.wulian.smarthomev6.support.core.apiunit.bean.legrand.EntranceGuardRegisterBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.utils.CameraUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

public class VideoScreenActivity extends BaseTitleActivity implements SipCallConnectedListener, SipCallDisConnectListener, SipRegisterListener, SipIncomingListener, SipOutgoingListener {
    private PowerManager.WakeLock wakeLock;
    private VideoTalkFragment videoTalkFragment;
    private VideoRingCallFragment videoRingCallFragment;
    private Handler handler = new Handler();
    private DeviceApiUnit deviceApiUnit;
    private Device device;

    private String communityId;
    private String uc;
    private String mainUc;
    private String deviceId;
    private boolean inVideoTalking;
    private boolean isRing;
    private String deviceSipNumber;


    public static void startRing(Context context, String communityId, String uc, String mainUc) {
        Intent intent = new Intent(context, VideoScreenActivity.class);
        intent.putExtra("communityId", communityId);
        intent.putExtra("uc", uc);
        intent.putExtra("mainUc", mainUc);
        intent.putExtra("isRing", true);
        context.startActivity(intent);
    }

    public static void start(Context context, String communityId, String uc) {
        Intent intent = new Intent(context, VideoScreenActivity.class);
        intent.putExtra("communityId", communityId);
        intent.putExtra("uc", uc);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (PjSipService.isready()) {
            PjSipService.instance().setSipCallConnectedListener(this);
            PjSipService.instance().setSipCallDisConnectListener(this);
        }
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        wakeLock = ((PowerManager) getSystemService(POWER_SERVICE))
                .newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                        | PowerManager.ON_AFTER_RELEASE, "WakeLockActivity");
        setContentView(R.layout.activity_video_screen, false);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initData() {
        super.initData();
        communityId = getIntent().getStringExtra("communityId");
        uc = getIntent().getStringExtra("uc");
        mainUc = getIntent().getStringExtra("mainUc");
        isRing = getIntent().getBooleanExtra("isRing", false);
        deviceId = communityId + uc;
        deviceApiUnit = new DeviceApiUnit(this);
        device = mainApplication.getDeviceCache().get(deviceId);
        if (TextUtils.isEmpty(mainUc)) {
            getHostUc();
        } else {
//            PjSipService.instance().SetLoudspeakerStatus(true);
//            PjSipService.instance().StopVideoChannel();
//            showRingVideoView();
            PjSipService.instance().SetLoudspeakerStatus(true);
            PjSipService.instance().StopVideoChannel();
            showVideoView();
        }
        if (device != null) {
            setTitleText(DeviceInfoDictionary.getNameByTypeAndName(device.type, device.name));
        }
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitleAndRightImg(getString(R.string.Device_Default_Name_CG27), R.drawable.icon_cateye_setting);
    }

    /**
     * 获取主机uc信息
     */
    private void getHostUc() {
        //获取社区的地址信息
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
                                deviceSipNumber = bean.getCommunityAddressVO().getDeviceSipNumber();
                                Log.i(TAG, "sip账号: " + deviceSipNumber);
                                registerSip();
                            }
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            ToastUtil.show(msg);
                        }
                    });
                }

            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    private void registerSip() {
        PjSipService.instance().loadConfig(false, null, null, null, null, "H264", "PCMA");
        PjSipService.instance().setSipRegisterListener(this);
        PjSipService.instance().setSipIncomingListener(this);
        PjSipService.instance().setSipOutgoingListener(this);
        if (PjSipService.instance().IsRegistered()) {
            PjSipService.instance().UnRegisterSipAccount();
        }
//        deviceApiUnit.entranceGuardSipRegister(communityId, mainUc, new DeviceApiUnit.DeviceApiCommonListener<EntranceGuardRegisterBean>() {
//            @Override
//            public void onSuccess(EntranceGuardRegisterBean bean) {
//                Log.i("hxc", "account: " + bean.getSipNumber() + ",sipPassword:" + bean.getPassword() + ",serverIp:" + bean.getSipServerIp());
//                PjSipService.instance().RegisterSipAccount(bean.getSipNumber(), bean.getPassword(), bean.getSipServerIp(), "udp");
//            }
//
//            @Override
//            public void onFail(int code, String msg) {
//                ToastUtil.show(msg);
//            }
//        });

        PjSipService.instance().RegisterSipAccount("1001", "2345", "112.74.130.67", "udp");

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (wakeLock != null) {
            wakeLock.release();
        }
        if (inVideoTalking) {
            PjSipService.instance().StopVideoChannel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (wakeLock != null) {
            wakeLock.acquire();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CameraUtil.setHasVideoActivityRunning(false);
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            this.finish();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.img_right:
                startActivityForResult(new Intent(this, CG27SettingActivity.class)
                        .putExtra("communityId", communityId).putExtra("uc", uc), 1);
                break;
        }
    }

    @Override
    public void onCallConnected(int i, SipApCallPeer sipApCallPeer) {
        Log.i(TAG, "onCallConnected: ");
        PjSipService.instance().SetLoudspeakerStatus(true);
        PjSipService.instance().StopVideoChannel();
        showVideoView();
    }

    @Override
    public void onCallDisConnect(int i) {

//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                finish();
//            }
//        }, 1000);
    }

    private void showRingVideoView() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (videoRingCallFragment == null) {
            videoRingCallFragment = new VideoRingCallFragment();
            Bundle bundle = new Bundle();
            bundle.putString("communityId", communityId);
            bundle.putString("uc", uc);
            bundle.putString("mainUc", mainUc);
            videoRingCallFragment.setArguments(bundle);
            transaction.replace(R.id.layout_fl, videoRingCallFragment);
            transaction.commit();
            inVideoTalking = true;
        }
    }

    private void showVideoView() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (videoTalkFragment == null) {
            videoTalkFragment = new VideoTalkFragment();
            Bundle bundle = new Bundle();
            bundle.putString("communityId", communityId);
            bundle.putString("uc", uc);
            bundle.putString("mainUc", mainUc);
            videoTalkFragment.setArguments(bundle);
            transaction.replace(R.id.layout_fl, videoTalkFragment);
            transaction.commit();
            inVideoTalking = true;
        }

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
        Log.i(TAG, "注册中");
    }

    @Override
    public void OnRegistrationSuccess(int i) {
        Log.i(TAG, "注册成功");
        PjSipService.instance().MakeCall("7550006801001010000201", true);
    }

    @Override
    public void OnRegisterationFailed(int i, int i1, String s) {
        Log.i(TAG, "注册失败");
    }

    @Override
    public void OnRegistrationCleared(int i) {
        Log.i(TAG, "未注册");
    }

    @Override
    public void onCallIncoming(SipApCallPeer sipApCallPeer) {

    }

    @Override
    public void onCallOutgoing(SipApCallPeer sipApCallPeer) {

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
