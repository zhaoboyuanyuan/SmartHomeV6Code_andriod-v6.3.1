package cc.wulian.smarthomev6.main.device.hisense.config;

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
import cc.wulian.smarthomev6.support.tools.zxing.activity.HisQRCodeActivity;

/**
 * created by huxc  on 2018/1/2.
 * func： 海信设备加网首页
 * email: hxc242313@qq.com
 */

public class AddHisenseDeviceActivity extends BaseTitleActivity implements View.OnClickListener {
    private ImageView ivIcon;
    private TextView tvTips;
    private Button btnNextStep;

    private String type;
    private boolean hasBind;

    public static void start(Context context, String type,boolean hasBind) {
        Intent it = new Intent();
        it.putExtra("type", type);
        it.putExtra("hasBind", hasBind);
        it.setClass(context, AddHisenseDeviceActivity.class);
        context.startActivity(it);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hisense_device, true);
    }

    @Override
    public void setToolBarTitle(String title) {
        super.setToolBarTitle(title);
    }

    @Override
    protected void initView() {
        super.initView();
        ivIcon = (ImageView) findViewById(R.id.iv_hisense_device);
        tvTips = (TextView) findViewById(R.id.tv_config_tips);
        btnNextStep = (Button) findViewById(R.id.btn_next_step);
    }

    @Override
    protected void initData() {
        super.initData();
        type = getIntent().getStringExtra("type");
        hasBind = getIntent().getBooleanExtra("hasBind",false);
        updateViewByType(type);

    }

    @Override
    protected void initTitle() {
        super.initTitle();
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        btnNextStep.setOnClickListener(this);
    }

    private void updateViewByType(String type) {
        switch (type) {
            case "HS01"://家用空调
                ivIcon.setImageResource(R.drawable.device_hs01_bg);
                tvTips.setText("连续按6次遥控器的“左右风”按钮，进入WiFi配置模式");
                break;
            case "HS02"://中央空调
                ivIcon.setImageResource(R.drawable.device_hs02_bg);
                tvTips.setText("上电后即可进入WiFi配置模式");
                break;
            case "HS03"://油烟机
                ivIcon.setImageResource(R.drawable.device_hs03_bg);
                tvTips.setText("长按吸油烟机“WiFi按钮”至灯光闪烁状态，进入WiFi配置模式");
                mTitle.setText("添加油烟机");
                break;
            case "HS04"://燃气灶
                ivIcon.setImageResource(R.drawable.device_hs04_bg);
                tvTips.setText("长按吸燃气灶“WiFi按钮”至灯光闪烁状态，进入WiFi配置模式");
                break;
            case "HS05"://冰箱
                mTitle.setText(getString(R.string.device_HS05_title));
                ivIcon.setImageResource(R.drawable.device_hs05_bg);
                tvTips.setText(getString(R.string.device_HS05_describe));
                break;
            case "HS06"://洗衣机
                ivIcon.setImageResource(R.drawable.device_hs06_bg);
                tvTips.setText("将旋钮旋至“单脱水”程序，长按“温度”键2秒，显示屏图标闪烁，进入WiFi配置模式。");
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_next_step:
                startActivity(new Intent(this, HisQRCodeActivity.class)
                .putExtra("hisType",type)
                .putExtra("hasBind",hasBind));
                this.finish();
                break;
            default:
                break;
        }
    }
}