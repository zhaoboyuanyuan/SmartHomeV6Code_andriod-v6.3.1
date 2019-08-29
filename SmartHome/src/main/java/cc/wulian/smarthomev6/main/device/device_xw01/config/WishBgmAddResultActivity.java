package cc.wulian.smarthomev6.main.device.device_xw01.config;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.home.HomeActivity;
import cc.wulian.smarthomev6.support.core.apiunit.UserApiUnit;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;

/**
 * created by huxc  on 2018/5/24.
 * func： 添加结果界面
 * email: hxc242313@qq.com
 */

public class WishBgmAddResultActivity extends BaseTitleActivity implements View.OnClickListener {
    private Button btnAddResult;
    private ImageView ivAddResult;
    private TextView tvAddResult;

    private String result;
    private String deviceId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wish_bgm_result, true);
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
        skinManager.setBackground(btnAddResult, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(btnAddResult, SkinResouceKey.COLOR_BUTTON_TEXT);
    }

    @Override
    protected void initView() {
        super.initView();
        btnAddResult = (Button) findViewById(R.id.btn_add_result);
        ivAddResult = (ImageView) findViewById(R.id.iv_add_result);
        tvAddResult = (TextView) findViewById(R.id.tv_add_result);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        btnAddResult.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        result = getIntent().getStringExtra("result");
        deviceId = getIntent().getStringExtra("deviceId");
        updateViewByResult();

    }

    private void updateViewByResult() {
        if (TextUtils.equals(result, "success")) {
            ivAddResult.setImageResource(R.drawable.icon_config_wifi_success);
            tvAddResult.setText(R.string.addDevice_XW01_add_success);
        } else if (TextUtils.equals(result, "fail")) {
            ivAddResult.setImageResource(R.drawable.icon_config_wifi_fail);
            tvAddResult.setText(R.string.addDevice_XW01_add_failure);
            btnAddResult.setText(R.string.Common_Retry);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.btn_add_result) {
            if (TextUtils.equals(result, "success")) {
                UserApiUnit userApiUnit = new UserApiUnit(this);
                userApiUnit.getAllDevice(true,false);
                startActivity(new Intent(this, HomeActivity.class));
            } else if (TextUtils.equals(result, "fail")) {
                startActivity(new Intent(this, WishBgmAddGuideActivity.class));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(new DeviceReportEvent(null));
    }
}
