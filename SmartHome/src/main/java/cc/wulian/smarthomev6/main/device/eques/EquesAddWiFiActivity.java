package cc.wulian.smarthomev6.main.device.eques;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.SpannableBean;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.config.WifiListDialog;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.DialogUtil;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * 作者: chao
 * 时间: 2017/6/5
 * 描述: 选择wifi页面
 * 联系方式: 805901025@qq.com
 */

public class EquesAddWiFiActivity extends BaseTitleActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{
    private TextView tvConfigWifiTips;
    private TextView tvWifiName;
    private TextView tvWifiChange;
    private EditText etWifiPwd;
    private Button btnNextStep;
    private CheckBox cbNoWiFiPwd;
    private CheckBox cbPwdShow;
    private RelativeLayout rlWifiPwdInput;
    private WifiManager wifiManager;
    private ScanResult scanResult;

    private WLDialog.Builder builder;
    private WLDialog dialog;
    private WLDialog tipDialog;

    private static final String PERMISSION_ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String PERMISSION_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_eques_wifi, true);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Config_WiFi));
    }

    @Override
    protected void initView() {
        super.initView();
        tvConfigWifiTips = (TextView) findViewById(R.id.tv_config_wifi_tips);
        tvConfigWifiTips.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        btnNextStep = (Button) findViewById(R.id.btn_next_step);
        cbNoWiFiPwd = (CheckBox) findViewById(R.id.no_wifi_pwd_checkbox);
        rlWifiPwdInput = (RelativeLayout) findViewById(R.id.rl_wifi_pwd_input);
        tvWifiName = (TextView) findViewById(R.id.tv_wifi_name);
        etWifiPwd = (EditText) findViewById(R.id.et_wifi_pwd);
        cbPwdShow = (CheckBox) findViewById(R.id.cb_wifi_pwd_show);
        tvWifiChange = (TextView) findViewById(R.id.tv_wifi_change);
        tvWifiChange.setText(getClickableSpan());
        etWifiPwd.setTypeface(Typeface.DEFAULT);
        etWifiPwd.setTransformationMethod(new PasswordTransformationMethod());
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
    protected void initData() {
        super.initData();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if ((activeNetInfo != null && !(activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI))) {
            showWifiHintDialog();
        }
    }

    @Override
    protected void onResume() {
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


    @Override
    protected void initListeners() {
        super.initListeners();
        btnNextStep.setOnClickListener(this);
        tvConfigWifiTips.setOnClickListener(this);
        cbNoWiFiPwd.setOnCheckedChangeListener(this);
        tvWifiName.setOnClickListener(this);
        cbPwdShow.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.tv_config_wifi_tips) {
            DialogUtil.showCommonInstructionsWebViewTipDialog(
                    this,
                    getResources().getString(R.string.Cateyemini_Adddevice_Quicklyconnect),
                    "fastconn");
        } else if (id == R.id.btn_next_step) {
            if (scanResult != null) {
                String wifiName = scanResult.SSID;
                Intent intent = new Intent(this, EquesAddQRActivity.class);
                intent.putExtra("wifiSsid", wifiName);
                intent.putExtra("wifiPwd", etWifiPwd.getText().toString());
                if (etWifiPwd.getText().length() < 8 && !cbNoWiFiPwd.isChecked()) {
                    ToastUtil.show(getString(R.string.WiFi_Password_Eight));
                } else {
                    startActivity(intent);
                }
            }else{
                WLog.i(TAG, "onClick: scanResult = null");
                tipDialog = DialogUtil.showCommonDialog(this, "",
                        "获取无线网络失败，请先打开位置服务", "去设置", getString(R.string.Cancel), new WLDialog.MessageListener() {
                            @Override
                            public void onClickPositive(View var1, String msg) {
                                Intent intent = new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(intent, 0);
                                tipDialog.dismiss();
                            }

                            @Override
                            public void onClickNegative(View var1) {
                                tipDialog.dismiss();
                            }
                        });
                tipDialog.show();
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


    private SpannableString getClickableSpan() {

        //监听器
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show("Click Success");
            }
        };

        SpannableString spanableInfo = new SpannableString(tvWifiChange.getText());

        //可以为多部分设置超链接
        spanableInfo.setSpan(new ForegroundColorSpan(this.getResources().getColor(R.color.v6_green)), tvWifiChange.length() - 2, tvWifiChange.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //设置背景色为青色
        return spanableInfo;
    }

    private void showWifiHintDialog(){
        builder = new WLDialog.Builder(this);
        builder.setCancelOnTouchOutSide(false)
                .setDismissAfterDone(false)
                .setCancelable(false)
                .setMessage(getString(R.string.Open_WIFI))
                .setPositiveButton(this.getResources().getString(R.string.Sure))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view, String msg) {
                        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        startActivity(intent);
                        dialog.dismiss();
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
}