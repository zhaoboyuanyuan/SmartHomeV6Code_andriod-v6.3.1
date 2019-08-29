package cc.wulian.smarthomev6.main.device.device_if02.config;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.DialogUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class IF02DevAddFailFragment extends WLFragment implements View.OnClickListener {
    private Button btnNextStep;
    private IF02InfoBean configData;
    //    private TextView tvMoreSolution;
    private TextView failTextView;
    private String type;
    private Dialog tipDialog;
    private String dialogTitle;
    private String dialogMessage;
    private Context context;
    private FragmentManager fm;
    private FragmentTransaction ft;

    public static IF02DevAddFailFragment newInstance(IF02InfoBean configData) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("configData", configData);
        IF02DevAddFailFragment deviceConfigSuccessFragment = new IF02DevAddFailFragment();
        deviceConfigSuccessFragment.setArguments(bundle);
        return deviceConfigSuccessFragment;
    }

    @Override
    public int layoutResID() {
        return R.layout.fragment_if01_dev_add_fail;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fm = getFragmentManager();
        context = getActivity();
        Bundle bundle = getArguments();
        configData = (IF02InfoBean) bundle.getSerializable("configData");
    }

    @Override
    protected void initTitle(View v) {
        super.initTitle(v);
        setLeftImg(R.drawable.icon_back);
    }

    @Override
    public void initListener() {
        //
        super.initListener();
        btnNextStep.setOnClickListener(this);
//        tvMoreSolution.setOnClickListener(this);
        mImgBack.setOnClickListener(this);
    }

    @Override
    public void initView(View v) {
        btnNextStep = (Button) v.findViewById(R.id.btn_retry);
//        tvMoreSolution = (TextView) v.findViewById(R.id.tv_more_solutions);
        failTextView = (TextView) v.findViewById(R.id.add_fail_tips);
//        tvMoreSolution.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
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
        dialogMessage = getString(R.string.Cateyemini_Adddevice_Resolvent);
        if (TextUtils.equals("IF02", configData.getDeviceType())) {
            mTvTitle.setText(getString(R.string.IF_009));
        } else if (TextUtils.equals("GW14", configData.getDeviceType())) {
            mTvTitle.setText(getString(R.string.Cateye_Result));
        }
        updateViewByDeviceId();
    }

    private void updateViewByDeviceId() {
        switch (configData.getDeviceType()) {
            case "IF02":
                dialogMessage = getString(R.string.Cateyemini_Adddevice_Resolvent);
                break;
            case "GW14":
                failTextView.setText(getString(R.string.WIFI_Failed));
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
//                DeviceWelcomeFragment deviceWelcomeFragment = DeviceWelcomeFragment.newInstance(configData);
//                if (!deviceWelcomeFragment.isAdded()) {
//                    ft.replace(android.R.id.content, deviceWelcomeFragment, DeviceWelcomeFragment.class.getName());
//                    ft.addToBackStack(null);
//                    ft.commit();
//                }

                AddIF02DeviceActivity.start(context, configData.getDeviceType(), false);
                getActivity().finish();
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
