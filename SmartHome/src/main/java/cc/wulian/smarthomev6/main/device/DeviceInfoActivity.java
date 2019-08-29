package cc.wulian.smarthomev6.main.device;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.adapter.DeviceInfoAdapter;
import cc.wulian.smarthomev6.main.mine.gatewaycenter.GatewayChannelActivity;
import cc.wulian.smarthomev6.main.mine.gatewaycenter.GatewaySettingActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceBean;
import cc.wulian.smarthomev6.support.core.device.AttributeFinder;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.DeviceInfoBean;
import cc.wulian.smarthomev6.support.core.mqtt.bean.GatewayInfoBean;
import cc.wulian.smarthomev6.support.event.AppSetGwDebugEvent;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GatewaySoftwareUpdateEvent;
import cc.wulian.smarthomev6.support.event.LanguageChangeEvent;
import cc.wulian.smarthomev6.support.event.SignalStrengthEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.FastBlur;
import cc.wulian.smarthomev6.support.utils.LanguageUtil;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

public class DeviceInfoActivity extends BaseTitleActivity {
    private static final String LOAD = "LOAD";
    private RecyclerView mRecyclerView;
    private ImageView iv_device_icon;
    private View v_bottom_line;
    protected String deviceId;
    private Device mDevice;
    private DeviceInfoAdapter mRecyclerAdapter;
    private View lineChannelTop, lineChannelBottom, lineDebug;
    private TextView item_update_gateway, itemChannel;
    private TextView tvSignalValue;
    private LinearLayout layoutChannel, layoutDebug;
    private LinearLayout layoutSignal;
    private ImageView imageDebug;
    private int defaultIconRes;
    private boolean isGateway = false;
    private boolean mIsOneKeyDevice = false;
    private List<Map<String, String>> list;

    private DeviceApiUnit deviceApiUnit;
    private String gatewayVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (LanguageUtil.isChina()) {
            defaultIconRes = R.drawable.default_device_icon;
        } else {
            defaultIconRes = R.drawable.default_device_cn_icon;
        }
        setContentView(R.layout.activity_device_info, true);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void initTitle() {
        setToolBarTitle(getString(R.string.Device_Info));
    }

    @Override
    protected void initView() {
        iv_device_icon = (ImageView) findViewById(R.id.device_icon);
        iv_device_icon.setImageResource(defaultIconRes);
        mRecyclerView = (RecyclerView) findViewById(R.id.item_device_info_recycler);
        v_bottom_line = findViewById(R.id.bottom_line);

        lineDebug = findViewById(R.id.lineDebug);
        layoutDebug = (LinearLayout) findViewById(R.id.layoutDebug);
        imageDebug = (ImageView) findViewById(R.id.imageDebug);
        item_update_gateway = (TextView) findViewById(R.id.item_update_gateway);
        layoutChannel = (LinearLayout) findViewById(R.id.layoutChannel);
        itemChannel = (TextView) findViewById(R.id.item_channel);
        lineChannelTop = findViewById(R.id.lineChannel_top);
        lineChannelBottom = findViewById(R.id.lineChannel_bottom);
        layoutSignal = (LinearLayout) findViewById(R.id.layout_signal);
        tvSignalValue = (TextView) findViewById(R.id.tv_signal_value);
        mRecyclerAdapter = new DeviceInfoAdapter(DeviceInfoActivity.this);
        mRecyclerView.setAdapter(mRecyclerAdapter);
    }

    @Override
    protected void initListeners() {
        item_update_gateway.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        deviceId = getIntent().getStringExtra(DeviceMoreActivity.KEY_DEVICE_ID);
        mDevice = MainApplication.getApplication().getDeviceCache().get(deviceId);
        mIsOneKeyDevice = isOneKeyDevice();
        isGateway = getIntent().getBooleanExtra("isGatewayFlag", false);
        setDebugGwVisible(false);
        if (!TextUtils.isEmpty(preference.getCurrentGatewayInfo())) {
            GatewayInfoBean gatewayInfoBean = JSON.parseObject(preference.getCurrentGatewayInfo(), GatewayInfoBean.class);
            if (gatewayInfoBean != null && TextUtils.equals(gatewayInfoBean.gwID, deviceId)) {
                if (TextUtils.equals("GW14", gatewayInfoBean.gwType)) {
                    layoutSignal.setVisibility(View.VISIBLE);
                    MainApplication.getApplication().getMqttManager()
                            .publishEncryptedMessage(MQTTCmdHelper.getSignalStrength(gatewayInfoBean.gwID, "start"),
                                    MQTTManager.MODE_GATEWAY_FIRST);
                }
                /*只有网关在线的时候才进行正常判断*/
                if (preference.getCurrentGatewayState().equals("1")) {
                    imageDebug.setOnClickListener(onclick_imageDebug);
                    boolean isShowDebug = preference.getAppIsShowGwDebug();
                    if (!isShowDebug) {
                        iv_device_icon.setOnLongClickListener(onLongClick_iv_device_icon);
                    }
                    setDebugGwVisible(isShowDebug);
                } else {
                    setDebugGwVisible(false);
                }
            }
        }

        if (isGateway) {
            layoutChannel.setVisibility(View.VISIBLE);
            lineChannelTop.setVisibility(View.VISIBLE);
            lineChannelBottom.setVisibility(View.VISIBLE);
            if ("1".equals(preference.getCurrentGatewayState())) {
                itemChannel.setTextColor(getResources().getColor(R.color.black));
                layoutChannel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(DeviceInfoActivity.this, GatewayChannelActivity.class);
                        intent.putExtra(GatewayChannelActivity.KEY_VERSION, gatewayVersion);
                        startActivity(intent);
                    }
                });
            } else {
                itemChannel.setTextColor(getResources().getColor(R.color.grey));
            }
        }

        list = new ArrayList<>();
        if (Preference.ENTER_TYPE_ACCOUNT.equals(preference.getUserEnterType())) {
            iv_device_icon.setVisibility(View.VISIBLE);
            getZigBeeDeviceInfoByCloud();
        } else {
            iv_device_icon.setVisibility(View.GONE);
            if (isGateway) {
                //局域网登录下网关信息
                getGatewayInfo();
            } else {
                //局域网登录下设备信息
                getZigBeeDeviceInfoByGw();
            }
        }

    }

    /**
     * the device is one key device?
     *
     * @return
     */
    private boolean isOneKeyDevice() {
        if (null == mDevice) {
            return false;
        }
        if (mDevice.type.equalsIgnoreCase("Bq")) {
            return true;
        }
        return false;
    }

    /**
     * 账号下登录获取ZigBee设备信息
     */
    private void getZigBeeDeviceInfoByCloud() {
        //先从本地获取
        if (isGateway) {
            GatewayInfoBean currentGateway = JSON.parseObject(Preference.getPreferences().getCurrentGatewayInfo(), GatewayInfoBean.class);
            Map<String, String> map1 = new HashMap<>();
            map1.put("name", getString(DeviceInfoDictionary.getDefaultNameByType(currentGateway.gwType)));
            list.add(map1);
            if (mIsOneKeyDevice) {
                Map<String, String> map3 = new HashMap<>();
                map3.put("number", deviceId);
                list.add(map3);
            }
            Map<String, String> map2 = new HashMap<>();
            map2.put("version", currentGateway.gwVer);
            list.add(map2);
            gatewayVersion = currentGateway.gwVer;
        } else {
            Device device = MainApplication.getApplication().getDeviceCache().get(deviceId);
            Map<String, String> map1 = new HashMap<>();
            map1.put("name", getString(DeviceInfoDictionary.getDefaultNameByType(device.type)));
            list.add(map1);
            if (mIsOneKeyDevice) {
                Map<String, String> map3 = new HashMap<>();
                map3.put("number", deviceId);
                list.add(map3);
            }
            Map<String, String> map2 = new HashMap<>();
            try {
                org.json.JSONObject jsonObject = new org.json.JSONObject(device.data);
                String version = jsonObject.optString("zVer");
                map2.put("version", version);
                if (!TextUtils.isEmpty(version)) {
                    list.add(map2);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (list.size() > 0) {
            mRecyclerAdapter.setData(list);
        }
        //从云获取
        if (deviceApiUnit == null) {
            deviceApiUnit = new DeviceApiUnit(this);
        }
        progressDialogManager.showDialog(LOAD, this, getString(R.string.Loading), null, getResources().getInteger(R.integer.http_timeout));
        deviceApiUnit.doGetZigBeeDeviceInfo(deviceId, new DeviceApiUnit.DeviceApiCommonListener<DeviceBean>() {
            @Override
            public void onSuccess(DeviceBean bean) {
                progressDialogManager.dimissDialog(LOAD, 0);
                list.clear();
                if (bean != null) {
                    if (!TextUtils.isEmpty(bean.url)) {
                        DisplayImageOptions options = new DisplayImageOptions.Builder()
                                .showImageOnLoading(defaultIconRes) // 设置图片下载期间显示的图片
                                .showImageForEmptyUri(defaultIconRes) // 设置图片Uri为空或是错误的时候显示的图片
                                .showImageOnFail(defaultIconRes) // 设置图片加载或解码过程中发生错误显示的图片
                                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                                .build(); // 创建配置过得DisplayImageOption对象
                        ImageLoader.getInstance().displayImage(bean.url, iv_device_icon, options);
                    }
                    if (!TextUtils.isEmpty(bean.type)) {
                        Map<String, String> info = new HashMap<>();
                        info.put("name", getString(DeviceInfoDictionary.getDefaultNameByType(bean.type)));
                        list.add(info);
                    }
                    if (mIsOneKeyDevice) {
                        Map<String, String> map3 = new HashMap<>();
                        map3.put("number", deviceId);
                        list.add(map3);
                    }
                    if (!TextUtils.isEmpty(bean.version)) {
                        gatewayVersion = bean.version;
                        Map<String, String> info = new HashMap<>();
                        info.put("version", bean.version);
                        list.add(info);
                    }
                    if (isGateway) {
                        GatewayInfoBean currentGateway = JSON.parseObject(Preference.getPreferences().getCurrentGatewayInfo(), GatewayInfoBean.class);
                        if (currentGateway != null && !TextUtils.isEmpty(currentGateway.localIP)) {
                            Map<String, String> info = new HashMap<>();
                            info.put("localIP", currentGateway.localIP);
                            list.add(info);
                        }
                    }

                    if (mDevice != null) {
                        if (mDevice.type.equals("DD")) {
                            SparseArray<String> map = AttributeFinder.toMap(mDevice);
                            String type = map.get(0x000f);
                            String roomName = map.get(0x0011);
                            String roomNo = map.get(0x0012);

                            if (!TextUtils.isEmpty(type)) {
                                Map<String, String> m = new HashMap<>();
                                m.put(getString(R.string.Device_DD_set_model), type);
                                list.add(m);
                            }

                            if (!TextUtils.isEmpty(roomName)) {
                                Map<String, String> m = new HashMap<>();
                                m.put(getString(R.string.Device_DD_set_room_name), roomName);
                                list.add(m);
                            }

                            if (!TextUtils.isEmpty(roomNo)) {
                                Map<String, String> m = new HashMap<>();
                                m.put(getString(R.string.Device_DD_set_room_id), roomNo);
                                list.add(m);
                            }
                        } else if (mDevice.type.equals("Oj")) {
                            SparseArray<String> map = AttributeFinder.toMap(mDevice);
                            String type = map.get(0x810C);
                            if (!TextUtils.isEmpty(type)) {
                                if ("01".equals(type)) {
                                    Map<String, String> m = new HashMap<>();
                                    m.put(getString(R.string.airsystem_Oj_Brand), getString(R.string.airsystem_Oj_broan));
                                    list.add(m);
                                }
                            }
                        }
                    }
                    if (list.size() > 0) {
                        mRecyclerAdapter.setData(list);
                        v_bottom_line.setVisibility(View.VISIBLE);
                    } else {
                        v_bottom_line.setVisibility(View.INVISIBLE);
                    }
                    if (isGateway) {
                        checkGatewaySoftwareUpdate();
                    }
                }
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(LOAD, 0);
                ToastUtil.show(R.string.Picture_Failure_Toast);
            }
        });
    }


    /**
     * 局域网登录获取ZigBee设备的信息
     */
    private void getZigBeeDeviceInfoByGw() {
        Device device = MainApplication.getApplication().getDeviceCache().get(deviceId);
        Map<String, String> map1 = new HashMap<>();
        map1.put("name", getString(DeviceInfoDictionary.getDefaultNameByType(device.type)));
        list.add(map1);
        if (mIsOneKeyDevice) {
            Map<String, String> map3 = new HashMap<>();
            map3.put("number", deviceId);
            list.add(map3);
        }
        Map<String, String> map2 = new HashMap<>();
        try {
            org.json.JSONObject jsonObject = new org.json.JSONObject(device.data);
            String version = jsonObject.optString("zVer");
            map2.put("version", version);
            if (!TextUtils.isEmpty(version)) {
                list.add(map2);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (device != null) {
            if (device.type.equals("DD")) {
                SparseArray<String> map = AttributeFinder.toMap(device);
                String type = map.get(0x000f);
                String roomName = map.get(0x0011);
                String roomNo = map.get(0x0012);

                if (!TextUtils.isEmpty(type)) {
                    Map<String, String> m = new HashMap<>();
                    m.put(getString(R.string.Device_DD_set_model), type);
                    list.add(m);
                }

                if (!TextUtils.isEmpty(roomName)) {
                    Map<String, String> m = new HashMap<>();
                    m.put(getString(R.string.Device_DD_set_room_name), roomName);
                    list.add(m);
                }

                if (!TextUtils.isEmpty(roomNo)) {
                    Map<String, String> m = new HashMap<>();
                    m.put(getString(R.string.Device_DD_set_room_id), roomNo);
                    list.add(m);
                }
            } else if (device.type.equals("Oj")) {
                SparseArray<String> map = AttributeFinder.toMap(device);
                String type = map.get(0x810C);
                if (!TextUtils.isEmpty(type)) {
                    if ("01".equals(type)) {
                        Map<String, String> m = new HashMap<>();
                        m.put(getString(R.string.airsystem_Oj_Brand), getString(R.string.airsystem_Oj_broan));
                        list.add(m);
                    }
                }
            }
        }
        if (list.size() > 0) {
            mRecyclerAdapter.setData(list);
            v_bottom_line.setVisibility(View.VISIBLE);
        } else {
            v_bottom_line.setVisibility(View.INVISIBLE);
        }
    }


    /**
     * 局域网登录获取网关信息
     */
    private void getGatewayInfo() {
        GatewayInfoBean currentGateway = JSON.parseObject(Preference.getPreferences().getCurrentGatewayInfo(), GatewayInfoBean.class);
        Map<String, String> map1 = new HashMap<>();
        map1.put("name", getString(DeviceInfoDictionary.getDefaultNameByType(currentGateway.gwType)));
        list.add(map1);
        if (mIsOneKeyDevice) {
            Map<String, String> map3 = new HashMap<>();
            map3.put("number", deviceId);
            list.add(map3);
        }
        Map<String, String> map2 = new HashMap<>();
        map2.put("version", currentGateway.gwVer);
        list.add(map2);
        Map<String, String> map4 = new HashMap<>();
        map4.put("localIP", currentGateway.localIP);
        list.add(map4);
        if (list.size() > 0) {
            mRecyclerAdapter.setData(list);
            v_bottom_line.setVisibility(View.VISIBLE);
        } else {
            v_bottom_line.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 判断网关版本
     * 如果网关版本低，则会收到GatewaySoftwareUpdateEvent，显示网关升级相关View
     */
    private void checkGatewaySoftwareUpdate() {
        if (deviceApiUnit == null) {
            deviceApiUnit = new DeviceApiUnit(this);
        }
        deviceApiUnit.checkGatewaySoftwareUpdate();
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == item_update_gateway) {
            //发送网关升级命令
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(
                            MQTTCmdHelper.gatewayAutoupgradeOrReboot(
                                    Preference.getPreferences().getCurrentGatewayID(),
                                    MainApplication.getApplication().getLocalInfo().appID,
                                    0), MQTTManager.MODE_GATEWAY_FIRST);
            //界面变化
            item_update_gateway.setTextColor(getResources().getColor(R.color.v6_text_gray_light));
            item_update_gateway.setText(R.string.GatewayCenter_Hold);
            item_update_gateway.setEnabled(false);
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
        if (bean != null) {
            if (bean.mode == 3) {//删除设备时退出界面
                finish();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLanguageChangeEvent(LanguageChangeEvent event) {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSignalStrengthEvent(SignalStrengthEvent event) {
        if (event != null && event.bean != null) {
            int value = event.bean.getValue();
            showSignStrength(value);
        }
    }

    private void showSignStrength(int value) {
        if (value >= -35) {
            tvSignalValue.setText(getString(R.string.Mine_Signal_strength_1) + "(" + value + ")");
        } else if (value >= -55) {
            tvSignalValue.setText(getString(R.string.Mine_Signal_strength_2) + "(" + value + ")");
        } else if (value >= -70) {
            tvSignalValue.setText(getString(R.string.Mine_Signal_strength_3) + "(" + value + ")");
        } else if (value >= -82) {
            tvSignalValue.setText(getString(R.string.Mine_Signal_strength_4) + "(" + value + ")");
        } else {
            tvSignalValue.setText(getString(R.string.Mine_Signal_strength_5) + "(" + value + ")");
        }
    }

    private void setDebugGwVisible(boolean isVisible) {
        if (isVisible) {
            lineDebug.setVisibility(View.VISIBLE);
            layoutDebug.setVisibility(View.VISIBLE);
            imageDebug.setVisibility(View.VISIBLE);
        } else {
            lineDebug.setVisibility(View.GONE);
            layoutDebug.setVisibility(View.GONE);
            imageDebug.setVisibility(View.GONE);
        }
    }

    boolean isOpenDebug = false;//当前的调试状态
    View.OnClickListener onclick_imageDebug = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            appSetDebug();
        }
    };
    View.OnLongClickListener onLongClick_iv_device_icon = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            /*只有在网关在线的时候才可以显示出来*/
            if (preference.getCurrentGatewayState().equals("1")) {
                setDebugGwVisible(true);
                preference.saveAppIsShowGwDebug(true);
            }
            return true;

        }
    };

    private void appSetDebug() {
        String gwID = deviceId;
        String appID = MainApplication.getApplication().getLocalInfo().appID;
        String status = "1";
        if (isOpenDebug) {
            status = "0";
        }
        MainApplication.getApplication().getMqttManager().
                publishEncryptedMessage(
                        MQTTCmdHelper.appSetGwDebug(gwID, appID, status),
                        MQTTManager.MODE_GATEWAY_FIRST);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAppSetGwDebugEvent(AppSetGwDebugEvent event) {
        if (event != null) {
            if (!StringUtil.isNullOrEmpty(event.gwID) && TextUtils.equals(event.gwID, deviceId)) {
                if (TextUtils.equals(event.status, "1")) {
                    isOpenDebug = true;
                    imageDebug.setImageResource(R.drawable.icon_on);
                } else {
                    isOpenDebug = false;
                    imageDebug.setImageResource(R.drawable.icon_off);
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGatewaySoftwareUpdateEvent(GatewaySoftwareUpdateEvent event) {
        if (isGateway) {
            if (event.status != 0) {
                //提醒升级网关
                for (int i = 0; i < list.size(); i++) {//先检查列表中是否已有新固件版本一栏，如果有则删除重新添加
                    Map<String, String> map = list.get(i);
                    String content = map.get(getString(R.string.GatewayCenter_Newfirmware));
                    if (content != null) {
                        list.remove(i);
                        break;
                    }
                }
                Map<String, String> map = new HashMap<>();
                map.put(getString(R.string.GatewayCenter_Newfirmware), event.newVersion);
                list.add(map);
                mRecyclerAdapter.setData(list);
                item_update_gateway.setVisibility(View.VISIBLE);
            } else {
                for (int i = 0; i < list.size(); i++) {
                    Map<String, String> map = list.get(i);
                    String version = map.get("version");
                    if (version != null) {
                        map.put("version", event.newVersion);
                    }
                    String content = map.get(getString(R.string.GatewayCenter_Newfirmware));
                    if (content != null) {
                        list.remove(i);
                        break;
                    }
                }
                mRecyclerAdapter.setData(list);
                item_update_gateway.setVisibility(View.GONE);
            }
        }
    }
}
