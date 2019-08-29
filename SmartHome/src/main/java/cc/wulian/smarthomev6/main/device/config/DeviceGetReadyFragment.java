package cc.wulian.smarthomev6.main.device.config;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
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
import cc.wulian.smarthomev6.entity.SpannableBean;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.ConstantsUtil;
import cc.wulian.smarthomev6.support.utils.DialogUtil;
import cc.wulian.smarthomev6.support.utils.StringUtil;

/**
 * Created by Administrator on 2017/7/6.
 */

public class DeviceGetReadyFragment extends WLFragment implements View.OnClickListener {
    private TextView tvNoVoice;
    private ConfigWiFiInfoModel configData;
    private TextView tvConfigTips;
    private TextView tvConfigTips2;
    private ImageView ivIcon;
    private Button btnNextStep;
    private Dialog tipDialog;
    private String messageTips;
    private FragmentTransaction ft;
    private FragmentManager fm;
    private Context context;
    private DeviceBarcodeFragment deviceBarcodeFragment;
    private DeviceWifiDirectFragment deviceWifiDirectFragment;

    public static DeviceGetReadyFragment newInstance(ConfigWiFiInfoModel configData) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("configData", configData);
        DeviceGetReadyFragment deviceGetReadyFragment = new DeviceGetReadyFragment();
        deviceGetReadyFragment.setArguments(bundle);
        return deviceGetReadyFragment;
    }

    @Override
    public int layoutResID() {
        return R.layout.activity_device_get_ready;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        fm = getFragmentManager();
        Bundle bundle = getArguments();
        configData = bundle.getParcelable("configData");

    }

    @Override
    public void initView(View v) {
        mTvTitle.setText(getString(R.string.Config_WiFi_Ready));
        tvNoVoice = (TextView) v.findViewById(R.id.tv_no_voice);
        tvNoVoice.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvConfigTips = (TextView) v.findViewById(R.id.tv_config_tips);
        tvConfigTips2 = (TextView) v.findViewById(R.id.tv_config_tips2);
        ivIcon = (ImageView) v.findViewById(R.id.iv_icon);
        btnNextStep = (Button) v.findViewById(R.id.btn_next_step);
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
        setLeftImg(R.drawable.icon_back);
    }

    @Override
    public void initListener() {
        super.initListener();
        tvNoVoice.setOnClickListener(this);
        btnNextStep.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        updateViewByDeviceId();
    }

    private void updateViewByDeviceId() {
        switch (configData.getDeviceType()) {
            case "CMICA1":
                ivIcon.setImageResource(R.drawable.icon_08_device_set);
                StringUtil.addColorOrSizeorEvent(tvConfigTips2, getResources().getString(R.string.Cateye_WiFi_Setting_Tip2), new SpannableBean[]{
                        new SpannableBean(getResources().getColor(R.color.v6_green), 16, null)});
                messageTips = getString(R.string.CameraNoSoundText);

                break;
            case "CMICA2":
                ivIcon.setImageResource(R.drawable.icon_camera_2_device_set);
                StringUtil.addColorOrSizeorEvent(tvConfigTips, getResources().getString(R.string.Swipe_Tone_Tip), new SpannableBean[]{
                        new SpannableBean(getResources().getColor(R.color.v6_green), 16, null)});
                tvConfigTips2.setVisibility(View.INVISIBLE);
                messageTips = getString(R.string.Lookever_Camera_No_Tone_Tip);
                break;
            case "CMICA3":
            case "CMICA6":
                ivIcon.setImageResource(R.drawable.icon_camera_3_device_set);
                StringUtil.addColorOrSizeorEvent(tvConfigTips, getResources().getString(R.string.Penguin_Camera_No_Tone_Tip), new SpannableBean[]{
                        new SpannableBean(getResources().getColor(R.color.v6_green), 16, null)});
                tvConfigTips2.setVisibility(View.INVISIBLE);
                messageTips = getString(R.string.PenguinCameraNoSoundText);
                break;
            case "CMICA4":
                ivIcon.setImageResource(R.drawable.icon_camera_4_click);
                StringUtil.addColorOrSizeorEvent(tvConfigTips, getString(R.string.Cylincam_Tone_Tip), new SpannableBean[]{
                        new SpannableBean(getResources().getColor(R.color.v6_green), 16, null)});
                tvConfigTips2.setVisibility(View.INVISIBLE);
                messageTips = getString(R.string.Cylincam_Five_SYS_TIP);
                mTvTitle.setText(getString(R.string.Camera_Prepare));
                break;
            case "CMICA5":
                ivIcon.setImageResource(R.drawable.icon_camera_5_click);
                StringUtil.addColorOrSizeorEvent(tvConfigTips, getString(R.string.Outdoor_Rotate_Tip), new SpannableBean[]{
                        new SpannableBean(getResources().getColor(R.color.v6_green), 16, null)});
                tvConfigTips2.setVisibility(View.INVISIBLE);
                messageTips = getString(R.string.Lookever_Camera_Connect_Tip);
                tvNoVoice.setText(getString(R.string.Outdoor_Rotate_Help));
                btnNextStep.setText(getString(R.string.Outdoor_Rotated));
                break;
            default:
                break;
        }
    }

    private void showTipNotHearTipVoice(String title) {
        tipDialog = DialogUtil.showCommonTipDialog(context, false, title, messageTips,
                getResources().getString(R.string.Sure), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tipDialog.dismiss();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_no_voice) {
            String title;
            if (TextUtils.equals(configData.getDeviceType(), "CMICA5")) {
                title = getString(R.string.Outdoor_Rotate_Help);
            } else {
                title = getString(R.string.Config_No_Voice);
            }
            showTipNotHearTipVoice(title);
        } else if (id == R.id.btn_next_step) {
            if (TextUtils.equals(configData.getDeviceType(), "CMICA5")) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                configData.setConfigWiFiType(ConstantsUtil.CONFIG_DIRECT_WIFI_THIRD_BIND_SETTING);
                deviceWifiDirectFragment = deviceWifiDirectFragment.newInstance(configData);
                ft.replace(android.R.id.content, deviceWifiDirectFragment, DeviceGetReadyFragment.class.getName());
                ft.addToBackStack(null);
                ft.commit();
            } else {
                deviceBarcodeFragment = DeviceBarcodeFragment.newInstance(configData);
                ft = fm.beginTransaction();
                ft.replace(android.R.id.content, deviceBarcodeFragment, DeviceGetReadyFragment.class.getName());
                ft.addToBackStack(null);
                ft.commit();
            }

        }
    }
}
