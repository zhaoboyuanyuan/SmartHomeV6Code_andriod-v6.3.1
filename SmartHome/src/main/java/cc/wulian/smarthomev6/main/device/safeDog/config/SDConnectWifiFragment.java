package cc.wulian.smarthomev6.main.device.safeDog.config;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.LanguageUtil;

/**
 * created by huxc  on 2018/1/23.
 * func：连接安全狗散发的热点界面
 * email: hxc242313@qq.com
 */

public class SDConnectWifiFragment extends WLFragment implements View.OnClickListener {
    private Button btnNextStep;
    private String deviceId;
    private ImageView ivBg;
    private SDConfigWifiFragment configWifiFragment;


    public static SDConnectWifiFragment newInstance(String deviceId) {
        Bundle bundle = new Bundle();
        bundle.putString("deviceId",deviceId);
        SDConnectWifiFragment connectWifi = new SDConnectWifiFragment();
        connectWifi.setArguments(bundle);
        return connectWifi;
    }

    @Override
    public int layoutResID() {
        return R.layout.fragment_safe_dog_connect_wifi;
    }

    @Override
    protected void initData() {
        super.initData();
        deviceId = getArguments().getString("deviceId");
    }

    @Override
    public void initListener() {
        super.initListener();
        btnNextStep.setOnClickListener(this);
    }

    @Override
    public void initView(View view) {
        btnNextStep = (Button) view.findViewById(R.id.btn_next_step);
        ivBg = (ImageView) view.findViewById(R.id.iv_bg);
        if (LanguageUtil.isEnglish()) {
            ivBg.setBackgroundResource(R.drawable.safe_dog_connect_wifi_en);
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
    protected void initTitle(View v) {
        super.initTitle(v);
        setLeftImg(R.drawable.icon_back);
        mTvTitle.setText(getString(R.string.Minigateway_Adddevice_Connectnetwork));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_next_step:
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                configWifiFragment= SDConfigWifiFragment.newInstance(deviceId);
                ft.replace(android.R.id.content, configWifiFragment);
                ft.commit();
                break;
        }

    }
}
