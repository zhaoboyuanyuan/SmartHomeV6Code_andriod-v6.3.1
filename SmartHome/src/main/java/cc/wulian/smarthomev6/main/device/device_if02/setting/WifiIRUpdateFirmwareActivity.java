package cc.wulian.smarthomev6.main.device.device_if02.setting;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceBean;
import cc.wulian.smarthomev6.support.core.device.Attribute;
import cc.wulian.smarthomev6.support.core.device.Cluster;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.Endpoint;
import cc.wulian.smarthomev6.support.core.device.EndpointParser;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * created by huxc  on 2018/6/13.
 * func：固件更新界面
 * email: hxc242313@qq.com
 */

public class WifiIRUpdateFirmwareActivity extends BaseTitleActivity {
    private static final String UPDATE = "UPDATE";

    private ImageView ivResult;
    private Button btnResult;
    private TextView tvResult;
    private TextView tvTips;

    private String deviceId;
    private String currentVersion;
    private String latestVersion;
    private int result;//0升级中,1成功 2,失败

    private ObjectAnimator animator;
    private DataApiUnit dataApiUnit;
    private DeviceApiUnit deviceApiUnit;
    private Runnable timeOutRunnable;
    private Runnable queryRunnable;
    private Handler handler;
    private Device device;

    public static void start(Context context, String deviceId, String latestVersion) {
        Intent intent = new Intent(context, WifiIRUpdateFirmwareActivity.class);
        intent.putExtra("deviceId", deviceId);
        intent.putExtra("latestVersion", latestVersion);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_if01_update_firmware, true);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initView() {
        super.initView();
        ivResult = (ImageView) findViewById(R.id.iv_result);
        tvResult = (TextView) findViewById(R.id.tv_update_result);
        tvTips = (TextView) findViewById(R.id.tv_update_tip);
        btnResult = (Button) findViewById(R.id.btn_result);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(R.string.Update_Firmware);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        btnResult.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        deviceId = getIntent().getStringExtra("deviceId");
        latestVersion = getIntent().getStringExtra("latestVersion");
        device = MainApplication.getApplication().getDeviceCache().get(deviceId);
        dataApiUnit = new DataApiUnit(this);
        deviceApiUnit = new DeviceApiUnit(this);
        handler = new Handler();
        setAnimator();
        sendUpdateCmd();
    }

    private void setAnimator() {
        timeOutRunnable = new Runnable() {
            @Override
            public void run() {
                animator.cancel();
                handler.removeCallbacks(queryRunnable);
                result = 2;
                updateView(result);
            }
        };

        queryRunnable = new Runnable() {
            @Override
            public void run() {
                getCurrentVersion();
                if (!TextUtils.equals(currentVersion, latestVersion)) {
                    handler.postDelayed(queryRunnable, 1000 * 5);
                } else {
                    result = 1;
                    updateView(1);
                }
            }
        };
        handler.postDelayed(timeOutRunnable, 1000 * 60);
        handler.postDelayed(queryRunnable, 1000 * 10);
        showRotateAnimation();
    }

    private void getCurrentVersion() {
        deviceApiUnit.doGetZigBeeDeviceInfo(deviceId, new DeviceApiUnit.DeviceApiCommonListener<DeviceBean>() {
            @Override
            public void onSuccess(DeviceBean bean) {
                if (bean != null) {
                    if (!TextUtils.isEmpty(bean.version)) {
                        WLog.i(TAG, "getCurrentVersion" + bean.version);
                        currentVersion = bean.version;
                    }
                }
            }

            @Override
            public void onFail(int code, String msg) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_result:
                if (result == 1) {
                    this.finish();
                } else if (result == 2) {
                    setAnimator();
                    sendUpdateCmd();
                }
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(null);
        EventBus.getDefault().unregister(this);
    }

    private void updateView(int result) {
        btnResult.setVisibility(result == 0 ? View.INVISIBLE : View.VISIBLE);
        tvTips.setVisibility(result == 0 ? View.VISIBLE : View.INVISIBLE);
        switch (result) {
            case 0:
                btnResult.setVisibility(View.INVISIBLE);
                tvResult.setText(getString(R.string.IF_006));
                ivResult.setImageResource(R.drawable.icon_update_loading);
                break;
            case 1:
                handler.removeCallbacks(null);
                btnResult.setText(getString(R.string.Done));
                btnResult.setVisibility(View.VISIBLE);
                ivResult.setImageResource(R.drawable.icon_config_wifi_success);
                tvResult.setText(getString(R.string.IF_008));
                break;
            case 2:
                btnResult.setText(getString(R.string.Common_Retry));
                tvResult.setText(getString(R.string.Cateyemini_Setup_Upgradefailed));
                btnResult.setVisibility(View.VISIBLE);
                ivResult.setImageResource(R.drawable.icon_config_wifi_fail);
                break;
        }
    }

    private void showRotateAnimation() {
        animator = ObjectAnimator.ofFloat(ivResult, "rotation", 0f, 360f);
        animator.setDuration(1000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }

    private void sendUpdateCmd() {
        updateView(0);
        dataApiUnit.updateIf02Firmware(deviceId, new DataApiUnit.DataApiCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {

            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && device != null) {
            if (TextUtils.equals(event.device.devID, device.devID)) {
                EndpointParser.parse(event.device, new EndpointParser.ParserCallback() {
                    @Override
                    public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute
                            attribute) {
                        if (cluster.clusterId == 0x0F01) {
                            switch (attribute.attributeId) {
                                case 0x8003:
                                    ToastUtil.show(attribute.attributeValue);
                                    result = 2;
                                    animator.cancel();
                                    updateView(result);
                                    break;
                            }
                        }
                    }
                });
            }
        }
    }
}
