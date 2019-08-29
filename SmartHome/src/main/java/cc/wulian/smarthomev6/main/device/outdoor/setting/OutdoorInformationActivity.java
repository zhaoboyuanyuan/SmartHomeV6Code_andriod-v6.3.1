package cc.wulian.smarthomev6.main.device.outdoor.setting;

import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wulian.sdk.android.ipc.rtcv2.IPCMsgController;
import com.wulian.sdk.android.ipc.rtcv2.message.IPCcameraXmlMsgEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.DeviceDetailMsg;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.support.callback.IcamMsgEventHandler;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.apiunit.ICamApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamDeviceBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamVersionInfoBean;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.CameraUtil;
import cc.wulian.smarthomev6.support.utils.DialogUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;
import cc.wulian.smarthomev6.support.utils.XmlHandler;

/**
 * created by huxc  on 2017/10/9.
 * func：户外摄像机详情界面
 * email: hxc242313@qq.com
 */

public class OutdoorInformationActivity extends BaseTitleActivity implements View.OnClickListener, IcamMsgEventHandler {
    private TextView tvDeviceType;
    private TextView tvDeviceNumber;
    private TextView tvFirmwareVersion;
    private TextView tvConnectWifi;
    private TextView tvWifiStrength;
    private TextView tvIpAddress;
    private TextView tvMacAddress;
    private ImageView ivUpdateVersion;
    private ICamDeviceBean iCamDeviceBean;
    private boolean checkDeviceVersion = true;
    private int deviceVersionCode;
    private boolean canBeUpdate = false;
    private WLDialog mNotifyUpdateDialog;
    private ICamVersionInfoBean iCamVersionInfoBean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_outdoor_information, true);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Device_Detail));
    }

    @Override
    protected void initView() {
        super.initView();
        tvDeviceType = (TextView) findViewById(R.id.tv_device_type);
        tvDeviceNumber = (TextView) findViewById(R.id.tv_device_number);
        tvFirmwareVersion = (TextView) findViewById(R.id.tv_firmware_version);
        tvConnectWifi = (TextView) findViewById(R.id.tv_connect_wifi);
        tvWifiStrength = (TextView) findViewById(R.id.tv_wifi_strength);
        tvIpAddress = (TextView) findViewById(R.id.tv_ip_address);
        tvMacAddress = (TextView) findViewById(R.id.tv_mac_address);
        ivUpdateVersion = (ImageView) findViewById(R.id.iv_new_version);
        tvFirmwareVersion.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initData() {
        super.initData();
        iCamDeviceBean = (ICamDeviceBean) getIntent().getSerializableExtra("ICamDeviceBean");
        tvDeviceNumber.setText(iCamDeviceBean.uniqueDeviceId);
        queryDetailInformation();
        getLatestFirmwareVersion();

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_firmware_version:
                if (canBeUpdate) {
//                    showUpdateDialog();
                }
                break;
        }
    }

    private void queryDetailInformation() {
        IPCMsgController.MsgQueryDeviceDescriptionInfo(iCamDeviceBean.uniqueDeviceId,
                iCamDeviceBean.sdomain);
    }

    private void getLatestFirmwareVersion() {
        checkDeviceVersion = true;
        IPCMsgController.MsgQueryFirewareVersion(iCamDeviceBean.uniqueDeviceId, iCamDeviceBean.sdomain);

    }

    private void setDeviceInformation(DeviceDetailMsg detailMsg) {
        if (TextUtils.equals("unknown", detailMsg.getWifi_ssid())) {
            tvConnectWifi.setText("——");
        } else {
            tvConnectWifi.setText(detailMsg.getWifi_ssid());
        }
        if (TextUtils.equals("0", detailMsg.getWifi_signal())) {
            tvWifiStrength.setText("——");
        } else {
            tvWifiStrength.setText(detailMsg.getWifi_signal() + "%");
        }
        if (TextUtils.isEmpty(detailMsg.getWifi_ip())) {
            tvIpAddress.setText("——");
        } else {
            tvIpAddress.setText(detailMsg.getWifi_ip());
        }
        tvMacAddress.setText(detailMsg.getWifi_mac());
        tvFirmwareVersion.setText(detailMsg.getVersion());
    }

    // 固件升级
    private void showUpdateDialog() {
        Resources rs = getResources();
        mNotifyUpdateDialog = DialogUtil.showCommonDialog(this,
                rs.getString(R.string.Update_Firmware) + " ",
                getString(R.string.Detection_New_Firmware) + iCamVersionInfoBean.version, rs.getString(R.string.Update_Now),
                rs.getString(R.string.Talk_Later),
                new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View var1, String msg) {
                        IPCMsgController.MsgNotifyFirewareUpdate(iCamDeviceBean.uniqueDeviceId, iCamDeviceBean.sdomain, iCamVersionInfoBean.version, iCamVersionInfoBean.code);
                        ivUpdateVersion.setVisibility(View.INVISIBLE);
                        tvFirmwareVersion.setText(getString(R.string.Firmware_Latest));
                        canBeUpdate = false;
                        mNotifyUpdateDialog.dismiss();
                    }

                    @Override
                    public void onClickNegative(View var1) {
                        mNotifyUpdateDialog.dismiss();
                    }
                });
        mNotifyUpdateDialog.show();
    }


    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(IPCcameraXmlMsgEvent event) {
        if (event.getCode() != 0) {
            Log.i("SettingSipMSg", "fail---" + "apiType = " + event.getApiType() + "msg = " + event.getMessage());
            switch (event.getApiType()) {
                case QUERY_DEVICE_DESCRIPTION_INFO:
                case QUERY_FIREWARE_VERSION:
                    ToastUtil.show(getString(R.string.Config_Query_Device_Fail));
                    break;
            }
        } else {
            switch (event.getApiType()) {
                case QUERY_DEVICE_DESCRIPTION_INFO:
                    DeviceDetailMsg detailMsg = XmlHandler
                            .getDeviceDetailMsg(event.getMessage());
                    if (detailMsg != null) {
                        setDeviceInformation(detailMsg);
                    }
                    break;
                case QUERY_FIREWARE_VERSION:
                    if (checkDeviceVersion) {
                        try {
                            deviceVersionCode = Integer.parseInt(CameraUtil.getParamFromXml(
                                    event.getMessage(), "version_id"));

                            ICamApiUnit iCamApiUnit = new ICamApiUnit(this, ApiConstant.getUserID());
                            iCamApiUnit.getVersionCheck(iCamDeviceBean.uniqueDeviceId.substring(0, 6), String.valueOf(deviceVersionCode), new ICamApiUnit.ICamApiCommonListener<ICamVersionInfoBean>() {
                                @Override
                                public void onSuccess(ICamVersionInfoBean bean) {
                                    iCamVersionInfoBean = bean;
                                    WLog.i("version_server", bean.code);
                                    WLog.i("version_camera", deviceVersionCode);
                                    if (bean.code > deviceVersionCode) {
                                        ivUpdateVersion.setVisibility(View.INVISIBLE);//暂时屏蔽手动固件升级
                                        canBeUpdate = true;
                                    } else {
                                        ivUpdateVersion.setVisibility(View.INVISIBLE);
                                    }
                                }

                                @Override
                                public void onFail(int code, String msg) {

                                }
                            });

                        } catch (NumberFormatException e) {
                            deviceVersionCode = 0;
                        }
                    }
                    break;
                case CONFIG_FIREWARE_UPDATE_MODE:
                    break;

            }
        }
    }
}

