package cc.wulian.smarthomev6.main.device.device_CG27.config;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.ConfigWiFiInfoModel;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.config.DeviceWelcomeFragment;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.ConstantsUtil;

public class DeviceAlreadyBindActivity extends BaseTitleActivity implements View.OnClickListener {
    private Button btnNextStep;
    private ImageView ivCameraIcon;
    private TextView tvBindGatewayTips;
    private TextView tvBindShow;
    private TextView tvBindTips;
    private String username;
    private int boundRelation;
    private String deviceId;
    private Context context;
    private DataApiUnit dataApiUnit;

    public static void start(Context context, String deviceId, String username, int boundRelation) {
        Intent it = new Intent(context, DeviceAlreadyBindActivity.class);
        it.putExtra("deviceId", deviceId);
        it.putExtra("username", username);
        it.putExtra("boundRelation", boundRelation);
        context.startActivity(it);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_already_bind, true);
    }

    @Override
    protected void initView() {
        super.initView();
        btnNextStep = (Button) findViewById(R.id.btn_next_step);
        ivCameraIcon = (ImageView) findViewById(R.id.camera_icon);
        tvBindGatewayTips = (TextView) findViewById(R.id.tv_bind_gateway_tips);
        tvBindShow = (TextView) findViewById(R.id.tv_bind_show);
        tvBindTips = (TextView) findViewById(R.id.tv_bind_tips);
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
        boundRelation = getIntent().getIntExtra("boundRelation", -1);
        deviceId = getIntent().getStringExtra("deviceId");
        username = getIntent().getStringExtra("username");
        updateView();
    }

    private void updateView() {
        if (boundRelation == 2) {
            tvBindShow.setText(getString(R.string.Config_Device_Already_In_List));
        } else {
            tvBindShow.setText(getString(R.string.Tips1_Already_Bind) + username + getString(R.string.BindGateway_Bind));
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_next_step:
                break;
        }
    }
}
