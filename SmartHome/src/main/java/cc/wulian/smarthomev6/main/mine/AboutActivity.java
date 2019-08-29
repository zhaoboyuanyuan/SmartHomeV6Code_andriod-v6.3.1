package cc.wulian.smarthomev6.main.mine;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.support.utils.DrawableUtil;
import cc.wulian.smarthomev6.support.utils.LanguageUtil;
import cc.wulian.smarthomev6.support.utils.VersionUtil;

/**
 * Created by mamengchao on 2017/3/1 0001.
 * Tips:关于
 */
public class AboutActivity extends BaseTitleActivity {
    private RelativeLayout itemAboutIntroduction;
    private RelativeLayout itemAboutUs;
    private LinearLayout layoutOfficialAccount;
    private TextView tvAppVersion;
    private ImageView ivLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about, true);
    }

    @Override
    protected void initTitle() {
        setToolBarTitle(getString(R.string.Mine_About));
    }

    @Override
    protected void initView() {
        itemAboutIntroduction = (RelativeLayout) findViewById(R.id.item_about_us_introduction);
        itemAboutUs = (RelativeLayout) findViewById(R.id.item_about_us_about);
        layoutOfficialAccount = (LinearLayout) findViewById(R.id.layout_official_account);
        tvAppVersion = (TextView) findViewById(R.id.tv_app_version);
        ivLogo = (ImageView) findViewById(R.id.iv_logo);
        ivLogo.setImageBitmap(DrawableUtil.getRoundedCornerBitmap(DrawableUtil.drawableToBitmap(getResources().getDrawable(R.drawable.ic_launcher)), 20));
    }

    @Override
    protected void initListeners() {
        itemAboutIntroduction.setOnClickListener(this);
        itemAboutUs.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        tvAppVersion.setText("v"+VersionUtil.getVersionName(AboutActivity.this));
        layoutOfficialAccount.setVisibility(LanguageUtil.isAllChina() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.item_about_us_introduction:
                startActivity(new Intent(this, IntroductionActivity.class));
                break;
            case R.id.item_about_us_about:
                startActivity(new Intent(this, AboutUsActivity.class));
                break;
            default:
                break;
        }
    }

}