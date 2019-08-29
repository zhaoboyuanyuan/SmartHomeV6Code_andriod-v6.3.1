package cc.wulian.smarthomev6.main.device.device_Az;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.MoreConfig;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.IDeviceMore;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;

/**
 * created by huxc  on 2018/2/22.
 * func：2013设备设置页help栏
 * email: hxc242313@qq.com
 */

public class DeviceMoreHelpView extends LinearLayout implements IDeviceMore {

    private static String TAG = DeviceMoreHelpView.class.getSimpleName();

    private TextView tvHelp;

    private String deviceID;
    private Device mDevice;


    public DeviceMoreHelpView(Context context) {
        super(context);

        initView(context);
    }

    private void initView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_device_more_help, null);
        addView(rootView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tvHelp = (TextView) rootView.findViewById(R.id.tv_help);

    }

    @Override
    public void onBindView(MoreConfig.ItemBean bean) {
        EventBus.getDefault().register(this);

        dealData(mDevice);
    }



    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReportEvent(DeviceReportEvent event) {
        if (event != null && TextUtils.equals(event.device.devID, deviceID)) {
            mDevice = MainApplication.getApplication().getDeviceCache().get(deviceID);
            dealData(event.device);
        }
    }

    private void dealData(Device device) {
        if (device == null) {
            return;
        }

        if (device.isOnLine()) {
            tvHelp.setTextColor(getResources().getColor(R.color.newPrimaryText));
            setEnabled(true);
        } else {
            tvHelp.setTextColor(getResources().getColor(R.color.newStateText));
            setEnabled(false);
        }

    }
}
