package cc.wulian.smarthomev6.main.device.config;

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
import cc.wulian.smarthomev6.entity.ConfigWiFiInfoModel;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.main.device.AddDeviceActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.ConstantsUtil;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by hxc on 2017/7/7.
 * func:摄像机已经绑定界面
 */

public class DeviceAlreadyBindFragment extends WLFragment implements View.OnClickListener {
    private Button btnNextStep;
    private ConfigWiFiInfoModel configData;
    private String scanType;
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

    public static DeviceAlreadyBindFragment newInstance(int boundRelation, String name, ConfigWiFiInfoModel configData) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("configData", configData);
        bundle.putString("bindAccount", name);
        bundle.putInt("boundRelation", boundRelation);
        DeviceAlreadyBindFragment deviceAlreadyBindFragment = new DeviceAlreadyBindFragment();
        deviceAlreadyBindFragment.setArguments(bundle);
        return deviceAlreadyBindFragment;
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
        configData = bundle.getParcelable("configData");
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
        scanType = configData.getScanType();
        deviceType = configData.getDeviceType();
        dataApiUnit = new DataApiUnit(getActivity());
        updateViewByDeviceId(scanType);
    }

    private void updateViewByDeviceId(String scanType) {
        if (boundRelation == 2) {
            tvBindShow.setText(getString(R.string.Config_Device_Already_In_List));
        } else {
            tvBindShow.setText(getString(R.string.Tips1_Already_Bind) + username + getString(R.string.BindGateway_Bind));
            if (TextUtils.equals(ConstantsUtil.DEVICE_SCAN_ENTRY, scanType) || TextUtils.equals(ConstantsUtil.CAMERA_ADD_ENTRY, scanType)) {
                tvBindTips.setText(getString(R.string.Scan_Result_Bound_Tip));
                btnNextStep.setText(getString(R.string.Cateyemini_Adddevice_Continue));
            }
        }
        switch (deviceType) {
            case "CMICA1":
                ivCameraIcon.setImageResource(R.drawable.icon_cmic08);
                break;
            case "CMICA2":
                ivCameraIcon.setImageResource(R.drawable.icon_camera_2);
                if (!TextUtils.isEmpty(scanType) && TextUtils.equals(ConstantsUtil.BIND_CAMERA_GATEWAY_ENTRY, scanType)) {
                    btnNextStep.setText(getString(R.string.Gateway_bind));
                    tvBindGatewayTips.setVisibility(View.VISIBLE);
                    tvBindTips.setVisibility(View.INVISIBLE);
                }
                break;
            case "CMICA3":
            case "CMICA6":
                ivCameraIcon.setImageResource(R.drawable.icon_camera_3);
                if (!TextUtils.isEmpty(scanType) && TextUtils.equals(ConstantsUtil.BIND_CAMERA_GATEWAY_ENTRY, scanType)) {
                    btnNextStep.setText(getString(R.string.Gateway_bind));
                    tvBindGatewayTips.setVisibility(View.VISIBLE);
                    tvBindTips.setVisibility(View.INVISIBLE);
                }
                break;
            case "CMICA5":
                ivCameraIcon.setImageResource(R.drawable.icon_camera_5);
                if (!TextUtils.isEmpty(scanType) && TextUtils.equals(ConstantsUtil.BIND_CAMERA_GATEWAY_ENTRY, scanType)) {
                    btnNextStep.setText(getString(R.string.Gateway_bind));
                    tvBindGatewayTips.setVisibility(View.VISIBLE);
                    tvBindTips.setVisibility(View.INVISIBLE);
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next_step:
                if (!StringUtil.isNullOrEmpty(scanType) && scanType.equals(ConstantsUtil.CAMERA_ADD_ENTRY)) {
                    if (boundRelation == 2) {
                        startActivity(new Intent(context, AddDeviceActivity.class));
                        getActivity().finish();
                    } else {
                        dataApiUnit.doPostIcamBindPush(configData.getDeviceId(), new DataApiUnit.DataApiCommonListener<Object>() {
                            @Override
                            public void onSuccess(Object bean) {
                                WLog.i(TAG, "onSuccess: ");
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                ToastUtil.show(msg);
                            }
                        });
                        deviceWelcomeFragment = DeviceWelcomeFragment.newInstance(configData);
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(android.R.id.content, deviceWelcomeFragment, DeviceWelcomeFragment.class.getName());
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                } else if (!StringUtil.isNullOrEmpty(scanType) && scanType.equals(ConstantsUtil.DEVICE_SCAN_ENTRY)) {
                    if (boundRelation == 2) {
                        getActivity().finish();
                    } else {
                        dataApiUnit.doPostIcamBindPush(configData.getDeviceId(), new DataApiUnit.DataApiCommonListener<Object>() {
                            @Override
                            public void onSuccess(Object bean) {
                                WLog.i(TAG, "onSuccess: ");
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                ToastUtil.show(msg);

                            }
                        });
                        deviceWelcomeFragment = DeviceWelcomeFragment.newInstance(configData);
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(android.R.id.content, deviceWelcomeFragment, DeviceWelcomeFragment.class.getName());
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                } else if (!StringUtil.isNullOrEmpty(scanType) && scanType.equals(ConstantsUtil.BIND_CAMERA_GATEWAY_ENTRY)) {
                    configData.setSeed("");
                    deviceWelcomeFragment = DeviceWelcomeFragment.newInstance(configData);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(android.R.id.content, deviceWelcomeFragment, DeviceWelcomeFragment.class.getName());
                    ft.addToBackStack(null);
                    ft.commit();
                }
                break;
            case R.id.base_img_back_fragment:
                if (TextUtils.equals(configData.getScanType(), ConstantsUtil.DEVICE_SCAN_ENTRY)
                        || TextUtils.equals(configData.getAsGateway(), ConstantsUtil.NEED_JUMP_BIND_GATEWAY_FLAG)
                        || TextUtils.equals(configData.getScanType(), ConstantsUtil.CAMERA_ADD_ENTRY)) {
                    startActivity(new Intent(getActivity(), AddDeviceActivity.class));
                    getActivity().finish();
                } else {
                    getActivity().finish();
                }
                break;
        }
    }
}
