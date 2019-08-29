/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.wulian.smarthomev6.main.device.device_xw01.config;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.zxing.Result;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.WishActivateBean;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.support.core.apiunit.AuthApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.UserBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.WishBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.WishBgmIdBean;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.tools.zxing.activity.ICaptureActivity;
import cc.wulian.smarthomev6.support.tools.zxing.camera.CameraManager;
import cc.wulian.smarthomev6.support.tools.zxing.decode.DecodeThread;
import cc.wulian.smarthomev6.support.tools.zxing.utils.BeepManager;
import cc.wulian.smarthomev6.support.tools.zxing.utils.CaptureActivityHandler;
import cc.wulian.smarthomev6.support.tools.zxing.utils.InactivityTimer;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;


/**
 * 扫码界面
 */
public final class WishQRCodeActivity extends BaseTitleActivity implements SurfaceHolder.Callback, ICaptureActivity {

    private static final String TAG = WishQRCodeActivity.class.getSimpleName();
    private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    private static final int PERMISSION_CAMERA_REQUEST_CODE = 1;
    private static final int BIND_GUIDE = 1;//配网绑定引导页入口
    private static final int SCAN_ACTIVATE_ENTRANCE = 2;//扫码激活入口

    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private InactivityTimer inactivityTimer;
    private BeepManager beepManager;

    private SurfaceView scanPreview = null;
    private RelativeLayout scanContainer;
    private RelativeLayout scanCropView;
    private ImageView scanLine;
    private TextView mTextTips;

    private int mGetCodeRequestCode;
    private String mTitle, mTips;

    private Rect mCropRect = null;
    private boolean isHasSurface = false;
    private boolean hasCameraPermission = false;
    private String type;
    private int flag;
    private String deviceId;

    private AuthApiUnit authApiUnit;
    private DeviceApiUnit deviceApiUnit;
    private WLDialog wlDialog;
    private WLDialog.Builder builder;

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_wish_qrcode, true);

        checkPermission();
    }

    @Override
    public boolean enableSwipeBack() {
        return false;
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                PERMISSION_CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, PERMISSION_CAMERA)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{PERMISSION_CAMERA},
                        PERMISSION_CAMERA_REQUEST_CODE);

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{PERMISSION_CAMERA},
                        PERMISSION_CAMERA_REQUEST_CODE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            hasCameraPermission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                hasCameraPermission = true;
            } else {
                // Permission Denied
                ToastUtil.show(R.string.Toast_Permission_Denied);
                finish();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void initView() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        scanPreview = (SurfaceView) findViewById(R.id.capture_preview);
        scanContainer = (RelativeLayout) findViewById(R.id.capture_container);
        scanCropView = (RelativeLayout) findViewById(R.id.capture_crop_view);
        scanLine = (ImageView) findViewById(R.id.capture_scan_line);
        mTextTips = (TextView) findViewById(R.id.capture_text_tips);

        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);

        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation
                .RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                0.9f);
        animation.setDuration(4500);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);
        scanLine.startAnimation(animation);
    }

    @Override
    protected void initListeners() {
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.img_left) {
            finish();
        }
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        mTitle = getIntent().getStringExtra("title");
//        if (!TextUtils.isEmpty(mTitle)) {
//            setToolBarTitle(mTitle);
//        } else {
//            setToolBarTitle(getString(R.string.AddDevice_AddDevice));
//        }
    }

    @Override
    protected void initData() {
        super.initData();
        type = getIntent().getStringExtra("type");
        flag = getIntent().getIntExtra("flag", -1);
        deviceId = getIntent().getStringExtra("deviceId");

        authApiUnit = new AuthApiUnit(this);
        deviceApiUnit = new DeviceApiUnit(this);

    }

    private void startWork() {

        // CameraManager must be initialized here(onResume), not in onCreate(). This is
        // necessary because we don't
        // want to open the camera driver and measure the screen size if we're
        // going to show the help on
        // first launch. That led to bugs where the scanning rectangle was the
        // wrong size and partially
        // off screen.
        cameraManager = new CameraManager(getApplication());

        handler = null;

        if (isHasSurface) {
            // The activity was paused but not stopped, so the surface still
            // exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(scanPreview.getHolder());
        } else {
            // Install the callback and wait for surfaceCreated() to init the
            // camera.
            scanPreview.getHolder().addCallback(this);
        }

        inactivityTimer.onResume();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isHasSurface) {
            scanPreview.getHolder().addCallback(this);
        }
        if (hasCameraPermission) {
            startWork();
        }
    }

    @Override
    protected void onPause() {
        if (hasCameraPermission) {
            if (handler != null) {
                handler.quitSynchronously();
                handler = null;
            }
            inactivityTimer.onPause();
            beepManager.close();
            cameraManager.closeDriver();
            if (!isHasSurface) {
                scanPreview.getHolder().removeCallback(this);
            }
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            WLog.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!isHasSurface) {
            isHasSurface = true;
            if (hasCameraPermission) {
                initCamera(holder);
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isHasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * A valid barcode has been found, so give an indication of success and show
     * the results
     *
     * @param rawResult The contents of the barcode.
     * @param bundle    The extras
     */
    public void handleDecode(Result rawResult, Bundle bundle) {
        inactivityTimer.onActivity();
        beepManager.playBeepSoundAndVibrate();
        final String qrCodeId = rawResult.getText();
        if (flag == BIND_GUIDE) {
            if (isJson(qrCodeId)) {
                final WishBean wishBean = JSON.parseObject(qrCodeId, WishBean.class);
                if (wishBean != null && wishBean.comName.startsWith("HOPE")) {
                    deviceApiUnit.doGetWishBgmDeviceId(qrCodeId, new DeviceApiUnit.DeviceApiCommonListener<WishBgmIdBean>() {
                        @Override
                        public void onSuccess(WishBgmIdBean bean) {
                            if (wishBean != null && !TextUtils.isEmpty(bean.deviceId)) {
                                deviceId = bean.deviceId;
                                authApiUnit.doGetAllBindAccount(deviceId, new AuthApiUnit.AuthApiCommonListener<List<UserBean>>() {
                                    @Override
                                    public void onSuccess(List<UserBean> bean) {
                                        if (bean != null && bean.size() == 0) {
                                            WLog.i(TAG, "doGetAllBindAccount: 暂时未被绑定");
                                            deviceApiUnit.doGetWishBmgActivateStatus(deviceId, new DataApiUnit.DataApiCommonListener<WishActivateBean>() {
                                                @Override
                                                public void onSuccess(WishActivateBean bean) {
                                                    if (bean != null && bean.activated) {
                                                        WLog.i(TAG, "doGetWishBmgActivateStatus:已被激活");
                                                        deviceApiUnit.doBindDevice(deviceId, null, "XW01", new DeviceApiUnit.DeviceApiCommonListener() {
                                                            @Override
                                                            public void onSuccess(Object bean) {
                                                                WLog.i(TAG, "设备绑定成功: ");
                                                                startActivity(new Intent(WishQRCodeActivity.this, WishBgmAddResultActivity.class)
                                                                        .putExtra("result", "success")
                                                                .putExtra("deviceId",deviceId));
                                                            }

                                                            @Override
                                                            public void onFail(int code, String msg) {
                                                                WLog.i(TAG, "设备绑定失败: ");
                                                                startActivity(new Intent(WishQRCodeActivity.this, WishBgmAddResultActivity.class)
                                                                        .putExtra("result", "fail")
                                                                        .putExtra("deviceId",deviceId));
                                                            }
                                                        });
                                                    } else {
                                                        WLog.i(TAG, "doGetWishBmgActivateStatus:未被激活");
                                                        startActivity(new Intent(WishQRCodeActivity.this, WishBgmActivateActivity.class)
                                                                .putExtra("deviceId", deviceId)
                                                                .putExtra("type", type));
                                                    }
                                                }

                                                @Override
                                                public void onFail(int code, String msg) {

                                                }
                                            });
                                        } else {
                                            WLog.i(TAG, "doGetAllBindAccount: 已被绑定");
                                            startActivity(new Intent(WishQRCodeActivity.this, WishBgmAlreadyBindActivity.class)
                                                    .putExtra("deviceId", deviceId)
                                                    .putExtra("boundUser", bean.get(0)));
                                        }
                                    }

                                    @Override
                                    public void onFail(int code, String msg) {
                                        WLog.i(TAG, "onFail: ");
                                        ToastUtil.show(msg);

                                    }
                                });

                            } else {
                                ToastUtil.show(getResources().getString(R.string.Scancode_Unrecognized));
                            }
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            ToastUtil.show(msg);

                        }
                    });
                } else {
                    WLog.i(TAG, "handleDecode:无效的二维码");
                    showRequestResultDialog(getString(R.string.Music_Scan_invalid));
                }
            } else {
                WLog.i(TAG, "handleDecode:无效的二维码");
                showRequestResultDialog(getString(R.string.Music_Scan_invalid));
            }
        } else if (flag == SCAN_ACTIVATE_ENTRANCE) {
            deviceApiUnit.doActiveDevice(deviceId, qrCodeId, "XW01", new DeviceApiUnit.DeviceApiCommonListener() {
                @Override
                public void onSuccess(Object bean) {
                    WLog.i(TAG, "设备激活成功: ");
                    deviceApiUnit.doBindDevice(deviceId, null, "XW01", new DeviceApiUnit.DeviceApiCommonListener() {
                        @Override
                        public void onSuccess(Object bean) {
                            WLog.i(TAG, "设备绑定成功: ");
                            startActivity(new Intent(WishQRCodeActivity.this, WishBgmAddResultActivity.class)
                                    .putExtra("result", "success")
                                    .putExtra("deviceId",deviceId));
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            WLog.i(TAG, "设备绑定失败: ");
                            startActivity(new Intent(WishQRCodeActivity.this, WishBgmAddResultActivity.class)
                                    .putExtra("result", "fail")
                                    .putExtra("deviceId",deviceId));
                        }
                    });

                }

                @Override
                public void onFail(int code, String msg) {
                    WLog.i(TAG, "设备激活失败: ");
                    if (20122 == code) {
                        showRequestResultDialog(getString(R.string.addDevice_XW01_activation_failure));
                    } else if (20123 == code) {
                        showRequestResultDialog(getString(R.string.addDevice_XW01_activation_no_use));
                    }

                }
            });

        }

    }


    private void showRequestResultDialog(String msg) {
        builder = new WLDialog.Builder(this);
        builder.setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setMessage(msg)
                .setPositiveButton(getString(R.string.Common_Retry))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        wlDialog.dismiss();
                        WishQRCodeActivity.this.finish();
                    }

                    @Override
                    public void onClickNegative(View view) {
                    }
                });
        wlDialog = builder.create();
        if (!wlDialog.isShowing()) {
            wlDialog.show();
        }
    }


    public boolean isJson(String content) {

        try {
            JSONObject jsonStr = JSONObject.parseObject(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            WLog.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a
            // RuntimeException.
            if (handler == null) {
                handler = new CaptureActivityHandler(this, cameraManager, DecodeThread.ALL_MODE);
            }

            initCrop();
        } catch (IOException ioe) {
            WLog.w(TAG, ioe);
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            WLog.w(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();
        }
    }

    private void displayFrameworkBugMessageAndExit() {
        // camera error
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage("Camera error");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }

        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        builder.show();
    }

    public void restartPreviewAfterDelay(long delayMS) {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
        }
    }

    public Rect getCropRect() {
        return mCropRect;
    }

    /**
     * 初始化截取的矩形区域
     */
    private void initCrop() {
        int cameraWidth = cameraManager.getCameraResolution().y;
        int cameraHeight = cameraManager.getCameraResolution().x;

        /** 获取布局中扫描框的位置信息 */
        int[] location = new int[2];
        scanCropView.getLocationInWindow(location);

        int cropLeft = location[0];
        int cropTop = location[1] - getStatusBarHeight();

        int cropWidth = scanCropView.getWidth();
        int cropHeight = scanCropView.getHeight();

        /** 获取布局容器的宽高 */
        int containerWidth = scanContainer.getWidth();
        int containerHeight = scanContainer.getHeight();

        /** 计算最终截取的矩形的左上角顶点x坐标 */
        int x = cropLeft * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的左上角顶点y坐标 */
        int y = cropTop * cameraHeight / containerHeight;

        /** 计算最终截取的矩形的宽度 */
        int width = cropWidth * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的高度 */
        int height = cropHeight * cameraHeight / containerHeight;

        /** 生成最终的截取的矩形 */
        mCropRect = new Rect(x, y, width + x, height + y);
    }

    private int getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}