package cc.wulian.smarthomev6.main.device.more;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.MoreConfig;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.DeviceMoreActivity;
import cc.wulian.smarthomev6.main.device.IDeviceMore;
import cc.wulian.smarthomev6.support.core.device.Attribute;
import cc.wulian.smarthomev6.support.core.device.Cluster;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.Endpoint;
import cc.wulian.smarthomev6.support.core.device.EndpointParser;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * created by huxc  on 2018/11/5.
 * func：Bn、Bc更多设置
 * email: hxc242313@qq.com
 */

public class BcBnSettinsView extends FrameLayout implements IDeviceMore {
    private Context mContext;
    private Device device;

    private String deviceID = "";
    private String gwID = "";
    private String config;

    private View itemView;

    public BcBnSettinsView(Context context, String deviceID, String gwID) {
        super(context);
        this.mContext = context;
        this.gwID = gwID;
        this.deviceID = deviceID;
        initView(context);
        initData();
    }

    @Override
    public void onBindView(MoreConfig.ItemBean bean) {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    private void initView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_device_more_custom_lock_settings, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        itemView = (RelativeLayout) rootView.findViewById(R.id.rl_lock_settings);
        device = MainApplication.getApplication().getDeviceCache().get(deviceID);
        updateMode();
    }


    private void initData() {
        device = MainApplication.getApplication().getDeviceCache().get(deviceID);
        if (device == null) {
            return;
        }
        if(TextUtils.equals("Bc",device.type)){
            config = "{\"deviceID\":\"BC_DEVICEID\",\"data\":[{\"group\":\"bc_dev\",\"param\":[{\"key\":\"gwID\",\"type\":\"string\",\"value\":\"BC_GWID\"},{\"key\":\"devId\",\"type\":\"string\",\"value\":\"BC_DEVICEID\"},{\"key\":\"deviceId\",\"type\":\"string\",\"value\":\"BC_DEVICEID\"}],\"item\":[{\"offLineDisable\":true,\"type\":\"jump\",\"name\":\"BC_ALARMSETTING\",\"action\":\"jump:DevBc_AlarmSetting\"},{\"type\":\"jump\",\"name\":\"BC_MESSAGE\",\"action\":\"jump:Lock_Message\",\"showWithEnterType\":\"account\",\"showInLocale\":\"zh\",\"param\":[{\"key\":\"url\",\"type\":\"string\",\"value\":\"SMSNotification\\/smsDist\\/sms.html\"},{\"key\":\"gwID\",\"type\":\"string\",\"value\":\"BC_GWID\"},{\"key\":\"devId\",\"type\":\"string\",\"value\":\"BC_DEVICEID\"}]},{\"type\":\"custom\",\"name\":\"Bd_Door_Panel\",\"action\":\"custom:Bd_Door_Panel\"},{\"type\":\"custom\",\"name\":\"BC_LEAVE\",\"action\":\"custom:LeaveHomeBtn\"},{\"type\":\"custom\",\"name\":\"BC_RING_SNAPSHOT\",\"action\":\"custom:door_ring_snapshot\"}]},],\"cameraId\":\"BC_CAMERAID\",\"cameraWifi\":\"BC_CAMERAWIFI\"}";
        }else if(TextUtils.equals("Bn",device.type)){
            config = "{\"deviceID\":\"BC_DEVICEID\",\"data\":[{\"group\":\"bc_dev\",\"param\":[{\"key\":\"gwID\",\"type\":\"string\",\"value\":\"BC_GWID\"},{\"key\":\"devId\",\"type\":\"string\",\"value\":\"BC_DEVICEID\"},{\"key\":\"deviceId\",\"type\":\"string\",\"value\":\"BC_DEVICEID\"}],\"item\":[{\"offLineDisable\":true,\"type\":\"jump\",\"name\":\"BC_ALARMSETTING\",\"action\":\"jump:DevBc_AlarmSetting\"},{\"type\":\"jump\",\"name\":\"BC_MESSAGE\",\"action\":\"jump:Lock_Message\",\"showWithEnterType\":\"account\",\"showInLocale\":\"zh\",\"param\":[{\"key\":\"url\",\"type\":\"string\",\"value\":\"SMSNotification\\/smsDist\\/sms.html\"},{\"key\":\"gwID\",\"type\":\"string\",\"value\":\"BC_GWID\"},{\"key\":\"devId\",\"type\":\"string\",\"value\":\"BC_DEVICEID\"}]},{\"type\":\"custom\",\"name\":\"BC_LEAVE\",\"action\":\"custom:LeaveHomeBtn\"},{\"type\":\"custom\",\"name\":\"BC_RING_SNAPSHOT\",\"action\":\"custom:door_ring_snapshot\"}]},],\"cameraId\":\"BC_CAMERAID\",\"cameraWifi\":\"BC_CAMERAWIFI\"}";
        }
        updateMode();
        itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, DeviceMoreActivity.class)
                        .putExtra(DeviceMoreActivity.KEY_DEVICE_ID, deviceID)
                        .putExtra(DeviceMoreActivity.KEY_MORE_CONFIG, getSettingConfigData(config))
                        .putExtra("isBcBnSettings", true));
            }
        });
    }

    private String getSettingConfigData(String data) {
        String config = data.replaceAll("BC_GWID", device.gwID)
                .replaceAll("BC_DEVICEID", device.devID)
                .replaceAll("BC_ALBUM", mContext.getString(R.string.CateEye_Album_Tittle))
                .replaceAll("BC_VISITRECORD", mContext.getString(R.string.CateEye_Visitor_Record))
                .replaceAll("BC_WIFICONFIG", mContext.getString(R.string.Config_WiFi))
                .replaceAll("BC_RING_SNAPSHOT", mContext.getString(R.string.One_key_lock_Doorbellsnapshot))
                .replaceAll("BC_LEAVE", mContext.getString(R.string.Device_Vidicon_AwayButton))
                .replaceAll("BC_USERMANAGER", mContext.getString(R.string.Device_Vidicon_UserAdministrate))
                .replaceAll("BC_MESSAGE", "短信通知")
                .replaceAll("BC_ALARMMESSAGE", mContext.getString(R.string.Message_Center_AlarmMessage))
                .replaceAll("BC_LOG", mContext.getString(R.string.Message_Center_Log))
                .replaceAll("BC_ALARMSETTING", mContext.getString(R.string.Device_Vidicon_AlarmSetting))
                .replaceAll("BC_DEVICEINFO", mContext.getString(R.string.Device_Info));
//                .replaceAll("BC_CAMERAID", cameraId)
//                .replaceAll("BC_CAMERAWIFI", wifiName);

//        if (TextUtils.isEmpty(cameraId)) {
//            config = config.replaceAll("BC_CAMERAID", "--");
//        } else {
//            config = config.replaceAll("BC_CAMERAID", cameraId);
//        }
//
//        if (TextUtils.isEmpty(wifiName)) {
//            config = config.replaceAll("BC_CAMERAWIFI", "--");
//        } else {
//            config = config.replaceAll("BC_CAMERAWIFI", wifiName);
//        }
        WLog.i("hxc", "----" + config);
        return config;
    }

    private void updateMode() {
        itemView.setEnabled(device.isOnLine());
        itemView.setAlpha(device.isOnLine() ? 1.0f : 0.54f);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null) {
            if (TextUtils.equals(event.device.devID, deviceID)) {
                if (event.device.mode == 3) {
                    // 设备删除
                } else if (event.device.mode == 2) {
                    // 设备离线
                    updateMode();
                } else if (event.device.mode == 1) {
                    // 更新上线
                    updateMode();
                } else if (event.device.mode == 0) {
                    updateMode();
                }
            }
        }
    }

}
