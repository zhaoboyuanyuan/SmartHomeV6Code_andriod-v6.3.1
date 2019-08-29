package cc.wulian.smarthomev6.support.utils.biometric;

import android.os.CancellationSignal;
import android.support.annotation.NonNull;

/**
 * Created by fenaming on 2018/10/16.
 */

interface IBiometricPromptImpl {

    void authenticate(@NonNull CancellationSignal cancel,
                      @NonNull BiometricPromptManager.OnBiometricIdentifyCallback callback);

}