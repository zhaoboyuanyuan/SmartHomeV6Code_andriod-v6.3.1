package cc.wulian.smarthomev6.main.device.hisense.config;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.main.device.hisense.bean.HisInfoBean;
import cc.wulian.smarthomev6.main.home.HomeActivity;
import cc.wulian.smarthomev6.support.core.apiunit.UserApiUnit;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;

/**
 * created by huxc  on 2018/1/4.
 * func：  海信设备添加成功界面
 * email: hxc242313@qq.com
 */

public class HisDevAddSuccessFragment extends WLFragment implements View.OnClickListener {
    private Button btnNextStep;
    private HisInfoBean configData;
    private String type;
    private Context context;

    private TextView tvTips;

    public static HisDevAddSuccessFragment newInstance(HisInfoBean configData) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("configData", configData);
        HisDevAddSuccessFragment deviceConfigSuccessFragment = new HisDevAddSuccessFragment();
        deviceConfigSuccessFragment.setArguments(bundle);
        return deviceConfigSuccessFragment;
    }

    @Override
    public int layoutResID() {
        return R.layout.fragment_hisdev_add_success;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        Bundle bundle = getArguments();
        configData = (HisInfoBean) bundle.getSerializable("configData");

    }

    @Override
    public void initView(View v) {
        btnNextStep = (Button) v.findViewById(R.id.btn_next_step);
        tvTips = (TextView) v.findViewById(R.id.tv_config_wifi_success);
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
        if (configData.isHasBind()) {
            tvTips.setText(getString(R.string.WiFi_Config_Fail));
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
            case R.id.btn_next_step:
                if (configData.isHasBind()) {
                    getActivity().finish();
                } else {
                    UserApiUnit userApiUnit = new UserApiUnit(getActivity());
                    userApiUnit.getAllDevice(true, false);
                    startActivity(new Intent(getActivity(), HomeActivity.class));
                }
                break;
            case R.id.base_img_back_fragment:
                getActivity().finish();
                break;
        }
    }
}

