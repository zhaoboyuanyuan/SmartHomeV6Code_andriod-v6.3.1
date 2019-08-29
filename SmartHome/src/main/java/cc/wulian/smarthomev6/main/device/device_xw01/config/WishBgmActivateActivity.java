package cc.wulian.smarthomev6.main.device.device_xw01.config;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;

/**
 * created by huxc  on 2018/5/24.
 * func：向往背景音乐激活界面
 * email: hxc242313@qq.com
 */

public class WishBgmActivateActivity extends BaseTitleActivity implements View.OnClickListener {
    private Button btnScanActivate;
    private String type;
    private String deviceId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_wish_bmg, true);
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
        skinManager.setBackground(btnScanActivate, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(btnScanActivate, SkinResouceKey.COLOR_BUTTON_TEXT);
    }

    @Override
    protected void initView() {
        super.initView();
        btnScanActivate = (Button) findViewById(R.id.btn_scan_activate);
    }


    @Override
    protected void initListeners() {
        super.initListeners();
        btnScanActivate.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        type = getIntent().getStringExtra("type");
        deviceId = getIntent().getStringExtra("deviceId");
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.btn_scan_activate) {
            startActivity(new Intent(this, WishQRCodeActivity.class)
                    .putExtra("type", type)
                    .putExtra("deviceId", deviceId)
                    .putExtra("flag", 2));

        }
    }
}