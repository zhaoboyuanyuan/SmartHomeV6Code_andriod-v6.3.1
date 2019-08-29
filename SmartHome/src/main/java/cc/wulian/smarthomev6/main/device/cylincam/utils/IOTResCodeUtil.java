package cc.wulian.smarthomev6.main.device.cylincam.utils;

import android.content.Context;

import com.tutk.IOTC.Camera;

/**
 * Created by syf on 2016/10/18.
 */

public class IOTResCodeUtil {
    public static String transformStr(int resCode, Context mContext) {
        switch (resCode) {
            case Camera.CONNECTION_NOT_INITIALIZED:
                return "通道没有初始化,请退出重试";
            case Camera.CONNECTION_STATE_IOTC_WAKE_UP://唤醒
                return "设备唤醒中请稍后";
            case Camera.CONNECTION_ER_DEVICE_OFFLINE://设备不在线
                return "设备不在线";
            case Camera.CONNECTION_STATE_CONNECTED://连接成功
                return "设备通道连接成功";
            case Camera.CONNECTION_EXCEED_MAX_SESSION://会话超出限制
                return "设备会话超过最大限制";
            case Camera.CONNECTION_DEVICE_NOT_LISTENING://设备连接异常
                return "设备连接异常请稍后再试";
            case Camera.CONNECTION_STATE_TIMEOUT:
            case Camera.CONNECTION_STATE_IOTC_INVALID_SID:
            case Camera.CONNECTION_STATE_IOTC_SESSION_CLOSE_BY_REMOTE:
            case Camera.CONNECTION_STATE_CONNECT_FAILED:
                return "网络通道会话异常,请返回加载列表重试";
            case Camera.CONNECTION_STATE_IOTC_CLIENT_MAX:
                return "连接人数超限制";
            case Camera.CONNECTION_STATE_IOTC_NETWORK_IS_POOR:
                return "当前网络差，请等待";
            default:
                return "";
        }
    }
}
