package cc.wulian.smarthomev6.main.device.device_if02.match;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.device_if02.WifiIRActivity;
import cc.wulian.smarthomev6.main.device.device_if02.WifiIRManage;
import cc.wulian.smarthomev6.main.device.device_if02.bean.CodeLibBean;
import cc.wulian.smarthomev6.main.device.device_if02.bean.ControllerBean;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * created by huxc  on 2017/11/3.
 * func：查询码库并创建遥控器界面
 * email: hxc242313@qq.com
 */

public class DownLoadCodeActivity extends BaseTitleActivity {
    private static final long TIMEOUT_TIME = 120 * 1000;
    private String code;
    private String deviceID;
    private String brandName;
    private String localName;
    private String blockType;
    private Device device;
    private DataApiUnit dataApiUnit;

    private ImageView loadingAnimation;
    private ImageView bgLoading;
    private AnimationDrawable mAnimation;
    private Handler handler;

    private WLDialog wlDialog;
    private Runnable mRunnable = new Runnable() {
        public void run() {
            startAnimation(mAnimation);
        }
    };

    public static void start(Context context, String deviceID, String brandName, String blockType, String code) {
        Intent intent = new Intent(context, DownLoadCodeActivity.class);
        intent.putExtra("deviceID", deviceID);
        intent.putExtra("brandName", brandName);
        intent.putExtra("ueiType", blockType);
        intent.putExtra("code", code);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_code_library, true);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initView() {
        super.initView();
        loadingAnimation = (ImageView) findViewById(R.id.loading);
        bgLoading = (ImageView) findViewById(R.id.bg_loading);
        bgLoading.setImageResource(R.drawable.bg_if01_loading);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Infraredrelay_Addremote_Download));
    }

    @Override
    protected void initData() {
        super.initData();
        code = getIntent().getStringExtra("code");
        deviceID = getIntent().getStringExtra("deviceID");
        brandName = getIntent().getStringExtra("brandName");
        blockType = getIntent().getStringExtra("ueiType");

        device = MainApplication.getApplication().getDeviceCache().get(deviceID);
        dataApiUnit = new DataApiUnit(this);
        handler = new Handler();
        mAnimation = (AnimationDrawable) loadingAnimation
                .getDrawable();
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(mRunnable, 1000);
        getWifiIRCodeLib();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAnimation(mAnimation);
    }

    protected void startAnimation(final AnimationDrawable animation) {
        if (animation != null && !animation.isRunning()) {
            animation.run();
        }
    }

    protected void stopAnimation(final AnimationDrawable animation) {
        if (animation != null && animation.isRunning())
            animation.stop();
    }


    /**
     * 获取型号对应的码库并创建遥控器
     */
    private void getWifiIRCodeLib() {
        dataApiUnit.doGetWifiIRCodeLib(blockType, code, "IF02", new DataApiUnit.DataApiCommonListener<CodeLibBean>() {

            @Override
            public void onSuccess(CodeLibBean bean) {
                if (bean.picks != null) {
                    dataApiUnit.doAddWifiIRController(deviceID, blockType, brandName + WifiIRManage.getDeviceTypeName(DownLoadCodeActivity.this, blockType), code, new DataApiUnit.DataApiCommonListener<ControllerBean>() {
                        @Override
                        public void onSuccess(ControllerBean bean) {
                            WifiIRActivity.start(DownLoadCodeActivity.this, deviceID, false);
                            EventBus.getDefault().post(new DeviceReportEvent(device));
                            finish();
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
                showDownloadFailDialog();

            }
        });


    }

    private void showDownloadFailDialog() {
        WLDialog.Builder builder = new WLDialog.Builder(this);
        builder.setTitle(R.string.Infraredrelay_Downloadfailed_Title)
                .setMessage(R.string.Infraredtransponder_Getcodelibrary_Fail)
                .setPositiveButton(R.string.Tip_I_Known)
                .setCancelOnTouchOutSide(false)
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View var1, String msg) {
                        wlDialog.dismiss();
                        DownLoadCodeActivity.this.finish();
                    }

                    @Override
                    public void onClickNegative(View var1) {

                    }
                });

        wlDialog = builder.create();
        if (!wlDialog.isShowing()) {
            wlDialog.show();
        }

    }

}
