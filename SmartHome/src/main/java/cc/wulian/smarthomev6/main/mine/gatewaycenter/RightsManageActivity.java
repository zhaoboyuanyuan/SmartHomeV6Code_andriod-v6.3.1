package cc.wulian.smarthomev6.main.mine.gatewaycenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.ColorConfig;

/**
 * Created by Administrator on 2017/2/28 0028.
 */

public class RightsManageActivity extends BaseTitleActivity {
    private RadioGroup rgTitle;
    private FragmentTransaction ft;
    private BindAccountFragment bindAccountFragment;
    private AuthAccountFragment authAccountFragment;
    private RadioButton rbTabAuthAccount;
    private RadioButton rbTabBindAccount;
    private View rl_header;

    private Integer nav_color;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_gateway, true);
    }

    @Override
    protected void initTitle() {
        setToolBarTitle(R.string.GatewayCenter_BindingUsers);
        //初始化控件
        rl_header = findViewById(R.id.rl_header);
        rgTitle = (RadioGroup) findViewById(R.id.rg_switch_gateway_title);
        rbTabBindAccount = (RadioButton) findViewById(R.id.rb_tab_bind_gateway);
        rbTabAuthAccount = (RadioButton) findViewById(R.id.rb_tab_auth_gateway);
        rbTabBindAccount.setText(R.string.AuthManager_Bind);
        rbTabAuthAccount.setText(R.string.AuthManager_Authorize);
        //碎片管理者对象
        ft = getSupportFragmentManager().beginTransaction();
        //碎片执行者对象

        bindAccountFragment = new BindAccountFragment();
        authAccountFragment = new AuthAccountFragment();

        //第一页显示
        ft.add(R.id.fl_switch_gateway_content, authAccountFragment, authAccountFragment.getClass().getSimpleName()).hide(authAccountFragment);
        ft.add(R.id.fl_switch_gateway_content, bindAccountFragment, bindAccountFragment.getClass().getSimpleName()).show(bindAccountFragment);
        //提交事件
        ft.commit();
    }

    @Override
    protected void initView() {
        rbTabBindAccount.setOnClickListener(this);
        rbTabAuthAccount.setOnClickListener(this);
        rgTitle.setOnClickListener(this);
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(rl_header, SkinResouceKey.BITMAP_NAV_BOTTOM_BACKGROUND);
        nav_color = skinManager.getColor(SkinResouceKey.COLOR_NAV);
        if (nav_color != null) {
            rbTabBindAccount.setTextColor(nav_color);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void initData() {
        rgTitle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                switch (i) {
                    case R.id.rb_tab_bind_gateway:
                        if (nav_color != null) {
                            rbTabBindAccount.setTextColor(nav_color);
                        } else {
                            rbTabBindAccount.setTextColor(getResources().getColor(R.color.grey));
                        }
                        rbTabAuthAccount.setTextColor(getResources().getColor(R.color.white));
                        if(bindAccountFragment.isAdded()){
                            ft.show(bindAccountFragment).hide(authAccountFragment);
                        }else {
                            ft.add(R.id.fl_switch_gateway_content, bindAccountFragment, bindAccountFragment.getClass().getSimpleName());
                        }
                        break;
                    case R.id.rb_tab_auth_gateway:
                        if (nav_color != null) {
                            rbTabAuthAccount.setTextColor(nav_color);
                        } else {
                            rbTabAuthAccount.setTextColor(getResources().getColor(R.color.grey));
                        }
                        rbTabBindAccount.setTextColor(getResources().getColor(R.color.white));
                        if(authAccountFragment.isAdded()){
                            ft.show(authAccountFragment).hide(bindAccountFragment);
                        }else {
                            ft.add(R.id.fl_switch_gateway_content, authAccountFragment, authAccountFragment.getClass().getSimpleName());
                        }
                        break;
                }
                //必须要写 不写会造成 白板 没有东西
                ft.commit();
            }
        });
    }

    @Override
    protected void initListeners() {
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.img_right:
                Intent intent = new Intent();
                intent.setClass(this, AuthAccountActivity.class);
                startActivityForResult(intent, 1);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            authAccountFragment.onActivityResult(1 , RESULT_OK, null);
        }
    }
}
