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
 * 描述: pir灵敏度
 * 联系方式: 805901025@qq.com
 */

public class EquesPirSensibilityActivity extends BaseTitleActivity {

    private RelativeLayout rl_eques_setting_pir_low;
    private RelativeLayout rl_eques_setting_pir_high;
    private ImageView iv_eques_setting_pir_low;
    private ImageView iv_eques_setting_pir_high;

    private EquesPirInfoBean infoBean;
    private int sensibility = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eques_pir_sensibility, true);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Cateye_Sensitive_Setting));
    }

    @Override
    protected void initView() {
        rl_eques_setting_pir_low = (RelativeLayout) findViewById(R.id.rl_eques_setting_pir_low);
        rl_eques_setting_pir_high = (RelativeLayout) findViewById(R.id.rl_eques_setting_pir_high);
        iv_eques_setting_pir_low = (ImageView) findViewById(R.id.iv_eques_setting_pir_low);
        iv_eques_setting_pir_high = (ImageView) findViewById(R.id.iv_eques_setting_pir_high);
    }

    @Override
    protected void initData() {
        infoBean = (EquesPirInfoBean) getIntent().getSerializableExtra("equesPirInfoBean");
        sensibility = infoBean.sense_sensitivity;
        refereshView(sensibility);
    }

    @Override
    protected void initListeners() {
        rl_eques_setting_pir_low.setOnClickListener(this);
        rl_eques_setting_pir_high.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_eques_setting_pir_low:
                sensibility = 2;
                break;
            case R.id.rl_eques_setting_pir_high:
                sensibility = 1;
                break;
            default:
                break;
        }
        Intent intent = new Intent();
        intent.putExtra("sensibility", sensibility);
        setResult(1, intent);
        finish();
    }

    private void refereshView(int sensibility) {
        if (sensibility == 1){
            iv_eques_setting_pir_low.setVisibility(View.GONE);
            iv_eques_setting_pir_high.setVisibility(View.VISIBLE);
        }else if (sensibility == 2){
            iv_eques_setting_pir_low.setVisibility(View.VISIBLE);
            iv_eques_setting_pir_high.setVisibility(View.GONE);
        }
    }
}
