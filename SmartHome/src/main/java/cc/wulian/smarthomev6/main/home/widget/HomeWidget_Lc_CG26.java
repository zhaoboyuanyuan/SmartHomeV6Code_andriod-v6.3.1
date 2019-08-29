package cc.wulian.smarthomev6.main.home.widget;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.camera_lc.NVRDeviceDetailActivity;
import cc.wulian.smarthomev6.main.device.camera_lc.NVRDeviceListActivity;
import cc.wulian.smarthomev6.main.device.camera_lc.adapter.NvrWidgetAdapter;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.LcDeviceInfoBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.NvrChildDeviceNameEvent;
import cc.wulian.smarthomev6.support.utils.ToastUtil;


public class HomeWidget_Lc_CG26 extends RelativeLayout implements IWidgetLifeCycle {
    private Context context;
    private Device device;
    private TextView name;
    private TextView state;
    private TextView tvNoChannel;
    private RecyclerView recyclerView;
    private NvrWidgetAdapter adapter;
    private DeviceApiUnit deviceApiUnit;
    private String token;
    private List<LcDeviceInfoBean.ExtDataBean.ChannelsBean> deviceList;

    public HomeWidget_Lc_CG26(Context context) {
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
        initListener();
    }

    @Override
    public void onViewRecycled() {

    }

    private void initView(Context context) {
        this.context = context;
        View rootView = LayoutInflater.from(context).inflate(R.layout.view_home_lc_cg26, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        recyclerView = findViewById(R.id.rv_nvr);
        name = (TextView) findViewById(R.id.home_view_cateye_name);
        state = (TextView) findViewById(R.id.home_view_cateye_state);
        tvNoChannel = (TextView) findViewById(R.id.tv_no_channel);

    }

    private void initData() {
        adapter = new NvrWidgetAdapter(context);
        deviceApiUnit = new DeviceApiUnit(context);
        initRecyclerViewStyle();
        getAllChannelInfo();

    }

    private void initListener() {
        adapter.setOnItemClickListener(new NvrWidgetAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (device.isOnLine()) {
                    NVRDeviceDetailActivity.start(context, device.devID, token, deviceList.get(position).getChannelId(), deviceList.get(position).getChannelName());
                }

            }
        });
        tvNoChannel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                NVRDeviceListActivity.start(context, device.devID);
            }
        });
    }

    private void initRecyclerViewStyle() {
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void getAllChannelInfo() {
        deviceApiUnit.getLcDeviceInfo(device.devID, new DeviceApiUnit.DeviceApiCommonListener<LcDeviceInfoBean>() {
            @Override
            public void onSuccess(LcDeviceInfoBean bean) {
                if (bean != null && bean.getExtData() != null) {
                    token = bean.getExtData().getToken();
                    deviceList = bean.getExtData().getChannels();
                    if (deviceList != null && deviceList.size() > 0) {
                        recyclerView.setVisibility(View.VISIBLE);
                        tvNoChannel.setVisibility(View.GONE);
                        adapter.update(deviceList);
                    } else {
                        tvNoChannel.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                } else {
                    tvNoChannel.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(msg);
            }
        });

    }

    private void setState(Device device) {
        if (device != null) {
            if (device.isOnLine()) {
                // 上线
                state.setText(getResources().getString(R.string.Device_Online));
            } else {
                //下线
                state.setText(getResources().getString(R.string.Device_Offline));
            }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNvrChildDeviceNameEvent(NvrChildDeviceNameEvent event) {
        if (!TextUtils.isEmpty(event.childDeviceName)) {
            getAllChannelInfo();
        }
    }
}
