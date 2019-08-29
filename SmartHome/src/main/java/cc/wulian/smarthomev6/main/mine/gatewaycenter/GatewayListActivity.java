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
 * Created by syf on 2017/2/17
 */
public class GatewayListActivity extends BaseTitleActivity {

    private static final int BIND = 0;
    private static final int CONFIRM = 1;

    private RadioGroup rgTitle;
    private FragmentTransaction ft;
    private BindGatewayFragment bindGatewayFragment;
    private AuthGatewayFragment authGatewayFragment;
    private RadioButton rbTabAuthGateway;
    private RadioButton rbTabBindGateway;
    private ImageView ivLeft;
    private ImageView ivRight;
    private View rl_header;

    private Integer nav_color;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_gateway, true);
    }

    @Override
    protected void initTitle() {
        setToolBarTitleAndRightImg(getString(R.string.GatewayCenter_GatewayList), R.drawable.icon_add);
        rl_header = findViewById(R.id.rl_header);
        ivLeft = (ImageView) findViewById(R.id.img_left);
        ivRight = (ImageView) findViewById(R.id.img_right);
        //初始化控件
        rgTitle = (RadioGroup) findViewById(R.id.rg_switch_gateway_title);
        rbTabBindGateway = (RadioButton) findViewById(R.id.rb_tab_bind_gateway);
        rbTabAuthGateway = (RadioButton) findViewById(R.id.rb_tab_auth_gateway);
        //碎片管理者对象
        ft = getSupportFragmentManager().beginTransaction();
        //碎片执行者对象

        bindGatewayFragment = new BindGatewayFragment();
        authGatewayFragment = new AuthGatewayFragment();

        //第一页显示
        ft.add(R.id.fl_switch_gateway_content, bindGatewayFragment, bindGatewayFragment.getClass().getSimpleName()).commit();
    }

    @Override
    protected void initView() {
        rbTabBindGateway.setOnClickListener(this);
        rbTabAuthGateway.setOnClickListener(this);
        rgTitle.setOnClickListener(this);
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(rl_header, SkinResouceKey.BITMAP_NAV_BOTTOM_BACKGROUND);
        nav_color = skinManager.getColor(SkinResouceKey.COLOR_NAV);
        if (nav_color != null) {
            rbTabBindGateway.setTextColor(nav_color);
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
                            rbTabBindGateway.setTextColor(nav_color);
                        } else {
                            rbTabBindGateway.setTextColor(getResources().getColor(R.color.grey));
                        }
                        rbTabAuthGateway.setTextColor(getResources().getColor(R.color.white));
                        if (bindGatewayFragment.isAdded()) {
                            ft.show(bindGatewayFragment).hide(authGatewayFragment);
                        } else {
                            ft.add(R.id.fl_switch_gateway_content, bindGatewayFragment, bindGatewayFragment.getClass().getSimpleName());
                        }
                        break;
                    case R.id.rb_tab_auth_gateway:
                        if (nav_color != null) {
                            rbTabAuthGateway.setTextColor(nav_color);
                        } else {
                            rbTabAuthGateway.setTextColor(getResources().getColor(R.color.grey));
                        }
                        rbTabBindGateway.setTextColor(getResources().getColor(R.color.white));
                        if (authGatewayFragment.isAdded()) {
                            ft.show(authGatewayFragment).hide(bindGatewayFragment);
                        } else {
                            ft.add(R.id.fl_switch_gateway_content, authGatewayFragment, authGatewayFragment.getClass().getSimpleName());
                        }
                        break;
                }
                ft.commit();
            }
        });
    }

    @Override
    protected void initListeners() {
        ivLeft.setOnClickListener(this);
        ivRight.setOnClickListener(this);
    }

    @Override
    public void onClickView(View v) {
//        super.onClick(v);
        switch (v.getId()) {
            case R.id.img_right:
                Intent intent = new Intent();
                intent.setClass(this, GatewayBindActivity.class);
                intent.putExtra("changePwd", true);
                startActivityForResult(intent, BIND);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            bindGatewayFragment.onActivityResult(requestCode, RESULT_OK, data);
        }
    }
}
