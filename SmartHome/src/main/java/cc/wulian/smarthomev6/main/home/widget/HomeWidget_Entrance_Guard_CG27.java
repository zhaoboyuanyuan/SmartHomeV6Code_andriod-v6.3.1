package cc.wulian.smarthomev6.main.home.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.legrand.EntranceGuardAddressBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.legrand.EntranceGuardHostInfo;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.utils.ToastUtil;


public class HomeWidget_Entrance_Guard_CG27 extends RelativeLayout implements IWidgetLifeCycle, View.OnClickListener {
    private Context context;
    private Device device;
    private TextView name;
    private TextView state;
    private DeviceApiUnit deviceApiUnit;
    private ImageView ivVideo;
    private ImageView ivUnlock;
    private String communityId;
    private String uc;
    private String mainUc;

    public HomeWidget_Entrance_Guard_CG27(Context context) {
        super(context);
        initView(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBindViewHolder(HomeItemBean bean) {
        device = MainApplication.getApplication().getDeviceCache().get(bean.getWidgetID());
        name.setText(DeviceInfoDictionary.getNameByDevice(device));
        setState(device);
        initData();
    }

    @Override
    public void onViewRecycled() {

    }

    private void initView(Context context) {
        this.context = context;
        View rootView = LayoutInflater.from(context).inflate(R.layout.view_home_cg27, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        name = (TextView) findViewById(R.id.home_view_cateye_name);
        state = (TextView) findViewById(R.id.home_view_cateye_state);
        ivUnlock = findViewById(R.id.iv_unlock);
        ivVideo = findViewById(R.id.iv_video);
        ivUnlock.setOnClickListener(this);
        ivVideo.setOnClickListener(this);

    }

    private void initData() {
        deviceApiUnit = new DeviceApiUnit(context);
        communityId = device.devID.substring(0, 8);
        uc = device.devID.substring(8);
        getHostInfo();

    }

    /**
     * 获取主机Uc
     */
    private void getHostInfo() {
        deviceApiUnit.getEntranceGuardAddressInfo(communityId, uc, new DeviceApiUnit.DeviceApiCommonListener<EntranceGuardAddressBean>() {
            @Override
            public void onSuccess(EntranceGuardAddressBean bean) {
                if (bean != null && bean.getCommunityAddressVO() != null) {
                    String dd = bean.getCommunityAddressVO().get(0).getDd();
                    String bb = bean.getCommunityAddressVO().get(0).getBb();
                    String rr = bean.getCommunityAddressVO().get(0).getRr();
                    deviceApiUnit.getEntranceGuardHostInfo(communityId, dd, bb, rr, new DeviceApiUnit.DeviceApiCommonListener<EntranceGuardHostInfo>() {
                        @Override
                        public void onSuccess(EntranceGuardHostInfo bean) {
                            if (bean != null && bean.getCommunityAddressVO() != null) {
                                mainUc = bean.getCommunityAddressVO().getUc();
                            }
                        }

                        @Override
                        public void onFail(int code, String msg) {

                        }
                    });
                }

            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    /**
     * 远程开锁
     */
    private void remoteUnlock() {
        deviceApiUnit.entranceGuardUnlock(communityId, mainUc, new DeviceApiUnit.DeviceApiCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {
                ToastUtil.show(R.string.device_CG27_opening);
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    private void setState(Device device) {
        if (device != null) {
            if (device.isOnLine()) {
                // 上线
                state.setText(getResources().getString(R.string.Device_Online));
                ivUnlock.setImageResource(R.drawable.icon_widget_lock_online);
                ivVideo.setImageResource(R.drawable.icon_widget_video_online);
            } else {
                //下线
                ivUnlock.setImageResource(R.drawable.icon_widget_lock_offline);
                ivVideo.setImageResource(R.drawable.icon_widget_video_offline);
                state.setText(getResources().getString(R.string.Device_Offline));
            }
            ivVideo.setEnabled(device.isOnLine());
            ivUnlock.setEnabled(device.isOnLine());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && device != null) {
            if (TextUtils.equals(event.device.devID, device.devID)) {
                device = MainApplication.getApplication().getDeviceCache().get(device.devID);
                device.mode = event.device.mode;
                setState(device);
                name.setText(DeviceInfoDictionary.getNameByDevice(device));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_video:
                break;
            case R.id.iv_unlock:
                remoteUnlock();
                break;
        }
    }
}
