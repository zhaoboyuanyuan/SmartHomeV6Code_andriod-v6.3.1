package cc.wulian.smarthomev6.main.device.eques;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesPirInfoBean;

/**
 * 作者: chao
 * 时间: 2017/6/12
 * 描述: pir类型
 * 联系方式: 805901025@qq.com
 */

public class EquesPirTypeActivity extends BaseTitleActivity {

    private RelativeLayout rl_eques_setting_pir_snapshot;
    private RelativeLayout rl_eques_setting_pir_video;
    private ImageView iv_eques_setting_pir_snapshot;
    private ImageView iv_eques_setting_pir_video;

    private EquesPirInfoBean infoBean;
    private int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eques_pir_type, true);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Device_More_AlarmSetting_BodyDetectRecordType));
    }

    @Override
    protected void initView() {
        rl_eques_setting_pir_snapshot = (RelativeLayout) findViewById(R.id.rl_eques_setting_pir_snapshot);
        rl_eques_setting_pir_video = (RelativeLayout) findViewById(R.id.rl_eques_setting_pir_video);
        iv_eques_setting_pir_snapshot = (ImageView) findViewById(R.id.iv_eques_setting_pir_snapshot);
        iv_eques_setting_pir_video = (ImageView) findViewById(R.id.iv_eques_setting_pir_video);
    }

    @Override
    protected void initData() {
        infoBean = (EquesPirInfoBean) getIntent().getSerializableExtra("equesPirInfoBean");
        type = infoBean.format;
        refereshView(type);
    }

    @Override
    protected void initListeners() {
        rl_eques_setting_pir_snapshot.setOnClickListener(this);
        rl_eques_setting_pir_video.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_eques_setting_pir_snapshot:
                type = 0;
                break;
            case R.id.rl_eques_setting_pir_video:
                type = 1;
                break;
            default:
                break;
        }
        Intent intent = new Intent();
        intent.putExtra("type", type);
        setResult(3, intent);
        finish();
    }

    private void refereshView(int sensibility) {
        if (sensibility == 1) {
            iv_eques_setting_pir_snapshot.setVisibility(View.GONE);
            iv_eques_setting_pir_video.setVisibility(View.VISIBLE);
        } else if (sensibility == 0) {
            iv_eques_setting_pir_snapshot.setVisibility(View.VISIBLE);
            iv_eques_setting_pir_video.setVisibility(View.GONE);
        }
    }
}