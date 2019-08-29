package cc.wulian.smarthomev6.main.device.eques;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesAlarmDetailBean;
import cc.wulian.smarthomev6.support.core.apiunit.EquesApiUnit;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.utils.FileUtil;

/**
 * 作者: chao
 * 时间: 2017/6/14
 * 描述: 移康猫眼报警详情页查看视频界面
 * 联系方式: 805901025@qq.com
 */

public class EquesVideoActivity extends BaseTitleActivity {
    private static final String DOWNLOAD = "DOWNLOAD";

    private RelativeLayout rl_play;
    private FrameLayout fl_play;
    private VideoView vv_play;
    private ImageView iv_bg;
    private ImageView iv_pre;

    private String deviceid;
    private Device device;
    private EquesAlarmDetailBean bean;

    private boolean isDownloading;

    public static void start(Context context, String deviceid, EquesAlarmDetailBean bean) {
        Intent intent = new Intent(context, EquesVideoActivity.class);
        intent.putExtra("deviceid", deviceid);
        intent.putExtra("equesAlarmDetailBean", bean);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bean = (EquesAlarmDetailBean) getIntent().getSerializableExtra("equesAlarmDetailBean");
        deviceid = getIntent().getStringExtra("deviceid");
        device = MainApplication.getApplication().getDeviceCache().get(deviceid);
        setContentView(R.layout.activity_eques_video, true);
    }

    @Override
    protected void initTitle() {
        setToolBarTitle(DeviceInfoDictionary.getNameByDevice(device));
    }

    @Override
    protected void initView() {
        rl_play = (RelativeLayout) findViewById(R.id.rl_play);
        fl_play = (FrameLayout) findViewById(R.id.fl_play);
        vv_play = (VideoView) findViewById(R.id.vv_play);
        iv_bg = (ImageView) findViewById(R.id.iv_bg);
        iv_pre = (ImageView) findViewById(R.id.iv_pre);
    }

    @Override
    protected void initData() {
        String picPath = FileUtil.getEquesTempPath() + "/" + bean.bid + "/" + bean.time + ".jpg";
        ImageLoader.getInstance().displayImage("file://" + picPath, iv_pre);
        downloadMp4();
    }

    @Override
    protected void initListeners() {
        rl_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(FileUtil.getEquesAlarmPath() + "/" + deviceid + "/" + bean.time + ".mp4");
                if (file.exists() && !isDownloading) {
                    if (vv_play.isPlaying()) {
                        vv_play.pause();
                        fl_play.setVisibility(View.VISIBLE);
                    } else {
                        vv_play.start();
                        fl_play.setVisibility(View.GONE);
                        iv_pre.setVisibility(View.GONE);
                    }
                } else {
                    downloadMp4();
                }
            }
        });
        vv_play.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                fl_play.setVisibility(View.VISIBLE);
                iv_pre.setVisibility(View.VISIBLE);
            }
        });
        vv_play.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                File file = new File(FileUtil.getEquesAlarmPath() + "/" + deviceid + "/" + bean.time + ".mp4");
                if (file.exists()) {
                    file.delete();
                }
                iv_bg.setVisibility(View.VISIBLE);
                downloadMp4();
                return true;
            }
        });
    }

    private void downloadMp4() {
        if (progressDialogManager.getDialog(DOWNLOAD) != null && progressDialogManager.getDialog(DOWNLOAD).isShowing()) {
        } else {
            progressDialogManager.showCancelableDialog(DOWNLOAD, this, null, null, null, 120000);
        }
        if (isDownloading) {

        } else {
            isDownloading = true;
            MainApplication.getApplication().getEquesApiUnit().loadAlarmFile(bean.fid.get(0), deviceid, bean.type, bean.time, new EquesApiUnit.EquesFileDownloadListener() {
                @Override
                public void onSuccess(File file) {
                    isDownloading = false;
                    progressDialogManager.dimissDialog(DOWNLOAD, 0);
                    vv_play.setVideoURI(Uri.fromFile(file));
                    iv_bg.setVisibility(View.GONE);
                }

                @Override
                public void onFail(int code, String msg) {
                    progressDialogManager.dimissDialog(DOWNLOAD, 0);
                    isDownloading = false;
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (progressDialogManager.getDialog(DOWNLOAD) != null && progressDialogManager.getDialog(DOWNLOAD).isShowing()) {
            progressDialogManager.dimissDialog(DOWNLOAD, 0);
        } else {
            super.onBackPressed();
        }
    }
}
