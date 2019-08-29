package cc.wulian.smarthomev6.main.device.device_Bn.config;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.ConfigWiFiInfoModel;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;

/**
 * Created by hxc on 2017/7/7.
 * func:bn绑定或配网成功界面
 */

public class BnConfigSuccessFragment extends WLFragment implements View.OnClickListener {
    private Button btnNextStep;
    private ConfigWiFiInfoModel configData;
    private TextView tvConfigWifiSuccess;
    private TextView tvConfigWifiSuccessTips;
    private Context context;

    public static BnConfigSuccessFragment newInstance(ConfigWiFiInfoModel configData) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("configData", configData);
        BnConfigSuccessFragment successFragment = new BnConfigSuccessFragment();
        successFragment.setArguments(bundle);
        return successFragment;
    }

    @Override
    public int layoutResID() {
        return R.layout.activity_device_config_success;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        Bundle bundle = getArguments();
        configData = bundle.getParcelable("configData");

    }

    @Override
    public void initView(View v) {
        btnNextStep = (Button) v.findViewById(R.id.btn_next_step);
        tvConfigWifiSuccess = (TextView) v.findViewById(R.id.tv_config_wifi_success);
        tvConfigWifiSuccessTips = (TextView) v.findViewById(R.id.tv_config_wifi_success_tips);
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
        mTvTitle.setText(getString(R.string.Config_Add_Success));
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
            case R.id.btn_next_step:
                getActivity().finish();
                break;
            case R.id.base_img_back_fragment:
                getActivity().finish();
                break;
        }
    }
}
