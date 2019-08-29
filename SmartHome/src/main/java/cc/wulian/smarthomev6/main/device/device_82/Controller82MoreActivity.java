package cc.wulian.smarthomev6.main.device.device_82;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.DeviceDetailMoreAreaActivity;
import cc.wulian.smarthomev6.main.device.DeviceInfoActivity;
import cc.wulian.smarthomev6.main.message.alarm.MessageAlarmActivity;
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
import cc.wulian.smarthomev6.support.customview.BottomMenu;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.LanguageChangeEvent;
import cc.wulian.smarthomev6.support.tools.HttpUrlKey;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

public class Controller82MoreActivity extends BaseTitleActivity {

    public static final String KEY_DEVICE_ID = "key_device_id";
    private static final String LOAD_SET_VALUE = "load_set_value";

    private RelativeLayout relativeRename, relativeReSetRoom, relativeInfo, relativeTemperatureFormat,
            relativeEquipmentSetting, relativeDifferentialSetting, relativeFactoryReset, relativeLog,
            relativeAlarm, relativeClickSound, relativeVibrate, relativeEmergencyHeating,relativeFind;
    private TextView textRenameName, textResetRoomName, textTemperatureFormat;
    private ToggleButton clickSoundToggle, vibrateToggle, emergencyHeatingToggle;
    private Button btnDelete;

    protected WLDialog.Builder builder;
    protected WLDialog dialog;

    protected String deviceID, gwID;
    protected Device device;
    private WLDialog mWLDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller_82_more, true);
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
        relativeTemperatureFormat = (RelativeLayout) findViewById(R.id.item_temperature_format);
        relativeEquipmentSetting = (RelativeLayout) findViewById(R.id.item_equipment_setting);
        relativeDifferentialSetting = (RelativeLayout) findViewById(R.id.item_differential_setting);
        relativeFactoryReset = (RelativeLayout) findViewById(R.id.item_factory_reset);
        relativeLog = (RelativeLayout) findViewById(R.id.item_log);
        relativeAlarm = (RelativeLayout) findViewById(R.id.item_alarm);
        relativeFind = (RelativeLayout) findViewById(R.id.item_device_more_find);
        relativeClickSound = (RelativeLayout) findViewById(R.id.item_click_sound);
        relativeVibrate = (RelativeLayout) findViewById(R.id.item_vibrate);
        relativeEmergencyHeating = (RelativeLayout) findViewById(R.id.item_emergency_heating);

        textRenameName = (TextView) findViewById(R.id.item_device_more_rename_name);
        textResetRoomName = (TextView) findViewById(R.id.item_device_more_area_name);


        textTemperatureFormat = (TextView) findViewById(R.id.item_temperature_format_value);
        clickSoundToggle = (ToggleButton) findViewById(R.id.item_click_sound_btn);
        vibrateToggle = (ToggleButton) findViewById(R.id.item_vibrate_btn);
        emergencyHeatingToggle = (ToggleButton) findViewById(R.id.item_emergency_heating_btn);
        btnDelete = (Button) findViewById(R.id.item_device_more_delete);
        if(Preference.ENTER_TYPE_GW.equals(preference.getUserEnterType())){
            relativeInfo.setVisibility(View.GONE);
        }else{
            relativeInfo.setVisibility(View.VISIBLE);
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
        relativeFind.setOnClickListener(this);
        relativeTemperatureFormat.setOnClickListener(this);
        relativeEquipmentSetting.setOnClickListener(this);
        relativeDifferentialSetting.setOnClickListener(this);
        relativeFactoryReset.setOnClickListener(this);
        clickSoundToggle.setOnClickListener(this);
        vibrateToggle.setOnClickListener(this);
        emergencyHeatingToggle.setOnClickListener(this);
        relativeLog.setOnClickListener(this);
        relativeAlarm.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        deviceID = getIntent().getStringExtra(KEY_DEVICE_ID);
        device = MainApplication.getApplication().getDeviceCache().get(deviceID);
        if (device != null) {
            gwID = device.gwID;
            textRenameName.setText(DeviceInfoDictionary.getNameByTypeAndName(device.type, device.name));
            String areaName = ((MainApplication) getApplicationContext()).getRoomCache().getRoomName(device.roomID);
            textResetRoomName.setText(areaName);
            initShowData();
        }
    }

    private void initShowData() {
        if (device.mode == 2) {
            // 设备离线
            updateMode(device.mode);
        } else {
            EndpointParser.parse(device, new EndpointParser.ParserCallback() {
                @Override
                public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                    if (cluster.clusterId == 0x0201) {
                        updateInfo(attribute);
                    }
                }
            });
            updateMode(device.mode);
        }
    }

    private void updateMode(int mode){
        if (mode == 2) {
            relativeTemperatureFormat.setEnabled(false);
            relativeEquipmentSetting.setEnabled(false);
            relativeDifferentialSetting.setEnabled(false);
            relativeFactoryReset.setEnabled(false);
            clickSoundToggle.setEnabled(false);
            vibrateToggle.setEnabled(false);
            relativeFind.setEnabled(false);
            emergencyHeatingToggle.setEnabled(false);
            relativeTemperatureFormat.setAlpha(0.54f);
            relativeEquipmentSetting.setAlpha(0.54f);
            relativeDifferentialSetting.setAlpha(0.54f);
            relativeFactoryReset.setAlpha(0.54f);
            relativeClickSound.setAlpha(0.54f);
            relativeVibrate.setAlpha(0.54f);
            relativeEmergencyHeating.setAlpha(0.54f);
            relativeFind.setAlpha(0.54f);
        } else {
            sendCmd(0x8010, null);
            sendCmd(0x8011, null);
            relativeTemperatureFormat.setEnabled(true);
            relativeEquipmentSetting.setEnabled(true);
            relativeDifferentialSetting.setEnabled(true);
            relativeFactoryReset.setEnabled(true);
            clickSoundToggle.setEnabled(true);
            vibrateToggle.setEnabled(true);
            relativeFind.setEnabled(true);
            emergencyHeatingToggle.setEnabled(true);
            relativeTemperatureFormat.setAlpha(1f);
            relativeEquipmentSetting.setAlpha(1f);
            relativeDifferentialSetting.setAlpha(1f);
            relativeFactoryReset.setAlpha(1f);
            relativeClickSound.setAlpha(1f);
            relativeVibrate.setAlpha(1f);
            relativeEmergencyHeating.setAlpha(1f);
            relativeFind.setAlpha(1f);
        }
    }

    private void sendCmd(int commandId, JSONArray parameter) {
        ProgressDialogManager.getDialogManager().showDialog(LOAD_SET_VALUE, Controller82MoreActivity.this, null, null, 10000);
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", gwID);
            object.put("devID", deviceID);
            object.put("commandType", 1);
            object.put("commandId", commandId);
            if(parameter != null){
                object.put("parameter", parameter);
            }
            object.put("clusterId", 0x0201);
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
        JSONArray parameter = null;
        super.onClick(v);
        switch (v.getId()) {
            case R.id.item_rename:
                showChangeNameDialog();
                break;
            case R.id.item_area:
                intent = new Intent(Controller82MoreActivity.this, DeviceDetailMoreAreaActivity.class);
                intent.putExtra(KEY_DEVICE_ID, deviceID);
                startActivity(intent);
                break;
            case R.id.item_info:
                intent = new Intent(Controller82MoreActivity.this, DeviceInfoActivity.class);
                intent.putExtra(KEY_DEVICE_ID, deviceID);
                startActivity(intent);
                break;
            case R.id.item_device_more_find:
                sendFindCmd();
                showFindDialog();
                break;
            case R.id.item_temperature_format:
                DeviceSetActivity.start(Controller82MoreActivity.this, deviceID, HttpUrlKey.URL_BASE_DEVICE + "/thermostat_HAVC_82/temperatureScale.html");
                break;
            case R.id.item_differential_setting:
                DeviceSetActivity.start(Controller82MoreActivity.this, deviceID, HttpUrlKey.URL_BASE_DEVICE + "/thermostat_HAVC_82/differenceSetting.html");
                break;
            case R.id.item_equipment_setting:
                DeviceSetActivity.start(Controller82MoreActivity.this, deviceID, HttpUrlKey.URL_BASE_DEVICE + "/thermostat_HAVC_82/HAVCSetting.html");
                break;
            case R.id.item_click_sound_btn:
                parameter = new JSONArray();
                if(clickSoundToggle.isChecked()){
                    clickSoundToggle.setChecked(false);
                    parameter.put("1");
                }else{
                    clickSoundToggle.setChecked(true);
                    parameter.put("0");
                }
                sendCmd(0x8018, parameter);
                break;
            case R.id.item_vibrate_btn:
                parameter = new JSONArray();
                if(vibrateToggle.isChecked()){
                    vibrateToggle.setChecked(false);
                    parameter.put("1");
                }else{
                    vibrateToggle.setChecked(true);
                    parameter.put("0");
                }
                sendCmd(0x801C, parameter);
                break;
            case R.id.item_emergency_heating_btn:
                parameter = new JSONArray();
                if(emergencyHeatingToggle.isChecked()){
                    emergencyHeatingToggle.setChecked(false);
                    parameter.put("1");
                }else{
                    emergencyHeatingToggle.setChecked(true);
                    parameter.put("0");
                }
                sendCmd(0x8019, parameter);
                break;
            case R.id.item_factory_reset:
                builder = new WLDialog.Builder(this);
                builder.setCancelOnTouchOutSide(false)
                        .setTitle(R.string.Hint)
                        .setDismissAfterDone(false)
                        .setMessage(R.string.Device_Bm_Details_Restore_tips)
                        .setPositiveButton(this.getResources().getString(R.string.Sure))
                        .setNegativeButton(this.getResources().getString(R.string.Cancel))
                        .setListener(new WLDialog.MessageListener() {
                            @Override
                            public void onClickPositive(View view, String msg) {
                                JSONArray param = new JSONArray();
                                param.put("00");
                                sendCmd(0x8015, param);
                                dialog.dismiss();
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
                break;
            case R.id.item_device_more_delete:
                showDelete(deviceID);
                break;
            case R.id.item_log:
                MessageLogActivity.start(Controller82MoreActivity.this, deviceID, device.type);
                break;
            case R.id.item_alarm:
                MessageAlarmActivity.start(Controller82MoreActivity.this, deviceID, device.type);
            default:
                break;
        }
    }

    private void showChangeNameDialog() {
        builder = new WLDialog.Builder(this);
        builder.setTitle(getString(R.string.Device_Rename))
                .setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setEditTextHint(R.string.EditText_Device_Nick)
                .setEditTextText(DeviceInfoDictionary.getNameByTypeAndName(device.type, device.name))
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

    private void showFindDialog(){
        if(mWLDialog == null){
            WLDialog.Builder builder = new WLDialog.Builder(this);
            builder.setCancelOnTouchOutSide(true);
            builder.setDismissAfterDone(false);
            builder.setMessage(this.getString(R.string.device_Find_Flashing_10s));
            builder.setPositiveButton(getResources().getString(R.string.device_Find_go_on));
            builder.setNegativeButton(getResources().getString(R.string.device_Find_ok));
            builder.setListener(new WLDialog.MessageListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClickPositive(View view, String msg) {
                    sendFindCmd();
                    mWLDialog.dismiss();
                    showFindDialog();
                }
                @Override
                public void onClickNegative(View view) {
                    mWLDialog.dismiss();
                }
            });
            mWLDialog = builder.create();
        }
        mWLDialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null) {
            if (TextUtils.equals(event.device.devID, deviceID)) {
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
                            if (cluster.clusterId == 0x0201) {
                                ProgressDialogManager.getDialogManager().dimissDialog(LOAD_SET_VALUE, 0);
                                updateInfo(attribute);
                            }
                        }
                    });
                }
            }
        }
    }

    private void updateInfo(Attribute attribute){
        if (attribute.attributeId == 0x8001) {
            if (attribute.attributeValue != null && attribute.attributeValue.length() >= 36) {
                try {
                    String temperatureFormatText = attribute.attributeValue.substring(4, 6);
                    int temperatureFormatFlag = Integer.valueOf(temperatureFormatText);
                    if(temperatureFormatFlag == 0){
                        textTemperatureFormat.setText("℃");
                    }else{
                        textTemperatureFormat.setText("℉");
                    }
                    String heatTypeText = attribute.attributeValue.substring(6, 8);
                    int heatTypeTextFlag = Integer.valueOf(heatTypeText);
                    if(heatTypeTextFlag == 3 || heatTypeTextFlag == 4){
                        relativeEmergencyHeating.setAlpha(1f);
                        emergencyHeatingToggle.setEnabled(true);
                    }else{
                        relativeEmergencyHeating.setAlpha(0.5f);
                        emergencyHeatingToggle.setEnabled(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if(attribute.attributeId == 0x8002){
            if (attribute.attributeValue != null && attribute.attributeValue.length() >= 26) {
                try {
                    String sound = attribute.attributeValue.substring(8, 10);
                    String emergencyHeating = attribute.attributeValue.substring(10, 12);
                    String vibrate = attribute.attributeValue.substring(24, 26);
                    int soundFlag = Integer.valueOf(sound);
                    int vibrateFlag = Integer.valueOf(vibrate);
                    int emergencyHeatingFlag = Integer.valueOf(emergencyHeating);
                    if(soundFlag == 1){
                        clickSoundToggle.setChecked(true);
                    }else{
                        clickSoundToggle.setChecked(false);
                    }
                    if(vibrateFlag == 1){
                        vibrateToggle.setChecked(true);
                    }else{
                        vibrateToggle.setChecked(false);
                    }
                    if(emergencyHeatingFlag == 1){
                        emergencyHeatingToggle.setChecked(true);
                    }else{
                        emergencyHeatingToggle.setChecked(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
                if (bean.retCode == 0){
                    if (bean.name != null) {
                        textRenameName.setText(DeviceInfoDictionary.getNameByTypeAndName(device.type, bean.name));
                        ToastUtil.show(R.string.Device_Name_Change_Success);
                    }
                    if (bean.roomID != null) {
                        String areaName = ((MainApplication) getApplicationContext()).getRoomCache().getRoomName(device.roomID);
                        textResetRoomName.setText(areaName);
                        ToastUtil.show(R.string.Device_Name_Change_Success);
                    }
                }else if (bean.retCode == 1){
                    ToastUtil.single(R.string.Device_More_Rename_Error);
                }else if (bean.retCode == 255){
                    ToastUtil.single(R.string.http_unknow_error);
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLanguageChangeEvent(LanguageChangeEvent event) {
        finish();
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
            object.put("commandType",1);
            object.put("commandId",36641);
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
