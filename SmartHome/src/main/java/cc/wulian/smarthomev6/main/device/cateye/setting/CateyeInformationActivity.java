package cc.wulian.smarthomev6.main.device.cateye.setting;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamDeviceBean;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.XmlHandler;

/**
 * Created by hxc on 2017/5/12.
 * 智能猫眼详情界面
 */

public class CateyeInformationActivity extends BaseTitleActivity implements IcamMsgEventHandler, View.OnClickListener {
    private TextView tvDeviceType;
    private TextView tvDeviceNumber;
    private TextView tvFirmwareVersion;
    private TextView tvConnectWifi;
    private TextView tvWifiStrength;
    private TextView tvIpAddress;
    private TextView tvMacAddress;
    private TextView tvBatteryLevel;
    private ICamDeviceBean iCamDeviceBean;
    private LinearLayout llOffline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_cateye_information, true);
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
        tvBatteryLevel = (TextView) findViewById(R.id.tv_battery_level);
        llOffline = (LinearLayout) findViewById(R.id.ll_offline);
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
        tvDeviceNumber.setText(iCamDeviceBean.did);
        if (iCamDeviceBean.online != 1) {
            llOffline.setVisibility(View.INVISIBLE);
        } else {
            String batteryLevel = getIntent().getStringExtra("batteryLevel");
            if (!StringUtil.isNullOrEmpty(batteryLevel)) {
                tvBatteryLevel.setText(batteryLevel + "%");
            }
            queryDetailInformation();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    private void queryDetailInformation() {
        IPCMsgController.MsgQueryDeviceDescriptionInfo(iCamDeviceBean.did,
                iCamDeviceBean.sdomain);
    }

    private void setDeviceInformation(DeviceDetailMsg detailMsg) {
        try {
            tvIpAddress.setText(detailMsg.getWifi_ip());
            tvWifiStrength.setText(detailMsg.getWifi_signal() + "%");
            tvMacAddress.setText(detailMsg.getWifi_mac());
            tvConnectWifi.setText(detailMsg.getWifi_ssid());
            tvFirmwareVersion.setText(detailMsg.getVersion());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(IPCcameraXmlMsgEvent event) {
        if (event.getCode() != 0) {
            Log.i("SettingSipMSg", "fail---" + "apiType = " + event.getApiType() + "msg = " + event.getMessage());
            switch (event.getApiType()) {
                case QUERY_DEVICE_DESCRIPTION_INFO:
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
            }
        }
    }
}
