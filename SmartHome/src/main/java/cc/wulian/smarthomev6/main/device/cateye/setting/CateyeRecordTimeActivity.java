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
 * Created by hxc on 2017/5/16.
 */

public class CateyeRecordTimeActivity extends BaseTitleActivity implements View.OnClickListener {
    private ImageView iv5sChecked;
    private RelativeLayout rl5s;
    private ImageView iv10sChecked;
    private RelativeLayout rl10s;
    private ImageView iv15sChecked;
    private RelativeLayout rl15s;
    private ImageView iv20sChecked;
    private RelativeLayout rl20s;

    private CateyeStatusEntity cateyeStatusEntity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cateye_record_time, true);
    }

    @Override
    protected void initTitle() {
        setToolBarTitle(getString(R.string.Stay_Detection_Check));
    }

    @Override
    protected void initView() {
        iv5sChecked = (ImageView) findViewById(R.id.iv_5s_checked);
        rl5s = (RelativeLayout) findViewById(R.id.rl_5s);
        iv10sChecked = (ImageView) findViewById(R.id.iv_10s_checked);
        rl10s = (RelativeLayout) findViewById(R.id.rl_10s);
        iv15sChecked = (ImageView) findViewById(R.id.iv_15s_checked);
        rl15s = (RelativeLayout) findViewById(R.id.rl_15s);
        iv20sChecked = (ImageView) findViewById(R.id.iv_20s_checked);
        rl20s = (RelativeLayout) findViewById(R.id.rl_20s);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        rl5s.setOnClickListener(this);
        rl10s.setOnClickListener(this);
        rl15s.setOnClickListener(this);
        rl20s.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        cateyeStatusEntity = (CateyeStatusEntity) getIntent().getSerializableExtra("CateyeStatusEntity");
        if (cateyeStatusEntity != null) {
            if (cateyeStatusEntity.getHoverDetectTime().equals("5")) {
                iv5sChecked.setVisibility(View.VISIBLE);
            } else if (cateyeStatusEntity.getHoverDetectTime().equals("10")) {
                iv10sChecked.setVisibility(View.VISIBLE);
            } else if (cateyeStatusEntity.getHoverDetectTime().equals("15")) {
                iv15sChecked.setVisibility(View.VISIBLE);
            } else if (cateyeStatusEntity.getHoverDetectTime().equals("20")) {
                iv20sChecked.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_5s:
                rl5s.setVisibility(View.VISIBLE);
                rl10s.setVisibility(View.GONE);
                rl15s.setVisibility(View.GONE);
                rl20s.setVisibility(View.GONE);
                cateyeStatusEntity.setHoverDetectTime("5");
                setResult(RESULT_OK, new Intent().putExtra("CateyeStatusEntity", cateyeStatusEntity));
                break;
            case R.id.rl_10s:
                rl5s.setVisibility(View.GONE);
                rl10s.setVisibility(View.VISIBLE);
                rl15s.setVisibility(View.GONE);
                rl20s.setVisibility(View.GONE);
                cateyeStatusEntity.setHoverDetectTime("10");
                setResult(RESULT_OK, new Intent().putExtra("CateyeStatusEntity", cateyeStatusEntity));
                break;
            case R.id.rl_15s:
                rl5s.setVisibility(View.GONE);
                rl10s.setVisibility(View.GONE);
                rl15s.setVisibility(View.VISIBLE);
                rl20s.setVisibility(View.GONE);
                cateyeStatusEntity.setHoverDetectTime("15");
                setResult(RESULT_OK, new Intent().putExtra("CateyeStatusEntity", cateyeStatusEntity));
                break;
            case R.id.rl_20s:
                rl5s.setVisibility(View.GONE);
                rl10s.setVisibility(View.GONE);
                rl15s.setVisibility(View.GONE);
                rl20s.setVisibility(View.VISIBLE);
                cateyeStatusEntity.setHoverDetectTime("20");
                setResult(RESULT_OK, new Intent().putExtra("CateyeStatusEntity", cateyeStatusEntity));
                break;
        }
        this.finish();
    }
}
