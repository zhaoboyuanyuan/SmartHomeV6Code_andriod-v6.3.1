package cc.wulian.smarthomev6.main.device.config;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.ConfigWiFiInfoModel;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.eques.EquesAddGuideActivity;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.DialogUtil;

/**
 * Created by hxc on 2017/5/8.
 * func:配置绑定失败界面
 */

public class DeviceConfigFailActivity extends BaseTitleActivity implements View.OnClickListener {

    private Button btnNextStep;
    private ConfigWiFiInfoModel configData;
    private TextView tvMoreSolution;
    private TextView failTextView;
    private String type;
    private Dialog tipDialog;
    private String dialogTitle;
    private String dialogMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_config_fail,true);

    }

    @Override
    protected void initData() {
        super.initData();
        configData = getIntent().getParcelableExtra("configData");
        type = getIntent().getStringExtra("type");
        if (TextUtils.equals(type, "camera_3")){
            failTextView.setText(getString(R.string.Cateyemini_Adddevice_Failureresult));
            dialogMessage = getString(R.string.Cateyemini_Adddevice_Resolvent);
        }
    }

    @Override
    protected void initView() {
        super.initView();
        btnNextStep = (Button) findViewById(R.id.btn_retry);
        tvMoreSolution = (TextView) findViewById(R.id.tv_more_solutions);
        failTextView = (TextView) findViewById(R.id.add_fail_tips);
        tvMoreSolution.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(btnNextStep, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(btnNextStep, SkinResouceKey.COLOR_BUTTON_TEXT);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Config_Add_Fail));
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        btnNextStep.setOnClickListener(this);
        tvMoreSolution.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if(id == R.id.btn_retry){
            if (TextUtils.equals(type, "camera_3")){
                startActivity(new Intent(this,EquesAddGuideActivity.class));
            }
            this.finish();
        }else if(id == R.id.tv_more_solutions){
            showMoreSolutionDialog();
        }
    }

    private void showMoreSolutionDialog(){
        tipDialog = DialogUtil.showCommonTipDialog(DeviceConfigFailActivity.this, false, getString(R.string.More_Solution), dialogMessage,
                getResources().getString(R.string.Sure), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tipDialog.dismiss();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        setResult(2);
        finish();
    }
}
