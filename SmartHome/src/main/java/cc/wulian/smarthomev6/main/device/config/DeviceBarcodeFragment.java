package cc.wulian.smarthomev6.main.device.config;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wulian.routelibrary.common.RouteLibraryParams;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.ConfigWiFiInfoModel;
import cc.wulian.smarthomev6.entity.SpannableBean;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.tools.zxing.encoding.EncodingUtils;
import cc.wulian.smarthomev6.support.utils.DialogUtil;
import cc.wulian.smarthomev6.support.utils.DirectUtils;
import cc.wulian.smarthomev6.support.utils.StringUtil;

/**
 * Created by hxc on 2017/7/6.
 * func:获取二维码界面
 */

public class DeviceBarcodeFragment extends WLFragment implements View.OnClickListener {
    private FragmentTransaction ft;
    private FragmentManager fm;
    private ConfigWiFiInfoModel configData;
    private Button btnNextStep;
    private ImageView iv_barcode;
    private TextView tvConfigBarcodeTips;
    private TextView tvNoVoiceTips;
    private Dialog tipDialog;
    private Context context;
    private DeviceCheckBindFragment deviceCheckBindFragment;
    private DeviceCylincamResultFragment cylincamResultFragment;
    private String originSSid;
    private String originSecurity;
    private String pwd;
    private String qrContent;

    public static DeviceBarcodeFragment newInstance(ConfigWiFiInfoModel configData) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("configData", configData);
        DeviceBarcodeFragment deviceBarcodeFragment = new DeviceBarcodeFragment();
        deviceBarcodeFragment.setArguments(bundle);
        return deviceBarcodeFragment;
    }

    @Override
    public int layoutResID() {
        return R.layout.activity_device_barcode;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fm = getFragmentManager();
        context = getActivity();
        Bundle bundle = getArguments();
        configData = bundle.getParcelable("configData");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setBrightestScreen(1);
        final ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        viewTreeObserver.addOnWindowFocusChangeListener(new ViewTreeObserver.OnWindowFocusChangeListener() {
            @Override
            public void onWindowFocusChanged(final boolean hasFocus) {
                // do your stuff here
                if (hasFocus) {
                    showQRImage();
                }
            }
        });
    }


    @Override
    public void initView(View v) {
        tvNoVoiceTips = (TextView) v.findViewById(R.id.tv_help);
        tvNoVoiceTips.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        btnNextStep = (Button) v.findViewById(R.id.btn_next_step);
        iv_barcode = (ImageView) v.findViewById(R.id.iv_barcode);
        tvConfigBarcodeTips = (TextView) v.findViewById(R.id.config_barcode_tips);
        if (TextUtils.equals("CMICA4",configData.getDeviceType())) {
            tvConfigBarcodeTips.setText(getResources().getString(R.string.Cylincam_Have_Tone_Tip));
        } else {
            StringUtil.addColorOrSizeorEvent(tvConfigBarcodeTips, getResources().getString(R.string.Config_Barcode_Tips),
                    new SpannableBean[]{new SpannableBean(getResources().getColor(R.color.v6_green), 16, null)});
        }

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
        mTvTitle.setText(getString(R.string.Config_Barcode));
    }

    @Override
    public void initListener() {
        super.initListener();
        btnNextStep.setOnClickListener(this);
        tvNoVoiceTips.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        DialogUtil.showBarcodeConfigTipDialog(context, configData.getDeviceId(), configData.getDeviceType());
    }

    private void setBrightestScreen(float a) {
        WindowManager.LayoutParams wl = getActivity().getWindow().getAttributes();
        wl.screenBrightness = a;
        getActivity().getWindow().setAttributes(wl);
    }

    private void showQRImage() {
        if (configData != null) {
            originSSid = configData.getWifiName();
            originSecurity = configData.getSecurity();
            pwd = configData.getWifiPwd();

            int width = iv_barcode.getWidth();
            int height = iv_barcode.getHeight();

            Bitmap bitmap = null;
            if (TextUtils.equals("CMICA4",configData.getDeviceType())) {
                bitmap = EncodingUtils.createQRCode(getCylincamQRInformation(), width, height, null);
            } else {
                bitmap = EncodingUtils.createQRCode(getICamQRInformation(), width, height, null);
            }

            iv_barcode.setImageBitmap(bitmap);
        }
    }

    private String getICamQRInformation() {
        StringBuilder sb = new StringBuilder();
        int secType = DirectUtils.getTypeSecurityByCap(originSecurity);
        if (TextUtils.equals(configData.getDeviceType(), "CMICA6")) {
            //如果是4G企鹅机不需要带WiFi信息，需要传9
            sb.append("9");
        } else {
            if (secType == DirectUtils.SECURITY_OPEN) {
                sb.append(secType + "\n");
                sb.append(originSSid+"\n");
            } else {
                sb.append(DirectUtils.getTypeSecurityByCap(originSecurity) + "\n");
                sb.append(originSSid + "\n");
                sb.append(RouteLibraryParams.EncodeMappingString(pwd));
            }
        }
        sb.append("\n");
        sb.append(RouteLibraryParams.EncodeMappingString(configData.getSeed()));
        return sb.toString();
    }

    private String getCylincamQRInformation() {
        StringBuilder sb = new StringBuilder();
        sb.append("01\n");
        sb.append(originSSid + "\n");
        sb.append("psk" + "\n");
        if (!TextUtils.isEmpty(pwd)) {
            sb.append(pwd + "\n");
        } else {
            sb.append("\n");
        }
        return sb.toString();
    }

    private void showNoVoiceTipsDialog() {
        String message = null;
        switch (configData.getDeviceType()) {
            case "CMICA1":
                message = getString(R.string.CameraNoSoundText);
                break;
            case "CMICA2":
                message = getString(R.string.Lookever_Camera_No_Tone_Tip);
                break;
            case "CMICA3":
            case "CMICA6":
                message = getString(R.string.PenguinCameraNoSoundText);
                break;
            case "CMICA4":
                message = getString(R.string.Cylincam_Five_SYS_TIP);
                break;
        }
        tipDialog = DialogUtil.showCommonTipDialog(context, false, getString(R.string.Config_No_Voice), message,
                getResources().getString(R.string.Sure), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tipDialog.dismiss();
                    }
                });
    }

    @Override
    public void onPause() {
        super.onPause();
        setBrightestScreen(0.5f);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_next_step) {
            ft = fm.beginTransaction();
            if (TextUtils.equals("CMICA4",configData.getDeviceType())) {
                cylincamResultFragment = DeviceCylincamResultFragment.newInstance(configData);
                ft.replace(android.R.id.content, cylincamResultFragment, DeviceCylincamResultFragment.class.getName());
            } else {
                deviceCheckBindFragment = DeviceCheckBindFragment.newInstance(configData);
                ft.replace(android.R.id.content, deviceCheckBindFragment, DeviceCheckBindFragment.class.getName());
            }
            ft.addToBackStack(null);
            ft.commit();
        } else if (id == R.id.tv_help) {
            showNoVoiceTipsDialog();
        }
    }
}
