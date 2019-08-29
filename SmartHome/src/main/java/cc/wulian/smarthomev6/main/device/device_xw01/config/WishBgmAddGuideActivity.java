package cc.wulian.smarthomev6.main.device.device_xw01.config;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;

/**
 * created by huxc  on 2018/5/23.
 * func：添加向往背景音乐引导页
 * email: hxc242313@qq.com
 */

public class WishBgmAddGuideActivity extends BaseTitleActivity implements View.OnClickListener {
    private TextView tvAddTip;
    private Button btnNextStep;
    private String type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wish_bgm_guide,true);
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
        skinManager.setBackground(btnNextStep, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(btnNextStep, SkinResouceKey.COLOR_BUTTON_TEXT);
    }

    @Override
    protected void initView() {
        super.initView();
        tvAddTip = (TextView) findViewById(R.id.tv_add_tip);
        btnNextStep = (Button) findViewById(R.id.btn_next_step);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        btnNextStep.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        type = getIntent().getStringExtra("type");
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(v.getId() == R.id.btn_next_step){
            startActivity(new Intent(this,WishQRCodeActivity.class)
                    .putExtra("type",type)
                    .putExtra("flag",1));
        }
    }
}
