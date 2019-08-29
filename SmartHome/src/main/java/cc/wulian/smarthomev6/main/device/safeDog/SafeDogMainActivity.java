package cc.wulian.smarthomev6.main.device.safeDog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.SafeDogHomeDataBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;

/**
 * Created by 上海滩小马哥 on 2018/01/23.
 * 安全狗主界面
 */

public class SafeDogMainActivity extends BaseTitleActivity {

    private static final String TAG = "SafeDogMainActivity";

    public RelativeLayout offlineLayout, rl_defense, rl_undefense, rl_device_safe, rl_wifi, rl_attack, rl_setting;
    public TextView defenseTextView, undefenseTextView, numTextView, scaningTextView;
    public String devId;
    public Device device;

    public static void start(Context context, String devId) {
        Intent intent = new Intent(context, SafeDogMainActivity.class);
        intent.putExtra("devId", devId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safedog, true);
    }


    @Override
    protected void updateSkin() {
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        devId = getIntent().getStringExtra("devId");
        device = mainApplication.getDeviceCache().get(devId);
        if (device == null) {
            setToolBarTitle(R.string.Device_Default_Name_sd01);
        } else {
            setToolBarTitle(DeviceInfoDictionary.getNameByDevice(device));
        }
        setToolbarBackground(1f, getResources().getColor(R.color.v6_safedog_tittle));
    }

    @Override
    protected void initView() {
        offlineLayout = (RelativeLayout) findViewById(R.id.offline_relative);
        rl_defense = (RelativeLayout) findViewById(R.id.rl_defense);
        rl_undefense = (RelativeLayout) findViewById(R.id.rl_undefense);
        rl_device_safe = (RelativeLayout) findViewById(R.id.rl_device_safe);
        rl_wifi = (RelativeLayout) findViewById(R.id.rl_wifi);
        rl_attack = (RelativeLayout) findViewById(R.id.rl_attack);
        rl_setting = (RelativeLayout) findViewById(R.id.rl_setting);
        defenseTextView = (TextView) findViewById(R.id.tv_defense);
        undefenseTextView = (TextView) findViewById(R.id.tv_undefense);
        numTextView = (TextView) findViewById(R.id.tv_defense_num);
        scaningTextView = (TextView) findViewById(R.id.tv_scanning);
    }


    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    protected void init() {
        new DataApiUnit(this).doGetSafedogHomeData(devId, new DataApiUnit.DataApiCommonListener<SafeDogHomeDataBean>() {
            @Override
            public void onSuccess(SafeDogHomeDataBean bean) {
                if (bean == null) {
                    return;
                }
//                if (bean.status == 2) {
//                    offlineLayout.setVisibility(View.VISIBLE);
//                } else {
                    offlineLayout.setVisibility(View.GONE);
                    if (null != bean.protectDevices) {
                        defenseTextView.setText(bean.protectDevices.sum);
                    }
                    if (null != bean.unprotectDevices) {
                        undefenseTextView.setText(bean.unprotectDevices.sum);
                    }
                    if (null != bean.attacks) {
                        numTextView.setText(bean.attacks.sum);
                    }
                    if (bean.scanStatus == 1 || bean.scanStatus == 2) {
                        scaningTextView.setVisibility(View.VISIBLE);
                    } else {
                        scaningTextView.setVisibility(View.GONE);
                    }
//                }
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    @Override
    protected void initListeners() {
        rl_defense.setOnClickListener(this);
        rl_undefense.setOnClickListener(this);
        rl_device_safe.setOnClickListener(this);
        rl_wifi.setOnClickListener(this);
        rl_attack.setOnClickListener(this);
        rl_setting.setOnClickListener(this);
    }

    @Override
    public void onClickView(View v) {
        super.onClickView(v);
        switch (v.getId()) {
            case R.id.rl_defense:
//                SafeDogDevicesActivity.start(SafeDogMainActivity.this, devId, SafeDogDevicesActivity.TYPE_PROTECTED);
                break;
            case R.id.rl_undefense:
//                SafeDogDevicesActivity.start(SafeDogMainActivity.this, devId, SafeDogDevicesActivity.TYPE_UNPROTECTED);
                break;
            case R.id.rl_device_safe:
                SafeDogSecurityActivity.start(SafeDogMainActivity.this, devId);
                break;
            case R.id.rl_wifi:
                break;
            case R.id.rl_attack:
                SafeDogAttackSourceRecord.start(SafeDogMainActivity.this, devId);
                break;
            case R.id.rl_setting:
                SafeDogSettingActivity.start(SafeDogMainActivity.this, devId);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            this.finish();
        }
    }
}
