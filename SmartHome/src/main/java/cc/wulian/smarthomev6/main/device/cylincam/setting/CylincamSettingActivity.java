package cc.wulian.smarthomev6.main.device.cylincam.setting;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.Packet;
import com.yuantuo.netsdk.TKCamHelper;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.util.Map;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.config.DeviceStartConfigActivity;
import cc.wulian.smarthomev6.main.device.cylincam.bean.BroadcastBean;
import cc.wulian.smarthomev6.main.device.cylincam.bean.CylincamInfoBean;
import cc.wulian.smarthomev6.main.device.cylincam.bean.IOTCDevChPojo;
import cc.wulian.smarthomev6.main.device.cylincam.server.IotSendOrder;
import cc.wulian.smarthomev6.main.device.cylincam.server.helper.CameraHelper;
import cc.wulian.smarthomev6.main.device.cylincam.server.queue.MessageQueue;
import cc.wulian.smarthomev6.main.device.cylincam.utils.DateUtil;
import cc.wulian.smarthomev6.main.device.cylincam.utils.IotUtil;
import cc.wulian.smarthomev6.main.device.lookever.setting.CameraBindDoorLockActivity;
import cc.wulian.smarthomev6.main.home.widget.HomeWidgetManager;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.ICamCloudApiUnit;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.Config;
import cc.wulian.smarthomev6.support.utils.ConstantsUtil;
import cc.wulian.smarthomev6.support.utils.DialogUtil;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

import static cc.wulian.smarthomev6.main.device.cylincam.CylincamPlayActivity.cameaHelper;

/**
 * created by huxc  on 2017/9/13.
 * func： 小物摄像机设置页
 * email: hxc242313@qq.com
 */

public class CylincamSettingActivity extends BaseTitleActivity implements View.OnClickListener, CameraHelper.Observer, CompoundButton.OnCheckedChangeListener {
    private static final String UNBIND = "UNBIND";
    private static final String UPDATE_NAME = "UPDATE_NAME";

    private RelativeLayout rlCameraName;
    private RelativeLayout rlCameraInformation;
    private RelativeLayout rlCameraBroadcast;
    private RelativeLayout rlCameraStorage;
    private RelativeLayout llBindLock;
    private RelativeLayout rlWifiConfig;
    private RelativeLayout rlCameraProtect;
    private RelativeLayout rlEnvironment;
    private Button btnDeleteCamera;
    private TextView tvStorage;
    private TextView tvCameraName;
    private ToggleButton tbEnvironment;

    private MessageQueue messageQueue = null;
    private WLDialog unbindDialog;
    private WLDialog.Builder builder;
    private WLDialog dialog;

    private String tutkUid;
    private String tutkPwd;
    private String deviceId;
    private String deviceName;
    private float total, free;
    private boolean showEnvironment;
    private boolean isStorageFlag = false;
    private boolean isLanguageFlag = false;
    private boolean isProtectFlag = false;
    private int protectSwitch;//安全防护开关，0关 1开
    private String[] fences = null;//活动检测防护数据,0-3分别为moveSensitivity，moveWeekday，moveTime,moveArea;

    private BroadcastBean broadcastBean;
    private CylincamInfoBean cylincamInfoBean;
    private Device device;
    private boolean isShared = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cylincam_setting, true);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(R.string.Mine_Setts);
    }

    @Override
    protected void initView() {
        super.initView();
        rlCameraName = (RelativeLayout) findViewById(R.id.rl_camera_name);
        rlCameraInformation = (RelativeLayout) findViewById(R.id.rl_camera_information);
        rlCameraBroadcast = (RelativeLayout) findViewById(R.id.rl_camera_broadcast);
        rlCameraProtect = (RelativeLayout) findViewById(R.id.rl_camera_protect);
        rlCameraStorage = (RelativeLayout) findViewById(R.id.rl_camera_storage);
        llBindLock = (RelativeLayout) findViewById(R.id.ll_bind_lock);
        rlWifiConfig = (RelativeLayout) findViewById(R.id.rl_wifi_config);
        rlEnvironment = (RelativeLayout) findViewById(R.id.rl_environment);
        btnDeleteCamera = (Button) findViewById(R.id.btn_delete_camera);
        tvCameraName = (TextView) findViewById(R.id.tv_device_name);
        tvStorage = (TextView) findViewById(R.id.tv_device_storage);
        tbEnvironment = (ToggleButton) findViewById(R.id.tb_environment);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        rlCameraBroadcast.setOnClickListener(this);
        rlCameraInformation.setOnClickListener(this);
        rlCameraName.setOnClickListener(this);
        rlCameraProtect.setOnClickListener(this);
        rlCameraStorage.setOnClickListener(this);
        llBindLock.setOnClickListener(this);
        rlWifiConfig.setOnClickListener(this);
        btnDeleteCamera.setOnClickListener(this);
        tbEnvironment.setOnCheckedChangeListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        tutkUid = getIntent().getStringExtra("tutkUid");
        tutkPwd = getIntent().getStringExtra("tutkPwd");
        deviceId = getIntent().getStringExtra("deviceId");
        device = MainApplication.getApplication().getDeviceCache().get(deviceId);
        isShared = device.isShared;
        tvCameraName.setText(DeviceInfoDictionary.getNameByTypeAndName("CMICA4", device.name));
        broadcastBean = new BroadcastBean();
        cylincamInfoBean = new CylincamInfoBean();
        showEnvironment = preference.getCylincamEnvironmentSet(deviceId);
        if (showEnvironment) {
            tbEnvironment.setChecked(true);
        } else {
            tbEnvironment.setChecked(false);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!isShared) {
            if (cameaHelper != null) {
                cameaHelper.attach(this);
                IotSendOrder.findDeskCameraVerByIoc(cameaHelper.getmCamera());
                IotSendOrder.findSdCodeStatus(cameaHelper.getmCamera());//成功后 去查询sd卡的状态
                IotSendOrder.findhLanguageByIoc(cameaHelper.getmCamera());//查询语言
                IotSendOrder.findVolumeByIoc(cameaHelper.getmCamera());//音量播报
                IotSendOrder.findMoveDataByIoc(cameaHelper.getmCamera());//活动检测查询
            }
        } else {
            rlCameraInformation.setVisibility(View.GONE);
            rlCameraBroadcast.setVisibility(View.GONE);
            rlCameraStorage.setVisibility(View.GONE);
            rlWifiConfig.setVisibility(View.GONE);
            rlCameraProtect.setVisibility(View.GONE);
            rlEnvironment.setVisibility(View.GONE);
            llBindLock.setVisibility(View.GONE);

        }
    }

    @Override
    public void avIOCtrlOnLine() {

    }

    @Override
    public void avIOCtrlDataSource(final byte[] data, final int avIOCtrlMsgType) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (avIOCtrlMsgType) {
                    case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_FW_UPDATA_RESP://版本信息
                        WLog.i(TAG, "摄像机版本信息查询返回");
                        getCylincamInfoBean(data);
                        break;
                    case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETMOTIONDETECT_RESP:
                        WLog.i(TAG, "安全防护查询返回");
                        isProtectFlag = true;
                        fences = IotUtil.getDeviceSafeProtectSetting(data);
                        protectSwitch = DateUtil.bytesToInt(data, 0);
                        break;
                    case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_VOLUME_RESP:
                        WLog.i(TAG, "播报音量查询data = " + data[0]);
                        broadcastBean.setVolume(data[0]);
                        break;
                    case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_LANGUAGE_RESP:
                        WLog.i(TAG, "语言查询data = " + data[0]);
                        broadcastBean.setLanguage(data[0]);
                        break;
                    case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_SDCARD_STATUS_RESP://sd卡状态
                        isStorageFlag = true;
                        WLog.i(TAG, "查询摄像机SD卡状态返回");
                        checkSdUpdateUi(IotUtil.byteArrayToMap(data));
                        break;
                    case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SDCARD_FORMAT_RESP://格式化
                        WLog.i(TAG, "格式化成功");
                        if (Packet.byteArrayToInt_Little(data, 0) == 0) {
                            IotSendOrder.findSdCodeStatus(cameaHelper.getmCamera());//格式化成功  然后去查询一下sd卡的状态
                            return;
                        }
                        ToastUtil.show(getString(R.string.Format_Failed));
                        break;
                    case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_VOLUME_RESP:
                        WLog.i(TAG, "音量播报设置成功");
                        break;
                    case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_IRCARD_RESP:
                        WLog.i(TAG, "红外夜视设置成功");
                        break;
                    case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_LANGUAGE_RESP:
                        WLog.i(TAG, "语言设置返回");
                        break;
                    case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETMOTIONDETECT_RESP:
                        WLog.i(TAG, "活动检测设置成功");
                }
            }
        });

    }

    @Override
    public void avIOCtrlMsg(int resCode, String method) {
        WLog.i(TAG, "avIOCtrlMsg");
        if (messageQueue != null) {
            final String msg = messageQueue.filter(resCode, method).sendMsg();
            if (msg != null && !msg.equals("")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.show(msg);
                    }
                });
            }
        }
    }


    /**
     * 获取小物摄像机ip地址和固件版本
     *
     * @param data
     */
    private void getCylincamInfoBean(final byte[] data) {
        Log.i(TAG, "parseCameConfigInfo");
        if (data.length < 0) {
            return;
        }
        //修改char为16位，防止ip地址获取位数不够
        char[] ip = new char[15];
        // 设备IP 高版本地址对齐多出4个字节
        int port = -1; // 设备监听端口
        int build = -1; // 表示编译的时间先后 ，值越在版本越新
        char[] version = new char[23]; // char version[64]; // 固件版本号
        // 高版本地址对齐多出5个字节
        // 获取设备ip
        byte[] ipDevice = new byte[15];
        System.arraycopy(data, 0, ipDevice, 0, 15);

        ip = DateUtil.getChars(ipDevice);
        StringBuilder sb1 = new StringBuilder();
        for (int i = 0; i < ip.length; i++) {
            if (String.valueOf(ip[i]).matches("[\\w\\d_\\+\\.]*$")) {
                sb1.append(String.valueOf(ip[i]));
            }
        }
        if (sb1.toString() != null) {
            Log.i(TAG, "" + sb1.toString().length());
            cylincamInfoBean.setIpAddress(sb1.toString());//ip地址
        }
        // 获取固件版本号。
        int version_length = 0;
        for (int i = 24; i < data.length; i++) {
            if (data[i] != 0x00) {
                version_length++;
            } else {
                break;
            }
        }
        byte[] versionDevice = new byte[version_length];
        System.arraycopy(data, 24, versionDevice, 0, version_length);
        version = DateUtil.getChars(versionDevice);
        StringBuilder sb2 = new StringBuilder();
        for (int i = 0; i < version.length; i++) {
            sb2.append(String.valueOf(version[i]));
        }
        if (sb2.toString() != null) {
            cylincamInfoBean.setFirmwareVersion(sb2.toString());//固件版本号

        }
        port = DateUtil.bytesToInt(data, 16);//端口
        build = DateUtil.bytesToInt(data, 20);//版本号
    }


    /**
     * 根据返回的信息 更新UI
     */
    private void checkSdUpdateUi(Map<String, Integer> sdkMap) {
        int status = sdkMap.get(Config.status);
        int sdexist = sdkMap.get(Config.sdexist);
        if (status == 0 && sdexist == 0) {
            rlCameraStorage.setClickable(true);
            //sd卡 存在， 可读可写   这时需要把free total 显示出来
            tvStorage.setText(getString(R.string.Have_SD));
            total = new BigDecimal(sdkMap.get(Config.totalMB) / 1024f).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            free = new BigDecimal(sdkMap.get(Config.freeMB) / 1024f).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        } else if (status == -1 && sdexist == 0) {//sd卡 存在 ，但不可用，因此 需要格式化
        } else if (sdexist == -1) {//sd卡 不存在
            tvStorage.setText(getString(R.string.Backsee_No_SDcard));
            rlCameraStorage.setClickable(false);
        }
    }

    /**
     * 修改设备名称
     */

    private void showChangeNameDialog(final Context context) {
        builder = new WLDialog.Builder(this);
        builder.setTitle(getString(R.string.GatewaySetts_ReviseName))
                .setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setEditTextHint(getString(R.string.Input_Device_Nick))
                .setEditTextText(DeviceInfoDictionary.getNameByTypeAndName("CMICA4", device.name))
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        if (!StringUtil.isNullOrEmpty(msg) && !msg.equals(deviceName)) {
                            updateDeviceName(msg);
                            ProgressDialogManager.getDialogManager().showDialog(UPDATE_NAME, context, null, null, 10000);
                        }
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

    private void updateDeviceName(String msg) {
        deviceName = msg;
        if (deviceName.length() > 30) {
            ToastUtil.show(R.string.NickStr_Less_Limit_Length);
        } else {
            ICamCloudApiUnit iCamCloudApiUnit = new ICamCloudApiUnit(this);
            iCamCloudApiUnit.doChangeDeviceName(deviceId, deviceName,null, new ICamCloudApiUnit.IcamApiCommonListener<Object>() {

                @Override
                public void onSuccess(Object bean) {
                    ProgressDialogManager.getDialogManager().dimissDialog(UPDATE_NAME, 0);
                    ToastUtil.show(R.string.Change_Success);
                    dialog.dismiss();
                    tvCameraName.setText(deviceName);
                    Device device = MainApplication.getApplication().getDeviceCache().get(deviceId);
                    if (device != null) {
                        device.setName(deviceName);
                        EventBus.getDefault().post(new DeviceReportEvent(device));
                    }
                }

                @Override
                public void onFail(int code, String msg) {
                    ToastUtil.show(R.string.Change_Fail);
                }
            });
        }

    }


    /**
     * 配置WiFi
     */
    private void jumpToConfigWiFi() {
        Intent it = new Intent(this, DeviceStartConfigActivity.class);
        it.putExtra("isBindDevice", false);
        it.putExtra("deviceId", deviceId);
        it.putExtra("deviceType", "CMICA4");
        it.putExtra("scanType", ConstantsUtil.CYLINCAME_SETTING_ENTRY);
        startActivity(it);
    }

    /**
     * 解绑设备
     */
    private void unbindDeviceDialog() {
        String tip;
        Resources rs = getResources();
        tip = rs.getString(R.string.Delete_Camera_Tip);
        unbindDialog = DialogUtil.showCommonDialog(this,
                rs.getString(R.string.Delete_Camera), tip, getString(R.string.Sure), getString(R.string.Cancel),
                new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View var1, String msg) {
                        unbindDevice();
                        unbindDialog.dismiss();
                        ProgressDialogManager.getDialogManager().showDialog(UNBIND, CylincamSettingActivity.this, null, null, 10000);
                    }

                    @Override
                    public void onClickNegative(View var1) {
                        unbindDialog.dismiss();
                    }
                });
        unbindDialog.show();
    }

    private void unbindDevice() {
        DeviceApiUnit deviceApiUnit = new DeviceApiUnit(this);
        deviceApiUnit.doUnBindDevice(deviceId, new DeviceApiUnit.DeviceApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                // 删除这个设备的widget
                HomeWidgetManager.deleteWidget(deviceId);
                MainApplication.getApplication().getDeviceCache().remove(deviceId);
                EventBus.getDefault().post(new DeviceReportEvent(null));
                ProgressDialogManager.getDialogManager().dimissDialog(UNBIND, 0);
                ToastUtil.show(R.string.Message_Device_Deleted);
                setResult(RESULT_OK, null);
                CylincamSettingActivity.this.finish();
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(R.string.Home_Scene_DeleteScene_Failed);
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_camera_name:
                if (!isShared) {
                    showChangeNameDialog(this);
                }
                break;
            case R.id.rl_camera_broadcast:
                if (broadcastBean != null) {
                    startActivityForResult(new Intent(this, CylincamBroadcastActivity.class)
                            .putExtra("broadcastBean", broadcastBean), 1);
                }
                break;
            case R.id.rl_camera_information:
                startActivity(new Intent(this, CylincamInformationActivity.class)
                        .putExtra("cylincamInfoBean", cylincamInfoBean)
                        .putExtra("deviceId", deviceId));
                break;
            case R.id.rl_camera_storage:
                if (isStorageFlag) {
                    startActivity(new Intent(this, CylincamRecordStorageActivity.class)
                            .putExtra("total", total)
                            .putExtra("free", free));
                } else {
                    ToastUtil.show(getString(R.string.Cylincam_Loading));
                }
                break;
            case R.id.ll_bind_lock:
                startActivity(new Intent(this, CameraBindDoorLockActivity.class)
                        .putExtra("cameraId", deviceId)
                        .putExtra("cameraType", "CMICY1"));
                break;
            case R.id.rl_wifi_config:
                jumpToConfigWiFi();
                break;
            case R.id.rl_camera_protect:
                if (fences != null) {
                    startActivity(new Intent(this, CylincamSafeProtectActivity.class)
                            .putExtra("fences", fences)
                            .putExtra("protectSwitch", protectSwitch)
                            .putExtra("deviceId", deviceId));
                } else {
                    ToastUtil.show(getString(R.string.Cylincam_Loading));
                }
                break;
            case R.id.btn_delete_camera:
                unbindDeviceDialog();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (cameaHelper != null) {
                    cameaHelper.attach(this);
                    IotSendOrder.findDeskCameraVerByIoc(cameaHelper.getmCamera());
                    IotSendOrder.findhLanguageByIoc(cameaHelper.getmCamera());//查询语言
                    IotSendOrder.findVolumeByIoc(cameaHelper.getmCamera());//音量播报
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.tb_environment:
                if (isChecked) {
                    preference.saveCylincamEnvironment(true, deviceId);
                } else {
                    preference.saveCylincamEnvironment(false, deviceId);
                }
                break;
            default:
                break;
        }
    }
}
