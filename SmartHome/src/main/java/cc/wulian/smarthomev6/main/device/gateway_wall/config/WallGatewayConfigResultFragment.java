package cc.wulian.smarthomev6.main.device.gateway_wall.config;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLFragment;
import cc.wulian.smarthomev6.main.mine.gatewaycenter.GatewayBindActivity;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.ConstantsUtil;

/**
 * created by huxc  on 2017/11/8.
 * func：   墙面网关配网结果界面
 * email: hxc242313@qq.com
 */

public class WallGatewayConfigResultFragment extends WLFragment implements View.OnClickListener {
    private Button btnConfigSuccess;
    private Button btnConfigFail;

    private String deviceId;
    private String scanEntry;
    private Context context;

    private WallGatewayGuideFragment guideFragment;
    private int type;

    public static WallGatewayConfigResultFragment newInstance(String deviceId, int type, String scanEntry) {
        Bundle bundle = new Bundle();
        bundle.putString("deviceId", deviceId);
        bundle.putString("scanEntry", scanEntry);
        bundle.putInt("type", type);
        WallGatewayConfigResultFragment configResultFragment = new WallGatewayConfigResultFragment();
        configResultFragment.setArguments(bundle);
        return configResultFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        Bundle bundle = getArguments();
        if (bundle != null) {
            deviceId = bundle.getString("deviceId");
            scanEntry = bundle.getString("scanEntry");
            type = bundle.getInt("type");
        }
    }

    @Override
    public int layoutResID() {
        return R.layout.fragment_wall_gateway_config_result;
    }

    @Override
    protected void initTitle(View v) {
        super.initTitle(v);
        setLeftImg(R.drawable.icon_back);
        mTvTitle.setText(getString(R.string.Minigateway_Connectnetwork_Result));
    }

    @Override
    public void initView(View view) {
        btnConfigFail = (Button) view.findViewById(R.id.btn_connect_fail);
        btnConfigSuccess = (Button) view.findViewById(R.id.btn_connect_success);
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(btnConfigSuccess, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(btnConfigSuccess, SkinResouceKey.COLOR_BUTTON_TEXT);
        skinManager.setTextButtonColorAndBackground(btnConfigFail, SkinResouceKey.COLOR_NAV);
    }

    @Override
    public void initListener() {
        super.initListener();
        btnConfigFail.setOnClickListener(this);
        btnConfigSuccess.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_connect_success:
                if (type == 0) {
                    if (TextUtils.equals(scanEntry, ConstantsUtil.MINI_GATEWAY_LOGIN_ENTRY)
                            || TextUtils.equals(scanEntry, ConstantsUtil.MINI_GATEWAY_LIST_ENTRY)) {
                        getActivity().finish();
                    } else {
                        GatewayBindActivity.start(getActivity(), deviceId, true);
                    }
                }
                getActivity().finish();
                break;
            case R.id.btn_connect_fail:
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (type == 0) {
                    guideFragment = guideFragment.newInstance(deviceId, scanEntry);
                    ft.replace(android.R.id.content, guideFragment, WallGatewayGuideFragment.class.getName());
                    ft.addToBackStack(null);
                } else {
                    ft.remove(this);
                }
                ft.commit();
                break;
        }
    }
}
