package cc.wulian.smarthomev6.support.utils.biometric;

import android.app.Activity;
import android.text.TextUtils;

import cc.wulian.smarthomev6.main.device.UnlockAutoDialogActivity;
import cc.wulian.smarthomev6.support.core.device.Device;

/**
 * Created by fenaming on 2018/10/17.
 */

public class BiometricListener {
    private Activity mActivity;
    private BiometricPromptManager mManager;
    private String mPwd;
    private Device mDevice;

    public BiometricListener(Activity activity, Device device, String pwd) {
        this.mActivity = activity;
        mDevice = device;
        mPwd = pwd;
    }

    public void handle() {
        mManager = BiometricPromptManager.from(mActivity);
        mManager.initData(mDevice, mPwd);
        if (mManager.isBiometricPromptEnable()) {
            mManager.authenticate(new BiometricPromptManager.OnBiometricIdentifyCallback() {
                @Override
                public void onUsePassword() {
                    if (TextUtils.equals("Bc", mDevice.type) || TextUtils.equals("Bn", mDevice.type)) {
                        UnlockAutoDialogActivity.start(mActivity, mDevice.devID);
                    }
                }

                @Override
                public void onSucceeded() {
                }

                @Override
                public void onFailed() {
                }

                @Override
                public void onError(int code, String reason) {
                }

                @Override
                public void onCancel() {
                }
            });
        } else {
            if (TextUtils.equals("Bc", mDevice.type) || TextUtils.equals("Bn", mDevice.type)) {
                UnlockAutoDialogActivity.start(mActivity, mDevice.devID);
            }
        }
    }
}
