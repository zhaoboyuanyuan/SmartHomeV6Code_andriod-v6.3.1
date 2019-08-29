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
import cc.wulian.smarthomev6.support.event.DeviceInfoChangedEvent;
import cc.wulian.smarthomev6.support.event.DeviceReportEvent;
import cc.wulian.smarthomev6.support.event.GetRoomListEvent;
import cc.wulian.smarthomev6.support.event.RoomInfoEvent;

/**
 * 凉霸 widget
 */
public class HomeWidget_OF_Ctrl extends RelativeLayout implements IWidgetLifeCycle, View.OnClickListener {
    private Device mDevice;
    private HomeItemBean mHomeItemBean;

    private TextView mTextName, mTextRoom, mTextState;
    private ImageView mImageUp, mImageDown, mImagePause;
    private View mRootView;
    /**
     * 状态
     * 1 开
     * 2 关
     * 0 停
     */
    private int mState = -1;

    public HomeWidget_OF_Ctrl(Context context) {
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

        if (!HomeWidgetManager.hasInCache(mDevice)) {
            sendCmd(0x8000, null);
        }
        HomeWidgetManager.add2Cache(mDevice);
    }

    @Override
    public void onViewRecycled() {
        EventBus.getDefault().unregister(this);
    }

    private void initView(final Context context) {
        mRootView = LayoutInflater.from(context).inflate(R.layout.view_home_of_ctrl, null);
        addView(mRootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mTextName = (TextView) mRootView.findViewById(R.id.widget_title_name);
        mTextRoom = (TextView) mRootView.findViewById(R.id.widget_title_room);
        mTextState = (TextView) mRootView.findViewById(R.id.widget_title_state);

        mImageUp = (ImageView) mRootView.findViewById(R.id.widget_of_image_up);
        mImageDown = (ImageView) mRootView.findViewById(R.id.widget_of_image_down);
        mImagePause = (ImageView) mRootView.findViewById(R.id.widget_of_image_pause);

        mImageUp.setOnClickListener(this);
        mImageDown.setOnClickListener(this);
        mImagePause.setOnClickListener(this);
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
            case R.id.widget_of_image_up:
                sendCmd(0x8010, "1");
                break;
            case R.id.widget_of_image_down:
                sendCmd(0x8010, "2");
                break;
            case R.id.widget_of_image_pause:
                sendCmd(0x8010, "0");
                break;
            default:
                break;
        }

    }

    private void dealData(Device device) {
        EndpointParser.parse(device, new EndpointParser.ParserCallback() {
            @Override
            public void onFindAttribute(Endpoint endpoint, Cluster cluster, Attribute attribute) {
                if (attribute.attributeId == 0x8001) {
                    try {
                        mState = Integer.parseInt(attribute.attributeValue);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        updateData();
    }

    private void updateData() {
        mImageDown.setImageResource(R.drawable.selector_widget_of_down);
        mImageUp.setImageResource(R.drawable.selector_widget_of_up);
        mImagePause.setImageResource(R.drawable.selector_widget_of_pause);
        switch (mState) {
            case 0:
                mImagePause.setImageResource(R.drawable.icon_of_pause_pre);
                break;
            case 1:
                mImageUp.setImageResource(R.drawable.icon_of_up_pre);
                break;
            case 2:
                mImageDown.setImageResource(R.drawable.icon_of_down_pre);
                break;
        }
    }

    /**
     * 发送命令
     */
    private void sendCmd(int commandId, Object args){
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "501");
            object.put("gwID", mDevice.gwID);
            object.put("devID", mDevice.devID);
            object.put("clusterId", 0x0102);
            object.put("commandType", 1);
            object.put("commandId", commandId);
            object.put("endpointNumber", 1);
            if (args != null) {
                JSONArray array = new JSONArray();
                array.put(args);
                object.put("parameter", array);
            }
            MainApplication.getApplication()
                    .getMqttManager()
                    .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceInfoChanged(DeviceInfoChangedEvent event) {
        if (event.deviceInfoBean != null && mDevice != null) {
            if (TextUtils.equals(event.deviceInfoBean.devID, mDevice.devID)) {
                if (event.deviceInfoBean.mode == 2) {
                    mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                    setName();
                    setRoomName();
                    // 设置在线离线的状态
                    updateMode();
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceReportEvent(DeviceReportEvent event) {
        if (event.device != null && mDevice != null) {
            if (TextUtils.equals(event.device.devID, mDevice.devID)) {
                dealData(event.device);
                mDevice = MainApplication.getApplication().getDeviceCache().get(mDevice.devID);
                setName();
                setRoomName();
                // 设置在线离线的状态
                updateMode();
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
