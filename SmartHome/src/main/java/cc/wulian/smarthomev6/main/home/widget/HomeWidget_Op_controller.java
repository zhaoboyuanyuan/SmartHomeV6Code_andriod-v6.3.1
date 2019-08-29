package cc.wulian.smarthomev6.main.home.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.DeviceDetailActivity;
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
 * Created by Veev on 2017/5/8
 * Function: 空调新风地暖集控器 首页widget
 */

public class HomeWidget_Op_controller extends RelativeLayout implements IWidgetLifeCycle {

    private static final String TAG = HomeWidget_Op_controller.class.getSimpleName();
    private Device mDevice;
    private HomeItemBean mHomeItemBean;

    private TextView mTextName, mTextRoom, mTextState;

    private TextView tvFreshAir, tvAirCondition, tvFloorHeat;
    private TextView tvDeviceError;
    private ImageView ivFreshAir, ivAirCondition, ivFloorHeat;

    private View mRootView;

    private int mode;
    private int freshAirNum;
    private int airConditionNum;
    private int floorHeatNum;


    public HomeWidget_Op_controller(Context context) {
        super(context);

        initView(context);
    }

    public HomeWidget_Op_controller(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);

        initView(context);
    }

    private void initView(Context context) {
        mRootView = LayoutInflater.from(context).inflate(R.layout.view_home_op_controller, null);
        addView(mRootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mTextName = (TextView) mRootView.findViewById(R.id.widget_title_name);
        mTextRoom = (TextView) mRootView.findViewById(R.id.widget_title_room);
        mTextState = (TextView) mRootView.findViewById(R.id.widget_title_state);

        tvAirCondition = (TextView) mRootView.findViewById(R.id.tv_air_condition);
        tvDeviceError = (TextView) mRootView.findViewById(R.id.tv_device_error);
        tvFloorHeat = (TextView) mRootView.findViewById(R.id.tv_floor_heat);
        tvFreshAir = (TextView) mRootView.findViewById(R.id.tv_fresh_air);
        ivAirCondition = (ImageView) mRootView.findViewById(R.id.iv_air_condition);
        ivFloorHeat = (ImageView) mRootView.findViewById(R.id.iv_floor_heat);
        ivFreshAir = (ImageView) mRootView.findViewById(R.id.iv_fresh_air);

        final DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();
        int titleH = mDisplayMetrics.widthPixels - TypedValue.complexToDimensionPixelSize(30, mDisplayMetrics);
        mTextName.setMaxWidth(titleH / 2);
        mTextRoom.setMaxWidth(titleH / 4);

        mRootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDevice == null) {
                    DeviceDetailActivity.start(getContext(), mDevice.devID);
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(HomeItemBean bean) {
        EventBus.getDefault().register(this);
        if (bean == null) {
            return;
        }
        mHomeItemBean = HomeWidgetManager.get(bean.getWidgetID());
        mDevice = MainApplication.getApplication().getDeviceCache().get(bean.getWidgetID());
        mode = mDevice.mode;

        HomeWidgetManager.add2Cache(mDevice);

        dealData(mDevice);
        countDevice(mDevice);
        // 设置在线离线的状态
        updateMode();

        setName();
        setRoomName();
    }

    private void updateData(int endpointNumber) {
        if (endpointNumber <= 320) {
            airConditionNum++;
        } else if (endpointNumber < 385) {
            freshAirNum++;
        } else if (endpointNumber <= 448) {
            floorHeatNum++;
        }
    }

    private void updateView() {
        tvFreshAir.setText(String.valueOf(freshAirNum));
        tvFloorHeat.setText(String.valueOf(floorHeatNum));
        tvAirCondition.setText(String.valueOf(airConditionNum));
    }


    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    private void setName() {
        if (mDevice == null) {
            mTextName.setText(DeviceInfoDictionary.getNameByTypeAndName(mHomeItemBean.getName(), mHomeItemBean.getType()));
        } else {
            mTextName.setText(DeviceInfoDictionary.getNameByTypeAndName(mDevice.type, mDevice.name));
        }
    }

    /**
     * 设置分区名
     */
    private void setRoomName() {
        String areaName = ((MainApplication) getContext().getApplicationContext()).getRoomCache().getRoomName(mDevice);
        mTextRoom.setText(areaName);
    }

    /**
     * 更新上下线
     */
    private void updateMode() {
        switch (mode) {
            case 0:
            case 4:
            case 1:
                // 上线
                mTextState.setText(R.string.Device_Online);
                mTextState.setTextColor(getResources().getColor(R.color.colorPrimary));

                ivFreshAir.setImageResource(R.drawable.icon_fresh_air_online);
                ivAirCondition.setImageResource(R.drawable.icon_air_condition_online);
                ivFloorHeat.setImageResource(R.drawable.icon_floor_heating_online);
                break;
            case 2:
                // 离线
                mTextState.setText(R.string.Device_Offline);
                mTextState.setTextColor(getResources().getColor(R.color.newStateText));
                ivFreshAir.setImageResource(R.drawable.icon_fresh_air_offline);
                ivAirCondition.setImageResource(R.drawable.icon_air_condition_offline);
                ivFloorHeat.setImageResource(R.drawable.icon_floor_heating_offline);
                break;
            case 3:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReport(DeviceReportEvent event) {
        if (event.device != null && mDevice != null) {
            if (TextUtils.equals(event.device.devID, mDevice.devID)) {
                mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                dealData(mDevice);
                countDevice(mDevice);
                mode = event.device.mode;
                updateMode();

                setName();
                setRoomName();
            }
        }
    }

    private void dealData(Device device) {
        EndpointParser.parse(device, new EndpointParser.ParserCallback() {
            @Override
            public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                if (attribute.attributeId == 0x000B && endpoint.endpointNumber == 1 && TextUtils.equals("1", attribute.attributeValue)) {
                    tvDeviceError.setVisibility(VISIBLE);
                }
            }
        });
    }


    private void countDevice(Device device) {
        airConditionNum = 0;
        floorHeatNum = 0;
        freshAirNum = 0;
        for (int i = 0; i < device.endpoints.size(); i++) {
            if (device.endpoints.get(i).clusters.get(0).clusterId == 0x0201 || device.endpoints.get(i).clusters.get(0).clusterId == 0x0203) {
                updateData(device.endpoints.get(i).endpointNumber);
            }
        }
        updateView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceInfoChanged(DeviceInfoChangedEvent event) {
        if (event.deviceInfoBean != null && mDevice != null) {
            if (TextUtils.equals(event.deviceInfoBean.devID, mDevice.devID)) {
                if (event.deviceInfoBean.mode == 2) {
                    mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                    setName();
                    setRoomName();
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRoomInfoEvent(RoomInfoEvent event) {
        mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
        setRoomName();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetRoomListEvent(GetRoomListEvent event) {
        mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
        setRoomName();
    }
}
