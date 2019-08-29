package cc.wulian.smarthomev6.main.device.cateye.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.CateyeStatusEntity;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;

/**
 * Created by hxc on 2017/5/12.
 * 智能猫眼灵敏度设置界面
 */

public class CateyeSensitivityActivity extends BaseTitleActivity implements View.OnClickListener {
    private ImageView ivSensitiveLow;
    private RelativeLayout rlSensitiveLow;
    private ImageView ivSensitiveHigh;
    private RelativeLayout rlSensitiveHigh;
    private CateyeStatusEntity cateyeStatusEntity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cateye_sensitivity, true);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Cateye_Sensitive_Setting));
    }

    @Override
    protected void initView() {
        ivSensitiveLow = (ImageView) findViewById(R.id.iv_sensitive_low);
        rlSensitiveLow = (RelativeLayout) findViewById(R.id.rl_sensitive_low);
        ivSensitiveHigh = (ImageView) findViewById(R.id.iv_sensitive_high);
        rlSensitiveHigh = (RelativeLayout) findViewById(R.id.rl_sensitive_high);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        rlSensitiveHigh.setOnClickListener(this);
        rlSensitiveLow.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        cateyeStatusEntity = (CateyeStatusEntity) getIntent().getSerializableExtra("CateyeStatusEntity");
        if (cateyeStatusEntity.getPIRDetectLevel().equals("1")) {
            ivSensitiveLow.setVisibility(View.VISIBLE);
            ivSensitiveHigh.setVisibility(View.GONE);
        } else if (cateyeStatusEntity.getPIRDetectLevel().equals("0")) {
            ivSensitiveLow.setVisibility(View.GONE);
            ivSensitiveHigh.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_sensitive_low:
                ivSensitiveLow.setVisibility(View.VISIBLE);
                ivSensitiveHigh.setVisibility(View.GONE);
                cateyeStatusEntity.setPIRDetectLevel("1");
                setResult(RESULT_OK, new Intent().putExtra("CateyeStatusEntity", cateyeStatusEntity));
                break;
            case R.id.rl_sensitive_high:
                ivSensitiveLow.setVisibility(View.GONE);
                ivSensitiveHigh.setVisibility(View.VISIBLE);
                cateyeStatusEntity.setPIRDetectLevel("0");
                setResult(RESULT_OK, new Intent().putExtra("CateyeStatusEntity", cateyeStatusEntity));
                break;
        }
        this.finish();
    }
}
