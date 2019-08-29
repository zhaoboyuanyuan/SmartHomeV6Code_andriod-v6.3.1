package cc.wulian.smarthomev6.main.mine.gatewaycenter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.home.HomeActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.DeviceForbiddenBean;
import cc.wulian.smarthomev6.support.core.mqtt.bean.GatewayInfoBean;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GatewayInfoEvent;
import cc.wulian.smarthomev6.support.event.GatewaySoftwareUpdateEvent;
import cc.wulian.smarthomev6.support.event.GatewayStateChangedEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.DialogUtil;
import cc.wulian.smarthomev6.support.utils.NetworkUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by syf on 2017/2/16
 */
public class GatewayCenterActivity extends BaseTitleActivity {

    private static final int REQUEST_CODE_GATEWAY_SETTINGS = 1;
    private RelativeLayout itemGatewayCenterList;
    private RelativeLayout itemGatewayCenterSetting;
    private RelativeLayout itemRouterSetting;
    private RelativeLayout mRelativeItemFog;
    private RelativeLayout item_gateway_bind_users;
    private LinearLayout llRouterSetting;
    private ImageView tvGatewayIcon;
    private TextView tvGatewayName;
    private TextView tvGatewayState;
    private TextView tvGatewayId;
    private TextView tvGatewayDevice;

    private TextView tv_gateway_setting, tv_gateway_fog, tv_gateway_bind_users;

    private View iv_gateway_update;

    //    private DeviceBean deviceBean;
    private DeviceApiUnit deviceApiUnit;
    private GatewayInfoBean gatewayInfoBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway_center, true);
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
        setToolBarTitle(getString(R.string.Mine_GatewayCenter));
    }

    @Override
    protected void initView() {
        itemGatewayCenterList = (RelativeLayout) findViewById(R.id.item_gateway_center_list);
        itemGatewayCenterSetting = (RelativeLayout) findViewById(R.id.item_gateway_center_setting);
        itemRouterSetting = (RelativeLayout) findViewById(R.id.item_router_setting);
        mRelativeItemFog = (RelativeLayout) findViewById(R.id.item_gateway_center_fog);
        item_gateway_bind_users = (RelativeLayout) findViewById(R.id.item_gateway_bind_users);
        llRouterSetting = (LinearLayout) findViewById(R.id.ll_item_router_setting);
        tvGatewayIcon = (ImageView) findViewById(R.id.item_gateway_center_icon);
        tvGatewayName = (TextView) findViewById(R.id.item_gateway_center_name);
        tvGatewayState = (TextView) findViewById(R.id.item_gateway_center_state);
        tvGatewayId = (TextView) findViewById(R.id.item_gateway_center_id);
        tvGatewayDevice = (TextView) findViewById(R.id.item_gateway_center_device);
        tv_gateway_setting = (TextView) findViewById(R.id.tv_gateway_setting);
        tv_gateway_fog = (TextView) findViewById(R.id.tv_gateway_fog);
        tv_gateway_bind_users = (TextView) findViewById(R.id.tv_gateway_bind_users);
        iv_gateway_update = findViewById(R.id.iv_gateway_update);

        if (preference.getUserEnterType().equals(Preference.ENTER_TYPE_GW)) {
            itemGatewayCenterList.setVisibility(View.GONE);
            item_gateway_bind_users.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void initData() {
        super.initData();
        if (!isGatewayForbidden()) {
            getGatewayInfo();
        }
    }

    private boolean isGatewayForbidden() {
        DeviceForbiddenBean deviceForbiddenBean;
        String forbiddenDevice = mainApplication.forbiddenDevice;
        if (!TextUtils.isEmpty(forbiddenDevice)) {
            deviceForbiddenBean = JSON.parseObject(forbiddenDevice, DeviceForbiddenBean.class);
            if (deviceForbiddenBean != null) {
                if (TextUtils.equals(preference.getCurrentGatewayID(), deviceForbiddenBean.getGwID())) {
                    if (deviceForbiddenBean.getType() == 0 && deviceForbiddenBean.getStatus() == 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected void initListeners() {
        itemGatewayCenterList.setOnClickListener(this);
        itemGatewayCenterSetting.setOnClickListener(this);
        itemRouterSetting.setOnClickListener(this);
        mRelativeItemFog.setOnClickListener(this);
        item_gateway_bind_users.setOnClickListener(this);

        updateGatewayInfo();
        updateDeviceCount();
        updateGatewayState();
        if (isGatewayForbidden()) {
            item_gateway_bind_users.setClickable(false);
            itemGatewayCenterSetting.setClickable(false);
            itemRouterSetting.setClickable(false);
            mRelativeItemFog.setClickable(false);
            item_gateway_bind_users.setAlpha(0.54f);
            itemGatewayCenterSetting.setAlpha(0.54f);
            itemRouterSetting.setAlpha(0.54f);
            mRelativeItemFog.setAlpha(0.54f);
        }
    }

    private void updateGatewayInfo() {
        if (!TextUtils.isEmpty(preference.getCurrentGatewayInfo())) {
            gatewayInfoBean = JSON.parseObject(preference.getCurrentGatewayInfo(), GatewayInfoBean.class);
            if (gatewayInfoBean != null) {
                tvGatewayState.setVisibility(View.VISIBLE);
                tvGatewayId.setVisibility(View.VISIBLE);
                tvGatewayDevice.setVisibility(View.VISIBLE);
                tvGatewayName.setText(gatewayInfoBean.gwName);
                if (gatewayInfoBean.gwID.startsWith("CG")) {
                    tvGatewayId.setText(getString(R.string.BindGateway_GatewayID) + ": "
                            + gatewayInfoBean.gwID.substring(0, 11));
                } else {
                    tvGatewayId.setText(getString(R.string.BindGateway_GatewayID) + ": "
                            + gatewayInfoBean.gwID);
                }
                if (TextUtils.equals(gatewayInfoBean.gwType, "GW09") || TextUtils.equals(gatewayInfoBean.gwType, "GW10")) {
                    llRouterSetting.setVisibility(View.VISIBLE);
                }
            }
        } else if (!TextUtils.isEmpty(preference.getCurrentGatewayID())) {
            tvGatewayState.setVisibility(View.VISIBLE);
            tvGatewayId.setVisibility(View.VISIBLE);
            tvGatewayDevice.setVisibility(View.VISIBLE);
            tvGatewayName.setText("");
            tvGatewayId.setText(
                    getString(R.string.BindGateway_GatewayID) + ": "
                            + preference.getCurrentGatewayID());
        } else {
            tvGatewayName.setText(getString(R.string.GatewayCenter_Default));
            tvGatewayState.setVisibility(View.GONE);
            tvGatewayId.setVisibility(View.GONE);
            tvGatewayDevice.setVisibility(View.GONE);
        }
    }

    private void updateDeviceCount() {
        String currentGatewayID = preference.getCurrentGatewayID();
        int count = 0;
        if (!TextUtils.isEmpty(currentGatewayID)) {
            for (Device device : ((MainApplication) getApplication()).getDeviceCache().getDevices()) {
                if (TextUtils.equals(device.gwID, currentGatewayID)) {
                    count += 1;
                }
            }
        }
        tvGatewayDevice.setText(String.format(getString(R.string.GatewayCenter_Device_Number), Integer.toString(count)));
    }

    private void updateGatewayState() {
        if (TextUtils.isEmpty(preference.getCurrentGatewayID())) {
            tvGatewayIcon.setImageResource(R.drawable.icon_gateway_offline);
            itemGatewayCenterSetting.setEnabled(false);
            mRelativeItemFog.setEnabled(false);
            item_gateway_bind_users.setEnabled(false);
            tv_gateway_setting.setTextColor(getResources().getColor(R.color.grey));
            tv_gateway_fog.setTextColor(getResources().getColor(R.color.grey));
            tv_gateway_bind_users.setTextColor(getResources().getColor(R.color.grey));
        } else {
            if ("1".equals(preference.getCurrentGatewayState())) {
                tvGatewayIcon.setImageResource(R.drawable.icon_gateway_online);
                tvGatewayState.setBackgroundResource(R.drawable.shape_gateway_state_online);
                tvGatewayState.setText(getResources().getString(R.string.Device_Online));
            } else {
                tvGatewayIcon.setImageResource(R.drawable.icon_gateway_offline);
                tvGatewayState.setBackgroundResource(R.drawable.shape_gateway_state_offline);
                tvGatewayState.setText(getResources().getString(R.string.Device_Offline));
            }
            if ("1".equals(preference.getCurrentGatewayState()) && !preference.isAuthGateway()) {
                mRelativeItemFog.setEnabled(true);
                tv_gateway_fog.setTextColor(getResources().getColor(R.color.black));
            } else {
                mRelativeItemFog.setEnabled(false);
                tv_gateway_fog.setTextColor(getResources().getColor(R.color.grey));
            }
            if (preference.isAuthGateway()) {
                item_gateway_bind_users.setEnabled(false);
                tv_gateway_bind_users.setTextColor(getResources().getColor(R.color.grey));
            } else {
                item_gateway_bind_users.setEnabled(true);
                tv_gateway_bind_users.setTextColor(getResources().getColor(R.color.black));
            }
            itemGatewayCenterSetting.setEnabled(true);
            tv_gateway_setting.setTextColor(getResources().getColor(R.color.black));
            if (gatewayInfoBean != null && TextUtils.equals(gatewayInfoBean.gwType, "GW99")) {
                mRelativeItemFog.setEnabled(false);
                item_gateway_bind_users.setEnabled(false);
                itemGatewayCenterSetting.setEnabled(false);
                tv_gateway_bind_users.setTextColor(getResources().getColor(R.color.grey));
                tv_gateway_setting.setTextColor(getResources().getColor(R.color.grey));
                tv_gateway_fog.setTextColor(getResources().getColor(R.color.grey));
            }
            if (gatewayInfoBean != null && TextUtils.equals(gatewayInfoBean.gwType, "GW14")) {
                mRelativeItemFog.setVisibility(View.GONE);

            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.item_gateway_center_list:
                startActivity(new Intent(this, GatewayListActivity.class));
                break;
            case R.id.item_gateway_center_setting:
                startActivityForResult(new Intent(this, GatewaySettingActivity.class), REQUEST_CODE_GATEWAY_SETTINGS);
                break;
            case R.id.item_router_setting:
                String ip = NetworkUtil.getIpAddress(this);
                String ipAddress = ip.substring(0, ip.length() - 1) + "1";
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse("http://" + ipAddress);
                intent.setData(content_url);
                startActivity(intent);
                break;
            case R.id.item_gateway_center_fog:
                FogGatewayActivity.start(this);
                break;
            case R.id.item_gateway_bind_users:
                startActivity(new Intent(this, RightsManageActivity.class));
                break;
            default:
                break;
        }
    }

    private void getGatewayInfo() {
        String appID = ((MainApplication) getApplication()).getLocalInfo().appID;
        String currentGatewayId = Preference.getPreferences().getCurrentGatewayID();
        if (!TextUtils.isEmpty(currentGatewayId)) {
            ((MainApplication) getApplication())
                    .getMqttManager()
                    .publishEncryptedMessage(
                            MQTTCmdHelper.createGatewayInfo(currentGatewayId, 0, appID, null),
                            MQTTManager.MODE_GATEWAY_FIRST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && REQUEST_CODE_GATEWAY_SETTINGS == requestCode) {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGatewayInfoChangedEvent(GatewayInfoEvent event) {
        updateGatewayInfo();
        updateGatewayState();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGatewayStateChanged(GatewayStateChangedEvent event) {
        updateGatewayInfo();
        updateGatewayState();
        if (event.bean != null && TextUtils.equals(event.bean.gwID, preference.getCurrentGatewayID())) {
            if ("15".equals(event.bean.cmd)) {

            } else if ("01".equals(event.bean.cmd)) {
                getGatewayInfo();
            }
        } else {
            getGatewayInfo();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        updateDeviceCount();
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
