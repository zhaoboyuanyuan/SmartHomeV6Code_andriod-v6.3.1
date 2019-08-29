package cc.wulian.smarthomev6.main.home.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.device.Attribute;
import cc.wulian.smarthomev6.support.core.device.Cluster;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.device.Endpoint;
import cc.wulian.smarthomev6.support.core.device.EndpointParser;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GetRoomListEvent;
import cc.wulian.smarthomev6.support.event.RoomInfoEvent;

/**
 * 海信冰箱 widget
 */
public class HomeWidget_HS05_Refrigerator extends RelativeLayout implements IWidgetLifeCycle, View.OnClickListener {
    private Device mDevice;
    private HomeItemBean mHomeItemBean;

    private TextView mTextName, mTextRoom, mTextState;
    private View mRootView;

    public HomeWidget_HS05_Refrigerator(Context context) {
        super(context);
        initView(context);
        initListener();
    }

    @Override
    public void onBindViewHolder(HomeItemBean bean) {
        EventBus.getDefault().register(this);
        if (bean == null) {
            return;
        }
        mHomeItemBean = HomeWidgetManager.get(bean.getWidgetID());
        mDevice = MainApplication.getApplication().getDeviceCache().get(bean.getWidgetID());

        dealData(mDevice);
        // 设置在线离线的状态
        updateMode();

        setName();
        setRoomName();
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    private void initView(final Context context) {
        mRootView = LayoutInflater.from(context).inflate(R.layout.view_home_hs05_refrigerator, null);
        addView(mRootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mTextName = (TextView) mRootView.findViewById(R.id.widget_title_name);
        mTextRoom = (TextView) mRootView.findViewById(R.id.widget_title_room);
        mTextState = (TextView) mRootView.findViewById(R.id.widget_title_state);
    }

    private void initListener() {
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
        switch (mDevice.mode) {
            case 0:
            case 4:
            case 1:
                // 上线
                mTextState.setText(R.string.Device_Online);
                mTextState.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
            case 2:
                // 离线
                mTextState.setText(R.string.Device_Offline);
                mTextState.setTextColor(getResources().getColor(R.color.newStateText));
                break;
            case 3:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }

    }

    private void dealData(Device device) {
        EndpointParser.parse(device, new EndpointParser.ParserCallback() {
            @Override
            public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {

            }
        });

        updateData();
    }

    private void updateData() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReportEvent(DeviceReportEvent event) {
        if (event.device != null && mDevice != null) {
            if (TextUtils.equals(event.device.devID, mDevice.devID)) {
                dealData(JSON.parseObject(event.device.data, Device.class));
                mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                setName();
                setRoomName();
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
