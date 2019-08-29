package cc.wulian.smarthomev6.main.device.device_CG27.config;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;

public class AddCG27GuideActivity extends BaseTitleActivity {
    private Button btnNextStep;
    private ImageView ivIcon;
    private TextView tvStep1;
    private String type;


    public static void start(Context context, String type) {
        Intent it = new Intent();
        it.putExtra("type", type);
        it.setClass(context, AddCG27GuideActivity.class);
        context.startActivity(it);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_acs_guide, true);
    }

    @Override
    protected void initView() {
        super.initView();
        ivIcon = (ImageView) findViewById(R.id.iv_device_img);
        btnNextStep = (Button) findViewById(R.id.btn_next_step);
        tvStep1 = (TextView) findViewById(R.id.tv_step1);
    }

    @Override
    protected void updateSkin() {

        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(btnNextStep, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(btnNextStep, SkinResouceKey.COLOR_BUTTON_TEXT);
    }

    @Override
    protected void initData() {
        super.initData();
        type = getIntent().getStringExtra("type");
    }


    @Override
    protected void initListeners() {
        super.initListeners();
        btnNextStep.setOnClickListener(this);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(R.string.Add_Device);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_next_step:
                startActivity(new Intent(this, AddCG27DetailActivity.class));
                finish();
                break;
            case R.id.img_left:
                finish();
                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
