package cc.wulian.smarthomev6.main.device.camera_lc.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.LcDeviceInfoBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by hxc on 2017/5/12.
 * 随便看详情界面
 */

public class LcInformationActivity extends BaseTitleActivity implements View.OnClickListener {
    private TextView tvDeviceType;
    private TextView tvDeviceNumber;
    private TextView tvFirmwareVersion;
    private ImageView ivBg;
    private Button btnUpgrade;
    private Device device;


    private boolean canBeUpdate = false;
    private String deviceId;

    public static void start(Context context, String deviceId) {
        Intent it = new Intent(context, LcInformationActivity.class);
        it.putExtra("deviceId", deviceId);
        context.startActivity(it);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_lc_information, true);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Device_Detail));
    }

    @Override
    protected void initView() {
        super.initView();
        tvDeviceType = (TextView) findViewById(R.id.tv_device_type);
        tvDeviceNumber = (TextView) findViewById(R.id.tv_device_number);
        tvFirmwareVersion = (TextView) findViewById(R.id.tv_firmware_version);
        ivBg = (ImageView) findViewById(R.id.iv_bg);
        btnUpgrade = (Button) findViewById(R.id.btn_upgrade);
        btnUpgrade.setOnClickListener(this);

    }

    @Override
    protected void initData() {
        super.initData();
        deviceId = getIntent().getStringExtra("deviceId");
        tvDeviceNumber.setText(deviceId);
        getLcDeviceInfo();
        device = mainApplication.getDeviceCache().get(deviceId);
        if (device == null) {
            return;
        }
        if (TextUtils.equals(device.type, "CG22") || TextUtils.equals(device.type, "CG23")) {
            tvDeviceType.setText(device.type);
            ivBg.setImageResource(R.drawable.bg_cg22_info);
        } else if (TextUtils.equals(device.type, "CG24") || TextUtils.equals(device.type, "CG25")) {
            tvDeviceType.setText(device.type);
            ivBg.setImageResource(R.drawable.bg_cg24_info);
        } else if (TextUtils.equals(device.type, "CG26")) {
            tvDeviceType.setText(device.type);
            ivBg.setImageResource(R.drawable.bg_cg26_info);
        }
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_upgrade:
                updateFirmware();
                break;
        }
    }

    private void getLcDeviceInfo() {
        DeviceApiUnit deviceApiUnit = new DeviceApiUnit(this);
        deviceApiUnit.getLcDeviceInfo(deviceId, new DeviceApiUnit.DeviceApiCommonListener<LcDeviceInfoBean>() {
            @Override
            public void onSuccess(LcDeviceInfoBean bean) {
                tvFirmwareVersion.setText(bean.getSoftVersion());
                canBeUpdate = bean.getUpgradeStatus() == 1;
                if (canBeUpdate) {
                    btnUpgrade.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    private void updateFirmware() {
        DeviceApiUnit deviceApiUnit = new DeviceApiUnit(this);
        deviceApiUnit.setDeviceUpgrade(deviceId, new DeviceApiUnit.DeviceApiCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {
                btnUpgrade.setText(R.string.deviceLC_upgrade_ing);
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(msg);
            }
        });
    }

}
