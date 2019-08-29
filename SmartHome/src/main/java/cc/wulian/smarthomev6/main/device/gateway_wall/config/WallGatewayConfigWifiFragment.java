package cc.wulian.smarthomev6.main.device.gateway_wall.config;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.main.device.gateway_mini.MiniGatewayWifiListDialog;
import cc.wulian.smarthomev6.support.core.apiunit.bean.WifiInfoBean;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.MiniMQTTManger;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;
import cc.wulian.smarthomev6.support.utils.WifiUtil;

/**
 * created by huxc  on 2017/8/21.
 * func：   墙面网关wifi配置界面
 * email: hxc242313@qq.com
 */

public class WallGatewayConfigWifiFragment extends WLFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String PERMISSION_ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String PERMISSION_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int PERMISSION_REQUEST_CODE = 1;

    private TextView tvWifiName;
    private EditText etWifiPassword;
    private EditText etWifiNameMini;
    private EditText etWifiPasswordMini;
    private CheckBox cbPassword;
    private CheckBox cbPasswordMini;
    private CheckBox cbSync;
    private Button btnNextStep;
    private WallGatewayConfigResultFragment configResultFragment;
    private WallGatewayGuideFragment guideFragment;

    private Context context;
    private String deviceId;
    private String scanEntry;
    private String wifiListData;
    private List<WifiInfoBean> wifiList;
    private WifiInfoBean wifiInfoBean;
    private WifiInfo wifiInfo;//mini网关自身热点
    private WifiManager wifiManager;
    private ScanResult scanResult;
    private int customFlag = 0;//是否同步 0:false,1:true
    private int type;//0局域网 1广域网

    public static WallGatewayConfigWifiFragment newInstance(String deviceId, String wifiListData, int type, String scanEntry) {
        Bundle bundle = new Bundle();
        bundle.putString("deviceId", deviceId);
        bundle.putString("scanEntry", scanEntry);
        bundle.putString("wifiListData", wifiListData);
        bundle.putInt("type", type);
        WallGatewayConfigWifiFragment configWifiFragment = new WallGatewayConfigWifiFragment();
        configWifiFragment.setArguments(bundle);
        return configWifiFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        Bundle bundle = getArguments();
        if (bundle != null) {
            deviceId = bundle.getString("deviceId");
            scanEntry = bundle.getString("scanEntry");
            wifiListData = bundle.getString("wifiListData");
            type = bundle.getInt("type");
        }
    }

    @Override
    public int layoutResID() {
        return R.layout.fragment_wall_gateway_wifi_config;
    }

    @Override
    protected void initTitle(View v) {
        super.initTitle(v);
        setLeftImg(R.drawable.icon_back);
        mTvTitle.setText(getString(R.string.Config_WiFi));
    }

    @Override
    public void initView(View view) {
        tvWifiName = (TextView) view.findViewById(R.id.tv_wifi_name);
        etWifiPassword = (EditText) view.findViewById(R.id.et_wifi_pwd);
        etWifiNameMini = (EditText) view.findViewById(R.id.et_mini_wifi_name);
        etWifiPasswordMini = (EditText) view.findViewById(R.id.et_wifi_pwd_mini);
        cbPassword = (CheckBox) view.findViewById(R.id.cb_wifi_pwd_show);
        cbPasswordMini = (CheckBox) view.findViewById(R.id.cb_wifi_pwd_show_mini);
        cbSync = (CheckBox) view.findViewById(R.id.cb_synchronize);
        btnNextStep = (Button) view.findViewById(R.id.btn_next_step);

    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(btnNextStep, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(btnNextStep, SkinResouceKey.COLOR_BUTTON_TEXT);
    }

    @Override
    public void initListener() {
        super.initListener();
        tvWifiName.setOnClickListener(this);
        btnNextStep.setOnClickListener(this);
        cbPassword.setOnCheckedChangeListener(this);
        cbPasswordMini.setOnCheckedChangeListener(this);
        cbSync.setOnCheckedChangeListener(this);
        mImgBack.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        wifiList = parseWifiListData();
        tvWifiName.setText(wifiList.get(0).essid);
        etWifiNameMini.setText("Wulian_" + deviceId.substring(deviceId.length() - 6, deviceId.length()));
        wifiInfoBean = new WifiInfoBean();
        wifiInfoBean = wifiList.get(0);
    }

    @Override
    public void onResume() {
        super.onResume();
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                getScanResult();
            }
        }
        getFocus();
    }

    /**
     * 解析网关返回的数据生成wifiList
     *
     * @return
     */
    private List<WifiInfoBean> parseWifiListData() {
        wifiList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(wifiListData);
            String data = jsonObject.optString("body");
            JSONObject dataObj = new JSONObject(data);
            JSONArray jsonArray = dataObj.getJSONArray("cell");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject js = (JSONObject) jsonArray.get(i);
                wifiList.add(new WifiInfoBean(js.optString("essid"),
                        js.optString("encryption"), js.optString("address"),
                        js.optString("quality"), js.optString("mode"), js.optString("channel"), js.optString("signal")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return wifiList;
    }

    private void getScanResult() {
        if (ContextCompat.checkSelfPermission(context, PERMISSION_ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, PERMISSION_ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{PERMISSION_ACCESS_COARSE_LOCATION, PERMISSION_ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        } else {
            consumeWifiScanResultList();
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
        MiniGatewayWifiListDialog dialog = new MiniGatewayWifiListDialog(context, new MiniGatewayWifiListDialog.OnWifiSelectedListener() {
            @Override
            public void onWifiSelected(WifiInfoBean bean) {
                wifiInfoBean = bean;
                tvWifiName.setText(bean.essid);
            }
        }, wifiList);
        dialog.show();
    }

    private void setAllLinkInfo() {
        int encryption = WifiUtil.getIntEncryption(wifiInfoBean.encryption);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "330");
            jsonObject.put("appID", MainApplication.getApplication().getLocalInfo().appID);
            jsonObject.put("operType", "setAlllinkInfo");
            jsonObject.put("msgid", "2");
            //上行路由字段
            JSONObject config0 = new JSONObject();
            config0.put("ssid", wifiInfoBean.essid);
            config0.put("key", etWifiPassword.getText());
            config0.put("encryption", encryption);
            config0.put("channel", wifiInfoBean.channel);
            config0.put("address", wifiInfoBean.address);
            config0.put("security_type", "aes");
            if (encryption == 4) {
                JSONObject WEP_INFO = new JSONObject();
                WEP_INFO.put("apcli_encryption", "open");
                WEP_INFO.put("apcli_index", "1234");
                config0.put("WEP_INFO", WEP_INFO);
            } else {
                config0.put("WEP_INFO", "");
            }

            //下行路由字段
            JSONObject config1 = new JSONObject();
            config1.put("ssid", etWifiNameMini.getText());
            config1.put("key", etWifiPasswordMini.getText());
            config1.put("encryption", encryption);
            config1.put("channel", wifiInfoBean.channel);

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("config0", config0);
            jsonObject1.put("config1", config1);
            jsonObject1.put("customflag", customFlag);

            jsonObject.put("body", jsonObject1);

            WLog.i(TAG, "setAllLinkInfo: json = " + jsonObject.toString());
            if (type == 0) {
                MiniMQTTManger.getInstance(getActivity()).publish("gw/config", jsonObject.toString());
            } else {
                jsonObject.put("gwID", deviceId);
                MainApplication.getApplication()
                        .getMqttManager()
                        .publishEncryptedMessage(jsonObject.toString(), MQTTManager.MODE_GATEWAY_FIRST);
            }
        } catch (JSONException e) {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_wifi_name:
                showDialog();
                break;
            case R.id.btn_next_step:
                if (etWifiPasswordMini.length()>=8) {
                    setAllLinkInfo();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    if (type == 0) {
                        configResultFragment = configResultFragment.newInstance(deviceId, 0, scanEntry);
                        ft.replace(android.R.id.content, configResultFragment, WallGatewayConfigResultFragment.class.getName());
                        ft.addToBackStack(null);
                    } else {
                        configResultFragment = configResultFragment.newInstance(deviceId, 1, scanEntry);
                        ft.add(android.R.id.content, configResultFragment, WallGatewayConfigResultFragment.class.getName());
                    }
                    ft.commit();
                } else {
                    ToastUtil.show(getString(R.string.WiFi_Password_Eight));
                }
                break;

            case R.id.base_img_back_fragment:
                FragmentTransaction ft2 = getFragmentManager().beginTransaction();
                if (type == 0) {
                    guideFragment = guideFragment.newInstance(deviceId, scanEntry);
                    ft2.replace(android.R.id.content, guideFragment, WallGatewayGuideFragment.class.getName());
                    ft2.addToBackStack(null);
                    ft2.commit();
                } else {
                    getActivity().finish();
                }

                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if (id == R.id.cb_wifi_pwd_show) {
            if (isChecked) {
                etWifiPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                etWifiPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            CharSequence charsequence = etWifiPassword.getText();
            if (charsequence instanceof Spannable) {
                Spannable spanText = (Spannable) charsequence;
                Selection.setSelection(spanText, charsequence.length());
            }
        } else if (id == R.id.cb_wifi_pwd_show_mini) {
            if (isChecked) {
                etWifiPasswordMini.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                etWifiPasswordMini.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            CharSequence charsequence = etWifiPasswordMini.getText();
            if (charsequence instanceof Spannable) {
                Spannable spanText = (Spannable) charsequence;
                Selection.setSelection(spanText, charsequence.length());
            }
        } else if (id == R.id.cb_synchronize) {
            if (isChecked) {
                etWifiNameMini.setText(tvWifiName.getText());
                etWifiPasswordMini.setText(etWifiPassword.getText());
                customFlag = 1;
            } else {
                etWifiNameMini.setText("Wulian_" + deviceId.substring(deviceId.length() - 6, deviceId.length()));
                etWifiPasswordMini.setText("");
                customFlag = 0;
            }
        }
    }

    //主界面获取焦点
    private void getFocus() {
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    FragmentTransaction ft2 = getFragmentManager().beginTransaction();
                    if (type == 0) {
                        guideFragment = guideFragment.newInstance(deviceId, scanEntry);
                        ft2.replace(android.R.id.content, guideFragment, WallGatewayGuideFragment.class.getName());
                        ft2.addToBackStack(null);
                        ft2.commit();
                    } else {
                        getActivity().finish();
                    }
                    return true;
                }
                return false;
            }
        });
    }
}
