package cc.wulian.smarthomev6.main.device.device_bc.settingmore;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.MoreConfig;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.IDeviceMore;
import cc.wulian.smarthomev6.main.device.device_Bn.config.DevBnWifiConfigActivity;
import cc.wulian.smarthomev6.main.device.device_bc.config.DevBcWifiConfigActivity;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;

/**
 * Created by yuxiaoxuan on 2017/6/7.
 * add by hxc wifi配置按钮增加对Bn锁的支持
 * wifi配置
 */

public class WifiSettingButtonView extends RelativeLayout implements IDeviceMore {
    String devID = "";
    String gwID = "";

    public WifiSettingButtonView(Context context) {
        super(context);
        initView(context);
    }

    private Device mDevice;

    @Override
    public void onBindView(MoreConfig.ItemBean bean) {
        EventBus.getDefault().register(this);
        gwID = bean.getValueByKey("gwID");
        devID = bean.getValueByKey("devId");
        mDevice = MainApplication.getApplication().getDeviceCache().get(devID);
        updateState();
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    private TextView left_rename;

    private void initView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_device_more_custom_wifisetting, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        left_rename = (TextView) rootView.findViewById(R.id.left_rename);
        setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            if (TextUtils.equals(mDevice.type, "Bc")) {
                intent.setClass(WifiSettingButtonView.this.getContext(), DevBcWifiConfigActivity.class);
            } else if (TextUtils.equals(mDevice.type, "Bn")) {
                intent.setClass(WifiSettingButtonView.this.getContext(), DevBnWifiConfigActivity.class);
            }
            intent.putExtra("gwID", gwID);
            intent.putExtra("devID", devID);
            intent.putExtra("isAddDevice", false);
            WifiSettingButtonView.this.getContext().startActivity(intent);
        }
    };

    private void updateState() {
        if (mDevice != null && mDevice.isOnLine()) {
            left_rename.setTextColor(getResources().getColor(R.color.newPrimaryText));
            setEnabled(true);
        } else {
            left_rename.setTextColor(getResources().getColor(R.color.newStateText));
            setEnabled(false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DeviceReportEvent event) {
        if (event != null) {
            mDevice = MainApplication.getApplication().getDeviceCache().get(devID);
            updateState();
        }
    }
}