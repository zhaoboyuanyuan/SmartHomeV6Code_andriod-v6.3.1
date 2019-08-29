package cc.wulian.smarthomev6.main.device.device_Av;

import android.content.Intent;
import android.os.Bundle;
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
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

public class RegulateSwitchAvMoreActivity extends BaseTitleActivity {

    public static final String KEY_DEVICE_ID = "key_device_id";
    public static final String KEY_GW_ID = "key_gw_id";
    private static final String LOAD_SET_VALUE = "load_set_value";

    private RelativeLayout relativeRename, relativeReSetRoom, relativeInfo, relativeMinBrightness, relativeFrontBack, relativeCFLMode, relativeLog;
    private TextView textRenameName, textResetRoomName, textMinBrightness, textFrontBack;
    private ToggleButton CFLToggle;
    private Button btnDelete;

    protected WLDialog.Builder builder;
    protected WLDialog dialog;
    private BottomMenu menuWindow;

    protected String deviceID, gwID;
    protected Device device;
    private int bottomMenuCheckValue;
    private int bottomMenuType;//1:最小亮度值2:前后沿切相
    private ArrayList<String> mMinBrightnessList;
    private ArrayList<String> mFrontBackCutList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regulate_switch_av_more, true);
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
        relativeMinBrightness = (RelativeLayout) findViewById(R.id.item_min_brightness);
        relativeFrontBack = (RelativeLayout) findViewById(R.id.item_front_back);
        relativeCFLMode = (RelativeLayout) findViewById(R.id.item_CFL_mode);
        relativeLog = (RelativeLayout) findViewById(R.id.item_log);

        textRenameName = (TextView) findViewById(R.id.item_device_more_rename_name);
        textResetRoomName = (TextView) findViewById(R.id.item_device_more_area_name);


        textMinBrightness = (TextView) findViewById(R.id.item_min_brightness_value);
        textFrontBack = (TextView) findViewById(R.id.item_front_back_value);
        CFLToggle = (ToggleButton) findViewById(R.id.item_CFL_mode_btn);
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
        relativeMinBrightness.setOnClickListener(this);
        relativeFrontBack.setOnClickListener(this);
        CFLToggle.setOnClickListener(this);
        relativeLog.setOnClickListener(this);
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
        sendCmd(0x8007, null);
    }

    private void updateMode(int mode){
        if (mode == 2) {
            relativeMinBrightness.setEnabled(false);
            relativeFrontBack.setEnabled(false);
            CFLToggle.setEnabled(false);
            relativeMinBrightness.setAlpha(0.54f);
            relativeFrontBack.setAlpha(0.54f);
            relativeCFLMode.setAlpha(0.54f);
        } else {
            relativeMinBrightness.setEnabled(true);
            relativeFrontBack.setEnabled(true);
            CFLToggle.setEnabled(true);
            relativeMinBrightness.setAlpha(1f);
            relativeFrontBack.setAlpha(1f);
            relativeCFLMode.setAlpha(1f);
        }
    }

    private void sendCmd(int commandId, JSONArray parameter) {
        ProgressDialogManager.getDialogManager().showDialog(LOAD_SET_VALUE, RegulateSwitchAvMoreActivity.this, null, null, 10000);
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
                intent = new Intent(RegulateSwitchAvMoreActivity.this, DeviceDetailMoreAreaActivity.class);
                intent.putExtra(KEY_DEVICE_ID, deviceID);
                startActivity(intent);
                break;
            case R.id.item_info:
                intent = new Intent(RegulateSwitchAvMoreActivity.this, DeviceInfoActivity.class);
                intent.putExtra(KEY_DEVICE_ID, deviceID);
                startActivity(intent);
                break;
            case R.id.item_min_brightness:
                bottomMenuType = 1;
                if(mMinBrightnessList == null){
                    mMinBrightnessList = new ArrayList<>();
                    for(int i=10; i<41; i++){
                        mMinBrightnessList.add(i + "");
                    }
                }
                int checked = 11;
                try {
                    checked = Integer.valueOf(textMinBrightness.getText().toString());
                }catch (Exception e){
                    WLog.e("luzx", "textMinBrightness is not num");
                }
                showBottomMenu(v, mMinBrightnessList, checked - 10);
                break;
            case R.id.item_front_back:
                bottomMenuType = 2;
                builder = new WLDialog.Builder(this);
                builder.setCancelOnTouchOutSide(false)
                        .setDismissAfterDone(false)
                        .setMessage(R.string.Cut_Explain)
                        .setPositiveButton(this.getResources().getString(R.string.Tip_I_Known))
                        .setListener(new WLDialog.MessageListener() {
                            @Override
                            public void onClickPositive(View view, String msg) {
                                if(mFrontBackCutList == null){
                                    mFrontBackCutList  = new ArrayList<>();
                                    mFrontBackCutList.add(getString(R.string.Cut_After));
                                    mFrontBackCutList.add(getString(R.string.Cut_Before));
                                }
                                int checked = 0;
                                try {
                                    checked = Integer.valueOf(textFrontBack.getTag().toString());
                                }catch (Exception e){
                                    WLog.e("luzx", "textFrontBack is not num");
                                }
                                showBottomMenu(v, mFrontBackCutList, checked);
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
            case R.id.item_CFL_mode_btn:
                JSONArray parameter = new JSONArray();
                if(CFLToggle.isChecked()){
                    parameter.put("1");
                    CFLToggle.setChecked(false);
                }else{
                    parameter.put("0");
                    CFLToggle.setChecked(true);
                }
                sendCmd(0x8006, parameter);

                break;
            case R.id.item_device_more_delete:
                showDelete(deviceID);
                break;
            case R.id.item_log:
                MessageLogActivity.start(RegulateSwitchAvMoreActivity.this, deviceID, device.type);
                break;
            default:
                break;
        }
    }

    private void showBottomMenu(View rootView, ArrayList<String> data, int index){
        if(menuWindow == null){
            menuWindow = new BottomMenu(RegulateSwitchAvMoreActivity.this, new BottomMenu.MenuClickListener() {
                @Override
                public void onSure() {
                    bottomMenuCheckValue = menuWindow.getCurrent();
                    JSONArray parameter = new JSONArray();
                    if(bottomMenuType == 1){
                        parameter.put(bottomMenuCheckValue + 10 + "");
                        sendCmd(0x8005, parameter);
                    }else if(bottomMenuType == 2){
                        parameter.put(bottomMenuCheckValue + "");
                        sendCmd(0x8004, parameter);
                    }
                }

                @Override
                public void onCancel() {

                }
            });
        }
        menuWindow.setData(data);
        menuWindow.setCurrent(index);
        menuWindow.show(rootView);
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
                                ProgressDialogManager.getDialogManager().dimissDialog(LOAD_SET_VALUE, 0);
                                if (attribute.attributeId == 0x8001) {
                                    if("1".equals(attribute.attributeValue)){
                                        textFrontBack.setText(R.string.Cut_Before);
                                        textFrontBack.setTag("1");
                                    }else{
                                        textFrontBack.setText(R.string.Cut_After);
                                        textFrontBack.setTag("0");
                                    }
                                } else if(attribute.attributeId == 0x8002){
                                    try{
                                        textMinBrightness.setText(Integer.valueOf(attribute.attributeValue).toString());
                                    }catch (Exception e){
                                        WLog.e("RegulateSwitchAvMoreActivity", "minBrightness is not num");
                                    }
                                } else if(attribute.attributeId == 0x8003){
                                    if("1".equals(attribute.attributeValue)){
                                        CFLToggle.setChecked(true);
                                    }else{
                                        CFLToggle.setChecked(false);
                                    }
                                }
                            }
                        }
                    });
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
                        ToastUtil.show(R.string.Device_Area_Change_Success);
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
}
