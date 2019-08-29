package cc.wulian.smarthomev6.support.utils.biometric;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.device.Attribute;
import cc.wulian.smarthomev6.support.core.device.Cluster;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.Endpoint;
import cc.wulian.smarthomev6.support.core.device.EndpointParser;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * Created by fenaming on 2018/10/16.
 */

public class BiometricPromptDialog extends DialogFragment {

    public static final int STATE_NORMAL = 1;
    public static final int STATE_FAILED = 2;
    public static final int STATE_ERROR = 3;
    public static final int STATE_SUCCEED = 4;
    private TextView mToast;
    private TextView mnegativeBtn;
    private TextView mpositiveBtn;
    private static Activity mActivity;
    private String mPwd;
    private Device mDevice;
    private boolean hasSendCmd = false;

    private OnBiometricPromptDialogActionCallback mDialogActionCallback;

    public interface OnBiometricPromptDialogActionCallback {
        void onDialogDismiss();
        void onUsePassword();
        void onCancel();
    }

    public void initData(Device device, String pwd){
        mDevice = device;
        mPwd = pwd;
    }

    public static BiometricPromptDialog newInstance() {
        BiometricPromptDialog dialog = new BiometricPromptDialog();
        return dialog;
    }

    public void setOnBiometricPromptDialogActionCallback(OnBiometricPromptDialogActionCallback callback) {
        mDialogActionCallback = callback;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupWindow(getDialog().getWindow());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_biometric_prompt_dialog, container);

        EventBus.getDefault().register(this);
        RelativeLayout rootView = view.findViewById(R.id.root_view);
        rootView.setClickable(false);
        hasSendCmd = false;

        mToast = view.findViewById(R.id.jzm_lock_text_toast);
        mnegativeBtn = view.findViewById(R.id.dialog_btn_negative);
        mpositiveBtn = view.findViewById(R.id.dialog_btn_positive);

//        mUsePasswordBtn.setVisibility(View.GONE);
//        mUsePasswordBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mDialogActionCallback != null) {
//                    mDialogActionCallback.onUsePassword();
//                }
//
//                dismiss();
//            }
//        });
        mnegativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDialogActionCallback != null) {
                    mDialogActionCallback.onCancel();
                }
                dismiss();
            }
        });

        mpositiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDialogActionCallback != null) {
                    mDialogActionCallback.onUsePassword();
                }
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(R.color.transparent_background);
        }
        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        EventBus.getDefault().unregister(this);
        if (mDialogActionCallback != null) {
            mDialogActionCallback.onDialogDismiss();
        }
    }

    private void setupWindow(Window window) {
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.gravity = Gravity.CENTER;
            lp.dimAmount = 0;
            lp.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(lp);
            window.setBackgroundDrawableResource(R.color.viewfinder_mask);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    public void changeToast(String toast, boolean isShowed){
        mToast.setText(toast);
        if (isShowed){
            mToast.setVisibility(VISIBLE);
        }else{
            mToast.setVisibility(View.GONE);
        }
    }

    private void toast(String sss) {
        mToast.setText(sss);
        mToast.setVisibility(VISIBLE);
        mToast.removeCallbacks(toastRun);
        mToast.postDelayed(toastRun, 1500);
    }

    private void toast(@StringRes int sss) {
        mToast.setText(sss);
        mToast.setVisibility(VISIBLE);
        mToast.removeCallbacks(toastRun);
        mToast.postDelayed(toastRun, 1500);
    }

    private Runnable toastRun = new Runnable() {
        @Override
        public void run() {
            mToast.setVisibility(INVISIBLE);
        }
    };

    public void setState(int state) {
        switch (state) {
            case STATE_NORMAL:
                break;
            case STATE_FAILED:
//                ToastUtil.show("指纹验证失败");
                toast(R.string.Touch_ID_unlock_error_1);
                break;
            case STATE_ERROR:
//                ToastUtil.show("指纹验证错误");
                break;
            case STATE_SUCCEED:
                sendCmd(mPwd);
                hasSendCmd = true;
//                ToastUtil.show("指纹验证通过");
//                toast("指纹验证通过");
//                mToast.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        dismiss();
//                    }
//                }, 1200);
                break;
        }
    }

    private void sendCmd(String pwd) {

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

            mToast.postDelayed(checkTimeOut, 20000);

            toastCheckPwd(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void toastCheckPwd(boolean isChecking) {
        mToast.removeCallbacks(checkPwdRun);
        if (isChecking) {
            checkProcess = 1;
            showCheckPwd();
        } else {
            //toast.setVisibility(INVISIBLE);
        }
    }

    private void showCheckPwd() {
        mToast.postDelayed(checkPwdRun, 700);
    }

    private int checkProcess = 1;
    private Runnable checkPwdRun = new Runnable() {
        @Override
        public void run() {
            StringBuffer sb = new StringBuffer();
            sb.append(mActivity.getResources().getString(R.string.Device_Lock_Widget_Status));
            for (int i = 1; i <= checkProcess; i++) {
                sb.append(".");
            }
            for (int i = checkProcess; i <= 3; i++) {
                sb.append(" ");
            }

            changeToast(String.valueOf(sb), true);

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
            mToast.removeCallbacks(checkPwdRun);
            ToastUtil.show(R.string.Xuanwulakeseries_Widget_Requesttimeout);
            dismiss();
        }
    };

    private void updateState(int attributeId, String attributeValue) {
        if (attributeValue.isEmpty()) {
            return;
        }

        WLog.i("fzm", attributeId + " " + attributeValue);

        switch (attributeId) {
            case 0x8001:
                if (TextUtils.equals("4", attributeValue)) {
                    ToastUtil.show(R.string.Xuanwulakeseries_Widget_Errorsmore);
                    if (hasSendCmd){
                        hasSendCmd = false;
                        mToast.removeCallbacks(checkTimeOut);
                        dismiss();
                    }
                }
                break;
            case 0x8002:
                ToastUtil.show(R.string.Home_Widget_Lock_Opened);
                if (hasSendCmd){
                    hasSendCmd = false;
                    mToast.removeCallbacks(checkTimeOut);
                    dismiss();
                }
                break;
            case 0x8006:
                if (TextUtils.equals("1", attributeValue)) {
//                    toast("开锁失败，请重试");密码已经修改
                    ToastUtil.show(R.string.Touch_ID_unlock_error);
                    if (hasSendCmd){
                        hasSendCmd = false;
                        mToast.removeCallbacks(checkTimeOut);
                        dismiss();
                    }
                }else if (TextUtils.equals("2", attributeValue)) {
                    ToastUtil.show(R.string.Touch_ID_unlock_error);
                    if (hasSendCmd){
                        hasSendCmd = false;
                        mToast.removeCallbacks(checkTimeOut);
                        dismiss();
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
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null) {
            if (TextUtils.equals(event.device.devID, mDevice.devID)) {
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
            }
        }
    }
}