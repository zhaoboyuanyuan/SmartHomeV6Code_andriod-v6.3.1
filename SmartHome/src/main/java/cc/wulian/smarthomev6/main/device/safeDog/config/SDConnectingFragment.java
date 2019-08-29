package cc.wulian.smarthomev6.main.device.safeDog.config;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.main.device.config.DeviceWelcomeFragment;

/**
 * created by huxc  on 2018/1/23.
 * func： 安全狗连接中界面
 * email: hxc242313@qq.com
 */

public class SDConnectingFragment extends WLFragment implements View.OnClickListener {
    private SDConnectFailFragment failFragment;

    public static SDConnectingFragment newInstance(String deviceId) {
        Bundle bundle = new Bundle();
        bundle.putString("deviceId", deviceId);
        SDConnectingFragment connectingFragment = new SDConnectingFragment();
        connectingFragment.setArguments(bundle);
        return connectingFragment;
    }

    @Override
    public int layoutResID() {
        return R.layout.fragment_safe_dog_connecting;
    }

    @Override
    public void initView(View view) {

    }

    @Override
    protected void initData() {
        super.initData();
        failFragment = SDConnectFailFragment.newInstance("123");
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (!failFragment.isAdded()) {
            ft.replace(android.R.id.content, failFragment);
            ft.addToBackStack(null);
            ft.commit();
        }


    }

    @Override
    public void onClick(View v) {

    }
}
