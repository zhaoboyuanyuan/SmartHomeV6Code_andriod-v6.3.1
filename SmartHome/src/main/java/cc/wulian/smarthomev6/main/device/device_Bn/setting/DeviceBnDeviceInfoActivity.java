package cc.wulian.smarthomev6.main.device.device_Bn.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.wulian.sdk.android.ipc.rtcv2.IPCMsgApiType;
import com.wulian.sdk.android.ipc.rtcv2.IPCMsgController;
import com.wulian.sdk.android.ipc.rtcv2.message.IPCcameraXmlMsgEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.callback.IcamMsgEventHandler;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.ICamApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.KeyValueBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.KeyValueListBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamLoginBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamVersionInfoBean;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.CameraUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Bn锁设备信息界面
 */
public class DeviceBnDeviceInfoActivity extends BaseTitleActivity implements IcamMsgEventHandler {
    private ICamApiUnit iCamApiUnit;
    private TextView devCameraNum;
    private TextView devCameraVersion;
    private TextView devWifiName;
    private TextView devDevVersion;
    private TextView devZigbeeVersion;
    private View viewNewVersion;
    private String gwID;
    private String devID;
    private int deviceVersionCode;//最新版本
    private String cameraId;
    private DataApiUnit dataApiUnit;

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Device_Info));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_bn_info, true);
        EventBus.getDefault().register(this);
        if (getIntent() != null) {
            gwID = getIntent().getStringExtra("gwID");
            devID = getIntent().getStringExtra("devId");
            getVersion();
        }
        iCamApiUnit = new ICamApiUnit(this, gwID, 1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataApiUnit = new DataApiUnit(this);
        getCameraIdByCloud();
        getConfigWifi();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void initView() {
        super.initView();
        devCameraNum = (TextView) findViewById(R.id.devCamaraNum);
        devCameraVersion = (TextView) findViewById(R.id.devCamaraVersion);
        devWifiName = (TextView) findViewById(R.id.devWifiName);
        devDevVersion = (TextView) findViewById(R.id.devDevVersion);
        devZigbeeVersion = (TextView) findViewById(R.id.devZigbeeVersion);
        viewNewVersion = findViewById(R.id.viewNewVersion);
    }

    private void getVersion() {
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", gwID);
            object.put("devID", devID);
            object.put("clusterId", 257);
            object.put("commandType", 1);
            object.put("commandId", 0x8018);
            object.put("endpointNumber", 1);
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getNewVersion(String did) {
        ICamLoginBean iCamInfo = Preference.getPreferences().getICamInfo(this.gwID);
        if (iCamInfo != null) {
            IPCMsgController.MsgQueryFirewareVersion(did, iCamInfo.sdomain);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        boolean isRight = event != null
                && event.device != null
                && TextUtils.equals(event.device.devID, devID)
                && event.device.mode == 0;
        if (isRight) {
            JSONObject object = null;
            try {
                object = new JSONObject(event.device.data);
                JSONArray endpoints = object.getJSONArray("endpoints");
                JSONArray clusters = ((JSONObject) endpoints.get(0)).getJSONArray("clusters");
                JSONArray attributes = ((JSONObject) clusters.get(0)).getJSONArray("attributes");
                int clusterId = ((JSONObject) clusters.get(0)).getInt("clusterId");
                if (clusterId == 257 && attributes != null && attributes.length() > 0) {
                    JSONObject attributesJson = attributes.getJSONObject(0);
                    int attributeId = attributesJson.getInt("attributeId");
                    String attributeValue = attributesJson.getString("attributeValue");
                    if (attributeId == 0x8007) {
                        String preFlag = attributeValue.substring(0, 2);
                        if (TextUtils.equals(preFlag, "07")) {
                            String lastFlag = attributeValue.substring(2);
                            String[] splitVersion = lastFlag.split("#");
                            if (splitVersion != null && splitVersion.length == 3) {
                                devDevVersion.setText(splitVersion[0]);//设备版本
                                devCameraVersion.setText(splitVersion[2]);//摄像机固件版本
                                devZigbeeVersion.setText(splitVersion[1]);//Zigbee版本
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    /*这边是最终比较版本*/
    ICamApiUnit.ICamApiCommonListener<ICamVersionInfoBean> ICamlistener = new ICamApiUnit.ICamApiCommonListener<ICamVersionInfoBean>() {
        @Override
        public void onSuccess(ICamVersionInfoBean bean) {
            if (bean != null) {
                if (bean.code > deviceVersionCode) {
                    viewNewVersion.setVisibility(View.VISIBLE);
                } else {
                    viewNewVersion.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void onFail(int code, String msg) {
            WLog.d(TAG, "获取最新版本失败!code=" + code + " msg=" + msg);
        }
    };


    /**
     * 获取bc锁的摄像机id
     */
    private void getCameraIdByCloud() {
        DataApiUnit dataApiUnit = new DataApiUnit(this);
        dataApiUnit.doGetBcCameraId(devID, "Bn", new DataApiUnit.DataApiCommonListener<String>() {
            @Override
            public void onSuccess(String bean) {
                if (!TextUtils.isEmpty(bean)) {
                    cameraId = bean;
                    devCameraNum.setText(cameraId);
                }
            }

            @Override
            public void onFail(int code, String msg) {


            }
        });
    }

    /**
     * 查询配置的WiFi名称
     */
    private void getConfigWifi() {
        dataApiUnit.doGetDeviceKeyValue(devID, "wifiName", new DataApiUnit.DataApiCommonListener<KeyValueListBean>() {
            @Override
            public void onSuccess(KeyValueListBean keyValueListBean) {
                if (keyValueListBean != null && keyValueListBean.keyValues != null && keyValueListBean.keyValues.size() > 0) {
                    for (KeyValueBean bean :
                            keyValueListBean.keyValues) {
                        if (bean.key.equals("wifiName")) {
                            devWifiName.setText(bean.value);
                        }
                    }
                }
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(msg);

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onMessageEvent(IPCcameraXmlMsgEvent event) {
        if (event.getCode() == 0) {
            if (event.getApiType() == IPCMsgApiType.QUERY_FIREWARE_VERSION) {/*查询固件版本*/
                try {
                    deviceVersionCode = Integer.parseInt(CameraUtil.getParamFromXml(event.getMessage(), "version_id"));
                    iCamApiUnit.getVersionCheck(cameraId.substring(0, 6).toLowerCase(), String.valueOf(deviceVersionCode), ICamlistener);
                } catch (NumberFormatException e) {
                }
            } else {

            }
        }
    }
}
