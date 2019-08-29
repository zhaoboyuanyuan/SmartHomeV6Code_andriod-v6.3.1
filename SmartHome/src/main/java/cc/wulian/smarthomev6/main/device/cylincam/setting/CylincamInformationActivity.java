package cc.wulian.smarthomev6.main.device.cylincam.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.device.cylincam.bean.CylincamInfoBean;

/**
 * created by huxc  on 2017/9/14.
 * func：小物摄像机详情界面
 * email: hxc242313@qq.com
 */

public class CylincamInformationActivity extends BaseTitleActivity implements View.OnClickListener {
    private TextView tvDeviceType;
    private TextView tvDeviceNumber;
    private TextView tvFirmwareVersion;
    private TextView tvIpAddress;
    private String deviceId;

    private CylincamInfoBean bean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_cylincam_information, true);
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
        tvIpAddress = (TextView) findViewById(R.id.tv_ip_address);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initData() {
        super.initData();
        bean = (CylincamInfoBean) getIntent().getSerializableExtra("cylincamInfoBean");
        deviceId = getIntent().getStringExtra("deviceId");
        tvIpAddress.setText(bean.getIpAddress());
        tvFirmwareVersion.setText(bean.getFirmwareVersion());
        tvDeviceNumber.setText(deviceId);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
        }
    }
}

