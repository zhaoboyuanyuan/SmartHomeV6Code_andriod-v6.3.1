package cc.wulian.smarthomev6.main.device.gateway_wall.config;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
 * func：墙面网关有线连接
 * email: hxc242313@qq.com
 */

public class WallGatewayWiredFragment extends WLFragment {
    private Button btnNextStep;
    private String deviceId;
    private String scanEntry;

    public static WallGatewayWiredFragment newInstance(String deviceId,String scanEntry){
        Bundle bundle = new Bundle();
        bundle.putString("deviceId", deviceId);
        bundle.putString("scanEntry", scanEntry);
        WallGatewayWiredFragment wallGatewayWiredFragment = new WallGatewayWiredFragment();
        wallGatewayWiredFragment.setArguments(bundle);
        return wallGatewayWiredFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        deviceId = bundle.getString("deviceId");
        scanEntry = bundle.getString("scanEntry");
    }

    @Override
    public int layoutResID() {
        return R.layout.fragment_wall_gateway_wired;
    }

    @Override
    public void initView(View view) {
        btnNextStep = (Button) view.findViewById(R.id.btn_next_step);
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(btnNextStep, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(btnNextStep, SkinResouceKey.COLOR_BUTTON_TEXT);
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
    }

    @Override
    protected void initTitle(View v) {
        super.initTitle(v);
        setLeftImg(R.drawable.icon_back);
        mTvTitle.setText(getString(R.string.Device_Default_Name_GW08));
    }

    @Override
    public void initListener() {
        super.initListener();
        btnNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.equals(scanEntry, ConstantsUtil.MINI_GATEWAY_LOGIN_ENTRY)
                        || TextUtils.equals(scanEntry, ConstantsUtil.MINI_GATEWAY_LIST_ENTRY)) {
                } else {
                    GatewayBindActivity.start(getActivity(), deviceId, true);
                }
                getActivity().finish();
            }
        });
    }
}
