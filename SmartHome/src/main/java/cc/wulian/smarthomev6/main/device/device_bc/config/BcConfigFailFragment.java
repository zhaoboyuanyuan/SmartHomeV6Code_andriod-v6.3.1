package cc.wulian.smarthomev6.main.device.device_bc.config;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.ConfigWiFiInfoModel;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;

/**
 * Created by hxc on 2017/7/7.
 * func:bc绑定或配网失败界面
 */

public class BcConfigFailFragment extends WLFragment implements View.OnClickListener {
    private Button btnNextStep;
    private ConfigWiFiInfoModel configData;
    private DevBcInputWifiFragment devBcInputWifiFragment;
    private DevBcCheckBindFragment checkBindFragment;
    private Device device;
	private String a;

    public static BcConfigFailFragment newInstance(ConfigWiFiInfoModel configData) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("configData", configData);
        BcConfigFailFragment successFragment = new BcConfigFailFragment();
        successFragment.setArguments(bundle);
        return successFragment;
    }

    @Override
    public int layoutResID() {
        return R.layout.fragment_dev_bc_config_fail;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        configData = bundle.getParcelable("configData");

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
    protected void initTitle(View v) {
        super.initTitle(v);
        mTvTitle.setText(getString(R.string.Cateye_Result));
        setLeftImg(R.drawable.icon_back);
    }

    @Override
    public void initListener() {
        super.initListener();
        btnNextStep.setOnClickListener(this);
        mImgBack.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        device = MainApplication.getApplication().getDeviceCache().get(configData.getDeviceId());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(new DeviceReportEvent(null));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_retry:
                if (configData.isUseGatewayWifi()) {
                    checkBindFragment = DevBcCheckBindFragment.newInstance(device.devID, device.gwID, configData);
                    if (!checkBindFragment.isAdded()) {
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(android.R.id.content, checkBindFragment);
                        ft.commit();
                    }
                } else {
                    devBcInputWifiFragment = DevBcInputWifiFragment.newInstance(device.gwID, device.devID, configData.isAddDevice());
                    if (!devBcInputWifiFragment.isAdded()) {
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(android.R.id.content, devBcInputWifiFragment, devBcInputWifiFragment.getClass().getName());
                        ft.commit();
                    }
                }

                break;
            case R.id.base_img_back_fragment:
                getActivity().finish();
                break;
            default:
                break;
        }
    }
}
