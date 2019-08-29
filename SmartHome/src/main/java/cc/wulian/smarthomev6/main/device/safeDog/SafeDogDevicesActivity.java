package cc.wulian.smarthomev6.main.device.safeDog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ListView;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.device.adapter.SafeDogDevicesAdapter;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.SafeDogProtectDeviceBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;

/**
 * Created by 上海滩小马哥 on 2018/01/24.
 * 保护/未保护设备列表页
 */

public class SafeDogDevicesActivity extends BaseTitleActivity {

    private static final String TAG = "SafeDogDevicesActivity";
    public static final int TYPE_PROTECTED = 1;
    public static final int TYPE_UNPROTECTED = 2;
    public ListView deviceListView;

    public SafeDogDevicesAdapter deviceListAdapter;
    public String devId;
    public int type;

    public static void start(Context context, String devId, int type) {
        Intent intent = new Intent(context, SafeDogDevicesActivity.class);
        intent.putExtra("devId", devId);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safedog_devices, true);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        type = getIntent().getIntExtra("type", TYPE_PROTECTED);
        devId = getIntent().getStringExtra("devId");
        if (type == TYPE_PROTECTED){
            setToolBarTitle("受保护设备");
        }else if (type == TYPE_UNPROTECTED){
            setToolBarTitle("未受保护设备");
        }
    }

    @Override
    protected void initView() {
        deviceListView = (ListView) findViewById(R.id.lv_devices);
        deviceListAdapter = new SafeDogDevicesAdapter(this, null);
        deviceListView.setAdapter(deviceListAdapter);
    }

    @Override
    protected void initData() {
        DataApiUnit dataApiUnit = new DataApiUnit(this);
        if (type == TYPE_PROTECTED) {
            dataApiUnit.doGetSafedogProtectedDevices(devId, new DataApiUnit.DataApiCommonListener<List<SafeDogProtectDeviceBean>>() {
                @Override
                public void onSuccess(List<SafeDogProtectDeviceBean> bean) {
                    if (null != bean) {
                        deviceListAdapter.swapData(bean);
                    }
                }

                @Override
                public void onFail(int code, String msg) {

                }
            });
        } else if (type == TYPE_UNPROTECTED) {
            dataApiUnit.doGetSafedogUnprotectedDevices(devId, new DataApiUnit.DataApiCommonListener<List<SafeDogProtectDeviceBean>>() {
                @Override
                public void onSuccess(List<SafeDogProtectDeviceBean> bean) {
                    if (null != bean) {
                        deviceListAdapter.swapData(bean);
                    }
                }

                @Override
                public void onFail(int code, String msg) {

                }
            });
        }
    }
}
