package cc.wulian.smarthomev6.main.device.device_Cc;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.DeviceDetailMoreAreaActivity;
import cc.wulian.smarthomev6.main.device.DeviceInfoActivity;
import cc.wulian.smarthomev6.main.message.log.MessageLogActivity;
import cc.wulian.smarthomev6.support.core.device.Attribute;
import cc.wulian.smarthomev6.support.core.device.Cluster;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceCache;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.device.Endpoint;
import cc.wulian.smarthomev6.support.core.device.EndpointParser;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.DeviceInfoBean;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.LanguageChangeEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

public class RegulateSwitchCcMoreActivity extends BaseTitleActivity {

    public static final String KEY_DEVICE_ID = "key_device_id";
    public static final String KEY_GW_ID = "key_gw_id";
    private static final String LOAD_SET_VALUE = "load_set_value";

    private RelativeLayout relativeRename;
    private RelativeLayout relativeReSetRoom;
    private RelativeLayout relativeInfo;
    private RelativeLayout rlModeSetting;
    private RelativeLayout rlRangeSetting;
    private RelativeLayout rlLightSourceSetting;
    private RelativeLayout relativeLog;
    private RelativeLayout rlFindDevice;
    private TextView textRenameName, textResetRoomName;
    private Button btnDelete;

    protected WLDialog.Builder builder;
    protected WLDialog dialog;
    private WLDialog findDeviceDialog;

    protected String deviceID, gwID;
    protected Device device;

    private String modeValue;
    private String minValue;
    private String maxValue;
    private String lightSourceValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regulate_switch_cc_more, true);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initTitle() {
        setToolBarTitle(getString(R.string.Home_Edit_More));
    }

    @Override
    protected void initView() {
        relativeRename = (RelativeLayout) findViewById(R.id.item_rename);
        relativeReSetRoom = (RelativeLayout) findViewById(R.id.item_area);
        relativeInfo = (RelativeLayout) findViewById(R.id.item_info);
        rlModeSetting = (RelativeLayout) findViewById(R.id.item_mode_setting);
        rlRangeSetting = (RelativeLayout) findViewById(R.id.item_range_setting);
        rlLightSourceSetting = (RelativeLayout) findViewById(R.id.item_light_source_setting);
        rlFindDevice = (RelativeLayout) findViewById(R.id.item_find_device);
        relativeLog = (RelativeLayout) findViewById(R.id.item_log);

        textRenameName = (TextView) findViewById(R.id.item_device_more_rename_name);
        textResetRoomName = (TextView) findViewById(R.id.item_device_more_area_name);

        btnDelete = (Button) findViewById(R.id.item_device_more_delete);
        if (Preference.ENTER_TYPE_GW.equals(preference.getUserEnterType())) {
            relativeInfo.setVisibility(View.GONE);
            relativeLog.setVisibility(View.GONE);
        } else {
            relativeInfo.setVisibility(View.VISIBLE);
            relativeLog.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void updateSkin() {
        super.updateSkin();
//        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
//        skinManager.setTextColor(btnDelete, SkinResouceKey.COLOR_NAV);
    }

    @Override
    protected void initListeners() {
        relativeRename.setOnClickListener(this);
        relativeReSetRoom.setOnClickListener(this);
        relativeInfo.setOnClickListener(this);
        rlModeSetting.setOnClickListener(this);
        rlRangeSetting.setOnClickListener(this);
        rlLightSourceSetting.setOnClickListener(this);
        relativeLog.setOnClickListener(this);
        rlFindDevice.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        deviceID = getIntent().getStringExtra(KEY_DEVICE_ID);
        gwID = getIntent().getStringExtra(KEY_GW_ID);
        device = MainApplication.getApplication().getDeviceCache().get(deviceID);

        if (device != null) {
            textRenameName.setText(DeviceInfoDictionary.getNameByTypeAndName(device.type, device.name));
            String areaName = ((MainApplication) getApplicationContext()).getRoomCache().getRoomName(device.roomID);
            textResetRoomName.setText(areaName);
            updateMode(device.mode);
        }
        sendCmd(0x8008, null);
    }


    private void updateMode(int mode) {
        if (mode == 2) {
            rlModeSetting.setEnabled(false);
            rlRangeSetting.setEnabled(false);
            rlModeSetting.setAlpha(0.54f);
            rlRangeSetting.setAlpha(0.54f);
            rlLightSourceSetting.setAlpha(0.54f);
        } else {
            rlModeSetting.setEnabled(true);
            rlRangeSetting.setEnabled(true);
            rlModeSetting.setAlpha(1f);
            rlRangeSetting.setAlpha(1f);
            rlLightSourceSetting.setAlpha(1f);
        }
    }

    private void sendCmd(int commandId, JSONArray parameter) {
        ProgressDialogManager.getDialogManager().showDialog(LOAD_SET_VALUE, RegulateSwitchCcMoreActivity.this, null, null, 5000);
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", gwID);
            object.put("devID", deviceID);
            object.put("commandType", 1);
            object.put("commandId", commandId);
            if (parameter != null) {
                object.put("parameter", parameter);
            }
            object.put("clusterId", 0x0008);
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(final View v) {
        Intent intent = null;
        super.onClick(v);
        switch (v.getId()) {
            case R.id.item_rename:
                showChangeNameDialog();
                break;
            case R.id.item_area:
                intent = new Intent(RegulateSwitchCcMoreActivity.this, DeviceDetailMoreAreaActivity.class);
                intent.putExtra(KEY_DEVICE_ID, deviceID);
                startActivity(intent);
                break;
            case R.id.item_info:
                intent = new Intent(RegulateSwitchCcMoreActivity.this, DeviceInfoActivity.class);
                intent.putExtra(KEY_DEVICE_ID, deviceID);
                startActivity(intent);
                break;
            case R.id.item_find_device:
                sendFindCmd();
                String msg = getString(R.string.device_Find_Flashing_10s);
                showFindDialog(msg);
                break;
            case R.id.item_mode_setting:
                startActivity(new Intent(this, ModeSettingActivity.class)
                        .putExtra("deviceID", deviceID)
                        .putExtra("modeValue", modeValue));
                break;
            case R.id.item_range_setting:
                startActivity(new Intent(this, RangeSettingActivity.class)
                        .putExtra("deviceID", deviceID)
                        .putExtra("minValue", minValue)
                        .putExtra("maxValue", maxValue));
                break;
            case R.id.item_light_source_setting:
                startActivity(new Intent(this, LightSourceSettingActivity.class)
                        .putExtra("deviceID", deviceID)
                        .putExtra("lightSourceValue", lightSourceValue));
                break;
            case R.id.item_device_more_delete:
                showDelete(deviceID);
                break;
            case R.id.item_log:
                MessageLogActivity.start(RegulateSwitchCcMoreActivity.this, deviceID, device.type);
                break;
            default:
                break;
        }
    }


    private void sendFindCmd() {
        if (!device.isOnLine()) {
            return;
        }
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", device.gwID);
            object.put("devID", device.devID);
            object.put("commandType", 1);
            object.put("commandId", 36641);
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showFindDialog(final String message) {
        if (findDeviceDialog == null) {
            WLDialog.Builder builder = new WLDialog.Builder(this);
            builder.setCancelOnTouchOutSide(true);
            builder.setDismissAfterDone(false);
            builder.setMessage(message);
            builder.setPositiveButton(getResources().getString(R.string.device_Find_go_on));
            builder.setNegativeButton(getResources().getString(R.string.device_Find_ok));
            builder.setListener(new WLDialog.MessageListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClickPositive(View view, String msg) {
                    sendFindCmd();
                    findDeviceDialog.dismiss();
                    showFindDialog(message);
                }

                @Override
                public void onClickNegative(View view) {
                    findDeviceDialog.dismiss();
                }
            });
            findDeviceDialog = builder.create();
        }
        findDeviceDialog.show();
    }

    private void showChangeNameDialog() {
        builder = new WLDialog.Builder(this);
        builder.setTitle(getString(R.string.Device_Rename))
                .setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setEditTextHint(R.string.EditText_Device_Nick)
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        if (TextUtils.isEmpty(msg.trim())) {
                            ToastUtil.show(R.string.Mine_Rename_Empty);
                            return;
                        }

                        String currentGatewayId = Preference.getPreferences().getCurrentGatewayID();
                        if (!TextUtils.isEmpty(currentGatewayId)) {
                            ((MainApplication) getApplication())
                                    .getMqttManager()
                                    .publishEncryptedMessage(
                                            MQTTCmdHelper.createSetDeviceInfo(currentGatewayId, deviceID, 2, msg.trim(), null),
                                            MQTTManager.MODE_GATEWAY_FIRST);
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onClickNegative(View view) {
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
        if (!dialog.isShowing()) {
            dialog.show();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null) {
            if (TextUtils.equals(event.device.devID, deviceID)) {
                progressDialogManager.dimissDialog(LOAD_SET_VALUE, 0);
                if (event.device.mode == 3) {
                    // 设备删除
                } else if (event.device.mode == 2) {
                    // 设备离线
                    updateMode(event.device.mode);
                } else if (event.device.mode == 1) {
                    // 更新上线
                    updateMode(event.device.mode);
                } else if (event.device.mode == 0) {
                    EndpointParser.parse(event.device, new EndpointParser.ParserCallback() {
                        @Override
                        public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                            if (cluster.clusterId == 0x0008) {
                                updateViews(attribute.attributeId, attribute.attributeValue);
                            }
                        }
                    });
                }
            }
        }
    }

    private void updateViews(int attributeId, String attributeValue) {
        switch (attributeId) {
            //前后沿切相
            case 0x8001:
                modeValue = attributeValue;
                break;
            //调光最小亮度值
            case 0x8002:
                minValue = attributeValue;
                break;
            //调光最大亮度值
            case 0x8003:
                maxValue = attributeValue;
                break;
            //光源设置上报
            case 0x8004:
                lightSourceValue = attributeValue;
                break;

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceInfoChangedEvent(DeviceInfoChangedEvent event) {
        DeviceInfoBean bean = event.deviceInfoBean;
        DeviceCache deviceCache = ((MainApplication) getApplication()).getDeviceCache();
        device = deviceCache.get(deviceID);
        // 更新adapter
        // 主要刷新一下  离线不可点击的控制
        if (event.deviceInfoBean.mode == 1 || event.deviceInfoBean.mode == 2) {
            updateMode(event.deviceInfoBean.mode);
        }
        if (bean != null) {
            if (bean.mode == 3) {//删除设备时退出界面
                finish();
            } else if (device != null && TextUtils.equals(bean.devID, deviceID)) {
                if (bean.retCode == 0) {
                    if (bean.name != null) {
                        textRenameName.setText(DeviceInfoDictionary.getNameByTypeAndName(device.type, bean.name));
                        ToastUtil.show(R.string.Device_Name_Change_Success);
                    }
                    if (bean.roomID != null) {
                        String areaName = ((MainApplication) getApplicationContext()).getRoomCache().getRoomName(device.roomID);
                        textResetRoomName.setText(areaName);
                        ToastUtil.show(R.string.Device_Area_Change_Success);
                    }
                } else if (bean.retCode == 1) {
                    ToastUtil.single(R.string.Device_More_Rename_Error);
                } else if (bean.retCode == 255) {
                    ToastUtil.single(R.string.http_unknow_error);
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLanguageChangeEvent(LanguageChangeEvent event) {
        finish();
    }
}
