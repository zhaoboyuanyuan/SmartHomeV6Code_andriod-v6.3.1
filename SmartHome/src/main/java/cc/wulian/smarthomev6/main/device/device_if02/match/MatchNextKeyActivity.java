package cc.wulian.smarthomev6.main.device.device_if02.match;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.SpannableBean;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.device_if02.WifiIRManage;
import cc.wulian.smarthomev6.main.device.device_if02.airconditioner.MatchResultActivity;
import cc.wulian.smarthomev6.main.device.device_if02.bean.ControllerBean;
import cc.wulian.smarthomev6.main.device.device_if02.bean.MatchNextCmpKey;
import cc.wulian.smarthomev6.main.device.device_if02.custom.CustomMainActivity;
import cc.wulian.smarthomev6.main.device.device_if02.fan.FanMainActivity;
import cc.wulian.smarthomev6.main.device.device_if02.it_box.ITBoxMainActivity;
import cc.wulian.smarthomev6.main.device.device_if02.projector.ProjectorMainActivity;
import cc.wulian.smarthomev6.main.device.device_if02.stb.StbRemoteMainActivity;
import cc.wulian.smarthomev6.main.device.device_if02.tv.TvRemoteMainActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.device.Attribute;
import cc.wulian.smarthomev6.support.core.device.Cluster;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.Endpoint;
import cc.wulian.smarthomev6.support.core.device.EndpointParser;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * created by huxc  on 2018/6/14.
 * func：一键匹配模式
 * email: hxc242313@qq.com
 */

public class MatchNextKeyActivity extends BaseTitleActivity {

    private static final String POWER = "power";
    private static final String ON = "on";
    private static final String OK = "ok";
    private LinearLayout layoutFailView;
    private LinearLayout layoutNextView;
    private TextView btnRetry;
    private TextView btnCustom;
    private TextView tvStepTip;
    private TextView tvTip;

    private String deviceId;
    private String type;
    private String brandId;
    private String nextKeyName;
    private String brandName;

    private DataApiUnit dataApiUnit;
    private Device device;
    private WLDialog updateNameDialog;
    private boolean hasDownLoad;
    private Handler handler;
    private Runnable timeoutRunnable;


    public static void start(Context context, String deviceID, String type, String brandId, String brandName) {
        Intent intent = new Intent(context, MatchNextKeyActivity.class);
        intent.putExtra("deviceID", deviceID);
        intent.putExtra("type", type);
        intent.putExtra("brandId", brandId);
        intent.putExtra("brandName", brandName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_if01_match_mode, true);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.IF_012));
    }

    @Override
    protected void initView() {
        super.initView();
        layoutFailView = (LinearLayout) findViewById(R.id.layout_fail_tip);
        layoutNextView = (LinearLayout) findViewById(R.id.ll_step_tip);
        tvStepTip = (TextView) findViewById(R.id.tv_step_tip);
        tvTip = (TextView) findViewById(R.id.tv_tips);
        btnRetry = (TextView) findViewById(R.id.tv_retry);
        btnCustom = (TextView) findViewById(R.id.tv_custom);
        StringUtil.addColorOrSizeorEvent(tvTip, getResources().getString(R.string.IF_013), new SpannableBean[]{
                new SpannableBean(getResources().getColor(R.color.orange), 16, null)});
    }


    @Override
    protected void initListeners() {
        super.initListeners();
        btnRetry.setOnClickListener(this);
        btnCustom.setOnClickListener(this);

    }

    @Override
    protected void initData() {
        super.initData();
        deviceId = getIntent().getStringExtra("deviceID");
        type = getIntent().getStringExtra("type");
        brandId = getIntent().getStringExtra("brandId");
        brandName = getIntent().getStringExtra("brandName");
        dataApiUnit = new DataApiUnit(this);
        handler = new Handler();
        device = MainApplication.getApplication().getDeviceCache().get(deviceId);
        timeoutRunnable = new Runnable() {
            @Override
            public void run() {
                updateView(false);
            }
        };
        setDefaultStatus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_retry:
                updateView(true);
                setDefaultStatus();
                break;
            case R.id.tv_custom:
                showCreateControllerDialog();
                break;
        }
    }

    private void setDefaultStatus() {
        if (TextUtils.equals(type, WifiIRManage.TYPE_AIR)) {
            nextKeyName = ON;
            tvStepTip.setText("\"" + "on" + "\"");
        } else if (TextUtils.equals(type, WifiIRManage.TYPE_SOUND)) {
            nextKeyName = OK;
            tvStepTip.setText("\"" + "ok" + "\"");
        } else {
            nextKeyName = POWER;
            tvStepTip.setText("\"" + "power" + "\"");
        }
        setLearningStatus(WifiIRManage.START_LEARN);
    }

    //非空调类一键匹配
    private void getNextKey(String cmpKey, String code) {
        dataApiUnit.doMatchWifiIRController(deviceId, type, brandId, cmpKey, code, new DataApiUnit.DataApiCommonListener<MatchNextCmpKey>() {
            @Override
            public void onSuccess(MatchNextCmpKey bean) {
                if (!TextUtils.isEmpty(bean.codeLib) && TextUtils.isEmpty(bean.nextCmpKey)) {
                    if (!hasDownLoad) {
                        DownLoadCodeActivity.start(MatchNextKeyActivity.this, deviceId, brandName, type, bean.codeLib);
                        hasDownLoad = true;
                        finish();
                    }
                } else {
                    nextKeyName = bean.nextCmpKey;
                    tvStepTip.setText("\"" + nextKeyName + "\"");
                    setLearningStatus(WifiIRManage.START_LEARN);
                    ToastUtil.show(getString(R.string.Infrared_transponder_match));
                }

            }

            @Override
            public void onFail(int code, String msg) {
                if (code == 20127) {
                    updateView(false);
                }
            }
        });

    }

    private void updateView(boolean flag) {
        if (flag) {
            layoutNextView.setVisibility(View.VISIBLE);
            layoutFailView.setVisibility(View.GONE);
        } else {
            layoutNextView.setVisibility(View.GONE);
            layoutFailView.setVisibility(View.VISIBLE);
        }
    }


    private void showCreateControllerDialog() {
        WLDialog.Builder builder = new WLDialog.Builder(this);
        builder.setEditTextHint(R.string.Infraredrelay_Custom_Entername)
                .setTitle(getString(R.string.Infraredrelay_Custom_Popuptitle))
                .setPositiveButton(R.string.Sure)
                .setNegativeButton(R.string.Cancel)
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View var1, String msg) {
                        createCustomController(msg);
                    }

                    @Override
                    public void onClickNegative(View var1) {
                        updateNameDialog.dismiss();

                    }
                });
        updateNameDialog = builder.create();
        updateNameDialog.show();
    }

    private void createCustomController(String msg) {
        DataApiUnit dataApiUnit = new DataApiUnit(this);
        dataApiUnit.doAddWifiIRController(deviceId, type, msg, null, new DataApiUnit.DataApiCommonListener<ControllerBean>() {
            @Override
            public void onSuccess(ControllerBean bean) {
                if (bean != null) {
                    switch (bean.blockType) {
                        case WifiIRManage.TYPE_TV:
                            TvRemoteMainActivity.learn(MatchNextKeyActivity.this, deviceId, bean.blockType, bean.blockName, bean.blockId);
                            break;
                        case WifiIRManage.TYPE_FAN:
                            FanMainActivity.learn(MatchNextKeyActivity.this, deviceId, bean.blockType, bean.blockName, bean.blockId);
                            break;
                        case WifiIRManage.TYPE_STB:
                            StbRemoteMainActivity.learn(MatchNextKeyActivity.this, deviceId, bean.blockType, bean.blockName, bean.blockId);
                            break;
                        case WifiIRManage.TYPE_IT_BOX:
                            ITBoxMainActivity.learn(MatchNextKeyActivity.this, deviceId, bean.blockType, bean.blockName, bean.blockId);
                            break;
                        case WifiIRManage.TYPE_PROJECTOR:
                            ProjectorMainActivity.learn(MatchNextKeyActivity.this, deviceId, bean.blockType, bean.blockName, bean.blockId);
                            break;
                        case WifiIRManage.TYPE_CUSTOM:
                        case WifiIRManage.TYPE_AIR:
                            CustomMainActivity.learn(MatchNextKeyActivity.this, deviceId, bean.blockType, bean.blockName, bean.blockId);
                            break;
                    }

                    finish();
                }

            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    private void setLearningStatus(final String status) {
        handler.postDelayed(timeoutRunnable, 30 * 1000);
        dataApiUnit.setWifiIRLearningStatus(deviceId, status, new DataApiUnit.DataApiCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {
                if (WifiIRManage.START_LEARN.equals(status)) {
                    WLog.i(TAG, "开启学习状态成功");
                } else {
                    WLog.i(TAG, "关闭学习状态成功");
                }
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    private void parserLearnData(String attribute) {
        switch (attribute) {
            case WifiIRManage.IR_START:
                WLog.i(TAG, "IR_START");
                break;
            case WifiIRManage.IR_STOP:
                WLog.i(TAG, "IR_STOP");
                break;
            case WifiIRManage.IR_TIMEROUT:
                WLog.i(TAG, "IR_TIMEROUT");
                updateView(false);
                break;
            default:
                WLog.i(TAG, "学到的码：" + attribute);
                if (TextUtils.equals(type, WifiIRManage.TYPE_AIR)) {
                    MatchResultActivity.start(this, deviceId, type, brandId, brandName, attribute);
                    finish();
                } else {
                    getNextKey(nextKeyName, attribute);
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && device != null) {
            if (TextUtils.equals(event.device.devID, device.devID)) {
                WLog.i(TAG, "onDeviceReport: " + event.device.data);
                EndpointParser.parse(event.device, new EndpointParser.ParserCallback() {
                    @Override
                    public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                        if (cluster.clusterId == 0x0F01) {
                            if (attribute.attributeId == 0x8001) {
                                handler.removeCallbacks(timeoutRunnable);
                                parserLearnData(attribute.attributeValue);
                            }
                        }
                    }
                });
            }
        }
    }

}
