package cc.wulian.smarthomev6.main.mine.sharedevice;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.WLBaseAdapter;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceListBeanAll;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.mqtt.bean.DeviceForbiddenBean;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.utils.DialogUtil;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by zbl on 2017/8/24.
 */

public class ShareDeviceMainActivity extends BaseTitleActivity {
    private static final String GET_DATA = "GET_DATA";

    private DeviceApiUnit deviceApiUnit;
    private ListView mListView;
    private DeviceListAdapter adapter;
    private List<DeviceBean> deviceList = new ArrayList<>();
    private String pageSize = "10";
    private int totalCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharedevice_main, true);
    }

    @Override
    protected void initTitle() {
        setToolBarTitle(getString(R.string.Share_Manager));
    }

    @Override
    protected void initView() {
        mListView = (ListView) findViewById(R.id.listView);
        adapter = new DeviceListAdapter(this, deviceList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DeviceBean deviceBean = deviceList.get(position);
                if (isGatewayForbidden(deviceBean.deviceId)) {
                    DialogUtil.showDeviceForbiddenDialog(ShareDeviceMainActivity.this, getString(R.string.device_gateway_Disable));
                    return;
                }
                ShareDeviceAccountListActivity.start(ShareDeviceMainActivity.this, deviceBean);
            }
        });
    }

    @Override
    protected void initData() {
        deviceApiUnit = new DeviceApiUnit(this);
        ProgressDialogManager.getDialogManager().showDialog(GET_DATA, this, null, null, getResources().getInteger(R.integer.http_timeout));
        getAllDevice();
    }

    private void getAllDevice() {
        deviceList.clear();
        deviceApiUnit.getWifiDeviceList("1", pageSize, new DeviceApiUnit.DeviceApiCommonListener<DeviceListBeanAll>() {
            @Override
            public void onSuccess(DeviceListBeanAll bean) {
                parserAllDeviceList(bean);
                int allCount = Integer.parseInt(bean.totalCount);
                int size = Integer.parseInt(pageSize);
                //总共需要分页的次数
                final int times = allCount % size == 0 ? allCount / size : allCount / size + 1;
                if (times <= 1) {
                    updateView();
                } else {
                    //需要开启的线程总数
                    totalCount = times - 1;
                    createThread(totalCount);
                }
            }

            @Override
            public void onFail(int code, String msg) {
                ProgressDialogManager.getDialogManager().dimissDialog(GET_DATA, 0);
                ToastUtil.show(msg);
            }
        });

    }

    private void parserAllDeviceList(DeviceListBeanAll bean) {
        String currentGatewayId = Preference.getPreferences().getCurrentGatewayID();
        for (DeviceBean deviceBean : bean.devices) {
            if (!deviceBean.isShared()) {
                if (StringUtil.equals(deviceBean.deviceId, currentGatewayId) || (!deviceBean.isGateway() && !StringUtil.equals(deviceBean.type, "CMICY1"))) {
                    deviceList.add(deviceBean);
                }
            }

        }
    }

    private void createThread(int times) {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
        for (int i = 0; i < times; i++) {
            final int page = i + 2;//第二页开始
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "开启子线程：" + (page - 1));
                    deviceApiUnit.getWifiDeviceList(String.valueOf(page), pageSize, new DeviceApiUnit.DeviceApiCommonListener<DeviceListBeanAll>() {
                        @Override
                        public void onSuccess(DeviceListBeanAll bean) {
                            --totalCount;
                            Log.i(TAG, "获取第" + bean.currentPage + "页数据，" + "剩余线程数：" + totalCount);
                            parserAllDeviceList(bean);
                            if (totalCount == 0) {
                                updateView();
                            }
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            --totalCount;
                        }
                    });
                }
            });
        }
        fixedThreadPool.shutdown();
    }

    private boolean isGatewayForbidden(String gwId) {
        DeviceForbiddenBean deviceForbiddenBean;
        String forbiddenDevice = mainApplication.forbiddenDevice;
        if (!TextUtils.isEmpty(forbiddenDevice)) {
            deviceForbiddenBean = JSON.parseObject(forbiddenDevice, DeviceForbiddenBean.class);
            if (deviceForbiddenBean != null) {
                if (deviceForbiddenBean.getStatus() == 0 && deviceForbiddenBean.getType() == 0 && TextUtils.equals(gwId, deviceForbiddenBean.getGwID())) {
                    return true;
                }
            }

        }
        return false;
    }

    private void updateView() {
        adapter.swapData(deviceList);
        ProgressDialogManager.getDialogManager().dimissDialog(GET_DATA, 0);
    }

    class DeviceListAdapter extends WLBaseAdapter<DeviceBean> {
        public DeviceListAdapter(Context context, List<DeviceBean> data) {
            super(context, data);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_device_list, null);
                holder.ivDeviceIcon = (ImageView) convertView.findViewById(R.id.iv_device_icon);
                holder.tvDeviceName = (TextView) convertView.findViewById(R.id.tv_device_name);
                holder.tvDeviceType = (TextView) convertView.findViewById(R.id.tv_device_type);
                holder.tvDeviceArea = (TextView) convertView.findViewById(R.id.tv_device_area);
                holder.tvDeviceinfo = (ImageView) convertView.findViewById(R.id.iv_device_info);
                holder.layout_info = convertView.findViewById(R.id.layout_info);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            DeviceBean deviceBean = mData.get(position);
            if (deviceBean.isGateway()) {
                holder.ivDeviceIcon.setImageResource(R.drawable.icon_device_gateway);
                holder.tvDeviceName.setText(DeviceInfoDictionary.getNameByTypeAndName(deviceBean.getType(), deviceBean.getName()));
                holder.tvDeviceType.setText(mContext.getString(R.string.BindGateway_GatewayID) + ":" + deviceBean.getDeviceId());
                holder.tvDeviceArea.setText("");
                holder.layout_info.setVisibility(View.VISIBLE);
            } else {
                holder.ivDeviceIcon.setImageResource(DeviceInfoDictionary.getIconByType(deviceBean.getType()));
                holder.tvDeviceName.setText(DeviceInfoDictionary.getNameByTypeAndName(deviceBean.getType(), deviceBean.getName()));
                holder.layout_info.setVisibility(View.GONE);
            }
//            if ("1".equals(mData.get(position).getState())) {
//                holder.tvDeviceName.setTextColor(Color.BLACK);
//            } else {
//                holder.tvDeviceName.setTextColor(Color.GRAY);
//            }

            return convertView;
        }
    }

    public final class ViewHolder {
        public ImageView ivDeviceIcon;
        public TextView tvDeviceName;
        public TextView tvDeviceType;
        public TextView tvDeviceArea;
        public ImageView tvDeviceinfo;
        public View layout_info;
    }
}
