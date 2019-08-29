package cc.wulian.smarthomev6.main.device.safeDog.config;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import cc.wulian.smarthomev6.main.application.BaseActivity;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;

/**
 * created by huxc  on 2018/1/23.
 * func：安全狗配网起始页
 * email: hxc242313@qq.com
 */

public class SDConfigActivity extends BaseTitleActivity {

    private SDGuideFragment guideFragment;
    private String deviceId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        guideFragment = SDGuideFragment.newInstance(deviceId);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(android.R.id.content, guideFragment);
        ft.commit();
    }


}
