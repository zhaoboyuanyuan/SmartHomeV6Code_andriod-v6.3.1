package cc.wulian.smarthomev6.main.device.config;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Permission;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.ConfigWiFiInfoModel;
import cc.wulian.smarthomev6.entity.SpannableBean;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.main.device.AddDeviceActivity;
import cc.wulian.smarthomev6.support.tools.WiFiLinker;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.ConstantsUtil;
import cc.wulian.smarthomev6.support.utils.DialogUtil;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;
import cc.wulian.smarthomev6.support.utils.WifiUtil;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by hxc on 2017/7/6.
 * func:摄像机欢迎界面
 */

public class DeviceWelcomeFragment extends WLFragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private Button btnNextStep;
    private Button btnWireConnect;
    private Button btnWifiConnect;
    private Context context;
    private ConfigWiFiInfoModel configData;
    private TextView tvConfigTips;
    private TextView tvOutdoorTips;
    private ImageView ivIcon;
    private LinearLayout llBindCamera;
    private LinearLayout llBindCameraTips;
    private RelativeLayout rlOutdoorLayout;
    private CheckBox cbBind;
    private String seed;
    private String deviceType;
    private WiFiLinker wiFiLinker;
    private DeviceInputWifiFragment deviceInputWifiFragment;
    private DeviceGetReadyFragment deviceGetReadyFragment;
    private DeviceWifiDirectFragment deviceWifiDirectFragment;
    private static final String PERMISSION_ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String PERMISSION_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private WLDialog dialog;

    public static DeviceWelcomeFragment newInstance(ConfigWiFiInfoModel configData) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("configData", configData);
        DeviceWelcomeFragment deviceIdQueryFragment = new DeviceWelcomeFragment();
        deviceIdQueryFragment.setArguments(bundle);
        return deviceIdQueryFragment;
    }

    @Override
    public int layoutResID() {
        return R.layout.activity_device_welcome;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        Bundle bundle = getArguments();
        configData = bundle.getParcelable("configData");
    }

    @Override
    public void initListener() {
        super.initListener();
        btnNextStep.setOnClickListener(this);
        btnWifiConnect.setOnClickListener(this);
        btnWireConnect.setOnClickListener(this);
        cbBind.setOnCheckedChangeListener(this);
        mImgBack.setOnClickListener(this);
    }

    @Override
    public void initView(View v) {
        btnNextStep = (Button) v.findViewById(R.id.btn_next_step);
        btnWireConnect = (Button) v.findViewById(R.id.btn_wired_connection);
        btnWifiConnect = (Button) v.findViewById(R.id.btn_wifi_connection);
        tvConfigTips = (TextView) v.findViewById(R.id.tv_config_tips);
        tvOutdoorTips = (TextView) v.findViewById(R.id.tv_outdoor_tips);
        ivIcon = (ImageView) v.findViewById(R.id.iv_icon);
        llBindCamera = (LinearLayout) v.findViewById(R.id.ll_bind_camera);
        llBindCameraTips = (LinearLayout) v.findViewById(R.id.ll_bind_camera_tips);
        cbBind = (CheckBox) v.findViewById(R.id.cb_bind_at_time);
        rlOutdoorLayout = (RelativeLayout) v.findViewById(R.id.rl_outdoor_layout);
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(btnNextStep, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setBackground(btnWireConnect, SkinResouceKey.BITMAP_BUTTON_BG_S);
//        skinManager.setTextButtonColorAndBackground(btnWifiConnect, SkinResouceKey.COLOR_NAV);
        Integer btnTextColor = skinManager.getColor(SkinResouceKey.COLOR_BUTTON_TEXT);
        if (btnTextColor != null) {
            btnNextStep.setTextColor(btnTextColor);
            btnWireConnect.setTextColor(btnTextColor);
        }
    }

    @Override
    protected void initTitle(View v) {
        super.initTitle(v);
        setLeftImg(R.drawable.icon_back);
    }

    @Override
    protected void initData() {
        super.initData();
        seed = configData.getSeed();
        deviceType = configData.getDeviceType();
        wiFiLinker = new WiFiLinker();
        wiFiLinker.WifiInit(context);
        updateViewByDeviceId();
        checkPermission();

    }


    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(context, PERMISSION_ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, PERMISSION_ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{PERMISSION_ACCESS_COARSE_LOCATION, PERMISSION_ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);

        }
    }


    private void updateViewByDeviceId() {
        switch (deviceType) {
            case "CMICA1":
                tvConfigTips.setText(getResources().getString(R.string.Tips_Device_Welcome));
                ivIcon.setImageResource(R.drawable.icon_cmic08);
                mTvTitle.setText(getString(R.string.Title_Start_device));
                break;
            case "CMICA2":
                StringUtil.addColorOrSizeorEvent(tvConfigTips,
                        getResources().getString(R.string.Camera_LED_Tip), new SpannableBean[]{new SpannableBean(getResources().getColor(R.color.v6_green), 16, null)});
                ivIcon.setImageResource(R.drawable.icon_camera_2);
                mTvTitle.setText(getString(R.string.Start_Camera));
                if (configData.isAddDevice() && configData.getScanType().equals(ConstantsUtil.BIND_CAMERA_GATEWAY_ENTRY)) {
                    if (TextUtils.isEmpty(seed)) {
                        cbBind.setChecked(false);
                    } else {
                        llBindCamera.setVisibility(View.VISIBLE);
                        llBindCameraTips.setVisibility(View.VISIBLE);
                        cbBind.setChecked(true);
                    }
                }
                break;
            case "CMICA3":
            case "CMICA6":
                StringUtil.addColorOrSizeorEvent(tvConfigTips,
                        getResources().getString(R.string.Camera_LED_Tip), new SpannableBean[]{new SpannableBean(getResources().getColor(R.color.v6_green), 16, null)});
                ivIcon.setImageResource(R.drawable.icon_camera_3);
                mTvTitle.setText(getString(R.string.Add_Penguin_Title));
                if (configData.isAddDevice() && configData.getScanType().equals(ConstantsUtil.BIND_CAMERA_GATEWAY_ENTRY)) {
                    if (TextUtils.isEmpty(seed)) {
                        cbBind.setChecked(false);
                    } else {
                        llBindCamera.setVisibility(View.VISIBLE);
                        llBindCameraTips.setVisibility(View.VISIBLE);
                        cbBind.setChecked(true);
                    }
                }
                break;

            case "CMICA4":
                StringUtil.addColorOrSizeorEvent(tvConfigTips,
                        getResources().getString(R.string.Cylincam_Red_Light), new SpannableBean[]{new SpannableBean(getResources().getColor(R.color.v6_green), 16, null)});
                if (configData.isAddDevice()) {
                    mTvTitle.setText(getString(R.string.Add_Cylincam));
                } else {
                    mTvTitle.setText(getString(R.string.Start_Camera));
                }
                ivIcon.setImageResource(R.drawable.icon_camera_4_red);
                break;

            case "CMICA5":
                mTvTitle.setText(getString(R.string.Start_Camera));
                tvOutdoorTips.setVisibility(View.VISIBLE);
                StringUtil.addColorOrSizeorEvent(tvConfigTips,
                        getResources().getString(R.string.Connect_Camerea_Tip), new SpannableBean[]{new SpannableBean(getResources().getColor(R.color.v6_green), 16, null)});
                ivIcon.setImageResource(R.drawable.icon_camera_5_light);
                btnNextStep.setVisibility(View.GONE);
                rlOutdoorLayout.setVisibility(View.VISIBLE);
                if (!configData.isAddDevice()) {
                    btnWireConnect.setVisibility(View.INVISIBLE);
                }
                if (configData.isAddDevice() && configData.getScanType().equals(ConstantsUtil.BIND_CAMERA_GATEWAY_ENTRY)) {
                    if (TextUtils.isEmpty(seed)) {
                        cbBind.setChecked(false);
                    } else {
                        llBindCamera.setVisibility(View.VISIBLE);
                        llBindCameraTips.setVisibility(View.VISIBLE);
                        cbBind.setChecked(true);
                    }
                }
                break;
            default:
                break;
        }
    }

    private boolean checkWiFiConnectState() {
        if (wiFiLinker.isWiFiEnable()) {
            WifiInfo info = wiFiLinker.getWifiInfo();
            if (info != null) {
                String ssid = info.getSSID();
                if (TextUtils.isEmpty(ssid) || "0x".equals(ssid)) {
                    ToastUtil.show(getString(R.string.Open_WIFI));
                    return false;
                } else {
                    //android 8.1.0修复了一个bug，当没有位置权限时无法获取当前手机连接的wifi的ssid
                    if (info.getHiddenSSID() || "<unknown ssid>".equals(ssid)) {
                        return false;
                    } else {
                        return true;
                    }
                }
            } else {
                ToastUtil.show(getString(R.string.Open_WIFI));
                return false;
            }
        } else {
            ToastUtil.show(getString(R.string.Open_WIFI));
            return false;
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        switch (id) {
            case R.id.cb_bind_at_time:
                if (isChecked) {
                    configData.setAddDevice(true);
                    configData.setSeed(seed);
                } else {
                    configData.setAddDevice(false);
                    configData.setSeed("");
                }
                break;
        }
    }

    //4G企鹅摄像机不需要判断手机是否连上wifi
    private void isDeviceCMICA6() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        deviceGetReadyFragment = deviceGetReadyFragment.newInstance(configData);
        ft.replace(android.R.id.content, deviceGetReadyFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_next_step) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                checkPermission();
                return;
            } else {
                if (!WifiUtil.isLocationEnabled(getActivity())) {
                    dialog = DialogUtil.showCommonDialog(getActivity(), "",
                            getString(R.string.Get_WiFi_Location), getString(R.string.Open_Btn), getString(R.string.Cancel), new WLDialog.MessageListener() {
                                @Override
                                public void onClickPositive(View var1, String msg) {
                                    Intent intent = new Intent(
                                            Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivityForResult(intent, 0);
                                    dialog.dismiss();
                                }

                                @Override
                                public void onClickNegative(View var1) {
                                    dialog.dismiss();
                                }
                            });
                    dialog.show();
                    return;
                }
            }
            if (TextUtils.equals(configData.getDeviceType(), "CMICA6")) {
                isDeviceCMICA6();
            } else {
                if (checkWiFiConnectState()) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    deviceInputWifiFragment = deviceInputWifiFragment.newInstance(configData);
                    ft.replace(android.R.id.content, deviceInputWifiFragment, DeviceInputWifiFragment.class.getName());
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }
        } else if (id == R.id.btn_wired_connection) {//户外摄像机有线连接
            if (checkWiFiConnectState()) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                configData.setConfigWiFiType(ConstantsUtil.CONFIG_WIRED_THIRD_BIND_SETTING);
                deviceWifiDirectFragment = deviceWifiDirectFragment.newInstance(configData);
                ft.replace(android.R.id.content, deviceWifiDirectFragment, DeviceWifiDirectFragment.class.getName());
                ft.addToBackStack(null);
                ft.commit();
            }
        } else if (id == R.id.btn_wifi_connection) {
            if (checkWiFiConnectState()) {//户外摄像机WiFi连接
                configData.setConfigWiFiType(ConstantsUtil.CONFIG_DIRECT_WIFI_THIRD_BIND_SETTING);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                deviceInputWifiFragment = deviceInputWifiFragment.newInstance(configData);
                ft.replace(android.R.id.content, deviceInputWifiFragment, DeviceInputWifiFragment.class.getName());
                ft.addToBackStack(null);
                ft.commit();
            }
        } else if (id == R.id.base_img_back_fragment) {
            if (TextUtils.equals(configData.getScanType(), ConstantsUtil.DEVICE_SCAN_ENTRY)
                    || TextUtils.equals(configData.getAsGateway(), ConstantsUtil.NEED_JUMP_BIND_GATEWAY_FLAG)
                    || TextUtils.equals(configData.getScanType(), ConstantsUtil.CAMERA_ADD_ENTRY)) {
                startActivity(new Intent(getActivity(), AddDeviceActivity.class));
                getActivity().finish();
            } else {
                getActivity().finish();
            }
        }
    }
}
