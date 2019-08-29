package cc.wulian.smarthomev6.main.mine.platform.whiterobot;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.ConfigWiFiInfoModel;
import cc.wulian.smarthomev6.entity.SpannableBean;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.config.DeviceInputWifiFragment;
import cc.wulian.smarthomev6.main.device.config.WifiListDialog;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.DialogUtil;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by zbl on 2017/12/19.
 * 小白机器人，wifi配置界面
 */

public class WhiteRobotWifiActivity extends BaseTitleActivity implements CompoundButton.OnCheckedChangeListener {

    private TextView tvWifiName;
    private TextView tvWifiChange;
    private EditText etWifiPwd;
    private Button btnNextStep;
    private CheckBox cbNoWiFiPwd;
    private CheckBox cbPwdShow;
    private RelativeLayout rlWifiPwdInput;
    private WifiManager wifiManager;
    private ScanResult scanResult;
    private WLDialog dialog;

    private String code;

    private static final String PERMISSION_ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String PERMISSION_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whiterobot_input_wifi, true);
    }

    @Override
    protected void initTitle() {
        setToolBarTitle(getString(R.string.Config_WiFi));
    }

    @Override
    protected void initView() {
        btnNextStep = (Button) findViewById(R.id.btn_next_step);
        cbNoWiFiPwd = (CheckBox) findViewById(R.id.no_wifi_pwd_checkbox);
        rlWifiPwdInput = (RelativeLayout) findViewById(R.id.rl_wifi_pwd_input);
        tvWifiName = (TextView) findViewById(R.id.tv_wifi_name);
        etWifiPwd = (EditText) findViewById(R.id.et_wifi_pwd);
        etWifiPwd.setTypeface(Typeface.DEFAULT);
        etWifiPwd.setTransformationMethod(new PasswordTransformationMethod());
        cbPwdShow = (CheckBox) findViewById(R.id.cb_wifi_pwd_show);
        tvWifiChange = (TextView) findViewById(R.id.tv_wifi_change);
        StringUtil.addColorOrSizeorEvent(tvWifiChange, getResources().getString(R.string.Cateye_Connect_Tip2), new SpannableBean[]{new SpannableBean(getResources().getColor(R.color.v6_green), 14, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        })});
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(btnNextStep, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(btnNextStep, SkinResouceKey.COLOR_BUTTON_TEXT);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        tvWifiName.setOnClickListener(this);
        btnNextStep.setOnClickListener(this);
        cbNoWiFiPwd.setOnCheckedChangeListener(this);
        cbPwdShow.setOnCheckedChangeListener(this);
    }

    @Override
    protected void initData() {
        code = getIntent().getStringExtra("code");
    }

    public static DeviceInputWifiFragment newInstance(ConfigWiFiInfoModel configData) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("configData", configData);
        DeviceInputWifiFragment deviceInputWifiFragment = new DeviceInputWifiFragment();
        deviceInputWifiFragment.setArguments(bundle);
        return deviceInputWifiFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                tvWifiName.setText(wifiInfo.getSSID().replace("\"", ""));
                getScanResult();
            }
        }
    }

    private void getScanResult() {
        if (ContextCompat.checkSelfPermission(this, PERMISSION_ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, PERMISSION_ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{PERMISSION_ACCESS_COARSE_LOCATION, PERMISSION_ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        } else {
            consumeWifiScanResultList();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == 1) {
            boolean hasPermission = true;
            if (grantResults != null) {
                for (int grantResult : grantResults) {
                    hasPermission = hasPermission & (grantResult == PackageManager.PERMISSION_GRANTED);
                }
            }
            if (hasPermission) {
                consumeWifiScanResultList();
            } else {
                // Permission Denied
                ToastUtil.show(R.string.Toast_Permission_Denied);
            }
            return;
        }
    }

    public void consumeWifiScanResultList() {
        List<ScanResult> list = wifiManager.getScanResults();
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        for (ScanResult result : list) {
            if (result.BSSID.equals(wifiInfo.getBSSID())) {
                scanResult = result;
                break;
            }
        }
    }

    public void showDialog() {
        WifiListDialog dialog = new WifiListDialog(this, new WifiListDialog.OnWifiSelectedListener() {
            @Override
            public void onGatewaySelected(ScanResult bean) {
                scanResult = bean;
                tvWifiName.setText(bean.SSID);
            }
        });
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.btn_next_step) {
            if (scanResult != null) {
                String wifiName = scanResult.SSID;
                String wifiPsw = etWifiPwd.getText().toString();
                if (wifiPsw.length() < 8 && !cbNoWiFiPwd.isChecked()) {
                    ToastUtil.show(getString(R.string.WiFi_Password_Eight));
                } else {
                    WhiteRobotQRCodeActivity.start(this, code, wifiName, wifiPsw);
                }
            } else {
                dialog = DialogUtil.showCommonDialog(this, "",
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

            }
        } else if (id == R.id.tv_wifi_name) {
            showDialog();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if (id == R.id.no_wifi_pwd_checkbox) {
            if (isChecked) {
                rlWifiPwdInput.setVisibility(View.GONE);
            } else {
                rlWifiPwdInput.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.cb_wifi_pwd_show) {
            if (isChecked) {
                etWifiPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                etWifiPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            CharSequence charsequence = etWifiPwd.getText();
            if (charsequence instanceof Spannable) {
                Spannable spanText = (Spannable) charsequence;
                Selection.setSelection(spanText, charsequence.length());
            }
        }
    }

}
