package cc.wulian.smarthomev6.main.device.more;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.LocalInfo;
import cc.wulian.smarthomev6.entity.MoreConfig;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.IDeviceMore;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.device.Attribute;
import cc.wulian.smarthomev6.support.core.device.Cluster;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.Endpoint;
import cc.wulian.smarthomev6.support.core.device.EndpointParser;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.AESUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;
import cc.wulian.smarthomev6.support.utils.biometric.BiometricPromptManager;


/**
 * Created by fenaming on 2018/10/12.
 */

public class TouchIDUnlockView extends FrameLayout implements IDeviceMore {

    private Context context;
    private Activity mActivity;
    private AppCompatCheckBox checkBox;
    private Device mDevice;

    private String deviceID = "";
    private String gwID = "";
    private String mEncryptPwd;
    //    private String mPwd = "";
    private String mCurrPwd = "";
    private Preference mpreference;
    private boolean hasSendCmd = false;
    View rootView, layoutContent;
    private WLDialog mDialog;
    private BiometricPromptManager mManager;
    WLDialog.Builder builder;
    private static boolean isVerify;  //该字段是为了确保在其他界面的开锁成功消息上报后不处理

    public TouchIDUnlockView(Context context, String deviceID, String gwID) {
        super(context);
        this.context = context;
        mActivity = (Activity) context;
        this.gwID = gwID;
        this.deviceID = deviceID;
        mpreference = Preference.getPreferences();
        mManager = BiometricPromptManager.from(mActivity);
        initView(context);
    }

    @Override
    public void onBindView(MoreConfig.ItemBean bean) {
        EventBus.getDefault().register(this);
        if (mDevice != null && mDevice.isOnLine()) {
            mEncryptPwd = mpreference.getTouchIDunLock(deviceID);
            if (TextUtils.equals(mEncryptPwd, "")) {
                checkBox.setChecked(false);
            } else {
                checkBox.setChecked(true);
            }
        }
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    private void initView(final Context context) {
        rootView = LayoutInflater.from(context).inflate(R.layout.item_device_more_custom_touch_unlock, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layoutContent = rootView.findViewById(R.id.layoutContent);
        checkBox = (AppCompatCheckBox) rootView.findViewById(R.id.right_image);
        mEncryptPwd = mpreference.getTouchIDunLock(deviceID);
//        app中密码明文，可以考虑用UserID/TerminalId(是否有切换用户仍可使用需求)的字节流做Aes加密处理。或者getTerminalId
//        byte[] encryptPwd = AESUtil.encrypt(mPwd.getBytes(), ApiConstant.getTerminalId().getBytes());//存入mpreference中
//        mPwd = new String(AESUtil.decrypt(encryptPwd, ApiConstant.getTerminalId().getBytes()));//解密后的密码字节流

        if (mManager.isBiometricPromptEnable()) {
            if (TextUtils.equals(mEncryptPwd, "")) {
                checkBox.setChecked(false);
            } else {
                checkBox.setChecked(true);
            }
        } else {
            mEncryptPwd = "";
            mpreference.setTouchIDunLock(deviceID, "");
            checkBox.setChecked(false);
        }
        mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
        if (!mDevice.isOnLine()) {
            rootView.setEnabled(false);
            checkBox.setEnabled(false);
            layoutContent.setAlpha(0.54f);
        } else {
            rootView.setEnabled(true);
            checkBox.setEnabled(true);
            layoutContent.setAlpha(1f);
        }

        checkBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.equals(mEncryptPwd, "")) {
                    mpreference.setTouchIDunLock(deviceID, "");
                    checkBox.setChecked(false);
                    mEncryptPwd = "";
                    mCurrPwd = "";
                } else {
                    //弹窗提示密码开门
                    checkBox.setChecked(false);
                    if (isSupportBio()) {
                        hasSendCmd = false;
                        showVerificationDialog();
                    }
                }
            }
        });
    }

    private boolean isSupportBio() {
        if (!isAboveApi23()) {
            ToastUtil.show(getResources().getString(R.string.Touch_ID_not_sport));
            return false;
        }
        if (!mManager.isHardwareDetected()) {
            ToastUtil.show(getResources().getString(R.string.Touch_ID_not_sport));
            return false;
        }
        if (!(mManager.hasEnrolledFingerprints()
                && mManager.isKeyguardSecure())) {
            ToastUtil.show(R.string.Touch_ID_no_one);
            return false;
        }

        return true;
    }

    private boolean isAboveApi23() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    private void showVerificationDialog() {
        builder = new WLDialog.Builder(context);
        builder.setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setTitle(R.string.Touch_ID_enter_password)
                .inputNumberPassword(true)
                .setCancelable(false)
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        if (msg != null) {
                            if (TextUtils.equals("", msg)) {
                                ToastUtil.show(R.string.Cateye_Connect_WiFi_NOPWD_Tip);
                            } else if (msg.length() < 6) {
                                ToastUtil.show(R.string.Register_tips03);
                            } else {
                                mCurrPwd = msg;
                                hasSendCmd = true;

                                sendCmd(msg);
                            }
                        } else {
                            ToastUtil.show(R.string.Cateye_Connect_WiFi_NOPWD_Tip);
                        }
                    }

                    @Override
                    public void onClickNegative(View view) {
                        mDialog.dismiss();
                    }
                });
        mDialog = builder.create();
        mDialog.show();
    }

    private void sendCmd(String pwd) {
        isVerify = true;
        // 设备不在线
        if (!mDevice.isOnLine()) {
            return;
        }
        org.json.JSONObject object = new org.json.JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", mDevice.gwID);
            object.put("devID", mDevice.devID);
            object.put("clusterId", 257);
            object.put("commandType", 1);
            object.put("commandId", 32772);
            object.put("endpointNumber", 1);
            JSONArray array = new JSONArray();
            array.put(pwd);
            object.put("parameter", array);

            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);

            postDelayed(checkTimeOut, 20000);

            toastCheckPwd(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void toastCheckPwd(boolean isChecking) {
        removeCallbacks(checkPwdRun);
        if (isChecking) {
            checkProcess = 1;
            showCheckPwd();
        } else {
            //toast.setVisibility(INVISIBLE);
        }
    }

    private void showCheckPwd() {
        postDelayed(checkPwdRun, 700);
    }

    private int checkProcess = 1;
    private Runnable checkPwdRun = new Runnable() {
        @Override
        public void run() {
            StringBuffer sb = new StringBuffer();
            sb.append(getResources().getString(R.string.Device_Lock_Widget_Status));
            for (int i = 1; i <= checkProcess; i++) {
                sb.append(".");
            }
            for (int i = checkProcess; i <= 3; i++) {
                sb.append(" ");
            }

            builder.changeToast(String.valueOf(sb), true);

            if (++checkProcess > 3) {
                checkProcess = 1;
            }
            showCheckPwd();
        }
    };

    /**
     * 判断超时
     */
    private Runnable checkTimeOut = new Runnable() {
        @Override
        public void run() {
            removeCallbacks(checkPwdRun);
            ToastUtil.show(R.string.Xuanwulakeseries_Widget_Requesttimeout);
            if (null != mDialog) {
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        }
    };

    private void updateState() {
        if (mDevice.isOnLine()) {
            rootView.setEnabled(true);
            checkBox.setEnabled(true);
            layoutContent.setAlpha(1f);
        } else {
            rootView.setEnabled(false);
            checkBox.setEnabled(false);
            layoutContent.setAlpha(0.54f);
        }
    }

    private void updateState(int attributeId, String attributeValue) {
        // 验证完成
        toastCheckPwd(false);
        if (attributeValue.isEmpty()) {
            return;
        }

        if (!isVerify) {
            return;
        }

        WLog.i("fzm", attributeId + " " + attributeValue);

        switch (attributeId) {
            case 0x8001:
                if (TextUtils.equals("4", attributeValue)) {
                    ToastUtil.show(R.string.Xuanwulakeseries_Widget_Errorsmore);
                    if (hasSendCmd) {
                        hasSendCmd = false;
                        removeCallbacks(checkTimeOut);
                        if (null != mDialog) {
                            if (mDialog.isShowing()) {
                                mDialog.dismiss();
                            }
                        }
                    }
                }
                break;
            case 0x8002:
                ToastUtil.show(R.string.Touch_ID_set_success);
                LocalInfo localInfo = MainApplication.getApplication().getLocalInfo();
                mEncryptPwd = AESUtil.encrypt(mCurrPwd, localInfo.appID, true);
                mpreference.setTouchIDunLock(deviceID, mEncryptPwd);
                checkBox.setChecked(true);

                if (hasSendCmd) {
                    hasSendCmd = false;
                    removeCallbacks(checkTimeOut);
                    if (null != mDialog) {
                        if (mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                    }
                }
                break;
            case 0x8006:
                if (TextUtils.equals("1", attributeValue)) {
                    ToastUtil.show(R.string.Touch_ID_password_error);
                    if (hasSendCmd) {
                        hasSendCmd = false;
                        removeCallbacks(checkTimeOut);
                        if (null != mDialog) {
                            if (mDialog.isShowing()) {
                                mDialog.dismiss();
                            }
                        }
                    }
                }
                if (TextUtils.equals("2", attributeValue)) {
                    ToastUtil.show(R.string.Touch_ID_password_error);
                    if (hasSendCmd) {
                        hasSendCmd = false;
                        removeCallbacks(checkTimeOut);
                        if (null != mDialog) {
                            if (mDialog.isShowing()) {
                                mDialog.dismiss();
                            }
                        }
                    }
                }
                break;
            case 0x8007:
            case 0x8008:
        }
    }

    private void dealData(Device device) {
        EndpointParser.parse(device, new EndpointParser.ParserCallback() {
            @Override
            public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                // 更新状态
                updateState(attribute.attributeId, attribute.attributeValue);
            }
        });
        isVerify = false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null) {
            if (TextUtils.equals(event.device.devID, deviceID)) {

                if (event.device.mode == 3) {
                    // 设备删除
                } else if (event.device.mode == 2) {
                    // 设备离线
                } else if (event.device.mode == 1) {
                    // 更新上线
                    dealData(JSON.parseObject(event.device.data, Device.class));
                } else if (event.device.mode == 0) {
                    dealData(JSON.parseObject(event.device.data, Device.class));
                }
                updateState();
            }
        }
    }
}
