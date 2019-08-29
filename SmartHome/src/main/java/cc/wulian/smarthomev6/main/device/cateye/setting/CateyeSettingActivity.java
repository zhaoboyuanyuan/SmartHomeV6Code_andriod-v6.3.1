package cc.wulian.smarthomev6.main.device.cateye.setting;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wulian.sdk.android.ipc.rtcv2.IPCMsgController;
import com.wulian.sdk.android.ipc.rtcv2.message.IPCcameraXmlMsgEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.CateyeStatusEntity;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.config.DeviceStartConfigActivity;
import cc.wulian.smarthomev6.main.device.lookever.setting.CameraBindDoorLockActivity;
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

/**
 * Created by hxc on 2017/5/12.
 * 智能猫眼设置界面
 */

public class CateyeSettingActivity extends BaseTitleActivity implements IcamMsgEventHandler, View.OnClickListener {
    private static final String QUERY = "QUERY";
    public static boolean hasInit = false;
    private static final int MSG_FINISH = 1;
    private static final int MSG_EDIT_META = 2;
    private static final int REQUEST_ZONE = 1;

    private RelativeLayout llDeviceName;
    private RelativeLayout llDeviceInfo;
    private RelativeLayout llDeviceDetection;
    private RelativeLayout llDeviceWiFiConfig;
    private RelativeLayout llBindLock;
    private RelativeLayout rlZoneSetting;
    private TextView tvDeviceName;
    private TextView tvZoneName;
    private Button btnUnbindCateye;

    private CateyeStatusEntity cateyeStatusEntity;
    private ICamDeviceBean iCamDeviceBean;

    private String deviceName;
    private boolean isShared = false;
    private String cityNum = "";

    private WLDialog.Builder builder;
    private WLDialog dialog;
    private WLDialog unbindDialog;


    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FINISH:// 结束页面
                    CateyeSettingActivity.this.finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cateye_setting, true);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getResources().getString(R.string.Mine_Setts));
    }

    @Override
    protected void initView() {
        super.initView();
        llDeviceName = (RelativeLayout) findViewById(R.id.ll_device_name);
        llDeviceInfo = (RelativeLayout) findViewById(R.id.ll_device_information);
        llDeviceDetection = (RelativeLayout) findViewById(R.id.ll_device_detection);
        llDeviceWiFiConfig = (RelativeLayout) findViewById(R.id.ll_device_wifi_config);
        llBindLock = (RelativeLayout) findViewById(R.id.ll_bind_lock);
        rlZoneSetting = (RelativeLayout) findViewById(R.id.rl_zone);
        btnUnbindCateye = (Button) findViewById(R.id.btn_device_unbind);
        tvDeviceName = (TextView) findViewById(R.id.tv_device_name);
        tvZoneName = (TextView) findViewById(R.id.tv_zone_name);

    }

    @Override
    protected void initData() {
        super.initData();
        iCamDeviceBean = (ICamDeviceBean) getIntent().getSerializableExtra("ICamDeviceBean");
        tvDeviceName.setText(DeviceInfoDictionary.getNameByTypeAndName("CMICA1", iCamDeviceBean.nick));
        cateyeStatusEntity = new CateyeStatusEntity();

        Device device = MainApplication.getApplication().getDeviceCache().get(iCamDeviceBean.did);
        if (device != null) {
            isShared = device.isShared;
        }
        if (isShared) {
            llDeviceInfo.setVisibility(View.GONE);
            llBindLock.setVisibility(View.GONE);
            llDeviceDetection.setVisibility(View.GONE);
            llDeviceWiFiConfig.setVisibility(View.GONE);
            rlZoneSetting.setVisibility(View.GONE);
        }

    }

    @Override
    protected void initListeners() {
        super.initListeners();
        llDeviceInfo.setOnClickListener(this);
        llDeviceName.setOnClickListener(this);
        llDeviceDetection.setOnClickListener(this);
        llDeviceWiFiConfig.setOnClickListener(this);
        btnUnbindCateye.setOnClickListener(this);
        llBindLock.setOnClickListener(this);
        rlZoneSetting.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ll_device_name:
                if (!isShared) {
                    showChangeNameDialog();
                }
                break;
            case R.id.ll_device_information:
                startActivity(new Intent(this, CateyeInformationActivity.class).
                        putExtra("ICamDeviceBean", iCamDeviceBean).putExtra("batteryLevel", cateyeStatusEntity.getBatteryLevel()));
                WLog.i("batteryLevel", cateyeStatusEntity.getBatteryLevel());
                break;
            case R.id.ll_device_detection:
                if (iCamDeviceBean.online == 1) {
                    Intent it = new Intent(this, CateyeDetectionActivity.class);
                    it.putExtra("CateyeStatusEntity", cateyeStatusEntity);
                    it.putExtra("ICamDeviceBean", iCamDeviceBean);
                    this.startActivity(it);
                }
                break;
            case R.id.ll_bind_lock:
                startActivity(new Intent(this, CameraBindDoorLockActivity.class)
                        .putExtra("cameraId", iCamDeviceBean.did)
                        .putExtra("cameraType", "CMICA1"));
                break;
            case R.id.btn_device_unbind:
                unbindDeviceDialog();
                break;
            case R.id.ll_device_wifi_config:
                jumpToConfigWiFi();
                break;
            case R.id.rl_zone:
                startActivityForResult(new Intent(this, CateyeZoneSettingActivity.class)
                        .putExtra("ICamDeviceBean", iCamDeviceBean)
                        .putExtra("cityNum", cityNum), REQUEST_ZONE);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ZONE:
                    int number = data.getIntExtra("cityNum", -1);
                    cateyeStatusEntity.setCityNum(number + "");
                    tvZoneName.setText(CameraUtil.getCateyeZoneNameByLanguage(this, number));
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isShared) {
            queryCateyeInfo();
            ProgressDialogManager.getDialogManager().showDialog(QUERY, this, null, null, 10000);
        }
    }

    private void queryCateyeInfo() {
        IPCMsgController.MsgWulianBellQueryDeviceConfigInformation(iCamDeviceBean.did, iCamDeviceBean.sdomain);//查询猫眼的配置信息

    }

    /**
     * 修改设备名称
     */

    private void showChangeNameDialog() {
        builder = new WLDialog.Builder(this);
        builder.setTitle(getString(R.string.GatewaySetts_ReviseName))
                .setCancelOnTouchOutSide(false)
                .setEditTextHint(getString(R.string.Input_Device_Nick))
                .setEditTextText(DeviceInfoDictionary.getNameByTypeAndName("CMICA1", iCamDeviceBean.nick))
                .setDismissAfterDone(false)
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setNegativeButton(this.getResources().getString(R.string.Cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        if (!StringUtil.isNullOrEmpty(msg) && !msg.equals(iCamDeviceBean.did)) {
                            updateDeviceName(msg);
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
                    ToastUtil.show(R.string.Change_Success);
                    dialog.dismiss();
                    tvDeviceName.setText(deviceName);
                    Device device = MainApplication.getApplication().getDeviceCache().get(iCamDeviceBean.did);
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
        it.putExtra("deviceId", iCamDeviceBean.did);
        it.putExtra("isBindDevice", false);
        it.putExtra("deviceType", "CMICA1");
        it.putExtra("scanType", ConstantsUtil.DEVICE_SETTING_ENTRY);
        startActivity(it);
    }

    /**
     * 解绑设备
     */
    private void unbindDeviceDialog() {
        String tip;
        Resources rs = getResources();
        tip = rs.getString(R.string.Makesure_Unbind_Device);
        unbindDialog = DialogUtil.showCommonDialog(this,
                rs.getString(R.string.Unbind_Cateye), tip, getString(R.string.Sure), getString(R.string.Cancel), new WLDialog.MessageListener() {
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
                ToastUtil.show(R.string.Message_Device_Deleted);
                MainApplication.getApplication().getDeviceCache().remove(iCamDeviceBean.did);
                setResult(RESULT_OK, null);
                CateyeSettingActivity.this.finish();
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
            Log.i("SettingSipMSg", "fail---" + "apiType = " + event.getApiType() + "msg = " + event.getMessage());
            switch (event.getApiType()) {
                case BELL_QUERY_DEVICE_CONFIG_INFORMATION:
                    ToastUtil.show(getString(R.string.Config_Query_Device_Fail));
                    break;
            }
        } else {
            ProgressDialogManager.getDialogManager().dimissDialog(QUERY, 0);
            switch (event.getApiType()) {
                case BELL_QUERY_DEVICE_CONFIG_INFORMATION:
                    cateyeStatusEntity = CameraUtil.getPojoByXmlData(event.getMessage());
                    cityNum = cateyeStatusEntity.getCityNum();
                    if (!TextUtils.isEmpty(cityNum)) {
                        tvZoneName.setText(CameraUtil.getCateyeZoneNameByLanguage(this, Integer.parseInt(cityNum)));
                    }
                    break;
            }
        }
    }

}
