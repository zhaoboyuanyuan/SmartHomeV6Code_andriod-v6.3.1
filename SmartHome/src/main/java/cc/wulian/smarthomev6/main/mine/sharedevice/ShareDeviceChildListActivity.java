package cc.wulian.smarthomev6.main.mine.sharedevice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.application.WLBaseAdapter;
import cc.wulian.smarthomev6.support.core.apiunit.AuthApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.UserBean;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceCache;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.mqtt.bean.DeviceForbiddenBean;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.skin.ButtonSkinWrapper;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

/**
 * Created by zbl on 2017/8/24.
 * 分享设备，网关下子设备列表界面
 */

public class ShareDeviceChildListActivity extends BaseTitleActivity {
    private static final String GET_DATA = "GET_DATA";
    private static final String DATA_DEVICE = "DATA_DEVICE";
    private static final String DATA_USER = "DATA_USER";
    private static final String DATA_IS_INIT = "DATA_IS_INIT";
    private static final String DATA_GRANT_DEVICES = "DATA_GRANT_DEVICES";

    //过滤到不能分享的设备
    private static final String[] deviceFilter = {
            "22", "23", "32", "33", "37", "Aw", "Ax", "Ay", "Az",
            "Bb", "Be", "Bi", "Bj", "Bk", "Bl", "Cp"
    };
    private static HashSet<String> deviceFilterSet = new HashSet<>();

    static {
        for (int i = 0; i < deviceFilter.length; i++) {
            deviceFilterSet.add(deviceFilter[i]);
        }
    }

    private AuthApiUnit authApiUnit;
    private TextView tv_count_total, tv_count_selected;
    private ListView mListView;
    private TextView btn_save;
    private DeviceListAdapter adapter;
    private List<DeviceBean> deviceList = new ArrayList<>();
    private String gatewayId;
    private String authUserId;
    private boolean isSelectAll;
    private boolean isInit;//true 从授权账号界面进入，首次授权; false 从账号列表界面进入，更新子设备授权

    private boolean isListChanged = false;
//    private ButtonSkinWrapper buttonSkinWrapper;

    public static void start(Context context, DeviceBean device, UserBean userBean) {
        Intent intent = new Intent(context, ShareDeviceChildListActivity.class);
        intent.putExtra(DATA_DEVICE, device);
        intent.putExtra(DATA_USER, userBean);
        intent.putExtra(DATA_IS_INIT, false);
        context.startActivity(intent);
    }

    public static void startForResult(Activity context, DeviceBean device, UserBean userBean, ArrayList<String> grantChildDevices, int requestCode) {
        Intent intent = new Intent(context, ShareDeviceChildListActivity.class);
        intent.putExtra(DATA_DEVICE, device);
        intent.putExtra(DATA_USER, userBean);
        intent.putExtra(DATA_IS_INIT, true);
        intent.putExtra(DATA_GRANT_DEVICES, grantChildDevices);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharedevice_child_list, true);
    }

    @Override
    protected void initTitle() {
        setToolBarTitleAndRightBtn(getString(R.string.Device_Limit), getString(R.string.CateEye_Album_caputure_select));
    }

    @Override
    protected void initView() {
        tv_count_total = (TextView) findViewById(R.id.tv_count_total);
        tv_count_selected = (TextView) findViewById(R.id.tv_count_selected);
        btn_save = (TextView) findViewById(R.id.btn_save);
        mListView = (ListView) findViewById(R.id.listView);
        adapter = new DeviceListAdapter(this, deviceList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DeviceBean deviceBean = deviceList.get(position);
                deviceBean.isSelect = !deviceBean.isSelect;
                adapter.notifyDataSetChanged();
                if (!isListChanged) {
                    isListChanged = true;
                    setSaveButtonActive(true);
                }
                updateCountText();
                if (deviceBean.isSelect) {
                    for (DeviceBean bean : deviceList) {
                        if (!bean.isSelect) {
                            return;
                        }
                    }
                    setToolbarRightButtonStatus(true);
                } else {
                    setToolbarRightButtonStatus(false);
                }
            }
        });
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
//        buttonSkinWrapper = new ButtonSkinWrapper(btn_save);
        setSaveButtonActive(false);
    }

    @Override
    protected void initListeners() {
        btn_save.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        DeviceBean gatewayDeviceBean = getIntent().getParcelableExtra(DATA_DEVICE);
        UserBean userBean = getIntent().getParcelableExtra(DATA_USER);
        isInit = getIntent().getBooleanExtra(DATA_IS_INIT, false);
        if (gatewayDeviceBean == null || userBean == null || TextUtils.isEmpty(gatewayDeviceBean.deviceId) || TextUtils.isEmpty(userBean.uId)) {
            return;
        }
        gatewayId = gatewayDeviceBean.deviceId;
        authUserId = userBean.uId;
        authApiUnit = new AuthApiUnit(this);
        if (isInit) {
            ArrayList<String> grantChildDevices = getIntent().getStringArrayListExtra(DATA_GRANT_DEVICES);
            HashSet<String> seletedDevicesSet = new HashSet<>();
            if (grantChildDevices != null) {
                seletedDevicesSet.addAll(grantChildDevices);
            }
            deviceList.clear();
            DeviceCache deviceCache = MainApplication.getApplication().getDeviceCache();
            List<Device> devices = deviceCache.getZigBeeDevices();
            for (Device device : devices) {
                if (!deviceFilterSet.contains(device.type)) {
                    if (isDeviceForbidden(device)) {
                        continue;
                    }
                    DeviceBean deviceBean = new DeviceBean();
                    deviceBean.deviceId = device.devID;
                    deviceBean.type = device.type;
                    deviceBean.name = device.name;
                    deviceBean.isSelect = grantChildDevices == null ? true : seletedDevicesSet.contains(deviceBean.deviceId);
                    deviceList.add(deviceBean);
                }
            }
            boolean selectAll = true;
            for (DeviceBean bean : deviceList) {
                if (!bean.isSelect) {
                    selectAll = false;
                    break;
                }
            }
            setToolbarRightButtonStatus(selectAll);
            updateCountText();
            adapter.swapData(deviceList);
        } else {
            ProgressDialogManager.getDialogManager().showDialog(GET_DATA, this, null, null, getResources().getInteger(R.integer.http_timeout));
            authApiUnit.doGetAuthChildDevices(gatewayDeviceBean.deviceId, userBean.uId, new AuthApiUnit.AuthApiCommonListener<List<String>>() {
                @Override
                public void onSuccess(List<String> childDeviceIdList) {
                    deviceList.clear();
                    HashSet<String> childDeviceIdSet = new HashSet<>();
                    if (childDeviceIdList != null && childDeviceIdList.size() > 0) {
                        childDeviceIdSet.addAll(childDeviceIdList);
                    }
                    DeviceCache deviceCache = MainApplication.getApplication().getDeviceCache();
                    List<Device> devices = deviceCache.getZigBeeDevices();
                    for (Device device : devices) {
                        if (!deviceFilterSet.contains(device.type)) {
                            DeviceBean deviceBean = new DeviceBean();
                            deviceBean.deviceId = device.devID;
                            deviceBean.type = device.type;
                            deviceBean.name = device.name;
                            deviceBean.isSelect = childDeviceIdSet.contains(deviceBean.deviceId);
                            deviceList.add(deviceBean);
                        }
                    }
                    boolean selectAll = true;
                    for (DeviceBean bean : deviceList) {
                        if (!bean.isSelect) {
                            selectAll = false;
                            break;
                        }
                    }
                    setToolbarRightButtonStatus(selectAll);
                    updateCountText();
                    adapter.swapData(deviceList);
                    ProgressDialogManager.getDialogManager().dimissDialog(GET_DATA, 0);
                }

                @Override
                public void onFail(int code, String msg) {
                    ProgressDialogManager.getDialogManager().dimissDialog(GET_DATA, 0);
                    ToastUtil.show(msg);
                }
            });
        }
    }

    private void setToolbarRightButtonStatus(boolean seletAll) {
        if (isSelectAll != seletAll) {
            this.isSelectAll = seletAll;
            getBtn_right().setText(seletAll ? R.string.All_Not : R.string.CateEye_Album_Tittle_Select_All);
        }
    }

    private boolean isDeviceForbidden(Device device) {
        DeviceForbiddenBean deviceForbiddenBean;
        String forbiddenDevice = mainApplication.forbiddenDevice;
        if (!TextUtils.isEmpty(forbiddenDevice)) {
            deviceForbiddenBean = JSON.parseObject(forbiddenDevice, DeviceForbiddenBean.class);
            if (deviceForbiddenBean != null) {
                if (deviceForbiddenBean.getStatus() == 0 && deviceForbiddenBean.getType() == 1) {
                    if (TextUtils.equals(preference.getCurrentGatewayID(), device.gwID)) {
                        for (String devId :
                                deviceForbiddenBean.getDevIDs()) {
                            if (TextUtils.equals(devId, device.devID)) {
                                return true;
                            }
                        }
                    }
                }
            }

        }
        return false;
    }

    private void updateCountText() {
        tv_count_total.setText(String.format(getString(R.string.Device_All_Data), deviceList.size() + ""));
        int sum = 0;
        for (DeviceBean deviceBean : deviceList) {
            if (deviceBean.isSelect) {
                sum += 1;
            }
        }
        tv_count_selected.setText(String.format(getString(R.string.Device_Shared_Data), sum + ""));
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.btn_right) {
            for (DeviceBean deviceBean : deviceList) {
                deviceBean.isSelect = !isSelectAll;
            }
            adapter.notifyDataSetChanged();
            setToolbarRightButtonStatus(!isSelectAll);
            updateCountText();
            if (!isListChanged) {
                isListChanged = true;
                setSaveButtonActive(true);
            }
        } else if (v == btn_save) {
            if (isInit) {
                returnShareChildDevices();
            } else {
                saveShareChildDevices();
            }
        }
    }

    private void returnShareChildDevices() {
        ArrayList<String> grantChildDevices = new ArrayList<>();
        ArrayList<String> unGrantChildDevices = new ArrayList<>();
        for (DeviceBean deviceBean : deviceList) {
            if (deviceBean.isSelect) {
                grantChildDevices.add(deviceBean.deviceId);
            } else {
                unGrantChildDevices.add(deviceBean.deviceId);
            }
        }
        if (unGrantChildDevices.size() == 0) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Intent intent = new Intent();
            intent.putExtra("grantChildDevices", grantChildDevices);
            intent.putExtra("unGrantChildDevices", unGrantChildDevices);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void saveShareChildDevices() {
        ArrayList<String> grantChildDevices = new ArrayList<>();
        ArrayList<String> unGrantChildDevices = new ArrayList<>();
        for (DeviceBean deviceBean : deviceList) {
            if (deviceBean.isSelect) {
                grantChildDevices.add(deviceBean.deviceId);
            } else {
                unGrantChildDevices.add(deviceBean.deviceId);
            }
        }
        AuthApiUnit.AuthApiCommonListener listener = new AuthApiUnit.AuthApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                ProgressDialogManager.getDialogManager().dimissDialog(GET_DATA, 0);
                finish();
            }

            @Override
            public void onFail(int code, String msg) {
                ProgressDialogManager.getDialogManager().dimissDialog(GET_DATA, 0);
                ToastUtil.show(msg);
            }
        };
        ProgressDialogManager.getDialogManager().showDialog(GET_DATA, this, null, null, getResources().getInteger(R.integer.http_timeout));
        if (unGrantChildDevices.size() == 0) {
            authApiUnit.doAuthAccount(gatewayId, authUserId, "2", null, null, "1", listener);
        } else {
            authApiUnit.doAuthAccount(gatewayId, authUserId, "1", grantChildDevices, unGrantChildDevices, "1", listener);
        }
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
                holder.ivDeviceinfo = (ImageView) convertView.findViewById(R.id.iv_device_info);
                holder.layout_info = convertView.findViewById(R.id.layout_info);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            DeviceBean deviceBean = mData.get(position);
            holder.ivDeviceIcon.setImageResource(DeviceInfoDictionary.getIconByType(deviceBean.getType()));
            holder.tvDeviceName.setText(DeviceInfoDictionary.getNameByTypeAndName(deviceBean.getType(), deviceBean.getName()));
            holder.layout_info.setVisibility(View.GONE);
            holder.ivDeviceinfo.setImageResource(deviceBean.isSelect ? R.drawable.icon_data_selected : R.drawable.icon_data_not_selected);
            return convertView;
        }
    }

    public final class ViewHolder {
        public ImageView ivDeviceIcon;
        public TextView tvDeviceName;
        public TextView tvDeviceType;
        public TextView tvDeviceArea;
        public ImageView ivDeviceinfo;
        public View layout_info;
    }

    public void setSaveButtonActive(boolean isActive) {
        if (isActive) {
            btn_save.setTextColor(getResources().getColor(R.color.colorPrimary));
            btn_save.setEnabled(true);
        } else {
            btn_save.setTextColor(getResources().getColor(R.color.grey));
            btn_save.setEnabled(false);
        }
    }
}
