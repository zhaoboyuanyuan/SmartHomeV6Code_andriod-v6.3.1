package cc.wulian.smarthomev6.main.device.safeDog.config;

import android.content.Context;
import android.graphics.Typeface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * created by huxc  on 2018/1/23.
 * func：安全狗WiFi配置界面
 * email: hxc242313@qq.com
 */

public class SDConfigWifiFragment extends WLFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private Context context;
    private TextView tvWifiName;
    private EditText etWifiPwd;
    private Button btnNextStep;
    private CheckBox cbPwdShow;
    private RelativeLayout rlWifiPwdInput;
    private WifiManager wifiManager;
    private ScanResult scanResult;
    private String deviceId;
    private FragmentManager manager;
    private FragmentTransaction ft;
    private SDConnectingFragment connectingFragment;


    public static SDConfigWifiFragment newInstance(String deviceId) {
        Bundle bundle = new Bundle();
        bundle.putString("deviceId", deviceId);
        SDConfigWifiFragment configWifiFragment = new SDConfigWifiFragment();
        configWifiFragment.setArguments(bundle);
        return configWifiFragment;
    }

    @Override
    public int layoutResID() {
        return R.layout.fragment_safe_dog_input_wifi;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        manager = getFragmentManager();
        Bundle bundle = getArguments();
        deviceId = bundle.getString("deviceId");
    }

    @Override
    public void initView(View v) {
        mTvTitle.setText(getString(R.string.Config_WiFi));
        btnNextStep = (Button) v.findViewById(R.id.btn_next_step);
        rlWifiPwdInput = (RelativeLayout) v.findViewById(R.id.rl_wifi_pwd_input);
        tvWifiName = (TextView) v.findViewById(R.id.tv_wifi_name);
        etWifiPwd = (EditText) v.findViewById(R.id.et_wifi_pwd);
        etWifiPwd.setTypeface(Typeface.DEFAULT);
        etWifiPwd.setTransformationMethod(new PasswordTransformationMethod());
        cbPwdShow = (CheckBox) v.findViewById(R.id.cb_wifi_pwd_show);
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
        tvWifiName.setOnClickListener(this);
        btnNextStep.setOnClickListener(this);
        cbPwdShow.setOnCheckedChangeListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                tvWifiName.setText(wifiInfo.getSSID().replace("\"", ""));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next_step:
                if (TextUtils.isEmpty(etWifiPwd.getText())) {
                    ToastUtil.show(getString(R.string.Input_Wifi_Password));
                } else if (etWifiPwd.getText().length() < 8) {
                    ToastUtil.show(getString(R.string.WiFi_Password_Eight));
                } else {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    connectingFragment = SDConnectingFragment.newInstance(deviceId);
                    ft.replace(android.R.id.content, connectingFragment);
                    ft.commit();
                }

                break;
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
        }
    }
}