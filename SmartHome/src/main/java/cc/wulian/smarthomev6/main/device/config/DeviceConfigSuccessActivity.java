package cc.wulian.smarthomev6.main.device.config;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.ConfigWiFiInfoModel;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.home.HomeActivity;
import cc.wulian.smarthomev6.support.core.apiunit.ApiConstant;
import cc.wulian.smarthomev6.support.core.apiunit.ICamApiUnit;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;

/**
 * Created by hxc on 2017/5/8.
 * func:配置绑定成功界面
 */

public class DeviceConfigSuccessActivity extends BaseTitleActivity implements View.OnClickListener {
    private Button btnNextStep;
    private ConfigWiFiInfoModel configData;
    private TextView tvConfigWifiSuccess;
    private TextView tvConfigWifiSuccessTips;
    private String type;

    private ICamApiUnit iCamApiUnit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iCamApiUnit = new ICamApiUnit(this, ApiConstant.getUserID());
        setContentView(R.layout.activity_device_config_success, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(new DeviceReportEvent(null));
    }

    @Override
    protected void initData() {
        super.initData();
        configData = getIntent().getParcelableExtra("configData");
        type = getIntent().getStringExtra("type");
        if (TextUtils.equals(type, "camera_3")) {
            tvConfigWifiSuccess.setText(getString(R.string.Cateyemini_Adddevice_Successresult));
        }
    }

    @Override
    protected void initView() {
        super.initView();
        btnNextStep = (Button) findViewById(R.id.btn_next_step);
        tvConfigWifiSuccess = (TextView) findViewById(R.id.tv_config_wifi_success);
        tvConfigWifiSuccessTips = (TextView) findViewById(R.id.tv_config_wifi_success_tips);
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
        setToolBarTitle(getString(R.string.Config_Add_Success));
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        btnNextStep.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.btn_next_step) {
            EventBus.getDefault().post(new DeviceReportEvent(null));
            if (TextUtils.equals(type, "camera_3")) {
                startActivity(new Intent(this, HomeActivity.class));
            }else {
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResult(2);
        this.finish();
    }
}
