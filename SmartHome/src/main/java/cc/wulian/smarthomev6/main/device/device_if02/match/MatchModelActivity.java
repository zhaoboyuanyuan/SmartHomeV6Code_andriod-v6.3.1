package cc.wulian.smarthomev6.main.device.device_if02.match;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.device.device_if02.WifiIRManage;
import cc.wulian.smarthomev6.main.device.device_if02.airconditioner.AirOneByOneMatchActivity;
import cc.wulian.smarthomev6.main.device.device_if02.bean.ControllerBean;
import cc.wulian.smarthomev6.main.device.device_if02.custom.CustomMainActivity;
import cc.wulian.smarthomev6.main.device.device_if02.fan.FanMainActivity;
import cc.wulian.smarthomev6.main.device.device_if02.fan.FanOneByOneMatchActivity;
import cc.wulian.smarthomev6.main.device.device_if02.it_box.ITBoxMainActivity;
import cc.wulian.smarthomev6.main.device.device_if02.it_box.ITBoxOneByOneMatchActivity;
import cc.wulian.smarthomev6.main.device.device_if02.projector.ProjectorMainActivity;
import cc.wulian.smarthomev6.main.device.device_if02.projector.ProjectorOneByOneMatchActivity;
import cc.wulian.smarthomev6.main.device.device_if02.stb.StbOneByOneMatchActivity;
import cc.wulian.smarthomev6.main.device.device_if02.stb.StbRemoteMainActivity;
import cc.wulian.smarthomev6.main.device.device_if02.tv.TvOneByOneMatchActivity;
import cc.wulian.smarthomev6.main.device.device_if02.tv.TvRemoteMainActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;

/**
 * created by huxc  on 2018/8/14.
 * func：匹配模式选择（一键匹配、逐个匹配、自定义学习）
 * email: hxc242313@qq.com
 */

public class MatchModelActivity extends BaseTitleActivity {
    private LinearLayout layoutOneClick;
    private LinearLayout layoutOneByOne;
    private LinearLayout layoutCustom;

    private String deviceId;
    private String type;
    private String brandId;
    private String brandName;

    private WLDialog customDialog;


    public static void start(Context context, String deviceID, String type, String brandId, String brandName) {
        Intent intent = new Intent(context, MatchModelActivity.class);
        intent.putExtra("deviceID", deviceID);
        intent.putExtra("type", type);
        intent.putExtra("brandId", brandId);
        intent.putExtra("brandName", brandName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_match_mode, true);
    }


    @Override
    protected void initView() {
        super.initView();
        layoutOneClick = (LinearLayout) findViewById(R.id.layout_one_key);
        layoutOneByOne = (LinearLayout) findViewById(R.id.layout_one_by_one);
        layoutCustom = (LinearLayout) findViewById(R.id.layout_custom);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        layoutOneClick.setOnClickListener(this);
        layoutOneByOne.setOnClickListener(this);
        layoutCustom.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        deviceId = getIntent().getStringExtra("deviceID");
        type = getIntent().getStringExtra("type");
        brandId = getIntent().getStringExtra("brandId");
        brandName = getIntent().getStringExtra("brandName");
        setToolBarTitle(brandName + WifiIRManage.getDeviceTypeName(this, type));
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.layout_one_key:
                MatchNextKeyActivity.start(v.getContext(), deviceId, type, brandId, brandName);
                break;
            case R.id.layout_one_by_one:
                jumpOneByOneMatch();
                break;
            case R.id.layout_custom:
                customDialog();
                break;
        }
    }

    private void jumpOneByOneMatch() {
        switch (type) {
            case WifiIRManage.TYPE_AIR:
                AirOneByOneMatchActivity.start(this, deviceId, type, brandId, brandName);
                break;
            case WifiIRManage.TYPE_TV:
                TvOneByOneMatchActivity.start(this, deviceId, type, brandId, brandName);
                break;
            case WifiIRManage.TYPE_STB:
                StbOneByOneMatchActivity.start(this, deviceId, type, brandId, brandName);
                break;
            case WifiIRManage.TYPE_FAN:
                FanOneByOneMatchActivity.start(this, deviceId, type, brandId, brandName);
                break;
            case WifiIRManage.TYPE_IT_BOX:
                ITBoxOneByOneMatchActivity.start(this, deviceId, type, brandId, brandName);
                break;
            case WifiIRManage.TYPE_PROJECTOR:
                ProjectorOneByOneMatchActivity.start(this, deviceId, type, brandId, brandName);
                break;
            default:
                break;
        }
    }

    private void jumpLearnMode(ControllerBean bean) {
        switch (bean.blockType) {
            case WifiIRManage.TYPE_TV:
                TvRemoteMainActivity.learn(MatchModelActivity.this, deviceId, bean.blockType, bean.blockName, bean.blockId);
                break;
            case WifiIRManage.TYPE_STB:
                StbRemoteMainActivity.learn(MatchModelActivity.this, deviceId, bean.blockType, bean.blockName, bean.blockId);
                break;
            case WifiIRManage.TYPE_FAN:
                FanMainActivity.learn(MatchModelActivity.this, deviceId, bean.blockType, bean.blockName, bean.blockId);
                break;
            case WifiIRManage.TYPE_IT_BOX:
                ITBoxMainActivity.learn(MatchModelActivity.this, deviceId, bean.blockType, bean.blockName, bean.blockId);
                break;
            case WifiIRManage.TYPE_PROJECTOR:
                ProjectorMainActivity.learn(MatchModelActivity.this, deviceId, bean.blockType, bean.blockName, bean.blockId);
                break;
            case WifiIRManage.TYPE_CUSTOM:
            case WifiIRManage.TYPE_AIR:
                CustomMainActivity.learn(MatchModelActivity.this, deviceId, bean.blockType, bean.blockName, bean.blockId);
                break;
            default:
                break;
        }
    }


    private void customDialog() {
        WLDialog.Builder builder = new WLDialog.Builder(this);
        builder.setEditTextHint(R.string.Infraredrelay_Custom_Entername)
                .setTitle(getString(R.string.Infraredrelay_Custom_Popuptitle))
                .setPositiveButton(R.string.Sure)
                .setNegativeButton(R.string.Cancel)
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View var1, String msg) {
                        createCustomController(msg);
                    }

                    @Override
                    public void onClickNegative(View var1) {
                        customDialog.dismiss();

                    }
                });
        customDialog = builder.create();
        customDialog.show();
    }

    private void createCustomController(String msg) {
        DataApiUnit dataApiUnit = new DataApiUnit(this);
        dataApiUnit.doAddWifiIRController(deviceId, type, msg, null, new DataApiUnit.DataApiCommonListener<ControllerBean>() {
            @Override
            public void onSuccess(ControllerBean bean) {
                if (bean != null) {
                    jumpLearnMode(bean);
                }

            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }
}

