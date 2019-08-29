package cc.wulian.smarthomev6.main.device.eques;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.eques.icvss.api.ICVSSUserInstance;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesDeleteBean;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesInfoBean;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesRebootBean;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesSetNickBean;
import cc.wulian.smarthomev6.main.device.eques.bean.LightEnableBean;
import cc.wulian.smarthomev6.main.device.lookever.setting.CameraBindDoorLockActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * 作者: chao
 * 时间: 2017/6/8
 * 描述: 移康猫眼设置页
 * 联系方式: 805901025@qq.com
 */

public class EquesSettingActivity extends BaseTitleActivity {
    public static String SET_NICK = "SET_NICK";
    public static String SET_LIGHT = "SET_LIGHT";
    public static String SET_REBOOT = "SET_REBOOT";
    public static String SET_DELETE = "SET_DELETE";
    public static int SET_PIR = 0;

    private RelativeLayout ll_device_name, ll_device_information, ll_device_detection, ll_device_light;
    private TextView tv_device_name, tv_device_detection;
    private ToggleButton tb_device_light;
    private Button btn_device_reboot, btn_device_unbind;
    private View view_device_detection_shadow, view_device_light_shadow, view_device_reboot_shadow;

    private String uid;
    private String deviceId;
    private String deviceNick;
    private boolean alarm_enable = false;
    private boolean lightEnable;
    private boolean isReboot = false;

    private ICVSSUserInstance icvss;
    private DeviceApiUnit deviceApiUnit;
    private Device device;
    private WLDialog dialog;
    private RelativeLayout ll_bind_lock;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        icvss = MainApplication.getApplication().getEquesApiUnit().getIcvss();
        deviceApiUnit = new DeviceApiUnit(this);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_eques_setting, true);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Mine_Setts));
    }

    @Override
    protected void initView() {
        ll_device_name = (RelativeLayout) findViewById(R.id.ll_device_name);
        ll_device_information = (RelativeLayout) findViewById(R.id.ll_device_information);
        ll_device_detection = (RelativeLayout) findViewById(R.id.ll_device_detection);
        ll_device_light = (RelativeLayout) findViewById(R.id.ll_device_light);
        tv_device_name = (TextView) findViewById(R.id.tv_device_name);
        tv_device_detection = (TextView) findViewById(R.id.tv_device_detection);
        tb_device_light = (ToggleButton) findViewById(R.id.tb_device_light);
        btn_device_reboot = (Button) findViewById(R.id.btn_device_reboot);
        btn_device_unbind = (Button) findViewById(R.id.btn_device_unbind);
        view_device_detection_shadow = (View) findViewById(R.id.view_device_detection_shadow);
        view_device_light_shadow = (View) findViewById(R.id.view_device_light_shadow);
        view_device_reboot_shadow = (View) findViewById(R.id.view_device_reboot_shadow);
        ll_bind_lock = (RelativeLayout) findViewById(R.id.ll_bind_lock);
    }

    @Override
    protected void initData() {
        uid = getIntent().getStringExtra("uid");
        deviceId = getIntent().getStringExtra("deviceId");
        device = MainApplication.getApplication().getDeviceCache().get(deviceId);
        if (!TextUtils.isEmpty(uid) && device.isOnLine()) {
            updateView(false);
            icvss.equesGetDeviceInfo(uid);
        } else {
            updateView(true);
        }

        if (!TextUtils.isEmpty(device.name)) {
            tv_device_name.setText(DeviceInfoDictionary.getNameByDevice(device));
        }
    }

    @Override
    protected void initListeners() {
        ll_bind_lock.setOnClickListener(this);
        ll_device_name.setOnClickListener(this);
        ll_device_information.setOnClickListener(this);
        ll_device_detection.setOnClickListener(this);
        btn_device_reboot.setOnClickListener(this);
        btn_device_unbind.setOnClickListener(this);
        tb_device_light.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                progressDialogManager.showDialog(SET_LIGHT, EquesSettingActivity.this, null, null);
                if (isChecked) {
                    lightEnable = true;
                    icvss.equesSetDoorbellLight(uid, 1);
                } else {
                    lightEnable = false;
                    icvss.equesSetDoorbellLight(uid, 0);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ll_device_name:
                showChangeNameDialog();
                break;
            case R.id.ll_device_information:
                EquesInfosActivity.start(this, deviceId, uid);
                break;
            case R.id.ll_device_detection:
                Intent intent = new Intent(this, EquesPirSettingActivity.class);
                intent.putExtra("deviceId", deviceId);
                intent.putExtra("uid", uid);
                intent.putExtra("alarm_enable", alarm_enable);
                startActivityForResult(intent, SET_PIR);
                break;
            case R.id.ll_bind_lock:
                startActivity(new Intent(this, CameraBindDoorLockActivity.class)
                        .putExtra("cameraId", deviceId)
                        .putExtra("cameraType", "CMICY1"));
                break;
            case R.id.btn_device_reboot:
                isReboot = true;
                showRebootDialog();
                break;
            case R.id.btn_device_unbind:
                showDeleteDialog();
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EquesInfoBean event) {
        if (TextUtils.equals(deviceId, event.from)) {
            if (TextUtils.equals(event.alarm_enable, "0")) {
                alarm_enable = false;
                tv_device_detection.setText(R.string.Http_Time_Out_Off);
            } else if (TextUtils.equals(event.alarm_enable, "1")) {
                alarm_enable = true;
                tv_device_detection.setText(R.string.Home_Scene_IsOpen);
            }

            if (TextUtils.equals(event.db_light_enable, "0")) {
                tb_device_light.setChecked(false);
                lightEnable = false;
            } else if (TextUtils.equals(event.db_light_enable, "1")) {
                tb_device_light.setChecked(true);
                lightEnable = true;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLightEnable(LightEnableBean event) {
        if (TextUtils.equals(deviceId, event.from)) {
            progressDialogManager.dimissDialog(SET_LIGHT, 0);
            if (TextUtils.equals(event.result, "1")) {
                tb_device_light.setChecked(lightEnable);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSetNick(EquesSetNickBean event) {
        progressDialogManager.dimissDialog(SET_NICK, 0);
        if (TextUtils.equals(event.code, "4000")) {
            tv_device_name.setText(deviceNick);
            Device device = MainApplication.getApplication().getDeviceCache().get(deviceId);
            device.name = deviceNick;
            EventBus.getDefault().post(new DeviceReportEvent(null));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDelete(EquesDeleteBean event) {
        progressDialogManager.dimissDialog(SET_DELETE, 0);
        if (TextUtils.equals(event.code, "4000") &&
                TextUtils.equals(event.bid, deviceId)) {
            tv_device_name.setText(deviceNick);
            updateToCloud(deviceId);
            setResult(RESULT_OK);
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReboot(EquesRebootBean event) {
        if (TextUtils.equals(event.from, deviceId)) {
            if (event.result != 1) {
                isReboot = false;
                progressDialogManager.dimissDialog(SET_REBOOT, 0);
                ToastUtil.show(getString(R.string.Cateyemini_Humandetection_Fail));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (isReboot && TextUtils.equals(event.device.devID, deviceId)) {
            if (event.device.isOnLine()) {
                isReboot = false;
                progressDialogManager.dimissDialog(SET_REBOOT, 0);
            }
        }
    }

    private void showChangeNameDialog() {
        WLDialog.Builder builder = new WLDialog.Builder(this);
        builder.setTitle(getString(R.string.Cateyemini_Setup_Rename))
                .setEditTextHint(getString(R.string.Cateyemini_Setup_Rename))
                .setCancelOnTouchOutSide(false)
                .setCancelable(false)
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View var1, String msg) {
                        progressDialogManager.showDialog(SET_NICK, EquesSettingActivity.this, null, null);
                        deviceNick = msg;
                        icvss.equesSetDeviceNick(deviceId, msg);
                        dialog.dismiss();
                        Device device = MainApplication.getApplication().getDeviceCache().get(deviceId);
                        device.name = msg;
                        if (device != null) {
                            EventBus.getDefault().post(new DeviceReportEvent(device));
                        }
                    }

                    @Override
                    public void onClickNegative(View var1) {
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
        dialog.show();
    }

    private void showRebootDialog() {
        WLDialog.Builder builder = new WLDialog.Builder(this);
        builder.setMessage(getString(R.string.Cateyemini_RemoteReboot_Determine))
                .setCancelOnTouchOutSide(false)
                .setCancelable(false)
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View var1, String msg) {
                        icvss.equesRestartDevice(uid);
                        dialog.dismiss();
                        progressDialogManager.showDialog(SET_REBOOT, EquesSettingActivity.this, getString(R.string.Cateyemini_Humandetection_Later), null, 300000);
                    }

                    @Override
                    public void onClickNegative(View var1) {
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
        dialog.show();
    }

    private void showDeleteDialog() {
        WLDialog.Builder builder = new WLDialog.Builder(this);
        builder.setMessage(getString(R.string.Cateyemini_Setup_Confirmdeletion))
                .setCancelOnTouchOutSide(false)
                .setCancelable(false)
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View var1, String msg) {
                        progressDialogManager.showDialog(SET_DELETE, EquesSettingActivity.this, null, null);
                        icvss.equesDelDevice(deviceId);
                        dialog.dismiss();
                    }

                    @Override
                    public void onClickNegative(View var1) {
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            boolean status = data.getBooleanExtra("status", alarm_enable);
            if (!status) {
                alarm_enable = false;
                tv_device_detection.setText(R.string.Http_Time_Out_Off);
            } else {
                alarm_enable = true;
                tv_device_detection.setText(R.string.Home_Scene_IsOpen);
            }
        }
    }

    private void updateToCloud(String deviceId) {
        deviceApiUnit.doUnBindDevice(deviceId, new DeviceApiUnit.DeviceApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {

            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }


    private void updateView(boolean isShow) {
        if (isShow) {
            view_device_detection_shadow.setVisibility(View.VISIBLE);
            view_device_light_shadow.setVisibility(View.VISIBLE);
            view_device_reboot_shadow.setVisibility(View.VISIBLE);
            btn_device_reboot.setClickable(false);
            btn_device_reboot.setEnabled(false);
        } else {
            view_device_detection_shadow.setVisibility(View.GONE);
            view_device_light_shadow.setVisibility(View.GONE);
            view_device_reboot_shadow.setVisibility(View.GONE);
            btn_device_reboot.setClickable(true);
            btn_device_reboot.setEnabled(true);
        }
    }
}
