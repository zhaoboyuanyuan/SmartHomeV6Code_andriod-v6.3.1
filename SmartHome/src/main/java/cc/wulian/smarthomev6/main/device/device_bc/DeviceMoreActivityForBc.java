package cc.wulian.smarthomev6.main.device.device_bc;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.DeviceMoreActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.ICamApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamLoginBean;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.GatewayConfigEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by Administrator on 2017/6/15.
 */

public class DeviceMoreActivityForBc extends DeviceMoreActivity {
    private String key521 = "";
    private String cameraId, wifiName;
    private DeviceApiUnit deviceApiUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isGet521Value = false;
        getConfigFrom521();
        configNet_Init();
    }

    @Override
    protected void initData() {
        super.initData();
        key521 = deviceId + "Bc";
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * 配网初始化
     */
    private void configNet_Init() {
        if (deviceApiUnit == null) {
            deviceApiUnit = new DeviceApiUnit(this);
        }
    }

    private void getConfigFrom521() {
        if (device != null) {
            MainApplication.getApplication().getMqttManager().publishEncryptedMessage(
                    MQTTCmdHelper.createGatewayConfig(
                            device.gwID,
                            3,
                            MainApplication.getApplication().getLocalInfo().appID,
                            device.devID,
                            key521,
                            null,
                            null
                    ), MQTTManager.MODE_GATEWAY_FIRST);
        }
    }

    boolean isGet521Value = false;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGatewayConfigEvent(GatewayConfigEvent event) {
        boolean isRight = event != null
                && event.bean != null
                && TextUtils.equals(event.bean.d, deviceId)
                && TextUtils.equals(event.bean.k, key521);
        if (isRight) {
            if (!StringUtil.isNullOrEmpty(event.bean.v)) {
                JSONObject jsonObject = null;
                try {
                    isGet521Value = true;
                    jsonObject = new JSONObject(event.bean.v);
                    cameraId = jsonObject.getString("cameraId");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
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
                            if (deviceApiUnit == null) {
                                deviceApiUnit = new DeviceApiUnit(DeviceMoreActivityForBc.this);
                            }
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
