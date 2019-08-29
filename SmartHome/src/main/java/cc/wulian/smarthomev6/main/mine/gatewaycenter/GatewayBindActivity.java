package cc.wulian.smarthomev6.main.mine.gatewaycenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.LocalInfo;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.config.DeviceStartConfigActivity;
import cc.wulian.smarthomev6.main.device.gateway_mini.config.MiniGatewayActivity;
import cc.wulian.smarthomev6.main.device.gateway_wall.config.WallGatewayActivity;
import cc.wulian.smarthomev6.main.login.GatewaySearchPop;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.CheckV6SupportBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceIsPushBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceListBeanAll;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.device.GatewayManager;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTCmdHelper;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.socket.GatewayBean;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GatewayStateChangedEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.RegularTool;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.tools.skin.ButtonSkinWrapper;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.zxing.activity.CaptureActivity;
import cc.wulian.smarthomev6.support.utils.ConstantsUtil;
import cc.wulian.smarthomev6.support.utils.DialogUtil;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.UrlUtil;

/**
 * 作者: mamengchao
 * 时间: 2017/3/30 0030
 * 描述:绑定网关
 * 联系方式: 805901025@qq.com
 */

public class GatewayBindActivity extends BaseTitleActivity implements View.OnClickListener {

    private static final String BIND_GATEWAY = "BIND_GATEWAY";
    private static final String VIRTUAL_GW_ID = "000000000000";
    private Context context;
    private EditText etGatewayUsername;
    private EditText etGatewayPassword;
    private Button btnGatewayLogin;
    private ImageView imageScan;
    private TextView tvResetTip;
    private TextView tvVirtualGw;
    private View btn_auto;
    private String gwID;
    private String deviceId;
    private int totalCount;
    private TextWatcher username_watcher;
    private TextWatcher password_watcher;

    private GatewayBean gatewayBean;
    private DeviceApiUnit deviceApiUnit;
    private WLDialog bindGatewayDialog;
    private WLDialog miniDialog;
    private WLDialog unknownGatewayDialog;
    private String miniGatewayId;
    private boolean isMiniGateway = false;
    private boolean qrScan = false;
    private boolean changePwd = false;

    private ButtonSkinWrapper buttonSkinWrapper;
    private GatewaySearchPop gatewaySearchPop;
    private static final int CONFIRM = 1;
    private List<DeviceBean> bindAllGateway;
    private GatewayManager gatewayManager;

    public static void start(Context context, String deviceId, boolean isMiniGateway) {
        Intent intent = new Intent(context, GatewayBindActivity.class);
        intent.putExtra("deviceId", deviceId);
        intent.putExtra("isMiniGateway", isMiniGateway);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        deviceApiUnit = new DeviceApiUnit(this);
        setContentView(R.layout.activity_gateway_bind, true);
    }

    @Override
    protected void onDestroy() {
        if (gatewaySearchPop != null && gatewaySearchPop.isShowing()) {
            gatewaySearchPop.dismiss();
        }
        super.onDestroy();
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitleAndRightImg(getString(R.string.Gateway_bind), R.drawable.icon_help);
    }

    @Override
    protected void initView() {
        super.initView();
        gatewaySearchPop = new GatewaySearchPop(this, new GatewaySearchPop.OnGatewaySelectedListener() {
            @Override
            public void onGatewaySearchFinish(List<GatewayBean> list) {
                if (!gatewaySearchPop.isShowing() && !isDestroyed()) {
                    if (list.size() > 0) {
                        gatewaySearchPop.showAsDropDown(etGatewayUsername);
                    }
                }
            }

            @Override
            public void onGatewaySelected(GatewayBean bean) {
                gatewayBean = bean;
                etGatewayUsername.setText(bean.gwID);
                etGatewayUsername.setSelection(etGatewayUsername.getText().length());
                etGatewayPassword.requestFocus();
            }
        });
        etGatewayUsername = (EditText) findViewById(R.id.et_gateway_username);
        etGatewayPassword = (EditText) findViewById(R.id.et_gateway_password);
        tvResetTip = (TextView) findViewById(R.id.gw_pwd_hint);
        etGatewayPassword.setTypeface(Typeface.DEFAULT);
        etGatewayPassword.setTransformationMethod(new PasswordTransformationMethod());
        btnGatewayLogin = (Button) findViewById(R.id.btn_gateway_login);
        btnGatewayLogin.setText(R.string.Gateway_bind);
        imageScan = (ImageView) findViewById(R.id.iv_gateway_scan);
        btn_auto = findViewById(R.id.btn_auto);
        tvVirtualGw = (TextView) findViewById(R.id.tv_virtual_gw);
        tvVirtualGw.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        //按钮
        buttonSkinWrapper = new ButtonSkinWrapper(btnGatewayLogin);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        initWatcher();
        etGatewayUsername.addTextChangedListener(username_watcher);
        etGatewayPassword.addTextChangedListener(password_watcher);
        btnGatewayLogin.setOnClickListener(this);
        imageScan.setOnClickListener(this);
        btn_auto.setOnClickListener(this);
        tvVirtualGw.setOnClickListener(this);
        updateButtonState();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            etGatewayUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        gatewaySearchPop.startSearch();
                    } else {
                        gatewaySearchPop.dismiss();
                    }
                }
            });
            gatewaySearchPop.startSearch();
        }
    }

    @Override
    protected void initData() {
        super.initData();
        deviceId = getIntent().getStringExtra("deviceId");
        isMiniGateway = getIntent().getBooleanExtra("isMiniGateway", false);
        changePwd = getIntent().getBooleanExtra("changePwd", false);
        bindAllGateway = new ArrayList<>();
        gatewayManager = new GatewayManager();
        if (!TextUtils.isEmpty(deviceId)) {
            if (deviceId.startsWith("CG") || deviceId.startsWith("ME")) {
                qrScan = true;//此处是为了从其他页面跳过来不在校验
                gwID = deviceId;
                etGatewayUsername.setText(gwID);
            } else if (deviceId.length() >= 12) {
                gwID = deviceId.substring(deviceId.length() - 12, deviceId.length()).toUpperCase();
                etGatewayUsername.setText(gwID);
            }
        }
        initVirtualTextView();
    }

    private void initVirtualTextView() {
        final List<DeviceBean> gateWayList = new ArrayList<>();
        deviceApiUnit = new DeviceApiUnit(context);
        deviceApiUnit.getExperienceGatewayStatus(VIRTUAL_GW_ID, new DeviceApiUnit.DeviceApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                String jsonData = bean.toString();
                String data = null;
                try {
                    org.json.JSONObject object = new org.json.JSONObject(jsonData);
                    data = object.optString("data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (TextUtils.equals("1", data)) {
                    deviceApiUnit.doGetAllDevice(new DeviceApiUnit.DeviceApiCommonListener<List<DeviceBean>>() {
                        @Override
                        public void onSuccess(List<DeviceBean> deviceBeanList) {
                            gateWayList.clear();
                            List<DeviceBean> allGateway = new ArrayList<>();
                            for (DeviceBean deviceBean : deviceBeanList) {
                                if (!deviceBean.isShared()) {
                                    gateWayList.add(deviceBean);
                                }
                                allGateway.add(deviceBean);
                            }
                            tvVirtualGw.setVisibility((allGateway.isEmpty() || allGateway.size() == 0) ? View.VISIBLE : View.GONE);
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            Log.i(TAG, "onFail: " + msg);
                        }
                    });
                }
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    private void initWatcher() {
        username_watcher = new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateButtonState();
                if (qrScan) {//这个地方是为了扫码后带出id重复调v6是否支持接口
                    qrScan = !qrScan;
                } else {
                    judgeCameraGateway();
                }
                gatewaySearchPop.filterGateway(s.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                etGatewayPassword.setText("");
            }
        };

        password_watcher = new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateButtonState();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        };
    }

    private void updateButtonState() {
        if (etGatewayUsername.getText().length() == 0 || etGatewayPassword.getText().length() < 6) {
            buttonSkinWrapper.setActive(false);
        } else {
            buttonSkinWrapper.setActive(true);
        }
    }

    private void judgeCameraGateway() {
        String inputId = etGatewayUsername.getText().toString().trim();
        if (inputId.startsWith("CG") || inputId.startsWith("ME")) {
            if (inputId.length() == 11) {
                doCheckV6Support(inputId, false);
            }
        } else if (inputId.length() == 16 && !inputId.startsWith("cmic") && !inputId.startsWith("GWMN02")) {
            inputId = "cmic" + inputId;
            doCheckV6Support(inputId, false);
        } else if (inputId.startsWith("cmic") && inputId.length() == 20) {
            doCheckV6Support(inputId, false);
        } else if (inputId.startsWith("GWMN02") && inputId.length() == 18) {
            doCheckV6Support(inputId, false);
        }
    }


    /**
     * 校验v6是否支持
     *
     * @param deviceId
     * @param isQrScan//是否是扫描
     */
    private void doCheckV6Support(String deviceId, final boolean isQrScan) {
        deviceApiUnit.doCheckV6Support(deviceId, new DeviceApiUnit.DeviceApiCommonListener<CheckV6SupportBean>() {
            @Override
            public void onSuccess(CheckV6SupportBean bean) {
                CheckV6SupportBean.SupportDevice gwDevice = null;
                CheckV6SupportBean.SupportDevice cameraDevice = null;
                List<CheckV6SupportBean.SupportDevice> supportList = bean.device;
                for (CheckV6SupportBean.SupportDevice device :
                        supportList) {
                    if (device.deviceFlag == 1) {//网关
                        gwDevice = device;
                    } else if (device.deviceFlag == 2) {//摄像机
                        cameraDevice = device;
                    } else {
                        showUnknownDeviceDialog();
                    }
                }

                if (cameraDevice != null) {
                    if (cameraDevice.supportV6Flag != 0) {
                        showCameraGatewayDialog(cameraDevice.deviceId, cameraDevice.type);
                        if (gwDevice == null) {//一物一码前的摄像机网关
                            etGatewayUsername.setText(cameraDevice.deviceId.substring(cameraDevice.deviceId.length() - 12, cameraDevice.deviceId.length()).toUpperCase());
                        } else {
                            if (isQrScan) {//一物一码摄像机网关
                                etGatewayUsername.setText(gwDevice.uniqueDeviceId);
                            }
                        }
                    } else {
                        showUnknownDeviceDialog();
                    }
                } else if (gwDevice != null) {//普通网关
                    if (gwDevice.supportV6Flag != 0) {
                        if (isQrScan) {
                            etGatewayUsername.setText(gwDevice.deviceId);
                        }
                        showCommonGatewayDialog(gwDevice.deviceId, gwDevice.type);
                    } else {
                        showUnknownDeviceDialog();
                    }
                }
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(msg);
            }
        });
    }


    /**
     * 摄像机网关
     *
     * @param cameraId
     * @param cameraType
     */
    private void showCameraGatewayDialog(final String cameraId, final String cameraType) {
        bindGatewayDialog = DialogUtil.showCommonDialog(this,
                this.getResources().getString(R.string.Hint), this.getResources().getString(R.string.Camera_GateWay_Tip), getString(R.string.Sure), getString(R.string.Cancel),
                new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View var1, String msg) {
                        Intent it = new Intent(GatewayBindActivity.this, DeviceStartConfigActivity.class);
                        it.putExtra("deviceType", cameraType);
                        it.putExtra("deviceId", cameraId);
                        it.putExtra("scanType", ConstantsUtil.BIND_CAMERA_GATEWAY_ENTRY);
                        if (TextUtils.equals("CMICA4", cameraType)) {
                            it.putExtra("isBindDevice", false);
                        } else {
                            it.putExtra("isBindDevice", true);
                        }
                        startActivity(it);
                        bindGatewayDialog.dismiss();
                    }

                    @Override
                    public void onClickNegative(View var1) {
                        bindGatewayDialog.dismiss();
                    }
                });
        bindGatewayDialog.show();
    }

    /**
     * 普通网关
     *
     * @param gwId
     * @param gwType
     */
    private void showCommonGatewayDialog(String gwId, String gwType) {
        if (TextUtils.equals(gwType, "GW04")) {//mini 网关
            showMiniTipsDialog(gwId, getString(R.string.Minigateway_Scancode_Popup),
                    getString(R.string.Minigateway_Popup_Goconfiguration), getString(R.string.Minigateway_Popup_Alreadyconfigured));
        } else if (TextUtils.equals(gwType, "GW08")) {//墙面网关
            showWallGwDialog(gwId, getString(R.string.Gateway_Dialog_Chose_Tip),
                    getString(R.string.Minigateway_Popup_Goconfiguration), getString(R.string.Minigateway_Popup_Alreadyconfigured));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            qrScan = true;
            tvResetTip.setVisibility(View.VISIBLE);
            if (UrlUtil.isWechatAlbumUrl(scanResult)) {
                etGatewayUsername.setText(UrlUtil.getShortUrl(scanResult.split(UrlUtil.URL_PREFIX)[1]).toUpperCase());
                tvResetTip.setVisibility(View.INVISIBLE);
                return;
            }
            doCheckV6Support(scanResult, true);

        }
    }


    /**
     * 未知设备dialog
     */
    private void showUnknownDeviceDialog() {
        unknownGatewayDialog = DialogUtil.showUnknownDeviceTips(this, new WLDialog.MessageListener() {
            @Override
            public void onClickPositive(View var1, String msg) {
                unknownGatewayDialog.dismiss();
            }

            @Override
            public void onClickNegative(View var1) {

            }
        }, getString(R.string.Scancode_Unrecognized));
        unknownGatewayDialog.show();
    }


    /**
     * mini网关扫描提示dialog
     *
     * @param deviceId
     * @param msg
     * @param posMsg
     * @param negMSg
     */
    private void showMiniTipsDialog(final String deviceId, String msg, String posMsg, String negMSg) {
        miniDialog = DialogUtil.showConfigOrBindDialog(this, msg, posMsg, negMSg, new WLDialog.MessageListener() {
            @Override
            public void onClickPositive(View var1, String msg) {
                startActivity(new Intent(GatewayBindActivity.this, MiniGatewayActivity.class)
                        .putExtra("deviceId", deviceId)
                        .putExtra("scanEntry", ConstantsUtil.MINI_GATEWAY_LIST_ENTRY));
                miniDialog.dismiss();
            }

            @Override
            public void onClickNegative(View var1) {
                miniDialog.dismiss();
            }
        });
        miniDialog.show();
    }

    /**
     * 墙面网关扫描提示dialog
     *
     * @param deviceId
     * @param msg
     * @param posMsg
     * @param negMSg
     */
    private void showWallGwDialog(final String deviceId, String msg, String posMsg, String negMSg) {
        miniDialog = DialogUtil.showConfigOrBindDialog(this, msg, posMsg, negMSg, new WLDialog.MessageListener() {
            @Override
            public void onClickPositive(View var1, String msg) {
                startActivity(new Intent(GatewayBindActivity.this, WallGatewayActivity.class)
                        .putExtra("deviceId", deviceId)
                        .putExtra("scanEntry", ConstantsUtil.MINI_GATEWAY_LIST_ENTRY));
                miniDialog.dismiss();
            }

            @Override
            public void onClickNegative(View var1) {
                miniDialog.dismiss();
            }
        });
        miniDialog.show();
    }

    private void bindDevice(final String id, final String password, String gwType) {
        if (StringUtil.isNullOrEmpty(id)) {
            ToastUtil.show(R.string.Login_Require_Username);
            return;
        }
        if (StringUtil.isNullOrEmpty(password)) {
            ToastUtil.show(R.string.GatewayChangePwd_NewPwd_Hint);
            return;
        }
        progressDialogManager.showDialog(BIND_GATEWAY, this, null, null, getResources().getInteger(R.integer.http_timeout));
        deviceApiUnit.doBindDevice(id, password, gwType, new DeviceApiUnit.DeviceApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                if (TextUtils.equals(id, VIRTUAL_GW_ID)) {
                    ToastUtil.show(R.string.Experience_Gateway_05);
                }
                getAllGateway();
                if (isMiniGateway) {
                    preference.saveGatewayPassword(id, password);
                    progressDialogManager.dimissDialog(BIND_GATEWAY, 0);
                    Intent intent = new Intent(context, GatewayListActivity.class);
                    context.startActivity(intent);
                    finish();
                } else {
                    preference.saveGatewayPassword(id, password);
                    progressDialogManager.dimissDialog(BIND_GATEWAY, 0);
                    Intent intent = new Intent();
                    intent.putExtra("gwId", id);
                    setResult(RESULT_OK, intent);
                    finish();
                }

            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(BIND_GATEWAY, 0);
                if (code == 20128) {
                    ToastUtil.show(R.string.Experience_Gateway_04);
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    private void getAllGateway() {
        deviceApiUnit.getWifiDeviceList("1", "10", new DeviceApiUnit.DeviceApiCommonListener<DeviceListBeanAll>() {
            @Override
            public void onSuccess(DeviceListBeanAll bean) {
                parserAllDeviceList(bean);
                int allCount = Integer.parseInt(bean.totalCount);
                int size = Integer.parseInt("10");
                //总共需要分页的次数
                final int times = allCount % size == 0 ? allCount / size : allCount / size + 1;
                if (times > 1) {
                    //需要开启的线程总数
                    totalCount = times - 1;
                    createThreadPoolTask(totalCount);
                }
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(msg);
            }
        });
    }

    private void createThreadPoolTask(int times) {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
        for (int i = 0; i < times; i++) {
            final int page = i + 2;//第二页开始
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "开启子线程：" + (page - 1));
                    try {
                        deviceApiUnit.getWifiDeviceList(String.valueOf(page), "10", new DeviceApiUnit.DeviceApiCommonListener<DeviceListBeanAll>() {
                            @Override
                            public void onSuccess(DeviceListBeanAll bean) {
                                --totalCount;
                                Log.i(TAG, "获取第" + bean.currentPage + "页数据，" + "剩余线程数：" + totalCount);
                                parserAllDeviceList(bean);
                                if (totalCount == 0) {
                                    Log.i(TAG, "totalCount = " + totalCount);
                                }
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                --totalCount;
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        fixedThreadPool.shutdown();
    }

    private void parserAllDeviceList(DeviceListBeanAll bean) {
        for (DeviceBean deviceBean : bean.devices) {
            if (deviceBean.isGateway()) {
                deviceBean.setName((DeviceInfoDictionary.getNameByTypeAndName(deviceBean.getType(), deviceBean.getName())));
                bindAllGateway.add(deviceBean);
                gatewayManager.saveGatewayInfo(deviceBean);
            }
            if (deviceBean.deviceId.equalsIgnoreCase(etGatewayUsername.getText().toString().toUpperCase())) {
                switchGateway(deviceBean, etGatewayPassword.getText().toString().trim(), true);
                break;
            }

        }
        // 保存 绑定 的 网关的 数量
        if (bindAllGateway.isEmpty()) {
            Preference.getPreferences().saveBindGatewayCount(0);
        } else {
            Preference.getPreferences().saveBindGatewayCount(bindAllGateway.size());
        }
        List<String> gatewayIdList = new ArrayList<>();
        for (DeviceBean deviceBean : bindAllGateway) {
            gatewayIdList.add(deviceBean.deviceId);
        }
        Preference.getPreferences().saveCurrentGatewayList(gatewayIdList);
        Log.i("huxctest", "gatewayIdList.size " + gatewayIdList.size());
    }

    //切换网关。只有在云账号登录下才有这个功能。
    private void switchGateway(DeviceBean bean, String password, boolean isFinish) {
        //清空当前网关信息
        MainApplication.getApplication().clearCurrentGateway();
        //保存网关密码
        preference.saveGatewayPassword(bean.deviceId, password);
        preference.saveCurrentGatewayID(bean.deviceId);
        deviceApiUnit.doSwitchGatewayId(bean.deviceId);
        saveCurrentGatewayInfo(bean);
        preference.saveCurrentGatewayState(bean.state);
        preference.saveGatewayRelationFlag(bean.relationFlag);
        EventBus.getDefault().post(new GatewayStateChangedEvent(null));
        MainApplication.getApplication().getDeviceCache().loadDatabaseCache(bean.deviceId, bean.state);//加载设备列表缓存
        EventBus.getDefault().post(new DeviceReportEvent(null));
        requestAllInfo(bean.deviceId);
        if ("1".equals(bean.getState())
                && !TextUtils.isEmpty(password)
                && (RegularTool.isNeedResetPwd(password) || TextUtils.equals(password, bean.deviceId.substring(bean.deviceId.length() - 6).toUpperCase()))) {
            if (!changePwd) {
                Log.i("hxc", "跳转到修改密码界面 ");
                Intent intent = new Intent(context, ConfirmGatewayPasswordActivity.class);
                intent.putExtra("oldPwd", preference.getGatewayPassword(bean.deviceId));
                startActivity(intent);
            }
        }
    }

    //根据http接口先存储网关的基本信息，后面mqtt接口的信息刷新为详细
    private void saveCurrentGatewayInfo(DeviceBean bean) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("gwID", bean.deviceId);
        jsonObject.put("gwVer", bean.version);
        jsonObject.put("gwName", bean.getName());
        jsonObject.put("gwType", bean.getType());
        jsonObject.put("hostFlag", bean.getHostFlag());
        preference.saveCurrentGatewayInfo(jsonObject.toJSONString());
    }

    private void requestAllInfo(String gatewayId) {
        getPushType(gatewayId);
        MQTTManager mqttManager = MainApplication.getApplication().getMqttManager();
        LocalInfo localInfo = MainApplication.getApplication().getLocalInfo();
        mqttManager.publishEncryptedMessage(
                MQTTCmdHelper.createGetAllDevices(gatewayId, localInfo.appID),
                mqttManager.MODE_CLOUD_ONLY);

        mqttManager.publishEncryptedMessage(
                MQTTCmdHelper.createGetAllRooms(gatewayId),
                MQTTManager.MODE_CLOUD_ONLY);

        mqttManager.publishEncryptedMessage(
                MQTTCmdHelper.createGetAllScenes(gatewayId),
                MQTTManager.MODE_CLOUD_ONLY);

        mqttManager.publishEncryptedMessage(
                MQTTCmdHelper.createGatewayInfo(gatewayId, 0, localInfo.appID, null),
                MQTTManager.MODE_CLOUD_ONLY);
    }

    private void getPushType(String gwId) {
        deviceApiUnit.doGetIsPush(gwId, new DeviceApiUnit.DeviceApiCommonListener<DeviceIsPushBean>() {
            @Override
            public void onSuccess(DeviceIsPushBean bean) {
                if (StringUtil.equals(bean.isPush, "0")) {
                    preference.saveAlarmPush(false);
                } else if (StringUtil.equals(bean.isPush, "1")) {
                    preference.saveAlarmPush(true);
                }
            }

            @Override
            public void onFail(int code, String msg) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_gateway_login:
                bindDevice(etGatewayUsername.getText().toString().toUpperCase(), etGatewayPassword.getText().toString(), null);
                break;
            case R.id.img_left:
                onBackPressed();
                break;
            case R.id.iv_gateway_scan:
                //打开扫描界面扫描条形码或二维码
                Intent openCameraIntent = new Intent(GatewayBindActivity.this, CaptureActivity.class);
                startActivityForResult(openCameraIntent, 0);
                break;
            case R.id.btn_auto:
                String gwID = etGatewayUsername.getText().toString().trim();
                if (gwID.length() >= 6) {
                    etGatewayPassword.setText(gwID.substring(gwID.length() - 6));
                }
                break;

            case R.id.tv_virtual_gw:
                bindDevice(VIRTUAL_GW_ID, "Wulian123", "GW99");
                break;
            case R.id.img_right:
                DialogUtil.showGatewayIntroductionDialog(this);
                break;
            default:
                break;
        }
    }


}
