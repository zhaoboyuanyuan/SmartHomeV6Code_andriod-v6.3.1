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
 * Func:猫眼人体设置记录类型界面
 */

public class CateyeRecordTypeActivity extends BaseTitleActivity implements View.OnClickListener {
    private ImageView ivLinkageSnapshot;
    private RelativeLayout rlLinkageSnapshot;
    private ImageView ivLinkageVideo;
    private RelativeLayout rlLinkageVideo;

    private CateyeStatusEntity cateyeStatusEntity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cateye_record_type, true);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Record_Type));
    }

    @Override
    protected void initView() {
        ivLinkageSnapshot = (ImageView) findViewById(R.id.iv_linkage_snapshot);
        rlLinkageSnapshot = (RelativeLayout) findViewById(R.id.rl_linkage_snapshot);
        ivLinkageVideo = (ImageView) findViewById(R.id.iv_linkage_video);
        rlLinkageVideo = (RelativeLayout) findViewById(R.id.rl_linkage_video);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        rlLinkageSnapshot.setOnClickListener(this);
        rlLinkageVideo.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        cateyeStatusEntity = (CateyeStatusEntity) getIntent().getSerializableExtra("CateyeStatusEntity");
        if (cateyeStatusEntity.getHoverProcMode().equals("0")) {
            ivLinkageSnapshot.setVisibility(View.VISIBLE);
            ivLinkageVideo.setVisibility(View.GONE);
        } else if (cateyeStatusEntity.getHoverProcMode().equals("1")) {
            ivLinkageSnapshot.setVisibility(View.GONE);
            ivLinkageVideo.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.rl_linkage_snapshot:
                ivLinkageVideo.setVisibility(View.GONE);
                ivLinkageSnapshot.setVisibility(View.VISIBLE);
                cateyeStatusEntity.setHoverProcMode("0");
                setResult(RESULT_OK, new Intent().putExtra("CateyeStatusEntity", cateyeStatusEntity));
                break;
            case R.id.rl_linkage_video:
                ivLinkageVideo.setVisibility(View.VISIBLE);
                ivLinkageSnapshot.setVisibility(View.GONE);
                cateyeStatusEntity.setHoverProcMode("1");
                setResult(RESULT_OK, new Intent().putExtra("CateyeStatusEntity", cateyeStatusEntity));
                break;
        }
        this.finish();

    }
}
