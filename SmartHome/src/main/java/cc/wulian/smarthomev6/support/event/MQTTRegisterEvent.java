package cc.wulian.smarthomev6.support.event;

import android.support.annotation.NonNull;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;

/**
 * Created by zbl on 2017/3/3.
 * MQTT注册状态变化
 * state 1 成功，0 失败，2 断开
 */

public class MQTTRegisterEvent {

    public static final int STATE_REGISTER_SUCCESS = 1;
    public static final int STATE_REGISTER_FAIL = 0;

    public int state;
    public String data;
    public String msg;

    public MQTTRegisterEvent(int state, String data) {
        this.state = state;
        this.data = data;
        switch (data) {
            case "11":
                msg = getString(R.string.GatewayLoginActivity_Toast_Gateway_LoginFail11);
                break;
            case "12":
                msg = getString(R.string.GatewayLoginActivity_Toast_Gateway_LoginFail12);
                break;
            case "13":
                msg = getString(R.string.GatewayLoginActivity_Toast_Gateway_LoginFail13);
                break;
            case "14":
                msg = getString(R.string.GatewayLoginActivity_Toast_Gateway_LoginFail14);
                break;
            case "15":
                msg = getString(R.string.GatewayLoginActivity_Toast_Gateway_LoginFail15);
                break;
            case "16":
                msg = getString(R.string.GatewayLoginActivity_Toast_Gateway_LoginFail16);
                break;
            case "-1":
                msg = getString(R.string.GatewayLoginActivity_Toast_Gateway_LoginFail);
                break;
        }
    }

    @NonNull
    private String getString(int resId) {
        return MainApplication.getApplication().getString(resId);
    }
}
