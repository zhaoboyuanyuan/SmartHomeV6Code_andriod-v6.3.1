package cc.wulian.smarthomev6.main.device.device_xw01.config;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.Format;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.hisense.config.HisDevAddSuccessFragment;
import cc.wulian.smarthomev6.main.home.HomeActivity;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.UserBean;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;

/**
 * created by huxc  on 2018/5/24.
 * func：向往背景音乐已被绑定界面
 * email: hxc242313@qq.com
 */

public class WishBgmAlreadyBindActivity extends BaseTitleActivity implements View.OnClickListener {
    private Button btnContinueAdd;
    private TextView tvAddTip;

    private String deviceUid;

    private UserBean userBean;

    private boolean selfBind = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_already_bind_wish_bgm, true);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.addDevice_XW01_title));
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(btnContinueAdd, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(btnContinueAdd, SkinResouceKey.COLOR_BUTTON_TEXT);
    }

    @Override
    protected void initView() {
        super.initView();
        btnContinueAdd = (Button) findViewById(R.id.btn_continue_add);
        tvAddTip = (TextView) findViewById(R.id.tv_add_tip);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        btnContinueAdd.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        deviceUid = getIntent().getStringExtra("deviceId");
        userBean = getIntent().getParcelableExtra("boundUser");
        if (userBean != null && !TextUtils.isEmpty(userBean.phone)) {
            if (userBean.uId.equals(ApiConstant.getUserID())) {
                selfBind = true;
                btnContinueAdd.setText(getString(R.string.Sure));
                tvAddTip.setText(getString(R.string.Config_Device_Already_In_List));
            } else {
                selfBind = false;
                tvAddTip.setText(getString(R.string.Tips1_Already_Bind) + userBean.phone + getString(R.string.BindGateway_Bind));
            }
        } else if (userBean != null && !TextUtils.isEmpty(userBean.email)) {
            if (userBean.uId.equals(ApiConstant.getUserID())) {
                selfBind = true;
                btnContinueAdd.setText(getString(R.string.Sure));
                tvAddTip.setText(getString(R.string.Config_Device_Already_In_List));
            } else {
                selfBind = false;
                tvAddTip.setText(getString(R.string.Tips1_Already_Bind) + userBean.email + getString(R.string.BindGateway_Bind));
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.btn_continue_add) {
            if (selfBind) {
                startActivity(new Intent(WishBgmAlreadyBindActivity.this, HomeActivity.class));
            } else {
                DeviceApiUnit deviceApiUnit = new DeviceApiUnit(this);
                deviceApiUnit.doBindDevice(deviceUid, "", "XW01", new DeviceApiUnit.DeviceApiCommonListener() {
                    @Override
                    public void onSuccess(Object bean) {
                        startActivity(new Intent(WishBgmAlreadyBindActivity.this, WishBgmAddResultActivity.class)
                                .putExtra("result", "success")
                                .putExtra("deviceId",deviceUid));
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        startActivity(new Intent(WishBgmAlreadyBindActivity.this, WishBgmAddResultActivity.class)
                                .putExtra("result", "fail")
                                .putExtra("deviceId",deviceUid));
                    }
                });
            }

        }
    }
}
