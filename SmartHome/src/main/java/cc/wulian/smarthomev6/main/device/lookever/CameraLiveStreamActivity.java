package cc.wulian.smarthomev6.main.device.lookever;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.classic.ijkplayer.VideoControllerView;
import com.classic.ijkplayer.widget.IjkVideoView;
import com.wulian.sdk.android.ipc.rtcv2.IPCMsgController;

import org.json.JSONException;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.lookever.setting.LookEverSettingActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.ICamCloudApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.LiveStreamAddressBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamDeviceBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamGetSipInfoBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.utils.SizeUtil;
import cc.wulian.smarthomev6.support.utils.WLog;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;


public class CameraLiveStreamActivity extends BaseTitleActivity implements View.OnClickListener {
    private static final String TAG = "CameraLiveStreamActivity";

    private String mVideoPath;

    private VideoControllerView mVideoControllerView;
    private IjkVideoView mVideoView;
    private RelativeLayout layout_video_container;
    private LinearLayout ll_bottom;
    private ImageView ivLandscape;
    private ImageView ivPortrait;
    private View layout_portrait;
    private View layout_landscape;
    private TextView tvTips;

    private boolean mBackPressed;
    private DataApiUnit dataApiUnit;
    private ICamDeviceBean iCamDeviceBean;
    private ICamGetSipInfoBean iCamGetSipInfoBean;
    private Handler handler;


    public static void start(Context context, Device bean) {
        Intent intent = new Intent(context, CameraLiveStreamActivity.class);
        ICamDeviceBean iCamDeviceBean = new ICamDeviceBean();
        iCamDeviceBean.did = bean.devID;
        if (bean.devID.startsWith("CG") && bean.devID.length() >= 11) {
            iCamDeviceBean.uniqueDeviceId = bean.devID.substring(0, 11);
        } else {
            iCamDeviceBean.uniqueDeviceId = bean.devID;
        }
        try {
            JSONObject jsonObject = new JSONObject(bean.data);
            boolean online = jsonObject.getBoolean("onLine");
            iCamDeviceBean.nick = jsonObject.getString("name");
            if (online) {
                iCamDeviceBean.online = 1;
            } else {
                iCamDeviceBean.online = 0;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        intent.putExtra("icam_device_bean", iCamDeviceBean);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookever_new, true);
//        mVideoControllerView = new VideoControllerView(this, false);
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

    }

    @Override
    protected void initView() {
        super.initView();
        layout_video_container = (RelativeLayout) findViewById(R.id.layout_video_container);
        ll_bottom = (LinearLayout) findViewById(R.id.ll_bottom);
        ivLandscape = (ImageView) findViewById(R.id.iv_landscape);
        ivPortrait = (ImageView) findViewById(R.id.iv_portrait);
        layout_portrait = findViewById(R.id.layout_portrait);
        layout_landscape = findViewById(R.id.layout_landscape);
        tvTips = (TextView) findViewById(R.id.tvTips);

        mVideoView = new IjkVideoView(this);
        DisplayMetrics displayMetrics = SizeUtil.getScreenSize(getApplicationContext());
        int deviceWidth = displayMetrics.widthPixels;
        int cameraPreviewWidth = deviceWidth;// 根据布局中的上下比例
        int cameraPreviewHeight = (int) ((float) cameraPreviewWidth
                * 9 / 16);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(cameraPreviewWidth, cameraPreviewHeight);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout_video_container.addView(mVideoView, 0, layoutParams);
    }

    @Override
    protected void initData() {
        super.initData();
//        mVideoPath = "http://live.hkstv.hk.lxdns.com/live/hks/playlist.m3u8";
        dataApiUnit = new DataApiUnit(this);
        Bundle bd = getIntent().getExtras();
        if (bd == null) {
            return;
        }
        iCamDeviceBean = (ICamDeviceBean) bd.getSerializable("icam_device_bean");
        iCamDeviceBean.isRtmp = 1;
        handler = new Handler();
        getSipInfo();
        getStreamUrl();
        mVideoView.setDataBackListener(new IjkVideoView.ConnectDataBackListener() {
            @Override
            public void onSucccess(int i) {
                WLog.i(TAG, "onSucccess: " + i);
                if (i == 3) {
                    tvTips.setVisibility(View.GONE);
                    handler.removeCallbacks(null);
                }
            }

            @Override
            public void onFail(int i) {
                WLog.i(TAG, "onFail: " + i);
                if (i == -10000) {
//                    tvTips.setText(getString(R.string.Camera_Hint_ReadFail));
                    mVideoView.setVideoPath(mVideoPath);
                    mVideoView.start();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tvTips.setText(getString(R.string.Camera_Hint_ReadFail));
                        }
                    },5000);
                }
            }
        });

        Device device = MainApplication.getApplication().getDeviceCache().get(iCamDeviceBean.did);
        if (device != null) {
            setTitleText(DeviceInfoDictionary.getNameByTypeAndName(device.type, device.name));
        }
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitleAndRightImg(getString(R.string.Lookever), R.drawable.icon_cateye_setting);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        ivLandscape.setOnClickListener(this);
        ivPortrait.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.start();
    }

    /**
     * 获取视频流地址
     *
     * @return
     */
    private void getStreamUrl() {
        dataApiUnit.doGetLiveStreamAddress(iCamDeviceBean.did, new DataApiUnit.DataApiCommonListener<LiveStreamAddressBean>() {
            @Override
            public void onSuccess(LiveStreamAddressBean bean) {
                mVideoPath = bean.video.flv;
                if (mVideoPath != null) {
                    mVideoPath = mVideoPath.replace("https", "http");
                    mVideoView.setVideoPath(mVideoPath);
                } else {
                    finish();
                    return;
                }
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    private void getSipInfo() {
        if (iCamGetSipInfoBean == null) {
            ICamCloudApiUnit iCamCloudApiUnit = new ICamCloudApiUnit(this);
            iCamCloudApiUnit.doGetSipInfo(iCamDeviceBean.did, true, new ICamCloudApiUnit.IcamApiCommonListener<ICamGetSipInfoBean>() {
                @Override
                public void onSuccess(ICamGetSipInfoBean bean) {
                    if (bean != null) {
                        iCamGetSipInfoBean = bean;
                    }
                }

                @Override
                public void onFail(int code, String msg) {

                }
            });
        }
    }

    /**
     * 进入全屏
     */
    private void performFullscreen() {
        getmToolBarHelper().setToolBarVisible(false);
        ll_bottom.setVisibility(View.GONE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(params);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        layout_portrait.setVisibility(View.GONE);
        layout_landscape.setVisibility(View.VISIBLE);

        DisplayMetrics displayMetrics = SizeUtil.getScreenSize(getApplicationContext());
        int cameraPreviewHeight = (int) ((float) displayMetrics.widthPixels
                * 9 / 16);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(displayMetrics.widthPixels, cameraPreviewHeight);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mVideoView.setLayoutParams(layoutParams);

    }


    /**
     * 退出全屏
     */
    private void exitFullscreen() {
        getmToolBarHelper().setToolBarVisible(true);
        ll_bottom.setVisibility(View.VISIBLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(params);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        layout_portrait.setVisibility(View.VISIBLE);
        layout_landscape.setVisibility(View.GONE);

        DisplayMetrics displayMetrics = SizeUtil.getScreenSize(getApplicationContext());
        int deviceWidth = displayMetrics.widthPixels;
        int cameraPreviewWidth = deviceWidth;// 根据布局中的上下比例
        int cameraPreviewHeight = (int) ((float) cameraPreviewWidth
                * 9 / 16);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(cameraPreviewWidth, cameraPreviewHeight);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mVideoView.setLayoutParams(layoutParams);
    }


    @Override
    public void onClickView(View v) {
        super.onClickView(v);
        switch (v.getId()) {
            case R.id.iv_landscape:
                performFullscreen();
                break;
            case R.id.iv_portrait:
                exitFullscreen();
                break;
            case R.id.img_right:
                if (iCamDeviceBean != null) {
                    Intent intent = new Intent(this, LookEverSettingActivity.class);
                    iCamDeviceBean.nick = mainApplication.getDeviceCache().get(iCamDeviceBean.did).name;
                    iCamDeviceBean.sdomain = iCamGetSipInfoBean.deviceDomain;
                    intent.putExtra("ICamDeviceBean", iCamDeviceBean);
                    startActivityForResult(intent, 1);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        mBackPressed = true;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            exitFullscreen();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mBackPressed || !mVideoView.isBackgroundPlayEnabled()) {
            mVideoView.stopPlayback();
            mVideoView.release(true);
            mVideoView.stopBackgroundPlay();
        } else {
            mVideoView.enterBackground();
        }
        IjkMediaPlayer.native_profileEnd();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean enableSwipeBack() {
        return false;
    }
}
