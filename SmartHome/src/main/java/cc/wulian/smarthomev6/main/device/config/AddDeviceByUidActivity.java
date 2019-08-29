package cc.wulian.smarthomev6.main.device.config;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.AddGateway06Activity;
import cc.wulian.smarthomev6.main.device.AddGateway11Activity;
import cc.wulian.smarthomev6.main.device.gateway_mini.config.MiniGatewayActivity;
import cc.wulian.smarthomev6.main.device.gateway_wall.config.WallGatewayActivity;
import cc.wulian.smarthomev6.main.mine.gatewaycenter.GatewayBindActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.CheckV6SupportBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceSupportV6Bean;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.tools.zxing.activity.QRCodeActivity;
import cc.wulian.smarthomev6.support.utils.ConstantsUtil;
import cc.wulian.smarthomev6.support.utils.DialogUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by hxc on 2017/5/5.
 * func:手动输入uid添加摄像机界面
 */

public class AddDeviceByUidActivity extends BaseTitleActivity {
    private Button btnNextStep;
    private EditText etUid;
    private String cameraType = "";
    private String gwType = "";
    private String scanType;
    private WLDialog unknownDeviceDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device_by_uid, true);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Add_Device));
    }

    @Override
    protected void initView() {
        super.initView();
        btnNextStep = (Button) findViewById(R.id.btn_next_step);
        etUid = (EditText) findViewById(R.id.et_input_uid);
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(btnNextStep, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(btnNextStep, SkinResouceKey.COLOR_BUTTON_TEXT);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        btnNextStep.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        scanType = getIntent().getStringExtra("scanType");

    }

    private boolean contains(String type){
        for(int i=0;i<QRCodeActivity.addDeviceWhiteList.length; i++){
            if(QRCodeActivity.addDeviceWhiteList[i].equals(type)){
                return true;
            }
        }
        return false;
    }

    private void showUnknownDeviceDialog() {
        unknownDeviceDialog = DialogUtil.showUnknownDeviceTips(this, new WLDialog.MessageListener() {
            @Override
            public void onClickPositive(View var1, String msg) {
                unknownDeviceDialog.dismiss();
            }

            @Override
            public void onClickNegative(View var1) {

            }
        }, getString(R.string.Scancode_Unrecognized_ID));
        unknownDeviceDialog.show();
    }

    /**
     * 判断摄像机绑定还是wifi配置
     *
     * @param cameraId
     */
    private void judgeBindOrConfig(String cameraId) {
        if (!TextUtils.isEmpty(cameraType)) {
            switch (cameraType) {
                case "CMICA1":
                    startActivity(new Intent(this, DeviceStartConfigActivity.class).
                            putExtra("deviceId", cameraId).
                            putExtra("scanType", scanType).
                            putExtra("isBindDevice", true).
                            putExtra("deviceType", cameraType));
                    finish();
                    break;
                case "CMICA2":
                case "CMICA3":
                case "CMICA4":
                case "CMICA5":
                case "CMICA6":
                    judgeDifferentEntry(cameraId);
                    break;
                default:
                    showUnknownDeviceDialog();
                    break;

            }
        }
    }


    //根据不同的扫码入口判断是否弹框提示绑定摄像机或者网关
    private void judgeDifferentEntry(String cameraId) {
        if (scanType.equals(ConstantsUtil.DEVICE_SCAN_ENTRY)) {
            switch (cameraType) {
                case "CMICA2":
                    showScanTipsDialog(cameraId,
                            getString(R.string.Scancode_Lookever_SelectAddtype), getString(R.string.Lookever), getString(R.string.Lookever_Gateway));
                    break;
                case "CMICA3":
                case "CMICA6":
                    showScanTipsDialog(cameraId,
                            getString(R.string.Scancode_Penguin_SelectAddtype), getString(R.string.Penguin), getString(R.string.Penguin_Gateway));
                    break;
                case "CMICA4":
                    showScanTipsDialog(cameraId,
                            getString(R.string.Scan_Cylinacam_Tip), getString(R.string.Cylincam), getString(R.string.Cylincam_Gateway));
                    break;
                case "CMICA5":
                    showScanTipsDialog(cameraId,
                            getString(R.string.Scancode_Outdoor_SelectAddtype), getString(R.string.Device_Default_Name_CMICA5), getString(R.string.Outdoor_Camera_Gateway));
                    break;
            }
        } else if (TextUtils.equals(scanType, ConstantsUtil.CAMERA_ADD_ENTRY)) {
            if (TextUtils.equals(cameraType, "CMICA4")) {
                startActivity(new Intent(this, DeviceStartConfigActivity.class).
                        putExtra("deviceId", cameraId).putExtra("scanType", scanType).putExtra("isBindDevice", false).putExtra("deviceType", cameraType));
            } else {
                startActivity(new Intent(this, DeviceStartConfigActivity.class).
                        putExtra("deviceId", cameraId).putExtra("scanType", scanType).putExtra("isBindDevice", true).putExtra("deviceType", cameraType));
            }
            finish();
        }
    }

    /**
     * 摄像机扫描提示dialog
     *
     * @param deviceId
     * @param msg
     * @param posMsg
     * @param negMSg
     */
    private void showScanTipsDialog(final String deviceId, String msg, String posMsg, String negMSg) {
        DialogUtil.showConfigOrBindDialog(this, msg, posMsg, negMSg, new WLDialog.MessageListener() {
            @Override
            public void onClickPositive(View var1, String msg) {
                if (TextUtils.equals("CMICA4",cameraType)) {
                    startActivity(new Intent(AddDeviceByUidActivity.this, DeviceStartConfigActivity.class).
                            putExtra("deviceId", deviceId).putExtra("scanType", scanType).putExtra("isBindDevice", false).putExtra("deviceType", cameraType));
                } else {
                    startActivity(new Intent(AddDeviceByUidActivity.this, DeviceStartConfigActivity.class).
                            putExtra("deviceId", deviceId).putExtra("scanType", scanType).putExtra("isBindDevice", true).putExtra("deviceType", cameraType));
                }
                finish();
            }

            @Override
            public void onClickNegative(View var1) {
                scanType = ConstantsUtil.BIND_CAMERA_GATEWAY_ENTRY;
                if (TextUtils.equals("CMICA4",cameraType)) {
                    startActivity(new Intent(AddDeviceByUidActivity.this, DeviceStartConfigActivity.class)
                            .putExtra("asGateway", ConstantsUtil.NEED_JUMP_BIND_GATEWAY_FLAG).putExtra("deviceId", deviceId).putExtra("scanType", scanType).putExtra("isBindDevice", false).putExtra("deviceType", cameraType));
                } else {
                    startActivity(new Intent(AddDeviceByUidActivity.this, DeviceStartConfigActivity.class)
                            .putExtra("asGateway", ConstantsUtil.NEED_JUMP_BIND_GATEWAY_FLAG).putExtra("deviceId", deviceId).putExtra("scanType", scanType).putExtra("isBindDevice", true).putExtra("deviceType", cameraType));
                }
                finish();
            }
        }).show();
    }


    /**
     * 跳网关设备的配网
     *
     * @param gwId
     */

    private void jumpToGatewayConfig(String gwId) {
        if (TextUtils.equals(gwType, "GW04")) {//mini 网关
            showMiniTipsDialog(gwId, getString(R.string.Minigateway_Scancode_Popup),
                    getString(R.string.Minigateway_Popup_Goconfiguration), getString(R.string.Minigateway_Popup_Alreadyconfigured));
        } else if (TextUtils.equals(gwType, "GW06") || TextUtils.equals(gwType, "GW12")) {//02增强型网关,网关02型(局域网)
            AddGateway06Activity.start(this, gwId, gwType);
            finish();
        } else if (TextUtils.equals(gwType, "GW08")) {//墙面网关
            showGWWallTipsDialog(gwId, getString(R.string.Gateway_Dialog_Chose_Tip),
                    getString(R.string.Minigateway_Popup_Goconfiguration), getString(R.string.Minigateway_Popup_Alreadyconfigured));
        } else if (TextUtils.equals(gwType, "GW11")) {//吸顶网关
            AddGateway11Activity.start(this, gwId, gwType);
            finish();
        } else {
            setResult(RESULT_OK, new Intent().putExtra("type", gwType).putExtra("id", gwId));
            finish();
        }
    }

    /**
     * mini网关扫描提示dialog
     *
     * @param deviceId
     * @param msg
     * @param posMsg
     * @param negMSg
     */
    private void showMiniTipsDialog(final String deviceId, String msg, String posMsg, String negMSg) {
        DialogUtil.showConfigOrBindDialog(this, msg, posMsg, negMSg, new WLDialog.MessageListener() {
            @Override
            public void onClickPositive(View var1, String msg) {
                startActivity(new Intent(AddDeviceByUidActivity.this, MiniGatewayActivity.class).
                        putExtra("deviceId", deviceId));
                finish();
            }

            @Override
            public void onClickNegative(View var1) {
                finish();
            }
        }).show();
    }

    /**
     * 墙面网关扫描提示dialog
     * @param msg
     * @param posMsg
     * @param negMSg
     */
    private void showGWWallTipsDialog(final String gwId, String msg, String posMsg, String negMSg) {
        DialogUtil.showConfigOrBindDialog(this, msg, posMsg, negMSg, new WLDialog.MessageListener() {
            @Override
            public void onClickPositive(View var1, String msg) {
                startActivity(new Intent(AddDeviceByUidActivity.this, WallGatewayActivity.class).
                        putExtra("deviceId", gwId).putExtra("scanEntry",ConstantsUtil.DEVICE_SCAN_ENTRY));
                finish();
            }

            @Override
            public void onClickNegative(View var1) {
                GatewayBindActivity.start(AddDeviceByUidActivity.this, gwId, true);
                finish();
            }
        }).show();
    }



    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.btn_next_step) {
            DeviceApiUnit deviceApiUnit = new DeviceApiUnit(this);
            deviceApiUnit.doCheckV6Support(etUid.getText().toString(), new DeviceApiUnit.DeviceApiCommonListener<CheckV6SupportBean>() {
                @Override
                public void onSuccess(CheckV6SupportBean bean) {
                    List<CheckV6SupportBean.SupportDevice> supportList = bean.device;
                    CheckV6SupportBean.SupportDevice gwDevice = null;
                    CheckV6SupportBean.SupportDevice cameraDevice = null;
                    CheckV6SupportBean.SupportDevice commonDevice = null;
                    for (CheckV6SupportBean.SupportDevice device :
                            supportList) {
                        if(preference.isAuthGateway() && !contains(device.type)){
                            ToastUtil.show(R.string.addDevice_toast_03);
                            return;
                        }
                        if (device.deviceFlag == 2) {
                            cameraDevice = device;
                            cameraType = cameraDevice.type;
                        } else if (device.deviceFlag == 1) {
                            gwDevice = device;
                            gwType = gwDevice.type;
                        } else if (device.deviceFlag == 3) {
                            commonDevice = device;
                        } else if (device.deviceFlag == 0) {
                            showUnknownDeviceDialog();
                        }
                    }
                    if (cameraDevice != null) {
                        if (cameraDevice.supportV6Flag != 0) {
                            judgeBindOrConfig(cameraDevice.deviceId);
                        } else {
                            showUnknownDeviceDialog();
                        }
                    } else if (gwDevice != null) {
                        if (gwDevice.supportV6Flag != 0) {
                            jumpToGatewayConfig(gwDevice.deviceId);
                        } else {
                            showUnknownDeviceDialog();
                        }
                    } else if (commonDevice != null) {
                        if (commonDevice.supportV6Flag != 0) {
                            setResult(RESULT_OK, new Intent().putExtra("type", commonDevice.type).putExtra("id", commonDevice.deviceId));
                            finish();
                        } else {
                            showUnknownDeviceDialog();
                        }
                    }
                }

                @Override
                public void onFail(int code, String msg) {

                }
            });
        }
    }
}
