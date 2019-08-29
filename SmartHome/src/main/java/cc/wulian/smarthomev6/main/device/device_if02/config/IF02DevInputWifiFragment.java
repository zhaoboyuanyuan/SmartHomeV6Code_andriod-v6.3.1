package cc.wulian.smarthomev6.main.device.device_if02.config;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hismart.easylink.localjni.EasylinkUtil;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.main.device.config.WifiListDialog;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.ToastUtil;


public class IF02DevInputWifiFragment extends WLFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Context context;
    private TextView tvWifiName;
    private EditText etWifiPwd;
    private Button btnNextStep;
    private CheckBox cbNoWiFiPwd;
    private ImageView ivDeleteText;
    private RelativeLayout rlWifiConfig;
    private CheckBox cbPwdShow;
    private RelativeLayout rlWifiPwdInput;
    private WifiManager wifiManager;
    private EasylinkUtil easylinkUtil;
    private ScanResult scanResult;
    private IF02InfoBean configData;
    private FragmentManager manager;
    private FragmentTransaction ft;
    private IF02DeviceCheckBindFragment checkBindFragment;

    private static final String PERMISSION_ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String PERMISSION_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int PERMISSION_REQUEST_CODE = 1;

    public IF02DevInputWifiFragment() {
    }

    public static IF02DevInputWifiFragment newInstance(IF02InfoBean configData) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("configData", configData);
        IF02DevInputWifiFragment inputWifiFragment = new IF02DevInputWifiFragment();
        inputWifiFragment.setArguments(bundle);
        return inputWifiFragment;
    }

    @Override
    public int layoutResID() {
        return R.layout.fragment_if01_dev_input_wifi;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        manager = getFragmentManager();
        Bundle bundle = getArguments();
        configData = (IF02InfoBean) bundle.getSerializable("configData");

    }

    @Override
    public void initView(View v) {
        mTvTitle.setText(getString(R.string.IF_009));
        btnNextStep = (Button) v.findViewById(R.id.btn_next_step);
        cbNoWiFiPwd = (CheckBox) v.findViewById(R.id.no_wifi_pwd_checkbox);
        rlWifiPwdInput = (RelativeLayout) v.findViewById(R.id.rl_wifi_pwd_input);
        tvWifiName = (TextView) v.findViewById(R.id.tv_wifi_name);
        etWifiPwd = (EditText) v.findViewById(R.id.et_wifi_pwd);
        etWifiPwd.setTypeface(Typeface.DEFAULT);
        etWifiPwd.setTransformationMethod(new PasswordTransformationMethod());
        cbPwdShow = (CheckBox) v.findViewById(R.id.cb_wifi_pwd_show);
        ivDeleteText = (ImageView) v.findViewById(R.id.ivDeleteText);

        rlWifiConfig = (RelativeLayout) v.findViewById(R.id.rl_device_wifi_config);
    }


    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(btnNextStep, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(btnNextStep, SkinResouceKey.COLOR_BUTTON_TEXT);
    }

    @Override
    protected void initTitle(View v) {
        super.initTitle(v);
        setLeftImg(R.drawable.icon_back);
    }

    @Override
    public void initListener() {
        super.initListener();
        rlWifiConfig.setOnClickListener(this);
        btnNextStep.setOnClickListener(this);
        ivDeleteText.setOnClickListener(this);
        cbNoWiFiPwd.setOnCheckedChangeListener(this);
        cbPwdShow.setOnCheckedChangeListener(this);
    }

    @Override
    protected void initData() {
        super.initData();

        etWifiPwd.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    ivDeleteText.setVisibility(View.GONE);
                } else {
                    ivDeleteText.setVisibility(View.VISIBLE);
                }
            }
        });
        switch (configData.getDeviceType()) {
            case "IF02":
                mTvTitle.setText(getString(R.string.IF_009));
                break;
            case "GW14":
                mTvTitle.setText(getString(R.string.Config_WiFi));
                break;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                String wifiName = wifiInfo.getSSID().replace("\"", "");
                tvWifiName.setText(wifiName);
                etWifiPwd.setText(preference.getWifiPassword(wifiName));
                getScanResult();
            }
        }
    }

    private void getScanResult() {
        if (ContextCompat.checkSelfPermission(context, PERMISSION_ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, PERMISSION_ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{PERMISSION_ACCESS_COARSE_LOCATION, PERMISSION_ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
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
        WifiListDialog dialog = new WifiListDialog(context, new WifiListDialog.OnWifiSelectedListener() {
            @Override
            public void onGatewaySelected(ScanResult bean) {
                scanResult = bean;
                tvWifiName.setText(bean.SSID);
                etWifiPwd.setText(preference.getWifiPassword(bean.SSID));
            }
        });
        dialog.show();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_next_step) {
            if (etWifiPwd.getText().length() < 8 && !cbNoWiFiPwd.isChecked()) {
                ToastUtil.show(getString(R.string.WiFi_Password_Eight));
            } else {
                String wifiName = tvWifiName.getText().toString();
                String wifiPassword = etWifiPwd.getText().toString();
                configData.setWifiName(wifiName);
                configData.setWifiPassword(wifiPassword);
                preference.setWifiNameAndPassword(wifiName, wifiPassword);
                ft = manager.beginTransaction();
                checkBindFragment = IF02DeviceCheckBindFragment.newInstance(configData);
                ft.replace(android.R.id.content, checkBindFragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        } else if (id == R.id.rl_device_wifi_config) {
//            showDialog();
            startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
        } else if (id == R.id.ivDeleteText) {
            etWifiPwd.setText("");
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
