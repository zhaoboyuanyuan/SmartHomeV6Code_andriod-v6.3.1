package cc.wulian.smarthomev6.main.device.camera_lc.config;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.ConfigWiFiInfoModel;
import cc.wulian.smarthomev6.entity.LcConfigWifiModel;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.main.device.AddDeviceActivity;
import cc.wulian.smarthomev6.main.device.config.DeviceWelcomeFragment;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.ConstantsUtil;
import cc.wulian.smarthomev6.support.utils.LogUtil;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by hxc on 2017/7/7.
 * func:摄像机已经绑定界面
 */

public class LcAlreadyBindFragment extends WLFragment implements View.OnClickListener {
    private Button btnNextStep;
    private LcConfigWifiModel configData;
    private ImageView ivCameraIcon;
    private TextView tvBindGatewayTips;
    private TextView tvBindShow;
    private TextView tvBindTips;
    private String username;
    private int boundRelation;
    private String deviceType;
    private Context context;
    private DeviceWelcomeFragment deviceWelcomeFragment;
    private DataApiUnit dataApiUnit;
    private LcInputDevicePwdFragment inputDevicePwdFragment;
    private FragmentManager fm;

    public static LcAlreadyBindFragment newInstance(int boundRelation, String name, LcConfigWifiModel configData) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("configData", configData);
        bundle.putString("bindAccount", name);
        bundle.putInt("boundRelation", boundRelation);
        LcAlreadyBindFragment alreadyBindFragment = new LcAlreadyBindFragment();
        alreadyBindFragment.setArguments(bundle);
        return alreadyBindFragment;
    }

    @Override
    public int layoutResID() {
        return R.layout.activity_already_bind;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        Bundle bundle = getArguments();
        configData = (LcConfigWifiModel) bundle.getSerializable("configData");
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
        mTvTitle.setText(getString(R.string.Scan_result));
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
        dataApiUnit = new DataApiUnit(getActivity());
        updateViewByDeviceId();
    }

    private void updateViewByDeviceId() {
        if (boundRelation == 2) {
            tvBindShow.setText(getString(R.string.Config_Device_Already_In_List));
        } else {
            tvBindShow.setText(String.format(getString(R.string.addDevice_XW01_Has_bound), username));
            tvBindTips.setText(getString(R.string.Scan_Result_Bound_Tip));
            btnNextStep.setText(getString(R.string.Cateyemini_Adddevice_Continue));
        }
        switch (deviceType) {
            case "CG22":
            case "CG23":
                ivCameraIcon.setImageResource(R.drawable.icon_cg22_bg);
                break;
            case "CG24":
            case "CG25":
                ivCameraIcon.setImageResource(R.drawable.icon_cg24_bg);
                break;
            case "CG26":
                ivCameraIcon.setImageResource(R.drawable.icon_cg26_bg);
                break;
            default:
                break;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next_step:
                if (boundRelation == 2) {
                    startActivity(new Intent(getActivity(), AddDeviceActivity.class));
                    getActivity().finish();
                } else {
                    inputDevicePwdFragment = LcInputDevicePwdFragment.newInstance(configData);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(android.R.id.content, inputDevicePwdFragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
                break;
            case R.id.base_img_back_fragment:
                getActivity().finish();
                break;
        }
    }
}
