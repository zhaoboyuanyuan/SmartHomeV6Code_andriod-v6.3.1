package cc.wulian.smarthomev6.main.device.hisense.config;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.AddGateway06Activity;
import cc.wulian.smarthomev6.main.device.config.DeviceStartConfigActivity;
import cc.wulian.smarthomev6.main.device.gateway_mini.config.MiniGatewayActivity;
import cc.wulian.smarthomev6.main.device.gateway_wall.config.WallGatewayActivity;
import cc.wulian.smarthomev6.main.mine.gatewaycenter.GatewayBindActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceSupportV6Bean;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.ConstantsUtil;
import cc.wulian.smarthomev6.support.utils.DialogUtil;

/**
 * Created by hxc on 2018/1/4.
 * func:手动输入uid添加海信设备界面
 */

public class AddHisDevByUidActivity extends BaseTitleActivity {
    private Button btnNextStep;
    private EditText etUid;
    private String deviceId;
    private String hisType;
    private boolean hasBind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hisdev_by_uid, true);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Add_Device));
    }

    @Override
    protected void initView() {
        super.initView();
        btnNextStep = (Button) findViewById(R.id.btn_next_step);
        etUid = (EditText) findViewById(R.id.et_input_uid);
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(btnNextStep, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(btnNextStep, SkinResouceKey.COLOR_BUTTON_TEXT);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        btnNextStep.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        hisType = getIntent().getStringExtra("hisType");
        hasBind = getIntent().getBooleanExtra("hasBind", false);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.btn_next_step) {
            deviceId = etUid.getText().toString();
            startActivity(new Intent(this,
                    HisDevStartConfigActivity.class).putExtra("hisType", hisType).putExtra("deviceId", deviceId)
                    .putExtra("hasBind", hasBind));
            finish();
        }
    }
}
