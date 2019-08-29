package cc.wulian.smarthomev6.main.device.device_if02.config;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.main.home.HomeActivity;
import cc.wulian.smarthomev6.main.mine.gatewaycenter.GatewayBindActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.LogUtil;

public class IF02DeviceAlreadyBindFragment extends WLFragment implements View.OnClickListener {

    private Button btnNextStep;
    private IF02InfoBean configData;
    private ImageView ivCameraIcon;
    private TextView tvBindGatewayTips;
    private TextView tvBindShow;
    private TextView tvBindTips;
    private String username;
    private int boundRelation;
    private String deviceType;
    private Context context;
    private boolean selfBind = false;
    private IF02DevAddSuccessFragment addSuccessFragment;
    private IF02DevAddFailFragment addFailFragment;

    public static IF02DeviceAlreadyBindFragment newInstance(int boundRelation, String name, IF02InfoBean configData) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("configData", configData);
        bundle.putString("bindAccount", name);
        bundle.putInt("boundRelation", boundRelation);
        IF02DeviceAlreadyBindFragment deviceAlreadyBindFragment = new IF02DeviceAlreadyBindFragment();
        deviceAlreadyBindFragment.setArguments(bundle);
        return deviceAlreadyBindFragment;
    }

    @Override
    public int layoutResID() {
        return R.layout.activity_his_device_already_bind;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        Bundle bundle = getArguments();
        configData = (IF02InfoBean) bundle.getSerializable("configData");
        username = bundle.getString("bindAccount");
        boundRelation = bundle.getInt("boundRelation");
    }

    @Override
    public void initView(View v) {
        btnNextStep = (Button) v.findViewById(R.id.btn_next_step);
        ivCameraIcon = (ImageView) v.findViewById(R.id.camera_icon);
        tvBindGatewayTips = (TextView) v.findViewById(R.id.tv_bind_gateway_tips);
        tvBindShow = (TextView) v.findViewById(R.id.tv_bind_show);
        tvBindTips = (TextView) v.findViewById(R.id.tv_bind_tips);
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
        mTvTitle.setText(getString(R.string.IF_009));
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
        deviceType = configData.getDeviceType();
        updateViewByDeviceId();
    }

    private void updateViewByDeviceId() {
        if (boundRelation == 2) {
            selfBind = true;
            tvBindShow.setText(getString(R.string.Config_Device_Already_In_List));
        } else {
            selfBind = false;
            tvBindShow.setText(getString(R.string.Tips1_Already_Bind) + username + getString(R.string.BindGateway_Bind));
            tvBindTips.setText(getString(R.string.Scan_Result_Bound_Tip));
            btnNextStep.setText(getString(R.string.Cateyemini_Adddevice_Continue));
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next_step:
                if (selfBind) {
                    startActivity(new Intent(getActivity(), HomeActivity.class));
                } else {
                    DeviceApiUnit deviceApiUnit = new DeviceApiUnit(getActivity());
                    LogUtil.WriteBcLog("deviceId = " + configData.getWifiId() + "#" + configData.getDeviceId() + ",deviceType = " + configData.getDeviceType());
                    deviceApiUnit.doBindDevice(configData.getDeviceId(), "", configData.getDeviceType(), new DeviceApiUnit.DeviceApiCommonListener() {
                        @Override
                        public void onSuccess(Object bean) {
                            addSuccessFragment = IF02DevAddSuccessFragment.newInstance(configData);
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.replace(android.R.id.content, addSuccessFragment);
                            ft.addToBackStack(null);
                            ft.commit();
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            addFailFragment = IF02DevAddFailFragment.newInstance(configData);
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.replace(android.R.id.content, addFailFragment);
                            ft.addToBackStack(null);
                            ft.commit();
                        }
                    });
                }
                break;
        }
    }
}
