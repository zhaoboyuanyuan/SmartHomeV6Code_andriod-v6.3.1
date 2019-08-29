package cc.wulian.smarthomev6.main.device;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.message.alarm.MessageAlarmActivity;
import cc.wulian.smarthomev6.main.message.log.MessageLogActivity;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceCache;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.DeviceInfoBean;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * 作者: mamengchao
 * 时间: 2017/4/5 0005
 * 描述: 设备详情页右上角更多
 * 联系方式: 805901025@qq.com
 */

public class DeviceDetailMoreActivity extends BaseTitleActivity {
    public static final String KEY_DEVICE_ID = "key_device_id";
    private Context context;
    private String deviceId;
    private RelativeLayout itemDeviceMoreRename;
    private RelativeLayout itemDeviceMoreArea;
    private RelativeLayout itemDeviceMoreBindScene;
    private RelativeLayout itemDeviceMoreAlarm;
    private RelativeLayout itemDeviceMoreLog;
    private Button btDeviceMoreDelete;
    private TextView tvDeviceMoreRenameName;
    private TextView tvDeviceMoreAreaName;
    private TextView tvDeviceMoreBindSceneName;

    private WLDialog.Builder builder;
    private WLDialog dialog;

    private Device device;


//    private Button btnLeavehome;
//    private Button btnAlarmSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail_more, true);
        EventBus.getDefault().register(this);
        context = this;
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
        itemDeviceMoreRename = (RelativeLayout) findViewById(R.id.item_device_more_rename);
        itemDeviceMoreArea = (RelativeLayout) findViewById(R.id.item_device_more_area);
        itemDeviceMoreBindScene = (RelativeLayout) findViewById(R.id.item_device_more_bind_scene);
        itemDeviceMoreAlarm = (RelativeLayout) findViewById(R.id.item_device_more_alarm);
        itemDeviceMoreLog = (RelativeLayout) findViewById(R.id.item_device_more_log);
        btDeviceMoreDelete = (Button) findViewById(R.id.item_device_more_delete);
        tvDeviceMoreRenameName = (TextView) findViewById(R.id.item_device_more_rename_name);
        tvDeviceMoreAreaName = (TextView) findViewById(R.id.item_device_more_area_name);
        tvDeviceMoreBindSceneName = (TextView) findViewById(R.id.item_device_more_bind_scene_name);

//        btnLeavehome= (Button) findViewById(R.id.btnLeavehome);
//        btnAlarmSetting= (Button) findViewById(R.id.btnLeavehome);

//        btnLeavehome.setOnClickListener(this);
//        btnAlarmSetting.setOnClickListener(this);
    }

    @Override
    protected void initListeners() {
        itemDeviceMoreRename.setOnClickListener(this);
        itemDeviceMoreArea.setOnClickListener(this);
        itemDeviceMoreBindScene.setOnClickListener(this);
        itemDeviceMoreAlarm.setOnClickListener(this);
        itemDeviceMoreLog.setOnClickListener(this);
        btDeviceMoreDelete.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        deviceId = getIntent().getStringExtra(KEY_DEVICE_ID);
        device = MainApplication.getApplication().getDeviceCache().get(deviceId);

        if (device != null) {
            if (DeviceInfoDictionary.getCategoryByType(device.type) == 4 &&
                    !TextUtils.equals(device.type, "01")) {
                itemDeviceMoreAlarm.setVisibility(View.VISIBLE);
            }
            if (DeviceInfoDictionary.getCategoryByType(device.type) == 5){
                itemDeviceMoreAlarm.setVisibility(View.GONE);
            }
            tvDeviceMoreRenameName.setText(DeviceInfoDictionary.getNameByTypeAndName(device.type, device.name));
            String areaName = ((MainApplication) getApplicationContext()).getRoomCache().getRoomName(device.roomID);
            tvDeviceMoreAreaName.setText(areaName);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.item_device_more_rename:
                showChangeNameDialog();
                break;
            case R.id.item_device_more_area:
                Intent intent = new Intent(DeviceDetailMoreActivity.this, DeviceDetailMoreAreaActivity.class);
                intent.putExtra(KEY_DEVICE_ID, deviceId);
                startActivity(intent);
                break;
            case R.id.item_device_more_bind_scene:
                break;
            case R.id.item_device_more_alarm:
                MessageAlarmActivity.start(context, deviceId, device.type);
                break;
            case R.id.item_device_more_log:
                MessageLogActivity.start(context, deviceId, device.type);
                break;
            case R.id.item_device_more_delete:
                showDelete(deviceId);
                break;
//            case R.id.btnLeavehome:{
//                Intent intent1=new Intent(DeviceDetailMoreActivity.this, LeaveHomeActivity.class);
//                intent1.putExtra(KEY_DEVICE_ID, deviceId);
//                startActivity(intent1);
//            }break;
//            case R.id.btnAlarmSetting:{
//
//            }break;
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

    private void showDelete(final String deviceId) {
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
            if (TextUtils.equals(event.device.devID, deviceId)) {
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
                    tvDeviceMoreRenameName.setText(DeviceInfoDictionary.getNameByTypeAndName(device.type, bean.name));
                    ToastUtil.show(R.string.Device_Name_Change_Success);
                }
                if (bean.roomID != null) {
                    String areaName = ((MainApplication) getApplicationContext()).getRoomCache().getRoomName(device.roomID);
                    tvDeviceMoreAreaName.setText(areaName);
                    ToastUtil.show(R.string.Device_Area_Change_Success);
                }
            }
        }
    }
}
