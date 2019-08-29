package cc.wulian.smarthomev6.main.device.cateye;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.ICamCloudApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.ShareNoticeBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.customview.FullScreenVideoView;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.dialog.ShareDialog;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.DateUtil;
import cc.wulian.smarthomev6.support.utils.LanguageUtil;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * 作者: chao
 * 时间: 2017/5/18
 * 描述: 报警记录视频播放页
 * 联系方式: 805901025@qq.com
 * updateAuthor:yuxx
 * update time:2017年9月1日13:49:43
 * desc：支持Bc锁报警记录中的MP4
 */

public class BcPlayVideoActivity extends BaseTitleActivity {
    private static final String TAG = "BcPlayVideoActivity";
    private String picUrl;
    private String videoUrl;
    private FullScreenVideoView vv_play;
    private String deviceId;
    private String cameraid;
    private String msg;
    private Long time;
    private ICamCloudApiUnit iCamCloudApiUnit;
    private String sodoMain;
    private TextView viewLoading;
    private boolean isChina;
    private String bucket;
    private String region;
    private WLDialog errorDialog;

    public static void start(Context mcontext, String deviceId, String cameraid, String msg, String picUrl, String videoUrl, long time, String sodomain, String bucket, String region) {
        Intent intent = new Intent();
        intent.setClass(mcontext, BcPlayVideoActivity.class);
        intent.putExtra("deviceId", deviceId);
        intent.putExtra("cameraid", cameraid);
        intent.putExtra("msg", msg);
        intent.putExtra("time", time);
        intent.putExtra("picUrl", picUrl);
        intent.putExtra("videoUrl", videoUrl);
        intent.putExtra("sodoMain", sodomain);
        intent.putExtra("bucket", bucket);
        intent.putExtra("region", region);
        mcontext.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cateye_alarm_play_forbc, true);
        initMyData();
        initMyView();
        StartPlayMp4();

    }

    @Override
    protected void initTitle() {

    }


    @Override
    public boolean enableSwipeBack() {
        return false;
    }

    private ImageView imageView;

    @Override
    protected void initView() {
        imageView = (ImageView) findViewById(R.id.iv_pic);
    }

    private void initMyView() {
        viewLoading = (TextView) findViewById(R.id.viewLoading);
        vv_play = (FullScreenVideoView) findViewById(R.id.vv_play);
        vv_play.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
                WLog.d(TAG, "what=" + what);
                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {/*缓冲开始*/
                    WLog.d(TAG, "缓冲开始...");
                } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {/*缓冲结束*/
                    WLog.d(TAG, "缓冲结束...");
                } else if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {/*渲染了视频的第一帧*/
                    viewLoading.setVisibility(View.GONE);
                    vv_play.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
        vv_play.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
                String errorMessage = null;
                viewLoading.setText("");
                if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
                    //媒体服务器挂掉了。此时，程序必须释放MediaPlayer 对象，并重新new 一个新的。
                    errorMessage = getString(R.string.Introducingvideo_Play_Error);
                } else if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
                    if (extra == MediaPlayer.MEDIA_ERROR_IO) {
                        //文件不存在或错误，或网络不可访问错误
                        errorMessage = getString(R.string.Introducingvideo_Play_Error);
                    } else if (extra == MediaPlayer.MEDIA_ERROR_TIMED_OUT) {
                        //超时
                        errorMessage = getString(R.string.Http_Time_Out);
                    } else {
                        errorMessage = getString(R.string.Introducingvideo_Play_Error);
                    }
                    showErrorDialog(errorMessage);
                }

                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    protected void initMyData() {
        picUrl = getIntent().getStringExtra("picUrl");
        videoUrl = getIntent().getStringExtra("videoUrl");
        msg = getIntent().getStringExtra("msg");
        deviceId = getIntent().getStringExtra("deviceId");
        cameraid = getIntent().getStringExtra("cameraid");
        bucket = getIntent().getStringExtra("bucket");
        region = getIntent().getStringExtra("region");
        if (TextUtils.isEmpty(cameraid)) {
            cameraid = deviceId;
        }
        time = getIntent().getLongExtra("time", 0);
        sodoMain = getIntent().getStringExtra("sodoMain");
        iCamCloudApiUnit = new ICamCloudApiUnit(this);
        String strDt = DateUtil.getFormatIMGTime(time);
        isChina = LanguageUtil.isChina();
        if (isChina) {
            setToolBarTitleAndRightImg(strDt, R.drawable.icon_btn_share);
        } else {
            setToolBarTitle(strDt);
        }
    }


    private void StartPlayMp4() {
        iCamCloudApiUnit.doGetPicVideo(cameraid, time, videoUrl, bucket, region, new ICamCloudApiUnit.IcamApiCommonListener<File>() {
            @Override
            public void onSuccess(File file) {
                WLog.d(TAG, "imgUrl=" + "file://" + file.getAbsolutePath().trim());
                final String videlFullUrl = "file://" + file.getAbsolutePath().trim();
                vv_play.setVideoURI(Uri.parse(videlFullUrl));
                MediaController controller = new MediaController(BcPlayVideoActivity.this);
                //双重绑定
                vv_play.setMediaController(controller);
                controller.setMediaPlayer(vv_play);
                vv_play.requestFocus();
                vv_play.start();
            }

            @Override
            public void onFail(int code, String msg) {
                WLog.d(TAG, "msg=" + msg);
            }
        });
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.img_right) {
            if (videoUrl != null) {
                iCamCloudApiUnit.doPostShare(cameraid, picUrl, videoUrl, new ICamCloudApiUnit.IcamApiCommonListener<ShareNoticeBean>() {
                    @Override
                    public void onSuccess(ShareNoticeBean bean) {
                        shareUrl(bean.pic, bean.video);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        ToastUtil.show(msg);
                    }
                });
            }
        }
    }

    private void shareUrl(String picUrl, String videoUrl) {
        ShareDialog shareDialog = new ShareDialog(this);
        String type = "";
        Device device = MainApplication.getApplication().getDeviceCache().get(deviceId);
        if (device != null) {
            shareDialog.setShareTitle(String.format(getString(R.string.Share_Source), getString(DeviceInfoDictionary.getDefaultNameByType(device.type))));
            shareDialog.setShareMsg(msg);
            type = device.type;
        } else {
            shareDialog.setShareTitle(String.format(getString(R.string.Share_Source), getString(R.string.Message_Center_AlarmMessage)));
        }
        shareDialog.setShareUrl(picUrl, videoUrl, type);
        shareDialog.show();
    }


    /**
     * 视频无法播放弹框
     *
     * @param errorMessage
     */
    private void showErrorDialog(String errorMessage) {
        WLDialog.Builder builder;
        builder = new WLDialog.Builder(this);
        builder.setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setMessage(errorMessage)
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        errorDialog.dismiss();
                        BcPlayVideoActivity.this.finish();
                    }

                    @Override
                    public void onClickNegative(View view) {

                    }
                });
        errorDialog = builder.create();
        if (!errorDialog.isShowing()) {
            errorDialog.show();
        }
    }

}
