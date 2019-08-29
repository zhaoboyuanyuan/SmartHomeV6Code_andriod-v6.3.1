package cc.wulian.smarthomev6.main.mine.gatewaycenter;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.AddDeviceActivity;
import cc.wulian.smarthomev6.main.device.DeviceInfoActivity;
import cc.wulian.smarthomev6.main.device.DeviceMoreActivity;
import cc.wulian.smarthomev6.main.device.device_if02.config.AddIF02DeviceActivity;
import cc.wulian.smarthomev6.main.h5.WVJBWebViewClient;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceBean;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.GatewayInfoBean;
import cc.wulian.smarthomev6.support.event.GatewayInfoEvent;
import cc.wulian.smarthomev6.support.event.GatewaySoftwareUpdateEvent;
import cc.wulian.smarthomev6.support.event.GatewayStateChangedEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.CameraUtil;
import cc.wulian.smarthomev6.support.utils.LanguageUtil;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by mamengchao on 2017/2/28 0028.
 * Tips:网关设置
 */

public class GatewaySettingActivity extends BaseTitleActivity implements CompoundButton.OnCheckedChangeListener {

    private String[] zoneArray = {"GMT±0", "GMT+1", "GMT+2", "GMT+3", "GMT+4", "GMT+5", "GMT+6", "GMT+7", "GMT+8", "GMT+9", "GMT+10", "GMT+11", "GMT±12"
            , "GMT-11", "GMT-10", "GMT-9", "GMT-8", "GMT-7", "GMT-6", "GMT-5", "GMT-4", "GMT-3", "GMT-2", "GMT-1"};
    private String[] zoneArrayValue = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
            "-11", "-10", "-9", "-8", "-7", "-6", "-5", "-4", "-3", "-2", "-1"};
    private static final String UNBIND = "UNBIND";
    private static final String LED = "LED";
    private static final String WIFI = "WIFI";
    private static final int REQUEST_CODE_CHANGE_PASSWORD = 1;
    private static final int REQUEST_CODE_CHANGE_ZONE = 2;
    private static final int REQUEST_CODE_CHANGE_ZONE_CMICA4 = 3;
    private RelativeLayout itemGatewaySettingName;
    private RelativeLayout itemGatewaySettingInfo;
    private RelativeLayout itemDeviceInfo;
    private RelativeLayout itemGatewaySettingLed;
    private RelativeLayout itemGatewaySettingWifi;
    private RelativeLayout itemGatewaySettingZone;
    private RelativeLayout itemGatewaySettingLocation;
    private RelativeLayout itemGatewayCenterNotify;
    private RelativeLayout itemGatewayCenterBackupRecovery;
    private RelativeLayout itemGatewayCenterWifiConfig;
    private LinearLayout itemWanPortSetting;

    private TextView tvGatewaySettingName;
    private TextView tvGatewaySettingHint;
    private TextView tvGatewaySettingInfoHint;
    private TextView tvGatewaySettingInfo;
    private TextView tvGatewaySettingLed;
    private TextView tvGatewaySettingWifi;
    private TextView tvGatewaySettingZone;
    private TextView tvGatewaySettingZoneHint;
    private TextView tvGatewaySettingLocation;
    private TextView tvGatewaySettingLocationHint;
    private TextView tvLocationTips;
    private TextView tvBackUpRecovery;
    private TextView tvGatewayWifiConfig;
    private TextView tvWanPortSetting;
    private Button unBindButton;
    private ToggleButton ledToggleButton;
    private ToggleButton wifiToggleButton;
    private View iv_gateway_update;
    private int ledSwitch = 1;
    private int wifiSwitch = 1;

    private WLDialog.Builder builder;
    private WLDialog dialog;

    //    private DeviceBean deviceBean;
    private DeviceApiUnit deviceApiUnit;
    private GatewayInfoBean gatewayInfoBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway_setting, true);
        EventBus.getDefault().register(this);
        deviceApiUnit = new DeviceApiUnit(this);
        deviceApiUnit.checkGatewaySoftwareUpdate();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void initTitle() {
        setToolBarTitle(getString(R.string.GatewayCenter_GatewaySetts));
    }

    @Override
    protected void initView() {
        itemGatewaySettingName = (RelativeLayout) findViewById(R.id.item_gateway_setting_name);
        itemDeviceInfo = (RelativeLayout) findViewById(R.id.item_device_info);
        itemGatewaySettingInfo = (RelativeLayout) findViewById(R.id.item_gateway_setting_info);
        itemGatewaySettingLed = (RelativeLayout) findViewById(R.id.item_gateway_setting_led);
        itemGatewaySettingWifi = (RelativeLayout) findViewById(R.id.item_gateway_setting_wifi);
        itemGatewaySettingZone = (RelativeLayout) findViewById(R.id.item_gateway_zone);
        itemGatewaySettingLocation = (RelativeLayout) findViewById(R.id.item_gateway_location);
        itemGatewayCenterNotify = (RelativeLayout) findViewById(R.id.item_gateway_center_notifi);
        itemGatewayCenterBackupRecovery = (RelativeLayout) findViewById(R.id.item_gateway_center_backup_recovery);
        itemGatewayCenterWifiConfig = (RelativeLayout) findViewById(R.id.item_gateway_center_wifi_config);
        itemWanPortSetting = (LinearLayout) findViewById(R.id.item_wan_port_setting);

        tvGatewaySettingName = (TextView) findViewById(R.id.tv_gateway_setting_name);
        tvGatewaySettingHint = (TextView) findViewById(R.id.item_gateway_setting_name_hint);
        tvGatewaySettingInfoHint = (TextView) findViewById(R.id.left_info);
        tvGatewaySettingInfo = (TextView) findViewById(R.id.item_gateway_setting_name_info);
        tvGatewaySettingLed = (TextView) findViewById(R.id.tv_gateway_setting_led);
        tvGatewaySettingWifi = (TextView) findViewById(R.id.tv_gateway_setting_wifi);
        tvGatewaySettingZone = (TextView) findViewById(R.id.tv_gateway_setting_zone_name);
        tvGatewaySettingZoneHint = (TextView) findViewById(R.id.item_gateway_setting_zone);
        tvGatewaySettingLocation = (TextView) findViewById(R.id.tv_gateway_setting_location_name);
        tvGatewaySettingLocationHint = (TextView) findViewById(R.id.item_gateway_setting_position);
        tvLocationTips = (TextView) findViewById(R.id.tv_location_tips);
        tvBackUpRecovery = (TextView) findViewById(R.id.tv_backup_recovery);
        tvGatewayWifiConfig = (TextView) findViewById(R.id.tv_wifi_config);
        tvWanPortSetting = (TextView) findViewById(R.id.tv_wan_port_setting);

        ledToggleButton = (ToggleButton) findViewById(R.id.tb_gateway_setting_led);
        wifiToggleButton = (ToggleButton) findViewById(R.id.tb_gateway_setting_wifi);
        unBindButton = (Button) findViewById(R.id.unbind_button);
        iv_gateway_update = findViewById(R.id.iv_gateway_update);

        if ("1".equals(preference.getCurrentGatewayState()) && !preference.isAuthGateway()) {
            itemGatewaySettingName.setEnabled(true);
            itemGatewaySettingInfo.setEnabled(true);
            itemGatewaySettingZone.setEnabled(true);
            itemGatewaySettingLocation.setEnabled(true);
            itemGatewayCenterBackupRecovery.setEnabled(true);
            tvGatewaySettingName.setEnabled(true);
            ledToggleButton.setEnabled(true);
            wifiToggleButton.setEnabled(true);
//            itemDeviceInfo.setEnabled(true);
            tvGatewaySettingHint.setTextColor(getResources().getColor(R.color.black));
            tvGatewaySettingInfo.setTextColor(getResources().getColor(R.color.black));
            tvGatewaySettingWifi.setTextColor(getResources().getColor(R.color.black));
            tvGatewaySettingLed.setTextColor(getResources().getColor(R.color.black));
            tvGatewaySettingZone.setTextColor(getResources().getColor(R.color.black));
            tvGatewaySettingLocation.setTextColor(getResources().getColor(R.color.black));
            tvBackUpRecovery.setTextColor(getResources().getColor(R.color.black));
//            tvGatewaySettingInfoHint.setTextColor(getResources().getColor(R.color.black));

        } else {
            itemGatewaySettingName.setEnabled(false);
            itemGatewaySettingInfo.setEnabled(false);
            itemGatewaySettingZone.setEnabled(false);
            itemGatewaySettingLocation.setEnabled(false);
            itemGatewayCenterBackupRecovery.setEnabled(false);
            tvGatewaySettingName.setEnabled(false);
            ledToggleButton.setEnabled(false);
            wifiToggleButton.setEnabled(false);
//            itemDeviceInfo.setEnabled(false);
            tvGatewaySettingHint.setTextColor(getResources().getColor(R.color.grey));
            tvGatewaySettingInfo.setTextColor(getResources().getColor(R.color.grey));
            tvGatewaySettingLed.setTextColor(getResources().getColor(R.color.grey));
            tvGatewaySettingWifi.setTextColor(getResources().getColor(R.color.grey));
            tvGatewaySettingZone.setTextColor(getResources().getColor(R.color.grey));
            tvGatewaySettingZoneHint.setTextColor(getResources().getColor(R.color.grey));
            tvGatewaySettingLocation.setTextColor(getResources().getColor(R.color.grey));
            tvGatewaySettingLocationHint.setTextColor(getResources().getColor(R.color.grey));
            tvBackUpRecovery.setTextColor(getResources().getColor(R.color.grey));
//            tvGatewaySettingInfoHint.setTextColor(getResources().getColor(R.color.grey));

        }
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(unBindButton, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(unBindButton, SkinResouceKey.COLOR_BUTTON_TEXT);
    }

    @Override
    protected void initData() {
        if (!TextUtils.isEmpty(preference.getCurrentGatewayInfo())) {
            gatewayInfoBean = JSON.parseObject(preference.getCurrentGatewayInfo(), GatewayInfoBean.class);
            if (gatewayInfoBean != null) {
                tvGatewaySettingName.setText(gatewayInfoBean.gwName);
                itemGatewayCenterBackupRecovery.setVisibility(gatewayInfoBean.cloneFlag == 0 ? View.GONE : View.VISIBLE);
            }
        } else {
            tvGatewaySettingName.setText("");
        }
        if (Preference.ENTER_TYPE_GW.equals(preference.getUserEnterType())) {
            unBindButton.setVisibility(View.GONE);
            itemGatewayCenterBackupRecovery.setVisibility(View.GONE);
            itemGatewayCenterWifiConfig.setVisibility(View.GONE);
        }
        if (gatewayInfoBean != null && !TextUtils.isEmpty(gatewayInfoBean.gwType)) {
            switch (gatewayInfoBean.gwType) {
                case "GW06":
                    itemGatewaySettingLed.setVisibility(View.VISIBLE);
                    if (Preference.ENTER_TYPE_ACCOUNT.equals(preference.getUserEnterType())
                            && "1".equals(preference.getCurrentGatewayState())
                            && !preference.isAuthGateway()) {
                    }
                    updateLedState();
                    break;
                case "GW08":
                    itemGatewaySettingLed.setVisibility(View.VISIBLE);
                    itemGatewaySettingWifi.setVisibility(View.VISIBLE);
                    updateLedState();
                    updateWifiState();
                    break;
//                case "GW05":
//                    //小物网关不同于其他网关，暂时隐藏时区设置
//                    itemGatewaySettingZone.setVisibility(View.GONE);
//                    break;
                case "GW09":
                case "GW10":
                    if (LanguageUtil.isChina()) {
                        itemGatewaySettingLocation.setVisibility(View.VISIBLE);
                        tvLocationTips.setVisibility(View.VISIBLE);
                        tvGatewaySettingLocation.setText(gatewayInfoBean.gwLocation);
                    }
                    break;
                case "GW11":
                    itemGatewaySettingWifi.setVisibility(View.VISIBLE);
                    updateWifiState();
                    break;
                case "GW14":
                    itemGatewayCenterWifiConfig.setVisibility(View.VISIBLE);
                    break;
                case "GW01":
                    itemWanPortSetting.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }

        if (gatewayInfoBean != null && !TextUtils.isEmpty(gatewayInfoBean.zone)) {
            if (!TextUtils.equals(gatewayInfoBean.gwType, "GW05")) {
                tvGatewaySettingZone.setText(CameraUtil.getZoneNameByLanguage(this, gatewayInfoBean.zone));
            } else {
                tvGatewaySettingZone.setText(zoneArray[Arrays.asList(zoneArrayValue).indexOf(gatewayInfoBean.zone)]);
            }
        }
    }

    @Override
    protected void initListeners() {
        itemGatewaySettingName.setOnClickListener(this);
        itemDeviceInfo.setOnClickListener(this);
        itemGatewaySettingZone.setOnClickListener(this);
        itemGatewayCenterNotify.setOnClickListener(this);
        itemGatewayCenterBackupRecovery.setOnClickListener(this);
        itemGatewaySettingInfo.setOnClickListener(this);
        itemGatewaySettingLocation.setOnClickListener(this);
        itemGatewayCenterWifiConfig.setOnClickListener(this);
        itemWanPortSetting.setOnClickListener(this);
        unBindButton.setOnClickListener(this);
        ledToggleButton.setOnCheckedChangeListener(this);
    }

    //0:关闭  ，1：打开
    private void sendCmd() {
        final JSONObject object = new JSONObject();
        object.put("cmd", "512");
        object.put("gwID", gatewayInfoBean.gwID);
        object.put("mode", 1);//模式
        object.put("appID", gatewayInfoBean.appID);
        object.put("ledSwitch", ledSwitch);
        object.put("wifiSwitch", wifiSwitch);
        MainApplication.getApplication()
                .getMqttManager()
                .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
    }

    private void updateLedState(boolean state) {
        ledToggleButton.setOnCheckedChangeListener(null);
        ledToggleButton.setChecked(state);
        ledToggleButton.setOnCheckedChangeListener(this);
    }

    private void updateWifiState(boolean state) {
        wifiToggleButton.setOnCheckedChangeListener(null);
        wifiToggleButton.setChecked(state);
        wifiToggleButton.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Intent intent = null;
        switch (v.getId()) {
            case R.id.item_gateway_setting_name:
                if (Preference.ENTER_TYPE_ACCOUNT.equals(preference.getUserEnterType()) && preference.isAuthGateway()) {
                    ToastUtil.show(R.string.AuthManager_No_Right);
                } else {
                    showChangeGatewayNameDialog();
                }
                break;
            case R.id.item_device_info:
                intent = new Intent(GatewaySettingActivity.this, DeviceInfoActivity.class);
                intent.putExtra(DeviceMoreActivity.KEY_DEVICE_ID, preference.getCurrentGatewayID());
                intent.putExtra("isGatewayFlag", true);
                startActivity(intent);
                break;
            case R.id.item_gateway_setting_info:
                if (Preference.ENTER_TYPE_ACCOUNT.equals(preference.getUserEnterType()) && preference.isAuthGateway()) {
                    ToastUtil.show(R.string.AuthManager_No_Right);
                } else {
                    intent = new Intent();
                    intent.setClass(this, ChangePasswordActivity.class);
                    intent.putExtra("type", ChangePasswordActivity.CHANGE_GATEWAY_PWD);
                    startActivityForResult(intent, REQUEST_CODE_CHANGE_PASSWORD);
                }
                break;

            case R.id.item_gateway_zone:
                if (TextUtils.equals("GW05", gatewayInfoBean.gwType)) {
                    startActivityForResult(new Intent(this, CylincamGatewayZoneActivity.class).
                            putExtra("zoneName", gatewayInfoBean.zone), REQUEST_CODE_CHANGE_ZONE_CMICA4);
                } else {
                    startActivityForResult(new Intent(this, GatewayZoneActivity.class).
                            putExtra("zoneName", gatewayInfoBean.zone), REQUEST_CODE_CHANGE_ZONE);
                }
                break;
            case R.id.item_gateway_center_notifi:
                startActivity(new Intent(this, RemindSetActivity.class));
                break;
            case R.id.item_gateway_center_backup_recovery:
                startActivity(new Intent(this, GatewayBackRecoveryActivity.class));
                break;
            case R.id.item_gateway_location:
                Intent it = new Intent(this, GatewayLocationActivity.class);
                it.putExtra("gwLocation", gatewayInfoBean.gwLocation);
                startActivity(it);
                break;
            case R.id.item_gateway_center_wifi_config:
                AddIF02DeviceActivity.start(GatewaySettingActivity.this, "GW14", true);
                break;
            case R.id.unbind_button:
                unBind();
                break;
            case R.id.item_wan_port_setting:
                startActivity(new Intent(this, GatewayWanPortSettingActivity.class));
                break;
        }
    }

    private void unBind() {
        String gwId = preference.getCurrentGatewayID();
        progressDialogManager.showDialog(UNBIND, this, null, null, getResources().getInteger(R.integer.http_timeout));
        deviceApiUnit.doUnBindDevice(gwId, new DeviceApiUnit.DeviceApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                progressDialogManager.dimissDialog(UNBIND, 0);
                ToastUtil.show(R.string.GatewaySetts_Disbind_Success);
                MainApplication.getApplication().clearCurrentGateway();
                finish();
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(UNBIND, 0);
                ToastUtil.show(msg);
            }
        });
    }

    private void updateLedState() {
        if (gatewayInfoBean.ledSwitch == 0) {
            updateLedState(false);
        } else if (gatewayInfoBean.ledSwitch == 1) {
            updateLedState(true);
        }
    }

    private void updateWifiState() {
        if (gatewayInfoBean.wifiSwitch == 0) {
            updateWifiState(false);
        } else if (gatewayInfoBean.wifiSwitch == 1) {
            updateWifiState(true);
        }
    }

    private void showChangeGatewayNameDialog() {
        builder = new WLDialog.Builder(this);
        builder.setTitle(this.getString(R.string.GatewaySetts_ReviseName))
                .setCancelOnTouchOutSide(false)
                .setEditTextHint(R.string.EditText_UserInfo_Nick)
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View var1, String msg) {
                        changeGatewayName(preference.getCurrentGatewayID(), msg);
                    }

                    @Override
                    public void onClickNegative(View view) {

                    }
                });
        dialog = builder.create();
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    private void changeGatewayName(String gwId, String name) {

        String appID = ((MainApplication) getApplication()).getLocalInfo().appID;
        if (!TextUtils.isEmpty(gwId)) {
            ((MainApplication) getApplication())
                    .getMqttManager()
                    .publishEncryptedMessage(
                            MQTTCmdHelper.createGatewayInfo(gwId, 1, appID, name),
                            MQTTManager.MODE_GATEWAY_FIRST);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CHANGE_PASSWORD) {
            setResult(RESULT_OK);
            finish();
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CHANGE_ZONE) {
            gatewayInfoBean.zone = data.getStringExtra("zoneName");
            tvGatewaySettingZone.setText(CameraUtil.getZoneNameByLanguage(this, gatewayInfoBean.zone));
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CHANGE_ZONE_CMICA4) {
            gatewayInfoBean.zone = data.getStringExtra("zoneName");
            tvGatewaySettingZone.setText(zoneArray[Arrays.asList(zoneArrayValue).indexOf(gatewayInfoBean.zone)]);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGatewayInfoChangedEvent(GatewayInfoEvent event) {
        if (event.bean != null) {
            progressDialogManager.dimissDialog(LED, 0);
            progressDialogManager.dimissDialog(WIFI, 0);
            tvGatewaySettingName.setText(event.bean.gwName);
            tvGatewaySettingLocation.setText(event.bean.gwLocation);
            ledSwitch = event.bean.ledSwitch;
            wifiSwitch = event.bean.wifiSwitch;
            if (event.bean.ledSwitch == 0) {
                updateLedState(false);
            } else if (event.bean.ledSwitch == 1) {
                updateLedState(true);
            }
            if (event.bean.wifiSwitch == 0) {
                updateWifiState(false);
            } else if (event.bean.wifiSwitch == 1) {
                updateWifiState(true);
            }
        } else {

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGatewayStateChanged(GatewayStateChangedEvent event) {
        if (event.bean != null && TextUtils.equals(event.bean.gwID, preference.getCurrentGatewayID())) {
            if ("15".equals(event.bean.cmd)) {
                finish();
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == ledToggleButton) {
            progressDialogManager.showDialog(LED, this, null, null, getResources().getInteger(R.integer.http_timeout));
            if (isChecked) {
                ledSwitch = 1;
            } else {
                ledSwitch = 0;
            }
            sendCmd();
        } else if (buttonView == wifiToggleButton) {
            progressDialogManager.showDialog(WIFI, this, null, null, getResources().getInteger(R.integer.http_timeout));
            if (isChecked) {
                wifiSwitch = 1;
            } else {
                wifiSwitch = 0;
            }
            sendCmd();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGatewaySoftwareUpdateEvent(GatewaySoftwareUpdateEvent event) {
        if (event.status != 0) {
            //提醒升级网关
            iv_gateway_update.setVisibility(View.VISIBLE);
        } else {
            iv_gateway_update.setVisibility(View.GONE);
        }
    }
}
