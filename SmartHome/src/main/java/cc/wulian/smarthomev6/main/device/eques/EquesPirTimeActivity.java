package cc.wulian.smarthomev6.main.device.eques;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.device.eques.bean.EquesPirInfoBean;

/**
 * 作者: chao
 * 时间: 2017/6/12
 * 描述: pir时间
 * 联系方式: 805901025@qq.com
 */

public class EquesPirTimeActivity extends BaseTitleActivity {

    private RelativeLayout rl_eques_setting_pir_1;
    private RelativeLayout rl_eques_setting_pir_3;
    private RelativeLayout rl_eques_setting_pir_5;
    private RelativeLayout rl_eques_setting_pir_10;
    private RelativeLayout rl_eques_setting_pir_15;
    private RelativeLayout rl_eques_setting_pir_20;
    private ImageView iv_eques_setting_pir_1;
    private ImageView iv_eques_setting_pir_3;
    private ImageView iv_eques_setting_pir_5;
    private ImageView iv_eques_setting_pir_10;
    private ImageView iv_eques_setting_pir_15;
    private ImageView iv_eques_setting_pir_20;

    private EquesPirInfoBean infoBean;
    private int time = 1;
    private List<ImageView> imageViews;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eques_pir_time, true);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Cateyemini_Humandetection_Sojourntime));
    }

    @Override
    protected void initView() {
        rl_eques_setting_pir_1 = (RelativeLayout) findViewById(R.id.rl_eques_setting_pir_1);
        rl_eques_setting_pir_3 = (RelativeLayout) findViewById(R.id.rl_eques_setting_pir_3);
        rl_eques_setting_pir_5 = (RelativeLayout) findViewById(R.id.rl_eques_setting_pir_5);
        rl_eques_setting_pir_10 = (RelativeLayout) findViewById(R.id.rl_eques_setting_pir_10);
        rl_eques_setting_pir_15 = (RelativeLayout) findViewById(R.id.rl_eques_setting_pir_15);
        rl_eques_setting_pir_20 = (RelativeLayout) findViewById(R.id.rl_eques_setting_pir_20);
        iv_eques_setting_pir_1 = (ImageView) findViewById(R.id.iv_eques_setting_pir_1);
        iv_eques_setting_pir_3 = (ImageView) findViewById(R.id.iv_eques_setting_pir_3);
        iv_eques_setting_pir_5 = (ImageView) findViewById(R.id.iv_eques_setting_pir_5);
        iv_eques_setting_pir_10 = (ImageView) findViewById(R.id.iv_eques_setting_pir_10);
        iv_eques_setting_pir_15 = (ImageView) findViewById(R.id.iv_eques_setting_pir_15);
        iv_eques_setting_pir_20 = (ImageView) findViewById(R.id.iv_eques_setting_pir_20);
    }

    @Override
    protected void initData() {
        infoBean = (EquesPirInfoBean) getIntent().getSerializableExtra("equesPirInfoBean");
        time = infoBean.sense_time;
        imageViews = new ArrayList<>();
        imageViews.add(iv_eques_setting_pir_1);
        imageViews.add(iv_eques_setting_pir_3);
        imageViews.add(iv_eques_setting_pir_5);
        imageViews.add(iv_eques_setting_pir_10);
        imageViews.add(iv_eques_setting_pir_15);
        imageViews.add(iv_eques_setting_pir_20);
        refereshView(time);
    }

    @Override
    protected void initListeners() {
        rl_eques_setting_pir_1.setOnClickListener(this);
        rl_eques_setting_pir_3.setOnClickListener(this);
        rl_eques_setting_pir_5.setOnClickListener(this);
        rl_eques_setting_pir_10.setOnClickListener(this);
        rl_eques_setting_pir_15.setOnClickListener(this);
        rl_eques_setting_pir_20.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.rl_eques_setting_pir_1:
                time = 1;
                break;
            case R.id.rl_eques_setting_pir_3:
                time = 3;
                break;
            case R.id.rl_eques_setting_pir_5:
                time = 5;
                break;
            case R.id.rl_eques_setting_pir_10:
                time = 10;
                break;
            case R.id.rl_eques_setting_pir_15:
                time = 15;
                break;
            case R.id.rl_eques_setting_pir_20:
                time = 20;
                break;
            default:
                break;
        }
        Intent intent = new Intent();
        intent.putExtra("time", time);
        setResult(2, intent);
        finish();
    }

    private void refereshView(int position){
        switch (position){
            case 1:
                iv_eques_setting_pir_1.setVisibility(View.VISIBLE);
                iv_eques_setting_pir_3.setVisibility(View.GONE);
                iv_eques_setting_pir_5.setVisibility(View.GONE);
                iv_eques_setting_pir_10.setVisibility(View.GONE);
                iv_eques_setting_pir_15.setVisibility(View.GONE);
                iv_eques_setting_pir_20.setVisibility(View.GONE);
                break;
            case 3:
                iv_eques_setting_pir_1.setVisibility(View.GONE);
                iv_eques_setting_pir_3.setVisibility(View.VISIBLE);
                iv_eques_setting_pir_5.setVisibility(View.GONE);
                iv_eques_setting_pir_10.setVisibility(View.GONE);
                iv_eques_setting_pir_15.setVisibility(View.GONE);
                iv_eques_setting_pir_20.setVisibility(View.GONE);
                break;
            case 5:
                iv_eques_setting_pir_1.setVisibility(View.GONE);
                iv_eques_setting_pir_3.setVisibility(View.GONE);
                iv_eques_setting_pir_5.setVisibility(View.VISIBLE);
                iv_eques_setting_pir_10.setVisibility(View.GONE);
                iv_eques_setting_pir_15.setVisibility(View.GONE);
                iv_eques_setting_pir_20.setVisibility(View.GONE);
                break;
            case 10:
                iv_eques_setting_pir_1.setVisibility(View.GONE);
                iv_eques_setting_pir_3.setVisibility(View.GONE);
                iv_eques_setting_pir_5.setVisibility(View.GONE);
                iv_eques_setting_pir_10.setVisibility(View.VISIBLE);
                iv_eques_setting_pir_15.setVisibility(View.GONE);
                iv_eques_setting_pir_20.setVisibility(View.GONE);
                break;
            case 15:
                iv_eques_setting_pir_1.setVisibility(View.GONE);
                iv_eques_setting_pir_3.setVisibility(View.GONE);
                iv_eques_setting_pir_5.setVisibility(View.GONE);
                iv_eques_setting_pir_10.setVisibility(View.GONE);
                iv_eques_setting_pir_15.setVisibility(View.VISIBLE);
                iv_eques_setting_pir_20.setVisibility(View.GONE);
                break;
            case 20:
                iv_eques_setting_pir_1.setVisibility(View.GONE);
                iv_eques_setting_pir_3.setVisibility(View.GONE);
                iv_eques_setting_pir_5.setVisibility(View.GONE);
                iv_eques_setting_pir_10.setVisibility(View.GONE);
                iv_eques_setting_pir_15.setVisibility(View.GONE);
                iv_eques_setting_pir_20.setVisibility(View.VISIBLE);
                break;
        }
    }
}
