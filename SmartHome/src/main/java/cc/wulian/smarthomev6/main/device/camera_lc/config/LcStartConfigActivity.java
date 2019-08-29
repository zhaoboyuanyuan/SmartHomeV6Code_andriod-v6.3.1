package cc.wulian.smarthomev6.main.device.camera_lc.config;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.LcConfigWifiModel;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.support.utils.LogUtil;

/**
 * Created by hxc on 2017/7/6.
 * func:乐橙摄像机配网起始页
 */

public class LcStartConfigActivity extends BaseTitleActivity {
    private LcConfigWifiModel configData;
    private LcInputDevicePwdFragment inputDevicePwdFragment;
    private LcInputWifiFragment inputWifiFragment;
    private LcAlreadyBindFragment alreadyBindFragment;
    private FragmentManager manager;
    private FragmentTransaction ft;
    private int boundRelation = -1;
    private String boundUser;

    public static void start(Context context, LcConfigWifiModel model, int boundRelation, String boundUser) {
        Intent it = new Intent();
        it.setClass(context, LcStartConfigActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("configData", model);
        bundle.putString("boundUser", boundUser);
        bundle.putInt("boundRelation", boundRelation);
        it.putExtras(bundle);
        context.startActivity(it);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_start_config, true);
    }

    @Override
    protected void initData() {
        super.initData();
        configData = (LcConfigWifiModel) getIntent().getExtras().getSerializable("configData");
        boundRelation = getIntent().getExtras().getInt("boundRelation");
        boundUser = getIntent().getExtras().getString("boundUser");
        Log.i(LogUtil.LC_TAG, "LcConfigWifiModel--->status =  " + configData.getmStatus());
        Log.i(LogUtil.LC_TAG, "LcConfigWifiModel--->mac = " + configData.getmMac());
        if (configData == null) {
            return;
        }
        if (configData.isWiredConnect()) {//有线连接
            manager = getSupportFragmentManager();
            ft = manager.beginTransaction();
            if (boundRelation == 0) {
                inputDevicePwdFragment = LcInputDevicePwdFragment.newInstance(configData);
                ft.replace(android.R.id.content, inputDevicePwdFragment, inputDevicePwdFragment.getClass().getName());
                ft.commit();
            } else {
                alreadyBindFragment = LcAlreadyBindFragment.newInstance(boundRelation, boundUser, configData);
                ft.replace(android.R.id.content, alreadyBindFragment, alreadyBindFragment.getClass().getName());
                ft.commit();
            }
        } else if (configData.isWifiConnect()) {//WiFi连接
            manager = getSupportFragmentManager();
            ft = manager.beginTransaction();
            if (boundRelation == 0) {
                inputWifiFragment = LcInputWifiFragment.newInstance(configData);
                ft.replace(android.R.id.content, inputWifiFragment, inputWifiFragment.getClass().getName());
                ft.commit();
            } else {
                alreadyBindFragment = LcAlreadyBindFragment.newInstance(boundRelation, boundUser, configData);
                ft.replace(android.R.id.content, alreadyBindFragment, alreadyBindFragment.getClass().getName());
                ft.commit();
            }

        }

    }

    @Override
    protected void initTitle() {
        super.initTitle();
    }

}
