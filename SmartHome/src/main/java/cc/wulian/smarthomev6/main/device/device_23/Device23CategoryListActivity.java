package cc.wulian.smarthomev6.main.device.device_23;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.device.device_23.AirConditioning.AirConditioningBrandActivity;
import cc.wulian.smarthomev6.main.device.device_23.custom.CustomAddActivity;
import cc.wulian.smarthomev6.main.device.device_23.tv.RemoteControlBrandActivity;
import cc.wulian.smarthomev6.support.event.UEIEvent;

import static cc.wulian.smarthomev6.main.device.device_23.tv.RemoteControlBrandActivity.SINGLE_CODE_PROJECTOR;
import static cc.wulian.smarthomev6.main.device.device_23.tv.RemoteControlBrandActivity.TYPE_AUDIO;
import static cc.wulian.smarthomev6.main.device.device_23.tv.RemoteControlBrandActivity.TYPE_PROJECTOR;
import static cc.wulian.smarthomev6.main.device.device_23.tv.RemoteControlBrandActivity.TYPE_STB;
import static cc.wulian.smarthomev6.main.device.device_23.tv.RemoteControlBrandActivity.TYPE_TV;

/**
 * Created by Veev on 2017/9/22
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    Device23CategoryListActivity
 */

public class Device23CategoryListActivity extends BaseTitleActivity {

    private LinearLayout mLinearAir, mLinearTv, mLinearStb, mLinearCustom, mLinearProjector, mLinearAudio;
    private String mDeviceID;

    public static void start(Context context, String deviceID) {
        Intent i = new Intent(context, Device23CategoryListActivity.class);
        i.putExtra("deviceID", deviceID);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_device23_category_list, true);
        EventBus.getDefault().register(this);

        mLinearAir = (LinearLayout) findViewById(R.id.device_23_category_item_air);
        mLinearTv = (LinearLayout) findViewById(R.id.device_23_category_item_tv);
        mLinearStb = (LinearLayout) findViewById(R.id.device_23_category_item_stb);
        mLinearProjector = (LinearLayout) findViewById(R.id.device_23_category_item_projector);
        mLinearAudio = (LinearLayout) findViewById(R.id.device_23_category_item_audio);
        mLinearCustom = (LinearLayout) findViewById(R.id.device_23_category_item_custom);

        mLinearAir.setOnClickListener(this);
        mLinearTv.setOnClickListener(this);
        mLinearStb.setOnClickListener(this);
        mLinearProjector.setOnClickListener(this);
        mLinearAudio.setOnClickListener(this);
        mLinearCustom.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
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
            case R.id.device_23_category_item_air:
                AirConditioningBrandActivity.start(Device23CategoryListActivity.this, mDeviceID);
                break;
            case R.id.device_23_category_item_tv:
                RemoteControlBrandActivity.start(Device23CategoryListActivity.this, mDeviceID, TYPE_TV);
                break;
            case R.id.device_23_category_item_stb:
                RemoteControlBrandActivity.start(Device23CategoryListActivity.this, mDeviceID, TYPE_STB);
                break;
            case R.id.device_23_category_item_projector:
                RemoteControlBrandActivity.start(Device23CategoryListActivity.this, mDeviceID, TYPE_PROJECTOR, SINGLE_CODE_PROJECTOR);
                break;
            case R.id.device_23_category_item_audio:
                RemoteControlBrandActivity.start(Device23CategoryListActivity.this, mDeviceID, TYPE_AUDIO);
                break;
            case R.id.device_23_category_item_custom:
                CustomAddActivity.start(Device23CategoryListActivity.this, mDeviceID);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UEIEvent event) {
        if (event.mode == 1 && TextUtils.equals(event.type, "Z")) {
            finish();
        }
    }
}
