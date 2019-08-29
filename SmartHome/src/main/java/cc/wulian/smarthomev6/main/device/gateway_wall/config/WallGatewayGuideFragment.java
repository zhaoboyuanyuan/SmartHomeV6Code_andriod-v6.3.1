package cc.wulian.smarthomev6.main.device.gateway_wall.config;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.main.device.gateway_mini.config.MiniGatewayConnectWifiFragment;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * created by huxc  on 2017/8/21.
 * func： 墙面网关配网引导页
 * email: hxc242313@qq.com
 */

public class WallGatewayGuideFragment extends WLFragment implements View.OnClickListener {
    private static final String TAG = "WallGatewayGuideFragment";
    private static final String DEVICE_ID = "deviceId";
    private String deviceId;
    private String scanEntry;
    private Context context;
    private Button btnWiredConnect;
    private Button btnWifiConnect;
    private WallGatewayWiredFragment wiredFragment;
    private WallGatewayConnectWifiFragment connectWifiFragment;

    public static WallGatewayGuideFragment newInstance(String deviceId, String scanEntry) {
        Bundle bundle = new Bundle();
        bundle.putString(DEVICE_ID, deviceId);
        bundle.putString("scanEntry", scanEntry);
        WallGatewayGuideFragment miniGatewayGuideFragment = new WallGatewayGuideFragment();
        miniGatewayGuideFragment.setArguments(bundle);
        return miniGatewayGuideFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        Bundle bundle = getArguments();
        if (bundle != null) {
            deviceId = bundle.getString(DEVICE_ID);
            scanEntry = bundle.getString("scanEntry");
        }
    }

    @Override
    public int layoutResID() {
        return R.layout.fragment_wall_gateway_guide;
    }

    @Override
    protected void initTitle(View v) {
        super.initTitle(v);
        setLeftImg(R.drawable.icon_back);
        mTvTitle.setText(getString(R.string.Device_Default_Name_GW08));
    }

    @Override
    public void initView(View view) {
        btnWiredConnect = (Button) view.findViewById(R.id.btn_wired_connection);
        btnWifiConnect = (Button) view.findViewById(R.id.btn_wifi_connection);

    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(btnWiredConnect, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(btnWiredConnect, SkinResouceKey.COLOR_BUTTON_TEXT);
        skinManager.setTextButtonColorAndBackground(btnWifiConnect, SkinResouceKey.COLOR_NAV);
    }

    @Override
    public void initListener() {
        super.initListener();
        btnWiredConnect.setOnClickListener(this);
        btnWifiConnect.setOnClickListener(this);
        mImgBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_wifi_connection:
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                connectWifiFragment = WallGatewayConnectWifiFragment.newInstance(deviceId,scanEntry);
                ft.replace(android.R.id.content, connectWifiFragment, WallGatewayConnectWifiFragment.class.getName());
                ft.addToBackStack(null);
                ft.commit();
                break;
            case R.id.btn_wired_connection:
                FragmentTransaction ft2 = getFragmentManager().beginTransaction();
                wiredFragment = WallGatewayWiredFragment.newInstance(deviceId,scanEntry);
                ft2.replace(android.R.id.content, wiredFragment, WallGatewayWiredFragment.class.getName());
                ft2.addToBackStack(null);
                ft2.commit();
                break;
            case R.id.base_img_back_fragment:
                getActivity().finish();
            default:
                break;
        }

    }
}
