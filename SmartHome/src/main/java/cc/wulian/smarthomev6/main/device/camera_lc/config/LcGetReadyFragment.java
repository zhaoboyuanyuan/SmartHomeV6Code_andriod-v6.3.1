package cc.wulian.smarthomev6.main.device.camera_lc.config;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.LcConfigWifiModel;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.DialogUtil;

/**
 * Created by Administrator on 2017/7/6.
 */

public class LcGetReadyFragment extends WLFragment implements View.OnClickListener {
    private TextView tvNoVoice;
    private LcConfigWifiModel configData;
    private TextView tvConfigTips;
    private ImageView ivIcon;
    private Button btnNextStep;
    private Dialog tipDialog;
    private String messageTips;
    private FragmentTransaction ft;
    private FragmentManager fm;
    private Context context;
    private LcConfigWifiFragment configWifiFragment;

    public static LcGetReadyFragment newInstance(LcConfigWifiModel configData) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("configData", configData);
        LcGetReadyFragment getReadyFragment = new LcGetReadyFragment();
        getReadyFragment.setArguments(bundle);
        return getReadyFragment;
    }

    @Override
    public int layoutResID() {
        return R.layout.activity_lc_get_ready;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        fm = getFragmentManager();
        Bundle bundle = getArguments();
        configData = (LcConfigWifiModel) bundle.getSerializable("configData");

    }

    @Override
    public void initView(View v) {
        mTvTitle.setText(getString(R.string.Config_WiFi_Ready));
        tvNoVoice = (TextView) v.findViewById(R.id.tv_no_voice);
        tvNoVoice.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvConfigTips = (TextView) v.findViewById(R.id.tv_config_tips);
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
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_no_voice) {
            DialogUtil.showLcNoGreenLightDialog(getActivity());
        } else if (id == R.id.btn_next_step) {
            configWifiFragment = LcConfigWifiFragment.newInstance(configData);
            ft = fm.beginTransaction();
            ft.replace(android.R.id.content, configWifiFragment, configWifiFragment.getClass().getName());
            ft.addToBackStack(null);
            ft.commit();

        }
    }
}
