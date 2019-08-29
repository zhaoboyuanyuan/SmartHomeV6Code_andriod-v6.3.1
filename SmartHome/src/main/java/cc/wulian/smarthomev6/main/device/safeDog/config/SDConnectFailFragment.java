package cc.wulian.smarthomev6.main.device.safeDog.config;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.main.device.config.DeviceWelcomeFragment;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;

/**
 * created by huxc  on 2018/1/24.
 * func：连接失败界面
 * email: hxc242313@qq.com
 */

public class SDConnectFailFragment extends WLFragment implements View.OnClickListener {
    private Button btnRetry;
    private SDGuideFragment sdGuideFragment;
    private FragmentManager fm;
    private FragmentTransaction ft;

    public static SDConnectFailFragment newInstance(String deviceId) {
        Bundle bundle = new Bundle();
        bundle.putString("deviceId", deviceId);
        SDConnectFailFragment connectFailFragment = new SDConnectFailFragment();
        connectFailFragment.setArguments(bundle);
        return connectFailFragment;
    }

    @Override
    public int layoutResID() {
        return R.layout.fragment_safe_dog_connect_fail;
    }

    @Override
    public void initView(View view) {
        btnRetry = (Button) view.findViewById(R.id.btn_retry);

    }

    @Override
    protected void initTitle(View v) {
        super.initTitle(v);
        setLeftImg(R.drawable.icon_back);
        mTvTitle.setText(getString(R.string.Config_Add_Fail));
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(btnRetry, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(btnRetry, SkinResouceKey.COLOR_BUTTON_TEXT);
    }

    @Override
    public void initListener() {
        super.initListener();
        btnRetry.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        fm = getFragmentManager();
        ft = fm.beginTransaction();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_retry:
                sdGuideFragment = SDGuideFragment.newInstance("1123");
                if (!sdGuideFragment.isAdded()) {
                    ft.replace(android.R.id.content, sdGuideFragment, DeviceWelcomeFragment.class.getName());
                    ft.addToBackStack(null);
                    ft.commit();
                }
                break;
        }

    }
}
