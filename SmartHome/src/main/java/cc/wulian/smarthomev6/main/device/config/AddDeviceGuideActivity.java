package cc.wulian.smarthomev6.main.device.config;

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
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.AddDeviceActivity;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.tools.zxing.activity.QRCodeActivity;
import cc.wulian.smarthomev6.support.utils.ConstantsUtil;

/**
 * Created by hxc on 2017/5/5.
 * func:添加摄像机设备引导页
 */

public class AddDeviceGuideActivity extends BaseTitleActivity implements View.OnClickListener {
    private Button btnNextStep;
    private String scanType;
    private String cameraType;
    private ImageView ivIcon;
    private TextView tvConfigTips;
    private TextView tvCameraGuideTips;

    public static void start(Context context, String cameraType) {
        Intent it = new Intent();
        it.putExtra("scanType", ConstantsUtil.CAMERA_ADD_ENTRY);
        it.putExtra("cameraType", cameraType);
        it.setClass(context, AddDeviceGuideActivity.class);
        context.startActivity(it);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cateye_guide, true);
    }

    @Override
    protected void initView() {
        super.initView();
        btnNextStep = (Button) findViewById(R.id.btn_next_step);
        ivIcon = (ImageView) findViewById(R.id.iv_camera_guide_icon);
        tvConfigTips = (TextView) findViewById(R.id.tv_config_tips);
        tvCameraGuideTips = (TextView) findViewById(R.id.tv_camera_guide_tips);
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(btnNextStep, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(btnNextStep, SkinResouceKey.COLOR_BUTTON_TEXT);
    }

    @Override
    protected void initData() {
        super.initData();
        scanType = getIntent().getStringExtra("scanType");
        cameraType = getIntent().getStringExtra("cameraType");
        updateViewByCameraType();
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        btnNextStep.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_next_step:
                startActivity(new Intent(this, QRCodeActivity.class).putExtra("scanType", scanType));
                this.finish();
                break;
            case R.id.img_left:
                startActivity(new Intent(this, AddDeviceActivity.class));
                finish();
                break;
        }
    }

    private void updateViewByCameraType() {
        if (!TextUtils.isEmpty(cameraType) && TextUtils.equals(cameraType, "CMICA1")) {
            setToolBarTitle(getResources().getString(R.string.Title_Add_Guide));
            ivIcon.setImageResource(R.drawable.add_cateye_guide);
            btnNextStep.setText(getResources().getString(R.string.Title_Add_Guide));
        } else if (!TextUtils.isEmpty(cameraType) && TextUtils.equals(cameraType, "CMICA2")) {
            setToolBarTitle(getResources().getString(R.string.Add_Lookever));
            ivIcon.setImageResource(R.drawable.icon_camera_2);
            tvConfigTips.setVisibility(View.INVISIBLE);
            tvCameraGuideTips.setText(getResources().getString(R.string.Transitional_Page_Explain));
            btnNextStep.setText(getResources().getString(R.string.Add_Lookever));
        }else if(!TextUtils.isEmpty(cameraType) && TextUtils.equals(cameraType, "CMICA3")){
            setToolBarTitle(getResources().getString(R.string.Add_Penguin_Title));
            ivIcon.setImageResource(R.drawable.icon_camera_3);
            tvConfigTips.setVisibility(View.INVISIBLE);
            tvCameraGuideTips.setText(getResources().getString(R.string.Transitional_Page_Explain));
            btnNextStep.setText(getResources().getString(R.string.Add_Penguin_Title));
        }else if(!TextUtils.isEmpty(cameraType) && TextUtils.equals(cameraType, "CMICA4")){
            setToolBarTitle(getString(R.string.Add_Cylincam));
            ivIcon.setImageResource(R.drawable.icon_camera_4);
            tvConfigTips.setVisibility(View.INVISIBLE);
            tvCameraGuideTips.setText(getResources().getString(R.string.Transitional_Page_Explain));
            btnNextStep.setText(getString(R.string.Add_Cylincam));
        } else if(!TextUtils.isEmpty(cameraType) && TextUtils.equals(cameraType, "CMICA5")){
            setToolBarTitle(getString(R.string.Add_Outdoor_Camera));
            ivIcon.setImageResource(R.drawable.icon_camera_5);
            tvConfigTips.setVisibility(View.INVISIBLE);
            tvCameraGuideTips.setText(getResources().getString(R.string.Transitional_Page_Explain));
            btnNextStep.setText(getString(R.string.Add_Outdoor_Camera));
        }  else if(!TextUtils.isEmpty(cameraType) && TextUtils.equals(cameraType, "CMICA6")){
            setToolBarTitle(getResources().getString(R.string.Add_Penguin_Title));
            ivIcon.setImageResource(R.drawable.icon_camera_3);
            tvConfigTips.setVisibility(View.INVISIBLE);
            tvCameraGuideTips.setText(getResources().getString(R.string.Transitional_Page_Explain));
            btnNextStep.setText(getResources().getString(R.string.Add_Penguin_Title));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,AddDeviceActivity.class));
    }
}
