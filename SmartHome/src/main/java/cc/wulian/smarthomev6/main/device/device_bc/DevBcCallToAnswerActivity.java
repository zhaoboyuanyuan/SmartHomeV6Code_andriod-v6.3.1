package cc.wulian.smarthomev6.main.device.device_bc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.ICamCloudApiUnit;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by yuxx on 2017/6/12.
 * 网络智能锁来电接听
 */

public class DevBcCallToAnswerActivity extends BaseActivity implements View.OnClickListener {
    private static final String KEY_DEST_ID = "DestID";
    private static final String KEY_PICTURE_URL = "pictureURL";
    public static boolean isReceived = false;

    private ICamCloudApiUnit iCamCloudApiUnit;

    private ImageView iv_bg;
    private RelativeLayout layoutCallHangUp;
    private RelativeLayout layoutCallAnswer;
    private TextView txtDevName;
    private TextView txtdesc;
    private Device device;
    private String cameraid;
    private String pictureUrl;
    private String devID;
    private String bucket;
    private String region;
    private MediaPlayer mMediaPlayer;
    private DisplayImageOptions mOptions;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable autoHangUpRunnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devbc_calltoanswer);
        mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.icon_bc_callbg)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        fullImagePath = "";
        initView();
        initData();
        initListeners();
        startRingCall();
    }

    @Override
    protected void onDestroy() {
        stopRingCall();
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    public static void start(Context context, String destID, String pictureURL, String cameraid, String bucket, String region) {
        Intent intent = new Intent(context, DevBcCallToAnswerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_DEST_ID, destID);
        intent.putExtra(KEY_PICTURE_URL, pictureURL);
        intent.putExtra("cameraid", cameraid);
        intent.putExtra("bucket", bucket);
        intent.putExtra("region", region);
        context.startActivity(intent);
    }

    protected void initView() {
        layoutCallHangUp = (RelativeLayout) findViewById(R.id.layoutCallHangUp);
        layoutCallAnswer = (RelativeLayout) findViewById(R.id.layoutCallAnswer);
        txtDevName = (TextView) findViewById(R.id.txtDevName);
        txtdesc = (TextView) findViewById(R.id.txtdesc);
        iv_bg = (ImageView) findViewById(R.id.iv_bg);
    }

    protected void initListeners() {
        layoutCallHangUp.setOnClickListener(this);
        layoutCallAnswer.setOnClickListener(this);
        findViewById(R.id.callhangUp).setOnClickListener(this);
        findViewById(R.id.txtHangUp).setOnClickListener(this);
        findViewById(R.id.callAnswer).setOnClickListener(this);
        findViewById(R.id.txtAnswer).setOnClickListener(this);
    }

    protected void initData() {
        iCamCloudApiUnit = new ICamCloudApiUnit(this);
        if (getIntent() != null) {
            devID = getIntent().getStringExtra(KEY_DEST_ID);
            cameraid = getIntent().getStringExtra("cameraid");
            bucket = getIntent().getStringExtra("bucket");
            region = getIntent().getStringExtra("region");
            pictureUrl = getIntent().getStringExtra(KEY_PICTURE_URL);
            if (!StringUtil.isNullOrEmpty(devID)) {
                device = MainApplication.getApplication().getDeviceCache().get(devID);
            }
        }
        autoHangUpRunnable = new Runnable() {
            @Override
            public void run() {
                stopRingCall();
                finish();
            }
        };
        handler.postDelayed(autoHangUpRunnable, 60 * 1000);
        txtdesc.setText(getString(R.string.Cateyemini_Title_Doorbellcalls) + "...");
        if (device != null) {
            String deviceName = DeviceInfoDictionary.getNameByTypeAndName(device.type, device.name);
            txtDevName.setText(deviceName);
        }
        delayGetImage();
    }

    private void delayGetImage() {
        if (!StringUtil.isNullOrEmpty(pictureUrl)) {
            TimerTask task = new TimerTask() {
                public void run() {
                    DevBcCallToAnswerActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (StringUtil.isNullOrEmpty(fullImagePath)) {
                                showBackground();
                            } else {
                                ImageLoader.getInstance().displayImage(fullImagePath, iv_bg, mOptions, listener);
                            }
                        }
                    });
                }
            };
            Timer timer = new Timer();
            timer.schedule(task, 1000L);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutCallHangUp:
            case R.id.callhangUp:
            case R.id.txtHangUp: {/*挂断时关闭*/
                handler.removeCallbacks(autoHangUpRunnable);
                isReceived = false;
                stopRingCall();
                finish();
            }
            break;
            case R.id.layoutCallAnswer:
            case R.id.callAnswer:
            case R.id.txtAnswer: {
                handler.removeCallbacks(autoHangUpRunnable);
                stopRingCall();
                /*接听跳转到视频界面*/
                isReceived = true;
                if (StringUtil.isNullOrEmpty(cameraid)) {
                    ToastUtil.single(R.string.Device_Information_Nodistributionnetwork);
                    return;
                }
                if (device != null) {
                    DeviceBcCameraActivity.start(DevBcCallToAnswerActivity.this, device.devID, cameraid, true);
                    finish();
                }
            }
            break;
        }
    }

    private String fullImagePath = "";

    private void showBackground() {
        if (!TextUtils.isEmpty(pictureUrl) && pictureUrl.length() >= 8) {
            String strPre = pictureUrl.substring(0, 8);
            String strLast = pictureUrl.substring(8);
            String picUrl = strPre + "/" + cameraid + "/" + strLast + ".jpg";
            iCamCloudApiUnit.doGetRingPic(picUrl, bucket, region, new ICamCloudApiUnit.IcamApiCommonListener<String>() {
                @Override
                public void onSuccess(String bean) {
                    WLog.d(TAG, "imagePath=" + bean);
                    fullImagePath = bean;
                    ImageLoader.getInstance().displayImage(fullImagePath, iv_bg, mOptions, listener);
                }

                @Override
                public void onFail(int code, String msg) {
                    WLog.d(TAG, "getImageFailed code=" + code + " msg=" + msg);
                    delayGetImage();
                }
            });
        }
    }

    ImageLoadingListener listener = new ImageLoadingListener() {
        @Override
        public void onLoadingStarted(String imageUri, View view) {

        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//            iv_bg.setImageResource(R.drawable.icon_bc_callbg);
            delayGetImage();
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            iv_bg.setImageResource(R.drawable.icon_bc_callbg);
        }
    };

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

    private void stopRingCall() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer = null;
        }
    }
}
