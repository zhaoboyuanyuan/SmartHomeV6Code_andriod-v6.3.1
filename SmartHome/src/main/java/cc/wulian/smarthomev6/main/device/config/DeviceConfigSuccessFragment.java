package cc.wulian.smarthomev6.main.device.config;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.ConfigWiFiInfoModel;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.main.mine.gatewaycenter.GatewayBindActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.ICamApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamInfoBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.ConstantsUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by hxc on 2017/7/7.
 * func:绑定或配网成功界面
 */

public class DeviceConfigSuccessFragment extends WLFragment implements View.OnClickListener {
    private Button btnNextStep;
    private ConfigWiFiInfoModel configData;
    private TextView tvConfigWifiSuccess;
    private TextView tvConfigWifiSuccessTips;
    private String type;
    private ICamApiUnit iCamApiUnit;
    private Context context;

    public static DeviceConfigSuccessFragment newInstance(ConfigWiFiInfoModel configData) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("configData", configData);
        DeviceConfigSuccessFragment deviceConfigSuccessFragment = new DeviceConfigSuccessFragment();
        deviceConfigSuccessFragment.setArguments(bundle);
        return deviceConfigSuccessFragment;
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
        updateViewByDeviceId();
        if (configData.isAddDevice()) {
            refreshIcamDevice();
        }
    }

    private void updateViewByDeviceId() {
        switch (configData.getDeviceType()) {
            case "CMICA1":
                if (configData.isAddDevice()) {
                    tvConfigWifiSuccess.setText(getString(R.string.Cateye_Binding_Success));
                    tvConfigWifiSuccessTips.setVisibility(View.GONE);
                } else {
                    tvConfigWifiSuccess.setText(getString(R.string.WiFi_Config_Fail));
                    tvConfigWifiSuccessTips.setVisibility(View.GONE);
                }
                break;
            case "CMICA2":
            case "CMICA3":
            case "CMICA5":
            case "CMICA6":
                if (configData.isAddDevice()) {
                    tvConfigWifiSuccess.setText(getString(R.string.Add_Camera_Success));
                    tvConfigWifiSuccessTips.setText(getString(R.string.Camera_Add_Success_Explain));
                } else {
                    tvConfigWifiSuccess.setText(getString(R.string.WiFi_Config_Fail));
                    tvConfigWifiSuccessTips.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }

    private void refreshIcamDevice() {
        DeviceApiUnit deviceApiUnit = new DeviceApiUnit(context);
        deviceApiUnit.doGetICamDeviceInfo(configData.getDeviceId(), new DeviceApiUnit.DeviceApiCommonListener<ICamInfoBean>() {
            @Override
            public void onSuccess(ICamInfoBean bean) {
                Device device = new Device(bean);
                MainApplication.getApplication().getDeviceCache().add(device);
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(msg);
            }
        });
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
                String uniqueDeviceId = configData.getDeviceId();
                if (uniqueDeviceId.startsWith("CG") && uniqueDeviceId.length() >= 11) {
                    uniqueDeviceId = uniqueDeviceId.substring(0, 11);
                }
                if (TextUtils.equals(configData.getAsGateway(), ConstantsUtil.NEED_JUMP_BIND_GATEWAY_FLAG)) {
                    startActivity(new Intent(getActivity(), GatewayBindActivity.class).putExtra("deviceId", uniqueDeviceId));
                }
                getActivity().finish();
                break;
            case R.id.base_img_back_fragment:
                getActivity().finish();
                break;
        }
    }
}
