package cc.wulian.smarthomev6.main.device.device_if02.config;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.DialogUtil;
import cc.wulian.smarthomev6.support.utils.WifiUtil;

public class AddIF02DeviceActivity extends BaseTitleActivity implements View.OnClickListener {

    private ImageView ivIcon;
    private TextView tvTips;
    private Button btnNextStep;

    private String type;
    private boolean hasBind;
    private static final String PERMISSION_ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String PERMISSION_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private WLDialog dialog;

    public static void start(Context context, String type, boolean hasBind) {
        Intent it = new Intent();
        it.putExtra("type", type);
        it.putExtra("hasBind", hasBind);
        it.setClass(context, AddIF02DeviceActivity.class);
        context.startActivity(it);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_if01_device, true);
    }

    @Override
    public void setToolBarTitle(String title) {
        super.setToolBarTitle(title);
    }

    @Override
    protected void initView() {
        super.initView();
        ivIcon = (ImageView) findViewById(R.id.iv_if01_device);
        tvTips = (TextView) findViewById(R.id.tv_config_tips);
        btnNextStep = (Button) findViewById(R.id.btn_next_step);
    }

    @Override
    protected void initData() {
        super.initData();
        type = getIntent().getStringExtra("type");
        hasBind = getIntent().getBooleanExtra("hasBind", false);
        updateViewByType(type);
        checkPermission();

    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, PERMISSION_ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, PERMISSION_ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{PERMISSION_ACCESS_COARSE_LOCATION, PERMISSION_ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);

        }
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

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(btnNextStep, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(btnNextStep, SkinResouceKey.COLOR_BUTTON_TEXT);
    }

    private void updateViewByType(String type) {
        switch (type) {
            case "IF02"://WIFI红外转发器
                setToolBarTitle(getString(R.string.IF_009));
                ivIcon.setImageResource(R.drawable.device_if01_bg);
                tvTips.setText(getString(R.string.IF_002));
                break;
            case "GW14"://低成本网关
                setToolBarTitle(getString(R.string.Device_Default_Name_GW14));
                ivIcon.setImageResource(R.drawable.device_gw14_bg);
                tvTips.setText(getString(R.string.Adddevice_Gateway_GW14));
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
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    checkPermission();
                    return;
                } else {
                    if (!WifiUtil.isLocationEnabled(this)) {
                        dialog = DialogUtil.showCommonDialog(this, "",
                                getString(R.string.Get_WiFi_Location), getString(R.string.Open_Btn), getString(R.string.Cancel), new WLDialog.MessageListener() {
                                    @Override
                                    public void onClickPositive(View var1, String msg) {
                                        Intent intent = new Intent(
                                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        startActivityForResult(intent, 0);
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onClickNegative(View var1) {
                                        dialog.dismiss();
                                    }
                                });
                        dialog.show();
                        return;
                    }
                }
                startActivity(new Intent(this, IF02DevStartConfigActivity.class)
                        .putExtra("deviceType", type)
                        .putExtra("hasBind", hasBind));
                this.finish();
                break;
            case R.id.img_left:
//                startActivity(new Intent(this, AddDeviceActivity.class));
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        startActivity(new Intent(this,AddDeviceActivity.class));
        this.finish();
    }
}
