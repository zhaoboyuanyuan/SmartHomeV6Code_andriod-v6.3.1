package cc.wulian.smarthomev6.main.device.config;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.ConfigWiFiInfoModel;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.DialogUtil;

/**
 * Created by hxc on 2017/7/7.
 * func:绑定或配网失败界面
 */

public class DeviceConfigFailFragment extends WLFragment implements View.OnClickListener {
    private Button btnNextStep;
    private ConfigWiFiInfoModel configData;
    private TextView tvMoreSolution;
    private TextView failTextView;
    private String type;
    private Dialog tipDialog;
    private String dialogTitle;
    private String dialogMessage;
    private Context context;
    private FragmentManager fm;
    private FragmentTransaction ft;

    public static DeviceConfigFailFragment newInstance(ConfigWiFiInfoModel configData) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("configData", configData);
        DeviceConfigFailFragment deviceConfigFailFragment = new DeviceConfigFailFragment();
        deviceConfigFailFragment.setArguments(bundle);
        return deviceConfigFailFragment;
    }

    @Override
    public int layoutResID() {
        return R.layout.activity_device_config_fail;
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
    protected void initTitle(View v) {
        super.initTitle(v);
        mTvTitle.setText(getString(R.string.Config_Add_Fail));
        setLeftImg(R.drawable.icon_back);
    }

    @Override
    public void initListener() {
        super.initListener();
        btnNextStep.setOnClickListener(this);
        tvMoreSolution.setOnClickListener(this);
        mImgBack.setOnClickListener(this);
    }

    @Override
    public void initView(View v) {
        btnNextStep = (Button) v.findViewById(R.id.btn_retry);
        tvMoreSolution = (TextView) v.findViewById(R.id.tv_more_solutions);
        failTextView = (TextView) v.findViewById(R.id.add_fail_tips);
        tvMoreSolution.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(btnNextStep, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(btnNextStep, SkinResouceKey.COLOR_BUTTON_TEXT);
    }

    @Override
    protected void initData() {
        super.initData();
        fm = getFragmentManager();
        ft = fm.beginTransaction();
        updateViewByDeviceId();
    }

    private void updateViewByDeviceId() {
        switch (configData.getDeviceType()) {
            case "CMICA1":
                if (!configData.isAddDevice()) {
                    failTextView.setText(getString(R.string.WIFI_Failed));
                } else {
                    failTextView.setText(getString(R.string.Add_Fail_Tips));
                }
                dialogMessage = getString(R.string.CameraMoreWaysText);
                break;
            case "CMICA2":
            case "CMICA3":
                if (!configData.isAddDevice()) {
                    failTextView.setText(getString(R.string.WIFI_Failed));
                } else {
                    failTextView.setText(getString(R.string.Add_Camera_Failed));
                }
                dialogMessage = getString(R.string.Lookever_Camera_Connect_Tip);
                break;
            case "CMICA5":
                if (!configData.isAddDevice()) {
                    failTextView.setText(getString(R.string.WIFI_Failed));
                } else {
                    failTextView.setText(getString(R.string.Add_Camera_Failed));
                }
               tvMoreSolution.setVisibility(View.INVISIBLE);
                break;
            case "CMICA6":
                if (!configData.isAddDevice()) {
                    failTextView.setText(getString(R.string.WIFI_Failed));
                } else {
                    failTextView.setText(getString(R.string.Add_Camera_Failed));
                }
                dialogMessage = getString(R.string.Penguin_Balance_Tip_4G);
                break;
            default:
                break;
        }
    }

    private void showMoreSolutionDialog() {
        tipDialog = DialogUtil.showCommonTipDialog(context, false, getString(R.string.More_Solution), dialogMessage,
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
        switch (id) {
            case R.id.btn_retry:
                DeviceWelcomeFragment deviceWelcomeFragment = DeviceWelcomeFragment.newInstance(configData);
                if (!deviceWelcomeFragment.isAdded()) {
                    ft.replace(android.R.id.content, deviceWelcomeFragment, DeviceWelcomeFragment.class.getName());
                    ft.addToBackStack(null);
                    ft.commit();
                }
                break;
            case R.id.tv_more_solutions:
                showMoreSolutionDialog();
                break;
            case R.id.base_img_back_fragment:
                getActivity().finish();
                break;
            default:
                break;
        }
    }
}
