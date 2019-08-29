package cc.wulian.smarthomev6.main.device.cateye;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.wulian.oss.H264CustomeView;
import com.wulian.oss.callback.ConnectDataCallBack;
import com.wulian.oss.service.WulianOssSimpleClient;
import com.wulian.sdk.android.ipc.rtcv2.IPCController;

import java.io.File;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseActivity;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.FileUtil;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者: chao
 * 时间: 2017/5/18
 * 描述: 报警记录视频播放页
 * 联系方式: 805901025@qq.com
 */

public class CateyePlayVideoActivity extends BaseActivity {
    private static final int START = 1;
    private static final int STOP = 2;
    private static final String TAG = "PlayAlarmActivity";

    private WulianOssSimpleClient mClient;
    RelativeLayout rl;
    private H264CustomeView mCustomeVieView;
    private String fileName;
    private String videoUrl;
    private boolean hasPlay = false;
    private Button btnPlay;
    private Handler myHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START:
                    btnPlay.setVisibility(View.GONE);
                    break;
                case STOP:
                    btnPlay.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    };

    public static void start(Context context, String url, String fileName) {
        Intent intent = new Intent(context, CateyePlayVideoActivity.class);
        intent.putExtra("videoUrl", url);
        intent.putExtra("fileName", fileName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cateye_alarm_play_video);

        initView();
        initData();
        initListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IPCController.setRender("", null);
        if (hasPlay) {
            mClient.disableLog();
            mClient.disconnect();
        }
    }

    protected void initView() {
        btnPlay = (Button) findViewById(R.id.btn_initData);
        mCustomeVieView = (H264CustomeView) findViewById(R.id.iv_video);
    }

    protected void initListeners() {
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File video = new File(FileUtil.getVideoPath() + "/" + fileName);
                if (video.exists()) {
                    PlayVideo();
                } else {
                    downloadVideo();
                }
            }
        });
    }

    protected void initData() {
        videoUrl = getIntent().getStringExtra("videoUrl");
        fileName = getIntent().getStringExtra("fileName");
        Log.i(TAG, "fileName = " + videoUrl);
        downloadVideo();
    }

    public void PlayVideo() {
        mClient = new WulianOssSimpleClient(mListener, getApplicationContext());
        mClient.enableLog();
        mClient.initData();
        mClient.playOSSObjectName(FileUtil.getVideoPath() + "/" + fileName);
        Log.e("H264FileName", fileName);
        mClient.connect();
        hasPlay = true;
    }

    ConnectDataCallBack mListener = new ConnectDataCallBack() {
        @Override
        public void onH264StreamMessage(byte[] data, int width, int height) {
            Log.e("H264", "返回h264消息");
            mCustomeVieView.PlayVideo(data, width, height);
        }

        @Override
        public void onError(Exception error) {
            Log.e("H264", "错误");
        }

        @Override
        public void onDisconnect(int code, String reason) {
            Log.e("H264", "断开");
            myHandler.sendEmptyMessage(STOP);
        }

        @Override
        public void onRequestObjectEndFlag() {
            Log.e("H264", "结束");
        }

        @Override
        public void onRequestGetObjectResultOK(long timestamp) {
            Log.e("H264", "确认当前视频已经过来了");
            myHandler.sendEmptyMessage(START);
        }
    };

    private void downloadVideo() {
        File video = new File(FileUtil.getVideoPath() + "/" + fileName);
        if (video.exists()) {
            return;
        }
        OkGo.get(videoUrl)
                .tag(this)
                .execute(new FileCallback(FileUtil.getVideoPath(), fileName) {
                    @Override
                    public void onSuccess(File file, Call call, Response response) {
                        Log.e("H264", "下载完成");
                    }
                });
    }
}
