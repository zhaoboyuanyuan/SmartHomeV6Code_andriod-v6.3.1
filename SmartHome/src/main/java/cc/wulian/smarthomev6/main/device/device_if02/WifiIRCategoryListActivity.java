package cc.wulian.smarthomev6.main.device.device_if02;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.device.device_if02.custom.AddCustomActivity;
import cc.wulian.smarthomev6.main.device.device_if02.match.WifiIRBrandActivity;

/**
 * created by huxc  on 2018/6/12.
 * func：wifi红外转发器设备选择界面
 * email: hxc242313@qq.com
 */

public class WifiIRCategoryListActivity extends BaseTitleActivity {

    private LinearLayout mLinearAir;
    private LinearLayout mLinearTv;
    private LinearLayout mLinearStb;
    private LinearLayout mLinearFan;
    private LinearLayout mLinearITBOX;
    private LinearLayout mLinearProjector;
    private LinearLayout mLinearCustom;
    private String mDeviceID;

    public static void start(Context context, String deviceID) {
        Intent i = new Intent(context, WifiIRCategoryListActivity.class);
        i.putExtra("deviceID", deviceID);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_if01_category_list, true);

        mLinearAir = (LinearLayout) findViewById(R.id.if01_category_item_air);
        mLinearTv = (LinearLayout) findViewById(R.id.if01_category_item_tv);
        mLinearStb = (LinearLayout) findViewById(R.id.if01_category_item_stb);
        mLinearFan = (LinearLayout) findViewById(R.id.if01_category_item_fan);
        mLinearITBOX = (LinearLayout) findViewById(R.id.if01_category_item_it_box);
        mLinearProjector = (LinearLayout) findViewById(R.id.if01_category_item_projector);
        mLinearCustom = (LinearLayout) findViewById(R.id.if01_category_item_custom);

        mLinearAir.setOnClickListener(this);
        mLinearTv.setOnClickListener(this);
        mLinearStb.setOnClickListener(this);
        mLinearFan.setOnClickListener(this);
        mLinearITBOX.setOnClickListener(this);
        mLinearProjector.setOnClickListener(this);
        mLinearCustom.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void initTitle() {
        super.initTitle();

        mDeviceID = getIntent().getStringExtra("deviceID");
        setToolBarTitle(R.string.Home_Widget_Addremote);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.if01_category_item_air:
            case R.id.if01_category_item_tv:
            case R.id.if01_category_item_stb:
            case R.id.if01_category_item_fan:
            case R.id.if01_category_item_it_box:
            case R.id.if01_category_item_projector:
                WifiIRBrandActivity.start(WifiIRCategoryListActivity.this, mDeviceID, v.getTag().toString());
                break;
            case R.id.if01_category_item_custom:
                AddCustomActivity.start(WifiIRCategoryListActivity.this,mDeviceID);
                break;
        }
    }

}
