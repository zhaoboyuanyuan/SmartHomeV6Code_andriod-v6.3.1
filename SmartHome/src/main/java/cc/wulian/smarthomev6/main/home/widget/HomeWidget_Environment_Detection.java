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
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GetRoomListEvent;
import cc.wulian.smarthomev6.support.event.HomeDeviceWidgetChangeEvent;
import cc.wulian.smarthomev6.support.event.RoomInfoEvent;

/**
 * Created by 上海滩小马哥 on 2017/10/12.
 */

public class HomeWidget_Environment_Detection extends RelativeLayout implements IWidgetLifeCycle, View.OnClickListener {

    private static final int GOOD = 1;
    private static final int NORMAL = 2;
    private static final int BAD = 3;
    private Context mContext;
    private View all_btn;
    private TextView categoryTextView;
    private RecyclerView rv_sensor_list;
    private SensorAdapter mSensorAdapter;
    private PopupWindow mPopupWindow;
    private PopupAdapter mPopupAdapter;

    private List<Device> sensorDevices;
    private List<String> sensorTypes;
    private int screenWidth;

    public HomeWidget_Environment_Detection(Context context) {
        super(context);
        mContext = context;
        initView(context);
    }

    public HomeWidget_Environment_Detection(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context);
    }

    @Override
    public void onBindViewHolder(HomeItemBean bean) {
        EventBus.getDefault().register(this);
        initAllTypes();
        initData(sensorTypes.get(MainApplication.getApplication().WidgetEnvironmentPosition));
        categoryTextView.setText("all".equals(sensorTypes.get(MainApplication.getApplication().WidgetEnvironmentPosition)) ? R.string.Device_All : DeviceInfoDictionary.getDefaultNameByType(sensorTypes.get(MainApplication.getApplication().WidgetEnvironmentPosition)));
        mSensorAdapter = new SensorAdapter();
        rv_sensor_list.setAdapter(mSensorAdapter);
        rv_sensor_list.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.all_btn:
                showPopupWindow(v);
                break;
        }
    }

    private void initView(Context context) {
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        View rootView = LayoutInflater.from(context).inflate(R.layout.view_home_environment_detection, null);
        addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        rv_sensor_list = (RecyclerView) rootView.findViewById(R.id.sensor_list);
        categoryTextView = (TextView) rootView.findViewById(R.id.tv_categroy);
        all_btn = rootView.findViewById(R.id.all_btn);
        all_btn.setOnClickListener(this);
    }

    private void initAllTypes(){
        List<Device> devices = MainApplication.getApplication().getDeviceCache().getZigBeeDevices();
        if (sensorTypes == null) {
            sensorTypes = new ArrayList<>();
        } else {
            sensorTypes.clear();
        }
        sensorTypes.add("all");
        for (Device device : devices) {
            switch (device.type) {
                case "A0":// 梦想之花 二氧化碳
                    if (!sensorTypes.contains("42")) {
                        sensorTypes.add("42");
                    }
                    break;
                case "42":// CO2检测器
                case "17":// 温湿度传感器
                case "D4":// 梦想之花 噪声
                case "D5":// 梦想之花 粉尘
                case "D6":// 梦想之花 VOC
                case "44":// 粉尘01型
                    if (!sensorTypes.contains(device.type)) {
                        sensorTypes.add(device.type);
                    }
                    break;
                case "19":// 光强检测器
                    if (!sensorTypes.contains(device.type)) {
                        sensorTypes.add(device.type);
                    }
                    break;
                case "Og":// PM2.5监测仪
                    if (!sensorTypes.contains(device.type)) {
                        sensorTypes.add(device.type);
                    }
                    break;
                default:
                    break;
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
                    case "A0":// 梦想之花 二氧化碳
                        sensorDevices.add(device);
                        if (!sensorTypes.contains("42")) {
                            sensorTypes.add("42");
                        }
                        break;
                    case "42":// CO2检测器
                    case "17":// 温湿度传感器
                    case "D4":// 梦想之花 噪声
                    case "D5":// 梦想之花 粉尘
                    case "D6":// 梦想之花 VOC
                    case "44":// 粉尘01型
                        sensorDevices.add(device);
                        if (!sensorTypes.contains(device.type)) {
                            sensorTypes.add(device.type);
                        }
                        break;
                    case "19":// 光强检测器
                        sensorDevices.add(device);
                        if (!sensorTypes.contains(device.type)) {
                            sensorTypes.add(device.type);
                        }
                        break;
                    case "Og":// PM2.5监测仪
                        sensorDevices.add(device);
                        if (!sensorTypes.contains(device.type)) {
                            sensorTypes.add(device.type);
                        }
                        break;
                    default:
                        break;
                }
            }
        } else {
            for (Device device : devices) {
                if (TextUtils.equals(type, "42") || TextUtils.equals(type, "A0")){
                    if ("42".equals(device.type) || "A0".equals(device.type)) {
                        sensorDevices.add(device);
                    }
                }else {
                    if (type.equals(device.type)) {
                        sensorDevices.add(device);
                    }
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
                    if (MainApplication.getApplication().WidgetEnvironmentPosition != position) {
                        categoryTextView.setText("all".equals(sensorTypes.get(position)) ? R.string.Device_All : DeviceInfoDictionary.getDefaultNameByType(sensorTypes.get(position)));
                        MainApplication.getApplication().WidgetEnvironmentPosition = position;
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
            if (MainApplication.getApplication().WidgetEnvironmentPosition == position) {
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

        private int iconOnlineRes;
        private int iconOfflineRes;
        String value1 = null;
        String value2 = null;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View itemView = layoutInflater.inflate(R.layout.item_environment_sensor_view, parent, false);
            return new SensorAdapter.ItemHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
            if (viewHolder instanceof SensorAdapter.ItemHolder) {
                if (position == 0) {
                    ((SensorAdapter.ItemHolder) viewHolder).itemDecoration.setVisibility(View.GONE);
                } else {
                    ((SensorAdapter.ItemHolder) viewHolder).itemDecoration.setVisibility(View.VISIBLE);
                }
                final Device device = sensorDevices.get(position);
                final String areaName = ((MainApplication) mContext.getApplicationContext()).getRoomCache().getRoomName(device.roomID);
                ((SensorAdapter.ItemHolder) viewHolder).name.setText(device.name);
                ((SensorAdapter.ItemHolder) viewHolder).area.setText("[" + areaName + "]");
                switch (device.type) {
                    case "42":// CO2检测器
                    case "A0"://
                        iconOfflineRes = R.drawable.icon_co2_offline;
                        break;
                    case "17":// 温湿度检测器
                        iconOfflineRes = R.drawable.icon_42_offline;
                        break;
                    case "19":// 光强检测器
                        iconOfflineRes = R.drawable.icon_19_offline;
                        break;
                    case "D4":// 噪声
                        iconOfflineRes = R.drawable.icon_noise_offline;
                        break;
                    case "D5":// 粉尘
                    case "44":// 粉尘
                        iconOfflineRes = R.drawable.icon_dust_offline;
                        break;
                    case "D6":// VOC
                        iconOfflineRes = R.drawable.icon_voc_offline;
                        break;
                    case "Og":// PM2.5检测器
                        iconOfflineRes = R.drawable.icon_og_offline;
                }
                if (device.mode == 2) {
                    //离线
                    ((SensorAdapter.ItemHolder) viewHolder).icon.setImageResource(iconOfflineRes);
                    ((SensorAdapter.ItemHolder) viewHolder).contentView.setBackgroundResource(R.drawable.selector_security_sensor_offline);
                    ((ItemHolder) viewHolder).valueOne.setText("--");
                    ((ItemHolder) viewHolder).unitOne.setVisibility(GONE);
                    ((ItemHolder) viewHolder).contentTwo.setVisibility(GONE);
                } else {
                    ((ItemHolder) viewHolder).unitOne.setVisibility(VISIBLE);
                    EndpointParser.parse(device, new EndpointParser.ParserCallback() {
                        @Override
                        public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                            ((ItemHolder) viewHolder).contentTwo.setVisibility(GONE);

                            if(cluster.clusterId == 2049 && "Og".equals(device.type)){
                                value1 = TextUtils.isEmpty(attribute.attributeValue)?"--":attribute.attributeValue;
                                ((ItemHolder) viewHolder).valueOne.setText(value1);
                                ((ItemHolder) viewHolder).unitOne.setText("ug/m3");

                                ViewGroup.LayoutParams layoutParams = ((ItemHolder) viewHolder).getContentView().getLayoutParams();
                                int width = (int) ((screenWidth - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics())) * 0.4);
                                layoutParams.width = width;
                                ((ItemHolder) viewHolder).getContentView().setLayoutParams(layoutParams);
                            }else if (endpoint.endpointNumber == 1) {
                                String unit = null;
                                switch (device.type) {
                                    case "42":// CO2检测器
                                    case "A0":// CO2检测器
                                    case "D6":// VOC
                                        unit = "ppm";
                                        break;
                                    case "17":// 温湿度检测器
                                        unit = "℃";
                                        if (attribute.attributeId != 0){
                                            return;
                                        }
                                        break;
                                    case "19":// 光强检测器
                                        unit = "lx";
                                        break;
                                    case "D4":// 噪声
                                        unit = "dB";
                                        break;
                                    case "D5":// 粉尘
                                    case "44":// 粉尘
                                        unit = "ug/m3";
                                        break;
                                }
                                value1 = TextUtils.isEmpty(attribute.attributeValue)?"--":attribute.attributeValue;
                                ((ItemHolder) viewHolder).valueOne.setText(value1);
                                ((ItemHolder) viewHolder).unitOne.setText(unit);

                                ViewGroup.LayoutParams layoutParams = ((ItemHolder) viewHolder).getContentView().getLayoutParams();
                                int width = (int) ((screenWidth - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics())) * 0.4);
                                layoutParams.width = width;
                                ((ItemHolder) viewHolder).getContentView().setLayoutParams(layoutParams);
                            } else if (endpoint.endpointNumber == 2) {
                                ((ItemHolder) viewHolder).contentTwo.setVisibility(VISIBLE);
                                String unit = null;
                                switch (device.type) {
                                    case "17":// 温湿度检测器
                                        if (attribute.attributeId != 0){
                                            return;
                                        }
                                        unit = "%";
                                        break;
                                }
                                value2 = TextUtils.isEmpty(attribute.attributeValue)?"--":attribute.attributeValue;
                                ((ItemHolder) viewHolder).valueTwo.setText(value2);
                                ((ItemHolder) viewHolder).unitTwo.setText(unit);

                                ViewGroup.LayoutParams layoutParams = ((ItemHolder) viewHolder).getContentView().getLayoutParams();
                                int width = (int) ((screenWidth - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics())) * 0.5);
                                layoutParams.width = width;
                                ((ItemHolder) viewHolder).getContentView().setLayoutParams(layoutParams);
                            }

                            //显示图标
                            int level = getLevel(device.type, value1, value2);
                            if (level == GOOD) {
                                ((SensorAdapter.ItemHolder) viewHolder).contentView.setBackgroundResource(R.drawable.selector_security_sensor_defence);
                                switch (device.type) {
                                    case "42":// CO2检测器
                                    case "A0":
                                        iconOnlineRes = R.drawable.icon_co2_good;
                                        break;
                                    case "D4":
                                        iconOnlineRes = R.drawable.icon_noise_good;
                                        break;
                                    case "D5":
                                    case "44":
                                        iconOnlineRes = R.drawable.icon_dust_good;
                                        break;
                                    case "D6":
                                        iconOnlineRes = R.drawable.icon_voc_good;
                                        break;
                                    case "17":// 温湿度检测器
                                        iconOnlineRes = R.drawable.icon_42_good;
                                        break;
                                    case "19":// 光强监测器
                                        iconOnlineRes = R.drawable.icon_19_suitable;
                                        break;
                                    case "Og":// PM2.5监测器
                                        iconOnlineRes = R.drawable.icon_og_good;
                                        break;
                                }
                            } else if (level == NORMAL) {
                                ((SensorAdapter.ItemHolder) viewHolder).contentView.setBackgroundResource(R.drawable.selector_security_sensor_not_defence);
                                switch (device.type) {
                                    case "42":// CO2检测器
                                    case "A0":
                                        iconOnlineRes = R.drawable.icon_co2_normal;
                                        break;
                                    case "D4":
                                        iconOnlineRes = R.drawable.icon_noise_normal;
                                        break;
                                    case "D5":
                                    case "44":
                                        iconOnlineRes = R.drawable.icon_dust_normal;
                                        break;
                                    case "D6":
                                        iconOnlineRes = R.drawable.icon_voc_normal;
                                        break;
                                    case "17":// 温湿度检测器
                                        iconOnlineRes = R.drawable.icon_42_normal;
                                        break;
                                    case "19":// 光强监测器
                                        iconOnlineRes = R.drawable.icon_19_weak;
                                        break;
                                    case "Og":// PM2.5监测器
                                        iconOnlineRes = R.drawable.icon_og_medium;
                                        break;
                                }
                            } else if (level == BAD) {
                                switch (device.type) {
                                    case "42":// CO2检测器
                                    case "A0":
                                        iconOnlineRes = R.drawable.icon_co2_bad;
                                        ((SensorAdapter.ItemHolder) viewHolder).contentView.setBackgroundResource(R.drawable.selector_environment_red);
                                        break;
                                    case "D4":
                                        iconOnlineRes = R.drawable.icon_noise_bad;
                                        ((SensorAdapter.ItemHolder) viewHolder).contentView.setBackgroundResource(R.drawable.selector_environment_red);
                                        break;
                                    case "D5":
                                    case "44":
                                        iconOnlineRes = R.drawable.icon_dust_bad;
                                        ((SensorAdapter.ItemHolder) viewHolder).contentView.setBackgroundResource(R.drawable.selector_environment_red);
                                        break;
                                    case "D6":
                                        iconOnlineRes = R.drawable.icon_voc_bad;
                                        ((SensorAdapter.ItemHolder) viewHolder).contentView.setBackgroundResource(R.drawable.selector_environment_red);
                                        break;
                                    case "17":// 温湿度检测器
                                        iconOnlineRes = R.drawable.icon_42_bad;
                                        ((SensorAdapter.ItemHolder) viewHolder).contentView.setBackgroundResource(R.drawable.selector_environment_blue);
                                        break;
                                    case "19":// 光强监测器
                                        iconOnlineRes = R.drawable.icon_19_strong;
                                        ((SensorAdapter.ItemHolder) viewHolder).contentView.setBackgroundResource(R.drawable.selector_environment_red);
                                        break;
                                    case "Og":// PM2.5监测器
                                        iconOnlineRes = R.drawable.icon_og_bad;
                                        ((SensorAdapter.ItemHolder) viewHolder).contentView.setBackgroundResource(R.drawable.selector_environment_red);
                                        break;
                                }
                            }
                            ((SensorAdapter.ItemHolder) viewHolder).icon.setImageResource(iconOnlineRes);
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

            private TextView valueOne;
            private TextView unitOne;

            private TextView valueTwo;
            private TextView unitTwo;

            private TextView name;

            private TextView area;

            private ImageView icon;

            private View itemDecoration;

            private View contentView;

            private View contentOne;
            private View contentTwo;

            public ItemHolder(View itemView) {
                super(itemView);
                valueOne = (TextView) itemView.findViewById(R.id.value_one);
                valueTwo = (TextView) itemView.findViewById(R.id.value_two);
                unitOne = (TextView) itemView.findViewById(R.id.unit_one);
                unitTwo = (TextView) itemView.findViewById(R.id.unit_two);
                name = (TextView) itemView.findViewById(R.id.name);
                area = (TextView) itemView.findViewById(R.id.area);
                icon = (ImageView) itemView.findViewById(R.id.sensor_icon);
                itemDecoration = itemView.findViewById(R.id.item_decoration);
                contentOne = itemView.findViewById(R.id.content_one);
                contentTwo = itemView.findViewById(R.id.content_two);
                contentView = itemView.findViewById(R.id.content_view);
                ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
                int width = (int) ((screenWidth - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics())) * 0.4);
                layoutParams.width = width;
                contentView.setLayoutParams(layoutParams);
            }

            public View getContentView() {
                return contentView;
            }
        }
    }

    private int getLevel(String type, String value_one, String value_two) {
        float value1 = 0;
        float value2 = 0;

        try {
            value1 = Float.valueOf(value_one);
        } catch (Exception e) {
            e.printStackTrace();
            return GOOD;
        }

        switch (type) {
            case "17":
                if (TextUtils.isEmpty(value_two)) {
                    return 0;
                }
                try {
                    value2 = Float.valueOf(value_two);
                } catch (Exception e) {
                    e.printStackTrace();
                    return GOOD;
                }
                if (value2 >= 80 && value2 <= 100 && value1 <= 18) {
                    return BAD;
                } else if (value2 >= 30 && value2 < 80 && value1 >= 0 && value1 <= 18) {
                    return BAD;
                } else if (value2 >= 30 && value2 < 80 && value1 < 0) {
                    return BAD;
                } else if (value2 >= 0 && value2 < 30 && value1 <= 18) {
                    return BAD;
                } else if (value2 >= 80 && value2 <= 100 && value1 > 18 && value1 <= 27) {
                    return BAD;
                } else if (value2 >= 30 && value2 < 80 && value1 > 18 && value1 <= 27) {
                    return GOOD;
                } else if (value2 >= 0 && value2 < 30 && value1 > 18 && value1 <= 27) {
                    return NORMAL;
                } else if (value2 >= 80 && value2 <= 100 && value1 > 27 && value1 <= 60) {
                    return NORMAL;
                } else if (value2 >= 30 && value2 < 80 && value1 > 27 && value1 <= 40) {
                    return NORMAL;
                } else if (value2 >= 30 && value2 < 80 && value1 > 40 && value1 <= 60) {
                    return NORMAL;
                } else if (value2 >= 0 && value2 < 30 && value1 > 27) {
                    return NORMAL;
                }
                break;
            case "42":
            case "A0":
                if (value1 <= 800 && value1 > 0) {
                    return GOOD;
                } else if (value1 <= 1500) {
                    return NORMAL;
                } else {
                    return BAD;
                }
            case "19":
                if (value1 > 1000) {
                    return BAD;
                } else if (value1 <= 1000 && value1 >= 500) {
                    return GOOD;
                } else if (value1 < 500){
                    return NORMAL;
                }
            case "D4":
                if (value1 <= 35) {
                    return GOOD;
                } else if (value1<= 65) {
                    return NORMAL;
                } else {
                    return BAD;
                }
            case "D5":
            case "44":
                if (value1 <= 75 && value1 > 0) {
                    return GOOD;
                } else if (value1 <= 150) {
                    return NORMAL;
                } else {
                    return BAD;
                }
            case "D6":
                if (value1 <= 300 && value1 > 0) {
                    return GOOD;
                } else if (value1 <= 600) {
                    return NORMAL;
                } else {
                    return BAD;
                }
            case "Og":
                if (value1 > 75) {
                    return BAD;
                } else if (value1 <= 75 && value1 > 35) {
                    return NORMAL;
                } else if (value1 < 35){
                    return GOOD;
                }
            default:
                break;
        }
        return GOOD;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && sensorDevices != null) {
            if (HomeWidgetManager.isEnvironmentDevice(event.device.type)) {
                initData(sensorTypes.get(MainApplication.getApplication().WidgetEnvironmentPosition));
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
                    initData(sensorTypes.get(MainApplication.getApplication().WidgetEnvironmentPosition));
                    mSensorAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRoomInfoEvent(RoomInfoEvent event) {
        initData(sensorTypes.get(MainApplication.getApplication().WidgetEnvironmentPosition));
        mSensorAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetRoomListEvent(GetRoomListEvent event) {
        initData(sensorTypes.get(MainApplication.getApplication().WidgetEnvironmentPosition));
        mSensorAdapter.notifyDataSetChanged();
    }
}
