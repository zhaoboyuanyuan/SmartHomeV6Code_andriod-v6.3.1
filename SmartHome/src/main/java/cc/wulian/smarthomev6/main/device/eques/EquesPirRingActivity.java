package cc.wulian.smarthomev6.main.device.eques;

import android.content.Intent;
import android.media.MediaPlayer;
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
 * 描述: pir铃声
 * 联系方式: 805901025@qq.com
 */

public class EquesPirRingActivity extends BaseTitleActivity {

    private RelativeLayout rl_eques_setting_pir_ns;
    private RelativeLayout rl_eques_setting_pir_dd;
    private RelativeLayout rl_eques_setting_pir_bj;
    private RelativeLayout rl_eques_setting_pir_jx;
    private RelativeLayout rl_eques_setting_pir_jy;
    private ImageView iv_eques_setting_pir_ns;
    private ImageView iv_eques_setting_pir_dd;
    private ImageView iv_eques_setting_pir_bj;
    private ImageView iv_eques_setting_pir_jx;
    private ImageView iv_eques_setting_pir_jy;

    private MediaPlayer mediaPlayer;
    private EquesPirInfoBean infoBean;
    private int position = 0;
    private List<ImageView> imageViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eques_pir_ring, true);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Cateyemini_Humandetection_Ringingtone));
    }

    @Override
    protected void initView() {
        rl_eques_setting_pir_ns = (RelativeLayout) findViewById(R.id.rl_eques_setting_pir_ns);
        rl_eques_setting_pir_dd = (RelativeLayout) findViewById(R.id.rl_eques_setting_pir_dd);
        rl_eques_setting_pir_bj = (RelativeLayout) findViewById(R.id.rl_eques_setting_pir_bj);
        rl_eques_setting_pir_jx = (RelativeLayout) findViewById(R.id.rl_eques_setting_pir_jx);
        rl_eques_setting_pir_jy = (RelativeLayout) findViewById(R.id.rl_eques_setting_pir_jy);
        iv_eques_setting_pir_ns = (ImageView) findViewById(R.id.iv_eques_setting_pir_ns);
        iv_eques_setting_pir_dd = (ImageView) findViewById(R.id.iv_eques_setting_pir_dd);
        iv_eques_setting_pir_bj = (ImageView) findViewById(R.id.iv_eques_setting_pir_bj);
        iv_eques_setting_pir_jx = (ImageView) findViewById(R.id.iv_eques_setting_pir_jx);
        iv_eques_setting_pir_jy = (ImageView) findViewById(R.id.iv_eques_setting_pir_jy);
    }

    @Override
    protected void initData() {
        infoBean = (EquesPirInfoBean) getIntent().getSerializableExtra("equesPirInfoBean");
        mediaPlayer = new MediaPlayer();
        position = infoBean.ringtone;
        imageViews = new ArrayList<>();
        imageViews.add(iv_eques_setting_pir_ns);
        imageViews.add(iv_eques_setting_pir_dd);
        imageViews.add(iv_eques_setting_pir_bj);
        imageViews.add(iv_eques_setting_pir_jx);
        imageViews.add(iv_eques_setting_pir_jy);
        refereshView(position);
    }

    @Override
    protected void initListeners() {
        rl_eques_setting_pir_ns.setOnClickListener(this);
        rl_eques_setting_pir_dd.setOnClickListener(this);
        rl_eques_setting_pir_bj.setOnClickListener(this);
        rl_eques_setting_pir_jx.setOnClickListener(this);
        rl_eques_setting_pir_jy.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.rl_eques_setting_pir_ns:
                position = 1;
                play(R.raw.alarm_ns);
                break;
            case R.id.rl_eques_setting_pir_dd:
                position = 2;
                play(R.raw.alarm_dd);
                break;
            case R.id.rl_eques_setting_pir_bj:
                position = 3;
                play(R.raw.alarm_bj);
                break;
            case R.id.rl_eques_setting_pir_jx:
                position = 4;
                play(R.raw.alarm_jx);
                break;
            case R.id.rl_eques_setting_pir_jy:
                position = 5;
                Intent intent = new Intent();
                intent.putExtra("ring", position);
                setResult(4, intent);
                finish();
                break;
            default:
                break;
        }
    }

    private void refereshView(int position){
        for (int i = 0; i< imageViews.size(); i++){
            imageViews.get(i).setVisibility(View.GONE);
            if ((i+ 1) == position){
                imageViews.get(i).setVisibility(View.VISIBLE);
            }
        }
    }

    private void play(int res){
        refereshView(position);
        try{
            mediaPlayer.reset();
            mediaPlayer=MediaPlayer.create(EquesPirRingActivity.this, res);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Intent intent = new Intent();
                    intent.putExtra("ring", position);
                    setResult(4, intent);
                    finish();
                }
            });
            mediaPlayer.start();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
