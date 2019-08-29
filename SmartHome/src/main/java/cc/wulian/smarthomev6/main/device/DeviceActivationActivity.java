package cc.wulian.smarthomev6.main.device;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.MessageListenerAdapter;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.tools.zxing.activity.QRCodeActivity;
import cc.wulian.smarthomev6.support.utils.ConstantsUtil;

/**
 * 设备激活
 */
public class DeviceActivationActivity extends BaseTitleActivity {

    private Button mButtonScan;

    public static void start(Context context, String devID) {
        Intent intent = new Intent(context, DeviceActivationActivity.class);
        intent.putExtra("devID", devID);
        context.startActivity(intent);
    }

    private String mDeviceId = "";

    private WLDialog.Builder builder;
    private WLDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_activation, true);

        mDeviceId = getIntent().getStringExtra("devID");
        EventBus.getDefault().register(this);
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();

        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(mButtonScan, SkinResouceKey.BITMAP_BUTTON_BG_S);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initTitle() {
//        setToolBarTitle(R.string.Music_Scan_code);
        setToolBarTitleAndRightImg(R.string.Music_Scan_code, R.drawable.ic_delete);
    }

    @Override
    protected void initView() {
        mButtonScan = (Button) findViewById(R.id.activation_button_scan);
    }

    @Override
    protected void initListeners() {
        mButtonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.equals(Preference.getPreferences().getUserEnterType(), Preference.ENTER_TYPE_GW)) {
                    showDialog(getString(R.string.Music_Scan_local_area_network), getString(R.string.Music_Scan_ok), new Action() {
                        @Override
                        public void onAction() {

                        }
                    });
                } else {
                    QRCodeActivity.getCode(DeviceActivationActivity.this,
                            0,
                            getString(R.string.Music_Scan_code),
                            getString(R.string.Music_Scan_hint_2));
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        if (v.getId() == R.id.img_right) {
            showDelete(mDeviceId);
        }
    }

    protected void showDelete(final String deviceId) {
        builder = new WLDialog.Builder(this);
        builder.setCancelOnTouchOutSide(false)
                .setMessage(this.getString(R.string.Device_Delete))
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        ((MainApplication) getApplication())
                                .getMqttManager()
                                .publishEncryptedMessage(
                                        MQTTCmdHelper.createSetDeviceInfo(Preference.getPreferences().getCurrentGatewayID(), deviceId, 3, null, null),
                                        MQTTManager.MODE_GATEWAY_FIRST);
                        dialog.dismiss();
                        setResult(RESULT_OK);
                        finish();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && data != null) {
            String code = data.getStringExtra(ConstantsUtil.QR_CODE);
            if (!TextUtils.isEmpty(code)) {
                new DeviceApiUnit(DeviceActivationActivity.this).doActiveDevice(
                        mDeviceId,
                        code,
                        "DD",
                        new DeviceApiUnit.DeviceApiCommonListener() {
                            @Override
                            public void onSuccess(Object bean) {
                                showDialog(getString(R.string.Music_Scan_success), getString(R.string.Music_Scan_ok), new Action() {
                                    @Override
                                    public void onAction() {
                                        DeviceDetailActivity.start(DeviceActivationActivity.this, mDeviceId);
                                        finish();
                                    }
                                });
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                String s = "", sBtn = getString(R.string.Music_Scan_ok);
                                if (code == 20122) {
                                    s = getString(R.string.Music_Scan_invalid);
                                } else if (code == 20123) {
                                    s = getString(R.string.Music_Scan_Not_available);
                                } else {
                                    s = getString(R.string.Music_Scan_failure);
                                    sBtn = getString(R.string.Music_Scan_Retry);
                                }
                                showDialog(s, sBtn, new Action() {
                                    @Override
                                    public void onAction() {

                                    }
                                });
//                                ToastUtil.single(msg);
                            }
                        });
            }
        }
    }

    private interface Action {
        void onAction();
    }

    private void showDialog(String msg, String btn, final Action action) {
        builder = new WLDialog.Builder(this);
        builder.setCancelOnTouchOutSide(false)
                .setMessage(msg)
                .setPositiveButton(btn)
                .setListener(new MessageListenerAdapter() {
                    @Override
                    public void onClickPositive(View var1, String msg) {
                        action.onAction();
                    }
                });
        dialog = builder.create();
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event != null && event.device != null) {
            if (TextUtils.equals(event.device.devID, mDeviceId)) {
                if (event.device.mode == 3) {
                    finish();
                }
            }
        }
    }
}
