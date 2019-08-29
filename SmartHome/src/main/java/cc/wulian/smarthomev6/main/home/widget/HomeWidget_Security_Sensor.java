package cc.wulian.smarthomev6.main.home.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

import net.sourceforge.pinyin4j.PinyinHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.device.Attribute;
import cc.wulian.smarthomev6.support.core.device.Cluster;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.device.Endpoint;
import cc.wulian.smarthomev6.support.core.device.EndpointParser;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GetRoomListEvent;
import cc.wulian.smarthomev6.support.event.RoomInfoEvent;

/**
 * Created by luzx on 2017/8/24
 * Function: 安防传感器 首页widget
 */

public class HomeWidget_Security_Sensor extends RelativeLayout implements IWidgetLifeCycle, View.OnClickListener {

    private static final String TAG = HomeWidget_Security_Sensor.class.getSimpleName();
    private Context mContext;
    private RecyclerView rv_sensor_list;
    private List<Device> sensorDevices;
    private List<String> sensorTypes;
    private SensorAdapter mSensorAdapter;
    private PopupAdapter mPopupAdapter;
    private View all_btn;
    private TextView categoryTextView;
    private PopupWindow mPopupWindow;
    private int screenWidth;

    public HomeWidget_Security_Sensor(Context context) {
        super(context);
        mContext = context;
        initView(context);
    }

    public HomeWidget_Security_Sensor(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context);
    }

    private void initView(Context context) {
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        View rootView = LayoutInflater.from(context).inflate(R.layout.view_home_securty_sensor, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        rv_sensor_list = (RecyclerView) rootView.findViewById(R.id.sensor_list);
        categoryTextView = (TextView) rootView.findViewById(R.id.tv_categroy);
        all_btn = rootView.findViewById(R.id.all_btn);
        all_btn.setOnClickListener(this);
    }

    @Override
    public void onBindViewHolder(HomeItemBean bean) {
        EventBus.getDefault().register(this);
        initAllTypes();
        initData(sensorTypes.get(MainApplication.getApplication().WidgetSecurityPosition));
        categoryTextView.setText("all".equals(sensorTypes.get(MainApplication.getApplication().WidgetSecurityPosition)) ? R.string.Device_All : DeviceInfoDictionary.getDefaultNameByType(sensorTypes.get(MainApplication.getApplication().WidgetSecurityPosition)));
        mSensorAdapter = new SensorAdapter();
        rv_sensor_list.setAdapter(mSensorAdapter);
        rv_sensor_list.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
    }

    private void initAllTypes() {
        List<Device> devices = MainApplication.getApplication().getDeviceCache().getZigBeeDevices();
        if (sensorTypes == null) {
            sensorTypes = new ArrayList<>();
        } else {
            sensorTypes.clear();
        }
        sensorTypes.add("all");
        for (Device device : devices) {
            switch (device.type) {
                case "02":// 红外入侵监测器
                    if (!sensorTypes.contains(device.type)) {
                        sensorTypes.add(device.type);
                    }
                    break;
                case "03":// 门磁感应器
                    if (!sensorTypes.contains(device.type)) {
                        sensorTypes.add(device.type);
                    }
                    break;
                case "06":// 水浸传感器
                    if (!sensorTypes.contains(device.type)) {
                        sensorTypes.add(device.type);
                    }
                    break;
                case "43":// 烟雾探测器
                    if (!sensorTypes.contains(device.type)) {
                        sensorTypes.add(device.type);
                    }
                    break;
                case "09":// 可燃气体泄漏探测器
                    if (!sensorTypes.contains(device.type)) {
                        sensorTypes.add(device.type);
                    }
                    break;
                case "a1":// 幕帘探测器
                    if (!sensorTypes.contains(device.type)) {
                        sensorTypes.add(device.type);
                    }
                    break;
//                case "Ad":// 多功能红外人体探测器（电池版）
//                    if (!sensorTypes.contains(device.type)) {
//                        sensorTypes.add(device.type);
//                    }
//                    break;
//                case "C0":// 多功能红外人体探测器（强电版）
//                    if (!sensorTypes.contains(device.type)) {
//                        sensorTypes.add(device.type);
//                    }
//                    break;
            }
        }
    }

    private void initData(String type) {
        if (TextUtils.isEmpty(type)) {
            type = "all";
        }
        List<Device> devices = MainApplication.getApplication().getDeviceCache().getZigBeeDevices();
        if (sensorDevices == null) {
            sensorDevices = new ArrayList<>();
        } else {
            sensorDevices.clear();
        }
        if ("all".equals(type)) {
            if (sensorTypes == null) {
                sensorTypes = new ArrayList<>();
            } else {
                sensorTypes.clear();
            }
            sensorTypes.add("all");
            for (Device device : devices) {
                switch (device.type) {
                    case "02":// 红外入侵监测器
                        sensorDevices.add(device);
                        if (!sensorTypes.contains(device.type)) {
                            sensorTypes.add(device.type);
                        }
                        break;
                    case "03":// 门磁感应器
                        sensorDevices.add(device);
                        if (!sensorTypes.contains(device.type)) {
                            sensorTypes.add(device.type);
                        }
                        break;
                    case "06":// 水浸传感器
                        sensorDevices.add(device);
                        if (!sensorTypes.contains(device.type)) {
                            sensorTypes.add(device.type);
                        }
                        break;
                    case "43":// 烟雾探测器
                        sensorDevices.add(device);
                        if (!sensorTypes.contains(device.type)) {
                            sensorTypes.add(device.type);
                        }
                        break;
                    case "09":// 可燃气体泄漏探测器
                        sensorDevices.add(device);
                        if (!sensorTypes.contains(device.type)) {
                            sensorTypes.add(device.type);
                        }
                        break;
                    case "a1":// 幕帘探测器
                        sensorDevices.add(device);
                        if (!sensorTypes.contains(device.type)) {
                            sensorTypes.add(device.type);
                        }
                        break;
//                    case "Ad":// 多功能红外人体探测器（电池版）
//                        sensorDevices.add(device);
//                        if (!sensorTypes.contains(device.type)) {
//                            sensorTypes.add(device.type);
//                        }
//                        break;
//                    case "C0":// 多功能红外人体探测器（强电版）
//                        sensorDevices.add(device);
//                        if (!sensorTypes.contains(device.type)) {
//                            sensorTypes.add(device.type);
//                        }
//                        break;
                }
            }
        } else {
            for (Device device : devices) {
                if (type.equals(device.type)) {
                    sensorDevices.add(device);
                }
            }
        }
        if (sensorDevices.size() == 0) {
//            EventBus.getDefault().post(new HomeDeviceWidgetChangeEvent());
        } else {
            Collections.sort(sensorDevices, new Comparator<Device>() {
                @Override
                public int compare(Device o1, Device o2) {
                    return getFirstChar(o1.name).compareTo(getFirstChar(o2.name));
                }
            });
        }
    }

    // 获得字符串的首字母 首字符 转汉语拼音
    public String getFirstChar(String value) {
        // 首字符
        char firstChar = value.charAt(0);
        // 首字母分类
        String first = null;
        // 是否是非汉字
        String[] print = PinyinHelper.toHanyuPinyinStringArray(firstChar);

        if (print == null) {
            // 将小写字母改成大写
            if ((firstChar >= 97 && firstChar <= 122)) {
                firstChar -= 32;
            }
            if (firstChar >= 65 && firstChar <= 90) {
                first = String.valueOf((char) firstChar);
            } else {
                // 认为首字符为数字或者特殊字符
                first = "#";
            }
        } else {
            // 如果是中文 分类大写字母
            first = String.valueOf((char) (print[0].charAt(0) - 32));
        }
        if (first == null) {
            first = "?";
        }
        return first;
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && sensorDevices != null) {
            if (HomeWidgetManager.isSecuritySensorDevice(event.device.type)) {
                initData(sensorTypes.get(MainApplication.getApplication().WidgetSecurityPosition));
                mSensorAdapter.notifyDataSetChanged();
            }
        } else if (event.device == null) {
//            initData(null);
//            mSensorAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceInfoChanged(DeviceInfoChangedEvent event) {
        if (event.deviceInfoBean != null && sensorDevices != null) {
            for (Device device : sensorDevices) {
                if (TextUtils.equals(event.deviceInfoBean.devID, device.devID)) {
                    initData(sensorTypes.get(MainApplication.getApplication().WidgetSecurityPosition));
                    mSensorAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRoomInfoEvent(RoomInfoEvent event) {
        initData(sensorTypes.get(MainApplication.getApplication().WidgetSecurityPosition));
        mSensorAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetRoomListEvent(GetRoomListEvent event) {
        initData(sensorTypes.get(MainApplication.getApplication().WidgetSecurityPosition));
        mSensorAdapter.notifyDataSetChanged();
    }

    private void sendCmd(final Device device) {
        if (device.isOnLine()) {
            final JSONObject object = new JSONObject();
            object.put("cmd", "502");
            object.put("gwID", device.gwID);
            object.put("devID", device.devID);
            object.put("mode", 0);
            EndpointParser.parse(device, new EndpointParser.ParserCallback() {
                @Override
                public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                    //endpointStatus:0.撤防，1.设防
//                    if ("Ad".equals(device.type) || "C0".equals(device.type)) {//多功能红外探测器有多个endpoint
//                        if (cluster.clusterId == 1280) {
//                            if ("0".equals(endpoint.endpointStatus)) {
//                                object.put("endpointStatus", "1");
//                            } else if ("1".equals(endpoint.endpointStatus)) {
//                                object.put("endpointStatus", "0");
//                            }
//                            object.put("endpointNumber", endpoint.endpointNumber);
//                            return;
//                        }
//                    } else {
                        if ("0".equals(endpoint.endpointStatus)) {
                            object.put("endpointStatus", "1");
                        } else if ("1".equals(endpoint.endpointStatus)) {
                            object.put("endpointStatus", "0");
                        }
                        object.put("endpointNumber", endpoint.endpointNumber);
//                    }
                }
            });
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.all_btn:
                showPopupWindow(v);
                break;
        }
    }

    private void showPopupWindow(View tabItem) {
        int width = Math.round(screenWidth / 2f);
        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow(this);
            View view = LayoutInflater.from(mContext).inflate(R.layout.security_sensor_type_popup_view,
                    null);
            ListView mListView = (ListView) view.findViewById(R.id.type_list_view);
            mPopupWindow.setContentView(view);
            mPopupWindow.setWidth(width);
            mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            // 设置背景，触摸框外区域也可以关闭弹出框
            mPopupWindow.setBackgroundDrawable(new ColorDrawable());
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setFocusable(true);
            mPopupAdapter = new PopupAdapter();
            mListView.setAdapter(mPopupAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (MainApplication.getApplication().WidgetSecurityPosition != position) {
                        categoryTextView.setText("all".equals(sensorTypes.get(position)) ? R.string.Device_All : DeviceInfoDictionary.getDefaultNameByType(sensorTypes.get(position)));
                        MainApplication.getApplication().WidgetSecurityPosition = position;
                        mPopupAdapter.notifyDataSetChanged();
                        initData(sensorTypes.get(position));
                        mSensorAdapter.notifyDataSetChanged();
                    }
                    mPopupWindow.dismiss();
                }
            });
        }
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        } else {
            int dp10 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
            mPopupWindow.showAsDropDown(tabItem, dp10, -tabItem.getHeight() - dp10, Gravity.RIGHT);
            mPopupWindow.update();
        }
    }

    class PopupAdapter extends BaseAdapter {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext)
                        .inflate(R.layout.item_security_sensor_type_view, null);
                viewHolder.typeName = (TextView) convertView
                        .findViewById(R.id.type_name);
                viewHolder.checkIcon = (ImageView) convertView
                        .findViewById(R.id.check_icon);
                viewHolder.line = convertView
                        .findViewById(R.id.line);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (position == 0) {
                viewHolder.line.setVisibility(View.GONE);
            } else {
                viewHolder.line.setVisibility(View.VISIBLE);
            }
            int typeNameRes = "all".equals(sensorTypes.get(position)) ? R.string.Device_All : DeviceInfoDictionary.getDefaultNameByType(sensorTypes.get(position));
            String typeName = getResources().getString(typeNameRes);
            viewHolder.typeName.setText(typeName);
            if (MainApplication.getApplication().WidgetSecurityPosition == position) {
                viewHolder.checkIcon.setVisibility(View.VISIBLE);
            } else {
                viewHolder.checkIcon.setVisibility(View.INVISIBLE);
            }
            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public int getCount() {
            return sensorTypes.size();
        }

        class ViewHolder {
            TextView typeName;
            ImageView checkIcon;
            View line;
        }
    }

    class SensorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private int iconDefenceRes;
        private int iconNotDefenceRes;
        private int iconOfflineRes;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View itemView = layoutInflater.inflate(R.layout.item_sensor_view, parent, false);
            return new ItemHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
            if (viewHolder instanceof ItemHolder) {
                if (position == 0) {
                    ((ItemHolder) viewHolder).itemDecoration.setVisibility(View.GONE);
                } else {
                    ((ItemHolder) viewHolder).itemDecoration.setVisibility(View.VISIBLE);
                }
                final Device device = sensorDevices.get(position);
                ((ItemHolder) viewHolder).name.setText(device.name);
                String areaName = ((MainApplication) mContext.getApplicationContext()).getRoomCache().getRoomName(device.roomID);
                ((ItemHolder) viewHolder).areaName.setText(areaName);
                switch (device.type) {
                    case "06":// 水浸传感器
                        iconDefenceRes = R.drawable.icon_water_defence;
                        iconNotDefenceRes = R.drawable.icon_water_not_defence;
                        iconOfflineRes = R.drawable.icon_water_offline;
                        break;
                    case "09":// 可燃气体泄漏探测器
                        iconDefenceRes = R.drawable.icon_fire_defence;
                        iconNotDefenceRes = R.drawable.icon_fire_not_defence;
                        iconOfflineRes = R.drawable.icon_fire_offline;
                        break;
                    case "43":// 烟雾探测器
                        iconDefenceRes = R.drawable.icon_fog_defence;
                        iconNotDefenceRes = R.drawable.icon_fog_not_defence;
                        iconOfflineRes = R.drawable.icon_fog_offline;
                        break;
                    case "03":// 门磁感应器
                        iconDefenceRes = R.drawable.icon_door_defence;
                        iconNotDefenceRes = R.drawable.icon_door_not_defence;
                        iconOfflineRes = R.drawable.icon_door_offline;
                        break;
                    case "02":// 红外入侵监测器
//                    case "C0"://多功能红外人体探测器（强电版）
//                    case "Ad"://多功能红外人体探测器（电池版）
                    case "a1"://幕帘探测器
                        iconDefenceRes = R.drawable.icon_infrared_defence;
                        iconNotDefenceRes = R.drawable.icon_infrared_not_defence;
                        iconOfflineRes = R.drawable.icon_infrared_offline;
                        break;
                }
                if (device.mode == 2) {
                    //离线
                    ((ItemHolder) viewHolder).icon.setImageResource(iconOfflineRes);
                    ((ItemHolder) viewHolder).stateIcon.setImageResource(R.drawable.icon_not_defence_offline);
                    ((ItemHolder) viewHolder).contentView.setBackgroundResource(R.drawable.selector_security_sensor_offline);
                    ((ItemHolder) viewHolder).contentView.setEnabled(false);
                } else {
                    ((ItemHolder) viewHolder).contentView.setEnabled(true);
                    EndpointParser.parse(device, new EndpointParser.ParserCallback() {
                        @Override
                        public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                            //endpointStatus:0.撤防，1.设防
//                            if ("Ad".equals(device.type) || "C0".equals(device.type)) {
//                                if (cluster.clusterId == 1280) {
//                                    if ("0".equals(endpoint.endpointStatus)) {
//                                        ((ItemHolder) viewHolder).icon.setImageResource(iconNotDefenceRes);
//                                        ((ItemHolder) viewHolder).stateIcon.setImageResource(R.drawable.icon_not_defence);
//                                        ((ItemHolder) viewHolder).contentView.setBackgroundResource(R.drawable.selector_security_sensor_not_defence);
//                                    } else if ("1".equals(endpoint.endpointStatus)) {
//                                        ((ItemHolder) viewHolder).icon.setImageResource(iconDefenceRes);
//                                        ((ItemHolder) viewHolder).stateIcon.setImageResource(R.drawable.icon_defence);
//                                        ((ItemHolder) viewHolder).contentView.setBackgroundResource(R.drawable.selector_security_sensor_defence);
//                                    }
//                                    return;
//                                }
//                            } else {
                                if ("0".equals(endpoint.endpointStatus)) {
                                    ((ItemHolder) viewHolder).icon.setImageResource(iconNotDefenceRes);
                                    ((ItemHolder) viewHolder).stateIcon.setImageResource(R.drawable.icon_not_defence);
                                    ((ItemHolder) viewHolder).contentView.setBackgroundResource(R.drawable.selector_security_sensor_not_defence);
                                } else if ("1".equals(endpoint.endpointStatus)) {
                                    ((ItemHolder) viewHolder).icon.setImageResource(iconDefenceRes);
                                    ((ItemHolder) viewHolder).stateIcon.setImageResource(R.drawable.icon_defence);
                                    ((ItemHolder) viewHolder).contentView.setBackgroundResource(R.drawable.selector_security_sensor_defence);
                                }
//                            }
                        }
                    });
                }
            }
        }

        @Override
        public int getItemCount() {
            return sensorDevices.size();
        }

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            super.onViewRecycled(holder);
        }

        class ItemHolder extends RecyclerView.ViewHolder {

            private TextView name;

            private TextView areaName;

            private ImageView icon;

            private ImageView stateIcon;

            private View itemDecoration;

            private View contentView;

            public ItemHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.name);
                areaName = (TextView) itemView.findViewById(R.id.area_name);
                icon = (ImageView) itemView.findViewById(R.id.sensor_icon);
                stateIcon = (ImageView) itemView.findViewById(R.id.sensor_state_icon);
                itemDecoration = itemView.findViewById(R.id.item_decoration);
                contentView = itemView.findViewById(R.id.content_view);
                ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
                int width = (int) ((screenWidth - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics())) * 0.4);
                layoutParams.width = width;
                contentView.setLayoutParams(layoutParams);
                contentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendCmd(sensorDevices.get(getAdapterPosition()));
                    }
                });
            }
        }
    }
}
