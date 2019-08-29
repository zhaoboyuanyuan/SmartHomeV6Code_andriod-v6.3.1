package cc.wulian.smarthomev6.main.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.config.DeviceStartConfigActivity;
import cc.wulian.smarthomev6.main.device.gateway_mini.config.MiniGatewayActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTApiConfig;
import cc.wulian.smarthomev6.support.core.socket.GatewayBean;
import cc.wulian.smarthomev6.support.core.socket.GatewaySearchUnit;
import cc.wulian.smarthomev6.support.event.MQTTRegisterEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.RegularTool;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.tools.skin.ButtonSkinWrapper;
import cc.wulian.smarthomev6.support.tools.zxing.activity.CaptureActivity;
import cc.wulian.smarthomev6.support.utils.CameraUtil;
import cc.wulian.smarthomev6.support.utils.ConstantsUtil;
import cc.wulian.smarthomev6.support.utils.DialogUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.UrlUtil;

/**
 * Created by syf on 2017/2/20.
 * 网关登录界面
 */
public class GatewayLoginActivity extends BaseTitleActivity implements View.OnClickListener {

    private Context context;
    private EditText etGatewayUsername;
    private EditText etGatewayPassword;
    private Button btnGatewayLogin;
    private ImageView imageScan;

    private TextWatcher username_watcher;
    private TextWatcher password_watcher;

    private GatewayBean gatewayBean;
    private GatewaySearchPop gatewaySearchPop;
    private String defaultGatewayID;
    private WLDialog bindGatewayDialog;
    private WLDialog miniDialog;
    private String gwID;
    private String gwPassword;
    private View btn_auto;

    private ButtonSkinWrapper buttonSkinWrapper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_gateway_login, true);
    }

    @Override
    protected void onDestroy() {
        if (gatewaySearchPop != null && gatewaySearchPop.isShowing()) {
            gatewaySearchPop.dismiss();
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Login_GateWay));
//        setToolBarTitleAndRightBtn(getString(R.string.GatewayLoginActivity_Title), getString(R.string.GatewayLoginActivity_Title_Right));
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
//                etGatewayPassword.setText(bean.gwID.substring(bean.gwID.length() - 6));
//                etGatewayPassword.setSelection(etGatewayPassword.getText().length());
            }
        });
        etGatewayUsername = (EditText) findViewById(R.id.et_gateway_username);
        etGatewayPassword = (EditText) findViewById(R.id.et_gateway_password);
        etGatewayPassword.setTypeface(Typeface.DEFAULT);
        etGatewayPassword.setTransformationMethod(new PasswordTransformationMethod());
        btnGatewayLogin = (Button) findViewById(R.id.btn_gateway_login);
        btnGatewayLogin.setText(R.string.Login_GateWay);
        imageScan = (ImageView) findViewById(R.id.iv_gateway_scan);
        btn_auto = findViewById(R.id.btn_auto);

        defaultGatewayID = getIntent().getStringExtra("gwID");
        if (defaultGatewayID != null) {
            etGatewayUsername.setText(defaultGatewayID);
            etGatewayPassword.requestFocus();
        }
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
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

    private void initWatcher() {
        username_watcher = new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateButtonState();
                judgeCameraGateway();
                judgeMiniGateway();
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
        gwID = etGatewayUsername.getText().toString();
        if (gwID.length() == 16 || gwID.length() == 20 || (gwID.startsWith("CG") && gwID.length() == 11)) {
            if (gwID.length() == 16) {
                gwID = "cmic" + gwID;
            }
            if (CameraUtil.isCameraGateway(gwID)) {
                final String finalScanResult = gwID;
                if (gwID.startsWith("cmic")) {
                    gwID = gwID.substring(8, gwID.length());
                    etGatewayUsername.setText(gwID.toUpperCase());
                }
                bindGatewayDialog = DialogUtil.showCommonDialog(this,
                        this.getResources().getString(R.string.Hint), this.getResources().getString(R.string.Camera_GateWay_Tip), getString(R.string.Sure), getString(R.string.Cancel),
                        new WLDialog.MessageListener() {
                            @Override
                            public void onClickPositive(View var1, String msg) {
                                Intent it = new Intent(GatewayLoginActivity.this, DeviceStartConfigActivity.class);
                                it.putExtra("deviceType", CameraUtil.getTypeByDeviceId(finalScanResult));
                                it.putExtra("deviceId", finalScanResult);
                                it.putExtra("scanType", ConstantsUtil.GATEWAY_LOGIN_ENTRY);
                                it.putExtra("isBindDevice", false);
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
        }
    }

    /**
     * 判断是否是mini网关
     */
    private void judgeMiniGateway() {
        gwID = etGatewayUsername.getText().toString();
        if (gwID.startsWith("GWMN02") && gwID.length() == 18) {
            gwID = gwID.substring(6, 18);
            etGatewayUsername.setText(gwID);
            showMiniTipsDialog(gwID, getString(R.string.Minigateway_Scancode_Popup),
                    getString(R.string.Minigateway_Popup_Goconfiguration), getString(R.string.Minigateway_Popup_Alreadyconfigured));
        } else if (gwID.startsWith("ME07") && gwID.length() == 11) {
            showMiniTipsDialog(gwID, getString(R.string.Minigateway_Scancode_Popup),
                    getString(R.string.Minigateway_Popup_Goconfiguration), getString(R.string.Minigateway_Popup_Alreadyconfigured));
        }
    }

    private void showGatewaySearchDialog() {
        new GatewaySearchDialog(this, new GatewaySearchDialog.OnGatewaySelectedListener() {
            @Override
            public void onGatewaySelected(GatewayBean bean) {
                gatewayBean = bean;
                etGatewayUsername.setText(bean.gwID);
                etGatewayUsername.setSelection(etGatewayUsername.getText().length());
                etGatewayPassword.setText(bean.gwID.substring(bean.gwID.length() - 6));
                etGatewayPassword.setSelection(etGatewayPassword.getText().length());
            }
        }).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_gateway_login:
                login();
                break;
            case R.id.img_left:
                onBackPressed();
                break;
            case R.id.btn_right:
                showGatewaySearchDialog();
                break;
            case R.id.iv_gateway_scan:
                //打开扫描界面扫描条形码或二维码
                Intent openCameraIntent = new Intent(GatewayLoginActivity.this, CaptureActivity.class);
                startActivityForResult(openCameraIntent, 0);
                break;
            case R.id.btn_auto:
                String gwID = etGatewayUsername.getText().toString().trim();
                if (gwID.length() >= 6) {
                    etGatewayPassword.setText(gwID.substring(gwID.length() - 6));
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            scanResult = getRealGatewayId(scanResult);
            if (RegularTool.isLegalGatewayID(scanResult)) {
                etGatewayUsername.setText(scanResult);
            } else if (scanResult.length() == 16 || scanResult.length() == 20 || scanResult.startsWith("CG")) {
                etGatewayUsername.setText(scanResult);
            } else if (scanResult.startsWith("GWMN02") || scanResult.startsWith("ME07")) {
                if (scanResult.startsWith("ME07")) {
                    etGatewayUsername.setText(scanResult);
                } else {
                    etGatewayUsername.setText(scanResult.substring(scanResult.length() - 12, scanResult.length()));
                }
                showMiniTipsDialog(scanResult, getString(R.string.Minigateway_Scancode_Popup),
                        getString(R.string.Minigateway_Popup_Goconfiguration), getString(R.string.Minigateway_Popup_Alreadyconfigured));
            } else if (UrlUtil.isWechatAlbumUrl(scanResult)) {
                etGatewayUsername.setText(UrlUtil.getShortUrl(scanResult.split(UrlUtil.URL_PREFIX)[1]).toUpperCase());
            } else {
                ToastUtil.show(R.string.GatewayLoginActivity_Toast_Gateway_Error_QRCode);
            }
        }
    }

    private void login() {
        progressDialogManager.showDialog("SearchGateway", context, null, null, getResources().getInteger(R.integer.http_timeout));
        gwID = etGatewayUsername.getText().toString().trim();
        gwPassword = etGatewayPassword.getText().toString().trim();
        if ((gwID.startsWith("CG") || gwID.startsWith("ME")) && gwID.length() >= 11) {
            gwID = gwID.substring(0, 11);
        }
        if (gatewayBean != null && TextUtils.equals(gatewayBean.gwID, gwID)) {
            MQTTApiConfig.GW_SERVER_URL = gatewayBean.host + ":" + MQTTApiConfig.GW_SERVER_PORT;
            preference.saveCurrentGatewayHost(MQTTApiConfig.GW_SERVER_URL);
            MQTTApiConfig.gwID = gwID;
            MQTTApiConfig.gwPassword = gwPassword;
            MQTTApiConfig.gwUserName = "a" + System.currentTimeMillis();
            MQTTApiConfig.gwUserPassword = "b";//etGatewayPassword.getText().toString().trim();
            ((MainApplication) getApplication()).getMqttManager().connectGateway();
        } else {
            gatewayBean = null;
            new GatewaySearchUnit().startSearch(new GatewaySearchUnit.SearchGatewayCallback() {
                @Override
                public void result(List<GatewayBean> list) {
                    for (GatewayBean bean : list) {
                        if (TextUtils.equals(gwID, bean.gwID)) {
                            gatewayBean = bean;
                            break;
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (gatewayBean != null) {
                                MQTTApiConfig.GW_SERVER_URL = gatewayBean.host + ":" + MQTTApiConfig.GW_SERVER_PORT;
                                preference.saveCurrentGatewayHost(MQTTApiConfig.GW_SERVER_URL);
                                MQTTApiConfig.gwID = gatewayBean.gwID;
                                MQTTApiConfig.gwPassword = gwPassword;
                                MQTTApiConfig.gwUserName = "a" + System.currentTimeMillis();
                                MQTTApiConfig.gwUserPassword = "b";//etGatewayPassword.getText().toString().trim();
                                ((MainApplication) getApplication()).getMqttManager().connectGateway();
                            } else {
                                ProgressDialogManager.getDialogManager().dimissDialog("SearchGateway", 0);
                                ToastUtil.show(R.string.GatewayLoginActivity_Toast_NoResultInSearchGateway);
                            }
                        }
                    });
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MQTTRegisterEvent event) {
        ProgressDialogManager.getDialogManager().dimissDialog("SearchGateway", 0);
        if (event.state == MQTTRegisterEvent.STATE_REGISTER_SUCCESS) {
            preference.saveUserEnterType(Preference.ENTER_TYPE_GW);
            preference.saveCurrentGatewayID(MQTTApiConfig.gwID);
            preference.saveGatewayPassword(MQTTApiConfig.gwID, MQTTApiConfig.gwPassword);
            new DeviceApiUnit(context).doSwitchGatewayId(MQTTApiConfig.gwID);
            preference.saveCurrentGatewayState("1");
            preference.saveIsLogin(true);
            preference.saveGatewayRelationFlag("1");
            preference.saveThirdPartyLogin(false);
            preference.saveAutoLogin(false);
//            if (RegularTool.isNeedResetPwd(MQTTApiConfig.gwPassword)) {
//                ConfirmGatewayPasswordActivity.start(GatewayLoginActivity.this, MQTTApiConfig.gwPassword);
//            }
            setResult(RESULT_OK);
            finish();
        } else {
            String msg = event.msg;
            if (!TextUtils.isEmpty(msg)) {
                ToastUtil.show(msg);
                ((MainApplication) getApplication()).getMqttManager().disconnectGateway();
            }
        }
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
                startActivity(new Intent(GatewayLoginActivity.this, MiniGatewayActivity.class)
                        .putExtra("deviceId", deviceId)
                        .putExtra("isGatewayLogin", true)
                        .putExtra("scanEntry", ConstantsUtil.MINI_GATEWAY_LOGIN_ENTRY));
                miniDialog.dismiss();
            }

            @Override
            public void onClickNegative(View var1) {
                miniDialog.dismiss();
            }
        });
        miniDialog.show();

    }

    private String getRealGatewayId(String scanResult) {
        if (scanResult.startsWith("CG") || scanResult.startsWith("ME")) {
            scanResult = scanResult.substring(0, 11);
        } else {
            return scanResult;
        }
        return scanResult;
    }

}
