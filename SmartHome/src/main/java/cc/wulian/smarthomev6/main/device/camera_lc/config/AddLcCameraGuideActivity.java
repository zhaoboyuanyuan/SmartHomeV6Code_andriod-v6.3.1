package cc.wulian.smarthomev6.main.device.camera_lc.config;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lechange.opensdk.media.DeviceInitInfo;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.LcConfigWifiModel;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.camera_lc.business.Business;
import cc.wulian.smarthomev6.support.core.apiunit.ICamCloudApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.IcamCloudCheckBindBean;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.ConstantsUtil;
import cc.wulian.smarthomev6.support.utils.LogUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

public class AddLcCameraGuideActivity extends BaseTitleActivity {
    private Button btnNextStep;
    private String cameraType;
    private String cameraId;
    private ImageView ivIcon;
    private TextView tvStep1;
    private TextView tvStep2;

    private final int startPolling = 0x10;
    private final int successOnline = 0x11;
    private final int asynWaitOnlineTimeOut = 0x12;
    private final int successAddDevice = 0x13;
    // private final int successOffline = 0x14;
    // private final int addDeviceTimeOut = 0x15;
    // private final int failedAddDevice = 0x16;
    // private final int successOnlineEx = 0x17;
    private final int deviceInitSuccess = 0x18;
    private final int deviceInitFailed = 0x19;
    private final int deviceInitByIPFailed = 0x1A;
    private final int deviceSearchSuccess = 0x1B;
    private final int deviceSearchFailed = 0x1C;
    private final int INITMODE_UNICAST = 0;
    private final int INITMODE_MULTICAST = 1;
    private int curInitMode = INITMODE_MULTICAST;
    private boolean mIsOffline = true;
    private boolean mIsDeviceSearched = false; //设备初始化标志，保证设备初始化接口只调用一次
    private boolean mIsDeviceInitSuccess = false;
    private Handler mHandler;
    private String boundUser;
    private int boundRelation;
    private boolean isBindDevice;
    private boolean hasBeenBinded = false;
    private LcConfigWifiModel lcConfigWifiModel;
    private String scanEntry;

    public static void start(Context context, String cameraType, String cameraId, String scanEntry) {
        Intent it = new Intent();
        it.putExtra("cameraType", cameraType);
        it.putExtra("cameraId", cameraId);
        it.putExtra("scanEntry", scanEntry);
        it.setClass(context, AddLcCameraGuideActivity.class);
        context.startActivity(it);
    }

    public static void start(Context context, LcConfigWifiModel lcConfigWifiModel) {
        Intent it = new Intent();
        it.putExtra("configData", lcConfigWifiModel);
        it.setClass(context, AddLcCameraGuideActivity.class);
        context.startActivity(it);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lc_camera_guide, true);
    }

    @Override
    protected void initView() {
        super.initView();
        ivIcon = (ImageView) findViewById(R.id.iv_device_img);
        btnNextStep = (Button) findViewById(R.id.btn_next_step);
        tvStep1 = (TextView) findViewById(R.id.tv_step1);
        tvStep2 = (TextView) findViewById(R.id.tv_step2);
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(btnNextStep, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(btnNextStep, SkinResouceKey.COLOR_BUTTON_TEXT);
    }

    @Override
    protected void initData() {
        super.initData();
        cameraType = getIntent().getStringExtra("cameraType");
        cameraId = getIntent().getStringExtra("cameraId");
        scanEntry = getIntent().getStringExtra("scanEntry");
        lcConfigWifiModel = (LcConfigWifiModel) getIntent().getSerializableExtra("configData");
        if (lcConfigWifiModel == null) {
            initLcConfigWifiModel();
        } else {
            cameraType = lcConfigWifiModel.getDeviceType();
            cameraId = lcConfigWifiModel.getDeviceId();
            scanEntry = lcConfigWifiModel.getScanEntry();
        }
        updateViewByConnectType();
        initHandler();
        startBindingCheck();
    }


    @Override
    protected void initListeners() {
        super.initListeners();
        btnNextStep.setOnClickListener(this);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(R.string.Add_Device);
    }

    private void updateViewByConnectType() {
        if (!TextUtils.isEmpty(cameraType)) {
            switch (cameraType) {
                case "CG22":
                case "CG23":
                    tvStep1.setText(R.string.add_deviceLC_step_1);
                    tvStep2.setText(R.string.add_deviceLC_step_2);
                    ivIcon.setImageResource(R.drawable.icon_cg22_bg);
                    break;
                case "CG24":
                case "CG25":
                    if (lcConfigWifiModel.isWifiConnect()) {
                        tvStep1.setText(R.string.add_deviceLC_step_hint_2);
                        tvStep2.setText(R.string.add_deviceLC_step_hint_3);
                        ivIcon.setImageResource(R.drawable.icon_cg24_bg);
                    } else if (lcConfigWifiModel.isWiredConnect()) {
                        tvStep1.setText(R.string.add_deviceLC_step_1);
                        tvStep2.setText(R.string.add_deviceLC_step_2);
                        ivIcon.setImageResource(R.drawable.bg_add_lc_wire);
                    }
                    break;
                case "CG26":
                    tvStep1.setText(R.string.addDevice_CG26_step_1);
                    tvStep2.setText(R.string.addDevice_CG26_step_2);
                    ivIcon.setImageResource(R.drawable.icon_cg26_bg);

                    break;
            }
        }
    }

    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.d(LogUtil.LC_TAG, "msg.what" + msg.what);
                switch (msg.what) {
                    case deviceSearchSuccess:
                        progressDialogManager.dimissDialog("config", 0);
                        lcConfigWifiModel.setmMac(((DeviceInitInfo) msg.obj).mMac);
                        lcConfigWifiModel.setmPort(((DeviceInitInfo) msg.obj).mPort);
                        lcConfigWifiModel.setmIp(((DeviceInitInfo) msg.obj).mIp);
                        lcConfigWifiModel.setmStatus(((DeviceInitInfo) msg.obj).mStatus);
                        Log.d(LogUtil.LC_TAG, "deviceSearchSuccess: ");
                        Log.d(LogUtil.LC_TAG, "mac: " + ((DeviceInitInfo) msg.obj).mMac);
                        Log.d(LogUtil.LC_TAG, "status: " + ((DeviceInitInfo) msg.obj).mStatus);
                        LcStartConfigActivity.start(AddLcCameraGuideActivity.this, lcConfigWifiModel, boundRelation, boundUser);
//                        finish();
                        break;
                    case deviceSearchFailed:
                        Log.d(LogUtil.LC_TAG, "deviceSearchFailed:" + (String) msg.obj);
                        break;
                }
            }
        };
    }

    private void initLcConfigWifiModel() {
        isBindDevice = true;
        lcConfigWifiModel = new LcConfigWifiModel();
        lcConfigWifiModel.setDeviceId(cameraId);
        lcConfigWifiModel.setDeviceType(cameraType);
        lcConfigWifiModel.setIsAddDevice(isBindDevice);
        lcConfigWifiModel.setScanEntry(scanEntry);
        switch (cameraType) {
            case "CG22":
            case "CG23":
            case "CG26":
                lcConfigWifiModel.setWiredConnect(true);
                break;
            case "CG24":
            case "CG25":
                lcConfigWifiModel.setWifiConnect(true);
                break;
        }
    }

    private void startBindingCheck() {
        ICamCloudApiUnit ICamCloudApiUnit = new ICamCloudApiUnit(this);
        ICamCloudApiUnit.doCheckBind(cameraId, cameraType, null, null, new ICamCloudApiUnit.IcamApiCommonListener<IcamCloudCheckBindBean>() {
            @Override
            public void onSuccess(IcamCloudCheckBindBean bean) {
                if (bean.boundRelation == 0) {
                    hasBeenBinded = false;
                    Log.i(LogUtil.LC_TAG, "未绑定");
                } else if (bean.boundRelation == 1 || bean.boundRelation == 2) {
                    Log.i(LogUtil.LC_TAG, "已被绑定 ");
                    hasBeenBinded = true;
                    boundRelation = bean.boundRelation;
                    boundUser = bean.boundUser;
                }
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(msg);
            }
        });
    }

    private void searchDevice() {
        Business.getInstance().searchDevice(cameraId, 15000, new Handler() {
            public void handleMessage(final Message msg) {
                if (msg.what < 0) {
                    if (msg.what == -2)
                        mHandler.obtainMessage(deviceSearchFailed, "device not found").sendToTarget();
                    else
                        mHandler.obtainMessage(deviceSearchFailed, "StartSearchDevices failed").sendToTarget();
                    return;
                }

                mHandler.obtainMessage(deviceSearchSuccess, msg.obj).sendToTarget();
            }
        });
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_next_step:
                if (lcConfigWifiModel.isWiredConnect()) {
                    Log.i(LogUtil.LC_TAG, "有线绑定流程");
                    ProgressDialogManager.getDialogManager().showDialog("config", this, null, null, 10000);
                    searchDevice();
                } else if (lcConfigWifiModel.isWifiConnect()) {
                    Log.i(LogUtil.LC_TAG, "无线绑定流程 ");
                    if (TextUtils.equals(ConstantsUtil.DEVICE_SCAN_ENTRY, scanEntry)) {
                        LcStartConfigActivity.start(this, lcConfigWifiModel, boundRelation, boundUser);
//                        finish();
                    }
                }

                break;
            case R.id.img_left:
//                if (lcConfigWifiModel.isWiredConnect() &&
//                        (TextUtils.equals(cameraType, "CG24") || TextUtils.equals(cameraType, "CG25"))) {
//                    finish();
//                } else {
//                    startActivity(new Intent(this, AddDeviceActivity.class));
//                    finish();
//                }
                finish();
                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        startActivity(new Intent(this, AddDeviceActivity.class));
    }
}
