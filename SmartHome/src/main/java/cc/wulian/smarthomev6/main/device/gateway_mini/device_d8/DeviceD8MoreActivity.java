package cc.wulian.smarthomev6.main.device.gateway_mini.device_d8;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
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
import cc.wulian.smarthomev6.main.device.gateway_mini.config.MiniGatewayWifiConfigFragment;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceCache;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.DeviceInfoBean;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.LanguageChangeEvent;
import cc.wulian.smarthomev6.support.event.MiniGatewayConfigEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

public class DeviceD8MoreActivity extends BaseTitleActivity {

    public static final String KEY_DEVICE_ID = "key_device_id";
    public static final String KEY_MORE_CONFIG = "key_more_config";
    private static final String GET_WIFI_LIST = "101";
    private RelativeLayout relativeRename, relativeReSetRoom, relativeInfo, relativeWificonfig;
    private TextView textRenameName, textResetRoomName;

    protected WLDialog.Builder builder;
    protected WLDialog dialog;

    protected String deviceId, configStr;
    protected Device device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_d8_more, true);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void initTitle() {
        setToolBarTitle(getString(R.string.Home_Edit_More));
    }

    @Override
    protected void initView() {
        relativeRename = (RelativeLayout) findViewById(R.id.item_device_more_rename);
        relativeReSetRoom = (RelativeLayout) findViewById(R.id.item_device_more_area);
        relativeInfo = (RelativeLayout) findViewById(R.id.item_device_more_info);
        relativeWificonfig = (RelativeLayout) findViewById(R.id.item_wifi_config);
        textRenameName = (TextView) findViewById(R.id.item_device_more_rename_name);
        textResetRoomName = (TextView) findViewById(R.id.item_device_more_area_name);

        if(Preference.ENTER_TYPE_GW.equals(preference.getUserEnterType())){
            relativeInfo.setVisibility(View.GONE);
            relativeWificonfig.setVisibility(View.GONE);
        }else{
            relativeInfo.setVisibility(View.VISIBLE);
            relativeWificonfig.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initListeners() {
        relativeRename.setOnClickListener(this);
        relativeReSetRoom.setOnClickListener(this);
        relativeInfo.setOnClickListener(this);
        relativeWificonfig.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        deviceId = getIntent().getStringExtra(KEY_DEVICE_ID);
        configStr = getIntent().getStringExtra(KEY_MORE_CONFIG);

        device = MainApplication.getApplication().getDeviceCache().get(deviceId);

        if (device != null) {
            textRenameName.setText(DeviceInfoDictionary.getNameByTypeAndName(device.type, device.name));
            String areaName = ((MainApplication) getApplicationContext()).getRoomCache().getRoomName(device.roomID);
            textResetRoomName.setText(areaName);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        super.onClick(v);
        switch (v.getId()) {
            case R.id.item_device_more_rename:
                showChangeNameDialog();
                break;
            case R.id.item_device_more_area:
                intent = new Intent(DeviceD8MoreActivity.this, DeviceDetailMoreAreaActivity.class);
                intent.putExtra(KEY_DEVICE_ID, deviceId);
                startActivity(intent);
                break;
            case R.id.item_device_more_info:
                intent = new Intent(DeviceD8MoreActivity.this, DeviceInfoActivity.class);
                intent.putExtra(KEY_DEVICE_ID, deviceId);
                startActivity(intent);
                break;
            case R.id.item_wifi_config:
                getWifiList();
                progressDialogManager.showDialog(GET_WIFI_LIST, DeviceD8MoreActivity.this, "", null, 10000);
                break;
            default:
                break;
        }
    }

    //获取mini网关周围 wifi列表
    private void getWifiList() {
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "330");
            object.put("appID", MainApplication.getApplication().getLocalInfo().appID);
            object.put("operType", "getWifiList");
            object.put("msgid", "2");
            object.put("gwID", device.gwID);
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //解析数据
    private void parseJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String result = jsonObject.optString("result");
            String reason = jsonObject.optString("reason");
            String data = jsonObject.optString("body");
            JSONObject dataObj = new JSONObject(data);
            JSONArray jsonArray = dataObj.getJSONArray("cell");
            progressDialogManager.dimissDialog(GET_WIFI_LIST, 0);
            if (TextUtils.equals(result, "0")) {
                Intent intent = new Intent(this, DeviceD8WifiConfigActivity.class);
                intent.putExtra("json", json);
                intent.putExtra("gwID", device.gwID);
                startActivity(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
                            ToastUtil.show(getString(R.string.Mine_Rename_Empty));
                            return;
                        }

                        String currentGatewayId = Preference.getPreferences().getCurrentGatewayID();
                        if (!TextUtils.isEmpty(currentGatewayId)) {
                            ((MainApplication) getApplication())
                                    .getMqttManager()
                                    .publishEncryptedMessage(
                                            MQTTCmdHelper.createSetDeviceInfo(currentGatewayId, deviceId, 2, msg.trim(), null),
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMiniGatewayConfigReport( MiniGatewayConfigEvent event){
        parseJson(event.jsonData);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null) {
            if (TextUtils.equals(event.device.devID, deviceId)) {
                DeviceCache deviceCache = ((MainApplication) getApplication()).getDeviceCache();
                device = deviceCache.get(deviceId);

                if (event.device.mode == 3) {
                    finish();
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceInfoChangedEvent(DeviceInfoChangedEvent event) {
        DeviceInfoBean bean = event.deviceInfoBean;
        DeviceCache deviceCache = ((MainApplication) getApplication()).getDeviceCache();
        device = deviceCache.get(deviceId);
        if (bean != null) {
            if (bean.mode == 3) {//删除设备时退出界面
                finish();
            } else if (device != null && TextUtils.equals(bean.devID, deviceId)) {
                if (bean.name != null) {
                    textRenameName.setText(DeviceInfoDictionary.getNameByTypeAndName(device.type, bean.name));
                    ToastUtil.show(R.string.Device_Name_Change_Success);
                }
                if (bean.roomID != null) {
                    String areaName = ((MainApplication) getApplicationContext()).getRoomCache().getRoomName(device.roomID);
                    textResetRoomName.setText(areaName);
                    ToastUtil.show(R.string.Device_Area_Change_Success);
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLanguageChangeEvent(LanguageChangeEvent event) {
        finish();
    }
}
