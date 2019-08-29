package cc.wulian.smarthomev6.main.device.device_Bn.config;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
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
 * func:bn绑定或配网失败界面
 */

public class BnConfigFailFragment extends WLFragment implements View.OnClickListener {
    private Button btnNextStep;
    private ConfigWiFiInfoModel configData;
    private DevBnInputWifiFragment inputWifiFragment;
    private DevBnCheckBindFragment checkBindFragment;
    private Device device;
    private String errorCode;
    private TextView tvErrorTip;

    public static BnConfigFailFragment newInstance(ConfigWiFiInfoModel configData, String errorCode) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("configData", configData);
        bundle.putString("errorCode", errorCode);
        BnConfigFailFragment failFragment = new BnConfigFailFragment();
        failFragment.setArguments(bundle);
        return failFragment;
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
        errorCode = bundle.getString("errorCode");

    }

    @Override
    public void initView(View v) {
        btnNextStep = (Button) v.findViewById(R.id.btn_retry);
        tvErrorTip = (TextView) v.findViewById(R.id.tv_config_wifi_fail_tip);
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
        showErrorCodeMessage();
    }


    private void showErrorCodeMessage() {
        if (!TextUtils.isEmpty(errorCode)) {
            switch (errorCode) {
                case "02":
                    tvErrorTip.setText(getResources().getString(R.string.addDevice_Bn_add_failure_2));
                    break;
                case "03":
                    tvErrorTip.setText(getResources().getString(R.string.addDevice_Bn_add_failure_1));
                    break;
                case "04":
                    tvErrorTip.setText(getResources().getString(R.string.addDevice_Bn_add_failure_3));
                    break;
                case "05":
                    tvErrorTip.setText(getResources().getString(R.string.addDevice_Bn_add_failure_4));
                    break;
            }
        }
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
                    checkBindFragment = DevBnCheckBindFragment.newInstance(device.devID, device.gwID, configData);
                    if (!checkBindFragment.isAdded()) {
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(android.R.id.content, checkBindFragment);
                        ft.commit();
                    }
                } else {
                    inputWifiFragment = DevBnInputWifiFragment.newInstance(device.gwID, device.devID, configData.isAddDevice());
                    if (!inputWifiFragment.isAdded()) {
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(android.R.id.content, inputWifiFragment, inputWifiFragment.getClass().getName());
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
