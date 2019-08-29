package cc.wulian.smarthomev6.main.device.camera_lc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.camera_lc.adapter.NvrDeviceListAdapter;
import cc.wulian.smarthomev6.main.device.camera_lc.setting.NVRListSettingActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.LcDeviceInfoBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.NvrChildDeviceNameEvent;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

public class NVRDeviceListActivity extends BaseTitleActivity implements View.OnClickListener {
    private static final String QUERY = "query";
    private FrameLayout flDeviceList;
    private View layoutOffline;
    private ScrollView flNoDevice;
    private RecyclerView recyclerView;
    private NvrDeviceListAdapter adapter;

    private Device device;
    private String deviceId;
    private DeviceApiUnit deviceApiUnit;
    private List<LcDeviceInfoBean.ExtDataBean.ChannelsBean> deviceList;
    private String token;

    public static void start(Context context, String deviceId) {
        Intent intent = new Intent(context, NVRDeviceListActivity.class);
        intent.putExtra("deviceId", deviceId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nvr_device_list, true);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initView() {
        super.initView();
        recyclerView = (RecyclerView) findViewById(R.id.rl_device_list);
        flNoDevice = (ScrollView) findViewById(R.id.layout_content);
        flDeviceList = (FrameLayout) findViewById(R.id.fl_device);
        layoutOffline = findViewById(R.id.relative_offline);

    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitleAndRightImg(getString(R.string.Device_Default_Name_CG26), R.drawable.icon_more);
    }

    @Override
    protected void initData() {
        super.initData();
        deviceId = getIntent().getStringExtra("deviceId");
        device = mainApplication.getDeviceCache().get(deviceId);
        deviceApiUnit = new DeviceApiUnit(this);
        adapter = new NvrDeviceListAdapter(this);
        if (device != null) {
            setTitleText(DeviceInfoDictionary.getNameByTypeAndName(device.type, device.name));
        }
        initRecyclerViewStyle();
        getAllChannelInfo();
        updateMode();
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        adapter.setOnItemClickListener(new NvrDeviceListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                NVRDeviceDetailActivity.start(NVRDeviceListActivity.this, deviceId, token, deviceList.get(position).getChannelId(), deviceList.get(position).getChannelName());
            }
        });
    }

    private void initRecyclerViewStyle() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void getAllChannelInfo() {
        progressDialogManager.showDialog(QUERY, this, "", null, 5 * 1000);
        deviceApiUnit.getLcDeviceInfo(deviceId, new DeviceApiUnit.DeviceApiCommonListener<LcDeviceInfoBean>() {
            @Override
            public void onSuccess(LcDeviceInfoBean bean) {
                progressDialogManager.dimissDialog(QUERY, 0);
                if (bean != null && bean.getExtData() != null) {
                    token = bean.getExtData().getToken();
                    deviceList = bean.getExtData().getChannels();
                    if (deviceList != null && deviceList.size() > 0) {
                        flDeviceList.setVisibility(View.VISIBLE);
                        flNoDevice.setVisibility(View.GONE);
                        adapter.update(deviceList);
                    } else {
                        flNoDevice.setVisibility(View.VISIBLE);
                        flDeviceList.setVisibility(View.GONE);
                    }
                } else {
                    flNoDevice.setVisibility(View.VISIBLE);
                    flDeviceList.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(QUERY, 0);
                ToastUtil.show(msg);
            }
        });

    }

    private void updateMode() {
        if (device != null && device.isOnLine()) {
            // 上线
            layoutOffline.setVisibility(View.INVISIBLE);
        } else {
            // 离线
            layoutOffline.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.img_right:
                Intent it = new Intent(this, NVRListSettingActivity.class);
                it.putExtra("deviceId", deviceId);
                startActivityForResult(it, 1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && device != null) {
            if (TextUtils.equals(event.device.devID, device.devID)) {
                device = MainApplication.getApplication().getDeviceCache().get(device.devID);
                if (event.device.mode == 0) {
                    updateMode();
                } else if (event.device.mode == 2) {
                    // 设备离线
                    updateMode();
                } else if (event.device.mode == 1) {
                    // 更新上线
                    updateMode();
                }
                setTitleText(DeviceInfoDictionary.getNameByTypeAndName(event.device.type, event.device.name));
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
