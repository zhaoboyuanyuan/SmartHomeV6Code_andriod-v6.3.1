package cc.wulian.smarthomev6.main.device.gateway_mini.config;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTApiConfig;
import cc.wulian.smarthomev6.support.core.mqtt.MiniMQTTManger;
import cc.wulian.smarthomev6.support.core.socket.GatewayBean;
import cc.wulian.smarthomev6.support.event.MiniGatewayConfigEvent;
import cc.wulian.smarthomev6.support.event.MiniGatewayConnectedEvent;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.DialogUtil;
import cc.wulian.smarthomev6.support.utils.LanguageUtil;
import cc.wulian.smarthomev6.support.utils.Netgear_IpAddressTranfer;

/**
 * created by huxc  on 2017/8/21.
 * func：  mini网关连接wifi提示页面
 * email: hxc242313@qq.com
 */

public class MiniGatewayConnectWifiFragment extends WLFragment implements View.OnClickListener {

    private TextView tvWifiName;
    private TextView tvWifiConnectTip;
    private Button btnNextStep;
    private ImageView ivBg;

    private String deviceId;
    private String scanEntry;
    private String gwID;
    private Context context;
    private Dialog dialog;
    private GatewayBean gatewayBean;
    private WifiInfo wifiInfo;
    private MiniMQTTManger mMiniMQTTManger;
    private MiniGatewayWifiConfigFragment mMiniGatewayWifiConfigFragment;


    public static MiniGatewayConnectWifiFragment newInstance(String deviceId,String scanEntry) {
        Bundle bundle = new Bundle();
        bundle.putString("deviceId", deviceId);
        bundle.putString("scanEntry", scanEntry);
        MiniGatewayConnectWifiFragment miniGatewayConnectWifiFragment = new MiniGatewayConnectWifiFragment();
        miniGatewayConnectWifiFragment.setArguments(bundle);
        return miniGatewayConnectWifiFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        context = getActivity();
        Bundle bundle = getArguments();
        deviceId = bundle.getString("deviceId");
        scanEntry = bundle.getString("scanEntry");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public int layoutResID() {
        return R.layout.fragment_minigateway_connect_wifi;
    }

    @Override
    protected void initTitle(View v) {
        super.initTitle(v);
        mTvTitle.setText(getString(R.string.Minigateway_Adddevice_Connectnetwork));
        setLeftImg(R.drawable.icon_back);
    }

    @Override
    public void initView(View view) {
        tvWifiName = (TextView) view.findViewById(R.id.tv_wifi_name);
        tvWifiConnectTip = (TextView) view.findViewById(R.id.tv_wifi_connect_tip);
        btnNextStep = (Button) view.findViewById(R.id.btn_next_step);
        ivBg = (ImageView) view.findViewById(R.id.iv_bg);
        if (LanguageUtil.isEnglish()) {
            ivBg.setBackgroundResource(R.drawable.mini_gateway_connect_wifi_en);
        }
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
        tvWifiName.setText("Mini_" + deviceId.substring(deviceId.length() - 6, deviceId.length()));
        gwID = deviceId.substring(deviceId.length() - 12, deviceId.length());
        connectGateway();
    }

    @Override
    public void initListener() {
        super.initListener();
        tvWifiConnectTip.setOnClickListener(this);
        btnNextStep.setOnClickListener(this);
    }

    public void connectGateway() {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            wifiInfo = wifiManager.getConnectionInfo();
            String routerIpAddress;
            routerIpAddress = Netgear_IpAddressTranfer.long2ip(wifiManager.getDhcpInfo().gateway);
//            ToastUtil.show(routerIpAddress);
            if (wifiInfo != null) {
                MQTTApiConfig.GW_SERVER_URL = routerIpAddress + ":1883";
                MQTTApiConfig.gwUserName = "a" + System.currentTimeMillis();
                MQTTApiConfig.gwUserPassword = "b";
                MiniMQTTManger.getInstance(getActivity()).connectGateway();
            }
        }
    }

    //解析数据
    private void parseJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String result = jsonObject.optString("result");
            String reason = jsonObject.optString("reason");
            String data = jsonObject.optString("body");
            JSONObject dataObj = new JSONObject(data);
            JSONArray jsonArray = dataObj.getJSONArray("cell");
            if (TextUtils.equals(result, "0") && mMiniGatewayWifiConfigFragment == null) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                mMiniGatewayWifiConfigFragment = MiniGatewayWifiConfigFragment.newInstance(deviceId, json, 0,scanEntry);
                ft.replace(android.R.id.content, mMiniGatewayWifiConfigFragment, MiniGatewayWifiConfigFragment.class.getName());
                ft.addToBackStack(null);
                ft.commit();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        if (v == tvWifiConnectTip) {
            dialog = DialogUtil.showCommonTipDialog(context, false, "", getString(R.string.Minigateway_Problems_Prompt), getString(R.string.Tip_I_Known), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        } else if (v == btnNextStep) {
            startActivityForResult(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS), 101);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101){
            connectGateway();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMiniGatewayConfigEvent(MiniGatewayConfigEvent event) {
        if (event != null) {
            parseJson(event.jsonData);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMiniGatewayConnectEvent(MiniGatewayConnectedEvent event) {
        if (event != null) {
        }
    }
}
