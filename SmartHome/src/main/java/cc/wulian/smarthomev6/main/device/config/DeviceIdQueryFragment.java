package cc.wulian.smarthomev6.main.device.config;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.io.Serializable;
import java.util.Locale;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.ConfigWiFiInfoModel;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.apiunit.ICamApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.ICamCloudApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamLoginBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.IcamBindRelationBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.IcamCloudCheckBindBean;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.CameraUtil;
import cc.wulian.smarthomev6.support.utils.DialogUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by hxc on 2017/7/6.
 * func:查询是否绑定界面
 */

public class DeviceIdQueryFragment extends WLFragment implements View.OnClickListener {
    private Button btnRetry;
    private RelativeLayout rlQueryDevice;
    private RelativeLayout rlQueryDeviceFail;

    private String deviceId;
    private String scanType;
    private String boundUser;
    private int boundRelation;
    private boolean isBindDevice;
    private boolean hasBeenBinded = false;
    private Context context;
    private ICamLoginBean iCamLoginBean;
    private ConfigWiFiInfoModel configData;
    private ICamApiUnit iCamApiUnit;
    private FragmentManager manager;
    private FragmentTransaction ft;
    private DeviceWelcomeFragment deviceWelcomeFragment;
    private DeviceAlreadyBindFragment deviceAlreadyBindFragment;


    public static DeviceIdQueryFragment newInstance(ConfigWiFiInfoModel configData) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("configData", configData);
        DeviceIdQueryFragment deviceIdQueryFragment = new DeviceIdQueryFragment();
        deviceIdQueryFragment.setArguments(bundle);
        return deviceIdQueryFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        Bundle bundle = getArguments();
        if (bundle != null) {
            configData = getArguments().getParcelable("configData");
        }
    }

    @Override
    public int layoutResID() {
        return R.layout.activity_device_id_query;
    }

    @Override
    public void initView(View v) {
        rlQueryDevice = (RelativeLayout) v.findViewById(R.id.rl_query_device);
        rlQueryDeviceFail = (RelativeLayout) v.findViewById(R.id.rl_query_device_fail);
        btnRetry = (Button) v.findViewById(R.id.btn_retry_query_device);
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
    protected void initTitle(View v) {
        super.initTitle(v);
        setLeftImg(R.drawable.icon_back);
        mTvTitle.setText(R.string.Config_Query);
    }

    @Override
    protected void initData() {
        super.initData();
        manager = getFragmentManager();
        ft = manager.beginTransaction();
        iCamLoginBean = Preference.getPreferences().getICamInfo(ApiConstant.getUserID());
        iCamApiUnit = new ICamApiUnit(context, ApiConstant.getUserID());
        deviceId = configData.getDeviceId();
        scanType = configData.getScanType();
        isBindDevice = configData.isAddDevice();
        if (deviceId != null) {
            showDeviceQuery();
            if (isBindDevice) {
                startBindingCheck();
            } else {
                startWiFiConfig();//纯配网
            }
        } else {
            DialogUtil.showUnknownDeviceTips(context, new WLDialog.MessageListener() {
                @Override
                public void onClickPositive(View var1, String msg) {
                    getActivity().finish();
                }

                @Override
                public void onClickNegative(View var1) {

                }
            }, getString(R.string.Scancode_Unrecognized)).show();
        }

    }

    private void startBindingCheck() {
        ICamCloudApiUnit ICamCloudApiUnit = new ICamCloudApiUnit(context);
        ICamCloudApiUnit.doCheckBind(deviceId, configData.getDeviceType(), null, null, new ICamCloudApiUnit.IcamApiCommonListener<IcamCloudCheckBindBean>() {
            @Override
            public void onSuccess(IcamCloudCheckBindBean bean) {
                if (bean.boundRelation == 0) {
                    hasBeenBinded = false;
                } else if (bean.boundRelation == 1 || bean.boundRelation == 2) {
                    hasBeenBinded = true;
                    boundRelation = bean.boundRelation;
                    boundUser = bean.boundUser;
                }
                startGetSeedInfo();
            }

            @Override
            public void onFail(int code, String msg) {
                showDeviceQueryFail();
            }
        });
    }

    private void startGetSeedInfo() {
        ICamCloudApiUnit ICamCloudApiUnit = new ICamCloudApiUnit(context);
        ICamCloudApiUnit.doGetBoundRelationCode(deviceId, configData.getDeviceType(), null, new ICamCloudApiUnit.IcamApiCommonListener<IcamBindRelationBean>() {
            @Override
            public void onSuccess(IcamBindRelationBean bean) {
                configData.setAddDevice(isBindDevice);
                configData.setDeviceId(deviceId);
                configData.setSeed(bean.seed);
                configData.setQrConnect(true);
                if (hasBeenBinded) {
                    deviceAlreadyBindFragment = DeviceAlreadyBindFragment.newInstance(boundRelation, boundUser, configData);
                    ft.replace(android.R.id.content, deviceAlreadyBindFragment);
                    ft.commit();
                } else {
                    deviceWelcomeFragment = DeviceWelcomeFragment.newInstance(configData);
                    ft.replace(android.R.id.content, deviceWelcomeFragment, DeviceWelcomeFragment.class.getName());
                    ft.commit();
                }

            }

            @Override
            public void onFail(int code, String msg) {
                showDeviceQueryFail();
            }
        });
    }

    private void startWiFiConfig() {
        configData.setAddDevice(isBindDevice);
        configData.setQrConnect(true);
        configData.setDeviceId(deviceId);
        deviceWelcomeFragment = DeviceWelcomeFragment.newInstance(configData);
        ft.replace(android.R.id.content, deviceWelcomeFragment, DeviceWelcomeFragment.class.getName());
        ft.commit();
    }

    private void showDeviceQuery() {
        rlQueryDevice.setVisibility(View.VISIBLE);
        rlQueryDeviceFail.setVisibility(View.GONE);
    }

    private void showDeviceQueryFail() {
        rlQueryDevice.setVisibility(View.GONE);
        rlQueryDeviceFail.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_retry_query_device:
                startBindingCheck();
                showDeviceQuery();
                break;
        }
    }
}
