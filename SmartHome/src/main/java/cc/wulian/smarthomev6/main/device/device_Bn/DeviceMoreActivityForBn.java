package cc.wulian.smarthomev6.main.device.device_Bn;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.DeviceMoreActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.GatewayConfigEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by hxc on 2017/6/15.
 */

public class DeviceMoreActivityForBn extends DeviceMoreActivity {
    private String cameraId;
    private DeviceApiUnit deviceApiUnit;
    private DataApiUnit dataApiUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initData() {
        super.initData();
        dataApiUnit = new DataApiUnit(this);
        deviceApiUnit = new DeviceApiUnit(this);
        getCameraIdByCloud();
    }


    /**
     * 获取bn锁的摄像机id
     */
    private void getCameraIdByCloud() {
        dataApiUnit.doGetBcCameraId(deviceId, "Bn", new DataApiUnit.DataApiCommonListener<String>() {
            @Override
            public void onSuccess(String bean) {
                if (!TextUtils.isEmpty(bean)) {
                    cameraId = bean;
                }
            }

            @Override
            public void onFail(int code, String msg) {


            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGatewayConfigEvent(GatewayConfigEvent event) {
    }


    @Override
    protected void showDelete(final String deviceId) {
        builder = new WLDialog.Builder(this);
        builder.setCancelOnTouchOutSide(false)
                .setMessage(this.getString(R.string.Device_Delete))
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        if (StringUtil.isNullOrEmpty(cameraId)) {
                            sendDelete();
                        } else {
                            WLog.d(TAG, "#####发送解绑命令");
                            deviceApiUnit.doUnBindDevice(cameraId, listener);
                        }
                    }

                    @Override
                    public void onClickNegative(View var1) {

                    }
                });
        dialog = builder.create();
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    final DeviceApiUnit.DeviceApiCommonListener<Object> listener = new DeviceApiUnit.DeviceApiCommonListener<Object>() {
        @Override
        public void onSuccess(Object bean) {
            //ToastUtil.single("解绑成功，开始删除!");
            sendDelete();
        }

        @Override
        public void onFail(int code, String msg) {
            ToastUtil.single(R.string.Device_UNBind_Fail);
        }
    };

    private void sendDelete() {
        WLog.d(TAG, "#####发送删除命令");
        ((MainApplication) getApplication())
                .getMqttManager()
                .publishEncryptedMessage(
                        MQTTCmdHelper.createSetDeviceInfo(Preference.getPreferences().getCurrentGatewayID(), deviceId, 3, null, null),
                        MQTTManager.MODE_GATEWAY_FIRST);
        dialog.dismiss();
        setResult(RESULT_OK, null);
        finish();
    }
}
