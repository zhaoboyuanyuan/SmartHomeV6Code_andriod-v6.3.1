package cc.wulian.smarthomev6.main.device.camera_lc.config;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.ConfigWiFiInfoModel;
import cc.wulian.smarthomev6.entity.LcConfigWifiModel;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.main.device.config.DeviceWelcomeFragment;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.DialogUtil;

/**
 * Created by hxc on 2017/7/7.
 * func:绑定或配网失败界面
 */

public class LcConfigFailFragment extends WLFragment implements View.OnClickListener {
    private Button btnNextStep;
    private LcConfigWifiModel configData;
    private Context context;
    private FragmentManager fm;
    private FragmentTransaction ft;

    public static LcConfigFailFragment newInstance(LcConfigWifiModel configData) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("configData", configData);
        LcConfigFailFragment failFragment = new LcConfigFailFragment();
        failFragment.setArguments(bundle);
        return failFragment;
    }

    @Override
    public int layoutResID() {
        return R.layout.fragment_lc_config_fail;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fm = getFragmentManager();
        context = getActivity();
        Bundle bundle = getArguments();
        configData = (LcConfigWifiModel) bundle.getSerializable("configData");
    }

    @Override
    protected void initTitle(View v) {
        super.initTitle(v);
        mTvTitle.setText(getString(R.string.Config_Add_Fail));
        setLeftImg(R.drawable.icon_back);
    }

    @Override
    public void initListener() {
        super.initListener();
        btnNextStep.setOnClickListener(this);
        mImgBack.setOnClickListener(this);
    }

    @Override
    public void initView(View v) {
        btnNextStep = (Button) v.findViewById(R.id.btn_retry);
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
        fm = getFragmentManager();
        ft = fm.beginTransaction();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_retry:
                AddLcCameraGuideActivity.start(getActivity(), configData.getDeviceType(), configData.getDeviceId(), configData.getScanEntry());
                getActivity().finish();
                break;
            case R.id.base_img_back_fragment:
                getActivity().finish();
                break;
            default:
                break;
        }
    }
}
