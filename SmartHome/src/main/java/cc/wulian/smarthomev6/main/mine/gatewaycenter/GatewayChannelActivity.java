package cc.wulian.smarthomev6.main.mine.gatewaycenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.DeviceMoreActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.GatewayInfoBean;
import cc.wulian.smarthomev6.support.customview.InformationChannelView;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GatewayInfoEvent;
import cc.wulian.smarthomev6.support.event.GatewaySoftwareUpdateEvent;
import cc.wulian.smarthomev6.support.event.GatewayStateChangedEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.NetworkUtil;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by syf on 2017/2/16
 */
public class GatewayChannelActivity extends BaseTitleActivity {

    public static final String KEY_VERSION = "key_version";
    public static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 0xf100;
    private RelativeLayout gatewayInfo;
    private TextView gatewayChannel;
    private TextView gatewayFrequencyPoint;
    private TextView gatewayVersion;
    private TextView gatewayName;
    private ImageView gatewayIcon;
    private InformationChannelView channelView;
    private String gwID;
    private WifiManager wifiManager = null;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway_channel, true);
        EventBus.getDefault().register(this);
        wifiManager = (WifiManager) GatewayChannelActivity.this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(wifiManager.getScanResults().size() > 0){
                    channelView.setWifiList(wifiManager.getScanResults());
                    channelView.setVisibility(View.VISIBLE);
                }else{
                    channelView.setVisibility(View.GONE);
                }
            }
        };
        checkPermission();
        getContentResolver()
                .registerContentObserver(
                        Settings.Secure
                                .getUriFor(Settings.Secure.LOCATION_PROVIDERS_ALLOWED),
                        false, mGpsMonitor);
    }

    private final ContentObserver mGpsMonitor = new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            boolean enabled = mLocationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
            if(enabled){
                mHandler.sendEmptyMessage(0);
            }
        }
    };

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        getContentResolver().unregisterContentObserver(mGpsMonitor);
        super.onDestroy();
    }

    @Override
    protected void initTitle() {
        setToolBarTitle(getString(R.string.Channel_Title_01));
    }

    @Override
    protected void initView() {
        gatewayInfo = (RelativeLayout) findViewById(R.id.item_gateway_info);
        channelView = (InformationChannelView) findViewById(R.id.channel_view);
        gatewayChannel = (TextView) findViewById(R.id.gateway_channel);
        gatewayFrequencyPoint = (TextView) findViewById(R.id.gateway_frequency_point);
        gatewayVersion = (TextView) findViewById(R.id.gateway_version);
        gatewayName = (TextView) findViewById(R.id.gateway_name);
        gatewayIcon = (ImageView) findViewById(R.id.gateway_icon);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void initData() {
        super.initData();
        getGatewayInfo();
    }

    @Override
    protected void initListeners() {
        gatewayInfo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.item_gateway_info:
                startActivity(new Intent(this, DeviceSignalActivity.class));
                break;
            default:
                break;
        }
    }

    private void getGatewayInfo() {
        String appID = ((MainApplication) getApplication()).getLocalInfo().appID;
        gwID = Preference.getPreferences().getCurrentGatewayID();
        if (!TextUtils.isEmpty(preference.getCurrentGatewayInfo())) {
            GatewayInfoBean gatewayInfoBean = JSON.parseObject(preference.getCurrentGatewayInfo(), GatewayInfoBean.class);
            gatewayName.setText(gatewayInfoBean.gwName);
            if(StringUtils.isNumeric(gatewayInfoBean.gwChannel)){
                gatewayChannel.setText(gatewayInfoBean.gwChannel);
                int channel = Integer.parseInt(gatewayInfoBean.gwChannel);
                gatewayFrequencyPoint.setText(getFrequencyPoint(channel));
                List<Integer> channels = new ArrayList<>();
                channels.add(channel);
                channelView.setZigbeeInfoList(channels);
            }
            gatewayIcon.setImageResource(getResources().getIdentifier("device_icon_" + gatewayInfoBean.gwType.toLowerCase(), "drawable", this.getPackageName()));
            String version = getIntent().getStringExtra(KEY_VERSION);
            if(!TextUtils.isEmpty(version)){
                gatewayVersion.setText(version);
            }
        }
        if (!TextUtils.isEmpty(gwID)) {
            ((MainApplication) getApplication())
                    .getMqttManager()
                    .publishEncryptedMessage(
                            MQTTCmdHelper.createGatewayInfo(gwID, 0, appID, null),
                            MQTTManager.MODE_GATEWAY_FIRST);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGatewayInfoChangedEvent(GatewayInfoEvent event) {
        if(event.bean != null && TextUtils.equals(event.bean.gwID, gwID)){
            GatewayInfoBean gatewayInfoBean = event.bean;
            gatewayName.setText(gatewayInfoBean.gwName);
            if(TextUtils.isDigitsOnly(gatewayInfoBean.gwChannel)){
                gatewayChannel.setText(gatewayInfoBean.gwChannel);
                int channel = Integer.parseInt(gatewayInfoBean.gwChannel);
                gatewayFrequencyPoint.setText(getFrequencyPoint(channel));
                List<Integer> channels = new ArrayList<>();
                channels.add(channel);
                channelView.setZigbeeInfoList(channels);
            }
            gatewayIcon.setImageResource(getResources().getIdentifier("device_icon_" + gatewayInfoBean.gwType.toLowerCase(), "drawable", this.getPackageName()));
            String version = getIntent().getStringExtra(KEY_VERSION);
            if(!TextUtils.isEmpty(version)){
                gatewayVersion.setText(version);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGatewayStateChanged(GatewayStateChangedEvent event) {
        if (event.bean != null && TextUtils.equals(event.bean.gwID, preference.getCurrentGatewayID())) {
            if ("15".equals(event.bean.cmd)) {

            } else if ("01".equals(event.bean.cmd)) {
                getGatewayInfo();
            }
        }
    }

    private String getFrequencyPoint(int channelValue){//为当前信道值（0～26）
        switch (channelValue) {
            case 0:
            {
                return "868.3";
            }
            case 1: case 2: case 3: case 4: case 5: case 6: case 7: case 8: case 9: case 10:
            {
                int result = 906 + 2*(channelValue - 1);
                return result+"";
            }
            case 11: case 12: case 13: case 14: case 15: case 16: case 17: case 18: case 19: case 20: case 21: case 22: case 23: case 24: case 25: case 26:
            {
                int result = 2401 + 5*(channelValue - 11);
                return result+"";
            }
            default:
            {
                return "";
            }
        }
    }


    private boolean checkPermission() {

        List<String> permissionsList = new ArrayList<String>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions((Activity) this, permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return false;
        }
        getWifiList();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                if (permissions.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                        (permissions.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                                grantResults[1] == PackageManager.PERMISSION_GRANTED)){
                    getWifiList();
                }
                break;
        }
    }

     public boolean isLocationEnabled() {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
             try {
                     locationMode = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
                 } catch (Settings.SettingNotFoundException e) {
                     e.printStackTrace();
                     return false;
                 }
             return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
             locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
             return !TextUtils.isEmpty(locationProviders);
        }
     }

     private void getWifiList(){
         if(isLocationEnabled()){
             if(wifiManager.getScanResults().size() > 0){
                 channelView.setWifiList(wifiManager.getScanResults());
                 channelView.setVisibility(View.VISIBLE);
             }else{
                 channelView.setVisibility(View.GONE);
             }
         }else{
             ToastUtil.show(R.string.Channel_Distance);
         }
     }
}
