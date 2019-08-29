package cc.wulian.smarthomev6.main.device.penguin.setting;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.wulian.sdk.android.ipc.rtcv2.IPCController;
import com.wulian.sdk.android.ipc.rtcv2.IPCMsgApiType;
import com.wulian.sdk.android.ipc.rtcv2.IPCMsgController;
import com.wulian.sdk.android.ipc.rtcv2.IPCResultCallBack;
import com.wulian.sdk.android.ipc.rtcv2.message.IPCcameraXmlMsgEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.config.DeviceStartConfigActivity;
import cc.wulian.smarthomev6.main.device.lookever.bean.LanguageVolumeBean;
import cc.wulian.smarthomev6.main.device.lookever.setting.CameraBindDoorLockActivity;
import cc.wulian.smarthomev6.main.device.lookever.setting.CameraZoneSettingActivity;
import cc.wulian.smarthomev6.main.home.widget.HomeWidgetManager;
import cc.wulian.smarthomev6.support.callback.IcamMsgEventHandler;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.ICamCloudApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamDeviceBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.CameraUtil;
import cc.wulian.smarthomev6.support.utils.ConstantsUtil;
import cc.wulian.smarthomev6.support.utils.DialogUtil;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;
import cc.wulian.smarthomev6.support.utils.XmlHandler;

/**
 * created by huxc  on 2017/8/3.
 * func：1080P企鹅机设置界面
 * email: hxc242313@qq.com
 */

public class PenguinSettingActivity extends BaseTitleActivity implements IcamMsgEventHandler, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private RelativeLayout rlDeviceName;
    private RelativeLayout rlDeviceInformation;
    private RelativeLayout rlRecordStorage;
    private RelativeLayout rlBindDoorlock;
    private RelativeLayout rlDeviceWifiConfig;
    private RelativeLayout rlSafeProtect;
    private RelativeLayout rlZoneSetting;
    private TextView tvDeviceName;
    private TextView tvHasSDCard;
    private TextView tvZoneName;
    private Button btnDeviceUnbind;
    private ToggleButton tbInvert;
    private ToggleButton tbLed;
    private WLDialog.Builder builder;
    private WLDialog dialog;

    private static final String QUERY = "QUERY";
    private static final int REQUEST_ZONE = 1;
    private static final String UPDATE_NAME = "UPDATE_NAME";
    private static final String UNBIND = "UNBIND";
    private static final String SEND_REQUEST = "SEND_REQUEST";
    private static final int MSG_FINISH = 1;
    private static final int MSG_EDIT_META = 2;
    private String deviceID;
    private String uniqueDeviceId;
    private String sipDomain;
    private String deviceName;
    private boolean hasSDCard;
    private int angle = 2;//0为不倒置，180为倒置
    private int led = 2;//0为灭，1为亮
    private int voice = 22;//0为静音，1为有声音
    private int volume = 10000;//0-100
    private String language = "";//ch,en
    private String zoneName = "";
    private WLDialog unbindDialog;
    private ICamDeviceBean iCamDeviceBean;
    private LanguageVolumeBean languageVolumeBean;
    private boolean isQueryLedAndVoiceAndInvert = true;
    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FINISH:// 结束页面
                    PenguinSettingActivity.this.finish();
                    break;
                default:
                    break;
            }
        }
    };
    private RelativeLayout rlInvert;
    private RelativeLayout rlLed;
    private RelativeLayout rlVoice;

    private boolean isShared = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penguin_setting, true);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initView() {
        super.initView();
        tvDeviceName = (TextView) findViewById(R.id.tv_device_name);
        tvHasSDCard = (TextView) findViewById(R.id.tv_has_SD_card);
        tvZoneName = (TextView) findViewById(R.id.tv_zone_name);
        rlInvert = (RelativeLayout) findViewById(R.id.rl_invert);
        rlLed = (RelativeLayout) findViewById(R.id.rl_led);
        rlVoice = (RelativeLayout) findViewById(R.id.rl_voice);
        rlDeviceName = (RelativeLayout) findViewById(R.id.rl_device_name);
        rlDeviceInformation = (RelativeLayout) findViewById(R.id.rl_device_information);
        rlRecordStorage = (RelativeLayout) findViewById(R.id.rl_record_storage);
        rlBindDoorlock = (RelativeLayout) findViewById(R.id.rl_bind_doorlock);
        rlDeviceWifiConfig = (RelativeLayout) findViewById(R.id.rl_device_wifi_config);
        rlSafeProtect = (RelativeLayout) findViewById(R.id.rl_safe_protect);
        rlZoneSetting = (RelativeLayout) findViewById(R.id.rl_zone);
        btnDeviceUnbind = (Button) findViewById(R.id.btn_device_unbind);
        tbInvert = (ToggleButton) findViewById(R.id.tb_invert);
        tbLed = (ToggleButton) findViewById(R.id.tb_led);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getResources().getString(R.string.Mine_Setts));
    }

    @Override
    protected void initData() {
        super.initData();
        iCamDeviceBean = (ICamDeviceBean) getIntent().getSerializableExtra("ICamDeviceBean");
        deviceID = iCamDeviceBean.did;
        uniqueDeviceId = iCamDeviceBean.uniqueDeviceId;
        sipDomain = iCamDeviceBean.sdomain;
        tvDeviceName.setText(DeviceInfoDictionary.getNameByTypeAndName(iCamDeviceBean.type, iCamDeviceBean.nick));
        languageVolumeBean = new LanguageVolumeBean();
        Device device = MainApplication.getApplication().getDeviceCache().get(deviceID);
        if (device != null) {
            isShared = device.isShared;
        }
        if (isShared) {
            rlDeviceInformation.setVisibility(View.GONE);
            rlInvert.setVisibility(View.GONE);
            rlLed.setVisibility(View.GONE);
            rlVoice.setVisibility(View.GONE);
            rlRecordStorage.setVisibility(View.GONE);
            rlBindDoorlock.setVisibility(View.GONE);
            rlDeviceWifiConfig.setVisibility(View.GONE);
            rlSafeProtect.setVisibility(View.GONE);
            rlZoneSetting.setVisibility(View.GONE);
        }
        //4G企鹅摄像机屏蔽WiFi配置入口
        if (TextUtils.equals(iCamDeviceBean.type, "CMICA6")) {
            rlDeviceWifiConfig.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isShared) {
            initWebData();
        }
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        rlInvert.setOnClickListener(this);
        rlLed.setOnClickListener(this);
        rlVoice.setOnClickListener(this);
        rlDeviceName.setOnClickListener(this);
        rlDeviceInformation.setOnClickListener(this);
        rlRecordStorage.setOnClickListener(this);
        rlBindDoorlock.setOnClickListener(this);
        rlZoneSetting.setOnClickListener(this);
        rlDeviceWifiConfig.setOnClickListener(this);
        rlSafeProtect.setOnClickListener(this);
        tbInvert.setOnCheckedChangeListener(this);
        tbLed.setOnCheckedChangeListener(this);
        btnDeviceUnbind.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_invert:
                isQueryLedAndVoiceAndInvert = false;
                tbInvert.toggle();
                break;
            case R.id.rl_led:
                isQueryLedAndVoiceAndInvert = false;
                tbLed.toggle();
                break;
            case R.id.rl_voice:
                startActivity(new Intent(this, PenguinBroadcastActivity.class)
                        .putExtra("languageVolumeBean", languageVolumeBean)
                        .putExtra("ICamDeviceBean", iCamDeviceBean));
                break;
            case R.id.rl_device_name:
                if (!isShared) {
                    showChangeNameDialog(this);
                }
                break;
            case R.id.rl_device_information:
                startActivity(new Intent(this, PenguinInformationActivity.class).
                        putExtra("ICamDeviceBean", iCamDeviceBean));
                break;
            case R.id.rl_record_storage:
                if (hasSDCard) {
                    startActivity(new Intent(this, PenguinRecordStorageActivity.class).
                            putExtra("ICamDeviceBean", iCamDeviceBean));
                }
                break;
            case R.id.rl_zone:
                startActivityForResult(new Intent(this, CameraZoneSettingActivity.class)
                        .putExtra("ICamDeviceBean", iCamDeviceBean)
                        .putExtra("zoneName", zoneName), REQUEST_ZONE);
                break;
            case R.id.rl_bind_doorlock:
                startActivity(new Intent(this, CameraBindDoorLockActivity.class).
                        putExtra("cameraId", iCamDeviceBean.did)
                        .putExtra("cameraType", iCamDeviceBean.type));
                break;
            case R.id.rl_device_wifi_config:
                jumpToConfigWiFi();
                break;
            case R.id.rl_safe_protect:
                startActivity(new Intent(this, PenguinSafeProtectActivity.class).
                        putExtra("ICamDeviceBean", iCamDeviceBean));
                break;
            case R.id.btn_device_unbind:
                unbindDeviceDialog();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ZONE:
                    zoneName = data.getStringExtra("zoneName");
                    tvZoneName.setText(CameraUtil.getZoneNameByLanguage(this, zoneName));
                    break;
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if (id == R.id.tb_invert) {
            if (isChecked) {
                angle = 180;
            } else {
                angle = 0;
            }
            if (isQueryLedAndVoiceAndInvert) {
                isQueryLedAndVoiceAndInvert = false;
            } else {
                configLEDVoiceAngel();
                reload();//画面倒置会挂断视频，需要重呼
            }
        } else if (id == R.id.tb_led) {
            if (isChecked) {
                led = 1;
            } else {
                led = 0;
            }
            if (isQueryLedAndVoiceAndInvert) {
                isQueryLedAndVoiceAndInvert = false;
            } else {
                configLEDVoiceAngel();
            }
        }
    }

    private void reload() {
        IPCController.closeAllVideoAsync(null);
        IPCController.makeCallAsync(new IPCResultCallBack() {
            @Override
            public void getResult(int i) {

            }
        }, uniqueDeviceId, iCamDeviceBean.sdomain);
    }

    /**
     * 查询随便看设置信息
     */
    private void initWebData() {
        WLog.i("hxc", "sip_cmd_query");
        IPCMsgController.MsgQueryLedAndVoicePromptInfo(uniqueDeviceId, sipDomain);//查询led和voice
        IPCMsgController.MsgQueryStorageStatus(uniqueDeviceId, sipDomain);//查询储状态信息
        IPCMsgController.MsgQueryVolume(uniqueDeviceId, sipDomain);//查询摄像机音量设置
        IPCMsgController.MsgQueryLanguage(uniqueDeviceId, sipDomain);//查询摄像机播报语言
        IPCMsgController.MsgQueryTimeZone(uniqueDeviceId, sipDomain);//查询摄像机时区
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
                .setEditTextText(DeviceInfoDictionary.getNameByTypeAndName("CMICA6", iCamDeviceBean.nick))
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        if (!StringUtil.isNullOrEmpty(msg) && !msg.equals(iCamDeviceBean.nick)) {
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
            iCamCloudApiUnit.doChangeDeviceName(iCamDeviceBean.did, deviceName,null, new ICamCloudApiUnit.IcamApiCommonListener<Object>() {

                @Override
                public void onSuccess(Object bean) {
                    ProgressDialogManager.getDialogManager().dimissDialog(UPDATE_NAME, 0);
                    ToastUtil.show(R.string.Change_Success);
                    dialog.dismiss();
                    tvDeviceName.setText(deviceName);
                    Device device = MainApplication.getApplication().getDeviceCache().get(deviceID);
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

    //更新led、voice、angle视图
    private void updateLedVoiceAngleView(String xmlData) {
        led = Integer.parseInt(CameraUtil.getParamFromXml(xmlData,
                "led_on").trim());
        voice = Integer.parseInt(CameraUtil.getParamFromXml(xmlData,
                "audio_online").trim());
        angle = Integer.parseInt(CameraUtil.getParamFromXml(xmlData,
                "angle").trim());
        if (angle == 180) {
            tbInvert.setChecked(true);
        } else {
            tbInvert.setChecked(false);
            isQueryLedAndVoiceAndInvert = false;
        }
        if (led == 1) {
            tbLed.setChecked(true);
        } else {
            tbLed.setChecked(false);
            isQueryLedAndVoiceAndInvert = false;
        }
    }

    //设置led、voice、angle
    private void configLEDVoiceAngel() {
        IPCMsgController.MsgConfigLedAndVoicePrompt(uniqueDeviceId, sipDomain, led == 1 ? true : false,
                voice == 1 ? true : false, angle == 180 ? true : false);
    }

    /**
     * 配置WiFi
     */
    private void jumpToConfigWiFi() {
        Intent it = new Intent(this, DeviceStartConfigActivity.class);
        it.putExtra("deviceId", iCamDeviceBean.did);
        it.putExtra("isBindDevice", false);
        it.putExtra("deviceType", iCamDeviceBean.type);
        it.putExtra("scanType", ConstantsUtil.DEVICE_SETTING_ENTRY);
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
        deviceApiUnit.doUnBindDevice(iCamDeviceBean.did, new DeviceApiUnit.DeviceApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                // 删除这个设备的widget
                HomeWidgetManager.deleteWidget(iCamDeviceBean.did);
                MainApplication.getApplication().getDeviceCache().remove(deviceID);
                EventBus.getDefault().post(new DeviceReportEvent(null));
                ProgressDialogManager.getDialogManager().dimissDialog(UNBIND, 0);
                ToastUtil.show(R.string.Message_Device_Deleted);
                setResult(RESULT_OK, null);
                PenguinSettingActivity.this.finish();
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(R.string.Home_Scene_DeleteScene_Failed);
            }
        });
    }

    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(IPCcameraXmlMsgEvent event) {
        if (event.getCode() != 0) {
            SipDataReturn(false, event.getApiType(), event.getMessage(),
                    event.getDestURI(), String.valueOf(event.getCode()));
        } else {
            SipDataReturn(true, event.getApiType(), event.getMessage(),
                    event.getDestURI(), String.valueOf(event.getCode()));
        }
    }

    protected void SipDataReturn(boolean isSuccess, IPCMsgApiType apiType, String xmlData, String from, String code) {
        if (isSuccess) {
            switch (apiType) {
                case QUERY_LED_AND_VOICE_PROMPT_INFO:
                    updateLedVoiceAngleView(xmlData);
                    break;
                case QUERY_STORAGE_STATUS:
                    Pattern pstatus = Pattern
                            .compile("<storage.*status=\"(\\d)\"\\s+.*/?>(</storage>)?");
                    Matcher matchers = pstatus.matcher(xmlData);
                    if (matchers.find()) {
                        String status = matchers.group(1).trim();
                        if ("1".equals(status)) {
                            hasSDCard = false;
                            tvHasSDCard.setText(getString(R.string.Backsee_No_SDcard));
                        } else if ("2".equals(status)) {
                            hasSDCard = true;
                            tvHasSDCard.setText(getString(R.string.Have_SD));
                        }
                    }
                    break;
                case QUERY_VOLUME:
                    volume = Integer.parseInt(CameraUtil.getParamFromXml(xmlData,
                            "vol").trim());
                    languageVolumeBean.setVolume(volume);
                    break;
                case QUERY_LANGUAGE:
                    language = XmlHandler
                            .parseDeviceSipInfo(xmlData, "language");
                    languageVolumeBean.setLanguage(language);
                    break;
                case QUERY_TIME_ZONE:
                    zoneName = XmlHandler
                            .parseDeviceSipInfo(xmlData, "zonename");
                    tvZoneName.setText(CameraUtil.getZoneNameByLanguage(this, zoneName));
                    WLog.i(TAG, "zoneName:" + zoneName);
                    break;

            }
        } else {
            switch (apiType) {
                case QUERY_LED_AND_VOICE_PROMPT_INFO:
                case QUERY_STORAGE_STATUS:
                case QUERY_LANGUAGE:
                case QUERY_VOLUME:
                case QUERY_TIME_ZONE:
                    ToastUtil.show(getString(R.string.Config_Query_Device_Fail));
                    break;
            }
        }
    }
}
